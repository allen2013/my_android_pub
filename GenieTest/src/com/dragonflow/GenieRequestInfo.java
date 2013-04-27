package com.dragonflow;

import java.util.ArrayList;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;

public class GenieRequestInfo {

	public String aRequestLable;
	public String aActionLable;
	public RequestActionType aRequestType;
	public String aServer;
	public String aMethod;
	public String aResponse;
	public String aResponseCode;
	public int aHttpResponseCode;
	public boolean aNeedParser ;
	public boolean aNeedwrap;
	public ArrayList<String> aElement;
	public RequestResultType aResultType ;
	public int errorcode;
	public int aTimeout;
	public int aSoapType;
	public int aSmartType;
	public int aHttpType;
	public int aOpenDNSType;

}
