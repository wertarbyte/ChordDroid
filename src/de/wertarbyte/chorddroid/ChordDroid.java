package de.wertarbyte.chorddroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ChordDroid extends Activity implements OnItemSelectedListener {	
	private List<Instrument> instruments;
	
	private Spinner s_instrument;
	private Spinner s_root;
	private Spinner s_scale;
	private Spinner s_extra;
	
	private ChordView chordView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        instruments = new ArrayList<Instrument>(4);
        
		// load known instruments
        for (int id : new int[]{R.raw.guitar_standard, R.raw.ukulele_gcea}) {
			try {
				instruments.add(new Instrument(getResources(), id));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

       setContentView(R.layout.main);
       
       chordView = (ChordView) findViewById(R.id.chordview);
       
        s_instrument = (Spinner) findViewById(R.id.instrument);
        s_root = (Spinner) findViewById(R.id.root);
        s_scale = (Spinner) findViewById(R.id.scale);
        s_extra = (Spinner) findViewById(R.id.extra);
        
        s_instrument.setOnItemSelectedListener(this);
        s_root.setOnItemSelectedListener(this);
        s_scale.setOnItemSelectedListener(this);
        s_extra.setOnItemSelectedListener(this);
        
        // restore old bundle data
        setSpinner(s_instrument, savedInstanceState, "instrument");
        setSpinner(s_root, savedInstanceState, "root");
        setSpinner(s_scale, savedInstanceState, "scale");
        setSpinner(s_extra, savedInstanceState, "extra");
    }
    
    private void setSpinner(Spinner s, Bundle b, String key) {
    	if (b != null && b.containsKey(key)) {
    		int pos = b.getInt(key);
    		s.setSelection(pos);
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("instrument", s_instrument.getSelectedItemPosition());
    	outState.putInt("root", s_root.getSelectedItemPosition());
    	outState.putInt("scale", s_scale.getSelectedItemPosition());
    	outState.putInt("extra", s_extra.getSelectedItemPosition());
    }
    
    public String getSelectedChord() {
    	StringBuilder sb = new StringBuilder();
    	sb.append( s_root.getSelectedItem() );
    	sb.append( s_scale.getSelectedItem() );
    	sb.append( s_extra.getSelectedItem() );
    	return sb.toString();
    }
    
    public Instrument getSelectedInstrument() {
    	int pos = s_instrument.getSelectedItemPosition();
    	return instruments.get(pos);
    }

	public void onItemSelected(AdapterView<?> sender, View v, int pos, long id) {
		TextView t = (TextView) findViewById(R.id.chordname);
		
		String selChord = getSelectedChord();
		
		chordView.setShape(null);
		t.setText("-");
		List<Shape> shapes = getSelectedInstrument().lookup(selChord);
		// for now, we just use the first chord shape
		if (shapes != null && shapes.size() > 0) {
			chordView.setShape(shapes.get(0));
			t.setText(shapes.get(0).toString());
		}	
	}

	public void onNothingSelected(AdapterView<?> v) {}
    
}