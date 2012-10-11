/*
 * 
 * Breathe2Relax
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
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
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.breathe2relax;

import java.util.EnumSet;
import java.util.logging.Logger;

import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.brightcove.mobile.android.BCPlayerView;
import com.brightcove.mobile.mediaapi.ReadAPI;
import com.brightcove.mobile.mediaapi.model.Video;
import com.brightcove.mobile.mediaapi.model.enums.MediaDeliveryTypeEnum;
import com.brightcove.mobile.mediaapi.model.enums.RegionEnum;
import com.brightcove.mobile.mediaapi.model.enums.VideoFieldEnum;

public class B2R_ShowMeHowActivity extends BaseNavigationActivity implements BCPlayerView.OnPlaybackChangeListener {
	private static final String apiKey = "KqxnaC4wR_9Z7OoPadhidvvddOPPUIwPiHwIj_WZXqJWZohd9G1Mmw..";
	static private final int TIMEOUT_BCPLAYER = 1000; // 1 second
	static private final int TIMEOUT_INTERVAL = 1000; // 1 second

	private String youtube = "http://www.youtube.com/watch?v=1IdKHi6JM1Q";
	private  long watch = 937057278001L;  // Default to Show Me How video (BrightCove)
	private Boolean useYoutube = false;
	
	private BCPlayerView player;
	private ReadAPI readAPI = new ReadAPI(apiKey);
	private Video pVideo;
	private CountDownTimer timer;
	private volatile boolean makeCall = false;
	
	volatile Object semaphore = new Object();
	volatile Object errorSem = new Object();
	volatile Object focusSem = new Object();
	
	private volatile int currentPosition = 0;
	private volatile boolean isPaused = false;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.setContentView(R.layout.b2r_biologyofbreathing);
		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setRightNavigationButtonVisibility(View.GONE);

		Log.d("B2R_ShowMeHowActivity", "onCreate: " + makeCall); 
		
		useYoutube = B2R_SettingsHolder.getBoolean( B2R_Setting.YOUTUBE_FALLBACK);
		
		Intent intent = this.getIntent();
		
		if (intent != null) {
			Bundle bun = intent.getExtras();
			if (bun != null) {
				if (bun.containsKey("WATCH")) {
					watch = bun.getLong("WATCH");
				}
				if (bun.containsKey("YOUTUBE")) {
					youtube = bun.getString("YOUTUBE");
				}
			}
		}

		Log.d("B2R_ShowMeHowActivity", "Media Choice backup ===> " + useYoutube);
		
		readAPI.setMediaDeliveryType(MediaDeliveryTypeEnum.HTTP);
		readAPI.setRegion(RegionEnum.US);
		Logger logger = Logger.getLogger("BCAndroidAPILogger"); 
		readAPI.setLogger(logger);
		
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage("No Internet (WiFi, WIMAX, UMTS, DataPlan) Connectivity");
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					semaphore = null;
				} catch (Exception ex) {}
			}
		});

		if (!B2R_Utility.canWeb(this) ) {
			if (semaphore != null) {
				alertbox.show();
			}
		}
		
		player = (BCPlayerView) findViewById(R.id.player);
		player.setOnErrorListener(new OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.d("B2R_ShowMeHowActivity", "onError --- what : " + what + ". extra: " + extra);
				if (errorSem != null) {
					errorSem = null;
					return doError();
				} else {
					return true;
				}
			}
		});
	}
	
	@Override
	public void onWindowFocusChanged (final boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		Log.d("B2R_ShowMeHowActivity", "top lavel");
		if (focusSem != null) {
			focusSem = null;
			if (hasFocus) {
				if (B2R_Utility.canWeb(this) ) {
					if (!makeCall) {
						if (player != null) {
							makeAPICall();
							makeCall = true;
							Log.d("B2R_ShowMeHowActivity", "makeAPICall");
						} else {
							closeMe();
						}
					}
				} else {
					closeMe();
				}
			}
		}
	}

	private void closeMe() {
		Log.d("B2R_ShowMeHowActivity", "closeMe");
		if (semaphore != null) {
			semaphore = null;

			Runnable myAction = new Runnable() {
				public void run(){
					try {
						B2R_ShowMeHowActivity.this.finish();
					} catch (Exception ex) {
						Log.d("B2R_ShowMeHowActivity", "Cannot close activity", ex);
					}
				}
			};

			B2R_ShowMeHowActivity.this.runOnUiThread(myAction);
		}
	}
	
	public void onStart() {
		Log.d("B2R_ShowMeHowActivity", "onStart: " + makeCall);
		super.onStart();
	}
	public void onStop() {
		Log.d("B2R_ShowMeHowActivity", "onStop: " + makeCall);
		
		super.onStop();
	}
    public void onPause() {
    	if (player != null) {
    		currentPosition = player.getCurrentPosition();
    		
    		if (player.isPlaying()) {
    			player.pause();
    			isPaused = false;
    		} else {
    			isPaused = true;
    		}
    		
    		Log.d("B2R_ShowMeHowActivity", "onPause: " + isPaused);
    	}
    	
        super.onPause();
    }
    public void onResume() {
    	if (player != null && makeCall) {
    		if ( currentPosition > 0) {
    			player.seekTo(currentPosition);
    		}

    		if (!isPaused) {
    			player.start();
    		} 
    		
    		Log.d("B2R_ShowMeHowActivity", "onResume: " + player.isPlaying());
    	}
    	
        super.onResume();
    }
	
	@Override
	public void onDestroy() {
		Log.d("B2R_ShowMeHowActivity", "onDestroy");

		semaphore = null;
		errorSem = null;
		focusSem = null;
		
		// Dismiss all dialogs
		
		super.onDestroy();
	}

	@Override
	protected void onLeftNavigationButtonPressed() {
		if (player != null) {
			player.stop();
			player = null;
		}
		if (timer != null) {
			timer.cancel();
		}

		// TEST
		semaphore = null;
		errorSem = null;
		focusSem = null;
		
		super.onLeftNavigationButtonPressed();
	}
	
	@Override
	public void onBackPressed() {
		if (player != null) {
			player.stop();
			player = null;
		}
		if (timer != null) {
			timer.cancel();
		}
		
		// TEST
		semaphore = null;
		errorSem = null;
		focusSem = null;
		
		super.onBackPressed();
	}

	private void makeAPICall() {
		Log.d("B2R_ShowMeHowActivity", "makeAPICall");

		EnumSet<VideoFieldEnum> videoFields = VideoFieldEnum.createEmptyEnumSet();
		videoFields.add(VideoFieldEnum.ID);
		videoFields.add(VideoFieldEnum.NAME);
		videoFields.add(VideoFieldEnum.RENDITIONS);
		videoFields.add(VideoFieldEnum.FLVURL);

		try{
			pVideo = readAPI.findVideoById(watch, videoFields, null);
		} catch(Exception e){
			Log.d("B2R_ShowMeHowActivity", "Video Exception (Should generate onError - but sometimes does not): " + e);
			pVideo = null;
		} 

		if (pVideo == null) {
			if (semaphore != null && errorSem != null) {
				errorSem = null;
				doError();
			}
			
			return;
		}
		String title = pVideo.getName();
		if (title != null && title.length() > 0) {
			title = title.replaceAll("Module", "");
			setTitle(title);
		}
		
		try {
			player.logEnabled(true);
			player.load(pVideo);

			player.start(); 
		} catch (Exception ex) {
			Log.d("B2R_ShowMeHowActivity", "Player Exception: " + ex);
			return;
		}

	}
	
	private synchronized boolean doError() {
		boolean ret = false;
		
		if (useYoutube != null && useYoutube == true) {
			Log.d("B2R_ShowMeHowActivity", "YouTube Count Down : " + TIMEOUT_BCPLAYER);
			if (timer == null) {
				timer = new CountDownTimer(TIMEOUT_BCPLAYER , TIMEOUT_INTERVAL) {
					public void onTick(long millisUntilFinished) {
					}

					public void onFinish() {
						// canWeb otherwise no onError message
						onTimeoutGotoYouTube(); // TODO: Seems to be sync-ed with onStop
					}
				};
				timer.start();
			}
			ret = true;
			
		} else {
			if (semaphore != null) {
				Log.d("B2R_ShowMeHowActivity", "Bit Rate Too High : " + player.getHighBitRate() + " --- " + player.getLowBitRate());
				AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
				alertbox.setMessage("Data Speed too Low to Play");
				alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						closeMe();
						semaphore = null;
					}
				});
				
				if (!this.isFinishing()) {
					if (semaphore != null) {
						alertbox.show();
					}
				}
			}
			ret = true;
		}
		
		return ret;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		  super.onNewIntent(intent);
		  
		  setIntent(intent);
	}
	
	protected void onTimeoutGotoYouTube() {
		Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube));
		youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(youtubeIntent);
		closeMe();
	}
}
