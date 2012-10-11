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

import org.t2health.lib.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Present a dialog in web view with a close button and the option to not 
 * show it again (next time the app is run). 
 * @author jon.hulthen
 */
public class B2R_PopupDialog extends Dialog implements OnClickListener {
	private WebView webView;
	private CheckBox doNotShowAgain;
	private final String urlString;
	private B2R_Menu next;
	private int filePointer;
	
	/**
	 * Construct a dialog for a particular URL.
	 * @param context Application context.
	 * @param urlString URL string for content to display.
	 * @param next Page to which dialog applies.
	 */
	public B2R_PopupDialog(Context context, String urlString, B2R_Menu next) {
		super(context);

		this.urlString = urlString;
		this.next = next;
	}

	/**
	 * Construct a dialog with for a particular URL with attendant audio file. 
	 * @param context Application context.
	 * @param urlString URL string for content to display.
	 * @param next Page to which dialog applies.
	 * @param filePointer File pointer number for accompanying audio file.
	 */
	public B2R_PopupDialog(Context context, String urlString, B2R_Menu next, int filePointer) {
		super(context);

		this.urlString = urlString;
		
		this.next = next;
		this.filePointer = filePointer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!B2R_SettingsHolder.show(next)) {
			this.cancel();
		}

		if (this.getContext().getResources().getConfiguration().orientation == 1) {
			setContentView(R.layout.b2r_dialog);
		} else {
			setContentView(R.layout.b2r_dialog_land);
		}

		webView = (WebView)findViewById(R.id.webkitInformationWebView);
		doNotShowAgain = (CheckBox)findViewById(R.id.buttoninformationnotagain);
		doNotShowAgain.setOnClickListener(this);
		findViewById(R.id.buttoninformationclose).setOnClickListener(this);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new InformationWebViewClient());
		webView.setBackgroundColor(0x000000);
		
		final Dialog activity = this;

		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {

			}
		});
		webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Log.d("B2R_PopupDialog", "" + "error: " + errorCode + ". descr: " + description + ". url: " + failingUrl);
				Toast.makeText(activity.getContext(), "Oh no! " + description, Toast.LENGTH_LONG).show();
			}

			// When URL is finished loading, grab its title and set it as the
			// title for this dialog.
			public void onPageFinished(WebView view, String url)
			{
				setTitle( webView.getTitle());
			}

		});

		webView.loadUrl(urlString);

		try {
			B2R_Utility.talkLongTalk(this.getContext(), filePointer);
		} catch (Exception ex) {
			Log.d("B2R_PopupDialog", "Exception", ex);
		}
	}

	/**
	 * Intercept the back key so that we can stop audio accompaniment,
	 * if it exists.
	 */
	@Override
	public void onBackPressed() {
		if ( webView.canGoBack()) {
			webView.goBack();
		}
		B2R_Utility.stopLongTalk();
		applySettings();
	    super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		// No need to intercept DoNotShow button click since we can just
		// check its state on the way out and update setting then.
		switch (v.getId()) {
		case R.id.buttoninformationclose:
			B2R_Utility.stopLongTalk();
			applySettings();
			break;
		}
	}

	private class InformationWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
	private void applySettings()
	{
		B2R_SettingsHolder.putBoolean( next.name(), !doNotShowAgain.isChecked());
		B2R_SettingsHolder.unsetShow(next);
		this.cancel();
	}

}