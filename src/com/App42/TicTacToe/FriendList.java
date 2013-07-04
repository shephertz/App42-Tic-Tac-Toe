package com.App42.TicTacToe;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.social.Social.Friends;


/*
 * This class is allows user to show face-book friend-list
 */
public class FriendList extends Activity implements OnItemClickListener {
	private ListView friendList;
	private List<Friends> fbFriendList;
	private List<Friends> searchFriendList;
	private final int RESULT_LOAD_IMAGE = 1;
	private ProgressDialog dialog;
	private int index = 0;
	private boolean searchTag = false;
	private AsyncApp42ServiceApi asyncService;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendlist);
		dialog = new ProgressDialog(this);
		asyncService = AsyncApp42ServiceApi.instance();
		Utilities.clearCache(this);
		loadMyFriendList();
	}

	/*
	 * This function allows user to load friend list if user is authenticated
	 * 
	 */
	private void loadMyFriendList() {
		if (Utilities.haveNetworkConnection(this)) {
			dialog.setMessage("Loading data...");
			dialog.show();
			FacebookService.instance().setContext(getApplicationContext());
			if (!Utilities.isAuthenticated()) {
				FacebookService.instance().fetchFacebookProfile(this);
			} else {
				UserContext.authorized = true;
				((TextView)findViewById(R.id.my_name)).setText(UserContext.myDisplayName);
				ImageView myimage=(ImageView)findViewById(R.id.my_pic);
				Utilities.loadImageFromUrl(myimage, UserContext.myPicUrl);
				asyncService.registerForPushNotification(this, UserContext.myUserName);
				asyncService.loadAllFriends(
						UserContext.myUserName, UserContext.accessToken, this);
			}
		}

	}

	/*
	 * Check Internet connectivity (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	public void onStart() {
		super.onStart();
		if (!Utilities.haveNetworkConnection(this)) {
			showErrorDialog("Error in Network Connection!");
		}
	}

	/*
	 * Show dialog with no connection
	 */
	private void showErrorDialog(String message) {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage(message).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Action for 'Yes' Button
						finish();
					}
				});
		AlertDialog alert = alt_bld.create();
		alert.setTitle("Error!");
		alert.setIcon(R.drawable.ic_launcher);
		alert.show();
	}

	

	 /*
	
	  * This function allows to send authorization callback to face-book
	  * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	  */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		 if (!UserContext.authorized) {
			 FacebookService.instance().authorizeCallback(requestCode,
						resultCode, data);
			UserContext.authorized = true;
		}

	}

	/*
	 * Callback when authorization with face-book
	 * If success load my friend list
	 */
	 void onFacebookProfileRetreived(boolean isSuccess) {
			if (isSuccess) {
				
				((TextView)findViewById(R.id.my_name)).setText(UserContext.myDisplayName);
				ImageView myimage=(ImageView)findViewById(R.id.my_pic);
				Utilities.loadImageFromUrl(myimage, UserContext.myPicUrl);
				asyncService.registerForPushNotification(this, UserContext.myUserName);
				asyncService.loadAllFriends(
						UserContext.myUserName, UserContext.accessToken, this);
				
			}
			else{
				dialog.dismiss();
			}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	public void onStop() {
		super.onStop();

	}

	/*
	 * button (non-Javadoc)
	 * 
	 * @override method of superclass
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	public void onDestroy() {
		super.onDestroy();
	}

	/*
	 * called when this activity is restart again
	 * 
	 * @override method of superclass
	 */
	public void onReStart() {
		super.onRestart();
	}

	/*
	 * called when activity is paused
	 * 
	 * @override method of superclass (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		super.onPause();
	}

	/*
	 * called when activity is resume
	 * 
	 * @override method of superclass (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
	}

	/*
	 * This function allows user to create alert dialog when logout option is selected
	 * @param name of friend whom you want to share image
	 */
	public void challengeFriendDialog(String name) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setTitle("Play Game with");
		alertbox.setMessage(name);
		alertbox.setIcon(R.drawable.ic_launcher);
		alertbox.setPositiveButton("Challenge",
				new DialogInterface.OnClickListener() {
					// do something when the button is clicked
					public void onClick(DialogInterface arg0, int arg1) {
						ChallengeFriend();
					}
				});
		alertbox.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		alertbox.show();
	}

	/*
	 * Call when user clicks on browse photo
	 */
	private void ChallengeFriend() {
		dialog.setMessage("Creating Game...");
		dialog.show();
		if(searchTag){
			asyncService.createGameWithFbFriend(searchFriendList.get(index).getId(), searchFriendList.get(index).getName(), 
					searchFriendList.get(index).getPicture(),
					this);
		}
		else{
			asyncService.createGameWithFbFriend(fbFriendList.get(index).getId(), fbFriendList.get(index).getName(), 
					fbFriendList.get(index).getPicture(),
					 this);
		}
	}
	
	void onCreateGame(boolean status,JSONObject gameObject,String remoteUserName){
		dialog.dismiss();
		if(status){
			Intent intent=new Intent(this,FbFriendGameList.class);
			startActivity(intent);
			asyncService.pushMessage(gameObject, remoteUserName);
		}
		else{
			Toast.makeText(this, "Challenge Failed", Toast.LENGTH_SHORT).show();
		}
	}
	public void onGameListClicked(View gameListBtn){
		Intent intent=new Intent(this,FbFriendGameList.class);
		startActivity(intent);
	}

	/*
	 * This function allows user to show facebook friend list when user gets friends information
	 * @param friendsInfo contains information of friends like id/picUrl/name/application installed status
	 */
	private void showFriendList(ArrayList<Friends> friendsInfo) {
	
		friendList = (ListView) findViewById(R.id.friend_list);
		fbFriendList = new ArrayList<Friends>();
		searchFriendList = new ArrayList<Friends>();
		int size = friendsInfo.size();
		for (int i = 0; i < size; i++) {
			fbFriendList.add(friendsInfo.get(i));
		}
		friendList.setAdapter(new ActionListAdapter(this, R.id.friend_list,
				fbFriendList));
		friendList.setOnItemClickListener(this);

		final EditText search = (EditText) findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				showSearchList(s, start, before, count, search);

			}
		});

	}
	
	
	/*
	 * This function allows user to show search list when user search his friend
	 */
	private void showSearchList(CharSequence s, int start, int before,
			int count, EditText search) {
		int textlength = search.getText().length();
		searchTag = true;
		searchFriendList.clear();
		for (int i = 0; i < fbFriendList.size(); i++) {
			if (textlength <= fbFriendList.get(i).getName().length()) {
				if (search
						.getText()
						.toString()
						.equalsIgnoreCase(
								(String) fbFriendList.get(i).getName()
										.subSequence(0, textlength))) {
					searchFriendList.add(fbFriendList.get(i));
				}
			}
		}
		friendList.setAdapter(new ActionListAdapter(this, R.id.friend_list,
				searchFriendList));

	}

	/*
	 * Call back method when friend list is fetched
	 */
	 void onFriendListFetched(ArrayList<Friends> fbFriends) {
		dialog.dismiss();
		showFriendList(fbFriends);
	}

	 /*
	  * Callback on error
	  */
	 void onFbError(String errorMsg) {
		dialog.dismiss();
		showErrorDialog(errorMsg);
	}

	 /*
	  * This function allows user to show friend list
	  */
	private class ActionListAdapter extends ArrayAdapter<Friends> {
		private List<Friends> listElementAdapter;
		Context context;

		public ActionListAdapter(Context context, int resourceId,
				List<Friends> listElements) {
			super(context, resourceId, listElements);
			this.context = context;
			this.listElementAdapter = listElements;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.friend_list_item, null);
			}

			Friends friend = listElementAdapter.get(position);
			if (friend != null) {

				ImageView picIcon = (ImageView) view
						.findViewById(R.id.profile_pic);
				TextView friendName = (TextView) view
						.findViewById(R.id.friend_name);
			
				if (picIcon != null) {
					Utilities.loadImageFromUrl(picIcon, friend.getPicture());
				}

				if (friendName != null) {
					friendName.setText(friend.getName());
				}
		
			}
			return view;
		}

	}
/*
 * This function allows user to browse dialog when friend is clicked
 * (non-Javadoc)
 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

		index = pos;
		try {
			if (searchTag) {
				challengeFriendDialog(searchFriendList.get(pos).getName());

			} else {
				challengeFriendDialog(fbFriendList.get(pos).getName());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * This function allows user to create menu
	 */
	private void CreateMenu(Menu menu) {
		menu.setQwertyMode(true);
		menu.add(0, 0, 0, "Refresh").setIcon(R.drawable.refresh);

	}

	/*
	 * This function allows user to handle selection of option menu
	 */
	private boolean MenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			loadMyFriendList();
			return true;

		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	// ---only created once---
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}

}
