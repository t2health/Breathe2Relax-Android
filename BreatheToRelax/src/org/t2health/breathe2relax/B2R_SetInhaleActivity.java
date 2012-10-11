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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class B2R_SetInhaleActivity extends BaseNavigationActivity implements OnClickListener{
	private TextView resultText;
	private String inhaleLength = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b2r_post_inhale_length);

		Intent intent = this.getIntent();
		if (intent != null) {
			Bundle bun = intent.getExtras();
			if (bun != null) {
				inhaleLength = bun.getString(B2R_SetInhaleLengthActivity.DURATION);
			}
		}

		this.setTitle( R.string.setInhaleLength);
		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setRightNavigationButtonVisibility(View.GONE);
		

		findViewById(R.id.buttonPressSaveInhale).setOnClickListener(this);
		findViewById(R.id.buttonPressRetryInhale).setOnClickListener(this);

		resultText = (TextView)findViewById(R.id.textResultOfInhale);

		try {
			if (inhaleLength != null) {
				float f = Integer.parseInt(inhaleLength)/10.0F;
				resultText.setText("" + f + resultText.getText());
			}
		} catch (Exception ex) {
			Log.d("B2R_SetExhaleActivity", "Exception reading value", ex);
		}
	}

	@Override
	public void onClick(View v) {
		Intent userIntent = null;
		
		switch (v.getId()) {
		case R.id.buttonPressSaveInhale:
			B2R_SettingsHolder.put(B2R_Setting.INHALE_LENGTH,inhaleLength);
			this.finish();
			break;
		case R.id.buttonPressRetryInhale:
			userIntent = new Intent(this, B2R_SetInhaleLengthActivity.class);
			startActivity(userIntent);
			finish();
			break;
		}
	}
}