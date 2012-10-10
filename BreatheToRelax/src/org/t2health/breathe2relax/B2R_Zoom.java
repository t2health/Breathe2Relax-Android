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
