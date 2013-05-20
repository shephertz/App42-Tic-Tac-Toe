package com.App42.TicTacToe;

public class Constants {
	
//	public static final String App42ApiKey = "eeceeb1e90f752d4da288bb875736e9892e3f38c4d6b92c3bb3e06b9a9415369";
//  public static final String App42ApiSecret = "6d52fda93f041ccebf65e34077b2b0947978b64c3afaccdbf1003b6203564c15";
	//test
	//public static final String App42ApiKey = "8ae0074461823561d9733b2f6fb468d42f440df420de07e76dfd5fee3ca5aa39";
//	public static final String App42ApiSecret = "aefac12e2c3ea42728ebb6c1068df16c46b5f04ae57a6007118597a4068a2c79";
	
	//produsction
	public static final String App42ApiKey= "877bfeffba7b7c4beb3b6b751a29e3567f36753f5d9060393a1d41eed35d7259";
	public static final String App42ApiSecret="56fe9028f88f84c71faf7b93b35d1c516f5921e854a926696cfa9033c2bab636";
	public static final String FB_APP_ID = "201013583359923";
	
	public static final String SharedPrefUname = "logged_in_username";
	public static final String IntentUserName = "intentUserName";
	public static final String IntentGameObject = "intentGameobj";

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
	public static final String GameName="TicTacToe_";
	
	public static final char BoardTileEmpty = 'e';
	public static final char BoardTileCross = 'x';
	public static final char BoardTileCircle = 'o';
	
	public static final int INVALID_SELECTION = -1;
	public static final int SPLASH_DISPLAY_TIME = 5000;
	
	public static final String IsGameAlive="game_Alive";
	 /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.App42.TicTacToe.DISPLAY_MESSAGE";
  //  791120981398
    static final String SENDER_ID = "407227506834";
    static final String EXTRA_MESSAGE = "message";

}