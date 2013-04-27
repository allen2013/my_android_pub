package com.netgear.genie.media.dlna;

import java.util.UUID;

public class DeviceDesc {

	public String getFriendlyName()
	{
		return mFriendlyName;
	}
	
	public UUID getUuid()
	{
		return mUuid;
	}
	
	public int getServiceCount()
	{
		return mServiceArr.length;
	}
	
	public ServiceDesc getService(int index)
	{
		return mServiceArr[index];
	}
	
	public int getIconCount()
	{
		return mIconArr.length;
	}
	
	public IconDesc getIcon(int index)
	{
		return mIconArr[index];
	}
	
	public ServiceDesc findServiceById(String serviceId)
	{
		for (int i = 0; i < mServiceArr.length; i++) {
			if (mServiceArr[i].getServiceId().equals(serviceId)) {
				return mServiceArr[i];
			}
		}
		return null;
	}
	
	public ServiceDesc findServiceByType(String serviceType)
	{
		for (int i = 0; i < mServiceArr.length; i++) {
			if (mServiceArr[i].getServiceType().equals(serviceType)) {
				return mServiceArr[i];
			}
		}
		return null;
	}
	
	String mFriendlyName;
	UUID mUuid;
	ServiceDesc[] mServiceArr = null;
	IconDesc[] mIconArr = null;
}
