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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.util.Log;
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
import android.widget.Toast;

import org.ch.chalendarviewer.objects.CalendarResource;
import org.ch.chalendarviewer.objects.Event;
import org.ch.chalendarviewer.objects.User;
import org.ch.chalendarviewer.service.ResourceManager;
import org.ch.chalendarviewer.service.UserManager;
import org.ch.chalendarviewer.service.exception.ResourceNotAvaiableException;
import org.ch.chalendarviewer.util.Observable;
import org.ch.chalendarviewer.util.Observer;
import org.ch.chalendarviewer.R;

public class HomeActivity extends Activity implements Observer {
	
	private ResourceManager mResourceManager;
	
	private User mUser;
	private ProgressDialog mProgress;
	String mToastErrorMessage;
	private List<String> mCalendarNames;
	Map<String,CalendarResource>  mCalendarMap;
	Map<String, List<? extends Event>> mEventMap;
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
	private boolean mPoll;
	private boolean mRefresh;
	
	private final int MIN_EVENT_TIME        = 15;
	private final int MINUTES_BETWEEN_POLLS = 2;
	
	private final int MIN_EVENT_SELECTION         = 1;
	private final int TWO_MIN_EVENTS_SELECTIONS   = 2;
	private final int THREE_MIN_EVENTS_SELECTIONS = 3;
	private final int FOUR_MIN_EVENTS_SELECTIONS  = 4;
	
	private final String TAG = "**** HomeActivity";
	
	UserManager mUserManager = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTableLayout = (TableLayout)findViewById(R.id.mainTableLayout);
        mFrameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        
        mProgress 	       = new ProgressDialog(this);
        mFormateador       = new SimpleDateFormat("HH:mm");
        mAllEvents         = new ArrayList<TextView>();
        mToastErrorMessage = getString(R.string.download_data_error);
        mEventMap          = new HashMap<String, List<? extends Event>>();
        
        mProgress.setMessage(getString(R.string.download_data));
        
