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

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ch.chalendarviewer.objects.Calendar;
import org.ch.chalendarviewer.objects.Event;
import org.ch.chalendarviewer.objects.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GoogleApiConnector implements the connection between Google Api
 * and the service
 * 
 *@since 20/Nov/2011
 *TODO comment this class
 */
public class GoogleApiConnector {

    private static final String URL_USER_INFO = "https://www.googleapis.com/oauth2/v1/userinfo";
   
    private static GoogleApiConnector _instance;
    
    private SessionManager mSessionManager;
    
    private GoogleApiConnector() {
        mSessionManager = new SessionManager();
    }

    public static synchronized GoogleApiConnector getInstance() {
        if (null == _instance) {
            _instance = new GoogleApiConnector();
        }
        return _instance;
    }
    
    public User getUserInformation(){
        User user = new User();
        
        return user;
    }
    
    public List<Calendar> getCalendars(){
        ArrayList<Calendar> calendars = new ArrayList<Calendar>();
        return calendars;
    }
    
    public List<Event> getEvents (Calendar calendar, Date Begin, Date end) {
        ArrayList<Event> events = new ArrayList<Event>();
        return events;
    }    
}