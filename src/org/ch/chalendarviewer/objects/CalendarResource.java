/**
    This file is part of QuickMeeting.

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


/**
 * Generic calendar class
 * @author vitor
 *
 */
public class CalendarResource {
    
    /** Empty constructor */
    public CalendarResource(){
        super();
    }
    
    /**
     * Constructor with parameters
     * @param mId id of calendar
     * @param mTitle title of calendar
     * @param eventsLink feed of events link
     */
    public CalendarResource(String mId, String mTitle, String eventsLink) {
        super();
        this.mId = mId;
        this.mTitle = mTitle;
        this.mEventFeedLink = eventsLink;
    }

    /** Calendar resource id*/
    protected String mId;
    /** Calendar title */
    protected String mTitle;
    /** Feed link of events */
    protected String mEventFeedLink;

    /**
     * Get id
     * @return id of calendar
     */
    public String getId() {
        return mId;
    }

    /**
     * Set id
     * @param id id of calendar
     */
    public void setId(String id) {
        this.mId = id;
    }

    /**
     * @return title of calendar
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @param title title of calendar
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * Get events feed link
     * @return events feed link
     */
    public String getEventFeedLink() {
        return mEventFeedLink;
    }

    /**
     * Get events feed link
     * @param eventFeedLik events feed link
     */
    public void setEventFeedLink(String eventFeedLik) {
        this.mEventFeedLink = eventFeedLik;
    }

}
