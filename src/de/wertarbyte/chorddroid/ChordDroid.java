package de.wertarbyte.chorddroid;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ChordDroid extends Activity implements OnItemSelectedListener {
	private ChordDB db;
	
	private Spinner s_instrument;
	
	private Spinner s_root;
	private Spinner s_scale;
	private Spinner s_extra;
	private ChordView chordView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		db = new ChordDB();

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
        
    }
    
    public String getSelectedChord() {
    	StringBuilder sb = new StringBuilder();
    	sb.append( s_root.getSelectedItem() );
    	sb.append( s_scale.getSelectedItem() );
    	sb.append( s_extra.getSelectedItem() );
    	return sb.toString();
    }
    
    private void loadDatabase(int pos) {
		int rid = -1;
		switch (pos) {
			case 0:
				rid = R.raw.guitar_standard;
				break;
			case 1:
				rid = R.raw.ukulele_gcea;
				break;

			default:
				break;
		}
		if (rid != -1) {
			try {
				db.loadDatabase(getResources(), rid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}    	
    }

	public void onItemSelected(AdapterView<?> sender, View v, int pos, long id) {
		if ( sender == s_instrument) {
			int p = s_instrument.getSelectedItemPosition();
			loadDatabase(p);
		}
		
		TextView t = (TextView) findViewById(R.id.chordname);
		
		String selChord = getSelectedChord();
		
		chordView.setShape(null);
		t.setText("-");
		List<Shape> shapes = db.lookup(selChord);
		// for now, we just use the first chord shape
		if (shapes != null && shapes.size() > 0) {
			chordView.setShape(shapes.get(0));
			t.setText(shapes.get(0).toString());
		}	
	}

	public void onNothingSelected(AdapterView<?> v) {}
    
}