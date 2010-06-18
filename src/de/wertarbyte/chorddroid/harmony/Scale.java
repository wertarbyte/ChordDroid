/*
 * Created on 18.06.2010
 *
 */
package de.wertarbyte.chorddroid.harmony;


public class Scale {
	
	public final static Scale MAJOR = new Scale(new int[]{0, 2, 4, 5, 7, 9, 11});
	public final static Scale MINOR = new Scale(new int[]{0, 2, 3,  5, 7, 8, 10});
	
	private int[] offsets;

	private Scale(int... offsets) {
		this.offsets = offsets;
	}
	
	public Note getStep(Note root, int steps) {
		return root.transpose( getHalfSteps(steps) );
	}
	
	public int getHalfSteps(int step) {
		return offsets[ (step-1)%offsets.length ];
	}
	
	public int size() {
		return offsets.length;
	}
}
