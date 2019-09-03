package com.example.try_stage_down_game;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.irons.happy_down_stairs.R;

public class GameView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	private SurfaceHolder sh; // 畫架
	private Bitmap background; // 背景圖
	int height, width; // 螢幕高寬
	float firstBgHeight; // 第一張背景圖的座標X
	float secondBgHeight; // 第二背景圖的座標X
	private Canvas canvas; // 畫布
	int footboardHeight, footboardWidth; // 地板高寬
	Player player; // 玩家
	private final float BASE_SPEED = (float)BitmapUtil.sreenWidth/160;
	float SPEED = BASE_SPEED; // 背景每次上升距離
	public static final float MOVESPEED = (float)BitmapUtil.sreenWidth/66; // 每次左右移動距離
	public final float DOWNSPEED = BASE_SPEED*5; // 墜落距離
	boolean playerDownOnFootBoard = false; // 玩家是否著地。一開始玩家就是在地板上，因此初始為否。
	boolean playerStandOnFootboard = false;
	Context context;
	public static boolean gameFlag = true;
	boolean readyFlag = true;
	int readyStep = 0;
	public static final float SLIDERSPEED = MOVESPEED/3;
	float playerMoveSpeed = 0;
	Controler controler;
	int whichFoorbar = 0;
	int count = 0;
	int level = 0;
	boolean gameSuccess = false;
	public static boolean gameStop = false;

	private int move = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static Bitmap nextBackground;
	boolean isPressLeftMoveBtn, isPressRightMoveBtn;
	final static int GAME_TIME = 60;
//	final static int FOOTBOARD_HRIGHT_PERSENT = 20;
	public final static int FOOTBOARD_WIDTH_PERSENT = 4;
	
	public static ToolUtil toolExplodingUtil; 
	
	final static int SMOOTH_DEVIATION = 4;
	
	private final Object CREATE_FOOTBAR_LOCK = new Object();
	
	private final int SCORE_MULTIPLE = 100;
	
	private static int[] ramdonBackgroundID= {R.drawable.new_bg1, R.drawable.new_bg2, R.drawable.new_bg3};
	
	Paint paintBG;
	
	private final int INFINITY_LEVEL = 11;
	
	public static final int TOOL_CURE_START_LEVEL = 2;
	
	public static final int TOOL_BOMB_START_LEVEL = 3;
	
	public static final int TOOL_EAT_MAN_TREE_START_LEVEL = 4;
	
	public static final int WOOD_START_LEVEL = 1;
	
	public static final int MOVING_START_LEVEL = 4;
	
	private final int FIREBALL_START_LEVEL = 5;
	
	private final int FIREBALL_START_FAST_LEVEL = 7;
	
	private final int FIREBALL_START_FAST2_LEVEL = 9;
	
	private int fireballCounter;
	
	/*
	 * 此版本在平板上也可完美執行。
	 */
	public GameView(Context context, Bitmap background, int height, int width,
			int level) {
		super(context);
		gameFlag = true;
		gameStop = false;
		// TODO Auto-generated constructor stub
		this.background = background;
		this.height = height;
		this.width = width;
		this.context = context;
		this.level = level;
//		SPEED = BASE_SPEED + level * 2;
		if(level<9){
			SPEED = BASE_SPEED + 8;
		}else{
			SPEED = BASE_SPEED + level;
		}
		 

		secondBgHeight = height;
		sh = this.getHolder();
		sh.addCallback(this);
		// this.setFocusable(true);
//		footboardHeight = (int) height / FOOTBOARD_HRIGHT_PERSENT;
		
//		paintBG = new Paint();
//		paintBG.setColorFilter(ColorFilterBuilder.getEffectColor(level));
		
		footboardWidth = (int) width / FOOTBOARD_WIDTH_PERSENT;
		footboardHeight = (int)((float)BitmapUtil.footboard_normal_bitmap.getHeight()
				/ BitmapUtil.footboard_normal_bitmap.getWidth() * footboardWidth);
		player = new Player(getContext(), 200, height, height, width);

		toolExplodingUtil=null;

		footboard = new Footboard(getContext(), 180, height - footboardHeight,
				footboardHeight, footboardWidth, level);
		footboard.setWhich(0);
		footboard.setToolNum(Footboard.NOTOOL);
		footboardsTheSameLine.add(footboard);
		footboards.add(footboardsTheSameLine);
		currentXs.add(currentX);
		player.y = footboard.y - player.height;
		
		if(level<3){
			AudioUtil.playLevelMusic(level);
		}else{
			AudioUtil.playRamdonMusic();
		}
	}

	int currentX = 200;
	ArrayList<Integer> currentXs = new ArrayList<Integer>();
	ArrayList<ArrayList<Footboard>> footboards = new ArrayList<ArrayList<Footboard>>();
	ArrayList<Footboard> footboardsTheSameLine = new ArrayList<Footboard>();
	Footboard footboard;
	ArrayList<FireBall> fireballs = new ArrayList<FireBall>();
	int DISTANCE_MULTIPLE = 30;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				Random r = new Random();

				// int i = r.nextInt(3); //隨機出現三種座標之一
				int i = 0;
