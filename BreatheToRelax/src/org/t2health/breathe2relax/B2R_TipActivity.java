package org.t2health.breathe2relax;


import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import org.t2health.lib.activity.BaseNavigationActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class B2R_TipActivity extends BaseNavigationActivity implements OnClickListener {
	private final Random random = new Random();
	private String stringXmlContent = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tiplayout);

		this.setTitle(R.string.tipTitle);
		this.setLeftNavigationButtonVisibility(View.GONE);
		this.setRightNavigationButtonVisibility(View.GONE);

		findViewById(R.id.buttonTipLayout).setOnClickListener(this);

		try {
			stringXmlContent = getEventsFromAnXML(this, getDayOfYear());
		} catch (XmlPullParserException e) {
			Log.d("B2R_TipActivity", "Exception", e);
		} catch (IOException e) {
			Log.d("B2R_TipActivity", "Exception", e);
		}

		TextView webView = (TextView)findViewById(R.id.webviewTipLayout);
		webView.setBackgroundColor(0x00000000);
		webView.setText(stringXmlContent);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.buttonTipLayout:
			finish();
			break;
		}
	}

	private String getEventsFromAnXML(Activity activity, int number) throws XmlPullParserException, IOException {
		Resources res = activity.getResources();
		XmlResourceParser xpp = res.getXml(R.xml.tips);
		xpp.next();
		int eventType = xpp.getEventType();
		String NodeValue;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_TAG) {
				NodeValue = xpp.getName();
				if (NodeValue.equalsIgnoreCase("tip")) {
					if (xpp.getAttributeName(0).equalsIgnoreCase("number")) {
						try {
							int cnt = Integer.parseInt(xpp.getAttributeValue(0));
							if (number == cnt) {
								if (xpp.getAttributeName(1).equalsIgnoreCase("text")) {
									return xpp.getAttributeValue(1);
								}
							}
						} catch (Exception ex) {
							ex.getStackTrace();
						}
					}
				}
			} 
			eventType = xpp.next();
		}

		return null;
	}

	protected int getDayOfYear() {
		Calendar rightNow = Calendar.getInstance();

		return rightNow.get(Calendar.DAY_OF_YEAR) % 55; 
	}

	protected int getRandom() {
		return random.nextInt(54);
	}
}
