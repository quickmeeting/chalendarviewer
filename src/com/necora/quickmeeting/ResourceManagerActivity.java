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
package com.necora.quickmeeting;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.necora.quickmeeting.adapter.CustomResourceAdapter;
import com.necora.quickmeeting.service.ResourceManager;
import com.necora.quickmeeting.service.exception.SyncFailedException;

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
        setContentView(R.layout.resourcemanager);
        
        progress = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.synchronizingCalendars));
        new ResourceAsyncTask().execute();
        
        setListeners();        
    }
    
    private void setListeners() {
        Button button = (Button)findViewById(R.id.resourceManagerFinished);
        
        button.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                finish();               
            }
        });
        
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

        /** success on synchronizing calendar */
        private boolean success = true;
        
        @Override
        protected Object doInBackground(String... params) {
            
            _resourceManager = ResourceManager.getInstance(getApplicationContext());
            
            try {
                _resourceManager.syncResources();
            } catch (SyncFailedException e) {
                Log.e( TAG, e.getMessage() + ":" + e.getCause());
                success = false;
            }
            
            return "yes";
        }
        
        protected void onPostExecute(Object result) {

            Log.d(TAG, "ResourceActivity loaded");

            if (ResourceManagerActivity.this.progress != null) {
                ResourceManagerActivity.this.progress.dismiss();
                if (success) {
                    setListAdapter(new CustomResourceAdapter(getApplicationContext(), _resourceManager));
                } else {
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), 
                            getString(R.string.synchronizingError), duration);
                    toast.show();
                    ResourceManagerActivity.this.finish();
                }

            }
        }
        
    }
    
}
