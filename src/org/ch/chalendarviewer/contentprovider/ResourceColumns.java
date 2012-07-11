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

package org.ch.chalendarviewer.contentprovider;

import android.provider.BaseColumns;


/**
 * Resource resource class for Database access
 * @author vitor
 * 
 */
public class ResourceColumns implements BaseColumns{
    
    /** Column name: auth_user foreign key */
    public static final String AUTH_USER_ID = "auth_user_id";
    /** Column name: email */
    public static final String LINK = "link";
    /** Column name: name */
    public static final String NAME = "name";
    /** Column name: name to display on main screen */
    public static final String DISPLAY_NAME = "displayName";
    /** Column name: name to display on main screen */
    public static final String ACTIVE = "active";
    /** Column name: name to display on main screen */
    public static final String EVENTS_LINK = "events_link";
    
    

    /** Content type */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.resources";
    
}
