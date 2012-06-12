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

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import org.ch.chalendarviewer.service.GoogleConstants;
import org.ch.chalendarviewer.service.UserManager;
import org.ch.chalendarviewer.ui.R;
import org.ch.chalendarviewer.ui.dialogs.AuthenticateDialog;
import org.ch.chalendarviewer.ui.dialogs.AuthenticateDialogListener;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

public class AccountManagerActivity extends Activity {
    
    static private final String TAG = AccountManagerActivity.class.getName(); 
    
    UserManager _userManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        _userManager = UserManager.getInstance(this);
        
        setContentView(R.layout.accountmanager);
        
        Button btnAddAccount = (Button) findViewById(R.id.accountManagerAddAccountButton);
        
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callCreateAccountActivity();
            }
        });

        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        verifyNoAccounts();
    }
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.accountmanager, menu);
        
        return true;
    }
    
    /**
     * Enables or disables menu options based on activity's state
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mnuOptAddAccount    =  menu.findItem(R.id.menuAccountManagerAddAccount);
        MenuItem mnuOptDeleteAccount =  menu.findItem(R.id.menuAccountManagerDeleteAccount);
        
        return super.onPrepareOptionsMenu(menu);
    }  
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuAccountManagerAddAccount:
                callCreateAccountActivity();
                return true;
            case R.id.menuAccountManagerDeleteAccount:
                // TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    private void verifyNoAccounts() {
        
        LinearLayout firstAccountView = (LinearLayout) findViewById(R.id.accountManagerFirstAccountLayout);
        ListView listAccounts = (ListView) findViewById(R.id.accountManagerList);
               
        if (_userManager.hasUserActiveAccessToken() == false) {            
            firstAccountView.setVisibility(View.VISIBLE);
            listAccounts.setVisibility(View.GONE);
        } else {
            firstAccountView.setVisibility(View.GONE);
            listAccounts.setVisibility(View.VISIBLE);
        }       
    }
    
    private void callCreateAccountActivity() {
        AuthenticateDialogListener listener = new AuthenticateDialogListener() {
            
            @Override
            public void onError(String error) {
                // TODO Auto-generated method stub
                Log.v(TAG, error);
            }
            
            @Override
            public void onComplete(String authorizationCode) {
                // TODO Auto-generated method stub
                verifyNoAccounts();
            }
        };
        
        AuthenticateDialog authDialog = new AuthenticateDialog(this, GoogleConstants.URL_OAUTH, listener);
        authDialog.show();
    }
}


