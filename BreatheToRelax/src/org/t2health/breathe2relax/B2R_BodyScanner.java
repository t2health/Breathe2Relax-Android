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

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * B2R_BodyScanner
 * 
 * This class is utilizing the Observer Pattern to handle zooming and panning.
 * Animation is handled via scrolling. Android's existing Animation classes
 * were not used since they cut of the images.
 * 
 * @author jon.hulthen
 *
 */
public class B2R_BodyScanner extends BaseNavigationActivity implements OnClickListener {
    /* Constants */
    private static final int LOWLIGHT_COLOR = 0xff808080;
    private static final int HIGHLIGHT_COLOR = 0xffffffff;
    private static final int GLOW_TRANSPARENT = 0;
    private static final int GLOW_FULLY = 255;
    private static final char BACKGROUND_COLOR =0x00000000;
    private static final int BODYPARTSCARDINALITY = 10;
    private static final int TIMETOSLEEPAFTERTOUCHANDDRAG = 8000; // 8 seconds
    private static final int SIMULATIONCYCLEHALFPERIOD = 3000; // 60 seconds each way
    private static final int SIMULATIONVELOCITY = 20; 
    private static final int MAX_OVERLAYS = 3;
    private static final String token = "DELAY";
    
    /* Threads and Handlers */
    private DecorationThread decorator = null;
    private Handler moveHandle;
    private Handler setHandle;
    private Handler fixitHandle;
    private Handler glitterHandle;
    
    /* Animation Option */
    private ImageView glitterLine;
    private AnimationDrawable animationGlowLine;
    private volatile boolean buttonPressed = false;
    private volatile boolean panPressed = false;
    private volatile boolean underTest = false;
    private BODY_PARTS mPreviouslySelectedBodypart = BODY_PARTS.NONE;
    private BODY_PARTS mCurrentlySelectedBodypart = BODY_PARTS.NONE;
    
    /* From XML Definition */
    private Button[]    mButtons = new Button[BODYPARTSCARDINALITY];
    private ImageView[] mGlows = new ImageView[BODYPARTSCARDINALITY];
    private TextView[]  mTexts = new TextView[BODYPARTSCARDINALITY]; 
    private ViewGroup mTextLayout;
    private WebView mTextWebView;
    private PortViewOverlay[] overlay = new PortViewOverlay[3];
    private MaskView maskview;
    private BodyView bodyview;
    private ZoomState mZoomState;
    
    /* Embedded */
    public enum ControlType {
            PAN, ZOOM
    }
    private volatile ControlType mControlType = ControlType.PAN;
    private SimpleZoomListener mZoomListener;
//    private static final int MENU_ID_ZOOM = 0;    // for testing only
//    private static final int MENU_ID_PAN = 1;    // for testing only
//    private static final int MENU_ID_RESET = 2;    // for testing only
    private float mX;                            // for testing only
    private float mY;

    @Override
    protected void onLeftNavigationButtonPressed() {
        stopAnimation();
        B2R_Utility.clearRecycleables();
        super.onLeftNavigationButtonPressed();
    }
    
    @Override 
    public void onBackPressed() {
        stopAnimation();
        B2R_Utility.clearRecycleables();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);

        mTextLayout.setVisibility(View.GONE);

