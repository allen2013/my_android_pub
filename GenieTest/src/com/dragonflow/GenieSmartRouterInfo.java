package com.dragonflow;

public class GenieSmartRouterInfo {
	String cpid;
	String model;
	String owner;
	String friendly_name;
	String active;
	String type;
	String serial;	
	String username;
	String password;
	
	public String toString()
	{
		return "friendly_name:" + friendly_name + ",model:" + model + ",owner:" + owner 
				+ ",type:" + type + ",serial:" + serial;
	}
}
