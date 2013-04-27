package com.dragonflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class GenieWifiModify extends Activity implements DialogInterface.OnClickListener
{
//	private GenieSoap soapCommand = null;
	private int activeIndex = 0;
	
	
	private int  ichannel = 0;
	private int  isecurity = 0;
	private String iusername = null;
	private String ipassword = null;
	
	private  String [] m_Channel_us = new String[] {"Auto","1","2","3","4","5","6","7","8","9","10","11"};
	private  String [] m_Channel = new String[] {"Auto","1","2","3","4","5","6","7","8","9","10","11","12","13"};
	
	private  String [] Channel = null;
	
	private  String [] m_string_01 = new String[] {"None","WPA2-PSK[AES]","WPA-PSK+WPA2-PSK"};
	private  String [] m_accesstime = new String[] {"Always","1 hour","5 hours","10 hours","1 day","1 week"};

	
	private Boolean m_input_username = false;
	private Boolean m_input_password = false;
	
	
	private static final int cAddHours[]={0,1,5,10,0,0};
	private static final int cAddDays[]={0,0,0,0,1,7};
    
	public EditText usernameET = null;
	public EditText passwordET = null;
	public TextView tpassword = null;
	public TextView tpasswordlimit = null;
	public Spinner Spinner_channel = null;
	private int m_channal_id = 0;
	private int m_Wpa_id = 0;
	private Button button_save = null;
	public Spinner Spinner_01 = null;
	private int size = 0;
	
	private Timer settimer = null;
	private TimerTask task = null;
	private int seconds = 100;
	private Boolean ConnectWififlag = false; 
	
	private ProgressDialog progressDialog = null;
	private ProgressDialog progressSetDialog = null;
	
	public GenieUserInfo userInfo = null;
	
	private ArrayAdapter<String> adapter_channel = null;
	private ArrayAdapter<String> adapter_01 = null;
	private String[] adapter_temp = null;
	
	private boolean WheelChanged = false;
	
	
	String GuesstModes[]={"None","WPA2-PSK","Mixed WPA"};
	String cModes[]={"None","WPA2-PSK","WPA-PSK/WPA2-PSK"};
	//String cModes[]={"None","WPA2-PSK","WPA-PSK"};
	
	//
	private boolean WheelScrolled = false;
	
	private Handler handler = new Handler()
	{   
        public void handleMessage(Message msg) 
        {   
        	String error = null;
        	GenieDebug.error("handleMessage", "listview handleMessage msg.what = "+msg.what);
        	switch(msg.what)
        	{
        	case GenieGlobalDefines.ESoapRequestsuccess: 
        		error = getResources().getString(R.string.success);
        		break;
        	case GenieGlobalDefines.ESoapRequestfailure :  
        		error = getResources().getString(R.string.failure);
        		//finish();
        		break;
        	case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS :          		
        		break;	
        	}
        	//CloseSetProgressDialog(0);
        	//CloseProgressDialog();
        	//showToast(error);
        }
	}; 
	

	
	
	 public void showToast(String error)
	 {
	   Toast toast = Toast.makeText(this,error, Toast.LENGTH_LONG);
	   toast.setGravity(Gravity.CENTER, 0, 0);
	   toast.show();

	 }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
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
		
		//setTheme(R.style.activityTitlebarNoSearch); //qicheng.ai change
		
		activeIndex = getIntent().getIntExtra(GenieGlobalDefines.LIST_TYPE_INDEX,0);
		
		 //requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
		requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
	   // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    	
        setContentView(R.layout.modify_2); 
        
        if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
		}else
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);    
		}
    	
    	

    	InitSsidView();
    	
    	//InitChannalView();
    	//InitWpaView();
    	
    	//button_save =  (Button) findViewById(R.id.savebutton);
    	//button_save.setOnClickListener(this);  
    	
    	InitPasswordView();
    	
    	InitChannalAndWpaView();
    	

    	GenieDebug.error("debug","wifimodeify onCreate activeIndex = "+activeIndex);
    	
//    	if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS)
//    	{
//    		
//    		if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1") == -1)
//    		{
//    			
//    			GenieDebug.error("debug","SetSaveButtonStatus   1000");
//    			m_Wpa_id = 0;
//    			Spinner_01.setSelection(m_Wpa_id, true);
//    			
//    			SetPassWordView_Gone();
//    			m_save.setEnabled(true);
//				m_save.setTextColor(Color.WHITE);
//    		}
//    	}
    	
    	SetSaveButtonStatus();
    	
    	
    	
    	//soapCommand = new GenieSoap(this);
    	
    	userInfo = GenieShareAPI.ReadDataFromFile(this, GenieGlobalDefines.USER_INFO_FILENAME);
    	
    	//getModelInfo();
    	//InitText();

    	
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
	
	public void InitTitleView(int text)
	{
		Button  back = (Button)findViewById(R.id.back);
		m_save = (Button)findViewById(R.id.about);
		
		m_save.setText(R.string.save);
		
		TextView title = (TextView)findViewById(R.id.netgeartitle);
		title.setText(text);
		
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
				showConfigDialog();
			}
		});
		
		back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //锟斤拷为抬锟斤拷时锟斤拷图片      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		

		
		m_save.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //锟斤拷为抬锟斤拷时锟斤拷图片      
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
	
	
    private void sendMessage2UI(int msg)
    {
    	handler.sendEmptyMessage(msg);
    }
	
//	public void changeScreenOrientation(Configuration newConfig)
//	{
//		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//		Display display = windowManager.getDefaultDisplay();
//
//		 GenieDebug.error("changeScreenOrientation", "changeScreenOrientation !!!!!!!!!");
//	}
	
   @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
