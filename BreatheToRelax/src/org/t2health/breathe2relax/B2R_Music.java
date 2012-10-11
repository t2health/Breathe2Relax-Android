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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum B2R_Music {
	AMBIENT_EVENINGS(R.raw.ambientevenings, "Ambient Evenings", "This soft, slow New Age track features resonant woodwind melodies and harmonies accompanied by birds singing and other gentle nature sound effects", "Music supplied by Getty Images"),
	EVO_SOLUTION(R.raw.evosolution, "Evosolution", "Poignant piano is accented by lingering strings and swelling keyboard effects in this slow, rich piece", "Music supplied by Getty Images"),
	OCEAN_MIST(R.raw.oceanmist, "Ocean Mist", "Seaside effects including seagulls and watery sounds offer a rich accompaniment to slow keyboard and woodwind melodies and harmonies in this soothing piece", "Music supplied by Getty Images"),
	WANING_MOMENTS(R.raw.waningmoments, "Waning Moments", "This romantic New Age track offers a lilting acoustic guitar melody accompanied by swelling ambient keyboard effects and an echoing bassline", "Music supplied by Getty Images"),
	WATER_MARKS(R.raw.watermark, "Water Mark", "Rich, echoing strings and light percussion sing this fluid melody, accompanied by stunning and resonant effects", "Music supplied by Getty Images"),
	RANDOM(0, "Random Music ", "Play randomly from list below", ""),
	NO_MUSIC(0, "null", "", "");

	private int id;
	private String text;
	private String description;
	private String credits;
	
	private static final Random chance = new Random();
	private static final List<B2R_Music> VALUES = Collections.unmodifiableList(Arrays.asList(B2R_Music.values()));
	private static final int SIZE = VALUES.size();


	B2R_Music(int id, String text, String description, String credits) {
		this.id = id;
		this.text = text;
		this.description = description;
		this.credits = credits;
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
	
	public String getCredits() {
		return this.credits;
	}

	public static B2R_Music fromString(String text) {
		if (text != null) {
			for (B2R_Music b : B2R_Music.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return B2R_Music.NO_MUSIC;
	}
	
	public static B2R_Music getRandom() {
		B2R_Music m = VALUES.get(chance.nextInt(SIZE));
		while (m.getId() == 0) {
			m = VALUES.get(chance.nextInt(SIZE));
		}
		
		return m;
	}
}
