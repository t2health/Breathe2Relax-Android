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
import java.util.Random;

import org.t2health.lib.R;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * B2R_VoiceInstructions
 * 
 * This Type contains the logic for sequencing voice fragments together to various
 * sentences based on the duration allocated per one inhale/exhale.
 * 
 * The SoundPool handles the individual voice segments asynchronously.
 * Therefore, the length of a voice slice has to complete before the next
 * word/sentence commences.
 * 
 * This ENUM is instantiated with the following:
 * SOUND_ID		- used to pause/resume the SoundPool
 * RESOURCE_ID	- the mp3 file name (placed in the res/raw/ directory)
 * DURATION		- the time of the utterance piece.
 * 
 * @author jon.hulthen
 *
 */

public enum B2R_VoiceInstructions {
	INHALE_DEEP_1(1, R.raw.f_in_deep_1, 3100),
	INHALE_DEEP_2(2, R.raw.f_in_deep_2, 2800),
	INHALE_DEEP_3(3, R.raw.f_in_deep_3, 2500),
	INHALE_DEEP_4(4, R.raw.f_in_deep_4, 2500),
	INHALE_FOCUS(5, R.raw.f_focus_breathing, 2200),
	INHALE_SMOOTH(6, R.raw.f_smooth_easy, 1200),
	INHALE_ONE_SECOND(7, R.raw.f_1_2, 600),
	INHALE_TWO_SECOND(8, R.raw.f_2_2, 600),
	INHALE_THREE_SECOND(9, R.raw.f_3_2, 600),
	INHALE_FOUR_SECOND(10, R.raw.f_4_2, 600),
	INHALE_FIVE_SECOND(11, R.raw.f_5_2, 600),
	INHALE_SIX_SECOND(12, R.raw.f_6_2, 600),
	INHALE_SEVEN_SECOND(13, R.raw.f_7_2, 600),
	INHALE_EIGHT_SECOND(14, R.raw.f_8_2, 600),
	INHALE_EXPAND(15, R.raw.f_in_expand, 2200),
	THINK_A_NUMBER(16, R.raw.f_think_number, 1800),
	COUNT_BACKWARD(17, R.raw.f_count_backward, 2300),

	EXHALE_OUT_SLOWLY_1(21, R.raw.f_out_slow_1, 2400),
	EXHALE_OUT_SLOWLY_2(22, R.raw.f_out_slow_2, 2000),
	EXHALE_OUT_SLOWLY_3(23, R.raw.f_out_slow_3, 2000),
	EXHALE_OUT_SLOWLY_4(24, R.raw.f_out_slow_4, 2000),
	EXHALE_RELAX_ONE(25, R.raw.f_relax_1, 600),
	EXHALE_RELAX_TWO(26, R.raw.f_relax_2, 1100),
	EXHALE_RELAX_THREE(27, R.raw.f_relax_3, 600),
	EXHALE_RELAX_FOUR(28, R.raw.f_relax_4, 600),
	EXHALE_DEFLATE(29, R.raw.f_out_deflate, 3200),
	PAUSE_NATURALLY(30, R.raw.f_pause_naturally, 1500),

	NO_VOICE_INSTRUCTIONS(0, 0, 0);

	private int soundId;
	private int resourceId;
	private int duration;
	private int tail;
	
	private static  List<CountDownTimer> clocks = new ArrayList<CountDownTimer>();
	private static List<Integer> inhaleList = new ArrayList<Integer>();
	private static List<Integer> exhaleList = new ArrayList<Integer>();
	private static final int FROM_EOT_RELAX = 400;
	private static final int FROM_EOT_PAUSE = 100;
	
	B2R_VoiceInstructions(int soundId, int resourceId, int duration) {
		this.soundId = soundId;
		this.resourceId = resourceId;
		this.duration = duration;
	}

	public int getSoundId() {
		return this.soundId;
	}
	public int getResourceId() {
		return this.resourceId;
	}
	public int getDuration() {
		return this.duration;
	}
	public int getTail() {
		return this.tail;
	}
	public void setTail(int tail) {
		this.tail = tail;
	}

	public static B2R_VoiceInstructions fromString(String text) {
		if (text != null) {
			for (B2R_VoiceInstructions b : B2R_VoiceInstructions.values()) {
				if (text.equalsIgnoreCase(b.name())) {
					return b;
				}
			}
		}
		return B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS;
	}

	private static volatile boolean isStopped = false;
	
	public static void pause() {
		isStopped = true;
		for (CountDownTimer t : clocks) {
			t.cancel();
		}
		clocks.clear();
	}
	public static void resume() {
		isStopped = false;
	}
	
