
package com.dragonflow;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.genie.ui.R;

/**
 * @author Administrator
 *
 */
public class GenieLPCmanage extends Activity implements Button.OnClickListener,DialogInterface.OnClickListener,RadioGroup.OnCheckedChangeListener
{

	public static String openDNSUserName = null;
	
	public ProgressDialog progressDialog = null;
	private ProgressDialog Refresh_progressDialog = null;
	private  ProgressDialog m_checkprogressDialog = null;
	//public GenieSoap  Command = null;
	public RadioGroup mRadioGroup = null;
	
	public GenieUserInfo userInfo = null;
	
	private View view = null;
	String mTimpName = null;
	private int activeindex = 0;
	private Boolean changcustom = false;
	
	private MyDialog dialog_view = null;
	
	
	private Thread  m_soapThread = null;
	
	public Boolean signln_input_username = false;
	public Boolean signln_input_password = false; 
	
	public Boolean CreateAccount_input_username = false;
	public Boolean CreateAccount_input_password = false; 
	public Boolean CreateAccount_input_password2 = false; 
	public Boolean CreateAccount_input_email = false;
	public Boolean CreateAccount_input_email2 = false;

	
	
	
	final public static int LpcSettingNone = 0;
	final public static int LpcSettingMinimal = 1;
	final public static int LpcSettingLow = 2;
	final public static int LpcSettingModerate = 3;
	final public static int LpcSettingHigh = 4;
	final public static int LpcPreSignInExist = 5;
	final public static int LpcPreSignInCreate = 6;
	final public static int settingtext[] = { R.string.lpcset_none,
										  R.string.lpcset_minimal,
										  R.string.lpcset_low,
										  R.string.lpcset_moderate,
										  R.string.lpcset_high};
	final public static String setting[] = { "None",
		  "Minimal",
		  "Low",
		  "Moderate",
		  "High"};
	
	private int radioindex = 7;
	
	private int CreateAccount_again = 0;
	private int Signin_again = 0;
	private int ToastMessageid = 0;
	
	public int LpcFunctionType = 0;
	
	//public ToggleButton  lpc_manage_controlBT = null;
	public CheckBox  lpc_manage_controlBT = null;
	public RelativeLayout  lpc_manage_filterchangBT = null;
	public RelativeLayout  lpc_manage_coutomchangBT = null;
	public RelativeLayout  lpc_bypassaccount = null;
	private TextView lpc_bypassaccount_name = null;
	
	private int activityresult = 0;
	
	private boolean m_NetworkStatus = true;

	private  boolean m_opendnshoststatus = false;
	private String dNSMasqDeviceID = null;
	
	//判断是否是修改过滤级别
	private boolean isChangeFiteringLevel=false;
	//类实例
	private static GenieLPCmanage currentInstance;
	
	private Handler handler = new Handler()
	{   
        @Override   
        public void handleMessage(Message msg) 
        {   
        	GenieDebug.error("debug", "main handleMessage!! msg.what = "+msg.what);
        	
        	closeRefreshDialog();
        	
        	if( GenieGlobalDefines.EFunctionParental_NetError == msg.what)
    		{
        		//activeindex = GenieGlobalDefines.EFunctionParental_NetError;
        		
        		activeindex = GenieGlobalDefines.EFunctionParental_manage;
        		

        		InitLpcManageView();
        		InitManageViewButton(false);
	
        		setLpcFunctionType(activeindex);
        		
        		closeWaitingDialogOnThread();
        		
        		
        		if(null != dialog_view)
            	{
        			
            		dialog_view.closeWaitingDialog();
            	}
        		
        		setToastMessageid(GenieGlobalDefines.EFunctionParental_NetError);
        		
            	showToast();
        		return ;
    		}

        	
        	if( GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable == msg.what)
        	{
        		
        		//if(null != dialog_view)
            	//{
            		dialog_view.closeWaitingDialog();
            	//}
        		showToast();
        		return ;
        	}
        	if(GenieGlobalDefines.EFunctionParental_manage == msg.what)
        	{		
        		
        		activeindex = GenieGlobalDefines.EFunctionParental_manage;
        		
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manage 0");
        		InitLpcManageView();
        		
        		InitManageViewButton(true);
        		
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manage 1");
        		
        		setLpcFunctionType(activeindex);
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manage 2");
        		closeWaitingDialogOnThread();
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manage 3");
        		
        		if(null != dialog_view)
            	{
        			GenieDebug.error("handleMessage","handleMessage EFunctionParental_manage 4");
            		dialog_view.closeWaitingDialog();
            	}
        		
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manage 5");
        		
            	showToast();
        		return ;
        	}
        	
        	if(GenieGlobalDefines.EFunctionParental_manageerror == msg.what)
        	{		
        		
        		activeindex = GenieGlobalDefines.EFunctionParental_manage;
        		
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manageerror 0");
        		//InitLpcManageView();
        		
        		InitManageViewButton(false);
        		
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manageerror 1");
        		
        		setLpcFunctionType(activeindex);
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manageerror 2");
        		closeWaitingDialogOnThread();
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manageerror 3");
        		
        		if(null != dialog_view)
            	{
        			GenieDebug.error("handleMessage","handleMessage EFunctionParental_manageerror 4");
            		dialog_view.closeWaitingDialog();
            	}
        		
        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manageerror 5");
        		
        		GenieDebug.error("handleMessage","handleMessage m_NetworkStatus = "+m_NetworkStatus);
        		
        		
        		if(!m_NetworkStatus)
        		{
        			ShowMessageDialog(getResources().getString(R.string.lpcinterneterror));
        		}
            	//showToast();
        		return ;
        	}
        	
        	
        	
//        	closeWaitingDialogOnThread();
//        	if(null != dialog_view)
//        	{
//        		dialog_view.closeWaitingDialog();
//        	}
        	switch(msg.what)
        	{
        	case GenieGlobalDefines.EFunctionParental_Firmware:
        		activeindex = GenieGlobalDefines.EFunctionParental_Firmware;        		
        		break;
        	case GenieGlobalDefines.EFunctionParental_intro:
        		activeindex = GenieGlobalDefines.EFunctionParental_intro;
        		break;
        	case GenieGlobalDefines.EFunctionParental_presignln:
        		activeindex = GenieGlobalDefines.EFunctionParental_presignln;
        		break;
        	case GenieGlobalDefines.EFunctionParental_signln:
        		activeindex = GenieGlobalDefines.EFunctionParental_signln;
        		break;
        	case GenieGlobalDefines.EFunctionParental_Settings:
        		activeindex = GenieGlobalDefines.EFunctionParental_Settings;
        		break;
        	case GenieGlobalDefines.EFunctionParental_Done:
        		activeindex = GenieGlobalDefines.EFunctionParental_Done;
        		break;
        	case GenieGlobalDefines.EFunctionParental_CreateAccount:
        		activeindex = GenieGlobalDefines.EFunctionParental_CreateAccount;
        		break;
//        	case GenieGlobalDefines.EFunctionParental_NetError:
//        		activeindex = GenieGlobalDefines.EFunctionParental_NetError;
//        		break;
        	case GenieGlobalDefines.EFunctionParental_failure:
        		//showdialog_Ex();
        		//InitManageViewButton(false);
        		setToastMessageid(GenieGlobalDefines.EFunctionParental_failure); 
        		showToast();
        		return ;
        	case GenieGlobalDefines.EFunctionParental_Success:
        		if(activeindex == GenieGlobalDefines.EFunctionParental_signln)
        		{
        			activeindex = GenieGlobalDefines.EFunctionParental_Settings;
        		}else if(activeindex == GenieGlobalDefines.EFunctionParental_CreateAccount)
        		{
        			activeindex = GenieGlobalDefines.EFunctionParental_signln;
        		}

        		break ;	
        	}
        	
        	closeWaitingDialogOnThread();
        	if(null != dialog_view)
        	{
        		dialog_view.closeWaitingDialog();
        	}
        	
        	setLpcFunctionType(activeindex);
        	showdialog_Ex();
        	showToast();
        }
	};   
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
		requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.lpcmanage);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
		}else
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		}
		
		
		InitTitleView();
		
		currentInstance=this;
		
		changcustom = false;
		m_NetworkStatus = true;
		openDNSUserName = null;
		
		activityresult = GenieGlobalDefines.EFunctionResult_failure;
		
		//showWaitingDialog();
		
		lpc_bypassaccount_name = (TextView) findViewById(R.id.lpc_bypassaccount_name);
		InitManageViewButton(true);
		
		//初始化lpc控制项显示
		InitLpcManageView();

		//隐藏家长控制界面
		setLCPManageViewDisplay(false);
		
		activeindex = 0;
		
		//String internet_status = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS);
		String lpc_support = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED);
		//if(internet_status.equals("Up"))
		//{
		
		GenieDebug.error("debug","lpc_support = ["+lpc_support+"]");
		
			if(lpc_support != null && (lpc_support.equals("1") || GenieRequest.m_SmartNetWork))
			{
				GenieDebug.error("debug","lpc_support == 1");
				CheckHostStatus();
			}else
			{
				GenieDebug.error("debug","lpc_support != 1");
				closeWaitingDialog();
				InitManageViewButton(false);
				ShowMessageDialog(getResources().getString(R.string.notsupport));
				//sendMessage2UI(GenieGlobalDefines.EFunctionParental_NetError);
			}
		//}else
		//{
		//	sendMessage2UI(GenieGlobalDefines.EFunctionParental_NetError);
		//}
		
		userInfo = GenieShareAPI.ReadDataFromFile(this, GenieGlobalDefines.USER_INFO_FILENAME);
		
		
	}
	
	private void ShowMessageDialog(String message)
	{
		GenieDebug.error("debug","ShowMessageDialog message = "+message);
		AlertDialog.Builder dialog =  new AlertDialog.Builder(this);
		
		dialog.setMessage(message);
		dialog.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				GenieLPCmanage.this.onBackPressed();
			}
		});
		
		dialog.setCancelable(false);
		dialog.show();
	}
	
	
	protected void onStart() {
		// TODO Auto-generated method stub
    	
		super.onStart();
					
		RegisterBroadcastReceiver();
		
	
		
		setBypassAccpuntName(openDNSUserName);
	
	}
	
	public void setBypassAccpuntName(String name){
		if(lpc_bypassaccount_name ==null){
			return;
		}
		if(name!=null){
			lpc_bypassaccount_name.setText(name);
		}else{
			lpc_bypassaccount_name.setText("");
		}
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		GenieDebug.error("debug", "onDestroy --0--");
		
		CancleSoapThread();
		//Command = null;
		handler = null;
		
		StopRequest();
		UnRegisterBroadcastReceiver();
		currentInstance=null;
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
	public void InitTitleView()
	{
		Button  back = (Button)findViewById(R.id.back);
		Button  about = (Button)findViewById(R.id.about);
		
		about.setText(R.string.refresh);
		
		TextView title = (TextView)findViewById(R.id.netgeartitle);
		

        title.setText(R.string.parentalcontrl);
		
		back.setBackgroundResource(R.drawable.title_bt_bj);
		about.setBackgroundResource(R.drawable.title_bt_bj);
		
		back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GetActivity().onBackPressed();
			}
		});
		
		about.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//OnClickAbout();
				
				showrefreshWaitingDialog();
				CheckHostStatus();
				//InitLpcDate();
			}
		});
		
		back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //���Ϊ����ʱ�ı���ͼƬ      
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //��Ϊ̧��ʱ��ͼƬ      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		

		
		about.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //���Ϊ����ʱ�ı���ͼƬ      
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //��Ϊ̧��ʱ��ͼƬ      
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
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		Bundle extra = new Bundle();
		
		Intent i = new Intent();
		int resultcode = 0;
		
		
	
        if(activityresult == GenieGlobalDefines.EFunctionResult_Success)
      	{
        		
        	resultcode = GenieGlobalDefines.EFunctionResult_Success;
        }else
        {
        	resultcode = GenieGlobalDefines.EFunctionResult_failure;
        }

		
		i.putExtras(extra);
		setResult(resultcode, i);

		extra = null;

		i = null;

		finish();
//		super.onBackPressed();
	}
	
	public GenieLPCmanage getthis( )
	{
		return this;
	}
	
	

	
	private OnClickListener onFilterchangclick =  new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			OnClickFilterChangButton();
		}
	};
	
	private OnClickListener onCoutomchangclick =  new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			OnClickCustomChangButton();
		}
	};
	
	//bypassAccount点击事件
	private OnClickListener onBypassAccountclick =  new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			
//			if(openDNSUserName!=null){
//				Intent  intent = new Intent();
//				intent.setClass(GenieLPCmanage.this, GenieLPCBypass.class);
//				
//				startActivity(intent);
//			}else{
			//先取设备id在获取用户列表
				GetMyDNSMasqDeviceID();//1
//			}
			
		}
	};
	
	private OnTouchListener  itemTouchListener = new OnTouchListener(){      
        @Override     
        public boolean onTouch(View v, MotionEvent event) {      
        	
        	
        		//GenieDebug.error("onTouch ","onTouch event.getAction() = "+event.getAction());
                if(event.getAction() == MotionEvent.ACTION_DOWN){      
                        //���Ϊ����ʱ�ı���ͼƬ      

                	//LinearLayout temp = (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
                	//v.setBackgroundResource(R.drawable.bj_dz);
                	v.setBackgroundColor(getResources().getColor(R.color.Black));
                        //v.setBackgroundResource(R.drawable.deviceiphone);
                		//SetBackgroundResource_down(v);
                }else if(event.getAction() == MotionEvent.ACTION_UP){      
                        //��Ϊ̧��ʱ��ͼƬ      
                        //v.setBackgroundResource(R.drawable.deviceipad);
                		//SetBackgroundResource_up(v);
                	
                	//LinearLayout temp = (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
                	//v.setBackgroundResource(R.drawable.bj);
                	v.setBackgroundDrawable(null);
                }  
                return false;      
        }      
	};
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		boolean ret = super.dispatchTouchEvent(ev);
		
		if(ev.getAction() == MotionEvent.ACTION_UP)
		{
			if(null != lpc_manage_filterchangBT)
			{
				lpc_manage_filterchangBT.setBackgroundDrawable(null);
			}
			if(null != lpc_manage_coutomchangBT)
			{
				lpc_manage_coutomchangBT.setBackgroundDrawable(null);
			}
			if(null!=lpc_bypassaccount){
				lpc_bypassaccount.setBackgroundDrawable(null);
			}

		}
		
		return ret;
	}
	
	public void InitManageViewButton(Boolean status)
	{
		if(status)
		{
			//lpc_manage_controlBT = (ToggleButton)findViewById(R.id.lpc_controlBT);		
			lpc_manage_controlBT = (CheckBox)findViewById(R.id.lpc_controlBT);
			lpc_manage_controlBT.setOnClickListener(this);
			lpc_manage_controlBT.setEnabled(true);
			
			lpc_manage_filterchangBT  = (RelativeLayout)findViewById(R.id.lpc_levelchangBT);
			lpc_manage_filterchangBT.setEnabled(true);
			lpc_manage_filterchangBT.setOnClickListener(onFilterchangclick);
			lpc_manage_filterchangBT.setOnTouchListener(itemTouchListener);
			
			lpc_manage_coutomchangBT  = (RelativeLayout)findViewById(R.id.lpc_customchangBT);
			lpc_manage_coutomchangBT.setEnabled(true);
			lpc_manage_coutomchangBT.setOnClickListener(onCoutomchangclick);
			lpc_manage_coutomchangBT.setOnTouchListener(itemTouchListener);
			
			lpc_bypassaccount =(RelativeLayout) findViewById(R.id.lpc_bypassaccount);
			lpc_bypassaccount.setEnabled(true);
			lpc_bypassaccount.setOnClickListener(onBypassAccountclick);
			lpc_bypassaccount.setOnTouchListener(itemTouchListener);
			
			
			
		}else
		{
			//lpc_manage_controlBT = (ToggleButton)findViewById(R.id.lpc_controlBT);		
			lpc_manage_controlBT = (CheckBox)findViewById(R.id.lpc_controlBT);
			//禁用家长控制
			//lpc_manage_controlBT.setEnabled(false);
			
			lpc_manage_filterchangBT  = (RelativeLayout)findViewById(R.id.lpc_levelchangBT);
			lpc_manage_filterchangBT.setVisibility(View.GONE);
			//lpc_manage_filterchangBT.setEnabled(false);
			
			lpc_manage_coutomchangBT  = (RelativeLayout)findViewById(R.id.lpc_customchangBT);
			lpc_manage_coutomchangBT.setVisibility(View.GONE);
			//lpc_manage_coutomchangBT.setEnabled(false);
			
			lpc_bypassaccount =(RelativeLayout) findViewById(R.id.lpc_bypassaccount);
			lpc_bypassaccount.setVisibility(View.GONE);
			//lpc_bypassaccount.setEnabled(false);
		}
	}


	public void changeScreenOrientation(Configuration newConfig)
	{
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();

		 GenieDebug.error("changeScreenOrientation", "changeScreenOrientation !!!!!!!!!");
	}
	
	public void setLpcFunctionType(int type)
	{
		LpcFunctionType = type; 
	}
	public int getLpcFunctionType()
	{
		return LpcFunctionType;
	}
	
	private void CancleSoapThread()
	{
		GenieDebug.error("debug", "CancleSoapThread --0--");
		if(m_soapThread != null)
	    {
			GenieDebug.error("debug", "CancleSoapThread --1--");
	    	if(m_soapThread.isAlive())
	    	{
	    		GenieDebug.error("debug", "CancleSoapThread --2--");
	    		m_soapThread.interrupt();	    		
	    	}
	    	m_soapThread = null;
	    }
	}
	
