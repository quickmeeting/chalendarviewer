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
public class Calendar {
    
     
    public final String FIELD_ID = "id";
    public final String FIELD_TITLE = "title";
    public final String FIELD_EVENT_FEED_LINK = "eventFeedLink";
    public final String FIELD_SELF_LINK = "selfLink";
    public final String FIELD_CAN_EDIT = "canEdit";
    public final String FIELD_AUTHOR = "author";
    public final String FIELD_ACCESS_LEVEL = "accessLevel";
    public final String FIELD_COLOR = "color";
    public final String FIELD_TIMEZONE = "timeZone";
    
    public final int CALENDAR_TYPE_RESOURCE = 0;
    public final int CALENDAR_TYPE_PERSONAL = 1;
    public final int CALENDAR_TYPE_GOOGLE   = 2;
    
    private int mType;
    
    private String mId;
    
    private String mTitle;
    
    private String mEventFeedLik;
    
    private String mSelfLink;
    
    private boolean mCanEdit;

    private User mAuthor;
    
    private String mAccessLevel;
    
    private Color mColor;
    
    private TimeZone mTimeZone;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmEventFeedLik() {
        return mEventFeedLik;
    }

    public void setmEventFeedLik(String mEventFeedLik) {
        this.mEventFeedLik = mEventFeedLik;
    }

    public String getmSelfLink() {
        return mSelfLink;
    }

    public void setmSelfLink(String mSelfLink) {
        this.mSelfLink = mSelfLink;
    }

    public boolean ismCanEdit() {
        return mCanEdit;
    }

    public void setmCanEdit(boolean mCanEdit) {
        this.mCanEdit = mCanEdit;
    }

    public User getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(User mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmAccessLevel() {
        return mAccessLevel;
    }

    public void setmAccessLevel(String mAccessLevel) {
        this.mAccessLevel = mAccessLevel;
    }

    public Color getmColor() {
        return mColor;
    }

    public void setmColor(Color mColor) {
        this.mColor = mColor;
    }

    public TimeZone getmTimeZone() {
        return mTimeZone;
    }

    public void setmTimeZone(TimeZone mTimeZone) {
        this.mTimeZone = mTimeZone;
    }   
    
    
    
}
