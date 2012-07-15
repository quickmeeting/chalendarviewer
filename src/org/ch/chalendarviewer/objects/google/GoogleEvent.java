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

import org.ch.chalendarviewer.objects.Event;
import org.ch.chalendarviewer.objects.User;

import java.util.ArrayList;
import java.util.List;

//TODO populate this class
public class GoogleEvent extends Event{

    static public final String FIELD_ID = "id";
    static public final String FIELD_SELF_LINK = "selfLink";
    static public final String FIELD_CAN_EDIT = "canEdit";
    static public final String FIELD_ALTERNATIVE_LINK = "alternateLink";
    static public final String FIELD_TITLE = "title";
    static public final String FIELD_DETAILS = "details";
    static public final String FIELD_STATUS = "status";
    static public final String FIELD_CREATOR = "creator";
    static public final String FIELD_LOCATION = "location";
    static public final String FIELD_ATTENDEES = "attendees";
    static public final String FIELD_BEGIN = "start";
    static public final String FIELD_END = "end";
    public static final String FIELD_WHEN_LIST = "when";

    
    private String mSelfLink;
    
    private String mAlternateLink;
    
    private boolean mCanEdit;

    private String mStatus;
    
    private User mCreator;
    
    private String mLocation;
    
    private ArrayList<User> mAttendees;
        
    public GoogleEvent() {
        mAttendees = new ArrayList<User>();
    }
    
    /**
     * Build GoogleEvent class from superclass
     * @param ev
     */
    public GoogleEvent(Event ev){
        this.mBegin = ev.getBegin();
        this.mEnd = ev.getEnd();
        this.mId = ev.getId();
        this.mTitle = ev.getTitle();
        this.mDetails = ev.getDetails();
        mAttendees = new ArrayList<User>();
    }

    public String getSelfLink() {
        return mSelfLink;
    }

    public void setSelfLink(String selfLink) {
        this.mSelfLink = selfLink;
    }

    public String getAlternateLink() {
        return mAlternateLink;
    }

    public void setAlternateLink(String alternateLink) {
        this.mAlternateLink = alternateLink;
    }

    public boolean ismCanEdit() {
        return mCanEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.mCanEdit = canEdit;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    public User getCreator() {
        return mCreator;
    }

    public void setCreator(User creator) {
        this.mCreator = creator;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public List<User> getAttendees() {
        return mAttendees;
    }

    public void addAttendee(User attendee) {
        mAttendees.add(attendee);
    }

    public void removeAttendee(User attendee) {
        mAttendees.remove(attendee);
    }

    public void removeAllAttendees() {
        mAttendees.clear();
    }    
   
    @Override
    public String getEventInfo() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(super.getEventInfo());
    	sb.append("\n");
    	if(this.mCreator!=null) sb.append(this.mCreator.getName());
    	if(this.mAttendees!=null){
    		for(User attendee: this.mAttendees) {
    			sb.append(attendee.getName());
    		}
    	}
    	return sb.toString();
    }
    
}
