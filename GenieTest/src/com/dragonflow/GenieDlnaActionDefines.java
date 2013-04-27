package com.dragonflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import android.R.bool;

import com.netgear.genie.media.dlna.DLNAConfig;
import com.netgear.genie.media.dlna.DLNAItem;
import com.netgear.genie.media.dlna.DLNAObject;
import com.netgear.genie.media.dlna.DLNAObjectList;
import com.netgear.genie.media.dlna.DLNARenderStatus;
import com.netgear.genie.media.dlna.DeviceDesc;

public class GenieDlnaActionDefines {
	final public static int ACTION_START = 1000;
	final public static int ACTION_STOP = 1001;
	final public static int ACTION_BROWSE_ITEM = 1002;
	final public static int ACTION_BROWSE_GOBACK = 1003;
	final public static int ACTION_BROWSE_REFRESH = 1004;
	final public static int ACTION_RENDER_REFRESH = 1005;
	final public static int ACTION_RENDER_CANCLE = 1006;
	final public static int ACTION_PLAYMEDIAITEM = 1007;
	final public static int ACTION_PLAYMEDIAITEM_CANCLE = 1008;
	
	final public static int ACTION_CONTROL_MEDIA_PLAY = 1009;
	final public static int ACTION_CONTROL_MEDIA_STOP = 1010;
	final public static int ACTION_CONTROL_MEDIA_PAUSE = 1011;
	final public static int ACTION_CONTROL_MEDIA_PREV = 1012;
	final public static int ACTION_CONTROL_MEDIA_NEXT = 1013;
	final public static int ACTION_CONTROL_MEDIA_MUTE = 1014;
	final public static int ACTION_CONTROL_MEDIA_CHANGEVOLUME = 1015;
	final public static int ACTION_CONTROL_MEDIA_SEEK = 1016;
	final public static int ACTION_CONTROL_MEDIA_QUERYPOSIINFO = 1017;
	final public static int ACTION_CONTROL_MEDIA_PLAY_CANCLE = 1018;
	final public static int ACTION_CONTROL_MEDIA_STOP_CANCLE = 1019;
	final public static int ACTION_CONTROL_MEDIA_PAUSE_CANCLE = 1020;
	final public static int ACTION_CONTROL_MEDIA_PREV_CANCLE = 1021;
	final public static int ACTION_CONTROL_MEDIA_NEXT_CANCLE = 1022;
	final public static int ACTION_CONTROL_MEDIA_MUTE_CANCLE = 1023;
	final public static int ACTION_CONTROL_MEDIA_CHANGEVOLUME_CANCLE = 1024;
	final public static int ACTION_CONTROL_MEDIA_SEEK_CANCLE = 1025;
	final public static int ACTION_CONTROL_MEDIA_QUERYPOSIINFO_CANCLE = 1026;
	final public static int ACTION_CONTROL_MEDIA_PLAY_SHOW = 1027;
	final public static int ACTION_CONTROL_MEDIA_STOP_SHOW = 1028;
	final public static int ACTION_CONTROL_MEDIA_PAUSE_SHOW = 1029;
	final public static int ACTION_CONTROL_MEDIA_PREV_SHOW = 1030;
	final public static int ACTION_CONTROL_MEDIA_NEXT_SHOW = 1031;
	final public static int ACTION_CONTROL_MEDIA_MUTE_SHOW = 1032;
	final public static int ACTION_CONTROL_MEDIA_CHANGEVOLUME_SHOW = 1033;
	final public static int ACTION_CONTROL_MEDIA_SEEK_SHOW = 1034;
	final public static int ACTION_CONTROL_MEDIA_QUERYPOSIINFO_SHOW = 1035;	
	final public static int ACTION_CONTROL_VIEW_REFRESH = 1036;
	final public static int ACTION_CONTROL_MEDIA_SLIDE_PLAY = 1037;
	final public static int ACTION_CONTROL_MEDIA_SLIDE_STOP = 1038;
	final public static int ACTION_CONTROL_SHAREFILE_PLAY_SHOW = 1039;
	final public static int ACTION_CONTROL_SHAREFILE_PLAY_CANCLE = 1040;
	final public static int ACTION_CONTROL_SHAREFILE_PLAY = 1041;
	
	
	final public static int ACTION_SERVERRESETANDBROWSE_REFRESH = 1042;
	final public static int ACTION_RENDERRESETANDBROWSE_REFRESH = 1043;
	
	final public static int ACTION_SHAREFILE_START = 1050;
	
	final public static int ACTION_RENDER_TOVIDEOPLAY = 1100;
	final public static int ACTION_RENDER_TOIMAGEPLAY = 1101;
	final public static int ACTION_RENDER_TOVIDEOREPLAY = 1102;
	final public static int ACTION_RENDER_TOIMAGEREPLAY = 1103;
	
	

	
	final public static int ACTION_RENDER_ONSTOPPED = 1104;
	final public static int ACTION_RENDER_ONPLAYING = 1105;
	final public static int ACTION_RENDER_ONLOADING = 1106;
	final public static int ACTION_RENDER_ONPAUSED = 1107;
	final public static int ACTION_RENDER_ONSEEK = 1108;
	final public static int ACTION_RENDER_ONVOLUME = 1109;
	final public static int ACTION_RENDER_ONERROR = 1110;
	
