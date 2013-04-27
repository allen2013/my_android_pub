package com.dragonflow;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;

import com.dlnashare.ImageDlnaShareActivity;
import com.netgear.genie.media.dlna.DeviceDesc;

public class GenieDlnaShare extends Activity {

	public ListView m_list = null;
	public EfficientAdapter m_listItemAdapter = null;
	public ArrayList<GenieDlnaRenderListItem> m_listdata = null;
	private  int m_chooseIndex = -1;
	private  UUID m_chooseuuid = null;
	
	private Button m_play=null;
	private Button m_stop=null;
	private Button m_pause=null;
	
	private int m_mimeType = MIME_TYPE_UNKOWN;
	
	private boolean m_sharefileopenflag = false;
	

	final public static int MESSAGE_SHOWOPENWIFIDIALOG = 10000;
	
	final public static int MIME_TYPE_UNKOWN = -1;
	final public static int MIME_TYPE_VIDEO = 1;
	final public static int MIME_TYPE_AUDIO = 2;
	final public static int MIME_TYPE_IMAGE = 3;
	
	public static boolean sendflag=false;
	public static GenieDlnaShare geniedlnashare=null;
	public static boolean openplay=false;
	public static boolean open=false;
	private Handler handler = new Handler()
	{   
        public void handleMessage(Message msg) 
        {   
//        	System.out.println(GenieDlnaShare.openplay+"~~~~~~~~返回消息_________________"+open+"~~~~~~~~"+ImageDlnaShareActivity.getInstanceCount());
        	//GenieDlnaActionDefines.m_playStyle==3&&
        	if (m_mimeType == MIME_TYPE_IMAGE&&GenieDlnaShare.openplay) {
        			if(!open){
//        				ImageDlnaShareActivity.getInstanceCount();//android 4.0没有如此方法
//        				if(!open||!ImageDlnaShareActivity.openplay){
        				System.out.println("是否进入划动页面_________________"+open);
        				ToImagePlayer();
        				open=true;
        			}
    				return;	
			}else{
				String error = null;
	        	GenieDebug.error("handleMessage", " GenieDlnaShare handleMessage msg.what = "+msg.what);
	        	switch(msg.what)
	        	{
	        	case GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH:
					RefreshUi();
					break;
	        	case MESSAGE_SHOWOPENWIFIDIALOG:
	        		ShowWiFiSetDialog();
	        		break;
	        	case GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE:
	    			CancleControlActionProgressDialog();
	    			break;
	    		case GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY_SHOW:				
				case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_SHOW:			
				case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_SHOW:
				case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_SHOW:
//				case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_SHOW:				
//				case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_SHOW:					
//				case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_SHOW:				
//				case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_SHOW:				
//				case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_SHOW:
					ShowControlActionProgressDialog(msg.what);
					break;
	        	case GenieDlnaActionDefines.ACTION_RENDER_SETPLAYING:
	        		if(null != GenieDlnaActionDefines.m_playurl)
	        		{
	        			if(GenieDlnaActionDefines.m_playStyle == 1 ||
	        					GenieDlnaActionDefines.m_playStyle == 2)
	        			{
	        				//ToVideoPlay();
	        			}else if(GenieDlnaActionDefines.m_playStyle == 3)
	        			{
	        				//ToImagePlayer();
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
	        	case GenieDlnaActionDefines.ACTION_PLAY_FAIL:
	        		onplayfail();
	        		break;
	        	}
			}
        }
	}; 
	
	
	private void ShowWiFiSetDialog()
	{
		AlertDialog.Builder dialog_wifiset =  new AlertDialog.Builder(this);
		
		
		dialog_wifiset.setIcon(android.R.drawable.ic_dialog_alert);
		dialog_wifiset.setTitle(R.string.login);
		dialog_wifiset.setMessage(R.string.wifisettext);
		dialog_wifiset.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent); 
				GenieDlnaShare.this.finish();
				//thisfinish();
			}
		});
		
		
		
		dialog_wifiset.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				GenieDlnaShare.this.finish();
			}
		});
		
		dialog_wifiset.setCancelable(false);
		dialog_wifiset.show();
	}
	
	private void onplayfail()
	{
		 Toast toast = Toast.makeText(this,getResources().getString(R.string.playfails), Toast.LENGTH_SHORT);
		   toast.setGravity(Gravity.CENTER, 0, 0);
		   toast.show();
	}
	
	private void ToImagePlayer()
	{
		
		GenieDebug.error("debug","dlnarender ToImagePlayer");
//		Intent intent = new Intent();
//		startActivity(intent);
		if(sendflag)return;
//		Intent intent = new Intent();
//		
//		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//
//		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//		if(!ImageDlnaShareActivity.class.getName().equals(cn.getClassName())){
			Intent imagebrowes=new Intent(GenieDlnaShare.this,ImageDlnaShareActivity.class);
			imagebrowes.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(imagebrowes);
//		}
	 
		
        //intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
//		intent.setClass(this, GenieDlnaImagePlayer.class);
//		intent.setClass(GenieDlnaShare.this, ImageDlnaShareActivity.class);
//		startActivity(intent);
	}	
	
	private void ToVideoPlay()
	{
		GenieDebug.error("debug","dlnarender ToVideoPlay");
		
		Intent intent = new Intent();
		        	
        //intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(this, GenieDlnaVideoPlay.class);
		startActivity(intent);
	}
	
	 public void sendMessage2UI(int msg)
	    {
	    	
	    	handler.sendEmptyMessage(msg);
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
	    				if(m_chooseuuid != null)
	    				{
	    					if(desc.getUuid().equals(m_chooseuuid))
		    				{
	    						GenieDlnaActionDefines.m_WorkRenderUUID = m_chooseuuid;
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
	    				
	    			}

	        		m_listdata.add(temp);

	        		n++;
	    		}
	    		
	    		m_listItemAdapter.notifyDataSetChanged();
	    		
	    		//GenieDebug.error("debug","m_chooseIndex =  "+m_chooseIndex);
	    		//m_list.setItemChecked(m_chooseIndex, true);
	    		
			}
	    	
	    	
	    	
	    	
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

	 
		private void RenderOnRefresh2()
		{
			StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_REFRESH);
		}
	 
	 
	 public void InitTitleView()
		{
			Button  back = (Button)findViewById(R.id.back);
			Button  about = (Button)findViewById(R.id.about);
			
			TextView title = (TextView)findViewById(R.id.netgeartitle);
			

	       
	        //title.setText("");
	        
			
			
			back.setBackgroundResource(R.drawable.title_bt_bj);
			about.setBackgroundResource(R.drawable.title_bt_bj);
			about.setText(R.string.refresh);
			
			back.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Intent intent = new Intent();
					intent.setClass(GenieDlnaShare.this, GenieMainView.class);
		    		startActivity(intent);
		    		GenieDlnaShare.this.finish();
				}
			});
			
			about.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openplay=false;
					ListClear();
					RenderOnRefresh2();
				}
			});
			
			back.setOnTouchListener(new OnTouchListener(){      
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
			

			
			about.setOnTouchListener(new OnTouchListener(){      
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
	 
	 
	 public void RefreshUi()
	{

					
			if(null == m_play || null == m_stop 
					|| null == m_pause 
					|| GenieDlnaActionDefines.m_DLNARenderStatus == null)
			{
				return ;
			}
			
	    	synchronized("ui")
			{
	    		
			//GenieDebug.error("debug","----RefreshUi---style =  "+style);	
	    		
	    		if(m_mimeType == MIME_TYPE_UNKOWN || m_mimeType == MIME_TYPE_IMAGE)
	        	{
	    			m_play.setEnabled(true);
	    			m_play.setBackgroundResource(R.drawable.play_play_off);
	    			m_stop.setEnabled(false);
	    			m_stop.setBackgroundResource(R.drawable.play_stop_invalid);
	    			m_pause.setEnabled(false);
	    			m_pause.setBackgroundResource(R.drawable.play_pause_invalid);
	    			return ;
	        	}
			
			
			if (null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState && (GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PLAYING"))){
				//	|| GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("TRANSITIONING"))) {
				
				m_play.setEnabled(false);
				m_play.setBackgroundResource(R.drawable.play_play_invalid);
				m_stop.setEnabled(true);
				m_stop.setBackgroundResource(R.drawable.play_stop_off);
				m_pause.setEnabled(true);
				m_pause.setBackgroundResource(R.drawable.play_pause_off);
				
				
			} else if (null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState
					&& GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PAUSED_PLAYBACK")) {
				
				m_play.setEnabled(true);
				m_play.setBackgroundResource(R.drawable.play_play_off);
				m_stop.setEnabled(true);
				m_stop.setBackgroundResource(R.drawable.play_stop_off);
				m_pause.setEnabled(false);
				m_pause.setBackgroundResource(R.drawable.play_pause_invalid);
				
				
				
			} else {
				
				m_play.setEnabled(true);
				m_play.setBackgroundResource(R.drawable.play_play_off);
				m_stop.setEnabled(false);
				m_stop.setBackgroundResource(R.drawable.play_stop_invalid);
				m_pause.setEnabled(false);
				m_pause.setBackgroundResource(R.drawable.play_pause_invalid);
				
			}
			
			if (null !=GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus
					&& GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus.equals("ERROR_OCCURRED"))
			{
				//OnPlayFailed();
			}
		}
	    	
	}
	 
	 private void InitView()
	 {
		 m_play = (Button)findViewById(R.id.Button_play);
			m_stop = (Button)findViewById(R.id.Button_stop);
			m_pause = (Button)findViewById(R.id.Button_pause);
			
			
			
			m_play.setOnTouchListener(new OnTouchListener(){      
	            @Override     
	            public boolean onTouch(View v, MotionEvent event) {   
	            		
	                    if(event.getAction() == MotionEvent.ACTION_DOWN){ 
	                    	  
	                		v.setBackgroundResource(R.drawable.play_play_on);
	                		
	                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
	                    	
	                         v.setBackgroundResource(R.drawable.play_play_off);
	                		
	                    }      
	                    return false;      
	            }      
			});
			m_play.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					GenieDebug.error("onClick", "onClick play ");
					openplay=true;
					ImageDlnaShareActivity.uuid=GenieDlnaActionDefines.m_WorkRenderUUID;		
					if(null != GenieDlnaActionDefines.m_DLNARenderStatus  &&
							null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport &&
							null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState)
					{
						if(GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PAUSED_PLAYBACK"))
						{
							System.out.println("发送~~~~~~~~~~~~~~~~");
							StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY);
							
						}else
						{
							System.out.println("发送~~~~~~~~~~~~~~~~");
							StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY);
						}
					}else
					{
						System.out.println("发送~~~~~~~~~~~~~~~~");
						StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY);
					}
				}
			});
			
			m_stop.setOnTouchListener(new OnTouchListener(){      
	            @Override     
	            public boolean onTouch(View v, MotionEvent event) {      
	                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
	                            v.setBackgroundResource(R.drawable.play_stop_on);
	                    		
	                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
	                            v.setBackgroundResource(R.drawable.play_stop_off);
	                    		
	                    }      
	                    return false;      
	            }      
			});
			
			m_stop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP);
				}
			});
			
			m_pause.setOnTouchListener(new OnTouchListener(){      
	            @Override     
	            public boolean onTouch(View v, MotionEvent event) {      
	                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
	                            v.setBackgroundResource(R.drawable.play_pause_on);
	                    		
	                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
	                            v.setBackgroundResource(R.drawable.play_pause_off);
	                    		
	                    }      
	                    return false;      
	            }      
			});
			
			m_pause.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE);
				}
			});
			
			
		 m_list = (ListView)findViewById(R.id.ListView01);
	        
		    m_listdata = new  ArrayList<GenieDlnaRenderListItem>();
		    	        
		    //m_listItemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,m_listdata);
		   
		    m_listItemAdapter = new  EfficientAdapter(this);
		    m_list.setAdapter(m_listItemAdapter);
			
		    m_list.setItemsCanFocus(false);
		    m_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		    m_list.setItemChecked(-1, true);
	        
			
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
						m_chooseuuid = GenieDlnaActionDefines.m_WorkRenderUUID;
						ImageDlnaShareActivity.uuid=GenieDlnaActionDefines.m_WorkRenderUUID;						
						//GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID = GenieDlnaActionDefines.m_WorkRenderUUID.toString();
						GenieDebug.error("onItemClick", "GenieDlnaActionDefines.m_WorkRenderUUID = "+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
					}
				}
			});
	 }
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		geniedlnashare=null;
		openplay=false;
        Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();
		System.out.println("从相册进来...");
		GenieDebug.error("onCreate", "onCreate --woindow_w == "+woindow_w);
    	
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
			setTheme(R.style.bigactivityTitlebarNoSearch);
		}else
		{
			setTheme(R.style.activityTitlebarNoSearch); 
		}
		
        
        
      GenieDebug.error("debug", "onCreate --0--");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
  		setContentView(R.layout.dlnashareview); 
  		
        if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
		}else
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		}
    	GenieDebug.error("debug", "onCreate --1--");
    	
    	
    	InitTitleView();
    	
    	m_chooseIndex = -1;
    	m_chooseuuid = null;
    	InitView();
    	
    	
    	////////////////////////////////////////////////////////////////////
		FileInputStream fis  =  null;
		String file = null;
		m_mimeType = MIME_TYPE_UNKOWN;
	
    	Intent it = getIntent();
    	if (it != null &&  it.getAction() != null && it.getAction().equals(Intent.ACTION_SEND)) 
    	{
    		GenieDebug.error("debug","0 it.getType()= "+it.getType());
        	GenieDebug.error("debug","0 getAction()= "+it.getAction());
        	GenieDebug.error("debug","0 getScheme()= "+it.getScheme());
        	
    		Bundle extras = it.getExtras();
    		if (extras.containsKey(Intent.EXTRA_STREAM)) //if (extras.containsKey("android.intent.extra.STREAM")) 
    		{
    			GenieDebug.error("debug","Intent extras ="+ extras.get(Intent.EXTRA_STREAM));
    			Uri uri = (Uri) extras.get(Intent.EXTRA_STREAM);
    			GenieDebug.error("debug","Intent uri ="+ uri.toString());
    			
    			//if(uri.toString().startsWith("content:"))
    			if(uri.getScheme().equals("content"))
    			{
    				Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);

    				cursor.moveToFirst();
    				for (int i = 0; i < cursor.getColumnCount(); i++) {
    					/*_data*/
    					GenieDebug.error("debug","Intent uri ="+i+"-"+cursor.getColumnName(i)+"-"+cursor.getString(i));
    					if(cursor.getColumnName(i).equals("_data"))
    					{
    						file = cursor.getString(i);
    						break;
    					}
    				}
    			}
    			
    			if(uri.getScheme().equals("file"))
    			{
    				file = uri.getPath();

    			}
    			
    			String type = it.getType();
            	
            	if(type != null && type.startsWith("video/"))
            	{
            		m_mimeType = MIME_TYPE_VIDEO;
            	}else if(type != null && type.startsWith("audio/"))
            	{
            		m_mimeType = MIME_TYPE_AUDIO;
            	}else if(type != null && type.startsWith("image/"))
            	{
            		m_mimeType = MIME_TYPE_IMAGE;
            	}else
            	{
            		m_mimeType = MIME_TYPE_UNKOWN;
            	}

    		}
    	}
    	
    	if (it != null &&  it.getAction() != null && it.getAction().equals(Intent.ACTION_VIEW)) 
    	{

        	Uri uri = it.getData();
        	
        	if(uri.getScheme().equals("file"))
        	{
        		file = uri.getPath();
        	}
        	String type = it.getType();
        	
        	if(type != null && type.startsWith("video/"))
        	{
        		m_mimeType = MIME_TYPE_VIDEO;
        	}else if(type != null && type.startsWith("audio/"))
        	{
        		m_mimeType = MIME_TYPE_AUDIO;
        	}else if(type != null && type.startsWith("image/"))
        	{
        		m_mimeType = MIME_TYPE_IMAGE;
        	}else
        	{
        		m_mimeType = MIME_TYPE_UNKOWN;
        	}

        	GenieDebug.error("debug","1 it.getType()= "+it.getType());
        	GenieDebug.error("debug","1 getAction()= "+it.getAction());
        	GenieDebug.error("debug","1 getScheme()= "+it.getScheme());
        	
    	}
    	
    	
    	GenieDebug.error("debug","file = "+file);
    	if(file != null)
    	{
    		ImageDlnaShareActivity.firsturl=file;
    		ImageDlnaShareActivity.imageurl=file.substring(0, file.lastIndexOf("/"))+"/";
    		try {
				fis = new FileInputStream(file);
	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			
			
	
			if(null != fis)
			{
				m_sharefileopenflag = true;
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else
			{
				m_sharefileopenflag = false;
			}
    	}else
    	{
    		m_sharefileopenflag = false;
    	}
		
    	///////////////////////////////////
    	
    	WifiManager wifiManag = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	
    	if(wifiManag.isWifiEnabled())
    	{	
    		if(m_sharefileopenflag)
    		{
    			GenieDlnaActionDefines.m_ShareFilePath = file;
    	    	 new Thread()
    			   {
    				   public void run() 
    			    	{   
    			    		GenieDebug.error("debug", " StartServer --run--");
    			    		
    			    		StartDlnaAction(GenieDlnaActionDefines.ACTION_SHAREFILE_START);
    			    	}
    			    }.start();
    		}else
    		{
    			 Toast toast = Toast.makeText(this,getResources().getString(R.string.fileopenfailed), Toast.LENGTH_SHORT);
    			   toast.setGravity(Gravity.CENTER, 0, 0);
    			   toast.show();
    		}
    		
    		

    	}else
    	{
    		sendMessage2UI(MESSAGE_SHOWOPENWIFIDIALOG);
    	}
    	
    	
    	m_play.setEnabled(true);
		m_play.setBackgroundResource(R.drawable.play_play_off);
		m_stop.setEnabled(false);
		m_stop.setBackgroundResource(R.drawable.play_stop_invalid);
		m_pause.setEnabled(false);
		m_pause.setBackgroundResource(R.drawable.play_pause_invalid);
    	
		GenieDlnaShare.geniedlnashare=this;
	}
	
	
	private void StartDlnaAction(int action)
	{		
		GenieDebug.error("debug","9999df999999 StartDlnaAction action = "+action);
		
		Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
		Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,action);
		startService(Dlna);
		GenieDebug.error("debug","9999df999999 StartDlnaAction end");

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
			System.out.println("从分享页面返回来...");
			RegisterBroadcastReceiver();
		
		}
	 
	 
	    @Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			
			 Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
			 stopService(Dlna);
			
			UnRegisterBroadcastReceiver();
			
			
	}
	
	 private class DLNAReceiver extends BroadcastReceiver{//缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�


         @Override
	        public void onReceive(Context context, Intent intent) {//閲嶅啓onReceive鏂规硶
     
        	 int action = -1;
     		
     		action = intent.getIntExtra(GenieGlobalDefines.DLNA_ACTION_RET,-1);
     		
     		GenieDebug.error("DLNAReceiver", "GenieDlnaShare onReceive DLNA_ACTION_RET ="+action);
		
     		sendMessage2UI(action);


         }
       }
    
    private DLNAReceiver m_DLNAReceiver = null;
    
    
	private  ControlActionProgressDialog m_ControlActionProgressDialog = null;
	 public void CancleControlActionProgressDialog()
	    {
	    	if(null != m_ControlActionProgressDialog)
	    	{
	    		if(m_ControlActionProgressDialog.isShowing())
	    			m_ControlActionProgressDialog.dismiss();
	    		m_ControlActionProgressDialog = null;
	    	}
	    }
	    
	    
	    public void ShowControlActionProgressDialog(int action)
	    {
	    	CancleControlActionProgressDialog();
	    	
	    	m_ControlActionProgressDialog = new ControlActionProgressDialog(this,action);
	    	m_ControlActionProgressDialog.setMessage("Please wait...");
	    	m_ControlActionProgressDialog.show();
	    }
    
	class ControlActionProgressDialog extends ProgressDialog{

		private int m_Action = -1;
		public ControlActionProgressDialog(Context context ,int action) {
			super(context);
			
			m_Action = action;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void dismiss() {
			// TODO Auto-generated method stub
			super.dismiss();
			
		}

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			
			switch(m_Action)
			{
			case GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY_SHOW:		
				//sendMessage2UI(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_SHOW);
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_SHOW:
				//sendMessage2UI(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_SHOW);
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_SHOW:
				//sendMessage2UI(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_SHOW);
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_CANCLE);
				break;	
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_SHOW:
//				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_CANCLE);
//				break;	
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_SHOW:
//				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_CANCLE);
//				break;	
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_SHOW:
//				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_CANCLE);
//				break;	
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_SHOW:
//				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_CANCLE);
//				break;	
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_SHOW:
//				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_CANCLE);
//				break;	
			}
			
			//StartDlnaAction(GenieDlnaActionDefines.ACTION_PLAYMEDIAITEM_CANCLE);
			
			GenieDebug.error("debug", "PlayItemProgressDialog onBackPressed");
		}
		
	}
	
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
            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
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
        		
        	}
          

            return convertView;
        }

        class ViewHolder {
            TextView text;
            ImageView icon;
            ImageView select;
        }
    }
	
	
	public void sendImage(String path,boolean sendflag){
		this.sendflag=sendflag;
		GenieDlnaActionDefines.m_ShareFilePath = path;
//		WifiManager wifiManag = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//    	
//    	if(wifiManag.isWifiEnabled())
//    	{	
//    		if(m_sharefileopenflag)
//    		{
//    			
//    	    	 new Thread()
//    			   {
//    				   public void run() 
//    			    	{   
//    			    		GenieDebug.error("debug", " StartServer --run--");
//    			    		
//    			    		StartDlnaAction(GenieDlnaActionDefines.ACTION_SHAREFILE_START);
//    			    	}
//    			    }.start();
//    		}else
//    		{
//    			 Toast toast = Toast.makeText(this,getResources().getString(R.string.fileopenfailed), Toast.LENGTH_SHORT);
//    			   toast.setGravity(Gravity.CENTER, 0, 0);
//    			   toast.show();
//    		}
//    		
//    	}
    	
		openplay=true;
		GenieDebug.error("onClick", "onClick play ");
		if(null != GenieDlnaActionDefines.m_DLNARenderStatus  &&
				null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport &&
				null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState)
		{
			if(GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PAUSED_PLAYBACK"))
			{
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY);
			}else
			{
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY);
			}
		}else
		{
			StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY);
		}
	}
}
