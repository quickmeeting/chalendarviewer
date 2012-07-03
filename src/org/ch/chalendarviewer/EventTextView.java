package org.ch.chalendarviewer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class EventTextView extends TextView {

	private boolean isUserEvent;
	
	public EventTextView(Context context) {
		super(context);
		setBackgroundResource(R.drawable.event_background);
		setTextColor(Color.BLACK);
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	public EventTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public EventTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	public boolean isUserEvent() {
		return isUserEvent;
	}

	public void setUserEvent(boolean isUserEvent) {
		this.isUserEvent = isUserEvent;
	}
}
