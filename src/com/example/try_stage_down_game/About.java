package com.example.try_stage_down_game;

import android.app.Activity;
import android.os.Bundle;
import com.irons.happy_down_stairs.R;

public class About extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setIsCountinueMusic(true);
		super.onBackPressed();
	}
}
