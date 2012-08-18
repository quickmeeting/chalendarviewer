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

package com.necora.quickmeeting.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.necora.quickmeeting.contentprovider.AccountColumns;
import com.necora.quickmeeting.contentprovider.DatabaseHelper;


import java.util.HashMap;

/**
 * Content provider for configuration data
 * @author vitor
 *
 */
public class QuickMeetingContentProvider extends ContentProvider {

    /** Content provider authority */
    public static final String AUTHORITY = "com.necora.quickmeeting.contentprovider.QuickMeetingContentProvider";

    /** Check correct URIs */
    private static final UriMatcher sUriMatcher;

    /** Map columns for authUser*/
    private static HashMap<String, String> authUserProjectionMap;
    
    /** Map columns for resource*/
    private static HashMap<String, String> resourceProjectionMap;
    
    /** Map columns for config*/
    private static HashMap<String, String> configProjectionMap;
    
    /** Database Helper */
    private DatabaseHelper dbHelper;
    
    /** AUTH_USERS constant for Uri matcher*/
    private static final int AUTH_USERS = 1;
    /** AUTH_USER_ID constant for Uri matcher*/
    private static final int AUTH_USER_ID = 2;
    /** AUTH_USER_RESOURCES constant for Uri matcher*/
    private static final int AUTH_USER_RESOURCES = 3;
    /** AUTH_USER_RESOURCE_ID constant for Uri matcher*/
    private static final int AUTH_USER_RESOURCE_ID = 4;
    /** CONFIG constant for Uri matcher*/
    private static final int CONFIG = 5;
    /** CONFIG constant for Uri matcher*/
    private static final int CONFIG_ID = 6;
    /** Reset config propertiesr*/
    private static final int CONFIG_RESET = 7;
    
    
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.AUTH_USER_TABLE_NAME, AUTH_USERS);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.AUTH_USER_TABLE_NAME + "/#", AUTH_USER_ID);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.AUTH_USER_TABLE_NAME + "/#/"
                + DatabaseHelper.RESOURCE_TABLE_NAME, AUTH_USER_RESOURCES);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.AUTH_USER_TABLE_NAME + "/#/"
                + DatabaseHelper.RESOURCE_TABLE_NAME + "/#", AUTH_USER_RESOURCE_ID);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.CONFIG_TABLE_NAME, CONFIG);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.CONFIG_TABLE_NAME + "/#", CONFIG_ID);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.RESET_CONFIG, CONFIG_RESET);

        authUserProjectionMap = new HashMap<String, String>();
        authUserProjectionMap.put(AccountColumns._ID, AccountColumns._ID);
        authUserProjectionMap.put(AccountColumns.ACCESS_TOKEN, AccountColumns.ACCESS_TOKEN);
        authUserProjectionMap.put(AccountColumns.REFRESH_TOKEN, AccountColumns.REFRESH_TOKEN);
        authUserProjectionMap.put(AccountColumns.EMAIL, AccountColumns.EMAIL );
        authUserProjectionMap.put(AccountColumns.ACTIVE_USER, AccountColumns.ACTIVE_USER );
        authUserProjectionMap.put(AccountColumns.EXPIRATION_DATE, AccountColumns.EXPIRATION_DATE );
        
        resourceProjectionMap = new HashMap<String, String>();
        resourceProjectionMap.put(ResourceColumns._ID, ResourceColumns._ID);
        resourceProjectionMap.put(ResourceColumns.AUTH_USER_ID, ResourceColumns.AUTH_USER_ID);
        resourceProjectionMap.put(ResourceColumns.LINK, ResourceColumns.LINK);
        resourceProjectionMap.put(ResourceColumns.EVENTS_LINK, ResourceColumns.EVENTS_LINK);
        resourceProjectionMap.put(ResourceColumns.NAME, ResourceColumns.NAME);
        resourceProjectionMap.put(ResourceColumns.DISPLAY_NAME, ResourceColumns.DISPLAY_NAME);
        resourceProjectionMap.put(ResourceColumns.ACTIVE, ResourceColumns.ACTIVE);
        
        configProjectionMap = new HashMap<String, String>();
        configProjectionMap.put(ConfigColumns._ID, ConfigColumns._ID);
        configProjectionMap.put(ConfigColumns.PROPERTY, ConfigColumns.PROPERTY);
        configProjectionMap.put(ConfigColumns.VALUE, ConfigColumns.VALUE);
        configProjectionMap.put(ConfigColumns.DEFAULT, ConfigColumns.DEFAULT);
    }
    
    
    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }
    
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case AUTH_USERS:
                count = db.delete(DatabaseHelper.AUTH_USER_TABLE_NAME, where, whereArgs);
                break;
            case AUTH_USER_RESOURCES:
                //ID of user is passed in URI
                where += " and " + ResourceColumns.AUTH_USER_ID + "=" + uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.RESOURCE_TABLE_NAME, where, whereArgs);
                break;
            case CONFIG:
                count = db.delete(DatabaseHelper.CONFIG_TABLE_NAME, where, whereArgs);
                break;
            case CONFIG_ID:
                //ID of user is passed in URI
                where += " and " + ConfigColumns._ID + "=" + uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.CONFIG_TABLE_NAME, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case AUTH_USERS:
                return AccountColumns.CONTENT_TYPE;
            case AUTH_USER_RESOURCES:
                return ResourceColumns.CONTENT_TYPE;
            case CONFIG:
                return ConfigColumns.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        String id= null;
        String table = null;
        
        switch (sUriMatcher.match(uri)) {
            case AUTH_USERS:
                table = DatabaseHelper.AUTH_USER_TABLE_NAME; 
                id = AccountColumns._ID;
                break;
            case AUTH_USER_RESOURCES:
                table = DatabaseHelper.RESOURCE_TABLE_NAME;
                id = ResourceColumns._ID;
                //ID of user is passed on URI
                values.put(ResourceColumns.AUTH_USER_ID, uri.getPathSegments().get(1));
                break;
            case CONFIG:
                table = DatabaseHelper.CONFIG_TABLE_NAME; 
                id = ConfigColumns._ID;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(table, id, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case AUTH_USERS:
                qb.setTables(DatabaseHelper.AUTH_USER_TABLE_NAME);
                qb.setProjectionMap(authUserProjectionMap);
                break;
            case AUTH_USER_ID:
                qb.setTables(DatabaseHelper.AUTH_USER_TABLE_NAME);
                //ID of user is passed on URI
                qb.appendWhere(AccountColumns._ID + "=" + uri.getPathSegments().get(1));
                break;
            case AUTH_USER_RESOURCES:
                qb.setTables(DatabaseHelper.RESOURCE_TABLE_NAME);
                //ID of user is passed on URI
                qb.appendWhere(ResourceColumns.AUTH_USER_ID + "=" + uri.getPathSegments().get(1));
                qb.setProjectionMap(resourceProjectionMap);
                break;
            case AUTH_USER_RESOURCE_ID:
                qb.setTables(DatabaseHelper.RESOURCE_TABLE_NAME);
                //ID of user is passed on URI
                qb.appendWhere(ResourceColumns.AUTH_USER_ID + "=" + uri.getPathSegments().get(1));
                qb.appendWhere(ResourceColumns._ID + "=" + uri.getPathSegments().get(3));
                qb.setProjectionMap(resourceProjectionMap);
                break;
            case CONFIG:
                qb.setTables(DatabaseHelper.CONFIG_TABLE_NAME);
                qb.setProjectionMap(configProjectionMap);
                break;
            case CONFIG_ID:
                qb.setTables(DatabaseHelper.CONFIG_TABLE_NAME);
                //ID of user is passed on URI
                qb.appendWhere(ConfigColumns._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 1;
        switch (sUriMatcher.match(uri)) {
            case AUTH_USERS:
                count = db.update(DatabaseHelper.AUTH_USER_TABLE_NAME, values, where, whereArgs);
                break;
            case AUTH_USER_RESOURCES:
                //not using whereArgs to store user_id parameter. It is coded directly on where clause
                where += " and " + ResourceColumns.AUTH_USER_ID + "=" + uri.getPathSegments().get(1);
                count = db.update(DatabaseHelper.RESOURCE_TABLE_NAME, values, where, whereArgs);
                break;
            case AUTH_USER_RESOURCE_ID:
                //not using whereArgs to store user_id parameter. It is coded directly on where clause
                String idCondition = ResourceColumns.AUTH_USER_ID + "=" + uri.getPathSegments().get(1);
                idCondition += " and " + ResourceColumns._ID + "=" + uri.getPathSegments().get(3);
                if(where == null){
                    where = idCondition;
                }else{
                    where = idCondition + " and " + where;
                }
                count = db.update(DatabaseHelper.RESOURCE_TABLE_NAME, values, where, whereArgs);
                break;
            case CONFIG:
                count = db.update(DatabaseHelper.CONFIG_TABLE_NAME, values, where, whereArgs);
                break;
            case CONFIG_ID:
                //not using whereArgs to store user_id parameter. It is coded directly on where clause
                where += " and " + ConfigColumns._ID + "=" + uri.getPathSegments().get(1);
                count = db.update(DatabaseHelper.CONFIG_TABLE_NAME, values, where, whereArgs);
                break;
            case CONFIG_RESET:
                resetConfigProperties();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    
    /**
     * Reset config properties
     */
    private void resetConfigProperties(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("********" , "update " + DatabaseHelper.CONFIG_TABLE_NAME + 
                " set " + ConfigColumns.VALUE + " = " + ConfigColumns.DEFAULT);
        db.execSQL("update " + DatabaseHelper.CONFIG_TABLE_NAME + 
                " set " + ConfigColumns.VALUE + " = " + ConfigColumns.DEFAULT);
    }

}
