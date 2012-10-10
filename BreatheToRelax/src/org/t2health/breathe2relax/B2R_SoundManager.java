package org.t2health.breathe2relax;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class B2R_SoundManager {
	private SoundPool mSoundPool = null;
	private HashMap<Integer, Integer> mSoundIdMap = null;
	private AudioManager mAudioManager = null;

	public SoundPool getSoundPool() {
		return mSoundPool;
	}

	public Integer getId(int id){
		return mSoundIdMap.get(id);
	}
	
	public B2R_SoundManager(Context context, int maxStreams) {
	
		if (null != mSoundPool) {
			mSoundPool.release();
			mSoundPool = null;
		}
		mSoundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);

		if (null != mSoundIdMap) {
			mSoundIdMap.clear();
		}
		mSoundIdMap = new HashMap<Integer, Integer>();

		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}

	public void loadSound(Context context, int soundId, int resourceId) {
		mSoundIdMap.put(soundId, mSoundPool.load(context, resourceId, 1));
	}

	public final int playSound(int id, int priority, int loop, float rate) {
		int ret = 0;
		
		if (mAudioManager == null) {
			return -1;
		}

		float streamVolumeCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float streamVolume = streamVolumeCurrent / streamVolumeMax;

		if (mSoundPool == null) {
			Log.d("B2R_SoundManager", "SoundPool null");
			return -1;
		}
		
		if (mSoundIdMap == null || mSoundIdMap.isEmpty()) {
			Log.d("B2R_SoundManager", "SoundIdMap null");
			return -1;
		}

		try {
			ret =  mSoundPool.play(mSoundIdMap.get(id), streamVolume, streamVolume, priority, loop, rate);
		} catch (Exception ex) {
			Log.d("B2R_SoundManager", "Exception: " + id, ex);
		}
		
		return ret;
	}

	public void cleanup() {
		Log.d("B2R_SoundManager", "cleanup");
		if (mSoundPool != null) {
			mSoundPool.release();
			mSoundPool = null;
		}
		if (mSoundIdMap != null) {
			mSoundIdMap.clear();
		}
		if (mAudioManager != null) {
			mAudioManager.unloadSoundEffects();
			mAudioManager = null;
		}
	}

}
