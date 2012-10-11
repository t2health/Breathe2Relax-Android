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

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class PortViewOverlay extends ScrollView implements Observer {
	private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private final Rect mRectSrc = new Rect();
	private final Rect mRectDst = new Rect();
	private Bitmap mBitmap;
	private BODY_PARTS bodyPart = BODY_PARTS.NONE;
	private float mAspectQuotient;
	private ZoomState mState;
	private volatile int alpha = 255;

	// TODO: intercept onInterceptTouchEvent?
	
	public PortViewOverlay(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setBodyImage();
		
		setFocusable( true );
	}

	public PortViewOverlay( Context context ){
		super( context );
		
		setBodyImage();
		
		setFocusable( true );
	}

	public PortViewOverlay( Context context, AttributeSet attributeSet, int stuff ){
		super(context, attributeSet, stuff);
		
		setBodyImage();
		
		setFocusable( true );
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public void setImage(BODY_PARTS bp) {
		if (!bodyPart.equals(bp)) {
			if ( !bp.equals(BODY_PARTS.NONE) ) {
				mBitmap = B2R_Utility.getReducedBitmap(getResources(), bp.getBitmapOrder(), bp.getBitmapInt());
			} else {
				mBitmap = null;
			}
		}

		bodyPart = bp;

		calculateAspectQuotient();

		invalidate();
	}
	
	public BODY_PARTS getBodypart() {
		return bodyPart;
	}

	private void setBodyImage() {
		//Init all body parts
		try {
			for (BODY_PARTS bp : BODY_PARTS.values()) {
				if ( !bp.equals(BODY_PARTS.NONE) ) {
					mBitmap = B2R_Utility.getReducedBitmap(getResources(), bp.getBitmapOrder(), bp.getBitmapInt());
				}
			}
			mBitmap = B2R_Utility.getReducedBitmap(getResources(), BODY_PARTS.BODY.getBitmapOrder(), BODY_PARTS.BODY.getBitmapInt());

			setSmoothScrollingEnabled(true);
			calculateAspectQuotient();

			invalidate();
		} catch (Exception ex) {
			Log.d("PortViewOverlay", "Exception", ex);
		}
	}

	public void setZoomState(ZoomState state) {
		if (mState != null) {
			mState.deleteObserver(this);
		}

		mState = state;
		mState.addObserver(this);

		invalidate();
	}

	private void calculateAspectQuotient() {
		if (mBitmap != null) {
			mAspectQuotient = (((float)mBitmap.getWidth()) / mBitmap.getHeight()) / (((float)getWidth()) / getHeight());
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if ( mState != null) {
			final int viewWidth = getWidth();
			final int viewHeight = getHeight();
			final float panX = mState.getPanX();
			final float panY = mState.getPanY();
			if (mBitmap != null) {
				final int bitmapWidth = mBitmap.getWidth();
				final int bitmapHeight = mBitmap.getHeight();
				final float zoomX = mState.getZoomX(mAspectQuotient) * viewWidth / bitmapWidth;
				final float zoomY = mState.getZoomY(mAspectQuotient) * viewHeight / bitmapHeight;

				// Setup source and destination rectangles
				mRectSrc.left = (int)(panX * bitmapWidth - viewWidth / (zoomX * 2));
				mRectSrc.top = (int)(panY * bitmapHeight - viewHeight / (zoomY * 2));
				mRectSrc.right = (int)(mRectSrc.left + viewWidth / zoomX);
				mRectSrc.bottom = (int)(mRectSrc.top + viewHeight / zoomY);
				mRectDst.left = getLeft();
				mRectDst.top = getTop();
				mRectDst.right = getRight();
				mRectDst.bottom = getBottom();

				// Adjust source rectangle so that it fits within the source image.
				if (mRectSrc.left < 0) {
					mRectDst.left += -mRectSrc.left * zoomX;
					mRectSrc.left = 0;
				}
				if (mRectSrc.right > bitmapWidth) {
					mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
					mRectSrc.right = bitmapWidth;
				}
				if (mRectSrc.top < 0) {
					mRectDst.top += -mRectSrc.top * zoomY;
					mRectSrc.top = 0;
				}
				if (mRectSrc.bottom > bitmapHeight) {
					mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
					mRectSrc.bottom = bitmapHeight;
				}

				mPaint.setAntiAlias(true);
				mPaint.setFilterBitmap(true);
				mPaint.setDither(true);

				mPaint.setAlpha(alpha);
				canvas.drawBitmap(mBitmap, mRectSrc, mRectDst, mPaint);
			} 
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}


	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		calculateAspectQuotient();
	}

	// implements Observer
	public void update(Observable observable, Object data) {
		computeScroll();
		invalidate();
	}
}