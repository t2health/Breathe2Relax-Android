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
import org.t2health.lib.analytics.Analytics;
import org.t2health.lib.preference.BasePreferenceNavigationActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Settings extends BasePreferenceNavigationActivity
	implements OnPreferenceClickListener, OnSharedPreferenceChangeListener {
	private static final String ANALYTICS_EVENT = "SETTINGS SCREEN";

	public static final String settings_background_key = "background";
	public static final String settings_metronome_key = "metronome";
	public static final String settings_visual_prompt_key = "visual_prompt";	
	public static final String settings_audio_prompt_key = "audio_prompt";
	public static final String settings_breathing_instruction_key = "breathing_instruction";
	public static final String settings_play_music_key = "play_music";
	public static final String settings_background_music_key = "background_music";
	public static final String settings_inhale_length_key = "inhale_length";
	public static final String settings_exhale_length_key = "exhale_length";
	public static final String settings_num_cycles_key = "num_cycles";
	public static final String settings_track_stress_key = "track_stress";
	public static final String settings_guide_prompt_key = "guide_prompt";
	public static final String settings_rate_key = "rate";
	public static final String settings_feedback_key = "feedback";
	public static final String settings_anon_data_key = "anon_data";
	public static final String settings_youtube_key = "youtube";
	public static final String prevent_screen_timeout_key = "prevent_screen_timeout";

	public static final String BACKGROUNDMUSIC_SELECTED_VALUE = "BACKGROUND_MUSIC_SELECTED";
	public static final String MOTIF_SELECTED_VALUE = "MOTIF_SELECTED";

	public static final int MOTIF_CODE = 100;
	public static final int MUSIC_CODE = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		addPreferencesFromResource(R.xml.settings);

		this.setLeftNavigationButtonText( R.string.buttonClose);
		this.setTitle( R.string.settings);
	
		PreferenceScreen screen = this.getPreferenceScreen();
		screen.findPreference( settings_background_key).setOnPreferenceClickListener(this);
		screen.findPreference( settings_background_music_key).setOnPreferenceClickListener(this);
		screen.findPreference( settings_inhale_length_key).setOnPreferenceClickListener(this);
		screen.findPreference( settings_exhale_length_key).setOnPreferenceClickListener(this);
		//screen.findPreference( settings_rate_key).setOnPreferenceClickListener(this);
		screen.findPreference( settings_feedback_key).setOnPreferenceClickListener(this);
	
		// Set number of cycles summary string with current value
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		// Set number of cycles summary string with current value
		setNumCycles( sharedPref);
		// Set inhale summary string with current value
		setInhaleSummary( sharedPref);
		// Set exhale summary string with current value
		setExhaleSummary( sharedPref);
		// Set selected background theme string with current value
		setMotifSummary( sharedPref);
		// Set selected background music string with current value
		setMusicSummary( sharedPref);
	
		sharedPref.registerOnSharedPreferenceChangeListener(this);
	
		Analytics.onEvent(ANALYTICS_EVENT);
	}


	@Override
	public boolean onPreferenceClick(Preference preference) {
		boolean handledClick = true;
		Intent userIntent = null;
		Bundle bundle = null;
		int returnCode = 0;
	
		// Background image
		if (preference.getKey().equals( settings_background_key))
		{
			userIntent = new Intent(this, B2R_ChooseVisualSettingsActivity.class);
			bundle = new Bundle();
			returnCode = MOTIF_CODE;
			bundle.putString(MOTIF_SELECTED_VALUE, "");
			userIntent.putExtras(bundle);

			B2R_Utility.clearRecycleables();
		}
	
		// Background music
		else if (preference.getKey().equals( settings_background_music_key))
		{
			userIntent = new Intent(this, B2R_SelectedBackgroundMusicActivity.class);
			bundle = new Bundle();
			returnCode = MUSIC_CODE;
			bundle.putString(BACKGROUNDMUSIC_SELECTED_VALUE, 
					B2R_SettingsHolder.get( B2R_Setting.BACKGROUND_MUSIC_SELECTED));
			userIntent.putExtras(bundle);
		}

		// Inhale
		else if (preference.getKey().equals( settings_inhale_length_key))
		{
			userIntent = new Intent(this, B2R_PresetInhaleLengthActivity.class);
		}
		// Exhale
		else if (preference.getKey().equals( settings_exhale_length_key))
		{
			userIntent = new Intent(this, B2R_PresetExhaleLengthActivity.class);
		}
		// Product rating
/*
		else if (preference.getKey().equals( settings_rate_key))
		{
			final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setMessage("Not implemented in this release");
			alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {}
				}
			});
			alertbox.show();
		}
*/
		// Product feedback
		else if (preference.getKey().equals( settings_feedback_key))
		{
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setMessage("No Email Connectivity");
			alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			if (check()) {
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				emailIntent.setType("vnd.android.cursor.dir/email");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "t2.org@gmail.com"});
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "B2R Feedback");
				// emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "text");
				// emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File("Filename")));
				try {
					startActivity(Intent.createChooser(emailIntent, "Send email..."));
				} catch (Exception ex) {
					Log.d("B2R_EmailActivity", "Exception while sending email");
					alertbox.show();
				} 
			} else  {
				alertbox.show();
			}
		}

		if(userIntent != null) {
			if (bundle != null && returnCode != 0) {
				startActivityForResult(userIntent, returnCode);
			} else {
				startActivity(userIntent);
			}
		}

		return handledClick;
	}


	/* (non-Javadoc)
	 * @see android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference.Preference, java.lang.Object)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key)
	{
		if (key.equals( settings_num_cycles_key))
		{
			setNumCycles( sharedPref);
		}
		else if (key.equals( settings_inhale_length_key))
		{
			// Set inhale summary string with current value
			setInhaleSummary( sharedPref);
		}
		else if (key.equals( settings_exhale_length_key))
		{
			// Set exhale summary string with current value
			setExhaleSummary( sharedPref);
		}
		else if (key.equals( settings_anon_data_key)) {
			Analytics.setEnabled( B2R_SettingsHolder.getBoolean( B2R_Setting.ANONYMOUS_DATA));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			String value;
			if (requestCode == MOTIF_CODE) {
				value = data.getStringExtra(MOTIF_SELECTED_VALUE);
				setMotifSummary( value);
				B2R_SettingsHolder.put(B2R_Setting.MOTIF_SELECTED, value);
			} else if (requestCode == MUSIC_CODE) {
				value = data.getStringExtra(BACKGROUNDMUSIC_SELECTED_VALUE);
				setMusicSummary( value);
				B2R_SettingsHolder.put(B2R_Setting.BACKGROUND_MUSIC_SELECTED, value);
			} 
		}
	}


	/* (non-Javadoc)
	 * @see org.t2health.lib.preference.BasePreferenceNavigationActivity#onLeftNavigationButtonPressed()
	 */
	@Override
	protected void onLeftNavigationButtonPressed()
	{
		applySettings();
		super.onLeftNavigationButtonPressed();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{
		applySettings();
		super.onBackPressed();
	}

	private boolean check() {
		boolean is_mobile = true;
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo mobile = null;
		
		try {
			mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		} catch (Exception ex) {
			ex.printStackTrace();
			is_mobile = false;
		}
		
		// WiFi or SMTP 
		return B2R_Utility.canWeb(this) || (is_mobile && mobile != null && mobile.isAvailable() && mobile.isConnectedOrConnecting());
	}

	// ===== Update preference summaries with current values =====

	private void setNumCycles( SharedPreferences sharedPref)
	{
		PreferenceScreen screen = this.getPreferenceScreen();
		screen.findPreference( settings_num_cycles_key).setSummary(
				String.format( this.getResources().getString(
				R.string.settings_num_cycles_summary),
				sharedPref.getString( settings_num_cycles_key, "16")));
	}

	// Set inhale summary string with current value
	private void setInhaleSummary( SharedPreferences sharedPref)
	{
		PreferenceScreen screen = this.getPreferenceScreen();
		// Stored value is in 10ths of seconds, so adjust it.
		String value = sharedPref.getString( settings_inhale_length_key, "?");
		String adjustedValue = value.equals("?") ? "?" :
			String.format( "%.1f", Integer.valueOf( value)/10.0);
		// Set inhale summary string with current value
		screen.findPreference( settings_inhale_length_key).setSummary(
				String.format( this.getResources().getString(
				R.string.settings_inhale_length_summary), adjustedValue));
	}

	// Set exhale summary string with current value
	private void setExhaleSummary( SharedPreferences sharedPref)
	{
		PreferenceScreen screen = this.getPreferenceScreen();
		// Stored value is in 10ths of seconds, so adjust it.
		String value = sharedPref.getString( settings_exhale_length_key, "?");
		String adjustedValue = value.equals("?") ? "?" :
			String.format( "%.1f", Integer.valueOf( value)/10.0);
		// Set exhale summary string with current value
		screen.findPreference( settings_exhale_length_key).setSummary(
				String.format( this.getResources().getString(
				R.string.settings_exhale_length_summary), adjustedValue));
	}


	// Set background motif summary string with current value
	private void setMotifSummary( SharedPreferences sharedPref)
	{
		setMotifSummary( sharedPref.getString( settings_background_key,
				B2R_Setting.MOTIF_SELECTED.getDefaultValue()));
	}
	private void setMotifSummary( String value)
	{
		PreferenceScreen screen = this.getPreferenceScreen();
		screen.findPreference( settings_background_key).setSummary(
				String.format( this.getResources().getString(
				R.string.settings_background_desc), value));
	}

	// Set selected background music summary string with current value
	private void setMusicSummary( SharedPreferences sharedPref)
	{
		setMusicSummary( sharedPref.getString( settings_background_music_key,
				B2R_Setting.BACKGROUND_MUSIC_SELECTED.getDefaultValue()));
	}
	private void setMusicSummary( String value)
	{
		PreferenceScreen screen = this.getPreferenceScreen();
		// Set selected background music string with current value
		screen.findPreference( settings_background_music_key).setSummary(
				String.format( this.getResources().getString(
				R.string.settings_background_music_desc), value));
	}

	private void applySettings()
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		// Reset guide prompts
		if ( sharedPref.getBoolean( settings_guide_prompt_key, false ))
		{
			// Reset all page-specific guide prompts back on, then turn this
			// setting back off (momentary action).
			Editor editor = sharedPref.edit();
			for (B2R_Menu m : B2R_Menu.values())
			{
				editor.putBoolean( m.name(), true);
				B2R_SettingsHolder.setShow( m);
			}
			editor.putBoolean( settings_guide_prompt_key, false);
			editor.commit();
		}
	}

	public static boolean isPersonalized()
	{
		boolean personalized;
		if ( B2R_SettingsHolder.isSet(B2R_Setting.MOTIF_SELECTED) &&
				B2R_SettingsHolder.get(B2R_Setting.MOTIF_SELECTED).
				equals( B2R_Setting.MOTIF_SELECTED.getDefaultValue() ))
		{
			 personalized = false;
		}
		else
		{
			personalized =
					B2R_SettingsHolder.get(B2R_Setting.MOTIF_SELECTED) != null &&
					B2R_SettingsHolder.get(B2R_Setting.INHALE_LENGTH) != null &&
					B2R_SettingsHolder.get(B2R_Setting.EXHALE_LENGTH) != null &&
					B2R_SettingsHolder.get(B2R_Setting.BACKGROUND_MUSIC_SELECTED) != null;
		}
		return personalized;
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item){
		if (item.getItemId() == R.id.buttonShowMeHowOnYoutubeOn) {
			B2R_SettingsHolder.putBoolean( B2R_Setting.YOUTUBE_FALLBACK, true);
		} else if (item.getItemId() == R.id.buttonShowMeHowOnYoutubeOff) {
			B2R_SettingsHolder.putBoolean( B2R_Setting.YOUTUBE_FALLBACK, false);
		}

		return super.onOptionsItemSelected( item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.youtube_option, menu);
		return true;
	}
}
