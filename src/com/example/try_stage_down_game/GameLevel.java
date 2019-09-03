package com.example.try_stage_down_game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.irons.happy_down_stairs.R;

public class GameLevel extends BaseActivity implements OnClickListener {
	ImageView imageView, imageView2, imageView3, imageView4;// 有四關圖片
	Button playerSelectorBtn;
	GameView rv;
//	Bitmap background;// 背景圖
	final int LEVELPAGE =0;
	final int GAMEVIEW=1;
	int layer=LEVELPAGE;
	int level;
	int vWidth, vHeight; 
	LinearLayout play_girl_image_place, play_boy_image_place;
	ImageView check_imageview;
	final public static int GIRL=0;
	final public static int BOY=1;
	public static int PLAYER_SEX = GIRL;
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			onBackPressed();
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 無標題
		setContentView(R.layout.game_level);
		
		DisplayMetrics dm = new DisplayMetrics();
		//取得螢幕大小
		GameLevel.this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		vWidth = dm.widthPixels;  //螢幕寬
		vHeight = dm.heightPixels; //螢幕高
		
		GameView.gameStop=false;
//		layer=GAMEVIEW;
		
		playerSelectorBtn = (Button)findViewById(R.id.player_selector_btn);
		check_imageview = new ImageView(this);
		check_imageview.setBackgroundResource(R.drawable.check_btn);
		check_imageview.setLayoutParams(new LayoutParams(vWidth/7*2, vHeight/7));
//		check_imageview.set
		play_girl_image_place = (LinearLayout)findViewById(R.id.play_girl_image_place);
		play_boy_image_place = (LinearLayout)findViewById(R.id.play_boy_image_place);

		if(PLAYER_SEX==GIRL)
			play_girl_image_place.addView(check_imageview);
		else
			play_boy_image_place.addView(check_imageview);
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		 options.inSampleSize = 1;
		 options.inJustDecodeBounds = false;
		GameView.nextBackground = BitmapFactory.decodeResource(
				GameLevel.this.getResources(), R.drawable.new_bg1, options);

		playerSelectorBtn.setOnClickListener(this);
		// 記錄使用者玩到第幾關
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		level = preferences.getInt("level", 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//取出圖片資源
		
		AudioUtil.PauseMusic();	
		
		GameView.gameStop=false;
		layer=GAMEVIEW;
		
		DisplayMetrics dm = new DisplayMetrics();
		//取得螢幕大小
		GameLevel.this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		
		//啟動遊戲，配置參數，最後的參數代表關卡編號
		rv = new GameView(GameLevel.this, GameView.nextBackground, vHeight, vWidth, 0);
		setContentView(rv);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == event.ACTION_DOWN && event.getY()>vHeight/2 && event.getY()<=vHeight/4*3){
			if(event.getX()<vWidth/2){
				play_boy_image_place.removeAllViews();
				play_girl_image_place.removeAllViews();
				play_girl_image_place.addView(check_imageview);
				PLAYER_SEX=0;
			}else{
				play_boy_image_place.removeAllViews();
				play_girl_image_place.removeAllViews();
				play_boy_image_place.addView(check_imageview);
				PLAYER_SEX=1;
			}
		}
		return super.onTouchEvent(event);		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setIsCountinueMusic(true);
		if(layer==LEVELPAGE){
			super.onBackPressed();
		}else{
			AudioUtil.PauseMusic();
			AudioUtil.initMusic();
			AudioUtil.PlayMusic();
			GameView.gameFlag=false;
			layer=LEVELPAGE;
			while(!GameView.gameStop){
				;
			}	
			super.onBackPressed();
		}		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		GameView.gameFlag=false;
//		if(layer==GAMEVIEW)
//			setIsCountinueMusic(true);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		GameView.gameFlag=true;
		super.onResume();
	}
}
