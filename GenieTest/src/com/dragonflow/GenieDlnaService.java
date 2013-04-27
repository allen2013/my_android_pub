package com.dragonflow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.dragonflow.GenieWIFIBroadcast.WIFIBroadcastCallBack;
import com.netgear.genie.media.dlna.AsyncOp;
import com.netgear.genie.media.dlna.BrowseOp;
import com.netgear.genie.media.dlna.DLNAConfig;
import com.netgear.genie.media.dlna.DLNAContainer;
import com.netgear.genie.media.dlna.DLNACore;
import com.netgear.genie.media.dlna.DLNAItem;
import com.netgear.genie.media.dlna.DLNAObject;
import com.netgear.genie.media.dlna.DLNAObjectList;
import com.netgear.genie.media.dlna.DeviceDesc;
import com.netgear.genie.media.dlna.ProgressiveBrowseOp;
import com.netgear.genie.media.dlna.QueryPositionInfoOp;
import com.netgear.genie.media.dlna.ServiceDesc;


public  class GenieDlnaService extends Service implements DLNACore.Callback, WIFIBroadcastCallBack {

	private DLNACore mCore = null;
	
	private BrowseOp m_BrowseOp = null;
	
	private ProgressiveBrowseOp m_ProgressiveBrowseOp = null;
	
	private AsyncOp m_PlayItemOp = null;
	private AsyncOp m_PlayOp = null;
	private AsyncOp m_PlayFileOp = null;
	
	private AsyncOp m_StopOp = null;
	private AsyncOp m_PauseOp = null;
	private AsyncOp m_PrevOp = null;
	private AsyncOp m_NextOp = null;
	private AsyncOp m_MuteOp = null;
	private AsyncOp m_ChangeVolumeOp = null;
	private AsyncOp m_SeekOp = null;
	private AsyncOp m_SlideOp = null;
	
	
	public GenieWIFIBroadcast m_WIFIBroadcast = null;
	
	final String dmrProtocolInfo =
		"http-get:*:image/png:*,"+
		"http-get:*:image/jpeg:*,"+
		"http-get:*:image/bmp:*,"+
		"http-get:*:image/gif:*,"+
		"http-get:*:audio/mpeg:*,"+
		"http-get:*:audio/3gpp:*,"+
		"http-get:*:audio/mp4:*,"+
		"http-get:*:audio/x-ms-wma:*,"+
		"http-get:*:audio/wav:*,"+
		"http-get:*:video/mp4:*,"+
		"http-get:*:video/mpeg:*,"+
		"http-get:*:video/x-ms-wmv:*,"+
		"http-get:*:video/x-ms-asf:*,"+
		"http-get:*:video/3gpp:*,"+
		"http-get:*:video/avi:*,"+
		"http-get:*:video/quicktime:*"
		;
	
	
	
	public interface ProgressBrowseLoadingCallback
	{
		public abstract void onProgressBrowseLoading(ArrayList<GenieDlnaDeviceInfo> arr,boolean first);
	}
	
	private static ProgressBrowseLoadingCallback  m_ProgressBrowseLoadingCallback = null;
	
	
	public static void SetProgressBrowseLoadingCallback(ProgressBrowseLoadingCallback callback)
	{
		m_ProgressBrowseLoadingCallback = callback;
	}
	
	private Handler handler = new Handler()
	{   
        public void handleMessage(Message msg) 
        {   
//        	String error = null;
//        	GenieDebug.error("GenieDlnaService", "GenieDlnaService handleMessage msg.what = "+msg.what);
//        	switch(msg.what)
//        	{
//        	//case :
//        	//	break;
//        	}
//        	

        	
			//sendbroad(msg.what);
        }
	}; 
	
	

		 
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		GenieDlnaActionDefines.InitList();
		RegisterBroadcastReceiver();
		
		m_gobackflag = false;
		m_gobackbackup = new DLNABrowseList[2];
		
