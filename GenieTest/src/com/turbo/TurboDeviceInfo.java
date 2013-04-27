package com.turbo;

import java.io.Serializable;

public class TurboDeviceInfo implements Serializable{
	
	private String deviceName="";
	private String ip="";
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

}
