package de.wertarbyte.chorddroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemSelectedListener;
import de.wertarbyte.chorddroid.harmony.Chord;

public class ChordDroid extends Activity implements OnItemSelectedListener, OnClickListener {
	private List<Instrument> instruments;
	
	private static final int TOBASKET = 0;
	private static final int TOLIB = 1;
	private static final int ADDTOBASKET = 2;
	
	private static final int CLEARBASKET = 3;
	private static final int REMOVEFROMBASKET = 4;
	
	private static final int CHANGE_VARIANT = 5;
	
	private ViewFlipper flipper;
	
	private Spinner s_instrument;
	private Spinner s_root;
	private Spinner s_triad;
	private Spinner s_extra;
	private Spinner s_transposer;
	
	private ChordView chordView;
	private TextView t_variant;
	
	private ListView l_basket;
	
	private ChordBasket basket;
	
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
		
		basket = new ChordBasket(this, this);
		
		setContentView(R.layout.chorddroid);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		
		Animation s_in = AnimationUtils.loadAnimation(this, R.anim.fadein);
		this.flipper.setInAnimation(s_in);
		
		chordView = (ChordView) findViewById(R.id.chordview);
		
		s_instrument = (Spinner) findViewById(R.id.instrument);
		s_root = (Spinner) findViewById(R.id.root);
		s_triad = (Spinner) findViewById(R.id.triad);
		s_extra = (Spinner) findViewById(R.id.extra);
		s_transposer = (Spinner) findViewById(R.id.transposer);
		
		t_variant = (TextView) findViewById(R.id.variant);
		
		l_basket = (ListView) findViewById(R.id.basketlist);
		l_basket.setAdapter(basket.getAdapter());
		
		s_instrument.setOnItemSelectedListener(this);
		s_root.setOnItemSelectedListener(this);
		s_triad.setOnItemSelectedListener(this);
		s_extra.setOnItemSelectedListener(this);
		s_transposer.setOnItemSelectedListener(this);
		
		// restore old bundle data
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("basket") ) {
				basket.fromStringArray(savedInstanceState.getStringArray("basket"));
			}
		}
		
		chordView.setClickable(true);
		chordView.setOnClickListener(this);
		
		registerForContextMenu(chordView);
		registerForContextMenu(l_basket);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArray("basket", basket.asStringArray());
	}
	
	public Chord getSelectedChord() {
		StringBuilder sb = new StringBuilder();
		sb.append(s_root.getSelectedItem());
		String triad = (String) s_triad.getSelectedItem();
		// for some reason, the order of chord name components differs
		if (Pattern.matches("^|m|dim$", triad)) {
			sb.append(s_triad.getSelectedItem());
			sb.append(s_extra.getSelectedItem());
		} else {
			sb.append(s_extra.getSelectedItem());
			sb.append(s_triad.getSelectedItem());
		}
		return Chord.lookup(sb.toString());
	}
	
	private int getTranspositionSteps() {
		int i = s_transposer.getSelectedItemPosition();
		int c = s_transposer.getCount();
		if (i == 0) {
			return 0;
		} else {
			return i - (c / 2);
		}
	}
	
	public Instrument getSelectedInstrument() {
		int pos = s_instrument.getSelectedItemPosition();
		return instruments.get(pos);
	}
	
	public void onItemSelected(AdapterView<?> sender, View v, int pos, long id) {
		chord_variant = 0;
		getSelectedInstrument().setTranspositionSteps( getTranspositionSteps() );
		currentShapes = getSelectedInstrument().lookup(getSelectedChord());
		refresh_shape();
	}
	
	public List<Shape> getShapes() {
		return currentShapes;
	}
	
	public void refresh_shape() {
		chordView.setShape(null);
		// refresh the shapes in our basket as well
		basket.getAdapter().notifyDataSetChanged();
		t_variant.setText(getSelectedChord().getName() + ": -");
		List<Shape> shapes = getShapes();
		if (shapes != null && shapes.size() > 0) {
			Shape s = shapes.get(chord_variant);
			t_variant.setText(s.getChord().getName()+": "+(chord_variant+1)+"/"+shapes.size());
			chordView.setShape( shapes.get(chord_variant) );
		}
	}
	
	public void onNothingSelected(AdapterView<?> v) {
	}
	
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
		if (v == chordView) {
			List<Shape> shapes = getShapes();
			int i = 0;
			for (Shape s : shapes) {
				menu.add(CHANGE_VARIANT, i++, 1, s.toString());
			}
		} else if (v == l_basket) {
			menu.add(Menu.NONE, REMOVEFROMBASKET, 1, R.string.remove_from_basket);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		if (item.getItemId() == REMOVEFROMBASKET) {
			if (info != null) {
				// TODO This feels quite hacky, how can I get the source of the context menu?
				basket.removeChord(info.position);
				return true;
			}
		}
		if (item.getGroupId() == CHANGE_VARIANT) {
			chord_variant = item.getItemId();
			refresh_shape();
		}
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (flipper.getDisplayedChild() == 0) {
			menu.add(Menu.NONE, ADDTOBASKET, 1, R.string.add_to_basket);
			menu.add(Menu.NONE, TOBASKET, 1, R.string.show_basket);
		} else {
			menu.add(Menu.NONE, CLEARBASKET, 1, R.string.empty_basket);
			menu.add(Menu.NONE, TOLIB, 1, R.string.show_library);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int choice = item.getItemId();
		switch (choice) {
		case TOBASKET:
			flipper.showNext();
			return true;
		case TOLIB:
			flipper.showPrevious();
			return true;
		case ADDTOBASKET:
			basket.addChord(getSelectedChord());
			return true;
		case CLEARBASKET:
			basket.clear();
			return true;
		}
		return false;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// Take care of calling this method on earlier versions of
			// the platform where it doesn't exist.
			onBackPressed();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() {
		// This will be called either automatically for you on 2.0
		// or later, or by the code above on earlier versions of the
		// platform.
		if (flipper.getDisplayedChild() == 0) {
			moveTaskToBack(false);
		} else {
			flipper.setDisplayedChild(0);
		}
		return;
	}
	
}
