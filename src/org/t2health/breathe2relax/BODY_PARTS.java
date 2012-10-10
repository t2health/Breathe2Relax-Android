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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.t2health.lib.R;

/**
 * This enum renders a data driven solution to the body geography.
 * 
 * @author jon.hulthen
 *
 */
public enum BODY_PARTS {
	BRAIN(  0, 			
			"brain", 		
			R.drawable.ibreathe_brain, 				
			R.id.ButtonBrain, 	
			R.drawable.button_brain, 	
			R.id.imageViewButtonGlowBrain, 		
			R.id.textViewBrain,		
			0.053D,
			B2R_Utility.BitmapOrder.BUTTON_BRAIN,
			B2R_Utility.BitmapOrder.BRAIN,
			0.03D,
			0.07D, 0, 0),
	EYES(   1, 			
			"eyes", 		
			R.drawable.ibreathe_eyes,				
			R.id.ButtonEyes, 	
			R.drawable.button_eyes, 	
			R.id.imageViewButtonGlowEyes, 		
			R.id.textViewEyes,		
			0.084D,
			B2R_Utility.BitmapOrder.BUTTON_EYES,
			B2R_Utility.BitmapOrder.EYES,
			0.08D,
			0.088D, 0, 0),
	EARS(   2, 			
			"ears", 		
			R.drawable.ibreathe_ears, 				
			R.id.ButtonEars, 	
			R.drawable.button_ears, 	
			R.id.imageViewButtonGlowEars, 		
			R.id.textViewEars,		
			0.103D,
			B2R_Utility.BitmapOrder.BUTTON_EARS,
			B2R_Utility.BitmapOrder.EARS,
			0.0954D,
			0.1146D, 0, 0),
	LUNGS(  3, 			
			"lungs", 		
			R.drawable.ibreathe_lungs, 				
			R.id.ButtonLungs, 	
			R.drawable.button_lungs, 	
			R.id.imageViewButtonGlowLungs, 		
			R.id.textViewLungs,		
			0.24D,
			B2R_Utility.BitmapOrder.BUTTON_LUNGS,
			B2R_Utility.BitmapOrder.LUNGS,
			0.197D,
			0.305D, 0, 0),
	STOMACH(4, 			
			"stomach", 		
			R.drawable.ibreathe_stomach, 			
			R.id.ButtonStomach, 
			R.drawable.button_stomach, 	
			R.id.imageViewButtonGlowStomach, 	
			R.id.textViewStomach,	
			0.35D,
			B2R_Utility.BitmapOrder.BUTTON_STOMACH,
			B2R_Utility.BitmapOrder.STOMACH,
			0.31D,
			0.387D, 0, 0),
	FINGERS(5, 			
			"fingers", 		
			R.drawable.ibreathe_fingers, 			
			R.id.ButtonFingers, 
			R.drawable.button_fingers,	
			R.id.imageViewButtonGlowFingers, 	
			R.id.textViewFingers,	
			0.495D,
			B2R_Utility.BitmapOrder.BUTTON_FINGERS,
			B2R_Utility.BitmapOrder.FINGERS,
			0.403D,
			0.537D,
			0.86D,
			0.98D),
	MUSCLES(6, 			
			"muscles", 		
			R.drawable.ibreathe_muscles, 			
			R.id.ButtonMuscles, 
			R.drawable.button_muscles, 	
			R.id.imageViewButtonGlowMuscles, 	
			R.id.textViewMuscles,	
			0.30D,
			B2R_Utility.BitmapOrder.BUTTON_MUSCLES,
			B2R_Utility.BitmapOrder.MUSCLES,
			0.169D,
			0.28D,
			0.5462D,
			0.684D),
	HEART(  7, 			
			"circulatory", 	
			R.drawable.ibreathe_circulatory, 		
			R.id.ButtonHeart, 	
			R.drawable.button_heart, 	
			R.id.imageViewButtonGlowHeart, 		
			R.id.textViewHeart,		
			0.31D,
			B2R_Utility.BitmapOrder.BUTTON_HEART,
			B2R_Utility.BitmapOrder.HEART,
			0.2535D,
			0.39D, 0, 0),
	IMMUNE_SYSTEM(8, 	
			"immunesystem", 
			R.drawable.ibreathe_body_immunesystem, 	
			R.id.ButtonImmune, 	
			R.drawable.button_immune, 	
			R.id.imageViewButtonGlowImmune, 	
			R.id.textViewImmune,	
			0.1433D,
			B2R_Utility.BitmapOrder.BUTTON_IMMUNE_SYSTEM,
			B2R_Utility.BitmapOrder.IMMUNE_SYSTEM,
			0.133D,
			0.177D, 0, 0),
	SKIN(   9, 			
			"skin",			
			R.drawable.ibreathe_skin, 				
			R.id.ButtonSkin, 	
			R.drawable.button_skin, 	
			R.id.imageViewButtonGlowSkin, 		
			R.id.textViewSkin,		
			0.43D,
			B2R_Utility.BitmapOrder.BUTTON_SKIN,
			B2R_Utility.BitmapOrder.SKIN,
			0.673D,
			0.83D, 0, 0),
	BODY(  10,			
			"body", 		
			R.drawable.ibreathe_body, 				
			0, 0, 0, 0,	0, B2R_Utility.BitmapOrder.NONE, B2R_Utility.BitmapOrder.BODY, 0, 0, 0, 0),
	MASK(  11,			
			"none", 		
			R.drawable.ibreathe_body_clip,			
			0, 0, 0, 0,	0, B2R_Utility.BitmapOrder.NONE, B2R_Utility.BitmapOrder.MASK, 0, 0, 0, 0),
	NONE(  12,			
			"none", 		
			0, 0, 0, 0, 0, 0, B2R_Utility.BitmapOrder.NONE, B2R_Utility.BitmapOrder.NONE, 0, 0, 0, 0);

