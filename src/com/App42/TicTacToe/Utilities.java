package com.App42.TicTacToe;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.ImageView;

import com.shephertz.app42.paas.sdk.android.storage.Storage.JSONDocument;


public class Utilities {

	/*
	 * This function allows user to make adeepCopy of a jsonArray
	 */
	public static JSONArray deepCopyJSONArray(JSONArray src) {
		JSONArray dst = new JSONArray();
		for (int i = 0; i < src.length(); i++) {
			try {
				dst.put(src.get(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return dst;

	}
	/*
	 * This function allows user to check  authentication with face-book
	 */
	public static boolean isAuthenticated() {
		return (FacebookService.instance().isFacebookSessionValid() && UserContext.MyUserName
				.length() > 0);
	}
	/*
	 * This function allows user to load image from URL in a background Thread
	 *  @param image ImageView on which image is loaded
	 *  @param url of image
	 */
		public static void loadImageFromUrl(final ImageView image, final String url) {
			final Handler callerThreadHandler = new Handler();
			new Thread() {
				@Override
				public void run() {
					final Bitmap bitmap = loadBitmap(url);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (bitmap != null) {
								image.setImageBitmap(bitmap);
							}
						}
					});
				}
			}.start();
		}
	/*
	 * This function allows user to load bitmap from URL
	 * @param url of image
	 * @return Bitmap 
	 */
		public static Bitmap loadBitmap(String url) {
			Bitmap bitmap = null;
			try {
				InputStream in = new java.net.URL(url).openStream();
				bitmap = BitmapFactory.decodeStream(in);
				in.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

	

	public static boolean areCharsEqual(char c1, char c2, char c3, char c4) {
		if (c1 == c2 && c2 == c3 && c3 == c4) {
			return true;
		}
		return false;
	}

	public static ArrayList<JSONObject> getJSONObjectsFromJSONDocuments(
			ArrayList<JSONDocument> input) {
		ArrayList<JSONObject> retValue = new ArrayList<JSONObject>();
		for (int i = 0; i < input.size(); i++) {
			JSONObject obj;
			try {
				obj = new JSONObject(input.get(i).jsonDoc);
				retValue.add(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return retValue;
	}
	
	/*
	 * This function allows user to check availability of network connection in
	 * android device uses CONNECTIVITY_SERVICE of android device to get desired
	 * network Internet connection
	 * 
	 * @return status of availability of Internet connection in true or false
	 * manner
	 */
	public static boolean haveNetworkConnection(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			for (NetworkInfo ni : netInfo) {
				if (ni.getTypeName().equalsIgnoreCase("WIFI"))
					if (ni.isConnected())
						haveConnectedWifi = true;
				if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
					if (ni.isConnected())
						haveConnectedMobile = true;
			}

		} catch (Exception e) {

		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	/*
	 * This function allows user to clear the cache of application
	 */
	public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);

            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
	/*
	 * This function allows user to delete cache 
	 */
	 public static boolean deleteDir(File dir) {
	        if (dir!=null && dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i = 0; i < children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }
	        return dir.delete();
	    }
}