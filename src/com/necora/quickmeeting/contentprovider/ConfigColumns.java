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

package com.necora.quickmeeting.contentprovider;

import android.provider.BaseColumns;


/**
 * Configuration class for Database access
 * @author vitor
 * 
 */
public class ConfigColumns implements BaseColumns{
    
    /** Column name: auth_user foreign key */
    public static final String PROPERTY = "property_name";
    /** Column name: email */
    public static final String VALUE = "property_value";
    /** Column name: name */
    public static final String DEFAULT = "property_default";

    /** Content type */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.config";
    
}
