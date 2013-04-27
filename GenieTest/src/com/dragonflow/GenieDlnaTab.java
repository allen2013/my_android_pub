package com.dragonflow;



import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;
import com.ewm.ImagesBrowseActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;

public class GenieDlnaTab extends TabActivity {
	private RadioGroup group;
	//private TabHost tabHost;
	public static TabHost tabHost;
	public static RadioButton  m_radio0;
	public static RadioButton  m_radio1;
	public static RadioButton  m_radio2;
	public static RadioButton  m_radio3;
	public static final String TAB_SERVER="tabserver";
	public static final String TAB_RENDER="tabRender";
	public static final String TAB_PLAY="tab_play";
	public static final String TAB_OPTION="tab_option";
	
	public static 	Button  m_back = null;
	public static 	Button  m_about = null;
	
	public static 	TextView m_dlnatitle = null;

	   
	   
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			GenieDebug.error("group"," --222222222222222222-onDestroy---");
			
			m_back = null;
			m_about = null;
		
			Bitmap mBitmap=null;
			try {
				for(String str:GneieDlna.bitmap_Cache.keySet()){
					
					mBitmap=GneieDlna.bitmap_Cache.get(str);
					if (mBitmap != null) {
						if (!mBitmap.isRecycled())
							mBitmap.recycle();
						mBitmap=null;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				System.out.println("lh~~~~~~~~~~~ Õ∑≈Õº∆¨±®¥Ì");
			}finally{
				System.gc();
			}
			
			GneieDlna.bitmap_Cache.clear();
		}

	
		
	//public native int StopServer();
	
//    static{
//    	System.loadLibrary("genieupnp");
//    }   
    
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub


		super.onConfigurationChanged(newConfig);
		
	   	 if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE )
		  {
			 GenieDebug.error("onConfigurationChanged", "onConfigurationChanged tab--0--");

			 GenieDlnaStatus.m_thisview.onConfigurationChanged(newConfig);
			 
		  }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT )
		  {

		    	GenieDebug.error("onConfigurationChanged", "onConfigurationChanged tab--1--");
				
		    	GenieDlnaStatus.m_thisview.onConfigurationChanged(newConfig);

		  }
		
		
	}
    
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//getWindow().setBackgroundDrawableResource(android.R.color.background_light);
		
		
//		FileInputStream fis  =  null;
//		
//		
//		//String  file = "/data/data/com.android.browser/app_sharedimage/google-search.png";
//		String  file = "/sdcard/download/169_mafia2_ot_mul_041108_hr.mp4";
//	
//		try {
//			fis = new FileInputStream(file);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}                 
//		if(null != fis)
//		{
//			try {
//				fis.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//
//    	Intent it = getIntent();
//    	if (it != null &&  it.getAction() != null && it.getAction().equals(Intent.ACTION_SEND)) 
//    	{
//    		Bundle extras = it.getExtras();
//    		if (extras.containsKey("android.intent.extra.STREAM")) 
//    		{
//    			GenieDebug.error("debug","Intent extras ="+ extras.get("android.intent.extra.STREAM"));
//    			Uri uri = (Uri) extras.get("android.intent.extra.STREAM");
//    			GenieDebug.error("debug","Intent uri ="+ uri.toString());
//    			
//    			if(uri.toString().startsWith("content:"))
//    			{
//    				Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
//
//    				cursor.moveToFirst();
//    				for (int i = 0; i < cursor.getColumnCount(); i++) {
//    					/*_data*/
//    					GenieDebug.error("debug","Intent uri ="+i+"-"+cursor.getColumnName(i)+"-"+cursor.getString(i));
//    					if(cursor.getColumnName(i).equals("_data"))
//    					{
//    						file = cursor.getString(i);
//    					}
//    				}
//    			}
//    			
//    			if(uri.toString().startsWith("file:"))
//    			{
//    				file = uri.toString();
//    			}
//
//
//    		}
//    	}
//    	
//    	//Bundle Extras =  getIntent().getExtras();
//    	Set<String> Categories  = getIntent().getCategories();
//    	String Type  = getIntent().getType();
//    	String Action  = getIntent().getAction();
//    	String Scheme  = getIntent().getScheme();
//    	String data  = getIntent().getDataString();
//    	
//    	if(data != null && data.startsWith("file:"))
//		{
//			file = data;
//		}
//    	
//
//    	//GenieDebug.error("debug","Intent Extras.toString() = "+Extras.toString());
//    	if(null != Categories)
//    	{
//    		for(String test : Categories)
//    		{
//    			GenieDebug.error("debug","Intent Categories test = "+test);
//    		}
//    	}
//    	GenieDebug.error("debug","Intent Type = "+Type);
//    	GenieDebug.error("debug","Intent Action = "+Action);
//    	GenieDebug.error("debug","Intent Scheme = "+Scheme);
//    	GenieDebug.error("debug","Intent Data = "+data);
//    	
//    	
//    	try {
//			fis = new FileInputStream(file);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}                 
//		if(null != fis)
//		{
//			try {
//				fis.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//    	
//    	///////////////////////////////////
		
		GenieDebug.error("group"," ---TabTest---onCreate ");
		
        Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();
		
		GenieDebug.error("onCreate", "onCreate --woindow_w == "+woindow_w);
    	
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
			setTheme(R.style.bigactivityTitlebarNoSearch);
		}else
		{
			setTheme(R.style.activityTitlebarNoSearch); //qicheng.ai add
		}
		
		 //setTheme(R.style.activityTitlebarNoSearch);//qicheng.ai add        
	        requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
	    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    	setContentView(R.layout.maintabs);
	        
	    	if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
			{
	    		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
			}else
			{
				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
			}
	        

		
		GenieDlnaStatus.InitList();
		
		
		m_back = (Button)findViewById(R.id.back);
		m_about = (Button)findViewById(R.id.about);
		
		m_dlnatitle = (TextView)findViewById(R.id.netgeartitle);
		
		m_back.setBackgroundResource(R.drawable.title_bt_bj);
		
		m_about.setBackgroundResource(R.drawable.title_bt_bj);
		
		GenieDebug.error("GenieDlnaTab"," ---GenieDlnaStatus.InitList---");
		
		group = (RadioGroup)findViewById(R.id.main_radio);
		
		m_radio0 = (RadioButton)findViewById(R.id.radio_button0);
		m_radio1 = (RadioButton)findViewById(R.id.radio_button1);
		m_radio2 = (RadioButton)findViewById(R.id.radio_button2);
		m_radio3 = (RadioButton)findViewById(R.id.radio_button3);
		

		
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_SERVER)
	                .setIndicator(TAB_SERVER)
	                .setContent(new Intent(this,GneieDlna.class)));
	    tabHost.addTab(tabHost.newTabSpec(TAB_RENDER)
	                .setIndicator(TAB_RENDER)
	                .setContent(new Intent(this,GenieDlnaRender.class)));
	    tabHost.addTab(tabHost.newTabSpec(TAB_PLAY)
	    		.setIndicator(TAB_PLAY)
	    		.setContent(new Intent(this,GneieDlnaPlay.class)));
	    tabHost.addTab(tabHost.newTabSpec(TAB_OPTION)
	    		.setIndicator(TAB_OPTION)
	    		.setContent(new Intent(this,GenieDlnaOption.class)));
	    
	    
	    group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_button0:
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					GenieDebug.error("group"," ---TAB_SERVER---");
					tabHost.setCurrentTabByTag(TAB_SERVER);
					break;
				case R.id.radio_button1:
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					GenieDebug.error("group"," ---TAB_RENDER---");
					tabHost.setCurrentTabByTag(TAB_RENDER);
					break;
				case R.id.radio_button2:
					
					int style =  GetVideoStyle();
					
					if(style==3){
						GenieDebug.error("group"," ---TAB_PLAY---");
						if(!ImagesBrowseActivity.isopen){
						Intent imagebrowes=new Intent(GenieDlnaTab.this,ImagesBrowseActivity.class);
						imagebrowes.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(imagebrowes);
						}
						m_radio0.setChecked(true);
						
						break;
					}
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						GenieDebug.error("group"," ---TAB_PLAY---");
						tabHost.setCurrentTabByTag(TAB_PLAY);
						break;
					
					
					
				case R.id.radio_button3:
					GenieDebug.error("group"," ---TAB_OPTION---");
					tabHost.setCurrentTabByTag(TAB_OPTION);
					break;
				default:
					break;
				}
			}
		});
	    
	    
	    this.setDefaultTab(TAB_SERVER);
	    m_radio0.setChecked(true);
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		if(tabHost.getCurrentTabTag() == TAB_SERVER)
		{
			//GneieDlna.
			if(GenieDlnaStatus.getcurrentServerStatus() == 1)
			{
				GenieDlnaStatus.m_thisview.onBackPressed();
							
			}else if(GenieDlnaStatus.getcurrentServerStatus() == 2)
			{
				GenieDlnaStatus.m_thisview.onBackPressed();
				return ;
			}
		}
		
		GenieDebug.error("group"," --222222222222222222-TabTest---");
		super.onBackPressed();
		finish();
		
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
//	    this.setDefaultTab(TAB_SERVER);
//	    m_radio0.setChecked(true);
	    

	    
		GenieDebug.error("group"," -tab-onResume---");
		super.onResume();
	}

	public  int GetVideoStyle()
	{
		GenieDebug.error("debug","GetVideoStyle 0");
		if(GenieDlnaActionDefines.m_playItem != null)
		{
			GenieDebug.error("debug","GetVideoStyle 1");
			
			GenieDebug.error("debug","GetVideoStyle 1 getParentId = "+GenieDlnaActionDefines.m_playItem.getParentId());

			
			String upnpclass = GenieDlnaActionDefines.m_playItem.getUpnpClass();
			
			GenieDebug.error("debug","GetVideoStyle 2");
			if(upnpclass.startsWith("object.item.videoItem"))
			{
				return 1;
			}else if(upnpclass.startsWith("object.item.audioItem"))
			{
				return 2;
			}
			else if(upnpclass.startsWith("object.item.imageItem"))
			{
				return 3;
			}
			GenieDebug.error("debug","GetVideoStyle 3");
		}
		GenieDebug.error("debug","GetVideoStyle 4");
		return -1;
	}

}
