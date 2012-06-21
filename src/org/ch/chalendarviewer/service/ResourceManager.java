package org.ch.chalendarviewer.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.ch.chalendarviewer.contentprovider.AuthUser;
import org.ch.chalendarviewer.contentprovider.Resource;
import org.ch.chalendarviewer.objects.GoogleCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage resources (calendars)
 * @author vitor
 *
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
                addCalendarToDatabase(calendar);
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
    private void addCalendarToDatabase(GoogleCalendar calendar) {
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
     * @return a list of resources links from database
     */
    public List<String> syncResources(){
        
        //Get Calendars from google (and add the new ones)
        List<String> googleLinks = loadLinksFromGoogle();
        //Initialize calendars from database
        List<String> resourceLinks = new ArrayList<String>();
        
        //build a where clause, to exclude calendars that exists in db, but not in google 
        String where = buildWhereClause(googleLinks);
        
        Uri resources = Uri.parse(AuthUser.CONTENT_URI + "/" + mUserManager.getActiveUserId() +"/" + "resources"); 
        
        int result = mProvider.delete(resources, where, null);
        Log.d(TAG, "Number of resources to delete: " + result);
        
        return resourceLinks;
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
    
    
}
