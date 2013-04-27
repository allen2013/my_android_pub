package com.dragonflow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dragonflow.genie.ui.R; 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewm.GetImageThread;
import com.ewm.ImagesBrowseActivity;
import com.netgear.genie.media.dlna.DeviceDesc;

public class GneieDlna extends Activity implements GenieDlnaService.ProgressBrowseLoadingCallback {
	
	private WifiManager wifiManager;//
	private WifiInfo wifiInfo;//
	private String m_path ;
	private String m_servername ;
	private String m_rendername ;
	private String m_server ;
	//public GenieDlnaStatus m_DlnaStatus = null;
	
	private Button back = null;
	private Button refresh = null;
	private Boolean m_create = false;
	
	private int  m_chooseindex = -1;
	
	private LinearLayout m_toplevel = null;
	
	public ProgressDialog progressDialog = null;
	
	public BrowseProgressDialog m_BrowseprogressDialog = null;
	public PlayItemProgressDialog m_PlayItemProgressDialog = null;
	
	
	private Dialog m_RenderDialog = null;
	
	public String m_ip = null;
	
	private int m_geticonindex = 0;
	
	private Context mContext;
	
	//private HashMap<String,String> map = null;

	
	public ListView m_list = null;
	//public ArrayAdapter m_listItemAdapter = null;
	public EfficientAdapter m_listItemAdapter = null;
	
	public static ArrayList<GenieDlnaDeviceInfo> m_listdata = new ArrayList<GenieDlnaDeviceInfo>();

	final public static int MESSAGE_REFRESHLIST = 100;
	final public static int MESSAGE_FAILED = 501;
	
	final public static int MESSAGE_REFRESHLIST_DIR = 101;
	final public static int MESSAGE_SHOWWAITDIALOG = 502;
	final public static int MESSAGE_CLOSEWAITDIALOG = 503;
	final public static int MESSAGE_SHOWRENDERDIALOG = 504;
	
	final public static int MESSAGE_PLAYFAIL = 505;
	
	public int m_ListfirstVisibleItem = -1;
	

		
	private static boolean  m_backflag = false;
	
	private static boolean  m_pause =  false;
	
	private TextView m_servertitler = null;
	private TextView m_totalitem = null;
	private ProgressBar m_loadingprogress = null;
	
	//add 2012-11-3
	public static GneieDlna genieDlna=null;
	public boolean image_contorl=false;
	public boolean sendflag=false;
	public static boolean isback=false;
	private static boolean isCancelThread=false;
	
//	图片缓存集合
	public static Map<String,Bitmap> bitmap_Cache =new HashMap<String,Bitmap>();
	public boolean sroll_flag=false;
	public int frist_image=0;
	public int end_image=30;
	public boolean isGridView= false;
	public GridView m_gridlist=null;
	public  int image_width=80;
	public int  display_width=320;
	public int number_image=3;
	public native void  reloadDir(String m_deviceId,String str);
	
	private Handler handler = new Handler()
	{   
        public void handleMessage(Message msg) 
        {  
        	String error = null;
        	GenieDebug.error("handleMessage", "GneieDlna handleMessage msg.what = "+msg.what);
        	
        	if(m_backflag)
        	{
        		return ;
        	}
        	switch(msg.what)
        	{
        	case MESSAGE_FAILED:        		
        		break;
        	case  MESSAGE_REFRESHLIST:
        		//RefreshSaverList();
        		break;
        	case  MESSAGE_REFRESHLIST_DIR:        		
        		//RefreshSaverDirList();
        		break;	        	
        	case MESSAGE_SHOWRENDERDIALOG:
        		//ShowRenderDialog();
        		break;
        	case MESSAGE_SHOWWAITDIALOG:
        		//showWaitingDialog2();
        		return ;
        	case MESSAGE_CLOSEWAITDIALOG:
        		//closeWaitingDilalog2();
        		return ;
        	case MESSAGE_PLAYFAIL:
        		//OnPlayFailed();
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
        	case GenieDlnaActionDefines.ACTION_RENDER_TOVIDEOPLAY:
        		ToVideoPlay();
        		break;
        	case GenieDlnaActionDefines.ACTION_RENDER_TOIMAGEPLAY:
        		ToImagePlayer();
        		break;		
        	case GenieDlnaActionDefines.ACTION_RET_SERVERLISTCHANGED:
        		RefreshSaverList();
        		break;
        	case GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_START:        	
        	//case GenieDlnaActionDefines.ACTION_RET_BROWSE_ITEM:
        		RefreshSaverDirList();
        		break;
        	case GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_PROGRESS:	
        		//RefreshSaverDirListEx();
        		break;
        	case GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_FINISHED:
        		SetLoadingItemIndex();
        		GetIconOnThread();
        		break;
        	case GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_SHOW:
     			ShowBrowseProgressDialog();
     			break;
     		case GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE:
     			CancleBrowseProgressDialog();
     			break;	
     		case GenieDlnaActionDefines.ACTION_RET_BROWSE_CHOOSERENDER:
     			ShowRenderDialog();
     			break;
     		case GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW:
     			ToPlayView();
     			break;
     		case GenieDlnaActionDefines.ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_SHOW:
     			ShowPlayItemProgressDialog();
     			break;
     		case GenieDlnaActionDefines.ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_CANCLE:
     			CanclePlayItemProgressDialog();
     			break;		
        		
        	}
        	
        	
        	
        	
        	//closeWaitingDialog();
        }
	}; 
	
	
	
	
	public int GetBackFlag()
	{
		if(m_backflag)
		{
			return 1;
		}else
		{
			return 0;
		}
	}
	
	
	private void OnPlayFailed()
	{
		Toast toast = Toast.makeText(this,
			 "error", Toast.LENGTH_LONG);
	   	   toast.setGravity(Gravity.CENTER, 0, 0);
	   	   toast.show();
	}
	
	public void PlayFailed()
	{
		sendMessage2UI(MESSAGE_PLAYFAIL);
	}


	private int  SendMssageToImagePlayer()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_IMAGEPLAYER);
		return 0;
	}
	
	
	private void ToImagePlayer()
	{
//		if(GenieDlnaStatus.m_OnSlidePlay)
//		{
//			return ;
//		}
		if(m_pause)
			return;
		
		GenieDebug.error("debug","dlna ToImagePlayer");
		CanclePlayItemProgressDialog();
		CancleBrowseProgressDialog();
//		Intent imagebrowes=new Intent(this,ImageBrowseActivity.class);
//		startActivity(imagebrowes);
		if(sendflag||image_contorl){
			image_contorl=false;
			sendflag=false;
			return;
		}
		Intent intent = new Intent();
		//intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GneieDlna.this, GenieDlnaImagePlayer.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	
	private int  SendMssageToVideoPlay()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_MEDIAVIDEOPLAY);
		return 0;
	}
	
	
	private void ToVideoPlay()
	{
		GenieDebug.error("debug","dlna ToVideoPlay");
		
		if(m_pause)
			return;
		
		CanclePlayItemProgressDialog();
		CancleBrowseProgressDialog();
		
		Intent intent = new Intent();
		        	
        //intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GneieDlna.this, GenieDlnaVideoPlay.class);
		startActivity(intent);
	}
	
	
	   @Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			//super.onBackPressed();
		
		   GenieDebug.error("debug","456 onBackPressed");
		   sroll_flag=true;
		   OnBack();
			
		}
	   
//	    @Override
//	    public boolean onKeyUp(int keyCode, KeyEvent event){
//	    	
//	    	GenieDebug.error("debug","456 onBackPressed keyCode = "+keyCode);
//	    	
//	    	switch (keyCode) {
//	    	case KeyEvent.KEYCODE_BACK: {
//	    			OnBack();
//		    		return true;
//		    	}
//	    	case KeyEvent.KEYCODE_SEARCH:
//	    		//fileViewGrid.setDragable(true);
//	    		return true;
//	    	}
//	    	return super.onKeyDown(keyCode, event);
//	    }
	   
	   
	   public void OnBack()
	   {
		   isGridView=false;
		   updateLayout();
		   GenieDebug.error("debug","GenieDlna 界面返回");
			if(GenieDlnaActionDefines.m_datastack.isEmpty())
			{
				//showWaitingDialog();
				
				//reloadDir(GenieDlnaStatus.m_Serverlist.get((int)arg3).m_deviceId,"test");
				//GenieDebug.error("onClick", "reloadDir end 1");
				
				m_backflag = true;
				isback=true;
				GneieDlna.this.finish();
				
				clearFileCache();
			}else 
			{
				//showWaitingDialog();
				isback=true;
				goback();
				//GenieDebug.error("onClick", "reloadDir end 2");
			}
			
	   }
	   
	   
	
	public void InitTitleView()
	{
    	if(GenieDlnaTab.m_about == null || GenieDlnaTab.m_back == null || GenieDlnaTab.m_dlnatitle == null)
    	{
    		return ;    		
    	}
    	GenieDlnaTab.m_dlnatitle.setText(R.string.servertiler);
    	
    	GenieDlnaTab.m_about.setText(R.string.refresh);
    	GenieDlnaTab.m_about.setVisibility(View.VISIBLE);
    	
    	
    	
    	
    	GenieDlnaTab.m_back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				OnBack();
				
			}
		});
		
    	GenieDlnaTab.m_about.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				GenieDebug.error("debug", "m_about onClick 0");
				ScrollView nodevices = (ScrollView)findViewById(R.id.TV_nodevices);
				
				if(null != nodevices &&
						nodevices.getVisibility() == View.VISIBLE )
		    	{
					GenieDebug.error("debug", "m_about onClick 1");
					
					StartDlnaAction(GenieDlnaActionDefines.ACTION_SERVERRESETANDBROWSE_REFRESH);
		    	}else
		    	{
		    		GenieDebug.error("debug", "m_about onClick 2");
		    		ListClear();
					ServerOnRefresh2();
		    	}
		    	
			}
		});

		
		GenieDlnaTab.m_back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                           v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		

		
		GenieDlnaTab.m_about.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
	}
	
	
	
	
	
	public void CancelSlidePlay()
	{
		Intent slide = new Intent("com.netgear.genie.GenieSlideService");
		stopService(slide);
	}
	
	
	private void StartDlnaAction(int action)
	{		
//		if(action != GenieDlnaActionDefines.ACTION_BROWSE_ITEM)
			CancelIconThread();

//		GenieDebug.error("debug","9999df999999 StartDlnaAction action = "+action);
		
		Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
		Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,action);
		startService(Dlna);
