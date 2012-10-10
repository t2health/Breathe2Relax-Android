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


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class B2R_Zoom extends View {
	private Drawable image;
	// Current zoom level
	private int _zoom;
	// Flag telling whether to zoom in (true) or out (false)
	private final boolean zoomIn;
	private static final int zoomSpeed = 2;
	private static final int zoomMin = 100; 
	private static final int zoomMax = 300;  

	public B2R_Zoom(Context context, boolean zoomIn) {
		super (context);
		
		this.zoomIn = zoomIn;
		
		if (zoomIn) {
			_zoom = 100;
		} else {
			_zoom = 300;
		}
		image = context.getResources().getDrawable(R.drawable.glass_green);
		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		image.setBounds(getWidth()/2-_zoom, getHeight()/2-_zoom, getWidth()/2+_zoom, getHeight()/2+_zoom);
		image.draw(canvas);
	}

	public void zoomIt() {
		if (zoomIn) {
			_zoom += zoomSpeed;
		} else {
			_zoom -= zoomSpeed;
		}
		if (_zoom < zoomMin) {
			_zoom = zoomMin;
		}
		else if (_zoom > zoomMax) {
			_zoom = zoomMax;
		}
		invalidate();
	}
}
