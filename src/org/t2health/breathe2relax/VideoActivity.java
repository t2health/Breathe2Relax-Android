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

import org.t2health.lib.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.brightcove.mobile.mediaapi.ReadAPI;
import com.brightcove.mobile.mediaapi.model.enums.MediaDeliveryTypeEnum;

public class VideoActivity extends Activity implements OnCompletionListener, OnPreparedListener, OnCancelListener {
	public static final String EXTRA_VIDEO_ID = "videoId";
	private static final String EXTRA_POSITION = "pos";
	
	private static final String BRIGHTCOVE_READ_TOKEN = "KqxnaC4wR_9Z7OoPadhidvvddOPPUIwPiHwIj_WZXqJWZohd9G1Mmw..";
	private ProgressDialog loadingDialog;
	private T2BCPlayerView player;
	private int playerStartPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		long videoId = this.getVideoId();
		if(videoId < 0) {
			this.finish();
			return;
		}
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.setContentView(R.layout.video_activity);
		
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setCancelable(true);
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage(getString(R.string.loading));
		loadingDialog.setOnCancelListener(this);
		
		ReadAPI brightcoveApi = new ReadAPI(BRIGHTCOVE_READ_TOKEN);
		brightcoveApi.setMediaDeliveryType(MediaDeliveryTypeEnum.HTTP);
		
		player = (T2BCPlayerView)this.findViewById(R.id.player);
		player.setOnCompletionListener(this);
		player.setOnPreparedListener(this);
		
		if(savedInstanceState != null) {
			playerStartPosition  = savedInstanceState.getInt(EXTRA_POSITION);
		}
		
		// if network is not available.
		if(!this.isNetworkAvailable()) {
			new AlertDialog.Builder(this)
				.setMessage(getString(R.string.no_conenction_message))
				.setCancelable(false)
				.setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.create()
				.show();
			
	    // if a network connection is available.
		} else {
			loadingDialog.show();
			try {
				player.load(brightcoveApi.findVideoById(videoId, null, null));
				player.start();
				
			} catch (Exception e) {
				new AlertDialog.Builder(this)
					.setMessage(getString(R.string.could_not_play_video))
					.setCancelable(false)
					.setPositiveButton(R.string.ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})
					.create()
					.show();
				e.printStackTrace();
			}
		}
	}
	
	private long getVideoId() {
		Long videoIdLong = null;
		String videoId = this.getIntent().getStringExtra(EXTRA_VIDEO_ID);
		if(videoId == null) {
			return -1;
		}
		try {
			videoIdLong = Long.parseLong(videoId);
		} catch (Exception e) {
			this.finish();
			return -1;
		}
		if(videoIdLong == null) {
			this.finish();
			return -1;
		}
		return videoIdLong;
	}
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_POSITION, player.getCurrentPosition());
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		this.finish();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		loadingDialog.hide();
		player.seekTo(playerStartPosition);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		this.finish();
	}
}
