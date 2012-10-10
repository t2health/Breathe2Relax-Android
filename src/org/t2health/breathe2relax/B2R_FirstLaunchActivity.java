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

import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class B2R_FirstLaunchActivity extends BaseNavigationActivity implements OnClickListener {
	
	private Button mSetInhaleButton;
	private Button mSetExhaleButton;
	private Button mSetScenery;
	private Button mSetBackgroundMusic;
	@SuppressWarnings("all")
	private Class nextActivity;
	
	public static final String NEXT_SCREEN = "nextScreen";
	
	@Override
	@SuppressWarnings("all")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.b2r_firstlaunchlayout);
		
		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setLeftNavigationButtonText( R.string.buttonClose);
		this.setTitle( R.string.personalize);
		
		(mSetInhaleButton = (Button)findViewById(R.id.buttonSetInhalePreferencesFirstLaunch)).setOnClickListener(this);
		(mSetExhaleButton = (Button)findViewById(R.id.buttonSetExhalePreferencesFirstLaunch)).setOnClickListener(this);
		(mSetScenery = (Button)findViewById(R.id.buttonChooseVisualFirstLaunch)).setOnClickListener(this);
		(mSetBackgroundMusic = (Button)findViewById(R.id.buttonChooseMusicFirstLaunch)).setOnClickListener(this);
		findViewById(R.id.buttonFirstLaunchShowMeHowFirstLaunch).setOnClickListener(this);

		Intent intent;
		Bundle bun;
		if ( (intent = this.getIntent()) != null &&
				(bun = intent.getExtras()) != null )
		{
			nextActivity = (Class)bun.getSerializable( NEXT_SCREEN);
		}

		doStart();
	}

	@Override
	public void onClick(View v) {
		Intent userIntent = null;
		Bundle bundle = null;
		
		switch ( v.getId() )
		{
		case R.id.buttonSetInhalePreferencesFirstLaunch :
		{
			userIntent = new Intent( this, B2R_PresetInhaleLengthActivity.class);
			startActivity( userIntent);
			break;
		}
		case R.id.buttonSetExhalePreferencesFirstLaunch :
		{
			userIntent = new Intent( this, B2R_PresetExhaleLengthActivity.class);
			startActivity( userIntent);
			break;
		}
		case R.id.buttonChooseVisualFirstLaunch :
		{
			userIntent = new Intent( this, B2R_ChooseVisualSettingsActivity.class);
			bundle = new Bundle();
			bundle.putString( Settings.MOTIF_SELECTED_VALUE, "");
			userIntent.putExtras( bundle);
			startActivityForResult( userIntent, Settings.MOTIF_CODE);
			break;
		}
		case R.id.buttonChooseMusicFirstLaunch :
		{
			userIntent = new Intent( this, B2R_SelectedBackgroundMusicActivity.class);
			bundle = new Bundle();
			bundle.putString( Settings.BACKGROUNDMUSIC_SELECTED_VALUE,
					B2R_SettingsHolder.get( B2R_Setting.BACKGROUND_MUSIC_SELECTED));
			userIntent.putExtras( bundle);
			startActivityForResult( userIntent, Settings.MUSIC_CODE);
			break;
		}
		case R.id.buttonFirstLaunchShowMeHowFirstLaunch :
		{
        	userIntent = new Intent(this, VideoActivity.class);
        	userIntent.putExtra(VideoActivity.EXTRA_VIDEO_ID, "937057278001"); // Show Me How video (BrightCove)
			startActivity( userIntent);
			break;
		}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data != null) {
			if (requestCode == Settings.MOTIF_CODE) {
				String d = data.getStringExtra( Settings.MOTIF_SELECTED_VALUE);
				if (d != null) {
					if (!d.equalsIgnoreCase(B2R_Setting.MOTIF_SELECTED.getDefaultValue())) {
						lambda(mSetScenery);
					} else {
						lambda(mSetScenery, R.drawable.more);
					}
				}
				B2R_SettingsHolder.put(B2R_Setting.MOTIF_SELECTED, d);
			} else if (requestCode == Settings.MUSIC_CODE) {
				String d = data.getStringExtra( Settings.BACKGROUNDMUSIC_SELECTED_VALUE);
				B2R_SettingsHolder.put(B2R_Setting.BACKGROUND_MUSIC_SELECTED, d);
				lambda(mSetBackgroundMusic);
			} 
		}
	}

	/*
	 * Populate the settings buttons from stored preferences.
	 */
	private void doStart()
	{
		try
		{
			for (B2R_Setting key : new B2R_Setting[] {
					B2R_Setting.MOTIF_SELECTED,
					B2R_Setting.BACKGROUND_MUSIC_SELECTED,
					B2R_Setting.INHALE_LENGTH,
					B2R_Setting.EXHALE_LENGTH })
			{
				String value = B2R_SettingsHolder.get( key);
				if (value != null && value.length() > 0)
				{
					switch (key)
					{
					case MOTIF_SELECTED:
						if (!value.equals( B2R_Setting.MOTIF_SELECTED.getDefaultValue()))
						{
							lambda( mSetScenery);
						}
						break;
					case BACKGROUND_MUSIC_SELECTED:
						lambda( mSetBackgroundMusic);
						break;
					case INHALE_LENGTH:
						setInhaleLength( value);
						break;
					case EXHALE_LENGTH:
						setExhaleLength( value);
						break;
					}
				}
			}
		}
		catch (Exception ex)
		{
			Log.d("B2R_FirstLaunchActivity", "Exception", ex);
		}
	}

	@Override
	public void onResume() {
		try {
			String exhale = B2R_SettingsHolder.get(B2R_Setting.EXHALE_LENGTH);
			String inhale = B2R_SettingsHolder.get(B2R_Setting.INHALE_LENGTH);

			if (inhale != null && inhale.length() > 0) {
				setInhaleLength( inhale);
			}
			if (exhale != null && exhale.length() > 0) {
				setExhaleLength( exhale);
			}	
		} catch (Exception ex) {
			Log.d("B2R_FirstLaunchActivity", "Exception", ex);
		}

		super.onResume();
	}
	
	@Override
	protected void onLeftNavigationButtonPressed() {
		doCancel();
		super.onLeftNavigationButtonPressed();
	}
	
	@Override 
	public void onBackPressed() {
		doCancel();
		super.onBackPressed();
	}

	private void doCancel() {
		if ( nextActivity != null )
		{
			startActivity( new Intent(this, nextActivity));
		}
	}

	private void setInhaleLength( String value)
	{
		try
		{
			float f = Integer.parseInt( value) / 10.0F;
			mSetInhaleButton.setText( "Set Inhale Length (" + f + " sec)");
			lambda( mSetInhaleButton);
		}
		catch (Exception ex)
		{
			Log.d( "B2R_FirstLaunchActivity", "INHALE_LENGTH", ex);
		}
	}
	
	private void setExhaleLength( String value)
	{
		try
		{
			float f = Integer.parseInt( value) / 10.0F;
			mSetExhaleButton.setText( "Set Exhale Length (" + f + " sec)");
			lambda( mSetExhaleButton);
		}
		catch (Exception ex)
		{
			Log.d( "B2R_FirstLaunchActivity", "EXHALE_LENGTH", ex);
		}
	}

	private void lambda( Button b)
	{

		lambda( b, R.drawable.tick);
	}

	private void lambda( Button b, int resource)
	{
		b.setCompoundDrawablesWithIntrinsicBounds( 0, 0, resource, 0);
	}
}
