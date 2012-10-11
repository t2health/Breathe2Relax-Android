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

import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class B2R_StartBreathingActivity extends BaseNavigationActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b2r_start_breathing);

		this.setTitle( R.string.breatheLabel);
		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setRightNavigationButtonVisibility(View.GONE);

		findViewById(R.id.buttonStartBreathingSession).setOnClickListener(this);

		try {
			BitmapDrawable bitmapDrawable = null;
			B2R_Motif selected = B2R_Motif.fromString(B2R_SettingsHolder.get(B2R_Setting.MOTIF_SELECTED));

			if (!selected.equals(B2R_Motif.NO_MOTIF)) {
				bitmapDrawable = B2R_Utility.getReducedBitmapDrawable(this.getResources(), B2R_Utility.BitmapOrder.FIRST);
				if (bitmapDrawable != null) {
					ImageView background = (ImageView)findViewById(R.id.backgroundStartBreathing);
					background.setImageDrawable(bitmapDrawable);
				}
				bitmapDrawable = null;
			}
		} catch (Exception ex) {
			Log.d("B2R_StartBreathingActivity", "Exception", ex);
		}
	}
	
	@Override
	protected void onLeftNavigationButtonPressed() {
		stopStartBreathing();
		super.onLeftNavigationButtonPressed();
	}
	
	@Override 
	public void onBackPressed() {
		stopStartBreathing();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		Intent userIntent = null;
		
		switch (v.getId()) {
		case R.id.buttonStartBreathingSession:
			userIntent = new Intent(this, B2R_InhaleActivity.class);
			break;
		}
		
		if (userIntent != null) {
			startActivity(userIntent);
		}
		
		finish();
	}

	@Override
	public void onDestroy() {
		Log.d("B2R_StartBreathingActivity", "onDestroy "+ B2R_Utility.getRecycleableCount());
	
		super.onDestroy();
	}

	
	@Override 
	public void onStop() {
		Log.d("B2R_StartBreathingActivity", "onStop ");
		
		super.onStop();
	}
	
	@Override 
	public void onStart() {
		Log.d("B2R_StartBreathingActivity", "onStart ");
		
		super.onStart();
	}
	
	@Override 
	public void onPause() {
		Log.d("B2R_StartBreathingActivity", "onPause ");
		 B2R_Utility.pauseLongTalk();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		Log.d("B2R_StartBreathingActivity", "onResume ");
		B2R_Utility.resumeLongTalk();
		if (B2R_SettingsHolder.show(B2R_Menu.BEFORE_STARTING_SUBMENU)) {
			showDialog(0);
		}
		overridePendingTransition(0,0);
		super.onResume();
	}
	

	@Override
	protected Dialog onCreateDialog(int i) {
		switch (i) {
		case 0: 
			if (B2R_SettingsHolder.show(B2R_Menu.BEFORE_STARTING_SUBMENU)) {
				return new B2R_PopupDialog(this, "file:///android_asset/html/b2r_breathe.html", B2R_Menu.BEFORE_STARTING_SUBMENU, R.raw.f_prestart);
			}
		}
		return null;
	}
	
	private void stopStartBreathing() {
		Boolean b = B2R_SettingsHolder.getBoolean(B2R_Setting.PLAY_MUSIC);
		if (b != null && b == true) {
			String music = B2R_SettingsHolder.get(B2R_Setting.BACKGROUND_MUSIC_SELECTED);
			if (music != null && music.length() > 0) {
				B2R_Utility.stopPlayMusic();
			}
		}
		B2R_Utility.clear(B2R_Utility.BitmapOrder.FIRST);
	}
}