//		MenuInflater inflater = getMenuInflater();  
//		inflater.inflate(R.menu.menu, menu);  
//		menu.findItem(R.id.item01).setTitle("save");
//		
//
//		menu.findItem(R.id.item02).setVisible(false);
//
//		
			
		return true;  
	}
	    

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) 
		{  
			case R.id.item01: 		    	
		    	showConfigDialog();
				return true;  
		}
		return false;
	}

	public void InitSsidView()
	{
		GenieDebug.error("InitSsidView","--strart--!!");
		usernameET = (EditText) findViewById(R.id.ename);   
		usernameET.setText("NETGEAR_GUEST");
		
//		if(sname.getText().toString().length() > 0)
//		{
//			signln_input_username = true;
//		}else
//		{
//			signln_input_username = false;
//		}
		usernameET.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				if(sname.getText().toString().length() > 0)
//				{
//					
//				}
				
				
				GenieDebug.error("ChangInput"," 12345 start = "+start);
				GenieDebug.error("ChangInput"," 12345 before = "+before);
				GenieDebug.error("ChangInput"," 12345 count = "+count);
				
				SetSaveButtonStatus();
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void SetPassWordView_Visible()
	{
		tpassword.setVisibility(View.VISIBLE);		
		passwordET.setVisibility(View.VISIBLE);
		tpasswordlimit.setVisibility(View.VISIBLE);
		
		SetSaveButtonStatus();
	}
	public void SetPassWordView_Gone()
	{
		tpassword.setVisibility(View.GONE);
		passwordET.setVisibility(View.GONE);
		tpasswordlimit.setVisibility(View.GONE);
		
		SetSaveButtonStatus();
	}
	public void InitPasswordView()
	{
		GenieDebug.error("InitPasswordView","--strart--!!");
		passwordET = (EditText) findViewById(R.id.epassword);    	
		tpassword = (TextView) findViewById(R.id.tpassword);
		tpasswordlimit = (TextView) findViewById(R.id.tpasswordlimit);
		
		passwordET.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				if(sname.getText().toString().length() > 0)
//				{
//					
//				}
				
				
				GenieDebug.error("ChangInput"," 12345 start = "+start);
				GenieDebug.error("ChangInput"," 12345 before = "+before);
				GenieDebug.error("ChangInput"," 12345 count = "+count);
				
				SetSaveButtonStatus();
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public void InitText()
	{
		GenieDebug.error("InitText","--strart--!!");
		switch(activeIndex)
	    {
	        case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
	        	
	        	InitTitleView(R.string.editsetting);
	        	InitWirelessSettingText();
	        	break;
				
			case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
				InitTitleView(R.string.guestsettings);
				InitGuestSettingText();
				break;
		}
	}
	
	
	public void InitWirelessSettingText()
	{
		try{
			GenieDebug.error("InitWirelessSettingText","--strart--!!");
			usernameET.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID));
			iusername = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
			
			if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES).equals("WEP"))
			{
				if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY).equals("None"))
				{
					passwordET.setText("");
					ipassword = "";
				}else
				{
					passwordET.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY));
					ipassword = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY);
				}
			}else
			{
				if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY).equals("None"))
				{
					passwordET.setText("");
					ipassword = "";
				}else
				{
					passwordET.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY));
					ipassword = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY);
				}
			}
			
			String wlan_region = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION);
			
			GenieDebug.error("InitChannalView","activeIndex = "+wlan_region);
			if(wlan_region.indexOf("US") != -1 || wlan_region.indexOf("us") != -1 || wlan_region.indexOf("United States") != -1 )
			{
				Channel = m_Channel_us;
			}else
			{
				Channel = m_Channel;
			}
			adapter_temp = Channel;
			
			TextView textview1 = (TextView) findViewById(R.id.channel_text);
			textview1.setText(getResources().getString(R.string.channel));
			TextView textview2 = (TextView) findViewById(R.id.wpa_text);
			textview2.setText(getResources().getString(R.string.wpa));
			
			SetSaveButtonStatus();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	
	private void SetSaveButtonStatus()
	{
		
		
		
		if(null == passwordET || null == usernameET )
			return ;
		
		if(passwordET.getText().toString().length() >= 8)
		{
			m_input_password = true;
		}else
		{
			m_input_password = false;
		}
		
		if(usernameET.getText().toString().length() > 0)
		{
			m_input_username = true;
		}else
		{
			m_input_username = false;
		}
		
		GenieDebug.error("debug","SetSaveButtonStatus m_input_username = "+m_input_username);
		GenieDebug.error("debug","SetSaveButtonStatus m_input_password = "+m_input_password);
		GenieDebug.error("debug","SetSaveButtonStatus ichannel = "+ichannel);
		GenieDebug.error("debug","SetSaveButtonStatus m_channal_id = "+m_channal_id);
		GenieDebug.error("debug","SetSaveButtonStatus m_Wpa_id = "+m_Wpa_id);
		GenieDebug.error("debug","SetSaveButtonStatus isecurity = "+isecurity);
		
		if(passwordET.getVisibility() != View.VISIBLE)
		{
			if(m_input_username)
			{
				GenieDebug.error("InitGuestSettingText","m_input  0");
				if(null != m_save)
				{
					GenieDebug.error("InitGuestSettingText","m_input  1");
					GenieDebug.error("InitGuestSettingText","m_input  111"+"iusername: "+iusername+"usernameET: "+usernameET.getText().toString());
					if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS)
			    	{
			    		
			    		if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1") == -1)
			    		{
			    			
			    			GenieDebug.error("debug","SetSaveButtonStatus   1000");
			    			
			    			m_save.setEnabled(true);
							m_save.setTextColor(Color.WHITE);
			    		}else
			    		{
			    			if((iusername!=null&&!iusername.equals(usernameET.getText().toString().trim()))||
			    					(ipassword != null && !ipassword.equals(passwordET.getText().toString())) || 
									ichannel != m_channal_id ||
									isecurity != m_Wpa_id )
							{
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
						if((iusername!=null&&!iusername.equals(usernameET.getText().toString().trim()))||
								(ipassword != null && !ipassword.equals(passwordET.getText().toString())) || 
								ichannel != m_channal_id ||
								isecurity != m_Wpa_id )
						{
							m_save.setEnabled(true);
							m_save.setTextColor(Color.WHITE);
						}else
						{
							m_save.setEnabled(false);
							m_save.setTextColor(Color.GRAY);
						}
			    	}
				}
			}else
			{
				GenieDebug.error("InitGuestSettingText","m_input  2");
				if(null != m_save)
				{
					GenieDebug.error("InitGuestSettingText","m_input  3");
					m_save.setEnabled(false);
					m_save.setTextColor(Color.GRAY);
				}
			}
		}else
		{
			if(m_input_username && m_input_password)
			{
				GenieDebug.error("InitGuestSettingText","m_input  4");
				if(null != m_save)
				{
					
					if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS)
			    	{
			    		
			    		if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1") == -1)
			    		{
			    			
			    			GenieDebug.error("debug","SetSaveButtonStatus   1000");
			    			
			    			m_save.setEnabled(true);
							m_save.setTextColor(Color.WHITE);
			    		}else{
			    			if((ipassword != null && !ipassword.equals(passwordET.getText().toString())) ||
									(iusername != null && !iusername.equals(usernameET.getText().toString()))||
									ichannel != m_channal_id ||
									isecurity != m_Wpa_id )
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
						if((iusername!=null&&!iusername.equals(usernameET.getText().toString().trim()))||
								(ipassword != null && !ipassword.equals(passwordET.getText().toString())) || 
								ichannel != m_channal_id ||
								isecurity != m_Wpa_id )
						{
							m_save.setEnabled(true);
							m_save.setTextColor(Color.WHITE);
						}else
						{
							m_save.setEnabled(false);
							m_save.setTextColor(Color.GRAY);
						}
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
	}
	
	public void InitGuestSettingText()
	{
		
		
    	//if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1") > -1)
    	//{
    		
		String guestssid = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID);
		if(guestssid != null && guestssid.length() > 1 && !guestssid.equals(GenieGlobalDefines.NULL))
		{	
			usernameET.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID));
    		
    		if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY).equals(GenieGlobalDefines.NULL))
    		{
    			GenieDebug.error("debug","InitGuestSettingText password 0");
    			passwordET.setText("");
    		}else
    		{
    			//GenieDebug.error("debug","InitGuestSettingText password 1 ["+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY)+"]");
    			passwordET.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY));
    		}
		}
    		
    	//}
    	
    	iusername = usernameET.getText().toString();
    	ipassword = passwordET.getText().toString();
    	
		GenieDebug.error("InitGuestSettingText","--strart--!!");
		adapter_temp = m_accesstime;
		
		TextView textview1 = (TextView) findViewById(R.id.channel_text);
		textview1.setText(getResources().getString(R.string.guestaccess_time));
		TextView textview2 = (TextView) findViewById(R.id.wpa_text);
		textview2.setText(getResources().getString(R.string.wpa));
		
		SetSaveButtonStatus();
	}
	
