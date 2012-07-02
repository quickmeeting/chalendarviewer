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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.ch.chalendarviewer.contentprovider.AuthUser;
import org.ch.chalendarviewer.contentprovider.Resource;
import org.ch.chalendarviewer.objects.CalendarResource;
import org.ch.chalendarviewer.objects.Event;
import org.ch.chalendarviewer.objects.google.GoogleCalendar;
import org.ch.chalendarviewer.objects.google.GoogleEvent;
import org.ch.chalendarviewer.service.exception.SyncFailedException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage resources (calendars)
 * @author vitor
 */
public class ResourceManager {

    /** Log tag */
    static private final String TAG = ResourceManager.class.toString();
    
    /** instance reference */
    private static ResourceManager sInstance = null;
    /** user manager reference */
    private UserManager mUserManager = null;
    /** app context */
    private Context mContext;
    /** Chalendar Provider object */
    private ContentResolver mProvider;
    /** List of active calendar resources */
    private List<CalendarResource> activeResources = null;    
    /** Hash with calendar ids and calendars */
    private  Map<String, GoogleCalendar> resourceMap = null;
    
    /**
     * Return an updated Map of resources (Calendars)
     * @return an  updated Map of resources
     */
    private Map<String, GoogleCalendar> getResourceMap() {
        if( resourceMap == null){
            refreshActiveResources();
        }
        return resourceMap;
    }

    /**
     * Private constructor for Resource Manager singleton
     * @param context app context
     */
    private ResourceManager(Context context){
        //TODO return exception if it is null
        mProvider = context.getContentResolver();
        mUserManager = UserManager.getInstance(context);
        mContext = context;
    }
    
    /**
     * Get resources cursor.  Each row has following columns: ID, NAME, ACTIVE
     * @return a cursor of resources.
     */
    public Cursor getResources(){
        
        // Form an array specifying which columns to return. 
        String[] projection = new String[] {
                Resource._ID,
                Resource.NAME,
                Resource.ACTIVE
        };
        

        // Get the base URI for the Resources table.
        Uri resourceUri = Uri.parse(AuthUser.CONTENT_URI + "/" + mUserManager.getActiveUserId() + "/" + "resources"); 

        // Make the query. 
        Cursor managedCursor = mProvider.query(resourceUri,
                projection, // Which columns to return 
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                // Put the results in ascending order by email
                Resource._ID + " ASC"); 
        
        return managedCursor;
    }
    
    
    /**
     * Get list of links FromGoogle and add new calendars to database
     * @return A list of calendar link for current active user
     */
    private List<String> loadLinksFromGoogle() {
        
        //Calendar links (google)
        List<String> gLinks = new ArrayList<String>();
        //Calendar links (db)
        List<String> dbLinks = getResourceLinksFromDatabase();

        //Get all calendars from google.
        GoogleCalendarApiConnector gConector = GoogleCalendarApiConnector.getInstance(mContext);
        List<GoogleCalendar> calendars = gConector.getCalendars();
        
        for(GoogleCalendar calendar: calendars){
            String link = calendar.getSelfLink();
            gLinks.add(link);
            
            if( !dbLinks.contains(link) ){
                addResourceToDatabase(calendar);
            }
        }
        
        return gLinks;
    }

    /**
     * Get a list of calendar links from database
     * @return a list of resources links from database
     */
    private List<String> getResourceLinksFromDatabase(){
        
        List<String> resourceLinks = new ArrayList<String>();
        
        // Form an array specifying which columns to return. 
        String[] projection = new String[] {
                Resource.LINK,
        };

        // Get the base URI for the Resources table.
        Uri resourceUri = Uri.parse(AuthUser.CONTENT_URI + "/" + mUserManager.getActiveUserId() + "/" + "resources"); 

        // Make the query. 
        Cursor managedCursor = mProvider.query(resourceUri,
                projection, // Which columns to return 
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                // Put the results in ascending order by email
                Resource._ID + " ASC");       

        if (managedCursor.moveToFirst()) {

            int linkColumn = managedCursor.getColumnIndex(Resource.LINK); 
            
            do {
                String link = managedCursor.getString(linkColumn);
                resourceLinks.add(link);

                Log.d(TAG, "Google calendar loaded from db: "+ link);

            } while (managedCursor.moveToNext());

        }
        
        return resourceLinks;
    }
    
    /**
     * Add a google calendar to database
     * @param calendar calendar to add
     */
    private void addResourceToDatabase(GoogleCalendar calendar) {
        ContentValues values = new ContentValues();

        values.put(Resource.NAME, calendar.getTitle());
        values.put(Resource.LINK, calendar.getSelfLink());
        values.put(Resource.DISPLAY_NAME, calendar.getTitle());
        values.put(Resource.ACTIVE, false);
        Uri resources = Uri.parse(AuthUser.CONTENT_URI + "/" + mUserManager.getActiveUserId() +"/" + "resources"); 
        Uri uri = mProvider.insert(resources, values);
        Log.d(TAG, "New calendar inserted: " + uri);
    }

    
    /**
     * Synchronizes google and database resources.
     * Add new calendars from google.
     * Delete old calendars stored in database 
     * @throws SyncFailedException When synchronizatoin fails
     */
    public void syncResources() throws SyncFailedException{
        
        try{
            //Get Calendars from google (and add the new ones)
            List<String> googleLinks = loadLinksFromGoogle();
            
            //build a where clause, to exclude calendars that exists in db, but not in google 
            String where = buildWhereClause(googleLinks);

            Uri resources = Uri.parse(AuthUser.CONTENT_URI + "/" + mUserManager.getActiveUserId() +"/" + "resources"); 

            int result = mProvider.delete(resources, where, null);
            Log.d(TAG, "Number of resources to delete: " + result);
            
        } catch (Exception e){
            throw new SyncFailedException("Cannot synchronize calendars with Google", e);
        }
        
    }

