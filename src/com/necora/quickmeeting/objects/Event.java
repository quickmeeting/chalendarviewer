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
package com.necora.quickmeeting.objects;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Generic event class
 * @author vitor
 */
public class Event {

    /** Event id */
    protected String mId;
    /** Title */
    protected String mTitle;
    /** Details, description */
    protected String mDetails;
    /** Init date of event */
    protected Calendar mBegin;
    /** Ending date of event */
    protected Calendar mEnd;

    /** Empty constructor */
    public Event(){
        /** Empty constructor */
    }
    
    /**
     * Constructor with id
     * @param idEvent id of event
     */
    public Event(String idEvent) {
        this.mId = idEvent;
    }
    
    /**
     * Get id 
     * @return id of event
     */
    public String getId() {
        return mId;
    }

    /**
     * Set id 
     * @param id id of event
     */
    public void setId(String id) {
        this.mId = id;
    }

    /**
     * Get title 
     * @return get title of event
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set title of event
     * @param title event title
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * Get event details. Description of event
     * @return event details
     */
    public String getDetails() {
        return mDetails;
    }

    /**
     * Set event details
     * @param details event details
     */
    public void setDetails(String details) {
        this.mDetails = details;
    }

    /**
     * Initial date of event
     * @return initial date of event
     */
    public Calendar getBegin() {
        return mBegin;
    }

    /**
     * Set initial date of event
     * @param begin initial date of event
     */
    public void setBegin(Calendar begin) {
        this.mBegin = begin;
    }

    /**
     * Get end date of event
     * @return date of event
     */
    public Calendar getEnd() {
        return mEnd;
    }

    /**
     * Set date of event
     * @param end date of event
     */
    public void setEnd(Calendar end) {
        this.mEnd = end;
    }
    
    /**
     * Returns String with the most relevant fields for users. This method should be 
     * overridden by subclasses
     * @return relevant event info for the users
     */
    public String getEventInfo() {
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm"); 
    	
    	StringBuilder sb = new StringBuilder();
    	//sb.append(this.mTitle).append("\n");
    	sb.append(formatter.format(this.mBegin.getTime()));
    	sb.append(" - ");
    	sb.append(formatter.format(this.mEnd.getTime()));
    	return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[EVENT] {");
            sb.append("ID = ").append(this.mId).append("; ");
            sb.append("TITLE = ").append(this.mTitle).append("; ");
            sb.append("DETAILS = ").append(this.mDetails).append("; ");
            sb.append("BEGIN = ").
                append(mBegin.get(Calendar.MONTH) + 1).append("/").
                append(mBegin.get(Calendar.DAY_OF_MONTH)).append(" ").
                append(mBegin.get(Calendar.HOUR)).append(":").
                append(mBegin.get(Calendar.MINUTE)).append("; ");
            sb.append("END = ").
                append(mEnd.get(Calendar.MONTH) + 1).append("/").
                append(mEnd.get(Calendar.DAY_OF_MONTH)).append(" ").
                append(mEnd.get(Calendar.HOUR)).append(":").
                append(mEnd.get(Calendar.MINUTE)).append("; ");
        sb.append("}");
        return sb.toString();
    }
    
    

}