	private String text;
	private int order;
	private int bitmapInt;
	private int buttonInt;
	private int buttondrawableInt;
	private int buttonglowInt;
	private int textInt;
	private double organCenter;
	private B2R_Utility.BitmapOrder buttonBitmapOrder;
	private B2R_Utility.BitmapOrder bitmapOrder;
	private double primaryRangeStart;
	private double primaryRangeEnd;
	private double secondaryRangeStart;
	private double secondaryRangeEnd;
	
	public static final int BODYPARTSCARDINALITY = 13;
	
	private  double    means = 0F;
	private  int      alphas = 0;
	
	BODY_PARTS(int order, String text, int bitmapInt, int buttonInt, int buttondrawableInt, int buttonglowInt, 
			int textInt, double organCenter, B2R_Utility.BitmapOrder buttonBitmapOrder, B2R_Utility.BitmapOrder bitmapOrder,
			double primaryRangeStart, double primaryRangeEnd, double secondaryRangeStart, double secondaryRangeEnd) {
		this.text = text;
		this.order = order;
		this.bitmapInt = bitmapInt;
		this.buttonInt = buttonInt;
		this.buttondrawableInt = buttondrawableInt;
		this.buttonglowInt = buttonglowInt;
		this.textInt = textInt;
		this.organCenter = organCenter;
		this.buttonBitmapOrder = buttonBitmapOrder;
		this.bitmapOrder = bitmapOrder;
		this.primaryRangeStart = primaryRangeStart;
		this.primaryRangeEnd = primaryRangeEnd;
		this.secondaryRangeStart = secondaryRangeStart;
		this.secondaryRangeEnd = secondaryRangeEnd;
	}

	public double getPrimaryRangeStart() {
		return primaryRangeStart;
	}
	
	public double getPrimaryRangeEnd() {
		return primaryRangeEnd;
	}
	
	public double getSecondaryRangeStart() {
		return secondaryRangeStart;
	}
	
	public double getSecondaryRangeSEnd() {
		return secondaryRangeEnd;
	}
	
	public String getText() {
		return text;
	}
	public int getInt() {
		return order;
	}
	
	public int getBitmapInt() {
		return bitmapInt;
	}

	public int getButtonInt() {
		return buttonInt;
	}
	
	public int getButtonDrawableInt() {
		return buttondrawableInt;
	}
	
	public int getButtonglowInt() {
		return buttonglowInt;
	}
	
	public int getTextInt() {
		return textInt;
	}
	
	public double getOrganCenter() {
		return organCenter;
	}
	
	public B2R_Utility.BitmapOrder getButtonBitmapOrder() {
		return buttonBitmapOrder;
	}
	
	public B2R_Utility.BitmapOrder getBitmapOrder() {
		return bitmapOrder;
	}
	
	public static BODY_PARTS getBodyPart(int i) {
		if (i >= 0 && i <= 11) {
			for (BODY_PARTS bp: BODY_PARTS.values()) {
				if (bp.getInt() == i) return bp;
			}
		}
		return BODY_PARTS.NONE;
	}
	
	@SuppressWarnings("all")
	public static  List<BODY_PARTS> grabBodypart(double f) {
		List<BODY_PARTS> list = new ArrayList<BODY_PARTS> ();
		
		for (BODY_PARTS bp : BODY_PARTS.values()) {
			bp.alphas = 0;
			bp.means = 0;
			
			if (validBodypart(bp)) {
				double mean = 0;
				double rel_dist = 0.0;
				
				if (f >= bp.primaryRangeStart && f <= bp.primaryRangeEnd) {
					mean = (bp.primaryRangeStart + bp.primaryRangeEnd)/2D;
				} else  if (f >= bp.secondaryRangeStart && f <= bp.secondaryRangeEnd ) {
					mean = (bp.secondaryRangeStart + bp.secondaryRangeEnd)/2D;
				} else continue;
				
				bp.means = mean;
				if (bp.means == 0D) continue;
				
				if ( f > mean) rel_dist = 2*(f - mean)/mean;
				else rel_dist = 2*(mean - f)/mean;
					
				bp.alphas = (int) (255 * (1 - rel_dist)*(1 - rel_dist));
				if (bp.alphas == 0D) continue;
				
				list.add(bp);
			}
		}
		
		if (!list.isEmpty()) {
			Collections.sort(list, new Comparator(){
				public int compare(Object o1, Object o2) {
					BODY_PARTS p1 = (BODY_PARTS) o1;
					BODY_PARTS p2 = (BODY_PARTS) o2;
            	
					if (p1.alphas > p2.alphas) return 1;
					else if (p1.alphas < p2.alphas) return -1;
					return 0;
				}
 
			});
		} else return null;
		
		
		return list;
	}
	
	public int getAlpha() {
		if (validBodypart(this))
			return this.alphas;
		else
			return 0;
	}
	
	public static boolean validBodypart(BODY_PARTS bp) {
		return (!bp.equals(BODY_PARTS.NONE) && !bp.equals(BODY_PARTS.BODY) && !bp.equals(BODY_PARTS.MASK));
	}
}
