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

package com.necora.quickmeeting.service;

/**
 * Constants to use 
 * @since 31/Mar/2012
 */
public class GoogleConstants {
    
    /** Google App Client Identification */
    public static final String CLIENT_ID = "960151117831.apps.googleusercontent.com"; /** DONT STORE THE REAL DATA ON SOURCE CODE VERSION CONTROL */
    
    /** Google App Client Secret */
    public static final String CLIENT_SECRET = "1fBedz_8UAKElxNrXJjWZJxC"; /** DONT STORE THE REAL DATA ON SOURCE CODE VERSION CONTROL */
    
    /** Google API User Info Address */
    public static final String URL_USER_INFO = "https://www.googleapis.com/oauth2/v1/userinfo";
    
    /** Google API All Calendar Info Address */
    public static final String URL_ALL_CALENDARS = "https://www.google.com/calendar/feeds/default/allcalendars/full?alt=jsonc";
   
    /** Google API Insert new event Address */
    public static final String URL_INSERT_EVENT =  "https://www.google.com/calendar/feeds/default/private/full/";
 
    /** Google API Access Token Address */
    public static final String URL_ACCESS_TOKEN = "https://accounts.google.com/o/oauth2/token";
    
    /** Google App authorization site */             
    public static final String OAUTH_SITE         = "https://accounts.google.com/o/oauth2/auth";
    /** Redirect URI (standard value for APP's */ 
    public static final String OAUTH_REDIRECT_URI  = "urn:ietf:wg:oauth:2.0:oob";
    /** Response type (standard value for APP's */
    private static final String OAUTH_RESPONSE_TYPE = "code";
    /** Needed permissions */
    private static final String OAUTH_CALENDAR_PERMS = "https://www.google.com/calendar/feeds/";
    /**  OAuth parameter mail */
    private static final String OAUTH_USERMAIL_PERMS = "https://www.googleapis.com/auth/userinfo.email";
    /**  OAuth parameter userinfo */
    private static final String OAUTH_USERINFO_PERMS = "https://www.googleapis.com/auth/userinfo.profile";
    /** OAuth parameter scope */
    private static final String OAUTH_SCOPE         = OAUTH_CALENDAR_PERMS+"+"+OAUTH_USERMAIL_PERMS+"+"+OAUTH_USERINFO_PERMS; 
    /** Final big authorization URL */
    public static final String URL_OAUTH = OAUTH_SITE + "?client_id=" + CLIENT_ID + 
                                                         "&redirect_uri=" + OAUTH_REDIRECT_URI +
                                                         "&scope=" + OAUTH_SCOPE +
                                                         "&response_type=" + OAUTH_RESPONSE_TYPE;
    /** URL OAuth reference */
    public static final String URL_OAUTH_REF =  URL_ACCESS_TOKEN + "?client_id=" + CLIENT_ID +
                                                             "&client_secret=" + CLIENT_SECRET +                                                             
                                                             "&grant_type=refresh_token&response_type=code";   
}
