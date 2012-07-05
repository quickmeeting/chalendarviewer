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

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Auth user class for Database access
 * @author vitor
 * 
 */
public class AccountColumns implements BaseColumns{
    
    /** Column name: authorization_code */
    public static final String REFRESH_TOKEN = "refresh_token";
    /** Column name: access token */
    public static final String ACCESS_TOKEN = "access_token";
    /** Column name: email */
    public static final String EMAIL = "email";
    /** Column name: active_user */
    public static final String ACTIVE_USER =  "active_user";
    /** Column name: expiration_date */
    public static final String EXPIRATION_DATE = "expiration_date";
    
    /** Content type */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.auth_users";
    
    /** Content URI */
    public static final Uri CONTENT_URI = Uri.parse("content://" + ChalendarContentProvider.AUTHORITY + "/auth_users");   

}
