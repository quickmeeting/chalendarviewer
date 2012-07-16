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
import android.util.AttributeSet;
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
