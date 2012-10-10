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
