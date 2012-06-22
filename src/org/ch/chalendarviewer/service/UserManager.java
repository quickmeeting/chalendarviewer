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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.ch.chalendarviewer.contentprovider.AuthUser;
import org.ch.chalendarviewer.contentprovider.ChalendarContentProvider;
import org.ch.chalendarviewer.objects.User;
import org.ch.chalendarviewer.util.ConnectionUtils;
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
import java.util.logging.SimpleFormatter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;


public class UserManager {

     static private final String TAG = UserManager.class.toString();

    /** instance reference */
    private static UserManager sInstance = null;
        
    /** Active user id */
    private String mUserId = "";
    
    /** Active user Mail */
    private String mUserMail = "";
    
    /** Active user access Token */
    private String mAccessToken;
    
    /** Refresh Token value */
    private String mRefreshToken;
    
    /** ExpirationDate value */
    private Date   mExpirationDate;
    
    /** Data Formatter */
    private SimpleDateFormat mDateFormatter;

    /** Chalendar Provider object */
    private ContentResolver mProvider;    
    
    /**
     * Internal Constructor
     */
    private UserManager(Context context) {
        
        // use the sqlite format for date
        mDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        
        //TODO return exception if it is null
        mProvider = context.getContentResolver();
        
        recoverDataFromDataBase();
    }
    
