package org.t2health.breathe2relax;


import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class B2R_PresetExhaleLengthActivity extends BaseNavigationActivity implements OnClickListener{
	private boolean flag;
	
	@Override 
	public void onBackPressed() {
		B2R_Utility.stopLongTalk();
		super.onBackPressed();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	B2R_Utility.stopLongTalk();
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.b2r_pre_set_exhale_length);
		
		setTitle( R.string.setExhaleLength);

		flag = true;
		Intent intent = this.getIntent();
		if (intent != null) {
			Bundle bun = intent.getExtras();
			if (bun != null) {
				if (bun.containsKey("FIRST_LAUNCH")) {
					flag = false;
				}
			}
		}
		
		findViewById(R.id.buttonIamReadyExhale).setOnClickListener(this);
		findViewById(R.id.buttonCancelExhale).setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		Intent userIntent = null;

		switch (v.getId()) {
		case R.id.buttonIamReadyExhale:
			userIntent = new Intent(this, B2R_SetExhaleLengthActivity.class);
			if (!flag) {
				userIntent.putExtra("FIRST_LAUNCH", "YES");
			}
			break;
		case R.id.buttonCancelExhale:
			finish();
			break;
		}
		
		if(userIntent != null) {
			startActivity(userIntent);
			finish();
		}
	}

}
