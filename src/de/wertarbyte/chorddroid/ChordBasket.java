/*
 * Created on 23.06.2010
 *
 */
package de.wertarbyte.chorddroid;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.wertarbyte.chorddroid.harmony.Chord;

public class ChordBasket {
	
	private List<Chord> chords;
	
	private ArrayAdapter<Chord> adapter;
	
	public ChordBasket(final Context context, final ChordDroid cd) {
		this.chords = new LinkedList<Chord>();
		
		this.adapter = new ArrayAdapter<Chord>(context, R.layout.chorditem, chords) {
			public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
				View v = convertView;
				if (v == null) {
					LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = li.inflate(R.layout.chorditem, null);
				}
				TextView tv = (TextView) v.findViewById(R.id.text);
				tv.setText(chords.get(position).getName());
				ChordView cv = (ChordView) v.findViewById(R.id.shape);
				cv.setShape(cd.getSelectedInstrument().lookup(chords.get(position)).get(0));
				
				return v;
			}
		};
	}
	
	public ArrayAdapter<Chord> getAdapter() {
		return adapter;
	}
	
	public void addChord(Chord c) {
		Iterator<Chord> it = chords.iterator();
		while (it.hasNext()) {
			if (it.next().equals(c)) {
				// by removing this chord, we move it to the end
				it.remove();
				break;
			}
		}
		chords.add(c);
		adapter.notifyDataSetChanged();
	}
	
	public void removeChord(int i) {
		chords.remove(i);
		adapter.notifyDataSetChanged();
	}
	
	public void removeChord(Chord c) {
		Iterator<Chord> it = chords.iterator();
		while (it.hasNext()) {
			if (it.next().equals(c)) {
				it.remove();
				return;
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	public void clear() {
		chords.clear();
		adapter.notifyDataSetChanged();
	}
	
	public String[] asStringArray() {
		String[] result = new String[chords.size()];
		int i = 0;
		for (Chord c : chords) {
			result[i++] = c.getName();
		}
		return result;
	}
	
	public void fromStringArray(String[] input) {
		for (String s : input) {
			Chord c = Chord.lookup(s);
			if (c != null) {
				addChord(c);
			}
		}
	}
}