//	 public void CheckHostStatus()
//	 {
//	  
//	    	
//	    GenieDebug.error("debug", "CheckHostStatus --1--");
//	    Command = new GenieSoap(this);
//	    GenieDebug.error("debug", "CheckHostStatus --2--");
//	    //Command.initialDictionaryElements();
//	    GenieDebug.error("debug", "CheckHostStatus --3--");
//	    Command.pLpcview = this;
//	    Command.parentview = GenieGlobalDefines.GenieView_LPCView;
//	    	
//	    CancleSoapThread();
//	    	
//	    m_soapThread =  new Thread()
//	    {
//	    	public void run() 
//	    	{   
//	    		try{
//		    		if(Command.CheckRouterStatus() && Command.GetCurrentSettings())
//		    		{
//		    			String PCUsername = new String(userInfo.PCUsername,0,userInfo.PCUsername.length);
//		    			String PCDeviceID = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
//		    			GenieDebug.error("CheckHostStatus","PCUsername = "+PCUsername);
//		    			GenieDebug.error("CheckHostStatus","PCDeviceID = "+PCDeviceID);
//		    			GenieDebug.error("CheckHostStatus","PCUsername.length = "+PCUsername.length());
//		    			GenieDebug.error("CheckHostStatus","PCDeviceID.length = "+PCDeviceID.length());
//		    			
//		    			
//		    			Command.WrappedGetDNSMasqDeviceID();
//		    			
//		    			String Deviceid = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
//		    			
//		    			GenieDebug.error("CheckHostStatus"," 77lpc Deviceid = "+Deviceid);
//		    			GenieDebug.error("CheckHostStatus"," 77lpc PCDeviceID = "+PCDeviceID);
//		    			GenieDebug.error("CheckHostStatus"," 77lpc PCUsername = "+PCUsername);
//		    			
//		    			if(PCUsername.equals(GenieSoap.KEY_NULL)||PCDeviceID.equals(GenieSoap.KEY_NULL) || (!PCDeviceID.equals(Deviceid)))
//		    			{
//		    			
//		    				sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
//		    			}else
//		    			{    				
//		    				String deviceid = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
//		    				String token  = new String(userInfo.PCLoginToken,0,userInfo.PCLoginToken.length);
//		    				String password  = new String(userInfo.PCPassword,0,userInfo.PCPassword.length);
//		    				
//		    				GenieDebug.error("CheckHostStatus","deviceid = "+deviceid);
//		    				GenieDebug.error("CheckHostStatus","token = "+token);
//		    				GenieDebug.error("CheckHostStatus","PCUsername = "+PCUsername);
//		    				GenieDebug.error("CheckHostStatus","password = "+password);
//		    				
//		    				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID,deviceid);
//		    				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN,token);
//		    				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME,PCUsername);
//		    				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD,password); 
//		    				
//		    				GenieDebug.error("CheckHostStatus","InitLpcDate = ");
//		    				InitLpcDate();
//		    			}
//		    		}else
//			    	{
//		    			m_NetworkStatus = false;
//		    			sendMessage2UI(GenieGlobalDefines.EFunctionParental_manageerror);
//			    	}
//	    		}catch (Exception e) {
//					// TODO: handle exception
//	    			e.printStackTrace();
//				}
//	    	}
//	    	
//	    };
//	    m_soapThread.start();
//	    	  
//	 }
	
	
	 public void CheckHostStatus()
	 {
	  
		 showCheckWaitingDialog();
	    	
	    GenieDebug.error("debug", "CheckHostStatus --1--");
	    
	    m_opendnshoststatus = false;
	    
	    ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    
    	GenieRequestInfo CheckHost = new GenieRequestInfo();
    	CheckHost.aRequestLable="GenieLPCmanage";
    	CheckHost.aHttpType = GenieGlobalDefines.EHttpCheckLpcHost;
    	CheckHost.aRequestType=GenieGlobalDefines.RequestActionType.Http;
    	CheckHost.aTimeout = 20000;
    	requestinfo.add(CheckHost);
    	
    	if(!GenieRequest.m_SmartNetWork)
    	{
	    	GenieRequestInfo currentsetting = new GenieRequestInfo();
	    	currentsetting.aRequestLable="GenieLPCmanage";
	    	currentsetting.aHttpType = GenieGlobalDefines.EHttpGetCurrentSetting;
	    	currentsetting.aRequestType=GenieGlobalDefines.RequestActionType.Http;
	    	currentsetting.aTimeout = 20000;
	    	requestinfo.add(currentsetting);
    	}
    	
    	StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	if(progressDialog!=null || Refresh_progressDialog !=null || m_checkprogressDialog!=null){
	  		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
	  	m_SettingRequest.Start();
	  	
	    
	    	
//	    m_soapThread =  new Thread()
//	    {
//	    	public void run() 
//	    	{   
//	    		try{
//		    		if(Command.CheckRouterStatus() && Command.GetCurrentSettings())
//		    		{
//		    			String PCUsername = new String(userInfo.PCUsername,0,userInfo.PCUsername.length);
//		    			String PCDeviceID = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
//		    			GenieDebug.error("CheckHostStatus","PCUsername = "+PCUsername);
//		    			GenieDebug.error("CheckHostStatus","PCDeviceID = "+PCDeviceID);
//		    			GenieDebug.error("CheckHostStatus","PCUsername.length = "+PCUsername.length());
//		    			GenieDebug.error("CheckHostStatus","PCDeviceID.length = "+PCDeviceID.length());
//		    			
//		    			
//		    			Command.WrappedGetDNSMasqDeviceID();
//		    			
//		    			String Deviceid = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
//		    			
//		    			GenieDebug.error("CheckHostStatus"," 77lpc Deviceid = "+Deviceid);
//		    			GenieDebug.error("CheckHostStatus"," 77lpc PCDeviceID = "+PCDeviceID);
//		    			GenieDebug.error("CheckHostStatus"," 77lpc PCUsername = "+PCUsername);
//		    			
//		    			if(PCUsername.equals(GenieSoap.KEY_NULL)||PCDeviceID.equals(GenieSoap.KEY_NULL) || (!PCDeviceID.equals(Deviceid)))
//		    			{
//		    			
//		    				sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
//		    			}else
//		    			{    				
//		    				String deviceid = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
//		    				String token  = new String(userInfo.PCLoginToken,0,userInfo.PCLoginToken[39]);
//		    				String password  = new String(userInfo.PCPassword,0,userInfo.PCPassword[39]);
//		    				
//		    				GenieDebug.error("CheckHostStatus","deviceid = "+deviceid);
//		    				GenieDebug.error("CheckHostStatus","token = "+token);
//		    				GenieDebug.error("CheckHostStatus","PCUsername = "+PCUsername);
//		    				GenieDebug.error("CheckHostStatus","password = "+password);
//		    				
//		    				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID,deviceid);
//		    				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN,token);
//		    				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME,PCUsername);
//		    				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD,password); 
//		    				
//		    				GenieDebug.error("CheckHostStatus","InitLpcDate = ");
//		    				InitLpcDate();
//		    			}
//		    		}else
//			    	{
//		    			m_NetworkStatus = false;
//		    			sendMessage2UI(GenieGlobalDefines.EFunctionParental_manageerror);
//			    	}
//	    		}catch (Exception e) {
//					// TODO: handle exception
//	    			e.printStackTrace();
//				}
//	    	}
//	    	
//	    };
//	    m_soapThread.start();
	    	  
	 }
	 
	 public void InitLpcManageView()
	 {
		 String pcstatus = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS);
		 String bundle = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE);
		 String opdnsname = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME);
		 String bypassAccount=GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_ChildDeviceIDUserName);
			 
		 
		 GenieDebug.error("InitLpcManageView", "pcstatus = "+pcstatus);
		 GenieDebug.error("InitLpcManageView", "bundle = "+bundle);
		 GenieDebug.error("InitLpcManageView", "opdnsname = "+opdnsname);
		 
		 TextView  textview = (TextView)this.findViewById(R.id.lpc_manage_control);
		// Button    button = (Button)this.findViewById(R.id.lpc_controlBT);
		 
		 RelativeLayout item1 = (RelativeLayout)this.findViewById(R.id.LPC_item01);
		 RelativeLayout item2 = (RelativeLayout)this.findViewById(R.id.LPC_item02);
		 RelativeLayout item3 = (RelativeLayout)this.findViewById(R.id.LPC_item3);
		 LinearLayout item4 = (LinearLayout)this.findViewById(R.id.LPC_item4);
		 
		 if("1".equals(pcstatus))
		 {
			 
			 textview.setText(getResources().getString(R.string.Lpc_control));
			 
			 item1.setVisibility(View.VISIBLE);
			 item2.setVisibility(View.VISIBLE);
			 item3.setVisibility(View.VISIBLE);
			 item4.setVisibility(View.VISIBLE);
			 //lpc_manage_controlBT.setText(getResources().getString(R.string.lpc_disable)+" "+getResources().getString(R.string.Lpc_control));
			 lpc_manage_controlBT.setChecked(true);
		 }else
		 {
			 item1.setVisibility(View.GONE);
			 item2.setVisibility(View.GONE);
			 item3.setVisibility(View.GONE);
			 item4.setVisibility(View.GONE);
			 textview.setText(getResources().getString(R.string.Lpc_control));
			 //lpc_manage_controlBT.setText(getResources().getString(R.string.lpc_enable)+" "+getResources().getString(R.string.Lpc_control));
			 lpc_manage_controlBT.setChecked(false);
		 }
		 
		 activityresult = GenieGlobalDefines.EFunctionResult_Success;
		 
		 textview = null;
		 textview = (TextView)this.findViewById(R.id.lpc_filterTV);
		 //textview.setText(getResources().getString(R.string.Filteringlevel)+" "+bundle);
		 
		 for(int i=0;i<setting.length;i++)
		 {
			 if(bundle!= null && bundle.toLowerCase().equals(setting[i].toLowerCase()))
			 {
				 textview.setText(getResources().getString(settingtext[i]));
				 break;
			 }
		 }
		 
		 
//		 if(bundle.equals(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CUSTOM))
//		 {
//			 textview = null;
//			 textview = (TextView)this.findViewById(R.id.lpc_customset);
//			 //textview.setText(getResources().getString(R.string.OpenDNSaccount)+" "+opdnsname);
//			 String customset = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CUSTOM);
//			 textview.setText(customset);
//		 }
		 textview = null;
		 textview = (TextView)this.findViewById(R.id.lpc_account);
		 //textview.setText(getResources().getString(R.string.OpenDNSaccount)+" "+opdnsname);
		 textview.setText(opdnsname);
		 
		 //设置bypass account
		 setBypassAccpuntName(bypassAccount);
		 openDNSUserName=bypassAccount;

		 
	 }
	 
	 	public void showRefreshDialog()
	    {
			closeWaitingDialog();
			
			String title = getResources().getString(R.string.loadingparentalcontrl)+" "+getResources().getString(R.string.info);
	    	
			Refresh_progressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.wait), true, true); 
	    	
			//Refresh_progressDialog =  ProgressDialog.show(this, "Loading...", "Please wait...", true, true);
//			progressDialog =  new ProgressDialog(this.getContext());
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			progressDialog.show(this.getContext(), "Loading...", "Please wait...", true, true);
	    }
	    public void closeRefreshDialog()
	    {
	    	if(Refresh_progressDialog != null)
			{
	    		Refresh_progressDialog.dismiss();
	    		Refresh_progressDialog = null;
			}
	    }
	 
	 @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			super.onOptionsItemSelected(item);
			switch (item.getItemId()) 
			{  
				case R.id.item02:
					showRefreshDialog();
					InitLpcDate();
					return true;  				
			}
			return false;
		}
	 
	    @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			super.onCreateOptionsMenu(menu);
			

			
//			MenuInflater inflater = getMenuInflater();  
//			inflater.inflate(R.menu.menu, menu);  
//			menu.findItem(R.id.item01).setTitle("modify");
//			menu.findItem(R.id.item02).setTitle("Refresh");
//			
//			
//
//			menu.findItem(R.id.item01).setVisible(false);
		
			return true;  
		}
	    
	    
	    private String m_loginpassword = null;
		
		public String GetLoginPassword()
		{
			GenieDebug.error("debug","GetLoginPassword ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SETTING_INFO, 0); 
			if(settings != null)
			{
			     String password = settings.getString(GenieGlobalDefines.PASSWORD, "password");

			     GenieDebug.error("debug","SaveLoginPassword settings != null password = "+password);
			     return password;
			}else
			{
				GenieDebug.error("debug","SaveLoginPassword settings == null return password  ");
				return "password";
			}
		}
		
		public void SaveLoginPassword(String password)
		{
			GenieDebug.error("debug","SaveLoginPassword password = "+password);
			 SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SETTING_INFO, 0);
		     if(null != settings)
		    	 settings.edit().putString(GenieGlobalDefines.PASSWORD, password).commit();

		}   
		
		
		public void AutoRouterLogin(String username ,String password)
		{
			GenieDebug.error("debug","AutoRouterLogin username = "+username);
			GenieDebug.error("debug","AutoRouterLogin password = "+password);
			
			ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    	
	    	GenieRequestInfo test2 = new GenieRequestInfo();
	    	test2.aRequestLable="GenieLPCmanage";
	    	test2.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
	    	test2.aServer = "ParentalControl";
	    	test2.aMethod = "Authenticate";
	    	test2.aNeedwrap = false;
	    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	    	test2.aNeedParser=false;
	    	test2.aTimeout = 20000;
	    	test2.aElement = new ArrayList<String>();	
	    	if(!GenieRequest.m_SmartNetWork)
	    	{
	    		test2.aElement.add("NewUsername");
		    	test2.aElement.add(username);
		    	test2.aElement.add("NewPassword");
		    	test2.aElement.add(password);
	    	}else
	    	{
	    		if(null != GenieRequest.m_SmartInfo)
	    		{
	    		    String serial = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).serial;
	    		    if(serial != null)
	    		    {
	    		    	test2.aElement.add("NewUsername");
	    		    	test2.aElement.add(GetSmartNetworkRouterUsername(serial));
	    		    	test2.aElement.add("NewPassword");
	    		    	test2.aElement.add(GetSmartNetworkRouterPassword(serial));
	    		    }
	    		}
	    	}
	    	
	    	requestinfo.add(test2);

	    	StopRequest();
	    	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	    	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
		  		m_SettingRequest.SetProgressInfo(false, false);
		  	}else{
		  		m_SettingRequest.SetProgressInfo(true, false);
		  	}
	    	m_SettingRequest.Start();
		}
		
		/**
		 * 获取家长控制状态请求
		 */
		private void GetParentalControlStatus()
		{
			
			
			ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    	
			 m_loginpassword = GetLoginPassword();
			 
			 
			 GenieRequestInfo test = new GenieRequestInfo();
			 test.aRequestLable="GenieLPCmanage";
			 test.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
			 test.aServer = "ParentalControl";
			 test.aMethod = "Authenticate";
			 test.aNeedwrap = false;
			 test.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
			 test.aNeedParser=false;
			 test.aTimeout = 20000;
			 test.aElement = new ArrayList<String>();	

			 
			 if(!GenieRequest.m_SmartNetWork)
	    	{
				 test.aElement.add("NewUsername");
				 test.aElement.add("admin");
				 test.aElement.add("NewPassword");
				 test.aElement.add(m_loginpassword);
	    	}else
	    	{
	    		if(null != GenieRequest.m_SmartInfo)
	    		{
	    		    String serial = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).serial;
	    		    if(serial != null)
	    		    {
	    		    	test.aElement.add("NewUsername");
	    		    	test.aElement.add(GetSmartNetworkRouterUsername(serial));
	    		    	test.aElement.add("NewPassword");
	    		    	test.aElement.add(GetSmartNetworkRouterPassword(serial));
	    		    }
	    		}
	    	}
	    	
	    	requestinfo.add(test);

			 
	    	GenieRequestInfo test2 = new GenieRequestInfo();
	    	test2.aRequestLable="GenieLPCmanage";
	    	test2.aSoapType = GenieGlobalDefines.ESoapRequestPCStatus;
	    	test2.aServer = "ParentalControl";
	    	test2.aMethod = "GetEnableStatus";
	    	test2.aNeedwrap = true;
	    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	    	test2.aNeedParser=true;
	    	test2.aTimeout = 20000;
	    	test2.aElement = new ArrayList<String>();	    	
