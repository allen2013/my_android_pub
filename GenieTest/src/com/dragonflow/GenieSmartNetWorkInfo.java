package com.dragonflow;

import java.util.ArrayList;

public class GenieSmartNetWorkInfo {
	String domain;
	String uiid;
	String smartrouterid;
	String workcpid;
	String sessionid;
	String username;
	String password;
	int trace = 0;
	ArrayList<GenieSmartRouterInfo> routerlist = new ArrayList<GenieSmartRouterInfo>(); 
	
	public GenieSmartRouterInfo GetWorkSmartRouterInfo(String workcpid)
	{
		for(GenieSmartRouterInfo router : routerlist)
		{
			if(router.cpid.equals(workcpid))
				return router;
		}
		
		return null;
	}
}
