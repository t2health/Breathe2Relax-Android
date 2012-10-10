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

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;


public class B2R_TrackActivity extends BaseNavigationActivity
	implements GestureDetector.OnGestureListener
{
	private GrabDataTask grabData;
	private float chartLineWidth;
	private float pointSize;
	private static final String CURRENT_MONTH = "current_month";
	private static final String RENDERER = "renderer";
	private static final String DATASET = "dataset";
	private static final SimpleDateFormat curFormat = new SimpleDateFormat("MMMM yyyy");
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private String mDateFormat;
	private GestureDetector gestureDetector;

	private Calendar startOfGraphCalendar;
	private Calendar endOfGraphCalendar;
	
	private View mMinusButton;
	private View mPlusButton;
	private TextView mMonth;

	private GraphicalView mChartView;

	private List<B2R_MoodTrackingsTable> resultList = null;

	private Calendar currentMonth;

	private final String seriesTitleBefore = "Before ";
	private final String seriesTitleAfter = "After";
	private XYSeries seriesBefore = new XYSeries(seriesTitleBefore);
	private XYSeries seriesAfter = new XYSeries(seriesTitleAfter);
	private XYSeriesRenderer rendererBefore = new XYSeriesRenderer();
	private XYSeriesRenderer rendererAfter = new XYSeriesRenderer();
	
	private int orientation;
	
	private volatile boolean useDummyData = false;
	private List<B2R_MoodTrackingsTable> list = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		chartLineWidth = 2 * displayMetrics.density;
		pointSize = 1.5F * chartLineWidth;
		gestureDetector = new GestureDetector(this, this);

		setContentView(R.layout.tracklayout);
		
		this.setTitle( R.string.results_title);

		mDataset.addSeries(seriesAfter);
		mDataset.addSeries(seriesBefore);

		mRenderer.addSeriesRenderer(rendererBefore);
		mRenderer.addSeriesRenderer(rendererAfter);
		
		configureGraph();

		mMinusButton = findViewById(R.id.monthMinusButton);
		mMinusButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v)
			{
				B2R_TrackActivity.this.previousMonth();
			}
		});
		mMinusButton.setOnLongClickListener( new OnLongClickListener() {
			@Override
			public boolean onLongClick( View v)
			{
				B2R_TrackActivity.this.previousYear();
				return true;
			}
		});
		
		mPlusButton = findViewById(R.id.monthPlusButton);
		mPlusButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v)
			{
				B2R_TrackActivity.this.nextMonth();
			}
		});
		mPlusButton.setOnLongClickListener( new OnLongClickListener() {
			@Override
			public boolean onLongClick( View v)
			{
				B2R_TrackActivity.this.nextYear();
				return true;
			}
		});
		
		if ( savedInstanceState != null ) {
			onRestoreInstanceState(savedInstanceState);
		} else {
			currentMonth = Calendar.getInstance();
			currentMonth.set( Calendar.DAY_OF_MONTH, 1);
			currentMonth.set( Calendar.HOUR_OF_DAY, 0);
			currentMonth.set( Calendar.MINUTE, 0);
			currentMonth.set( Calendar.SECOND, 0);
		}
		
		mMonth = (TextView) findViewById(R.id.monthName);
		updateActiveMonth();
		
		orientation = this.getResources().getConfiguration().orientation;

		//doStart();
		grabData = new GrabDataTask();
		grabData.execute("");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mDataset = (XYMultipleSeriesDataset) savedState.getSerializable(DATASET);
		mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable(RENDERER);
		mDateFormat = savedState.getString("date_format");
		currentMonth = (Calendar)savedState.getSerializable( CURRENT_MONTH);
		
		orientation = savedState.getInt("ORIENTATION");
		this.setRequestedOrientation(orientation);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(DATASET, mDataset);
		outState.putSerializable(RENDERER, mRenderer);
		outState.putString("date_format", mDateFormat);
		outState.putSerializable( CURRENT_MONTH, currentMonth);
		
		outState.putInt("ORIENTATION", orientation);
		Log.d("onSaveInstanceState", "o = " + orientation);
	}

	@Override
	protected void onResume() {
		if (B2R_SettingsHolder.show(B2R_Menu.RESULTS_MENU)) {
			showDialog(0);
		}

		super.onResume();

		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			boolean enabled = mDataset.getSeriesCount() > 0;
			setSeriesEnabled(enabled);
		} else {
			mChartView.repaint();
		}
	}

	private void setSeriesEnabled(boolean enabled) {
	}

	private void doStart() {
		seriesBefore.clear();
		seriesAfter.clear();

		try {
			Dao<B2R_MoodTrackingsTable, ?> dao = this.getHelper().getDao(
					B2R_MoodTrackingsTable.class);
			if (!dao.isTableExists())
			{
				Log.d( "onStart", "db empty");
				resultList = new ArrayList<B2R_MoodTrackingsTable>();
			} else {
				QueryBuilder<B2R_MoodTrackingsTable, ?> queryBuilder = dao
						.queryBuilder();
				// Currently getting the whole batch of data rather than just
				// a month or two.
				queryBuilder.orderBy( "date", true);
				resultList = dao.query( queryBuilder.prepare());
				if (resultList.isEmpty()) {
					Log.d( "onStart", "db empty");
					resultList = new ArrayList<B2R_MoodTrackingsTable>();
				} else {
					startOfGraphCalendar = Calendar.getInstance();
					endOfGraphCalendar = Calendar.getInstance();
					startOfGraphCalendar.setTime( resultList.get(0).getDate());
					endOfGraphCalendar.setTime(  resultList.get(resultList.size()-1).getDate());
				}

			}
		} catch (SQLException e) {
			Log.d("B2R_TrackActivity", "Exception", e);
			resultList = new ArrayList<B2R_MoodTrackingsTable>();
		}

		buildGraph();
	}
	
	private void doStart(List<B2R_MoodTrackingsTable> list) {
		seriesBefore.clear();
		seriesAfter.clear();

		if (list != null && !list.isEmpty()) {
			startOfGraphCalendar = Calendar.getInstance();
			endOfGraphCalendar = Calendar.getInstance();
			startOfGraphCalendar.setTime( list.get(0).getDate());
			endOfGraphCalendar.setTime(  list.get(list.size()-1).getDate());
		
			buildGraph(list);
		}
	}

	private void configureGraph() {
		
		mRenderer.setPanEnabled( false, false);
		mRenderer.setZoomEnabled( false, false);
		mRenderer.setXTitle( "Day of Month");
		mRenderer.setYTitle( "Stress Level");
		mRenderer.setShowLegend( true);
		mRenderer.setApplyBackgroundColor( true);
		mRenderer.setPointSize( pointSize);
		mRenderer.setFitLegend( true);

		rendererBefore.setColor(Color.CYAN);
		rendererBefore.setFillPoints( true);
		rendererBefore.setLineWidth( chartLineWidth);
		rendererBefore.setPointStyle( PointStyle.CIRCLE);
		
		rendererAfter.setColor(Color.YELLOW);
		rendererAfter.setFillPoints( true);
		rendererAfter.setLineWidth( chartLineWidth);
		rendererAfter.setPointStyle( PointStyle.DIAMOND);
		
		mRenderer.setAxisTitleTextSize(20);
		mRenderer.setLabelsTextSize(16);
		mRenderer.setLegendTextSize(16);
		mRenderer.setBackgroundColor(Color.DKGRAY);
		mRenderer.setAxesColor(Color.WHITE);
		mRenderer.setXAxisMin(1.00);
		mRenderer.setXAxisMax(31.50);
		mRenderer.setYAxisMin(0.00);
		mRenderer.setYAxisMax(100.00);
		mRenderer.setYLabels(10);
		mRenderer.setXLabels(15);
		
		// Amendment
		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setMargins(new int[] { 10, 65, 18, 20 });
		mRenderer.setLegendHeight(100);

	}

	private void buildGraph() {

		seriesBefore.clear();
		seriesAfter.clear();

		for (B2R_MoodTrackingsTable entry : resultList) {
			Calendar cal = Calendar.getInstance();
			Date date = entry.getDate();
			cal.setTime(date);
			if (( currentMonth.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) &&
					( currentMonth.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))
			{
				seriesBefore.add(cal.get(Calendar.DAY_OF_MONTH), entry.getBeforeResult());
				seriesAfter.add(cal.get(Calendar.DAY_OF_MONTH), entry.getAfterResult());
			} 
		}
	}
	
	private void buildGraph(List<B2R_MoodTrackingsTable> list) {

		seriesBefore.clear();
		seriesAfter.clear();

		for (B2R_MoodTrackingsTable entry : list) {
			Calendar cal = Calendar.getInstance();
			Date date = entry.getDate();
			cal.setTime(date);
			if (( currentMonth.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) &&
					( currentMonth.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))
			{
				seriesBefore.add(cal.get(Calendar.DAY_OF_MONTH), entry.getBeforeResult());
				seriesAfter.add(cal.get(Calendar.DAY_OF_MONTH), entry.getAfterResult());
			} 
		}
	}

	@Override
	protected Dialog onCreateDialog(int i) {
		return new B2R_PopupDialog(this, "file:///android_asset/html/b2r_results.html", B2R_Menu.RESULTS_MENU);
	}
	
	/*
	 * Go to the previous month of chart data.
	 */
	private void previousMonth() {               
		// Update your view from here only.   
		currentMonth.add(Calendar.MONTH,-1);

		updateActiveMonth();
		
		if (this.useDummyData) {
			buildGraph(list);
		} else {
			buildGraph();
		}
		
		if (mChartView != null) {
			mChartView.repaint();
		}

	}
	
	/*
	 * Go to the next month of chart data.
	 */
	private void nextMonth() {               
		// Update your view from here only.
		currentMonth.add(Calendar.MONTH,1);

		updateActiveMonth();
		if (this.useDummyData) {
			buildGraph(list);
		} else {
			buildGraph();
		}

		if (mChartView != null) {
			mChartView.repaint();
		}
		
	}

	/*
	 * Go to the previous month of chart data.
	 */
	private void previousYear() {               
		// Update your view from here only.   
		currentMonth.add(Calendar.YEAR,-1);
		updateActiveMonth();
		if (this.useDummyData) {
			buildGraph(list);
		} else {
			buildGraph();
		}

		if (mChartView != null) {
			mChartView.repaint();
		}
	}
	
	/*
	 * Go to the next month of chart data.
	 */
	private void nextYear() {               
		// Update your view from here only.   
		currentMonth.add(Calendar.YEAR,1);
		updateActiveMonth();
		if (this.useDummyData) {
			buildGraph(list);
		} else {
			buildGraph();
		}

		if (mChartView != null) {
			mChartView.repaint();
		}
		
	}
	
	private void updateActiveMonth() {
		mMonth.setText(curFormat.format(currentMonth.getTime()));
	}

	/*
	 * Load the menu for this activity.
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.track_menu, menu);
		return true;
	}

	// Handle selection of menu items
	@Override
	public boolean onOptionsItemSelected( MenuItem item) {
		Intent userIntent = null;
		switch (item.getItemId()) {
		// Show about page.
		case R.id.buttonTrackMenuAbout:
			userIntent = new Intent(this, B2R_WebViewActivity.class);
			userIntent.putExtra("FilePath", "file:///android_asset/html/trackResults.html");
			startActivity( userIntent);
			return true;
		// Generate test stress ratings data.
		case R.id.buttonTrackGenerate:
			generateData();
			return true;
		// Clear stress ratings data.
		case R.id.buttonTrackClear:
			if (useDummyData) {
				list.clear();
				doStart(list);
				//doStart();
				grabData = null;
				grabData = new GrabDataTask();
				grabData.execute("");
			} else { 
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            //Yes button clicked
				        	clearData();
				        	
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Are you sure? (real data)").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
			}
			useDummyData = false;
			
			return true;
		// Clear stress ratings data.
		case R.id.buttonTrackRedo:
			if (!useDummyData) {
				clearTodayData();
			}

			return true;
		}
		return super.onOptionsItemSelected( item);
	}
	
	/*
	 * Generate random data in order to demo charting.
	 * //Currently generates 31 days worth of data (up through yesterday),
	 * Currently generates 400 days of history and 40 days of future,
	 * placing a data value only for days that do not already have data.
	 */
	private void generateData(){
		B2R_MoodTrackingsTable row = null;
		Calendar pastDay;
		Date day;
		
		Random rand = new Random( System.currentTimeMillis());

		try {
			list = new ArrayList<B2R_MoodTrackingsTable>();

			for (int daysOffset = -400; daysOffset <= 40; daysOffset++) {
				pastDay = Calendar.getInstance();
				pastDay.add( Calendar.DAY_OF_YEAR, daysOffset);
				day = pastDay.getTime();

				row = new B2R_MoodTrackingsTable();
				row.setDate( day);
				row.setBeforeResult( rand.nextInt( 100));
				row.setAfterResult( rand.nextInt( 100));
				list.add(row);
			}
			
			if (!list.isEmpty()) {
				Collections.sort(list, new Comparator<B2R_MoodTrackingsTable>(){
					public int compare(B2R_MoodTrackingsTable o1, B2R_MoodTrackingsTable o2) {
						return (o1.getDate().compareTo(o2.getDate()));
					}
	 
				});
				
				useDummyData = true;
				doStart(list);
			}
		} catch (Exception ex) {
			Log.d("B2R_TrackActivity", "Exception", ex);
		}
		
	}
	
	/*
	 * Clear all stress rating data from database.
	 */
	private void clearData() {
		try {
			Dao<B2R_MoodTrackingsTable, ?> dao = this.getHelper().getDao(
					B2R_MoodTrackingsTable.class);
			DeleteBuilder<B2R_MoodTrackingsTable, ?> deleter = dao.deleteBuilder();
			dao.delete( deleter.prepare());
			//doStart();
			grabData = null;
			grabData = new GrabDataTask();
			grabData.execute("");
		} catch (Exception ex) {
			Log.d("B2R_TrackActivity", "Exception", ex);
		}
	}
	
	/*
	 * Clear all stress rating data from database.
	 */
	private void clearTodayData() {
		try {
			Dao<B2R_MoodTrackingsTable, ?> dao = this.getHelper().getDao(
					B2R_MoodTrackingsTable.class);
			DeleteBuilder<B2R_MoodTrackingsTable, ?> deleter = dao.deleteBuilder();
			Calendar today = Calendar.getInstance();
			today.set( Calendar.HOUR_OF_DAY, 0);
			today.set( Calendar.MINUTE, 0);
			today.set( Calendar.SECOND, 0);
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.setTime( today.getTime());
			tomorrow.add( Calendar.DAY_OF_YEAR, 1);
			tomorrow.add( Calendar.MILLISECOND, -1);
			// Delete only between midnight this morning and a millisecond 
			// before midnight tonight (tomorrow morning).
			deleter.where().between( "date", today.getTime(), tomorrow.getTime());
			dao.delete( deleter.prepare());
			
			//doStart();
			grabData = null;
			grabData = new GrabDataTask();
			grabData.execute("");
		} catch (Exception ex) {
			Log.d("B2R_TrackActivity", "Exception", ex);
		}
		
	}
	
	@Override
	public boolean onDown( MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
    }

	@Override
	public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		final int velocityThreshold = 200;
		if(velocityX > velocityThreshold) {
			previousMonth();
			return true;
			
		} else if(velocityX < -velocityThreshold) {
			nextMonth();
			return true;
		}
		
		return false;
	}

	@Override
	public void onLongPress( MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress( MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp( MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class GrabDataTask extends AsyncTask<String, Integer, Void> {
	     private ProgressDialog dialog;
	     protected Void doInBackground(String... dontCare) {
	         doStart();
	         return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	     }

	     protected void onPostExecute(Void result) {
	         dialog.cancel();
	     }

	     protected void onPreExecute() {
	         dialog = new ProgressDialog(B2R_TrackActivity.this);
	         dialog.setMessage("Thinking...");
	         dialog.setIndeterminate(true);
	         dialog.setCancelable(false);
	         dialog.show();
	     }
	 }
}