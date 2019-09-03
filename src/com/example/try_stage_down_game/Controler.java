package com.example.try_stage_down_game;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.irons.happy_down_stairs.R;

public class Controler {
	/**
	 * 模擬鍵盤繪製
	 */
	private Bitmap e1, e2; //左與右方向鍵
	
	int height, width; 
	public static int bmpHeight, bmpWidth;
	
	public Controler(Context ct, int height, int width) {
		this.height = height;
		this.width = width;
		Resources res = ct.getResources();
		e1 = BitmapFactory.decodeResource(res, R.drawable.left_keyboard_btn);
		e2 = BitmapFactory.decodeResource(res, R.drawable.right_keyboard_btn);
		bmpHeight = e1.getHeight();
		bmpWidth = e1.getWidth();
		res = null;
	}

	public void paint(Canvas c) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		c.drawBitmap(e1, 0, height-bmpHeight, paint);
		c.drawBitmap(e2, width-bmpWidth, height-bmpHeight, paint);
	}
}
