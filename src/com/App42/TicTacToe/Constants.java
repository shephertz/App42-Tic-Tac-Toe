package com.App42.TicTacToe;

/*
 * This class contains all constants variables that require in application
 */
public class Constants {

	 static final String App42ApiKey = "<Your API KEY>";
	 static final String App42ApiSecret = "<YOUR SECRET KEY>";
	 static final String FbAppId = "146820035504808";

	 static final String SharedPrefUname = "logged_in_username";
	 static final String SharedPrefPassword = "logged_in_password";
	 static final String SharedPrefEmail = "logged_in_Email";
	 static final String IntentUserName = "intentUserName";
	 static final String IntentGameObject = "intentGameobj";
	 static final String TicTacToePref="App42TicTacToePreferences";
	 static final String App42UserStorageId = "user_storage_id";
	 static final String App42UserStoreGameListName = "games_list";
	 static final String App42DBName = "tictactoe2";
	 static final String App42UserGamesCollectionPrefix = "games_";

	 static final String GameFirstUserKey = "user_one";
	 static final String GameSecondUserKey = "user_two";
	 static final String GameStateKey = "state";
	 static final String GameBoardKey = "board";
	 static final String GameIdKey = "game_id";
	 static final String GameWinnerKey = "winner";
	 static final String GameNextMoveKey = "next";
	 static final String GameIdleState = "eeeeeeeee";

	 static final String GameStateIdle = "idle";
	 static final String GameStateActive = "active";
	 static final String GameStateFinished = "finished";
	 static final String GameName = "TicTacToe_";

	 static final char BoardTileEmpty = 'e';
	 static final char BoardTileCross = 'x';
	 static final char BoardTileCircle = 'o';
	
	 static final String GameFbName="me";
	 static final String GameFbFriendName="friend";
	 static final String GameMyPicUrl="my_Pic";
	 static final String GameFriendPicUrl="friend_Pic";

	 static final int SplashDisplayTime = 5000;

	/**
	 * Intent used to display a message in the screen.
	 */
	 static final String DisplayMessageAction = "com.App42.TicTacToe.DISPLAY_MESSAGE";
	 static final String ProjectNo = "<YOUR PROJECT NO>";
	 static final String NotificationMessage = "message";
	 static final String FromNotification="yes";
	 static final int InvalidSelection = 0;

}