    private String getUserMail(String accessToken) {
        
        String userMail = null;
        
        String[] paramsKey =  {"Authorization"};
        String[] paramsValue = {"Bearer " + accessToken};
        
        String googleResponse = null;
        try {
            googleResponse = ConnectionUtils.getHttpsGetConnection(GoogleConstants.URL_USER_INFO, paramsKey, paramsValue);
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
            userMail = (jsonUserObj.getString(User.FIELD_EMAIL));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return userMail;
    }
    
    /**
     * Get data from database
     * @return true if data is recovered, false otherwise
     */
    private boolean recoverDataFromDataBase(){
        
        // return value
        boolean dataRecovered = false;
        
        // Form an array specifying which columns to return. 
        String[] projection = new String[] {
                AuthUser._ID,
                AuthUser.EMAIL,
                AuthUser.ACCESS_TOKEN,
                AuthUser.REFRESH_TOKEN,
                AuthUser.EXPIRATION_DATE
        };
        String whereClause = AuthUser.ACTIVE_USER + " = ?";
        String[] whereArgs = new String[]{"1"};
        
        // Make the query. 
        Cursor managedCursor = mProvider.query(
                AuthUser.CONTENT_URI, // uri                
                projection,           // Which columns to return 
                whereClause,          // Which rows to return (active user)
                whereArgs,            // Selection arguments 
                null                  // Put the results in ascending order by email
        );       
        
        // is there at least one user?
        if (managedCursor.moveToFirst()) {
            
            int userIdColumn         = managedCursor.getColumnIndex(AuthUser._ID);
            int userMailColumn       = managedCursor.getColumnIndex(AuthUser.EMAIL);
            int accessTokenColumn    = managedCursor.getColumnIndex(AuthUser.ACCESS_TOKEN);
            int refreshTokenColumn   = managedCursor.getColumnIndex(AuthUser.REFRESH_TOKEN);
            int expirationDateColumn = managedCursor.getColumnIndex(AuthUser.EXPIRATION_DATE);
                        
            try {
                mUserId         = managedCursor.getString(userIdColumn);
                mUserMail       = managedCursor.getString(userMailColumn);
                mAccessToken    = managedCursor.getString(accessTokenColumn);
                mRefreshToken   = managedCursor.getString(refreshTokenColumn);
                mExpirationDate = mDateFormatter.parse(managedCursor.getString(expirationDateColumn));
                dataRecovered = true;
                
                // for each cursor that you do not close, a little panda dies..
                managedCursor.close();
                
            } catch (ParseException e) {
                // failed to recover expiration date, get new token
                mExpirationDate = Calendar.getInstance().getTime();                
                dataRecovered = refreshToken();
            }
        } else {
            // no users
            dataRecovered = false;
        }        
        
        return dataRecovered;        
    }
    
    /**
     * Refresh access Token
     * @return true if refresh is ok, false otherwise
     */
    private boolean refreshToken() {
        
        // temporary Calendar
        Calendar tCalendar = Calendar.getInstance();
        
        // temporary json object
        JSONObject jsonTokenObj;
        
        // temporary access Token var
        String accessToken = null;
        
        // temporary expiration Date var
        Date expirationDate = null;
        
        // return value
        boolean refrehTokenReturn = true;
        

        //verify if token still valid
        if (tCalendar.getTime().before(mExpirationDate) == true) {
            //token is valid
            refrehTokenReturn = true;
        } else {
            
            // fill parameters
            // TODO create constants to these parameters
            String[] paramsKey =   {"cliend_id","client_secret","refresh_token","grant_type"};
            String[] paramsValue = {GoogleConstants.CLIENT_ID,GoogleConstants.CLIENT_SECRET,mRefreshToken,"refresh_token"};
            
            // connect to google and get the response
            String googleResponse = null;
            try {
                googleResponse = ConnectionUtils.getHttpsGetConnection(GoogleConstants.URL_ACCESS_TOKEN, paramsKey, paramsValue);
                
                /************ Parse the response ************/
                jsonTokenObj = (JSONObject) new JSONTokener(googleResponse).nextValue();
                accessToken = jsonTokenObj.getString("access_token");            
                
                // set new expiration time (current time + valid period)
                tCalendar.add(Calendar.SECOND, Integer.parseInt(jsonTokenObj.getString("expires_in")));                
                expirationDate = tCalendar.getTime();
                             
                /************ Content OK, update user */
                if (updateActiveUser(accessToken,expirationDate) == false) {
                    refrehTokenReturn = false;
                }
                
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                refrehTokenReturn = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                refrehTokenReturn = false;
                e.printStackTrace();
            } catch (HttpException e) {
                // TODO Auto-generated catch block
                refrehTokenReturn = false;
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                refrehTokenReturn = false;
                e.printStackTrace();
            }
        }
        
        return refrehTokenReturn;
    }

    /**
     * Update active user with new access token and expiration date to this token
     * @param accessToken - new accessToken
     * @param expirationDate - new Expiration date
     * @return if active user was updated
     */
    private boolean updateActiveUser(String accessToken, Date expirationDate) {

        // return Value
        boolean activeUserWasUpdated = false; 
        
        // data to change
        ContentValues values = new ContentValues();

        values.put(AuthUser.ACCESS_TOKEN, accessToken);
        values.put(AuthUser.EXPIRATION_DATE, mDateFormatter.format(expirationDate));
        
        if (updateUser(mUserMail, values) == true) {
            mAccessToken = accessToken;
            mExpirationDate = expirationDate;
            activeUserWasUpdated = true;
        }
        
        return activeUserWasUpdated;
    }

    /**
     * Update an user with new data
     * @param userMail - user's to update
     * @param values - values to update
     * @return if user was updated
     */
    private boolean updateUser(String userMail, ContentValues values) {
        
        // return Value
        boolean userWasUpdated = false; 

        // where clause
        String where = AuthUser.EMAIL + " = ?";
        String[] whereParams = new String[]{userMail};
        
        // update the row
        int rowsAffected = mProvider.update(AuthUser.CONTENT_URI, values, where, whereParams);
        
        //verify if user is updated
        if (rowsAffected == 1) {
            userWasUpdated = true;
        }
        
        return userWasUpdated;        
    }
    
    
    public boolean addActiveUserToken(String authorizationCode) {
        Log.d(TAG, "addActiveUserToken begin");
        
        // return value
        boolean userWasAdded = false;
        
        // HTML response 
        String HTMLresponse = null;
        
        // Active user Access token
        String accessToken = null;
        
        // Active user Refresh token
        String refreshToken = null;
        
         // Active user's mail
        String userMail = null;
        
        // period in sec's to expirate current token
        Date expirationDate = null;
        
        // temp calendar
        Calendar tCalendar = Calendar.getInstance();
        
        // recover user internal data
        try {
            // fill parameters
            // TODO create constansts
            String[] paramsKey = {"client_id","client_secret","code","redirect_uri","grant_type"};        
            String[] paramsValue = {GoogleConstants.CLIENT_ID, GoogleConstants.CLIENT_SECRET, authorizationCode, GoogleConstants.OAUTH_REDIRECT_URI,"authorization_code"}; 
            HTMLresponse = ConnectionUtils.doHttpsPostFormUrlEncoded(GoogleConstants.URL_ACCESS_TOKEN, paramsKey, paramsValue);
            
            JSONObject jsonObj = (JSONObject) new JSONTokener(HTMLresponse).nextValue();
            
            //TODO create constants
            //recover accessToken and refreshToken
            accessToken  = jsonObj.getString("access_token");
            refreshToken = jsonObj.getString("refresh_token");
            
            //set date 
            tCalendar.add(Calendar.SECOND, Integer.parseInt(jsonObj.getString("expires_in")));                
            expirationDate = tCalendar.getTime();

            //getUserMail
            userMail = getUserMail(accessToken);
            
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
        
        // mail is not empty or null
        if (userMail != null && !userMail.equals("")) {
        
            //this part needs to be synchronized to execute
            synchronized (mUserMail) {
                
                //verify if user exists on database
                // Form an array specifying which columns to return. 
                String[] projection = new String[] {
                        AuthUser._ID
                };
                // Get the base URI for the Auth users table content provider.
                Uri authUsers =  AuthUser.CONTENT_URI;

                // build the where clause
                String where = AuthUser.EMAIL + "= ?";
                String[] whereParams = new String[]{userMail};  
                
                // Make the query. 
                Cursor managedCursor = mProvider.query(
                        authUsers,         // content_uri
                        projection,        // Which columns to return 
                        where,             // Which rows to return (email = ?)
                        whereParams,       // Selection arguments (new ActiveUser)
                        null);             // user order       

                //set values (will be used in a near future)
                ContentValues values = new ContentValues();
                values.put(AuthUser.EMAIL, userMail);
                values.put(AuthUser.ACCESS_TOKEN, accessToken);
                values.put(AuthUser.REFRESH_TOKEN, refreshToken);
                values.put(AuthUser.ACTIVE_USER, true);
                values.put(AuthUser.EXPIRATION_DATE,  mDateFormatter.format(expirationDate));
                
                // verify if user exists on dataBase
                if (managedCursor.moveToFirst() == true) {
                    Log.d(TAG, "User " + userMail + " already exists, updating data" );
                    
                    // user exists, update it!!                      
                    boolean updateResult = updateUser(userMail, values);
                    if (updateResult == true) {
                        Log.d(TAG, "User " + userMail + " updated and defined as ACTIVE");
                        userWasAdded = true;
                    }
                } else {
                    Log.d(TAG, "User " + userMail + " is a new user!" );

                    Uri uri = mProvider.insert(AuthUser.CONTENT_URI, values);
                    Log.d(TAG, "User " + userMail + " inserted and defined as ACTIVE");
                    userWasAdded = true;                   
                }
                
                //always close a cursor, I said always
                managedCursor.close();
                
                //update current values?
                if (userWasAdded == true) {
                    mAccessToken = accessToken;
                    mExpirationDate = expirationDate;
                    mUserMail = userMail;
                    mRefreshToken = refreshToken;
                }
            }
        }  
        
        Log.d(TAG, "addActiveUserToken end (result = " + userWasAdded + ")");
        return userWasAdded;
    }
    
    /**
     * Returns a valid SessionManager
     * @param contentResolver content resolver to be used by the object
     * @return SessionManager instance
     */
    public static synchronized UserManager getInstance(Context context){        
        if (sInstance == null) {
            sInstance = new UserManager(context);
        }
        
        return sInstance;
    }
        
    /**
     * Get a valid Access Token
     * @return valid Access Token 
     * @throws IllegalStateException Throws this exception if there is no valid token
     */
    public String getActiveUserAccessToken()  throws IllegalStateException {
        
        synchronized (mUserMail) {
            if (mAccessToken == null) {
                throw new IllegalStateException("No token found!");
            } else {
                //try to get the freshest token and verify its result
                if (refreshToken() == false) {
                    throw new IllegalStateException("Problem to recover new valid token!");
                }        
            }
        }   
        //everything ok! return token
        return mAccessToken;
    }
    
    /**
     * Verify if the Session has a valid token to provide
     * @return true if token is available, false otherwise
     */
    public boolean hasUserActiveAccessToken() {
        synchronized (mUserMail) {
            return (mAccessToken != null);
        }
    }
    
    /**
     * active user id
     * @return active user id
     */
    public String getActiveUserId() {
        if(mUserId == null){
            recoverDataFromDataBase();
        }
        return mUserId;
    }
    
}
