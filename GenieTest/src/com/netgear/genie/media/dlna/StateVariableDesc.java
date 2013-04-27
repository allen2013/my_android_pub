package com.netgear.genie.media.dlna;

public class StateVariableDesc {

	public String getName()
	{
		return mName;
	}
	
	public String getDataType()
	{
		return mDataType;
	}
	
	public String getDefaultValue()
	{
		return mDefaultValue;
	}
	
	public int getAllowedValueCount()
	{
		return mAllowedValues.length;
	}
	
	public String getAllowedValue(int index)
	{
		return mAllowedValues[index];
	}
	
	public String getAllowedValueRangeMinimum()
	{
		return mAllowedMin;
	}
	
	public String getAllowedValueRangeMaximum()
	{
		return mAllowedMax;
	}
	
	public String getAllowedValueStep()
	{
		return mAllowedStep;
	}
	
	String mName;
	String mDataType;
	String mDefaultValue;
	String[] mAllowedValues;
	String mAllowedMin;
	String mAllowedMax;
	String mAllowedStep;
}
