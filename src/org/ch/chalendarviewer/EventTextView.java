package org.ch.chalendarviewer;

import org.ch.chalendarviewer.objects.Event;
import org.ch.chalendarviewer.util.Observable;
import org.ch.chalendarviewer.util.Observer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Represents an event in UI
 * @author jlafuente
 *
 */
public class EventTextView extends TextView implements Observable {

    private Observer mObs;
	/** True: event was created by chalendar */
	private boolean mIsUserEvent;
	/** Related Event */
	private Event mEvent;

    public EventTextView(Context context, Event event, boolean isUserEvent) {
		super(context);
		this.mIsUserEvent = isUserEvent;
		this.mEvent = event;
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
		if(mIsUserEvent) {
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
    
    public Event getEvent() {
    	return mEvent;
    }

	public boolean isUserEvent() {
		return mIsUserEvent;
	}

	public void notifyObserver() {
		if(mObs!=null) mObs.notify(this);
	}
	
	@Override
	public void setObserver(Observer o) {
		mObs = o;
	}
}
