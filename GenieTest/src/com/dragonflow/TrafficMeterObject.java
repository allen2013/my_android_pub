package com.dragonflow;

public class TrafficMeterObject {
	
	private String time;
	private String upLoad;
	private String downLoad;
	private String total;
	private String upLoadAvg;
	private String upLoadMax;
	private String downLoadAvg;
	private String downLoadMax;
	public TrafficMeterObject(){};
	
	public TrafficMeterObject(String time, String upLoad, String downLoad) {
		super();
		this.time = time;
		this.upLoad = upLoad;
		this.downLoad = downLoad;
	}

	public TrafficMeterObject(String time, String upLoad, String downLoad,
			String total) {
		super();
		this.time = time;
		this.upLoad = upLoad;
		this.downLoad = downLoad;
		this.total = total;
	}

	public TrafficMeterObject(String time, String upLoad, String downLoad,
			String total, String upLoadAvg, String upLoadMax,
			String downLoadAvg, String downLoadMax) {
		super();
		this.time = time;
		this.upLoad = upLoad;
		this.downLoad = downLoad;
		this.total = total;
		this.upLoadAvg = upLoadAvg;
		this.upLoadMax = upLoadMax;
		this.downLoadAvg = downLoadAvg;
		this.downLoadMax = downLoadMax;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUpLoad() {
		return upLoad;
	}

	public void setUpLoad(String upLoad) {
		this.upLoad = upLoad;
	}

	public String getDownLoad() {
		return downLoad;
	}

	public void setDownLoad(String downLoad) {
		this.downLoad = downLoad;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getUpLoadAvg() {
		return upLoadAvg;
	}

	public void setUpLoadAvg(String upLoadAvg) {
		this.upLoadAvg = upLoadAvg;
	}

	public String getUpLoadMax() {
		return upLoadMax;
	}

	public void setUpLoadMax(String upLoadMax) {
		this.upLoadMax = upLoadMax;
	}

	public String getDownLoadAvg() {
		return downLoadAvg;
	}

	public void setDownLoadAvg(String downLoadAvg) {
		this.downLoadAvg = downLoadAvg;
	}

	public String getDownLoadMax() {
		return downLoadMax;
	}

	public void setDownLoadMax(String downLoadMax) {
		this.downLoadMax = downLoadMax;
	}

	
}
