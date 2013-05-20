package com.App42.TicTacToe;

import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.android.R;

public class UserHomeActivity extends ListActivity {

	private String userName;
	private EditText newGameOppName;

	private GamesListAdapter adapter;
	private ProgressDialog progressDialog;
	private ImageView profileImage;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_home);
		newGameOppName = (EditText) findViewById(R.id.opp_name);
		profileImage = (ImageView) findViewById(R.id.profile_pic);

		// don't automatically show the soft keyboard. wait till user actually
		// clicks on
		// the edit box.
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		userName = getIntent().getStringExtra(Constants.IntentUserName);

		// create adapter after fetching userName parameter from intent.
		adapter = new GamesListAdapter(this, userName);
		this.setListAdapter(adapter);
	}

	public void onStartGameClicked(View view) {
		String opponent = newGameOppName.getText().toString();
		if (opponent.isEmpty() || opponent.equals(userName)) {
			return;
		}
		progressDialog = ProgressDialog.show(this, "", "creating game..");
		progressDialog.setCancelable(true);
		adapter.beginNewGame(newGameOppName.getText().toString());
	}

	public void onProfileClicked(View view) {
	}

	public void onOperationSuccess() {
		progressDialog.dismiss();
	}

	public void onOperationFail() {
		progressDialog.dismiss();
	}

	public void onSignOutClicked(View view) {
		SharedPreferences mPrefs = getSharedPreferences(
				MainActivity.class.getName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove(Constants.SharedPrefUname);
		editor.apply();
		finish();
		Intent myIntent = new Intent(this, MainActivity.class);
		this.startActivity(myIntent);
	}

	public void onReloadClicked(View view) {
		progressDialog = ProgressDialog.show(this, "", "loading games");
		progressDialog.setCancelable(true);
		this.adapter.refreshNewUserList(userName);
	}

	public void onStart() {
		super.onStart();
		Intent intent = getIntent();
		userName = intent.getStringExtra(Constants.IntentUserName);

		if (adapter.getCount() < 1) {
			progressDialog = ProgressDialog.show(this, "", "loading games");
		}
		this.adapter.refreshNewUserList(userName);
	}

	public void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		this.setIntent(newIntent);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		JSONObject game = (JSONObject) this.adapter.getItem(position);
		Intent myIntent = new Intent(this, GameActivity.class);
		myIntent.putExtra(Constants.IntentGameObject, game.toString());
		myIntent.putExtra(Constants.IntentUserName, userName);
		this.startActivity(myIntent);
	}
}