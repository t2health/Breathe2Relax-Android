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

import java.util.ArrayList;
import java.util.List;

import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class B2R_ChooseVisualSettingsActivity extends BaseNavigationActivity implements OnClickListener {
	private ListView listView;
	private B2R_Motif it;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.b2r_choose_visual_setting);

		this.setLeftNavigationButtonVisibility(View.VISIBLE);
		this.setRightNavigationButtonVisibility(View.GONE);
		this.setTitle( R.string.backgroundScenery);
		
		ArrayList<B2R_Motif> icontextList = new ArrayList<B2R_Motif>();
		icontextList.add(B2R_Motif.RAIN_FOREST );
		icontextList.add(B2R_Motif.MOUNTAIN_MEADOWS );
		icontextList.add( B2R_Motif.COSMIC_PHOTOS );
		icontextList.add(B2R_Motif.BEACHES);
		icontextList.add(B2R_Motif.FLOWERS );
		icontextList.add(B2R_Motif.SUNSET);
		icontextList.add(B2R_Motif.NO_MOTIF);

		final IconMotifAdapter icontextAdapter = new IconMotifAdapter( 
				this, 
				R.layout.b2r_row,
				icontextList ); 

		listView = (ListView) findViewById(R.id.ChooseVisualSettingsListView);

		listView.setAdapter(icontextAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				it = ((B2R_Motif)parent.getItemAtPosition(position));
				if (it != null) {
					Log.d("SettingsActivity", "item selected: " + it.getText());
				}
				{
					Intent userIntent = new Intent();
					Bundle bundle = new Bundle();
					int returnCode = Settings.MOTIF_CODE;
					
					bundle.putString("MOTIF_SELECTED", it.getText());
					userIntent.putExtras(bundle);

					setResult(returnCode, userIntent);
					finish();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		finish();
	}

	public class IconMotifAdapter extends ArrayAdapter<B2R_Motif> {
		private Context context;
		private List<B2R_Motif> icontextList;

		public IconMotifAdapter(Context context, int row, List<B2R_Motif> icontextList ) { 
			super(context, row, icontextList);
			this.context = context;
			this.icontextList = icontextList;
		}

		public int getCount() {                        
			return icontextList.size();
		}

		public B2R_Motif getItem(int position) {     
			return icontextList.get(position);
		}

		public long getItemId(int position) {  
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) { 
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.b2r_row, null);
			}
			B2R_Motif icontext = icontextList.get(position);

			return new IconMotifAdapterView(this.context, icontext );
		}
	}
	class IconMotifAdapterView extends LinearLayout {        
		public IconMotifAdapterView(Context context, B2R_Motif icontext ) {
			super( context );

			this.setVerticalScrollBarEnabled(true);
			this.setOrientation(HORIZONTAL);       

			LinearLayout.LayoutParams iconParams = 
				new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
			ImageView iconControl = new ImageView( context );
			iconControl.setImageResource( icontext.getId() );

			addView( iconControl, iconParams );

			LinearLayout.LayoutParams textParams = 
				new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			textParams.setMargins(1, 1, 1, 1);
	
			LinearLayout myText = new LinearLayout(B2R_ChooseVisualSettingsActivity.this);
			myText.setOrientation(VERTICAL);	
			
			TextView textControl = new TextView( context );
			textControl.setText( icontext.getText() );
			textControl.setTextSize(14f);
			textControl.setTextColor(Color.WHITE);

			myText.addView( textControl, textParams);      
			
			TextView textControl2 = new TextView( context );
			textControl2.setText( icontext.getDescription() );
			textControl2.setTextSize(10f);
			textControl2.setTextColor(Color.WHITE);
			
			myText.addView(textControl2, textParams);      
			
			addView(myText);
		}
	}

}
