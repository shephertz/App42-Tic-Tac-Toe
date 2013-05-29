package com.App42.TicTacToe;

/*
 * This class contains all constants variables that require in application
 */
public class Constants {

	public static final String App42ApiKey = "877bfeffba7b7c4beb3b6b751a29e3567f36753f5d9060393a1d41eed35d7259";
	public static final String App42ApiSecret = "56fe9028f88f84c71faf7b93b35d1c516f5921e854a926696cfa9033c2bab636";
	public static final String FB_APP_ID = "146820035504808";

	public static final String SharedPrefUname = "logged_in_username";
	public static final String SharedPrefPassword = "logged_in_password";
	public static final String SharedPrefEmail = "logged_in_Email";
	public static final String IntentUserName = "intentUserName";
	public static final String IntentGameObject = "intentGameobj";
	public static final String TicTacToePref="App42TicTacToePreferences";
	public static final String App42UserStorageId = "user_storage_id";
	public static final String App42UserStoreGameListName = "games_list";
	public static final String App42DBName = "tictactoe2";
	public static final String App42UserGamesCollectionPrefix = "games_";

	public static final String GameFirstUserKey = "user_one";
	public static final String GameSecondUserKey = "user_two";
	public static final String GameStateKey = "state";
	public static final String GameBoardKey = "board";
	public static final String GameIdKey = "game_id";
	public static final String GameWinnerKey = "winner";
	public static final String GameNextMoveKey = "next";
	public static final String GameIdleState = "eeeeeeeee";

	public static final String GameStateIdle = "idle";
	public static final String GameStateActive = "active";
	public static final String GameStateFinished = "finished";
	public static final String GameName = "TicTacToe_";

	public static final char BoardTileEmpty = 'e';
	public static final char BoardTileCross = 'x';
	public static final char BoardTileCircle = 'o';
	
	public static final String GameFbName="me";
	public static final String GameFbFriendName="friend";
	public static final String GameMyPicUrl="my_Pic";
	public static final String GameFriendPicUrl="friend_Pic";

	public static final int INVALID_SELECTION = -1;
	public static final int SPLASH_DISPLAY_TIME = 5000;

	/**
	 * Intent used to display a message in the screen.
	 */
	static final String DisplayMessageAction = "com.App42.TicTacToe.DISPLAY_MESSAGE";
	static final String SenderId = "407227506834";
	static final String NotificationMessage = "message";
	static final String FromNotification="yes";

}