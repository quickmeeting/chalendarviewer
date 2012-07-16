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

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import java.util.HashMap;

public class AboutActivity  extends PreferenceActivity {

    static final private String TAG  =  AboutActivity.class.toString();
    
    private HashMap<String, String> mPageRefHashMap;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        addPreferencesFromResource(R.xml.aboutpreferences);
        
        mPageRefHashMap = new HashMap<String, String>();
        
        setListener("aboutAbout","about");
        setListener("aboutChangeLog","releases");        
        setListener("aboutLicense","gpl-3.0-standalone");
        
    }
    
    private void setListener(String option, String webPage) {
        Preference pref = getPreferenceScreen().findPreference(option);      
        
        mPageRefHashMap.put(option, webPage);
        
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(AboutActivity.this, WebViewerActivity.class);
                String page = mPageRefHashMap.get(preference.getKey()); 
                intent.putExtra(WebViewerActivity.URL, WebViewerActivity.FROM_ASSET + page + ".htm");
                startActivity(intent); 
                return true;
            }
        });
        
    }
}
