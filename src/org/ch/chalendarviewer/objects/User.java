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


/**
 * User class stores logged user information like name, family_name, e-mail
 *
 * @since 13/Nov/2011
 * @version 1.0
 */
public class User {
    
    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_FAMILYNAME = "family_name";

    public static final String FIELD_NAME = "given_name";
    
    /**
     * User's name
     */
    private String mName;
    
    /**
     * User's family name
     */
    private String mFamilyName;
    
    /**
     * User's email
     */
    private String mEmail;
    
    /**
     * User's constructor
     * Initializes all fields as null
     */
    public User() {
        this(null, null, null);
    }
    
    
    /**
     * User's Constructor
     * Initializes the fields with the given values
     * @param name User's name
     * @param familyName User's Family Name
     * @param email User's logged email
     */
    public User(String name, String familyName, String email) {
        setName(name);
        setFamilyName(familyName);
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
     * @return User's family Name
     */
    public String getFamilyName() {
        return mFamilyName;
    }
    /**
     * @param familyName Family Name to set
     */
    public void setFamilyName(String familyName) {
        this.mFamilyName = mFamilyName;
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