//		GenieDebug.error("debug","9999df999999 StartDlnaAction end");

	}
	
	private void ServerOnRefresh2()
	{
		ShowNodevicesTV();
    	
		StartDlnaAction(GenieDlnaActionDefines.ACTION_BROWSE_REFRESH);
	}
	
	private void goback()
	{
		//主线程先睡眠等待取图片线程执行结束
		CancelIconThread();
//		CancelIconThread_one();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//先清理再发命令
		ClearListData();
		isback=false;
		System.out.println("clear 完成");
		StartDlnaAction(GenieDlnaActionDefines.ACTION_BROWSE_GOBACK);
	}
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setTheme(R.style.listtextsize);
        
        //setTheme(R.style.activityTitlebarNoSearch);//qicheng.ai add        
        //requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    	setContentView(R.layout.dlnamain);
        
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        
    	//getWindow().setBackgroundDrawableResource(android.R.color.background_light);
    	
    	GenieDlnaStatus.m_thisview = this;
    	
    	InitTitleView();
    	m_chooseindex = -1;
    	
    	m_servertitler = (TextView)findViewById(R.id.servername);
    	m_totalitem = (TextView)findViewById(R.id.totalitem);
    	m_loadingprogress = (ProgressBar)findViewById(R.id.loadingprogressBar);	
    	
    	m_toplevel = (LinearLayout)findViewById(R.id.toplevel);
    	m_toplevel.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				OnBack();
			}
		});
    	m_toplevel.setVisibility(View.GONE);
    	
		
    	m_toplevel.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                    	
                    	m_toplevel.setBackgroundColor(getResources().getColor(R.color.limegreen));
                           //v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //v.setBackgroundResource(R.drawable.title_bt_bj);
                    	//m_toplevel.setBackgroundResource(R.drawable.bj);
                    	//m_toplevel.setBackgroundColor(R.color.Black);
                    	v.setBackgroundDrawable(null);
                    	
                    }      
                    return false;      
            }      
		});
    	

        
        this.wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);// 锟斤拷取Wifi锟斤拷锟斤拷      
		// 锟矫碉拷Wifi锟斤拷息       
		this.wifiInfo = wifiManager.getConnectionInfo();
		
		m_create = true;
		
		m_backflag = false;
		
		m_pause =  true;		
		
		m_ip = ipIntToString(wifiInfo.getIpAddress());
		
		GenieDebug.error("debug","----------ip = "+m_ip);
		
		
		m_list = (ListView)findViewById(R.id.ListView01);
		//back   = (Button)findViewById(R.id.goback);
		//refresh   = (Button)findViewById(R.id.refresh);
        
	    m_listdata = new  ArrayList<GenieDlnaDeviceInfo>();
	   
	    	        
	   // m_listItemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,m_listdata);
	   
	    m_listItemAdapter = new  EfficientAdapter(this);
	    m_list.setAdapter(m_listItemAdapter);
		
		//m_DlnaStatus = new GenieDlnaStatus();
	    //GenieDlnaStatus.InitList();
		//initFieldAndMethod();
	    
	    
		
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
				ScrollView nodevices = (ScrollView)findViewById(R.id.TV_nodevices);
		    	nodevices.setVisibility(View.GONE);
			}
		});
		
		m_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(m_backflag){
					return;
				}
				GenieDebug.error("onItemClick", "onItemClick arg3 = "+arg3);
				image_contorl=true;
				GenieDlnaActionDefines.m_BrowseItemId = (int)arg3;
				
				StartDlnaAction(GenieDlnaActionDefines.ACTION_BROWSE_ITEM);

			}
			
		});
		
		m_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				//GenieDebug.error("debug", "ListView onScrollStateChanged scrollState = "+scrollState);
				
				if(scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
				{
			    	CancelIconThread();
			    	return ;
				}
				    
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
				{
					GetIconOnThread(); 
					return ;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				//GenieDebug.error("debug", "ListView onScroll firstVisibleItem ="+firstVisibleItem);
				//GenieDebug.error("debug", "ListView onScroll");
			}
		});
		
		
		//添加Gridview 布局
		
		m_gridlist=(GridView) this.findViewById(R.id.GridView01);
		m_gridlist.setAdapter(m_listItemAdapter);
		m_gridlist.setVisibility(View.GONE);
		
	    display_width=getResources().getDisplayMetrics().widthPixels;
	    image_width=160;
	    if((display_width/image_width)>5){
	    	image_width=display_width/5;
	    	number_image=5;
	    }else{
	    	
	    	number_image=display_width/image_width;
	    }
	    
		m_gridlist.setNumColumns(number_image);//动态设置列数
		
				
				m_gridlist.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
						
						@Override
						public void onChildViewRemoved(View parent, View child) {
							// TODO Auto-generated method stub
							GenieDebug.error("debug","987654321  onChildViewRemoved"); 
							ShowNodevicesTV();
						}
						
						@Override
						public void onChildViewAdded(View parent, View child) {
							// TODO Auto-generated method stub
							ScrollView nodevices = (ScrollView)findViewById(R.id.TV_nodevices);
					    	nodevices.setVisibility(View.GONE);
						}
					});
					
				m_gridlist.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
							if(m_backflag){
								return;
							}
							
							GenieDebug.error("onItemClick", "onItemClick arg3 = "+arg3);
							image_contorl=true;
							GenieDlnaActionDefines.m_BrowseItemId = (int)arg3;
							
							StartDlnaAction(GenieDlnaActionDefines.ACTION_BROWSE_ITEM);

						}
						
					});
					
					
					
				m_gridlist.setOnScrollListener(new OnScrollListener() {
						
						@Override    
						public void onScrollStateChanged(AbsListView view, int scrollState) {
							// TODO Auto-generated method stub
							
							if(scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
							{
						    	CancelIconThread();
//						    	CancelIconThread_one();
						    	return ;
							}
							
							if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
							{
//								CancelIconThread();
								GetIconOnThread();
//								GetIconOnThread_one(frist_image, end_image);
								return ;
							}
							
						}
						
						@Override
						public void onScroll(AbsListView view, int firstVisibleItem,
								int visibleItemCount, int totalItemCount) {
//								System.out.println("firstVisibleItem--"+firstVisibleItem+" visibleItemCount -- "+visibleItemCount+" totalItemCount  --  "+totalItemCount);
//								lastItem = firstVisibleItem + visibleItemCount-1;
								frist_image=firstVisibleItem;
								end_image=firstVisibleItem + visibleItemCount;
							}
						
						});
				
		
		GenieDlnaService.SetProgressBrowseLoadingCallback(this);
		
		
//		refresh.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				//showWaitingDialog();
//				
//				ListClear();
//				ServerOnRefresh2();
//			}
//		});
//		
//		back.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				//showWaitingDialog();
//				goback();
//			}
//		});
		int length = 0;
		byte[] buffer = new byte[32];
		SetcurrentServerStatus(1);
				
		//if((length = getExternalStoragePath(buffer, 32)) > 0)
		//{
		
			mContext = this;
			GenieDlnaStatus.map = GenieSerializ.ReadMap(mContext, GenieDlnaStatus.DLNASTATUSFILENAME);
			if(GenieDlnaStatus.map == null)
			{
				GenieDlnaStatus.map = new HashMap<String,String>();
				
				GenieDlnaStatus.m_ServerGuid = "DFG";//GetGuid();
				
				GenieDebug.error("onItemClick", "map == null ServerGuid = "+GenieDlnaStatus.m_ServerGuid);
				
				GenieDlnaStatus.map.put(GenieDlnaStatus.SERVERGUID, GenieDlnaStatus.m_ServerGuid);
				
				GenieDlnaStatus.m_RenderGuid = "DFG"; //GetGuid();
				
				GenieDebug.error("onItemClick", "map == null RenderGuid = "+GenieDlnaStatus.m_RenderGuid);
				
				GenieDlnaStatus.map.put(GenieDlnaStatus.RENDERGUID, GenieDlnaStatus.m_RenderGuid);				
				
				GenieDlnaStatus.m_SharePath = Environment.getExternalStorageDirectory().getAbsolutePath();
				
				GenieDebug.error("onItemClick", "map == null m_SharePath = "+GenieDlnaStatus.m_SharePath);
				
				GenieDlnaStatus.map.put(GenieDlnaStatus.SHAREPATH, GenieDlnaStatus.m_SharePath);
				
				GenieDlnaStatus.m_chooserguid = "null";				
				
				GenieDlnaStatus.map.put(GenieDlnaStatus.SAVEGUID, GenieDlnaStatus.m_chooserguid);
				
				
				
				GenieSerializ.WriteMap(mContext, GenieDlnaStatus.map, GenieDlnaStatus.DLNASTATUSFILENAME);
				
				//SetDlnaGuid(ServerGuid,RenderGuid);
				//312
				
				
			}
			else
			{
				GenieDlnaStatus.m_ServerGuid = GenieDlnaStatus.map.get(GenieDlnaStatus.SERVERGUID);
				GenieDlnaStatus.m_RenderGuid = GenieDlnaStatus.map.get(GenieDlnaStatus.RENDERGUID);
				GenieDlnaStatus.m_SharePath = GenieDlnaStatus.map.get(GenieDlnaStatus.SHAREPATH);
				
				GenieDlnaStatus.m_chooserguid = GenieDlnaStatus.map.get(GenieDlnaStatus.SAVEGUID);
				
				GenieDebug.error("onItemClick", "map != null ServerGuid = "+GenieDlnaStatus.m_ServerGuid);
				GenieDebug.error("onItemClick", "map != null RenderGuid = "+GenieDlnaStatus.m_RenderGuid);
				

				//SetDlnaGuid(ServerGuid,RenderGuid);
				
			}			
			//m_path = new String(buffer, 0, length);
			
			//m_path = Environment.getExternalStorageDirectory().getAbsolutePath();
			
			GenieDebug.error("debug","-GenieDlnaStatus.m_chooserguid = "+GenieDlnaStatus.m_chooserguid);
			
			
			m_path = GenieDlnaStatus.m_SharePath;
			
			//m_path = m_path+"/download";
						
			GenieDebug.error("debug","----------path = "+m_path);
			
			Build bd = new Build();
			//m_server = bd.MODEL+bd.PRODUCT;
			m_server = bd.MODEL+":"+"Media Server"+":"+"("+m_ip+")";
			GenieDebug.error("debug","----------m_server = "+m_server);
			
			GenieDlnaStatus.m_Servername = m_server;
			
			m_servername = "ReadySHARE";
			GenieDlnaStatus.m_Sharename = m_servername;
			//m_server = "android device";
			
			m_rendername = bd.MODEL+":"+"Media Renderer"+":"+"("+m_ip+")";
			
			GenieDlnaStatus.m_Rendername = m_rendername;
			
			//StartServer(m_path,m_rendername,m_server,GenieDlnaStatus.m_ServerGuid,GenieDlnaStatus.m_RenderGuid);		
//			StartServer(temp,temp,temp,temp,temp);		
			
			//SetDlnaGuid(GenieDlnaStatus.m_chooserguid);
			
			 new Thread()
			   {
				   public void run() 
			    	{   
			    		GenieDebug.error("debug", " StartServer --run--");
			    		
			    		StartDlnaAction(GenieDlnaActionDefines.ACTION_START);
			    	}
			    }.start();  
			
		//}
		
		//SetViewOnServer();
		GenieDebug.error("debug","-----onCreate end------ ");
       
		
		ShowNodevicesTV();
		
		
		genieDlna=this;
    }
