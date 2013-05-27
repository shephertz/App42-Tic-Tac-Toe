package com.App42.TicTacToe;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.google.android.gcm.GCMRegistrar;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.ServiceAPI;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;
import com.shephertz.app42.paas.sdk.android.social.Social;
import com.shephertz.app42.paas.sdk.android.social.Social.Friends;
import com.shephertz.app42.paas.sdk.android.social.SocialService;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.Storage.JSONDocument;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.UserService;



public class AsyncApp42ServiceApi {
	private UserService userService;
	private StorageService storageService;
	private PushNotificationService pushService;
	private static AsyncApp42ServiceApi mInstance = null;
	private SocialService socialService;

	private AsyncApp42ServiceApi() {
		ServiceAPI sp = new ServiceAPI(Constants.App42ApiKey,
				Constants.App42ApiSecret);
		this.userService = sp.buildUserService();
		this.storageService = sp.buildStorageService();
		this.pushService = sp.buildPushNotificationService();
		this.socialService=sp.buildSocialService();

	}

	public static AsyncApp42ServiceApi instance() {

		if (mInstance == null) {
			mInstance = new AsyncApp42ServiceApi();
		}

		return mInstance;
	}
	/*
	 * Used to load all Facebook friends
	 * @param userID FB id of user
	 * @param accessToken facebook accesstoken
	 * @param callback instance of class on which we have to return
	 */
	public void loadAllFriends(final String userID, final String accessToken,
			final FriendList callback) {
		final Handler callingThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					Social linkObj = socialService.linkUserFacebookAccount(userID,
							accessToken);
					Social socialObj = socialService.getFacebookFriendsFromLinkUser(userID);
					final ArrayList<Friends> friendList =socialObj.getFriendList();
					callingThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callback != null) {
								callback.onFriendListFetched(friendList);
							}
						}
					});
				} catch (final Exception ex) {
					callingThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callback != null) {
								callback.onFbError(ex.toString());
							}
						}
					});
				}
			}
		}.start();
	}
	
	public void createUser(final String name, final String pswd,
			final String email, final App42ServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final User user = userService.createUser(name, pswd, email);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onUserCreated(user);
						}
					});
				} catch (final App42Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								System.out.println(ex.toString());
								callBack.onUserCreated(null);
							}
						}
					});

				}
			}
		}.start();
	}
	public void registerForPushNotification(Context context,final String userID) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		final String deviceId = GCMRegistrar.getRegistrationId(context);
		if (deviceId.equals("")) {
			GCMRegistrar.register(context, Constants.SenderId);
		} else {
			final Handler callerThreadHandler = new Handler();
			new Thread() {
				@Override
				public void run() {
					try {
						ServiceAPI sp = new ServiceAPI(
								Constants.App42ApiKey,
								Constants.App42ApiSecret);
						String userName = Constants.GameName+ userID;
						pushService.storeDeviceToken(userName, deviceId);
						callerThreadHandler.post(new Runnable() {
							@Override
							public void run() {

							}
						});
					} catch (Exception e) {

					}
				}
			}.start();
		}
	}
	public void authenticateUser(final String name, final String pswd,
			final App42ServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final App42Response response = userService.authenticate(
							name, pswd);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onUserAuthenticated(response);
						}
					});
				} catch (final App42Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								System.out.println(ex.toString());
								callBack.onUserAuthenticated(null);
							}
						}
					});
				}
			}
		}.start();
	}

	public void getUserGamesList(final String uname,
			final App42ServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					String collName = Constants.App42UserGamesCollectionPrefix
							+ uname;
					Storage response = storageService.findAllDocuments(
							Constants.App42DBName, collName);
					final ArrayList<JSONDocument> jsonDocList = response
							.getJsonDocList();
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onGetUserGamesList(Utilities
									.getJSONObjectsFromJSONDocuments(jsonDocList));
						}
					});
				} catch (App42Exception ex) {
					System.out.println(ex.toString());
					callBack.onGetUserGamesList(null);
				}
			}
		}.start();
	}
	
	public void createGameWithFbFriend(final String friendId,final String friendName,final String frindPicUrl,
			final FriendList callBack){

		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					final JSONObject gameObject = new JSONObject();
					gameObject.put(Constants.GameFirstUserKey, UserContext.MyUserName);
					gameObject.put(Constants.GameSecondUserKey, friendId);
					
					gameObject.put(Constants.GameFbName, UserContext.MyDisplayName);
					gameObject.put(Constants.GameFbFriendName, friendName);
					
					gameObject.put(Constants.GameMyPicUrl, UserContext.MyPicUrl);
					gameObject.put(Constants.GameFriendPicUrl, frindPicUrl);
					
					gameObject.put(Constants.GameStateKey,
							Constants.GameStateIdle);
					gameObject.put(Constants.GameBoardKey,Constants.GameIdleState);
					gameObject.put(Constants.GameWinnerKey, "");
					gameObject.put(Constants.GameNextMoveKey, friendId);
		
						gameObject.put(Constants.GameIdKey, java.util.UUID
								.randomUUID().toString());
						storageService.insertJSONDocument(
								Constants.App42DBName,
								Constants.App42UserGamesCollectionPrefix
										+ UserContext.MyUserName, gameObject.toString());
						storageService
								.insertJSONDocument(
										Constants.App42DBName,
										Constants.App42UserGamesCollectionPrefix
												+ friendId,
										gameObject.toString());


					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onCreateGame(true,gameObject,friendId);
						}
					});

				} catch (Exception e) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (callBack != null) {
								callBack.onCreateGame(false,null,null);
							}
						}
					});
				}
			}
		}.start();
	
	}

	public void createGame(final String uname1, final String remoteUserName,
			final App42ServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {

					final JSONObject gameObject = new JSONObject();
					gameObject.put(Constants.GameFirstUserKey, uname1);
					gameObject.put(Constants.GameSecondUserKey, remoteUserName);
					gameObject.put(Constants.GameStateKey,
							Constants.GameStateIdle);
					gameObject.put(Constants.GameBoardKey,
							Constants.GameIdleState);
					gameObject.put(Constants.GameWinnerKey, "");
					gameObject.put(Constants.GameNextMoveKey, uname1);
					gameObject.put(Constants.GameIdKey, java.util.UUID
							.randomUUID().toString());

					// Insert in to user1's game collection
					storageService.insertJSONDocument(Constants.App42DBName,
							Constants.App42UserGamesCollectionPrefix + uname1,
							gameObject.toString());
					// Insert in to user2's game collection
					storageService.insertJSONDocument(Constants.App42DBName,
							Constants.App42UserGamesCollectionPrefix
									+ remoteUserName, gameObject.toString());
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onCreateGame(gameObject);
						}
					});

				} catch (Exception e) {
					callBack.onCreateGame(null);
				}
			}
		}.start();
	}

	public void updateGame(final JSONObject newGameObj,
			final App42ServiceListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {

				try {
					String collName1 = Constants.App42UserGamesCollectionPrefix
							+ newGameObj.getString(Constants.GameFirstUserKey);
					String collName2 = Constants.App42UserGamesCollectionPrefix
							+ newGameObj.getString(Constants.GameSecondUserKey);
					String id = newGameObj.getString(Constants.GameIdKey);
					storageService.updateDocumentByKeyValue(
							Constants.App42DBName, collName1,
							Constants.GameIdKey, id, newGameObj.toString());
					storageService.updateDocumentByKeyValue(
							Constants.App42DBName, collName2,
							Constants.GameIdKey, id, newGameObj.toString());
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onUpdateGame(newGameObj);
						}
					});
				} catch (JSONException e) {
					callBack.onUpdateGame(null);
				}
			}
		}.start();
	}

	public void pushMessage(final JSONObject newGameObj, final String userName) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					pushService.sendPushMessageToUser(Constants.GameName
							+ userName, newGameObj.toString());
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {

						}
					});
				} catch (Exception e) {

				}
			}
		}.start();
	}

	public static interface App42ServiceListener {
		public void onUserCreated(User response);

		public void onUserAuthenticated(App42Response response);

		public void onGetUserGamesList(ArrayList<JSONObject> arrayList);

		public void onCreateGame(JSONObject createdGameObject);

		public void onUpdateGame(JSONObject updatedGameObject);
	}
}