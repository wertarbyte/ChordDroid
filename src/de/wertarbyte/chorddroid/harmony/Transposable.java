/*
 * Created on 18.06.2010
 *
 */
package de.wertarbyte.chorddroid.harmony;

public interface Transposable<E extends Transposable<E>> {
	public E transpose(int steps);
}
