/**
 * 
 */
package com.dragonflow;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;


import jcifs.smb.SmbFile;   
import jcifs.smb.SmbFileInputStream;   
import jcifs.smb.SmbFileOutputStream;  

/**
 * @author 
 *
 */
public class GenieTrafficSetting extends Activity {

	
	//private GenieSoap soapCommand = null;
	private ProgressDialog progressDialog = null;
	
	private Toast toast = null;
	private int activeIndex = 0;
	private int controlIndex = 0;
	private int time_h = 1;
	private int time_m = 0;
	private int dayIndex = 0;
	private int sp_input_hour =0;
	private EditText monthlimit = null;
//	private EditText input_hour = null;
	private EditText input_mins = null;
	private Spinner sp_trafficmeter_hour = null;
	private Spinner Spinner_control = null;
	private ArrayAdapter<String> adapter_control = null;
	private ArrayAdapter<String> adapter_hour = null;
	private Spinner Spinner_time = null;
	private ArrayAdapter<String> adapter_time = null;
	private Spinner Spinner_day = null;
	private ArrayAdapter<String> adapter_day = null;
	private static final String [] m_control = {"No Limit","Download only","Both directions"};
	private static final String [] m_control2 = {"Unlimited","Download Only","Download & Upload"};
	private static final String [] m_str = {"AM","PM"};
	private static final String [] m_day = {"1st","2nd","3rd","4th","5th","6th","7th","8th","9th","10th",
											 "11th","12th","13th","14th","15th","16th","17th","18th","19th","20th",
											 "21th","22th","23th","24th","25th","26th","27th","28th"}; //,"29th","30th","31th"};
	
