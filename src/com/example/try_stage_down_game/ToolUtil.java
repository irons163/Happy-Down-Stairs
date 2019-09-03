package com.example.try_stage_down_game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ToolUtil {
	
	public float tool_x;
	public float tool_y;
	public int tool_width;
	Bitmap bitmap;
	public int type;
	Thread bombExplodeThread;
	boolean isExploding;
	boolean isEated;
	int count;
	Thread eatThraed;
	
	public ToolUtil(float footboard_x, float footboard_y, int type){
		if(type==Footboard.BOMB){
		bitmap = BitmapUtil.tool_bomb_bitmap;
		}else if(type==Footboard.BOMB_EXPLODE){
			bitmap = BitmapUtil.tool_bomb_explosion_bitmap;
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
					isExploding=false;
				}
			};
			
			if(bombExplodeThread!=null){
				bombExplodeThread.interrupt();
			}
			
			isExploding=true;
			bombExplodeThread = new Thread(runnable);
			bombExplodeThread.start();
		}else if(type==Footboard.EAT_MAN_TREE){
			bitmap = BitmapUtil.toll_eat_man_tree2_bitmap;
		}
		else{
			bitmap = BitmapUtil.toll_cure_bitmap;
		}
		tool_width=bitmap.getHeight();
		this.tool_x = footboard_x+30;
		this.tool_y = footboard_y-tool_width;
		this.type = type;
	}
	
	public void draw(Canvas canvas, float dy) {
		this.tool_y -= dy; 
		canvas.drawBitmap(bitmap, tool_x, tool_y, null);
	}
	
	public void doEat(){
		if(type==Footboard.EAT_MAN_TREE){
			if(eatThraed==null || !eatThraed.isAlive()){
			eatThraed = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					for(int i = 0; i < 3; i++){
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(count==0){
							bitmap = BitmapUtil.toll_eat_man_tree_bitmap;
							count++;
						}else if(count==1){
							bitmap = BitmapUtil.toll_eat_man_tree3_bitmap;
							isEated = true;
							count++;
						}else{
							bitmap = BitmapUtil.toll_eat_man_tree2_bitmap;				
							count = 0;
							isEated = false;
						}
						
					}				
				}
			});
			eatThraed.start();
			}
		}
	}
	
	public boolean isEated(){
		if(isEated){
			isEated = false;
			return true;
		}
		return isEated;
	}
}
