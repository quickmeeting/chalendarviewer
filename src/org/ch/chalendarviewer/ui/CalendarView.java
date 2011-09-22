package org.ch.chalendarviewer.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarView extends LinearLayout {

	private String calendarId;
	private TextView title;
	private LinearLayout eventContainer;
	
	public CalendarView(Context context) {
		this(context, "UNKOWN");
	}
	
	public CalendarView(Context context, String calendarId) {
		super(context);
		this.calendarId = calendarId;
		title = new TextView(context);
		eventContainer = new LinearLayout(context);
	    addView(title);
	    addView(eventContainer);		
	}
	
	public String getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(String id) {
		this.calendarId = id;
	}

	/**
	 * A–ade un evento al final del contenedor de eventos.
	 * @param child
	 */
	public void addEvent(EventView child) {
		eventContainer.addView(child);
	}
	
	/**
	 * Borra todos los eventos de un calendario.
	 */
	public void removeAllEvents() {
		eventContainer.removeAllViews();
	}
	
	/**
	 * Define la apariencia de los calendario.
	 * @param weight: anchura del calendario en relaci—n a la pantalla.
	 */
	public void setStyle(float weight) {
		
		//TODO: se deberia cargar de un xml
		//Se configura la apariencia del CalendarView
		setOrientation(LinearLayout.VERTICAL);  
		setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams calendarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
	    calendarParams.weight = weight;
	    calendarParams.leftMargin = 2;
	    calendarParams.rightMargin = 2;
	    setLayoutParams(calendarParams);
		
	    //TODO: se deberia cargar de un xml
	    //Se configura el titulo del calendario
	    title.setTextSize(15);
	    title.setText(calendarId);
	    title.setGravity(Gravity.CENTER);
	    LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	    titleParams.bottomMargin = 5;
	    title.setLayoutParams(titleParams);
	    
	    //TODO: se deberia cargar de un xml
	    //Se configura la apariencia del contenedor de eventos
	    eventContainer.setBackgroundDrawable(getResources().getDrawable(R.drawable.calendar_background));
	    eventContainer.setOrientation(LinearLayout.VERTICAL); 
	    LinearLayout.LayoutParams eventContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
	    eventContainerParams.weight = weight;
	    eventContainerParams.leftMargin = 5;
	    eventContainerParams.rightMargin = 5;
	    eventContainer.setLayoutParams(eventContainerParams);
	}

}
