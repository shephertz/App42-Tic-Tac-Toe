package com.App42.TicTacToe;


import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

//import com.facebook.android.R;

public class FbFriendGameList extends ListActivity {

	
	private FbGameListAdapter adapter;
	private ProgressDialog progressDialog;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fb_game_home);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		((TextView)findViewById(R.id.my_name)).setText(UserContext.myDisplayName);
		ImageView myimage=(ImageView)findViewById(R.id.my_pic);
		Utilities.loadImageFromUrl(myimage, UserContext.myPicUrl);
		adapter = new FbGameListAdapter(this);
		this.setListAdapter(adapter);
	}
	public void onOperationSuccess() {
		progressDialog.dismiss();
	}

	public void onOperationFail() {
		progressDialog.dismiss();
	}
	public void onRefreshClicked(View view) {
		progressDialog = ProgressDialog.show(this, "", "loading games");
		progressDialog.setCancelable(true);
		this.adapter.refreshNewUserList();
	}

	public void onStart() {
		super.onStart();
		Intent intent = getIntent();
		if (adapter.getCount() < 1) {
			progressDialog = ProgressDialog.show(this, "", "loading games");
		}
		this.adapter.refreshNewUserList();
	}

	public void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		this.setIntent(newIntent);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		JSONObject game = (JSONObject) this.adapter.getItem(position);
		GCMIntentService.isFromNotification=false;
		Intent myIntent = new Intent(this, GameActivity.class);
		myIntent.putExtra(Constants.IntentGameObject, game.toString());
		myIntent.putExtra(Constants.IntentUserName, UserContext.myUserName);
		this.startActivity(myIntent);
	}
}