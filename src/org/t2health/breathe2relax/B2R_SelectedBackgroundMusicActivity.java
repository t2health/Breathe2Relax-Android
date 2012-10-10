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

import java.util.ArrayList;

import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class B2R_SelectedBackgroundMusicActivity extends BaseNavigationActivity implements OnClickListener {
	private ArrayList<B2R_Music> m_music;
	private MusicTextAdapter m_adapter;
	private ListView listView;
	private B2R_Music it;
	
	private Button mPreviewButton;
	
	private TextView mTextTitle;
	private TextView mTextDescription;
	private TextView mTextCredits;
	
	private View myView;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        Log.d(this.getClass().getName(), "android back button pressed");
	        
	        B2R_Utility.stopPlayPreviewMusic();
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onLeftNavigationButtonPressed() {
		Log.d(this.getClass().getName(), "b2r back button pressed");
		
		B2R_Utility.stopPlayPreviewMusic();
		
		super.onLeftNavigationButtonPressed();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.b2r_selected_background_music);

		this.setRightNavigationButtonVisibility(View.GONE);
		this.setTitle( R.string.backgroundMusic);
		
		findViewById(R.id.buttonuseselectionID).setOnClickListener(this);

		mPreviewButton = (Button)findViewById(R.id.buttonpreviewselectionID);
		mPreviewButton.setOnClickListener(this);
		
		mTextTitle = (TextView)findViewById(R.id.textmh2);
		mTextDescription = (TextView)findViewById(R.id.textmh4);
		mTextCredits = (TextView)findViewById(R.id.textmh6);

		getMusicText();
		this.m_adapter = new MusicTextAdapter(this, R.layout.selected_background_music_row, m_music);
		setListView();
		
		String musicName = null;
		if ( savedInstanceState != null )
		{
			musicName = savedInstanceState.getString( Settings.BACKGROUNDMUSIC_SELECTED_VALUE);
		}
		if ( musicName == null )
		{
			Intent intent;
			Bundle bun;
			if ( (intent = this.getIntent()) != null &&
					(bun = intent.getExtras()) != null )
			{
				musicName = bun.getString( Settings.BACKGROUNDMUSIC_SELECTED_VALUE);
			}
		}
		B2R_Music currentMusic = (musicName == null) ?
				B2R_Music.RANDOM : B2R_Music.fromString( musicName);
		if ( currentMusic == B2R_Music.NO_MUSIC ) currentMusic = B2R_Music.RANDOM;
		mTextTitle.setText(currentMusic.getText());
		mTextDescription.setText(currentMusic.getDescription());
		mTextCredits.setText(currentMusic.getCredits());
		
	}

	@Override
	public void onClick(View v) {
		B2R_Utility.stopPlayPreviewMusic();

		switch (v.getId()) {
		case R.id.buttonuseselectionID:
			if (it != null) {
				if (myView != null) myView.setBackgroundColor(Color.BLACK);
				
				Intent userIntent = new Intent();
				Bundle bundle = new Bundle();
				int returnCode = Settings.MUSIC_CODE;
				
				bundle.putString(Settings.BACKGROUNDMUSIC_SELECTED_VALUE, it.getText());
				userIntent.putExtras(bundle);

				setResult(returnCode, userIntent);
				finish();
			}
			break;
		case R.id.buttonpreviewselectionID:
			if (it != null) {
				for (int i = 0; i < m_music.size(); i++) {
					if (it.equals(m_music.get(i))) {
						B2R_Utility.playPreviewMusic(this, it);
					}
				}
			}
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState( Bundle outState)
	{
		// TODO Auto-generated method stub
		if (it != null && it.getText() != null && outState != null) {
			outState.putString( Settings.BACKGROUNDMUSIC_SELECTED_VALUE, it.getText());
		}
		super.onSaveInstanceState( outState);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			it = B2R_Music.fromString(savedInstanceState.getString( Settings.BACKGROUNDMUSIC_SELECTED_VALUE));
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	public class MusicTextAdapter extends ArrayAdapter<B2R_Music> {
		private ArrayList<B2R_Music> items;

		public MusicTextAdapter(Context context, int textViewResourceId, ArrayList<B2R_Music> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return new MusicTextAdapterView(this.getContext(), items.get(position) );
		}
	}

	class MusicTextAdapterView extends LinearLayout {        
		public MusicTextAdapterView(Context context, B2R_Music icontext ) {
			super( context );

			this.setVerticalScrollBarEnabled(true);
			this.setOrientation(VERTICAL);       

			LinearLayout.LayoutParams textParams = 
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			textParams.setMargins(1, 1, 1, 1);
			TextView textControl = new TextView( context );
			textControl.setText( icontext.getText() );
			textControl.setTextSize(18f);
			textControl.setTextColor(Color.WHITE);
			textControl.setSingleLine(true);
			addView( textControl, textParams);      

			textControl = new TextView( context );
			textControl.setText( icontext.getDescription() );
			textControl.setTextSize(14f);
			textControl.setTextColor(Color.GRAY);
			textControl.setLines(1);
			textControl.setEllipsize( TextUtils.TruncateAt.END);
			
			addView( textControl, textParams);      
		}
	}

	private void getMusicText(){
		m_music = new ArrayList<B2R_Music>();
		
		m_music.add( B2R_Music.RANDOM);
		m_music.add( B2R_Music.AMBIENT_EVENINGS);
		m_music.add( B2R_Music.EVO_SOLUTION);
		m_music.add( B2R_Music.OCEAN_MIST);
		m_music.add( B2R_Music.WANING_MOMENTS);
		m_music.add( B2R_Music.WATER_MARKS);
		
		Log.d("ARRAY", ""+ m_music.size());
	}

	private void setListView() {
		listView = (ListView) findViewById(R.id.backgroundmusiclistview);

		listView.setAdapter(m_adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				B2R_Utility.stopPlayPreviewMusic();
			
				if (myView != null) myView.setBackgroundColor(Color.BLACK);
				
				it = ((B2R_Music)parent.getItemAtPosition(position));
				if (it != null) {
					Log.d("SelectedBackgroundMusicActivity", "item selected: " + it.getText());
					B2R_SettingsHolder.put(B2R_Setting.BACKGROUND_MUSIC_SELECTED, it.getText());
					myView = view;
					if (myView != null) {
						mTextTitle.setText(it.getText());
						mTextDescription.setText(it.getDescription());
						mTextCredits.setText(it.getCredits());
						
						if (it.getText().equals(B2R_Music.RANDOM.getText())) {
							mPreviewButton.getHandler().post(new Runnable() {
								public void run() {
									mPreviewButton.setEnabled( false);
								}
							});
						} else {
							mPreviewButton.getHandler().post(new Runnable() {
								public void run() {
									mPreviewButton.setEnabled( true);
								}
							});
						}
					}
					
				}
			}
		});
	}
	
	@Override
	public void onDestroy() {
		Log.d("B2R_SelectedBackgroundMusicActivity", "onDestroy");

		if (it != null && it.getText() != null) {
			Intent intent;
			Bundle bun;
			if ( (intent = this.getIntent()) != null && (bun = intent.getExtras()) != null ) {
				bun.putString( Settings.BACKGROUNDMUSIC_SELECTED_VALUE, it.getText());
			}
		}
		super.onDestroy();
	}
}
