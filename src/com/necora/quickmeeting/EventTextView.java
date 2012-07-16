/**
    This file is part of QuickMeeting.

    QuickMeeting is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QuickMeeting is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with QuickMeeting.  If not, see <http://www.gnu.org/licenses/>.    
*/

package com.necora.quickmeeting;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.necora.quickmeeting.objects.Event;
import com.necora.quickmeeting.util.Observable;
import com.necora.quickmeeting.util.Observer;

/**
 * Represents an event in UI
 * @author jlafuente
 *
 */
public class EventTextView extends TextView implements Observable {

    private Observer mObs;
	/** True: event was created by quickMeeting */
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
