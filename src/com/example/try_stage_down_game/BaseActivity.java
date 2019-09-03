package com.example.try_stage_down_game;

import android.app.Activity;

public class BaseActivity extends Activity{
	
    boolean musciCountinue = false;
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	if(!musciCountinue)
    		AudioUtil.PauseMusic();
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	if(!musciCountinue)
    		AudioUtil.PlayMusic();
		else
			musciCountinue=false;
    }
    
	protected void setIsCountinueMusic(boolean countinue) {
		musciCountinue = countinue;
	}
}
