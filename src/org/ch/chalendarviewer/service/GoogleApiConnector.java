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
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ch.chalendarviewer.objects.Calendar;
import org.ch.chalendarviewer.objects.Event;
import org.ch.chalendarviewer.objects.User;
import org.ch.chalendarviewer.util.ConnectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * GoogleApiConnector implements the connection between Google Api
 * and the service
 * 
 *@since 20/Nov/2011
 *TODO comment this class
 */
public class GoogleApiConnector {

    private static final String URL_USER_INFO = "https://www.googleapis.com/oauth2/v1/userinfo";
    
    private static final String URL_ALL_CALENDARS = "https://www.google.com/calendar/feeds/default/allcalendars/full?alt=jsonc";
   
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
        
        String[] paramsKey =  {"Authorization"};
        String[] paramsValue = {"Bearer " + mSessionManager.getAccessToken()};
        
        String googleResponse = null;
        try {
            googleResponse = ConnectionUtils.getHttpsGetConnection(URL_USER_INFO, paramsKey, paramsValue);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JSONObject jsonUserObj;
        try {
            jsonUserObj = (JSONObject) new JSONTokener(googleResponse).nextValue();
            user.setEmail(jsonUserObj.getString(User.FIELD_EMAIL));
            user.setFamilyName(jsonUserObj.getString(User.FIELD_FAMILYNAME));
            user.setName(jsonUserObj.getString(User.FIELD_NAME));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return user;
    }
    
    public List<Calendar> getCalendars(){
        ArrayList<Calendar> calendarsList = new ArrayList<Calendar>();
        
        String[] paramsKey =  {"Authorization"};
        String[] paramsValue = {"Bearer " + mSessionManager.getAccessToken()};
        
        String googleResponse = null;
        try {
            googleResponse = ConnectionUtils.getHttpsGetConnection(URL_ALL_CALENDARS, paramsKey, paramsValue);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JSONObject jsonUserObj;
        try {
            jsonUserObj = (JSONObject) new JSONTokener(googleResponse).nextValue();
            
            JSONObject jsonData = jsonUserObj.getJSONObject("data");
            
            JSONArray jsonCalendarsList = (JSONArray) jsonData.getJSONArray("items");
                        
            int ilength = jsonCalendarsList.length();

            Calendar cal;
            JSONObject jsonCalendar;
            for (int j = 0; j < ilength; j++) {
                cal = new Calendar();
                jsonCalendar = (JSONObject) jsonCalendarsList.get(j);
                
                cal.setColor(jsonCalendar.getString(Calendar.FIELD_COLOR));
                cal.setEventFeedLik(jsonCalendar.getString(Calendar.FIELD_EVENT_FEED_LINK));
                cal.setId(jsonCalendar.getString(Calendar.FIELD_ID));
                cal.setSelfLink(jsonCalendar.getString(Calendar.FIELD_SELF_LINK));
                cal.setTimeZone(TimeZone.getTimeZone(jsonCalendar.getString(Calendar.FIELD_TIMEZONE)));
                cal.setTitle(jsonCalendar.getString(Calendar.FIELD_TITLE));
                
                calendarsList.add(cal);               
            }
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return calendarsList;
    }
    
    public List<Event> getEvents (Calendar calendar, Date Begin, Date end) {
        ArrayList<Event> events = new ArrayList<Event>();
        return events;
    }    
}