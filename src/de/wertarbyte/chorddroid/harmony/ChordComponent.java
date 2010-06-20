/*
 * Created on 18.06.2010
 *
 */
package de.wertarbyte.chorddroid.harmony;

import java.util.HashMap;

public class ChordComponent {
	public static final ChordComponent MAJOR = new ChordComponent("", stepsToOffsets(Scale.MAJOR, new int[] {1, 3, 5}));
	public static final ChordComponent MINOR = new ChordComponent("m", stepsToOffsets(Scale.MINOR, new int[] {1, 3, 5}));
	public static final ChordComponent SUS4 = new ChordComponent("sus4", stepsToOffsets(Scale.MAJOR, new int[] {1, 4, 5}));
	public static final ChordComponent SUS2 = new ChordComponent("sus2", stepsToOffsets(Scale.MAJOR, new int[] {1, 2, 5}));
	public static final ChordComponent POWER = new ChordComponent( "5", stepsToOffsets(Scale.MAJOR, new int[] {1, 5}));
	public static final ChordComponent AUGMENTED = new ChordComponent("aug", new int[] {1, 4, 8});
	public static final ChordComponent DIMINISHED = new ChordComponent("dim", new int[] {1, 3, 6});
	public static final ChordComponent DOM_SEVENTH = new ChordComponent("7", stepsToOffsets(Scale.MINOR, new int[] {7}));
	public static final ChordComponent SIXTH = new ChordComponent("6", stepsToOffsets(Scale.MAJOR, new int[] {6}));
	
	private static final HashMap<String, ChordComponent> comps = new HashMap<String, ChordComponent>();

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
	private String label;
	
	private ChordComponent(String label, Scale scale, int... steps) {
		this(label, scaleStepsToHalfSteps(scale, steps));
	}
	
	private static int[] scaleStepsToHalfSteps(Scale scale, int...steps) {
		int[] offsets = new int[steps.length];
		for (int i=0; i<steps.length; i++) {
			offsets[i] = scale.getHalfSteps(steps[i]);
		}
		return offsets;
	}
	
	private ChordComponent(String label, int... offsets) {
		this.label = label;
		this.offsets = offsets;
		
		// put new component into lookup table
		comps.put(label, this);
	}
	
	public int[] getOffsets() {
		return offsets;
	}
	
	public String getLabel() {
		return label;
	}	
}
