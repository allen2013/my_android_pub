package com.dragonflow;

import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiManagerment {    
	private WifiManager wifiManager;//
	private WifiInfo wifiInfo;//
	private List<ScanResult> scanResultList; //
	private List<WifiConfiguration> wifiConfigList;//
	private WifiLock wifiLock;// Wifi
	
	public WifiManagerment(Context context)
	{        
		this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);// ��ȡWifi����      
		this.wifiInfo = wifiManager.getConnectionInfo();
	}
	
	public boolean getWifiStatus()    
	{         
		return wifiManager.isWifiEnabled();    
	}
	
	public boolean OpenWifi() 
	{
		if (!wifiManager.isWifiEnabled()) 
		{
			return wifiManager.setWifiEnabled(true);      
		}
		else 
		{
			return false;
		}
	}

	public boolean CloseWifi() 
	{
		if (!wifiManager.isWifiEnabled()) 
		{
			return true; 
		}
		else
		{
			return wifiManager.setWifiEnabled(false);        
		}
	}
	
	public void LockWifi() 
	{
		wifiLock.acquire();    
	}
	
	public void UnLockWifi() 
	{
		if (!wifiLock.isHeld()) 
		{
			wifiLock.release(); //    
		}
	}
	
	public void CreateWifiLock() 
	{        
		wifiLock = wifiManager.createWifiLock("flyfly"); //  
	}
	
	public void StartScan() 
	{
		wifiManager.startScan();    
		scanResultList = wifiManager.getScanResults(); // 
		wifiConfigList = wifiManager.getConfiguredNetworks(); //
	}
	
	public List<ScanResult> getWifiList() 
	{
		return scanResultList;    
	}
	
	public List<WifiConfiguration> getWifiConfigList() 
	{
		return wifiConfigList;    
	}
	
	public StringBuilder LookUpscan() 
	{
		StringBuilder scanBuilder = new StringBuilder();      
		for (int i = 0; i < scanResultList.size(); i++) 
		{
			scanBuilder.append(" " + (i + 1)); 
			scanBuilder.append(scanResultList.get(i).toString());  //
			scanBuilder.append("\n");        
		}
		
		return scanBuilder;    
	}
	
	public int getLevel(int NetId)    
	{
		return scanResultList.get(NetId).level;    
	}
	
	public String getMac() 
	{
		return (wifiInfo == null) ? "" : wifiInfo.getMacAddress();    
	}
	
	public String getBSSID() 
	{
		return (wifiInfo == null) ? null : wifiInfo.getBSSID();    
	}
	
	public String getSSID() 
	{       
		return (wifiInfo == null) ? null : wifiInfo.getSSID();    
	}
	
	public int getCurrentNetId() 
	{
		return (wifiInfo == null) ? null : wifiInfo.getNetworkId();    
	}
	
	public String getwifiInfo() 
	{
		return (wifiInfo == null) ? null : wifiInfo.toString();    
	}
	
	public int getIP() 
	{
		return (wifiInfo == null) ? null : wifiInfo.getIpAddress();    
	}
	
	public boolean AddNetWordLink(WifiConfiguration config) 
	{
		int NetId = wifiManager.addNetwork(config);   
		return wifiManager.enableNetwork(NetId, true);    
	}
	
	public boolean DisableNetWordLick(int NetId) 
	{
		wifiManager.disableNetwork(NetId);     
		return wifiManager.disconnect();    
	}
	
	public boolean removeNetworkLink(int NetId) 
	{
		return wifiManager.removeNetwork(NetId);    
	}
	
	public void hiddenSSID(int NetId)    
	{
		wifiConfigList.get(NetId).hiddenSSID=true;    
	}
	
	public void displaySSID(int NetId)    
	{
		wifiConfigList.get(NetId).hiddenSSID=false;    
	}
	
	
	public boolean ConnectWifi(String ssid,String key)
	{
//		//ScanResult sr = resultList.get(position);
//        WifiConfiguration wc = new WifiConfiguration();
//        wc.BSSID=sr.BSSID;
//                        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        wc.status = WifiConfiguration.Status.ENABLED;
//        wc.networkId = wifiManager.addNetwork(wc);
//        wifiManager.enableNetwork(wc.networkId, true);     
		
		//WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		//wifiManager.getConfiguredNetworks();
		int index = -1;
		WifiConfiguration wc = null;
		
		StartScan();
		
		//wifiConfigList = wifiManager.getConfiguredNetworks();
		//wifiManager.disconnect();   
		String tempssid  = "\""+ssid+"\"";
		
		for(int i = 0;i<wifiConfigList.size();i++)
		{
			DisableNetWordLick(i);
//			GenieDebug.error("boolean", "SSID = " + wifiConfigList.get(i).SSID );
//			GenieDebug.error("boolean", "ConnectWifi  tempssid " + tempssid );
//			GenieDebug.error("boolean", "ConnectWifi  i " + i );
			if(wifiConfigList.get(i).SSID.equals(tempssid))
			{
				GenieDebug.error("boolean", "equals  i " + i );
				index = i;
				break;
			}
			
		}
		
//		GenieDebug.error("boolean", "ConnectWifi  ssid " + ssid );
//		GenieDebug.error("boolean", "ConnectWifi  key " + key );
//		GenieDebug.error("boolean", "ConnectWifi  index " + index );
//		GenieDebug.error("boolean", "ConnectWifi  wifiConfigList.size() " + wifiConfigList.size() );
//		
		if(index >-1 && index <wifiConfigList.size())
		{
			wc = wifiConfigList.get(index);
		}else
		{
			wc = new WifiConfiguration();
		}

		// This is must be quoted according to the documentation

		// http://developer.android.com/reference/android/net/wifi/WifiConfiguration.html#SSID

//		wc.SSID = "\"NETGEAR_WNDR3800_Online1\"";
//		wc.preSharedKey = "\"12345678\"";
		
		wc.SSID = "\""+ssid+"\"";
		
		wc.preSharedKey = "\""+key+"\"";
		
		wc.hiddenSSID = false;
		wc.status = WifiConfiguration.Status.ENABLED;
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		int res = wifiManager.addNetwork(wc);
		GenieDebug.error("boolean", "add Network returned " + res );
		boolean b = wifiManager.enableNetwork(res, false);
		GenieDebug.error("boolean", "enableNetwork returned " + b );

		return b;
	}
}
