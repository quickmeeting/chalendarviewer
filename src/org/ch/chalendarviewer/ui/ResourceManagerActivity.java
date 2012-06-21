package org.ch.chalendarviewer.ui;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
        progress = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.synchronizing));
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
