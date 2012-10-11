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
    
import java.util.List;

import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * Main Breathe activity 
 * @author jon.hulthen
 */
public class B2R_InhaleActivity extends BaseNavigationActivity implements ViewFactory {
    static private final int DEFAULT_CYCLES = 16;
    static private final int DEFAULT_BREATHE_LENGTH = 70;
    static private final String SHORTEN_INHALE = "Shorten Inhale";
    static private final String SHORTEN_EXHALE = "Shorten Exhale";
    static private final String LENGTHEN_INHALE = "Lengthen Inhale";
    static private final String LENGTHEN_EXHALE = "Lengthen Exhale";

    private Thread motifThread;
    private Thread voiceinstructionsThread;
    private MyThread myThread;
    private CountDownTimer timer;
    
    private Handler inhaleExhaleHandler ;
    private Handler motifHandle;
    private Handler talkInhaleHandle;
    private Handler talkExhaleHandle;
    private Handler myHandle;
    private Handler pressedDownHandler;
    private Handler pressedUpHandler;
    
    private Runnable myPressedDownRunnable;
    private Runnable myPressedUpRunnable;
    
    private ImageView animationBlue;
    private ImageView animationBlueHead;
    private ImageView animationWhite;
    private ImageView bottomOfMetronome;
    private ImageView backgroundPicture;
    
    private Button mDecreaseButton;             
    private ToggleButton mPauseButton;        
    private Button mIncreaseButton;    
    private Button mRestartButton;
    private Button mProceedButton;
    private TextView mHeader;
    private TextView mBreathLength;
    private TextView mCycles;

    private Animation scaleInAnimation;
    private Animation scaleOutAnimation;
    private volatile Animation moveUp;
    private volatile Animation moveDown;
   
    private Animation alphaIn;
    private Animation alphaOut;
    private AnimationSet inhaleSet;
    private AnimationSet exhaleSet;
    private Animation scale_breathing_in;
    private Animation scale_breathing_out;
    private Animation header_out;
    
    private int numberOfCycles = DEFAULT_CYCLES;
    private volatile int looper = 0;
    private volatile int myProgress_in = 0;
    private volatile int myProgress_out = 0;
    private volatile int maxValue_in = DEFAULT_BREATHE_LENGTH;
    private volatile int maxValue_out = DEFAULT_BREATHE_LENGTH;
    private volatile int pressed_down_counter = 0;
    private volatile int pressed_up_counter = 0;
    private static volatile int resume = 0;
    
    private volatile boolean isInhale = true;
    private volatile boolean stopMe = false;
    private volatile boolean timeChanged = false;
    private volatile boolean pause = false;
    private volatile boolean wasPaused = false; 
    private volatile boolean pressed_down = false;
    private volatile boolean pressed_up = false;
    private volatile boolean talkFlag = false;
    private volatile boolean backButtonPressed = false;
    
    public View makeView() {
        TextView t = new TextView(this);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(36);
        return t;
    }

    /**
     * Left (Back) button pressed.
     * Stop everything and exit.
     */
    @Override
    protected void onLeftNavigationButtonPressed() {
        doBack();
        stopEverything();
        B2R_Utility.clearRecycleables();
        backButtonPressed = true;
        super.onBackPressed();
    }

