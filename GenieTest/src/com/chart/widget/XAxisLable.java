package com.chart.widget;

public class XAxisLable {
	
	/**
	 * ����
	 */
	private String trafficName;
	/**
	 * ����
	 */
	private double downLoad;
	/**
	 * �ϴ�
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
