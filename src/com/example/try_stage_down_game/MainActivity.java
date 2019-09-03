package com.example.try_stage_down_game;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.irons.happy_down_stairs.R;


public class MainActivity extends BaseActivity {
	Button button, button2, button3, button4;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        AudioUtil.init(this);
        
        ColorFilterBuilder.init();
        
        button=(Button)findViewById(R.id.bbutton1);
        button2=(Button)findViewById(R.id.bbutton2);
        button3=(Button)findViewById(R.id.bbutton3);
        button4=(Button)findViewById(R.id.bbutton4);
        
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setIsCountinueMusic(true);
				Intent intent = new Intent(MainActivity.this, GameLevel.class);
				startActivity(intent);
			}
		});
        
        button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setIsCountinueMusic(true);
				Intent intent = new Intent(MainActivity.this, Rank.class);
				startActivity(intent);
			}
		});
        
        button3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//				builder.setTitle("版權說明");
//				builder.setMessage("作者:");
//				builder.setNegativeButton("關閉", null);
//				builder.show();
				
				setIsCountinueMusic(true);
				Intent intent = new Intent(MainActivity.this, About.class);
				startActivity(intent);
			}
		});
        
        button4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    }
    

}
