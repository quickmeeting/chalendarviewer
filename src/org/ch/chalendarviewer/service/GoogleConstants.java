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

package org.ch.chalendarviewer.service;

/**
 * Constants to use 
 * @since 31/Mar/2012
 */
public class GoogleConstants {
    
    /** Google API Access Token Address */
    public static final String URL_ACCESS_TOKEN = "https://accounts.google.com/o/oauth2/token";
    
    /** Google API redirection URI */
    public static final String URL_REDIRECTION_URI = "urn:ietf:wg:oauth:2.0:oob";
    
    /** Google API User Info Address */
    public static final String URL_USER_INFO = "https://www.googleapis.com/oauth2/v1/userinfo";
    
    /** Google API All Calendar Info Address */
    public static final String URL_ALL_CALENDARS = "https://www.google.com/calendar/feeds/default/allcalendars/full?alt=jsonc";
   
    /** Google API Insert new event Address */
    public static final String URL_INSERT_EVENT =  "https://www.google.com/calendar/feeds/default/private/full";
 
    /** Google App Client Identification */
    public static final String CLIENT_ID = "960151117831.apps.googleusercontent.com"; /** DONT STORE THE REAL DATA ON SOURCE CODE VERSION CONTROL */
    
    /** Google App Client Secret */
    public static final String CLIENT_SECRET = "1fBedz_8UAKElxNrXJjWZJxC"; /** DONT STORE THE REAL DATA ON SOURCE CODE VERSION CONTROL */
}
