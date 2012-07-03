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
		if(isUserEvent) {
			setBackgroundResource(R.drawable.user_event_background);
			setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return false;
				}
			});
			setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	notifyObserver();
	            }
	        });
		}
	}

	public void notifyObserver() {
		if(obs!=null) obs.notify(this);
	}
	
	@Override
	public void setObserver(Observer o) {
		obs = o;
	}
}
