package com.example.try_stage_down_game;

import java.util.Random;
import java.util.logging.Level;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Footboard {
	Context context;
	float x, y;
	Bitmap bitmap, bitmap1, bitmap2, bitmap3; //繪製圖片與暫存圖片兩張
	int height, width;
	int which; //哪種地板
	int animStep; //記數，為了產生圖片變化
	public static final int NOTOOL =0;
	public static final int BOMB =1;
	public static final int CURE =2;
	public static final int BOMB_EXPLODE =3;
	public static final int EAT_MAN_TREE =4;
	public int toolNum = NOTOOL; 
	
	private ToolUtil toolUtil;
	
	/**
	 * 建構子
	 * @param context 呼叫者的context
	 * @param x 座標X
	 * @param y 座標Y
	 * @param height 地板圖片的高
	 * @param width 地板圖片的寬
	 */
	public Footboard(Context context, int x, int y, int height, int width, int currentLevel) {
		this.context = context;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		Random r = new Random();
		
		if(currentLevel >= GameView.MOVING_START_LEVEL){
			which = r.nextInt(7); //隨機 0~5，目前每種地板出現機率依樣，可做更動。
			 if (which == 0||which == 6) {  //普通地板
				 which = 0;
					bitmap = BitmapUtil.footboard_normal_bitmap;
			 } else if (which == 1) { //往左地板
					bitmap1 = BitmapUtil.footboard_moving_left1_bitmap;
					bitmap2 = BitmapUtil.footboard_moving_left2_bitmap;
					bitmap3 = BitmapUtil.footboard_moving_left3_bitmap;
					bitmap = bitmap1;
			 } else if (which == 2){ //往右地板
					bitmap = BitmapUtil.footboard_moving_right1_bitmap;
			 }else if(which == 3){ //不穩定地板
					bitmap1 = BitmapUtil.footboard_unstable1_bitmap;
					bitmap2=BitmapUtil.footboard_unstable2_bitmap;
					bitmap3=BitmapUtil.footboard_unstable3_bitmap;
					bitmap = bitmap1;
			 }else if(which==4){ //滑動地板
	//			 bitmap = BitmapUtil.footboard_spring_bitmap;
					bitmap1 = BitmapUtil.footboard_wood_bitmap;
					bitmap2=BitmapUtil.footboard_wood2_bitmap;
					bitmap3=BitmapUtil.footboard_wood3_bitmap;
				 bitmap = BitmapUtil.footboard_wood_bitmap;
			 }else if(which==5){ //陷阱地板
				 bitmap = BitmapUtil.footboard_spiked_bitmap;
			 }
		}else if(currentLevel >= GameView.WOOD_START_LEVEL){
			which = r.nextInt(6); //隨機 0~5，目前每種地板出現機率依樣，可做更動。
			 if (which == 0) {  //普通地板
				 which = 0;
				 bitmap = BitmapUtil.footboard_normal_bitmap;
			 } else if (which == 1) { //往左地板
				 which = 0;
				 bitmap = BitmapUtil.footboard_normal_bitmap;
			 } else if (which == 2){ //往右地板
				 which = 0;
				 bitmap = BitmapUtil.footboard_normal_bitmap;
			 }else if(which == 3){ //不穩定地板
					bitmap1 = BitmapUtil.footboard_unstable1_bitmap;
					bitmap2=BitmapUtil.footboard_unstable2_bitmap;
					bitmap3=BitmapUtil.footboard_unstable3_bitmap;
					bitmap = bitmap1;
			 }else if(which==4){ //滑動地板
	//			 bitmap = BitmapUtil.footboard_spring_bitmap;
					bitmap1 = BitmapUtil.footboard_wood_bitmap;
					bitmap2=BitmapUtil.footboard_wood2_bitmap;
					bitmap3=BitmapUtil.footboard_wood3_bitmap;
				 bitmap = BitmapUtil.footboard_wood_bitmap;
			 }else if(which==5){ //陷阱地板
				 bitmap = BitmapUtil.footboard_spiked_bitmap;
			 }
		}else{
			which = r.nextInt(6); //隨機 0~5，目前每種地板出現機率依樣，可做更動。
			 if (which == 0) {  //普通地板
					bitmap = BitmapUtil.footboard_normal_bitmap;
			 } else if (which == 1) { //往左地板
				 which = 0;
				 bitmap = BitmapUtil.footboard_normal_bitmap;
			 } else if (which == 2){ //往右地板
				 which = 0;
				 bitmap = BitmapUtil.footboard_normal_bitmap;
			 }else if(which == 3){ //不穩定地板
				 which = 0;
				 bitmap = BitmapUtil.footboard_normal_bitmap;
			 }else if(which==4){ //滑動地板
				 which = 5;
				 bitmap = BitmapUtil.footboard_spiked_bitmap;
			 }else if(which==5){ //陷阱地板
				 bitmap = BitmapUtil.footboard_spiked_bitmap;
			 }
		}
		 
		 if(currentLevel>=GameView.TOOL_EAT_MAN_TREE_START_LEVEL){
			 int random = r.nextInt(30);
		 
			 if(random==1){
				 toolNum=1;
			 }else if(random==2||random==3||random==4){
				 toolNum=2;
			 }else if(random==5||random==6){
				 toolNum=4;
			 }else{
				 toolNum=0;
			 }
		 }else if(currentLevel>=GameView.TOOL_BOMB_START_LEVEL){
			 int random = r.nextInt(30);
		 
			 if(random==1){
				 toolNum=1;
			 }else if(random==2||random==3||random==4){
				 toolNum=2;
			 }else{
				 toolNum=0;
			 }
		 }else if(currentLevel>=GameView.TOOL_CURE_START_LEVEL){
			 int random = r.nextInt(30);
		 
			 if(random==1){
				 toolNum=2;
			 }else if(random==2||random==3||random==4){
				 toolNum=2;
			 }else{
				 toolNum=0;
			 }
		 }else{
			 toolNum=0;
		 }
	}

	
	/**
	 * 繪圖動作
	 * @param canvas 要繪圖的畫布
	 * @param dy 圖片Y軸移動距離
	 */
	public void draw(Canvas canvas, float dy) {
		
		this.y -= dy;
//		Rect rect1 = new Rect(x,y,x+width,y+height);	
		RectF rect1 = new RectF(x,y,x+width,y+height);
		
		if(which == 1){ //往左地板
			//圖片有兩張，進行切換，但這裡的方法很笨，應該有更好的方法
//			if(animStep<10){
//				bitmap = bitmap1;
//				animStep++;
//			}else if(animStep>=10 && animStep<19){
//				bitmap = bitmap2;
//				animStep++;
//			}else{
//				animStep=0;
//			}
			
			if(animStep%3==0){
				bitmap = BitmapUtil.footboard_moving_left1_bitmap;
				animStep++;
			}else if(animStep%3==1){
				bitmap = BitmapUtil.footboard_moving_left2_bitmap;
				animStep++;
			}else{
				bitmap = BitmapUtil.footboard_moving_left3_bitmap;
				animStep=0;
			}
			
		}else if(which == 2){
			if(animStep%3==0){
				bitmap = BitmapUtil.footboard_moving_right1_bitmap;
				animStep++;
			}else if(animStep%3==1){
				bitmap = BitmapUtil.footboard_moving_right2_bitmap;
				animStep++;
			}else{
				bitmap = BitmapUtil.footboard_moving_right3_bitmap;
				animStep=0;
			}
		}else if(which == 3){ //不穩定地板
			if(animStep<10){
				bitmap = bitmap1; //初始圖片
			}else if(animStep<20){
				bitmap = bitmap2; //產生裂痕
			}else if(animStep<28){
				bitmap = bitmap3; //產生裂痕
			}else if(animStep>=30){
				bitmap=null; //地板不見
			}
		}else if(which == 4){ //朽木地板
			if(animStep%10==0){
				bitmap = bitmap1;
			}else if(animStep%10==1){
				bitmap = bitmap2;
			}else if(animStep%10==3){
				bitmap = bitmap3;
			}else if(animStep%10==5){
				animStep=0;
				bitmap=null;
			}
		}
		
		if(bitmap!=null) 
		canvas.drawBitmap(bitmap, null, rect1, null);
	}
	
	/**
	 * 計算地板甚麼時候不見，只在不穩定和朽木地板的情況下使用。
	 */
	public void setCount(){
		if(which==3 || which==4){
		animStep++;}
	}
	
	public void setWhich(int witch){
		this.which = witch;
		
		 if (which == 0) {  //普通地板
				bitmap = BitmapUtil.footboard_normal_bitmap;
		 } else if (which == 1) { //往左地板
				bitmap1 = BitmapUtil.footboard_moving_left1_bitmap;
				bitmap2 = BitmapUtil.footboard_moving_left2_bitmap;
				bitmap = bitmap1;
		 } else if (which == 2){ //往右地板
				bitmap = BitmapUtil.footboard_moving_right1_bitmap;
		 }else if(which == 3){ //不穩定地板
				bitmap1 = BitmapUtil.footboard_unstable1_bitmap;
				bitmap2=BitmapUtil.footboard_unstable2_bitmap;
				bitmap = bitmap1;
		 }else if(which==4){ //朽木地板
//			 bitmap = BitmapUtil.footboard_spring_bitmap;
				bitmap1 = BitmapUtil.footboard_wood_bitmap;
				bitmap2=BitmapUtil.footboard_wood2_bitmap;
				bitmap3=BitmapUtil.footboard_wood3_bitmap;
			 bitmap = BitmapUtil.footboard_wood_bitmap;
		 }else if(which==5){ //陷阱地板
			 bitmap = BitmapUtil.footboard_spiked_bitmap;
		 }
	}
	
	public void setToolNum(int num){
		toolNum = num;
	}
	
	public void setToolUtil(ToolUtil toolUtil){
		this.toolUtil = toolUtil;
	}
	
	public ToolUtil getToolUtil(){
		return toolUtil;
	}
}
