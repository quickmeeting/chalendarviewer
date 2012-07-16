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


/**
 * User class stores logged user information like name, family_name, e-mail
 *
 * @since 13/Nov/2011
 * @version 1.0
 */
public class User {
    
    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_DISPLAY_NAME = "displayName";
    
    /**
     * User's name
     */
    private String mName;
    
    
    /**
     * User's email
     */
    private String mEmail;
    
    /**
     * User's constructor
     * Initializes all fields as null
     */
    public User() {
        this(null, null);
    }
    
    
    /**
     * User's Constructor
     * Initializes the fields with the given values
     * @param name User's name
     * @param email User's logged email
     */
    public User(String name, String email) {
        setName(name);       
        setEmail(email);
    }


    /**
     * @return User's name
     */
    public String getName() {
        return mName;
    }
    /**
     * @param mName name to set
     */
    public void setName(String mName) {
        this.mName = mName;
    }
    
    /**
     * @return the Email
     */
    public String getEmail() {
        return mEmail;
    }
    /**
     * @param email the mEmail to set
     */
    public void setEmail(String email) {
        this.mEmail = email;
    }
}
