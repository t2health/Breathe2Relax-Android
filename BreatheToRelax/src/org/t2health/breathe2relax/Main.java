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
import org.t2health.lib.util.Eula;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 
 *   Splash     ---> Main    ---> Tip
 *                           ---> t2health.org
 *                    |
 *                    |
 *                    |
 *    ---------------------------------------                             Popup
 *    |       |                |            |                               |
 *    |       |                |            |                               |
 *   Help    ShowMe          Track        RateStress           -----------------------------
 *    |                                     |                  |       |        |    |     |
 *    |                                     |                  |       |        |    |     |
 *   -------------------                   Start             Help  RateStress Start Main Track
 *   |        |        |                    |
 *   |        |        |                    |
 *  Read    View      Scan                 End
 *                                          |
 *                                          |
 *                                       RateStress
 *            
 *            
 *               Menu
 *                |
 *                |
 *          --------------
 *          |            |
 *          |            |         ---> flurry
 *        WebView      Settings    ---> email 
 *                       |
 *                       |
 *   -------------------------------
 *   |            |                |
 *   |            |                |
 *  Visual      Music        Inhale/Exhale -------------> Inhale/Exhale Length
 * 
 *
 */
public class Main extends BaseNavigationActivity implements OnClickListener {
	private static final String TAG = "Breathe2Relax";
    private boolean once = true;
    
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Log.d(TAG, this.getClass().getSimpleName() + ".onCreate()");        
		Eula.show(this);        
        
        setContentView(R.layout.main);

        this.setLeftNavigationButtonText( R.string.personalize);
        this.setRightNavigationButtonVisibility(View.VISIBLE);
        this.setRightNavigationButtonText(R.string.buttonTip);
        
        findViewById(R.id.buttonMenuBreathe).setOnClickListener(this); // Breathe
        findViewById(R.id.buttonShowMeHow).setOnClickListener(this); // Show Me How
        findViewById(R.id.buttonMenuTrack).setOnClickListener(this); // Results
        findViewById(R.id.buttonMenuHelp).setOnClickListener(this);  // Learn
        
        findViewById(R.id.t2LogoMainView).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    if (B2R_Utility.canWeb(Main.this)) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://t2health.org/")));
                    } else {
                        Log.d("Main", "http://t2health.org/");
                    }
                } catch (Exception ex) {
                    Log.d("Main", "http://t2health.org/", ex);
                }
            }
        });
        
    }
    
    // Bring up 'Tip of the Day'
    @Override
    protected void onRightNavigationButtonPressed() {
        startActivity(new Intent(this, B2R_TipActivity.class));
    }

    @Override
    protected void onLeftNavigationButtonPressed() {
        Intent myIntent = new Intent(this, B2R_FirstLaunchActivity.class);

        startActivity(myIntent);
    }

    // Handle clicks on any clickable widgets (only buttons here).
    @Override
    public void onClick( View v) {
        Intent userIntent = null;

        switch (v.getId()) {
        // Bring up Breath page
        case R.id.buttonMenuBreathe:
            try {
                // If Track Stress was turned on and no stress ratings yet
                // recorded for today, go to 'Before' stress rating.
                Boolean b = B2R_SettingsHolder.getBoolean( B2R_Setting.TRACK_STRESS);
                if ((b != null && b == true) &&
                        !B2R_Utility.isTodayStressRated( this.getHelper())    ) {
                    userIntent = new Intent( this,
                            B2R_RateStressBeforeActivity.class);
                } else {
                // ...otherwise skip 'Before' stress rating and go to Breathing.
                    userIntent = new Intent( this,
                            B2R_StartBreathingActivity.class);
                }

            } catch (Exception br_ex) {
                Log.d("Main", "Exception", br_ex);
            }
            break;
        // Bring up Show Me How (video)
        case R.id.buttonShowMeHow:
        	
        	userIntent = new Intent(this, VideoActivity.class);
        	userIntent.putExtra(VideoActivity.EXTRA_VIDEO_ID, "937057278001"); // Show Me How video (BrightCove)
            break;
        // Bring up Results (Track) page.
        case R.id.buttonMenuTrack:
            try {
                userIntent = new Intent( this, B2R_TrackActivity.class);
            } catch (Exception tr_ex) {
                tr_ex.printStackTrace();
            }
            break;
        // Bring up Learn (Help) page.
        case R.id.buttonMenuHelp:
            try {
                userIntent = new Intent( this, B2R_HelpActivity.class);
                B2R_Utility.clearRecycleables();
            } catch (Exception lrn_ex) {
                Log.d("Main", "Exception", lrn_ex);
            }
            break;
        }

        if (userIntent != null)
        {
            startActivity( userIntent);
        }
    }
    
    @Override
    public void onStart() {
		Log.d(TAG, this.getClass().getSimpleName() + ".onStart()");        

        super.onStart();
        try {
            // If settings are already personalized, hide Personalize button.
            if ( Settings.isPersonalized() ) {
                this.setLeftNavigationButtonVisibility(View.GONE);
            } else {
            // ...otherwise show it.
                this.setLeftNavigationButtonVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Log.d("Main", "onStart Failure", ex);
        }
    }

    @Override
    public void onResume() {
        try {
            // Show main menu guide prompt, if allowed.
            if ( B2R_SettingsHolder.show(B2R_Menu.MAIN_MENU)) {
                if (once) {
                    once = false;
                    showDialog(0);
                }
            }
        } catch (Exception ex) {
            Log.d("Main", "onResume Failure", ex);
        }

        overridePendingTransition(0,0);

        super.onResume();
    }

    @Override
    protected Dialog onCreateDialog(int i) {
        switch (i) {
        // Show main menu guide prompt
        case 0: return new B2R_PopupDialog(this, "file:///android_asset/html/b2r_main.html", B2R_Menu.MAIN_MENU);
        }
        return null;
    }

    // Handle 
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        Intent userIntent = null;
        switch (item.getItemId()) {
        // Show about page.
        case R.id.buttonMenuAbout:
            userIntent = new Intent(this, B2R_WebViewActivity.class);
            userIntent.putExtra("FilePath", "file:///android_asset/html/about.html");
            startActivity( userIntent);
            return true;
        // Create Settings (preferences) menu.
        case R.id.buttonMenuSettings:
            userIntent = new Intent(this, Settings.class);
            startActivity( userIntent);
            return true;
        }
        return super.onOptionsItemSelected( item);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}