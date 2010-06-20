/*
 * Created on 18.06.2010
 *
 */
package de.wertarbyte.chorddroid.harmony;

import java.util.HashMap;

public class ChordComponent implements Comparable<ChordComponent>{
	private static final HashMap<String, ChordComponent> comps = new HashMap<String, ChordComponent>();
	
	public static final ChordComponent MAJOR = new ChordComponent("", 0, stepsToOffsets(Scale.MAJOR, new int[] {1, 3, 5}));
	public static final ChordComponent MINOR = new ChordComponent("m", 0, stepsToOffsets(Scale.MINOR, new int[] {1, 3, 5}));
	public static final ChordComponent SUS4 = new ChordComponent("sus4", 200, stepsToOffsets(Scale.MAJOR, new int[] {1, 4, 5}));
	public static final ChordComponent SUS2 = new ChordComponent("sus2", 200, stepsToOffsets(Scale.MAJOR, new int[] {1, 2, 5}));
	public static final ChordComponent POWER = new ChordComponent( "5", 0, stepsToOffsets(Scale.MAJOR, new int[] {1, 5}));
	public static final ChordComponent AUGMENTED = new ChordComponent("aug", 200, new int[] {1, 4, 8});
	public static final ChordComponent DIMINISHED = new ChordComponent("dim", 50, new int[] {1, 3, 6});
	public static final ChordComponent DOM_SEVENTH = new ChordComponent("7", 100, stepsToOffsets(Scale.MINOR, new int[] {7}));
	public static final ChordComponent MAJ_SEVENTH = new ChordComponent("maj7", 100, stepsToOffsets(Scale.MAJOR, new int[] {7}));
	public static final ChordComponent SIXTH = new ChordComponent("6", 100, stepsToOffsets(Scale.MAJOR, new int[] {6}));

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

	private int order;
	
	private ChordComponent(String label, int order, Scale scale, int... steps) {
		this(label, order, scaleStepsToHalfSteps(scale, steps));
	}
	
	private static int[] scaleStepsToHalfSteps(Scale scale, int...steps) {
		int[] offsets = new int[steps.length];
		for (int i=0; i<steps.length; i++) {
			offsets[i] = scale.getHalfSteps(steps[i]);
		}
		return offsets;
	}
	
	private ChordComponent(String label, int order, int... offsets) {
		this.label = label;
		this.offsets = offsets;
		this.order = order; 
		
		// put new component into lookup table
		comps.put(label, this);
	}
	
	public int[] getOffsets() {
		return offsets;
	}
	
	public String getLabel() {
		return label;
	}

	public int compareTo(ChordComponent other) {
		return new Integer(order).compareTo(other.order);
	}
	
}
