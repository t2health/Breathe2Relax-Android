package org.t2health.breathe2relax;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class B2R_SeekBar extends SeekBar implements B2R_FromParentTouchHandler {

	public B2R_SeekBar(Context context) {
		super(context);
	}

	public B2R_SeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public B2R_SeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouchEventFromParent(MotionEvent event) {

		return this.onTouchEvent(event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {

		return super.onTrackballEvent(event);
	}
}
