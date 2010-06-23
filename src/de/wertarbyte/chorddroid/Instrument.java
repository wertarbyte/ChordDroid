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
	private int transpositionSteps;
	
	public Instrument(AssetManager assets, String name) throws IOException {
		this.name = name;
		this.assets = assets;
		this.transpositionSteps = 0;
	}
	
	public int getTranspositionSteps() {
		return transpositionSteps;
	}
	
	public void setTranspositionSteps(int transpositionSteps) {
		this.transpositionSteps = transpositionSteps;
	}
	
	private List<Shape> searchDatabase(Chord chord) throws IOException {
		// we ignore the base note while searching
		Chord wanted = chord.slashless();
		
		// transpose the query chord according to our instrument setup
		Chord transposed = wanted.transpose(-1 * getTranspositionSteps());
		
		List<Shape> result = new LinkedList<Shape>();
		// load chord shapes from resource
                InputStream is = assets.open("chords/" + name + "/" + transposed.getName());
		
                if (is == null) {
			return result;
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] fields = line.split("[[:space:]]+");
			Chord key = Chord.lookup(fields[0]);
			
			// we compare the names to disregard the pitch level
			if (key.slashless().getName().equals(transposed.getName())) {
				// a chord might have multiple shapes
				for (int i = 1; i < fields.length; i++) {
					String shape = fields[i];
					// we store the transposed chord alongside the shape
					Shape s = Shape.createShape(key.transpose(getTranspositionSteps()), shape);
					result.add(s);
				}
			}
		}
		is.close();
		return result;
	}
	
	public List<Shape> lookup(Chord chord) {
		try {
			return searchDatabase(chord);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new LinkedList<Shape>();
	}
}
