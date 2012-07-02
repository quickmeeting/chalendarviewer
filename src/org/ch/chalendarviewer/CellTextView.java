package org.ch.chalendarviewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CellTextView extends TextView {

	private String calendarId;
	private int position;
	
	public CellTextView(Context context) {
		super(context);
	}
	
	public CellTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CellTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


	public String getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
