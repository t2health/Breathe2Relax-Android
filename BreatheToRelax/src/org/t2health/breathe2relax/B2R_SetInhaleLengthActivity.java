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

public class B2R_SetInhaleLengthActivity extends BaseNavigationActivity implements OnTouchListener{
	private TextView mOldResultText;
	private TextView mResultText;
	private B2R_Zoom zoom;
	private ViewGroup group;
	private volatile boolean pressed = true;
	private volatile int counter = 1;
	private Intent userIntent;
	
	public static final String DURATION = "duration";
	private static final int MAX_ZOOM_SEC_TENTHS = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_inhale_length_text);
		
		setTitle( R.string.setInhaleLength);

		findViewById(R.id.buttonPressHereInhale).setOnTouchListener(this);

		mResultText = (TextView)findViewById(R.id.textCurrentMeasureInhale);
		mOldResultText = (TextView)findViewById(R.id.textPreviousMeasureInhale); 
		String s = B2R_SettingsHolder.get(B2R_Setting.INHALE_LENGTH);
		if (s != null) {
			float f = Integer.parseInt(s)/10.0F;
			mOldResultText.setText(f + " sec");
		}
		mOldResultText.setVisibility(View.VISIBLE);
		mResultText.setVisibility(View.VISIBLE);

		group = (ViewGroup)findViewById(R.id.main_main_main_Inhale);

		zoom = new B2R_Zoom(this, true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.buttonPressHereInhale:
			if ( event.getAction() == MotionEvent.ACTION_DOWN ) { 
				mOldResultText.setVisibility(View.VISIBLE);
				mResultText.setVisibility(View.VISIBLE);

				group.removeAllViews();

				String s = B2R_SettingsHolder.get(B2R_Setting.INHALE_LENGTH);
				if (s != null) {
					float f = Integer.parseInt(s)/10.0F;
					mOldResultText.setText(f+" sec");
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
				Log.d("B2R_SetInhaleLengthActivity", "Setting inhale to: " + counter);
				userIntent = new Intent(this, B2R_SetInhaleActivity.class);
				userIntent.putExtra(B2R_SetInhaleLengthActivity.DURATION,""+counter);

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
			//update your view from here only.   
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
