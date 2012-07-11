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

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.ch.chalendarviewer.objects.CalendarResource;
import org.ch.chalendarviewer.objects.User;
import org.ch.chalendarviewer.objects.google.GoogleCalendar;
import org.ch.chalendarviewer.objects.google.GoogleEvent;
import org.ch.chalendarviewer.util.ConnectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * GoogleApiConnector implements the connection between Google Api
 * and the service
 * 
 *@since 20/Nov/2011
 *TODO comment this class
 */
public class GoogleCalendarApiConnector {

    /** TAG for logging*/
    private static final String TAG = GoogleCalendarApiConnector.class.getName();
       
    /** Singleton instance*/
    private static GoogleCalendarApiConnector _instance;
    
    /** userManager instance*/
    private UserManager mSessionManager;
    
    /** DateTime formatter for interval events */
    private SimpleDateFormat mDateTimeFormatter;
    /** Date formatter for complete-day events */
    private SimpleDateFormat mDateFormatter;
    
    /**
     * Constructor. Get userManager instance and initialize formatters
     * @param context application context
     */
    private GoogleCalendarApiConnector(Context context) {
        mSessionManager = UserManager.getInstance(context);
        mDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        mDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Get a singleton instance of service
     * @param context application context
     * @return the unique instance of this service
     */
    public static synchronized GoogleCalendarApiConnector getInstance(Context context) {
        if (null == _instance) {            
            _instance = new GoogleCalendarApiConnector(context);
        }

        return _instance;
    }
    
    /**
     * Get all available calendars of active user
     * @return googlecalendar list
     */
    public List<GoogleCalendar> getCalendars(){
        ArrayList<GoogleCalendar> calendarsList = new ArrayList<GoogleCalendar>();
        
        String[] paramsKey =  {"Authorization"};
        String[] paramsValue = {"Bearer " + mSessionManager.getActiveUserAccessToken()};
        
        String googleResponse = null;
        try {
            googleResponse = ConnectionUtils.getHttpsGetConnection(GoogleConstants.URL_ALL_CALENDARS, paramsKey, paramsValue);
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
                Log.d(TAG,jsonCalendar.toString());
                cal.setColor(jsonCalendar.getString(GoogleCalendar.FIELD_COLOR));
                cal.setEventFeedLink(jsonCalendar.getString(GoogleCalendar.FIELD_EVENT_FEED_LINK));
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
    
    /**
     * Get calendar data from link
     * @param link calendar link
     * @return a google Calendar from a link
     */
    public CalendarResource getCalendarByLink(String link){
        Log.d(TAG,link);
        GoogleCalendar cal = new GoogleCalendar(); 

        String[] paramsKey =  {"Authorization"};
        String[] paramsValue = {"Bearer " + mSessionManager.getActiveUserAccessToken()};

        String googleResponse = null;
        try {
            googleResponse = ConnectionUtils.getHttpsGetConnection(link + "?alt=jsonc", paramsKey, paramsValue);
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
        Log.d(TAG,googleResponse);
        JSONObject jsonCalendarObj;
        try {
            jsonCalendarObj = (JSONObject) new JSONTokener(googleResponse).nextValue();

            JSONObject jsonCalendar = jsonCalendarObj.getJSONObject("data");

            cal = new GoogleCalendar();
            Log.d(TAG,jsonCalendar.toString());
            cal.setColor(jsonCalendar.getString(GoogleCalendar.FIELD_COLOR));
            cal.setEventFeedLink(jsonCalendar.getString(GoogleCalendar.FIELD_EVENT_FEED_LINK));
            cal.setId(jsonCalendar.getString(GoogleCalendar.FIELD_ID));
            cal.setSelfLink(jsonCalendar.getString(GoogleCalendar.FIELD_SELF_LINK));
            cal.setTimeZone(TimeZone.getTimeZone(jsonCalendar.getString(GoogleCalendar.FIELD_TIMEZONE)));
            cal.setTitle(jsonCalendar.getString(GoogleCalendar.FIELD_TITLE));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return cal;
    }
    
    
    /**
     * Get events from googleCalendar
     * @param calendar calendar which contains events
     * @param begin Begin date
     * @param end End date
     * @return list of events from calendar between begin and end dates
     */
    public List<GoogleEvent> getEvents (CalendarResource calendar, Calendar begin, Calendar end) {
        ArrayList<GoogleEvent> events = new ArrayList<GoogleEvent>();
        
       
        String googleDateInit = formatToGoogleDateTime(begin);
        String googleDateEnd = formatToGoogleDateTime(end);
        
        String url = calendar.getEventFeedLink() 
                           +  "?alt=jsonc&start-min=" + googleDateInit
                           + "&start-max="+ googleDateEnd
                           + "&ctz=Europe/Madrid" ;
                
        String[] paramsKey =  {"Authorization"};
        String[] paramsValue = {"Bearer " + mSessionManager.getActiveUserAccessToken()};
        
               
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
            Log.d(TAG, "MESSAGE => " + jsonData.toString());
            JSONArray jsonEventsList = (JSONArray) jsonData.getJSONArray("items");
                        
            int ilength = jsonEventsList.length();

            JSONObject jsonEvent;
            for (int j = 0; j < ilength; j++) {
                jsonEvent = (JSONObject) jsonEventsList.get(j);
                events.add( parseEvent(jsonEvent));               
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

    /**
     * Format a Calendar to a google valid date
     * @param begin Example 2012-07-07 11:15:19+02:00
     * @return output example: 2012-07-07T11%3A15%3A19%2B02%3A00
     */
    private String formatToGoogleDateTime(Calendar begin) {
        String preFormattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(begin.getTime());
        StringBuffer sb = new StringBuffer(preFormattedDate); 
        
        sb.insert(22, ':');
        String googleFormattedDate = Uri.encode(sb.toString());
        
        Log.d(TAG, "Date to google"+ googleFormattedDate);
        return googleFormattedDate;
    }
    
    
    /**
     * Converts a string form GoogleCalendar date o dateTime into a calendar.
     * @param dateTime string as date or dateTime
     * @return a Calendar representation of input string
     * @throws ParseException in case of unparseable string
     */
    private Calendar parseGoogleDate(String dateTime) throws ParseException{
        SimpleDateFormat formatter = mDateTimeFormatter;
        if(isCompleteDayEvent(dateTime)){
            formatter = mDateFormatter;
        }
        Calendar evCalendar = new GregorianCalendar();
        evCalendar.setTime(formatter.parse(dateTime));
        return evCalendar;
    }

    /**
     * Check if event represents a complete day
     * @param dateTime string
     * @return true: Means that string type is Date (complete day). false: Means that string type is DateTime
     */
    private boolean isCompleteDayEvent(String dateTime) {
        return dateTime.length() == 10;
    }
    
    /**
     * Creates a Google Event
     * @param calendar calendar where events is created
     * @param event event to event
     * @return returning event from Google with more data, like generated id
     */
    public GoogleEvent setEvent(CalendarResource calendar, GoogleEvent event) {
        
        JSONObject data = new JSONObject();
        JSONObject attendee = new JSONObject();
        JSONArray attendeeList = new JSONArray();
        JSONObject when = new JSONObject();
        JSONArray whenList = new JSONArray();
        boolean retValue = true;
        GoogleEvent gEvent = null;
        try {
            attendee.put(GoogleCalendar.FIELD_RESOURCE, true);
            attendee.put(User.FIELD_DISPLAY_NAME,calendar.getTitle());
            String email = getEmailFromCalendarID(calendar);
            attendee.put(User.FIELD_EMAIL, email);
            attendeeList.put(attendee);
            
            when.put(GoogleEvent.FIELD_BEGIN, mDateTimeFormatter.format(event.getBegin().getTime()));
            when.put(GoogleEvent.FIELD_END,   mDateTimeFormatter.format(event.getEnd().getTime()));
            whenList.put(when);
            
            data.put(GoogleEvent.FIELD_TITLE, event.getTitle());
            data.put(GoogleEvent.FIELD_DETAILS, event.getDetails());
            data.put(GoogleEvent.FIELD_STATUS, event.getStatus());
            data.put(GoogleEvent.FIELD_LOCATION, calendar.getTitle());
            data.put(GoogleEvent.FIELD_ATTENDEES, attendeeList);
            data.put(GoogleEvent.FIELD_WHEN_LIST, whenList);
            data = new JSONObject().put("data", data);
    
            
            String[] paramsKey =   {"Authorization"};
            String[] paramsValue = {"Bearer " + mSessionManager.getActiveUserAccessToken()};
            Log.d(TAG, data.toString());
            StringEntity stringEntity = new StringEntity(data.toString());
            stringEntity.setContentType("application/json");
            String googleResponse =  ConnectionUtils.doHttpsPost(GoogleConstants.URL_INSERT_EVENT, paramsKey, paramsValue, stringEntity);
            
            Log.d(TAG, "RESPONSE => " + googleResponse);
            
            JSONObject jsonDataObj = (JSONObject) new JSONTokener(googleResponse).nextValue();

            JSONObject jsonData = jsonDataObj.getJSONObject("data");
            Log.d(TAG, "MESSAGE => " + jsonData.toString());

            gEvent = parseEvent(jsonData);
            
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            retValue = false;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            retValue = false;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            retValue = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            retValue = false;
        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
        return retValue ? gEvent : null;
    }
    
    
    /**
     * Converts a JSON Object to GoogleEvent
     * @param jsonEvent Google Event in JSON format
     * @return GoogleEvent object
     * @throws JSONException Error parsing JSON Object
     * @throws ParseException Error parsing date objects
     */
    public GoogleEvent parseEvent(JSONObject jsonEvent) throws JSONException, ParseException{
        GoogleEvent ev = new GoogleEvent();
        
        Log.d(TAG, " ========> " +jsonEvent.toString());
        ev.setAlternateLink(jsonEvent.getString(GoogleEvent.FIELD_ALTERNATIVE_LINK));
        ev.setCanEdit(jsonEvent.getBoolean(GoogleEvent.FIELD_CAN_EDIT));
        ev.setDetails(jsonEvent.getString(GoogleEvent.FIELD_DETAILS));
        ev.setId(jsonEvent.getString(GoogleEvent.FIELD_ID));
        ev.setLocation(jsonEvent.optString(GoogleEvent.FIELD_LOCATION));
        ev.setSelfLink(jsonEvent.getString(GoogleEvent.FIELD_SELF_LINK));
        ev.setStatus(jsonEvent.getString(GoogleEvent.FIELD_STATUS));
        ev.setTitle(jsonEvent.getString(GoogleEvent.FIELD_TITLE));
       
        JSONArray listWhen  = (JSONArray) jsonEvent.getJSONArray(GoogleEvent.FIELD_WHEN_LIST);
        
        Calendar evBegin = parseGoogleDate(((JSONObject)listWhen.get(0)).getString(GoogleEvent.FIELD_BEGIN));
        Calendar evEnd = parseGoogleDate(((JSONObject)listWhen.get(0)).getString(GoogleEvent.FIELD_END));
        
        ev.setBegin(evBegin);
        ev.setEnd(evEnd);
        
        JSONObject jsonUser = (JSONObject) jsonEvent.getJSONObject(GoogleEvent.FIELD_CREATOR);
        ev.setCreator(new User(jsonUser.getString(User.FIELD_DISPLAY_NAME), jsonUser.getString(User.FIELD_EMAIL)));
        
        JSONArray listAttendees = (JSONArray) jsonEvent.getJSONArray(GoogleEvent.FIELD_ATTENDEES);
        for (int indexAtendees = 0; indexAtendees < listAttendees.length();indexAtendees++) {
            jsonUser = (JSONObject) listAttendees.get(indexAtendees);
            ev.addAttendee(new User(jsonUser.getString(User.FIELD_DISPLAY_NAME), jsonUser.getString(User.FIELD_EMAIL)));
        }
        
        Log.d(TAG, "EVENT => "+ ev);
        return ev;
    }

    /**
     * Email is a part of ID URL of a google calendar
     * @param calendar ResourceCalendar
     * @return email
     */
    private String getEmailFromCalendarID(CalendarResource calendar) {
        String email = calendar.getId().replace("http://www.google.com/calendar/feeds/default/allcalendars/full/","")
        .replace("%40", "@");
        return email;
    }
    
    
    /**
     * Delete event from google
     * @param gEvent event to delete
     */
    public void deleteEvent(GoogleEvent gEvent){
        String eventURL = GoogleConstants.URL_INSERT_EVENT + gEvent.getId();
        Log.d(TAG, eventURL);
        
        //If-Match: * header allow to delete an event, even if it was modified after its insertion
        String[] paramsKey =   {"Authorization","If-Match"};
        String[] paramsValue = {"Bearer " + mSessionManager.getActiveUserAccessToken(),"*"};
        boolean response = ConnectionUtils.doHttpsDelete(eventURL, paramsKey, paramsValue);
        Log.d(TAG, "gConnector response : " + Boolean.toString(response));
        
    }
}