	// Generates a timelist with compiled voice segments from dispatches
	public static void talkforExhale(int timeLeft, int currentCycle, final int startTime, final int startCycles) {
		if (isStopped) return;
		List<B2R_VoiceInstructions> list = new ArrayList<B2R_VoiceInstructions>();
		int total = 0;
		
		B2R_Utility.pauseShortTalkExhale();
		resume(); // flag only
		exhaleList.clear();

		switch (currentCycle) {
		case 1:
		case 2:	
			if (timeLeft > 2000) {
				list.add(B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_1);
				total += B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_1.getDuration();
			}

			if (timeLeft > 6000) {
				list.add(B2R_VoiceInstructions.EXHALE_DEFLATE);
				total += B2R_VoiceInstructions.EXHALE_DEFLATE.getDuration();
			} else {
				list.add(B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_3);
				total += B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_3.getDuration();
			}
			
			if (timeLeft > 3000) {
				list.add(B2R_VoiceInstructions.PAUSE_NATURALLY);
				total += B2R_VoiceInstructions.PAUSE_NATURALLY.getDuration();
			}
			
			break;
		default:
			if (timeLeft > 5000) {
				Random chance = new Random();
				int num = chance.nextInt(4); // RANDOM(1, 4);
				B2R_VoiceInstructions j = B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS;
				B2R_VoiceInstructions k = B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS;
				switch (num) {
				case 0: j = B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_1; total += B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_1.getDuration(); k = B2R_VoiceInstructions.EXHALE_RELAX_ONE; total += B2R_VoiceInstructions.EXHALE_RELAX_ONE.getDuration(); break;
				case 1: j = B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_2; total += B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_2.getDuration(); k = B2R_VoiceInstructions.EXHALE_RELAX_TWO; total += B2R_VoiceInstructions.EXHALE_RELAX_TWO.getDuration(); break;
				case 2: j = B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_3; total += B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_3.getDuration(); k = B2R_VoiceInstructions.EXHALE_RELAX_THREE; total += B2R_VoiceInstructions.EXHALE_RELAX_THREE.getDuration(); break;
				case 3: j = B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_4; total += B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_4.getDuration(); k = B2R_VoiceInstructions.EXHALE_RELAX_FOUR; total += B2R_VoiceInstructions.EXHALE_RELAX_FOUR.getDuration(); break;
				}
				if (!j.equals(B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS)) {
					list.add(j);
					list.add(k);
				}
			} else {
				Random chance = new Random();
				int num = chance.nextInt(2); // RANDOM(3, 4);
				B2R_VoiceInstructions j = B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS;
				B2R_VoiceInstructions k = B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS;
				switch (num) {
				case 0: j = B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_3; total += B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_3.getDuration(); k = B2R_VoiceInstructions.EXHALE_RELAX_THREE; k = B2R_VoiceInstructions.EXHALE_RELAX_THREE; total += B2R_VoiceInstructions.EXHALE_RELAX_THREE.getDuration(); break;
				case 1: j = B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_4; total += B2R_VoiceInstructions.EXHALE_OUT_SLOWLY_4.getDuration(); k = B2R_VoiceInstructions.EXHALE_RELAX_FOUR; k = B2R_VoiceInstructions.EXHALE_RELAX_THREE; total += B2R_VoiceInstructions.EXHALE_RELAX_FOUR.getDuration();break;
				}
				if (!j.equals(B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS)) {
					list.add(j);
					list.add(k);
				}
			}
		}
		if (isStopped) return;
		generateExhaleSounds(list, timeLeft, total);
	}

	// Pause/Resume
	public static List<Integer> getExhaleList() {
		return exhaleList;
	}