	private static final String [] trafficmeter_hour={"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
	// Time changed flag
	private boolean timeChanged = false;
	
	//
	private boolean timeScrolled = false;
	
	
	
	private Boolean m_input_monthlimit = false;
	private Boolean m_input_mins = false;
	private Boolean m_input_day = false;
	private Boolean m_input_hour = false;
	private Boolean m_input_control = false;
	
	private String  m_set_monthlimit = null;
	private String  m_set_mins = null;
	private int  m_set_day;
	private int  m_set_hour;
	private int  m_set_control;
	
	private Handler handler = new Handler()
	{   
        @Override   
        public void handleMessage(Message msg) 
        {   
        	GenieDebug.error("debug", "main handleMessage!! msg.what = "+msg.what);
        	

        	switch(msg.what)
        	{
        	case GenieGlobalDefines.ESoapRequestfailure:
        		closeWaitingDialog();
        		break ;
        	case GenieGlobalDefines.ESoapRequestsuccess:
        		closeWaitingDialog();
        		break ;	
        	case GenieGlobalDefines.ESoapRequestTrafficMeter:
        		SetViewDate();
        		break ;
        	}
        	closeWaitingDialog();
        }
	};   

	private void SetViewDate()
	{
		
		if(null != monthlimit)
		{
			String limit = 	GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT);
			if(null != limit &&  !limit.equals(GenieGlobalDefines.NULL))
			{
				monthlimit.setText(limit);
			}
			
			m_set_monthlimit = monthlimit.getText().toString();
		}
		
		if(null != sp_trafficmeter_hour )
		{
			String hour = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR);
			if(!hour.equals(GenieGlobalDefines.NULL))
			{
//				input_hour.setText(hour);
//				sp_input_hour
				int hours = Integer.parseInt(hour);
				for (int i = 0; i < trafficmeter_hour.length; i++) {
					if(hours == Integer.parseInt(trafficmeter_hour[i])){
						try {
							m_set_hour = i;
							sp_trafficmeter_hour.setSelection(i,true);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
		}
		if(null != input_mins)
		{
			String mins = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE);
			if(!mins.equals(GenieGlobalDefines.NULL))
			{
				input_mins.setText(mins);
			}
			
			m_set_mins = input_mins.getText().toString();
		}
		
		if(null != Spinner_control)
		{
			String control = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL);
			
			for(int i = 0;i<m_control.length;i++)
			{
				if(control.equalsIgnoreCase(m_control[i]))
				{
					try{
						m_set_control = i;
						Spinner_control.setSelection(i,true);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
		if(null != Spinner_day)
		{
			String day = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY);
			
			GenieDebug.error("SetViewDate","33333 day = "+day);
			if(!day.equals(GenieGlobalDefines.NULL))
			{
				try{
					int index = Integer.valueOf(day);
					if(index > 0 && index <= m_day.length)
					{
						m_set_day = index - 1;
						Spinner_day.setSelection((index - 1),true);
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
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
		
		//setTheme(R.style.activityTitlebarNoSearch); //qichemg.ai add
		
        activeIndex = getIntent().getIntExtra(GenieGlobalDefines.LIST_TYPE_INDEX,0);
        
        GenieDebug.error("debug", "GenieTrafficSetting onCreate --0--");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        
        GenieDebug.error("debug", "GenieTrafficSetting onCreate --1--");
       
        setContentView(R.layout.trafficsetting_2); 
        
        GenieDebug.error("debug", "GenieTrafficSetting onCreate --2--");
        if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
		}else
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		}
    	

    	InitTitleView();
    	
    	GenieDebug.error("debug", "GenieTrafficSetting onCreate --3--");
    	
    	InitSpinnerData_1();
    	
    	//soapCommand = new GenieSoap(this);
    	//getInfo();
    	SetViewDate();
	}
	

	

    
	  public void getInfo()
	    {
	    	GenieDebug.error("debug", "getModelInfo --0--");
	    	
	    	GetTrafficMeterOptions();
//	    	closeWaitingDialog();
//
//	    	showWaitingDialog(); 
//	    	
//	    	GenieDebug.error("debug", "getModelInfo --1--");
//	    	
//	    	new Thread()
//	    	{
//	    		public void run() 
//	    		{   
//
//	    			if(soapCommand.getTrafficMeterOptions())
//	    			{
//	    				sendMessage2UI(GenieGlobalDefines.ESoapRequestTrafficMeter);
//	    			}else
//	    			{
//	    				sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//	    			}
//
//	    		}
//	    	}.start();  
	    }
		
	  
	  
	  private void GetTrafficMeterOptions()
		{
			ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	  		
		  	GenieRequestInfo request0 = new GenieRequestInfo();
		  	request0.aRequestLable="GenieTrafficSetting";
		  	request0.aSoapType = GenieGlobalDefines.ESoapRequestTrafficOptions;
		  	request0.aServer = "DeviceConfig";
		  	request0.aMethod = "GetTrafficMeterOptions";
		  	request0.aNeedwrap = false;
		  	request0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		  	request0.aNeedParser=true;
		  	request0.aTimeout = 25000;
		  	request0.aElement = new ArrayList<String>();
		  
		  	requestinfo.add(request0);
		  	


		  	m_SettingRequest = new GenieRequest(this,requestinfo);
		  	m_SettingRequest.SetProgressInfo(true, true);
		  	m_SettingRequest.Start();
		}
	
	
	
	
	
	
	private AlertDialog aboutdialog = null;
	
	public void OnClickAbout()
	{
		
///////////////////////////////////////////////////////////	
    	PackageManager manager = this.getPackageManager();  

		PackageInfo info = null;
		String packageName = null;
		int versionCode = 0;
		String versionName = null ;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			
			packageName = info.packageName;  

			versionCode = info.versionCode;  

			versionName = info.versionName;  
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

  
    			
		GenieDebug.error("OnClickAbout", "packageName = "+packageName);
		GenieDebug.error("OnClickAbout", "versionCode = "+versionCode);
		GenieDebug.error("OnClickAbout", "versionName = "+versionName);

   ////////////////////////////////////////////////////////////////
		
		String text = getResources().getString(R.string.version)+" "+versionName+"."+versionCode+"\n"+getResources().getString(R.string.about_info);
    		
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.about,null);
		
		TextView aboutinfo = (TextView)view.findViewById(R.id.about_tv);
		aboutinfo.setTextSize(22);
		aboutinfo.setText(text);
		
		TextView model = (TextView)view.findViewById(R.id.about_model);
		model.setTextSize(22);
		model.setText("Router Model:" + GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME));
		
		TextView version = (TextView)view.findViewById(R.id.about_version);
		version.setTextSize(22);
		String Version_text = "Firmware Version:" + GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION);
		
		int posE = 0;
		StringBuffer temp = new StringBuffer(Version_text);
		String str = null;

		posE = temp.indexOf("-");
		if(posE > -1)
		{
			str = temp.substring(0, posE);
		}else 
		{
			posE = temp.indexOf("_");
			if(posE > -1)
			{
				str = temp.substring(0, posE);
			}else
			{
				str = temp.substring(0, temp.length());
			}
		}
		
		if(null != str)
		{
			version.setText(str);
		}
		
		
		
		Button close = (Button)view.findViewById(R.id.about_cancel);
		close.setText(R.string.close);
		close.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != aboutdialog)
				{
					aboutdialog.dismiss();
					aboutdialog = null;
				}
			}
			
		});



