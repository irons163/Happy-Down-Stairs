package com.example.try_stage_down_game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import com.irons.happy_down_stairs.R;

/*
 * 此类为工具类之一，专门处理游戏过程中的声音播放问题
 * 调用方式为类名加方法
 * 第一步：AudioUtil.init(this)  初始化
 * 第二步：调用成员方法AudioUtil.PlayMusic()，PauseMusic(),StopMusic()控制背景声音文件；
 * 第三步：调用音效文件的方法AudioUtil.PlaySoundPool(R.raw.back)
 * 音乐开关 AudioUtil.soundControl()
 */
public class AudioUtil {
	// 声明媒体播放类对象
	private static MediaPlayer mMediaPlayer;
	// 声明声音池类对象
	private static SoundPool soundPool;
	// 获取系统声音管理类对象
	private static AudioManager mgr;

	// 媒体播放控制开关
	private static boolean musicRunning = true;
	// 声音池播放开关
	private static boolean soundRunning = true;
	private static boolean ON = true;
	// 上下文对象
	private static Context mContext;

	// 声音池键值对 声音资源ID与
	private static Map<Integer, Integer> soundPoolMap;
	// 音乐资源ID数组
	private static final int musicId[] = { R.raw.mainmenu_music};
	
	private static final int musicLevelId[] = {
	R.raw.level_one_music, R.raw.level_two_music,
	R.raw.level_three_music };

	// 初始化资源
	public static void init(Context context) {
		mContext = context;
		initMusic();
		initSoundPool();
	}

	// 初始化music
	public static void initMusic() {
		mMediaPlayer = MediaPlayer.create(mContext, musicId[0]);
		mMediaPlayer.setLooping(true);
	}

	/**
	 * 播放音效
	 * 
	 * @param index
	 *            音效位置
	 */
	public static void playLevelMusic(int index) {
		if (mMediaPlayer != null && musicRunning) {

			mMediaPlayer = MediaPlayer.create(mContext, musicLevelId[index]);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.start();
		}
	}
	
	public static void playRamdonMusic() {
		if (mMediaPlayer != null && musicRunning) {
			
			Random random = new Random();
			int index = random.nextInt(3);
			mMediaPlayer = MediaPlayer.create(mContext, musicLevelId[index]);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.start();
		}
	}

	// 初始化音效池
	public static void initSoundPool() {
		mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(R.raw.fireball_music,
				soundPool.load(mContext, R.raw.fireball_music, 1));
		soundPoolMap.put(R.raw.eat_men_tree_music,
				soundPool.load(mContext, R.raw.eat_men_tree_music, 1));
	}

	// 播放音效
	public static void PlaySoundPool(int resid) {
		if (soundRunning == false) {
			return;
		}
		Integer soundId = soundPoolMap.get(resid);
		if (soundId != null && soundRunning) {
			soundPool
					.play(soundId,
							mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
							mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
							1, 0, 1f);
			System.out.println("1--->" + resid);
		}
	}

	// 暂停播放
	public static void PauseMusic() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
		}
	}

	// 停止播放
	public static void StopMusic() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
	}

	// 开始播放
	public static void PlayMusic() {
		if (mMediaPlayer != null && musicRunning) {
			mMediaPlayer.start();
		}
	}

	// 设置音乐播放开关
	public static void setMusicRunning(boolean musicRunning) {
		AudioUtil.musicRunning = musicRunning;
	}

	// 设置音效池开关
	public static void setSoundRunning(boolean soundRunning) {
		AudioUtil.soundRunning = soundRunning;
	}

	// 获取音乐播放开关状态
	public static boolean getMusicRunning() {
		return AudioUtil.musicRunning;
	}

	// 获取音效池播放开关状态
	public static boolean getSoundRunning() {
		return AudioUtil.soundRunning;
	}

	// 声音开关
	public static void soundControl() {
		ON = !ON;
		if (ON == false) {
			AudioUtil.setMusicRunning(false);
			AudioUtil.setSoundRunning(false);
			AudioUtil.PauseMusic();
		} else {
			AudioUtil.setMusicRunning(true);
			AudioUtil.setSoundRunning(true);
			AudioUtil.PlayMusic();
		}

	}
}