//	    	test2.aElement.add("NewPassword");
//	    	test2.aElement.add(password);
//	    	test2.aElement.add("NewUsername");
//	    	test2.aElement.add(username);
	    	
	    	requestinfo.add(test2);

	    	StopRequest();
	    	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	    	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
	    		m_SettingRequest.SetProgressInfo(false, false);
		  	}else{
		  		m_SettingRequest.SetProgressInfo(true, false);
		  	}
	    	m_SettingRequest.Start();
		}
		
		/**
		 * 初始化家长控制数据
		 */
		 public void InitLpcDate()
		 {
			 GenieDebug.error("InitLpcDate","InitLpcDate ");

			 GetParentalControlStatus(); 
			
			 		 		 
		 }	
	    
//	 public void InitLpcDate()
//	 {
//		 GenieDebug.error("InitLpcDate","InitLpcDate ");
//		 
//		 CancleSoapThread();
//		 
//		 m_soapThread =  new Thread(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try{
//				
//					m_loginpassword = GetLoginPassword();
//					
//					Command.AutoRouterLogin("admin",
//							m_loginpassword);
//					
//					GenieDebug.error("InitLpcDate","InitLpcDate run 00");
//					if(Command.WrappedgetParentalControlStatus() && Command.OpenDNS_GetFilters())
//					{
//						setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success); 
//						sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
//					}else
//					{
//						setToastMessageid(GenieGlobalDefines.EFunctionParental_failure); 
//						//sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
//						if(0 == activeindex)
//						{
//							sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
//						}else
//						{
//							sendMessage2UI(GenieGlobalDefines.EFunctionParental_manageerror);
//						}
//						
//					}
//				}catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//			 
//		 };
//		 m_soapThread.start();
//		 		 		 
//	 }
	
	public void sendMessage2UI(int msg)
	{
		GenieDebug.error("sendMessage2UI", "msg = "+msg);
	   	handler.sendEmptyMessage(msg);
	}
	 
	private void showWaitingDialog()
    {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				String title = getResources().getString(R.string.loadingparentalcontrl)+" "+getResources().getString(R.string.info);
		    	
		    	progressDialog = ProgressDialog.show(GenieLPCmanage.this, title, getResources().getString(R.string.wait), true, false); 
		    	
				//progressDialog = ProgressDialog.show(GenieLPCmanage.this, "Loading...", "Please wait...", true, false);   
			}
		});
	}
	
//	public void ShowToastOnThread()
//	{
//		
//	}
	public void closeWaitingDialogOnThread()
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(progressDialog != null)
				{
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});
	}
	private void closeWaitingDialog()
    {
    	if(progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
    }
	public void showdialog_Ex()
	{
		int resurceid = 0;
		int titleid = 0;
		int Positiveid = 0;
		int Negativeid = 0;
		
		GenieDebug.error("showdialog_Ex", "activeindex ="+activeindex);
		if(0 != activeindex)
		{
			switch(activeindex)
        	{
        	case GenieGlobalDefines.EFunctionParental_Firmware:
        		resurceid = R.layout.lpcintro;
        		titleid = R.string.login; 
        		Positiveid = R.string.lpc_Positive;
        		Negativeid = R.string.lpc_Negative;
        		break;
        	case GenieGlobalDefines.EFunctionParental_intro:
        		resurceid = R.layout.lpcintro;
        		titleid = R.string.lpc_introtitle; 
        		Positiveid = R.string.yes;
        		Negativeid = R.string.no;
        		break;
        	case GenieGlobalDefines.EFunctionParental_presignln:
        		resurceid = R.layout.lpcpresignln;
        		titleid = R.string.lpc_presignlntitle; 
        		Positiveid = R.string.lpc_Positive;
        		Negativeid = R.string.lpc_Negative;
        		break;
        	case GenieGlobalDefines.EFunctionParental_signln:
        		resurceid = R.layout.lpcsignin;
        		titleid = R.string.lpcsignintitle; 
        		Positiveid = R.string.lpc_Positive;
        		Negativeid = R.string.lpc_Negative;
        		break;
        	case GenieGlobalDefines.EFunctionParental_Settings:
        		resurceid = R.layout.lpcsetting;
        		titleid = R.string.lpcsettitle; 
        		Positiveid = R.string.lpc_Positive;
        		Negativeid = R.string.lpc_Negative;
        		break;
        	case GenieGlobalDefines.EFunctionParental_Done:
        		resurceid = R.layout.lpcintro;
        		titleid = R.string.lpc_donetitle; 
        		Positiveid = R.string.lpc_Positive;
        		Negativeid = R.string.lpc_done_bttext;
        		break;
        	case GenieGlobalDefines.EFunctionParental_CreateAccount:
        		resurceid = R.layout.lpccreate2;
        		titleid = R.string.lpc_createtitle; 
        		Positiveid = R.string.lpc_Positive;
        		Negativeid = R.string.lpc_Negative;
        		break;
        	case GenieGlobalDefines.EFunctionParental_NetError:
        		resurceid = R.layout.lpcintro;
        		titleid = R.string.login; 
        		Positiveid = R.string.lpc_Positive;
        		Negativeid = R.string.lpc_Negative;
        		break;
        	}
		}
		GenieDebug.error("showdialog_Ex", "resurceid ="+resurceid);
		GenieDebug.error("showdialog_Ex", "resurceid ="+resurceid);
		if(0 != resurceid && 0 != titleid)
		{
			showdialog(resurceid,titleid,Positiveid,Negativeid);
		}
	}
	
	 public void InitDialogView_Firmware(int Positiveid,int Negativeid)
	 {
		//dialog_view.setNegativeButton(Negativeid, this);
		 dialog_view.setButton2(getResources().getString(Negativeid), this);
	 }
	 public void InitDialogView_intro(int Positiveid,int Negativeid)
	 {
		 //dialog_view.setPositiveButton(Positiveid, this);
		 //dialog_view.setNegativeButton(Negativeid, this);
		 dialog_view.setButton(getResources().getString(Negativeid), this);
		 dialog_view.setButton2(getResources().getString(Positiveid), this);
		 //dialog_view
		 
		 //TextView textview = (TextView)view.findViewById(R.id.Lpcintro);
		 //textview.setText(R.string.lpc_introtext);
	 }
	 public void InitDialogView_presignln(int Positiveid,int Negativeid)
	 {
		 
		 mRadioGroup = (RadioGroup)view.findViewById(R.id.PresignlnRG01);
		 mRadioGroup.setOnCheckedChangeListener(this);
		 
		 RadioButton mRadioButton = (RadioButton)view.findViewById(R.id.PresignlnCB01);
		 mRadioButton.setChecked(true);
		 SetRadioindex(LpcPreSignInExist);
		 
		 //dialog_view.setPositiveButton(Positiveid, this);
		 //dialog_view.setNegativeButton(Negativeid, this);
		 dialog_view.setButton(getResources().getString(Positiveid), this);
		 dialog_view.setButton2(getResources().getString(Negativeid), this);
	 }
	 public void InitDialogView_signln(int Positiveid,int Negativeid)
	 {
		 //dialog_view.setPositiveButton(Positiveid, this);
		 //dialog_view.setNegativeButton(Negativeid, this);
		 
		 dialog_view.setButton(getResources().getString(Positiveid), this);
		 dialog_view.setButton2(getResources().getString(Negativeid), this);
		 
		//qicheng.ai
		 EditText ETname = (EditText)view.findViewById(R.id.signinET_name);
		 ETname.setText(mTimpName);
		 if(getSignin_again() == 1)
		 {
			 //EditText ETname = (EditText)view.findViewById(R.id.signinET_name);
			 EditText ETpassword = (EditText)view.findViewById(R.id.signinET_password);
			 ETname.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME));
			 ETpassword.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD));		
			 
			 setSignin_again(0);
		 }
		 
	 }
	 public void InitDialogView_Settings(int Positiveid,int Negativeid)
	 {
		 mRadioGroup = (RadioGroup)view.findViewById(R.id.lpcsetRadioGroup01);
		 mRadioGroup.setOnCheckedChangeListener(this);
		 
		 
		 
		 String bundle = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE);
		 
		 GenieDebug.error("debug", "setting bundle="+bundle);
		 
		 if(bundle.equals(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CUSTOM))
		 {
			 RadioButton mRadioButton = (RadioButton)view.findViewById(R.id.lpcsetCBnone);
			 mRadioButton.setChecked(true);
			 SetRadioindex(LpcSettingNone);
		 }else if(bundle.toLowerCase().equals(setting[0].toLowerCase()))
		 {
			 RadioButton mRadioButton = (RadioButton)view.findViewById(R.id.lpcsetCBnone);
			 mRadioButton.setChecked(true);
			 SetRadioindex(LpcSettingNone);
		 }else if(bundle.toLowerCase().equals(setting[1].toLowerCase()))
		 {
			 RadioButton mRadioButton = (RadioButton)view.findViewById(R.id.lpcset_CBminimal);
			 mRadioButton.setChecked(true);
			 SetRadioindex(LpcSettingMinimal);
		 }else if(bundle.toLowerCase().equals(setting[2].toLowerCase()))
		 {
			 RadioButton mRadioButton = (RadioButton)view.findViewById(R.id.lpcset_CBlow);
			 mRadioButton.setChecked(true);
			 SetRadioindex(LpcSettingLow);
		 }else if(bundle.toLowerCase().equals(setting[3].toLowerCase()))
		 {
			 RadioButton mRadioButton = (RadioButton)view.findViewById(R.id.lpcset_CBmoderate);
			 mRadioButton.setChecked(true);
			 SetRadioindex(LpcSettingModerate);
		 }else if(bundle.toLowerCase().equals(setting[4].toLowerCase()))
		 {
			 RadioButton mRadioButton = (RadioButton)view.findViewById(R.id.lpcset_CBhigh);
			 mRadioButton.setChecked(true);
			 SetRadioindex(LpcSettingHigh);
		 }else 
		 {
			 RadioButton mRadioButton = (RadioButton)view.findViewById(R.id.lpcset_CBlow);
			 mRadioButton.setChecked(true);
			 SetRadioindex(LpcSettingLow);
		 }
		 
		 
//		 dialog_view.setPositiveButton(Positiveid, this);
//		 dialog_view.setNegativeButton(Negativeid, this);
		 dialog_view.setButton(getResources().getString(Positiveid), this);
		 dialog_view.setButton2(getResources().getString(Negativeid), this);
	 }
	 public void InitDialogView_Done(int Positiveid,int Negativeid)
	 {
		 //dialog_view.setPositiveButton(Positiveid, this);
		 //dialog_view.setButton(getResources().getString(Positiveid), this);
		 
		 dialog_view.setButton2(getResources().getString(Negativeid), this);

		 //dialog_view.setNegativeButton(Negativeid, this);
		 String str = getResources().getString(R.string.lpc_doneintro)+"\n\n *  "+
		 getResources().getString(R.string.lpc_doneintro1)+"\n\n *  "+
		 getResources().getString(R.string.lpc_doneintro2)+"\n\n *  "+
		 getResources().getString(R.string.lpc_doneintro3)+"\n\n *  "+
		 getResources().getString(R.string.lpc_doneintro4);
		 TextView textview = (TextView)view.findViewById(R.id.Lpcintro);
		 textview.setText(str);
	 }
	 public void InitDialogView_CreateAccount(int Positiveid,int Negativeid)
	 {
//		 dialog_view.setPositiveButton(Positiveid, this);
//		 dialog_view.setNegativeButton(Negativeid, this);
		 Button button = (Button)view.findViewById(R.id.createBT_check);
		 button.setOnClickListener(this);
		 dialog_view.setButton(getResources().getString(Positiveid), this);
		 dialog_view.setButton2(getResources().getString(Negativeid), this);
		 
		 EditText ETname = (EditText)view.findViewById(R.id.createET_name);
		 EditText ETpassword = (EditText)view.findViewById(R.id.createET_password);
		 EditText ETpassword2 = (EditText)view.findViewById(R.id.createET_password2);
		 EditText ETemail = (EditText)view.findViewById(R.id.createET_email);
		 EditText ETemail2 = (EditText)view.findViewById(R.id.createET_email2);
		 	
		 if(1 == CreateAccount_again)
		 {
			
		 
		 	ETname.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEUSERNAME));
		 	ETpassword.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEPASSWORD));		
		 	ETpassword2.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEPASSWORD2));
		 	ETemail.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEEMAIL));
		 	ETemail2.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEEMAIL2));
		 
		 	CreateAccount_again = 0;
		 }
		 
		
//		 ETname.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// TODO Auto-generated method stub
//				
//				EditText ETname = (EditText)view.findViewById(R.id.createET_name);
//
//				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//				{					 
//					 ETname.setHint(R.string.lpc_createname);
//				}else
//				{
//					ETname.setHint("");
//				}
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		 
//		 ETpassword.addTextChangedListener(new TextWatcher() {
//				
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//					// TODO Auto-generated method stub
//					
//					EditText ETpassword = (EditText)view.findViewById(R.id.createET_password);
//
//					if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//					{					 
//						ETpassword.setHint(R.string.lpc_createpassword);
//					}else
//					{
//						ETpassword.setHint("");
//					}
//				}
//				
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count,
//						int after) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void afterTextChanged(Editable s) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//		 
//		 ETpassword2.addTextChangedListener(new TextWatcher() {
//				
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//					// TODO Auto-generated method stub
//					
//					EditText ETpassword2 = (EditText)view.findViewById(R.id.createET_password2);
//
//					if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//					{					 
//						ETpassword2.setHint(R.string.lpc_createpassword2);
//					}else
//					{
//						ETpassword2.setHint("");
//					}
//				}
//				
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count,
//						int after) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void afterTextChanged(Editable s) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//		 
//		 ETemail.addTextChangedListener(new TextWatcher() {
//				
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//					// TODO Auto-generated method stub
//					
//					EditText ETemail = (EditText)view.findViewById(R.id.createET_email);
//
//					if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//					{					 
//						ETemail.setHint(R.string.lpc_createemail);
//					}else
//					{
//						ETemail.setHint("");
//					}
//				}
//				
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count,
//						int after) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void afterTextChanged(Editable s) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//		 
//		 ETemail2.addTextChangedListener(new TextWatcher() {
//				
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//					// TODO Auto-generated method stub
//					
//					EditText ETemail2 = (EditText)view.findViewById(R.id.createET_email2);
//
//					if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//					{					 
//						ETemail2.setHint(R.string.lpc_createemail2);
//					}else
//					{
//						ETemail2.setHint("");
//					}
//				}
//				
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count,
//						int after) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void afterTextChanged(Editable s) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//		 
		 

	 }
	 public void InitDialogView_NetError(int Positiveid,int Negativeid)
	 {
//		 dialog_view.setPositiveButton(Positiveid, this);
//		 dialog_view.setNegativeButton(Negativeid, this);
		 dialog_view.setButton(getResources().getString(Positiveid), this);
		 dialog_view.setButton2(getResources().getString(Negativeid), this);

	 }
	
	public void InitDialogView(int Positiveid,int Negativeid)
	{
		if(null == dialog_view || null == view)
		{
			return ;
		}
		
		switch(activeindex)
    	{
    	case GenieGlobalDefines.EFunctionParental_Firmware:
    		InitDialogView_Firmware(Positiveid,Negativeid);
    		break;
    	case GenieGlobalDefines.EFunctionParental_intro:
    		InitDialogView_intro(Positiveid,Negativeid);
    		break;
    	case GenieGlobalDefines.EFunctionParental_presignln:
    		InitDialogView_presignln(Positiveid,Negativeid);
    		break;
    	case GenieGlobalDefines.EFunctionParental_signln:
    		InitDialogView_signln(Positiveid,Negativeid);
    		break;
    	case GenieGlobalDefines.EFunctionParental_Settings:
    		InitDialogView_Settings(Positiveid,Negativeid);
    		break;
    	case GenieGlobalDefines.EFunctionParental_Done:
    		InitDialogView_Done(Positiveid,Negativeid);
    		break;
    	case GenieGlobalDefines.EFunctionParental_CreateAccount:
    		InitDialogView_CreateAccount(Positiveid,Negativeid);
    		break;
    	case GenieGlobalDefines.EFunctionParental_NetError:
    		InitDialogView_NetError(Positiveid,Negativeid);
    		break;
    	}
	}
	
	public void ChangInput()
	{
		switch(activeindex)
    	{

    	case GenieGlobalDefines.EFunctionParental_signln:
    		EditText sname = (EditText)view.findViewById(R.id.signinET_name);
    		if(sname.getText().toString().length() > 0)
    		{
    			signln_input_username = true;
    		}else
    		{
    			signln_input_username = false;
    		}
    		sname.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
//					if(sname.getText().toString().length() > 0)
//					{
//						
//					}
					
					
					GenieDebug.error("ChangInput"," 12345 start = "+start);
					GenieDebug.error("ChangInput"," 12345 before = "+before);
					GenieDebug.error("ChangInput"," 12345 count = "+count);
					EditText temp = (EditText)view.findViewById(R.id.signinET_name);
					if(temp.getText().toString().length() > 0)
					{
						signln_input_username = true;	
					}else
					{
						signln_input_username = false;
					}
					if(signln_input_username && signln_input_password)
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
					}else
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
    		EditText spassword = (EditText)view.findViewById(R.id.signinET_password);
    		if(spassword.getText().toString().length() > 0)
    		{
    			signln_input_password = true;
    		}else
    		{
    			signln_input_password = false;
    		}
    		spassword.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					EditText temp = (EditText)view.findViewById(R.id.signinET_password);
					if(temp.getText().toString().length() > 0)
					{
						signln_input_password = true;	
					}else
					{
						signln_input_password = false;
					}
					if(signln_input_username && signln_input_password)
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
					}else
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
    		break;
    		
    		

 
    	case GenieGlobalDefines.EFunctionParental_CreateAccount:

    		EditText cname = (EditText)view.findViewById(R.id.createET_name);
    		if(cname.getText().toString().length() > 0)
    		{
    			CreateAccount_input_username = true;
    		}else
    		{
    			CreateAccount_input_username = false;
    		}
    		cname.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					EditText temp = (EditText)view.findViewById(R.id.createET_name);
					if(temp.getText().toString().length() > 0)
					{
						CreateAccount_input_username = true;	
					}else
					{
						CreateAccount_input_username = false;
					}
					if(CreateAccount_input_username
							&& CreateAccount_input_password 
							&& CreateAccount_input_password2
							&& CreateAccount_input_email
							&& CreateAccount_input_email2)
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
					}else
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
    		EditText cpassword = (EditText)view.findViewById(R.id.createET_password);
    		if(cpassword.getText().toString().length() > 0)
    		{
    			CreateAccount_input_password = true;
    		}else
    		{
    			CreateAccount_input_password = false;
    		}

    		cpassword.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
