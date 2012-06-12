package org.ch.chalendarviewer;

import org.ch.chalendarviewer.ui.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventView extends TextView {

	public EventView(Context context) {
		this(context, "UNKNOWN");
	}
	
	public EventView(Context context, String text) {
		super(context);
	    if("LIBRE".equalsIgnoreCase(text)) setTextSize(20);
	    else setTextSize(10);
	    
	    //TODO: se deberia cargar de un xml
	    setTextColor(Color.BLACK);
	    setGravity(Gravity.CENTER);
	    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	    lp.bottomMargin = 5;
	    setLayoutParams(lp);
	    setBackgroundDrawable(getResources().getDrawable(R.drawable.event_background));
	    setText(text);
	}

}
