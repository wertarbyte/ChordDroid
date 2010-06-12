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

public class ChordDB {
	
	private HashMap<String, List<Shape>> shape;
	
	public ChordDB() {
		shape = new HashMap<String, List<Shape>>();
	}
	
	public void loadDatabase(Resources resources, int resourceID) throws IOException {
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
				
				if (! this.shape.containsKey(fields[0])) {
					shape.put(fields[0], new LinkedList<Shape>());
				}
				List<Shape> list = shape.get(fields[0]);
				
				// a chord might have multiple shapes
				for (int i = 1; i < fields.length; i++) {
					String shape = fields[i];
					Shape s = Shape.createShape(shape);
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
