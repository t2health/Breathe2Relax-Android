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


import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class B2R_WebViewActivity extends BaseNavigationActivity{
	private WebView webView;
	@Override
	protected void onLeftNavigationButtonPressed() {
		webView.goBack();
		webView.destroy();
		finish();
		
		super.onLeftNavigationButtonPressed();
	}
	
	@Override
	public void onBackPressed() {
		webView.goBack();
		webView.destroy();
		finish();
		
		super.onBackPressed();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);

		Intent intent = getIntent();
		String urlString = (String) intent.getCharSequenceExtra("FilePath");

		this.setLeftNavigationButtonVisibility(View.VISIBLE);

		webView = (WebView)findViewById(R.id.webkitWebView);

		webView. getSettings().setJavaScriptEnabled (true);
		webView.getSettings().setBuiltInZoomControls(true);

		webView. getSettings().setJavaScriptCanOpenWindowsAutomatically (false);
		webView. getSettings().setPluginsEnabled (true);
		webView. getSettings().setSupportMultipleWindows (false);
		webView. getSettings().setSupportZoom (false);
		webView. setVerticalScrollBarEnabled (false);
		webView. setHorizontalScrollBarEnabled (false);
		webView.setBackgroundColor(0x00000000);
		
		webView.setWebViewClient(new WebViewClient() {

			// When URL is finished loading, grab its title and set it as the
			// title for this dialog.
			public void onPageFinished(WebView view, String url)
			{
				String title = webView.getTitle();
				if ( title != null && title.length() > 0 )
				{
					setTitle( title);
				}
			}

		});

		webView.loadUrl(urlString);
	}
}
