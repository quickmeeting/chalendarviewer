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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
        setListeners();
    }

    private void setListeners() {
        Button buttonOK     = (Button) findViewById(R.id.welcomeOk);
        Button buttonCancel = (Button) findViewById(R.id.welcomeCancel);
        
        
        buttonCancel.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                finish();               
            }
        });
        
        buttonOK.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent nextStepInt = new Intent(WelcomeActivity.this, WelcomeAccountActivity.class);
                startActivity(nextStepInt);
            }
        });
    }
}
