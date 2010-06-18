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
	
	private final static Pattern CHORD_RE = Pattern.compile("^([a-gA-G][b]?)(|m|aug|dim|5)(6|7)?(/([a-gA-G][b]?))?$");
	
	private Chord(String name) throws InvalidChordException {
		Matcher m = CHORD_RE.matcher(name);
		if (m.matches()) {
			this.name = name;
			root = Note.lookup(m.group(1));
			offsets = new HashSet<Integer>();
			String scale = m.group(2);
			String extra = m.group(3);
			if ("".equals(scale)) {
				offsets.add( Scale.MAJOR.getHalfSteps(3) );
				offsets.add( Scale.MAJOR.getHalfSteps(5) );
				if ("7".equals(extra)) {
					offsets.add( Scale.MAJOR.getHalfSteps(7) );
				} else if ("6".equals(extra)) {
					offsets.add( Scale.MAJOR.getHalfSteps(6) );
				}
			} else if ("m".equals(scale)) {
				offsets.add( Scale.MINOR.getHalfSteps(3) );
				offsets.add( Scale.MINOR.getHalfSteps(5) );
				if ("7".equals(extra)) {
					offsets.add( Scale.MINOR.getHalfSteps(7) );
				} else if ("6".equals(extra)) {
					offsets.add( Scale.MINOR.getHalfSteps(6) );
				}
			} else if ("aug".equals(scale)) {
				offsets.add(4);
				offsets.add(8);
			} else if ("dim".equals(scale)) {
				offsets.add(3);
				offsets.add(6);				
			} else if ("5".equals(scale)) {
				offsets.add( Scale.MAJOR.getHalfSteps(5) );
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