		AlertDialog.Builder dialog_login = new AlertDialog.Builder(this)
													.setView(view)
													.setTitle(R.string.about)
													.setIcon(R.drawable.icon);
													
		//dialog_login.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener()
		//{

		//	@Override
		//	public void onClick(DialogInterface dialog, int which) {
		//		// TODO Auto-generated method stub
				
		//	}
			
		//});		
		
		aboutdialog = dialog_login.create();
		aboutdialog.show();
		
		//Button tempp = (Button)testdialog.getWindow().findViewById(AlertDialog.BUTTON_NEGATIVE);
		//GenieDebug.error("OnClickAbout","OnClickAbout 0");
		//Button tempp = testdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		

		//Button tempp = testdialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		//GenieDebug.error("OnClickAbout","OnClickAbout 1");
		//tempp.setEnabled(false);
		//tempp.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		GenieDebug.error("OnClickAbout","OnClickAbout 2");
		//testdialog.show();
	}
	
	private Activity GetActivity()
	{
		return this;
	}
	private Button m_save = null;
	public void InitTitleView()
	{
		Button  back = (Button)findViewById(R.id.back);
		m_save = (Button)findViewById(R.id.about);
		
		m_save.setText(R.string.save);
		
		TextView title = (TextView)findViewById(R.id.netgeartitle);
		title.setText(R.string.traffic);
		
		back.setBackgroundResource(R.drawable.title_bt_bj);
		m_save.setBackgroundResource(R.drawable.title_bt_bj);
		
		back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GetActivity().onBackPressed();
			}
		});
		
		m_save.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//OnClickAbout();
				
				 GenieDebug.error("debug", "onOptionsItemSelected m_controlIndex = "+m_control[controlIndex]);
				 GenieDebug.error("debug", "onOptionsItemSelected monthlimit = "+monthlimit.getText().toString());
				 GenieDebug.error("debug", "onOptionsItemSelected time_h = "+Integer.toString(time_h));
				 GenieDebug.error("debug", "onOptionsItemSelected time_m = "+Integer.toString(time_m));
				 GenieDebug.error("debug", "onOptionsItemSelected dayIndex = "+Integer.toString(dayIndex));
				 
				 String hour = trafficmeter_hour[sp_input_hour];
				 String mins = input_mins.getText().toString();
				 
				 GenieDebug.error("debug", "onOptionsItemSelected hour = "+hour);
				 GenieDebug.error("debug", "onOptionsItemSelected mins = "+mins);
				 String MonthlyLimit = monthlimit.getText().toString().trim();
				 GenieDebug.error("debug", "onOptionsItemSelected MonthlyLimit = "+MonthlyLimit.length());
				 if(MonthlyLimit.length() == 0||(MonthlyLimit.equals("")||MonthlyLimit == null)||hour.length() == 0 ||mins.length() == 0||Integer.parseInt(MonthlyLimit)>1000000)
				 {
					 showtoast();
				 }else
				 {
					 try{
						 time_h = Integer.parseInt(hour);
						 time_m = Integer.parseInt(mins);
					 }catch(Exception e){
						 e.printStackTrace();
						 showtoast();
						 return ;
					 }
					 if(time_h > 23 || time_h < 0 || time_m > 59 || time_m < 0 )
					 {
						 showtoast();
						 return ;
					 }
					 OnSender();
				 }
			}
		});
		
		back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //閿熸枻鎷烽敓杞款亷鎷烽敓鏂ゆ嫹閿熺粸鎲嬫嫹璋嬮敓鏂ゆ嫹閿熼叺璁★拷     
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //閿熸枻鎷蜂负鎶敓鏂ゆ嫹鏃堕敓鏂ゆ嫹鍥剧墖      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		

		
		m_save.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //閿熸枻鎷烽敓杞款亷鎷烽敓鏂ゆ嫹閿熺粸鎲嬫嫹璋嬮敓鏂ゆ嫹閿熼叺璁★拷     
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //閿熸枻鎷蜂负鎶敓鏂ゆ嫹鏃堕敓鏂ゆ嫹鍥剧墖      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		
//		TextView text = (TextView)findViewById(R.id.model);
//		text.setText("Router:" + GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME));
//		
//		text = (TextView)findViewById(R.id.version);
//		text.setText("Firmware:" + GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION));
//	
	}
	
	
	public void changeScreenOrientation(Configuration newConfig)
	{
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();

		 GenieDebug.error("changeScreenOrientation", "changeScreenOrientation !!!!!!!!!");
	}
	
    public void showWaitingDialog()
    {
    	closeWaitingDialog();
//    	progressDialog = ProgressDialog.show(GenieMainView.this, "Loading...", "Please wait...", true, false);   
    	progressDialog =  ProgressDialog.show(this, null, "Please wait...", true, true);
    }
    public void closeWaitingDialog()
    {
    	if(progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
    }
	


    private void SetSaveButtonStatus()
    {
    	
    	if(null == monthlimit || null == input_mins )
			return ;
    	
    	GenieDebug.error("debug","SetSaveButtonStatus 0");
		
		if(monthlimit.getText().toString().length() > 0)
		{
			m_input_monthlimit = true;
		}else
		{
			m_input_monthlimit = false;
		}
		
		if(input_mins.getText().toString().length() > 0)
		{
			m_input_mins = true;
		}else
		{
			m_input_mins = false;
		}
		
		
	
		
		if(m_input_monthlimit && m_input_mins)
		{
			
			if(null != m_save)
			{
				if((m_set_monthlimit != null && !m_set_monthlimit.equals(monthlimit.getText().toString())) ||
						(m_set_mins != null && !m_set_mins.equals(input_mins.getText().toString()))||
						m_set_hour != sp_input_hour ||
						m_set_control != controlIndex || 
						m_set_day != (dayIndex -1) )
				{
				GenieDebug.error("InitGuestSettingText","m_input  5");
					m_save.setEnabled(true);
					m_save.setTextColor(Color.WHITE);
				}else
				{
					m_save.setEnabled(false);
					m_save.setTextColor(Color.GRAY);
				}
			}
		}else
		{
			GenieDebug.error("InitGuestSettingText","m_input  6");
			if(null != m_save)
			{
				GenieDebug.error("InitGuestSettingText","m_input  7");
				m_save.setEnabled(false);
				m_save.setTextColor(Color.GRAY);
			}
		}
    }
    
	private void InitSpinnerData_1() 
	{
		//final WheelView tmsetting = (WheelView) findViewById(R.id.tmsettingSpinner_01);
		//tmsetting.setAdapter(new ArrayWheelAdapter<String>(m_control));
		//tmsetting.setLabel("hours");
		
		monthlimit = (EditText)findViewById(R.id.monthlimit);
//		input_hour = (EditText)findViewById(R.id.hour);
		sp_trafficmeter_hour = (Spinner) findViewById(R.id.sp_trafficmeter_hour);
		input_mins = (EditText)findViewById(R.id.mins);
		
		input_mins.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

				SetSaveButtonStatus();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		monthlimit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				EditText monthlimit = (EditText)findViewById(R.id.monthlimit);
//				System.out.println(start+""+before+"s"+s+"count"+count);
				GenieDebug.error("EditText addTextChangedListener", start+""+before+"s"+s+"count"+count);
//				EditText etnote = (EditText) findViewById(R.id.etnote);
				String note = s.toString().trim();
//				System.out.println(note);
				GenieDebug.error("EditText String", note);
				if(s.length()>1&&s.toString().startsWith("0")){
					int Intnote = Integer.parseInt(note);
					monthlimit.setText(Intnote+"");
//					s= Intnote+"";
					monthlimit.setSelection(start);
				} 
				
				SetSaveButtonStatus();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		adapter_hour = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,trafficmeter_hour);
		adapter_hour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_trafficmeter_hour.setAdapter(adapter_hour);
		sp_trafficmeter_hour.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				sp_input_hour = arg2;
				SetSaveButtonStatus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Spinner_control = (Spinner)findViewById(R.id.tmsettingSpinner_01);	
		adapter_control = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_control2);
		
		// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨鎲嬫嫹閿熸枻鎷�
		adapter_control.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //閿熸枻鎷稟dapter閿熸枻鎷锋嫢閿熺禈dapter_channel
		Spinner_control.setAdapter(adapter_control);
    	
		Spinner_control.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				controlIndex = arg2;
				arg0.setVisibility(View.VISIBLE);
				
				SetSaveButtonStatus();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Spinner_day = (Spinner)findViewById(R.id.day);	
		adapter_day = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_day);
		
		// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨鎲嬫嫹閿熸枻鎷�
		adapter_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //閿熸枻鎷稟dapter閿熸枻鎷锋嫢閿熺禈dapter_channel
    	Spinner_day.setAdapter(adapter_day);
    	
    	Spinner_day.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				dayIndex = arg2 + 1;
				arg0.setVisibility(View.VISIBLE);
				
				SetSaveButtonStatus();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});		
		
