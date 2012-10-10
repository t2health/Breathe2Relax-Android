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
