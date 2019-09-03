package com.example.try_stage_down_game;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

public class ColorFilterBuilder {
	private static final int FROZEN = 0;
	private static final int FIRE = 1;
	private static final int NORMAL = 2;
	private static final int HEAL = 3;
	
	private static final int RED = 0;
	private static final int YELLOW = 1;
	
	private static final int[] COLORS =
		{Color.RED, 0xFFFFFF77};
	
	private static ColorFilter[] colorMatrixColorFilters = new ColorFilter[4];
	
	public static void init(){
		colorMatrixColorFilters[FROZEN] = createFozenEffectColor();
		colorMatrixColorFilters[FIRE] = createFireEffectColor();
		colorMatrixColorFilters[NORMAL] = createNormalColor();
		colorMatrixColorFilters[HEAL] = createHealColor();
	}
	
	public static ColorFilter getEffectColor(int type){
		if(type == FROZEN){
			return colorMatrixColorFilters[FROZEN];
		}else if(type == FIRE){
			return colorMatrixColorFilters[FIRE];
		}else if(type == NORMAL){
			return colorMatrixColorFilters[NORMAL];
		}else if(type == HEAL){
			return colorMatrixColorFilters[HEAL];
		}
		
		return null;
	}
	
	private static ColorMatrixColorFilter createFozenEffectColor(){
		ColorMatrix cm = new ColorMatrix();
		
		ColorFilterGenerator.adjustHue(cm, -162);
		
		ColorFilterGenerator.adjustBrightness(cm, -30);
		
		ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(cm);
		
		return colorMatrixColorFilter;
	}
	
	private static ColorMatrixColorFilter createFireEffectColor(){
		ColorMatrix cm = new ColorMatrix();
		
		ColorFilterGenerator.adjustHue(cm, -80);
//		
		ColorFilterGenerator.adjustBrightness(cm, -30);
		
//		ColorFilterGenerator.adjustColor(58, 0, 84, 0);
		
		ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(cm);
		
		return colorMatrixColorFilter;
	}
	
	
	private static ColorFilter createNormalColor(){
		
		PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(COLORS[RED], PorterDuff.Mode.MULTIPLY);
		
		return porterDuffColorFilter;
		
	}
	
	private static ColorFilter createHealColor(){
		
		PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(COLORS[YELLOW], PorterDuff.Mode.MULTIPLY);
		
		return porterDuffColorFilter;
		
	}
}