/*		
		final WheelView hours = (WheelView) findViewById(R.id.hour);
		hours.setAdapter(new NumericWheelAdapter(0, 23));
		hours.setLabel("hours");
		hours.setCyclic(true);
	
		final WheelView mins = (WheelView) findViewById(R.id.mins);
		mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		mins.setLabel("mins");
		mins.setCyclic(true);
	
		final WheelView days = (WheelView) findViewById(R.id.day);
		days.setAdapter(new NumericWheelAdapter(1, 31, "%02d"));
		days.setLabel("day");
		days.setCyclic(true);
	
		// set current time
		Calendar c = Calendar.getInstance();
		int curHours = c.get(Calendar.HOUR_OF_DAY);
		int curMinutes = c.get(Calendar.MINUTE);
		int curday = c.get(Calendar.DAY_OF_MONTH);
		
		GenieDebug.error("InitSpinnerData_1","curHours = "+curHours);
		GenieDebug.error("InitSpinnerData_1","curMinutes = "+curMinutes);
		GenieDebug.error("InitSpinnerData_1","curday = "+curday);
	
		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);
		days.setCurrentItem(curday-1);
		//tmsetting.setCurrentItem(0);
	
		
	
		// add listeners
		addChangingListener(mins, "min");
		addChangingListener(hours, "hour");
		addChangingListener(days, "day");
	
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!timeScrolled) {
					timeChanged = true;
					//controlIndex = tmsetting.getCurrentItem();
					time_h = hours.getCurrentItem();
					time_m = mins.getCurrentItem();
					dayIndex = days.getCurrentItem()+1;
					
					GenieDebug.error("onChanged","time_h = "+time_h);
					GenieDebug.error("onChanged","time_m = "+time_m);
					GenieDebug.error("onChanged","dayIndex = "+dayIndex);
					
					//picker.setCurrentHour(hours.getCurrentItem());
					//picker.setCurrentMinute(mins.getCurrentItem());
					timeChanged = false;
				}
			}
		};

		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);
		days.addChangingListener(wheelListener);
		//tmsetting.addChangingListener(wheelListener);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				timeScrolled = true;
			}
			public void onScrollingFinished(WheelView wheel) {
				timeScrolled = false;
				timeChanged = true;
				//controlIndex = tmsetting.getCurrentItem();
				time_h = hours.getCurrentItem();
				time_m = mins.getCurrentItem();
				dayIndex = days.getCurrentItem()+1;
				
				GenieDebug.error("onScrollingFinished","time_h = "+time_h);
				GenieDebug.error("onScrollingFinished","time_m = "+time_m);
				GenieDebug.error("onScrollingFinished","dayIndex = "+dayIndex);
				
				//picker.setCurrentHour(hours.getCurrentItem());
				//picker.setCurrentMinute(mins.getCurrentItem());
				timeChanged = false;
			}
		};
		
		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
		days.addScrollingListener(scrollListener);
		//tmsetting.addScrollingListener(scrollListener);
		
*/		
	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * @param wheel the wheel
	 * @param label the wheel label
	 */
