package com.App42.TicTacToe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookService {
    	
	private Handler mUIThreadHandler = null;
	private Facebook facebook = new Facebook(Constants.FB_APP_ID);
	public static AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;    
	private Context appContext = null;
    private static FacebookService _instance = null;
 
    
    public static FacebookService instance(){
    	if(_instance == null){
    		_instance = new FacebookService();
    	}
    	return _instance;
    }
    /*
     * This function allows user to get information of face-book user
     */
    public void setContext(Context context){
    	_instance.appContext = context;
    	_instance.refreshFromContext();
    }
    
    private FacebookService(){
        mAsyncRunner = new AsyncFacebookRunner(facebook);                
	}
	
	public void authorizeCallback(int requestCode, int resultCode, Intent data)
	{
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	public boolean isFacebookSessionValid(){
		return facebook.isSessionValid();
	}
    /*
     * This function allows user to handle face-book sign-out
     */
    public void signout() throws MalformedURLException, IOException{
    	facebook.logout(appContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("display_name", "");
        editor.putString("warp_join_id", "");
        editor.putString("profile_url", "");
        editor.putString("access_token", null);
        editor.putLong("access_expires", 0);
        editor.commit();
    }
    
    /*
     * Used to get information of face-book user if saved
     */
    private void refreshFromContext(){
    	mPrefs = appContext.getSharedPreferences(Constants.TicTacToePref, android.content.Context.MODE_PRIVATE);
        /*
         * Get existing access_token if any
         */
        
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
            UserContext.accessToken=access_token;
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }   
        if(facebook.isSessionValid()){
        	UserContext.MyDisplayName = mPrefs.getString("display_name", "");
        	UserContext.MyUserName = mPrefs.getString("warp_join_id", "");
        	UserContext.MyPicUrl = mPrefs.getString("profile_url", "");
        }else{
        	UserContext.MyDisplayName = "";
        	UserContext.MyUserName = "";
        	UserContext.MyPicUrl = "";
        }
    }
    

    /*
     * This function allows user to call for authorization with face-book
     */
    public void fetchFacebookProfile(final FriendList hostActivity)
    {
    	if(mUIThreadHandler == null){
    		mUIThreadHandler = new Handler();
    	}
   
        // force by showing the facebook auth dialog
    	facebook.authorize(hostActivity, new
        		String[] {
        		"friends_online_presence"},facebook.FORCE_DIALOG_AUTH,
        		new DialogListener() {
            @Override
            public void onComplete(Bundle values) {
            	System.out.println("authorize on complete");
            	if(mPrefs == null){
            		mPrefs = appContext.getSharedPreferences(Constants.TicTacToePref, android.content.Context.MODE_PRIVATE);
            	}
                SharedPreferences.Editor editor = mPrefs.edit();
                UserContext.accessToken= facebook.getAccessToken();
                editor.putString("access_token", facebook.getAccessToken());
                editor.putLong("access_expires", facebook.getAccessExpires());
                editor.commit();
                FacebookService.this.getFacebookProfile(hostActivity);
            }

            @Override
            public void onFacebookError(FacebookError error) {
    
            	hostActivity.onFbError(error.toString());
            }

            @Override
            public void onError(DialogError error) {
            	hostActivity.onFbError(error.toString());
            }

            @Override
            public void onCancel() {     	
            	hostActivity.onFbError("Cancel");
            }
        });	
    }
    
    
/*
 * This function allows user to fetch face-book profile when authorization  is succeeded    
 */
    public void getFacebookProfile(FriendList callingActivity)
    {
    	System.out.println("euierrewre");
        Bundle params = new Bundle();
        params.putString("fields", "name, picture");    	
    	mAsyncRunner.request("me", params, new FacebookRequestListener(callingActivity));
    }
 
	private class FacebookRequestListener implements RequestListener
	{
		FriendList callBack;
		public FacebookRequestListener(FriendList callingActivity){
			this.callBack = callingActivity;
		}
		
		@Override
		public void onComplete(String response, Object state) {
	        JSONObject jsonObject;
	        try {
	            jsonObject = new JSONObject(response);
	            JSONObject picObj = jsonObject.getJSONObject("picture");
	            JSONObject dataObj = picObj.getJSONObject("data");
	            
	            UserContext.MyDisplayName = jsonObject.getString("name");
	            UserContext.MyUserName = jsonObject.getString("id");
	            UserContext.MyPicUrl = dataObj.getString("url");
	            
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("display_name", UserContext.MyDisplayName);
                editor.putString("warp_join_id", UserContext.MyUserName);
                editor.putString("profile_url", UserContext.MyPicUrl);
                editor.commit();
                
	            mUIThreadHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                	callBack.onFacebookProfileRetreived(true);
	                }
	            });

	        } catch (JSONException e) {
	            mUIThreadHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                	callBack.onFacebookProfileRetreived(false);
	                }
	            });
	            e.printStackTrace();
	        }
		}

	
		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			callBack.onFbError(e.toString());
			
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			callBack.onFbError(e.toString());
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			callBack.onFbError(e.toString());
			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			callBack.onFbError(e.toString());
			
		}
	}
	

	
	
	
}



