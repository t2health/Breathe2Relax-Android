package org.t2health.breathe2relax;


import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ViewAnimator;

public class B2R_HelpActivity extends BaseNavigationActivity implements OnClickListener {
	private ViewAnimator switcher;
	
	private static final String WATCH_DIAPHRAGM = "937057278001";
	private static final String WATCH_BIOLOGY = "932645258001";
	private static final String WATCH_REACTION = "932799449001";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.b2r_read_n_watch_switcher);
		this.setLeftNavigationButtonVisibility(View.VISIBLE);

		this.setRightNavigationButtonVisibility(View.GONE);
		this.setTitle(R.string.buttonHelp);

		switcher = (ViewAnimator) findViewById(R.id.readwatchswitcher);
		
		findViewById(R.id.buttonReadBiologyOfStress).setOnClickListener(this);
		findViewById(R.id.buttonDiaphragmaticBreathing).setOnClickListener(this);
		findViewById(R.id.buttonEffectsOfStressOnTheBody).setOnClickListener(this);
		
		findViewById(R.id.buttonHowToBreath).setOnClickListener(this);
		findViewById(R.id.buttonWatchBiologyOfBreathing).setOnClickListener(this);
		findViewById(R.id.buttonBodysReactionToStress).setOnClickListener(this);

		findViewById(R.id.buttonReadWatchResults).setOnClickListener(this);
		findViewById(R.id.buttonWatchReadResults).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent userIntent = null;

		switch ( v.getId() ) {
		case R.id.buttonReadBiologyOfStress:
			userIntent = new Intent(this, B2R_ReadingActivity.class);
			userIntent.putExtra("FilePath", "file:///android_asset/html/learn.html");
			break;
		case R.id.buttonDiaphragmaticBreathing:
			userIntent = new Intent(this, B2R_ReadingActivity.class);
			userIntent.putExtra("FilePath", "file:///android_asset/html/watch.html");
			break;
		case R.id.buttonEffectsOfStressOnTheBody:
			B2R_Utility.clearRecycleables();
			userIntent = new Intent(this, B2R_BodyScanner.class);
			break;
		case R.id.buttonWatchBiologyOfBreathing:
        	userIntent = new Intent(this, VideoActivity.class);
        	userIntent.putExtra(VideoActivity.EXTRA_VIDEO_ID, WATCH_BIOLOGY);
			break;
		case R.id.buttonBodysReactionToStress:
        	userIntent = new Intent(this, VideoActivity.class);
        	userIntent.putExtra(VideoActivity.EXTRA_VIDEO_ID, WATCH_REACTION);
			break;
		case R.id.buttonHowToBreath:
        	userIntent = new Intent(this, VideoActivity.class);
        	userIntent.putExtra(VideoActivity.EXTRA_VIDEO_ID, WATCH_DIAPHRAGM);
			break;
		case R.id.buttonReadWatchResults:
			switcher.setDisplayedChild(1);
			break;
		case R.id.buttonWatchReadResults:
			switcher.setDisplayedChild(0);
			break;
		}

		if(userIntent != null) {
			startActivity(userIntent);
		}
	}

	@Override
	public void onResume() {
		if (B2R_SettingsHolder.show(B2R_Menu.LEARN_MENU)) {
			showDialog(0);
		}
		overridePendingTransition(0,0);
		super.onResume();
	}

	@Override
	protected Dialog onCreateDialog(int i) {
		return new B2R_PopupDialog(this, "file:///android_asset/html/b2r_learn.html", B2R_Menu.LEARN_MENU);
	}
}