//    static{
//    	System.loadLibrary("genieupnp");
//    }
    
    
    public void ShowNodevicesTV()
    {
    	
    	GenieDebug.error("debug","987654321  ShowNodevicesTV GenieDlnaActionDefines.m_datastack ="+GenieDlnaActionDefines.m_datastack);
    	//if(GenieDlnaActionDefines.m_datastack != null)
    	//	GenieDebug.error("debug","987654321  ShowNodevicesTV GenieDlnaActionDefines.m_datastack.size() ="+GenieDlnaActionDefines.m_datastack.size());
    	if((null != m_toplevel &&
			m_toplevel.getVisibility() == View.GONE)&&(GenieDlnaActionDefines.m_datastack == null
				|| GenieDlnaActionDefines.m_datastack.size() <= 0))
		{
    		ScrollView nodevices = (ScrollView)findViewById(R.id.TV_nodevices);
	    	nodevices.setVisibility(View.GONE);
	    	
			handler.postDelayed(new Runnable() {
			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					CheckDevices();
				}
			}, 2500);
		}else
		{
			ScrollView nodevices = (ScrollView)findViewById(R.id.TV_nodevices);
	    	nodevices.setVisibility(View.GONE);
		}
    }
    
    public void  CheckDevices()
    {
    	GenieDebug.error("debug","987654321  CheckDevices m_list = "+m_list);
    	//if(m_list != null)
    	//	GenieDebug.error("debug","987654321  CheckDevices m_list.getCount() = "+m_list.getCount());
    	
    	
    	
    	ScrollView nodevices = (ScrollView)findViewById(R.id.TV_nodevices);
    	
    	if(null != m_toplevel &&
    			m_toplevel.getVisibility() == View.VISIBLE)
    	{
    		nodevices.setVisibility(View.GONE);
    		return ;
    	}
    	
    	if(m_list == null || m_list.getCount() <= 0)
    	{
    		GenieDebug.error("debug","987654321  CheckDevices nodevices.setVisibility(View.VISIBLE)");
    		nodevices.setVisibility(View.VISIBLE);
    		return ;
    	}
    	
    	if(m_list != null && m_list.getCount() > 0)
    	{
    		GenieDebug.error("debug","987654321  CheckDevices nodevices.setVisibility(View.GONE)");
    		nodevices.setVisibility(View.GONE);
    		return ;
    	}
    }
    
    

    
    
	private void ListClear()
    {
    	if(GenieDlnaStatus.m_Serverlist == null || null == m_listdata || null == m_listItemAdapter )
    	{
    		return ;
    	}
    	
    	ClearListData();    		
		//GenieDlnaStatus.m_Serverlist.clear();
    
    	isback=false;
		m_listItemAdapter.notifyDataSetChanged();
    }
    
    
	 @Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			m_pause =  true;
			
			GenieDlnaService.SetProgressBrowseLoadingCallback(null);
		}
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		GenieDebug.error("debug","----GneieDlna- onResume------ ");
		GenieDlnaStatus.m_thisview = this;
		
		
		
		if(m_create)
		{
			m_create = false;
		}else
		{
			//initFieldAndMethod();
			//showWaitingDialog();
			
//			if(GenieDlnaActionDefines.m_listdatabackup != null 
//					&& GenieDlnaActionDefines.m_datastack != null 
//					&& GenieDlnaActionDefines.m_datastack.size() > 0)
//			{
//				PushListData(GenieDlnaActionDefines.m_listdatabackup,false);
//			}
		}
		
		m_pause =  false;
		
		GenieDlnaService.SetProgressBrowseLoadingCallback(this);
		
		InitTitleView();
		
		//StartDlnaAction(GenieDlnaActionDefines.ACTION_BROWSE_REFRESH);
		
		//SetViewOnServer();
		//GenieDlnaStatus.setcurrentview(GenieDlnaStatus.VIEW_MEDIASERVER);
		
		//ShowNodevicesTV();
		//ServerOnRefresh();
		
		re_m_listItemAdapter();
		
		
	}
	
	
	
	private void showWaitingDialog2()
	{
		if(m_backflag)
		{
			return ;
		}
		closeWaitingDilalog2();
		
		progressDialog = ProgressDialog.show(GneieDlna.this, getResources().getString(R.string.loading)+"...", getResources().getString(R.string.wait), true, true);   
	}
	 
	private void showWaitingDialog()
    {
		if(m_backflag)
		{
			return ;
		}
		sendMessage2UI(MESSAGE_SHOWWAITDIALOG);
		return ;
		
	}
	
	
	private void closeWaitingDilalog2()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}   
	}
	
	private void closeWaitingDialog()
    {
		if(m_backflag)
		{
			return ;
		}
		sendMessage2UI(MESSAGE_CLOSEWAITDIALOG);

    }
	
	
	

    public void RefreshSaverList()
    {
    	if(null == m_list || null == m_listdata || null == m_listItemAdapter ||
    			GenieDlnaActionDefines.m_Serverlist == null)
    	{
    		return ;
    	}
    	
		if(m_backflag)
		{
			return ;
		}
    	
    	synchronized("RefreshSaverList")
		{
    		ClearListData();  	
    		
    		GenieDlnaActionDefines.m_playItem = null;
    		
    		for (DeviceDesc desc : GenieDlnaActionDefines.m_Serverlist)
        	{ 
        			GenieDlnaDeviceInfo device = new GenieDlnaDeviceInfo();
            		
            		device.m_container = 0;

            		device.m_deviceId = desc.getUuid().toString();

            		device.m_objectId = desc.getFriendlyName();
            		device.m_title = desc.getFriendlyName();
            		device.m_filestyle = 0;   
            		
            		if(desc.getIconCount() > 0)
            		{
            			device.m_iconflag = 1;
            			device.m_icon = desc.getIcon(0).getIconData();
            		}else
            		{
            			device.m_iconflag = 0;
            		}
            		
            		m_listdata.add(device);
        	}
    		
    		m_listItemAdapter.notifyDataSetChanged();
    		
    		if(null != m_toplevel)
    			m_toplevel.setVisibility(View.GONE);
    		
    		
    		ShowNodevicesTV();
		}
    	
    	
    	
    }
    
    
    public Thread m_IconThread = null;
    public Thread m_IconThread1=null;
    public void CancelIconThread()
    {
    	if(m_IconThread != null)
    	{
    		//设置取消线程
    		isCancelThread=true;
    		if(m_IconThread.isAlive()){
    			m_IconThread.interrupt();
    		}else{
    			isCancelThread=false;
    		}
    		
    	}
    	
    }
    
    
