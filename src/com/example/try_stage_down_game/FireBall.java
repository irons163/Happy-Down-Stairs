package com.example.try_stage_down_game;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class FireBall {
	Bitmap bitmap;
	int width;
	int whichForFireBall;
	float x=0;
	public float y=0;
	public int fireballHeight;
	public int fireballWidth;
	
	public FireBall(int width){
		this.width = width;
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		 options.inSampleSize = 20;
		 options.inJustDecodeBounds = false;
		bitmap = BitmapUtil.fire_ball;
		fireballHeight = bitmap.getHeight();
		fireballWidth = bitmap.getWidth();
		
		Random random = new Random();
		whichForFireBall = random.nextInt(3);
		
		switch (whichForFireBall) { //出現火球的地方
		case 0:
			x=width/6*1; //螢幕1/6處
			break;
		case 1:
			x=width/6*3; //1/2處
			break;
		case 2:
			x=width/6*5; //5/6處
			break;
		}
	}
	
	public void draw(Canvas canvas, float dy, float dx) {
		
		this.y -= dy; //座標y變小，代表圖片往左移
		this.x -= dx; //座標x變小，代表圖片往上移
		canvas.drawBitmap(bitmap, x, y, null);
	}
}