        if(mTextWebView != null) {
            mTextWebView.setBackgroundColor(BACKGROUND_COLOR);
        }        
    }

    @Override
    public void onDestroy() {
    	try {
    		Log.d("BodyScanner", "onDestroy: " + android.os.Debug.getNativeHeapAllocatedSize());
    	} catch (Exception ex) {
    	}

        stopAnimation();
        
        mZoomState = null;

        for (int i = 0; i < overlay.length; i++) {
        	if (overlay[i] != null) {
        		unbindDrawables(overlay[i]);
        		overlay[i] = null;
        	}
        }
        overlay = null;
        
        if (maskview != null) {
    		unbindDrawables(maskview);
    	}
        maskview = null;
        
        if (bodyview != null) {
    		unbindDrawables(bodyview);
    	}
        bodyview = null;
        
        if (glitterLine != null) {
    		unbindDrawables(glitterLine);
    		glitterLine.setBackgroundDrawable(null);
    	}
        glitterLine = null;
        
        if (animationGlowLine != null) {
        	animationGlowLine.setCallback(null);
        }
        
        animationGlowLine = null;

        for (int i = 0; i < mButtons.length; i++) {
        	unbindDrawables(mButtons[i]);
        	mButtons[i] = null;
        }
        for (int i = 0; i < mGlows.length; i++) {
        	unbindDrawables(mGlows[i]);
        	mGlows[i] = null;
        }
        for (int i = 0; i < mTexts.length; i++) {
        	unbindDrawables(mTexts[i]);
        	mTexts[i] = null;
        }
        
        mButtons = null;
        mGlows = null;
        mTexts = null;
        
        if (mTextLayout != null) {
    		unbindDrawables(mTextLayout);
    	}
        mTextLayout = null;
        
        if (mTextWebView != null) {
    		unbindDrawables(mTextWebView);
    	}
        mTextWebView = null;
        
        System.gc();
        System.runFinalization();
        System.gc();

        super.onDestroy();
    }
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
    		Log.d("BodyScanner", "onCreate: " + android.os.Debug.getNativeHeapAllocatedSize());
    	} catch (Exception ex) {
    	}
    	
        setContentView(R.layout.b2r_body_scanner_layout);
        
        this.setLeftNavigationButtonVisibility(View.VISIBLE);
        this.setRightNavigationButtonVisibility(View.GONE);
        this.setTitle(R.string.buttonBodyScanner);
        
        try {
            setup();
        } catch (Exception ex) {
            Log.d("B2R_BodyScanner", "Exception", ex);
        }
        
        try {
        	animationSetup();
        } catch (Exception ex) {
            Log.d("B2R_BodyScanner", "Exception", ex);
        }
        
        underTest = false;
    }

    @Override
    public void onWindowFocusChanged (final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            clearButtonHighlights();
            handleSelection(BODY_PARTS.BODY);
            
            startDecorationWithDelay();
        }
    }
    
    @Override
    public void onClick(View v) {
        if(!v.equals(mTextLayout)) {
            buttonPressed = true;
            
            if(v != null) {
                Button button = (Button) v;
                clearButtonHighlights();
                for (BODY_PARTS bp : BODY_PARTS.values()) {
                    if (!BODY_PARTS.validBodypart(bp)) continue;
                    if (button.equals(mButtons[bp.getInt()])) {
                        mPreviouslySelectedBodypart = mCurrentlySelectedBodypart;
                        mCurrentlySelectedBodypart = bp;
                        showHTMLfromBodyPartString();
                        handleSelection(mCurrentlySelectedBodypart);
                        break;
                    }
                }
            }
        }
        else {
            mCurrentlySelectedBodypart = BODY_PARTS.NONE;
            mTextLayout.setVisibility(View.INVISIBLE);
            buttonPressed = false;
        }
    }
    
//  For testing only
//    @Override
//    public boolean onOptionsItemSelected( MenuItem item){
//        switch (item.getItemId()) {
//        case MENU_ID_ZOOM:
//            mControlType = ControlType.ZOOM;
//            underTest = true;
//            break;
//
//        case MENU_ID_PAN:
//            mControlType = ControlType.PAN;
//            underTest = true;
//            break;
//
//        case MENU_ID_RESET:
//            resetZoomState();
//            underTest = false;
//            break;
//        }
//
//        return super.onOptionsItemSelected( item);
//    }
    
    /** ****************************** Private Methods ******************************************* */
    private void resetZoomState() {
        int i = this.getResources().getConfiguration().orientation;
        if (i == 1) {
            mZoomState.setPanX(0.6330f);//0.6730
            mZoomState.setPanY(0.2504f);
            mZoomState.setZoom(1.65f);// 2.2266 
                mZoomState.notifyObservers();
        } else if (i == 2) {
            mZoomState.setPanX(0.5f);
            mZoomState.setPanY(0.05f);
            mZoomState.setZoom(3.35F); //4.25
                mZoomState.notifyObservers();
        } else {
            mZoomState.setPanX(0.5f);
            mZoomState.setPanY(0.5f);
            mZoomState.setZoom(1f);
            mZoomState.notifyObservers();
        }
    }