//				long delayTime =0;
				
				while (gameFlag) {
					while (!readyFlag) {
						
						synchronized (CREATE_FOOTBAR_LOCK) {
							try {
								CREATE_FOOTBAR_LOCK.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
//						try {
////							Thread.sleep((int)(2000 / ((level + 2.8)*0.35)));
//							Thread.sleep((long)(BASE_SPEED*2000/SPEED)-delayTime);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						
						
//						delayTime = System.currentTimeMillis();
						
						if(!gameFlag) {
							break;
						}
						int readyForCreateFootboardNumber = currentXs.size();
						int randomCreateNumber = r.nextInt(readyForCreateFootboardNumber+1);
						randomCreateNumber++;
						if(randomCreateNumber>2) randomCreateNumber=2;
						
						footboardsTheSameLine = new ArrayList<Footboard>();
						ArrayList<Integer> tempCurrentXs = new ArrayList<Integer>();
						int des = r.nextInt(21);
						if(des<7)
						{
							des=7;
						}	
						else if(10<=des && des<18){
							des=18;
						}
							
						des *= DISTANCE_MULTIPLE;
						int temp=0;
						int x = r.nextInt(readyForCreateFootboardNumber+1);
						x++;
						if(x>2)x=2;
						
						if(x==2 && currentXs.size()==1){
							x = 2;
						}else if(x==1 && currentXs.size()==1){
							x = 1;
						}else if(x==2 && currentXs.size()==2){
							x = 2;
						}else if(x==1 && currentXs.size()==2){
							x = 1;
						}

//						boolean left=false, right=;

						int addX=0;
						for(int number=0; number<readyForCreateFootboardNumber;number++){
							currentX = currentXs.get(number);
							
							if(number==1 && tempCurrentXs.size()>0){
								if(addX+footboardWidth > width-footboardWidth-(BitmapUtil.playerWidth+2)){
									x--;
								}
							}
							
							if(currentX<(BitmapUtil.playerWidth+2) && x==2){
								x--;
							}else if(currentX>width-footboardWidth-(BitmapUtil.playerWidth+2) && x==2){
								x--;
							}

//							}else if(randomCreateNumber==1){
//								
//							}else {
//								
//							}
						for(int k =0 ;k<x;k++){
							
						
						if (currentX == width - footboardWidth) {
//							x=1;
							if (des < DISTANCE_MULTIPLE*10) {
								temp = -des;
							} else {
								temp = des - DISTANCE_MULTIPLE*20;
							}
						} else if (currentX == 0) {
							if (des >= DISTANCE_MULTIPLE*10) {
								temp = DISTANCE_MULTIPLE*20 - des;
							}else{
								temp = des;
							}
						} else {
							if (des < DISTANCE_MULTIPLE*10) {
								temp = -des;
							} else {
								temp = des - DISTANCE_MULTIPLE*10;
							}
						}
						
						if(number==1 && tempCurrentXs.size()>0){
							if(addX+footboardWidth+(BitmapUtil.playerWidth+2) > currentX+temp){
								temp = Math.abs(temp);
							}
						}
						
						if(x == 2 && k==0){
							temp = -Math.abs(temp);
						}else if(x == 2 && k==1){
							temp = Math.abs(temp);
						}

						addX=0;
						if (i == 0) { // 第一種座標
//							currentX += temp;
							addX = currentX + temp;
							if (addX < 0) {
								addX = 0;
							} else if (addX > width - footboardWidth) {
								addX = width - footboardWidth;
							}
							//產生地板的Y座標為螢幕下面邊緣，此方法會產生和人物交錯的現象。
//							footboard = new Footboard(getContext(), addX,
//									height-footboardHeight, footboardHeight, footboardWidth);
							//產生地板的Y座標為螢幕下面再加一個地板高，此方法不會有人物交錯的現象，
							//但是會有人物剛好掉在一半掉在螢幕外僥倖沒死的情況。此外，因為多往下一個地板高，
							//所以第二個地板與第一個地板的間距會比較大，因為第一個地板預設是螢幕下面邊緣。
							footboard = new Footboard(getContext(), addX,
									height, footboardHeight, footboardWidth, level);
							footboardsTheSameLine.add(footboard);
							

						} 
						
						
//							else if (i == 1) { // 第二種座標
//							footboard = new Footboard(getContext(), currentX,
//									height, footboardHeight, footboardWidth);
//							footboards.add(footboard);
//						} else { // 第三種座標
//							footboard = new Footboard(getContext(), currentX,
//									height, footboardHeight, footboardWidth);
//							footboards.add(footboard);
//						}
						
							tempCurrentXs.add(addX);
						}
						x = randomCreateNumber - x;
						}
						
						currentXs = tempCurrentXs;
						footboards.add(footboardsTheSameLine);
						
						if(level >= FIREBALL_START_LEVEL){
							int fireballRate;
							if(level >= FIREBALL_START_FAST2_LEVEL)
								fireballRate = 2;
							else if(level >= FIREBALL_START_FAST_LEVEL)
								fireballRate = 3;
							else
								fireballRate = 4;
							if(fireballCounter%fireballRate==(fireballRate-1)){
								FireBall fireBall = new FireBall(width);
								fireballs.add(fireBall);
								fireballCounter=0;
							}else{
								fireballCounter++;
							}
						}					
						
//						delayTime = System.currentTimeMillis() - delayTime; 
					}
				}
			}
		}).start();

		Thread ready = new Thread(new Runnable() {
			public void run() {

				for (int i = 0; i < 4; i++) {
					readyStep = i;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				readyFlag = false;

			}
		});
		ready.start();

		Thread time = new Thread(new Runnable() {
			public void run() {
				while (gameFlag) {
					while (!readyFlag) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
//						while (!gameFlag) {
//							;
//						}
						if(gameFlag)
							count++;
						else
							break;
					}
				}
			}
		});
		time.start();

		this.controler = new Controler(context, this.getHeight(),
				this.getWidth());

		while (gameFlag) {
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			draw();
		}

		gameStop = true;
	}

	Paint paint = new Paint();
	int life = 90;

	public void draw() {
		boolean isInjure = false;
		boolean isDrawPlayer = true;
		canvas = sh.lockCanvas();// 這行代表把畫布放上畫架，沒有這行，就不能draw，因為不能畫在畫架上。
		// 背景循環顯示
		Rect rect1 = new Rect(0, 0, background.getWidth(),
				background.getHeight());
		RectF rect2 = new RectF(0, firstBgHeight, width, height + firstBgHeight);// 左和右固定，上下等量變化，形成整個背景移動效果(方塊移動)
		RectF rect3 = new RectF(0, secondBgHeight, width, height + secondBgHeight);// 與上面相同，但是height2起始值不同
		canvas.drawColor(Color.BLACK);
		// canvas.drawBitmap(background, 0, 0, null);//純畫圖(靜態)，不使用
		
		canvas.drawBitmap(background, rect1, rect2, paintBG);// 畫出第一個背景方塊
		canvas.drawBitmap(background, rect1, rect3, paintBG);// 畫出第二個背景方塊(在第一個背景方塊上面)

		if (readyFlag) {

			paint.setColor(Color.WHITE);
			paint.setTextSize(120);
			if (readyStep == 0) {
				Rect bounds = new Rect();
			    paint.getTextBounds("READY", 0, "READY".length(), bounds);
				canvas.drawText("READY", width/2 - bounds.width()/2, height / 2, paint);
			} else {
				canvas.drawText(4 - readyStep + "", width / 2, height / 2,
						paint);
			}

			footboard.draw(canvas, 0);

			player.draw(canvas, 0, 0);

		} else {

			controler.paint(canvas);
			// if (canMove(Controler.direction)) {
			// if (Controler.direction.equals("left")) {
			// x -= 32;
			// }
			// if (Controler.direction.equals("right")) {
			// x += 32;
			// }
			// if (Controler.direction.equals("up")) {
			// y -= 32;
			// }
			// if (Controler.direction.equals("down")) {
			// y += 32;
			// }
			// }

			firstBgHeight -= SPEED;// 往上10
			secondBgHeight -= SPEED;// 往上10
			
			Log.e("Height", "firstBgHeight" + firstBgHeight+"secondBgHeight"+secondBgHeight);
			
			if (firstBgHeight <= -height) {
				firstBgHeight = height;// 背景頂部到-430
			}
			if (secondBgHeight <= -height) {
				secondBgHeight = height;
			}

			// Bitmap floor = BitmapFactory.decodeResource(getResources(),
			// R.drawable.footboard_normal);
			// canvas.drawBitmap(floor, 0, height2, null);

			// Log.e("a", footboards.size()+"");

			float footboardByPlayerX, footboardByPlayerY = 0;

			
			int re = 0;

			playerStandOnFootboard = false;
			for (int j = 0; j < footboards.size(); j++) {	
				ArrayList<Footboard> f = footboards.get(j);
				for (int k = 0; k < f.size(); k++) {
					boolean remove = false;
				Footboard ene = f.get(k);
				int i = new Random().nextInt(20);
				// if (i == 0) {
				// Bitmap bulle = BitmapFactory.decodeResource(
				// this.getResources(), R.drawable.bullet);
				// bull = new Bullet(ene.x+9, ene.y+31, bulle,time);
				// ebullets.add(bull);
				// }
				ToolUtil toolUtil = null;
				toolUtil = ene.getToolUtil();
				if (ene.toolNum == Footboard.NOTOOL) {
					toolUtil = null;
				} else if (ene.toolNum == Footboard.BOMB) {
					if(toolUtil==null){
						toolUtil = new ToolUtil(ene.x, ene.y, Footboard.BOMB);
						ene.setToolUtil(toolUtil);
					}
//					toolUtil.draw(canvas, SPEED);
				} else if (ene.toolNum == Footboard.BOMB_EXPLODE) {

				} else if (ene.toolNum == Footboard.EAT_MAN_TREE) {
//					toolUtil = new ToolUtil(ene.x, ene.y, Footboard.EAT_MAN_TREE);
					if(toolUtil==null){
						toolUtil = new ToolUtil(ene.x, ene.y, Footboard.EAT_MAN_TREE);
						ene.setToolUtil(toolUtil);
					}
//					toolUtil.draw(canvas, SPEED);
				} else {
//					toolUtil = new ToolUtil(ene.x, ene.y, Footboard.CURE);
					if(toolUtil==null){
						toolUtil = new ToolUtil(ene.x, ene.y, Footboard.CURE);
						ene.setToolUtil(toolUtil);
					}
//					toolUtil.draw(canvas, SPEED);
				}

				int a = (int) (player.y + player.height);
				if ((ene.y) >= (int) (player.y + player.height)) {
					Log.e("b", "a");
				}
				if (ene.y < player.y + player.height + 10) {
					Log.e("c", "a");
				}
				if (ene.y >= player.y + player.height - 60) {
					Log.e("c", "ene.y" + ene.y + "player.y + player.height" + (player.y + player.height));
					Log.e("d", "ene.y" + ene.y + "player.y + player.height + DOWNSPEED + SPEED" + (player.y
							+ player.height + DOWNSPEED + SPEED));
				}

				//ene.y >= player.y + player.height -1 最後的-1是因為浮點數運算，有時會小個0.000X。
				if ((ene.x < player.x + player.width - SMOOTH_DEVIATION*4)
						&& (ene.x + footboardWidth > player.x + SMOOTH_DEVIATION*4)
						&& (ene.y >= player.y + player.height -1 && ene.y < player.y
								+ player.height + DOWNSPEED + SPEED)) {
							
					if (toolUtil != null
							&& (toolUtil.tool_x < player.x + player.width -SMOOTH_DEVIATION*4)
							&& (toolUtil.tool_x + toolUtil.tool_width > player.x +SMOOTH_DEVIATION*4)
							&& ene.toolNum != Footboard.BOMB_EXPLODE) {
						if (ene.toolNum == Footboard.BOMB) {
							isInjure = true;
							ene.toolNum = Footboard.BOMB_EXPLODE;
							toolExplodingUtil = new ToolUtil(ene.x, ene.y, Footboard.BOMB_EXPLODE);
							life -= 60;
							paint.setColor(Color.RED);
							paint.setAlpha(100);
							Rect rect6 = new Rect(0, 0, width, height);
							canvas.drawRect(rect6, paint);
							if (life < 0) {
								life = 0;
							}
						} else if (ene.toolNum == Footboard.CURE){
							life = 90;
							ene.toolNum = Footboard.NOTOOL;
						} else if (ene.toolNum == Footboard.EAT_MAN_TREE){
							toolUtil.doEat();
							if(toolUtil.isEated()){
								isInjure = true;
								life -= 30;
								paint.setColor(Color.RED);
								paint.setAlpha(100);
								Rect rect6 = new Rect(0, 0, width, height);
								canvas.drawRect(rect6, paint);
							}
						}		
						
					}
					


					if (playerDownOnFootBoard) {
						whichFoorbar = 0;
						// move = 0;
						playerMoveSpeed = 0;
						if (ene.which == 5) {
							isInjure = true;
							life -= 30;
							if (life < 0) {
								life = 0;
							}
							paint.setColor(Color.RED);
							paint.setAlpha(100);
							Rect rect6 = new Rect(0, 0, width, height);
							canvas.drawRect(rect6, paint);
						}

//						if (ene.which == 4) {
//							ene.bitmap = null;
//						}

						if (ene.which == 1) {
							whichFoorbar = 1;
							// move = LEFT;
							// move = 0;
							playerMoveSpeed = SLIDERSPEED;
						} else if (ene.which == 2) {
							whichFoorbar = 2;
							// move = RIGHT;
							// move = 0;
							playerMoveSpeed = -SLIDERSPEED;
						}
					}

					// if (ene.which == 1) {
					// whichFoorbar = ene.which;
					// move = LEFT;
					// playerMoveSpeed = SLIDERSPEED;
					// } else if (ene.which == 2) {
					// whichFoorbar = ene.which;
					// move = RIGHT;
					// playerMoveSpeed = -SLIDERSPEED;
					// }

					playerStandOnFootboard = true;
					footboardByPlayerY = ene.y;
					player.y = footboardByPlayerY - player.height;
					// move=0;
					ene.setCount();
					playerDownOnFootBoard = false;
					// playerDownOnFootBoard = false;
				}
				// else{
				// playerStandOnFootboard=false;
				// }

				if(ene.toolNum == Footboard.BOMB_EXPLODE && toolExplodingUtil!=null){
					if(toolExplodingUtil.isExploding){
						toolUtil = null;
						if(isDrawPlayer){
							isDrawPlayer = false;
							player.draw(canvas, SPEED, 0, isInjure);
							toolExplodingUtil.draw(canvas, SPEED);
						}
					}else{
						toolExplodingUtil = null;
						ene.toolNum = Footboard.NOTOOL;
					}
				}
				
				if(toolUtil != null){
					toolUtil.draw(canvas, SPEED);
				}
				
				if (ene.y > 0 - SPEED && ene.bitmap != null) {
					ene.draw(canvas, SPEED);
				} else {
					remove = true;
					re = k;
				}
				
				if (remove) {
					f.remove(re);
					k--;
				}
			}
			}



			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.top_spiked_bar);
//			canvas.drawBitmap(bitmap, 0, 0, null);
			float topSpikedBarHeight = (float)bitmap.getHeight()/bitmap.getWidth() * width;
			RectF rectTopSpikedBar = new RectF(0, 0, width,
					topSpikedBarHeight);
			canvas.drawBitmap(bitmap, null, rectTopSpikedBar, null);
			
			if (topSpikedBarHeight >= player.y) {
				isInjure = true;
				life = 0;
			}
			
			float playerDy=0;
			float playerDx=0;
			if(isDrawPlayer){

			
			if (playerStandOnFootboard) {
				Log.e("d", "a");

				if (move == LEFT) {

					if (player.x <= 0) {
						playerDy = SPEED;
						playerDx = 0;
//						player.draw(canvas, SPEED, 0, isInjure);
						move = 0;
					} else {
						playerDy = SPEED;
						playerDx = MOVESPEED + playerMoveSpeed;
//						player.draw(canvas, SPEED, MOVESPEED + playerMoveSpeed, isInjure);
					}
				} else if (move == RIGHT) {
					if (player.x + player.width >= width) {
						playerDy = SPEED;
						playerDx = 0;
//						player.draw(canvas, SPEED, 0, isInjure);
						move = 0;
					} else {
						playerDy = SPEED;
						playerDx = -MOVESPEED + playerMoveSpeed;
//						player.draw(canvas, SPEED, -MOVESPEED + playerMoveSpeed, isInjure);
					}
				} else {
					if (whichFoorbar == 1) {
						playerDy = SPEED;
						playerDx = playerMoveSpeed;
//						player.draw(canvas, SPEED, playerMoveSpeed, isInjure);
					} else if (whichFoorbar == 2) {
						playerDy = SPEED;
						playerDx = playerMoveSpeed;
//						player.draw(canvas, SPEED, playerMoveSpeed, isInjure);
					} else {
						playerDy = SPEED;
						playerDx = 0;
//						player.draw(canvas, SPEED, 0, isInjure);
					}

				}

			} else {

				if (move == LEFT) {
					if (player.x <= 0) {
						playerDy = -DOWNSPEED;
						playerDx = 0;
//						player.draw(canvas, -DOWNSPEED, 0, isInjure);
						move = 0;
					} else {
						playerDy = -DOWNSPEED;
						playerDx = 8;
//						player.draw(canvas, -DOWNSPEED, 8, isInjure);
					}
				} else if (move == RIGHT) {
					if (player.x + player.width >= width) {
						playerDy = -DOWNSPEED;
						playerDx = 0;
//						player.draw(canvas, -DOWNSPEED, 0, isInjure);
						move = 0;
					} else {
						playerDy = -DOWNSPEED;
						playerDx = -8;
//						player.draw(canvas, -DOWNSPEED, -8, isInjure);
					}
				} else {
					playerDy = -DOWNSPEED;
					playerDx = 0;
//					player.draw(canvas, -DOWNSPEED, 0, isInjure);
				}
				playerDownOnFootBoard = true;
			}
			}
		
			for (int i = 0; i < fireballs.size(); i++) {
				FireBall ball = fireballs.get(i);		
				if (player.y < ball.y + ball.fireballHeight
						&& player.y > ball.y
						&& player.x + player.width > ball.x + SMOOTH_DEVIATION
						&& player.x < ball.x + ball.fireballWidth - SMOOTH_DEVIATION) {
					isInjure = true;
					life = 0;
					if(isDrawPlayer){
						isDrawPlayer = false;
						player.draw(canvas, playerDy, playerDx, isInjure);
					}
				}
				ball.draw(canvas, -(SPEED + SPEED), 0);
			}
			
			if(isDrawPlayer){
				player.draw(canvas, playerDy, playerDx, isInjure);
			}

//			paint.setColor(Color.GRAY);
//			Rect rect5 = new Rect(width - 120, 60, width - 30, 80);
//			canvas.drawRect(rect5, paint);
//			paint.setColor(Color.RED);
//			Rect rect4 = new Rect(width - 120, 60, width - 120 + life, 80);
//			canvas.drawRect(rect4, paint);
			int lifeBarX = 0;
			Bitmap lifeBgBmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.life_bg);
			Bitmap lifeBmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.life);
			RectF rect5 = new RectF(width - lifeBarX - lifeBgBmp.getWidth(), topSpikedBarHeight, width - lifeBarX , topSpikedBarHeight+lifeBgBmp.getHeight());
			canvas.drawBitmap(lifeBgBmp, null, rect5, null);
			int w = lifeBmp.getWidth();
			int ww = ((lifeBmp.getWidth()*(life/30))/3);
			RectF rect4 = new RectF(width - lifeBarX - ((lifeBmp.getWidth()*(life/30))/3) - (((float)(lifeBgBmp.getWidth() - lifeBmp.getWidth())/2)), topSpikedBarHeight+(((float)(lifeBgBmp.getHeight() - lifeBmp.getHeight())/2)), width - lifeBarX - (((float)(lifeBgBmp.getWidth() - lifeBmp.getWidth())/2)), topSpikedBarHeight+ lifeBmp.getHeight()+(((float)(lifeBgBmp.getHeight() - lifeBmp.getHeight())/2)));
			Rect rectLife;
			if(life==0 || player.y > height){
				rectLife = new Rect(0, 0, 0, 0);
			}else if(life==30){
				rectLife = new Rect(lifeBmp.getWidth()*2/3, 0, lifeBmp.getWidth(), lifeBmp.getHeight());
			}else if(life==60){
				rectLife = new Rect(lifeBmp.getWidth()/3, 0, lifeBmp.getWidth(), lifeBmp.getHeight());
			}else {
				rectLife = new Rect(0, 0, lifeBmp.getWidth(), lifeBmp.getHeight());
			}
			
			canvas.drawBitmap(lifeBmp, rectLife, rect4, null);
			
			if (!gameSuccess) {
				paint.setColor(Color.BLUE);
				// Rect rect7 = new Rect(50, 10, 350-count*10 , 20);
				// canvas.drawRect(rect7, paint);

//				canvas.drawText(count + "s", 0, 100, paint);
				
				Bitmap gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.second_0);
				Bitmap gameTimeSingleDigitsCountBmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.second_0);
				Rect rectTensCount = new Rect(0, 0+60, gameTimeTensCountBmp.getWidth() , gameTimeTensCountBmp.getHeight()+60);
				Rect rectSingleDigitsCount = new Rect(gameTimeTensCountBmp.getWidth(), 0+60, gameTimeTensCountBmp.getWidth()*2 , gameTimeTensCountBmp.getHeight()+60);
				Rect rectgameTime = new Rect(gameTimeTensCountBmp.getWidth()*2, 0+60, gameTimeTensCountBmp.getWidth()*3 , gameTimeTensCountBmp.getHeight()+60);
				
				switch (count/10) {
				case 0:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_0);
					break;
				case 1:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_1);;
					break;
				case 2:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_2);;
					break;
				case 3:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_3);;
					break;
				case 4:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_4);;
					break;
				case 5:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_5);;
					break;
				case 6:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_6);;
					break;
				case 7:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_7);;
					break;
				case 8:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_8);;
					break;
				case 9:
					gameTimeTensCountBmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.second_9);;
					break;

				default:
					break;
				}
				
				gameTimeSingleDigitsCountBmp = getSingleDigitsBmp(count);
				
				Bitmap gameTimeBmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.second_s);
				canvas.drawBitmap(gameTimeTensCountBmp, null, rectTensCount, null);
				canvas.drawBitmap(gameTimeSingleDigitsCountBmp, null, rectSingleDigitsCount, null);
				canvas.drawBitmap(gameTimeBmp, null, rectgameTime, null);
				
				//嘗試將surfaceview截圖，失敗。
