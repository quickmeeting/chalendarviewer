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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.necora.quickmeeting.contentprovider.ConfigColumns;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage resources (calendars)
 * @author vitor
 */
public class ConfigManager {

    /** Log tag */
    static private final String TAG = ConfigManager.class.toString();
    
    /** instance reference */
    private static ConfigManager sInstance = null;
    /** app context */
    private Context mContext;
    /** QuickMeeting Provider object */
    private ContentResolver mProvider;
    /** */
    private  Map<String, String> mConfigMap = null;
    
    /**
     * Returns a valid ConfigManager
     * @param context application context
     * @return a valid ConfigManager
     */
    public static synchronized ConfigManager getInstance(Context context){        
        if (sInstance == null) {
            sInstance = new ConfigManager(context);
        }

        return sInstance;
    }
    
    /**
     * Return an updated Map of resources (Calendars)
     * @return an  updated Map of resources
     */
    private Map<String, String> getConfigMap() {
        if( mConfigMap == null){
            refreshConfigMap();
        }
        return mConfigMap;
    }

    /**
     * Cache properties from db
     */
    private void refreshConfigMap() {
        mConfigMap = new HashMap<String, String>();
        
        // Form an array specifying which columns to return. 
        String[] projection = new String[] {
                ConfigColumns._ID,
                ConfigColumns.PROPERTY,
                ConfigColumns.VALUE,
        };
        

        // Make the query. 
        Cursor managedCursor = mProvider.query(ConfigColumns.CONTENT_URI,
                projection, // Which columns to return 
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null);       

        Log.d(TAG, Integer.toString(managedCursor.getCount()));

        if (managedCursor.moveToFirst()) {

            String propertyName ="" ;
            String propertyValue = "";
            int properyNameColumn = managedCursor.getColumnIndex(ConfigColumns.PROPERTY); 
            int propertyValueColumn = managedCursor.getColumnIndex(ConfigColumns.VALUE);

            Log.d(TAG, "LINK\t\tNAME\t\tDISPLAY NAME");
            do {
                // Get the field values
                propertyName = managedCursor.getString(properyNameColumn);
                propertyValue = managedCursor.getString(propertyValueColumn);
                
                this.mConfigMap.put(propertyName, propertyValue);
                Log.d(TAG, propertyName + "\t\t" + propertyValue);

            } while (managedCursor.moveToNext());

        }
        
    }

    /**
     * Private constructor for Config Manager singleton
     * @param context app context
     */
    private ConfigManager(Context context){
        //TODO return exception if it is null
        mProvider = context.getContentResolver();
        mContext = context;
    }
    
    /**
     * Get a property value from property name
     * @param property property name
     * @return property value
     */
    public String getProperty(String property) {
        return getConfigMap().get(property);
    }

    /**
     * Modifies the value of a property
     * @param property property name
     * @param value value
     */
    public void putProperty(String property, String value){
        this.getConfigMap().put(property, value);
        this.updatePropertyInDatabase(property, value);
    }

    /**
     * Update a property in database
     * @param property property name
     * @param value property value
     */
    private void updatePropertyInDatabase(String property, String value) {
        ContentValues values = new ContentValues();

        values.put(ConfigColumns.VALUE, value);
        
        String where = ConfigColumns.PROPERTY + "=? ";
        String[] whereParams = new String[]{property}; 
        int result = mProvider.update(ConfigColumns.CONTENT_URI, values, where, whereParams);
        
        Log.d(TAG, "Result update: " + result);
        
    }
    
}