/*	public void InitChannalAndWpaView()
	{
		
		InitText();
		
		final WheelView channel = (WheelView) findViewById(R.id.channel);
		channel.setAdapter(new ArrayWheelAdapter<String>(adapter_temp));
		//channel.setLabel("hours");
	
		final WheelView wpa = (WheelView) findViewById(R.id.wpa);
		wpa.setAdapter(new ArrayWheelAdapter<String>(m_string_01));
		//wpa.setLabel("mins");
		wpa.setCyclic(true);
	
	
	
		// set current time
//		Calendar c = Calendar.getInstance();
//		int curHours = c.get(Calendar.HOUR_OF_DAY);
//		int curMinutes = c.get(Calendar.MINUTE);
		
		channel.setCurrentItem(m_channal_id);
		
		for(int i = 0;i<size;i++)
    	{
    		GenieDebug.error("InitChannalAndWpaView",GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET));
    		//GenieDebug.error("InitChannalView","adapter_temp[i] = "+adapter_temp[i]);
    		
    		if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET).equals(m_Channel[i]))
        	{
        		m_channal_id = i;        		
        		channel.setCurrentItem(m_channal_id);
        		break;
        	}
        	
    		
    	}
		
		if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("None"))
		{    		
    		SetPassWordView_Gone();
    		m_Wpa_id = 0;
    		
		}else if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("WPA2-PSK"))
		{
    		m_Wpa_id = 1;
    		
		}else if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("WPA-PSK/WPA2-PSK"))
		{
    		m_Wpa_id = 2;
    		
		}		
		wpa.setCurrentItem(m_Wpa_id);
	

	
		// add listeners
		//addChangingListener(mins, "min");
		//addChangingListener(hours, "hour");
	
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!WheelScrolled) {
					WheelChanged = true;
					m_channal_id = channel.getCurrentItem();
					m_Wpa_id = wpa.getCurrentItem();
					if(0 == m_Wpa_id)
					{
						SetPassWordView_Gone();
					}else
					{
						SetPassWordView_Visible();
					}
					GenieDebug.error("onChanged","--m_channal_id = "+m_channal_id);
					GenieDebug.error("onChanged","--m_Wpa_id = "+m_Wpa_id);
					WheelChanged = false;
				}
			}
		};

		channel.addChangingListener(wheelListener);
		wpa.addChangingListener(wheelListener);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				WheelScrolled = true;
			}
			public void onScrollingFinished(WheelView wheel) {
				WheelScrolled = false;
				WheelChanged = true;
				m_channal_id = channel.getCurrentItem();
				m_Wpa_id = wpa.getCurrentItem();
				if(0 == m_Wpa_id)
				{
					SetPassWordView_Gone();
				}else
				{
					SetPassWordView_Visible();
				}
				GenieDebug.error("onScrollingFinished","--m_channal_id = "+m_channal_id);
				GenieDebug.error("onScrollingFinished","--m_Wpa_id = "+m_Wpa_id);
				WheelChanged = false;
			}
		};
		
		channel.addScrollingListener(scrollListener);
		wpa.addScrollingListener(scrollListener);
		

	}
*/
	public void InitChannalAndWpaView()
	{
		
		InitText();
		
		Spinner_channel = (Spinner)findViewById(R.id.channel);
		
//		if(activeIndex ==  GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS)
//    	{
//			adapter_temp = m_Channel;
//    	}else if(activeIndex ==  GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS)
//    	{
//    		adapter_temp = m_accesstime;
//    	}
    	//锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷ArrayAdapter锟斤拷锟斤拷
		
		GenieDebug.error("InitChannalView","activeIndex = "+activeIndex);
		GenieDebug.error("InitChannalView","ACTIVE_FUNCTION_WIRELESS = "+GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		
		if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS)
    	{
			String wlan_region = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION);
			
			GenieDebug.error("InitChannalView","wlan_region = "+wlan_region);
			if(wlan_region != null && (wlan_region.indexOf("US") != -1 || wlan_region.indexOf("us") != -1 || wlan_region.indexOf("United States") != -1 ))
			{
				Channel = m_Channel_us;
			}else
			{
				Channel = m_Channel;
			}
			adapter_channel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Channel);
			size = Channel.length;
    	}else
    	{
    		Channel = m_accesstime;
    		adapter_channel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Channel);
    		size = Channel.length;
    	}
        
    	
        // 锟斤拷锟斤拷锟斤拷锟斤拷锟叫憋拷锟斤拷
    	adapter_channel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
        //锟斤拷Adapter锟斤拷拥锟絘dapter_channel
    	Spinner_channel.setAdapter(adapter_channel);
    	
    	Spinner_channel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				m_channal_id = arg2;
				
				SetSaveButtonStatus();
				
				GenieDebug.error("debug","Spinner_channel  m_channal_id = "+m_channal_id);
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	
    	m_channal_id = 0; 
    	GenieDebug.error("InitChannalView"," "+size);
    	
    	
    	String guesttime = null;
    	String guestMac = null;
    	String wlan_mac = null;
    	
		HashMap<String,String> accesstime =  GenieSerializ.ReadMap(this, "guestaccess");

		
		
		
		
		if(accesstime != null)
		{			
			guesttime = accesstime.get("GUESTTIME");
			guestMac = accesstime.get("GUESTTIMEMAC");
		}else
		{
			GenieDebug.error("InitChannalView","guesttime == null");
		}
		wlan_mac = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
												
		GenieDebug.error("InitChannalView","InitChannalView guesttime = "+guesttime);
		GenieDebug.error("InitChannalView","InitChannalView GUESTTIMEMAC guestMac = "+guestMac);
		GenieDebug.error("InitChannalView","InitChannalView wlan_mac = "+wlan_mac);
		
		
    	
    	for(int i = 0;i<size;i++)
    	{
    		GenieDebug.error("InitChannalView","DICTIONARY_KEY_WLAN_CHANNEL = "+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL));
    		//GenieDebug.error("InitChannalView","adapter_temp[i] = "+adapter_temp[i]);
    		if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS)
        	{
//    			  if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES).equals("WEP"))
//    			  {
//    				  m_channal_id = 1;
//	        			Spinner_channel.setSelection(m_channal_id, true);
//	        			break;
//    			  }else
//    			  {
		    			if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL).equals(m_Channel[i])
		    					|| GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL).equals(("0"+m_Channel[i])))
		        		{
		        			m_channal_id = i;
		        			Spinner_channel.setSelection(m_channal_id, true);
		        			break;
		        		}
    			  //}
        	}else
        	{
        		//if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1") > -1)
        		//{
        			if(guesttime != null && guestMac != null )
        			{	
        				if(guestMac.equals(wlan_mac) && guesttime.equals(m_accesstime[i]))
        				{
        					m_channal_id = i;
        					Spinner_channel.setSelection(m_channal_id, true);
        					break;
        				}
        			}
        		//}
        	}
    		
    	}
    	
    	ichannel = m_channal_id;
    	
    	
