package com.dragonflow;

import java.util.ArrayList;

import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;
import com.netgear.genie.media.dlna.DeviceDesc;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GenieDlnaRender extends Activity {


	
	private Button refresh = null;
	
	public ProgressDialog progressDialog = null;
	
	public ListView m_list = null;
	public EfficientAdapter m_listItemAdapter = null;
	public ArrayList<GenieDlnaRenderListItem> m_listdata = null;
	
	private Boolean m_create = false;
	
	private GenieDlnaDeviceInfo m_workrender = null;
	private int m_chooseIndex = -1;
	
	final public static int MESSAGE_REFRESHLIST = 100;
	
	private static boolean m_ispause = false;
	

	

	
	private Handler handler = new Handler()
	{   
        public void handleMessage(Message msg) 
        {   
        	String error = null;
        	GenieDebug.error("handleMessage", " GenieDlnaRender handleMessage msg.what = "+msg.what);
        	switch(msg.what)
        	{
        	case  MESSAGE_REFRESHLIST:
        		//RefreshRendererList();
        		break;	
        	case GenieDlnaStatus.VIEW_MEDIAVIDEOPLAY:
        		ToVideoPlay();
        		break;
        	case GenieDlnaStatus.VIEW_IMAGEPLAYER:
        		ToImagePlayer();
        		break;	
        	case GenieDlnaActionDefines.ACTION_RENDER_SETPLAYING:
        		if(null != GenieDlnaActionDefines.m_playurl)
        		{
        			if(GenieDlnaActionDefines.m_playStyle == 1 ||
        					GenieDlnaActionDefines.m_playStyle == 2)
        			{
        				ToVideoPlay();
        			}else if(GenieDlnaActionDefines.m_playStyle == 3)
        			{
        				ToImagePlayer();
        			}
        		}
        		break;	
        	case GenieDlnaActionDefines.ACTION_RET_RENDERLISTCHANGED:
        		RefreshRendererList();
        		break;
        	case GenieDlnaActionDefines.ACTION_RENDER_TOVIDEOPLAY:
        		ToVideoPlay();
        		break;
        	case GenieDlnaActionDefines.ACTION_RENDER_TOIMAGEPLAY:
        		ToImagePlayer();
        		break;		
        	}
        }
	}; 
		
	private int  SendMssageToImagePlayer()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_IMAGEPLAYER);
		return 0;
	}
	
	
	private void ToImagePlayer()
	{
		if(m_ispause)
			return ;
		
		GenieDebug.error("debug","dlnarender ToImagePlayer");
		
		Intent intent = new Intent();
		        	
        //intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GenieDlnaRender.this, GenieDlnaImagePlayer.class);
		startActivity(intent);
	}	
	
	private int  SendMssageToVideoPlay()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_MEDIAVIDEOPLAY);
		return 0;
	}
	
	private void ToVideoPlay()
	{
		if(m_ispause)
			return;
		GenieDebug.error("debug","dlnarender ToVideoPlay");
		
		Intent intent = new Intent();
		        	
        //intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GenieDlnaRender.this, GenieDlnaVideoPlay.class);
		startActivity(intent);
	}
	
	

    
	   @Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			//super.onBackPressed();
			   
				GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
				GenieDlnaTab.m_radio0.setChecked(true);
			
		}
	
	public void InitTitleView()
	{
    	if(GenieDlnaTab.m_about == null || GenieDlnaTab.m_back == null || GenieDlnaTab.m_dlnatitle == null)
    	{
    		return ;    		
    	}
    	GenieDlnaTab.m_dlnatitle.setText(R.string.rendertitle);
    	
    	GenieDlnaTab.m_about.setText(R.string.refresh);
    	GenieDlnaTab.m_about.setVisibility(View.VISIBLE);
    	
    	
    	GenieDlnaTab.m_back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//GenieDlnaRender.this.onBackPressed();
				
				GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
				GenieDlnaTab.m_radio0.setChecked(true);
			}
		});
    	
    	
    	
		
    	GenieDlnaTab.m_about.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				GenieDebug.error("debug", "m_about onClick 0");
				ScrollView nodevices = (ScrollView)findViewById(R.id.TV_norender);
				
				if(null != nodevices &&
						nodevices.getVisibility() == View.VISIBLE )
		    	{
					GenieDebug.error("debug", "m_about onClick 1");
					StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDERRESETANDBROWSE_REFRESH);
		    	
				}else
				{
					GenieDebug.error("debug", "m_about onClick 2");
					ListClear();
					RenderOnRefresh2();
				}
			}
		});
		
		GenieDlnaTab.m_back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //     
                           v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		

		
		GenieDlnaTab.m_about.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
	}
	
	
	public void sendbroad(int value)
	{
		GenieDebug.error("sendbroad", "GenieDlnaRender---- sendbroad value ="+value);
		Intent slide = new Intent(GenieGlobalDefines.DLNA_ACTION_BROADCAST);
		slide.putExtra(GenieGlobalDefines.DLNA_ACTION_RET, value);
		sendBroadcast(slide);
	}
	
	
	private void ShowChooseSourceDialog()
	{
		AlertDialog.Builder dialog_wifiset =  new AlertDialog.Builder(this);
		
		
		dialog_wifiset.setIcon(android.R.drawable.ic_dialog_alert);
		//dialog_wifiset.setTitle(" ");
		dialog_wifiset.setMessage(R.string.choosesource);
		dialog_wifiset.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
				GenieDlnaTab.m_radio0.setChecked(true);
			}
		});
		
		
		
		dialog_wifiset.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		
		
		
		dialog_wifiset.setCancelable(false);
		dialog_wifiset.show();
		
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//setTheme(R.style.listtextsize);
		
        //setTheme(R.style.activityTitlebarNoSearch);//qicheng.ai add        
        //requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    	setContentView(R.layout.renderview);
        
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        
    	GenieDlnaStatus.m_thisview = this;
    	
    	InitTitleView();
        
		
		m_create = true;
		m_ispause = true;
		
		m_list = (ListView)findViewById(R.id.ListView01);
		//refresh   = (Button)findViewById(R.id.refresh);
        
	    m_listdata = new  ArrayList<GenieDlnaRenderListItem>();
	    	        
	   // m_listItemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,m_listdata);
	   
	    m_listItemAdapter = new  EfficientAdapter(this);
	    m_list.setAdapter(m_listItemAdapter);
		
	    m_list.setItemsCanFocus(false);
	    m_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    m_list.setItemChecked(-1, true);
        
		//m_DlnaStatus = new GenieDlnaStatus();
	    //GenieDlnaStatus.InitList();
	   // initRenderFieldAndMethod();
		
		m_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				GenieDebug.error("onItemClick", "onItemClick arg3 = "+arg3);
				
				int size = GenieDlnaActionDefines.m_Rendererlist.size();
				
				if(arg3 >= 0 && arg3 < size)
				{
					int Index = (int)arg3;
					if(Index >=0 && Index < m_listdata.size())
					{	
						m_chooseIndex = Index;
						for(int i = 0; i < m_listdata.size();i++)
						{
							if(i == m_chooseIndex)
							{
								m_listdata.get(i).m_select = true;
							}else
							{
								m_listdata.get(i).m_select = false;
							}
						}
						m_listItemAdapter.notifyDataSetChanged();
					}
					GenieDlnaActionDefines.m_WorkRenderUUID = GenieDlnaActionDefines.m_Rendererlist.get((int)arg3).getUuid();
					GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID = GenieDlnaActionDefines.m_WorkRenderUUID.toString();
					GenieDebug.error("onItemClick", "GenieDlnaActionDefines.m_WorkRenderUUID = "+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
					
					StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_SAVECONFIG);
					sendbroad(GenieDlnaActionDefines.ACTION_RENDER_CHENGED);
					
					ShowChooseSourceDialog();
				}
			}
		});
		
		   m_list.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
				
				@Override
				public void onChildViewRemoved(View parent, View child) {
					// TODO Auto-generated method stub
					GenieDebug.error("debug","987654321  onChildViewRemoved"); 
					ShowNodevicesTV();
				}
				
				@Override
				public void onChildViewAdded(View parent, View child) {
					// TODO Auto-generated method stub
					ScrollView nodevices = (ScrollView)findViewById(R.id.TV_norender);
			    	nodevices.setVisibility(View.GONE);
				}
			});
		
		
		   
		   ShowNodevicesTV();
