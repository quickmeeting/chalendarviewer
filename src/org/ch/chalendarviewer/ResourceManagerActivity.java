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
package org.ch.chalendarviewer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.ch.chalendarviewer.adapter.CustomResourceAdapter;
import org.ch.chalendarviewer.service.ResourceManager;

/**
 * Display resources in a list of checkbox objects
 * @author vitor
 */
public class ResourceManagerActivity extends ListActivity {

    /** Log tag **/
    static private final String TAG = ResourceManagerActivity.class.getName(); 
    
    /** Resource manager service */
    private ResourceManager _resourceManager = null;

    /** Progress dialog to show */
    private ProgressDialog progress;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.synchronizingCalendars));
        new ResourceAsyncTask().execute();
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    /**
     * Load resources in background
     * @author vitor
     *
     */
    private class ResourceAsyncTask extends AsyncTask<String, Void, Object>{

        @Override
        protected Object doInBackground(String... params) {
            
            _resourceManager = ResourceManager.getInstance(getApplicationContext());
            
            _resourceManager.syncResources();
            
            return "yes";
        }
        
        protected void onPostExecute(Object result) {
            
            Log.d(TAG, "ResourceActivity loaded");
            
            if (ResourceManagerActivity.this.progress != null) {
                ResourceManagerActivity.this.progress.dismiss();
                setListAdapter(new CustomResourceAdapter(getApplicationContext(), _resourceManager));
            }
        }

        
    }
    
}
