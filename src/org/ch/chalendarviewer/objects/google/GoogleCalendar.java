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

package org.ch.chalendarviewer.objects.google;

import org.ch.chalendarviewer.objects.CalendarResource;

import java.util.TimeZone;

/**
 * Calendar class stores calendar information
 *
 * @since 15/Nov/2011
 * @version 1.0
 */
// TODO comment code
public class GoogleCalendar extends CalendarResource {
   

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
    
    private String mSelfLink;
        
    private String mColor;
    
    private TimeZone mTimeZone;
    
    
    public GoogleCalendar(){
        super();
    }

    public GoogleCalendar(String id, String name, String link, String eventsLink) {
        super(id,name, eventsLink);
        this.mSelfLink = link;
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
