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


public enum B2R_Setting {
	MOTIF_SELECTED("none", false, Settings.settings_background_key),
	BACKGROUND_MUSIC_SELECTED("none", false, Settings.settings_background_music_key),
	INHALE_LENGTH("none", false, Settings.settings_inhale_length_key),
	EXHALE_LENGTH("none", false, Settings.settings_exhale_length_key),
	METRONOME("On", true, Settings.settings_metronome_key),
	VISUAL_PROMPTS("On", true, Settings.settings_visual_prompt_key),
	AUDIO_PROMPTS("On", true, Settings.settings_audio_prompt_key),
	INSTRUCTIONS_PROMPTS("On", true, Settings.settings_breathing_instruction_key),
	PLAY_MUSIC("On", true, Settings.settings_play_music_key),
	CYCLE("none", false, Settings.settings_num_cycles_key),
	TRACK_STRESS("On", true, Settings.settings_track_stress_key),
	TRACK_STRESS_SKIP("Off", true),
	GUIDE_PROMPTS("Off", true, Settings.settings_guide_prompt_key),
	ANONYMOUS_DATA("On", true, Settings.settings_anon_data_key),
	RELAXED_STRESSED_BEFORE("none", false),
	RELAXED_STRESSED_AFTER("none", false),
	YOUTUBE_FALLBACK("Off", true, Settings.settings_youtube_key),
	NO_SETTINGS("none", false),
	PREVENT_SCREEN_TIMEOUT("Off", true, Settings.prevent_screen_timeout_key);

	private String defaultValue;
	private boolean defaultValidity;
	private String key;

	B2R_Setting(String defaultValue, boolean defaultValidity)
	{
		this.defaultValue = defaultValue;
		this.defaultValidity = defaultValidity;
	}

	B2R_Setting(String defaultValue, boolean defaultValidity, String key)
	{
		this.defaultValue = defaultValue;
		this.defaultValidity = defaultValidity;
		this.key = key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public Boolean getDefaultBoolean() {
		return defaultValue.equals("On");
	}

	public boolean getDefaultValidity() {
		return defaultValidity;
	}

	public String getKey() {
		return (key != null) ? key : name();
	}

	public static B2R_Setting fromString(String text) {
		if (text != null) {
			for (B2R_Setting b : B2R_Setting.values()) {
				if (text.equalsIgnoreCase(b.name())) {
					return b;
				}
			}
		}
		return B2R_Setting.NO_SETTINGS;
	}
}
