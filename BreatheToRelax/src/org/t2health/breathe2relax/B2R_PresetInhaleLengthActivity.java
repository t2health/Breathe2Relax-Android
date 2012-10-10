package org.t2health.breathe2relax;


import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class B2R_PresetInhaleLengthActivity extends BaseNavigationActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.b2r_pre_set_inhale_length);
		
		setTitle( R.string.setInhaleLength);
		
		findViewById(R.id.buttonIamReadyInhale).setOnClickListener(this);
		findViewById(R.id.buttonCancelInhale).setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		Intent userIntent = null;

		switch (v.getId()) {
		case R.id.buttonIamReadyInhale:
			userIntent = new Intent(this, B2R_SetInhaleLengthActivity.class);
			break;
		case R.id.buttonCancelInhale:
			finish();
			break;
		}
		
		if(userIntent != null) {
			startActivity(userIntent);
			finish();
		}
	}


}