//				if(isFirstDraw){
////					isFirstDraw = false;
////				canvas.save();
//				pauseCanvasBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888); 
////				canvas.restore();
//				canvas.setBitmap(pauseCanvasBitmap);
//				
//				draw(canvas);
//				
////				View cv = ((Activity)context).getWindow().getDecorView();
//				View cv = this.getRootView();
//				cv.draw(canvas);
////				pauseCanvasBitmap=this.getDrawingCache();
//				Matrix matrix = canvas.getMatrix();
//				Matrix matrix2 = new Matrix(matrix);
////				Matrix matrix3 = new Matrix(sh.getSurfaceFrame());
////				canvas.drawBitmap(pauseCanvasBitmap, null, sh.getSurfaceFrame(), null);
////				canvas.;
////				Picture picture = new Picture();
////				canvas.drawRect(sh.getSurfaceFrame(), null);
//				}
				
				if (GAME_TIME - count <= 0) {
					// Message message = new Message();
					// message.what=1;
					// handler.sendEmptyMessage(message.what);
					if (level != INFINITY_LEVEL) {
						gameSuccess = true;
						handler.sendEmptyMessage(0);
					}
				}

			}

			if (life == 0 || player.y > height) {
				paint.setColor(Color.RED);
				paint.setAlpha(100);
				Rect rect6 = new Rect(0, 0, width, height);
				canvas.drawRect(rect6, paint);
				// handler.sendMessage(new Message());
				
//				if(level==3){
//					gameSuccess=true;
//				}
				
				gameFlag = false;
				if(!isGameFinish){
					isGameFinish = true;					
					handler.sendEmptyMessage(0);
				}
					

			}
			
