package com.dragonflow;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class GenieDlnaStatus {
	public static String  m_currentServerUUID;
	public static String  m_currentRendererUUID;
	public static int	   m_systemUpdateID;
	public static int		m_currentop;
	public static int		m_currentServerStatus;
	public static int		m_currentRenderStatus;
	public static int 		m_currentview;
	public static  ArrayList<GenieDlnaDeviceInfo> m_Serverlist = null;
	public static  ArrayList<GenieDlnaDeviceInfo> m_Rendererlist= null;
	public static  ArrayList<GenieDlnaDeviceInfo> m_dirlist = null;
	public static  ArrayList<GenieDlnaDeviceInfo> m_dialogRendererlist= null;
	
	public static Activity  m_thisview = null;
	
	public static int m_min_value;
	public static int m_max_value;
	public static int m_value;
	public static int m_muted;  //  1 or 0
	public static int m_playing; // 1 or 0
	public static int m_paused;  // 1 or 0
	public static int m_duration; 
	public static int m_Status;  //  1 or 0
	public static int m_PlayPosition;
	
	
	public static String m_ServerGuid ;
	public static String m_RenderGuid ;
	public static String m_SharePath;
	
	public static String m_Sharename;
	public static String m_Servername;
	public static String m_Rendername;
	
	public static String m_chooserguid;
	
	
	final public static String DLNASTATUSFILENAME = "DlnaStatus";
	
	final public static String SERVERGUID = "ServerGuid";
	final public static String RENDERGUID = "RenderGuid";
	final public static String SHAREPATH = "SharePath";
	final public static String SAVEGUID = "ChooseGuid";
	
	
	public static HashMap<String,String> map = null;
	
	
	public static int m_VideoPlaySeekPos;
	public static int m_VideoPlaySetMute;
	public static int m_VideoPlaySetVolume;
	public static int m_VideoPlayVolumeBackup;
	
	final public static int VIEW_MEDIASERVER = 1;
	final public static int VIEW_MEDIARENDER = 2;
	
	final public static int VIEW_MEDIAVIDEOPLAY = 900;
	final public static int VIEW_IMAGEPLAYER = 901;
	
	public static Boolean  m_OnSlidePlay = false;
	
	static public void InitList()
	{
		m_currentServerStatus = 0;
		m_currentRenderStatus = 0;
		m_systemUpdateID = -1;
		m_currentview = -1;
		
		m_min_value = 0;
		m_max_value = 0;
		m_value = 0;
		m_muted = 0;
		m_playing = 0;
		m_paused = 0;
		m_duration = 0;
		m_PlayPosition = 0;
		m_Status = 0;
		
		m_VideoPlaySeekPos = 0;
		m_VideoPlaySetMute = 0;
		m_VideoPlaySetVolume = 0;
		m_VideoPlayVolumeBackup = 0;
		
		m_ServerGuid = null;
		m_RenderGuid = null;
		m_SharePath = null;
		m_chooserguid = null;
		map = null;
		
		m_thisview = null;
		
		
		m_Sharename = null;
		m_Servername = null ;
		m_Rendername = null;
		
		m_OnSlidePlay = false;
		
		m_Serverlist = new ArrayList<GenieDlnaDeviceInfo>();
		m_Rendererlist= new ArrayList<GenieDlnaDeviceInfo>();
		m_dirlist =  new ArrayList<GenieDlnaDeviceInfo>();
		m_dialogRendererlist = new ArrayList<GenieDlnaDeviceInfo>();
	}
	
	static public void setcurrentview(int view)
	{
		m_currentview = view;
	}
	static public int getcurrentview()
	{
		return m_currentview;
	}

	static public void setcurrentServerStatus(int Status)
	{
		m_currentServerStatus = Status;
	}
	static public int getcurrentServerStatus()
	{
		return m_currentServerStatus;
	}
	
	static public void setcurrentRenderStatus(int Status)
	{
		m_currentRenderStatus = Status;
	}
	static public int getcurrentRenderStatus()
	{
		return m_currentRenderStatus;
	}
	
	
	static public void setcurrentServerUUID(String UUID)
	{
		m_currentServerUUID = UUID;
	}
	static public String getcurrentServerUUID()
	{
		return m_currentServerUUID;
	}
	
	static public void setcurrentRendererUUID(String UUID)
	{
		m_currentRendererUUID = UUID;
	}
	static public String getcurrentRendererUUID()
	{
		return m_currentRendererUUID;
	}
	
	static public void setsystemUpdateID(int ID)
	{
		m_systemUpdateID = ID;
	}
	static public int getsystemUpdateID()
	{
		return m_systemUpdateID;
	}
	
	static public void saveStatusMap(Context context, String filename)
	{
		GenieDebug.error("debug","-55555555555555555555----onDestroy end------ ");
		
		GenieDebug.error("onDestroy", "onDestroy ServerGuid = "+GenieDlnaStatus.m_ServerGuid);
		
		GenieDlnaStatus.map.put(GenieDlnaStatus.SERVERGUID, GenieDlnaStatus.m_ServerGuid);
		
		GenieDebug.error("onDestroy", "onDestroy RenderGuid = "+GenieDlnaStatus.m_RenderGuid);
		
		GenieDlnaStatus.map.put(GenieDlnaStatus.RENDERGUID, GenieDlnaStatus.m_RenderGuid);				
		
		GenieDebug.error("onDestroy", "onDestroy m_SharePath = "+GenieDlnaStatus.m_SharePath);
		
		GenieDlnaStatus.map.put(GenieDlnaStatus.SHAREPATH, GenieDlnaStatus.m_SharePath);
		
		GenieDebug.error("onDestroy", "onDestroy m_chooserguid = "+GenieDlnaStatus.m_chooserguid);
		
		GenieDlnaStatus.map.put(GenieDlnaStatus.SAVEGUID, GenieDlnaStatus.m_chooserguid);
		
		GenieSerializ.WriteMap(context, GenieDlnaStatus.map, filename);
		
		return ;
	}
}
