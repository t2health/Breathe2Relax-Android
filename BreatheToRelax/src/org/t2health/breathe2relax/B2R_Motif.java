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
