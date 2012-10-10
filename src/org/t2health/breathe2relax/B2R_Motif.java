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



public enum B2R_Motif {
	RAIN_FOREST(R.drawable.rainforestthumb, "Rain Forest", "Quiet Lush Forest"),
	MOUNTAIN_MEADOWS(R.drawable.meadowthumb, "Meadows", "Mountain Meadows"),
	COSMIC_PHOTOS(R.drawable.cosmosthumb, "Cosmos", "Cosmic Photos Courtesy NASA and (STSI)"),
	BEACHES(R.drawable.beachthumb, "Beaches", "Beaches Courtesy NOAA"),
	FLOWERS(R.drawable.flowerthumb, "Flowers", "Flowers courtesy www.turbophoto.com"),
	SUNSET(R.drawable.sunsetthumb, "Sunsets", "Peaceful Sunsets Courtesy NOAA"),
	NO_MOTIF(0, "none", "Blank background");

	private int id;
	private String text;
	private String description;

	B2R_Motif(int id, String text, String description) {
		this.id = id;
		this.text = text;
		this.description = description;
	}

	public int getId() {
		return this.id;
		
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getDescription() {
		return this.description;
	}

	public static B2R_Motif fromString(String text) {
		if (text != null) {
			for (B2R_Motif b : B2R_Motif.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return B2R_Motif.NO_MOTIF;
	}

}
