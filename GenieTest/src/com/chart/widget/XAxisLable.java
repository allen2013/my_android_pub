package com.chart.widget;

public class XAxisLable {
	
	/**
	 * 日期
	 */
	private String trafficName;
	/**
	 * 下载
	 */
	private double downLoad;
	/**
	 * 上传
	 */
	private double upLoad;
	
	public XAxisLable() {}
	
	public XAxisLable(String trafficName, double downLoad,
			double upLoad) {
		this.trafficName = trafficName;
		this.downLoad = downLoad;
		this.upLoad = upLoad;
	}



	public String getTrafficName() {
		return trafficName;
	}

	public void setTrafficName(String trafficName) {
		this.trafficName = trafficName;
	}

	public double getDownLoad() {
		return downLoad;
	}

	public void setDownLoad(double downLoad) {
		this.downLoad = downLoad;
	}

	public double getUpLoad() {
		return upLoad;
	}

	public void setUpLoad(double upLoad) {
		this.upLoad = upLoad;
	}

	
	
	

	
	
}
