/*
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

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import org.ch.chalendarviewer.service.UserManager;
import org.ch.chalendarviewer.R;

public class HomeActivity extends Activity implements OnClickListener {
	
	private ArrayList<CalendarView> calendars = new ArrayList<CalendarView>();
    
	private UserManager mUserManager;
	
	UserManager _userManager = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Layout principal que contiene los calendarios
        LinearLayout mainLayout = new LinearLayout(this);  
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);  
        mainLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));      
        
        //Se cargan los calendarios desde xml. De esta forma se tiene que definir un xml para
        //cada calenario. En nuestro caso debe ser dinamico.
        //calendars.add(0, (LinearLayout)this.getLayoutInflater().inflate(R.layout.calendar_1, mainLayout, false));
        //calendars.add(1, (LinearLayout)this.getLayoutInflater().inflate(R.layout.calendar_2, mainLayout, false));
        //calendars.add(2, (LinearLayout)this.getLayoutInflater().inflate(R.layout.calendar_3, mainLayout, false));
        //calendars.add(3, (LinearLayout)this.getLayoutInflater().inflate(R.layout.calendar_4, mainLayout, false));
        
        //Se crean cuatro calendarios. El numero de calendarios y sus identificadores
        //se leen del fichero de configuraci—n.
        calendars.add(new CalendarView(this, "Sala 1"));
        calendars.add(new CalendarView(this, "Sala 2"));
        calendars.add(new CalendarView(this, "Sala 3"));
        calendars.add(new CalendarView(this, "Sala 4"));
        
        //Se a–aden los calendarios a la View principal
        for(CalendarView c: calendars) {
        	mainLayout.addView(c);
        	c.setStyle((float)1/calendars.size());
        }
        
        //A modo de prueba, se a–aden eventos a los calendarios
        calendars.get(0).addEvent(new EventView(this, "8:00 - 10:00 \nP-722: Revisi—n"));
        calendars.get(0).addEvent(new EventView(this, "LIBRE"));
        calendars.get(1).addEvent(new EventView(this, "LIBRE"));
        calendars.get(2).addEvent(new EventView(this, "9:20 - 11:20 \nReuni—n de R-Team"));   
        calendars.get(2).addEvent(new EventView(this, "12:20 - 13:30 \nReuni—n de Jefes de proyectos (peri—dica)"));
        calendars.get(3).addEvent(new EventView(this, "8:00 - 15:00 \nSr. Ruesga"));
        
        setContentView(mainLayout);
        
        //Se setea un listener para escuchar cualquier click sobre la pantalla
        mainLayout.setOnClickListener(this);
        
        
        _userManager = UserManager.getInstance(this);
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
        //TextView tv = (TextView) this.getLayoutInflater().inflate(R.layout.event, calendars.get(3), false);
        //tv.setText("Event inserted!");
        calendars.get(3).removeAllEvents();
        EventView nuevoEvent = new EventView(this, "Event inserted!");
        calendars.get(3).addEvent(nuevoEvent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	/**
	 * Enables or disables menu options based on activity's state
	 */
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mnuOptRefresh =  menu.findItem(R.id.menuHomeRefresh);
        MenuItem mnuOptManageResources =  menu.findItem(R.id.menuHomeManageResources);
        
        if (_userManager.hasUserActiveAccessToken() == false) {
            mnuOptManageResources.setEnabled(false);
            mnuOptRefresh.setEnabled(false);            
        } else {
            mnuOptManageResources.setEnabled(true);
            mnuOptRefresh.setEnabled(true);
        }
        
        return super.onPrepareOptionsMenu(menu);
    }  
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuHomeRefresh:
                // TODO
                return true;
            case R.id.menuHomeManageAccounts:
                Intent intent = new Intent(this, AccountManagerActivity.class);
                startActivity(intent);
                return true;
            case R.id.menuHomeManageResources:
                // TODO
                return true;
            case R.id.menuHomeConfiguration:
                // TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    } 
}