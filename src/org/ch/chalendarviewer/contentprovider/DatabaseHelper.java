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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper for database access
 * @author vfdiaz
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /** Auth_user table */
    public static final String AUTH_USER_TABLE_NAME = "auth_users";
    /** Resources table */
    public static final String RESOURCE_TABLE_NAME = "resources";
    /** SQLite Database name */
    private static final String DATABASE_NAME = "config.db";
    /** SQLite Database version */
    private static final int DATABASE_VERSION = 4;
    /** TAG for log entries */
    private static final String TAG = "DatabaseHelper";
    
    /** 
     * Main Constructor
     * @param context context of application
     */
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + AUTH_USER_TABLE_NAME + " (" 
                        + AccountColumns._ID             + " INTEGER PRIMARY KEY AUTOINCREMENT," 
                        + AccountColumns.ACCESS_TOKEN    + " VARCHAR(255)," 
                        + AccountColumns.REFRESH_TOKEN   + " VARCHAR(255)," 
                        + AccountColumns.EMAIL           + " VARCHAR(255),"
                        + AccountColumns.ACTIVE_USER     + " BOOLEAN,"
                        + AccountColumns.EXPIRATION_DATE + " DATETIME);");
        
        db.execSQL("CREATE TABLE " + RESOURCE_TABLE_NAME + " (" 
                + ResourceColumns._ID             + " INTEGER PRIMARY KEY AUTOINCREMENT," 
                + ResourceColumns.AUTH_USER_ID    + " VARCHAR(255)," 
                + ResourceColumns.NAME            + " VARCHAR(255)," 
                + ResourceColumns.LINK            + " VARCHAR(255),"
                + ResourceColumns.ACTIVE          + " BOOLEAN,"
                + ResourceColumns.DISPLAY_NAME    + " VARCHAR(255));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + AUTH_USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RESOURCE_TABLE_NAME);
        onCreate(db);
    }
}