//    public void CancelIconThread_one(){
//    	sroll_flag=true;
//    	if(m_IconThread1 != null)
//    	{
//    		//设置取消线程
//    		isCancelThread=true;
//    		if(m_IconThread1.isAlive()){
//    			m_IconThread1.interrupt();
//    		}else{
//    			sroll_flag=false;
//    		}
//    		
//    	}
//    	
//    	
//    }
    
    
    private byte[] getBytes(InputStream is) throws Exception {
    	if(is==null)return null;
    	ByteArrayOutputStream baos=null;
    	try{
	    	baos =new ByteArrayOutputStream();
	    	byte[] b =new byte[1024];
	    	int len =0;
	
	    	while ((len = is.read(b, 0, 1024)) !=-1) 
	    	{
	    	baos.write(b, 0, len);
	    	baos.flush();
	    	}
	    	byte[] bytes = baos.toByteArray();
	    	
	    	baos.reset();
	    	
	    	baos.close();
	    	
	    	baos = null;
	    	b = null;
	    	
	    	return bytes;
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		
    		return null;
    	}finally{
    		if(baos!=null){
    			baos.close();
    		}
    		if(is!=null){
    			is.close();
    		}
    	}
    }
    
    public void GetIconOnThread()
    {
    	CancelIconThread();
    	
    	m_ListfirstVisibleItem = m_list.getFirstVisiblePosition();
    	
    	GenieDebug.error("debug","m_ListfirstVisibleItem = "+m_ListfirstVisibleItem);
    	
    	//sfd
    	
    	m_IconThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					
					if(isback){
						return;
					}
				
					Boolean flag = true;
					int index = 0;
					int n = -1;
					int size = m_listdata.size();
					
					m_geticonindex = 0;
					
					if(m_ListfirstVisibleItem < 0 || m_ListfirstVisibleItem >= size)
						m_ListfirstVisibleItem = 0;
					
					n = m_ListfirstVisibleItem;
					
					GenieDebug.error("debug","m_IconThread start");
					
	//				
					//取消线程跳出循环
					while(flag)
					{
						if(isback){
							return;
						}
						
						size = m_listdata.size();
						
						if(size > 500)
						{
							if(index >= 30 || index >= size || n > size)
							{
								break;
							}
						}else{
							if( index >= 30 || index >= size || n > size)
							{
								break;
							}
						}
						
						
						
						if(n == size)
							n = 0;					
						
						
						if(m_listdata.get(n).m_container == 2 && m_listdata.get(n).m_iconflag == 0 && m_listdata.get(n).m_iconUrl != null)
						{
							try {
								
								if(isback){
									return;
								}
								Bitmap b=null; 
								byte[] icon=null;
								GenieDlnaDeviceInfo deviceInfo=m_listdata.get(n);
								if(bitmap_Cache.containsKey(deviceInfo.m_iconUrl)){
									b=bitmap_Cache.get(deviceInfo.m_iconUrl);
									System.out.println("lh~~~~ 取出路径="+deviceInfo.m_iconUrl);
								}
								//取文件缓存
								if(b==null){
									b=getBitmapByCache(m_listdata.get(n).m_iconUrl.trim(),true);
								}
								
								if(b==null){
									System.out.println("lh~~~~ 缓存取图片示成功,URL 取图片");
									URL url = new URL(deviceInfo.m_iconUrl);     
					    		    URLConnection conn;     
					    	        conn = url.openConnection();     
					    	        conn.connect();     
					    	        InputStream in = conn.getInputStream();   
					    	        
					    	        try {
										icon = getBytes(in);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										continue;
									}
					    	        
					    	        if(isback || isCancelThread){
										return;
									}
					    	        
					    	        if(icon == null)
					    	        {
					    	        	n++;	
										index++;
										continue;
					    	        }
					    	        
					    	        BitmapFactory.Options opt = new BitmapFactory.Options(); 
		        	    			
				            		opt.inPreferredConfig = Bitmap.Config.RGB_565;
				            		opt.inJustDecodeBounds = true;
				            		BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
				            		opt.inJustDecodeBounds = false;
				            		//Log.d(tag, "phSize: " + phSize + " pix_scale: " + PIX_SCALE);
				            		if (opt.outWidth > opt.outHeight) {
				            			opt.inSampleSize = opt.outWidth / image_width;
				            		} else {
				            			opt.inSampleSize = opt.outHeight / image_width;
				            		}
				            		b = 	BitmapFactory.decodeByteArray(icon, 0,icon.length, opt);
				            		  
				            		if(isback || isCancelThread){
										return;
				            		}
								}
								
								
			            		
			            		icon = null;
			            		
			            		if(b != null)
			            		{
			            			System.out.println("lh~~~~ 取图片示成功");
			            			m_listdata.get(n).downloading = false;
									m_listdata.get(n).downimage = b;
//					    	        m_listdata.get(n).m_bitmap=b; 
					    	        m_listdata.get(n).m_iconflag = 1;
					    	        
					    	        m_geticonindex++;
					    	        
					    	        if(!bitmap_Cache.containsKey(deviceInfo.m_iconUrl)){
					    	        	bitmap_Cache.put(deviceInfo.m_iconUrl, b);
					    	        }
					    	        
					    	        if(m_geticonindex == 3 || (index == (size -1)))
					    	        {
					    	        //b.recycle();
					    	        
						    	        handler.post(new Runnable() {
											
											@Override
											public void run() {
												// TODO Auto-generated method stub
//												if(!isback && !isCancelThread)
													m_listItemAdapter.notifyDataSetChanged();
											}
										});
						    	        m_geticonindex = 0;
					    	        }
			            		}
			            		
							}catch (Exception e)
				    	    {
			    	    	     e.printStackTrace();
			    	    	     System.gc();
			    	    	     continue;
			    	    	     
			    	       }catch(Error e)
			    	       { 
//			    	    	   
			    	    	   	  System.gc();
			    	    	      e.printStackTrace();
			    	    	      return ;
			    	    	}finally{
			    	    		if(isback || isCancelThread){
									return;
								}
			    	    	}
	 
				    	 }
						n++;	
						index++;	
						
						System.gc();
						
					}
					
					int now_count=0;
					int start=frist_image;
            		int end =end_image;
            		for(int i=start;i<end;i++){
						if (isback||isCancelThread) {
							return;
						}
						String pathurl="";
						if(m_listdata.get(i).m_container == 2 && m_listdata.get(i).m_iconflag == 0 && m_listdata.get(i).m_iconUrl != null){
							byte[] icon = null;
							pathurl=m_listdata.get(i).m_iconUrl;
							Bitmap mBitmap = null;
							try {
								if (GneieDlna.bitmap_Cache
										.containsKey(pathurl)) {

									mBitmap = GneieDlna.bitmap_Cache
											.get(pathurl);

								}

								if (mBitmap == null) {
									URL url = new URL(pathurl);
									URLConnection conn;
									conn = url.openConnection();
									conn.connect();
									if (isback||isCancelThread) {
										return;
									}
									InputStream in=null;
									try {
										in= conn.getInputStream();
										icon = getBytes(in);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										continue;
									}
									 if(isback || isCancelThread){
											return;
										}
									if (icon == null) {
										
										continue;
									}
									BitmapFactory.Options opt = new BitmapFactory.Options();
									opt.inPreferredConfig = Bitmap.Config.RGB_565;
									opt.inJustDecodeBounds = true;
									BitmapFactory.decodeByteArray(icon, 0,
											icon.length, opt);
									if (opt.outWidth > opt.outHeight) {
										opt.inSampleSize = opt.outWidth / image_width;
									} else {
										opt.inSampleSize = opt.outHeight / image_width;
									}
									opt.inJustDecodeBounds = false;
									mBitmap = BitmapFactory.decodeByteArray(
											icon, 0, icon.length, opt);
								}

								if (isback||isCancelThread) {
									return;
								}
								icon=null;
								if (mBitmap != null) {
									m_listdata.get(i).downloading = false;
									m_listdata.get(i).downimage = mBitmap;
									 m_listdata.get(i).m_iconflag = 1;
									 if(!GneieDlna.bitmap_Cache.containsKey(pathurl)){
										 GneieDlna.bitmap_Cache.put(pathurl, mBitmap);
						    	     }
									 now_count++;
									 if(now_count==3){
											
										 now_count=0;
										 handler.post(new Runnable() {
												
												@Override
												public void run() {
													// TODO Auto-generated method stub
//													if(!isback && !isCancelThread)
													System.out.println("lh~~~!!! GetIconOnThread_one 刷新");
														m_listItemAdapter.notifyDataSetChanged();
												}
											});
									 }
								}
							}catch(Exception e){
								  System.gc();
								continue;
							}
						}
						
						
					}
            		
					
					GenieDebug.error("debug","m_IconThread end");
				
				}catch(Exception e){
					System.out.println("lh--------"+e.getMessage());
				}finally{
					//取消线程标识还原
					isCancelThread=false;
				}
			}
		});
    	
    	m_IconThread.start();
    }
    
    
    public void ClearListData()
    {
    	if(null == m_listdata)
    		return ;
    	
//    	for(GenieDlnaDeviceInfo device : m_listdata)
//    	{
//    		try {
//				device.downloading = true;
//				if (device.downimage != null) {
//					if (!device.downimage.isRecycled())
//						device.downimage.recycle();
//					device.downimage = null;
//				}
//				device.bigloading = true;
//				if (device.bitimage != null) {
//					if (!device.bitimage.isRecycled())
//						device.bitimage.recycle();
//					device.bitimage = null;
//				}
//				 if(device.m_bitmap != null )
//				   {
//					   if(!device.m_bitmap.isRecycled())
//						   device.m_bitmap.recycle();
//					   device.m_bitmap = null;
//				   }
//			} catch (Exception e2) {
//				// TODO: handle exception
//				e2.printStackTrace();
//			}
//    		if(device.m_icon != null)
//    			device.m_icon = null;
//    	}
    	System.gc();
    	m_listdata.clear();
    }
    
    final public static int Size_M = (1024*1024);
    final public static int Size_K = 1024;
    
    public void SetLoadingItemIndex()
    {
    	if(m_totalitem != null && null != m_listdata)
    	{
    		//m_totalitem.setText(String.format("%d/%d",(GenieDlnaActionDefines.m_ListLoadingItem + GenieDlnaActionDefines.m_ListLoadingAddNum)
    		//		,GenieDlnaActionDefines.m_ListToalItem));
    		
    		
    		m_totalitem.setText(String.format("%d/%d",m_listdata.size()
    				,GenieDlnaActionDefines.m_ListToalItem));
    	}
    	
    	if(m_loadingprogress != null && null != m_listdata)
    	{
    		if(m_listdata.size()
    				== GenieDlnaActionDefines.m_ListToalItem)
    			{
    				GenieDebug.error("debug","m_loadingprogress.setVisibility(View.GONE)");
    				m_loadingprogress.setVisibility(View.GONE);
    			}
    		
    		if(m_listdata.size()
    				< GenieDlnaActionDefines.m_ListToalItem)
    			{
    				GenieDebug.error("debug","m_loadingprogress.setVisibility(View.VISIBLE)");
    				m_loadingprogress.setVisibility(View.VISIBLE);
    			}
    	}
    }
    
    public void RefreshSaverDirList()
    {
    	
    	GenieDebug.error("debug", "RefreshSaverDirList 0 GenieDlnaActionDefines.m_BrowseItemId = "+GenieDlnaActionDefines.m_BrowseItemId);
    	
    	if(m_servertitler != null && null != GenieDlnaActionDefines.m_Serverlist
    			&& null != GenieDlnaActionDefines.m_currentDeviceUUID)
    	{
    		GenieDebug.error("debug", "RefreshSaverDirList 1 ");
    		DeviceDesc temp = GenieDlnaActionDefines.GetServerByList(GenieDlnaActionDefines.m_currentDeviceUUID);
    		if(temp != null)
    			m_servertitler.setText(temp.getFriendlyName());
    		
    	}
    	
		if(null != m_toplevel)
			m_toplevel.setVisibility(View.VISIBLE);
    	
    	SetLoadingItemIndex();
//    	
//    	if(null == m_list || null == m_listdata || null == m_listItemAdapter )
//    	{
//    		return ;
//    	}
//    	
////		if(m_backflag)
////		{
////			return ;
////		}
//		
//		CancelIconThread();
//		
//		
//    	
//    	synchronized(this)
//		{
//
//    		
//    		m_listItemAdapter.notifyDataSetChanged();
//    		
//    		GetIconOnThread();
//    		
//    		if(null != m_toplevel)
//    			m_toplevel.setVisibility(View.VISIBLE);
//    	} 	
    	
    	
    }
    

    
    public void RefreshSaverDirListEx()
    {
    	
    	GenieDebug.error("debug", "RefreshSaverDirListEx 0 GenieDlnaActionDefines.m_BrowseItemId = "+GenieDlnaActionDefines.m_BrowseItemId);

    	
    	if(null == m_list || null == m_listdata || null == m_listItemAdapter )
    	{
    		return ;
    	}
    	
    	SetLoadingItemIndex();
    	synchronized (this) {
			

    		 m_listItemAdapter.notifyDataSetChanged();
		}
    		//if((GenieDlnaActionDefines.m_ListLoadingItem + GenieDlnaActionDefines.m_ListLoadingAddNum)== GenieDlnaActionDefines.m_ListToalItem)
    			//m_listItemAdapter.notifyDataSetChanged();
    	
    	
    	
    }
    
    public void sendMessage2UI(int msg)
    {
    	GenieDebug.error("debug","sendMessage2UI msg = "+msg);
    	handler.sendEmptyMessage(msg);
    }
    
    
    
    
    public void RefreshDirListItem()
    {
    	
    	GenieDebug.error("debug","RefreshDirListItem MESSAGE_REFRESHLIST_DIR");
    	
    	sendMessage2UI(MESSAGE_REFRESHLIST_DIR);
    }
    
    
    public void RefreshListItem()
    {
    	
    	
    	sendMessage2UI(MESSAGE_REFRESHLIST);
    }
    
    
    public void refreshServerDirList(GenieDlnaDeviceInfo[] devicearrry,int all)
    {
    	GenieDebug.error("debug","refreshServerDirList ");
    	
    	if(devicearrry.length < 0)
    	{
    		GenieDebug.error("debug","refreshServerDirList devicearrry.length < 0");
    		return ;
    	}
    	
		if(m_backflag)
		{
			return ;
		}
 
    	synchronized("refreshServerDirList")
		{
    		if(all == 0)
    		{
    			GenieDlnaStatus.m_dirlist.clear();
    		}
    		
    		for(int i = 0;i < devicearrry.length;i++)
        	{
//        		GenieDebug.error("debug","m_title = "+devicearrry[i].m_title);
        		
        		GenieDlnaDeviceInfo device = new GenieDlnaDeviceInfo();
        		
        		device.m_container = devicearrry[i].m_container;
        		device.m_deviceId = devicearrry[i].m_deviceId;
        		device.m_objectId = devicearrry[i].m_objectId;
        		device.m_title = devicearrry[i].m_title;
        		device.m_filestyle = devicearrry[i].m_filestyle;
        		
        		GenieDlnaStatus.m_dirlist.add(device);      		

        	}     
    		
    		
    		
    		
    		
    		SetcurrentServerStatus(2);
    		RefreshDirListItem();
    		
    		
		}
    	
    }
    

    
    
    public void CopyMediaRendererList(GenieDlnaDeviceInfo[] devicearrry,int all)
    {
    	
		if(m_backflag)
		{
			return ;
		}
    	GenieDebug.error("debug","CopyMediaRendererList start");
    	
    	if(devicearrry.length <= 0)
    	{
    		GenieDebug.error("debug","refreshMediaServerList devicearrry.length <= 0");
    		return ;
    	}
    	
    	
    	
    	synchronized("refreshMediaServerList")
		{
    		GenieDlnaStatus.m_dialogRendererlist.clear();
    		
    		for(int i = 0;i < devicearrry.length;i++)
        	{
//        		GenieDebug.error("debug","m_title = "+devicearrry[i].m_title);
        		
        		GenieDlnaDeviceInfo device = new GenieDlnaDeviceInfo();
        		
        		device.m_container = devicearrry[i].m_container;
        		device.m_deviceId = devicearrry[i].m_deviceId;
        		device.m_objectId = devicearrry[i].m_objectId;
        		device.m_title = devicearrry[i].m_title;
        		device.m_filestyle = devicearrry[i].m_filestyle;
        		
        		GenieDlnaStatus.m_dialogRendererlist.add(device);        		
        	}       		
    		
    		sendMessage2UI(MESSAGE_SHOWRENDERDIALOG);
		}
    	
    	GenieDebug.error("debug","CopyMediaRendererList end");
    }
    
    
    
    public void refreshMediaServerList(GenieDlnaDeviceInfo[] devicearrry,int all)
    {
    	GenieDebug.error("debug","refreshMediaServerList 0");
    	
    	if(devicearrry.length <= 0)
    	{
    		GenieDebug.error("debug","refreshMediaServerList devicearrry.length <= 0");
    		return ;
    	}
    	
    	GenieDebug.error("debug","refreshMediaServerList 1");
		if(m_backflag)
		{
			return ;
		}
		
		GenieDebug.error("debug","refreshMediaServerList 2");
    	    	
		
    	
    	synchronized("refreshMediaServerList")
		{
    		GenieDlnaStatus.m_Serverlist.clear();
    		
    		for(int i = 0;i < devicearrry.length;i++)
        	{
//        		GenieDebug.error("debug","m_title = "+devicearrry[i].m_title);
        		
        		GenieDlnaDeviceInfo device = new GenieDlnaDeviceInfo();
        		
        		device.m_container = devicearrry[i].m_container;

        		device.m_deviceId = devicearrry[i].m_deviceId;

        		device.m_objectId = devicearrry[i].m_objectId;
        		device.m_title = devicearrry[i].m_title;
        		device.m_filestyle = devicearrry[i].m_filestyle;
        		device.m_iconflag = devicearrry[i].m_iconflag;
        		device.m_icon = devicearrry[i].m_icon;
        		
        		
        		
        		GenieDebug.error("debug","m_iconflag = "+devicearrry[i].m_iconflag);
        		GenieDebug.error("debug","devicearrry[i].m_icon.length = "+devicearrry[i].m_icon.length);
        		GenieDebug.error("debug","device.m_icon.length = "+device.m_icon.length);
        		
        		GenieDlnaStatus.m_Serverlist.add(device);
        		

        		
        	}   
    		
    		GenieDebug.error("debug","refreshMediaServerList RefreshListItem");
    		
    		RefreshListItem();
    		
    		SetcurrentServerStatus(1);
		}
    	
    }
    
    
    public void SetsystemUpdateID(int id)
	{

		//m_currentServerStatus = Status;
    	GenieDlnaStatus.setsystemUpdateID(id);
	}
	public int GetsystemUpdateID()
	{
		GenieDebug.error("debug","---getsystemUpdateID = "+GenieDlnaStatus.getsystemUpdateID());
		return GenieDlnaStatus.getsystemUpdateID();
	}
    
	
    public void SetcurrentServerStatus(int Status)
	{
		//m_currentServerStatus = Status;
    	GenieDlnaStatus.setcurrentServerStatus(Status);
	}
	public int GetcurrentServerStatus()
	{
		GenieDebug.error("debug","---getcurrentServerStatus = "+GenieDlnaStatus.getcurrentServerStatus());
		return GenieDlnaStatus.getcurrentServerStatus();
	}
	