//			if(!readyFlag)
				drawCount++;
			if(drawCount%((int)((float)30/SPEED*BASE_SPEED))==0){
				synchronized (CREATE_FOOTBAR_LOCK) {
					CREATE_FOOTBAR_LOCK.notifyAll();
				}		
				drawCount=0;
			}
			
		}
		sh.unlockCanvasAndPost(canvas);// 將畫布從畫架取出展示，沒有這行無法顯示畫布，且下次再lockCanvas會出錯。
		
	}
	
	boolean isGameFinish = false;
	
	int drawCount;
	
	private Bitmap getSingleDigitsBmp(int count){
		Bitmap bitmap = null;
		switch (count%10) {
		case 0:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_0);
			break;
		case 1:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_1);
			break;
		case 2:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_2);
			break;
		case 3:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_3);
			break;
		case 4:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_4);
			break;
		case 5:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_5);
			break;
		case 6:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_6);
			break;
		case 7:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_7);
			break;
		case 8:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_8);
			break;
		case 9:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.second_9);
			break;
		}
		return bitmap;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if(count<GAME_TIME)
			new Thread(this).start();
		else{
//			canvas = sh.lockCanvas();
////			if(pauseCanvas!=null)
////				canvas = pauseCanvas;
//			
//			if(pauseCanvasBitmap!=null){
//				canvas.drawBitmap(pauseCanvasBitmap, 0, 0,null);
////				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
////						R.drawable.top_spiked_bar);
////				canvas.drawBitmap(bitmap, 0, 0, null);
//			}
//			
//			
//			sh.unlockCanvasAndPost(canvas);
//			
//			draw();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		synchronized (CREATE_FOOTBAR_LOCK) {
			CREATE_FOOTBAR_LOCK.notifyAll();
		}	
	}

	/*
	 * 
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {


			
			if (msg.what == 0) {
				gameFlag = false;

				if (!gameSuccess) {
					submitScore();
				} else {
					SharedPreferences preferences = context
							.getSharedPreferences("user", Context.MODE_PRIVATE);
					int maxLevel = preferences.getInt("level", 0);
					if (maxLevel < INFINITY_LEVEL-1) {
						Editor editor = preferences.edit();
						int lv = maxLevel + 1;
						editor.putInt("level", lv);
						editor.commit();
					}
//					if (level == 3) {
//						submitScore();
//					} else {

						final Dialog dialog = new Dialog(context,
								R.style.Translucent_NoTitle);
						dialog.setContentView(R.layout.success_dialog);
						dialog.setCanceledOnTouchOutside(false);
						
						Button button = (Button) dialog
								.findViewById(R.id.button1);
						Button button2 = (Button) dialog
								.findViewById(R.id.button2);

						if (level == INFINITY_LEVEL-1) {
							button2.setBackgroundResource(R.drawable.rank_level);
						}

						button.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								GameLevel gameLevel = (GameLevel) context;
								gameLevel.handler.sendEmptyMessage(0);
								gameFlag = false;
								dialog.dismiss();
							}
						});

						button2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								final BitmapFactory.Options options = new BitmapFactory.Options();
								 options.inSampleSize = 3;
								 options.inJustDecodeBounds = false;
								if (level + 1 == 1) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.bg1_2, options);
								} else if (level + 1 == 2) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.bg1_3, options);
								} else if (level + 1 == 3) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.bg1_4, options);
								} else if (level + 1 == 4) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.new_bg2, options);
								} else if (level + 1 == 5) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.new_bg2_2, options);
								} else if (level + 1 == 6) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.new_bg2_3, options);
								} else if (level + 1 == 7) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.new_bg2_4, options);
								} else if (level + 1 == 8) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.new_bg3, options);
								} else if (level + 1 == 9) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.new_bg3_2, options);
								} else if (level + 1 == 10) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.new_bg3_3, options);
								} else if (level + 1 == INFINITY_LEVEL) {
									nextBackground = BitmapFactory
											.decodeResource(
													context.getResources(),
													R.drawable.new_bg3_4, options);
								}

								GameView rv = new GameView(context,
										nextBackground, height, width,
										level + 1);
								GameLevel activity = (GameLevel) context;
								activity.setContentView(rv);
								dialog.dismiss();
								System.gc();
							}
						});

						dialog.show();
					}

//				}
			} else if (msg.what == 1) {

			}

		}
	};

	@Override
	public synchronized boolean onTouchEvent(MotionEvent event) {
		float _x = event.getX();
		float _y = event.getY();
		if (((event.getAction() & event.ACTION_MASK) == event.ACTION_POINTER_DOWN)
				|| ((event.getAction() & event.ACTION_MASK) == event.ACTION_POINTER_UP)) {
			_x = event.getX(event.getActionIndex());
			_y = event.getY(event.getActionIndex());
		}
		if (event.getAction() == event.ACTION_DOWN
				|| ((event.getAction() & event.ACTION_MASK) == event.ACTION_POINTER_DOWN)) {
			if (_x > 0 && _x < Controler.bmpWidth
					&& _y > this.getHeight() - Controler.bmpHeight
					&& _y < this.getHeight()) {
				isPressLeftMoveBtn = true;
				move = LEFT;
				player.updateBitmap(LEFT);
			}
			if (_x > this.getWidth() - Controler.bmpWidth
					&& _x < this.getWidth()
					&& _y > this.getHeight() - Controler.bmpHeight
					&& _y < this.getHeight()) {
				isPressRightMoveBtn = true;
				move = RIGHT;
				player.updateBitmap(RIGHT);
			}
		}
		if ((event.getAction() == event.ACTION_UP)
				&& (isPressLeftMoveBtn || isPressRightMoveBtn)) {
			if (isPressLeftMoveBtn) {
				isPressLeftMoveBtn = !isPressLeftMoveBtn;
			} else if (isPressRightMoveBtn) {
				isPressRightMoveBtn = !isPressRightMoveBtn;
			}
			move = 0;
			player.updateBitmap(0);
		} else if (((event.getAction() & event.ACTION_MASK) == event.ACTION_POINTER_UP)
				&& isPressLeftMoveBtn && isPressRightMoveBtn) {
			if (_x > 0 && _x < Controler.bmpWidth
					&& _y > this.getHeight() - Controler.bmpHeight
					&& _y < this.getHeight()) {
				isPressLeftMoveBtn = false;
				move = RIGHT;
				player.updateBitmap(RIGHT);
			}
			if (_x > this.getWidth() - Controler.bmpWidth
					&& _x < this.getWidth()
					&& _y > this.getHeight() - Controler.bmpHeight
					&& _y < this.getHeight()) {
				isPressRightMoveBtn = false;
				move = LEFT;
				player.updateBitmap(LEFT);
			}
		}
		return true;
	}

	private void submitScore() {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		 options.inSampleSize = 3;
		 options.inJustDecodeBounds = false;
//		if (level + 1 == 1) {
//			nextBackground = BitmapFactory.decodeResource(
//					context.getResources(), R.drawable.new_bg2 ,options);
//		} else if (level + 1 == 2) {
//			nextBackground = BitmapFactory.decodeResource(
//					context.getResources(), R.drawable.new_bg3, options);
//		} else if (level + 1 == 3) {
//			nextBackground = BitmapFactory.decodeResource(
//					context.getResources(), getRandomBgResId(), options);
//		}
		 
		if (level + 1 == 1) {
				nextBackground = BitmapFactory
						.decodeResource(
								context.getResources(),
								R.drawable.bg1_2, options);
		} else if (level + 1 == 2) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.bg1_3, options);
		} else if (level + 1 == 3) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.bg1_4, options);
		} else if (level + 1 == 4) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.new_bg2, options);
		} else if (level + 1 == 5) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.new_bg2_2, options);
		} else if (level + 1 == 6) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.new_bg2_3, options);
		} else if (level + 1 == 7) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.new_bg2_4, options);
		} else if (level + 1 == 8) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.new_bg3, options);
		} else if (level + 1 == 9) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.new_bg3_2, options);
		} else if (level + 1 == 10) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.new_bg3_3, options);
		} else if (level + 1 == INFINITY_LEVEL) {
			nextBackground = BitmapFactory
					.decodeResource(
							context.getResources(),
							R.drawable.new_bg3_4, options);
		}

		final int score = GAME_TIME * level * SCORE_MULTIPLE + count * SCORE_MULTIPLE;
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		final View submitTextView = layoutInflater.inflate(
				R.layout.rank_dialog, null);
		final EditText mNameEditText = (EditText) submitTextView
				.findViewById(R.id.editText1);
		final TextView scoreTextView = (TextView) submitTextView
				.findViewById(R.id.textView3);
		scoreTextView.setText(score + "");

		Button button = (Button) submitTextView.findViewById(R.id.button1);
		Button button2 = (Button) submitTextView.findViewById(R.id.button2);

		final Dialog dialog = new Dialog(context);
		dialog.setTitle("遊戲結束");
		dialog.setContentView(submitTextView);
		dialog.setCanceledOnTouchOutside(false);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SQLiteHelper helper = new SQLiteHelper(context);
				final String name = mNameEditText.getText().toString();
				if (name.equals("") || name.trim().equals("")) {
					Toast.makeText(context,
							getResources().getString(R.string.cantnull),
							Toast.LENGTH_LONG).show();
					dialog.cancel();
					submitScore();
				} else {

					final String rank = helper.queryrank(String
							.valueOf(score));
					helper.insertData(name, score,
							Integer.parseInt(rank) + 1);// 插入排行

//					AlertDialog.Builder builder = new AlertDialog.Builder(
//							context);
//					builder.setTitle(getResources()
//							.getString(R.string.recordOK));
//					builder.setMessage(getResources().getString(
//							R.string.countinue));
//					builder.setPositiveButton(
//							getResources().getString(R.string.retry),
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO Auto-generated method stub
//									GameView rv = new GameView(context,
//											nextBackground, height, width, level);
//									GameLevel activity = (GameLevel) context;
//									activity.setContentView(rv);
//									System.gc();
//								}
//							});
//					builder.show();
					
					final Dialog gameOverDialog = new Dialog(context,
							R.style.Translucent_NoTitle);
					gameOverDialog.setContentView(R.layout.gameover_dialog);
					gameOverDialog.setCanceledOnTouchOutside(false);
					
					Button button = (Button) gameOverDialog.findViewById(R.id.button1);
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							GameLevel gameLevel = (GameLevel) context;
							gameLevel.handler.sendEmptyMessage(0);
							gameFlag = false;
							gameOverDialog.dismiss();
						}
					});
					gameOverDialog.show();
					
					dialog.cancel();
				}
			}
		});
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GameLevel gameLevel = (GameLevel) context;
				gameLevel.handler.sendEmptyMessage(0);
				gameFlag = false;
				dialog.cancel();
			}
		});
		dialog.show();
	}
	
	private int getRandomBgResId(){
		Random random = new Random();
		int index = random.nextInt(3);
		return ramdonBackgroundID[index];
	}
}
