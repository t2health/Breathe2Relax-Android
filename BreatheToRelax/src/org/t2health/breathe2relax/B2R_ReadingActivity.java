package org.t2health.breathe2relax;

import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class B2R_ReadingActivity extends BaseNavigationActivity {
	private WebView webView;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2r_read);
        this.setLeftNavigationButtonVisibility(View.VISIBLE);
        
        Intent intent = getIntent();
		String urlString = (String) intent.getCharSequenceExtra("FilePath");
		webView = (WebView)findViewById(R.id.webkitWebView);
		webView.setBackgroundColor(0x00000000);

		webView.setWebViewClient(new WebViewClient() {
			// When URL is finished loading, grab its title and set it as the
			// title for this dialog.
			public void onPageFinished(WebView view, String url)
			{
				setTitle( webView.getTitle());
			}

		});
		webView.loadUrl(urlString);
	}
	
}