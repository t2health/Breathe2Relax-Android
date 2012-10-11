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

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class B2R_SettingsHolder {
	static private Set<B2R_Menu> showSet = new HashSet<B2R_Menu> ();
	
	static private SharedPreferences sharedPref = null;

	static public void init(Context context) {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		reset();
	}
	
	// ===== Manage guide prompt first shows =====

	/**
	 * Set this B2R_Menu item to be showable this session.
	 * @param menu B2R_Menu item to set showable
	 */
	static public void setShow( B2R_Menu menu)
	{
		if (!showSet.contains( menu))
		{
			showSet.add( menu);
		}
	}

	/**
	 * Don't show this item again this session.
	 * @param menu B2R_Menu item to set not showable
	 */
	static public void unsetShow( B2R_Menu menu)
	{
		if (showSet.contains( menu))
		{
			showSet.remove( menu);
		}
	}

	/**
	 * Set all items to be showable this session.
	 */
	static public void reset()
	{
		for (B2R_Menu m : B2R_Menu.values())
		{
			if (getBoolean( m) || !isSet( m))
			{
				showSet.add( m);
			}
		}
	}

	/**
	 * Set all items to be not showable this session.
	 */
	static public void clear()
	{
		showSet.clear();
	}

	/**
	 * Indicate whether a particular prompt should show.
	 * @param menu B2R_Menu item to check for show ability.
	 * @return Flag (boolean) indicating whether or not to show menu guide. true
	 *         = show; false = hide
	 */
	static public boolean show( B2R_Menu menu)
	{
		return showSet.contains( menu);
	}

	// ===== Manage Settings (preferences) =====

	// Puts

	/**
	 * Save the specified key/value pair to preferences.
	 * 
	 * @param key String key for preference
	 * @param value String value to put into preferences
	 */
	static public void put( String key, String value)
	{
		Editor editor = sharedPref.edit();
		editor.putString( key, value);
		editor.commit();
	}

	/**
	 * Save the specified key/value pair to preferences.
	 * @param key B2R_Setting key for preference
	 * @param value String value to put into preferences
	 */
	static public void put( B2R_Setting key, String value)
	{
		Editor editor = sharedPref.edit();
		editor.putString( key.getKey(), value);
		editor.commit();
	}

	/**
	 * Save the specified key/value pair to preferences as boolean flag.
	 * @param key String key for preference
	 * @param value boolean value to put into preferences
	 */
	static public void putBoolean( String name, boolean value)
	{
		Editor editor = sharedPref.edit();
		editor.putBoolean( name, value);
		editor.commit();
	}

	/**
	 * Save the specified key/value pair to preferences as boolean flag.
	 * @param key B2R_Setting key for preference
	 * @param value boolean value to put into preferences
	 */
	static public void putBoolean( B2R_Setting key, boolean value)
	{
		putBoolean( key.getKey(), value);
	}

	/**
	 * Save the specified key/value pair to preferences as boolean flag.
	 * @param key B2R_Menu key for preference
	 * @param value boolean value to put into preferences
	 */
	static public void putBoolean( B2R_Menu key, boolean value)
	{
		putBoolean( key.name(), value);
	}

	// Set Checks

	/**
	 * Find whether a preference is stored for the specified key.
	 * @param key String key for preference
	 * @return boolean flag indicating whether preference is set
	 */
	static public Boolean isSet( String key)
	{
		return ((sharedPref == null) ? false : sharedPref.contains( key));
	}

	/**
	 * Find whether a preference is stored for the specified key.
	 * @param key B2R_Setting key for preference
	 * @return boolean flag indicating whether preference is set
	 */
	static public Boolean isSet( B2R_Setting key)
	{
		return isSet( key.getKey());
	}

	/**
	 * Find whether a preference is stored for the specified key.
	 * @param key B2R_Menu key for preference
	 * @return boolean flag indicating whether preference is set
	 */
	static public Boolean isSet( B2R_Menu key)
	{
		return isSet( key.name());
	}

	// Gets

	/**
	 * Get the preference value as String for specified key.
	 * @param key B2R_Setting key for preference
	 * @return String value to put into preferences
	 */
	static public String get( B2R_Setting key)
	{
		String value;
		try
		{
			value = sharedPref.getString( key.getKey(),
					key.getDefaultValidity() ? key.getDefaultValue() : null);
		}
		catch (Exception e)
		{
			// TODO: What should client do with this?
			value = null;	
		}
		return value;
	}

	/**
	 * Get the preference value as boolean flag for specified key.
	 * @param key B2R_Menu key for preference
	 * @return String value to put into preferences
	 */
	static public Boolean getBoolean( String key)
	{
		return sharedPref.getBoolean( key, false);
	}

	/**
	 * Get the preference value as boolean flag for specified key.
	 * @param key B2R_Menu key for preference
	 * @return String value to put into preferences
	 */
	static public Boolean getBoolean( B2R_Setting key)
	{
		try {
			return sharedPref.getBoolean( key.getKey(),
				key.getDefaultValidity() ? key.getDefaultBoolean() : null);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Get the preference value as boolean flag for specified key.
	 * @param key B2R_Menu key for preference
	 * @return String value to put into preferences
	 */
	static public Boolean getBoolean( B2R_Menu key)
	{
		return getBoolean( key.name());
	}
}
