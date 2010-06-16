/*
 * Created on 12.06.2010
 *
 */
package de.wertarbyte.chorddroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.res.Resources;

public class Instrument {
	
	private HashMap<String, List<Shape>> shape;
	
	public Instrument(Resources resources, int resourceID) throws IOException {
		shape = new HashMap<String, List<Shape>>();
		loadDatabase(resources, resourceID);
	}
	
	private void loadDatabase(Resources resources, int resourceID) throws IOException {
		shape.clear();
		// load chord shapes from resource
		InputStream is = resources.openRawResource(resourceID);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int n = 0;
		while ((line = br.readLine()) != null) {
			// ignore the first line, which contains the instrument name and its tuning
			if (n++ > 0) {
				String[] fields = line.split("[[:space:]]+");
				String chord = fields[0];
				// TODO for now, we ignore the base note (slash chords) 
				String key = chord.replaceFirst("/[A-G][b#]?$", "");
				
				if (! this.shape.containsKey(key)) {
					shape.put(key, new LinkedList<Shape>());
				}
				List<Shape> list = shape.get(key);
				
				// a chord might have multiple shapes
				for (int i = 1; i < fields.length; i++) {
					String shape = fields[i];
					Shape s = Shape.createShape(chord, shape);
					list.add(s);
				}
			}
		}
		is.close();
	}
	
	public List<Shape> lookup(String chord) {
		return shape.get(chord);
	}
}
