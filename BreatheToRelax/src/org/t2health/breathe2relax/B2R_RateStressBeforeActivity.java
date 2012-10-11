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
import org.t2health.lib.widget.TextImageButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;

public class B2R_RateStressBeforeActivity  extends BaseNavigationActivity implements B2R_SliderWidget.OnSliderWidgetChangeListener, OnClickListener {
	private CheckBox mRadioButton;
	private B2R_SliderWidget slider;

    @Override
    protected void onLeftNavigationButtonPressed() {
    	B2R_Utility.stopLongTalk();
       
        super.onBackPressed();
    }

    @Override 
    public void onBackPressed() {
    	B2R_Utility.stopLongTalk();
        
        super.onBackPressed();
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b2r_rate_pre_layout);

		this.setTitle( R.string.rateStress);
		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setRightNavigationButtonVisibility(View.GONE);
		
		TextImageButton button = (TextImageButton)this.findViewById(R.id.navigationRightButton);
		button.setText("Personalize");

		button.setTextVisibility(View.VISIBLE);
		button.setImageVisibility(View.GONE);

		findViewById(R.id.buttonNextPreRateYourStress).setOnClickListener(this);
		findViewById(R.id.buttonSkipPreRateYourStress).setOnClickListener(this);
		
		mRadioButton = (CheckBox)findViewById(R.id.radioButtonPreRateStress);
		mRadioButton.setOnClickListener(this);

		slider = (B2R_SliderWidget)findViewById(R.id.sliderBarWidget);
		slider.setOnSliderWidgetChangeListener(this);
		
		B2R_SettingsHolder.putBoolean(B2R_Setting.TRACK_STRESS_SKIP, false);
		
		try {
			BitmapDrawable bitmapDrawable = null;
			B2R_Motif selected = B2R_Motif.fromString(B2R_SettingsHolder.get(B2R_Setting.MOTIF_SELECTED));
			
			if (!selected.equals(B2R_Motif.NO_MOTIF)) {
				bitmapDrawable = B2R_Utility.getReducedBitmapDrawable(this.getResources(), B2R_Utility.BitmapOrder.RATE);
				if (bitmapDrawable != null) {
					ImageView background = (ImageView)findViewById(R.id.ratestressbeforeBreathing);
					background.setImageDrawable(bitmapDrawable);
				}
				bitmapDrawable = null;
			}
		} catch (Exception ex) {
			Log.d("B2R_RateStressBeforeActivity", "Exception", ex);
		}
		
		B2R_SettingsHolder.put(B2R_Setting.RELAXED_STRESSED_BEFORE, null);
		B2R_SettingsHolder.put(B2R_Setting.RELAXED_STRESSED_AFTER, null);
	}

	@Override
	public void onClick(View v) {
		Intent userIntent = null;

		switch (v.getId()) {
		case R.id.buttonNextPreRateYourStress:
			B2R_SettingsHolder.put(B2R_Setting.RELAXED_STRESSED_BEFORE, ""+slider.getProgress());
			userIntent = new Intent(this, B2R_StartBreathingActivity.class);
			B2R_SettingsHolder.putBoolean(B2R_Setting.TRACK_STRESS, !mRadioButton.isChecked());	
			break;
		case R.id.buttonSkipPreRateYourStress:
			B2R_SettingsHolder.putBoolean(B2R_Setting.TRACK_STRESS_SKIP, true);
			userIntent = new Intent(this, B2R_StartBreathingActivity.class);
			B2R_SettingsHolder.putBoolean(B2R_Setting.TRACK_STRESS, !mRadioButton.isChecked());	
			B2R_SettingsHolder.put(B2R_Setting.RELAXED_STRESSED_BEFORE, null);
			break;
		}

		if(userIntent != null) {
			startActivity(userIntent);
			finish();
		}
	}

	public void onProgressChanged(B2R_SliderWidget seekBar, int progress, boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(B2R_SliderWidget seekBar) {
	}

	@Override
	public void onStopTrackingTouch(B2R_SliderWidget seekBar) {	
	}
	
	@Override 
	public void onStop() {
		Log.d("B2R_RateStressBeforeActivity", "onStop ");
		
		super.onStop();
	}
	
	@Override 
	public void onStart() {
		Log.d("B2R_RateStressBeforeActivity", "onStart ");
		
		super.onStart();
	}
	
	@Override 
	public void onPause() {
		Log.d("B2R_RateStressBeforeActivity", "onPause ");
		B2R_Utility.pauseLongTalk();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		Log.d("B2R_RateStressBeforeActivity", "onResume ");
		B2R_Utility.resumeLongTalk();
		if (B2R_SettingsHolder.show(B2R_Menu.RATE_STRESS_BEFORE)) {
			showDialog(0);
		}
		overridePendingTransition(0,0);
		super.onResume();
	}

	public void onDestroy() {
		Log.d("B2R_RateStressBeforeActivity", "onDestroy: " + B2R_Utility.getRecycleableCount());
	
		super.onDestroy();
	}
	
	@Override
	protected Dialog onCreateDialog(int i) {
		
		switch (i) {
		case 0: 
			if (B2R_SettingsHolder.show(B2R_Menu.RATE_STRESS_BEFORE)) {
				return new B2R_PopupDialog(this, "file:///android_asset/html/b2r_before_breathing.html", B2R_Menu.RATE_STRESS_BEFORE, R.raw.f_prevas);
			}
		}
		return null;
	}
}
