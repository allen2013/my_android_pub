package com.wifianalyzer.bo;

import java.util.Date;
import java.util.Random;

public class WifiRoomSignalInfo {
	
	private String id="";
	private String BSSID="";
	private String roomName="";
	private int signalLevel;
	private Date createDateTime;
	private Date lastModifyDateTime;
	
	public WifiRoomSignalInfo() {
		this.id=getRandId();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getBSSID() {
		return BSSID;
	}
	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}
	
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public int getSignalLevel() {
		return signalLevel;
	}
	public void setSignalLevel(int signalLevel) {
		this.signalLevel = signalLevel;
	}
	
	public Date getCreateDateTime() {
		return createDateTime;
	}
	
	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	
	public Date getLastModifyDateTime() {
		return lastModifyDateTime;
	}
	public void setLastModifyDateTime(Date lastModifyDateTime) {
		this.lastModifyDateTime = lastModifyDateTime;
	}
	

	private String getRandId(){
		
		Random rand = new Random();
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i <16; i++) {
			String str=Integer.toHexString(rand.nextInt(256));
			if(str.length()==1){
				str+=rand.nextInt(10);
			}
			buff.append(str);
		}
		return buff.toString();
		
	}

}
