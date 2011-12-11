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

package org.ch.chalendarviewer.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.ch.chalendarviewer.contentprovider.AuthUser;
import org.ch.chalendarviewer.contentprovider.DatabaseHelper;

import java.util.HashMap;

/**
 * Content provider for configuration data
 * @author vitor
 *
 */
public class ChalendarContentProvider extends ContentProvider {

    /** Content provider authority */
    public static final String AUTHORITY = "org.ch.chalendarviewer.contentprovider.ChalendarContentProvider";

    /** Check correct URIs */
    private static final UriMatcher sUriMatcher;

    /** Map columns for authUser*/
    private static HashMap<String, String> authUserProjectionMap;
    
    /** Database Helper */
    private DatabaseHelper dbHelper;
    
    /** AUTH_USERS constant */
    private static final int AUTH_USERS = 1;
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.AUTH_USER_TABLE_NAME, AUTH_USERS);

        authUserProjectionMap = new HashMap<String, String>();
        authUserProjectionMap.put(AuthUser._ID, AuthUser._ID);
        authUserProjectionMap.put(AuthUser.ACCESS_TOKEN, AuthUser.ACCESS_TOKEN);
        authUserProjectionMap.put(AuthUser.REFRESH_TOKEN, AuthUser.REFRESH_TOKEN);
        authUserProjectionMap.put(AuthUser.EMAIL, AuthUser.EMAIL );
        authUserProjectionMap.put(AuthUser.ACTIVE_USER, AuthUser.ACTIVE_USER );
        authUserProjectionMap.put(AuthUser.EXPIRATION_DATE, AuthUser.EXPIRATION_DATE );
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
                return AuthUser.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != AUTH_USERS) { throw new IllegalArgumentException("Unknown URI " + uri); }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(DatabaseHelper.AUTH_USER_TABLE_NAME, AuthUser._ID, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(AuthUser.CONTENT_URI, rowId);
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
        int count;
        switch (sUriMatcher.match(uri)) {
            case AUTH_USERS:
                count = db.update(DatabaseHelper.AUTH_USER_TABLE_NAME, values, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