//	public void SetcurrentRenderStatus(int Status)
//	{
//		//m_currentRenderStatus = Status;
//		GenieDlnaStatus.setcurrentRenderStatus(Status);
//	}
//	public int GetcurrentRenderStatus()
//	{
//		return GenieDlnaStatus.getcurrentRenderStatus();
//	}
    
    
//    public void onMediaRendererAdded(GenieDlnaDeviceInfo device)
//    {
//    	synchronized("onMediaServerAdded")
//		{
//    		GenieDebug.error("debug","-onMediaRendererAdded-m_deviceId = "+device.m_deviceId);
//    		GenieDebug.error("debug","-onMediaRendererAdded-m_objectId = "+device.m_objectId);
//    		GenieDebug.error("debug","-onMediaRendererAdded-m_title = "+device.m_title);
//    		GenieDebug.error("debug","-onMediaRendererAdded-m_container = "+device.m_container);
//    	
//    	
//    		GenieDlnaDeviceInfo temp = new GenieDlnaDeviceInfo();
//    	
//    		temp.m_container = device.m_container;
//    		temp.m_deviceId = device.m_deviceId;
//    		temp.m_objectId = device.m_objectId;
//    		temp.m_title = device.m_title;
//    	
//    	
//    		GenieDlnaStatus.m_Rendererlist.add(temp);
//    
//		}
//    }
//    public void onMediaRendererRemoved(GenieDlnaDeviceInfo device)
//    {
//    	GenieDebug.error("debug","-onMediaRendererRemoved-m_deviceId = "+device.m_deviceId);
//    	GenieDebug.error("debug","-onMediaRendererAdded-m_title = "+device.m_title);
//    	GenieDlnaStatus.m_Rendererlist.remove(device);
//    }
	
	
    public void onMediaServerAdded(GenieDlnaDeviceInfo device)
    {
		if(m_backflag)
		{
			return ;
		}
    	synchronized("onMediaServerAdded")
		{
    		GenieDebug.error("debug","-onMediaServerAdded-m_deviceId = "+device.m_deviceId);
    		GenieDebug.error("debug","-onMediaServerAdded-m_objectId = "+device.m_objectId);
    		GenieDebug.error("debug","-onMediaServerAdded-m_title = "+device.m_title);
    		GenieDebug.error("debug","-onMediaServerAdded-m_container = "+device.m_container);
    	
    	
    		GenieDlnaDeviceInfo temp = new GenieDlnaDeviceInfo();
    	
    		temp.m_container = device.m_container;
    		temp.m_deviceId = device.m_deviceId;
    		temp.m_objectId = device.m_objectId;
    		temp.m_title = device.m_title; 
    		temp.m_systemUpdateID = device.m_systemUpdateID;
    		temp.m_filestyle = device.m_filestyle;
    		
    		if(!GenieDlnaStatus.m_Serverlist.contains(temp))
    		{
    			GenieDlnaStatus.m_Serverlist.add(temp);
    		}    		
    		
    		GenieDebug.error("debug","onMediaServerAdded RefreshListItem");
    		RefreshListItem();
    		
    		SetcurrentServerStatus(1);
		}
    }
    public void onMediaServerRemoved(GenieDlnaDeviceInfo device)
    {
		if(m_backflag)
		{
			return ;
		}
    	synchronized("onMediaServerAdded")
		{
    		GenieDebug.error("debug","-onMediaServerRemoved-m_deviceId = "+device.m_deviceId);
    		GenieDebug.error("debug","-onMediaServerRemoved-m_objectId = "+device.m_objectId);
    		GenieDebug.error("debug","-onMediaServerRemoved-m_title = "+device.m_title);
    		GenieDebug.error("debug","-onMediaServerRemoved-m_container = "+device.m_container);
    	
    	
    		GenieDlnaDeviceInfo temp = new GenieDlnaDeviceInfo();
    	
    		temp.m_container = device.m_container;
    		temp.m_deviceId = device.m_deviceId;
    		temp.m_objectId = device.m_objectId;
    		temp.m_title = device.m_title;   
    		temp.m_systemUpdateID = device.m_systemUpdateID;
    		temp.m_filestyle = device.m_filestyle;
    		
    		if(GenieDlnaStatus.m_Serverlist.contains(temp))
    		{
    			
    			GenieDlnaStatus.m_Serverlist.remove(temp);
    		}     		
    		GenieDebug.error("debug","onMediaServerRemoved RefreshListItem");
    		RefreshListItem();
    		
    		SetcurrentServerStatus(1);
		}
    	//m_DlnaStatus.m_Serverlist.remove(device);
    }
    
    private String ipIntToString(int ip)      
    {          
    	try          
    	{              
    		byte[] bytes = new byte[4];
    		bytes[0] = (byte) (0xff & ip);
    		bytes[1] = (byte) ((0xff00 & ip) >> 8);
    		bytes[2] = (byte) ((0xff0000 & ip) >> 16);
    		bytes[3] = (byte) ((0xff000000 & ip) >> 24);
    		return Inet4Address.getByAddress(bytes).getHostAddress();
    	}
    	catch (Exception e)
    	{
    		return "";
    	}
    }  
    
    
  
	public int getExternalStoragePath(byte[] buffer, int length)
	{
		File path = null;
		
		path = Environment.getExternalStorageDirectory();
		
		if(path != null)
		{
			byte[] temp = null;
			String state = null;
			
			try
			{	
				temp = path.getAbsolutePath().getBytes("ASCII");

				if(length < temp.length)
				{
					return -3;
				}
				System.arraycopy(temp, 0, buffer, 0, temp.length);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				return -1;
			}
			
			state = Environment.getExternalStorageState();
			if(state != null && state.equals(Environment.MEDIA_MOUNTED))
			{
				return temp.length;
			}
			else
			{
				return -2;
			}
		}
		
		return -1;
	}
	
	private  class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Bitmap mIcon1;
        private Bitmap mIcon2;
        private Bitmap mIcon0;
        private Bitmap mIcon_server;
        
        private Bitmap file_video;
        private Bitmap file_audio;
        private Bitmap file_image;
        
        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            // Icons bound to the rows.
            mIcon0 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
            mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.file);
            
            mIcon_server = BitmapFactory.decodeResource(context.getResources(), R.drawable.upnpserver);
            
            file_video = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_video);
            file_audio = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_audio);
            file_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_image);
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
            ViewHolder holder;
            
            if (convertView == null) {
            	   if(isGridView){
                 	  convertView = mInflater.inflate(R.layout.fileofimagelist, null);
                 	  convertView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                       holder = new ViewHolder();
                       holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                       holder.icon.setScaleType(ScaleType.CENTER);
                       holder.text = (TextView) convertView.findViewById(R.id.text);
                       holder.probar=(ProgressBar) convertView.findViewById(R.id.progressbar);
                       holder.probar.setVisibility(View.GONE);
//                       holder.icon.setVisibility(View.GONE);
                       holder.ressize = (TextView) convertView.findViewById(R.id.ressize);
                 }else{
                 	  convertView = mInflater.inflate(R.layout.list_item_icon_text, null);
                       holder = new ViewHolder();
                       holder.text = (TextView) convertView.findViewById(R.id.text);
                       holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                       holder.ressize = (TextView) convertView.findViewById(R.id.ressize);
                 }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            //判断集合大小
            if(position>=m_listdata.size()){
            	return convertView;
            }
            GenieDlnaDeviceInfo deviceInfo=m_listdata.get(position);
            // Bind the data efficiently with the holder.
            holder.text.setText(deviceInfo.m_title);
            if(deviceInfo.m_container == 1)
            {
            	holder.ressize.setVisibility(View.INVISIBLE);
            	if(!mIcon1.isRecycled()){
            		holder.icon.setImageBitmap(mIcon1);
            	}
            }else if(deviceInfo.m_container == 2)
            {
            	String IconUrl = deviceInfo.m_iconUrl;
            	int filestyle = deviceInfo.m_filestyle;
            	int iconflag = deviceInfo.m_iconflag;
            	String ResourceSize = deviceInfo.m_ResourceSize;
            	
            	//GenieDebug.error("debug","000000 m_filestyle ="+filestyle);
            	//GenieDebug.error("debug","000000 m_iconUrl ="+IconUrl);
            	//GenieDebug.error("debug","000000 m_iconflag ="+iconflag);
            	//GenieDebug.error("debug","000000 position ="+position);
            	
            	if(ResourceSize != null)
            	{
            		holder.ressize.setVisibility(View.VISIBLE);
            		holder.ressize.setText(ResourceSize);
            	}else
            	{
            		holder.ressize.setVisibility(View.INVISIBLE);
            	}
            	
            	
            	if(filestyle == 1)
	        	{
            		if(!file_video.isRecycled()){
            			holder.icon.setImageBitmap(file_video);
            		}
	        	}else if(filestyle == 2)
	        	{
	        		if(!file_audio.isRecycled()){
	        			holder.icon.setImageBitmap(file_audio);
	        		}
	        	}else if(filestyle == 3)
	        	{
	        		if(iconflag == 1)
                	{ 
                		if(deviceInfo.downimage != null && !deviceInfo.downimage.isRecycled()&&!deviceInfo.downloading)
                		{
                			holder.icon.setVisibility(View.VISIBLE);
                			holder.probar.setVisibility(View.GONE);
                			holder.icon.setScaleType(ScaleType.CENTER_CROP);
                			holder.icon.setImageBitmap(deviceInfo.downimage);
                		}
                	}else if(!file_image.isRecycled()){
                		
                		if(deviceInfo.m_long_ResourceSize>4*Size_M){
                			holder.icon.setVisibility(View.GONE);
                			holder.probar.setVisibility(View.VISIBLE);
                		}else{
                			holder.icon.setVisibility(View.VISIBLE);
                			holder.probar.setVisibility(View.GONE);
                    		holder.icon.setScaleType(ScaleType.CENTER);
    	        			holder.icon.setImageBitmap(file_image);
                		}
                		
	        		}
	        		
	        	}else
	        	{
	        		if(!mIcon2.isRecycled()){
	        			holder.icon.setImageBitmap(mIcon2);
	        		}
	        	}
            	
            }else if(deviceInfo.m_container == 0)
            {
            	holder.ressize.setVisibility(View.INVISIBLE);
            	//holder.icon.setImageBitmap(mIcon1);
            	if(deviceInfo.m_iconflag == 1)
            	{
            		if(deviceInfo.m_icon.length > 0)
            		{
            			BitmapFactory.Options opt = new BitmapFactory.Options(); 
            		            	    			
	            		opt.inPreferredConfig = Bitmap.Config.RGB_565;
	            		opt.inJustDecodeBounds = true;
	            		BitmapFactory.decodeByteArray(deviceInfo.m_icon, 0, deviceInfo.m_icon.length, opt);
	            		opt.inJustDecodeBounds = false;
	            		//Log.d(tag, "phSize: " + phSize + " pix_scale: " + PIX_SCALE);
	            		if (opt.outWidth > opt.outHeight) {
	            			opt.inSampleSize = opt.outWidth / 40;
	            		} else {
	            			opt.inSampleSize = opt.outHeight / 40;
	            		}
	            		Bitmap b = 	BitmapFactory.decodeByteArray(deviceInfo.m_icon, 0, m_listdata.get(position).m_icon.length, opt);
	            		  

	            		if(b != null)
	            		{
	            			if(!b.isRecycled()){
	            				holder.icon.setImageBitmap(b);
	            			}
	            		}else{
	            			if(!mIcon_server.isRecycled()){
	            				holder.icon.setImageBitmap(mIcon_server);
	            			}
	            		}	            		
	            		
            		}else
            		{
            			if(!mIcon_server.isRecycled()){
            				holder.icon.setImageBitmap(mIcon_server);
            			}
            		}
            	}else
            	{
            		if(!mIcon_server.isRecycled()){
            			holder.icon.setImageBitmap(mIcon_server);
            		}
            	}
    			
            }else{
            	holder.ressize.setVisibility(View.INVISIBLE);
            	if(!mIcon2.isRecycled()){
            		holder.icon.setImageBitmap(mIcon2);
            	}
            }

            return convertView;
        }

        class ViewHolder {
            TextView text;
            ImageView icon;
            TextView ressize;
            ProgressBar probar;
        }
    }
    
	
	class BrowseProgressDialog extends ProgressDialog{

		public BrowseProgressDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void dismiss() {
			// TODO Auto-generated method stub
			super.dismiss();
			
			GenieDebug.error("debug", "BrowseProgressDialog dismiss");
		}

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			
			//StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_CANCLE);
			
			StartDlnaAction(GenieDlnaActionDefines.ACTION_PROGRESSBROWSE_CANCLE);
			
			GenieDebug.error("debug", "BrowseProgressDialog onBackPressed");
		}
		
		
		
	}
	
	
	class PlayItemProgressDialog extends ProgressDialog{

		public PlayItemProgressDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void dismiss() {
			// TODO Auto-generated method stub
			super.dismiss();
			
			GenieDebug.error("debug", "PlayItemProgressDialog dismiss");
		}

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			
			StartDlnaAction(GenieDlnaActionDefines.ACTION_PLAYMEDIAITEM_CANCLE);
			
			GenieDebug.error("debug", "PlayItemProgressDialog onBackPressed");
		}
		
		
		
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
			IntentFilter filter = new IntentFilter();//鍒涘缓IntentFilter瀵硅薄
			filter.addAction(GenieGlobalDefines.DLNA_ACTION_BROADCAST);
			registerReceiver(m_DLNAReceiver, filter);//娉ㄥ唽Broadcast Receiver
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
			
			
			GenieDlnaService.SetProgressBrowseLoadingCallback(null);
			
			CancelIconThread();
