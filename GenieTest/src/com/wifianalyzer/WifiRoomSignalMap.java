package com.wifianalyzer;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class WifiRoomSignalMap extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display d=getWindowManager().getDefaultDisplay();
		final int woindow_w=d.getWidth();
		final int woindow_h=d.getHeight();
		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			setTheme(R.style.bigactivityTitlebarNoSearch);
		}else{			
			setTheme(R.style.activityTitlebarNoSearch);
		} 
		requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.roomsignal_map);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_big);
		}else{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		}
		GenieGlobalDefines.SaveWiFiScanDefaultEntry(this, 5);
		InitTitleView();
		
	}
	
	/**
	 * 初始化标题栏
	 */
	 public void InitTitleView(){
		 
		 Button button1 = (Button)findViewById(R.id.back);
		 button1.setBackgroundResource(R.drawable.title_bt_bj);
		 Button button2 = (Button)findViewById(R.id.about);
		 button2.setBackgroundResource(R.drawable.title_more);
		 button2.setText(null);
		 button2.setVisibility(View.INVISIBLE);
		 button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
//		 button2.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				ShowMoreDialog();
//			}
//		});
		 button1.setOnTouchListener(new OnTouchListener() {
			 public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN){
						v.setBackgroundResource(R.drawable.title_bt_fj);
					}else if(event.getAction() == MotionEvent.ACTION_UP){
						v.setBackgroundResource(R.drawable.title_bt_bj);
					}
					return false;
				}
		});
		 
	 }
	
}