      m_WIFIBroadcast = new GenieWIFIBroadcast(this);
      if(null != m_WIFIBroadcast)
      {
      	m_WIFIBroadcast.RegisterBroadcastReceiver();
      	m_WIFIBroadcast.SetOnWIFIBroadcastCallBack(this);
      	
        m_WifiSSID = null;
        m_netip = null;
        m_netid = -1;
      }
	}
	
	
	boolean isRootLevel() 
	{

		return GenieDlnaActionDefines.m_datastack.size() == 0;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		//GenieDlnaActionDefines.onDestroy();
		StopDlna();
		//GenieDlnaActionDefines.Destroy();
		UnRegisterBroadcastReceiver();
		
		 if(null != m_WIFIBroadcast)
        {
        	m_WIFIBroadcast.UnRegisterBroadcastReceiver();
        	m_WIFIBroadcast = null;
        	 m_WifiSSID = null;
             m_netip = null;
             m_netid = -1;
        }
	}
	
	
	public void sendbroad(int value)
	{
		GenieDebug.error("sendbroad", "GenieDlanService--- sendbroad value ="+value);
		Intent slide = new Intent(GenieGlobalDefines.DLNA_ACTION_BROADCAST);
		slide.putExtra(GenieGlobalDefines.DLNA_ACTION_RET, value);
		sendBroadcast(slide);
	}
	
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		int ret = super.onStartCommand(intent, flags, startId);
		
		int action = -1;

		if(intent != null)
		{
			action = intent.getIntExtra(GenieGlobalDefines.DLNA_ACTION,-1);
		
			GenieDebug.error("GenieDlnaService", "onStartCommand action ="+action);
			
			switch(action)
			{
			case GenieDlnaActionDefines.ACTION_START:
				StartDlna();
				break;
			case GenieDlnaActionDefines.ACTION_SHAREFILE_START:
				StartDlna_ShareFile();
				break;
			case GenieDlnaActionDefines.ACTION_STOP:
				StopDlna();
				break;	
			case GenieDlnaActionDefines.ACTION_BROWSE_ITEM:
				OnBrowseOp();
				break;
			case GenieDlnaActionDefines.ACTION_BROWSE_GOBACK:
				goback();
				break;	
			case GenieDlnaActionDefines.ACTION_BROWSE_REFRESH:
				RrfreshBrowseOp();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_REFRESH:
				RrfreshRenderOp();
				break;	
			case GenieDlnaActionDefines.ACTION_RENDER_CANCLE:
				CancleBrowse();
				break;	
			case GenieDlnaActionDefines.ACTION_PLAYMEDIAITEM:
				onPlayMediaItem();
				break;
			case GenieDlnaActionDefines.ACTION_PLAYMEDIAITEM_CANCLE:
				CanclePlayMediaItem();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY:
				onPlayMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_CANCLE:
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP:
				onStopMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_CANCLE:
				CanclePlayMedia();
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE:
				onPauseMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_CANCLE:
				CanclePauseMedia();
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV:
				onPrevMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_CANCLE:
				CanclePrevMedia();
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT:
				onNextMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_CANCLE:
				CancleNextMedia();
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE:
				onMuteMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_CANCLE:
				CancleMuteMedia();
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME:
				onChangeVolumeMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_CANCLE:
				CancleChangeVolumeMedia();
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK:
				onSeekMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_CANCLE:
				CancleSeekMedia();
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_QUERYPOSIINFO:
				onQueryPositionInfoMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_PLAY:
				onSlidePlayMedia();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_STOP:
				onStopSlidePlayMedia();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_ONSTOPPED:
				onRenderStateStop();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_ONPLAYING:
				onRenderStatePlaying();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_ONLOADING:
				onRenderStateLoading();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_ONPAUSED:
				onRenderStatePaused();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_ONSEEK:
				onRenderStateSeek();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_ONVOLUME:
				onRenderStateChangeVolume();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_ONERROR:
				onRenderStateError();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY:
				onPlayShareFile();
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY_CANCLE:
				CanclePlayShareFile();
				break;
			case GenieDlnaActionDefines.ACTION_DLNA_RESET:
				DlnaReset();
				break;
			case GenieDlnaActionDefines.ACTION_DLNA_SWITCHSERVER:
				SwitchServer();
				break;
			case GenieDlnaActionDefines.ACTION_DLNA_SWITCHRENDER:
				SwitchRender();
				break;
			case GenieDlnaActionDefines.ACTION_DLNA_STOPRENDER:
				StopRender();
				break;					
			case GenieDlnaActionDefines.ACTION_DLNA_SAVECONFIG:
				DlnaSaveConfig();
				break;	
			case GenieDlnaActionDefines.ACTION_DLNA_REFRESHSHARECONTENT:
				RefreshShareContent();
				break;
			case GenieDlnaActionDefines.ACTION_SERVERRESETANDBROWSE_REFRESH:
				DlnaServerResetAndBrowse();
				break;				
			case GenieDlnaActionDefines.ACTION_RENDERRESETANDBROWSE_REFRESH:
				DlnaRenderResetAndBrowse();
				break;
			case GenieDlnaActionDefines.ACTION_PROGRESSBROWSE_ITEM:
				break;
			case GenieDlnaActionDefines.ACTION_PROGRESSBROWSE_CANCLE:				
				if(m_gobackflag)
				{
					if(null != GenieDlnaActionDefines.m_datastack)
					{
						GenieDlnaActionDefines.m_datastack.push(m_gobackbackup[1]);
						GenieDlnaActionDefines.m_datastack.push(m_gobackbackup[0]);
					}
				}
				CancleProgressBrowse();
				break;		
			}
		}
		
		return ret;
	}
	
	

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void SwitchServer()
	{
		if(null != mCore && GenieDlnaActionDefines.m_DLNAConfig != null)
		{
			if(GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch.equals("1"))
			{
				mCore.enableFunction(DLNACore.FUNCTION_MEDIA_SERVER, true);
			}else
			{
				mCore.enableFunction(DLNACore.FUNCTION_MEDIA_SERVER, false);
			}
			//mCore.enableFunction(DLNACore.FUNCTION_MEDIA_SERVER, GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch);
			DlnaSaveConfig();
		}
	}
	
	
	private void RefreshShareContent()
	{
		if(null != mCore)
		{
			sendbroad(GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_SHOW);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mCore.clearMediaServerContent();
					mCore.importFileSystemToMediaServer("/mnt/sdcard",null,true);
				}
			}).start();
			
			
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					sendbroad(GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_CANCLE);
				}
			}, 5000);
			
		}
	}
	
	
	private void StopRender()
	{
		if(null != mCore )
		{
			
			mCore.enableFunction(DLNACore.FUNCTION_MEDIA_RENDERER, false);
		}
		
		if(GenieDlnaActionDefines.m_datastack != null)
		{
			GenieDlnaActionDefines.m_datastack.clear();
		}
	}
	
	private void SwitchRender()
	{
		if(null != mCore && GenieDlnaActionDefines.m_DLNAConfig != null)
		{
			if(GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch.equals("1"))
			{
				mCore.enableFunction(DLNACore.FUNCTION_MEDIA_RENDERER, true);
			}else
			{
				mCore.enableFunction(DLNACore.FUNCTION_MEDIA_RENDERER, false);
			}
			DlnaSaveConfig();
		}
	}
	
	
	
	private void DlnaServerResetAndBrowse()
	{
		GenieDebug.error("debug","GenieDlnaService DlnaServerResetAndBrowse");
		
		sendbroad(GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_SHOW);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(mCore != null)
				{
					GenieDebug.error("debug","GenieDlnaService run mCore != null");
				    mCore.stop();
				    mCore.start();
				    
				}else
				{
					GenieDebug.error("debug","GenieDlnaService run mCore == null");
					StartDlna();
				}
				
				DLNARefresh(DLNACore.FLUSH_MODE_MEDIA_SERVER_ONLY);
				
				sendbroad(GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_CANCLE);
				
			}
		}).start();
		
		
		
	}
	
	
	private void DlnaRenderResetAndBrowse()
	{
		GenieDebug.error("debug","GenieDlnaService DlnaRenderResetAndBrowse");
		
		sendbroad(GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_SHOW);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				
				if(mCore != null)
				{
					GenieDebug.error("debug","GenieDlnaService run mCore != null");
					
				    mCore.stop();
				    mCore.start();
				    
				}else
				{
					GenieDebug.error("debug","GenieDlnaService run mCore == null");
					StartDlna();
				}
				
				
				DLNARefresh(DLNACore.FLUSH_MODE_MEDIA_RENDERER_ONLY);
				
				sendbroad(GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_CANCLE);
				
			}
		}).start();
		
		
		
	}
	
	private void DlnaReset()
	{
		GenieDebug.error("debug","GenieDlnaService DlnaReset");
		
		sendbroad(GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_SHOW);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(mCore != null)
				{
				    mCore.stop();
				    mCore.start();
				    
				}else
				{
					StartDlna();
				}
				
				GenieDlnaActionDefines.m_playItem = null;
				sendbroad(GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_CANCLE);
				
			}
		}).start();
		
		
		
	}
	
	private void DlnaSaveConfig()
	{
		synchronized (this) {
			
			GenieDebug.error("debug","GenieDlnaService DlnaSaveConfig");
			if(null == GenieDlnaActionDefines.m_DLNAConfig)
				return ;
			
			GenieDebug.error("debug","GenieDlnaService DlnaSaveConfig  ServerSwitch ="+GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch);
			GenieDebug.error("debug","GenieDlnaService DlnaSaveConfig  RenderSwitch ="+GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch);
			GenieSerializ.WriteDLNAConfig(this, GenieDlnaActionDefines.m_DLNAConfig, "DLNACONFIG");
		}
	}
	
	private void StartDlna_ShareFile()
	{
		GenieDebug.error("GenieDlnaService", "StartDlna_ShareFile mCore =" +mCore);
		//if(mCore == null)
		//{
			if(mCore != null)
			{
		    	mCore.stop();
		    	mCore.dispose();
		    	mCore = null;
			}
			
			mCore = new DLNACore(this);			
			
			Build bd = new Build();
			//m_server = bd.MODEL+bd.PRODUCT;
			String FriendlyName = bd.MODEL;
			mCore.setProperty("PlatformName", "Android");
			mCore.setProperty("FriendlyName", FriendlyName);
			mCore.setProperty("OSVersion", "Android/"+Build.VERSION.RELEASE);
			
			mCore.setProperty("DMRProtocolInfo", dmrProtocolInfo);		
			mCore.start();
			
			
			
			GenieDebug.error("GenieDlnaService", "StartDlna_ShareFile 0");
			mCore.enableFunction(DLNACore.FUNCTION_CONTROL_POINT, true);
			GenieDebug.error("GenieDlnaService", "StartDlna_ShareFile 1");
			mCore.enableFunction(DLNACore.FUNCTION_MEDIA_RENDERER, true);
			GenieDebug.error("GenieDlnaService", "StartDlna_ShareFile 2"); 
		//}
	}
	
	private void StartDlna()
	{
		GenieDebug.error("GenieDlnaService", "StartDlna mCore =" +mCore);
		//if(mCore == null)
		//{
			if(mCore == null)
			{
//		    	mCore.stop();
//		    	mCore.dispose();
//		    	mCore = null;
				
				mCore = new DLNACore(this);
				
				Build bd = new Build();
				
				//m_server = bd.MODEL+bd.PRODUCT;
				String FriendlyName = bd.MODEL;
				
				GenieDebug.error("GenieDlnaService", "StartDlna FriendlyName =" +FriendlyName);
				
				mCore.setProperty("PlatformName", "Android");
				mCore.setProperty("FriendlyName", FriendlyName);
				mCore.setProperty("OSVersion", "Android\\"+Build.VERSION.RELEASE);
				mCore.setProperty("DMRProtocolInfo", dmrProtocolInfo);
				mCore.importFileSystemToMediaServer("/mnt/sdcard",null,true);
				
				GenieDlnaActionDefines.m_DLNAConfig = GenieSerializ.ReadDLNAConfig(this,"DLNACONFIG");
				if(GenieDlnaActionDefines.m_DLNAConfig == null)
				{
					GenieDebug.error("debug","78@879 GenieDlnaActionDefines.m_DLNAConfig == null");
					GenieDlnaActionDefines.m_DLNAConfig = new DLNAConfig();
					GenieDlnaActionDefines.m_DLNAConfig.configData = mCore.saveConfig();
					GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID = "";
					GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch = "1";
					GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch = "1";
				}else
				{
					GenieDebug.error("debug","78@87 GenieDlnaActionDefines.m_DLNAConfig != null");
					mCore.loadConfig(GenieDlnaActionDefines.m_DLNAConfig.configData);
				}
				
				GenieDebug.error("debug","DLNACONFIG = "+GenieDlnaActionDefines.m_DLNAConfig.configData.toString());
				
				GenieDebug.error("debug","78@879 SaveRenderUUID = "+GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID);
				GenieDebug.error("debug","78@879 RenderSwitch = "+GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch);
				GenieDebug.error("debug","78@879 ServerSwitch = "+GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch);
				
				mCore.start();
				
			
			
				
				GenieDebug.error("GenieDlnaService", "StartDlna 0");
				mCore.enableFunction(DLNACore.FUNCTION_CONTROL_POINT, true);
				GenieDebug.error("GenieDlnaService", "StartDlna 1");
				
				
		
				
				if(GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch.equals("1"))
				{
					mCore.enableFunction(DLNACore.FUNCTION_MEDIA_SERVER, true);
				}
				
				GenieDebug.error("GenieDlnaService", "StartDlna 2");
				if(GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch.equals("1"))
				{
					mCore.enableFunction(DLNACore.FUNCTION_MEDIA_RENDERER, true);
				}
				GenieDebug.error("GenieDlnaService", "StartDlna 3"); 
			}else
			{
				GenieDebug.error("GenieDlnaService", "StartDlna 4");
				mCore.enableFunction(DLNACore.FUNCTION_CONTROL_POINT, true);
				GenieDebug.error("GenieDlnaService", "StartDlna 5");
				
				
				
				
				if(GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch.equals("1"))
				{
					if(!mCore.isFunctionEnabled(DLNACore.FUNCTION_MEDIA_SERVER))
						mCore.enableFunction(DLNACore.FUNCTION_MEDIA_SERVER, true);
				}
				
				GenieDebug.error("GenieDlnaService", "StartDlna 6");
				if(GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch.equals("1"))
				{
					if(!mCore.isFunctionEnabled(DLNACore.FUNCTION_MEDIA_RENDERER))
						mCore.enableFunction(DLNACore.FUNCTION_MEDIA_RENDERER, true);
				}
				GenieDebug.error("GenieDlnaService", "StartDlna 7"); 
				
				sendbroad(GenieDlnaActionDefines.ACTION_RET_SERVERLISTCHANGED);
				
				sendbroad(GenieDlnaActionDefines.ACTION_RET_RENDERLISTCHANGED);
			}
				

		//}
	}
	
	
	private void StopDlna()
	{
		GenieDebug.error("GenieDlnaService", "StopDlna 0");
		if(mCore != null)
		{
			GenieDebug.error("GenieDlnaService", "StopDlna 1");
	    	mCore.stop();
	    	GenieDebug.error("GenieDlnaService", "StopDlna 2");
	    	mCore.dispose();
	    	GenieDebug.error("GenieDlnaService", "StopDlna 3");
	    	mCore = null;
		}
		GenieDebug.error("GenieDlnaService", "StopDlna 4");
		GenieDlnaActionDefines.Destroy();
		GenieDebug.error("GenieDlnaService", "StopDlna 5");
		//DlnaSaveConfig();
		
	}
	
	public void onRenderStateError()
	{
		if(null != mCore)
		{
			mCore.dmrReportErrorStatus(true);
		}
	}
	public void onRenderStateChangeVolume()
	{
		if(null != mCore)
		{
			mCore.dmrReportVolume(GenieDlnaActionDefines.m_PlayViewVolume, GenieDlnaActionDefines.m_PlayViewMuted);
		}
	}
	public void onRenderStateSeek()
	{
		if(null != mCore)
		{
			mCore.dmrReportProgress(GenieDlnaActionDefines.m_PlayViewCurrentMillis, GenieDlnaActionDefines.m_PlayViewTotalMillis);
		}
	}
	public void onRenderStatePaused()
	{
		if(null != mCore)
		{
			mCore.dmrReportState(mCore.DMR_STATE_PAUSED);
		}
	}
	public void onRenderStateLoading()
	{
		if(null != mCore)
		{
			mCore.dmrReportState(mCore.DMR_STATE_LOADING);
		}
	}
	public void onRenderStatePlaying()
	{
		if(null != mCore)
		{
			mCore.dmrReportState(mCore.DMR_STATE_PLAYING);
		}
	}
	
	public void onRenderStateStop()
	{
		if(null != mCore)
		{
			mCore.dmrReportState(mCore.DMR_STATE_STOPPED);
		}
	}
	
	
	public void DLNARefresh(int func)
	{
		if(mCore!=null){
			mCore.flushDeviceList(func);
			mCore.searchDevices(30);
		}
	}
	
	
	public void RrfreshRenderOp()
	{	
		DLNARefresh(DLNACore.FLUSH_MODE_MEDIA_RENDERER_ONLY);
    }
	
	public void RrfreshBrowseOp()
	{
		
		if(isRootLevel())
    	{
			DLNARefresh(DLNACore.FLUSH_MODE_MEDIA_SERVER_ONLY);
    	}else
    	{
    		m_gobackflag = false;
    		DLNABrowseList browselist = null;
    		browselist = GenieDlnaActionDefines.m_datastack.pop();
			
    		GenieDlnaActionDefines.m_parentobjid = browselist.parentobjid;
			
			browselist.objlist.dispose();
			
			//Browse(GenieDlnaActionDefines.m_currentDeviceUUID,GenieDlnaActionDefines.m_parentobjid);
			ProgressBrowse(GenieDlnaActionDefines.m_currentDeviceUUID,GenieDlnaActionDefines.m_parentobjid);
    	}
	}
	
	private boolean m_gobackflag = false;
	private DLNABrowseList[] m_gobackbackup = null;
	
	
	
	 public void  goback()
	    {
		 	CancleProgressBrowse();
	    	if(GenieDlnaActionDefines.m_datastack.size() > 1)
	    	{
	    		m_gobackflag = true;
	    		
	    		DLNABrowseList browselist = null;
	    		browselist = GenieDlnaActionDefines.m_datastack.pop();
	    		m_gobackbackup[0] = browselist;
	    		
	    		browselist.objlist.dispose();
	    		
	    		
	    		
	    		browselist  = GenieDlnaActionDefines.m_datastack.pop();
	    		
	    		m_gobackbackup[1] = browselist;
	    		
	    		GenieDlnaActionDefines.m_parentobjid = browselist.parentobjid;
				
				browselist.objlist.dispose();
				
				GenieDlnaActionDefines.m_playItem = null;
				GenieDlnaActionDefines.m_BrowseItemId = -1;
				
				ProgressBrowse(GenieDlnaActionDefines.m_currentDeviceUUID,GenieDlnaActionDefines.m_parentobjid);
				//Browse(GenieDlnaActionDefines.m_currentDeviceUUID,GenieDlnaActionDefines.m_parentobjid);
	  
	    	}else
	    	{
	    		if(!GenieDlnaActionDefines.m_datastack.isEmpty())
	    		{
	    			DLNABrowseList browselist  = GenieDlnaActionDefines.m_datastack.pop();
	    			browselist.objlist.dispose();
	    			GenieDlnaActionDefines.m_datastack.clear();
	    		}

	    		sendbroad(GenieDlnaActionDefines.ACTION_RET_SERVERLISTCHANGED);
	    	}
	    }
	 
	 
	 
	 
	 
	 public void CancleProgressBrowse()
		{
			synchronized (this) {
				
				GenieDebug.error("debug", "CancleProgressBrowse 0");
				
				if(m_ProgressiveBrowseOp != null)
				{
					GenieDebug.error("debug", "CancleProgressBrowse 1");
					//if(!m_BrowseOp.isDisposed())
					m_ProgressiveBrowseOp.abort();
					GenieDebug.error("debug", "CancleProgressBrowse 2");
					m_ProgressiveBrowseOp.dispose();
					GenieDebug.error("debug", "CancleProgressBrowse 3");
					m_ProgressiveBrowseOp = null;
				}
				GenieDebug.error("debug", "CancleProgressBrowse 4");
			}
			
			
		}
	 
	 
	    final public static int Size_M = (1024*1024);
	    final public static int Size_K = 1024;
	    
	    
	    
	    
	    public void PushListData(DLNAObject[] arr,boolean clear)
	    {
	    	
//			if(GneieDlna.m_listdata == null)
//				return ;
//				
//			if(clear)
//				GneieDlna.m_listdata.clear();
	    	
	    	
			
	    	if(GenieDlnaActionDefines.m_listdatabackup == null)
				return ;
				
			if(clear)
				GenieDlnaActionDefines.m_listdatabackup.clear();
			
	    	for(DLNAObject obj:arr)
    		{

    				GenieDlnaDeviceInfo device = new GenieDlnaDeviceInfo();
        			
        			boolean isDLNAContainer = obj instanceof DLNAContainer;
    	
            		device.m_deviceId = obj.getObjectId();

            		device.m_objectId = obj.getObjectId();
            		device.m_title = obj.getTitle();
            		device.m_iconflag = 0;
            		device.m_bitmap = null;
            		
        			if(isDLNAContainer)
        			{	
        				device.m_container = 1;
        				device.m_filestyle = 0;
        				device.m_ResourceSize = null;
        				
        			}else
        			{
        				device.m_container = 2;
        				long ResourceSize = obj.getResourceSize();
        				device.m_long_ResourceSize=ResourceSize;
        				
        				if(ResourceSize > 0)
        				{
        					if(ResourceSize >= Size_M)
        					{
        						
        						float mb = ResourceSize/(float)Size_M;
        						device.m_ResourceSize = String.format("%.2fMB", mb);
        					}else if(ResourceSize >= Size_K)
        					{
        						float kb = ResourceSize/(float)Size_K;
        						device.m_ResourceSize = String.format("%.2fKB", kb);
        					}else
        					{
        						device.m_ResourceSize = Long.toString(ResourceSize)+"Bytes";
        					}
        				}else
        				{
        					device.m_ResourceSize = null;
        				}
        				
        				String upnpclass = obj.getUpnpClass();
        				
        				String mimetype[] = {"image/png","image/jpg"};
        				
        				if(upnpclass.startsWith("object.item.videoItem"))
        				{
        					device.m_filestyle = 1;	
        					device.m_iconUrl = obj.findThumbnailURL(40, 40, mimetype);
        					
        					//GenieDebug.error("debug", "device.m_iconUrl = "+device.m_iconUrl);
        				}else if(upnpclass.startsWith("object.item.audioItem"))
        				{
        					device.m_filestyle = 2;	
     
        					device.m_iconUrl = obj.findThumbnailURL(40, 40, mimetype);
        					
        					//GenieDebug.error("debug", "device.m_iconUrl = "+device.m_iconUrl);
        				}
        				else if(upnpclass.startsWith("object.item.imageItem"))
        				{
        					device.m_filestyle = 3;	
        					
        					device.m_iconUrl = obj.findThumbnailURL(40, 40, mimetype);
        					
        					//GenieDebug.error("debug", "device.m_iconUrl = "+device.m_iconUrl);
        					
        				}else
        				{
        					device.m_filestyle = 0;	
        					device.m_iconUrl = null;
        				}
        					
        				
        			}
            		
            		
        			GenieDlnaActionDefines.m_listdatabackup.add(device);
        			
    			}
	    }
		public void ProgressBrowse(UUID uuid,String Title)
		{
			//BrowseOp op = null;
			
			//m_BrowseOp
			
				
			
			try {
				//List<String> v = mCore.queryStateVariables(GenieDlnaActionDefines.m_Serverlist.get(0).getUuid(), "urn:upnp-org:serviceId:ContentDirectory", new String[]{"SystemUpdateID"});
				//GenieDebug.error("debug","JNICore "+ v.get(0));
				
				//GenieDlnaActionDefines.m_currentDeviceUUID = GenieDlnaActionDefines.m_Serverlist.get(GenieDlnaActionDefines.m_BrowseItemId).getUuid();
			
				CancleProgressBrowse();
				GenieDlnaActionDefines.m_OnSlidePlay = false;
				GenieDlnaActionDefines.m_BrowseItemId = -1;
				
				m_ProgressiveBrowseOp = mCore.browseMediaServerEx(uuid, Title, 100, new ProgressiveBrowseOp.Callback() {
					
					@Override
					public void onFinished(ProgressiveBrowseOp op) {
						// TODO Auto-generated method stub
						
						GenieDebug.error("debug", "ProgressiveBrowseOp onFinished 0");
						if(m_ProgressiveBrowseOp != null)
						{					
							m_ProgressiveBrowseOp.dispose();
							m_ProgressiveBrowseOp = null;
						}
						
						m_gobackflag = false;
						
						//GenieDlnaActionDefines.clearbackuplistdata();  /////
						
						GenieDebug.error("debug", "ProgressiveBrowseOp onFinished 1");
						sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_FINISHED);
					}
					
					@Override
					public void onBrowseResult(ProgressiveBrowseOp op, int startingIndex,
							int numberReturned, int totalMatches, DLNAObject[] arr) {
						// TODO Auto-generated method stub
						synchronized (this) {
							
							try{
									m_gobackflag = false;
									
									GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult startingIndex = "+startingIndex);
									GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult numberReturned = "+numberReturned);
									GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult totalMatches = "+totalMatches);
									
									if(startingIndex == 0 && numberReturned == 0 && totalMatches == 0)
									{
										sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
										sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_FAIL);
										return ;
									}
									if(m_ProgressiveBrowseOp == null)
										return ;
									
									GenieDlnaActionDefines.m_ListLoadingItem = startingIndex;
									GenieDlnaActionDefines.m_ListLoadingAddNum = numberReturned;
									GenieDlnaActionDefines.m_ListToalItem = totalMatches;
									if(startingIndex == 0 && numberReturned > 0)
									{
										GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult 4");
										DLNABrowseList browselist  = new DLNABrowseList();
										
										browselist.parentobjid = GenieDlnaActionDefines.m_parentobjid;
										browselist.objlist = new DLNAObjectList(arr);
										
										GenieDlnaActionDefines.m_datastack.push(browselist);
										
										PushListData(arr,true);
										
										if(m_ProgressBrowseLoadingCallback != null)
										{
											
											m_ProgressBrowseLoadingCallback.onProgressBrowseLoading(GenieDlnaActionDefines.m_listdatabackup,true);
										}
										
										
										sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_START);
										//sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
									}else
									{
										GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult 5");
		//								if(null != GenieDlnaActionDefines.m_ListLoading)
		//									GenieDlnaActionDefines.m_ListLoading = null;
		//								
		//								
		//								GenieDlnaActionDefines.m_ListLoading = arr;
										
										DLNABrowseList browselist  = GenieDlnaActionDefines.m_datastack.peek();
		
							    		browselist.objlist.append(new DLNAObjectList(arr));
							    		
							    		PushListData(arr,false);
							    		
							    		if(m_ProgressBrowseLoadingCallback != null)
										{
											m_ProgressBrowseLoadingCallback.onProgressBrowseLoading(GenieDlnaActionDefines.m_listdatabackup,false);
										}
							    		
							    		
		//					    		handler.postDelayed(new Runnable() {
		//									
		//									@Override
		//									public void run() {
		//										// TODO Auto-generated method stub
		//										sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_PROGRESS);
		//									}
		//								}, 50);
										//sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_PROGRESS);
									}
									
									//加载完成关闭加载框
									System.out.println("加载项详情："+startingIndex+"-"+numberReturned+"-"+totalMatches);
									if((startingIndex+numberReturned)>=totalMatches){
										sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
									}
									
									GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult 6");
							}catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}
				});
				
				sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_SHOW);
				
				
			} catch (Exception e) {
				// failed
				sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
				e.printStackTrace();
			}
		}	    
		
