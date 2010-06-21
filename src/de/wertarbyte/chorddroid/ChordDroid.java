package de.wertarbyte.chorddroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.wertarbyte.chorddroid.harmony.Chord;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ChordDroid extends Activity implements OnItemSelectedListener, OnClickListener {	
	private List<Instrument> instruments;
	
	private Spinner s_instrument;
	private Spinner s_root;
	private Spinner s_triad;
	private Spinner s_extra;
	private Spinner s_transposer;
	
	private ChordView chordView;
	private TextView t_variant;
	
	private int chord_variant;
	
	private List<Shape> currentShapes;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chord_variant = 0;
        currentShapes = new ArrayList<Shape>();
        
		// load known instruments
        String[] instr = getResources().getStringArray(R.array.instrument_dirs);
        
        instruments = new ArrayList<Instrument>(instr.length);
        
        for (int i = 0; i < instr.length; i++) {
			try {
				instruments.add(new Instrument(getAssets(), instr[i]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

       setContentView(R.layout.main);
       
       chordView = (ChordView) findViewById(R.id.chordview);
       
        s_instrument = (Spinner) findViewById(R.id.instrument);
        s_root = (Spinner) findViewById(R.id.root);
        s_triad = (Spinner) findViewById(R.id.triad);
        s_extra = (Spinner) findViewById(R.id.extra);
        s_transposer = (Spinner) findViewById(R.id.transposer);
        
        t_variant = (TextView) findViewById(R.id.variant);
        
        s_instrument.setOnItemSelectedListener(this);
        s_root.setOnItemSelectedListener(this);
        s_triad.setOnItemSelectedListener(this);
        s_extra.setOnItemSelectedListener(this);
        s_transposer.setOnItemSelectedListener(this);
        
        // restore old bundle data
        setSpinner(s_instrument, savedInstanceState, "instrument");
        setSpinner(s_root, savedInstanceState, "root");
        setSpinner(s_triad, savedInstanceState, "scale");
        setSpinner(s_extra, savedInstanceState, "extra");
        
        chordView.setClickable(true);
        chordView.setOnClickListener(this);
        
        registerForContextMenu(chordView);
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
    	outState.putInt("scale", s_triad.getSelectedItemPosition());
    	outState.putInt("extra", s_extra.getSelectedItemPosition());
    }
    
    public Chord getSelectedChord() {
    	StringBuilder sb = new StringBuilder();
    	sb.append( s_root.getSelectedItem() );
    	String triad = (String) s_triad.getSelectedItem();
    	// for some reason, the order of chord name components differs
    	if (Pattern.matches("^|m|dim$", triad)) {
    		sb.append( s_triad.getSelectedItem() );
    		sb.append( s_extra.getSelectedItem() );
    	} else {
    		sb.append( s_extra.getSelectedItem() );
      		sb.append( s_triad.getSelectedItem() );   		
    	}
    	return Chord.lookup(sb.toString()).transpose(getTranspositionSteps());
    }
    
    private int getTranspositionSteps() {
    	int i = s_transposer.getSelectedItemPosition();
    	int c = s_transposer.getCount();
    	if (i == 0) {
    		return 0;
    	} else {
    		return i-(c/2);
    	}
    }
    
    public Instrument getSelectedInstrument() {
    	int pos = s_instrument.getSelectedItemPosition();
    	return instruments.get(pos);
    }

	public void onItemSelected(AdapterView<?> sender, View v, int pos, long id) {
		chord_variant = 0;
		currentShapes = getSelectedInstrument().lookup(getSelectedChord());
		refresh_shape();
	}
	
	public List<Shape> getShapes() {
		return currentShapes;
	}
	
	public void refresh_shape() {
		chordView.setShape(null);
		t_variant.setText(getSelectedChord()+": -");
		List<Shape> shapes = getShapes();
		if (shapes != null && shapes.size() > 0) {
			Shape s = shapes.get(chord_variant);
			t_variant.setText(s.getChord().getName()+": "+(chord_variant+1)+"/"+shapes.size());
			chordView.setShape( shapes.get(chord_variant) );
		}
	}

	public void onNothingSelected(AdapterView<?> v) {}

	public void onClick(View src) {
		if (src == chordView) {
			// advance to next chord variant
			if (getShapes() != null) {
				chord_variant++;
				chord_variant %= getShapes().size();
				refresh_shape();
			}
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		List<Shape> shapes = getShapes();
		int i = 0;
		for (Shape s : shapes) {
			menu.add(0, i++, 0, s.toString());
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		chord_variant = item.getItemId();
		refresh_shape();
		return true;
	}
    
}