//			CancelIconThread_one();
			ClearListData();
			
			GenieDlnaStatus.saveStatusMap(mContext,GenieDlnaStatus.DLNASTATUSFILENAME);
			
			UnRegisterBroadcastReceiver();
			
			m_pause =  true;
			
			
			StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_STOPRENDER);
			
			
			
			
//	        Intent Dlna = new Intent("com.dragonflow.GenieDlnaService");
//			stopService(Dlna);
			
			
			//StartDlnaAction(GenieDlnaActionDefines.ACTION_STOP);
	}
	    
	    
	    public void CancleBrowseProgressDialog()
	    {
	    	if(null != m_BrowseprogressDialog)
	    	{
	    		if(m_BrowseprogressDialog.isShowing())
	    			m_BrowseprogressDialog.dismiss();
	    		m_BrowseprogressDialog = null;
	    	}
	    }
	    
	    
	    public void ShowBrowseProgressDialog()
	    {
	    	CancleBrowseProgressDialog();
	    	
	    	m_BrowseprogressDialog = new BrowseProgressDialog(GneieDlna.this);
	    	m_BrowseprogressDialog.setMessage(getResources().getString(R.string.wait));
	    	m_BrowseprogressDialog.show();
	    }
	    
	    
	    public void CanclePlayItemProgressDialog()
	    {
	    	if(null != m_PlayItemProgressDialog)
	    	{
	    		if(m_PlayItemProgressDialog.isShowing())
	    			m_PlayItemProgressDialog.dismiss();
	    		m_PlayItemProgressDialog = null;
	    	}
	    }
	    
	    
	    public void ShowPlayItemProgressDialog()
	    {
	    	CanclePlayItemProgressDialog();
	    	
	    	m_PlayItemProgressDialog = new PlayItemProgressDialog(GneieDlna.this);
	    	m_PlayItemProgressDialog.setMessage(getResources().getString(R.string.wait));
	    	m_PlayItemProgressDialog.show();
	    }
	    
	    
	    
	    private class DLNAReceiver extends BroadcastReceiver{//


	         @Override
		        public void onReceive(Context context, Intent intent) {//
	     
	        	 int action = -1;
	     		
	     		action = intent.getIntExtra(GenieGlobalDefines.DLNA_ACTION_RET,-1);
	     		
	     		GenieDebug.error("DLNAReceiver", "GneieDlna onReceive DLNA_ACTION_RET ="+action);
	     		
	     		//if(m_pause)
	    		//	return;
	     		sendMessage2UI(action);
//	     		switch(action)
//	     		{
//	     		case GenieDlnaActionDefines.ACTION_RET_SERVERLISTCHANGED:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_SERVERLISTCHANGED);
//	     			break;
//	     		case GenieDlnaActionDefines.ACTION_RET_BROWSE_ITEM:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_BROWSE_ITEM);
//	     			break;
//	     		case GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_SHOW:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_SHOW);
//	     			break;
//	     		case GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
//	     			break;	
//	     		case GenieDlnaActionDefines.ACTION_RET_BROWSE_CHOOSERENDER:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_BROWSE_CHOOSERENDER);
//	     			break;
//	     		case GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW);
//	     			break;
//	     		case GenieDlnaActionDefines.ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_SHOW:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_SHOW);
//	     			break;
//	     		case GenieDlnaActionDefines.ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_CANCLE:
//	     			sendMessage2UI(GenieDlnaActionDefines.ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_CANCLE);
//	     			break;	
//	     		}

	         }
	       }
	    
	    
	    public int ToPlayView()
		{
	    	if(sendflag){
	    		sendflag=false;
	    		return 0;
	    	}
	    	CanclePlayItemProgressDialog();
			CancleBrowseProgressDialog();
			
			int style =  GetVideoStyle();
			
			if(style==3){
//				ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//
//				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//				if(!ImagesBrowseActivity.class.getName().equals(cn.getClassName())){
//				if(!ImagesBrowseActivity.isopen){
//				CancelIconThread_one();
					Intent imagebrowes=new Intent(this,ImagesBrowseActivity.class);
					imagebrowes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(imagebrowes);
//				}
			}else{
				if(m_listdata.size()== GenieDlnaActionDefines.m_ListToalItem){
					GenieDebug.error("ToPlayView","ToPlayView");
					GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_PLAY);
					GenieDlnaTab.m_radio2.setChecked(true);
				}
//				else{
//					 Toast.makeText(GneieDlna.this,R.string.picture_loading, Toast.LENGTH_SHORT).show();
//				}
			}
			return 0;
		}
	    private DLNAReceiver m_DLNAReceiver = null;
	    
	    
	    private void ShowNoRenderDialog()
	    {
	    	AlertDialog.Builder dialog_norender =  new AlertDialog.Builder(this);
			
			
	    	dialog_norender.setIcon(android.R.drawable.ic_dialog_alert);
			//dialog_wifiset.setTitle(" ");
	    	dialog_norender.setMessage(R.string.norender);
//	    	dialog_norender.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//					GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
//					GenieDlnaTab.m_radio0.setChecked(true);
//				}
//			});
			
			
			
	    	dialog_norender.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});
			
			
			
	    	dialog_norender.setCancelable(true);
	    	dialog_norender.show();
	    }
	    
	    
		public ListView m_renderlist = null;
		public EfficientAdapter_render m_renderlistItemAdapter = null;
		public ArrayList<GenieDlnaRenderListItem> m_renderlistdata = null;
		
		
	    private void ShowRenderDialog()
	    {
	    	GenieDebug.error("debug","ShowRenderDialog start");
	    	
	    	if(GenieDlnaActionDefines.m_Rendererlist.isEmpty())
	    	{
	    		ShowNoRenderDialog();
	    		GenieDebug.error("debug","ShowRenderDialog GenieDlnaActionDefines.m_Rendererlist.isEmpty()");
	    		return ;
	    	}    	
	    	
	    	if(m_renderlistdata == null)
	    	{
	    		m_renderlistdata = new  ArrayList<GenieDlnaRenderListItem>();
	    	}else
	    	{
	    		m_renderlistdata.clear(); 
	    	}
	    	int n = 0;
	    	m_chooseindex = 0;
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
    			
    			
//    			GenieDebug.error("debug","desc.getUuid() ="+desc.getUuid().toString());
//    			if(null != GenieDlnaActionDefines.m_WorkRenderUUID)
//    			{
//    				
//    				GenieDebug.error("debug","GenieDlnaActionDefines.m_WorkRenderUUID ="+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
//    				if(desc.getUuid().equals(GenieDlnaActionDefines.m_WorkRenderUUID))
//    				{
//    					GenieDebug.error("debug","m_chooseIndex ="+n);
//    					m_chooseindex = n;
//    					temp.m_select = true;
//    				}else
//    				{
//    					temp.m_select = false;
//    				}
//    				
//    			}else
//    			{
//    				temp.m_select = false;
//    				GenieDebug.error("debug","null == m_workrender ");
//    			}
    			//n++;
    			
    			temp.m_select = false;
    			m_renderlistdata.add(temp);

        		
    		}
    		
    	
	    	if(null != m_renderlistItemAdapter)
	    	{
	    		m_renderlistItemAdapter = null;
	    	}
	    	
	    	
	    		
	    	if(m_RenderDialog != null)
	    	{
	    		m_RenderDialog.dismiss();
	    		m_RenderDialog = null;
	    	}
	    	
	    	
	    	
	    	
	    	

			View view = LayoutInflater.from(this).inflate(R.layout.renderview,null);
	    	
			m_renderlist = (ListView)view.findViewById(R.id.ListView01);
			
			m_renderlistItemAdapter = new EfficientAdapter_render(this);
			m_renderlist.setAdapter(m_renderlistItemAdapter);
			
		    m_renderlist.setItemsCanFocus(false);
		    m_renderlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		    m_renderlist.setItemChecked(-1, true);
		    
		    
		    m_renderlist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					GenieDebug.error("onItemClick", "onItemClick arg3 = "+arg3);
					
					
					
					int size = m_renderlistdata.size();
					
					GenieDebug.error("onItemClick", "onItemClick size = "+size);
					
					if(arg3 >= 0 && arg3 < size)
					{
						int Index = (int)arg3;
						
						GenieDebug.error("onItemClick", "onItemClick Index = "+Index);
						
						//if(Index >=0 && Index < size)
						//{	
							GenieDebug.error("onItemClick", "onItemClick 0 m_chooseindex = "+m_chooseindex);
							
							m_chooseindex = Index;
							for(int i = 0; i < m_renderlistdata.size();i++)
							{
								if(i == m_chooseindex)
								{
									m_renderlistdata.get(i).m_select = true;
								}else
								{
									m_renderlistdata.get(i).m_select = false;
								}
							}
							//m_chooseindex = Index;
							GenieDebug.error("onItemClick", "onItemClick 1 m_chooseindex = "+m_chooseindex);
							m_renderlistItemAdapter.notifyDataSetChanged();
							GenieDebug.error("onItemClick", "onItemClick m_renderlistItemAdapter.notifyDataSetChanged()");
						//}
					}
					
					
	            	size = GenieDlnaActionDefines.m_Rendererlist.size();
					
					if(m_chooseindex >= 0 && m_chooseindex < size)
					{
						GenieDlnaActionDefines.m_WorkRenderUUID = GenieDlnaActionDefines.m_Rendererlist.get((int)m_chooseindex).getUuid();
						GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID = GenieDlnaActionDefines.m_WorkRenderUUID.toString();
						GenieDebug.error("onItemClick", "GenieDlnaActionDefines.m_WorkRenderUUID = "+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
						StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_SAVECONFIG);					
						StartDlnaAction(GenieDlnaActionDefines.ACTION_PLAYMEDIAITEM);
						
						if(m_RenderDialog != null)
				    	{
				    		m_RenderDialog.dismiss();
				    		m_RenderDialog = null;
				    	}
					}
					
				}
			});
		    
	    	
	    	m_RenderDialog = new AlertDialog.Builder(GneieDlna.this)
	        .setIcon(R.drawable.icon)
	        .setTitle(R.string.chooserender)
	        .setView(view)
	        .create();
	        //.setSingleChoiceItems(m_dialogItemAdapter, 0, new DialogInterface.OnClickListener() {
