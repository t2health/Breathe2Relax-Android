/*
 * Breathe2Relax
 * 
 * Copyright © 2009-2012 United States Government as represented by the 
 * Chief Information Officer of the National Center for Telehealth and 
 * Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE,
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT AS 
 * REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: Breathe2Relax001
 * Government Agency Original Software Title: Breathe2Relax
 * User Registration Requested. Please send email with your contact 
 * information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.breathe2relax;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.t2health.lib.R;
import org.t2health.lib.db.DatabaseOpenHelper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class B2R_Utility {
	public enum BitmapOrder {
		RATE,
		FIRST,
		FIRST_BREATHE,
		LAST_BREATHE,
		LAST,
		BRAIN,
		EYES,
		EARS,
		LUNGS,
		HEART,
		STOMACH,
		FINGERS,
		MUSCLES,
		IMMUNE_SYSTEM,
		SKIN,
		BODY,
		MASK,
		BUTTON_BRAIN,
		BUTTON_EYES,
		BUTTON_EARS,
		BUTTON_LUNGS,
		BUTTON_HEART,
		BUTTON_STOMACH,
		BUTTON_FINGERS,
		BUTTON_MUSCLES,
		BUTTON_IMMUNE_SYSTEM,
		BUTTON_SKIN,
		BUTTON_BODY,
		BUTTON_MASK,
		GLOW,
		SCAN0,
		SCAN1,
		SCAN2,
		SCAN3,
		SCAN4,
		SCAN5,
		NONE;
	}
	
	// TODO: Only for versions 2.2 and higher
//	static private AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener() {
//		public void onAudioFocusChange(int focusChange) {
//			Log.d("B2R_Utility", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> FADE_IN/FADE_OUT: " + focusChange);
			// See fadeOutMusic
//		}
//	};
	
	static private AudioManager audMgr;
	
	// Overall Volume Controls
	static private float musicVolume = 0;
	static private float instructionVolume = 0;
	
	static private final int NOT_SET = 0;
	static private int imageIntPointer = -1;
	static private MediaPlayer mMusicPlayer;
	static private MediaPlayer mLongTalk;
	static private B2R_SoundManager mShortTalkInhale;
	static private B2R_SoundManager mShortTalkExhale;
	
	static private Map<BitmapOrder, Bitmap > bitmapManager = new HashMap<BitmapOrder, Bitmap >();
	
	static protected void fadeOutMusic() {
		if (musicVolume < 0.01) return;
		
		try {
			if (mMusicPlayer != null) {
				Runnable runner = new Runnable() {
					boolean exceptionOccurred = false;
					public void run() {
						float volume = musicVolume;

						for (int i = 0; i < 20; i++) {
							volume -= volume * 0.1F;
							if (volume < 0) break;

							try {
								Thread.sleep(200);
								if (mMusicPlayer != null) {
									mMusicPlayer.setVolume(volume, volume); // TODO: Occasional IllegalState Exception
								} else {
									break;
								}
							} catch (Exception q) {
								Log.d("B2R_Utility", "EXCEPTION", q);
								exceptionOccurred = true;
								break;
							}

						}
						try {
							if (mMusicPlayer != null) {
								if (!exceptionOccurred) mMusicPlayer.stop(); // TODO: Occasional IllegalState Exception
								mMusicPlayer.release();
								mMusicPlayer = null;
							}
						} catch (Exception e) {
							Log.d("B2R_Utility", "EXCEPTION", e);
						}
					}
				};
				new Thread(runner).start();
			}
		} catch (Exception ex) {
			Log.d("B2R_Utility", "EXCEPTION", ex);
		}	
	}

	static protected void fadeInMusic() {
		ballanceMusic();
	}

	static public void setAudioManager(AudioManager audioManager) {
		audMgr = audioManager;
	}
	
	static public boolean isShortTalkInhaleActive() {
		return (mShortTalkInhale != null);
	}
	static public boolean isShortTalkExhaleActive() {
		return (mShortTalkExhale != null);
	}
	
	static public void talkShortTalkInhale(Context context, int maxStreams) {
		try {
			if (mShortTalkInhale != null) {
				stopShortTalkInhale();
			}
			mShortTalkInhale = new B2R_SoundManager(context, maxStreams);
			B2R_VoiceInstructions.resume();
			loadShortTalkInhale(context);

		} catch (Exception ex) {
			Log.d("B2R_Utility", "Talk Short Talk exception", ex);
		}
	}
	static public void talkShortTalkExhale(Context context, int maxStreams) {
		try {
			if (mShortTalkExhale != null) {
				stopShortTalkExhale();
			}
			mShortTalkExhale = new B2R_SoundManager(context, maxStreams);
			B2R_VoiceInstructions.resume();
			loadShortTalkExhale(context);

		} catch (Exception ex) {
			Log.d("B2R_Utility", "Talk Short Talk exception", ex);
		}
	}
	
	static protected void ballanceMusic() {
		float streamVolumeCurrent = audMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = audMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		if ( B2R_SettingsHolder.getBoolean( B2R_Setting.INSTRUCTIONS_PROMPTS)) {
			
			if ( mMusicPlayer != null && B2R_SettingsHolder.getBoolean( B2R_Setting.PLAY_MUSIC) && B2R_SettingsHolder.isSet( B2R_Setting.BACKGROUND_MUSIC_SELECTED)) {
				instructionVolume = 20F*streamVolumeCurrent;
				if (instructionVolume > streamVolumeMax) {
					instructionVolume = streamVolumeMax;
				}
				
				musicVolume = instructionVolume/20;
				mMusicPlayer.setVolume(musicVolume, musicVolume);
			} else {
				// Nothing to ballance
				instructionVolume = streamVolumeCurrent;
			}
		} else {
			instructionVolume = 0F;
			if ( mMusicPlayer != null && B2R_SettingsHolder.getBoolean( B2R_Setting.PLAY_MUSIC) && B2R_SettingsHolder.isSet( B2R_Setting.BACKGROUND_MUSIC_SELECTED)) {
				musicVolume = streamVolumeCurrent;
			} else {
				musicVolume = 0F;
			}
		}
	}
	
	static protected int balanceInstructionsAndMusic(int id,  B2R_SoundManager mShortTalk) {
		if (id == 0) return 0;
		
		int ret = 0;
		int priority = 1;
		int loop = 0; 
		float rate = 1f;
		float streamVolumeMax = audMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		ballanceMusic();
		
		if ( B2R_SettingsHolder.getBoolean( B2R_Setting.INSTRUCTIONS_PROMPTS)) {
			if (mShortTalk != null && mShortTalk.getSoundPool() != null) {
				ret = mShortTalk.getSoundPool().play(mShortTalk.getId(id), instructionVolume/streamVolumeMax, instructionVolume/streamVolumeMax, priority, loop, rate);
			}
		} 
		
		return ret;
	}

	static public int playShortTalkInhale(int id) {
		if (mShortTalkInhale == null) {
			Log.d("B2R_Utility", "ShortTalk is null");
			return -1;
		}
		
		return balanceInstructionsAndMusic(id, mShortTalkInhale);

	}
	static public int playShortTalkExhale(int id) {
		if (mShortTalkExhale == null) {
			Log.d("B2R_Utility", "ShortTalk is null");
			return -1;
		}
		
		return balanceInstructionsAndMusic(id, mShortTalkExhale);
	}
	
	static public void pauseShortTalkInhale() {
		List<Integer> list = null;
		
		if (mShortTalkInhale != null) {
			if (mShortTalkInhale.getSoundPool() != null) {
				// version 9
				//mShortTalk.getSoundPool().autoPause();
				B2R_VoiceInstructions.pause();
				list = B2R_VoiceInstructions.getInhaleList();
				for (int i = 0; i < list.size(); i++) {
					mShortTalkInhale.getSoundPool().stop(list.get(i));
				}
			}
		}
	}
	static public void pauseShortTalkExhale() {
		List<Integer> list = null;
		
		if (mShortTalkExhale != null) {
			if (mShortTalkExhale.getSoundPool() != null) {
				// version 9
				//mShortTalk.getSoundPool().autoPause();
				B2R_VoiceInstructions.pause();
				list = B2R_VoiceInstructions.getExhaleList();
				for (int i = 0; i < list.size(); i++) {
					mShortTalkExhale.getSoundPool().stop(list.get(i));
				}
			}
		}
	}
	
	static public void resumeShortTalkInhale() {
		List<Integer> list = null;
		
		if (mShortTalkInhale != null) {
			if (mShortTalkInhale.getSoundPool() != null) {
				// version 9
				//mShortTalk.getSoundPool().autoResume();
				B2R_VoiceInstructions.resume();
				list = B2R_VoiceInstructions.getInhaleList();
				for (int i = 0; i < list.size(); i++) {
					mShortTalkInhale.getSoundPool().resume(list.get(i));
				}
			}
		}
	}
	static public void resumeShortTalkExhale() {
		List<Integer> list = null;
		
		if (mShortTalkExhale != null) {
			if (mShortTalkExhale.getSoundPool() != null) {
				// version 9
				//mShortTalk.getSoundPool().autoResume();
				B2R_VoiceInstructions.resume();
				list = B2R_VoiceInstructions.getExhaleList();
				for (int i = 0; i < list.size(); i++) {
					mShortTalkExhale.getSoundPool().resume(list.get(i));
				}
			}
		}
	}
	

	static public void stopShortTalkInhale() {
		if (mShortTalkInhale != null) {
			pauseShortTalkInhale();
			
			mShortTalkInhale.cleanup();
			mShortTalkInhale = null;
		}
	}
	static public void stopShortTalkExhale() {
		if (mShortTalkExhale != null) {
			pauseShortTalkExhale();
			
			mShortTalkExhale.cleanup();
			mShortTalkExhale = null;
		}
	}
	
	static private void loadShortTalkInhale(Context context) {
		if (mShortTalkInhale != null) {
			for (B2R_VoiceInstructions b : B2R_VoiceInstructions.values()) {
				try {
					if (b.getSoundId() > 0 && b.getSoundId() < 20) {
						mShortTalkInhale.loadSound(context, b.getSoundId(), b.getResourceId());
					}
				} catch (Exception ex) {
					Log.d("B2R_Utility", "Talk Short Talk exception", ex);
				}
			}
		}
	}
	static private void loadShortTalkExhale(Context context) {
		if (mShortTalkExhale != null) {
			for (B2R_VoiceInstructions b : B2R_VoiceInstructions.values()) {
				try {
					if (b.getSoundId() > 20 && b.getSoundId() < 40) {
						mShortTalkExhale.loadSound(context, b.getSoundId(), b.getResourceId());
					}
				} catch (Exception ex) {
					Log.d("B2R_Utility", "Talk Short Talk exception", ex);
				}
			}
		}
	}
	
	static public void talkLongTalk(Context content, int id) {
		try {
			if (B2R_SettingsHolder.getBoolean(B2R_Setting.AUDIO_PROMPTS)) {
				if (mLongTalk == null && id != 0) {
					mLongTalk = MediaPlayer.create(content, id);
					mLongTalk.start();
				}
			}
		} catch (Exception ex) {
		}
	}
	
	static public void talkLongTalkGuide(Context content, int id) {
		try {
			if (B2R_SettingsHolder.getBoolean(B2R_Setting.GUIDE_PROMPTS)) {
				if (mLongTalk == null && id != 0) {
					mLongTalk = MediaPlayer.create(content, id);
					mLongTalk.start();
				}
			}
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Talk Long Talk exception", ex);
		}
	}
	
	static public void pauseLongTalk() {
		try {
			if (mLongTalk != null && mLongTalk.isPlaying()) mLongTalk.pause();
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Talk Long Talk exception", ex);
		}
	}
	
	static public void resumeLongTalk() {
		try {
			if (mLongTalk != null && !mLongTalk.isPlaying()) mLongTalk.start();
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Talk Long Talk exception", ex);
		}
	}
	
	static public void resetLongTalk() {
		try {
			if (mLongTalk != null && !mLongTalk.isPlaying()) mLongTalk.reset();
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Talk Long Talk exception", ex);
		}
	}
	
	static public void stopLongTalk() {
		if (mLongTalk != null) {
			try {
				if (mLongTalk.isPlaying()) {
					mLongTalk.stop();
				}
				mLongTalk.release();
			} catch (Exception ex) {
				Log.d("B2R_Utility", "Talk Long Talk exception", ex);
			}
			mLongTalk = null;
		}
	}
	
	static public void stopPlayPreviewMusic() {
		try {
			if (mMusicPlayer != null) {
				mMusicPlayer.release();
			}
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Stop Music exception", ex);
		}
	}

	static public void playPreviewMusic(final Context content, B2R_Music music) {
		try {
			if (music.equals(B2R_Music.NO_MUSIC)) return;
			if (music.equals(B2R_Music.RANDOM)) {
				music = B2R_Music.getRandom();
			}
			if (music.getId() == 0) {
				Log.d("B2R_Utility", "Music Id not set: " + music.name());
				return;
			}
			if (mMusicPlayer != null) {
				stopPlayPreviewMusic();
				mMusicPlayer = null;
			}
			
			mMusicPlayer = MediaPlayer.create(content, music.getId()); 
			float streamVolumeCurrent = audMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
			mMusicPlayer.setVolume( streamVolumeCurrent, streamVolumeCurrent );		
			
			mMusicPlayer.start();
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Play Music exception", ex);
		}
	}

	static public void playMusic(final Context content) {
		try {
			B2R_Music music = B2R_Music.fromString(B2R_SettingsHolder.get(B2R_Setting.BACKGROUND_MUSIC_SELECTED));

			if (music.equals(B2R_Music.NO_MUSIC)) return;
			if (music.equals(B2R_Music.RANDOM)) {
				music = B2R_Music.getRandom();
			}
			if (music.getId() == 0) {
				Log.d("B2R_Utility", "Music Id not set: " + music.name());
				return;
			}
			if (mMusicPlayer != null) {
				stopPlayMusic();
				mMusicPlayer = null;
			}
			
			mMusicPlayer = MediaPlayer.create(content, music.getId()); 
//			audMgr.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK );	
			// Keep playing
//			mMusicPlayer.setOnCompletionListener(new OnCompletionListener(){
//				public void onCompletion(MediaPlayer mediaPlayer) {
//					B2R_Utility.playMusic(content);
//				}
//			});
			
			mMusicPlayer.setLooping(true);
			fadeInMusic();
			mMusicPlayer.start();
	
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Play Music exception", ex);
		}
	}
	
	static public void resumePlayMusic() {
		try {
			if (mMusicPlayer != null) {
				if (!mMusicPlayer.isPlaying()) {
					mMusicPlayer.start();
				}
			}
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Resume Music exception", ex);
		}
	}

	static public void pausePlayMusic() {
		try {
			if (mMusicPlayer != null) {
				if (mMusicPlayer.isPlaying()) {
					mMusicPlayer.pause();
				}
			}
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Pause Music exception", ex);
		}
	}

	static public void resetPlayMusic() {
		try {
			if (mMusicPlayer != null) {
				if (!mMusicPlayer.isPlaying()) {
					mMusicPlayer.reset();
				} else {
					mMusicPlayer.stop();
					mMusicPlayer.reset();
				}
			}
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Pause Music exception", ex);
		}
	}
	
	static public void stopPlayMusic() {
		try {	
			if (mMusicPlayer != null) {
				fadeOutMusic();
			}
		} catch (Exception ex) {
			Log.d("B2R_Utility", "Stop Music exception", ex);
		}
	}

	static public int getMotif() {
		B2R_Motif motif = B2R_Motif.fromString(B2R_SettingsHolder.get(B2R_Setting.MOTIF_SELECTED));
		int res = NOT_SET;
		switch (motif) {
		case RAIN_FOREST: res = getRainForest(); break;
		case MOUNTAIN_MEADOWS: res = getMountainMeadows(); break;
		case COSMIC_PHOTOS: res = getCosmicPhotos(); break;
		case BEACHES: res = getBeaches(); break;
		case FLOWERS: res = getFlowers(); break;
		case SUNSET: res = getSunset(); break;
		default: res = NOT_SET; break;
		}

		return res;
	}

	static private int getSunset() {
		imageIntPointer++;
		if (imageIntPointer > 17) {
			imageIntPointer = 0;
		}
		
		int sunset_ptr = imageIntPointer;

		switch (sunset_ptr) {
		case 0: return R.drawable.sunset1;
		case 1: return R.drawable.sunset2;
		case 2: return R.drawable.sunset3;
		case 3: return R.drawable.sunset4;
		case 4: return R.drawable.sunset5;
		case 5: return R.drawable.sunset6;
		case 6: return R.drawable.sunset7;
		case 7: return R.drawable.sunset8;
		case 8: return R.drawable.sunset9;
		case 9: return R.drawable.sunset10;
		case 10: return R.drawable.sunset11;
		case 11: return R.drawable.sunset12;
		case 12: return R.drawable.sunset13;
		case 13: return R.drawable.sunset14;
		case 14: return R.drawable.sunset15;
		case 15: return R.drawable.sunset16;
		case 16: return R.drawable.sunset17;
		default: return R.drawable.sunset18;
		}
	}

	static private int getFlowers() {
		imageIntPointer++;
		if (imageIntPointer > 8) {
			imageIntPointer = 0;
		}
		
		int flower_ptr = imageIntPointer;

		switch (flower_ptr) {
		case 0: return R.drawable.flower1;
		case 1: return R.drawable.flower2;
		case 2: return R.drawable.flower3;
		case 3: return R.drawable.flower4;
		case 4: return R.drawable.flower5;
		case 5: return R.drawable.flower6;
		case 6: return R.drawable.flower7;
		case 7: return R.drawable.flower8;
		default: return R.drawable.flower9;
		}
	}

	static private int getBeaches() {
		imageIntPointer++;
		if (imageIntPointer > 11) {
			imageIntPointer = 0;
		}
		
		int beaches_ptr = imageIntPointer;

		switch (beaches_ptr) {
		case 0: return R.drawable.beach1;
		case 1: return R.drawable.beach2;
		case 2: return R.drawable.beach3;
		case 3: return R.drawable.beach4;
		case 4: return R.drawable.beach5;
		case 5: return R.drawable.beach6;
		case 6: return R.drawable.beach7;
		case 7: return R.drawable.beach8;
		case 8: return R.drawable.beach9;
		case 9: return R.drawable.beach10;
		case 10: return R.drawable.beach11;
		default: return R.drawable.beach12;
		}
	}

	static private int getCosmicPhotos() {
		imageIntPointer++;
		if (imageIntPointer > 13) {
			imageIntPointer = 0;
		}
		
		int cosmicphotos_ptr = imageIntPointer;

		switch (cosmicphotos_ptr) {
		case 0: return R.drawable.cosmos1;
		case 1: return R.drawable.cosmos2;
		case 2: return R.drawable.cosmos3;
		case 3: return R.drawable.cosmos4;
		case 4: return R.drawable.cosmos5;
		case 5: return R.drawable.cosmos6;
		case 6: return R.drawable.cosmos7;
		case 7: return R.drawable.cosmos8;
		case 8: return R.drawable.cosmos9;
		case 9: return R.drawable.cosmos10;
		case 10: return R.drawable.cosmos11;
		case 11: return R.drawable.cosmos12;
		case 12: return R.drawable.cosmos13;
		default: return R.drawable.cosmos14;
		}
	}

	static private int getMountainMeadows() {
		imageIntPointer++;
		if (imageIntPointer > 12) {
			imageIntPointer = 0;
		}
		
		int mountainmeadows_ptr = imageIntPointer;

		switch (mountainmeadows_ptr) {
		case 0: return R.drawable.meadow1;
		case 1: return R.drawable.meadow2;
		case 2: return R.drawable.meadow3;
		case 3: return R.drawable.meadow4;
		case 4: return R.drawable.meadow5;
		case 5: return R.drawable.meadow6;
		case 6: return R.drawable.meadow7;
		case 7: return R.drawable.meadow8;
		case 8: return R.drawable.meadow9;
		case 9: return R.drawable.meadow10;
		case 10: return R.drawable.meadow11;
		case 11: return R.drawable.meadow12;
		default: return R.drawable.meadow13;
		}
	}

	static private int getRainForest() {
		imageIntPointer++;
		if (imageIntPointer > 7) {
			imageIntPointer = 0;
		}
		
		int rainforest_ptr = imageIntPointer;

		switch (rainforest_ptr) {
		case 0: return R.drawable.rainforest1;
		case 1: return R.drawable.rainforest2;
		case 2: return R.drawable.rainforest3;
		case 3: return R.drawable.rainforest4;
		case 4: return R.drawable.rainforest5;
		case 5: return R.drawable.rainforest6;
		case 6: return R.drawable.rainforest7;
		default: return R.drawable.rainforest8;
		}
	}

	protected static int getMotifSet() {
		B2R_Motif motif = B2R_Motif.fromString(B2R_SettingsHolder.get(B2R_Setting.MOTIF_SELECTED));

		switch (motif) {
		case RAIN_FOREST: return R.drawable.b2r_animation_rainforests;
		case MOUNTAIN_MEADOWS: return R.drawable.b2r_animation_mountainmeadows;
		case COSMIC_PHOTOS: return R.drawable.b2r_animation_cosmicphotos;
		case BEACHES:return R.drawable.b2r_animation_beaches;
		case FLOWERS:return R.drawable.b2r_animation_flowers;
		case SUNSET:return R.drawable.b2r_animation_sunsets;
		default: return NOT_SET;
		}
	}

	protected static boolean hasOrder(BitmapOrder order) {
		return bitmapManager.containsKey(order);
	}
	
	protected static void clear(BitmapOrder order) {
		if (bitmapManager.containsKey(order)) {
			bitmapManager.remove(order);
		}
	}
	
	protected static void clearRecycleables() {
		bitmapManager.clear();
		
		System.gc();
		System.runFinalization();
		System.gc();
	}

	protected static int getRecycleableCount() {
		
		return bitmapManager.size();
	}

	protected static BitmapFactory.Options getOptions(BitmapOrder order) {
		System.gc();
		System.runFinalization();
		System.gc();

		BitmapFactory.Options options = new BitmapFactory.Options(); 
		options.inTempStorage = new byte[4*1024];
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false; 

		switch (order) {
		case RATE:
		case FIRST:
		case FIRST_BREATHE:
		case LAST_BREATHE:
		case LAST: return options;
		default:
			options.inSampleSize = 2;
			return options;
		}
	}

	protected static Bitmap getReducedBitmap(Resources resources, BitmapOrder order, int imageRef) {
		Bitmap bm = null;
		
		if (bitmapManager.containsKey(order)) {
			bm = bitmapManager.get(order);
		} else {
			try {
				BitmapFactory.Options options = getOptions(order);
				
				bm = BitmapFactory.decodeResource(resources, imageRef, options);
				bitmapManager.put(order, bm);
			} catch (Exception ex) {
				Log.d("B2R_Utility", "Bitmap Create failed", ex);
			}
		}

		return bm;
	}
	
	protected static Bitmap getReducedBitmap(Resources resources, BitmapOrder order) {
		Bitmap bm = null;

		if (bitmapManager.containsKey(order)) {
			bm = bitmapManager.get(order);
		} else {
			int imageRef = getMotif();
			
			try {
				BitmapFactory.Options options = getOptions(order);
				
				bm = BitmapFactory.decodeResource(resources, imageRef, options);
				bitmapManager.put(order, bm);
			} catch (Exception ex) {
				Log.d("B2R_Utility", "Bitmap Create failed", ex);
			}
		}

		return bm;
	}
	
	protected static BitmapDrawable getReducedBitmapDrawable(Resources resources, BitmapOrder order, int imageRef) {
		if (imageRef == 0) return null;

		return new BitmapDrawable(resources, B2R_Utility.getReducedBitmap(resources, order, imageRef));
	}

	protected static BitmapDrawable getReducedBitmapDrawable(Resources resources, BitmapOrder order) {
		if (getMotif() == 0) return null;

		return new BitmapDrawable(resources, B2R_Utility.getReducedBitmap(resources, order));
	}
	
	public static boolean canWeb(Context context) {
		try {
			ConnectivityManager mConnectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			// may return null
			if (mConnectivity == null) return false;
			NetworkInfo mNetworkInfo = mConnectivity.getActiveNetworkInfo();
			if (mNetworkInfo == null) return false;
			return mNetworkInfo.isConnectedOrConnecting();
		} catch (Exception ex) {
			Log.d("ConnectivityManager", "Exception", ex);
		}
		return false;
	}
	
	/**
	 * Determine whether stress ratings have already been stored for today.
	 * @param dbHelper DatabaseOpenHelper to get access to database.
	 * @return true if stored already for today; false if not stored
	 */
	public static boolean isTodayStressRated( DatabaseOpenHelper dbHelper)
	{
		boolean found = false;
		List<B2R_MoodTrackingsTable> resultList;
		try
		{
			Dao<B2R_MoodTrackingsTable, ?> dao = dbHelper.getDao(B2R_MoodTrackingsTable.class);
			QueryBuilder<B2R_MoodTrackingsTable, ?> queryBuilder = dao.queryBuilder();
			// Query for just the entry (hopefully not more than 1!) for today.
			// Query limit is from midnight this morning to a millisecond 
			// before midnight tonight (tomorrow morning).
			Calendar today = Calendar.getInstance();
			today.set( Calendar.HOUR_OF_DAY, 0);
			today.set( Calendar.MINUTE, 0);
			today.set( Calendar.SECOND, 0);
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.setTime( today.getTime());
			tomorrow.add( Calendar.DAY_OF_YEAR, 1);
			tomorrow.add( Calendar.MILLISECOND, -1);
			queryBuilder.where().between( "date", today.getTime(), tomorrow.getTime());
			resultList = dao.query( queryBuilder.prepare());
			found = !resultList.isEmpty();
		}
		catch (SQLException e)
		{
			Log.d("B2R_Utility.isTodayStressRated", "Error accessing stress data.", e);
			found = false;
		}

		return ( found);
	}
}
