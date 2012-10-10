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

public enum B2R_Menu {
		MAIN_MENU,
		SHOW_ME_HOW_MENU,
		BREATHE_MENU,
		SETUP_MENU,
		RESULTS_MENU,
		LEARN_MENU,
		ABOUT_MENU,
		START_BREATHING_SUBMENU,
		BEFORE_STARTING_SUBMENU,
		AFTER_STARTING_SUBMENU,
		RATE_STRESS_BEFORE,
		PERSONALIZE,
		NO_SETTINGS;

		public static B2R_Menu fromString(String text) {
			if (text != null) {
				for (B2R_Menu b : B2R_Menu.values()) {
					if (text.equalsIgnoreCase(b.name())) {
						return b;
					}
				}
			}
			return B2R_Menu.NO_SETTINGS;
		}
	}