//					if(sname.getText().toString().length() > 0)
//					{
//						
//					}
					GenieDebug.error("ChangInput"," 12345 start = "+start);
					GenieDebug.error("ChangInput"," 12345 before = "+before);
					GenieDebug.error("ChangInput"," 12345 count = "+count);
					EditText temp = (EditText)view.findViewById(R.id.createET_password);
					if(temp.getText().toString().length() > 0)
					{
						CreateAccount_input_password = true;	
					}else
					{
						CreateAccount_input_password = false;
					}
					if(CreateAccount_input_username
							&& CreateAccount_input_password 
							&& CreateAccount_input_password2
							&& CreateAccount_input_email
							&& CreateAccount_input_email2)
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
					}else
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
    		EditText cpassword2 = (EditText)view.findViewById(R.id.createET_password2);
    		if(cpassword2.getText().toString().length() > 0)
    		{
    			CreateAccount_input_password2 = true;
    		}else
    		{
    			CreateAccount_input_password2 = false;
    		}
    		cpassword2.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
//					if(sname.getText().toString().length() > 0)
//					{
//						
//					}
					GenieDebug.error("ChangInput"," 12345 start = "+start);
					GenieDebug.error("ChangInput"," 12345 before = "+before);
					GenieDebug.error("ChangInput"," 12345 count = "+count);
					EditText temp = (EditText)view.findViewById(R.id.createET_password2);
					if(temp.getText().toString().length() > 0)
					{
						CreateAccount_input_password2 = true;	
					}else
					{
						CreateAccount_input_password2 = false;
					}
					if(CreateAccount_input_username
							&& CreateAccount_input_password 
							&& CreateAccount_input_password2
							&& CreateAccount_input_email
							&& CreateAccount_input_email2)
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
					}else
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
    		EditText cemail = (EditText)view.findViewById(R.id.createET_email);
    		if(cemail.getText().toString().length() > 0)
    		{
    			CreateAccount_input_email = true;
    		}else
    		{
    			CreateAccount_input_email = false;
    		}
    		cemail.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
//					if(sname.getText().toString().length() > 0)
//					{
//						
//					}
					GenieDebug.error("ChangInput"," 12345 start = "+start);
					GenieDebug.error("ChangInput"," 12345 before = "+before);
					GenieDebug.error("ChangInput"," 12345 count = "+count);
					EditText temp = (EditText)view.findViewById(R.id.createET_email);
					if(temp.getText().toString().length() > 0)
					{
						CreateAccount_input_email = true;	
					}else
					{
						CreateAccount_input_email = false;
					}
					if(CreateAccount_input_username
							&& CreateAccount_input_password 
							&& CreateAccount_input_password2
							&& CreateAccount_input_email
							&& CreateAccount_input_email2)
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
					}else
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
    		EditText cemail2 = (EditText)view.findViewById(R.id.createET_email2);
    		if(cemail2.getText().toString().length() > 0)
    		{
    			CreateAccount_input_email2 = true;
    		}else
    		{
    			CreateAccount_input_email2 = false;
    		}
    		cemail2.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
//					if(sname.getText().toString().length() > 0)
//					{
//						
//					}
					GenieDebug.error("ChangInput"," 12345 start = "+start);
					GenieDebug.error("ChangInput"," 12345 before = "+before);
					GenieDebug.error("ChangInput"," 12345 count = "+count);
					EditText temp = (EditText)view.findViewById(R.id.createET_email2);
					if(temp.getText().toString().length() > 0)
					{
						CreateAccount_input_email2 = true;	
					}else
					{
						CreateAccount_input_email2 = false;
					}
					if(CreateAccount_input_username
							&& CreateAccount_input_password 
							&& CreateAccount_input_password2
							&& CreateAccount_input_email
							&& CreateAccount_input_email2)
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
					}else
					{
						if(null != dialog_view)
						    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
    		break;
    	}
	}
	
	//qicheng.ai
	private void FillNameAndPassword()
	{
		switch(activeindex)
    	{
    	case GenieGlobalDefines.EFunctionParental_signln:
			EditText temp = (EditText)view.findViewById(R.id.signinET_name);
			temp.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME));
			EditText temp2 = (EditText)view.findViewById(R.id.signinET_password);
			temp2.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD));
			break;
    	}
	}
	
	 public  void showdialog(int resurceid,int titleid,int Positiveid,int Negativeid)
	 {
	   	//LayoutInflater inflater = getLayoutInflater();
		//View login = inflater.inflate(R.layout.login,(ViewGroup) findViewById(R.layout.login));
			
		LayoutInflater inflater = LayoutInflater.from(this);
		view = inflater.inflate(resurceid,null);
		
//		if(null != dialog_view)
//		{
//			
//		}
		
		
		

		dialog_view = new MyDialog(this);
		if(activeindex != GenieGlobalDefines.EFunctionParental_intro){
			dialog_view.setTitle(titleid);
		}else{
			//清除保存的家长控制BypassAccount设置信息
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_ChildDeviceIDUserName, null);
			setBypassAccpuntName(null);
			openDNSUserName=null;
		}
		dialog_view.setView(view);		
	
		
		
		InitDialogView(Positiveid,Negativeid);
		dialog_view.show();
		
		//qicheng.ai
		FillNameAndPassword();
		
		ChangInput();
		
		switch(activeindex)
    	{

    	case GenieGlobalDefines.EFunctionParental_signln:
    		if(signln_input_username && signln_input_password)
    		{
    			if(null != dialog_view)
    				dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
    		}else
    		{
    			if(null != dialog_view)
    				dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
    		}
    		break;
    	case GenieGlobalDefines.EFunctionParental_CreateAccount:
    		if(CreateAccount_input_username
					&& CreateAccount_input_password 
					&& CreateAccount_input_password2
					&& CreateAccount_input_email
					&& CreateAccount_input_email2)
			{
				if(null != dialog_view)
				    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
			}else
			{
				if(null != dialog_view)
				    dialog_view.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
			}
    		break;
    	}
				

	}

	 public void onDialog_Positive_Firmware()
	 {
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
	 }
	 public void onDialog_Positive_intro()
	 {
		 
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_CreateAccount);
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_presignln);
	 }
	 public void onDialog_Positive_presignln()
	 {
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
	 }
	 public void onDialog_Positive_signln()
	 {
		 EditText temp = (EditText)view.findViewById(R.id.signinET_name);
		 mTimpName = temp.getText().toString();
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_presignln);
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
	 }
	 public void onDialog_Positive_Settings()
	 {
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
	 }
	 public void onDialog_Positive_Done()
	 {
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_presignln);
	 }
	 public void onDialog_Positive_CreateAccount()
	 {
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_presignln);
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
	 }
	 public void onDialog_Positive_NetError()
	 {
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
	 }
	public void onDialog_Positive()
	{
		GenieDebug.error("onDialog_Positive", "activeindex ="+activeindex);
		switch(activeindex)
    	{
    	case GenieGlobalDefines.EFunctionParental_Firmware:
    		onDialog_Positive_Firmware();
    		break;
    	case GenieGlobalDefines.EFunctionParental_intro:
    		onDialog_Positive_intro();
    		break;
    	case GenieGlobalDefines.EFunctionParental_presignln:
    		onDialog_Positive_presignln();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_signln:
    		onDialog_Positive_signln();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_Settings:
    		onDialog_Positive_Settings();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_Done:
    		onDialog_Positive_Done();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_CreateAccount:
    		onDialog_Positive_CreateAccount();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_NetError:
    		onDialog_Positive_NetError();
    		
    		break;
    	}
	}
	
//	public void LpcSettingsOnThread()
//	{
//		
//		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE,getResources().getString(setting[radioindex]));
//		
//		Command.OpenDNS_SetFilters();
//		
//		Command.sendMessage2UI();
//	}
	
	public void LpcSettingsOnThread()
	{
		
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE,setting[radioindex]);
		
		OpenDNS_SetFilters();
		
		//Command.sendMessage2UI();
	}
	
	private void OpenDNS_SetFilters()
	{
		//showOpenDNSLoginWaitingDialog();
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_SetFilters;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "filters_set";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("filters_set");	
	  	request.aElement.add("token");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
	  	request.aElement.add("device_id");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID));
		request.aElement.add("bundle");
		request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE));
		
		requestinfo.add(request);
	  	
		StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
	}
	
	
	
	
//	public void  LpcSigninOnThread()
//	{
//		if(Command.OpenDNS_login())
//		{			
//			m_loginpassword = GetLoginPassword();
//			
//			GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 0");
//			Command.AutoRouterLogin("admin",
//					m_loginpassword);
//			GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 1");
//			Command.OpenDNS_GetSetDeviceId();
//			GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 2");
//			Command.OpenDNS_GetFilters();
//			GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 3");
//		}
//		
//		GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 4");
//		Command.sendMessage2UI();
//	}
	
	/**
	 * 获得设备ID
	 * @param username
	 * @param password
	 */
	private void OpenDNS_GetSetDeviceId(String username ,String password)
	{
		
		GenieDebug.error("debug","AutoRouterLogin username = "+username);
		GenieDebug.error("debug","AutoRouterLogin password = "+password);
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
    	
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCmanage";
    	test2.aActionLable="OpenDNS_GetSetDeviceId";
    	test2.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
    	test2.aServer = "ParentalControl";
    	test2.aMethod = "Authenticate";
    	test2.aNeedwrap = false;
    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test2.aNeedParser=false;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();	
    	
    	
    	 if(!GenieRequest.m_SmartNetWork)
    	{
    		test2.aElement.add("NewUsername");
    	    test2.aElement.add(username);
    	    test2.aElement.add("NewPassword");
    	    test2.aElement.add(password);
    	}else
    	{
    		if(null != GenieRequest.m_SmartInfo)
    		{
    		    String serial = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).serial;
    		    if(serial != null)
    		    {
    		    	test2.aElement.add("NewUsername");
    		    	test2.aElement.add(GetSmartNetworkRouterUsername(serial));
    		    	test2.aElement.add("NewPassword");
    		    	test2.aElement.add(GetSmartNetworkRouterPassword(serial));
    		    }
    		}
    	}
    	 
    	
    	requestinfo.add(test2);
    	
    	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aActionLable="OpenDNS_GetSetDeviceId";
	  	request.aSoapType = GenieGlobalDefines.ESoapRequestDeviceID;
	  	request.aServer = "ParentalControl";
	  	request.aMethod = "GetDNSMasqDeviceID";
	  	request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("NewMACAddress");
	  	request.aElement.add("default");
	  	requestinfo.add(request);

	  	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
    	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
    		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
    	m_SettingRequest.Start();
	}
	
	private void OpenDNS_CreateDevice()
	{
		
		String Devicekey = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME)+"-"+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS);
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY,Devicekey);
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aActionLable="OpenDNS_GetSetDeviceId";
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_CreateDevice;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "device_create";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("device_create");		
	  	request.aElement.add("token");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
	  	request.aElement.add("device_key");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY));
		
	  	
	  	requestinfo.add(request);
	  	
	  	StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
	  		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
	  	m_SettingRequest.Start();
	}
	
	private void OpenDNS_GetDevice()
	{
		
		String Devicekey = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME)+"-"+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS);
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY,Devicekey);
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aActionLable="OpenDNS_GetSetDeviceId";
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_GetDevice;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "device_get";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("device_get");		
	  	request.aElement.add("token");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
	  	request.aElement.add("device_key");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY));
		
	  	
	  	requestinfo.add(request);
	  	
	  	StopRequest();
	  	
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
	  		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
	  	m_SettingRequest.Start();
	}
	
	private void OpenDNS_GetLabel()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aActionLable="OpenDNS_GetSetDeviceId";
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_GetLabel;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "label_get";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("label_get");		
	  	request.aElement.add("token");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
	  	request.aElement.add("device_id");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID));
		
	  	
	  	requestinfo.add(request);
	  	
	  	StopRequest();
	  	
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
	  		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
	  	m_SettingRequest.Start();
	}
	
	private void OpenDNS_login()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		

		String name = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME);
		String password = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD);
		
		GenieDebug.error("debug","OpenDNS_login name = "+name);
		GenieDebug.error("debug","OpenDNS_login password = "+password);
		
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_Login;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "account_signin";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("account_signin");		
	  	request.aElement.add("username");
	  	request.aElement.add(name);		
	  	request.aElement.add("password");
	  	request.aElement.add(password);	
	  	
	  	requestinfo.add(request);
	  	
	  	StopRequest();
	  	
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
	  		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
	  	m_SettingRequest.Start();
	}
	public void  LpcSigninOnThread()
	{
		showOpenDNSLoginWaitingDialog();
		OpenDNS_login();
		

	}
	
	
	private void OpenDNS_CreateAccount()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		
		String name = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEUSERNAME);
	 	String password = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEPASSWORD);		
	 	String email = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEEMAIL);
	 	
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_CreateAccount;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "account_create";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("account_create");		
	  	request.aElement.add("username");
	  	request.aElement.add(name);
	  	request.aElement.add("password");
	  	request.aElement.add(password);
	  	request.aElement.add("email");
	  	request.aElement.add(email);
		requestinfo.add(request);
	  	
		StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
	}
	
	 public void onDialog_Negative_OnThread()
	 {
	  
    		switch(activeindex)
        	{
        	case GenieGlobalDefines.EFunctionParental_Firmware:
        		
        		break;
        	case GenieGlobalDefines.EFunctionParental_intro:
        		
        		break;
        	case GenieGlobalDefines.EFunctionParental_presignln:
        		
        		break;
        	case GenieGlobalDefines.EFunctionParental_signln:
        		
        		LpcSigninOnThread();
        		
        		break;
        	case GenieGlobalDefines.EFunctionParental_Settings:
        		
        		LpcSettingsOnThread();
        		break;
        	case GenieGlobalDefines.EFunctionParental_Done:
        		
        		break;
        	case GenieGlobalDefines.EFunctionParental_CreateAccount:
        		OpenDNS_CreateAccount();	        		
        		break;
        	case GenieGlobalDefines.EFunctionParental_NetError:
        		break;
        	}
	    		
	    	  
	 }
	
