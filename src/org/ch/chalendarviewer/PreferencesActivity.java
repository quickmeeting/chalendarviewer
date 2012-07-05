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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import org.ch.chalendarviewer.contentprovider.AccountColumns;
import org.ch.chalendarviewer.service.GoogleConstants;
import org.ch.chalendarviewer.service.UserManager;
import org.ch.chalendarviewer.ui.dialogs.AuthenticateDialog;
import org.ch.chalendarviewer.ui.dialogs.AuthenticateDialogListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class PreferencesActivity extends PreferenceActivity {

    private Preference mCurrentActiveAccountPref;
    private Preference mAddAccountPref;
    private ListPreference mChangeActiveAccount;
    private ListPreference mDeleteAccount;
    private Preference mManageResources;
    
    private UserManager mUserManager;
    
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
        
        mUserManager = UserManager.getInstance(this);
        
        setListeners();        
        refreshScreenBasedOnAccounts();
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
    }

    protected void callManagerResourceActivity() {
        
    }

    private void refreshScreenBasedOnAccounts() {
        if (mUserManager.hasUserActiveAccessToken()) {
            mCurrentActiveAccountPref.setSummary(mUserManager.getActiveUserEmail());
            mDeleteAccount.setEnabled(true);
            mChangeActiveAccount.setEnabled(true);
            mManageResources.setEnabled(true);
            
            Cursor cursor = mUserManager.getAllAccountsIdEmail();
            String[] emailList = new String[cursor.getCount()];
            String[] idList = new String[cursor.getCount()];;
            
            if (cursor.moveToFirst()) {
                int pos = 0;
                do {        
                    idList[pos] = cursor.getString(cursor.getColumnIndex(AccountColumns._ID));
                    emailList[pos] = cursor.getString(cursor.getColumnIndex(AccountColumns.EMAIL)); 
                    Log.d(TAG, "Inserted[" + pos +"] - " +  idList[pos] + " - "+  emailList[pos]);
                    pos++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            Log.d(TAG, "Total items = " + emailList.length);
            
            mChangeActiveAccount.setEntries(emailList);
            mChangeActiveAccount.setEntryValues(idList);
            mDeleteAccount.setEntries(emailList);
            mDeleteAccount.setEntryValues(idList);
            
            
        } else {
            mCurrentActiveAccountPref.setSummary(R.string.activeAccountNotDefined);
            mDeleteAccount.setEnabled(false);
            mChangeActiveAccount.setEnabled(false);
            mManageResources.setEnabled(false);
        }
        
    }
    
    private void callAuthorizeActivity() {
        AuthenticateDialogListener listener = new AuthenticateDialogListener() {
            
            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Log.v(TAG, error);
            }
            
            @Override
            public void onComplete(String authorizationCode) {
                mUserManager.addActiveUserToken(authorizationCode);
                refreshScreenBasedOnAccounts();
            }
        };
        
        AuthenticateDialog authDialog = new AuthenticateDialog(this, GoogleConstants.URL_OAUTH, listener);
        authDialog.show();
    }
}
