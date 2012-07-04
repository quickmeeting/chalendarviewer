package org.ch.chalendarviewer;

import org.ch.chalendarviewer.util.Observable;
import org.ch.chalendarviewer.util.Observer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class EventTextView extends TextView implements Observable {

	private Observer obs;
	private boolean isUserEvent;
	
	public EventTextView(Context context, boolean isUserEvent) {
		super(context);
		this.isUserEvent = isUserEvent;
		init();
	}
	
	private EventTextView(Context context) {
		super(context);
	}
	
	private EventTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	private EventTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    private void init() {
		setTextColor(Color.BLACK);
		if(isUserEvent) {
			setBackgroundResource(R.drawable.user_event_background);
		}
		else {
			setBackgroundResource(R.drawable.event_background);
		}
		setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	notifyObserver();
            }
        });
    }

	public boolean isUserEvent() {
		return isUserEvent;
	}

	public void setUserEvent(boolean isUserEvent) {
		this.isUserEvent = isUserEvent;
	}

	public void notifyObserver() {
		if(obs!=null) obs.notify(this);
	}
	
	@Override
	public void setObserver(Observer o) {
		obs = o;
	}
}
