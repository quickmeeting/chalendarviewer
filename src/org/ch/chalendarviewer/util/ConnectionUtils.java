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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;



// TODO populate this class
public class ConnectionUtils {

    static private final String TAG = "ConnectionUtils";
    
    static public String getHttpsGetConnection(String url, String[] paramsKey, String[] paramsValue) throws IllegalStateException, IOException, HttpException {
        Log.d(TAG,"getHttpsGetConnection Begin");
        
        Log.d(TAG,"url = "+ url);
        
        HttpGet httpGetConn = new HttpGet(url);
        
        for (int index = 0; index < paramsKey.length; index++ ){
            httpGetConn.setHeader(paramsKey[index], (paramsValue.length > index ? "" : paramsValue[index])); 
        }
        
        DefaultHttpClient httpClient = new DefaultHttpClient();
        
        HttpResponse response = httpClient.execute(httpGetConn);
        
        HttpEntity entity = response.getEntity();
        
        int returnCode = response.getStatusLine().getStatusCode();
        
        Log.d(TAG,"Response code is " + returnCode);
        
        if (returnCode != 200) {
            Log.e(TAG,"There was an error " + returnCode + " processing the url " + url);
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
}
