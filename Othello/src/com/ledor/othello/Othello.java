package com.ledor.othello;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

public class Othello extends Activity {

    OthelloGUI mBoardGUI;
    boolean mIsHighScoreSaved = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preference_setting, false);
        mBoardGUI = new OthelloGUI(this);
        setContentView(mBoardGUI);
        registerForContextMenu(mBoardGUI); 
        
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int score = prefs.getInt("high_score", 0);
		if (score > 0)
		{
			mIsHighScoreSaved = true;
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_othello, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
      case R.id.menu_new:
    	mBoardGUI.newGame();
        return true;
      case R.id.menu_highscore:
    	  ShowHighScore();
      return true;   
      case R.id.menu_settings:
    	  mBoardGUI.newGame();
    	  Intent intent = new Intent(Othello.this, Setting.class);
    	  startActivity(intent);
        return true;   
      default:
        return super.onOptionsItemSelected(item);
      }
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (mIsHighScoreSaved)
        {
            menu.getItem(1).setEnabled(true);
        }
        else
        {
        	menu.getItem(1).setEnabled(false);
        }
        
        return true;
    }
    
    /*
     * Set the High Score indicator to enable Menu Item "High Score"
     */
    public void setHighScoreInd(boolean saved)
    {
    	mIsHighScoreSaved = saved;
    }
    
    /*
     * Show high score information
     */
    private void ShowHighScore()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	String highUser = prefs.getString("high_user", "Gamer");
    	String highLevel = prefs.getString("high_level", "Beginner");
    	int highScore = prefs.getInt("high_score", 0);
    	String message = highUser + " got a high score of " + highScore + " at " + highLevel + " level.";
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(message)
	         .setCancelable(true)
	         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {
	            	 dialog.cancel();
	             }
	         });
	  AlertDialog alert = builder.create();
	  alert.show();    	
    }
}
    
