/*
 * Created on 18.06.2010
 *
 */
package de.wertarbyte.chorddroid.harmony;

import java.util.Set;

public interface Polynote<X extends Transposable<X>> extends Transposable<X> {
	
	public Set<Note> getNotes();
	
	public X transpose(int steps);
	
}
