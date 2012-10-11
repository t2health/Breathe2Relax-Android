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

import org.t2health.breathe2relax.B2R_Utility.BitmapOrder;
import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class B2R_EndBreathingActivity extends BaseNavigationActivity implements OnClickListener {

	@Override
	protected void onLeftNavigationButtonPressed() {
		Log.d(this.getClass().getName(), "LeftNavigation button pressed");
        B2R_Utility.stopLongTalk();
        B2R_Utility.stopShortTalkInhale();
        B2R_Utility.stopShortTalkExhale();
		super.onLeftNavigationButtonPressed();
	}
	
	@Override 
	public void onBackPressed() {
		Log.d(this.getClass().getName(), "Back button pressed");
        B2R_Utility.stopLongTalk();
        B2R_Utility.stopShortTalkInhale();
        B2R_Utility.stopShortTalkExhale();
		super.onBackPressed();
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b2r_rerun);

		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setRightNavigationButtonVisibility(View.GONE);

		findViewById(R.id.buttonFinished).setOnClickListener(this);

		findViewById(R.id.buttonRerun).setOnClickListener(this); 

		this.setTitle( R.string.breatheLabel);
		
		try {
			BitmapDrawable bitmapDrawable = null;
			B2R_Motif selected = B2R_Motif.fromString(B2R_SettingsHolder.get(B2R_Setting.MOTIF_SELECTED));

			if (!selected.equals(B2R_Motif.NO_MOTIF)) {
				bitmapDrawable = B2R_Utility.getReducedBitmapDrawable(this.getResources(), B2R_Utility.BitmapOrder.LAST);
				if (bitmapDrawable != null) {
					ImageView background = (ImageView)findViewById(R.id.backgroundReRun);
					background.setImageDrawable(bitmapDrawable);
				}
				bitmapDrawable = null;
			}
		} catch (Exception ex) {
			Log.d("B2R_EndBreathingActivity", "Exception", ex);
		}

		Runnable noMoreBitmapMemoryErrors = new Runnable() {
			public void run() {
				B2R_Utility.clear(B2R_Utility.BitmapOrder.RATE);
			}
		};
		new Thread(noMoreBitmapMemoryErrors).start();

		B2R_Utility.talkLongTalk(this, R.raw.f_poststart);
	}

	public void onClick(View v) {
		Intent userIntent = null;

		B2R_Utility.stopLongTalk();
		
		switch ( v.getId() ) {
		case R.id.buttonFinished:
			B2R_Utility.clearRecycleables();
			String s_b = B2R_SettingsHolder.get(B2R_Setting.RELAXED_STRESSED_BEFORE);
			if (!B2R_SettingsHolder.getBoolean(B2R_Setting.TRACK_STRESS_SKIP) &&
					B2R_SettingsHolder.getBoolean(B2R_Setting.TRACK_STRESS) &&
					!B2R_Utility.isTodayStressRated( this.getHelper()) && s_b != null) {

				userIntent = new Intent(this, B2R_RateStressAfterActivity.class);
			}
			break;
		case R.id.buttonRerun:
			B2R_Utility.clear(BitmapOrder.RATE);
			B2R_Utility.clear(BitmapOrder.FIRST);
			B2R_Utility.clear(BitmapOrder.BODY);
			
			userIntent = new Intent(this, B2R_InhaleActivity.class);
			
			userIntent.putExtra("RERUN", "YES");
			break;
		}
		
		if(userIntent != null) {
			startActivity(userIntent);
		}
		finish();
	}

	@Override
	public void onDestroy() {
		Log.d("B2R_EndBreathingActivity", "onDestroy: " + B2R_Utility.getRecycleableCount());

		super.onDestroy();
	}
}