	// Generates a timelist with compiled voice segments from dispatches
	public static void talkforInhale(final int timeLeft, final int currentCycle, final int startTime, final int numberOfCycles) {
		if (isStopped) return;
		List<B2R_VoiceInstructions> list = new ArrayList<B2R_VoiceInstructions>();
		int total = 0;
		
		B2R_Utility.pauseShortTalkInhale(); 
		resume(); // flag only
		inhaleList.clear();

		switch (currentCycle) {
		case 1:
		case 2:
			if (timeLeft > 2000) {
				list.add(B2R_VoiceInstructions.INHALE_DEEP_1);
				total += B2R_VoiceInstructions.INHALE_DEEP_1.getDuration();
			}

			if (timeLeft > 7000) {
				list.add(B2R_VoiceInstructions.INHALE_EXPAND);
				total += B2R_VoiceInstructions.INHALE_EXPAND.getDuration();
			} else {
				list.add(B2R_VoiceInstructions.INHALE_DEEP_2);
				total += B2R_VoiceInstructions.INHALE_DEEP_2.getDuration();
			}
			
			break;

		default:
			if (timeLeft > 5000) {
				if (currentCycle - numberOfCycles/2 == 1) {
					list.add(B2R_VoiceInstructions.INHALE_DEEP_3);
					total += B2R_VoiceInstructions.INHALE_DEEP_3.getDuration();
				} else {
					Random chance = new Random();
					int num = chance.nextInt(4); // RANDOM(1, 4);
					B2R_VoiceInstructions i = B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS;
					switch (num) {
					case 0: i = B2R_VoiceInstructions.INHALE_DEEP_1; total += B2R_VoiceInstructions.INHALE_DEEP_1.getDuration();break;
					case 1: i = B2R_VoiceInstructions.INHALE_DEEP_2; total += B2R_VoiceInstructions.INHALE_DEEP_2.getDuration();break;
					case 2: i = B2R_VoiceInstructions.INHALE_DEEP_3; total += B2R_VoiceInstructions.INHALE_DEEP_3.getDuration();break;
					case 3: i = B2R_VoiceInstructions.INHALE_DEEP_4; total += B2R_VoiceInstructions.INHALE_DEEP_4.getDuration();break;
					}
					
					if (!i.equals(B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS)) {
						list.add(i);
					}
				}
			} else if (currentCycle - numberOfCycles/2 == 1){ 
				list.add(B2R_VoiceInstructions.INHALE_DEEP_3);
				total += B2R_VoiceInstructions.INHALE_DEEP_3.getDuration();
			} else {
				Random chance = new Random();
				int num = chance.nextInt(2); // RANDOM(3, 4);
				B2R_VoiceInstructions i = B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS;
				switch (num) {
				case 0: i = B2R_VoiceInstructions.INHALE_DEEP_3; total += B2R_VoiceInstructions.INHALE_DEEP_3.getDuration();break;
				case 1: i = B2R_VoiceInstructions.INHALE_DEEP_4; total += B2R_VoiceInstructions.INHALE_DEEP_4.getDuration();break;
				}

				if (!i.equals(B2R_VoiceInstructions.NO_VOICE_INSTRUCTIONS)) {
					list.add(i);
				}
			}

			// Changed logics to cover >> 8 cycles
			int half = (int)((numberOfCycles + 1)/2) + 1;
			int vc = (numberOfCycles + 1 - currentCycle);
		
			if (currentCycle <= numberOfCycles/2 && currentCycle <= 8) {
				if (currentCycle < 6 && timeLeft > 4000) {
						list.add(B2R_VoiceInstructions.THINK_A_NUMBER);
						total += B2R_VoiceInstructions.THINK_A_NUMBER.getDuration();
						B2R_VoiceInstructions vi = getVoiceCount(currentCycle);
						list.add(vi);
						total += vi.getDuration();
				}
			} else if (vc > 0 && vc <= 8){
				if (currentCycle > 4 && timeLeft > 4000) {
					if (currentCycle == half) {
						list.add(B2R_VoiceInstructions.COUNT_BACKWARD);
						total += B2R_VoiceInstructions.COUNT_BACKWARD.getDuration();
					}
						
					B2R_VoiceInstructions vi = getVoiceCount(vc);
					list.add(vi);
					total += vi.getDuration();
				}
			}
			
			break;
		}
		
		if (isStopped) return;
		generateInhaleSounds(list, timeLeft, total);
	}
	
	private static B2R_VoiceInstructions getVoiceCount(int numCycles) {
		switch (numCycles) {
		case 0: return B2R_VoiceInstructions.INHALE_ONE_SECOND;
		case 1: return B2R_VoiceInstructions.INHALE_ONE_SECOND;
		case 2: return B2R_VoiceInstructions.INHALE_TWO_SECOND;
		case 3: return B2R_VoiceInstructions.INHALE_THREE_SECOND;
		case 4: return B2R_VoiceInstructions.INHALE_FOUR_SECOND;
		case 5: return B2R_VoiceInstructions.INHALE_FIVE_SECOND;
		case 6: return B2R_VoiceInstructions.INHALE_SIX_SECOND;
		case 7: return B2R_VoiceInstructions.INHALE_SEVEN_SECOND;
		case 8: return B2R_VoiceInstructions.INHALE_EIGHT_SECOND;
		}

		return NO_VOICE_INSTRUCTIONS;
	}
	
