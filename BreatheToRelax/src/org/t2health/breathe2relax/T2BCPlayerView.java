package org.t2health.breathe2relax;

import android.content.Context;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;

import com.brightcove.mobile.android.BCPlayerView;

public class T2BCPlayerView extends BCPlayerView {
	public T2BCPlayerView(Context context) {
		super(context);
	}

	public T2BCPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public T2BCPlayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener pListener) {
		super.setOnCompletionListener(pListener);
	}

	@Override
	public void setOnPreparedListener(OnPreparedListener pListener) {
		super.setOnPreparedListener(pListener);
	}
}
