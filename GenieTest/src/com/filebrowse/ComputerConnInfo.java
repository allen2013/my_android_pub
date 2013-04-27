package com.filebrowse;

import java.io.Serializable;
import java.util.Random;

public class ComputerConnInfo implements Serializable{
	
	private String id="";
	private String ip="";
	private String username="";
	private String password="";
	private String name="";

	public ComputerConnInfo() {
		// TODO Auto-generated constructor stub
		this.id=getRandId();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toXml(){
		StringBuffer buffer=new StringBuffer();
		buffer.append("<computer ").append("id=\"").append(this.id).append("\" ").append("name=\"").append(this.name).append("\">");
		buffer.append("<ip>").append(this.ip).append("</ip>");
		buffer.append("<username>").append(this.username).append("</username>");
		buffer.append("<password>").append(this.password).append("</password>");
		buffer.append("</computer>");
		return buffer.toString();
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