////////////////////////////////////////
		GenieDebug.error("InitWpaView","--strart--!!");
    	Spinner_01 = (Spinner)findViewById(R.id.wpa);
    	//锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷ArrayAdapter锟斤拷锟斤拷
    	adapter_01 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_string_01);
        
        // 锟斤拷锟斤拷锟斤拷锟斤拷锟叫憋拷锟斤拷
    	adapter_01.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //锟斤拷Adapter锟斤拷拥锟絘dapter_01
    	Spinner_01.setAdapter(adapter_01);
    	
    	Spinner_01.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				m_Wpa_id = arg2;
				if(m_Wpa_id > 0)
				{
					//tpassword.setVisibility(View.VISIBLE);
					//passwordET.setEnabled(true);
					//passwordET.setVisibility(View.VISIBLE);
					SetPassWordView_Visible();
				}else if(m_Wpa_id == 0)
				{
					//passwordET.setEnabled(false);
					//passwordET.setVisibility(View.GONE);
					SetPassWordView_Gone();
				}
				SetSaveButtonStatus();
				
				GenieDebug.error("debug","Spinner_01  m_Wpa_id = "+m_Wpa_id);
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	GenieDebug.error("InitWpaView",GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY));
    	

    	m_Wpa_id = 0;
    	
    	GenieDebug.error("InitWpaView", "DICTIONARY_KEY_WLAN_WPA_KEY = "+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY));
    	if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS)
    	{
    		
			  if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES).equals("WEP"))
			  {
				  	m_Wpa_id = 1;
	    		  	Spinner_01.setSelection(m_Wpa_id, true);
			  }else
			  {
		    		String  wpa = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY);
		    		
		    		GenieDebug.error("wpa", "DICTIONARY_KEY_WLAN_WPA_KEY wpa = "+wpa);
		    		if(wpa.equals("None"))
		    		{
		    			//tpassword.setVisibility(View.INVISIBLE);
		    			//passwordET.setEnabled(false);
		    			//passwordET.setVisibility(View.GONE);
		    			SetPassWordView_Gone();
		    			m_Wpa_id = 0;
		    			Spinner_01.setSelection(m_Wpa_id, true);
		    		}else if(wpa.equals("WPA2-PSK") || wpa.equals("WEP64") || wpa.equals("WEP128"))
		    		{
		    			m_Wpa_id = 1;
		    			Spinner_01.setSelection(m_Wpa_id, true);
		    		}else if(wpa.equals("WPA-PSK/WPA2-PSK") || wpa.equals("WPA-PSK") || wpa.equals("Mixed WPA"))
		    		{
		    			
		    			m_Wpa_id = 2;
		    			Spinner_01.setSelection(m_Wpa_id, true);
		    		}else
		    		{
		    			m_Wpa_id = 1;
		    			Spinner_01.setSelection(m_Wpa_id, true);
		    		}
			  }
    	}else
    	{
    		String  wpa = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE);
    		
    		GenieDebug.error("wpa", "DICTIONARY_KEY_GUEST_MODE wpa = "+wpa);
    		
    		if(wpa!= null && wpa.equals("None"))
    		{
    			//tpassword.setVisibility(View.INVISIBLE);
    			//passwordET.setEnabled(false);
    			//passwordET.setVisibility(View.GONE);
    			SetPassWordView_Gone();
    			m_Wpa_id = 0;
    			Spinner_01.setSelection(m_Wpa_id, true);
    		}else if(wpa!= null && (wpa.equals("WPA2-PSK") || wpa.equals("WEP64") || wpa.equals("WEP128")))
    		{
    			m_Wpa_id = 1;
    			Spinner_01.setSelection(m_Wpa_id, true);
    		}else if(wpa!= null && (wpa.equals("WPA-PSK/WPA2-PSK") || wpa.equals("WPA-PSK") || wpa.equals("Mixed WPA")))
    		{
    			m_Wpa_id = 2;
    			Spinner_01.setSelection(m_Wpa_id, true);
    		}else
    		{
    			m_Wpa_id = 0;
    			Spinner_01.setSelection(m_Wpa_id, true);
    			
    			SetPassWordView_Gone();
    		}
    	}
			

    	
    	isecurity = m_Wpa_id;
    	
    	
    	if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS)
    	{
    		if(GenieRequest.m_SmartNetWork)
    		{
    			TextView textview1 = (TextView) findViewById(R.id.channel_text);
    			textview1.setVisibility(View.GONE);
    			Spinner_channel.setVisibility(View.GONE);
    		}else
    		{
    			TextView textview1 = (TextView) findViewById(R.id.channel_text);
    			textview1.setVisibility(View.VISIBLE);
    			Spinner_channel.setVisibility(View.VISIBLE);
    		}
    	}
		
		
	}
	/*
	public void InitChannalView()
	{
		GenieDebug.error("InitChannalView","--strart--!!");
		Spinner_channel = (Spinner)findViewById(R.id.Spinner_channel);
		
//		if(activeIndex ==  GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS)
//    	{
//			adapter_temp = m_Channel;
//    	}else if(activeIndex ==  GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS)
//    	{
//    		adapter_temp = m_accesstime;
//    	}
    	//锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷ArrayAdapter锟斤拷锟斤拷
		
		GenieDebug.error("InitChannalView","activeIndex = "+activeIndex);
		GenieDebug.error("InitChannalView","ACTIVE_FUNCTION_WIRELESS = "+GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		
		if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS)
    	{
			adapter_channel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_Channel);
			size = m_Channel.length;
    	}else
    	{
    		adapter_channel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_accesstime);
    		size = m_accesstime.length;
    	}
        
    	
        // 锟斤拷锟斤拷锟斤拷锟斤拷锟叫憋拷锟斤拷
    	adapter_channel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //锟斤拷Adapter锟斤拷拥锟絘dapter_channel
    	Spinner_channel.setAdapter(adapter_channel);
    	
    	Spinner_channel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				m_channal_id = arg2;
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	GenieDebug.error("InitChannalView"," "+size);
    	
    	for(int i = 0;i<size;i++)
    	{
    		GenieDebug.error("InitChannalView",GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET));
    		//GenieDebug.error("InitChannalView","adapter_temp[i] = "+adapter_temp[i]);
    		if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS)
        	{
    			if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET).equals(m_Channel[i]))
        		{
        			m_channal_id = i;
        			Spinner_channel.setSelection(m_channal_id, true);
        			break;
        		}
        	}else
        	{
        		if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET).equals(m_accesstime[i]))
        		{
        			m_channal_id = i;
        			Spinner_channel.setSelection(m_channal_id, true);
        			break;
        		}
        	}
    		
    	}
    	
	}
	
	public void InitWpaView()
	{
////////////////////////////////////////
		GenieDebug.error("InitWpaView","--strart--!!");
    	Spinner_01 = (Spinner)findViewById(R.id.Spinner_01);
    	//锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷ArrayAdapter锟斤拷锟斤拷
    	adapter_01 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_string_01);
        
        // 锟斤拷锟斤拷锟斤拷锟斤拷锟叫憋拷锟斤拷
    	adapter_01.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //锟斤拷Adapter锟斤拷拥锟絘dapter_01
    	Spinner_01.setAdapter(adapter_01);
    	
    	Spinner_01.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				m_Wpa_id = arg2;
				if(m_Wpa_id > 0)
				{
					//tpassword.setVisibility(View.VISIBLE);
					//passwordET.setEnabled(true);
					//passwordET.setVisibility(View.VISIBLE);
					SetPassWordView_Visible();
				}else if(m_Wpa_id == 0)
				{
					//passwordET.setEnabled(false);
					//passwordET.setVisibility(View.GONE);
					SetPassWordView_Gone();
				}
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	GenieDebug.error("InitWpaView",GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY));
    	
    	if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("None"))
		{
    		//tpassword.setVisibility(View.INVISIBLE);
    		//passwordET.setEnabled(false);
    		//passwordET.setVisibility(View.GONE);
    		SetPassWordView_Gone();
    		m_Wpa_id = 0;
    		Spinner_01.setSelection(m_Wpa_id, true);
		}else if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("WPA2-PSK"))
		{
    		m_Wpa_id = 1;
    		Spinner_01.setSelection(m_Wpa_id, true);
		}else if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("WPA-PSK/WPA2-PSK"))
		{
    		m_Wpa_id = 2;
    		Spinner_01.setSelection(m_Wpa_id, true);
		}else
		{
			
		}
    	
	}
	*/
	
	public Boolean setGuestAccess()
	{
		Boolean Status = false;
		
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID_SET, usernameET.getText().toString());
		
		GenieDebug.error("setGuestAccess", "setGuestAccess m_Wpa_id = "+m_Wpa_id);

		if(m_Wpa_id >= 0 && m_Wpa_id <= 2)
		{
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET, GuesstModes[m_Wpa_id]);
		}else
		{
			return false;
		}
		
