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


import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class B2R_SetExhaleLengthActivity extends BaseNavigationActivity implements OnTouchListener{

	public static final String DURATION = "duration";
	private static final int MAX_ZOOM_SEC_TENTHS = 100;
	
	private TextView mOldResultText;
	private TextView mResultText;
	private B2R_Zoom zoom;
	private ViewGroup group;
	private volatile boolean pressed = true;
	private volatile int counter = 1;
	private Intent userIntent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_exhale_length_text);
		
		setTitle( R.string.setExhaleLength);

		findViewById(R.id.buttonPressHereExhale).setOnTouchListener(this);

		mResultText = (TextView)findViewById(R.id.textCurrentMeasureExhale);
		mOldResultText = (TextView)findViewById(R.id.textPreviousMeasureExhale);
		String s = B2R_SettingsHolder.get(B2R_Setting.EXHALE_LENGTH);
		if (s != null) {
			int i  = Integer.parseInt(s);
			float f = (float)i;
			f = f/10.0F;
			mOldResultText.setText(f+" sec");
		}
		mOldResultText.setVisibility(View.VISIBLE);
		mResultText.setVisibility(View.VISIBLE);

		group = (ViewGroup)findViewById(R.id.main_main_main_Exhale);

		zoom = new B2R_Zoom(this, false);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.buttonPressHereExhale:
			if ( event.getAction() == MotionEvent.ACTION_DOWN ) { 
				mOldResultText.setVisibility(View.VISIBLE);
				mResultText.setVisibility(View.VISIBLE);

				group.removeAllViews();

				String s = B2R_SettingsHolder.get(B2R_Setting.EXHALE_LENGTH);
				if (s != null) {
					float f = Integer.parseInt(s)/10.0F;
					mOldResultText.setText(f + " sec");
				}

				mResultText.setText("0.0 sec");
				 
				RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				
				iconParams.addRule(RelativeLayout.BELOW, mOldResultText.getId());
				group.addView(zoom, iconParams);


				Thread myThread = new Thread(myRunnable);
				myThread.start();

				return true;                 
			} else if (  event.getAction() == MotionEvent.ACTION_UP ) { 
				pressed = false;

				float f = counter/10.0F;
				mOldResultText.setText(f + " sec");
				
				Log.d("B2R_SetExhaleLengthActivity", "Setting exhale count: " + counter);
				userIntent = new Intent(this, B2R_SetExhaleActivity.class);
				userIntent.putExtra(B2R_SetExhaleLengthActivity.DURATION,""+counter);

				Runnable runnable = new Runnable() {
					public void run() {
						exitHandler.sendEmptyMessage(0);
					}
				};
				new Thread(runnable).start();
				
				return true;   
			}
			return false; 
		}

		return false;
	}

	private Handler exitHandler = new Handler() {           
		public void  handleMessage(Message msg) {                
			startActivity(userIntent);
			finish();
		}      
	};
	
	private Handler handler = new Handler() {           
		public void  handleMessage(Message msg) {                
			
			mResultText.clearComposingText();
			float f = counter/10.0F;
			
			mResultText.setText(f + " sec");
			mResultText.invalidate();

			if (counter < MAX_ZOOM_SEC_TENTHS) {
				zoom.zoomIt();
			}
		}      
	};

	private Runnable myRunnable = new Runnable() {

		@Override
		public void run() {
			while (pressed) {
				counter++;
				try {
					handler.sendEmptyMessage(0);
					Thread.sleep(100);
				} catch (Exception e) {
					Log.e("BACKGROUND_PROC", e.getMessage());
				}
			}
		}
	};


}