	final public static int ACTION_RENDER_SETSTOPPED = 1111;
	final public static int ACTION_RENDER_SETPLAYING = 1112;
	final public static int ACTION_RENDER_SETPAUSED = 1113;
	final public static int ACTION_RENDER_SETSEEKTO = 1114;
	final public static int ACTION_RENDER_SETMUTED = 1115;
	final public static int ACTION_RENDER_SETVOLUME = 1116;
	
	final public static int ACTION_RENDER_CHENGED = 1117;
	
	
	final public static int ACTION_PLAY_FAIL = 1500;
	
	
	final public static int ACTION_RET_SERVERLISTCHANGED = 2000;
	final public static int ACTION_RET_RENDERLISTCHANGED = 2001;
	final public static int ACTION_RET_BROWSE_ITEM = 2002;
	final public static int ACTION_RET_BROWSEPROGRESSDIALOG_SHOW = 2003;
	final public static int ACTION_RET_BROWSEPROGRESSDIALOG_CANCLE = 2004;
	final public static int ACTION_RET_BROWSE_CHOOSERENDER = 2005;
	final public static int ACTION_RET_TOPLAYCONTROLVIEW = 2006;
	final public static int ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_SHOW = 2007;
	final public static int ACTION_RET_PLAYMEDIAITEMPROGRESSDIALOG_CANCLE = 2008;
	final public static int ACTION_RET_CONTROLPROGRESSDIALOG_SHOW = 2009;
	final public static int ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE = 2010;
	final public static int ACTION_RET_PLAYPOSIREFRESH = 2011;
	
	final public static int ACTION_RET_PREVPLAY_SUCCEEDED = 2012;
	final public static int ACTION_RET_NEXTPLAY_SUCCEEDED = 2013;
	final public static int ACTION_RET_PLAY_SUCCEEDED = 2014;
	final public static int ACTION_RET_SLIDEPLAY_SUCCEEDED = 2015;
	
	
	final public static int ACTION_RET_PREVPLAY_FAILED = 2016;
	final public static int ACTION_RET_NEXTPLAY_FAILED = 2017;
	final public static int ACTION_RET_PLAY_FAILED = 2018;
	final public static int ACTION_RET_SLIDEPLAY_FAILED = 2019;
	
	final public static int ACTION_RET_PREVPLAY_START = 2020;
	final public static int ACTION_RET_NEXTPLAY_START = 2021;
	final public static int ACTION_RET_PLAY_START = 2022;
	final public static int ACTION_RET_SLIDEPLAY_START = 2023;
	
	final public static int ACTION_RET_PROGRESSBROWSE_START = 2024;
	final public static int ACTION_RET_PROGRESSBROWSE_PROGRESS = 2025;
	final public static int ACTION_RET_PROGRESSBROWSE_FINISHED = 2026;
	final public static int ACTION_RET_PROGRESSBROWSE_FAIL = 2027;
	
	
	final public static int ACTION_PROGRESSBROWSE_ITEM = 2028;
	final public static int ACTION_PROGRESSBROWSE_CANCLE = 2029;
	
	final public static int ACTION_RET_SELECT_SOURCE = 2030;
	final public static int ACTION_RET_SELECT_RENDER = 2031;
	
	final public static int ACTION_DLNA_RESET = 3000;
	final public static int ACTION_DLNA_SWITCHSERVER = 3001;
	final public static int ACTION_DLNA_SWITCHRENDER = 3002;
	final public static int ACTION_DLNA_SAVECONFIG = 3003;
	final public static int ACTION_DLNA_REFRESHSHARECONTENT = 3004;
	final public static int ACTION_DLNA_STOPRENDER = 3005;
	
	final public static int ACTION_DLNA_OPTOIN_SHOW = 4000;
	final public static int ACTION_DLNA_OPTOIN_CANCLE = 4001;
	
	public static int m_BrowseItemId = -1 ;
	
	public static String m_parentobjid = null;
	
	public static  List<DeviceDesc> m_Serverlist = null;
	public static  List<DeviceDesc> m_Rendererlist= null;
	public static  Stack<DLNABrowseList> m_datastack = null;
	
	public static UUID m_currentDeviceUUID = null;
	public static UUID m_WorkRenderUUID = null;
	public static DLNAItem m_playItem = null;
	public static String m_playurl = null;
	public static int m_playStyle = -1;
	
	public static DLNAConfig m_DLNAConfig = null;
	
	public static boolean m_Mute = false;
	public static int m_Volume = -1;
	public static int m_SeekTimeInMillis = -1;
	
	public static int m_PlayPosition = -1;
	public static boolean m_OnSlidePlay = false;
	public static int m_OnSlidePlaySpeedIndex = -1;
	
