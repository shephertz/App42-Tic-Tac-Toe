package com.App42.TicTacToe;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.user.User;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GamesListAdapter extends BaseAdapter implements AsyncApp42ServiceApi.App42ServiceListener
{
	private AsyncApp42ServiceApi asyncService;	
	private String userName;
	private ArrayList<JSONObject> gamesList;
	private Context context;
	
	public GamesListAdapter(Context context, String userName) {

    	this.asyncService = AsyncApp42ServiceApi.instance();    	
    	this.userName = userName;
    	this.context = context;    	
    	gamesList = new ArrayList<JSONObject>();
    	this.asyncService.getUserGamesList(userName, this);
	}

	public void beginNewGame(String opponentName) {
		asyncService.createGame(userName, opponentName, this);		
	}
	
	// BaseAdapter overrides
	@Override
	public int getCount() {		
		return gamesList.size();
	}

	@Override
	public Object getItem(int position) {
		return gamesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		//TODO how to map uuid string to long
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.gamelist_item, null);
        }
        JSONObject item = gamesList.get(position);
        if (item != null) {
        	
        	String u1Name = "", u2Name = "", winner = "", nextTurn = "", state = "";
        	
			try {
				u1Name = item.getString(Constants.GameFirstUserKey);
				u2Name = item.getString(Constants.GameSecondUserKey);
				winner = item.getString(Constants.GameWinnerKey);
				nextTurn = item.getString(Constants.GameNextMoveKey);
				state = item.getString(Constants.GameStateKey);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	TextView tvOppName = (TextView) convertView.findViewById(R.id.opp_name);
        	
        	if(u1Name.equalsIgnoreCase(userName))
        	{        		
        		tvOppName.setText(u2Name);
        	}
        	else
        	{
        		tvOppName.setText(u1Name);
        	}
        	
        	TextView tvState = (TextView) convertView.findViewById(R.id.game_state);
        	if(state.equals(Constants.GameStateFinished))
        	{
	        	if(!winner.isEmpty())
	        	{
	        		if(winner.equals(userName)) {	        			
	        			tvState.setText("You won!");
	        		}
	        		else
	        		{
	        			tvState.setText("You lost :(");
	        		}
	        	}
	        	else
	        	{
	        		tvState.setText("match drawn");
	        	}
        	}
        	else if(nextTurn.equalsIgnoreCase(userName))
        	{
        		tvState.setText("your turn");
        	}
        	else
        	{
        		tvState.setText(nextTurn+"'s turn");
        	}        	
        }
        
        return convertView;	
    }

	// App42 async callBacks
	@Override
	public void onUserCreated(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserAuthenticated(App42Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetUserGamesList(final ArrayList<JSONObject> gamesList) {
    	if(gamesList == null)
    	{
    		((UserHomeActivity)GamesListAdapter.this.context).onOperationFail();    		
    	}            	  
    	else
    	{
    		GamesListAdapter.this.gamesList = gamesList;
    		GamesListAdapter.this.notifyDataSetChanged();
    		((UserHomeActivity)GamesListAdapter.this.context).onOperationSuccess();
    	}		
	}

	@Override
	public void onCreateGame(final JSONObject game) {

    	if(game != null)
    	{
        	System.out.println(game.toString());
        	GamesListAdapter.this.gamesList.add(game);
        	GamesListAdapter.this.notifyDataSetChanged();
        	((UserHomeActivity)GamesListAdapter.this.context).onOperationSuccess();
    	}
    	else
    	{
    		((UserHomeActivity)GamesListAdapter.this.context).onOperationFail();
    	}		
	}

	@Override
	public void onUpdateGame(final JSONObject game) {
		System.out.println(game.toString());		
	}

	public void refreshNewUserList(String newUserName) {
		this.userName = newUserName;
		this.asyncService.getUserGamesList(userName, this);		
	}
	
}