package com.example.try_stage_down_game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Player {
	Bitmap bitmap, walkBitmap01, walkBitmap02, walkBitmap03, downbitmap, injureBmp;
	float x, y; //x座標與y座標
	int height, width; //圖片高寬
	Context context;
	int walkCount = 0;
	boolean isInjure=false;
	Thread injureThread;
	/**
	 * 建構子
	 * @param context 呼叫者的context 
	 * @param x 座標X
	 * @param y 圖片底部座標Y，尚需減圖片高才能得到座標Y
	 */
	public Player (Context context, int x ,int y, int height, int width){
		this.y =y;//圖片底部座標Y，尚需減圖片高才能得到座標Y
		this.x =x;//起始座標X
		this.context=context;
		//取得人物腳色圖片
		setPlayerBmpLeft();

				
		this.height=bitmap.getHeight();//圖片高
		this.width=bitmap.getWidth();//圖片寬

		this.y -= this.height; //起始座標Y
	}
	
	/**
	 * 繪圖動作
	 * @param canvas 要繪圖的畫布
	 * @param dy 圖片Y軸移動距離
	 * @param dx 圖片X軸移動距離
	 */
	public void draw(Canvas canvas, float dy, float dx) {
		
		this.y -= dy; //座標y變小，代表圖片往左移
		this.x -= dx; //座標x變小，代表圖片往上移
		
		if(isInjure){
			canvas.drawBitmap(injureBmp, x+=dx, y, null);
			walkCount = 0;
			return;
		}
		
		if(dx==0 && dy>=0){
			bitmap = walkBitmap02;
			canvas.drawBitmap(bitmap, x, y, null);
			walkCount = 0;
		}else if(dy<0){
			canvas.drawBitmap(downbitmap, x, y, null);
			walkCount = 0;
			
		//如果位移等於slide速度，代表玩家並沒有移動，只是地板在使人物動，因此要用靜止圖(walkBitmap02)
		//但是此方法的缺點是，SLIDERSPEED不能剛好是MoveSpeed的兩倍，
		//否則當玩家在移動時 MoveSpeed - SLIDERSPEED = SLIDERSPEED 會導致誤判為靜止。
		}else if(GameView.SLIDERSPEED==Math.abs(dx)){
			bitmap = walkBitmap02;
			canvas.drawBitmap(bitmap, x, y, null);
			walkCount = 0;
		}else{
			if(walkCount%2==0){
				bitmap = walkBitmap02;
			}else if(walkCount%3==0){
				bitmap = walkBitmap01;
			}else{
				bitmap = walkBitmap03;
			}
			canvas.drawBitmap(bitmap, x, y, null);
			walkCount++;
		}
	}
	
	public void draw(Canvas canvas, float dy, float dx, boolean isInjure){
		if(isInjure){
			this.isInjure = isInjure;
			
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Player.this.isInjure=false;
				}
			};
			
			if(injureThread!=null){
				injureThread.interrupt();
			}
			
			injureThread = new Thread(runnable);
			injureThread.start();

			draw(canvas, dy, dx);
		}else{	
			draw(canvas, dy, dx);
		}
	}
	
	public void updateBitmap(int type){
		if(type==GameView.LEFT){
			setPlayerBmpLeft();
		}else if(type==GameView.RIGHT){
			setPlayerBmpRifgt();
		}
	}
	
	private void setPlayerBmpLeft(){
		if(GameLevel.PLAYER_SEX==GameLevel.GIRL){
			bitmap = BitmapUtil.player_girl_left02_bitmap;
			walkBitmap01 = BitmapUtil.player_girl_left01_bitmap;
			walkBitmap02 = BitmapUtil.player_girl_left02_bitmap;
			walkBitmap03 = BitmapUtil.player_girl_left03_bitmap;
			downbitmap = BitmapUtil.player_girl_down_left_bitmap;
			injureBmp = BitmapUtil.player_girl_injure_left_bitmap;
		}else{
			bitmap = BitmapUtil.player_boy_left02_bitmap;
			walkBitmap01 = BitmapUtil.player_boy_left01_bitmap;
			walkBitmap02 = BitmapUtil.player_boy_left02_bitmap;
			walkBitmap03 = BitmapUtil.player_boy_left03_bitmap;
			downbitmap = BitmapUtil.player_boy_down_left_bitmap;
			injureBmp = BitmapUtil.player_boy_injure_left_bitmap;
		}
	}
	
	private void setPlayerBmpRifgt(){
		if(GameLevel.PLAYER_SEX==GameLevel.GIRL){
			bitmap = BitmapUtil.player_girl_right02_bitmap;
			walkBitmap01 = BitmapUtil.player_girl_right01_bitmap;
			walkBitmap02 = BitmapUtil.player_girl_right02_bitmap;
			walkBitmap03 = BitmapUtil.player_girl_right03_bitmap;
			downbitmap = BitmapUtil.player_girl_down_right_bitmap;
			injureBmp = BitmapUtil.player_girl_injure_right_bitmap;
		}else{
			bitmap = BitmapUtil.player_boy_right02_bitmap;
			walkBitmap01 = BitmapUtil.player_boy_right01_bitmap;
			walkBitmap02 = BitmapUtil.player_boy_right02_bitmap;
			walkBitmap03 = BitmapUtil.player_boy_right03_bitmap;
			downbitmap = BitmapUtil.player_boy_down_right_bitmap;
			injureBmp = BitmapUtil.player_boy_injure_right_bitmap;
		}
	}
}
