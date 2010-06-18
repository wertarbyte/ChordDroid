/*
 * Created on 18.06.2010
 *
 */
package de.wertarbyte.chorddroid.harmony;



public class Note implements Transposable<Note> {
	private int pitch;
	
	private static final String[] NAMES = new String[] {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };
	
	private Note(int pitch) {
		this.pitch = pitch;
	}
	
	public static Note lookup(String name) {
		int p = 0;
		for (String s : NAMES) {
			if (s.equals(name)) {
				return new Note(p);
			}
			p++;
		}
		return null;
	}
	
	private int getNormalizedPitch() {
		int i = pitch%NAMES.length;
		// Java uses the other modulo operator (not the one used by python), so we have to handle negative values ourself
		while (i < 0) {
			i += NAMES.length;
		}
		return i;
	}
	
	public String getName() {
		int i = getNormalizedPitch();
		return NAMES[i];
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass().equals(getClass())) {
			// now we are on common ground
			Note other = (Note) o;
			return getNormalizedPitch() == other.getNormalizedPitch();
		}
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		return pitch;
	}

	public Note transpose(int steps) {
		return new Note(pitch+steps);
	}
	
}
