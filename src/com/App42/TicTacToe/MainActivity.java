package com.App42.TicTacToe;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.ServiceAPI;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;
import com.shephertz.app42.paas.sdk.android.user.User;

public class MainActivity extends Activity implements
		AsyncApp42ServiceApi.App42ServiceListener {

	private AsyncApp42ServiceApi asyncService;
	private EditText userName;
	private EditText password;
	private EditText emailid;
	private SharedPreferences mPrefs;
	private ProgressDialog progressDialog;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		userName = (EditText) this.findViewById(R.id.uname);
		password = (EditText) this.findViewById(R.id.pswd);
		emailid = (EditText) this.findViewById(R.id.email);
		mPrefs = getSharedPreferences(MainActivity.class.getName(),
				MODE_PRIVATE);
		asyncService = AsyncApp42ServiceApi.instance();
		String loggedInName = mPrefs.getString(Constants.SharedPrefUname, null);
		if (loggedInName != null && !loggedInName.isEmpty()) {

			gotoHomeActivity(loggedInName);
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void onStart() {
		super.onStart();
	}

	public void onSigninClicked(View view) {
		progressDialog = ProgressDialog.show(this, "", "signing in..");
		progressDialog.setCancelable(true);
		asyncService.authenticateUser(userName.getText().toString(), password
				.getText().toString(), this);
	}

	public void onRegisterClicked(View view) {
		progressDialog = ProgressDialog.show(this, "", "registering..");
		progressDialog.setCancelable(true);
		asyncService.createUser(userName.getText().toString(), password
				.getText().toString(), emailid.getText().toString(), this);
	}
	
	public void onFacebookConnect(View view){
		this.finish();
		Intent mainIntent = new Intent(this, FriendList.class);
		this.startActivity(mainIntent);
	}

	private void saveCreds() {
		mPrefs = getSharedPreferences(MainActivity.class.getName(),
				MODE_PRIVATE);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(Constants.SharedPrefUname, userName.getText()
				.toString());
		editor.commit();
	}

	private void gotoHomeActivity(String signedInUserName) {
		// Finish the splash activity so it can't be returned to.
		this.finish();
		Intent mainIntent = new Intent(this, UserHomeActivity.class);
		mainIntent.putExtra(Constants.IntentUserName, signedInUserName);
		this.startActivity(mainIntent);
	}

	@Override
	public void onUserCreated(final User user) {
		progressDialog.dismiss();
		if (user != null) {
			saveCreds();
			asyncService.registerForPushNotification(this, userName.getText().toString());
			gotoHomeActivity(userName.getText().toString());
		} else {
			Toast.makeText(this, "User creation failed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onUserAuthenticated(final App42Response response) {
		progressDialog.dismiss();
		if (response != null) {
			System.out.println(response.toString());
			saveCreds();
			asyncService.registerForPushNotification(this, userName.getText().toString());
		
			gotoHomeActivity(userName.getText().toString());
		} else {
			Toast.makeText(this, "Authentication failed..!!",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onGetUserGamesList(ArrayList<JSONObject> arrayList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateGame(JSONObject createdGameObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateGame(JSONObject updatedGameObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

//	private class PushRegistration {
//		Context context;
//
//		public void doRegistration(Context context, final String userID) {
//			this.context = context;
//			GCMRegistrar.checkDevice(context);
//			GCMRegistrar.checkManifest(context);
//			// registerReceiver(mHandleMessageReceiver, new IntentFilter(
//			// Constants.DISPLAY_MESSAGE_ACTION));
//			final String deviceId = GCMRegistrar.getRegistrationId(context);
//			if (deviceId.equals("")) {
//				GCMRegistrar.register(MainActivity.this, Constants.SenderId);
//			} else {
//				mRegisterTask = new AsyncTask<Void, Void, Void>() {
//					@Override
//					protected Void doInBackground(Void... params) {
//						try {
//							ServiceAPI sp = new ServiceAPI(
//									Constants.App42ApiKey,
//									Constants.App42ApiSecret);
//							String userName = Constants.GameName + userID;
//							PushNotificationService push = sp
//									.buildPushNotificationService();
//							push.storeDeviceToken(userName, deviceId);
//						} catch (Exception e) {
//						}
//						return null;
//					}
//
//					@Override
//					protected void onPostExecute(Void result) {
//						mRegisterTask = null;
//
//					}
//
//				};
//				mRegisterTask.execute(null, null, null);
//			}
//		}
//	}
}