//	private void addChangingListener(final WheelView wheel, final String label) {
//		wheel.addChangingListener(new OnWheelChangedListener() {
//			public void onChanged(WheelView wheel, int oldValue, int newValue) {
//				wheel.setLabel(newValue != 1 ? label + "s" : label);
//			}
//		});
//	}

	
/*    private void InitSpinnerData_1() {
		// TODO Auto-generated method stub
    	
    	monthlimit = (EditText)findViewById(R.id.monthlimit);
    	
    	TimePicker timePicker=(TimePicker)findViewById(R.id.TimePicker01);
    	timePicker.setCurrentHour(time_h);
    	timePicker.setCurrentMinute(time_m);
    	
    	timePicker.setOnTimeChangedListener(new OnTimeChangedListener(){
    		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) 
    		{
    			GenieDebug.error("debug","閿熸枻鎷烽�閿熸枻鎷烽敓缁炴唻鎷烽敓鏂ゆ嫹鐗甸敓锟�hourOfDay+"鏃�+minute+"閿熻鈽呮嫹");
    			time_h = hourOfDay;
    			time_m = minute;
    		}
    	});
		
//////////////////////////////////////////////////////////////////////////////////
		Spinner_control = (Spinner)findViewById(R.id.tmsettingSpinner_01);	
		adapter_control = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_control);
		
		// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨鎲嬫嫹閿熸枻鎷�
		adapter_control.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //閿熸枻鎷稟dapter閿熸枻鎷锋嫢閿熺禈dapter_channel
		Spinner_control.setAdapter(adapter_control);
    	
		Spinner_control.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				controlIndex = arg2;
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	Spinner_day = (Spinner)findViewById(R.id.tmsettingSpinner_date);	
		adapter_day = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_day);
		
		// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨鎲嬫嫹閿熸枻鎷�
		adapter_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //閿熸枻鎷稟dapter閿熸枻鎷锋嫢閿熺禈dapter_channel
    	Spinner_day.setAdapter(adapter_day);
    	
    	Spinner_day.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				dayIndex = arg2 + 1;
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});		
		
	}
*/
//	public void InitSpinnerData()
//	{
//		
//		//////////////////////////////////////////////////////////////////////////////////
//		Spinner_control = (Spinner)findViewById(R.id.tmsettingSpinner_01);	
//		adapter_control = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_control);
//		
//		// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨鎲嬫嫹閿熸枻鎷�
//		adapter_control.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        
//        //閿熸枻鎷稟dapter閿熸枻鎷锋嫢閿熺禈dapter_channel
//		Spinner_control.setAdapter(adapter_control);
//    	
//		Spinner_control.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				controlIndex = arg2;
//				arg0.setVisibility(View.VISIBLE);
//
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//    	//////////////////////////////////////////////////////////////////////////////////
//    	Spinner_time = (Spinner)findViewById(R.id.tmsettingSpinner_time);	
//		adapter_time = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_str);
//		
//		// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨鎲嬫嫹閿熸枻鎷�
//		adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        
//        //閿熸枻鎷稟dapter閿熸枻鎷锋嫢閿熺禈dapter_channel
//    	Spinner_time.setAdapter(adapter_time);
//    	
//    	Spinner_time.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				timeIndex = arg2;
//				arg0.setVisibility(View.VISIBLE);
//
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//    	////////////////////////////////////////////////////////////////////////////////////////
//    	Spinner_day = (Spinner)findViewById(R.id.tmsettingSpinner_date);	
//		adapter_day = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_day);
//		
//		// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨鎲嬫嫹閿熸枻鎷�
//		adapter_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        
//        //閿熸枻鎷稟dapter閿熸枻鎷锋嫢閿熺禈dapter_channel
//    	Spinner_day.setAdapter(adapter_day);
//    	
//    	Spinner_day.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				dayIndex = arg2;
//				arg0.setVisibility(View.VISIBLE);
//
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//	}
//	
//	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
		//MenuInflater inflater = getMenuInflater();  
		//inflater.inflate(R.menu.menu, menu);  
		//menu.findItem(R.id.item01).setTitle("Apply");
		//menu.findItem(R.id.item02).setTitle("Cancel").setVisible(false);		
			
		return true;  
	}
	    
	
	private void showtoast()
	{
		 toast = Toast.makeText(this,
		   	     getResources().getString(R.string.inputerror), Toast.LENGTH_LONG);
		   	   toast.setGravity(Gravity.CENTER, 0, 0);
		   	   toast.show();
	}
	
	public void sendMessage2UI(int msg)
    {
    	handler.sendEmptyMessage(msg);
    }
	
	public void OnSender()
	{

		TrafficMeterSettingSender();
		
//	   showWaitingDialog();
//	   GenieDebug.error("debug", "onMenuid_100 --1--");
//	    	
//	   new Thread()
//	   {
//		   public void run() 
//	    	{   
//	    		GenieDebug.error("debug", "onMenuid_100 --run--");
//	    		
//	    		if(TrafficMeterSettingSender())
//	    		{
//	    			sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);    				
//	    		}else
//	    		{
//	    			sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//	    		}
//	    		
//	    	}
//	    }.start();  
	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) 
		{  
			case R.id.item01: 		    	
				 GenieDebug.error("debug", "onOptionsItemSelected m_controlIndex = "+m_control[controlIndex]);
				 GenieDebug.error("debug", "onOptionsItemSelected monthlimit = "+monthlimit.getText().toString());
				 GenieDebug.error("debug", "onOptionsItemSelected time_h = "+Integer.toString(time_h));
				 GenieDebug.error("debug", "onOptionsItemSelected time_m = "+Integer.toString(time_m));
				 GenieDebug.error("debug", "onOptionsItemSelected dayIndex = "+Integer.toString(dayIndex));
				 
				 String hour = trafficmeter_hour[sp_input_hour];
				 String mins = input_mins.getText().toString();
				 
				 GenieDebug.error("debug", "onOptionsItemSelected hour = "+hour);
				 GenieDebug.error("debug", "onOptionsItemSelected mins = "+mins);
				 
				 if(monthlimit.getText().toString().length() == 0 && hour.length()  == 0 && mins.length() == 0 )
				 {
					 showtoast();
				 }else
				 {
					 try{
						 time_h = Integer.parseInt(hour);
						 time_m = Integer.parseInt(mins);
					 }catch(Exception e){
						 e.printStackTrace();
						 showtoast();
						 return true;
					 }
					 if(time_h > 23 || time_h < 0 || time_m > 59 || time_m < 0 )
					 {
						 showtoast();
						 return true;
					 }
					 OnSender();
				 }
				return true;  
		}
		return false;
	}
	private Boolean TrafficMeterSettingSender()
	{
		Boolean status = false;
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL_SET, m_control[controlIndex]);
    	GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT_SET, monthlimit.getText().toString());
    	GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR_SET, Integer.toString(time_h));
    	GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE_SET, Integer.toString(time_m));
    	GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY_SET, Integer.toString(dayIndex));
    	
    	SetTrafficMeterOptions();
    	//soapCommand.configurationstarted();
    	//status = soapCommand.setTrafficMeterOptions();
    	//soapCommand.configurationfinished();
    	
    	return status;

	}
	
	
	private void SetTrafficMeterOptions()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
	  	GenieRequestInfo request0 = new GenieRequestInfo();
	  	request0.aRequestLable="GenieTrafficSetting";
	  	request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigTraffic;
	  	request0.aServer = "DeviceConfig";
	  	request0.aMethod = "SetTrafficMeterOptions";
	  	request0.aNeedwrap = true;
	  	request0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request0.aNeedParser=false;
	  	request0.aTimeout = 25000;
	  	request0.aElement = new ArrayList<String>();
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL_SET));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT_SET));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR_SET));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE_SET));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY_SET));
		
	  	requestinfo.add(request0);
	  	


	  	m_SettingRequest = new GenieRequest(this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
	}
	
	
	protected void onStart() {
		// TODO Auto-generated method stub
    	
		super.onStart();
					
		RegisterBroadcastReceiver();
	
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		StopRequest();
		UnRegisterBroadcastReceiver();
	}
	
	private Activity GetTrafficActivity()
	{
		return this;
	}
	
	
	private GenieRequest m_SettingRequest = null;
        
    private void StopRequest()
    {
  	  if(null != m_SettingRequest)
  	  {
  		m_SettingRequest.Stop();
  		m_SettingRequest = null;
  	  }  	
  	  
		  try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void HideSoftKeyboard()
    {
//    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
//    	boolean isOpen=imm.isActive();  
//    	GenieDebug.error("debug","HideSoftKeyboard isOpen = "+isOpen);
//    	

    	
//    	if(isOpen)
//    	{
//    		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
//    	}
        
        
    }
    

	
	
	   private RequestReceiver m_RequestReceiver = null;
	    
		 private void UnRegisterBroadcastReceiver()
		 {
			 if(null != m_RequestReceiver)
				{
					unregisterReceiver(m_RequestReceiver);
					m_RequestReceiver = null;
				}
		 }

		 private void RegisterBroadcastReceiver()
		 {
			 UnRegisterBroadcastReceiver();
			 
				m_RequestReceiver = new RequestReceiver();
				IntentFilter filter = new IntentFilter();//
				filter.addAction(GenieGlobalDefines.REQUEST_ACTION_RET_BROADCAST);
				registerReceiver(m_RequestReceiver, filter);//
		 }
		 
		 
		  private class RequestReceiver extends BroadcastReceiver{//


		         @Override
			        public void onReceive(Context context, Intent intent) {//
		     
		     		String lable = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_LABLE);
		     		RequestActionType ActionType = (RequestActionType) intent.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE);
		     		RequestResultType aResultType = (RequestResultType) intent.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE);
		     		String aServer = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SERVER);
		     		String aMethod = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_METHOD);
		     		String aResponseCode = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSECODE);
		     		int aHttpResponseCode = intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTPRESPONSECODE,-2);
		     		int aHttpType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTP_TYPE,-2);
		     		int aSoapType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SOAP_TYPE,-2);
		     		String aResponse = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSE);
		     	
		     		GenieDebug.error("debug", "RequestReceiver onReceive lable ="+lable);
	    		
		     		
