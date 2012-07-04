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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ch.chalendarviewer.objects.GoogleEvent;
import org.ch.chalendarviewer.objects.User;
import org.ch.chalendarviewer.service.UserManager;
import org.ch.chalendarviewer.util.Observable;
import org.ch.chalendarviewer.util.Observer;
import org.ch.chalendarviewer.R;

public class HomeActivity extends Activity implements Observer {
	
	private UserManager mUserManager;
	
	private User mUser;
	private ProgressDialog mProgress;
	private ArrayList<GoogleEvent> mGoogleEventList;
	private ArrayList<String> mCalendars;
	private SimpleDateFormat mFormateador;
	private TableLayout mTableLayout;
	private FrameLayout mFrameLayout;
	private int mNumberOfRows;
	private int mCalendarColumnWidth;
	private int mCalendarRowHeight;
	private int mFirstColumnWidth;
	private int mFirstRowHeight;
	private Calendar mCalendarBegin;
	private Calendar mCalendarEnd;
	private CellTextView mSelectedCell;
	private EventTextView mSelectedEvent;
	private ArrayList<TextView> mAllEvents;
	
	private final int MIN_EVENT_TIME        = 15;
	private final int MINUTES_BETWEEN_POLLS = 2;
	
	private final int MIN_EVENT_SELECTION         = 1;
	private final int TWO_MIN_EVENTS_SELECTIONS   = 2;
	private final int THREE_MIN_EVENTS_SELECTIONS = 3;
	private final int FOUR_MIN_EVENTS_SELECTIONS  = 4;
	
	UserManager _userManager = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTableLayout = (TableLayout)findViewById(R.id.mainTableLayout);
        mFrameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        
        mProgress 	 = new ProgressDialog(this);
        mFormateador = new SimpleDateFormat("HH:mm");
        mAllEvents   = new ArrayList<TextView>();
        
        mProgress.setMessage(getString(R.string.download_data));
        
        mCalendarColumnWidth = getResources().getDimensionPixelSize(R.dimen.calendar_column_width);
        mFirstColumnWidth    = getResources().getDimensionPixelSize(R.dimen.first_column_width);
        mFirstRowHeight      = getResources().getDimensionPixelSize(R.dimen.first_row_height);
        mCalendarRowHeight   = getResources().getDimensionPixelSize(R.dimen.calendar_row_height);
        
        mCalendars = new ArrayList<String>(
        	    Arrays.asList("Sala 1", "Sala 2", "Sala 3", "Sala 4"));
        
        drawBackground();
        
        _userManager = UserManager.getInstance(this);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	startPolling();
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
    
    private void drawBackground(){
        drawFirstRow();
        drawBackgroundCells();
    }
    