	public static boolean m_isImagePlayView = false;
	public static boolean m_isVideoPlayVIew = false;
	
	
	public static long m_PlayViewCurrentMillis = -1;
	public static long m_PlayViewTotalMillis = -1;
	public static int m_PlayViewVolume = -1;
	public static boolean m_PlayViewMuted = false;
	
	public static long m_PlayViewSeekTimeInMillis = -1;
	public static int m_PlayViewSetVolume = -1;
	public static boolean m_PlayViewSetMuted = false;
	
	public static boolean m_PlayViewLoading = false;
	
	public static String m_ShareFilePath = null;
	
	public static int m_ListToalItem = 0;
	public static int m_ListLoadingItem = 0;
	public static int m_ListLoadingAddNum = 0;
	
	final public static int m_Speed[] = {
		30000,
		20000,
		10000,
	};
	
	public static DLNAObject[]  m_ListLoading = null;
	
	public static DLNARenderStatus m_DLNARenderStatus  = null;
	
	
	public static ArrayList<GenieDlnaDeviceInfo> m_listdatabackup = null;

	static public void InitList()
	{
		m_Serverlist =  new ArrayList<DeviceDesc>();
		m_Rendererlist =  new ArrayList<DeviceDesc>();
		m_datastack = new Stack<DLNABrowseList>();
		
		m_listdatabackup = new  ArrayList<GenieDlnaDeviceInfo>();
		
		m_BrowseItemId = -1;
		m_ListToalItem = 0;
		m_ListLoadingItem = 0;
		m_ListLoadingAddNum = 0;
		m_DLNARenderStatus  = new DLNARenderStatus();
		//m_DLNAConfig = new DLNAConfig();
		m_PlayViewCurrentMillis = -1;
		m_PlayViewTotalMillis = -1;
		m_PlayViewVolume = -1;
		m_PlayViewMuted = false;
		m_playStyle = -1;
		m_PlayViewSeekTimeInMillis = -1;
		m_PlayViewSetVolume = -1;
		m_PlayViewSetMuted = false;
		m_PlayViewLoading = false;
		//m_ShareFilePath = null;
		
		m_ListLoading = null;
		
	}
	static public void Destroy()
	{
		m_Serverlist = null;
		m_Rendererlist =  null;
		m_datastack = null;
		m_DLNARenderStatus  = null;
		m_currentDeviceUUID = null;
		m_WorkRenderUUID = null;
		m_playItem = null;
		m_DLNAConfig = null;
		
		m_Mute = false;
		m_Volume = -1;
		m_SeekTimeInMillis = -1;
		
		m_PlayPosition = -1;
		m_ListToalItem = 0;
		m_ListLoadingItem = 0;
		m_ListLoadingAddNum = 0;
		
		m_BrowseItemId = -1;
		
		m_parentobjid = null;
		m_playurl = null;
		
		m_isImagePlayView = false;
		m_isVideoPlayVIew = false;
		m_PlayViewCurrentMillis = -1;
		m_PlayViewTotalMillis = -1;
		m_PlayViewVolume = -1;
		m_PlayViewMuted = false;
		m_playStyle = -1;
		m_PlayViewSeekTimeInMillis = -1;
		m_PlayViewSetVolume = -1;
		m_PlayViewSetMuted = false;
		m_PlayViewLoading = false;
		m_ShareFilePath = null;
		
		m_ListLoading = null;
		
		clearbackuplistdata();
		m_listdatabackup = null;
		
	}
	
	static public void clearbackuplistdata()
	{
		if(null != m_listdatabackup)
		{		
	    	for(GenieDlnaDeviceInfo device : m_listdatabackup)
	    	{
	    		if(device.m_bitmap != null)
	    			device.m_bitmap.recycle();
	    		if(device.m_icon != null)
	    			device.m_icon = null;
	    	}
	    	m_listdatabackup.clear();
		}
	}
	
	static public Boolean findServerByList(UUID uuid)
	{
		if(null == m_Serverlist || m_Serverlist.isEmpty())
			return false;
		for(DeviceDesc desc : m_Serverlist)
		{
			if(desc.getUuid().equals(uuid))
				return true;
		}
		
		return false;
	}
	
	static public Boolean findRenderByList(UUID uuid)
	{
		if(null == m_Rendererlist || m_Rendererlist.isEmpty())
			return false;
		for(DeviceDesc desc : m_Rendererlist)
		{
			if(desc.getUuid().equals(uuid))
				return true;
		}
		
		return false;
	}
	
	static public DeviceDesc GetServerByList(UUID uuid)
	{
		if(null == m_Serverlist ||  m_Serverlist.isEmpty())
			return null;
		for(DeviceDesc desc : m_Serverlist)
		{
			if(desc.getUuid().equals(uuid))
				return desc;
		}
		
		return null;
	}
	
	static public DeviceDesc GetRenderByList(UUID uuid)
	{
		if(null ==m_Serverlist ||  m_Rendererlist.isEmpty())
			return null;
		for(DeviceDesc desc : m_Rendererlist)
		{
			if(desc.getUuid().equals(uuid))
				return desc;
		}
		
		return null;
	}
	

}