//	 public void onDialog_Negative_OnThread()
//	 {
//	  
//		 CancleSoapThread();
//		 
//		 m_soapThread =  new Thread(){
//	    	public void run() 
//	    	{   
//	    		try{
//		    		switch(activeindex)
//		        	{
//		        	case GenieGlobalDefines.EFunctionParental_Firmware:
//		        		
//		        		break;
//		        	case GenieGlobalDefines.EFunctionParental_intro:
//		        		
//		        		break;
//		        	case GenieGlobalDefines.EFunctionParental_presignln:
//		        		
//		        		
//		        		break;
//		        	case GenieGlobalDefines.EFunctionParental_signln:
//		        		
//		        		LpcSigninOnThread();
//		        		
//		        		break;
//		        	case GenieGlobalDefines.EFunctionParental_Settings:
//		        		
//		        		LpcSettingsOnThread();
//		        		break;
//		        	case GenieGlobalDefines.EFunctionParental_Done:
//		        		
//		        		
//		        		break;
//		        	case GenieGlobalDefines.EFunctionParental_CreateAccount:
//		        		Command.OpenDNS_CreateAccount();	        		
//		        		break;
//		        	case GenieGlobalDefines.EFunctionParental_NetError:
//		        		break;
//		        	}
//	    		}catch (Exception e) {
//					// TODO: handle exception
//	    			e.printStackTrace();
//				}
//	    	}
//	    };
//	    m_soapThread.start();
//	    	  
//	 }
	
	 public void onDialog_Negative_Firmware()
	 {
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
	 }
	 public void onDialog_Negative_intro()
	 {
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_presignln);
	 }
	 public void onDialog_Negative_presignln()
	 {
		 switch(radioindex)
		 {
		 case LpcPreSignInExist:
			 sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
			 break;
		 case LpcPreSignInCreate:
			 sendMessage2UI(GenieGlobalDefines.EFunctionParental_CreateAccount);
			 break;
		 }
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
	 }
	 public void setSignin_again(int again)
	 {
		 Signin_again = again;
	 }
	 public int getSignin_again()
	 {
		 return Signin_again;
	 }
	 public void onDialog_Negative_signln()
	 {
		 EditText temp = (EditText)view.findViewById(R.id.signinET_name);
		 String name = temp.getText().toString();
		 EditText temp2 = (EditText)view.findViewById(R.id.signinET_password);
		 String password = temp2.getText().toString();
		 
		 GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME,name);
		 GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD,password);
		 
//		 dialog_view.showWaitingDialog();
		 onDialog_Negative_OnThread();
		 
	 }
	 public void onDialog_Negative_Settings()
	 {
//		 dialog_view.showWaitingDialog();
		 onDialog_Negative_OnThread();
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_Done);
	 }
	 
	 public void SaveUserInfo()
	 {
		 
		 String temp = null;
		 
		 temp = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME);
		 GenieDebug.error("SaveUserInfo","userInfo.PCUsername = "+temp);
		 GenieShareAPI.strncpy(userInfo.PCUsername, temp.toCharArray(),0,temp.length());
		 
		 temp = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD);		 
		 GenieDebug.error("SaveUserInfo","userInfo.PCPassword = "+temp);
		 GenieShareAPI.strncpy(userInfo.PCPassword, temp.toCharArray(),0,temp.length());
		 
		 temp = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
		 GenieDebug.error("SaveUserInfo","userInfo.PCDeviceID = "+temp);
		 GenieShareAPI.strncpy(userInfo.PCDeviceID, temp.toCharArray(),0,temp.length());
		 
		 temp = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN);		 
		 GenieDebug.error("SaveUserInfo","userInfo.PCLoginToken = "+temp);
		 GenieShareAPI.strncpy(userInfo.PCLoginToken, temp.toCharArray(),0,temp.length());
		 
		 GenieShareAPI.WriteData2File(userInfo,this, GenieGlobalDefines.USER_INFO_FILENAME);
		 
		 //重新赋值userInfo
		// userInfo = GenieShareAPI.ReadDataFromFile(this, GenieGlobalDefines.USER_INFO_FILENAME);
		 
	 }

	 //设置完成
	 public void onDialog_Negative_Done()
	 {
		 GenieDebug.error("onDialog_Negative_Done","onDialog_Negative_Done 0");
		 showrefreshWaitingDialog();
		 SaveUserInfo();
		 InitLpcDate();
		 
		 //设置完成，把判断是否为设置过滤级别标识设为初始值
		 isChangeFiteringLevel=false;
		//设置家长控制界面显示
		 setLCPManageViewDisplay(true);
		 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_presignln);
	 }
	 
	 
	 public void setCreateAccount_again(int again)
	 {
		 CreateAccount_again = again;
	 }
	 public int getCreateAccount_again()
	 {
		 return CreateAccount_again;
	 }
	 public void onDialog_Negative_CreateAccount()
	 {
		 EditText ETname = (EditText)view.findViewById(R.id.createET_name);
		 EditText ETpassword = (EditText)view.findViewById(R.id.createET_password);
		 EditText ETpassword2 = (EditText)view.findViewById(R.id.createET_password2);
		 EditText ETemail = (EditText)view.findViewById(R.id.createET_email);
		 EditText ETemail2 = (EditText)view.findViewById(R.id.createET_email2);
		 String Sname = ETname.getText().toString();
		 String Spassword = ETpassword.getText().toString();
		 String Spassword2 = ETpassword2.getText().toString();
		 String Semail = ETemail.getText().toString();
		 String Semail2 = ETemail2.getText().toString();
		 
		 GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEUSERNAME,Sname);
		 GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEPASSWORD,Spassword);		
		 GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEPASSWORD2,Spassword2);
		 GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEEMAIL,Semail);
		 GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEEMAIL2,Semail2);
		 
		 CreateAccount_again = 0;
		 
		 if(Sname != null && 
				 (Sname.indexOf(".") != -1 || 
				  Sname.indexOf("-") != -1 ||
				  Sname.indexOf("_") != -1))
		 {
			 CreateAccount_again = 1;
			 
			 activeindex = GenieGlobalDefines.EFunctionParental_CreateAccount;
			 setLpcFunctionType(GenieGlobalDefines.EFunctionParental_CreateAccount);
	         showdialog_Ex();
	         
	         String message = getResources().getString(R.string.usernameformatillegal);
			 Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_LONG);
			 toast.setGravity(Gravity.CENTER, 0, 0);
			 toast.show();
				
			 return ;			 
		 }
		 

		 if(Spassword != null && Spassword2 != null && !Spassword.equals(Spassword2))
		 {
			 CreateAccount_again = 1;
			 
			 activeindex = GenieGlobalDefines.EFunctionParental_CreateAccount;
			 setLpcFunctionType(GenieGlobalDefines.EFunctionParental_CreateAccount);
	         showdialog_Ex();
	         
	         String message = getResources().getString(R.string.lpc_createpassword2);
			 Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_LONG);
			 toast.setGravity(Gravity.CENTER, 0, 0);
			 toast.show();
				
			 //setToastMessageid(GenieGlobalDefines.EFunctionParental_CreatePassWordNotEquals);
			 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_CreateAccount);
			 return ;			 
		 }
		 
		 
		 if(Spassword != null && Spassword2 != null && 
		 	(Spassword.length() < 6 || Spassword.length() > 20))
		 {
			 CreateAccount_again = 1;
			 
			 activeindex = GenieGlobalDefines.EFunctionParental_CreateAccount;
			 setLpcFunctionType(GenieGlobalDefines.EFunctionParental_CreateAccount);
	         showdialog_Ex();
	         
	         String message = getResources().getString(R.string.passwordlengthillegal);
			 Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_LONG);
			 toast.setGravity(Gravity.CENTER, 0, 0);
			 toast.show();
				
			 //setToastMessageid(GenieGlobalDefines.EFunctionParental_CreatePassWordNotEquals);
			 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_CreateAccount);
			 return ;			 
		 }
		 
		
		 
		 if(Semail != null && Semail2 != null && !Semail.equals(Semail2))
		 {
			 CreateAccount_again = 1;
			 activeindex = GenieGlobalDefines.EFunctionParental_CreateAccount;
			 setLpcFunctionType(GenieGlobalDefines.EFunctionParental_CreateAccount);
	         showdialog_Ex();
	         
	         String message = getResources().getString(R.string.lpc_createemail2);
			 Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_LONG);
			 toast.setGravity(Gravity.CENTER, 0, 0);
			 toast.show();
			 
			 //setToastMessageid(GenieGlobalDefines.EFunctionParental_CreateEmailNotEquals);	
			 //sendMessage2UI(GenieGlobalDefines.EFunctionParental_CreateAccount);			 		 
			 return ;
		 }
		 
		 if(Semail != null )
		 {
			 String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			 Pattern regex = Pattern.compile(check);
			 
			 Matcher matcher = regex.matcher(Semail);
			 boolean isMatched = matcher.matches();
			 
			 
			 GenieDebug.error("debug", "isMatched = "+isMatched);
			 
			 if(!isMatched )
			 {
				 CreateAccount_again = 1;
				 
				 activeindex = GenieGlobalDefines.EFunctionParental_CreateAccount;
				 setLpcFunctionType(GenieGlobalDefines.EFunctionParental_CreateAccount);
		         showdialog_Ex();
		         
		         String message = getResources().getString(R.string.lpc_createeinvalidmail);
				 Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_LONG);
				 toast.setGravity(Gravity.CENTER, 0, 0);
				 toast.show();
				 
				 return ;
			 }
		 }
		 
		 
		 //dialog_view.showWaitingDialog();
		 onDialog_Negative_OnThread();

	 }
	 public void onDialog_Negative_NetError()
	 {
		 sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
	 }
	public void onDialog_Negative()
	{
		//dialog_view.showWaitingDialog();      //test
		GenieDebug.error("onDialog_Negative", "activeindex ="+activeindex);
		switch(activeindex)
    	{
    	case GenieGlobalDefines.EFunctionParental_Firmware:
    		onDialog_Negative_Firmware();
    		break;
    	case GenieGlobalDefines.EFunctionParental_intro:
    		onDialog_Negative_intro();
    		break;
    	case GenieGlobalDefines.EFunctionParental_presignln:
    		onDialog_Negative_presignln();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_signln:
    		onDialog_Negative_signln();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_Settings:
    		onDialog_Negative_Settings();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_Done:
    		onDialog_Negative_Done();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_CreateAccount:
    		onDialog_Negative_CreateAccount();
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_NetError:
    		onDialog_Negative_NetError();
    		
    		break;
    	}
	}	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		GenieDebug.error("onClick", "which ="+which);
		
		switch(which)
    	{
    		case DialogInterface.BUTTON_POSITIVE:
    			{
    				onDialog_Positive();
    				dialog.dismiss();
    			}
    			break;
        	case DialogInterface.BUTTON_NEGATIVE:
        		{
        			onDialog_Negative();
        			dialog.dismiss();
        		}
        		break;    		
    		default:
    			break;
    	}
	}
	
	private void OpenDNS_CheckNameAvailable()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
		String createname = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEUSERNAME);
		
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "account_signin";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("check_username");		
	  	request.aElement.add("username");
	  	request.aElement.add(createname);
		requestinfo.add(request);
	  	
		StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
	}
	

//	public void OpenDNS_CheckNameAvailable_OnThread()
//	 {
//	  
//		CancleSoapThread();
//		
//		m_soapThread = new Thread()
//	    {
//	    	public void run() 
//	    	{   
//	    		try{
//	    			Command.OpenDNS_CheckNameAvailable();
//	    		}catch (Exception e) {
//					// TODO: handle exception
//	    			e.printStackTrace();
//				}
//	    	}
//	    };
//	    m_soapThread.start();
//	    	  
//	 }
	
	public void onClick_CheckNameAvailable()
	{
		EditText edittext = (EditText)view.findViewById(R.id.createET_name);
		if(edittext.getText().toString().equals(""))
			return;

		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEUSERNAME,edittext.getText().toString());
		//dialog_view.showWaitingDialog();
		OpenDNS_CheckNameAvailable();
		
	}
	
	 public void OnControlButton()
	 {
		 boolean isEnable=false;
		 String pcstatus = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS);
					 
					 GenieDebug.error("OnControlButton", "pcstatus = "+pcstatus);
					 String enable = null;
					
		 if(null!= pcstatus &&  pcstatus.equals("1"))
		 {
			 enable = "0";
			 isEnable=false;
		 }else
		 {
			 enable = "1";
			 isEnable=true;
		 }
		 
		 
		 
		 ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	  
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aSoapType = GenieGlobalDefines.ESoapRequestConfigPCStatus;
	  	request.aServer = "ParentalControl";
	  	request.aMethod = "EnableParentalControl";
	  	request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request.aNeedParser=false;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();	  	
	  	request.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_PC_ENABLE);
	  	request.aElement.add(enable);
		
		requestinfo.add(request);
	  	
		StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
	  	
	  	//设置界面
	  	setLCPManageEnable(isEnable);
		  
	 }
//	 public void OnControlButton()
//	 {
//		 CancleSoapThread();
//		 
//		 m_soapThread =  new Thread(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try{
//					 String pcstatus = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS);
//					 
//					 GenieDebug.error("OnControlButton", "pcstatus = "+pcstatus);
//					 String enable = null;
//					
//					 if(pcstatus.equals("1"))
//					 {
//						 enable = "0";
//					 }else
//					 {
//						 enable = "1";
//					 }
//					
//					if(Command.WrappedsetParentalControlStatus(enable))
//					{
//						Command.WrappedgetParentalControlStatus();
//						setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success); 
//						sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
//					}else
//					{
//						Command.WrappedgetParentalControlStatus();
//						setToastMessageid(GenieGlobalDefines.EFunctionParental_failure); 
//						sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
//					}
//				}catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//			 
//		 };
//		 m_soapThread.start();
//		 		 		 
//	 }
	
	public void OnClickControlButton()
	{
		//showWaitingDialog();
		OnControlButton();
	}
	
	/**
	 * 过滤级别点击事件
	 */
	public void OnClickFilterChangButton()
	{
		isChangeFiteringLevel=true;
		setSignin_again(1);
		sendMessage2UI(GenieGlobalDefines.EFunctionParental_Settings);
	}
	
	public void OnCustomButton()
	 {
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
	
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_AccountRelay;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "account_relay";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("account_relay");		
	  	request.aElement.add("token");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));	
		
		requestinfo.add(request);
	  	
		StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	m_SettingRequest.SetProgressInfo(true, true);
	  	m_SettingRequest.Start();
	 
	 }
	
