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

public class Instrument {
	
	private String name;
	private AssetManager assets;

	public Instrument(AssetManager assets, String name) throws IOException {
		this.name = name;
		this.assets = assets;
	}
	
	private List<Shape> loadDatabase(String name, String chord) throws IOException {
		List<Shape> result = new LinkedList<Shape>();
		// load chord shapes from resource
		InputStream is = assets.open("chords/"+name+"/"+chord);
		if (is == null) {
			return result;
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int n = 0;
		while ((line = br.readLine()) != null) {
			// ignore the first line, which contains the instrument name and its tuning
			if (n++ > 0) {
				String[] fields = line.split("[[:space:]]+");
				String realchord = fields[0];
				// TODO for now, we ignore the base note (slash chords) 
				String key = chord.replaceFirst("/[A-G][b#]?$", "");
				
				if (key.equals(chord)) {
					// a chord might have multiple shapes
					for (int i = 1; i < fields.length; i++) {
						String shape = fields[i];
						Shape s = Shape.createShape(realchord, shape);
						result.add(s);
					}
				}
			}
		}
		is.close();
		return result;
	}
	
	public List<Shape> lookup(String chord) {
		try {
			return loadDatabase(name, chord);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new LinkedList<Shape>();
	}
}