//		if(m_Wpa_id == 1)
//    	{
//    		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET, "WPA2-PSK");
//    	}else if(m_Wpa_id == 2)
//    	{
//    		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET, "WPA-PSK/WPA2-PSK");
//    	}else if(m_Wpa_id == 0)
//    	{
//    		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET, "None");
//    	}
		
		
		if(m_Wpa_id == 0)
		{
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY_SET, "0");
		}else
		{
			//GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET, Channel[m_channal_id]);
		
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY_SET, passwordET.getText().toString());
		}
		
		if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1") > -1)
		{
			SetGuestAccessNetwork();
		}else
		{		
			setGuestAccessEnabled2();
		}
    	
    	//return Status;
    	return true;
	}
	
	public void setGuestAccessEnabled2()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
	  	GenieRequestInfo request0 = new GenieRequestInfo();
	  	request0.aRequestLable="GenieWifiModify";
	  	request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigGuestEnable2;
	  	request0.aServer = "WLANConfiguration";
	  	request0.aMethod = "SetGuestAccessEnabled2";
	  	request0.aNeedwrap = true;
	  	request0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request0.aNeedParser=false;
	  	request0.aTimeout = 25000;
	  	request0.aElement = new ArrayList<String>();
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE);
	  	request0.aElement.add("1");
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID_SET));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET));
	  	request0.aElement.add("NewKey1");
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY_SET));
	  	request0.aElement.add("NewKey2");
	  	request0.aElement.add("0");
	  	request0.aElement.add("NewKey3");
	  	request0.aElement.add("0");
	  	request0.aElement.add("NewKey4");
	  	request0.aElement.add("0");
		
	  	requestinfo.add(request0);
	  	


	  	m_SettingRequest = new GenieRequest(this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
	}
	
	public void SetGuestAccessNetwork()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
	  	GenieRequestInfo request0 = new GenieRequestInfo();
	  	request0.aRequestLable="GenieWifiModify";
	  	request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigGuestAccessNetwork;
	  	request0.aServer = "WLANConfiguration";
	  	request0.aMethod = "SetGuestAccessNetwork";
	  	request0.aNeedwrap = true;
	  	request0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request0.aNeedParser=false;
	  	request0.aTimeout = 25000;
	  	request0.aElement = new ArrayList<String>();
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID_SET));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET));
	  	request0.aElement.add("NewKey1");
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY_SET));
	  	request0.aElement.add("NewKey2");
	  	request0.aElement.add("0");
	  	request0.aElement.add("NewKey3");
	  	request0.aElement.add("0");
	  	request0.aElement.add("NewKey4");
	  	request0.aElement.add("0");
		
	  	requestinfo.add(request0);
	  	


	  	m_SettingRequest = new GenieRequest(this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
	}
	

	
	public Boolean setWLANNoSecurity()
	{
		Boolean Status = false;
		
		
		GenieDebug.error("setWLANNoSecurity","setWLANNoSecurity usernameET = "+usernameET.getText().toString());
		GenieDebug.error("setWLANNoSecurity","setWLANNoSecurity Channel[m_channal_id] = "+Channel[m_channal_id]);
		
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID_SET, usernameET.getText().toString());
    	GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET, Channel[m_channal_id]);
    	
    	
    	ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
	  	GenieRequestInfo request0 = new GenieRequestInfo();
	  	request0.aRequestLable="GenieWifiModify";
	  	request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigNoSecurity;
	  	request0.aServer = "WLANConfiguration";
	  	request0.aMethod = "SetWLANNoSecurity";
	  	request0.aNeedwrap = true;
	  	request0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request0.aNeedParser=false;
	  	request0.aTimeout = 25000;
	  	request0.aElement = new ArrayList<String>();
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID_SET));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET));
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE);
	  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE));

	  	requestinfo.add(request0);
	  	


	  	m_SettingRequest = new GenieRequest(this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
    	
    	return Status;
	}
	
	public Boolean setWLANWPAPSKByPassphrase()
	{
		Boolean Status = false;
		
		GenieDebug.error("setWLANWPAPSKByPassphrase",passwordET.getText().toString());
		GenieDebug.error("setWLANWPAPSKByPassphrase","m_Wpa_id = "+m_Wpa_id);
		
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID_SET, usernameET.getText().toString());
    	GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET, Channel[m_channal_id]);
    	GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY_SET, passwordET.getText().toString());
    	
    	
		if(m_Wpa_id >= 0 && m_Wpa_id <= 2)
		{
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY_SET, cModes[m_Wpa_id]);
		}else
		{
			return false;
		}
    	

    	GenieDebug.error("setWLANWPAPSKByPassphrase","DICTIONARY_KEY_WLAN_WPA_KEY_SET = "+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY_SET));
    	

    	
    	if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY_SET).equals("None"))
		{
			setWLANNoSecurity();
		}
		else 
		{
	    	ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	  		
		  	GenieRequestInfo request0 = new GenieRequestInfo();
		  	request0.aRequestLable="GenieWifiModify";
		  	request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigWLan;
		  	request0.aServer = "WLANConfiguration";
		  	request0.aMethod = "SetWLANWPAPSKByPassphrase";
		  	request0.aNeedwrap = true;
		  	request0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		  	request0.aNeedParser=false;
		  	request0.aTimeout = 25000;
		  	request0.aElement = new ArrayList<String>();
		  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
		  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID_SET));
		  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION);
			//element.add("US");
		  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION));
		  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL);
			//element.add("6");
		  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET));
		  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE);			
			//element.add("g &amp; b");
		  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE));
		  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY);
		  	request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY_SET));
			request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY);
			request0.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY_SET));
		  	
		  	requestinfo.add(request0);
		  	


		  	m_SettingRequest = new GenieRequest(this,requestinfo);
		  	m_SettingRequest.SetProgressInfo(true, true);
		  	m_SettingRequest.Start();
		}
    	//soapCommand.configurationRouterfinished();
    	
    	return Status;
	}
	
	private void showConfigDialog()
    {
		
		GenieDebug.error("wifimodify","m_channal_id = "+m_channal_id);
		GenieDebug.error("wifimodify",usernameET.getText().toString());
    	GenieDebug.error("wifimodify",Channel[m_channal_id]);
    	GenieDebug.error("wifimodify",""+passwordET.getText().length());
    	
		AlertDialog.Builder dialog = new AlertDialog.Builder(this)
													//.setIcon(R.drawable.icon)
													//.setTitle(" ")
													.setPositiveButton(R.string.ok, this)
													.setMessage(R.string.wirelsssettin)
													.setNegativeButton(R.string.cancel, this);
    		
    	dialog.show();
    }
	

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch(which)
    	{
    	case GenieGlobalDefines.LOGIN_ROUTER:
    	{
    		switch(activeIndex)
    	    {
    	        case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
    	        	WirelessSettingSender();
    	        	break;
    				
    			case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
    				GuestSettingsender();
    				break;
    		}
    		
    	}
    	break;
    		
    	default:
    		break;
    	}
	}
	
