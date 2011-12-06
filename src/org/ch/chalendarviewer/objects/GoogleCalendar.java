/**
    This file is part of ChalendarViewer.

    ChalendarViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ChalendarViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ChalendarViewer.  If not, see <http://www.gnu.org/licenses/>.    
*/

package org.ch.chalendarviewer.objects;

import android.graphics.Color;

import java.util.TimeZone;

/**
 * Calendar class stores calendar information
 *
 * @since 15/Nov/2011
 * @version 1.0
 */
// TODO comment code
public class GoogleCalendar {
    
     
    static public final String FIELD_ID = "id";
    static public final String FIELD_TITLE = "title";
    static public final String FIELD_EVENT_FEED_LINK = "eventFeedLink";
    static public final String FIELD_SELF_LINK = "selfLink";
    static public final String FIELD_COLOR = "color";
    static public final String FIELD_TIMEZONE = "timeZone";
    public static final String FIELD_RESOURCE =  "resource";
    
    public final int CALENDAR_TYPE_RESOURCE = 0;
    public final int CALENDAR_TYPE_PERSONAL = 1;
    public final int CALENDAR_TYPE_GOOGLE   = 2;
    
    private int mType;
    
    private String mId;
    
    private String mTitle;
    
    private String mEventFeedLik;
    
    private String mSelfLink;
        
    private String mColor;
    
    private TimeZone mTimeZone;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getEventFeedLik() {
        return mEventFeedLik;
    }

    public void setEventFeedLik(String eventFeedLik) {
        this.mEventFeedLik = eventFeedLik;
    }

    public String getSelfLink() {
        return mSelfLink;
    }

    public void setSelfLink(String selfLink) {
        this.mSelfLink = selfLink;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        this.mColor = color;
    }

    public TimeZone getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.mTimeZone = timeZone;
    }   
    
    
    
}
