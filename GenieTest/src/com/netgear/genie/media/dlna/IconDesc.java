package com.netgear.genie.media.dlna;

public class IconDesc {
	
	public int getWidth()
	{
		return mWidth;
	}
	
	public int getHeight()
	{
		return mHeight;
	}
	
	public String getMimeType()
	{
		return mMimeType;
	}
	
	public byte[] getIconData()
	{
		return mIconData;
	}

	int mWidth;
	int mHeight;
	String mMimeType;
	byte[] mIconData;
}
