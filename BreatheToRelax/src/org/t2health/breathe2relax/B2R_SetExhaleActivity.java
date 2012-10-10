package org.t2health.breathe2relax;

import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class B2R_SetExhaleActivity extends BaseNavigationActivity implements OnClickListener{
	private TextView resultText;
	private String inhaleLength = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b2r_post_exhale_length);

		Intent intent = this.getIntent();
		if (intent != null) {
			Bundle bun = intent.getExtras();
			if (bun != null) {
				inhaleLength = bun.getString(B2R_SetExhaleLengthActivity.DURATION);
			}
		}
	
		this.setTitle( R.string.setExhaleLength);
		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setRightNavigationButtonVisibility(View.GONE);


		findViewById(R.id.buttonPressSaveExhale).setOnClickListener(this);
		findViewById(R.id.buttonPressRetryExhale).setOnClickListener(this);

		resultText = (TextView)findViewById(R.id.textResultOfExhale);

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
		case R.id.buttonPressSaveExhale:
			B2R_SettingsHolder.put(B2R_Setting.EXHALE_LENGTH,inhaleLength);
			this.finish();
			break;
		case R.id.buttonPressRetryExhale:
			userIntent = new Intent(this, B2R_SetExhaleLengthActivity.class);
			startActivity(userIntent);
			this.finish();
			break;
		}
	}
}