    private void drawFirstRow() {
    	
    	ArrayList<String> al = mCalendars;
    	
    	//Adding time column
    	TableRow tr = new TableRow(this);
        tr.setLayoutParams(new LayoutParams(
        			   LayoutParams.FILL_PARENT,
                       LayoutParams.WRAP_CONTENT));
        TextView tv = new TextView(this);
        
        tr.addView(tv);
        mTableLayout.addView(tr,new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
    	
    	//Adding calendar columns
        int scaledFontSize = getResources().getDimensionPixelSize(R.dimen.calendar_name_font_size);
    	for(String calendar_name: al) {
        	TableRow tr_cal = new TableRow(this);
        	tr_cal.setLayoutParams(new LayoutParams(
        				   LayoutParams.FILL_PARENT,
                           LayoutParams.WRAP_CONTENT));
        	
            TextView calendar = new TextView(this);
            calendar.setText(calendar_name);
            calendar.setTextColor(Color.BLACK);
            calendar.setTypeface(null,Typeface.BOLD);
            calendar.setTextSize(scaledFontSize);
            calendar.setWidth(mCalendarColumnWidth);
            calendar.setGravity(Gravity.CENTER_HORIZONTAL);
            calendar.setPadding(0, 10, 0, 10);
            
            tr.addView(calendar);
            mTableLayout.addView(tr_cal,new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
    	}
    }
    
    private void drawBackgroundCells() {
    	ArrayList<String> al = mCalendars;
    	
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screen_width_pixels = display.getHeight();
        int scaledFontSize = getResources().getDimensionPixelSize(R.dimen.time_font_size);
        
        mNumberOfRows = screen_width_pixels/mCalendarRowHeight-2;
        for(int i = 0; i<mNumberOfRows; i++) {
        	
        	//Adding time cell
        	TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(
                           LayoutParams.FILL_PARENT,
                           LayoutParams.WRAP_CONTENT));
             
             TextView tv = new TextView(this);
             tv.setBackgroundResource(R.drawable.cell_background);
             tv.setPadding(10, 5, 10, 5);
             tv.setTextSize(scaledFontSize);
             
             tr.addView(tv);
             
             //Adding calendars cells
             for(int j=0; j<al.size(); j++) {                 
                 final CellTextView cell = new CellTextView(this);
                 cell.setCalendarId(al.get(j));
                 cell.setPosition(i);
                 cell.setTextSize(scaledFontSize);
                 cell.setWidth(mCalendarColumnWidth);
                 
                 tr.addView(cell);
                 
                 cell.setOnClickListener(new OnClickListener() {
                     @Override
                     public void onClick(View v) {
                    	 mSelectedCell = cell;
                    	 showReservationDialog();
                     }
                 });
             }
             
             mTableLayout.addView(tr,new TableLayout.LayoutParams(
	                 LayoutParams.FILL_PARENT,
	                 LayoutParams.WRAP_CONTENT));
        }
    }
    
    private void updateTimeColumn() {
        Calendar now = Calendar.getInstance();
        int minutes = now.get(Calendar.MINUTE);
        int calendarMinutes = 0;
        while( minutes >= (calendarMinutes+MIN_EVENT_TIME) ) {
        	calendarMinutes += MIN_EVENT_TIME;
        }
        now.set(Calendar.MINUTE, calendarMinutes);
        mCalendarBegin = (Calendar)now.clone();
        
    	for(int i=1; i<mTableLayout.getChildCount(); i++) {
    		TableRow tr = (TableRow)mTableLayout.getChildAt(i);
    		TextView tv = (TextView)tr.getChildAt(0);
            if(tv!=null) {
            	tv.setText(mFormateador.format(now.getTime()));
            	now.add(Calendar.MINUTE, MIN_EVENT_TIME);
            }
    	}
    	now.add(Calendar.MINUTE, -MIN_EVENT_TIME);
    	mCalendarEnd = (Calendar)now.clone();
    }
    
    private void drawEvents() {
    	boolean test = true;
        
    	for(GoogleEvent e: mGoogleEventList) {
    		String title = e.getTitle();
    		Calendar begin = e.getBegin();
    		Calendar end = e.getEnd();
    		User u = e.getCreator();
    		if( end.before(mCalendarBegin) || begin.after(mCalendarEnd)) {
    			//Event out of range
    			break;
    		}
    		
    		int startCellPos = getCellPositionAtCertainTime(begin, true);
    		int endCellPos = getCellPositionAtCertainTime(end, false)+1;
    		
    		//simulamos la columna a la que pertenece
    		int calendarPos = 0;
    		if(test) test = false;
    		else calendarPos = 2;
    		
    		String text = title + "\n" 
    				+ mFormateador.format(begin.getTime()) + " - " 
    				+ mFormateador.format(end.getTime());
    		createEvent(calendarPos, startCellPos, text, endCellPos-startCellPos, false);
    	}
    }
    
    private void createEvent(int calendarPos, int startCellPos, String text, int height, boolean isAppUser) {
		EventTextView event = new EventTextView(this, isAppUser);
		event.setWidth(mCalendarColumnWidth);
		event.setHeight(height*mCalendarRowHeight);
		event.setText(text);
		event.setObserver(this);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
		        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		        (Gravity.LEFT | Gravity.TOP));
		int column = mFirstColumnWidth + calendarPos*mCalendarColumnWidth;
		fl.setMargins(column, mFirstRowHeight+startCellPos*mCalendarRowHeight, 0, 0);
		mFrameLayout.addView(event,fl);
		
		mAllEvents.add(event);
    }
    
    private void destroyEvent(TextView event) {
    	mAllEvents.remove(event);
    	mFrameLayout.removeView(event);
    }
    
    private void destroyAllEvents() {
    	for(TextView event: mAllEvents) {
    		mFrameLayout.removeView(event);
    	}
    	mAllEvents.clear();
    }
    
