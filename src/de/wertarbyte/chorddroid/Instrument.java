/*
 * Created on 12.06.2010
 *
 */
package de.wertarbyte.chorddroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import android.content.res.AssetManager;
import de.wertarbyte.chorddroid.harmony.Chord;

public class Instrument {
	
	private String name;
	private AssetManager assets;

	public Instrument(AssetManager assets, String name) throws IOException {
		this.name = name;
		this.assets = assets;
	}
	
	private List<Shape> loadDatabase(Chord chord) throws IOException {
		// we ignore the base note while searching
		Chord wanted = chord.slashless();
		
		List<Shape> result = new LinkedList<Shape>();
		// load chord shapes from resource
		InputStream is = assets.open("chords/"+name+"/"+wanted.getName());
		if (is == null) {
			return result;
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] fields = line.split("[[:space:]]+");
			Chord key = Chord.lookup(fields[0]);
				
			if (key.slashless().equals(wanted)) {
				// a chord might have multiple shapes
				for (int i = 1; i < fields.length; i++) {
					String shape = fields[i];
					Shape s = Shape.createShape(key.getName(), shape);
					result.add(s);
				}
			}
		}
		is.close();
		return result;
	}
	
	public List<Shape> lookup(Chord chord) {
		try {
			return loadDatabase(chord);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new LinkedList<Shape>();
	}
}
