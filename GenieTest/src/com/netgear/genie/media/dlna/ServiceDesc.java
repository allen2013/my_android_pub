package com.netgear.genie.media.dlna;

public class ServiceDesc {

	public String getServiceId()
	{
		return mServiceId;
	}
	
	public String getServiceType()
	{
		return mServiceType;
	}
	
	public int getStateVariableCount()
	{
		return mVarArr.length;
	}
	
	public StateVariableDesc getStateVariable(int index)
	{
		return mVarArr[index];
	}
	
	public StateVariableDesc findStateVariableByName(String name)
	{
		for (int i = 0; i < mVarArr.length; i++) {
			if (mVarArr[i].getName().equals(name)) {
				return mVarArr[i];
			}
		}
		return null;
	}
	
	String mServiceId;
	String mServiceType;
	StateVariableDesc[] mVarArr;
}