//		refresh.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				ListClear();
//				RenderOnRefresh2();
//			}
//		});
		
		//SetViewOnRender();
		
	}
	
	
    public void ShowNodevicesTV()
    {
    	
    	GenieDebug.error("debug","987654321  ShowNodevicesTV GenieDlnaActionDefines.m_datastack ="+GenieDlnaActionDefines.m_datastack);
    	//if(GenieDlnaActionDefines.m_datastack != null)
    	//	GenieDebug.error("debug","987654321  ShowNodevicesTV GenieDlnaActionDefines.m_datastack.size() ="+GenieDlnaActionDefines.m_datastack.size());
    	
    	
    		ScrollView nodevices = (ScrollView)findViewById(R.id.TV_norender);
	    	nodevices.setVisibility(View.GONE);
	    	
			handler.postDelayed(new Runnable() {
			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					CheckDevices();
				}
			}, 2500);
	
    }
    
    public void  CheckDevices()
    {
    	GenieDebug.error("debug","987654321  CheckDevices m_list = "+m_list);
    	//if(m_list != null)
    	//	GenieDebug.error("debug","987654321  CheckDevices m_list.getCount() = "+m_list.getCount());
    	
    	ScrollView nodevices = (ScrollView)findViewById(R.id.TV_norender);
    	if(m_list == null || m_list.getCount() <= 0)
    	{
    		GenieDebug.error("debug","987654321  CheckDevices nodevices.setVisibility(View.VISIBLE)");
    		nodevices.setVisibility(View.VISIBLE);
    	}
    	
    	if(m_list != null && m_list.getCount() > 0)
    	{
    		GenieDebug.error("debug","987654321  CheckDevices nodevices.setVisibility(View.GONE)");
    		nodevices.setVisibility(View.GONE);
    	}
    }
    
	
	private void StartDlnaAction(int action)
	{		

		GenieDebug.error("debug","9999df999999 StartDlnaAction action = "+action);
		
		Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
		Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,action);
		startService(Dlna);
		GenieDebug.error("debug","9999df999999 StartDlnaAction end");

	}
	
	private void RenderOnRefresh2()
	{
		
		StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_REFRESH);
		ShowNodevicesTV();
	}
	
	private void ListClear()
	{
		if( null == m_listdata || null == m_listItemAdapter )
    	{
    		return ;
    	}
    	

    		m_listdata.clear();    		

    		m_listItemAdapter.notifyDataSetChanged();
    		
    		

	}

	
	public  void RenderOnRefresh()
	{
		if(null == m_list || null == m_listdata || null == m_listItemAdapter 
				|| GenieDlnaActionDefines.m_Rendererlist == null)
    	{
    		return ;
    	}
    	
    	synchronized("RefreshRendererList")
		{
    		
    		m_listdata.clear();  
    		
    		
    		//m_workrender = GetWorkRender();
    		m_chooseIndex = -1;
    		int n = 0;
    		
    		for (DeviceDesc desc : GenieDlnaActionDefines.m_Rendererlist)
        	{
    			GenieDlnaRenderListItem temp = new GenieDlnaRenderListItem();
    			temp.m_FriendlyName = desc.getFriendlyName();
    			
    			if(desc.getIconCount() > 0)
        		{
        			temp.m_iconflag = true;
        			temp.m_icon = desc.getIcon(0).getIconData();
        		}else
        		{
        			temp.m_iconflag = false;
        		}
    			
    			
    			GenieDebug.error("debug","desc.getUuid() ="+desc.getUuid().toString());
    			if(null != GenieDlnaActionDefines.m_WorkRenderUUID)
    			{
    				
    				GenieDebug.error("debug","GenieDlnaActionDefines.m_WorkRenderUUID ="+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
    				if(desc.getUuid().equals(GenieDlnaActionDefines.m_WorkRenderUUID))
    				{
    					GenieDebug.error("debug","m_chooseIndex ="+n);
    					m_chooseIndex = n;
    					temp.m_select = true;
    				}else
    				{
    					temp.m_select = false;
    				}
    				
    			}else
    			{
    				temp.m_select = false;
    				GenieDebug.error("debug","null == m_workrender ");
    			}

        		m_listdata.add(temp);
        		n++;
        	}
    		
 
    		m_listItemAdapter.notifyDataSetChanged();
    		m_list.setItemChecked(m_chooseIndex, true);
    		//ShowNodevicesTV();
    		
		}

    	
    }

	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//SetViewOnRender();
		GenieDlnaStatus.m_thisview = this;
		
		GenieDebug.error("debug","----GenieDlnaRender- onResume------ ");
		if(m_create)
		{
			m_create = false;
		}else
		{
		//	initRenderFieldAndM ethod();
			
		}
		
		//StartDlnaAction(GenieDlnaActionDefines.ACTION_BROWSE_REFRESH);
		
		InitTitleView();
		RenderOnRefresh();
		m_ispause = false;
	}
	
	
	
	
	 public void RefreshRendererList()
	    {
		 GenieDebug.error("debug","----RefreshRendererList------ ");
	    	if(null == m_list || null == m_listdata || null == m_listItemAdapter )
	    	{
	    		return ;
	    	}
	    	
	    	synchronized("RefreshRendererList")
			{
	    		m_listdata.clear();  
	    		
	    		
	    		//m_workrender = GetWorkRender();
	    		m_chooseIndex = -1;
	    		int n = 0;
	    			
	    		for (DeviceDesc desc : GenieDlnaActionDefines.m_Rendererlist)
	        	{
	    			GenieDlnaRenderListItem temp = new GenieDlnaRenderListItem();
	    			temp.m_FriendlyName = desc.getFriendlyName();
	    			
	    			if(desc.getIconCount() > 0)
	        		{
	        			temp.m_iconflag = true;
	        			temp.m_icon = desc.getIcon(0).getIconData();
	        		}else
	        		{
	        			temp.m_iconflag = false;
	        		}
	    			
	    			
	    			GenieDebug.error("debug","desc.getUuid() ="+desc.getUuid().toString());
	    			if(null != GenieDlnaActionDefines.m_WorkRenderUUID)
	    			{
	    				
	    				GenieDebug.error("debug","GenieDlnaActionDefines.m_WorkRenderUUID ="+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
	    				if(desc.getUuid().equals(GenieDlnaActionDefines.m_WorkRenderUUID))
	    				{
	    					GenieDebug.error("debug","m_chooseIndex ="+n);
	    					m_chooseIndex = n;
	    					temp.m_select = true;
	    				}else
	    				{
	    					temp.m_select = false;
	    				}
	    				
	    			}else
	    			{
	    				temp.m_select = false;
	    				GenieDebug.error("debug","null == m_workrender ");
	    			}

	        		m_listdata.add(temp);

	        		n++;
	    		}
	    		
	    		m_listItemAdapter.notifyDataSetChanged();
	    		
	    		//GenieDebug.error("debug","m_chooseIndex =  "+m_chooseIndex);
	    		m_list.setItemChecked(m_chooseIndex, true);
	    		
	    		ShowNodevicesTV();
			}
	    	
	    	
	    	
	    	
	    }
	
    public void RefreshListItem()
    {
    	
    	
    	sendMessage2UI(MESSAGE_REFRESHLIST);
    }
	
    public void onMediaRendererAdded(GenieDlnaDeviceInfo device)
    {
    	synchronized("onMediaRendererAdded")
		{
    		GenieDebug.error("debug","-onMediaRendererAdded-m_deviceId = "+device.m_deviceId);
    		GenieDebug.error("debug","-onMediaRendererAdded-m_objectId = "+device.m_objectId);
    		GenieDebug.error("debug","-onMediaRendererAdded-m_title = "+device.m_title);
    		GenieDebug.error("debug","-onMediaRendererAdded-m_container = "+device.m_container);
    	
    	
    		GenieDlnaDeviceInfo temp = new GenieDlnaDeviceInfo();
    	
    		temp.m_container = device.m_container;
    		temp.m_deviceId = device.m_deviceId;
    		temp.m_objectId = device.m_objectId;
    		temp.m_title = device.m_title; 
    		temp.m_systemUpdateID = device.m_systemUpdateID;
    		
    		if(!GenieDlnaStatus.m_Rendererlist.contains(temp))
    		{
    			GenieDlnaStatus.m_Rendererlist.add(temp);
    		}    		
    		
    		RefreshListItem();
    		
		}
    	
    }
    public void onMediaRendererRemoved(GenieDlnaDeviceInfo device)
    {
    	GenieDebug.error("debug","-onMediaRendererRemoved-m_deviceId = "+device.m_deviceId);
    	synchronized("onMediaRendererRemoved")
		{
    		GenieDebug.error("debug","-onMediaRendererRemoved-m_deviceId = "+device.m_deviceId);
    		GenieDebug.error("debug","-onMediaRendererRemoved-m_objectId = "+device.m_objectId);
    		GenieDebug.error("debug","-onMediaRendererRemoved-m_title = "+device.m_title);
    		GenieDebug.error("debug","-onMediaRendererRemoved-m_container = "+device.m_container);
    	
    	
    		GenieDlnaDeviceInfo temp = new GenieDlnaDeviceInfo();
    	
    		temp.m_container = device.m_container;
    		temp.m_deviceId = device.m_deviceId;
    		temp.m_objectId = device.m_objectId;
    		temp.m_title = device.m_title;   
    		temp.m_systemUpdateID = device.m_systemUpdateID;
    		
    		if(GenieDlnaStatus.m_Rendererlist.contains(temp))
    		{
    			GenieDlnaStatus.m_Rendererlist.remove(temp);
    		}     		
    		
    		RefreshListItem();
    		
		}
    }
    
    
    
    
    public void refreshMediaRenderList(GenieDlnaDeviceInfo[] devicearrry,int all)
    {
    	
    	if(devicearrry.length <= 0)
    	{
    		GenieDebug.error("debug","refreshMediaRenderList devicearrry.length <= 0");
    		return ;
    	}
    	    	
    	
    	synchronized("refreshMediaRenderList")
		{
    		GenieDlnaStatus.m_Rendererlist.clear();
    		
    		for(int i = 0;i < devicearrry.length;i++)
        	{
        		GenieDebug.error("debug","m_title = "+devicearrry[i].m_title);
        		
        		GenieDlnaDeviceInfo device = new GenieDlnaDeviceInfo();
        		
        		device.m_container = devicearrry[i].m_container;
        		device.m_deviceId = devicearrry[i].m_deviceId;
        		device.m_objectId = devicearrry[i].m_objectId;
        		device.m_title = devicearrry[i].m_title;
        		
        		GenieDlnaStatus.m_Rendererlist.add(device);
        		

        		
        	}   
    		
    		RefreshListItem();
    		
    		
		}
    	
    }
	
	
	public void SetcurrentRenderStatus(int Status)
	{
		//m_currentRenderStatus = Status;
		GenieDlnaStatus.setcurrentRenderStatus(Status);
	}
	public int GetcurrentRenderStatus()
	{
		return GenieDlnaStatus.getcurrentRenderStatus();
	}
	
	 public void sendMessage2UI(int msg)
	    {
	    	
	    	handler.sendEmptyMessage(msg);
	    }
	
	 
	 @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		m_ispause = true;
	}
	 
	 
	 private void UnRegisterBroadcastReceiver()
	 {
		 if(null != m_DLNAReceiver)
			{
				unregisterReceiver(m_DLNAReceiver);
				m_DLNAReceiver = null;
			}
	 }

	 private void RegisterBroadcastReceiver()
	 {
		 UnRegisterBroadcastReceiver();
		 
			m_DLNAReceiver = new DLNAReceiver();
			IntentFilter filter = new IntentFilter();//閸掓稑缂揑ntentFilter鐎电钖�
			filter.addAction(GenieGlobalDefines.DLNA_ACTION_BROADCAST);
			registerReceiver(m_DLNAReceiver, filter);//濞夈劌鍞紹roadcast Receiver
	 }


	@Override
		protected void onStart() {
			// TODO Auto-generated method stub
	    	
			super.onStart();
						
			RegisterBroadcastReceiver();
		
		}
	 
	 
	    @Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			
			UnRegisterBroadcastReceiver();
			
			m_ispause = true;
			
	}
	 
	    private class DLNAReceiver extends BroadcastReceiver{//缂佈勫閼风嫙roadcastReceiver閻ㄥ嫬鐡欑猾锟�

	         @Override
		        public void onReceive(Context context, Intent intent) {//闁插秴鍟搊nReceive閺傝纭�
	     
	        	 int action = -1;
	     		
	     		action = intent.getIntExtra(GenieGlobalDefines.DLNA_ACTION_RET,-1);
	     		
	     		GenieDebug.error("DLNAReceiver", "GenieDlnaRender onReceive DLNA_ACTION_RET ="+action);
	     		
	     		//if(m_ispause)
	    		//	return;
	     		
	     		sendMessage2UI(action);
//	     		switch(action)
//	     		{
//	     		case GenieDlnaActionDefines.ACTION_RET_RENDERLISTCHANGED:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_RENDERLISTCHANGED);
//	     			break;
//	     		}
	     		

	         }
	       }
	    
	    private DLNAReceiver m_DLNAReceiver = null;
	    

    
		private  class EfficientAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;
	        private Bitmap mIcon1;
	        private Bitmap mIcon2;
	        private Bitmap mIcon0;
	        
	        private Bitmap file_video;
	        private Bitmap file_audio;
	        private Bitmap file_image;

	        public EfficientAdapter(Context context) {
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);

	            // Icons bound to the rows.
	            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.renderer);
	        }

	        /**
	         * The number of items in the list is determined by the number of speeches
	         * in our array.
	         *
	         * @see android.widget.ListAdapter#getCount()
	         */
	        public int getCount() {
	        	return m_listdata.size();
	            //return m_listdata.length;
	        }

	        /**
	         * Since the data comes from an array, just returning the index is
	         * sufficent to get at the data. If we were using a more complex data
	         * structure, we would return whatever object represents one row in the
	         * list.
	         *
	         * @see android.widget.ListAdapter#getItem(int)
	         */
	        public Object getItem(int position) {
	            return position;
	            //return m_listdata.get(position);
	        }

	        /**
	         * Use the array index as a unique id.
	         *
	         * @see android.widget.ListAdapter#getItemId(int)
	         */
	        public long getItemId(int position) {
	            return position;
	        }
	        
	       

	        /**
	         * Make a view to hold each row.
	         *
	         * @see android.widget.ListAdapter#getView(int, android.view.View,
	         *      android.view.ViewGroup)
	         */
	        public View getView(int position, View convertView, ViewGroup parent) {
	            // A ViewHolder keeps references to children views to avoid unneccessary calls
	            // to findViewById() on each row.
	            ViewHolder holder;

	            // When convertView is not null, we can reuse it directly, there is no need
	            // to reinflate it. We only inflate a new View when the convertView supplied
	            // by ListView is null.
	            if (convertView == null) {
	                convertView = mInflater.inflate(R.layout.list_item_icon_text_radio, null);

	                // Creates a ViewHolder and store references to the two children views
	                // we want to bind data to.
	                holder = new ViewHolder();
	                holder.text = (TextView) convertView.findViewById(R.id.text);
	                holder.icon = (ImageView) convertView.findViewById(R.id.rendericon);
	                holder.select = (ImageView)convertView.findViewById(R.id.iconselect);

	                convertView.setTag(holder);
	            } else {
	                // Get the ViewHolder back to get fast access to the TextView
	                // and the ImageView.
	                holder = (ViewHolder) convertView.getTag();
	            }

	            // Bind the data efficiently with the holder.
	            holder.text.setText(m_listdata.get(position).m_FriendlyName);
	            if(m_listdata.get(position).m_select)
	            {
	            	holder.select.setVisibility(View.VISIBLE);
	            }else
	            {
	            	holder.select.setVisibility(View.GONE);
	            }
	            
	            
	            if(m_listdata.get(position).m_iconflag)
	        	{
	        		if(m_listdata.get(position).m_icon.length > 0)
	        		{
	        			BitmapFactory.Options opt = new BitmapFactory.Options(); 
	        		            	    			
	            		opt.inPreferredConfig = Bitmap.Config.RGB_565;
	            		opt.inJustDecodeBounds = true;
	            		BitmapFactory.decodeByteArray(m_listdata.get(position).m_icon, 0, m_listdata.get(position).m_icon.length, opt);
	            		opt.inJustDecodeBounds = false;
	            		//Log.d(tag, "phSize: " + phSize + " pix_scale: " + PIX_SCALE);
	            		if (opt.outWidth > opt.outHeight) {
	            			opt.inSampleSize = opt.outWidth / 40;;
	            		} else {
	            			opt.inSampleSize = opt.outHeight / 40;
	            		}
	            		Bitmap b = 	BitmapFactory.decodeByteArray(m_listdata.get(position).m_icon, 0, m_listdata.get(position).m_icon.length, opt);
	            		  

	            		if(b != null)
	            		{
	            			holder.icon.setImageBitmap(b);
	            		}else{
	            			holder.icon.setImageBitmap(mIcon1);
	            		}	            		
	            		
	        		}else
	        		{
	        			holder.icon.setImageBitmap(mIcon1);
	        		}
	        		
	        	}else
	        	{
	        		holder.icon.setImageBitmap(mIcon1);
	        	}
	          

	            return convertView;
	        }

	        class ViewHolder {
	            TextView text;
	            ImageView icon;
	            ImageView select;
	        }
	    }

}