//	public void OnCustomButton()
//	 {
//		CancleSoapThread();
//		
//		m_soapThread = new Thread(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub				
//				try{
//	
//					if(Command.OpenDNS_AccountRelay())
//					{
//											
//						String url1 = "http://netgear.opendns.com/sign_in.php?device_id=";
//						String url2 = "&api_key=3443313B70772D2DD73B45D39F376199&relay_token=";
//						String relay_token = null;
//						String device_id = null;
//						relay_token = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RELAY_TOKEN);
//						device_id = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
//						
//						GenieDebug.error("OnCustomButton","relay_token = "+relay_token);
//						GenieDebug.error("OnCustomButton","device_id = "+device_id);
//						
//						setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success); 
//						sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
//						
//						changcustom = true;
//				    	String url = url1+device_id+url2+relay_token;
//				    	Uri uri = Uri.parse(url);
//				    	Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//				    	startActivity(intent);
//						
//						
//					}else
//					{
//						changcustom = true;
//						String url = "http://netgear.opendns.com/sign_in.php?";
//				    	Uri uri = Uri.parse(url);
//				    	Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//				    	startActivity(intent);
//						//setToastMessageid(GenieGlobalDefines.EFunctionParental_failure); 
//						//sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
//					}
//				}catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//			
//		 };
//		 m_soapThread.start();
//		 		 		 
//	 }
	
	public void OnClickCustomChangButton()
	{
		//showWaitingDialog();
		OnCustomButton();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		GenieDebug.error("onResume", "onResume changcustom = "+changcustom);
		if(changcustom)
		{
			GenieDebug.error("onResume", "onResume 1");
			//showWaitingDialog();
			InitLpcDate();
			GenieDebug.error("onResume", "onResume 2");
			changcustom = false;
			GenieDebug.error("onResume", "onResume 3");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		GenieDebug.error("onClick", "getId = "+v.getId());
		GenieDebug.error("onClick", "lpc_controlBT = "+R.id.lpc_controlBT);
		GenieDebug.error("onClick", "lpc_levelchangBT = "+R.id.lpc_levelchangBT);
		
		switch(v.getId())
		{
		case R.id.createBT_check:
			GenieDebug.error("onClick", "R.id.createBT_check");
			//dialog_view.showWaitingDialog();qicheng.ai
			onClick_CheckNameAvailable();
			break;
		case R.id.lpc_controlBT:
			OnClickControlButton();			
			break;
//		case R.id.lpc_levelchangBT:
//			OnClickFilterChangButton();
//			break;
//		case R.id.lpc_customchangBT:
//			OnClickCustomChangButton();
//			break;
		}
		
	}
	
	public void SetRadioindex(int index)
	{
		radioindex = index;
	}

	public void onCheckedChanged_presignln(int arg1)
	{
		switch(arg1)
    	{    	
    	case R.id.PresignlnCB01:
    		SetRadioindex(LpcPreSignInExist);
    		break;
    	case R.id.PresignlnCB02:
    		SetRadioindex(LpcPreSignInCreate);
    		break;
    	
    	}
		GenieDebug.error("onCheckedChanged_presignln", "radioindex="+radioindex);
//		radioindex
	}
	public void onCheckedChanged_Settings(int arg1)
	{
		switch(arg1)
    	{    	
    	case R.id.lpcsetCBnone:
    		SetRadioindex(LpcSettingNone);
    		break;
    	case R.id.lpcset_CBminimal:
    		SetRadioindex(LpcSettingMinimal);
    		break;
    	case R.id.lpcset_CBlow:
    		SetRadioindex(LpcSettingLow);
    		break;
    	case R.id.lpcset_CBmoderate:
    		SetRadioindex(LpcSettingModerate);
    		break;
    	case R.id.lpcset_CBhigh:
    		SetRadioindex(LpcSettingHigh);
    		break;
    	
    	}
		GenieDebug.error("onCheckedChanged_Settings", "radioindex="+radioindex);
	}
	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		GenieDebug.error("onCheckedChanged", "arg1 = "+arg1);
		switch(activeindex)
    	{    	
    	case GenieGlobalDefines.EFunctionParental_presignln:
    		onCheckedChanged_presignln(arg1);
    		break;
    	case GenieGlobalDefines.EFunctionParental_Settings:
    		onCheckedChanged_Settings(arg1);
    		break;
    	
    	}
	}
	public void setToastMessageid(int messageid)
	{
		ToastMessageid = messageid;
	}
	
	 public void showToast()
	 {
		 String error = null;
		 String error_message = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
		 
		 if(0 == ToastMessageid)
		 {
			 return ;
		 }
		switch(ToastMessageid)
		 {
		 case GenieGlobalDefines.EFunctionParental_failure:
			 error = GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR+":"+"\n"+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR)+"\n"+error_message;
			 break;
		 case GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable:
			 error = getResources().getString(R.string.lpc_createcheck)+":"+"\n"+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_AVAILABLE);
			 break;
		 case GenieGlobalDefines.EFunctionParental_CreatePassWordNotEquals:
			 error = GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR+":"+"\n"+"PassWordNotEquals";
			 break ;
		 case GenieGlobalDefines.EFunctionParental_CreateEmailNotEquals:
			 error = GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR+":"+"\n"+"EmailNotEquals";
			 break;
		 case GenieGlobalDefines.EFunctionParental_NetError:
			 error = GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR+":"+"\n"+getResources().getString(R.string.main_net_disable);
			 break ;
		 case GenieGlobalDefines.EOpenDNSRequest_Success:
			 error = "Success";
			 return ;
		 }
		
		
		
		 GenieDebug.error("debug","11111111 error = "+error);
		 GenieDebug.error("debug","11111111 error.length() = "+error.length());
	//qicheng.ai 	Cancel remark
		if(ToastMessageid == GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable ) //|| ToastMessageid ==  GenieGlobalDefines.EFunctionParental_failure)
		{
			//if(error.length() > 8)
			//{
				Toast toast = Toast.makeText(this,error, Toast.LENGTH_LONG);
			
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			//}
		}
	   
	   
	   ToastMessageid = 0;
	 }
	
	public class MyDialog extends AlertDialog
	{
		public ProgressDialog progressDialog = null;
		
		public MyDialog(Context context) {
			super(context);
//			this.setCanceledOnTouchOutside(false);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();		
			getthis().onBackPressed();
		}
		
		@Override
		public void cancel() {
			// TODO Auto-generated method stub
			if(activeindex==GenieGlobalDefines.EFunctionParental_intro || activeindex==GenieGlobalDefines.EFunctionParental_signln || activeindex==GenieGlobalDefines.EFunctionParental_CreateAccount){
				if(!isChangeFiteringLevel){
					super.cancel();
					getthis().onBackPressed();
				}else{
					super.cancel();
				}
					
			}
			
		}
		
		public void showWaitingDialog()
	    {
			closeWaitingDialog();
			
			progressDialog =  ProgressDialog.show(this.getContext(),null, getResources().getString(R.string.pleasewait)+"...", true, true,new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					if(!isChangeFiteringLevel){
						getthis().onBackPressed();
					}
//					isChangeFiteringLevel=false;
				}
			});
			

			
//			progressDialog =  new ProgressDialog(this.getContext());
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			progressDialog.show(this.getContext(), "Loading...", "Please wait...", true, true);
	    	
	    }
	    public void closeWaitingDialog()
	    {
	    	if(progressDialog != null)
			{
				progressDialog.dismiss();
				progressDialog = null;
			}
	    }
	}
	
	
	private void SetDNSMasqDeviceID()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aSoapType = GenieGlobalDefines.ESoapRequestSetDNSMasqDeviceID;
	  	request.aServer = "ParentalControl";
	  	request.aMethod = "SetDNSMasqDeviceID";
	  	request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request.aNeedParser=false;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS);
		//element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS));
	  	request.aElement.add("default");
		
	  	request.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID));
	  	requestinfo.add(request);
	  	
	  	StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
	  		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
	  	m_SettingRequest.Start();
	}
	
	private String GetSmartNetworkRouterUsername(String serial)
    {
  	  GenieDebug.error("debug","GetSmartNetworkRouterUsername ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     String username = settings.getString(GenieGlobalDefines.SMARTROUTERUSERRNAME+serial,null);
			     return username;
			}else
			{
				return null;
			}
    }
    private String GetSmartNetworkRouterPassword(String serial)
    {
  	  GenieDebug.error("debug","GetSmartNetworkRouterPassword ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     String password = settings.getString(GenieGlobalDefines.SMARTROUTERPASSWORD+serial,null);
			     return password;
			}else
			{
				return null;
			}
    }
	
    
	private void GetDNSMasqDeviceID()
	{
		
		m_loginpassword = GetLoginPassword();
		 
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
		GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCmanage";
    	test2.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
    	test2.aServer = "ParentalControl";
    	test2.aMethod = "Authenticate";
    	test2.aNeedwrap = false;
    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test2.aNeedParser=false;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();	
    	if(!GenieRequest.m_SmartNetWork)
    	{
	    	test2.aElement.add("NewUsername");
	    	test2.aElement.add("admin");
	    	test2.aElement.add("NewPassword");
	    	test2.aElement.add(m_loginpassword);
    	}else
    	{
    		if(null != GenieRequest.m_SmartInfo)
    		{
    		    String serial = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).serial;
    		    if(serial != null)
    		    {
    		    	test2.aElement.add("NewUsername");
    		    	test2.aElement.add(GetSmartNetworkRouterUsername(serial));
    		    	test2.aElement.add("NewPassword");
    		    	test2.aElement.add(GetSmartNetworkRouterPassword(serial));
    		    }
    		}
    	}
    	
    	requestinfo.add(test2);
    	
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aSoapType = GenieGlobalDefines.ESoapRequestDeviceID;
	  	request.aServer = "ParentalControl";
	  	request.aMethod = "GetDNSMasqDeviceID";
	  	request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("NewMACAddress");
	  	request.aElement.add("default");
	  	requestinfo.add(request);
	  	
	  	StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
	  		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
	  	m_SettingRequest.Start();
	}
	
	
	private void OpenDNS_GetFilters(String ActionLable)
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieLPCmanage";
	  	request.aActionLable=ActionLable;
	  	request.aOpenDNSType = GenieGlobalDefines.EOpenDNSRequest_GetFilters;
	  	//request.aServer = "ParentalControl";
	  	request.aMethod = "filters_get";
	  	//request.aNeedwrap = true;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	request.aElement.add("api_key");
	  	request.aElement.add(GenieRequest.API_KEY);
	  	request.aElement.add("method");
	  	request.aElement.add("filters_get");		
	  	request.aElement.add("token");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
	  	request.aElement.add("device_id");
	  	request.aElement.add(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID));
	  	requestinfo.add(request);
	  	
	  	StopRequest();
	  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
	  	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
	  		m_SettingRequest.SetProgressInfo(false, false);
	  	}else{
	  		m_SettingRequest.SetProgressInfo(true, false);
	  	}
	  	m_SettingRequest.Start();
	}


	
	
private  ProgressDialog m_OpenDNSLoginProgressDialog = null;
	
	
	private void closeOpenDNSLoginWaitingDialog()
    {
    	if(m_OpenDNSLoginProgressDialog != null)
		{
    		if(m_OpenDNSLoginProgressDialog.isShowing())
    			m_OpenDNSLoginProgressDialog.dismiss();
    		m_OpenDNSLoginProgressDialog = null;
		}
    }
	
	private void showOpenDNSLoginWaitingDialog()
    {
		
		//String title = getResources().getString(R.string.loading)+" "+getResources().getString(R.string.parentalcontrl)+" "+getResources().getString(R.string.info);
		    	
		m_OpenDNSLoginProgressDialog = ProgressDialog.show(GenieLPCmanage.this, null, getResources().getString(R.string.wait), true,true,new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				StopRequest();
				getthis().onBackPressed();
			}
		}); 
		
	}
	
	
	
	
	private  ProgressDialog m_refreshprogressDialog = null;
	
	
	private void closerefreshWaitingDialog()
    {
    	if(m_refreshprogressDialog != null)
		{
    		if(m_refreshprogressDialog.isShowing())
    			m_refreshprogressDialog.dismiss();
    		m_refreshprogressDialog = null;
		}
    }
	
	private void showrefreshWaitingDialog()
    {
		
		String title = getResources().getString(R.string.loadingparentalcontrl)+" "+getResources().getString(R.string.info);
		    	
		m_refreshprogressDialog = ProgressDialog.show(GenieLPCmanage.this, null, getResources().getString(R.string.wait), true,true,new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				StopRequest();
			}
		}); 
		
	}
	
	
	
	private void closeCheckWaitingDialog()
    {
    	if(m_checkprogressDialog != null)
		{
    		if(m_checkprogressDialog.isShowing())
    			m_checkprogressDialog.dismiss();
    		m_checkprogressDialog = null;
		}
    }
	
	private void showCheckWaitingDialog()
    {
		
		String title = getResources().getString(R.string.loadingparentalcontrl)+" "+getResources().getString(R.string.info);
		    	
		m_checkprogressDialog = ProgressDialog.show(GenieLPCmanage.this, title, getResources().getString(R.string.wait), true,true,new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				StopRequest();
				getthis().onBackPressed();
			}
		}); 
		
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
	    
		 public void UnRegisterBroadcastReceiver()
		 {
			 if(null != m_RequestReceiver)
				{
					unregisterReceiver(m_RequestReceiver);
					m_RequestReceiver = null;
				}
		 }

		 public void RegisterBroadcastReceiver()
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
		     
		        	 
 		        	 String actionlable = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_ACTION_LABLE);
		     		String lable = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_LABLE);
		     		RequestActionType ActionType = (RequestActionType) intent.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE);
		     		RequestResultType aResultType = (RequestResultType) intent.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE);
		     		String aServer = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SERVER);
		     		String aMethod = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_METHOD);
		     		String aResponseCode = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSECODE);
		     		int aHttpResponseCode = intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTPRESPONSECODE,-2);
		     		int aHttpType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTP_TYPE,-2);
		     		int aSoapType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SOAP_TYPE,-2);
		     		int aOpenDNSType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_OPENDNS_TYPE,-2);
		     		String aResponse = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSE);
		     	
		     		GenieDebug.error("debug", "RequestReceiver onReceive lable ="+lable);
		     		GenieDebug.error("debug", "RequestReceiver onReceive actionlable ="+actionlable);
	    		
		     		
