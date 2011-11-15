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

import java.util.ArrayList;

//TODO populate this class
public class Event {

    private String mId;
    
    private String mSelfLink;
    
    private String mAlternateLink;
    
    private boolean mCanEdit;

    private String mTitle;
    
    private String mDetails;
    
    private String mStatus;
    
    private User mCreator;
    
    private String mWhere;
    
    private ArrayList<User> mAttendees;
    
    private ArrayList<Calendar> mResources;
    
    private Date mBegin;
    
    private Date mEnd;    
    
}
