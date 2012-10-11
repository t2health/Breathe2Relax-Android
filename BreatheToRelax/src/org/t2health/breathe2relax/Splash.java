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

import java.util.Timer;
import java.util.TimerTask;

import org.t2health.lib.activity.BaseActivity;
import org.t2health.lib.analytics.Analytics;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


/**
 * Splash screen to show application and provider logos.
 * @author steve.warren
 */
public class Splash extends BaseActivity implements OnClickListener
{
	private static final String TAG = "Breathe2Relax";
	
	private Timer timer = new Timer();
	private static final long splashDelay = 5000;

	private Handler startHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startNextActivity();
		}
	};

	/*
	 * Build splash screen with a timer (to proceed if no user touch).
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.splash);
		this.findViewById(R.id.linearlayoutMain_B2R).setOnClickListener(this);		
		Log.d(TAG, this.getClass().getSimpleName() + ".onCreate()");   		

		initApp();
		
		try {
			B2R_Utility.setAudioManager((AudioManager)getSystemService(Context.AUDIO_SERVICE));
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
		} catch (Exception ex) {
			Log.d("Splash", "Exception", ex);
		}
		
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				startHandler.sendEmptyMessage(0);
			}
		}, splashDelay);
	}

	@Override
	protected void onStop()
	{
		Log.d(TAG, this.getClass().getSimpleName() + ".onStop()");   		
		
		// Make sure the timer gets canceled so that the main screen does not
		// pop up after user has exited.
		timer.cancel();
		super.onStop();
	}

	private void startNextActivity() {

		Intent userIntent;

		userIntent = new Intent(this, Main.class);
		
		this.startActivity( userIntent);
		this.finish();
	}
	
	/*
	 * This may be the only chance we get to initialize the app since just
	 * about every other place (besides splash screen) may get executed
	 * more than once.
	 */
	private void initApp()
	{
		B2R_SettingsHolder.init( getBaseContext());
		// If user chose whether to send anonymous data, set analytics accordingly
		if (B2R_SettingsHolder.isSet( B2R_Setting.ANONYMOUS_DATA)){
			Analytics.setEnabled( B2R_SettingsHolder.getBoolean( B2R_Setting.ANONYMOUS_DATA));
			return;
		}
		
		Boolean myBoolean = readBooleanFromManifest("analyticsEnabled");
		if (myBoolean != null) {
		// ...otherwise initialize the user preference based on the application (manifest) setting 
			B2R_SettingsHolder.putBoolean( B2R_Setting.ANONYMOUS_DATA, myBoolean);
			return;
		}
		
		// ... else default to default setting
		Analytics.setEnabled(B2R_SettingsHolder.getBoolean(B2R_Setting.ANONYMOUS_DATA));
	}
	
	private Boolean readBooleanFromManifest(String key) {
		Boolean myBoolean = null;
		try {
		    ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
		    Bundle bundle = ai.metaData;
		    myBoolean = new Boolean(bundle.getBoolean(key));
		} catch (Exception e) {
		    Log.e("Splash", "Failed to load meta-data" + e.getMessage());
		    myBoolean = null;
		}
		return myBoolean;
	}

	/*
	 * Treat any screen touch as permission to proceed to main panel
	 */	
	@Override
	public void onClick(View arg0) {
		Log.d(TAG, this.getClass().getSimpleName() + ".onClick()");   		
		
		// Cancel timer so that the main screen does not get displayed twice
		// (once per user touch and again on timer expiration).
		timer.cancel();
		startNextActivity();
		
	}
}