//	            public void onClick(DialogInterface dialog, int whichButton) {
//	            	
//	            	m_chooseindex = whichButton;
//	            	
//	            	GenieDebug.error("debug","ShowRenderDialog m_chooseindex = "+m_chooseindex);
//	            	
//	                /* User clicked on a radio button do some stuff */
//	            }
//	        })
//	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//	            public void onClick(DialogInterface dialog, int whichButton) {
//
//	                /* User clicked Yes so do some stuff */
//	            	
//	            	
//	            	int size = GenieDlnaActionDefines.m_Rendererlist.size();
//					
//					if(m_chooseindex >= 0 && m_chooseindex < size)
//					{
//						GenieDlnaActionDefines.m_WorkRenderUUID = GenieDlnaActionDefines.m_Rendererlist.get((int)m_chooseindex).getUuid();
//						GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID = GenieDlnaActionDefines.m_WorkRenderUUID.toString();
//						GenieDebug.error("onItemClick", "GenieDlnaActionDefines.m_WorkRenderUUID = "+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
//						StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_SAVECONFIG);					
//						StartDlnaAction(GenieDlnaActionDefines.ACTION_PLAYMEDIAITEM);
//					}
	            	
//	            	if(m_chooseindex >= 0 && m_chooseindex < GenieDlnaStatus.m_dialogRendererlist.size())
//	            	{
//	            		
//	            		
//	            		String uuid = GenieDlnaStatus.m_dialogRendererlist.get(m_chooseindex).m_deviceId;
//	            		GenieDlnaStatus.m_chooserguid = uuid;
//	            		//GneieDlna.this.SetDlnaGuid(GenieDlnaStatus.m_chooserguid);
//	            		//ToPlayMedia(uuid);
//	            	}else
//	            	{
//	            		
//	            	}
//	            }
//	        })
//	        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//	            public void onClick(DialogInterface dialog, int whichButton) {
//
//	                /* User clicked No so do some stuff */
//	            }
//	        })
//	       .create();
	    	
	    	
	    	m_RenderDialog.show();

	    }
		
	    
		private  class EfficientAdapter_render extends BaseAdapter {
	        private LayoutInflater mInflater;
	        private Bitmap mIcon1;
	        private Bitmap mIcon2;
	        private Bitmap mIcon0;
	        
	        private Bitmap file_video;
	        private Bitmap file_audio;
	        private Bitmap file_image;

	        public EfficientAdapter_render(Context context) {
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
	        	return m_renderlistdata.size();
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
	            
	            try{
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
		            
		            //GenieDebug.error("debug", "View getView position= "+position);
		            //GenieDebug.error("debug", "View getView m_renderlistdata.get(position).m_select= "+m_renderlistdata.get(position).m_select);
	
		            // Bind the data efficiently with the holder.
		            holder.text.setText(m_renderlistdata.get(position).m_FriendlyName);
		            if(m_renderlistdata.get(position).m_select)
		            {
		            	holder.select.setVisibility(View.VISIBLE);
		            }else
		            {
		            	holder.select.setVisibility(View.GONE);
		            }
		            
		            
		            if(m_renderlistdata.get(position).m_iconflag)
		        	{
		        		if(m_renderlistdata.get(position).m_icon.length > 0)
		        		{
		        			BitmapFactory.Options opt = new BitmapFactory.Options(); 
		        		            	    			
		            		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		            		opt.inJustDecodeBounds = true;
		            		BitmapFactory.decodeByteArray(m_renderlistdata.get(position).m_icon, 0, m_renderlistdata.get(position).m_icon.length, opt);
		            		opt.inJustDecodeBounds = false;
		            		//Log.d(tag, "phSize: " + phSize + " pix_scale: " + PIX_SCALE);
		            		if (opt.outWidth > opt.outHeight) {
		            			opt.inSampleSize = opt.outWidth / 40;;
		            		} else {
		            			opt.inSampleSize = opt.outHeight / 40;
		            		}
		            		Bitmap b = 	BitmapFactory.decodeByteArray(m_renderlistdata.get(position).m_icon, 0, m_renderlistdata.get(position).m_icon.length, opt);
		            		  
	
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
		        		
		        	}
	            }catch(Exception e){
	            	System.out.println("ln--------GenieDlna 取图片");
	            }

	            return convertView;
	        }

	        class ViewHolder {
	            TextView text;
	            ImageView icon;
	            ImageView select;
	        }
	    }


		
	    public void PushListData(ArrayList<GenieDlnaDeviceInfo> arr,boolean clear)
	    {
	    	
			if(m_listdata == null)
				return ;
			
			GenieDebug.error("debug", "PushListData 010 arr.size() = "+arr.size());
			GenieDebug.error("debug", "PushListData 010 m_listdata.size() = "+m_listdata.size());
			GenieDebug.error("debug", "PushListData 010 clear = "+clear);

			
			if(clear){
				if(m_listdata!=null&&arr.get(0).m_filestyle==3){
					isGridView=true;
					updateLayout();
				}
				isback=false;
				m_listdata.clear();
				GetIconOnThread();
//				GetIconOnThread_one(0,30);
			}
				
			
			if(arr.size() <= m_listdata.size())
				return ;
	    	
			
			CancelIconThread();
//			CancelIconThread_one();
//	    	if(GenieDlnaActionDefines.m_listdatabackup == null)
//				return ;
//				
//			if(clear)
//				GenieDlnaActionDefines.m_listdatabackup.clear();
			
			GenieDebug.error("debug", "PushListData 010 m_listdata.size() = "+m_listdata.size());
			
			for(int i = m_listdata.size();i <arr.size(); i++)
			{
				//GenieDebug.error("debug", "PushListData 010 i = "+i);
				m_listdata.add(arr.get(i));
			}
			
			
			m_listItemAdapter.notifyDataSetChanged();
			SetLoadingItemIndex();
			
			int size = m_listdata.size();
			
			if(clear){
				CanclePlayItemProgressDialog();
				CancleBrowseProgressDialog();
			}
//			if(size <= 500)
//			{
//				GetIconOnThread();
//			}
	    	
	    }


		@Override
		public void onProgressBrowseLoading(ArrayList<GenieDlnaDeviceInfo> arr, boolean first) {
			// TODO Auto-generated method stub
			synchronized (this) {
				
				if(m_servertitler != null && null != GenieDlnaActionDefines.m_Serverlist
		    			&& null != GenieDlnaActionDefines.m_currentDeviceUUID)
		    	{
		    		GenieDebug.error("debug", "RefreshSaverDirList 1 ");
		    		DeviceDesc temp = GenieDlnaActionDefines.GetServerByList(GenieDlnaActionDefines.m_currentDeviceUUID);
		    		if(temp != null)
		    			m_servertitler.setText(temp.getFriendlyName());
		    		
		    	}
		    	
				
				if(null != m_toplevel)
					m_toplevel.setVisibility(View.VISIBLE);
				
				
				
				GenieDebug.error("debug"," onProgressBrowseLoading arr.length = "+arr.size());
				GenieDebug.error("debug"," onProgressBrowseLoading first = "+first);
			
				PushListData(arr,first);
				

	    		
			}
			
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
  
		public void StartDlnaAction(int intent,int action,boolean sendflag)
		{		
			this.sendflag=sendflag;
			
			System.out.println("发送图片~~~"+m_listdata.get(action).m_filestyle);
			
			if(m_listdata.get(action).m_filestyle==3){
				
				GenieDlnaActionDefines.m_BrowseItemId=action;
				if(action != GenieDlnaActionDefines.ACTION_BROWSE_ITEM){
					CancelIconThread();
//					CancelIconThread_one();
				}
					
	
				GenieDebug.error("debug","9999df999999 StartDlnaAction action = "+action);
				
				Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
				Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,intent);
				startService(Dlna);
				GenieDebug.error("debug","9999df999999 StartDlnaAction end");
				
			}

		}

		public  void re_m_listItemAdapter() {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					if(!isback && !isCancelThread)
						m_listItemAdapter.notifyDataSetChanged();
				}
			});
		}
		
