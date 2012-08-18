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
package com.necora.quickmeeting.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ResourceCursorAdapter;

import com.necora.quickmeeting.R;
import com.necora.quickmeeting.contentprovider.ResourceColumns;
import com.necora.quickmeeting.service.ResourceManager;


/**
 * Custom ResourceCursorAdapter which encapsulates Resource data and managing
 * @author vitor
 */
public class CustomResourceAdapter extends ResourceCursorAdapter {
    
    /** Logging tag*/
    private static final String TAG = CustomResourceAdapter.class.getName();
    
    /** Resource manager reference*/
    private ResourceManager _resourceManager = null;
    
    /**
     * Custom constructor
     * @param context application context
     * @param resourceManager ResourceManager instance;
     */
    public CustomResourceAdapter(Context context,  ResourceManager resourceManager) {
        
        super(context, R.layout.resource,resourceManager.getResources());
        _resourceManager = ResourceManager.getInstance(context);
        
                
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return li.inflate(R.layout.resource, parent, false);
    }
    
    @Override
    public void bindView(View view, Context context, final Cursor cur) {
        final CheckBox cbListCheck = (CheckBox)view.findViewById(R.id.checkResourceActive);

        //Get data from cursor
        final String checkId = cur.getString(cur.getColumnIndex(ResourceColumns._ID));
        int active = cur.getInt(cur.getColumnIndex(ResourceColumns.ACTIVE));
        String name = cur.getString(cur.getColumnIndex(ResourceColumns.NAME));
        
        //Bind data to UI
        cbListCheck.setText(name);
        cbListCheck.setChecked((active==0? false:true));
        

        // Create a listener for the CheckBox
        cbListCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Changed resource " + checkId + cbListCheck.isChecked());
                _resourceManager.changeResourceActive(checkId, cbListCheck.isChecked() );
            }

        });

    }
}