	// Generates voice sequence
	protected static void generateInhaleSounds(final List<B2R_VoiceInstructions> list, int timeLeft, int total) {
		if (total == 0) return;
		boolean starving = (total > timeLeft);
		boolean adjustmentFlag = false;
		
		if (starving) {
			Log.w("B2R_VoiceInstructions", "Starving: " + total + " vs. " + timeLeft);
		}
		
		int duration = 100;
		
		for (B2R_VoiceInstructions item : list) {
			if (isStopped) return;
			// Just uttering the numbers
			if (starving && (item.equals(B2R_VoiceInstructions.THINK_A_NUMBER) || item.equals(B2R_VoiceInstructions.COUNT_BACKWARD))) {
				continue;
			}
			
			if ((duration + item.getDuration()) > timeLeft) {
				Log.e("B2R_VoiceInstructions", "Too many voice instructions skipping this inhale time slice: "  + item.name() + ". Duration = " + (duration + item.getDuration()) + " vs. " + timeLeft + ". breaking here");
				continue;
			}
			
			if (item.equals(B2R_VoiceInstructions.COUNT_BACKWARD)) {
				adjustmentFlag = true;
			}
			
			final int soundId = item.getSoundId();
			if (adjustmentFlag && (soundId >= B2R_VoiceInstructions.INHALE_ONE_SECOND.getSoundId() && soundId <= B2R_VoiceInstructions.INHALE_EIGHT_SECOND.getSoundId())) {
				duration = duration + 200;
			}
			
			if (isStopped) {
				for (CountDownTimer t : clocks) {
					t.cancel();
				}
				clocks.clear();
				
				return;
			}
			
			CountDownTimer timer = new CountDownTimer(duration , duration) {
				public void onTick(long millisUntilFinished) {
				}

				public void onFinish() {
					if (isStopped == false) {
						final int k = B2R_Utility.playShortTalkInhale(soundId);
						if (k > 0) inhaleList.add(k);
					}
					clocks.remove(this);
				}
			};
			
			clocks.add(timer);
			timer.start();
			duration = duration + item.getDuration();
		}
	}
	
	// Generates voice sequence
	protected static void generateExhaleSounds(final List<B2R_VoiceInstructions> list, int timeLeft, int total) {
		if (isStopped) return;
		if (total == 0) return;
		boolean starving = (total > timeLeft);
		
		if (starving) {
			Log.w("B2R_VoiceInstructions", "Starving: " + total + " vs. " + timeLeft);
		}
		
		CountDownTimer timer = null;
		int duration = 100;
		
		for (B2R_VoiceInstructions item : list) {
			if ((duration + item.getDuration() - 200) > timeLeft) {
				Log.e("B2R_VoiceInstructions", "Too many voice instructions skipping this exhale time slice: " + item.name() + ". Duration = " + (duration + item.getDuration()) + " vs. " + timeLeft + ". breaking here");
				continue;
			}
			final int soundId = item.getSoundId();
			
			if (isStopped) {
				for (CountDownTimer t : clocks) {
					t.cancel();
				}
				clocks.clear();
				
				return;
			}
			
			// if RELAX
			if (soundId >= B2R_VoiceInstructions.EXHALE_RELAX_ONE.getSoundId() && soundId <= B2R_VoiceInstructions.EXHALE_RELAX_FOUR.getSoundId()) {
				// Assuming the last in list, put at end minus 2 seconds ....
				int dur = timeLeft - item.getDuration() - FROM_EOT_RELAX;
				int durr = duration;
				if (dur > durr) {
					durr = dur;
				}
				timer = new CountDownTimer(durr , durr) {
					public void onTick(long millisUntilFinished) {
					}

					public void onFinish() {
						if (isStopped == false) {
							final int k = B2R_Utility.playShortTalkExhale(soundId);
							if (k > 0) exhaleList.add(k);
						}
						clocks.remove(this);
					}
				};
				
			} else if (item.equals(B2R_VoiceInstructions.PAUSE_NATURALLY)) {
				// PAUSE
				// Assuming the last in list, put at end minus 2 seconds ....
				int dur = timeLeft - item.getDuration() - FROM_EOT_PAUSE;
				int durr = duration;
				if (dur > durr) {
					durr = dur;
				}
				timer = new CountDownTimer(durr , durr) {
					public void onTick(long millisUntilFinished) {
					}

					public void onFinish() {
						if (isStopped == false) {
							final int k = B2R_Utility.playShortTalkExhale(soundId);
							if (k > 0) exhaleList.add(k);
						}
						clocks.remove(this);
					}
				};
				
			} else {
				timer = new CountDownTimer(duration , duration) {
					public void onTick(long millisUntilFinished) {
					}

					public void onFinish() {
						if (isStopped == false) {
							final int k = B2R_Utility.playShortTalkExhale(soundId);
							if (k > 0) exhaleList.add(k);
						}
						clocks.remove(this);
					}
				};
				
			}
			clocks.add(timer);
			timer.start();
			duration = duration + item.getDuration();
		}
	}

	// Pause/Resume
	public static List<Integer> getInhaleList() {
		return inhaleList;
	}
}
