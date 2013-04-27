package com.netgear.genie.media.dlna;

public class DLNAObject {

	public void dispose()
	{
		if (!mDisposed) {
			nativeRelease(mObject);
			mObject = 0;
			mDisposed = true;
		}
	}
	
	public String getObjectId()
	{
		return nativeGetObjectId(mObject);
	}
	
	public String getParentId()
	{
		return nativeGetParentId(mObject);
	}
	
	public String getTitle()
	{
		return nativeGetTitle(mObject);
	}
	
	public String getUpnpClass()
	{
		return nativeGetUpnpClass(mObject);
	}
	
	public long getResourceSize()
	{
		return nativeGetResourceSize(mObject);
	}
	
	public String findThumbnailURL(int width, int height, String[] preferredMimeTypes)
	{
		return nativeFindThumbnailURL(mObject, width, height, preferredMimeTypes);
	}
	
	void addRef()
	{
		nativeAddRef(mObject);
	}
	
	void release()
	{
		nativeRelease(mObject);
	}
	
	private static native void nativeAddRef(long obj);
	private static native void nativeRelease(long obj);
	private static native String nativeGetObjectId(long obj);
	private static native String nativeGetParentId(long obj);
	private static native String nativeGetTitle(long obj);
	private static native String nativeGetUpnpClass(long obj);
	private static native long nativeGetResourceSize(long obj);
	private static native String nativeFindThumbnailURL(long obj, int width, int height, String[] preferredMimeTypes);
	
	long mObject;
	boolean mDisposed;
}