//		public void ProgressBrowse(UUID uuid,String Title)
//		{
//			//BrowseOp op = null;
//			
//			//m_BrowseOp
//			
//				
//			
//			try {
//				//List<String> v = mCore.queryStateVariables(GenieDlnaActionDefines.m_Serverlist.get(0).getUuid(), "urn:upnp-org:serviceId:ContentDirectory", new String[]{"SystemUpdateID"});
//				//GenieDebug.error("debug","JNICore "+ v.get(0));
//				
//				//GenieDlnaActionDefines.m_currentDeviceUUID = GenieDlnaActionDefines.m_Serverlist.get(GenieDlnaActionDefines.m_BrowseItemId).getUuid();
//			
//				CancleProgressBrowse();
//				GenieDlnaActionDefines.m_OnSlidePlay = false;
//				GenieDlnaActionDefines.m_BrowseItemId = -1;
//				
//				m_ProgressiveBrowseOp = mCore.browseMediaServerEx(uuid, Title, 100, new ProgressiveBrowseOp.Callback() {
//					
//					@Override
//					public void onFinished(ProgressiveBrowseOp op) {
//						// TODO Auto-generated method stub
//						
//						GenieDebug.error("debug", "ProgressiveBrowseOp onFinished 0");
//						if(m_ProgressiveBrowseOp != null)
//						{					
//							m_ProgressiveBrowseOp.dispose();
//							m_ProgressiveBrowseOp = null;
//						}
//						
//						//GenieDlnaActionDefines.clearbackuplistdata();  /////
//						
//						GenieDebug.error("debug", "ProgressiveBrowseOp onFinished 1");
//						sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_FINISHED);
//					}
//					
//					@Override
//					public void onBrowseResult(ProgressiveBrowseOp op, int startingIndex,
//							int numberReturned, int totalMatches, DLNAObject[] arr) {
//						// TODO Auto-generated method stub
//						synchronized (this) {
//							
//							GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult startingIndex = "+startingIndex);
//							GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult numberReturned = "+numberReturned);
//							GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult totalMatches = "+totalMatches);
//							
//							if(startingIndex == 0 && numberReturned == 0 && totalMatches == 0)
//							{
//								sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
//								sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_FAIL);
//								return ;
//							}
//							if(m_ProgressiveBrowseOp == null)
//								return ;
//							
//							GenieDlnaActionDefines.m_ListLoadingItem = startingIndex;
//							GenieDlnaActionDefines.m_ListLoadingAddNum = numberReturned;
//							GenieDlnaActionDefines.m_ListToalItem = totalMatches;
//							if(startingIndex == 0 && numberReturned > 0)
//							{
//								GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult 4");
//								DLNABrowseList browselist  = new DLNABrowseList();
//								
//								browselist.parentobjid = GenieDlnaActionDefines.m_parentobjid;
//								browselist.objlist = new DLNAObjectList(arr);
//								
//								GenieDlnaActionDefines.m_datastack.push(browselist);
//								
//								PushListData(arr,true);
//								
//								sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_START);
//								sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
//							}else
//							{
//								GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult 5");
////								if(null != GenieDlnaActionDefines.m_ListLoading)
////									GenieDlnaActionDefines.m_ListLoading = null;
////								
////								
////								GenieDlnaActionDefines.m_ListLoading = arr;
//								
//								DLNABrowseList browselist  = GenieDlnaActionDefines.m_datastack.peek();
//
//					    		browselist.objlist.append(new DLNAObjectList(arr));
//					    		
//					    		PushListData(arr,false);
//					    		
////					    		handler.postDelayed(new Runnable() {
////									
////									@Override
////									public void run() {
////										// TODO Auto-generated method stub
////										sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_PROGRESS);
////									}
////								}, 50);
//								sendbroad(GenieDlnaActionDefines.ACTION_RET_PROGRESSBROWSE_PROGRESS);
//							}
//							
//							GenieDebug.error("debug", "ProgressiveBrowseOp onBrowseResult 6");
//							
//						}
//					}
//				});
//				
//				sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_SHOW);
//				
//				
//			} catch (Exception e) {
//				// failed
//				sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
//				e.printStackTrace();
//			}
//		}
		
	 
	 
	public void CancleBrowse()
	{
		synchronized (this) {
			if(m_BrowseOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_BrowseOp.abort();
				
				m_BrowseOp.dispose();
				m_BrowseOp = null;
			}
		}
	}
	
	public void Browse(UUID uuid,String Title)
	{
		//BrowseOp op = null;
		
		//m_BrowseOp
		
		
		try {
			List<String> v = mCore.queryStateVariables(GenieDlnaActionDefines.m_Serverlist.get(0).getUuid(), "urn:upnp-org:serviceId:ContentDirectory", new String[]{"SystemUpdateID"});
			GenieDebug.error("debug","JNICore "+ v.get(0));
			
			//GenieDlnaActionDefines.m_currentDeviceUUID = GenieDlnaActionDefines.m_Serverlist.get(GenieDlnaActionDefines.m_BrowseItemId).getUuid();
		
			
			m_BrowseOp = mCore.browseMediaServer(uuid, Title, false, new BrowseOp.Callback() {
				public void onFinished(BrowseOp op) {
					GenieDebug.error("debug","JNICore "+  "NICE!!!");
					synchronized (this) {
	
						if(m_BrowseOp == null)
							return ;
						if (op.succeeded()) {
							
							DLNABrowseList browselist  = new DLNABrowseList();
							
							browselist.parentobjid = GenieDlnaActionDefines.m_parentobjid;
							browselist.objlist = op.getObjectList();
							
							DLNAObjectList objList2 = op.getObjectList();
							
							GenieDlnaActionDefines.m_datastack.push(browselist);
							
							GenieDebug.error("debug","JNICore "+ "BrowseOp succeeded, total " + objList2.size());
							for (int i = 0; i < objList2.size(); i++) {
								DLNAObject mediaObj = objList2.get(i);
								GenieDebug.error("debug","JNICore "+ "Title: " + mediaObj.getTitle());
								GenieDebug.error("debug","JNICore "+ "id: " + mediaObj.getObjectId());
								GenieDebug.error("debug","JNICore "+ "parentId: " + mediaObj.getParentId());
								GenieDebug.error("debug","JNICore "+ "upnpClass: " + mediaObj.getUpnpClass());
								GenieDebug.error("debug","JNICore "+ "url: " + mediaObj.findThumbnailURL(48, 48, null));
							}
							//objList.dispose();
							
							sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSE_ITEM);
						}else
						{
							
						}
						m_BrowseOp.dispose();
						m_BrowseOp = null;
						sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
						
					}
				}
			});
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			GenieDlnaActionDefines.m_BrowseItemId = -1;
			sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_SHOW);

		} catch (Exception e) {
			// failed
			sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE);
		}
	}
	
	public void OnBrowseOp()
	{

		
		if(isRootLevel())
		{
			GenieDebug.error("debug", "isRootLevel() = true");
			if (!GenieDlnaActionDefines.m_Serverlist.isEmpty() && GenieDlnaActionDefines.m_BrowseItemId>=0 && GenieDlnaActionDefines.m_BrowseItemId<GenieDlnaActionDefines.m_Serverlist.size()) {
				
				GenieDlnaActionDefines.m_currentDeviceUUID = GenieDlnaActionDefines.m_Serverlist.get(GenieDlnaActionDefines.m_BrowseItemId).getUuid();
				GenieDlnaActionDefines.m_parentobjid = "0";
				//Browse(GenieDlnaActionDefines.m_currentDeviceUUID,"0");
				ProgressBrowse(GenieDlnaActionDefines.m_currentDeviceUUID,"0");
			}
		}else
		{
			m_gobackflag = false;
			
			GenieDebug.error("debug", "isRootLevel() = false");
			
			
			DLNABrowseList browselist  = GenieDlnaActionDefines.m_datastack.peek();
			
			if(GenieDlnaActionDefines.m_BrowseItemId < 0 &&
					GenieDlnaActionDefines.m_BrowseItemId >= browselist.objlist.size())
				return ;

			DLNAObject obj = browselist.objlist.get(GenieDlnaActionDefines.m_BrowseItemId);
			
			if(obj == null)
				return ;
			
			boolean isDLNAContainer = obj instanceof DLNAContainer;
			
			GenieDlnaActionDefines.m_parentobjid = obj.getObjectId();
			
			if(isDLNAContainer)
			{
				GenieDlnaActionDefines.m_playItem = null;
				
				ProgressBrowse(GenieDlnaActionDefines.m_currentDeviceUUID,GenieDlnaActionDefines.m_parentobjid);
				//Browse(GenieDlnaActionDefines.m_currentDeviceUUID,GenieDlnaActionDefines.m_parentobjid);
				
			}else
			{
				GenieDlnaActionDefines.m_playItem = null;
				GenieDlnaActionDefines.m_playItem = (DLNAItem)obj;
				
				if(GenieDlnaActionDefines.m_WorkRenderUUID != null)
				{
					GenieDebug.error("debug", "GenieDlnaActionDefines.m_WorkRenderUUID != null");
					onPlayMediaItem();
				}else
				{
					GenieDebug.error("debug", "GenieDlnaActionDefines.m_WorkRenderUUID == null");
					sendbroad(GenieDlnaActionDefines.ACTION_RET_BROWSE_CHOOSERENDER);
				}
			}
			
		}
	}
	
	
	/*
	 * QueryPositionInfomedia
	 */

	
	
	public void onQueryPositionInfoMedia()
	{
		

		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			
			
			try{
				
				QueryPositionInfoOp QueryPositionInfo = null;
				
				QueryPositionInfo = mCore.queryMediaPositionInfo(GenieDlnaActionDefines.m_WorkRenderUUID,
						new QueryPositionInfoOp.Callback() {
							
							@Override
							public void onFinished(QueryPositionInfoOp op) {
								// TODO Auto-generated method stub
								if(op.succeeded())
								{
									GenieDlnaActionDefines.m_PlayPosition = (op.getTrackTime()/1000);
								}else
								{
									
								}
								op.dispose();
								op = null;
								sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAYPOSIREFRESH);							}
						});
				
				
				
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			
		}
	}
	
	/*
	 * Prevmedia
	 */
	public void CanclePrevMedia()
	{
		synchronized (this) {
			if(m_PrevOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_PrevOp.abort();
				
				m_PrevOp.dispose();
				m_PrevOp = null;
			}
		}
	}
	
	
	public void onPrevMedia()
	{
		
		
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			if(GenieDlnaActionDefines.m_datastack.isEmpty())
				return ;
			
			if(GenieDlnaActionDefines.m_playItem == null)
				return ;
			
			DLNABrowseList browselist  = GenieDlnaActionDefines.m_datastack.peek();

			int size  = browselist.objlist.size();
			if(size <= 0)
				return;
			
			if(GenieDlnaActionDefines.m_BrowseItemId >= size || 
					GenieDlnaActionDefines.m_BrowseItemId < 0)
				GenieDlnaActionDefines.m_BrowseItemId = 0;
			
			if(GenieDlnaActionDefines.m_BrowseItemId == 0)
			{
				GenieDlnaActionDefines.m_BrowseItemId = size -1;
			}else
			{
				GenieDlnaActionDefines.m_BrowseItemId = GenieDlnaActionDefines.m_BrowseItemId - 1;
			}
			
			
			for(int i = GenieDlnaActionDefines.m_BrowseItemId; i >= 0; i--)
			{
				DLNAObject obj = browselist.objlist.get(i);
				if(obj == null)
					return ;
				boolean isDLNAContainer = obj instanceof DLNAContainer;
				
				if(!isDLNAContainer)
				{
					GenieDlnaActionDefines.m_playItem = null;
					GenieDlnaActionDefines.m_playItem = (DLNAItem)obj;
					GenieDlnaActionDefines.m_BrowseItemId = i;
					break;
				}
				
				if(i == 0)
					i = size-1;
			}
			
			try{
				
				sendbroad(GenieDlnaActionDefines.ACTION_RET_PREVPLAY_START);
				//AsyncOp playop = null;
				m_PrevOp = null;
				m_PrevOp = mCore.playMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						GenieDlnaActionDefines.m_playItem,new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_PrevOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
								
									sendbroad(GenieDlnaActionDefines.ACTION_RET_PREVPLAY_SUCCEEDED);
								}else
								{
									sendbroad(GenieDlnaActionDefines.ACTION_RET_PREVPLAY_FAILED);
								}
								
								m_PrevOp.dispose();
								m_PrevOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_RENDER);
		}
	}
	
	/*
	 * Nextmedia
	 */
	public void CancleNextMedia()
	{
		synchronized (this) {
			if(m_NextOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_NextOp.abort();
				
				m_NextOp.dispose();
				m_NextOp = null;
			}
		}
	}
	
	
	public void onNextMedia()
	{
		
		
		GenieDebug.error("debug", "onNextMedia");
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			if(GenieDlnaActionDefines.m_datastack.isEmpty())
				return ;
			
			if(GenieDlnaActionDefines.m_playItem == null)
				return ;
			
			DLNABrowseList browselist  = GenieDlnaActionDefines.m_datastack.peek();

			int size  = browselist.objlist.size();
			if(size <= 0)
				return;
			
			if(GenieDlnaActionDefines.m_BrowseItemId >= size || 
					GenieDlnaActionDefines.m_BrowseItemId < 0)
				GenieDlnaActionDefines.m_BrowseItemId = 0;
			
			if(GenieDlnaActionDefines.m_BrowseItemId == (size -1))
			{
				GenieDlnaActionDefines.m_BrowseItemId = 0;
			}else
			{
				GenieDlnaActionDefines.m_BrowseItemId = GenieDlnaActionDefines.m_BrowseItemId + 1;
			}
			
			for(int i = GenieDlnaActionDefines.m_BrowseItemId; i<size; i++)
			{
				DLNAObject obj = browselist.objlist.get(i);
				if(obj == null)
					return ;
				
				boolean isDLNAContainer = obj instanceof DLNAContainer;
				
				if(!isDLNAContainer)
				{
					GenieDlnaActionDefines.m_playItem = null;
					GenieDlnaActionDefines.m_playItem = (DLNAItem)obj;
					GenieDlnaActionDefines.m_BrowseItemId = i;
					break;
				}
				
				if(i == (size-1))
					i = 0;
			}
			
			GenieDebug.error("debug", "onNextMedia m_BrowseItemId = "+GenieDlnaActionDefines.m_BrowseItemId);
			
			try{
				
				//AsyncOp playop = null;
				sendbroad(GenieDlnaActionDefines.ACTION_RET_NEXTPLAY_START);
				m_NextOp = null;
				m_NextOp = mCore.playMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						GenieDlnaActionDefines.m_playItem,new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_NextOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
									sendbroad(GenieDlnaActionDefines.ACTION_RET_NEXTPLAY_SUCCEEDED);
								}else
								{
									sendbroad(GenieDlnaActionDefines.ACTION_RET_NEXTPLAY_FAILED);
								}
								
								m_NextOp.dispose();
								m_NextOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_RENDER);
		}
	}
	
	
	/*
	 * SlidePlayMedia
	 */
	public void onStopSlidePlayMedia()
	{
		
		synchronized (this) {
			if(m_SlideOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_SlideOp.abort();
				
				m_SlideOp.dispose();
				m_SlideOp = null;
			}
			
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
		}
	}
	
	
	public void onSlidePlayMedia()
	{
		
		
		if(GenieDlnaActionDefines.m_BrowseItemId < 0)
		{
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
			return ;
		}
		
		GenieDebug.error("debug", "m_SlideOp");
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			if(GenieDlnaActionDefines.m_datastack.isEmpty())
			{
				GenieDlnaActionDefines.m_OnSlidePlay = false;
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
				return ;
			}
			
			DLNABrowseList browselist  = GenieDlnaActionDefines.m_datastack.peek();

			int size  = browselist.objlist.size();
			if(size <= 0)
			{
				GenieDlnaActionDefines.m_OnSlidePlay = false;
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
				return ;
			}
			
			if(GenieDlnaActionDefines.m_BrowseItemId >= size || 
					GenieDlnaActionDefines.m_BrowseItemId < 0)
				GenieDlnaActionDefines.m_BrowseItemId = 0;
			
			if(GenieDlnaActionDefines.m_BrowseItemId == (size -1))
			{
				GenieDlnaActionDefines.m_BrowseItemId = 0;
			}else
			{
				GenieDlnaActionDefines.m_BrowseItemId = GenieDlnaActionDefines.m_BrowseItemId + 1;
			}
			
			for(int i = GenieDlnaActionDefines.m_BrowseItemId; i<size; i++)
			{
				DLNAObject obj = browselist.objlist.get(i);
				if(obj == null)
					return ;
				
				boolean isDLNAContainer = obj instanceof DLNAContainer;
				
				String upnpclass = obj.getUpnpClass();
				
				if(!isDLNAContainer && upnpclass.startsWith("object.item.imageItem"))
				{
					GenieDlnaActionDefines.m_playItem = null;
					GenieDlnaActionDefines.m_playItem = (DLNAItem)obj;
					GenieDlnaActionDefines.m_BrowseItemId = i;
					break;
				}
				
				if(i == (size-1))
					i = 0;
			}
			
			GenieDebug.error("debug", "m_SlideOp m_BrowseItemId = "+GenieDlnaActionDefines.m_BrowseItemId);
			
			try{
				
				//AsyncOp playop = null;
				m_SlideOp = null;
				sendbroad(GenieDlnaActionDefines.ACTION_RET_SLIDEPLAY_START);
				m_SlideOp = mCore.playMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						GenieDlnaActionDefines.m_playItem,new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_SlideOp == null)
									return ;
								
								if(!GenieDlnaActionDefines.m_OnSlidePlay)
									return ;
								
								
								if(op.succeeded())
								{
									sendbroad(GenieDlnaActionDefines.ACTION_RET_SLIDEPLAY_SUCCEEDED);
								}else
								{
									sendbroad(GenieDlnaActionDefines.ACTION_RET_SLIDEPLAY_FAILED);
								}
								
								m_SlideOp.dispose();
								m_SlideOp = null;
								
								
								GenieDebug.error("debug", "m_SlideOp m_OnSlidePlaySpeedIndex  = "+GenieDlnaActionDefines.m_OnSlidePlaySpeedIndex);
								
								handler.postDelayed(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										
										GenieDebug.error("debug", "m_SlideOp handler GenieDlnaActionDefines.m_OnSlidePlaySpeedIndex  = "+GenieDlnaActionDefines.m_OnSlidePlaySpeedIndex);
										
										if(!GenieDlnaActionDefines.m_OnSlidePlay)
											return ;
										
										onSlidePlayMedia();
									}
								}, GenieDlnaActionDefines.m_Speed[GenieDlnaActionDefines.m_OnSlidePlaySpeedIndex]);
								
								sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
							}
						});
				GenieDlnaActionDefines.m_OnSlidePlay = true;
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
				
			}catch(Exception e)
			{
				// 
				GenieDlnaActionDefines.m_OnSlidePlay = false;
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
				e.printStackTrace();
			}
				
		}else
		{
			sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_RENDER);
		}
	}
	
	
	
	
	
	
	/*
	 * SeekMedia
	 */
	public void CancleSeekMedia()
	{
		synchronized (this) {
			if(m_SeekOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_SeekOp.abort();
				
				m_SeekOp.dispose();
				m_SeekOp = null;
			}
		}
	}
	
	
	public void onSeekMedia()
	{
		
		
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			try{
				
				//AsyncOp playop = null;
				m_SeekOp = null;
				m_SeekOp = mCore.seekMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						GenieDlnaActionDefines.m_SeekTimeInMillis,new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_SeekOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
									//sendbroad(GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW);
								}else
								{
									
								}
								
								m_SeekOp.dispose();
								m_SeekOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			
		}
	}

	
	/*
	 * ChangeVolumeMedia
	 */
	public void CancleChangeVolumeMedia()
	{
		synchronized (this) {
			if(m_ChangeVolumeOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_ChangeVolumeOp.abort();
				
				m_ChangeVolumeOp.dispose();
				m_ChangeVolumeOp = null;
			}
		}
	}
	
	
	public void onChangeVolumeMedia()
	{
		
		
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			try{
				
				//AsyncOp playop = null;
				m_ChangeVolumeOp = null;
				m_ChangeVolumeOp = mCore.changeMediaVolume(GenieDlnaActionDefines.m_WorkRenderUUID,
						GenieDlnaActionDefines.m_Volume,new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_ChangeVolumeOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
									//sendbroad(GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW);
								}else
								{
									
								}
								
								m_ChangeVolumeOp.dispose();
								m_ChangeVolumeOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			
		}
	}
	
	/*
	 * MuteMedia
	 */
	public void CancleMuteMedia()
	{
		synchronized (this) {
			if(m_MuteOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_MuteOp.abort();
				
				m_MuteOp.dispose();
				m_MuteOp = null;
			}
		}
	}
	
	
	public void onMuteMedia()
	{
		
		
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			try{
				
				//AsyncOp playop = null;
				m_MuteOp = null;
				m_MuteOp = mCore.muteMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						GenieDlnaActionDefines.m_Mute,new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_MuteOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
									//sendbroad(GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW);
								}else
								{
									
								}
								
								m_MuteOp.dispose();
								m_MuteOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			
		}
	}
	
	/*
	 * PauseMedia
	 */
	public void CanclePauseMedia()
	{
		synchronized (this) {
			if(m_PauseOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_PauseOp.abort();
				
				m_PauseOp.dispose();
				m_PauseOp = null;
			}
		}
	}
	
	
	public void onPauseMedia()
	{
		
		
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			try{
				
				//AsyncOp playop = null;
				m_PauseOp = null;
				m_PauseOp = mCore.pauseMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_PauseOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
									//sendbroad(GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW);
								}else
								{
									
								}
								
								m_PauseOp.dispose();
								m_PauseOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			
		}
	}
	
	/*
	 * StopMedia
	 */
	public void CancleStopMedia()
	{
		synchronized (this) {
			if(m_StopOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_StopOp.abort();
				
				m_StopOp.dispose();
				m_StopOp = null;
			}
		}
	}
	
	
	public void onStopMedia()
	{
		
		
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			try{
				
				//AsyncOp playop = null;
				m_StopOp = null;
				m_StopOp = mCore.stopMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_StopOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
									//sendbroad(GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW);
								}else
								{
									
								}
								
								m_StopOp.dispose();
								m_StopOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			//sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_RENDER);
		}
	}
	
	/*
	 * playmedia
	 */
	public void CanclePlayMedia()
	{
		synchronized (this) {
			if(m_PlayOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_PlayOp.abort();
				
				m_PlayOp.dispose();
				m_PlayOp = null;
			}
		}
	}
	
	
	public void onPlayMedia()
	{
		
	
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			try{
				
				if(null == GenieDlnaActionDefines.m_playItem)
				{
					sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_SOURCE);
					return ;
				}
				
				//AsyncOp playop = null;
				sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAY_START);
				m_PlayOp = null;
				m_PlayOp = mCore.playMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						null, new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								if(m_PlayOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
									sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAY_SUCCEEDED);
								}else
								{
									sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAY_FAILED);
								}
								
								m_PlayOp.dispose();
								m_PlayOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_RENDER);
		}
	}
	
	
	
	
	
	/*
	 * PlayShareFile
	 */
	
	public void CanclePlayShareFile()
	{
		synchronized (this) {
			if(m_PlayFileOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_PlayFileOp.abort();
				
				m_PlayFileOp.dispose();
				m_PlayFileOp = null;
			}
		}
	}
	
	public void onPlayShareFile()
	{
		if(null == GenieDlnaActionDefines.m_ShareFilePath)
			return ;
		
		
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			try{
				
				//GenieDebug.error(tag, msg)
				//AsyncOp playop = null;
				m_PlayFileOp = null;
				m_PlayFileOp = mCore.playFile(GenieDlnaActionDefines.m_WorkRenderUUID,
						GenieDlnaActionDefines.m_ShareFilePath, new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								GenieDebug.error("debug", "m_PlayFileOp onFinished op.succeeded() "+(op.succeeded()));
								
								if(m_PlayFileOp == null)
									return ;
								
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE);
								if(op.succeeded())
								{
									//sendbroad(GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW);
								}else
								{
									sendbroad(GenieDlnaActionDefines.ACTION_PLAY_FAIL);
								}
								
								m_PlayFileOp.dispose();
								m_PlayFileOp = null;
								
								
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_SHAREFILE_PLAY_SHOW);
				GenieDlnaShare.openplay=true;
			}catch(Exception e)
			{
				// 
				sendbroad(GenieDlnaActionDefines.ACTION_PLAY_FAIL);
				e.printStackTrace();
			}
				
		}else
		{
			sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_RENDER);
		}
	}
	
	/*
	 * playmediaItem
	 */
	public void CanclePlayMediaItem()
	{
		synchronized (this) {
			if(m_PlayItemOp != null)
			{
				//if(!m_BrowseOp.isDisposed())
				m_PlayItemOp.abort();
				
				m_PlayItemOp.dispose();
				m_PlayItemOp = null;
			}
		}
	}
	

	public void onPlayMediaItem()
	{
		if(null == GenieDlnaActionDefines.m_playItem)
		{
			sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_SOURCE);
			return ;
		}
		
		
		if(GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			try{
				
				//AsyncOp playop = null;
				sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAY_START);
				m_PlayItemOp = null;
				m_PlayItemOp = mCore.playMedia(GenieDlnaActionDefines.m_WorkRenderUUID,
						GenieDlnaActionDefines.m_playItem, new AsyncOp.Callback() {
							
							@Override
							public void onFinished(AsyncOp op) {
								// TODO Auto-generated method stub
								
								GenieDebug.error("debug", "playMedia onFinished op.succeeded() "+(op.succeeded()));
								
								if(m_PlayItemOp == null)
									return ;
								
//								if(op.isDisposed())
//									return;
								
								
								
								if(op.succeeded())
								{
									sendbroad(GenieDlnaActionDefines.ACTION_RET_TOPLAYCONTROLVIEW);
								}else
								{
									
								}
								
								m_PlayItemOp.dispose();
								m_PlayItemOp = null;
								
								sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_CANCLE);
							}
						});
				
				sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_SHOW);
			}catch(Exception e)
			{
				// 
				e.printStackTrace();
			}
				
		}else
		{
			sendbroad(GenieDlnaActionDefines.ACTION_RET_SELECT_RENDER);
		}
	}

	@Override
    public void onMediaServerListChanged()
	{
		
		GenieDlnaActionDefines.m_Serverlist = mCore.snapshotMediaServerList();
    	
    	if(isRootLevel())
    	{
    		sendbroad(GenieDlnaActionDefines.ACTION_RET_SERVERLISTCHANGED);
    	}else
    	{
    		if(null != GenieDlnaActionDefines.m_currentDeviceUUID)
    		{
    			if(!GenieDlnaActionDefines.findServerByList(GenieDlnaActionDefines.m_currentDeviceUUID))
    			{
    				GenieDebug.error("debug","uuid not in serverlist");
    				int size = GenieDlnaActionDefines.m_datastack.size();
    				
    				for(int i = 0;i<size;i++)
    				{
    					DLNABrowseList browselist  = GenieDlnaActionDefines.m_datastack.pop();
    					browselist.objlist.dispose();
    				}
    				GenieDlnaActionDefines.m_datastack.clear();
    				
    				sendbroad(GenieDlnaActionDefines.ACTION_RET_SERVERLISTCHANGED);
    			}
    		}
    	}
		
		
		for (DeviceDesc desc : GenieDlnaActionDefines.m_Serverlist) {
			GenieDebug.error("JNICore", "onMediaServerListChanged 78@87 "+desc.getFriendlyName());
			GenieDebug.error("JNICore", "onMediaServerListChanged 78@87 "+desc.getUuid().toString());
			GenieDebug.error("JNICore", "onMediaServerListChanged "+"Service count: " + desc.getServiceCount());
    		for (int i = 0; i < desc.getServiceCount(); i++) {
    			ServiceDesc serviceDesc = desc.getService(i);
    			GenieDebug.error("JNICore", "onMediaServerListChanged "+serviceDesc.getServiceId());
    			GenieDebug.error("JNICore", "onMediaServerListChanged "+serviceDesc.getServiceType());
    		}
    		
//    		GenieDebug.error("JNICore", "Icon count: " + desc.getIconCount());
//    		for (int i = 0; i < desc.getIconCount(); i++) {
//    			IconDesc iconDesc = desc.getIcon(i);
//    			GenieDebug.error("JNICore", "onMediaServerListChanged "+"width: " + iconDesc.getWidth());
//    			GenieDebug.error("JNICore", "onMediaServerListChanged "+"height: " +iconDesc.getHeight());
//    			GenieDebug.error("JNICore", "onMediaServerListChanged "+"mimetype: " + iconDesc.getMimeType());
//    			GenieDebug.error("JNICore", "onMediaServerListChanged "+"dataLen: " + iconDesc.getIconData().length);
//    		}
    	}
    	
//    	if (!GenieDlnaActionDefines.m_Serverlist.isEmpty()) {
//    		BrowseOp op = null;
//    		try {
//    			List<String> v = mCore.queryStateVariables(GenieDlnaActionDefines.m_Serverlist.get(0).getUuid(), "urn:upnp-org:serviceId:ContentDirectory", new String[]{"SystemUpdateID"});
//    			Log.i("JNICore", v.get(0));
//    			
//    			op = mCore.browseMediaServer(GenieDlnaActionDefines.m_Serverlist.get(0).getUuid(), "0", false, new BrowseOp.Callback() {
//					public void onFinished(BrowseOp op) {
//						Log.i("JNICore", "NICE!!!");
//						if (op.succeeded()) {
//							DLNAObjectList objList = op.getObjectList();
//							Log.i("JNICore", "BrowseOp succeeded, total " + objList.size());
//							for (int i = 0; i < objList.size(); i++) {
//								DLNAObject mediaObj = objList.get(i);
//								Log.i("JNICore", "Title: " + mediaObj.getTitle());
//								Log.i("JNICore", "id: " + mediaObj.getObjectId());
//								Log.i("JNICore", "parentId: " + mediaObj.getParentId());
//								Log.i("JNICore", "upnpClass: " + mediaObj.getUpnpClass());
//								Log.i("JNICore", "url: " + mediaObj.findThumbnailURL(48, 48, null));
//							}
//							objList.dispose();
//						}
//						op.dispose();
//					}
//				});
//
//    		} catch (Exception e) {
//    			// failed
//    		}
    		
    		//if (op != null) {
    		//	op.abort();
    		//	op.dispose();
    		//}
   // 	}
    	

    }
	
	
	
	@Override
	public void onMediaRendererListChanged() {
		
		GenieDebug.error("debug","JNICore onMediaRendererListChanged");
		
		GenieDlnaActionDefines.m_Rendererlist = mCore.snapshotMediaRendererList();
    	for (DeviceDesc desc : GenieDlnaActionDefines.m_Rendererlist) {
    		GenieDebug.error("debug","JNICore"+desc.getFriendlyName());
    		GenieDebug.error("debug","JNICore"+desc.getUuid().toString());
    	}
		
		if(GenieDlnaActionDefines.m_DLNAConfig != null &&
				GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID != null)
		{
			if(GenieDlnaActionDefines.m_WorkRenderUUID == null)
			{
				try{
					GenieDlnaActionDefines.m_WorkRenderUUID = UUID.fromString(GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		if(!GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			GenieDlnaActionDefines.m_WorkRenderUUID = null;
		}
    	
    	sendbroad(GenieDlnaActionDefines.ACTION_RET_RENDERLISTCHANGED);
    }


	@Override
	public void onMediaServerStateVariablesChanged(UUID deviceUuid,
			String serviceId, String[] names, String[] values) {
		// TODO Auto-generated method stub
		GenieDebug.error("debug","onMediaServerStateVariablesChanged deviceUuid = "+deviceUuid.toString());
		GenieDebug.error("debug","onMediaServerStateVariablesChanged serviceId = "+serviceId);
		for(String strname : names)
		{
			GenieDebug.error("debug","onMediaServerStateVariablesChanged strname = "+strname);
		}
		
		for(String strvalues : values)
		{
			GenieDebug.error("debug","onMediaServerStateVariablesChanged strvalues = "+strvalues);
		}
	}


	@Override
	public void onMediaRendererStateVariablesChanged(UUID deviceUuid,
			String serviceId, String[] names, String[] values) {
		// TODO Auto-generated method stub
		
		
		
		//GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState = "0";
		
		synchronized (this) {
			
			GenieDebug.error("debug","onMediaRendererStateVariablesChanged deviceUuid = "+deviceUuid.toString());
			GenieDebug.error("debug","onMediaRendererStateVariablesChanged serviceId = "+serviceId);
			
			
			if(GenieDlnaActionDefines.m_WorkRenderUUID != null
					&& GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID)
					&& deviceUuid.equals(GenieDlnaActionDefines.m_WorkRenderUUID))
			{
				
				DeviceDesc render = GenieDlnaActionDefines.GetRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID);
				if(null != render)
				{
					try{
						List<String> v = mCore.queryStateVariables(GenieDlnaActionDefines.m_WorkRenderUUID, "urn:upnp-org:serviceId:RenderingControl", new String[]{"Volume"});
						GenieDebug.error("debug","volume "+ v.get(0));
					
						GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Volume = Integer.parseInt(v.get(0));
					
						GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_Volume = "
							+GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Volume);
						
						List<String> m = mCore.queryStateVariables(GenieDlnaActionDefines.m_WorkRenderUUID, "urn:upnp-org:serviceId:RenderingControl", new String[]{"Mute"});
						GenieDebug.error("debug","volume "+ m.get(0));
					
						GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Mute = Integer.parseInt(m.get(0));
					
						GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_Mute = "
							+GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Mute);
						
						if(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Mute == 1)
						{
							GenieDlnaActionDefines.m_Mute = true;
						}else
						{
							GenieDlnaActionDefines.m_Mute = false;
						}
						
						List<String> AVTransport = mCore.queryStateVariables(GenieDlnaActionDefines.m_WorkRenderUUID, "urn:upnp-org:serviceId:AVTransport", 
								new String[]{"CurrentTrackDuration","TransportState","TransportStatus"});
						GenieDebug.error("debug","AVTransport CurrentTrackDuration = "+ AVTransport.get(0));
					
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_CurrentTrackDuration = AVTransport.get(0);
						GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_CurrentTrackDuration = "
								+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_CurrentTrackDuration);
						if(null != mCore)
						{
							GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis = mCore.parseTrackDuration(AVTransport.get(0));
							GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_TrackDurationInMillis = "
									+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis);
						}else
						{
							GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis = -1;
						}
						
						GenieDebug.error("debug","AVTransport TransportState = "+ AVTransport.get(1));
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState = AVTransport.get(1);
						GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_TransportState = "
								+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState);
					
						GenieDebug.error("debug","AVTransport TransportStatus = "+ AVTransport.get(2));
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus = AVTransport.get(2);
						GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_TransportStatus = "
								+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus);
						
						String maxvolume = render.findServiceById("urn:upnp-org:serviceId:RenderingControl").findStateVariableByName("Volume").getAllowedValueRangeMaximum();
						GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_MaxVolume = Integer.parseInt(maxvolume);
							
						GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_MaxVolume = "
								+GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_MaxVolume);
						
						
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
					
				}else
				{
					GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_MaxVolume = -1;
				}
				
				
				if(serviceId.equals("urn:upnp-org:serviceId:AVTransport"))
				{
					int size = names.length;
					for(int i = 0; i < size;i++)
					{
						if(names[i].equals("TransportStatus"))
						{
							GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus = values[i];
							
							GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_TransportStatus = "
									+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus);
						}
						if(names[i].equals("TransportState"))
						{
							GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState = values[i];
							GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_TransportState = "
									+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState);
						}
						if(names[i].equals("CurrentTrackDuration"))
						{
							GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_CurrentTrackDuration = values[i];
							GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_CurrentTrackDuration = "
									+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_CurrentTrackDuration);
							if(null != mCore)
							{
								GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis = mCore.parseTrackDuration(values[i]);
								GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_TrackDurationInMillis = "
										+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis);
							}else
							{
								GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis = -1;
							}
						}
					}
				}
				
				if(serviceId.equals("urn:upnp-org:serviceId:RenderingControl"))
				{
					int size = names.length;
					for(int i = 0; i < size;i++)
					{
						if(names[i].equals("Volume"))
						{
							try{
								GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Volume = Integer.parseInt(values[i]);
								
								
								GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_Volume = "
										+GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Volume);
							}catch (Exception e) {
								// TODO: handle exception
							}
							
						}
						if(names[i].equals("Mute"))
						{
							try{
								GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Mute = Integer.parseInt(values[i]);
							
								GenieDebug.error("debug","onMediaRendererStateVariablesChanged m_Mute = "
										+GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Mute);
							}catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
			}
			
			if(GenieDlnaActionDefines.m_WorkRenderUUID == null
					&& !GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
			{
				GenieDlnaActionDefines.m_WorkRenderUUID = null;
				
				sendbroad(GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH);
			}
			
//			for(String strname : names)
//			{
//				GenieDebug.error("debug","onMediaRendererStateVariablesChanged strname = "+strname);
//			}
//			
//			for(String strvalues : values)
//			{
//				GenieDebug.error("debug","onMediaRendererStateVariablesChanged strvalues = "+strvalues);
//			}
		}
		
		
		
	}


	@Override
	public void dmrOpen(String url, String mimeType, String metaData) {
		// TODO Auto-generated method stub
		GenieDebug.error("debug","dmrOpen url = "+url);
		GenieDebug.error("debug","dmrOpen mimeType = "+mimeType);
		GenieDebug.error("debug","dmrOpen metaData = "+metaData);
		
		GenieDlnaActionDefines.m_PlayViewCurrentMillis = -1;
		GenieDlnaActionDefines.m_PlayViewTotalMillis = -1;
		GenieDlnaActionDefines.m_PlayViewVolume = -1;
		GenieDlnaActionDefines.m_PlayViewMuted = false;
		GenieDlnaActionDefines.m_playStyle = -1;
		GenieDlnaActionDefines.m_PlayViewSeekTimeInMillis = -1;
		GenieDlnaActionDefines.m_PlayViewSetVolume = -1;
		GenieDlnaActionDefines.m_PlayViewSetMuted = false;
		
		GenieDlnaActionDefines.m_PlayViewLoading = true;
		
		GenieDlnaActionDefines.m_playurl = url;
		
		if(mimeType.startsWith("image/"))
		{
			GenieDlnaActionDefines.m_playStyle = 3;
			if(GenieDlnaActionDefines.m_isImagePlayView)
			{
				sendbroad(GenieDlnaActionDefines.ACTION_RENDER_TOIMAGEREPLAY);
			}else{
				sendbroad(GenieDlnaActionDefines.ACTION_RENDER_TOIMAGEPLAY);
			}
			
		}else
		{
			if(mimeType.startsWith("video/"))
			{
				GenieDlnaActionDefines.m_playStyle = 1;
			}else if(mimeType.startsWith("audio/"))
			{
				GenieDlnaActionDefines.m_playStyle = 2;
			}
			
			if(GenieDlnaActionDefines.m_isVideoPlayVIew)
			{
				sendbroad(GenieDlnaActionDefines.ACTION_RENDER_TOVIDEOREPLAY);
			}else
			{
				sendbroad(GenieDlnaActionDefines.ACTION_RENDER_TOVIDEOPLAY);
			}
		}
		if(null !=  mCore)
		{
			mCore.dmrReportState(mCore.DMR_STATE_LOADING);
		}
			
	}

	@Override
	public void dmrPlay() {
		// TODO Auto-generated method stub
		if(!GenieDlnaActionDefines.m_PlayViewLoading)
			sendbroad(GenieDlnaActionDefines.ACTION_RENDER_SETPLAYING);
	}


	@Override
	public void dmrPause() {
		// TODO Auto-generated method stub
		sendbroad(GenieDlnaActionDefines.ACTION_RENDER_SETPAUSED);
	}


	@Override
	public void dmrStop() {
		// TODO Auto-generated method stub
		sendbroad(GenieDlnaActionDefines.ACTION_RENDER_SETSTOPPED);
	}


	@Override
	public void dmrSeekTo(long timeInMillis) {
		// TODO Auto-generated method stub
		GenieDebug.error("debug","dmrSeekTo timeInMillis = "+timeInMillis);

		GenieDlnaActionDefines.m_PlayViewSeekTimeInMillis = timeInMillis;
		
		sendbroad(GenieDlnaActionDefines.ACTION_RENDER_SETSEEKTO);
	}


	@Override
	public void dmrSetMute(boolean mute) {
		// TODO Auto-generated method stub
		GenieDebug.error("debug","dmrSetMute mute = "+mute);

		
		GenieDlnaActionDefines.m_PlayViewSetMuted = mute;
		sendbroad(GenieDlnaActionDefines.ACTION_RENDER_SETMUTED);
	}


	@Override
	public void dmrSetVolume(int volume) {
		// TODO Auto-generated method stub
		GenieDebug.error("debug","dmrSetMute volume = "+volume);
		GenieDlnaActionDefines.m_PlayViewSetVolume = volume;
		sendbroad(GenieDlnaActionDefines.ACTION_RENDER_SETVOLUME);
		
	}
	
	
	
	 private void UnRegisterBroadcastReceiver()
	 {
		 if(null != m_WIFIReceiver)
			{
				unregisterReceiver(m_WIFIReceiver);
				m_WIFIReceiver = null;
			}
	 }

	private WifiManager m_wifiManager = null;
	 private void RegisterBroadcastReceiver()
	 {
		 UnRegisterBroadcastReceiver();
		 
		 m_WIFIReceiver = new WIFIReceiver();
			IntentFilter filter = new IntentFilter();//鍒涘缓IntentFilter瀵硅薄
			filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			//filter.addAction(WifiManager.EXTRA_PREVIOUS_WIFI_STATE);
			//filter.addAction(WifiManager.EXTRA_SUPPLICANT_CONNECTED);
			//filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
			 

			 

			registerReceiver(m_WIFIReceiver, filter);//娉ㄥ唽Broadcast Receiver
			
			m_wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	 }

	
 
	 private void sendMessage2UI(int msg)
	    {
	    	
	    	handler.sendEmptyMessage(msg);
	    }
	
	
	private class WIFIReceiver extends BroadcastReceiver{//缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�


        @Override
	        public void onReceive(Context context, Intent intent) {//閲嶅啓onReceive鏂规硶
    
       	 	int action = -1;
       	 	
       	 
    		
//       	 GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive intent.getAction() ="+intent.getAction());
       	 	
    		action = intent.getIntExtra("wifi_state",-1);
    		
//    		GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive DLNA_ACTION_RET ="+action);

    		int previous_wifi_state = intent.getIntExtra("previous_wifi_state",-1);
    		
//    		GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive previous_wifi_state ="+previous_wifi_state);
    		
    		boolean connected = intent.getBooleanExtra("connected",false);
    		
//    		GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive connected ="+connected);
    		
    		
    		WifiInfo test =	m_wifiManager.getConnectionInfo();
    		if(test != null)
    		{
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test != null");
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test.ip ="+test.getIpAddress());
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test.mac ="+test.getMacAddress());
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test.NetworkId ="+test.getNetworkId());
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test.ssid ="+test.getSSID());
    		}else
    		{
//    			 GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test == null");
    		}
//    		GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive m_wifiManager.getWifiState() ="+m_wifiManager.getWifiState());
    		sendMessage2UI(action);


       	 	
        }
      }
	
	 private WIFIReceiver m_WIFIReceiver = null;



	    private String m_WifiSSID = null;
	    private String m_netip = null;
	    private int m_netid = -1;

	@Override
	public void CallBack(String action,String ip, int Value, int n) {
		// TODO Auto-generated method stub
		
		
//		GenieDebug.error("debug","WIFI CallBack action = "+action);
//		GenieDebug.error("debug","WIFI CallBack ip = "+ip);
//		GenieDebug.error("debug","WIFI CallBack Value = "+Value);
//		GenieDebug.error("debug","WIFI CallBack n = "+n);
		
		if(m_WifiSSID == null &&
				m_netip == null &&
				m_netid == -1 )
		{
			if(action != null &&
					n >= 0 &&
					Value >= 0)
			{
				m_WifiSSID =  action;
				m_netip = ip;
				m_netid = Value;
			}
		}else
		{
			if(action != null &&
					n >= 0 &&
					Value >= 0)
			{
				if(!m_WifiSSID.equals(action) || 
						!m_netip.equals(ip) 
						|| m_netid != Value)
				{
					
					CancleProgressBrowse();
					GenieDlnaActionDefines.clearbackuplistdata();
					
					
					
					if(mCore != null)
					{
					    mCore.stop();
					    mCore.start();
					    
					}else
					{
						StartDlna();
					}
					
					
					DLNARefresh(DLNACore.FLUSH_MODE_ALL);
					
//					GenieDebug.error("debug","WIFI CallBack  net changed!!");
					
					m_WifiSSID =  action;
					m_netip = ip;
					m_netid = Value;
				}
			}
		}
		
	}
	
//    public void toggleConnector(View view)
//    {
//    	boolean checked = ((ToggleButton)view).isChecked();
//    	if (checked) {
//    		mCore.start();
//    	} else {
//    		mCore.stop();
//    	}
//    }
//
//    public void toggleControlPoint(View view)
//    {
//    	boolean checked = ((ToggleButton)view).isChecked();
//    	mCore.enableFunction(DLNACore.FUNCTION_CONTROL_POINT, checked);
//    }
//
//    public void toggleMediaServer(View view)
//    {
//    	boolean checked = ((ToggleButton)view).isChecked();
//    	mCore.enableFunction(DLNACore.FUNCTION_MEDIA_SERVER, checked);
//    }
//
//    public void toggleMediaRenderer(View view)
//    {
//    	boolean checked = ((ToggleButton)view).isChecked();
//    	mCore.enableFunction(DLNACore.FUNCTION_MEDIA_RENDERER, checked);
//    }
//    
//    public void searchAll(View view)
//    {
//    	mCore.flushDeviceList();
//    	mCore.searchDevices(5);
//    }
	

	
	
}
