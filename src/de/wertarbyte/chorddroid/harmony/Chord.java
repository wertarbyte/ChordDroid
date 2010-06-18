/*
 * Created on 18.06.2010
 *
 */
package de.wertarbyte.chorddroid.harmony;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chord implements Polynote<Chord> {
	
	private Note root;
	private Set<Integer> offsets;
	private String name;
	
	private final static Pattern CHORD_RE = Pattern.compile("^([a-g][b]?)(|m|aug|dim|5|sus[24])(6|7)?(/([a-g][b]?))?$");
	
	private void addComponentOffsets(ChordComponent cp) {
		if (cp != null) {
			for (int o : cp.getOffsets()) {
				offsets.add(o);
			}
		}
	}
	
	private Chord(String name) throws InvalidChordException {
		Matcher m = CHORD_RE.matcher(name.toLowerCase());
		if (m.matches()) {
			this.name = name;
			root = Note.lookup(m.group(1));
			offsets = new HashSet<Integer>();
			String triad = m.group(2);
			String extra = m.group(3);
			String base = m.group(5);
			
			addComponentOffsets(ChordComponent.getByString(triad));
			addComponentOffsets(ChordComponent.getByString(extra));
			
			// add the additional base note
			if (base != null) {
				// transpose it down an octave
				Note baseNote = Note.lookup(base).transpose(-12);
				int difference = root.getHalfStepsTo(baseNote);
				offsets.add(difference);
			}
		} else {
			throw new InvalidChordException();
		}
	}
	
	public static Chord lookup(String name) {
		try {
			return new Chord(name);
		} catch (InvalidChordException e) {
			return null;
		}
	}
	
	public String getName() {
		return name;
	}

	public Set<Note> getNotes() {
		SortedSet<Note> result = new TreeSet<Note>();
		result.add(root);
		for (int i : offsets) {
			result.add( root.transpose(i) );
		}
		return result;
	}

	public Chord transpose(int steps) {
		Chord r = null;
		try {
			r = new Chord(name);
			// now we replace the root
			r.root = root.transpose(steps);
		} catch (InvalidChordException e) {
			e.printStackTrace();
		}
		return r;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass().equals(getClass())) {
			Chord other = (Chord) o;
			return other.getNotes().containsAll(getNotes()) && getNotes().containsAll(other.getNotes());
		}
		return super.equals(o);
	}
}