//		     		if(aSoapType == GenieGlobalDefines.ESoapRequestGuestEnable
//		     				|| aSoapType == GenieGlobalDefines.ESoapReqiestTrafficEnable)
//		     		{
//		     			aResponseCode = "401";
//		     			aResultType = RequestResultType.failed;
//		     		}
		     		
		     				          	
		     		if(lable != null && lable.equals("GenieLPCmanage"))
		     		{
			     		GenieDebug.error("debug", "RequestReceiver onReceive aServer ="+aServer);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aMethod ="+aMethod);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResponseCode ="+aResponseCode);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aHttpResponseCode ="+aHttpResponseCode);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResponse ="+aResponse);
			     		GenieDebug.error("debug", "RequestReceiver onReceive ActionType ="+ActionType);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResultType ="+aResultType);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aHttpType ="+aHttpType);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aOpenDNSType ="+aOpenDNSType);
			     		GenieDebug.error("debug", "RequestReceiver onReceive m_opendnshoststatus ="+m_opendnshoststatus);

			     		
			     		if(null != ActionType && ActionType == RequestActionType.Soap)
			     		{
			     			if(null != aResultType)
			     			{
			     				switch(aSoapType)
			     				{
			     				case GenieGlobalDefines.ESoapRequestDeviceID:
			     					if(null!= actionlable && actionlable.equals("OpenDNS_GetSetDeviceId"))
			     					{
			     						if(aResultType == RequestResultType.Succes)
					     				{
			     							String deviceid = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
			     							GenieDebug.error("debug", "9999 deviceid = "+deviceid);
			     							if(deviceid != null)
			     							{
			     								if(!deviceid.equals(""))
			     								{
			     									OpenDNS_GetLabel();
			     									
			     								}else
			     								{
			     									GetRouterModerInfoAndWAN();
			     								}
			     							}else
			     							{
			     								closeOpenDNSLoginWaitingDialog();
						     					setSignin_again(1);
						     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
					    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
					    	    	        	showdialog_Ex();
			     							}
					     				}else
					     				{
					     					closeOpenDNSLoginWaitingDialog();
					     					setSignin_again(1);
					     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
				    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
				    	    	        	showdialog_Ex();
					     				}
			     					}else if(null!= actionlable && "OnlyRequestDeviceID".equals(actionlable)){
			     						String PCUsername = new String(userInfo.PCUsername,0,userInfo.PCUsername.length);
			     						if(PCUsername!=null){
			     							PCUsername=PCUsername.trim();
			     						}
				    	    			String PCDeviceID = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
				    	    			if(PCDeviceID!=null){
				    	    				PCDeviceID=PCDeviceID.trim();
			     						}
				    	    			String Deviceid = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
				    	    			if(PCUsername == null ||PCDeviceID == null || Deviceid == null ||
				    	    					PCUsername.equals(GenieSoap.KEY_NULL)||PCDeviceID.equals(GenieSoap.KEY_NULL) || (!PCDeviceID.equals(Deviceid)))
				    		    		{
				    	    				
				    	    	        	return ;
				    		    		}else
				    		    		{    				
				    		    			String deviceid = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
				    		    			String token  = new String(userInfo.PCLoginToken,0,userInfo.PCLoginToken.length);
				    		    			String password  = new String(userInfo.PCPassword,0,userInfo.PCPassword.length);
				    		    			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID,deviceid==null?deviceid:deviceid.trim());
				    		    			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN,token==null?token:token.trim());
				    		    			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME,PCUsername==null?PCUsername:PCUsername.trim());
				    		    			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD,password==null?password:password.trim()); 
				    		    		}
				    	    			
			     					}else{
				     					String PCUsername = new String(userInfo.PCUsername,0,userInfo.PCUsername.length);
				     					if(PCUsername!=null){
			     							PCUsername=PCUsername.trim();
			     						}
				    	    			String PCDeviceID = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
				    	    			if(PCDeviceID!=null){
				    	    				PCDeviceID=PCDeviceID.trim();
			     						}
				    	    			String Deviceid = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
				    	    			if(PCUsername == null ||PCDeviceID == null || Deviceid == null ||
				    	    					PCUsername.equals(GenieSoap.KEY_NULL)||PCDeviceID.equals(GenieSoap.KEY_NULL) || (!PCDeviceID.equals(Deviceid)))
				    		    		{
				    	    				closeCheckWaitingDialog();
				    	    				StopRequest();
				    	    				activeindex = GenieGlobalDefines.EFunctionParental_intro;
				    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_intro);
				    	    	        	showdialog_Ex();
				    	    	        	return ;
				    		    		}else
				    		    		{    				
				    		    			String deviceid = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
				    		    			String token  = new String(userInfo.PCLoginToken,0,userInfo.PCLoginToken.length);
				    		    			String password  = new String(userInfo.PCPassword,0,userInfo.PCPassword.length);
				    		    				
				    		    			GenieDebug.error("CheckHostStatus","deviceid = "+deviceid);
				    		    			GenieDebug.error("CheckHostStatus","token = "+token);
				    		    			GenieDebug.error("CheckHostStatus","PCUsername = "+PCUsername);
				    		    			GenieDebug.error("CheckHostStatus","password = "+password);
				    		    				
				    		    			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID,deviceid==null?deviceid:deviceid.trim());
				    		    			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN,token==null?token:token.trim());
				    		    			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME,PCUsername==null?PCUsername:PCUsername.trim());
				    		    			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD,password==null?password:password.trim()); 
				    		    				
				    		    			GenieDebug.error("CheckHostStatus","InitLpcDate = ");
				    		    			InitLpcDate();
				    		    		}
			     					}
			     	        		break;	
			     				case GenieGlobalDefines.ESoapRequestSetDNSMasqDeviceID:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						
			     						closeOpenDNSLoginWaitingDialog();
				     					setSignin_again(1);
				     					activeindex = GenieGlobalDefines.EFunctionParental_Settings;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_Settings);
			    	    	        	showdialog_Ex();
				     				}else
				     				{
				     					closeOpenDNSLoginWaitingDialog();
				     					setSignin_again(1);
				     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
			    	    	        	showdialog_Ex();
				     				}
			     					break;
			     				case GenieGlobalDefines.ESoapRequestRouterInfo:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						m_getroutermoderinfo = true;
				     				}else
				     				{
				     					m_getroutermoderinfo = false;
				     				}
			     					break;
			     				case GenieGlobalDefines.ESoapRequestConfigWan:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						if(m_getroutermoderinfo)
			     						{
			     							OpenDNS_GetDevice();
			     						}else
			     						{
			     							closeOpenDNSLoginWaitingDialog();
					     					setSignin_again(1);
					     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
				    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
				    	    	        	showdialog_Ex();
			     						}
				     				}else
				     				{
				     					closeOpenDNSLoginWaitingDialog();
				     					setSignin_again(1);
				     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
			    	    	        	showdialog_Ex();
				     				}
			     					break;	
			     				case GenieGlobalDefines.ESoapRequestPCStatus:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						OpenDNS_GetFilters("ESoapRequestPCStatus");
				     				}else
				     				{
				     					closeCheckWaitingDialog();
				     		        	activeindex = GenieGlobalDefines.EFunctionParental_manage;				     		        	
				     		        	InitManageViewButton(false);				     		        		
				     		        	GenieDebug.error("handleMessage","handleMessage EFunctionParental_manageerror 1");
				     		        	setLpcFunctionType(GenieGlobalDefines.EFunctionParental_manage);
				     		        	closerefreshWaitingDialog();
				     		        	//显示家长控制选项
				     		        	setLCPManageViewDisplay(true);
				     				}
			     					break;
			     				case GenieGlobalDefines.ESoapRequestConfigPCStatus:
			     					if(aResultType == RequestResultType.Succes)
			     					{
			     						GetParentalControlStatus();
			     						//setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success); 
			     						//sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
			     					 }else
			     					 {
			     						GetParentalControlStatus();
			     						//setToastMessageid(GenieGlobalDefines.EFunctionParental_failure); 
			     						//sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
			     					 }
			     					break;
			     					
			     				case GenieGlobalDefines.ESoapDNSMasqDeviceID:
									
									
									
									if(aResultType == RequestResultType.Succes){
										String dNSMasqDeviceID = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_NewDeviceID);
										GenieLPCmanage.this.dNSMasqDeviceID = dNSMasqDeviceID;
										GenieDebug.error("debug",
												"RequestReceiver onReceive ESoapDNSMasqDeviceID ="
														+ dNSMasqDeviceID);
											GetUsersForDeviceID(dNSMasqDeviceID);//4
									}else{
										Toast.makeText(GenieLPCmanage.this, R.string.failed_getdeviceid, Toast.LENGTH_SHORT).show();
									}

									break;
								case GenieGlobalDefines.ESoapMyDNSMasqDeviceID:
									
									if(aResultType == RequestResultType.Succes){
										
										String myDeviceID = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_MyNewDeviceID);
										GenieDebug.error("debug",
												"RequestReceiver onReceive ESoapMyDNSMasqDeviceID ="
														+ myDeviceID);
										
										String PCUsername = new String(userInfo.PCUsername,0,userInfo.PCUsername.length);
			     						if(PCUsername!=null){
			     							PCUsername=PCUsername.trim();
			     						}
				    	    			String PCDeviceID = new String(userInfo.PCDeviceID,0,userInfo.PCDeviceID.length);
				    	    			if(PCDeviceID!=null){
				    	    				PCDeviceID=PCDeviceID.trim();
			     						}
				    	    			//判断是否切换路由
				    	    			if(PCUsername == null ||PCDeviceID == null || myDeviceID == null ||
				    	    					PCUsername.equals(GenieSoap.KEY_NULL)||PCDeviceID.equals(GenieSoap.KEY_NULL) || (!PCDeviceID.equals(myDeviceID)))
				    		    		{
				    	    				StopRequest();
				    	    				closeCheckWaitingDialog();
				    	    				activeindex = GenieGlobalDefines.EFunctionParental_intro;
				    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_intro);
				    	    	        	showdialog_Ex();
				    	    	        	GenieShareAPI.strncpy(userInfo.PCDeviceID, myDeviceID.toCharArray(),0,myDeviceID.length());
				    	    	        	return ;
				    		    		}else{
				    		    			GetUsersForChildMyDeviceID(myDeviceID);
				    		    		}
				    	    			
										
									}else{
										Toast.makeText(GenieLPCmanage.this, R.string.failed_get_bindusers, Toast.LENGTH_SHORT).show();
									}
									break;
			     				default:
			     					break;
			     				}
			     				
			     			}
			     		}else if(null != ActionType && ActionType == RequestActionType.Http)
			     		{
			     			if(null != aResultType)
			     			{
			     				if(aResultType == RequestResultType.Succes)
			     				{
				     				switch(aHttpType)
				     				{
				     				case GenieGlobalDefines.EHttpCheckLpcHost:
				     					m_opendnshoststatus = true;
				     					if(GenieRequest.m_SmartNetWork)
				     					{
				     						GenieDebug.error("debug", "RequestReceiver onReceive m_opendnshoststatus ="+m_opendnshoststatus);
				     						GenieDebug.error("debug", "RequestReceiver onReceive m_opendnshoststatus 1");
				     						
				     						GetDNSMasqDeviceID();
				     					}
				     	        		break;			     	      
				     				case GenieGlobalDefines.EHttpGetCurrentSetting:
				     					
				     					if(!m_opendnshoststatus)
				     					{
				     						GenieDebug.error("debug", "RequestReceiver onReceive m_opendnshoststatus ="+m_opendnshoststatus);
				     						GenieDebug.error("debug", "RequestReceiver onReceive m_opendnshoststatus 0");
				     						closeCheckWaitingDialog();
				     						activeindex = GenieGlobalDefines.EFunctionParental_manage;
					     	        		InitManageViewButton(false);
					     	        		setLpcFunctionType(activeindex);
					     	        		ShowMessageDialog(getResources().getString(R.string.lpcinterneterror));
					     	        		//显示家长控制选项
					     	        		//setLCPManageViewDisplay(true);
					     	        		
				     					}else
				     					{
				     						GenieDebug.error("debug", "RequestReceiver onReceive m_opendnshoststatus ="+m_opendnshoststatus);
				     						GenieDebug.error("debug", "RequestReceiver onReceive m_opendnshoststatus 1");
				     						
				     						GetDNSMasqDeviceID();
				     					}
				     	        		break;	
				     				default:
				     					break;
				     				}
			     				}else
			     				{
			     					switch(aHttpType)
				     				{
				     				case GenieGlobalDefines.EHttpCheckLpcHost:
				     					m_opendnshoststatus = false;
				     					if(GenieRequest.m_SmartNetWork)
				     					{
				     						closeCheckWaitingDialog();
					     					activeindex = GenieGlobalDefines.EFunctionParental_manage;
					     	        		InitManageViewButton(false);
					     	        		setLpcFunctionType(activeindex);
					     	        		ShowMessageDialog(getResources().getString(R.string.lpcinterneterror));
					     	        		
					     	        		//setLCPManageViewDisplay(true);
					     	        		
				     					}
				     	        		break;			     	      
				     				case GenieGlobalDefines.EHttpGetCurrentSetting:	
				     					
				     					closeCheckWaitingDialog();
				     					activeindex = GenieGlobalDefines.EFunctionParental_manage;
				     	        		InitManageViewButton(false);
				     	        		setLpcFunctionType(activeindex);
				     	        		ShowMessageDialog(getResources().getString(R.string.lpcinterneterror));
				     	        		
				     	        		//setLCPManageViewDisplay(true);
				     	        		
				     	        		break;
				     				default:
				     					break;
				     				}
			     				}
			     			}
			     		}else if(null != ActionType && ActionType == RequestActionType.OpenDNS)
			     		{
			     			if(null != aResultType)
			     			{
			     				switch(aOpenDNSType)
			     				{
			     				case GenieGlobalDefines.EOpenDNSRequest_GetDevice:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						SetDNSMasqDeviceID();
				     				}else
				     				{
				     					OpenDNS_CreateDevice();
				     				}
			     					break;
			     				case GenieGlobalDefines.EOpenDNSRequest_CreateDevice:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						SetDNSMasqDeviceID();
				     				}else
				     				{
				     					closeOpenDNSLoginWaitingDialog();
				     					setSignin_again(1);
				     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
			    	    	        	showdialog_Ex();
				     				}
			     					break;
			     				case GenieGlobalDefines.EOpenDNSRequest_GetFilters:			     					
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						
			     						if(actionlable != null)
			     						{
			     							if(actionlable.equals("OpenDNS_GetSetDeviceId"))
			     							{
			     								closeOpenDNSLoginWaitingDialog();
						     					setSignin_again(1);
						     					activeindex = GenieGlobalDefines.EFunctionParental_Settings;
					    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_Settings);
					    	    	        	showdialog_Ex();
			     							}else
			     							{
			     								closeCheckWaitingDialog();
			     								activeindex = GenieGlobalDefines.EFunctionParental_manage;
			     				        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manage 0");
			     				        		InitLpcManageView();
			     				        		InitManageViewButton(true);
			     				        		//设置家长控制界面显示
			     				        		setLCPManageViewDisplay(true);
			     				        		
			     				        		GenieDebug.error("handleMessage","handleMessage EFunctionParental_manage 1");
			     				        		setLpcFunctionType(activeindex);
			     				        		closerefreshWaitingDialog();
			     				        		//closeRefreshDialog();
			     							}
			     						}
				     				}else
				     				{
				     					if(actionlable != null)
			     						{
			     							if(actionlable.equals("OpenDNS_GetSetDeviceId"))
			     							{
			     								closeOpenDNSLoginWaitingDialog();
						     					setSignin_again(1);
						     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
					    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
					    	    	        	showdialog_Ex();
			     							}else 
			     							{
			     								closeCheckWaitingDialog();
						     		        	activeindex = GenieGlobalDefines.EFunctionParental_manage;				     		        	
						     		        	InitManageViewButton(false);				     		        		
						     		        	GenieDebug.error("handleMessage","handleMessage EFunctionParental_manageerror 1");
						     		        	setLpcFunctionType(GenieGlobalDefines.EFunctionParental_manage);
						     		        	closerefreshWaitingDialog();
						     		        	
						     		        	setLCPManageViewDisplay(true);
			     							}
			     						}
				     				}
			     					
			     					break;
			     				case GenieGlobalDefines.EOpenDNSRequest_GetLabel:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						
				     				}else
				     				{
				     					String error = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
				     					GenieDebug.error("OpenDNS_GetSetDeviceId","error = "+error);
				     					if(null != error && error.equals("4001"))
				     					{
				     						closeOpenDNSLoginWaitingDialog();
					     					setSignin_again(1);
					     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
				    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
				    	    	        	showdialog_Ex();
				    	    	        	
		    	    	        			String	message = getResources().getString(R.string.opendnsdeviceisnotmine);
		    	    	        			
		    	    	        			if(message != null)
		    	    	        			{
		    	    	        				Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_SHORT);
		    	    	        				toast.setGravity(Gravity.CENTER, 0, 0);
		    	    	        				toast.show();
		    	    	        			}
		    	    	        			
				     						return ;
				     					}else if(null != error && error.equals("4003"))
				     					{
				     						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, "");
				     					}
				     				}
			     					String deviceid = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
			     					if(deviceid != null)
			     					{
			     						if(deviceid.equals(""))
			     						{
			     							GetRouterModerInfoAndWAN();
			     						}else
			     						{
			     							OpenDNS_GetFilters("OpenDNS_GetSetDeviceId");
			     						}
			     					}else
			     					{
			     						closeOpenDNSLoginWaitingDialog();
				     					setSignin_again(1);
				     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
			    	    	        	showdialog_Ex();
			    	    	        	
			    	    	        	String error =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
			    	    	        	GenieDebug.error("debug","opendns error = "+error);
			    	    	        	if(null != error)
			    	    	        	{
			    	    	        		try{
			    	    	        			int errorcode = Integer.valueOf(error);			    	    	        			
			    	    	        			String message = null;			    	    	        			
			    	    	        			if(errorcode == 4001)
			    	    	        			{
			    	    	        				message = getResources().getString(R.string.opendnsdeviceisnotmine);
			    	    	        			}
			    	    	        			if(message != null)
			    	    	        			{
			    	    	        				Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_SHORT);
			    	    	        				toast.setGravity(Gravity.CENTER, 0, 0);
			    	    	        				toast.show();
			    	    	        			}
			    	    	        		}catch(Exception e)
			    	    	        		{
			    	    	        			e.printStackTrace();
			    	    	        		}
			    	    	        	}
			     					}
			     					break;
			     				case GenieGlobalDefines.EOpenDNSRequest_CreateAccount:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						activeindex = GenieGlobalDefines.EFunctionParental_signln;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
			    	    	        	showdialog_Ex();
				     				}else
				     				{
				     					setCreateAccount_again(1);
				     					activeindex = GenieGlobalDefines.EFunctionParental_CreateAccount;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_CreateAccount);
			    	    	        	showdialog_Ex();
			    	    	        	
			    	    	        	String error =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
			    	    	        	GenieDebug.error("debug","opendns error = "+error);
			    	    	        	if(null != error)
			    	    	        	{
			    	    	        		try{
			    	    	        			int errorcode = Integer.valueOf(error);			    	    	        			
			    	    	        			String message = null;			    	    	        			
			    	    	        			if(errorcode == 3005)
			    	    	        			{
			    	    	        				message = getResources().getString(R.string.emailisunavailable);
			    	    	        			}
			    	    	        			if(errorcode == 3001)
			    	    	        			{
			    	    	        				message = getResources().getString(R.string.usernamenotavailable);
			    	    	        			}
			    	    	        			if(message != null)
			    	    	        			{
			    	    	        				Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_SHORT);
			    	    	        				toast.setGravity(Gravity.CENTER, 0, 0);
			    	    	        				toast.show();
			    	    	        			}
			    	    	        		}catch(Exception e)
			    	    	        		{
			    	    	        			e.printStackTrace();
			    	    	        		}
			    	    	        	}
				     				}
			     					break;
			     				case GenieGlobalDefines.EOpenDNSRequest_Login:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						m_loginpassword = GetLoginPassword();
			     						
			     						GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 0");
			     						OpenDNS_GetSetDeviceId("admin",m_loginpassword);
			     						GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 1");
			     						//Command.OpenDNS_GetSetDeviceId();
			     						GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 2");
			     						//OpenDNS_GetFilters();
			     						GenieDebug.error("LpcSigninOnThread"," LpcSigninOnThread 3");
				     				}else
				     				{
				     					closeOpenDNSLoginWaitingDialog();
				     					setSignin_again(1);
				     					activeindex = GenieGlobalDefines.EFunctionParental_signln;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_signln);
			    	    	        	showdialog_Ex();
				     				}
			     					break;
			     				case GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable:
			     					String message = getResources().getString(R.string.lpc_createcheck)+":"+"\n"+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_AVAILABLE);
			     					Toast toast = Toast.makeText(GenieLPCmanage.this,message, Toast.LENGTH_LONG);
			     					toast.setGravity(Gravity.CENTER, 0, 0);
			     					toast.show();
			     					break;
			     				case GenieGlobalDefines.EOpenDNSRequest_AccountRelay:
			     					if(aResultType == RequestResultType.Succes)
				     				{
			     						String url1 = "http://netgear.opendns.com/sign_in.php?device_id=";
			    						String url2 = "&api_key=3443313B70772D2DD73B45D39F376199&relay_token=";
			    						String relay_token = null;
			    						String device_id = null;
			    						relay_token = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RELAY_TOKEN);
			    						device_id = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
			    						
			    						GenieDebug.error("OnCustomButton","relay_token = "+relay_token);
			    						GenieDebug.error("OnCustomButton","device_id = "+device_id);
			    						
			    						setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success); 
			    						sendMessage2UI(GenieGlobalDefines.EFunctionParental_manage);
			    						
			    						changcustom = true;
			    				    	String url = url1+device_id+url2+relay_token;
			    				    	Uri uri = Uri.parse(url);
			    				    	Intent intent2 = new Intent(Intent.ACTION_VIEW,uri);
			    				    	startActivity(intent2);
			    						
			    					}else
			    					{
			    						changcustom = true;
			    						String url = "http://netgear.opendns.com/sign_in.php?";
			    				    	Uri uri = Uri.parse(url);
			    				    	Intent intent2 = new Intent(Intent.ACTION_VIEW,uri);
			    				    	startActivity(intent2);			    						
			    					}
			     					break;
			     				case GenieGlobalDefines.EOpenDNSRequest_SetFilters:
			     					if(aResultType == RequestResultType.Succes)
				     				{
	     								//closeOpenDNSLoginWaitingDialog();
				     					activeindex = GenieGlobalDefines.EFunctionParental_Done;
				     					//设置完成保存用户信息
				     					SaveUserInfo();
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_Done);
			    	    	        	showdialog_Ex();
			     						
				     				}else
				     				{
				     					
				     					//closeOpenDNSLoginWaitingDialog();
				     					activeindex = GenieGlobalDefines.EFunctionParental_Settings;
			    	    				setLpcFunctionType(GenieGlobalDefines.EFunctionParental_Settings);
			    	    	        	showdialog_Ex();
			     							
				     				}
			     					break;
			     					
			     				case GenieGlobalDefines.ESoapDeviceChildUserName:
									
									
									
									if(aResultType == RequestResultType.Succes){
										String childDeviceIDUserName = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_ChildDeviceIDUserName);
										GenieDebug.error("debug",
												"RequestReceiver onReceive ESoapDeviceChildUserName ="
														+ childDeviceIDUserName);
										openDNSUserName = childDeviceIDUserName;
										StopRequest();
										setBypassAccpuntName(openDNSUserName);
									}else{
										String error = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
										GenieDebug.error("debug",
												"RequestReceiver onReceive ESoapDeviceChildUserName ="
														+ error);
										if(error.equals("4003")){
											//没有用户登陆
//											if(openDNSuserNames!=null){
//												setNotExistChildUserName(openDNSuserNames);
//											}
											GetDNSMasqDeviceID1();//3
										}
									}
									
									
									break;
								case GenieGlobalDefines.ESoapDeviceUserName:
									
									
									
									if(aResultType == RequestResultType.Succes){
										String deviceIDUserName = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceIDUserName);
										dNSMasqDeviceID=GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_MyNewDeviceID);
										GenieDebug.error("debug",
												"RequestReceiver onReceive ESoapDeviceUserName ="
														+ deviceIDUserName+"  dNSMasqDeviceID:"+dNSMasqDeviceID   );