//	 public void getModelInfo()
//	    {
//	    	GenieDebug.error("debug", "getModelInfo --0--");
//	    	
//	    	ShowProgressDialog();
//	    	
//	    	GenieDebug.error("debug", "getModelInfo --1--");
//	    	
//	    	new Thread()
//	    	{
//	    		public void run() 
//	    		{   
//	        		soapCommand.getWLanInfo();
//	        		sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
//	    		}
//	    	}.start();  
//	    }
	
	public void ShowProgressDialog()
	{
		progressDialog = ProgressDialog.show(this, "Loading...", "Please wait...", true, true);
	}

	public void CloseProgressDialog()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	
	public void  GuestSaveTimeAndMac()
	{
		 String temp = null;
		 String guestmac = null;
		 long currenttime = 0;
		 long disabletime = 0;
		 
		 
		 guestmac = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
		 GenieDebug.error("GuestSaveTimeAndMac","guestmac = "+guestmac);
		 GenieShareAPI.strncpy(userInfo.TimeMac, guestmac.toCharArray(),0,guestmac.length());
		 
		 currenttime = System.currentTimeMillis();
		 
		 GenieDebug.error("GuestSaveTimeAndMac","m_channal_id = "+m_channal_id);
		 
		 
		 HashMap<String,String> accesstime =  new HashMap<String,String>();
		 
		 if(0 < m_channal_id && m_channal_id < 6)
		 {
			 
			 
			 
			 
			 accesstime.put("GUESTABLE","1");
			 accesstime.put("GUESTTIME",m_accesstime[m_channal_id]);
			 accesstime.put("GUESTTIMEMAC",guestmac);
			 
			 
			 GenieDebug.error("GuestSaveTimeAndMac","GUESTTIMEMAC disabletime = "+guestmac);
			 
			 

			 disabletime = (((60*60*cAddHours[m_channal_id])+(cAddDays[m_channal_id]*60*60*24))*1000)+currenttime;
			 //disabletime = currenttime+(2*60*1000);
			 
			 temp = String.valueOf(currenttime);
			 GenieDebug.error("GuestSaveTimeAndMac","currenttime = "+temp);
			 GenieShareAPI.strncpy(userInfo.start_time, temp.toCharArray(),0,temp.length());
			 
			 accesstime.put("GUESTSTARTTIME",temp);
			 
			 temp = String.valueOf(disabletime);
			 GenieDebug.error("GuestSaveTimeAndMac","disabletime = "+temp);
			 GenieShareAPI.strncpy(userInfo.disabletime, temp.toCharArray(),0,temp.length());
			 
			
			 accesstime.put("GUESTENDTIME",temp);
			 
			 int second = (int)((disabletime - currenttime)/1000);
			 //int second = 300;
				
			//	GenieDebug.error("DisableGuestTime","DisableGuestTime  second = "+second);
			 
			 GenieDebug.error("GuestSaveTimeAndMac","second = "+second);
				
				 Calendar calendar=Calendar.getInstance();
				 calendar.setTimeInMillis(System.currentTimeMillis());
				 calendar.add(Calendar.SECOND, second);
				 
				if((GenieMainView.alarmManager != null) && (GenieMainView.pendingActivityIntent != null))
				{
					GenieMainView.alarmManager.cancel(GenieMainView.pendingActivityIntent);
					GenieMainView.alarmManager = null;
					GenieMainView.pendingActivityIntent = null;
				}	
				
				Intent intent = new Intent(this, GenieAlarmActivity.class);  
				GenieMainView.pendingActivityIntent = PendingIntent.getActivity(this, 0,intent, 0);
				GenieMainView.alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);  
				//alarmManager.setRepeating(AlarmManager.RTC,0,5000, pendingActivityIntent);
				GenieMainView.alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), GenieMainView.pendingActivityIntent);

		 }else
		 {			 
			 accesstime.put("GUESTABLE","0");
			 accesstime.put("GUESTTIME",GenieGlobalDefines.NULL);
			 
			 accesstime.put("GUESTSTARTTIME",GenieGlobalDefines.NULL);
			 	
			 accesstime.put("GUESTENDTIME",GenieGlobalDefines.NULL);
			 
			 accesstime.put("GUESTTIMEMAC",GenieGlobalDefines.NULL);
			 
			 temp = GenieGlobalDefines.NULL;
			 GenieDebug.error("GuestSaveTimeAndMac","currenttime = "+temp);
			 GenieShareAPI.strncpy(userInfo.start_time, temp.toCharArray(),0,temp.length());
			 
			 temp = GenieGlobalDefines.NULL;
			 GenieDebug.error("GuestSaveTimeAndMac","disabletime = "+temp);
			 GenieShareAPI.strncpy(userInfo.disabletime, temp.toCharArray(),0,temp.length());

		 }
		 
		 GenieSerializ.WriteMap(this, accesstime, "guestaccess");

		 GenieShareAPI.WriteData2File(userInfo,this, GenieGlobalDefines.USER_INFO_FILENAME);
	
	}
	private void GuestSettingsender() {
		// TODO Auto-generated method stub

		Show90SecondDialog();
		setGuestAccess();
		
//    	new Thread()
//    	{
//    		public void run() 
//    		{  
//    			if(setGuestAccess())
//    			{
//    				GuestSaveTimeAndMac();
//    				sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
//    			}else
//    			{
//    				sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//    			}
//    		}
//    	}.start();
	}
	
	public void Show90SecondDialog()
	{
		ShowSetProgressDialog();
		
		seconds = 90;
		ConnectWififlag = false;
		
		settimer  = new Timer(); 
		task = new TimerTask()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
						
					GenieDebug.error("run","seconds = "+seconds);
					if(seconds > 0)
					{
						seconds--;
						SetProgressDialogMassage();
//						if(seconds == 10)
//						{
//							soapCommand.configurationfinished();
//						}
	//					if(seconds == 10)
	//					{
	//						
	//				    	WifiManagerment test = new WifiManagerment(GenieWifiModify.this);
	//				    	ConnectWififlag = test.ConnectWifi(usernameET.getText().toString(), passwordET.getText().toString());
	//				    	if(!ConnectWififlag)
	//				    	{
	//				    		ConnectWififlag = test.ConnectWifi(usernameET.getText().toString(), passwordET.getText().toString());
	//				    	}				    	
	//					}
						
					}else
					{
	//					if(ConnectWififlag)
	//					{
	//						soapCommand.configurationfinished();
	//						CloseSetProgressDialog();
	//						settimer.cancel();
	//					}else
	//					{
							CloseSetProgressDialog();   //test
							settimer.cancel();
							
							runOnUiThread(new Runnable() {
								public void run() {
								
									settimer = null;
									wirelesssetting_result();
								}
							});
	//					}
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			
		};
		settimer.schedule(task,1000,1000);
	}
	
	
	public void ShowSetProgressDialog()
	{
		CloseSetProgressDialog();
		progressSetDialog = ProgressDialog.show(this, null, getResources().getString(R.string.pleasewait)+"... 90", true, false);
		//progressSetDialog.setm
	}
	
	public void SetProgressDialogMassage()
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(progressSetDialog != null)
				{
					progressSetDialog.setMessage(getResources().getString(R.string.pleasewait)+"... "+seconds);
					progressSetDialog.setProgress(seconds);
				}
			}
		});
		
	}

	public void CloseSetProgressDialog(int code)
	{
		if(code == 0)
		{
			if(progressSetDialog != null)
			{
				progressSetDialog.dismiss();
				progressSetDialog = null;
			}
			AlertDialog.Builder dialog = new AlertDialog.Builder(this)
//			.setIcon(R.drawable.icon)
//			.setTitle(" ")
			.setPositiveButton(R.string.ok,
			new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					setResult(GenieGlobalDefines.WIRELESSSETTING_RESULT);
					finish();	
				}
			})
			.setMessage(R.string.wirelsssettinrelogin);

			dialog.show();
		}
	}
	
	public void CloseSetProgressDialog()
	{
		if(progressSetDialog != null)
		{
			progressSetDialog.dismiss();
			progressSetDialog = null;
		}
//		runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				if(progressSetDialog != null)
//				{
//					progressSetDialog.dismiss();
//					progressSetDialog = null;
//				}
//			}
//		});
	}
	
	
	
	private void WirelessSettingSender() {
		
		Show90SecondDialog();
		
		
		GenieDebug.error("debug","WirelessSettingSender m_Wpa_id = "+m_Wpa_id);
		GenieDebug.error("debug","WirelessSettingSender passwordET = "+passwordET.getText().toString());
		GenieDebug.error("debug","WirelessSettingSender passwordET.getText().length() = "+passwordET.getText().length());
		// TODO Auto-generated method stub
		if(m_Wpa_id == 0 || (passwordET.getText().length() == 0))
    	{
			setWLANNoSecurity();
//    		if(setWLANNoSecurity())
//    		{
//    			sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
//    		}else
//    		{
//    			sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//    		}
    	}else
    	{
    		setWLANWPAPSKByPassphrase();
//    		if(setWLANWPAPSKByPassphrase())
//    		{
//    			sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
//    		}else
//    		{
//    			sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//    		}
    	}
		
//    	new Thread()
//    	{
//    		public void run() 
//    		{   
//    			GenieDebug.error("debug","WirelessSettingSender m_Wpa_id = "+m_Wpa_id);
//    			GenieDebug.error("debug","WirelessSettingSender passwordET = "+passwordET.getText().toString());
//    			GenieDebug.error("debug","WirelessSettingSender passwordET.getText().length() = "+passwordET.getText().length());
//				// TODO Auto-generated method stub
//				if(m_Wpa_id == 0 || (passwordET.getText().length() == 0))
//		    	{
//		    		if(setWLANNoSecurity())
//		    		{
//		    			sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
//		    		}else
//		    		{
//		    			sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//		    		}
//		    	}else
//		    	{
//		    		if(setWLANWPAPSKByPassphrase())
//		    		{
//		    			sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
//		    		}else
//		    		{
//		    			sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//		    		}
//		    	}
//    		}
//    	}.start();
	}

	
	private void wirelesssetting_result()
	{
		
		if(GenieRequest.m_SmartNetWork)
		{
			setResult(GenieGlobalDefines.WIRELESSSETTING_REFRESH);			
			this.finish();
			return ;
		}
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this)
//		.setIcon(R.drawable.icon)
//		.setTitle(" ")
		.setPositiveButton(R.string.ok,
		new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				setResult(GenieGlobalDefines.WIRELESSSETTING_RESULT);
				finish();	
			}
		})
		.setMessage(R.string.wirelsssettinrelogin);

		dialog.show();
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
		
		if(settimer != null)
		{
			settimer.cancel();
			settimer = null;
		}
		StopRequest();
		UnRegisterBroadcastReceiver();
	}
	
	
    private GenieRequest m_SettingRequest = null;
    
    
    
    private void StopRequest()
    {
  	  if(null != m_SettingRequest)
  	  {
  		m_SettingRequest.Stop();
  		m_SettingRequest = null;
  	  }  	
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
		     		
		     		if(lable != null && lable.equals("GenieWifiModify"))
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
			     				if(aResultType == RequestResultType.Succes)
			     				{
				     				switch(aSoapType)
				     				{
				     				case GenieGlobalDefines.ESoapRequestConfigGuestEnable2:
				     				case GenieGlobalDefines.ESoapRequestConfigGuestAccessNetwork:	
				     					GuestSaveTimeAndMac();
				     	        		break;			     	        	
				     				default:
				     					break;
				     				}
			     				}else
			     				{
			     					switch(aSoapType)
				     				{
				     				case GenieGlobalDefines.ESoapRequestConfigGuestEnable2:
				     				case GenieGlobalDefines.ESoapRequestConfigGuestAccessNetwork:
				     					GuestSaveTimeAndMac();
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
	
	

//	private void WirelessSettingSender() {
//		
//		ShowProgressDialog();
//		
//    	new Thread()
//    	{
//    		public void run() 
//    		{   
//				// TODO Auto-generated method stub
//				if(m_Wpa_id == 0|| (passwordET.getText().length() == 0))
//		    	{
//		    		if(setWLANNoSecurity())
//		    		{
//		    			sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
//		    		}else
//		    		{
//		    			sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//		    		}
//		    	}else
//		    	{
//		    		if(setWLANWPAPSKByPassphrase())
//		    		{
//		    			sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
//		    		}else
//		    		{
//		    			sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//		    		}
//		    	}
//    		}
//    	}.start();
//	}

	
}
