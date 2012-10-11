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

import java.util.Calendar;
import java.util.Date;

import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.j256.ormlite.dao.Dao;

public class B2R_RateStressAfterActivity  extends BaseNavigationActivity implements B2R_SliderWidget.OnSliderWidgetChangeListener, OnClickListener {
	private CheckBox mRadioButton;
	private B2R_SliderWidget slider;
	private StuffDataTask stuffData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b2r_rate_post_layout);

		this.setTitle( R.string.rateStress);
		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setRightNavigationButtonVisibility(View.GONE);

		findViewById(R.id.buttonNextPostRateYourStress).setOnClickListener(this);
		findViewById(R.id.buttonSkipPostRateYourStress).setOnClickListener(this);
		
		mRadioButton = (CheckBox)findViewById(R.id.radioButtonPostRateStress);
		mRadioButton.setOnClickListener(this);
		slider = (B2R_SliderWidget)findViewById(R.id.sliderBarWidget);

		B2R_Utility.talkLongTalk(this, R.raw.f_postvas);
		
		try {
			BitmapDrawable bitmapDrawable = null;
			B2R_Motif selected = B2R_Motif.fromString(B2R_SettingsHolder.get(B2R_Setting.MOTIF_SELECTED));
			
			if (!selected.equals(B2R_Motif.NO_MOTIF)) {
				bitmapDrawable = B2R_Utility.getReducedBitmapDrawable(this.getResources(), B2R_Utility.BitmapOrder.RATE);
				if (bitmapDrawable != null) {
					ImageView background = (ImageView)findViewById(R.id.ratestressafterBreathing);
					background.setImageDrawable(bitmapDrawable);
				}
				bitmapDrawable = null;
			}
		} catch (Exception ex) {
			Log.d("B2R_RateStressAfterActivity", "Exception", ex);
		}
	}

    @Override
    protected void onLeftNavigationButtonPressed() {
    	B2R_Utility.stopLongTalk();
        B2R_Utility.stopShortTalkInhale();
        B2R_Utility.stopShortTalkExhale();
        super.onBackPressed();
    }

    @Override 
    public void onBackPressed() {
    	B2R_Utility.stopLongTalk();
        B2R_Utility.stopShortTalkInhale();
        B2R_Utility.stopShortTalkExhale();
        super.onBackPressed();
    }
    
	
	@Override
	public void onClick(View v) {

		B2R_Utility.stopLongTalk();

		switch (v.getId()) {
		case R.id.buttonNextPostRateYourStress:
			if (!mRadioButton.isChecked()) {
				doSaveIt();
			}
			doDone();
			finish();
			break;
		case R.id.buttonSkipPostRateYourStress:
			doDone();
			finish();
			break;
		}
	}

	private void doDone() {
		B2R_SettingsHolder.putBoolean(B2R_Setting.TRACK_STRESS, !mRadioButton.isChecked());
	}
	
	private void doSaveIt() {
		B2R_Utility.clearRecycleables();
		
		stuffData = new StuffDataTask();
		stuffData.execute("");
	}
	
	private void doSave() {
		Date now = Calendar.getInstance().getTime();
		Integer before = null;
		Integer after = slider.getProgress();
		String s_b = B2R_SettingsHolder.get(B2R_Setting.RELAXED_STRESSED_BEFORE);
		
		// If 'before' level was not supplied (can that happen?), then bail.
		if ( s_b == null ) {
			return;
		}
		
		try {
			before = Integer.parseInt(s_b);
		} catch (Exception be) {
			Log.d("B2R_RateStressAfterActivity", "Exception", be);
			return;
		}
		
		B2R_SettingsHolder.put(B2R_Setting.RELAXED_STRESSED_AFTER, ""+after);
		
		B2R_MoodTrackingsTable row = new B2R_MoodTrackingsTable();
		try {
				Dao<B2R_MoodTrackingsTable, ?> dao = this.getHelper().getDao(B2R_MoodTrackingsTable.class);
				row.setDate(now);
				row.setBeforeResult(before);
				row.setAfterResult(after);
				// create record of before/after stress levels
				dao.create(row);
		} 
		catch (Exception ex) {
			Log.d("B2R_RateStressAfterActivity", "Exception", ex);
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
	
	public void onDestroy() {
		// TODO: Gets called too late! (causing bitmap size exceeds VM budget)
		Log.d("B2R_RateStressAfterActivity", "onDestroy: " + B2R_Utility.getRecycleableCount());
		B2R_Utility.clearRecycleables();
		super.onDestroy();
	}
	
	public void onPause() {
        Log.d("B2R_RateStressAfterActivity", "onPause");
        B2R_Utility.pauseLongTalk();
        
        super.onPause();
    }
    public void onResume() {
        Log.d("B2R_RateStressAfterActivity", "onResume");
        B2R_Utility.resumeLongTalk();
        
        super.onResume();
    }
    
	private class StuffDataTask extends AsyncTask<String, Integer, Void> {
	     protected Void doInBackground(String... dontCare) {
	    	 doSave();
	         return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	     }

	     protected void onPostExecute(Void result) {
	     }

	     protected void onPreExecute() {
	     }
	 }
}
