package com.dragonflow;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public MarqueeTextView(Context context, AttributeSet attrs) {
		  super(context, attrs);
		}
	
	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		  super(context, attrs, defStyle);
		}
	
	@Override
	public boolean isFocused() {
	return true;
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction,
	   Rect previouslyFocusedRect) {  
	}
}