//										openDNSuserNames = getString(deviceIDUserName);
										StopRequest();
//										UnRegisterBroadcastReceiver();
										if(GenieLPCBypass.getString(deviceIDUserName)!=null && GenieLPCBypass.getString(deviceIDUserName).length>0){
											Intent  intentBypass = new Intent();
											intentBypass.setClass(GenieLPCmanage.this, GenieLPCBypass.class);
											intentBypass.putExtra("openDNSuserNames", deviceIDUserName);
											intentBypass.putExtra("dNSMasqDeviceID", dNSMasqDeviceID);
											GenieLPCmanage.this.startActivity(intentBypass);
										}else{
											Toast.makeText(GenieLPCmanage.this, R.string.no_bypassaccount_inf, Toast.LENGTH_SHORT).show();
										}
										
//										GetMyDNSMasqDeviceID();//3
									}else{
										String error1 = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
										GenieDebug.error("debug",
												"RequestReceiver onReceive ESoapDeviceUserName ="
														+ error1);
										if(error1.equals("4012")){
											//OpenDns中没有创建子账号
//											showToast(R.string.NoCreateUserName);
											showLoginDialog();
										}
									}
									break;
			     				default:
			     					break;
			     				}
			     				
			     			}
			     		}
			     		
		     		}

		         }
		  }
		  
		  private boolean m_getroutermoderinfo  = false;
	
		  private void  GetRouterModerInfoAndWAN()
		  {
			  m_getroutermoderinfo = false;
			  ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		  		
			  	GenieRequestInfo request = new GenieRequestInfo();
			  	request.aRequestLable="GenieLPCmanage";
			  	request.aSoapType = GenieGlobalDefines.ESoapRequestRouterInfo;
			  	request.aServer = "DeviceInfo";
			  	request.aMethod = "GetInfo";
			  	request.aNeedwrap = false;
			  	request.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
			  	request.aNeedParser=true;
			  	request.aTimeout = 25000;
			  	request.aElement = new ArrayList<String>();
			  	requestinfo.add(request);
			  	
			  	GenieRequestInfo request0 = new GenieRequestInfo();
			  	request0.aRequestLable="GenieLPCmanage";
			  	request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigWan;
			  	request0.aServer = "WANIPConnection";
			  	request0.aMethod = "GetInfo";
			  	request0.aNeedwrap = false;
			  	request0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
			  	request0.aNeedParser=true;
			  	request0.aTimeout = 25000;
			  	request0.aElement = new ArrayList<String>();
			  	
			  	requestinfo.add(request0);
			  	
			  	
			  	GenieRequestInfo request1 = new GenieRequestInfo();

			  	StopRequest();
			  	m_SettingRequest = new GenieRequest(GenieLPCmanage.this,requestinfo);
			  	if(progressDialog!=null || Refresh_progressDialog!=null || m_checkprogressDialog!=null){
			  		m_SettingRequest.SetProgressInfo(false, false);
			  	}else{
			  		m_SettingRequest.SetProgressInfo(true, false);
			  	}
			  	m_SettingRequest.Start();
		  }
		  
		  
		
		  
		//获取默认的设备ID
			private void GetDNSMasqDeviceID1()
			{
				
				
				ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		    	
				

				 
		    	GenieRequestInfo test2 = new GenieRequestInfo();
		    	test2.aRequestLable="GenieLPCmanage";
		    	test2.aSoapType = GenieGlobalDefines.ESoapDNSMasqDeviceID;
		    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
		    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;
		    	test2.aNeedwrap = true;
		    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		    	test2.aNeedParser=true;
		    	test2.aTimeout = 20000;
		    	test2.aElement = new ArrayList<String>();
		    	test2.aElement.add("NewMACAddress");
		    	test2.aElement.add("default");
		    	
		    	requestinfo.add(test2);

		    	StopRequest();
		    	m_SettingRequest = new GenieRequest(this,requestinfo);
		    	//String title = getResources().getString(R.string.loading);
				
		    	m_SettingRequest.SetProgressInfo(true, true);
		    	m_SettingRequest.SetProgressText(null, null);
		    	m_SettingRequest.Start();
			}
			
			//获取自己设备ID
			private void GetMyDNSMasqDeviceID()
			{
				
				
				ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
				
				m_loginpassword = GetLoginPassword();
				 
				 
				 GenieRequestInfo test = new GenieRequestInfo();
				 test.aRequestLable="GenieLPCmanage";
				 test.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
				 test.aServer = "ParentalControl";
				 test.aMethod = "Authenticate";
				 test.aNeedwrap = false;
				 test.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
				 test.aNeedParser=false;
				 test.aTimeout = 25000;
				 test.aElement = new ArrayList<String>();

				 
				 if(!GenieRequest.m_SmartNetWork)
		    	{
					 test.aElement.add("NewUsername");
					 test.aElement.add("admin");
					 test.aElement.add("NewPassword");
					 test.aElement.add(m_loginpassword);
		    	}
				 
//				 else
//		    	{
//		    		if(null != GenieRequest.m_SmartInfo)
//		    		{
//		    		    String serial = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).serial;
//		    		    if(serial != null)
//		    		    {
//		    		    	test.aElement.add("NewUsername");
//		    		    	test.aElement.add(GetSmartNetworkRouterUsername(serial));
//		    		    	test.aElement.add("NewPassword");
//		    		    	test.aElement.add(GetSmartNetworkRouterPassword(serial));
//		    		    }
//		    		}
//		    	}
		    	
		    	requestinfo.add(test);
		    	
		    	//查询设备ID
		    	GenieRequestInfo test1 = new GenieRequestInfo();
		    	test1.aRequestLable="GenieLPCmanage";
		    	test1.aActionLable="OnlyRequestDeviceID";
		    	test1.aSoapType = GenieGlobalDefines.ESoapRequestDeviceID;
		    	test1.aServer = "ParentalControl";
		    	test1.aMethod = "GetDNSMasqDeviceID";
		    	test1.aNeedwrap = true;
		    	test1.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		    	test1.aNeedParser=true;
		    	test1.aTimeout = 25000;
		    	test1.aElement = new ArrayList<String>();
		    	test1.aElement.add("NewMACAddress");
		    	test1.aElement.add("default");
			  	requestinfo.add(test1);

				 
		    	GenieRequestInfo test2 = new GenieRequestInfo();
		    	test2.aRequestLable="GenieLPCmanage";
		    	test2.aSoapType = GenieGlobalDefines.ESoapMyDNSMasqDeviceID;
		    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
		    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;
		    	test2.aNeedwrap = true;
		    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		    	test2.aNeedParser=true;
		    	test2.aTimeout = 25000;
		    	test2.aElement = new ArrayList<String>();
		    	test2.aElement.add("NewMACAddress");
		    	test2.aElement.add(getMACAddress());
		    	
		    	
		    	
		    	requestinfo.add(test2);

		    	StopRequest();
		    	m_SettingRequest = new GenieRequest(this,requestinfo);
		    	//String title = getResources().getString(R.string.loading);
				
		    	m_SettingRequest.SetProgressInfo(true, true);
		    	m_SettingRequest.SetProgressText(null, null);
		    	m_SettingRequest.Start();
			}
			
			//getMACAddress
			public String getMACAddress(){
				WifiManager  WIFI = (WifiManager)getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = WIFI.getConnectionInfo();
				String mac = info.getMacAddress();
				GenieDebug.error("getMACAddress:", "MACAddress: "+mac);
				String macStr = mac.replaceAll(":", "");
				return macStr;
			}
			
			//获取自己设备绑定的用户
			private void GetUsersForChildMyDeviceID(String myDeviceId)
			{
				if(myDeviceId == null){
					return;
				}
				
				ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

				 
		    	GenieRequestInfo test2 = new GenieRequestInfo();
		    	test2.aRequestLable="GenieLPCmanage";
		    	test2.aOpenDNSType = GenieGlobalDefines.ESoapDeviceChildUserName;
		    	
		    	test2.aMethod = "device_child_username_get";
		    	
//		    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
//		    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;

			  	test2.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
		    	test2.aNeedParser=true;
		    	test2.aTimeout = 20000;
		    	test2.aElement = new ArrayList<String>();
		    	test2.aElement.add("api_key");
		    	test2.aElement.add(GenieRequest.API_KEY);
		    	test2.aElement.add("method");
		    	test2.aElement.add("device_child_username_get");
		    	test2.aElement.add("device_id");
		    	test2.aElement.add(myDeviceId);
		    	
		    	requestinfo.add(test2);

		    	StopRequest();
		    	m_SettingRequest = new GenieRequest(this,requestinfo);
		    	//String title = getResources().getString(R.string.loading);
				
		    	m_SettingRequest.SetProgressInfo(true, true);
		    	m_SettingRequest.SetProgressText(null, null);
		    	m_SettingRequest.Start();
			}
			
			
			//获取设备所有的用户
			private void GetUsersForDeviceID(String deviceId)
			{
				if(deviceId == null){
					return;
				}
				
				ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

				 
		    	GenieRequestInfo test2 = new GenieRequestInfo();
		    	test2.aRequestLable="GenieLPCmanage";
		    	test2.aOpenDNSType = GenieGlobalDefines.ESoapDeviceUserName;
		    	
		    	test2.aMethod = "device_children_get";
		    	
//		    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
//		    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;

			  	test2.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
		    	test2.aNeedParser=true;
		    	test2.aTimeout = 20000;
		    	test2.aElement = new ArrayList<String>();
		    	test2.aElement.add("api_key");
		    	test2.aElement.add(GenieRequest.API_KEY);
		    	test2.aElement.add("method");
		    	test2.aElement.add("device_children_get");
		    	test2.aElement.add("parent_device_id");
		    	test2.aElement.add(deviceId);
		    	
		    	requestinfo.add(test2);

		    	StopRequest();
		    	m_SettingRequest = new GenieRequest(this,requestinfo);
		    	//String title = getResources().getString(R.string.loading);
		    	String message = getResources().getString(R.string.getbypass);
				
		    	m_SettingRequest.SetProgressInfo(true, true);
		    	m_SettingRequest.SetProgressText(null, message);
		    	m_SettingRequest.Start();
			}
			
			
			private void  showLoginDialog(){
				AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);
				loginDialog.setMessage(R.string.NoCreatebypassaccount);
				
				loginDialog.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				
				loginDialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String url = "http://netgear.opendns.com/sign_in.php?";
				    	Uri uri = Uri.parse(url);
				    	Intent intent2 = new Intent(Intent.ACTION_VIEW,uri);
				    	startActivity(intent2);	
					}
				});
				
//				dialog = 
					loginDialog.create().show();
//				dialog.show();
				
			}
			
		//设置家长控制界面是否显示
		private void setLCPManageViewDisplay(boolean isVisible){
			
			RelativeLayout layout=(RelativeLayout) this.findViewById(R.id.LPC_mainitem);
			RelativeLayout item1 = (RelativeLayout)this.findViewById(R.id.LPC_item01);
			RelativeLayout item2 = (RelativeLayout)this.findViewById(R.id.LPC_item02);
			RelativeLayout item3 = (RelativeLayout)this.findViewById(R.id.LPC_item3);
			LinearLayout item4 = (LinearLayout)this.findViewById(R.id.LPC_item4);
			
			if(isVisible){
				
				if(layout!=null){
					layout.setVisibility(View.VISIBLE);
				}
				
			}else{
				
				if(layout!=null){
					layout.setVisibility(View.GONE);
					item1.setVisibility(View.GONE);
					item2.setVisibility(View.GONE);
					item3.setVisibility(View.GONE);
					item4.setVisibility(View.GONE);
					lpc_manage_controlBT.setChecked(false);
				}
			}
			
		}
		
		/**
		 * check按钮点击后快速设置家长控制项是否显示
		 * @param isEnable
		 * @categorytegory
		 */
		private void setLCPManageEnable(boolean isEnable){
			
			RelativeLayout item1 = (RelativeLayout)this.findViewById(R.id.LPC_item01);
			RelativeLayout item2 = (RelativeLayout)this.findViewById(R.id.LPC_item02);
			RelativeLayout item3 = (RelativeLayout)this.findViewById(R.id.LPC_item3);
			LinearLayout item4 = (LinearLayout)this.findViewById(R.id.LPC_item4);
			
			if(isEnable){
				item1.setVisibility(View.VISIBLE);
				item2.setVisibility(View.VISIBLE);
				item3.setVisibility(View.VISIBLE);
				item4.setVisibility(View.VISIBLE);
				//lpc_manage_controlBT.setChecked(true);
			}else{
				item1.setVisibility(View.GONE);
				item2.setVisibility(View.GONE);
				item3.setVisibility(View.GONE);
				item4.setVisibility(View.GONE);
				//lpc_manage_controlBT.setChecked(false);
			}
			
		}
		
		/**
		 * 获得当前类实例
		 * @return
		 */
		public static GenieLPCmanage getLPCInstance(){
			
			return currentInstance;
			
		}

}