    @Override 
    public void onBackPressed() {
        doBack();
        stopEverything();
        B2R_Utility.clearRecycleables();
        backButtonPressed = true;
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(final Configuration conf) {
    	super.onConfigurationChanged(conf);

    	Log.d("OnConfigurationChanged", "o = " + conf.orientation);
    	
    	// This is necessary to enable proper updating for aspects due to
    	// the Andriod threading model.
    	// If this logic is removed switching from portrait to landscape and vice versa
    	// will cause several multiples of the animation to co-exist since Android is
    	// not thread safe by default.
    	Boolean b = B2R_SettingsHolder.getBoolean(B2R_Setting.METRONOME);
    	if (b != null && b == true && !pause) {
    		handleRealTimeOrientationChange(conf.orientation);
    	} else {
    		animationBlue.setVisibility(View.GONE);
            animationBlueHead.setVisibility(View.GONE);
            bottomOfMetronome.setVisibility(View.GONE);
            animationWhite.setVisibility(View.GONE);
    	}
    }

    @Override
    public void onPause() {
        if (pause) {
        	doPause();
        }
        
        Log.d("B2R_InhaleActivity", "onPause: " + pause);
        super.onPause();
    }
    
    @Override
    public void onResume() {
        if (!pause) {
        	if (resume > 0) {
        		looper--;
        		doResume();
        	}
        }
        
        Log.d("B2R_InhaleActivity", "onResume: " + pause);
        resume++;
        super.onResume();
    }
    
    @Override
    public void onDestroy() {
    	try {
    		Log.d("B2R_InhaleActivity", "onDestroy: " + android.os.Debug.getNativeHeapAllocatedSize() + "   " + B2R_Utility.getRecycleableCount());
    	} catch (Exception ex) {
    	}
        
        if (this.backgroundPicture != null) {
            try {
                if (B2R_SettingsHolder.isSet(B2R_Setting.MOTIF_SELECTED)) {
                    backgroundPicture.clearAnimation();
                }
            } catch (Exception ex) {
            	Log.d("B2R_InhaleActivity", "Exception", ex);
            }
            backgroundPicture = null;
        }

        B2R_Utility.clear(B2R_Utility.BitmapOrder.FIRST_BREATHE);
        B2R_Utility.clear(B2R_Utility.BitmapOrder.LAST_BREATHE);

        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("B2R_InhaleActivity", "onStop: " + pause);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("B2R_InhaleActivity", "onStart: " + pause);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean preventScreenTimeout = B2R_SettingsHolder.getBoolean(B2R_Setting.PREVENT_SCREEN_TIMEOUT);
        if (preventScreenTimeout)
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.b2r_inhale);
        this.setTitle( R.string.breatheLabel);

        this.setLeftNavigationButtonVisibility(View.VISIBLE);

        resume = 0;
       
        header_out = AnimationUtils.loadAnimation(this, R.anim.disappear);
        scale_breathing_in = AnimationUtils.loadAnimation( this, R.anim.b2r_scale_inhale_exhale );
        scale_breathing_out = AnimationUtils.loadAnimation( this, R.anim.b2r_scale_exhale_inhale );
        inhaleSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.b2r_animation_scale);
        exhaleSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.b2r_animation_scale_out);
        moveUp = AnimationUtils.loadAnimation(this, R.anim.b2r_move_up);
        moveDown = AnimationUtils.loadAnimation(this, R.anim.b2r_move_down);
        mDecreaseButton = (Button)findViewById(R.id.decreaseExhaleButtonB2R);
        mPauseButton = (ToggleButton)findViewById(R.id.pauseExhaleButtonB2R);    
        mIncreaseButton = (Button)findViewById(R.id.increaseExhaleButtonB2R);  
        mRestartButton = (Button)findViewById(R.id.buttonRestart);
        mProceedButton = (Button)findViewById(R.id.buttonProceed);    
        mHeader = (TextView)findViewById(R.id.textOneAnimationB2R);
        mBreathLength = (TextView)findViewById(R.id.textBreathLength);
        mCycles = (TextView)findViewById(R.id.textCyclesAnimationB2R);
        backgroundPicture = (ImageView)findViewById(R.id.imageviewInhale);
        backgroundPicture.setDrawingCacheEnabled(true);
        animationBlue = (ImageView)findViewById(R.id.imageBlueInhaleExhaleForB2R);
        animationBlueHead = (ImageView)findViewById(R.id.imageBlueHeadInhaleExhaleForB2R);
        animationWhite = (ImageView)findViewById(R.id.imageWhiteForB2R);
        bottomOfMetronome = (ImageView)findViewById(R.id.imageBlueBottomInhaleExhaleForB2R);
        animationBlue.setDrawingCacheEnabled(true);
        animationBlueHead.setDrawingCacheEnabled(true);
        
        clearIt(B2R_Utility.BitmapOrder.FIRST);
        clearIt(B2R_Utility.BitmapOrder.LAST);
        
        initializations();
        callbacks();
        
        backButtonPressed = false;
    }

    @Override
    public void onWindowFocusChanged (final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (resume == 1 && hasFocus && !B2R_Utility.hasOrder(B2R_Utility.BitmapOrder.FIRST_BREATHE)) {
        	initEverything();
        	startEverything();

        	Log.d("B2R_InhaleActivity", "onWindowFocusChanged(hasFocus)");
        }
    }
    
    public void startEverything() {
        doInhaleExhale();
    }
    
    protected void doInhaleExhale() {
    	if (backButtonPressed) return;
    	
        if (pause) {
            isInhale = true;
            return;
        }

        if ( isInhale )
            looper++;

        if (isDone()) {
            nextActivity( false);

        } else {
            this.setMaxValue();
            
            Runnable mainRunnable = new Runnable() {
                public void run() {
                	inhaleExhaleHandler.sendEmptyMessage(0);
                }
            };
            new Thread(mainRunnable).start();
        }
    }

    protected void nextActivity( boolean confirmed) {
    	resume++;
    	if (backButtonPressed) return;
    	backButtonPressed = true;
    	
        Intent userIntent = null;

        // Save the currently used inhale/exhale lengths at end of breathing,
        // in case they changed the values (shorten/lengthen).
        // Rationalizing that it is OK to save them , even if the user did not
        // shorten/lengthen (and they were never explicitly set by the user)
        // presuming they liked the current value if they got all the way
        // through the breathing cycles.  If they exited out before the cycles
        // finished, then the settings (lengths) would not get saved.
        B2R_SettingsHolder.put(B2R_Setting.INHALE_LENGTH, "" + maxValue_in);
        B2R_SettingsHolder.put(B2R_Setting.EXHALE_LENGTH, "" + maxValue_out); 

        String s_b = B2R_SettingsHolder.get( B2R_Setting.RELAXED_STRESSED_BEFORE);
        if (confirmed) {
            if (!B2R_SettingsHolder.getBoolean( B2R_Setting.TRACK_STRESS_SKIP)
                    && B2R_SettingsHolder.getBoolean( B2R_Setting.TRACK_STRESS)
                    && !B2R_Utility.isTodayStressRated( this.getHelper())
                    && s_b != null) {
                userIntent = new Intent( this, B2R_RateStressAfterActivity.class);
            }
        } else {
            userIntent = new Intent( this, B2R_EndBreathingActivity.class);
        }

        stopEverything();

        final Intent intent = userIntent;
        Runnable myAction = new Runnable() {
            public void run() {
                if (intent != null) {
                	B2R_InhaleActivity.this.startActivity( intent);
                }
                B2R_InhaleActivity.this.finish();
            }
        };

        B2R_InhaleActivity.this.runOnUiThread(myAction);
    }

    /* *************************************** private methods ******************************************** */
    private void initEverything() {
        initVoiceInstructions();
        setupCounters();
        setupBackgroundAnimation();
        setupMusicPlaying();
    }
    
    private void stopAlmostEverything() {
    	stopMe = true;
        if (timer != null) timer.cancel();

        if (motifThread != null) motifThread.interrupt();
        if (voiceinstructionsThread != null) voiceinstructionsThread.interrupt();
        
        if (animationBlue != null) {
            animationBlue.setVisibility(View.GONE);
            animationBlue.clearAnimation();
        }
        if (animationBlueHead != null) {
            animationBlueHead.setVisibility(View.GONE);
            animationBlueHead.clearAnimation();
        }
        if (bottomOfMetronome != null) {
            bottomOfMetronome.setVisibility(View.GONE);
        }
        if (animationWhite != null) {
            animationWhite.setVisibility(View.GONE);
        }
        if (backgroundPicture != null) {
            if (B2R_SettingsHolder.isSet(B2R_Setting.MOTIF_SELECTED)) {
                backgroundPicture.clearAnimation();
            }
        }
        if (scaleInAnimation != null) {
            scaleInAnimation.reset();
        }
        if (scaleOutAnimation != null) {
            scaleOutAnimation.reset();
        }
        if (scale_breathing_in != null) {
            scale_breathing_in.reset();
        }
        if (scale_breathing_out != null) {
            scale_breathing_out.reset();
        }
        if (moveUp != null) {
            moveUp.reset();
        }
        if (moveDown != null) {
            moveDown.reset();
        }
        if (inhaleSet != null) {
        	inhaleSet.reset();
        }
        if (exhaleSet != null) {

        	exhaleSet.reset();
        }
        if (alphaIn != null) {
        	alphaIn.reset();
        }
        if (alphaOut != null) {
        	alphaOut.reset();
        }
        try {
        	if (myThread != null) {
        		myThread.stopIt();
        	}
        } catch (Exception ex) {
        	Log.d("B2R_InhaleActivity", "Exception", ex);
        }
        
        myThread = null;
    }
    
    private void stopEverything() {
    	stopAlmostEverything();
    	
        B2R_Utility.stopShortTalkInhale();
        B2R_Utility.stopShortTalkExhale();

        Boolean b = B2R_SettingsHolder.getBoolean(B2R_Setting.PLAY_MUSIC);
        if (b != null && b == true) {
            String music = B2R_SettingsHolder.get(B2R_Setting.BACKGROUND_MUSIC_SELECTED);
            if (music != null && music.length() > 0) {
                B2R_Utility.stopPlayMusic();
            }
        }
    }

    private void doResume() {
        // Resume selected.
        pause = false;
        wasPaused = true;
        stopMe = false;
        isInhale = true;

        Boolean b = B2R_SettingsHolder.getBoolean(B2R_Setting.METRONOME);
        if (b != null && b == true) {
            if (animationBlue != null) {
                animationBlue.setVisibility(View.VISIBLE);
                animationBlueHead.setVisibility(View.VISIBLE);
                bottomOfMetronome.setVisibility(View.VISIBLE);

            }
            if (animationWhite != null) {
                animationWhite.setVisibility(View.VISIBLE);
            }
        }

        b = B2R_SettingsHolder.getBoolean(B2R_Setting.VISUAL_PROMPTS);
        if (b != null && b == true) {
            mBreathLength.setVisibility( View.VISIBLE);
            mCycles.setVisibility( View.VISIBLE);
        }

        restoreButtons();

        Log.d("B2R_InhaleActivity", "doResume");
        b = B2R_SettingsHolder.getBoolean(B2R_Setting.PLAY_MUSIC);
        if (b != null && b == true) {
            B2R_Utility.resumePlayMusic();
        }

        b = B2R_SettingsHolder.getBoolean(B2R_Setting.INSTRUCTIONS_PROMPTS);
        if (b != null && b == true) {
            B2R_Utility.resumeShortTalkInhale();
            B2R_Utility.resumeShortTalkExhale();
        }

        // Continue
        doInhaleExhale();
    }

    private void restoreButtons()
    {
        mIncreaseButton.setVisibility( View.VISIBLE);
        mDecreaseButton.setVisibility( View.VISIBLE);
        mRestartButton.setVisibility( View.GONE);
        mProceedButton.setVisibility( View.GONE);
    }

    private void doPause()
    {
        // Pause selected.
        pause = true;
        
        stopAlmostEverything();
        
        // pause music
        Log.d("B2R_InhaleActivity", "doPause");
        if (B2R_SettingsHolder.getBoolean( B2R_Setting.PLAY_MUSIC)) {
            B2R_Utility.pausePlayMusic();
        }

        // pause short talk
        if (B2R_SettingsHolder.getBoolean(B2R_Setting.INSTRUCTIONS_PROMPTS)) {
            B2R_Utility.pauseShortTalkInhale();
            B2R_Utility.pauseShortTalkExhale();
        }

        mIncreaseButton.setVisibility( View.GONE);
        mDecreaseButton.setVisibility( View.GONE);
        mRestartButton.setVisibility( View.VISIBLE);
        mProceedButton.setVisibility( View.VISIBLE);
    }

    private void setupBackgroundAnimation() {
    	List<Animation> inList = inhaleSet.getAnimations();
    	List<Animation> exList = exhaleSet.getAnimations();

    	for (Animation animal : inList) {
    		if (animal instanceof ScaleAnimation) {
    			scaleInAnimation = (ScaleAnimation) animal;
    			scaleInAnimation.setDuration(maxValue_in*100);
    		} else if (animal instanceof AlphaAnimation) {
    			alphaIn = (AlphaAnimation) animal;
    			alphaIn.setDuration(500);
    		}
    	}
    	for (Animation animal : exList) {
    		if (animal instanceof ScaleAnimation) {
    			scaleOutAnimation = (ScaleAnimation) animal;
    			scaleOutAnimation.setDuration(maxValue_out*100);
    		} else if (animal instanceof AlphaAnimation) {
    			alphaOut = (AlphaAnimation) animal;
    			alphaOut.setDuration(500);
    		}
    	}
    }

    private void setupMusicPlaying() {
        if (B2R_SettingsHolder.getBoolean(B2R_Setting.PLAY_MUSIC)) {
            String music = B2R_SettingsHolder.get(B2R_Setting.BACKGROUND_MUSIC_SELECTED);
            if (music != null && music.length() > 0) {
                B2R_Utility.playMusic(this);
            }
        }
    }

    private void setupCounters() {
        String count = B2R_SettingsHolder.get(B2R_Setting.CYCLE);
        if (count != null) {
            try {
                numberOfCycles = Integer.parseInt(count);
            } catch (Exception ex) {
                numberOfCycles = DEFAULT_CYCLES;
                Log.d("EXCEPTION", "count = " + count, ex);
            }
        } else {
            numberOfCycles = DEFAULT_CYCLES;
        }

        Log.d("B2R_InhaleActivity", "Number of Cycles = " + numberOfCycles);

        if (B2R_SettingsHolder.getBoolean(B2R_Setting.VISUAL_PROMPTS)) {
            mBreathLength.setVisibility(View.VISIBLE);
            mCycles.setVisibility(View.VISIBLE);
        } else {
            mBreathLength.setVisibility(View.GONE);
            mCycles.setVisibility(View.GONE);
        }
    }

    private void setupDelayedCylinderAnimation() {  
    	if (!pause) {
    		animationBlue.setVisibility(ImageView.VISIBLE);
    		animationBlueHead.setVisibility(ImageView.VISIBLE);
    		bottomOfMetronome.setVisibility(ImageView.VISIBLE);
    		animationWhite.setVisibility(ImageView.VISIBLE);
    	}
    }

    private void setCycleValue() {
        if (looper <= numberOfCycles) {
            if ( looper > numberOfCycles/2 )
            {
                int i = this.numberOfCycles+1-looper;
                if (i == 1) {
                    mCycles.setText(i + " cycle remaining");
                } else {
                    mCycles.setText(i + " cycles remaining");
                }
            }
            else
            {
                mCycles.setText("Cycle " + looper + " of " + this.numberOfCycles);
            }
        }
    }

    private void setMaxValue() {
        myProgress_in = 0;
        myProgress_out = 0;

        mHeader.clearAnimation();
       
        if (looper <= numberOfCycles) {
            if (isInhale) {
                mHeader.setText("Inhale");
                header_out.setDuration(maxValue_in*100);
                float f = (float)maxValue_in;
                mBreathLength.setText((f/10.0F) + " seconds inhale");
            } else {
                mHeader.setText("Exhale");
                header_out.setDuration(maxValue_out*100);
                float f = (float)maxValue_out;
                mBreathLength.setText((f/10.0F) + " seconds exhale");
            }

            mHeader.startAnimation(header_out);
        }
    }

    private boolean isDone() {
        if (stopMe) return true;
        if (pause) return false;

        if (looper > numberOfCycles) {
            animationBlue.setVisibility(View.GONE);
            animationBlueHead.setVisibility(View.GONE);
            bottomOfMetronome.setVisibility(View.GONE);

            return true;
        } else {
            setCycleValue();
            return false;
        }
    }

    private void startAnimation() {
        int duration = 0;
        Animation breathing = null;
        Animation direction = null;
        Runnable voiceInstructions = null;
        Runnable motifs = null;
        voiceinstructionsThread = null;
        motifThread = null;
        
        if (!pause) {
        	B2R_VoiceInstructions.pause();
        	B2R_VoiceInstructions.resume();
        }
        
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (stopMe) return;
        if (looper <= 0) return;

        if (timeChanged) {
            timeChanged = false;
        }

        if (myThread != null) {
            myThread.stopIt();
            myThread = null;
        }

        if (B2R_SettingsHolder.isSet(B2R_Setting.MOTIF_SELECTED)) {
            motifs = new Runnable() {
                public void run() {
                    motifHandle.sendEmptyMessage(0);
                }
            };
            motifThread = new Thread(motifs);
        }

        if (isInhale) {
            mDecreaseButton.setText(SHORTEN_INHALE);
            mIncreaseButton.setText(LENGTHEN_INHALE);
            duration = maxValue_in*100;
            breathing = scale_breathing_in;
            direction = moveUp;

            if (B2R_SettingsHolder.getBoolean(B2R_Setting.INSTRUCTIONS_PROMPTS)) {
                voiceInstructions = new Runnable() {
                    public void run() {
                        talkInhaleHandle.sendEmptyMessage(0);
                    }
                };
                voiceinstructionsThread = new Thread(voiceInstructions);
            }
        } else {
            mDecreaseButton.setText(SHORTEN_EXHALE);
            mIncreaseButton.setText(LENGTHEN_EXHALE);
            duration = maxValue_out*100;
            breathing = scale_breathing_out;
            direction = moveDown;

            if (B2R_SettingsHolder.getBoolean(B2R_Setting.INSTRUCTIONS_PROMPTS)) {
                voiceInstructions = new Runnable() {
                    public void run() {
                    	talkFlag = true;
                        talkExhaleHandle.sendEmptyMessage(0);
                    }
                };
                voiceinstructionsThread = new Thread(voiceInstructions);
            }
        }

        if ( B2R_SettingsHolder.getBoolean(B2R_Setting.METRONOME)) {
            breathing.setDuration(duration);
            direction.setDuration(duration);

            myThread = new MyThread();
        } else {
            breathing = null;
            direction = null;
            final boolean type = (isInhale? false:true);
            timer = new CountDownTimer(duration , 100) {
                public void onTick(long millisUntilFinished) {
                    if (stopMe) this.cancel();
                    else doProgress();
                }

                public void onFinish() {
                    isInhale = type;
                    if (!isDone()) {    
                        doInhaleExhale();
                    }
                }
            };
        }

        scheduleTheAnimation(breathing);
    }

    private void scheduleTheAnimation(final Animation breathing) {
    	 if (motifThread != null && !motifThread.isAlive()) {
         	motifThread.start();
         }
    	 
        if (breathing != null) {
        	new Thread(new Runnable() {
        	    public void run() {
        	    	animationBlue.post(new Runnable() {
        	        public void run() {
        	        	if (!pause) {
        	        		animationBlue.startAnimation(breathing);
        	        		setupDelayedCylinderAnimation();
        	        	}
        	        }
        	      });
        	    }
        	 }).start();
        }

        if (timer != null) {
        	timer.start();
        }
        if (myThread != null && !myThread.isAlive()) {
        	myThread.start();
        }
        
        try {
        	if (voiceinstructionsThread != null && !voiceinstructionsThread.isAlive() && !talkFlag) {
        		voiceinstructionsThread.start();
        	}
        } catch (Exception ex) {
        	Log.d("B2R_InhaleActivity", "Exception", ex);
        }
    }

    private boolean isNotDone() {
        if (isInhale) return (myProgress_in < maxValue_in); else return (myProgress_out < maxValue_out);
    }
    private void doProgress() {
        if (isInhale) myProgress_in++; else myProgress_out++;
    }

    private float getMillisecondsRemaining() {
        if (isInhale) {
            float f1 = (float)maxValue_in;
            float f2 = (float)myProgress_in;

            return (f1-f2)*100F; 
        } else {
            float f1 = (float)maxValue_out;
            float f2 = (float)myProgress_out;

            return (f1-f2)*100F; 
        }
    }

    private void initVoiceInstructions() {
    	try {
    		if (B2R_SettingsHolder.getBoolean(B2R_Setting.INSTRUCTIONS_PROMPTS)) {
    			if ( ! B2R_Utility.isShortTalkInhaleActive()) {
    				B2R_Utility.talkShortTalkInhale(this, 1);
    			}
    			if ( ! B2R_Utility.isShortTalkExhaleActive()) {
    				B2R_Utility.talkShortTalkExhale(this, 1);
    			}
    		}
    	} catch (Exception ex) {
    		Log.d("B2R_InhaleActivity", "Exception", ex);
    	}
    }

    private void talkInhale() {
        if (stopMe) return;

        B2R_VoiceInstructions.talkforInhale((int)getMillisecondsRemaining(), looper, maxValue_in, numberOfCycles);
    }

    private void talkExhale() {
        if (stopMe) return;

        B2R_VoiceInstructions.talkforExhale((int)getMillisecondsRemaining(), looper, maxValue_out, numberOfCycles);
    }

    private void doBack()
    {
        final Handler handleIt = new Handler(){
            public void handleMessage(Message msg) {
            	Boolean b = B2R_SettingsHolder.getBoolean(B2R_Setting.PLAY_MUSIC);
                if (b != null && b == true) {
                    String music = B2R_SettingsHolder.get(B2R_Setting.BACKGROUND_MUSIC_SELECTED);
                    if (music != null && music.length() > 0) {
                        B2R_Utility.stopPlayMusic();
                    }
                }
            }
        };
        Runnable runIt = new Runnable() {
            public void run() {
                handleIt.sendMessage(handleIt.obtainMessage());
            }
        };
        new Thread(runIt).start();
    }

    private void handleRealTimeOrientationChange(final int orient) {
    	Runnable configAction = new Runnable() {
			public void run() {
				try {
					Thread head = null;
					Thread body = null;
					if (orient == 1) {
						if (isInhale) {
							head = new Thread(new Runnable() {
								public void run() {
									animationBlueHead.post(new Runnable() {
										public void run() {
											animationBlueHead.setAnimation(moveUp);
											if (pause) {
												animationBlueHead.clearAnimation();
												animationBlueHead.setVisibility(View.GONE);
											}
										}
									});
								}
							});
							body = new Thread(new Runnable() {
								public void run() {
									animationBlue.post(new Runnable() {
										public void run() {
											animationBlue.setAnimation(scale_breathing_in);
											if (pause) {
												animationBlue.clearAnimation();
												animationBlue.setVisibility(View.GONE);
											}
										}
									});
								}
							});
						} else {
							head = new Thread(new Runnable() {
								public void run() {
									animationBlueHead.post(new Runnable() {
										public void run() {
											animationBlueHead.setAnimation(moveDown);
											if (pause) {
												animationBlueHead.clearAnimation();
												animationBlueHead.setVisibility(View.GONE);
											}
										}
									});
								}
							});
							body = new Thread(new Runnable() {
								public void run() {
									animationBlue.post(new Runnable() {
										public void run() {
											animationBlue.setAnimation(scale_breathing_out);
											if (pause) {
												animationBlue.clearAnimation();
												animationBlue.setVisibility(View.GONE);
											}
										}
									});
								}
							});
						}
					} else if (orient == 2) {
						if (isInhale) {  
							head = new Thread(new Runnable() {
								public void run() {
									animationBlueHead.post(new Runnable() {
										public void run() {
											animationBlueHead.setAnimation(moveUp);
											if (pause) {
												animationBlueHead.clearAnimation();
												animationBlueHead.setVisibility(View.GONE);
											}
										}
									});
								}
							});
							body = new Thread(new Runnable() {
								public void run() {
									animationBlue.post(new Runnable() {
										public void run() {
											animationBlue.setAnimation(scale_breathing_in);
											if (pause) {
												animationBlue.clearAnimation();
												animationBlue.setVisibility(View.GONE);
											}
										}
									});
								}
							});
						} else {
							head = new Thread(new Runnable() {
								public void run() {
									animationBlueHead.post(new Runnable() {
										public void run() {
											animationBlueHead.setAnimation(moveDown);
											if (pause) {
												animationBlueHead.clearAnimation();
												animationBlueHead.setVisibility(View.GONE);
											}
										}
									});
								}
							});
							body = new Thread(new Runnable() {
								public void run() {
									animationBlue.post(new Runnable() {
										public void run() {
											animationBlue.setAnimation(scale_breathing_out);
											if (pause) {
												animationBlue.clearAnimation();
												animationBlue.setVisibility(View.GONE);
											}
										}
									});
								}
							});
						}
					}
					body.start();
					head.start();
				} catch (Exception ex) {
					Log.d("B2R_InhaleActivity", "Exception", ex);
				}
			}
		};

		B2R_InhaleActivity.this.runOnUiThread(configAction);
    }
    
    private void setTheBackgroundTheSimpleWay() {
    	if (isDone()) return;
    	if (backgroundPicture == null) return;
    	if (scaleInAnimation == null) return;
    	if (scaleOutAnimation == null) return;

    	if (isInhale) {
    		inhaleSet.reset();
    		scaleInAnimation.reset();
    		if (wasPaused && resume > 1) {
    			scaleInAnimation.setDuration((maxValue_in-this.myProgress_in)*100);
    		} else {
    			clearIt(B2R_Utility.BitmapOrder.FIRST_BREATHE);
    			final BitmapDrawable bmd = B2R_Utility.getReducedBitmapDrawable(getResources(), B2R_Utility.BitmapOrder.FIRST_BREATHE);
    			new Thread(new Runnable() {
    				public void run() {
    					backgroundPicture.post(new Runnable() {
    						public void run() {
    							backgroundPicture.setImageDrawable(bmd);
    						}
    					});
    				}
    			}).start();

    			scaleInAnimation.setDuration(maxValue_in*100);
    		} 
    		if (alphaIn != null) {
    			alphaIn.reset();
    			alphaIn.setDuration(maxValue_in*10);
    		}
    		new Thread(new Runnable() {
    			public void run() {
    				backgroundPicture.post(new Runnable() {
    					public void run() {
    						backgroundPicture.startAnimation(inhaleSet);
    					}
    				});
    			}
    		}).start();
    	} else {
    		exhaleSet.reset();
    		scaleOutAnimation.reset();
    		if (wasPaused && resume > 1) {
    			scaleOutAnimation.setDuration((maxValue_out-this.myProgress_out)*100);
    		} else {
    			clearIt(B2R_Utility.BitmapOrder.LAST_BREATHE);
    			final BitmapDrawable bmd = B2R_Utility.getReducedBitmapDrawable(getResources(), B2R_Utility.BitmapOrder.LAST_BREATHE);
    			new Thread(new Runnable() {
    				public void run() {
    					backgroundPicture.post(new Runnable() {
    						public void run() {
    							backgroundPicture.setImageDrawable(bmd);
    						}
    					});
    				}
    			}).start();
    			scaleOutAnimation.setDuration(maxValue_out*100);
    		} 
    		if (alphaOut != null) {
    			alphaOut.reset();
    			alphaOut.setDuration(maxValue_out*10);
    		}
    		new Thread(new Runnable() {
    			public void run() {
    				backgroundPicture.post(new Runnable() {
    					public void run() {
    						backgroundPicture.startAnimation(exhaleSet);
    					}
    				});
    			}
    		}).start();
    	}
    	wasPaused = false;
    }

    private void clearIt(final B2R_Utility.BitmapOrder order) {
        Runnable myRunnable = new Runnable() {
            public void run() {
                B2R_Utility.clear(order);
            }
        };
        new Thread(myRunnable).start();
    }
    
    private void callbacks() {
    	header_out.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                mHeader.setText("");
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationStart(Animation animation) {
            }
        });
    	
    	scale_breathing_in.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {

                isInhale = false;     

                if (!isDone()) {    
                    doInhaleExhale();
                }
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationStart(Animation animation) {
            	animationBlueHead.startAnimation(moveUp);
            }
        });

        scale_breathing_out.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {   

                isInhale = true;      

                if (!isDone()) {
                    doInhaleExhale();
                }
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationStart(Animation animation) {
            	animationBlueHead.startAnimation(moveDown);
            }
        });

        mDecreaseButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(v.equals(mDecreaseButton)) {
                    if ( event.getAction() == MotionEvent.ACTION_DOWN ) { 
                        pressed_down = true;
                        mDecreaseButton.setPressed(true);
                        Thread myThread = new Thread(myPressedDownRunnable);
                        myThread.start();
                        return true;                 
                    } else if ( event.getAction() == MotionEvent.ACTION_UP ) { 
                        pressed_down = false;
                        timeChanged = true;
                        mDecreaseButton.setPressed(false);
                        return true;   
                    }
                    return false; 
                }
                return false;
            }
        });
        
        mPauseButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(v.equals(mPauseButton)) {
                    String s = "" + mPauseButton.getText();
                    String s1 = "" + mPauseButton.getTextOff();

                    // Resume selected.
                    if (s.equals(s1)) {
                        looper--;
                        doResume();
                    } else {
                        doPause();
                    }
                }
            }
        });

        mIncreaseButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(v.equals(mIncreaseButton)) {
                    if ( event.getAction() == MotionEvent.ACTION_DOWN ) { 
                        pressed_up = true;
                        mIncreaseButton.setPressed(true);
                        Thread myThread = new Thread(myPressedUpRunnable);
                        myThread.start();
                        return true;                 
                    } else if ( event.getAction() == MotionEvent.ACTION_UP ) { 
                        pressed_up = false;
                        timeChanged = true;
                        mIncreaseButton.setPressed(false);
                        return true;   
                    }
                    return false; 
                }
                return false;
            }
        });
        
        mRestartButton.setOnClickListener(new OnClickListener() {

            // Restart Breathing
            @Override
            public void onClick( View v) {
                stopMe = false;
                pause = false;
                looper = 0;
                isInhale = true;
                mPauseButton.toggle();
                resume++;
                restoreButtons();
                startEverything();
            }
        });
        
        mProceedButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick( View v)
            {
                nextActivity( true);
            }
        });
    }
    
    private void initializations() {
    	// Use inhale/exhale length preferences, if defined.  Otherwise use
        // a system default, but do not save that system default into the
        // Preferences since it would mask the fact that the user never set/
        // confirmed the lengths.
        try {
            String s = B2R_SettingsHolder.get(B2R_Setting.INHALE_LENGTH);
            if (s != null) {
                maxValue_in  = Integer.parseInt(s);
            } else {
                maxValue_in  = DEFAULT_BREATHE_LENGTH;
                Log.d("INHALE","INVALUE NULL");
            }
        } catch (Exception ex) {
            maxValue_in  = DEFAULT_BREATHE_LENGTH;
            Log.d("INHALE","INVALUE NULL", ex);
        }
        try {
            String s = B2R_SettingsHolder.get(B2R_Setting.EXHALE_LENGTH);
            if (s != null) {
                maxValue_out  = Integer.parseInt(s);
            } else {
                maxValue_out = DEFAULT_BREATHE_LENGTH;
                Log.d("EXHALE","OUTVALUE NULL");
            }
        } catch (Exception ex) {
            maxValue_out = DEFAULT_BREATHE_LENGTH;
            Log.d("EXHALE","OUTVALUE NULL", ex);
        }
        
        inhaleExhaleHandler = new Handler() {           
            public void  handleMessage(Message msg) {       
                //update your view from here only.   
            	if (stopMe) return;
            	
            	startAnimation();
            }      
        };
        
        motifHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                setTheBackgroundTheSimpleWay();
            }
        };
        
        talkInhaleHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                talkInhale();
            }
        };
        
        talkExhaleHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                talkExhale();
                talkFlag = false;
            }
        };
        
        myHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (stopMe) return;
                doProgress();
            }
        };
        
        pressedDownHandler = new Handler() {           
            public void  handleMessage(Message msg) {                
                //update your view from here only.   
                if (isInhale) {
                    maxValue_in--;
                    // Min 2.5 seconds
                    if (maxValue_in < 25) {
                        maxValue_in = 25;
                    }
                    float f = (float)maxValue_in;
                    mBreathLength.setText((f/10.0F) + " seconds inhale");
                } else {
                    maxValue_out--;
                    if (maxValue_out < 25) {
                        maxValue_out = 25;
                    }
                    float f = (float)maxValue_out;
                    mBreathLength.setText((f/10.0F) + " seconds exhale");
                }
            }      
        };
        
        pressedUpHandler = new Handler() {           
            public void  handleMessage(Message msg) {                
                //update your view from here only.   
                if (isInhale) {
                    maxValue_in++;
                    float f = (float)maxValue_in;
                    mBreathLength.setText((f/10.0F) + " seconds inhale");
                } else {
                    maxValue_out++;
                    float f = (float)maxValue_out;
                    mBreathLength.setText((f/10.0F) + " seconds exhale");
                }
            }      
        };
        
        myPressedDownRunnable = new Runnable() {
            @Override
            public void run() {
                while (pressed_down) {
                    pressed_down_counter++;
                    try {
                        pressedDownHandler.sendEmptyMessage(0);
                        Thread.sleep(100);
                    } catch (Exception e) {
                        Log.e("BACKGROUND_PROC", e.getMessage());
                    }
                }
            }
        };

        myPressedUpRunnable = new Runnable() {
            @Override
            public void run() {
                while (pressed_up) {
                    pressed_up_counter++;
                    try {
                        pressedUpHandler.sendEmptyMessage(0);
                        Thread.sleep(100);
                    } catch (Exception e) {
                        Log.e("BACKGROUND_PROC", e.getMessage());
                    }
                }
            }
        };
    }
    
    private class MyThread extends Thread {
        private boolean stop = false;
        public void stopIt() {
            stop = true;
        }
        public void run() {
            while (isNotDone()){
                if (stopMe) return;

                if (stop) return;

                try{

                    if (!pause) {
                        Thread.sleep(100);
                        if (myHandle != null) {
                            myHandle.sendMessage(myHandle.obtainMessage());
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                catch(Throwable t){
                    Log.d("EXCEPTION", "RUN", t);
                }
            }
        }
    }
}
