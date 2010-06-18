/*
 * Created on 18.06.2010
 *
 */
package de.wertarbyte.chorddroid.harmony;

import java.util.HashMap;

public class ChordComponent {
	public static final ChordComponent MAJOR = new ChordComponent( stepsToOffsets(Scale.MAJOR, new int[] {1, 3, 5}));
	public static final ChordComponent MINOR = new ChordComponent(stepsToOffsets(Scale.MINOR, new int[] {1, 3, 5}));
	public static final ChordComponent SUS4= new ChordComponent(stepsToOffsets(Scale.MAJOR, new int[] {1, 4, 5}));
	public static final ChordComponent SUS2= new ChordComponent(stepsToOffsets(Scale.MAJOR, new int[] {1, 2, 5}));
	public static final ChordComponent POWER= new ChordComponent(stepsToOffsets(Scale.MAJOR, new int[] {1, 5}));
	public static final ChordComponent AUGMENTED = new ChordComponent(new int[] {1, 4, 8});
	public static final ChordComponent DIMINISHED = new ChordComponent(new int[] {1, 3, 6});
	public static final ChordComponent DOM_SEVENTH = new ChordComponent(stepsToOffsets(Scale.MINOR, new int[] {7}));
	public static final ChordComponent SIXTH = new ChordComponent( stepsToOffsets(Scale.MAJOR, new int[] {6}));
	
	private static final HashMap<String, ChordComponent> comps = new HashMap<String, ChordComponent>();
	static {
		comps.put("", MAJOR);
		comps.put("m", MINOR);
		comps.put("sus4", SUS4);
		comps.put("sus2", SUS2);
		comps.put("5", POWER);
		comps.put("aug", AUGMENTED);
		comps.put("dim", DIMINISHED);
		comps.put("7", DOM_SEVENTH);
		comps.put("6", SIXTH);
	}

	private static int[] stepsToOffsets(Scale s, int...steps) {
		int[] offsets = new int[steps.length];
		for (int i=0; i<steps.length; i++) {
			offsets[i] = s.getHalfSteps(steps[i]);
		}
		return offsets;
	}
	
	public static ChordComponent getByString(String s) {
		return comps.get(s);
	}
	
	private int[] offsets;
	
	private ChordComponent(Scale scale, int... steps) {
		offsets = new int[steps.length];
		for (int i=0; i<steps.length; i++) {
			offsets[i] = scale.getHalfSteps(steps[i]);
		}
	}
	
	private ChordComponent(int... offsets) {
		this.offsets = offsets;
	}
	
	public int[] getOffsets() {
		return offsets;
	}


}