//		/**
//		 * 获取缩略图
//		 * 
//		 * @param selecone
//		 * @param nowcount
//		 */
//		public void GetIconOnThread_one(final int select_first, final int select_end) {
//
//			if (m_listdata == null)
//				return;
//			
//			CancelIconThread_one();
//
//			m_IconThread1 = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//
//					try {
//						if (isback||sroll_flag||select_end>m_listdata.size()) {
//							return;
//						}
//
//						int now_count=0;
//						
//					}catch(Exception e){
//						  System.gc();
//						return;
//					}finally{
//						sroll_flag=false;
//					}
//				}
//			});
//			System.gc();
//			m_IconThread1.start();
//		}
		
		
		private byte[] getBytes_one(InputStream is) throws Exception {

			if(is==null)return null;
			
			ByteArrayOutputStream baos=null;
			try{
				baos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				int len = 0;
		
				while ((len = is.read(b, 0, 1024)) != -1) {
					baos.write(b, 0, len);
					baos.flush();
				}
				byte[] bytes = baos.toByteArray();
				//baos.close();
				return bytes;
			}catch(Exception e1){
				e1.printStackTrace();
				return null;
			}finally{
				if(baos!=null){
					baos.close();
				}
				if(is!=null){
					is.close();
				}
			}
		}
		
	private void updateLayout(){
			
			if(isGridView){
				m_gridlist.setVisibility(View.VISIBLE);
				m_list.setVisibility(View.GONE);
			}else{
				m_list.setVisibility(View.VISIBLE);
				m_gridlist.setVisibility(View.GONE);
			}
			
		}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		 display_width=getResources().getDisplayMetrics().widthPixels;
		 image_width=160;
		    if((display_width/image_width)>5){
		    	image_width=display_width/5;
		    	number_image=5;
		    }else{
		    	number_image=display_width/image_width;
		    }
		m_gridlist.setNumColumns(number_image);//动态设置列数
	}
	
	
	/**
	 * 从缓存中获取图片
	 * @param fileUrl
	 * @return
	 */
	public Bitmap getBitmapByCache(String fileUrl,boolean isPreview){
		
		try{
			if(GetImageThread.fileCacheList!=null && fileUrl!=null && !"".equals(fileUrl)){
				if(GetImageThread.fileCacheList.containsKey(fileUrl.trim())){
					String filename=GetImageThread.fileCacheList.get(fileUrl.trim());
					File newFile=new File(GetImageThread.getCachePath()+filename);
					if(newFile.exists()){
						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inPreferredConfig = Bitmap.Config.RGB_565;
						opt.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(newFile.getPath(), opt);
						int w = 480;
						int h = 800;
						if (getResources().getDisplayMetrics() != null) {
							w = getResources().getDisplayMetrics().widthPixels;
							h = getResources().getDisplayMetrics().heightPixels;
						}
						final int SampleSize =isPreview?40:(w > h ? w : h);
						if (SampleSize!=0 && opt.outWidth > SampleSize
								|| opt.outHeight > SampleSize) {
							if (opt.outWidth > opt.outHeight) {
								opt.inSampleSize = opt.outWidth / SampleSize;
							} else {
								opt.inSampleSize = opt.outHeight / SampleSize;
							}
						}
						
						opt.inJustDecodeBounds = false;
						opt.inTempStorage=new byte[1024];
						return BitmapFactory.decodeFile(newFile.getPath(),opt);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 清除文件缓存
	 */
	private void clearFileCache(){
		
		File file=new File(GetImageThread.getCachePath());
		if(file.exists()){
			File[] childFiles=file.listFiles();
			for(File f:childFiles){
				f.delete();
			}
		}
		
	}
}