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
import org.ch.chalendarviewer.objects.GoogleEvent;
import org.ch.chalendarviewer.objects.GoogleCalendar;
import org.ch.chalendarviewer.objects.User;
import org.ch.chalendarviewer.util.ConnectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
            user.setName(jsonUserObj.getString(User.FIELD_NAME));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return user;
    }
    
    public List<GoogleCalendar> getCalendars(){
        ArrayList<GoogleCalendar> calendarsList = new ArrayList<GoogleCalendar>();
        
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

            GoogleCalendar cal;
            JSONObject jsonCalendar;
            for (int j = 0; j < ilength; j++) {
                cal = new GoogleCalendar();
                jsonCalendar = (JSONObject) jsonCalendarsList.get(j);
                
                cal.setColor(jsonCalendar.getString(GoogleCalendar.FIELD_COLOR));
                cal.setEventFeedLik(jsonCalendar.getString(GoogleCalendar.FIELD_EVENT_FEED_LINK));
                cal.setId(jsonCalendar.getString(GoogleCalendar.FIELD_ID));
                cal.setSelfLink(jsonCalendar.getString(GoogleCalendar.FIELD_SELF_LINK));
                cal.setTimeZone(TimeZone.getTimeZone(jsonCalendar.getString(GoogleCalendar.FIELD_TIMEZONE)));
                cal.setTitle(jsonCalendar.getString(GoogleCalendar.FIELD_TITLE));
                
                calendarsList.add(cal);               
            }
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return calendarsList;
    }
    
    public List<GoogleEvent> getEvents (GoogleCalendar calendar, Calendar begin, Calendar end) {
        ArrayList<GoogleEvent> events = new ArrayList<GoogleEvent>();
        
        begin.setTimeZone(calendar.getTimeZone());
        end.setTimeZone(calendar.getTimeZone());
        
        //pattern 2011-11-23T00:00:00
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        String url = calendar.getEventFeedLik() +  "?alt=jsonc&start-min=" + formatter.format(begin.getTime()) + "&start-max="+ formatter.format(end.getTime());
                
        String[] paramsKey =  {"Authorization"};
        String[] paramsValue = {"Bearer " + mSessionManager.getAccessToken()};
        
               
        String googleResponse = null;
        try {
            googleResponse = ConnectionUtils.getHttpsGetConnection(url, paramsKey, paramsValue);
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
            
            JSONArray jsonEventsList = (JSONArray) jsonData.getJSONArray("items");
                        
            int ilength = jsonEventsList.length();

            GoogleEvent  ev;
            JSONObject jsonEvent, jsonUser;
            for (int j = 0; j < ilength; j++) {
                ev = new GoogleEvent();
                
                jsonEvent = (JSONObject) jsonEventsList.get(j);
                
                ev.setAlternateLink(jsonEvent.getString(GoogleEvent.FIELD_ALTERNATIVE_LINK));
                ev.setCanEdit(jsonEvent.getBoolean(GoogleEvent.FIELD_CAN_EDIT));
                ev.setDetails(jsonEvent.getString(GoogleEvent.FIELD_DETAILS));
                ev.setId(jsonEvent.getString(GoogleEvent.FIELD_ID));
                ev.setLocation(jsonEvent.getString(GoogleEvent.FIELD_LOCATION));
                ev.setSelfLink(jsonEvent.getString(GoogleEvent.FIELD_SELF_LINK));
                ev.setStatus(jsonEvent.getString(GoogleEvent.FIELD_STATUS));
                ev.setTitle(jsonEvent.getString(GoogleEvent.FIELD_TITLE));
               
                JSONArray listWhen  = (JSONArray) jsonEvent.getJSONArray(GoogleEvent.FIELD_WHEN_LIST);
                begin.setTime(formatter.parse(((JSONObject)listWhen.get(0)).getString(GoogleEvent.FIELD_BEGIN)));
                end.setTime(formatter.parse(((JSONObject)listWhen.get(0)).getString(GoogleEvent.FIELD_END)));
                ev.setBegin(begin);
                ev.setEnd(end);
                
                jsonUser = (JSONObject) jsonEvent.getJSONObject(GoogleEvent.FIELD_CREATOR);
                ev.setCreator(new User(jsonUser.getString(User.FIELD_DISPLAY_NAME), jsonUser.getString(User.FIELD_EMAIL)));
                
                JSONArray listAttendees = (JSONArray) jsonEvent.getJSONArray(GoogleEvent.FIELD_ATTENDEES);
                for (int indexAtendees = 0; indexAtendees < listAttendees.length();indexAtendees++) {
                    jsonUser = (JSONObject) listAttendees.get(indexAtendees);
                    ev.addAttendee(new User(jsonUser.getString(User.FIELD_DISPLAY_NAME), jsonUser.getString(User.FIELD_EMAIL)));
                }
                
                events.add(ev);               
            }            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
      
        
        return events;
    }    
}