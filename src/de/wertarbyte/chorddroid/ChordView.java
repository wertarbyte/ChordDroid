/*
 * Created on 12.06.2010
 *
 */
package de.wertarbyte.chorddroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ChordView extends View {
	
	private Shape chordshape;
	private Paint fretPaint;
	private Paint dotPaint;
	private Paint stringPaint;
	private Paint nutPaint;
	
	public ChordView(Context context, AttributeSet as) {
		super(context, as);
		chordshape = null;
		
		stringPaint = new Paint();
		stringPaint.setColor(Color.WHITE);
		stringPaint.setAntiAlias(true);
		
		fretPaint = new Paint();
		fretPaint.setColor(Color.WHITE);
		fretPaint.setAntiAlias(true);
		
		nutPaint = new Paint();
		nutPaint.setColor(Color.WHITE);
		nutPaint.setAntiAlias(true);
		
		dotPaint = new Paint();
		dotPaint.setColor(Color.WHITE);
		dotPaint.setAntiAlias(true);
	}
	
	public void setShape(Shape chordshape) {
		this.chordshape = chordshape;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		
		// we use a square playing field
		int size = Math.min(width, height);
		
		float penWidth = size / 100;
		stringPaint.setStrokeWidth(penWidth);
		nutPaint.setStrokeWidth(penWidth * 2);
		fretPaint.setStrokeWidth(penWidth / 2);
		fretPaint.setTextSize(12 * penWidth);
		
		if (chordshape != null) {
			// draw the chord shape
			drawLines(canvas, size);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(size, size);
	}
	
	private void drawLines(Canvas c, int size) {
		int[] pos = chordshape.getPositions();
		int n = pos.length;
		
		// the last fret used by this shape: A shape consisting only of muted
		// and open strings will display at least one fret
		int last_fret = Math.max(1, chordshape.getMaxPos());
		// the first fret used by this shape, excluding open strings
		int first_fret = Math.max(1, chordshape.getMinPos());
		
		// we will display at least 4 frets, so we expand the range
		while (1 + (last_fret - first_fret) < 4) {
			if (first_fret > 1) {
				first_fret--;
			} else {
				last_fret++;
			}
		}
		
		int frets = 1 + (last_fret - first_fret);
		
		float d_w = size / (frets);
		float d_h = size / (n + 1);
		
		// label the first fret
		c.drawText(first_fret + "", d_w * 0.5f, d_h * 0.7f, fretPaint);
		
		for (int i = n; i > 0; i--) {
			c.drawLine(0, d_h * i, size, d_h * i, stringPaint);
		}
		
		for (int i = frets; i > 0; i--) {
			c.drawLine(d_w * i, d_h * 0.75f, d_w * i, d_h * (pos.length + 0.25f), stringPaint);
		}
		if (first_fret == 1) {
			c.drawLine(d_w * 0 + nutPaint.getStrokeWidth() / 2, d_h * 0.75f, d_w * 0 + nutPaint.getStrokeWidth() / 2, d_h * (pos.length + 0.25f), nutPaint);
		}
		
		for (int string = 0; string < pos.length; string++) {
			int fret = pos[pos.length - string - 1];
			// fret position relative to the fretboard position
			int rel_fret = fret - first_fret;
			float r = d_w / 4f;
			if (fret > 0) {
				float c_x = d_w * ((float) rel_fret + 0.5f);
				float c_y = d_h * (1f + (float) string);
				c.drawCircle(c_x, c_y, r, fretPaint);
			} else if (fret < 0) {
				// paint markers on muted strings
				float c_x = d_w * 0.5f;
				float c_y = d_h * (1f + (float) string);
				c.drawLine(c_x - r, c_y - r, c_x + r, c_y + r, stringPaint);
				c.drawLine(c_x - r, c_y + r, c_x + r, c_y - r, stringPaint);
			}
		}
		
	}
}
