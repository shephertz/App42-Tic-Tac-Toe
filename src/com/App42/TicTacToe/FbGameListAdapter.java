package com.App42.TicTacToe;


import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.user.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FbGameListAdapter extends BaseAdapter implements
		AsyncApp42ServiceApi.App42ServiceListener {
	private AsyncApp42ServiceApi asyncService;

	private ArrayList<JSONObject> gamesList;
	private Context context;

	public FbGameListAdapter(Context context) {

		this.asyncService = AsyncApp42ServiceApi.instance();
		this.context = context;
		gamesList = new ArrayList<JSONObject>();
		this.asyncService.getUserGamesList(UserContext.MyUserName, this);
	}


	// BaseAdapter overrides
	@Override
	public int getCount() {
		return gamesList.size();
	}

	@Override
	public Object getItem(int position) {
		return gamesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO how to map uuid string to long
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.friend_list_item, null);
		}
		JSONObject item = gamesList.get(position);
		if (item != null) {

			String u1Name = "", u2Name = "", winner = "", nextTurn = "", state = "", picUrl="";
			

			try {
				u1Name = item.getString(Constants.GameFbName);
				u2Name = item.getString(Constants.GameFbFriendName);
				winner = item.getString(Constants.GameWinnerKey);
				nextTurn = item.getString(Constants.GameNextMoveKey);
				state = item.getString(Constants.GameStateKey);
				if(u1Name.equals(UserContext.MyDisplayName)){
					picUrl=item.getString(Constants.GameFriendPicUrl);
				}
				else{
					picUrl=item.getString(Constants.GameMyPicUrl);
				}
				

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ImageView friendPic=(ImageView)convertView.findViewById(R.id.profile_pic);
			Utilities.loadImageFromUrl(friendPic, picUrl);

			TextView tvState = (TextView) convertView
					.findViewById(R.id.friend_name);
			if (state.equals(Constants.GameStateFinished)) {
				if (!winner.isEmpty()) {
					if (winner.equals(UserContext.MyUserName)) {
						tvState.setText("You won!");
					} else {
						tvState.setText("You lost :(");
					}
				} else {
					tvState.setText("match drawn");
				}
			} else if (nextTurn.equalsIgnoreCase(UserContext.MyUserName)) {
				tvState.setText("your turn");
			} else {
				if (u1Name.equalsIgnoreCase(UserContext.MyDisplayName)) {
					tvState.setText(u2Name + "'s turn");
				} else {
					tvState.setText(u1Name + "'s turn");
				}
				
			}
		}

		return convertView;
	}

	// App42 async callBacks
	@Override
	public void onUserCreated(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserAuthenticated(App42Response response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetUserGamesList(final ArrayList<JSONObject> gamesList) {
		if (gamesList == null) {
			((FbFriendGameList) FbGameListAdapter.this.context)
					.onOperationFail();
		} else {
			FbGameListAdapter.this.gamesList = gamesList;
			FbGameListAdapter.this.notifyDataSetChanged();
			((FbFriendGameList) FbGameListAdapter.this.context)
					.onOperationSuccess();
		}
	}

	@Override
	public void onUpdateGame(final JSONObject game) {
		System.out.println(game.toString());
	}

	public void refreshNewUserList() {
		this.asyncService.getUserGamesList(UserContext.MyUserName, this);
	}


	@Override
	public void onCreateGame(JSONObject createdGameObject) {
		// TODO Auto-generated method stub
		
	}

}