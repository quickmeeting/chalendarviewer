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
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.WindowManager;

import com.necora.quickmeeting.contentprovider.AccountColumns;
import com.necora.quickmeeting.service.ConfigManager;
import com.necora.quickmeeting.service.GoogleConstants;
import com.necora.quickmeeting.service.ResourceManager;
import com.necora.quickmeeting.service.UserManager;
import com.necora.quickmeeting.service.exception.SyncFailedException;

public class PreferencesActivity extends PreferenceActivity {

    private Preference mCurrentActiveAccountPref;
    private Preference mAddAccountPref;
    private ListPreference mChangeActiveAccount;
    private ListPreference mDeleteAccount;
    private Preference mManageResources;
    private CheckBoxPreference mKeepScreenOnPreference;
    private int mNumberAccounts; 
    
    /** User manager singleton */
    private UserManager mUserManager;
    /** Resources manager singleton */
    private ResourceManager mResourceManager;
    /** Configuration manager singleton */
    private ConfigManager mConfigManager;
    
    static final private String TAG  =  PreferencesActivity.class.toString();
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        addPreferencesFromResource(R.xml.mainpreferences);
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        
        mCurrentActiveAccountPref = preferenceScreen.findPreference("currentActiveAccount");
        mAddAccountPref           = preferenceScreen.findPreference("addAccount");
        mDeleteAccount            = (ListPreference) preferenceScreen.findPreference("deleteAccount");
        mChangeActiveAccount      = (ListPreference) preferenceScreen.findPreference("changeActiveAccount");
        mManageResources          = preferenceScreen.findPreference("manageResources");
        mKeepScreenOnPreference	  = (CheckBoxPreference) preferenceScreen.findPreference("keepScreenOn");
        
        
        mUserManager     = UserManager.getInstance(this);
        mResourceManager = ResourceManager.getInstance(this);
        mConfigManager   = ConfigManager.getInstance(this);
        
        //Initialize keepOnScreen checkbox
        mKeepScreenOnPreference.setChecked(Boolean.valueOf(mConfigManager.getProperty(ConfigManager.KEEP_SCREEN_ON)));
        
        setListeners();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //refresh the buttons status
        refreshAccountsReferences();
    }

    private void setListeners() {
        mAddAccountPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                callAuthorizeActivity();
                return true;
            }
        });   
        
        mManageResources.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(PreferencesActivity.this, ResourceManagerActivity.class);
                startActivity(intent); 
                return true;
            }
        });
        
        mChangeActiveAccount.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                changeActiveAccount(newValue);
                return true;
            }
        });
        
        mDeleteAccount.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                deleteInactiveAccount(newValue);
                return true;
            }
        });
        
        mKeepScreenOnPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                changeScreenOnPreference(newValue);
                return true;
            }
        });
    }

    private void setPreferencesEnabled() {
        if (mUserManager.hasUserActiveAccessToken()) {
            mManageResources.setEnabled(true);            
        } else {
            mCurrentActiveAccountPref.setTitle(R.string.activeAccountNotDefined);
            mManageResources.setEnabled(false);
        }
        
        if (mNumberAccounts > 1) {
            mChangeActiveAccount.setEnabled(true);
            mDeleteAccount.setEnabled(true);
        } else {
            mChangeActiveAccount.setEnabled(false);
            mDeleteAccount.setEnabled(false);
        }        
    }
    
    private void refreshAccountsReferences() {
        Cursor cursor = mUserManager.getAllAccountsEmail();
        String[] emailList = new String[cursor.getCount()-1];
        String activeAccount = mUserManager.getActiveUserEmail();
        
        if (cursor.moveToFirst()) {
            int pos = 0;
            String email;
            do {
                email = cursor.getString(cursor.getColumnIndex(AccountColumns.EMAIL));
                if (!activeAccount.equals(email)) {
                    emailList[pos] = email; 
                    Log.d(TAG, "Inserted[" + pos +"] - " + emailList[pos]);
                    pos++;
                }
            } while (cursor.moveToNext());
            
            // store the account quantity to enable or disable preferences
            mNumberAccounts = pos+1;
        }
        
        cursor.close();
        Log.d(TAG, "Total items = " + emailList.length);
        
        //Change Account preference
        mChangeActiveAccount.setEntries(emailList);
        mChangeActiveAccount.setEntryValues(emailList);
        mChangeActiveAccount.setDefaultValue(mUserManager.getActiveUserEmail());
        mChangeActiveAccount.setValue(mUserManager.getActiveUserEmail());

        //Delete Account preference
        mDeleteAccount.setEntries(emailList);
        mDeleteAccount.setEntryValues(emailList);
        
                
        //Active Account title
        mCurrentActiveAccountPref.setTitle(activeAccount);     
        
        mResourceManager.notifyUserHasChanged();
        
        //refresh options
        setPreferencesEnabled();
    }
    
    private void callAuthorizeActivity() {
        Intent nextStepInt = new Intent(PreferencesActivity.this, AccountManagerActivity.class);
        startActivity(nextStepInt);        
    }

    private void changeActiveAccount(Object newValue) {
        String newActiveAccount = (String) newValue;
        Log.d(TAG,"New active account = " + newActiveAccount);
        mUserManager.changeAccountActive(newActiveAccount);
        refreshAccountsReferences();
    }
    
    private void deleteInactiveAccount(Object newValue) {
        String deletedAccount = (String) newValue;
        Log.d(TAG,"Deleted account = " + deletedAccount);
        mUserManager.deleteInactiveAccount(deletedAccount);
        refreshAccountsReferences();
    }

    private void changeScreenOnPreference(Object newValue) {
        Boolean screenOn = (Boolean) newValue;
        Log.d(TAG,"New keep screen on = " + screenOn);
        if(screenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        mConfigManager.putProperty(ConfigManager.KEEP_SCREEN_ON, screenOn.toString());
    }

}
