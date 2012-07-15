/**
    This file is part of QuickMeeting.

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


package org.ch.chalendarviewer.ui.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.content.Context;

import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;

import android.webkit.CookieSyncManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ch.chalendarviewer.R;

/**
 * Display Google Authentication dialog.
 * 
 * Based on class created by Lorensius W. L. T <lorenz@londatiga.net>
 * (http://www.londatiga.net/featured-articles/how-to-use-foursquare-api-on-android-application/)
 * 
 * TODO review this class
 */
public class AuthenticateDialog extends Dialog {
	static final float[] DIMENSIONS_LANDSCAPE = {460, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                         						ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;

    private String mUrl;
    private AuthenticateDialogListener mListener;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;
    
    private Context mContext;
    
    private static final String TAG = "AuthenticateDialog";
    
	public AuthenticateDialog(Context context, String url, AuthenticateDialogListener listener) {
		super(context);
		mUrl		= url;
		mListener	= listener;
		mContext    = context;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    Log.d(TAG, "onCreate");
	    super.onCreate(savedInstanceState);
        
	    // Create Spinner Dialog
        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage(mContext.getResources().getText(R.string.loading));

        
        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        mContent.setScrollBarStyle(LinearLayout.SCROLLBARS_INSIDE_INSET);
        
        setUpTitle();
        setUpWebView();
        
        Display display 	= getWindow().getWindowManager().getDefaultDisplay();
        final float scale 	= getContext().getResources().getDisplayMetrics().density;
        float[] dimensions 	= (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
        
        addContentView(mContent, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f),
        							(int) (dimensions[1] * scale + 0.5f)));
        
        CookieSyncManager.createInstance(getContext()); 
    	
    	CookieManager cookieManager = CookieManager.getInstance();
    	
    	cookieManager.removeAllCookie(); 
    }
	
	 private void setUpTitle() {
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        
	        mTitle = new TextView(getContext());
	        mTitle.setText(R.string.loading);
	        mTitle.setTextColor(Color.WHITE);
	        mTitle.setTypeface(Typeface.DEFAULT_BOLD);
	        mTitle.setBackgroundColor(0xFF0cbadf);
	        mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
	        mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
	        mTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	        
	        mContent.addView(mTitle);
	    }

	    private void setUpWebView() {
	        mWebView = new WebView(getContext());
	        
	        mWebView.setVerticalScrollBarEnabled(false);
	        mWebView.setHorizontalScrollBarEnabled(false);
	        mWebView.setWebViewClient(new TwitterWebViewClient());
	        mWebView.getSettings().setJavaScriptEnabled(true);
	        mWebView.loadUrl(mUrl);
	        mWebView.setLayoutParams(FILL);
	        mWebView.setScrollContainer(true);
	        
	        mContent.addView(mWebView);
	    }

	    private class TwitterWebViewClient extends WebViewClient {

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
	      
	            mListener.onError(description);
	            
	            //AuthenticateDialog.this.dismiss();
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
	            String title = mWebView.getTitle();
	            if (title != null && title.length() > 0) {
	                mTitle.setText(title);
	            }
	            
	            mSpinner.dismiss();
	            
	            if (view.getTitle() != null && view.getTitle().startsWith("Success")) {
                    
	                String urls[] = view.getTitle().replaceAll(" ", "").split("=");
                    
                    mListener.onComplete(urls[1]);
                    
                    AuthenticateDialog.this.dismiss();
                }  
	            
	        }

	    }
	    

}