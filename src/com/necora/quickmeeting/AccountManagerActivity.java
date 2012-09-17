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

package com.necora.quickmeeting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.necora.quickmeeting.service.GoogleConstants;
import com.necora.quickmeeting.service.UserManager;

public class AccountManagerActivity extends Activity {

    private static final String TAG = "AccountManagerActivity";
    
    private UserManager mUserManager;

    private ProgressDialog mSpinner;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountmanager);
        
        mUserManager = UserManager.getInstance(this);
        
        // Create Spinner Dialog
        mSpinner = new ProgressDialog(this);
        mSpinner.setCancelable(false);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage(getResources().getText(R.string.loading));
        
        setListeners();
        setWebView();
        loadGoogleAuthorizationPage();
    }
    
    private void setWebView() {
        mWebView = (WebView) findViewById(R.id.welcomeAccountWebView);
        
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);        
        mWebView.setScrollContainer(true);        
    }

    private void loadGoogleAuthorizationPage() {
        Log.d(TAG, "loadGoogleAuthorizationPage begin");
        
        mSpinner.show();
        
        //remove all cookies
        CookieSyncManager.createInstance(this); 
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie(); 
        
        mWebView.loadUrl(GoogleConstants.URL_OAUTH);
        
        
        Log.d(TAG, "loadGoogleAuthorizationPage end");
    }

    private void setListeners() {
        Button buttonRestart = (Button) findViewById(R.id.welcomeAccountRestart);
        Button buttonCancel  = (Button) findViewById(R.id.welcomeAccountCancel);
        
        buttonCancel.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                finish();               
            }
        });
        
        buttonRestart.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                loadGoogleAuthorizationPage();
            }
        });        
    }
    
    public void showMsgGoToResourceActivity() {
        Toast toast = Toast.makeText(this, R.string.accountConfigured, Toast.LENGTH_SHORT);
        toast.show();    
        
        Intent nextStepInt = new Intent(AccountManagerActivity.this, ResourceManagerActivity.class);
        startActivity(nextStepInt);
        finish();
        
        
    }
    
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirecting URL " + url);
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e(TAG, "Page error: " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
            //TODO what to do when got an error?      
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished: Loaded URL " + url);
            
            super.onPageFinished(view, url);
            mSpinner.dismiss();
            
            String title = mWebView.getTitle();
            Log.d(TAG, "title = '" + title + "'");
                       
            if (title != null && title.startsWith("Success")) {
                
                String urls[] = view.getTitle().replaceAll(" ", "").split("=");
                mUserManager.addActiveUserToken(urls[1]);
            
                showMsgGoToResourceActivity();
                            
            }  
        }

    }
}