        mCalendarColumnWidth = getResources().getDimensionPixelSize(R.dimen.calendar_column_width);
        mFirstColumnWidth    = getResources().getDimensionPixelSize(R.dimen.first_column_width);
        mFirstRowHeight      = getResources().getDimensionPixelSize(R.dimen.first_row_height);
        mCalendarRowHeight   = getResources().getDimensionPixelSize(R.dimen.calendar_row_height);
        
        
        mUserManager     = UserManager.getInstance(this); 
        mResourceManager = ResourceManager.getInstance(this);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mRefresh = true;
        try {
        	mTableLayout.removeAllViews();
        	List<CalendarResource> calendars = mResourceManager.getActiveResources();
        	mCalendarNames = new ArrayList<String>();
        	mCalendarMap = new HashMap<String, CalendarResource>();
        	for( CalendarResource calendar:  calendars) {
        		mCalendarMap.put(calendar.getTitle(), calendar);
        		mCalendarNames.add(calendar.getTitle());
        	}
            drawBackground();
            refreshEvents();
            startPolling();
        } catch (Exception e) {
			// TODO: handle exception
            e.printStackTrace();
        	Log.i(TAG, e.getMessage());
		}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mRefresh = false;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuHomeRefresh:
            	refreshEvents();
                return true;
            case R.id.menuHomeConfiguration:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            case R.id.menuHomeAbout:
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
    	for(String cal: mCalendarNames) {
        	TableRow tr_cal = new TableRow(this);
        	tr_cal.setLayoutParams(new LayoutParams(
        				   LayoutParams.FILL_PARENT,
                           LayoutParams.WRAP_CONTENT));
        	
            TextView calendar = new TextView(this);
            calendar.setText(cal);
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
    	
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screen_width_pixels = display.getHeight();
        int scaledFontSize = getResources().getDimensionPixelSize(R.dimen.time_font_size);
        
        mNumberOfRows = 4*10;//screen_width_pixels/mCalendarRowHeight-2;
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
             for(int j=0; j<mCalendarNames.size(); j++) {                 
                 final CellTextView cell = new CellTextView(this);
                 cell.setCalendarId(mCalendarNames.get(j));
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
        now.add(Calendar.HOUR_OF_DAY, -8);
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
        //Remove old events
    	removeAllEvents();
    	
    	//Draw new ones
    	for(String calendarName: mEventMap.keySet()) {
    		List<? extends Event> eventList = mEventMap.get(calendarName);
    		for( Event event: eventList ) {
	    		String title = event.getTitle();
	    		Calendar eventBegin = event.getBegin();
	    		Calendar eventEnd = event.getEnd();

	    		if( !(eventEnd.before(mCalendarBegin) || eventBegin.after(mCalendarEnd)) ) {
		    		int startCellPos = getCellPosition(eventBegin, true);
		    		int endCellPos = getCellPosition(eventEnd, false)+1;
		    		
		    		//simulamos la columna a la que pertenece
		    		int calendarPos = mCalendarNames.indexOf(calendarName);
		    		
		    		String text = title + "\n" 
		    				+ mFormateador.format(eventBegin.getTime()) + " - " 
		    				+ mFormateador.format(eventEnd.getTime());
		    		
		    		boolean isCreatedByChalendar = event.getDetails().equals(getString(R.string.createdByChalendar));
		    		
		    		addEvent(calendarPos, startCellPos, text, endCellPos-startCellPos, isCreatedByChalendar, event.getId());
	    		}
    		}
    	}
    }
    
    private void addEvent(int calendarPos, int startCellPos, String text, int height, boolean isAppUser, String idEvent) {
        EventTextView event = new EventTextView(this, isAppUser);
		event.setWidth(mCalendarColumnWidth);
		event.setHeight(height*mCalendarRowHeight);
		event.setText(text);
		event.setObserver(this);
		event.setIdEvent(idEvent);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
		        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		        (Gravity.LEFT | Gravity.TOP));
		int column = mFirstColumnWidth + calendarPos*mCalendarColumnWidth;
		fl.setMargins(column, mFirstRowHeight+startCellPos*mCalendarRowHeight, 0, 0);
		mFrameLayout.addView(event,fl);
		
		mAllEvents.add(event);
    }
    
    /**
     * Remove event from UI and remote calendar
     * @param event UI component for event
     */
    private void removeEvent(TextView event) {
        mAllEvents.remove(event);
    	
    	EventTextView evTextView = (EventTextView) event;
    	
    	Log.d(TAG, evTextView.getIdEvent());
    	
    	try {
            mResourceManager.deleteEvent(new Event(evTextView.getIdEvent()));
        } catch (ResourceNotAvaiableException e) {
            e.printStackTrace();
        }
        
    	mFrameLayout.removeView(event);
    }
    
    private void removeAllEvents() {
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
    	    	removeEvent(mSelectedEvent);
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
		menu.setHeaderTitle(getString(R.string.selectDuration));
		menu.add(0, MIN_EVENT_SELECTION, 0, String.valueOf(MIN_EVENT_TIME) + getString(R.string.minutes));
		menu.add(0, TWO_MIN_EVENTS_SELECTIONS, 0, String.valueOf(MIN_EVENT_TIME*2) + getString(R.string.minutes));
		menu.add(0, THREE_MIN_EVENTS_SELECTIONS, 0, String.valueOf(MIN_EVENT_TIME*3) + getString(R.string.minutes));
		menu.add(0, FOUR_MIN_EVENTS_SELECTIONS, 0, String.valueOf(MIN_EVENT_TIME*4) + getString(R.string.minutes));
	}
	
	/**
	 * Create event manager
	 */
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

		//Obtain calendar data
        String calendarId = mSelectedCell.getCalendarId();
        CalendarResource calendarResource = mCalendarMap.get(calendarId);
        
