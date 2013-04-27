package com.dragonflow;

import android.graphics.Bitmap;

public class GenieDlnaDeviceInfo {
	public String  m_deviceId;
	public String  m_objectId;
	public String  m_title;
	public String  m_iconUrl;
	public int	   m_container;
	public int	   m_filestyle;
	public int	   m_systemUpdateID;
	public int	   m_iconflag;
	public String	   m_ResourceSize;
	public byte[]  m_icon;
	public Bitmap  m_bitmap;
	public boolean downloading=true;
	public Bitmap downimage;
	public boolean bigloading=true;
	public Bitmap bitimage;
	public int originalWidth;
	public int originalHeight;
	public int loadingProNum=0;
	public long m_long_ResourceSize;
}