    /**
     * build a where clause, to exclude calendars that exists in db, but not in google 
     * @param googleLinks calendars that exists in google
     * @return where clause of type "not in ('http://calendar1...', 'http://calendar2');
     */
    private String buildWhereClause(List<String> googleLinks) {
        
        //Build Comma separated values (calendar links) 
        StringBuilder sb = new StringBuilder();
        for(String link : googleLinks){
            sb.append("'").append(link).append("',");
        }
        if(sb.length()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        
        String where = Resource.LINK + " not in (" + sb.toString() + ")";
        return where;
    }
    
    /**
     * Changes the active state of a resource
     * @param id resource id
     * @param state active/not active
     */
    public void changeResourceActive(String id, boolean state){
        ContentValues values = new ContentValues();

        values.put(Resource.ACTIVE, state);
        
        Uri resources = Uri.parse(AuthUser.CONTENT_URI + "/" + mUserManager.getActiveUserId() + "/" + "resources/" + id); 
        int result = mProvider.update(resources, values, null, null);
        Log.d(TAG, "Result update: " + result);
        activeResources = null;
    }


    /**
     * Returns a valid ResourceManager
     * @param context application context
     * @return a valid ResourceManager
     */
    public static synchronized ResourceManager getInstance(Context context){        
        if (sInstance == null) {
            sInstance = new ResourceManager(context);
        }

        return sInstance;
    }
    
    /**
     * List of active calendar resources
     */
    private  void refreshActiveResources(){
        activeResources = new ArrayList<CalendarResource>();
        resourceMap = new HashMap<String, GoogleCalendar>();

        // Form an array specifying which columns to return. 
        String[] projection = new String[] {
                Resource._ID,
                Resource.NAME,
                Resource.LINK
        };

        
        // Get the base URI for the Resources table.
        Uri resourceUri = Uri.parse(AuthUser.CONTENT_URI + "/" + mUserManager.getActiveUserId() + "/" + "resources"); 

        String where = Resource.ACTIVE + "=?";
        String[] whereParams = new String[]{"1"};
        
        // Make the query. 
        Cursor managedCursor = mProvider.query(resourceUri,
                projection, // Which columns to return 
                where,       // Which rows to return (all rows)
                whereParams,       // Selection arguments (none)
                // Put the results in ascending order by email
                Resource._ID + " ASC");       

        if (managedCursor.moveToFirst()) {

            int idColumn   = managedCursor.getColumnIndex(Resource._ID);
            int nameColumn = managedCursor.getColumnIndex(Resource.NAME); 
            int linkColumn = managedCursor.getColumnIndex(Resource.LINK);
            
            do {
                String id = managedCursor.getString(idColumn);
                String name = managedCursor.getString(nameColumn);
                String link = managedCursor.getString(linkColumn);
                GoogleCalendar gCalendar = new GoogleCalendar(id,name,link);
                activeResources.add(gCalendar);
                resourceMap.put(id,gCalendar);

                Log.d(TAG, "Resource calendar loaded from db: " + id + "/" + name );
            } while (managedCursor.moveToNext());
        }
    }

    /**
     * Return a list of active resources
     * @return List of active resources
     */
    public List<CalendarResource> getActiveResources(){
        if (activeResources == null){
            refreshActiveResources();
        }
        return activeResources;
    }
    
    /**
     * Get events of a resource 
     * @param resourceId id of resource
     * @param begin Calendar begin
     * @param end Calendar end
     * @return list of events of selected resource in interval (begin,end)
     */
    public List<? extends Event> getEvents(String resourceId, Calendar begin, Calendar end){
        
        GoogleCalendar gCalendar = getResourceMap().get(resourceId);
        
        GoogleCalendarApiConnector gConector = GoogleCalendarApiConnector.getInstance(mContext);
        
        GoogleCalendar completeGCalendar = gConector.getCalendarByLink(gCalendar.getSelfLink());
        return gConector.getEvents(completeGCalendar, begin, end);
        
    }
    
    /**
     * Get resource link from database
     * @param resourceId id of resource
     * @return link
     */
    public String getResourceLinkFromDatabase(String resourceId) {
        
        String link = null;
        String[] projection = new String[] {
                Resource._ID,
                Resource.LINK
        };

        // Get the base URI for the Resources table.
        Uri resourceUri = Uri.parse(AuthUser.CONTENT_URI + "/" + mUserManager.getActiveUserId() + "/" + "resource" + "/" + resourceId); 

        
        // Make the query. 
        Cursor managedCursor = mProvider.query(resourceUri,
                projection, // Which columns to return 
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                // Put the results in ascending order by email
                Resource._ID + " ASC");       

        if (managedCursor.moveToFirst()) {

            int idColumn   = managedCursor.getColumnIndex(Resource._ID);
            int nameColumn = managedCursor.getColumnIndex(Resource.NAME);

            String id = managedCursor.getString(idColumn);
            link = managedCursor.getString(nameColumn);
            
            Log.d(TAG, "Resource calendar loaded from db: " + id + "/" + link );
        }
        
        return link;
    }
    
    /**
     * Create an event in a specified resource
     * @param resourceId id of resource
     * @param event event data
     */
    public void createEvent(String resourceId, Event event){
        GoogleCalendar gCalendar = getResourceMap().get(resourceId);

        GoogleCalendarApiConnector gConector = GoogleCalendarApiConnector.getInstance(mContext);

        GoogleCalendar completeGCalendar = gConector.getCalendarByLink(gCalendar.getSelfLink());
        
        Log.d(TAG, Boolean.toString(gConector.setEvent(completeGCalendar, new GoogleEvent(event))));
    }


}
