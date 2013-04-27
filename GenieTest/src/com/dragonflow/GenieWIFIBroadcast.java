package com.dragonflow;



import java.net.Inet4Address;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class GenieWIFIBroadcast {

	 private WIFIReceiver m_WIFIReceiver = null;
	 private WifiManager m_wifiManager = null;
	 private Context m_Context = null;
	 private WIFIBroadcastCallBack m_callback = null;
	 
	 public interface WIFIBroadcastCallBack {
			
			void CallBack (String action,String ip, int Value, int n);

		}
	 
	 public void SetOnWIFIBroadcastCallBack(WIFIBroadcastCallBack callback)
	 {
		 m_callback = callback;
	 }
	 
	public GenieWIFIBroadcast(Context context) {
		// TODO Auto-generated constructor stub
		m_Context = context;
		m_wifiManager = (WifiManager) m_Context.getSystemService(Context.WIFI_SERVICE);
	}
	
	
	 public void UnRegisterBroadcastReceiver()
	 {
		 if(null != m_WIFIReceiver)
			{
			 m_Context.unregisterReceiver(m_WIFIReceiver);
				m_WIFIReceiver = null;
			}
	 }

	 public void RegisterBroadcastReceiver()
	 {
		 UnRegisterBroadcastReceiver();
		 
		 m_WIFIReceiver = new WIFIReceiver();
			IntentFilter filter = new IntentFilter();//鍒涘缓IntentFilter瀵硅薄
//			filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//			filter.addAction(WifiManager.EXTRA_PREVIOUS_WIFI_STATE);
//			filter.addAction(WifiManager.EXTRA_SUPPLICANT_CONNECTED);
//			filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
//			
//			filter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
//			filter.addAction(WifiManager.EXTRA_SUPPLICANT_CONNECTED);
//			
//			filter.addAction(WifiManager.EXTRA_WIFI_STATE);
//			filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
			filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION); 
			filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); 

			 
			m_Context.registerReceiver(m_WIFIReceiver, filter);//娉ㄥ唽Broadcast Receiver
			
			
	 }
	
	
	 
	 private String ipIntToString(int ip)      
	    {          
	    	try          
	    	{              
	    		byte[] bytes = new byte[4];
	    		bytes[0] = (byte) (0xff & ip);
	    		bytes[1] = (byte) ((0xff00 & ip) >> 8);
	    		bytes[2] = (byte) ((0xff0000 & ip) >> 16);
	    		bytes[3] = (byte) ((0xff000000 & ip) >> 24);
	    		return Inet4Address.getByAddress(bytes).getHostAddress();
	    	}
	    	catch (Exception e)
	    	{
	    		return "";
	    	}
	    }  
	 
	private class WIFIReceiver extends BroadcastReceiver{//缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�


        @Override
	        public void onReceive(Context context, Intent intent) {//閲嶅啓onReceive鏂规硶
    
       	 	int action = -1;
       	 	
       	 
    		
       	 GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive intent.getAction() ="+intent.getAction());
       	 	
    		action = intent.getIntExtra("wifi_state",-1);
    		
    		GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive DLNA_ACTION_RET ="+action);

    		int previous_wifi_state = intent.getIntExtra("previous_wifi_state",-1);
    		
    		GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive previous_wifi_state ="+previous_wifi_state);
    		
    		boolean connected = intent.getBooleanExtra("connected",false);
    		
//    		GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive connected ="+connected);
    		
    		
    		WifiInfo test =	m_wifiManager.getConnectionInfo();
    		if(test != null)
    		{
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test != null");
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test.ip ="+ipIntToString(test.getIpAddress()));
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test.mac ="+test.getMacAddress());
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test.NetworkId ="+test.getNetworkId());
//    			GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test.ssid ="+test.getSSID());
    		}else
    		{
//    			 GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive test == null");
    		}
//    		GenieDebug.error("WIFIReceiver", "WIFIReceiver onReceive m_wifiManager.getWifiState() ="+m_wifiManager.getWifiState());
    		
    		if(m_callback != null)
    		{
    			m_callback.CallBack(test.getSSID(),ipIntToString(test.getIpAddress()), test.getNetworkId(), test.getIpAddress());
    		}
    		//sendMessage2UI(action);
    		
        }
      }

}