        int startCellPos = mSelectedCell.getPosition();
        
        Log.d(TAG, "Calendar resource: "+ calendarResource.getId());
        
        
        Calendar eventBegin = convertCellPositionToCalendar(startCellPos, mCalendarBegin);
        Calendar eventEnd = convertCellPositionToCalendar(height, eventBegin);
        
        Event eventToCreate = new Event();
        eventToCreate.setTitle(getString(R.string.createdByChalendar));
        eventToCreate.setBegin(eventBegin);
        eventToCreate.setEnd(eventEnd);
        eventToCreate.setDetails(getString(R.string.createdByChalendar));
        
        try {
            mResourceManager.createEvent(calendarResource.getId(), eventToCreate);
        } catch (ResourceNotAvaiableException e) {
            Toast.makeText(HomeActivity.this, getString(R.string.creationError), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        refreshEvents();
		return true;
	}

    /**
     * Convert a cell position or offset into a calendar, adding cells to a initial time
     * @param cellOffset cells to add to initial time.
     * @param initTime Reference time
     * @return initTime + (time) cellOffset
     */
    private Calendar convertCellPositionToCalendar(int cellOffset, Calendar initTime) {
        Calendar eventBegin = (Calendar) initTime.clone();
    	int initTimeOffset = MIN_EVENT_TIME * cellOffset; 
    	eventBegin.add(Calendar.MINUTE, initTimeOffset);
        return eventBegin;
    }
    
    private void loadData() throws ResourceNotAvaiableException {
    	Map<String, List<? extends Event>> tmp = new HashMap<String, List<? extends Event>>();
    	for(String key : mCalendarMap.keySet()) {
    		CalendarResource calendar = mCalendarMap.get(key);
			List<? extends Event> events = 
						mResourceManager.getEvents(calendar.getId(), mCalendarBegin, mCalendarEnd);
			Log.d(TAG, "=> Event list size: " + events.size());
			mEventMap.put(key, events);
    	}
    	//mEventMap = tmp;
    }
    
    private void startPolling() {
    	
    	if( !mPoll ) {
        	mPoll = true;
	    	new Thread() {
	    		@Override
	    		public void run() {
	    			while(true) {
	    				try {
	    					sleep(MINUTES_BETWEEN_POLLS*60*1000);
	    					if(mRefresh) {
	    						mPollHandler.sendMessage(mPollHandler.obtainMessage(0));
	    					}
	    				} catch (Exception e) {
							e.printStackTrace();
						}
	    			}
	    		}
	    	}.start();
    	}
    }

    private Handler mPollHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		refreshEvents();
    	}
    };
    
    synchronized private void refreshEvents() {
		mProgress.show();
		updateTimeColumn();
    	new Thread() {
    		@Override
    		public void run() {
    			int what = 0;
				try {
					loadData();
				} catch (Exception e) {
					what = 1;
					Log.i(TAG, e.getMessage());
					e.printStackTrace();
				}
				mRefreshHandler.sendMessage(mRefreshHandler.obtainMessage(what));
    		}
    	}.start();
    }
    
    private Handler mRefreshHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		if( msg.what == 1 ) {
    			Toast.makeText(HomeActivity.this, mToastErrorMessage, Toast.LENGTH_SHORT).show();
    		}
    		drawEvents();
    		mProgress.dismiss();
    	}
    };
    
    /**
     * Return the cell position for a certain time.
     * @param time given time to search for their cell
     * @param includeBounds true for counting the time at the border.
     * @return vertical cell position
     */
    private int getCellPosition(Calendar time, boolean includeBounds) {
		int count = 0;
		Calendar loopControl = (Calendar) mCalendarBegin.clone();
		while(loopControl.compareTo(time) < 0) {
			loopControl.add(Calendar.MINUTE, MIN_EVENT_TIME);
			count++;
		}
		//TODO: WHY???
		//if(count > 0) --count;
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