//  For testing only
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(Menu.NONE, MENU_ID_ZOOM, 0, R.string.menu_zoom);
//        menu.add(Menu.NONE, MENU_ID_PAN, 1, R.string.menu_pan);
//        menu.add(Menu.NONE, MENU_ID_RESET, 2, R.string.menu_reset);
//        
//        return super.onCreateOptionsMenu(menu);
//    }

    private void setup() {
        findViewById(R.id.buttonCloseThisDialog).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mCurrentlySelectedBodypart = BODY_PARTS.BODY;
                if (mTextLayout != null) {
                    mTextLayout.setVisibility(View.INVISIBLE);
                    mPreviouslySelectedBodypart = mCurrentlySelectedBodypart;
                    mCurrentlySelectedBodypart = BODY_PARTS.NONE;
                    clearButtonHighlights();
                    buttonPressed = false;
                }
            }
        });

        overlay[0] = (PortViewOverlay)findViewById(R.id.portviewoverlay);
        overlay[1] = (PortViewOverlay)findViewById(R.id.bodyfadingview);
        overlay[2] = (PortViewOverlay)findViewById(R.id.basefadingview);
        
        maskview = (MaskView)findViewById(R.id.maskview);
        maskview.setVisibility(View.GONE);
        bodyview = (BodyView)findViewById(R.id.bodyview);
        
        Bitmap btnGlow = B2R_Utility.getReducedBitmap(getResources(), B2R_Utility.BitmapOrder.GLOW, R.drawable.button_outerglow);
            
        for (BODY_PARTS bp : BODY_PARTS.values()) {
            int i = bp.getInt();
            if (BODY_PARTS.validBodypart(bp)) {
                mTexts[i] = (TextView)findViewById(bp.getTextInt());
            
                mButtons[i] = (Button)findViewById(bp.getButtonInt());
                mButtons[i].setBackgroundDrawable(B2R_Utility.getReducedBitmapDrawable(getResources(), bp.getButtonBitmapOrder(), bp.getButtonDrawableInt()));
                mButtons[i].setOnClickListener(this);
            
                mGlows[i] = (ImageView)findViewById(bp.getButtonglowInt());
                mGlows[i].setImageBitmap(btnGlow);
            }
        }
        
        glitterLine = (ImageView)findViewById(R.id.imageViewLaserLine);
        glitterLine.setVisibility(View.GONE);

        mTextLayout = (FrameLayout)findViewById(R.id.FrameLayoutFrameForWeb);
        mTextLayout.setVisibility(View.GONE);
        mTextWebView = (WebView)findViewById(R.id.webkitWebViewFramed);

        mTextLayout.setOnClickListener(this);
        mTextWebView.setBackgroundColor(BACKGROUND_COLOR);

        mCurrentlySelectedBodypart = BODY_PARTS.NONE;
        clearButtonHighlights();
        
        mZoomState = new ZoomState();
        maskview.setZoomState(mZoomState);
        bodyview.setZoomState(mZoomState);
        mZoomListener = new SimpleZoomListener();
        
        for (int i =0; i < overlay.length; i++) {
            overlay[i].setZoomState(mZoomState);
            overlay[i].setOnTouchListener(mZoomListener);
        }
        
        maskview.setOnTouchListener(mZoomListener);
        bodyview.setOnTouchListener(mZoomListener);
        
        resetZoomState();
        
        fixitHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (isInUse()) {
                        return;
                    } else {
                        if (maskview != null) maskview.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    Log.d("B2R_BodyScanner", "Exception", ex);
                }
            }
        };
        
        moveHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (isInUse()) {
                        return;
                    } else {
                        if (mZoomState != null) {
                            float f = msg.getData().getFloat(token, 0);

                            mZoomState.setPanY(f);
                            mZoomState.notifyObservers();
                        }
                    }
                } catch (Exception ex) {
                    Log.d("B2R_BodyScanner", "Exception", ex);
                }
            }
        };

        setHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (isInUse()) {
                        return;
                    } else {
                        if (mZoomState != null) {
                            float f = msg.getData().getFloat(token, 0);
                            handleOverlays(f);
                        }
                    }
                } catch (Exception ex) {
                    Log.d("B2R_BodyScanner", "Exception", ex);
                }
            }
        };
        
        glitterHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (glitterLine != null) {
                        glitterLine.setVisibility(View.VISIBLE);
                    }
                    if (maskview != null) {
                        maskview.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    Log.d("B2R_BodyScanner", "Exception", ex);
                }
            }
        };
    }

    private void showHTMLfromBodyPartString() {
        if(!mCurrentlySelectedBodypart.equals(mPreviouslySelectedBodypart)) {
            mTextLayout.setVisibility(View.VISIBLE);

            String urlString = "file:///android_asset/html/body/" + mCurrentlySelectedBodypart.getText() + ".html";
            mTextWebView.loadUrl(urlString);
        }
    }
    
    private void handleSelection(BODY_PARTS bodyPart) {
        if (mTexts == null || mGlows == null) return;
        
        setButtonHighlights(bodyPart);
        overlay[0].setImage(bodyPart);
        overlay[0].setAlpha(255);
        
        overlay[1].setImage(BODY_PARTS.NONE);
        overlay[1].setAlpha(0);
        
        overlay[2].setImage(BODY_PARTS.NONE);
        overlay[2].setAlpha(0);
        
        mZoomState.setPanY((float)bodyPart.getOrganCenter());
        mZoomState.notifyObservers();
    }

    private void setButtonHighlights(BODY_PARTS bodyPart) {
        clearButtonHighlights(mPreviouslySelectedBodypart);
        
        if (BODY_PARTS.validBodypart(bodyPart)) {
            mPreviouslySelectedBodypart = bodyPart;
            if (mGlows[bodyPart.getInt()] == null) return;
            try {
                mTexts[bodyPart.getInt()].setTextColor(HIGHLIGHT_COLOR);
                mTexts[bodyPart.getInt()].setTypeface(null,Typeface.BOLD);
                mGlows[bodyPart.getInt()].setAlpha(GLOW_FULLY);
            } catch (Exception ex) {
                Log.d("setButtonHighlights", "BP = " + bodyPart.name(), ex);
            }
        }
    }
    
    private void clearButtonHighlights(BODY_PARTS bodyPart) {
        if (BODY_PARTS.validBodypart(bodyPart)) {
            if (mGlows[bodyPart.getInt()] == null) return;
            try {
                mGlows[bodyPart.getInt()].setAlpha(GLOW_TRANSPARENT);
                mTexts[bodyPart.getInt()].setTextColor(LOWLIGHT_COLOR);
                mTexts[bodyPart.getInt()].setTypeface(null,Typeface.NORMAL);
            } catch (Exception ex) {
                Log.d("clearButtonHighlights", "BP = " + bodyPart.name(), ex);
            }
        }
    }
    
    private void clearButtonHighlights() {
        for (BODY_PARTS bp : BODY_PARTS.values()) {
            if (BODY_PARTS.validBodypart(bp)) {
                clearButtonHighlights(bp);
            }
        }
    }

    private void animationSetup() {
        try {
            animationGlowLine = new AnimationDrawable();

            animationGlowLine.addFrame(B2R_Utility.getReducedBitmapDrawable(getResources(), B2R_Utility.BitmapOrder.SCAN0, R.drawable.ibreathe_body_scan0), 33);
            animationGlowLine.addFrame(B2R_Utility.getReducedBitmapDrawable(getResources(), B2R_Utility.BitmapOrder.SCAN1, R.drawable.ibreathe_body_scan1), 33);
            animationGlowLine.addFrame(B2R_Utility.getReducedBitmapDrawable(getResources(), B2R_Utility.BitmapOrder.SCAN2, R.drawable.ibreathe_body_scan2), 33);
            animationGlowLine.addFrame(B2R_Utility.getReducedBitmapDrawable(getResources(), B2R_Utility.BitmapOrder.SCAN3, R.drawable.ibreathe_body_scan3), 33);
            animationGlowLine.addFrame(B2R_Utility.getReducedBitmapDrawable(getResources(), B2R_Utility.BitmapOrder.SCAN4, R.drawable.ibreathe_body_scan4), 33);
            // TODO: Commented out to prevent Out-of Memory Error
            //animationGlowLine.addFrame(B2R_Utility.getReducedBitmapDrawable(getResources(), B2R_Utility.BitmapOrder.SCAN5, R.drawable.ibreathe_body_scan5), 33);
        
            animationGlowLine.setOneShot(false);
            animationGlowLine.setVisible(true, true);
            glitterLine.setBackgroundDrawable(animationGlowLine);
            
            // run the start() method later on the UI thread
            glitterLine.post(new Starter());
        } catch (Exception ex) {
            Log.d("B2R_BodyScanner", "Exception", ex);
        }
    }

    private boolean isInUse() {
        return (buttonPressed || panPressed);
    }
    
    private void startDecorationWithDelay() {
        if (decorator == null) {
            decorator = new DecorationThread();
            decorator.start();
            if (glitterLine != null) {
                new glitterThread().start();
            }
        }
    }
    
    private void stopAnimation() {
        buttonPressed = false;
        panPressed = false;
        
        if (decorator != null) {
            decorator.setOK(false);
            decorator = null;
        }
        
        if (maskview != null) {
            maskview.setOnTouchListener(null);
        }
        if (bodyview != null) {
            bodyview.setOnTouchListener(null);
        }
        
        for (PortViewOverlay pvo : overlay) {
            if (pvo != null) {
                pvo.setOnTouchListener(null);
            }
        }

        if (animationGlowLine != null) {
            animationGlowLine.stop();
        }
        
        if (glitterLine != null) {
            glitterLine.clearAnimation();
        }
        
        if (mZoomState != null) {
             mZoomState.deleteObservers();
        }
    }

    private void handleOverlays(float f) {
        clearButtonHighlights();
        
        List<BODY_PARTS> bps = BODY_PARTS.grabBodypart(f);
        if (bps == null || bps.isEmpty()) return;
        
        for (int i = 0; i < bps.size(); i++) {
            if (i < MAX_OVERLAYS) {
                if (overlay[i] != null) {
                    overlay[i].setImage(bps.get(i));
                    overlay[i].setAlpha(bps.get(i).getAlpha());
                }
            }
        }
        
        if (bps.size() < MAX_OVERLAYS) {
            for (int i = bps.size(); i < MAX_OVERLAYS; i++) {
                overlay[i].setImage(BODY_PARTS.NONE);
                overlay[i].setAlpha(0);
            }
        }
        
        if (BODY_PARTS.validBodypart(bps.get(0))) {
            setButtonHighlights(bps.get(0));
            mGlows[bps.get(0).getInt()].setAlpha(bps.get(0).getAlpha()); 
        } 
        if (bps.size() > 1) {
            if (BODY_PARTS.validBodypart(bps.get(1))) {
                mGlows[bps.get(1).getInt()].setAlpha(bps.get(1).getAlpha()); 
            } 
        }
        if (bps.size() > 2) {
            if (BODY_PARTS.validBodypart(bps.get(2))) {
                mGlows[bps.get(2).getInt()].setAlpha(bps.get(2).getAlpha()); 
            } 
        }
        
        bps.clear();
    }
    
    private void unbindDrawables(View view) {     
    	if (view != null) {
    		if (view.getBackground() != null) {         
    			view.getBackground().setCallback(null);     
    		}     
    		if (view instanceof ViewGroup) {         
    			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {             
    				unbindDrawables(((ViewGroup) view).getChildAt(i));         
    			}         
    			((ViewGroup) view).removeAllViews();     
    		} 
    	}
    } 

    private class SimpleZoomListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            final float x = event.getX(); // for testing only
            final float y = event.getY();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mX = x; // for testing only
                    mY = y;
                    panPressed = true;
                    break;

                case MotionEvent.ACTION_MOVE: {
                    final float dx = (x - mX) / v.getWidth(); // for testing only
                    final float dy = (y - mY) / v.getHeight();
                    panPressed = true;
                    
                    if (mControlType == ControlType.ZOOM) {
                        mZoomState.setZoom(mZoomState.getZoom() * (float)Math.pow(20, -dy));
                        mZoomState.notifyObservers();
                    } else {
                        if (underTest) mZoomState.setPanX(mZoomState.getPanX() - dx); // for testing only
                        mZoomState.setPanY(mZoomState.getPanY() - dy);
                        
                        float f = mZoomState.getPanY();
                        if (f < 0.03 || f > 0.97) {
                            maskview.setVisibility(View.GONE);
                        } else {
                            maskview.setVisibility(View.VISIBLE);
                        }
                        
                        if (!buttonPressed) {
                            handleOverlays(f);
                        }
                        mZoomState.notifyObservers();
                    }
                    mX = x; // for testing only
                    mY = y;
                    break;
                }
                case MotionEvent.ACTION_UP:
                    panPressed = false;
                    break;
                default:
                    panPressed = false;
            }
            
            return true;
        }
    }
    
    private class DecorationThread extends Thread {
        private boolean ok = true;
        
        public void setOK(boolean f) {ok = f;}
        
        public void run() {
            try{
                while (ok) {
                    for (int i = 0; i < SIMULATIONCYCLEHALFPERIOD; i++) {
                        while (isInUse() && ok) {
                            Thread.sleep(TIMETOSLEEPAFTERTOUCHANDDRAG);
                            fixitHandle.sendMessage(fixitHandle.obtainMessage());
                        }
                        if (ok) {
                            Message message1 = moveHandle.obtainMessage();
                            Message message3 = setHandle.obtainMessage();

                            float f = ((float) (i)) /((float)SIMULATIONCYCLEHALFPERIOD);
                            if (f < 0.03 || f > 0.97) continue;

                            message1.getData().putFloat(token, f);
                            message3.getData().putFloat(token, f);

                            moveHandle.sendMessage(message1);

                            setHandle.sendMessage(message3);
                            Thread.sleep(SIMULATIONVELOCITY);
                        }
                    }
                    for (int i = SIMULATIONCYCLEHALFPERIOD; i > 0; i--) {
                        while (isInUse() && ok) {
                            Thread.sleep(TIMETOSLEEPAFTERTOUCHANDDRAG);
                            fixitHandle.sendMessage(fixitHandle.obtainMessage());
                        }
                        if (ok) {
                            Message message1 = moveHandle.obtainMessage();
                            Message message3 = setHandle.obtainMessage();

                            float f = ((float) (i)) /((float)SIMULATIONCYCLEHALFPERIOD);
                            if (f < 0.03 || f > 0.97)  continue;

                            message1.getData().putFloat(token, f);
                            message3.getData().putFloat(token, f);

                            moveHandle.sendMessage(message1);

                            setHandle.sendMessage(message3);
                            Thread.sleep(SIMULATIONVELOCITY);
                        }
                    }
                }
            } catch(Throwable t){
                Log.d("EXCEPTION", "RUN", t);
            }
        }
    }
    
    private class glitterThread extends Thread {
        public void run() {
            try {
                Thread.sleep(100);

                glitterHandle.sendMessage(glitterHandle.obtainMessage());
            } catch (Exception ex) {
                Log.d("B2R_BodyScanner", "Exception", ex);
            }
        }
    }

    private class Starter implements Runnable {

        public void run() {
            if (animationGlowLine != null) animationGlowLine.start();        
        }
    }
}