package org.t2health.breathe2relax;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class B2R_SliderWidget extends LinearLayout implements OnSeekBarChangeListener {
	private TextView minLabel;
	private TextView maxLabel;
	private SeekBar slideBar;

	private OnSliderWidgetChangeListener onSliderWidgetChangeListener;

	public B2R_SliderWidget(Context context) {
		super(context);
	}

	public B2R_SliderWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		this.minLabel = (TextView)this.findViewById(R.id.minLabel);
		this.maxLabel = (TextView)this.findViewById(R.id.maxLabel);
		this.slideBar = (SeekBar)this.findViewById(R.id.slideBar);
		this.slideBar.setProgress(50);
		this.slideBar.setOnSeekBarChangeListener(this);
	}

	public void setMinLabelText(CharSequence s) {
		this.minLabel.setText(s);
	}

	public CharSequence getMinLabelText() {
		return this.minLabel.getText();
	}

	public void setMaxLabelText(CharSequence s) {
		this.maxLabel.setText(s);
	}

	public CharSequence getMaxLabelText() {
		return this.maxLabel.getText();
	}

	public void setProgress(int p) {

		this.slideBar.setProgress(p);
	}

	public int getProgress() {
		return this.slideBar.getProgress();
	}

	public void setOnSliderWidgetChangeListener(OnSliderWidgetChangeListener l) {

		this.onSliderWidgetChangeListener = l;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		if(onSliderWidgetChangeListener != null) {
			onSliderWidgetChangeListener.onProgressChanged(this, progress, fromUser);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

		if(onSliderWidgetChangeListener != null) {
			onSliderWidgetChangeListener.onStartTrackingTouch(this);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		if(onSliderWidgetChangeListener != null) {
			onSliderWidgetChangeListener.onStopTrackingTouch(this);
		}
	}



	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		this.requestDisallowInterceptTouchEvent(false);
		return super.dispatchTouchEvent(ev);
	}

	public interface OnSliderWidgetChangeListener {
		public void onProgressChanged(B2R_SliderWidget sliderWidget, int progress,
				boolean fromUser);
		public void onStartTrackingTouch(B2R_SliderWidget sliderWidget);
		public void onStopTrackingTouch(B2R_SliderWidget sliderWidget);
	}

}
