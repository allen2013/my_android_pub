package com.netgear.genie.media.dlna;

public class DLNARenderStatus {
	public  DLNARenderingControl m_RenderingControl;
	public  DLNAAVTransport m_AVTransport;
	
	public DLNARenderStatus()
	{
		m_RenderingControl = new DLNARenderingControl();
		m_AVTransport = new DLNAAVTransport();
	}
}
