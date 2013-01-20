/*
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.necora.quickmeeting.objects.CalendarResource;
import com.necora.quickmeeting.objects.Event;
import com.necora.quickmeeting.service.ConfigManager;
import com.necora.quickmeeting.service.ResourceManager;
import com.necora.quickmeeting.service.UserManager;
import com.necora.quickmeeting.service.exception.ResourceNotAvaiableException;
import com.necora.quickmeeting.util.Observable;
import com.necora.quickmeeting.util.Observer;

import sheetrock.panda.changelog.ChangeLog;

public class HomeActivity extends Activity implements Observer {
	
	
    private ResourceManager mResourceManager;
    private ConfigManager mConfigManager;
	UserManager mUserManager;
	
	private ProgressDialog mProgress;
	String mToastErrorMessage;
	private List<String> mCalendarNames;
	Map<String,CalendarResource>  mCalendarMap;
	Map<String, List<? extends Event>> mEventMap;
	private SimpleDateFormat mFormatter;
	private TableLayout mTableLayout;
	private FrameLayout mFrameLayout;
	private LinearLayout mHeaderLayout;
	private int mNumberOfRows;
	private int mCalendarColumnWidth;
	private int mCalendarRowHeight;
	private int mFirstColumnWidth;
	private int mEventTextSize;
	private Calendar mCalendarBegin;
	private Calendar mCalendarEnd;
	private CellTextView mSelectedCell;
	private ArrayList<TextView> mAllEvents;
	private boolean mPoll;
	private boolean mRefresh;
	
	private final int HOURS_IN_CALENDAR     = 5;
	private final int MIN_EVENT_TIME        = 15;
	private final int MINUTES_BETWEEN_POLLS = 2;
	
	//Maximum number of columns per screen
	private final int MAX_NUM_OF_COLUMNS_PER_SCREEN = 4;
	
	private final int MIN_EVENT_SELECTION         = 1;
	private final int TWO_MIN_EVENTS_SELECTIONS   = 2;
	private final int THREE_MIN_EVENTS_SELECTIONS = 3;
	private final int FOUR_MIN_EVENTS_SELECTIONS  = 4;
	
	private final String TAG = "**** HomeActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ChangeLog cl = new ChangeLog(this);
        if (cl.firstRun())
            cl.getLogDialog().show();
        
        //No title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        
        setContentView(R.layout.main);
        
        mHeaderLayout        = (LinearLayout)findViewById(R.id.ll_calendar_names);
        mFrameLayout         = (FrameLayout)findViewById(R.id.frameLayout);
        mTableLayout         = (TableLayout)findViewById(R.id.mainTableLayout);
        
        mProgress 	       = new ProgressDialog(this);
        mFormatter         = new SimpleDateFormat("HH:mm");
        mAllEvents         = new ArrayList<TextView>();
        mEventMap          = new HashMap<String, List<? extends Event>>();
        mToastErrorMessage = getString(R.string.download_data_error);
        
        mProgress.setMessage(getString(R.string.download_data));
        
        mFirstColumnWidth    = getResources().getDimensionPixelSize(R.dimen.first_column_width);
        mCalendarRowHeight   = getResources().getDimensionPixelSize(R.dimen.calendar_row_height);
        mEventTextSize       = getResources().getDimensionPixelSize(R.dimen.event_font_size);
        
        mUserManager     = UserManager.getInstance(this); 
        mResourceManager = ResourceManager.getInstance(this);
        mConfigManager   = ConfigManager.getInstance(this);
        
        //Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //int screen_width_pixels = display.getHeight();
        //mNumberOfRows = screen_width_pixels/mCalendarRowHeight;
        mNumberOfRows = HOURS_IN_CALENDAR*(60/MIN_EVENT_TIME);
    }
    
    @Override
    protected void onResume() {
        
    	super.onResume();
    	
    	//Keep screen on
    	if (Boolean.valueOf(mConfigManager.getProperty("keepScreenOn"))){
    		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        
    	//Verify if there is at least one account
    	forceToCreateAnAccount();
    	
    	//Verify if there is at least one calendar selected
    	forceToSelectCalendars();
    	
    	mRefresh = true;
        try {
        	//clear screen
        	removeAllEvents();
        	mTableLayout.removeAllViews();
        	mHeaderLayout.removeAllViews();
        	
        	List<CalendarResource> calendars = mResourceManager.getActiveResources();
        	mCalendarNames = new ArrayList<String>();
        	mCalendarMap = new HashMap<String, CalendarResource>();
        	for( CalendarResource calendar:  calendars) {
        		mCalendarMap.put(calendar.getTitle(), calendar);
        		mCalendarNames.add(calendar.getTitle());
        	}
            refreshEvents();
            startPolling();
        } catch (Exception e) {
			// TODO: handle exception
            e.printStackTrace();
        	Log.d(TAG, e.getMessage());
		}
    }
    
    private void forceToCreateAnAccount() {
        Log.d(TAG,"forceToCreateAnAccount");
        if (mUserManager.hasUserActiveAccessToken() == false) {
            //open Welcome activity and close myself
            Intent welcomeAct = new Intent(this, WelcomeActivity.class);
            startActivity(welcomeAct);                     
        }
    }

    private void forceToSelectCalendars() {
        List<CalendarResource> resourceList = null;
        try {
            resourceList = mResourceManager.getActiveResources();
        } catch (ResourceNotAvaiableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
        
        if (resourceList == null || resourceList.size() == 0) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setIcon(android.R.drawable.ic_dialog_alert);
            b.setTitle(getString(R.string.noResourceSelected));
            b.setMessage(getString(R.string.noResourceSelectedHelp));
            b.setCancelable(false);
            b.setNegativeButton(R.string.maybeLater, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();                    
                }
            });
            b.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(HomeActivity.this, ResourceManagerActivity.class));            
                }
            });            
            b.show();                     
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
                startActivity(new Intent(this, PreferencesActivity.class));                
                return true;
            case R.id.menuHomeAbout:
                startActivity(new Intent(this, AboutActivity.class));                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void drawBackground(){
    	setColumnWidth();
        drawFirstRow();
        drawBackgroundCells();
    }
    
    private void setColumnWidth() {
    	Display display = getWindowManager().getDefaultDisplay(); 
    	int width = display.getWidth() - mFirstColumnWidth;
    	int numberOfCalendars = mCalendarNames.size();
    	if( numberOfCalendars <= 1 ) {
    		mCalendarColumnWidth = width/2;
    	}
    	else if( numberOfCalendars <= MAX_NUM_OF_COLUMNS_PER_SCREEN ) {
    		mCalendarColumnWidth = width/numberOfCalendars;
    	}
    	else {
    		mCalendarColumnWidth = width/MAX_NUM_OF_COLUMNS_PER_SCREEN;
    	}
    }
    
    private void drawFirstRow() {
    	
    	//Adding time column
        TextView tv = new TextView(this);
        tv.setWidth(mFirstColumnWidth);
        tv.setLayoutParams(new LayoutParams(
                			   LayoutParams.WRAP_CONTENT,
                               LayoutParams.WRAP_CONTENT));
        
        mHeaderLayout.addView(tv,new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
    	
    	//Adding calendar columns
        //int scaledFontSize = getResources().getDimensionPixelSize(R.dimen.calendar_name_font_size);
    	for(String cal: mCalendarNames) {
            TextView calendar = new TextView(this);
            calendar.setText(cal);
            //calendar.setTextSize(scaledFontSize);
            calendar.setTextAppearance(this, R.style.tinyText);
            calendar.setTextColor(Color.BLACK);
            calendar.setTypeface(null,Typeface.BOLD);
            calendar.setWidth(mCalendarColumnWidth);
            calendar.setGravity(Gravity.CENTER_HORIZONTAL);
            calendar.setPadding(0, 10, 0, 10);
            calendar.setMaxLines(1);
            calendar.setEllipsize(TruncateAt.END);
            calendar.setLayoutParams(new LayoutParams(
            						LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));
            
            mHeaderLayout.addView(calendar,new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
    	}
    }
    
    private void drawBackgroundCells() {
    	
        //int scaledFontSize = getResources().getDimensionPixelSize(R.dimen.time_font_size);
        
        Calendar tmp = (Calendar) mCalendarBegin.clone();
        
        
        for(int i = 0; i<mNumberOfRows; i++) {
        	
        	//Adding time cell
        	TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(
                           LayoutParams.FILL_PARENT,
                           LayoutParams.WRAP_CONTENT));
             
             TextView tv = new TextView(this);
             //tv.setTextSize(scaledFontSize);
             tv.setTextAppearance(this, R.style.tinyText);
             tv.setGravity(Gravity.TOP);
             if( tmp.get(Calendar.MINUTE)%(2*MIN_EVENT_TIME) == 0 ) {
            	 tv.setText(mFormatter.format(tmp.getTime()));
            	 tv.setBackgroundResource(R.drawable.cell_background_dark_top);
             }
             else {
            	 tv.setText("");
            	 tv.setBackgroundResource(R.drawable.cell_background);
             }
             tv.setPadding(10, 0, 10, 10);
             
             tr.addView(tv);
             
             //Adding calendars cells
             for(int j=0; j<mCalendarNames.size(); j++) {                 
                 final CellTextView cell = new CellTextView(this);
                 cell.setCalendarId(mCalendarNames.get(j));
                 cell.setPosition(i);
                 //cell.setTextSize(scaledFontSize);
                 tv.setTextAppearance(this, R.style.tinyText);
                 cell.setWidth(mCalendarColumnWidth);
                 if( tmp.get(Calendar.MINUTE)%(2*MIN_EVENT_TIME) == 0 ) {
                	 cell.setBackgroundResource(R.drawable.cell_background_dark_top);
                	 cell.setDarkOnTop(true);
                 }
                 else {
                	 cell.setBackgroundResource(R.drawable.cell_background);
                 }
                 cell.setPadding(10, 0, 10, 10);
                 
                 tr.addView(cell);
                 
                 cell.setOnClickListener(new OnClickListener() {
                     @Override
                     public void onClick(View v) {
                    	 mSelectedCell = cell;
                    	 cell.setBackgroundResource(R.drawable.cell_background_dark);
                    	 showReservationDialog();
                     }
                 });
             }
             
             mTableLayout.addView(tr,new TableLayout.LayoutParams(
	                 LayoutParams.FILL_PARENT,
	                 LayoutParams.WRAP_CONTENT));
             
             tmp.add(Calendar.MINUTE, MIN_EVENT_TIME);
        }
    }
    
    private void drawEvents() {
    	//clear screen
    	mTableLayout.removeAllViews();
    	mHeaderLayout.removeAllViews();
    	removeAllEvents();
    	
    	//Redraw background
    	drawBackground();
    	
    	//Draw new events
    	for(String calendarName: mEventMap.keySet()) {
    		List<? extends Event> eventList = mEventMap.get(calendarName);
    		for( Event event: eventList ) {
		    	addEvent(calendarName, event);
    		}
    	}
    }
    
    private void addEvent(String calendarName, Event event) {
		Calendar eventBegin = event.getBegin();
		Calendar eventEnd = event.getEnd();

		if( !(eventEnd.before(mCalendarBegin) || eventBegin.after(mCalendarEnd)) ) {
    		int startCellPos = convertCalendarToCellPosition(eventBegin, true);
    		int endCellPos = convertCalendarToCellPosition(eventEnd, false)+1;
    		int height = endCellPos-startCellPos;
    		
    		//get column position
    		int calendarPos = mCalendarNames.indexOf(calendarName);
    		
    		boolean isCreatedByQuickMeeting = event.getDetails().equals(getString(R.string.createdByQuickMeeting));

	    	String title = event.getTitle();
			if( title == null || title.length() == 0) title = getString(R.string.reserved);
			String text = title + "\n" 
					+ mFormatter.format(eventBegin.getTime()) + " - " 
					+ mFormatter.format(eventEnd.getTime());
			
	        EventTextView eventTextView = new EventTextView(this, event, isCreatedByQuickMeeting);
			eventTextView.setWidth(mCalendarColumnWidth);
			eventTextView.setHeight(height*mCalendarRowHeight);
			eventTextView.setTextSize(mEventTextSize);
			eventTextView.setText(text);
			eventTextView.setObserver(this);
			
			FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
			        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
			        (Gravity.LEFT | Gravity.TOP));
			int column = mFirstColumnWidth + calendarPos*mCalendarColumnWidth;
			fl.setMargins(column, startCellPos*mCalendarRowHeight, 0, 0);
			mFrameLayout.addView(eventTextView,fl);
			
			mAllEvents.add(eventTextView);
		}
    }
    
    /**
     * Remove event from UI and remote calendar
     * @param event UI component for event
     */
    private void removeEvent(Event event) {
        //mAllEvents.remove(event);
    	
    	Log.d(TAG, event.getId());
    	
    	try {
            mResourceManager.deleteEvent(event);
            refreshEvents();
        } catch (ResourceNotAvaiableException e) {
        	Toast.makeText(HomeActivity.this, getString(R.string.deletionError), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        
    	//mFrameLayout.removeView(event);
    }
    
    private void removeAllEvents() {
    	for(TextView event: mAllEvents) {
    		mFrameLayout.removeView(event);
    	}
    	mAllEvents.clear();
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
        eventToCreate.setTitle(getString(R.string.createdByQuickMeeting));
        eventToCreate.setBegin(eventBegin);
        eventToCreate.setEnd(eventEnd);
        eventToCreate.setDetails(getString(R.string.createdByQuickMeeting));
        
        createEvent(calendarResource.getId(), eventToCreate);
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
    
    synchronized private void loadData() throws ResourceNotAvaiableException {
    	
        Calendar now = Calendar.getInstance();
        int minutes = now.get(Calendar.MINUTE);
        int calendarMinutes = 0;
        while( minutes >= (calendarMinutes+MIN_EVENT_TIME) ) {
        	calendarMinutes += MIN_EVENT_TIME;
        }
        now.set(Calendar.MINUTE, calendarMinutes);
        mCalendarBegin = (Calendar)now.clone();
        
        now.add(Calendar.MINUTE, (mNumberOfRows-1)*MIN_EVENT_TIME);
    	
    	mCalendarEnd = (Calendar)now.clone();
    	
    	mEventMap.clear();
    	
    	for(String key : mCalendarMap.keySet()) {
    		CalendarResource calendar = mCalendarMap.get(key);
			List<? extends Event> events = 
						mResourceManager.getEvents(calendar.getId(), mCalendarBegin, mCalendarEnd);
			Log.d(TAG, "=> Event list size: " + events.size());
			mEventMap.put(key, events);
    	}
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
	    						loadData();
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
    		drawEvents();
    	}
    };
    
    synchronized private void createEvent(final String calendarId, final Event event) {
		mProgress.show();
    	new Thread() {
    		@Override
    		public void run() {
    			int what = 0;
				try {
					mResourceManager.createEvent(calendarId, event);
				} catch (ResourceNotAvaiableException e) {
					what = 1;
					Log.d(TAG, e.getMessage());
					e.printStackTrace();
				}
				mCreateEventHandler.sendMessage(mRefreshHandler.obtainMessage(what));
    		}
    	}.start();
    }
    
    private Handler mCreateEventHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		if( msg.what == 1 ) {
    			Toast.makeText(HomeActivity.this, getString(R.string.creationError), Toast.LENGTH_SHORT).show();
    		}
    		refreshEvents();
    	}
    };
    
    private void refreshEvents() {
		if(!mProgress.isShowing()) mProgress.show();
    	new Thread() {
    		@Override
    		public void run() {
    			int what = 0;
				try {
					loadData();
				} catch (Exception e) {
					what = 1;
					Log.d(TAG, e.getMessage());
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
    private int convertCalendarToCellPosition(Calendar time, boolean includeBounds) {
    	
    	int cellPosition = 0;
    	
    	Log.d(TAG, "time: " + mFormatter.format(time.getTime()));
    	Log.d(TAG, "mCalendarBegin: " + mFormatter.format(mCalendarBegin.getTime()));
    	Log.d(TAG, "mCalendarEnd: " + mFormatter.format(mCalendarEnd.getTime()));
    	
    	long timeInMilli  = time.getTimeInMillis();
    	long beginInMilli = mCalendarBegin.getTimeInMillis();
    	long endInMilli   = mCalendarEnd.getTimeInMillis();
    	
    	long endDiffMin   = (endInMilli - timeInMilli) / (60*1000);
    	long beginDiffMin = (timeInMilli - beginInMilli) / (60*1000);
    	//The time starts before the first cell from the calendar
    	if( beginDiffMin <= 0 ) return cellPosition;
    	//The time ends after the last cell from the calendar
    	if( endDiffMin <= 0 ) return (mNumberOfRows-1);
    	
    	cellPosition = (int) (beginDiffMin+1)/MIN_EVENT_TIME;
    	
    	Log.d(TAG, "endDiffMin: " + endDiffMin);
    	Log.d(TAG, "beginDiffMin: " + beginDiffMin);
    	Log.d(TAG, "cellPosition: " + cellPosition);
    	
    	Log.d(TAG, "includeBounds: " + includeBounds);
		if(!includeBounds && (time.get(Calendar.MINUTE) % MIN_EVENT_TIME) ==0)  {
			Log.d(TAG, "in includeBounds: --cellPosition");
			 --cellPosition;
		}
		return cellPosition;
    }
    
	@Override
	public void notify(Observable o) {
		EventTextView e = (EventTextView) o;
		if( e.isUserEvent() ) {
			showCancelReservationDialog(e.getEvent());
		}
		else {
			showEventInfoDialog(e.getEvent());
		}
	}
	
	/**
	 * Display dialog to confirm a reservation
	 */
    public void showReservationDialog() {
    	//Get event time to show in dialog title
        Calendar eventCalendar = convertCellPositionToCalendar(mSelectedCell.getPosition(), mCalendarBegin);
    	String eventTime = mFormatter.format(eventCalendar.getTime());
    	
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	b.setIcon(android.R.drawable.ic_dialog_alert);
    	b.setTitle(mSelectedCell.getCalendarId() + " - " + eventTime);
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
                if( mSelectedCell.isDarkOnTop() ) {
                	mSelectedCell.setBackgroundResource(R.drawable.cell_background_dark_top);
                }
                else {
                	mSelectedCell.setBackgroundResource(R.drawable.cell_background);
                }
    	    }
    	});
    	b.show();
    }
    
    public void showCancelReservationDialog(final Event event) {
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	b.setIcon(android.R.drawable.ic_dialog_alert);
    	b.setMessage(getString(R.string.unreserve_question));
    	b.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	removeEvent(event);
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
    
    public void showEventInfoDialog(final Event event) {
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	String title = event.getTitle();
		if( title == null || title.length() == 0) title = getString(R.string.reserved);
    	b.setTitle(title);
    	b.setMessage(event.getEventInfo());
    	b.setCancelable(false);
    	b.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	//focus go back to main screen
    	    }
    	});
    	b.show();
    }
}
