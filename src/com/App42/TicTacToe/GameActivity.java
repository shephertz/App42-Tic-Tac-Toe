package com.App42.TicTacToe;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.user.User;

public class GameActivity extends Activity implements
		AsyncApp42ServiceApi.App42ServiceListener {

	private JSONObject gameObject;
	private int selectedCell = Constants.InvalidSelection;
	private String localUserName;
	private String remoteUserName;
	private int localUserCellImageId;
	private AsyncApp42ServiceApi asyncService;
	private char localUserTile;
	private String currentState;
	private String nextTurn;
	private ProgressDialog progressDialog;
	private ImageButton selectedButton = null;
	private boolean fromNotification=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		this.asyncService = AsyncApp42ServiceApi.instance();
		Intent intent = getIntent();
		try {
			
			if(GCMIntentService.isFromNotification){
				localUserName = getUserName();
				gameObject = new JSONObject(GCMIntentService.notificationMessage);
			}
			else{
			localUserName = intent.getStringExtra(Constants.IntentUserName);
			gameObject = new JSONObject(
					intent.getStringExtra(Constants.IntentGameObject));
			}
			this.initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onStart() {
		super.onStart();
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Constants.DisplayMessageAction));
		
	}
/*
 * This function allows to intialize the game 
 */
	private void initialize() {
		try {
			String u1Name = gameObject.getString(Constants.GameFirstUserKey);
			String u2Name = gameObject.getString(Constants.GameSecondUserKey);
			String winner = gameObject.getString(Constants.GameWinnerKey);
			currentState = gameObject.getString(Constants.GameStateKey);
			nextTurn = gameObject.getString(Constants.GameNextMoveKey);
			checkAndUpdateUserNAme(u1Name,u2Name);
			if (u1Name.equalsIgnoreCase(localUserName)) {
				localUserCellImageId = R.drawable.cross_cell;
				localUserTile = Constants.BoardTileCross;
				remoteUserName = u2Name;
			} else {
				localUserCellImageId = R.drawable.circle_cell;
				localUserTile = Constants.BoardTileCircle;
				remoteUserName = u1Name;
			}

			((Button) this.findViewById(R.id.submit)).setClickable(false);

			this.drawImagesForBoard(gameObject
					.getString(Constants.GameBoardKey));
			if (currentState.equals(Constants.GameStateFinished)) {
				((Button) this.findViewById(R.id.rematch))
						.setVisibility(View.VISIBLE);
				((Button) this.findViewById(R.id.submit))
						.setVisibility(View.INVISIBLE);
				if (winner.isEmpty()) {
					((TextView) this.findViewById(R.id.status))
							.setText("Match Drawn!");
				} else if (winner.equalsIgnoreCase(localUserName)) {
					((TextView) this.findViewById(R.id.status))
							.setText("You Won!");
				} else {
					((TextView) this.findViewById(R.id.status))
							.setText("You Lost :(");
				}
			} else {
				((Button) this.findViewById(R.id.rematch))
						.setVisibility(View.INVISIBLE);
				((TextView) this.findViewById(R.id.status)).setText("");
				if (nextTurn.equalsIgnoreCase(localUserName)) {
					((TextView) this.findViewById(R.id.status))
							.setText("Your turn.");
					((Button) this.findViewById(R.id.submit))
							.setVisibility(View.VISIBLE);
				} else {
					((TextView) this.findViewById(R.id.status))
							.setText("Waiting for opponent to move");
					((Button) this.findViewById(R.id.submit))
							.setVisibility(View.INVISIBLE);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * This function validate user whether from face-book or App42
	 */
	private void checkAndUpdateUserNAme(String u1Name,String u2Name){
		if(u1Name.equals(UserContext.myUserName)||
				u2Name.equals(UserContext.myUserName)){
			localUserName=UserContext.myUserName;
		}
	}
	

	/*
	 * This function allows to play game again if game is finished
	 */
	public void onRematchClicked(View view) {
		progressDialog = ProgressDialog.show(this, "", "creating game..");
		progressDialog.setCancelable(true);
		try {
			gameObject.put(Constants.GameFirstUserKey, localUserName);
			gameObject.put(Constants.GameSecondUserKey, remoteUserName);
			gameObject.put(Constants.GameStateKey,
					Constants.GameStateIdle);
			gameObject.put(Constants.GameBoardKey,
					Constants.GameIdleState);
			gameObject.put(Constants.GameWinnerKey, "");
			gameObject.put(Constants.GameNextMoveKey, localUserName);
			asyncService.updateGame(gameObject, this);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*
	 * This function allows to handle event on submit button
	 */
	public void onSubmitClicked(View view) {
		try {
			String boardState = gameObject.getString(Constants.GameBoardKey);
			String prefix = "";
			if (selectedCell > 0) {
				prefix = boardState.substring(0, selectedCell);
			}

			String suffix = "";
			if (selectedCell < 8) {
				suffix = boardState.substring(selectedCell + 1);
			}

			String newBoardState = prefix + localUserTile + suffix;
			gameObject.put(Constants.GameBoardKey, newBoardState);
			gameObject.put(Constants.GameNextMoveKey, remoteUserName);
			if (isGameOver(newBoardState)) {
				gameObject.put(Constants.GameStateKey,
						Constants.GameStateFinished);
				gameObject.put(Constants.GameWinnerKey, localUserName);
			} else if (isBoardFull(newBoardState)) {
				gameObject.put(Constants.GameStateKey,
						Constants.GameStateFinished);
				gameObject.put(Constants.GameWinnerKey, "");
			} else {
				gameObject.put(Constants.GameStateKey,
						Constants.GameStateActive);
			}
			progressDialog = ProgressDialog.show(this, "", "sending move");
			asyncService.updateGame(gameObject, this);
			asyncService.pushMessage(gameObject, remoteUserName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This function checks the status of game
	 */
	private boolean isGameOver(String boardState) {
		// Check rows
		if (Utilities.areCharsEqual(boardState.charAt(0), boardState.charAt(1),
				boardState.charAt(2), localUserTile)
				|| Utilities.areCharsEqual(boardState.charAt(3),
						boardState.charAt(4), boardState.charAt(5),
						localUserTile)
				|| Utilities.areCharsEqual(boardState.charAt(6),
						boardState.charAt(7), boardState.charAt(8),
						localUserTile)) {
			return true;
		}

		// Check columns
		if (Utilities.areCharsEqual(boardState.charAt(0), boardState.charAt(3),
				boardState.charAt(6), localUserTile)
				|| Utilities.areCharsEqual(boardState.charAt(1),
						boardState.charAt(4), boardState.charAt(7),
						localUserTile)
				|| Utilities.areCharsEqual(boardState.charAt(2),
						boardState.charAt(5), boardState.charAt(8),
						localUserTile)) {
			return true;
		}

		// Check diagonals
		if (Utilities.areCharsEqual(boardState.charAt(0), boardState.charAt(4),
				boardState.charAt(8), localUserTile)
				|| Utilities.areCharsEqual(boardState.charAt(2),
						boardState.charAt(4), boardState.charAt(6),
						localUserTile)) {
			return true;
		}

		return false;
	}

	/*
	 * This function checks the board is full or not
	 */
	private boolean isBoardFull(String boardState) {
		for (int i = 0; i < 9; i++) {
			if (boardState.charAt(i) == Constants.BoardTileEmpty) {
				return false;
			}
		}

		return true;
	}

	/*
	 * This function handle click event when cell is clicked
	 */
	public void onCellClicked(View view) {

		((Button) this.findViewById(R.id.submit)).setClickable(true);
		if (this.selectedButton != null) {
			this.selectedButton.setImageResource(R.drawable.empty_cell);
		}
		((ImageButton) view).setImageResource(localUserCellImageId);
		this.selectedButton = (ImageButton) view;
		this.selectedCell = getCellIndexFromView(view);
	}

	/*
	 * This function returns the index of cell that is clicked
	 */
	private int getCellIndexFromView(View view) {
		int viewId = view.getId();
		switch (viewId) {
		case R.id.cell_00:
			return 0;
		case R.id.cell_01:
			return 1;
		case R.id.cell_02:
			return 2;
		case R.id.cell_10:
			return 3;
		case R.id.cell_11:
			return 4;
		case R.id.cell_12:
			return 5;
		case R.id.cell_20:
			return 6;
		case R.id.cell_21:
			return 7;
		case R.id.cell_22:
			return 8;
		}
		return 0;
	}

	private void drawImagesForBoard(String boardState) {
		setupButton(boardState.charAt(0),
				(ImageButton) this.findViewById(R.id.cell_00));
		setupButton(boardState.charAt(1),
				(ImageButton) this.findViewById(R.id.cell_01));
		setupButton(boardState.charAt(2),
				(ImageButton) this.findViewById(R.id.cell_02));
		setupButton(boardState.charAt(3),
				(ImageButton) this.findViewById(R.id.cell_10));
		setupButton(boardState.charAt(4),
				(ImageButton) this.findViewById(R.id.cell_11));
		setupButton(boardState.charAt(5),
				(ImageButton) this.findViewById(R.id.cell_12));
		setupButton(boardState.charAt(6),
				(ImageButton) this.findViewById(R.id.cell_20));
		setupButton(boardState.charAt(7),
				(ImageButton) this.findViewById(R.id.cell_21));
		setupButton(boardState.charAt(8),
				(ImageButton) this.findViewById(R.id.cell_22));

	}

	private void setupButton(char boardTile, ImageButton button) {
		if (boardTile == Constants.BoardTileEmpty) {
			if (!currentState.equals(Constants.GameStateFinished)
					&& nextTurn.equals(localUserName)) {
				button.setClickable(true);
			} else {
				button.setClickable(false);
			}
			button.setImageResource(R.drawable.empty_cell);
		} else if (boardTile == Constants.BoardTileCircle) {
			button.setClickable(false);
			button.setImageResource(R.drawable.circle_cell);
		} else {
			button.setClickable(false);
			button.setImageResource(R.drawable.cross_cell);
		}
	}

	@Override
	public void onUserCreated(User response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserAuthenticated(App42Response response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetUserGamesList(ArrayList<JSONObject> arrayList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateGame(final JSONObject createdGameObject) {
		if (createdGameObject != null) {
			progressDialog.dismiss();
			GameActivity.this.gameObject = createdGameObject;
			GameActivity.this.initialize();
		}

	}

	/*
	 * This function retrives user name when notification receives
	 */
	private String getUserName() {
		SharedPreferences mPrefs = getSharedPreferences(
				MainActivity.class.getName(), MODE_PRIVATE);
		return mPrefs.getString(Constants.SharedPrefUname, null);
	}

	

	/*
	 * This method receives push notification and update the game
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			try {
				localUserName = getUserName();
				gameObject = new JSONObject(intent.getExtras().getString(
						Constants.NotificationMessage));
				GameActivity.this.selectedButton = null;
				initialize();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};



	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mHandleMessageReceiver);
	}

	@Override
	public void onUpdateGame(final JSONObject updatedGameObject) {
		if (updatedGameObject != null) {
			progressDialog.dismiss();
			GameActivity.this.gameObject = updatedGameObject;
			GameActivity.this.initialize();
		}
	}

}