    public void showReservationDialog() {
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	b.setIcon(android.R.drawable.ic_dialog_alert);
    	b.setTitle(mSelectedCell.getCalendarId());
    	b.setMessage(getString(R.string.reserve_question));
    	b.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        registerForContextMenu(mFrameLayout); 
    	        openContextMenu(mFrameLayout);
    	        unregisterForContextMenu(mFrameLayout);
    	    }
    	});
    	b.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        //Do Nothing
    	    }
    	});
    	b.show();
    }
    
    public void showCancelReservationDialog() {
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	b.setIcon(android.R.drawable.ic_dialog_alert);
    	b.setMessage(getString(R.string.unreserve_question));
    	b.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	destroyEvent(mSelectedEvent);
    	    }
    	});
    	b.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        //Do Nothing
    	    }
    	});
    	b.show();
    }
    
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Ordenar por");
		menu.add(0, MIN_EVENT_SELECTION, 0, String.valueOf(MIN_EVENT_TIME) + " min");
		menu.add(0, TWO_MIN_EVENTS_SELECTIONS, 0, String.valueOf(MIN_EVENT_TIME*2) + " min");
		menu.add(0, THREE_MIN_EVENTS_SELECTIONS, 0, String.valueOf(MIN_EVENT_TIME*3) + " min");
		menu.add(0, FOUR_MIN_EVENTS_SELECTIONS, 0, String.valueOf(MIN_EVENT_TIME*4) + " min");
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		int height = 1;
		switch (item.getItemId()) {
		case MIN_EVENT_SELECTION:
		    break;
		case TWO_MIN_EVENTS_SELECTIONS:
			height = 2;
			break;
		case THREE_MIN_EVENTS_SELECTIONS:
			height = 3;
			break;
		case FOUR_MIN_EVENTS_SELECTIONS:
			height = 4;
			break;
		default:
		    return super.onContextItemSelected(item);
		}
    	int calendarPos = mCalendars.indexOf(mSelectedCell.getCalendarId());
    	String text = getString(R.string.reserved);
    	int startCellPos = mSelectedCell.getPosition();
    	createEvent(calendarPos, startCellPos, text, height, true);
		return true;
	}
    
    private void loadData() {
    	mGoogleEventList = new ArrayList<GoogleEvent>();
    	GoogleEvent g1 = new GoogleEvent();
    	g1.setTitle("Reunion VDSL");
    	Calendar now = Calendar.getInstance();
    	g1.setBegin((Calendar)now.clone());
    	Calendar end = (Calendar)now.clone();
    	end.add(Calendar.HOUR, 1);
    	end.set(Calendar.MINUTE, 0);
    	g1.setEnd((Calendar)end.clone());
    	mGoogleEventList.add(g1);
    	
    	GoogleEvent g2 = new GoogleEvent();
    	g2.setTitle("Traslados (JANDON)");
    	g2.setBegin((Calendar)end.clone());
    	end.add(Calendar.HOUR, 1);
    	g2.setEnd((Calendar)end.clone());
    	mGoogleEventList.add(g2);
    }
    
    private void startPolling() {
    	
    	new Thread() {
    		@Override
    		public void run() {
    			while(true) {
    				try {
    					mHandler.sendMessage(mHandler.obtainMessage(0));
    					sleep(MINUTES_BETWEEN_POLLS*60*1000);
    				} catch (Exception e) {
						//Do nothing
					}
    			}
    		}
    	}.start();
    }

    private Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		if( !mProgress.isShowing() ) mProgress.show();
        	destroyAllEvents();
        	updateTimeColumn();
        	loadData();
        	drawEvents();
    		mProgress.dismiss();
    	}
    };
    
    /**
     * Return the cell position for a certain time.
     * @param time: given time to search for their cell
     * @param includeBounds: true for counting the time at the border.
     * @return vertical cell position
     */
    private int getCellPositionAtCertainTime(Calendar time, boolean includeBounds) {
		int count = 0;
		Calendar loopControl = (Calendar) mCalendarBegin.clone();
		while(loopControl.compareTo(time) < 0) {
			loopControl.add(Calendar.MINUTE, MIN_EVENT_TIME);
			count++;
		}
		if(count > 0) --count;
		if(!includeBounds && (time.get(Calendar.MINUTE) % MIN_EVENT_TIME) ==0)  {
			 --count;
		}
		return count;
    }
    
	@Override
	public void notify(Observable o) {
		EventTextView e = (EventTextView) o;
		if( e.isUserEvent() ) {
			mSelectedEvent = e;
			showCancelReservationDialog();
		}
	}
}