//		     		if(aSoapType == GenieGlobalDefines.ESoapRequestGuestEnable
//		     				|| aSoapType == GenieGlobalDefines.ESoapReqiestTrafficEnable)
//		     		{
//		     			aResponseCode = "401";
//		     			aResultType = RequestResultType.failed;
//		     		}
		     		
		     		if(lable != null && lable.equals("GenieTrafficSetting"))
		     		{
			     		GenieDebug.error("debug", "RequestReceiver onReceive aServer ="+aServer);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aMethod ="+aMethod);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResponseCode ="+aResponseCode);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aHttpResponseCode ="+aHttpResponseCode);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResponse ="+aResponse);
			     		GenieDebug.error("debug", "RequestReceiver onReceive ActionType ="+ActionType);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResultType ="+aResultType);

			     		if(null != ActionType && ActionType == RequestActionType.Soap)
			     		{
			     			if(null != aResultType)
			     			{
			     				
			     				
			     				switch(aSoapType)
			     				{
			     				case GenieGlobalDefines.ESoapRequestTrafficOptions:
			     					if(aResultType == RequestResultType.Succes)
				     				{
					     				SetViewDate();
				     				}
			     	        		break;	
			     				case GenieGlobalDefines.ESoapRequestConfigTraffic:
			     					StopRequest();
			     					HideSoftKeyboard();
			     					GetTrafficActivity().setResult(GenieGlobalDefines.TRAFFICSETTING_RESULT);
			     					GetTrafficActivity().finish();
			     					break;
			     				default:
			     					break;
			     				}
			     				
			     			}
			     		}
		     		}

		         }
		  }

}
