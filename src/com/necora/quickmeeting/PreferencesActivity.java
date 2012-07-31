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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.necora.quickmeeting.contentprovider.AccountColumns;
import com.necora.quickmeeting.service.GoogleConstants;
import com.necora.quickmeeting.service.ResourceManager;
import com.necora.quickmeeting.service.UserManager;
import com.necora.quickmeeting.service.exception.SyncFailedException;
import com.necora.quickmeeting.ui.dialogs.AuthenticateDialog;
import com.necora.quickmeeting.ui.dialogs.AuthenticateDialogListener;

public class PreferencesActivity extends PreferenceActivity {

    private Preference mCurrentActiveAccountPref;
    private Preference mAddAccountPref;
    private ListPreference mChangeActiveAccount;
    //private ListPreference mDeleteAccount;
    private Preference mManageResources;
    
    /** User manager singleton */
    private UserManager mUserManager;
    /** Resources manager singleton */
    private ResourceManager mResourceManager;
    
    static final private String TAG  =  PreferencesActivity.class.toString();
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        addPreferencesFromResource(R.xml.mainpreferences);
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        
        mCurrentActiveAccountPref = preferenceScreen.findPreference("currentActiveAccount");
        mAddAccountPref           = preferenceScreen.findPreference("addAccount");
        //mDeleteAccount            = (ListPreference) preferenceScreen.findPreference("deleteAccount");
        mChangeActiveAccount      = (ListPreference) preferenceScreen.findPreference("changeActiveAccount");
        
        mManageResources          = preferenceScreen.findPreference("manageResources");
        
        mUserManager = UserManager.getInstance(this);
        mResourceManager = ResourceManager.getInstance(this);
        
        setListeners();        
        refreshScreenBasedOnAccounts();
    }

    private void setListeners() {
        mAddAccountPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                callAuthorizeActivity();
                refreshActiveAccount();
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
                String newActiveAccount = (String) newValue;
                Log.d(TAG,"New active account = " + newActiveAccount);
                mUserManager.changeAccountActive(newActiveAccount);
                refreshActiveAccount();
                return true;
            }
        });
    }

    private void refreshScreenBasedOnAccounts() {
        if (mUserManager.hasUserActiveAccessToken()) {
            //mDeleteAccount.setEnabled(true);
            mChangeActiveAccount.setEnabled(true);
            mManageResources.setEnabled(true);
            refreshActiveAccount();
        } else {
            mCurrentActiveAccountPref.setTitle(R.string.activeAccountNotDefined);
            //mDeleteAccount.setEnabled(false);
            mChangeActiveAccount.setEnabled(false);
            mManageResources.setEnabled(false);
        }
        
    }
    
    private void refreshActiveAccount() {
        Cursor cursor = mUserManager.getAllAccountsEmail();
        String[] emailList = new String[cursor.getCount()];           
        
        if (cursor.moveToFirst()) {
            int pos = 0;
            do {        
                emailList[pos] = cursor.getString(cursor.getColumnIndex(AccountColumns.EMAIL)); 
                Log.d(TAG, "Inserted[" + pos +"] - " + emailList[pos]);
                pos++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d(TAG, "Total items = " + emailList.length);
        
        mChangeActiveAccount.setEntries(emailList);
        mChangeActiveAccount.setEntryValues(emailList);
        mChangeActiveAccount.setDefaultValue(mUserManager.getActiveUserEmail());
        //selected account
        mChangeActiveAccount.setValue(mUserManager.getActiveUserEmail());
        
        //mDeleteAccount.setEntries(emailList);
        //mDeleteAccount.setEntryValues(emailList);
        
        mCurrentActiveAccountPref.setTitle(mUserManager.getActiveUserEmail());     
        
        ResourceManager resourceManager = ResourceManager.getInstance(this);

        mResourceManager.notifyUserHasChanged();
    }
    
    private void callAuthorizeActivity() {
        AuthenticateDialogListener listener = new AuthenticateDialogListener() {
            
            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Log.e(TAG, error);
            }
            
            @Override
            public void onComplete(String authorizationCode) {
                mUserManager.addActiveUserToken(authorizationCode);
                refreshScreenBasedOnAccounts();
                mResourceManager.notifyUserHasChanged();
            }
        };
        
        AuthenticateDialog authDialog = new AuthenticateDialog(this, GoogleConstants.URL_OAUTH, listener);
        authDialog.show();
    }
}
