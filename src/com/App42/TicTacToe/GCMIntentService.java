package com.App42.TicTacToe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	public static boolean isFromNotification=false;
	public static String notificationMessage="";
	public GCMIntentService() {
		super(Constants.SenderId);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Device registered: regId = " + arg1);

	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		
		Log.i(TAG, "Received message");
		Bundle b = intent.getExtras();
		String message = (String) b.get("message");
		notificationMessage=message;
			displayMessage(context, message);
			generateNotification(context, message);
		

	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		generateNotification(context, message);
	}

	@Override
	protected void onRegistered(Context arg0, String registrationId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Device registered: regId = " + registrationId);

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		System.out.println("onUnregistered" + arg1);
		// TODO Auto-generated method stub

	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, GameActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);

	}



	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	static void displayMessage(Context context, String message) {
		isFromNotification=true;
		Intent intent = new Intent(Constants.DisplayMessageAction);
		intent.putExtra(Constants.NotificationMessage, message);
		context.sendBroadcast(intent);
	}

}
