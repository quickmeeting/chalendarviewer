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

package org.ch.chalendarviewer.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



// TODO populate this class
public class ConnectionUtils {

    static private final String TAG = "ConnectionUtils";
    
    static private final int HTTP_OK      = 200;
    static private final int HTTP_CREATED = 201;
    
    static public String getHttpsGetConnection(String url, String[] paramsKey, String[] paramsValue) throws IllegalStateException, IOException, HttpException {
        Log.d(TAG,"getHttpsGetConnection Begin");
        
        Log.d(TAG,"url = "+ url);
        
        HttpGet httpGetConn = new HttpGet(url);
        
        for (int index = 0; index < paramsKey.length; index++ ){
            Log.d(TAG,"param = " + paramsKey[index]);
            Log.d(TAG,"value = " + (index < paramsValue.length ? paramsValue[index] : ""));
            httpGetConn.setHeader(paramsKey[index], (index < paramsValue.length ? paramsValue[index] : ""));
        }
        
        DefaultHttpClient httpClient = new DefaultHttpClient();
        
        HttpResponse response = httpClient.execute(httpGetConn);
        
        HttpEntity entity = response.getEntity();
        
        int returnCode = response.getStatusLine().getStatusCode();
        
        Log.d(TAG,"Response code is " + returnCode);
        
        if (returnCode != HTTP_OK && returnCode != HTTP_CREATED) {
            Log.e(TAG,"There was an error " + returnCode + " processing the url " + url);
            Log.e(TAG,streamToString(entity.getContent()));
            throw new HttpException("There was an error " + returnCode + " processing the url " + url);
        }
    
        String htmlResult = streamToString(entity.getContent());
        
        Log.d(TAG,"getHttpsGetConnection End");
        
        return htmlResult;
    }

    static private String streamToString(final InputStream is) throws IOException {
        Log.d(TAG,"streamToString Begin");
        String str  = "";
        
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
           
            try {
                BufferedReader reader  = new BufferedReader(new InputStreamReader(is));
               
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
               
                reader.close();
            } finally {
                is.close();
            }
            str = sb.toString();
        }
       
        Log.d(TAG,"streamToString End");
        return str;
    }

    public static String doHttpsPost(final String url, final String[] paramsKey, final String[] paramsValue, final StringEntity stringEntity) throws  HttpException, ClientProtocolException, IOException {
        Log.d(TAG,"doHttpsPost Begin");
        
        Log.d(TAG,"url = "+ url);
                
        HttpPost httpPost = new HttpPost(url);
        for (int index = 0; index < paramsKey.length; index++ ){
            Log.d(TAG,"param = " + paramsKey[index]);
            Log.d(TAG,"value = " + (index < paramsValue.length ? paramsValue[index] : ""));
            httpPost.setHeader(paramsKey[index], (index < paramsValue.length ? paramsValue[index] : ""));
        }        
        httpPost.setEntity(stringEntity);
  
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(httpPost);
        
        // Don't know why, but 2 calls are needed to get a HTTP_CREATED response
        if (response.getStatusLine().getStatusCode() == HTTP_OK){
            response = httpClient.execute(httpPost);
        }
        HttpEntity entity = response.getEntity();
        
        int returnCode = response.getStatusLine().getStatusCode();
        
        Log.d(TAG,"Response code is " + returnCode);
        
        if (returnCode != HTTP_OK && returnCode != HTTP_CREATED) {
            Log.e(TAG,"There was an error " + returnCode + " processing the url " + url);
            Log.e(TAG,streamToString(entity.getContent()));
            throw new HttpException("There was an error " + returnCode + " processing the url " + url);
        }
    
        String htmlResult = streamToString(entity.getContent());
        
        Log.d(TAG,"getHttpsGetConnection End");
        
        return htmlResult;
    }
    
    
    public static String doHttpsPostFormUrlEncoded(final String url, final String[] paramsKey, final String[] paramsValue) throws ClientProtocolException, IOException, HttpException {
        Log.d(TAG,"doHttpsPostFormUrlEncoded Begin");
        
        // Http Client
        DefaultHttpClient httpClient = new DefaultHttpClient();
        
        // Post URL
        HttpPost httpPost = new HttpPost(url);
       
       // Add your data
       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
       for (int index = 0; index < paramsKey.length; index++ ){
           Log.d(TAG,"param = " + paramsKey[index]);
           Log.d(TAG,"value = " + (index < paramsValue.length ? paramsValue[index] : ""));
           nameValuePairs.add(new BasicNameValuePair(paramsKey[index], (index < paramsValue.length ? paramsValue[index] : "")));
       }
       httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
       
       // Execute HTTP Post Request
       HttpResponse response = httpClient.execute(httpPost);
       HttpEntity   entity   = response.getEntity();
        
       int returnCode = response.getStatusLine().getStatusCode();
       
       Log.d(TAG,"Response code is " + returnCode);
        
       if (returnCode != HTTP_OK && returnCode != HTTP_CREATED) {
           Log.e(TAG,"There was an error " + returnCode + " processing the url " + url);
           Log.e(TAG,streamToString(entity.getContent()));  
           throw new HttpException("There was an error " + returnCode + " processing the url " + url);           
       }
    
       String htmlResult = streamToString(entity.getContent());
        
       Log.d(TAG,"getHttpsGetConnection End");
        
       return htmlResult;
    }
    
    /**
     * Executes a HTTP DELETE request
     * @param url url to send request
     * @param paramsKey header key params array
     * @param paramsValue header value params array
     * @return result of delete request
     */
    static public boolean doHttpsDelete(String url, String[] paramsKey, String[] paramsValue) {
        
        boolean result = true;
        
        HttpDelete httpDelete = new HttpDelete(url);
        for (int index = 0; index < paramsKey.length; index++ ){
            Log.d(TAG,"param = " + paramsKey[index]);
            Log.d(TAG,"value = " + (index < paramsValue.length ? paramsValue[index] : ""));
            httpDelete.setHeader(paramsKey[index], (index < paramsValue.length ? paramsValue[index] : ""));
        }        
  
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(httpDelete);
            int returnCode = response.getStatusLine().getStatusCode();
            
            if(returnCode == HTTP_OK){
                Log.d(TAG, "DELETION OF EVENT OK");
            }else{
                result = false;
                Log.e(TAG,"Delete response code is " + returnCode);
                Log.e(TAG, "RESPONSE HTML => " + streamToString(response.getEntity().getContent()));
            }
            
            
            
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = false;
        }
        
        return result;
    }
    
}
