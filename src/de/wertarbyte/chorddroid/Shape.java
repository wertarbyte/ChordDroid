/*
 * Created on 12.06.2010
 *
 */
package de.wertarbyte.chorddroid;

import de.wertarbyte.chorddroid.harmony.Chord;

public class Shape {
	private final static int MUTE = -1;
	
	private final int[] pos;
	private final Chord chord;
	
	public Shape(Chord chord, int... p) {
		pos = p;
		this.chord = chord;
	}
	
	public static Shape createShape(Chord chord, String shape) {
		String[] f = shape.split("-");
		int[] p = new int[f.length];
		for (int i = 0; i < f.length; i++) {
			String c = f[i];
			if ("x".equals(c.toLowerCase())) {
				p[i] = MUTE;
			} else {
				p[i] = Integer.parseInt(c);
			}
		}
		return new Shape(chord, p);
	}

	public int[] getPositions() {
		return pos;
	}
	
	public Chord getChord() {
		return chord;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(chord.getName());
		sb.append('\t');
		for (int i = 0; i < pos.length; i++) {
			if (pos[i] == MUTE) {
				sb.append('x');
			} else {
				sb.append(pos[i]);
			}
			if (i != pos.length-1) {
				sb.append('-');
			}
		}
		return sb.toString();
	}
	
	public int getMinPos() {
		int r=-2;
		for (int i = 0; i < pos.length; i++) {
			int p = pos[i];
			if (r == -2) {
				// the counter has not been touched yet, we accept any value
				r = p;
			} else {
				// ignore open (0) and muted (-1) strings
				if (p>0 && (r<1 || p < r)) {
					r = p;
				}
			}
		}
		return r;
	}
	public int getMaxPos() {
		int r=-2;
		for (int i = 0; i < pos.length; i++) {
			int p = pos[i];
			if (r == -2 || r < p) {
				r = p;
			}
		}
		return r;
	}
}
