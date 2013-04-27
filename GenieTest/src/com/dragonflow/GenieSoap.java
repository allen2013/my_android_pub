package com.dragonflow;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.webkit.CookieManager;
import android.widget.Toast;

public class GenieSoap 
{
	int errCode = 0;
	int soapType;
	int elementIndex;
	int recordResults = 0;
	int activeSetItem;
	int OpenDNSType;
	public Boolean OpenDNSRequestStatus = false;
	public Boolean SoapRequestStatus = false;
	public GenieMainView pMainView = null;
	public GenieListView pListView = null;
	public GenieMap pmap = null;
	public GenieLPCmanage pLpcview = null;
	public Boolean netisable = false;
	private  int  http_timeout = 60000;
	
	public static String m_GateWayIp = null;
	
	
	private URL m_url=null;
	private HttpURLConnection m_httpConn= null ;


	Timer timer = null; 
	TimerTask task = null;
	
	public static ArrayList<HashMap<String,String>> routerMap = new ArrayList<HashMap<String,String>>();
	
	public static Map<String,String> dictionary = new HashMap<String, String>();
	public static int port = 80;
	public int parentview = 0;
	private static final String API_KEY = "3443313B70772D2DD73B45D39F376199"; 
	public static final String  KEY_NULL = "N/A";
	
	
	private static final String  m_SessionID = "AND88AE69687E58D9A00";
	
	public int httpresponseCode = -1;
	public Boolean support = true;
	
	private String mSyn = "Synchronized";
	private String mSyn2 = "Synchronized2";
	
	private Context m_context = null;
	
	
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
	    
		public  String getGateWay() 
		{  
			
//			String temp = null;
//			
//			try {
//				
//				
//				InetAddress ip = InetAddress.getByName("www.routerlogin.net");
//				temp =  ip.getHostAddress();
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			GenieDebug.error("debug","gateway temp = "+temp);
			
			
			WifiManager test = (WifiManager)m_context.getSystemService(Context.WIFI_SERVICE);
				DhcpInfo   dhcpInfo=test.getDhcpInfo();

				GenieDebug.error("debug","gateway = "+ipIntToString(dhcpInfo.gateway));
			
			return ipIntToString(dhcpInfo.gateway);
		}  
	
	public GenieSoap(Context context)
	{
		m_context = context;
		m_GateWayIp = getGateWay();
	}

	public static void initialDictionaryElements()
	{
		//if(null != dictionary) return;
		
		//routerMap = new ArrayList<HashMap<String,String>>();
		//dictionary = new HashMap<String, String>();

		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_VERYTI_OK, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_ENABLE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_STATUS, GenieGlobalDefines.NULL);
		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED, GenieGlobalDefines.NULL);

		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_ROUTER_IP, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_ENABLE_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET, GenieGlobalDefines.NULL);

		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE_SET, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY_SET, GenieGlobalDefines.NULL);
		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES, GenieGlobalDefines.NULL);
		
	
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID,"");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_PC_ENABLE, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_PC_SUPPORT, GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BUNDLE, GenieGlobalDefines.NULL);
		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, GenieGlobalDefines.NULL);
		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC, "");
		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, "");		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEID, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE,GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CUSTOM,GenieGlobalDefines.NULL);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_AVAILABLE, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY, "");
		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEUSERNAME, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEPASSWORD, "");		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEPASSWORD2, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEEMAIL, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEEMAIL2, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RELAY_TOKEN, "");
		
		
		dictionary.put(GenieGlobalDefines.SMARTNETWORK_KEY_AUTHENTICATED, GenieGlobalDefines.NULL);
		
	}
	
	public void ClearErrorState()
	{
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, "");
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, "");
	}

	public void setSoapType(int type)
	{
		soapType = type;
	}
	
	
	public String sendRequest2Router(String aServer, String aMothed, ArrayList<String> aElement,boolean needParser)
	{
		
		ClearErrorState();
		GenieDebug.error("sendRequest2Router","needParser  = "+needParser);
		GenieDebug.error("sendRequest2Router","aServer  = "+aServer);
		GenieDebug.error("sendRequest2Router","aMothed  = "+aMothed);
		
		//String SOAP_TEMPLATE_P1 = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n<env:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n        xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <env:Body>\r\n    ";
		//String SOAP_TEMPLATE_P2 = "\r\n  </env:Body> \r\n</env:Envelope>";
		
		//String SOAP_TEMPLATE_P1 = "";
		//String SOAP_TEMPLATE_P2 = "";
				
		//String test = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><SOAP-ENV:Envelope xmlns:SOAPSDK1=\"http://www.w3.org/2001/XMLSchema\" xmlns:SOAPSDK2=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAPSDK3=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header><SessionID>E6A88AE69687E58D9A00</SessionID></SOAP-ENV:Header><SOAP-ENV:Body><M1:SetWLANWPAPSKByPassphrase xmlns:M1=\"urn:NETGEAR-ROUTER:service:WLANConfiguration:1\"><NewChannel>Auto</NewChannel><NewRegion>US</NewRegion><NewSSID>NETGEAR_WNDR3400_online</NewSSID><NewWPAEncryptionModes>WPA2-PSK</NewWPAEncryptionModes><NewWPAPassphrase>12345678</NewWPAPassphrase><NewWirelessMode>g &amp; b</NewWirelessMode></M1:SetWLANWPAPSKByPassphrase></SOAP-ENV:Body></SOAP-ENV:Envelope>";
		
		String SOAP_TEMPLATE_P1 = null;
		String SOAP_TEMPLATE_P2 = null;
		String headerstr = null;
		String soapAction = String.format("\"urn:NETGEAR-ROUTER:service:%s:1#%s\"",aServer,aMothed);
		String SOAP_TEMPLATE_P3 = " xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"";
		StringBuffer soapMessage = null;
		String SOAP_TEMPLATE_P4 = null;
		String SOAP_TEMPLATE_P5 = null;
		
		if(soapType == GenieGlobalDefines.ESoapRequestVerityUser)
		{
			SOAP_TEMPLATE_P1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
			SOAP_TEMPLATE_P2 = "</SOAP-ENV:Envelope>";
			headerstr = "<SOAP-ENV:Header><SessionID"+SOAP_TEMPLATE_P3+">"+m_SessionID+"</SessionID></SOAP-ENV:Header>";
			soapMessage = new StringBuffer();
			soapMessage.append(SOAP_TEMPLATE_P1);
			soapMessage.append(headerstr);
			soapMessage.append("<SOAP-ENV:Body>");
			soapMessage.append("<Authenticate>");
			
			
			for(int i = 0; i < aElement.size(); i += 2)
			{
				soapMessage.append("<");				
				soapMessage.append(aElement.get(i));
				soapMessage.append(SOAP_TEMPLATE_P3);
				soapMessage.append(">");

				soapMessage.append(aElement.get(i + 1));

				soapMessage.append("</");
				soapMessage.append(aElement.get(i));
				soapMessage.append(">");
				
			}
			soapMessage.append("</Authenticate>");
			soapMessage.append("</SOAP-ENV:Body>");
			soapMessage.append(SOAP_TEMPLATE_P2);
			
		}else if(soapType == GenieGlobalDefines.ESoapRequestConfigStart)
		{
			SOAP_TEMPLATE_P1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
			SOAP_TEMPLATE_P2 = "</SOAP-ENV:Envelope>";
			SOAP_TEMPLATE_P4 = String.format("<M1:%s xmlns:M1=\"urn:NETGEAR-ROUTER:service:%s:1\">",aMothed,aServer);
			SOAP_TEMPLATE_P5 = String.format("</M1:%s>",aMothed);
			soapMessage = new StringBuffer();
			soapMessage.append(SOAP_TEMPLATE_P1);
			soapMessage.append("<SOAP-ENV:Body>");
			soapMessage.append(SOAP_TEMPLATE_P4);
			soapMessage.append("<NewSessionID>"+m_SessionID+"</NewSessionID>");
			soapMessage.append(SOAP_TEMPLATE_P5);
			soapMessage.append("</SOAP-ENV:Body>");
			soapMessage.append(SOAP_TEMPLATE_P2);
						
			
		}else if(soapType == GenieGlobalDefines.ESoapRequestConfigFinish)
		{
			SOAP_TEMPLATE_P1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
			SOAP_TEMPLATE_P2 = "</SOAP-ENV:Envelope>";
			SOAP_TEMPLATE_P4 = String.format("<M1:%s xmlns:M1=\"urn:NETGEAR-ROUTER:service:%s:1\">",aMothed,aServer);
			SOAP_TEMPLATE_P5 = String.format("</M1:%s>",aMothed);
			soapMessage = new StringBuffer();
			soapMessage.append(SOAP_TEMPLATE_P1);
			soapMessage.append("<SOAP-ENV:Body>");
			soapMessage.append(SOAP_TEMPLATE_P4);
			soapMessage.append("<NewSessionID>"+m_SessionID+"</NewSessionID>");
			soapMessage.append("<NewStatus>ChangesApplied</NewStatus>");
			soapMessage.append(SOAP_TEMPLATE_P5);
			soapMessage.append("</SOAP-ENV:Body>");
			soapMessage.append(SOAP_TEMPLATE_P2);
		}else
		{
			SOAP_TEMPLATE_P1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
			SOAP_TEMPLATE_P2 = "</SOAP-ENV:Envelope>";
			headerstr = "<SOAP-ENV:Header><SessionID>"+m_SessionID+"</SessionID></SOAP-ENV:Header>";
			SOAP_TEMPLATE_P4 = String.format("<M1:%s xmlns:M1=\"urn:NETGEAR-ROUTER:service:%s:1\">",aMothed,aServer);
			SOAP_TEMPLATE_P5 = String.format("</M1:%s>",aMothed);
			soapMessage = new StringBuffer();
			soapMessage.append(SOAP_TEMPLATE_P1);
			soapMessage.append(headerstr);
			soapMessage.append("<SOAP-ENV:Body>");
			soapMessage.append(SOAP_TEMPLATE_P4);
			
			for(int i = 0; i < aElement.size(); i += 2)
			{
				soapMessage.append("<");
				soapMessage.append(aElement.get(i));
				soapMessage.append(">");

				soapMessage.append(aElement.get(i + 1));

				soapMessage.append("</");
				soapMessage.append(aElement.get(i));
				soapMessage.append(">");
				
			}
			soapMessage.append(SOAP_TEMPLATE_P5);
			soapMessage.append("</SOAP-ENV:Body>");
			soapMessage.append(SOAP_TEMPLATE_P2);
		}
		


		return httpUrlConnection(soapMessage.substring(0),soapAction,needParser);
	}

//	public String sendRequest2Router(String aServer, String aMothed, ArrayList<String> aElement,boolean needParser)
//	{
//		
//		ClearErrorState();
//		GenieDebug.error("sendRequest2Router","needParser  = "+needParser);
//		
//		String SOAP_TEMPLATE_P1 = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n<env:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n        xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <env:Body>\r\n    ";
//		String SOAP_TEMPLATE_P2 = "\r\n  </env:Body> \r\n</env:Envelope>";
//		
//		GenieDebug.error("sendRequest2Router","--strart--");
//		String soapAction = String.format("urn:NETGEAR-ROUTER:service:%s:1#%s",aServer,aMothed);
//
//		StringBuffer soapMessage = new StringBuffer();
//		
//		soapMessage.append(SOAP_TEMPLATE_P1);
//		
//		soapMessage.append("<");		
//		soapMessage.append(aMothed);
//		soapMessage.append(">\r\n");
//		
//		
//		for(int i = 0; i < aElement.size(); i += 2)
//		{
//			soapMessage.append("<");
//			soapMessage.append(aElement.get(i));
//			soapMessage.append(">");
//
//			soapMessage.append(aElement.get(i + 1));
//
//			soapMessage.append("</");
//			soapMessage.append(aElement.get(i));
//			//soapMessage.append(">\r\n");	
//			soapMessage.append(">");
//
//		}
//		
//		soapMessage.append("</");
//		soapMessage.append(aMothed);
//		soapMessage.append(">");		
//		
//		
//		soapMessage.append(SOAP_TEMPLATE_P2);
//		
//		return httpUrlConnection(soapMessage.substring(0),soapAction,needParser);
//	}

	
//	public String sendRequest2Router(String aServer, String aMothed, ArrayList<String> aElement,boolean needParser)
//	{
//		GenieDebug.error("sendRequest2Router","--strart--");
//		String soapAction = String.format("urn:NETGEAR-ROUTER:service:%s:1#%s",aServer,aMothed);
//
//		StringBuffer soapMessage = new StringBuffer();
//		soapMessage.append(String.format("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//				"<SOAP-ENV:Header>\n" +
//				"<SessionID xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\">123456789A123456789A</SessionID>\n" +
//				"</SOAP-ENV:Header>\n" +
//				"<SOAP-ENV:Body>\n" +
//				"<%s>\n",aMothed));
//		
//		for(int i = 0; i < aElement.size(); i += 2)
//		{
//			soapMessage.append(String.format("<%s xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\">%s</%s>\n",	
//														aElement.get(i),aElement.get(i + 1),aElement.get(i)));
//		}
//		
//		soapMessage.append(String.format("</%s>\n" + 
//									"</SOAP-ENV:Body>\n" +
//									"</SOAP-ENV:Envelope>\n",aMothed));
//		
//		return httpUrlConnection(soapMessage.substring(0),soapAction,needParser);
//	}
	
	public void cancelhttpUrlConnection()
	{
		if(null != m_httpConn)
		{
			m_httpConn.disconnect();
			m_httpConn = null;
		}
	}
	


	private String httpUrlConnection(String requestString,String soapAction,boolean needParser)
	{
		m_url = null;
		m_httpConn = null ;
		
		synchronized(mSyn)
		{
		try{
			
			//port = 5000;
			
			//if(m_GateWayIp == null || m_GateWayIp.equals(""))
			//{
				m_GateWayIp = getGateWay();
			//}
			
			
			
			
			GenieDebug.error("httpUrlConnection","--strart-- m_GateWayIp = "+m_GateWayIp);
			GenieDebug.error("httpUrlConnection","-httpUrlConnection-port = "+port);
			//String pathUrl = "http://www.routerlogin.com:"+ port + "/soap/server_sa/";

//			String url2 = "routerlogin.net";
//			InetAddress address = null;
//			String RouterIP = null;
//			
//			address = InetAddress.getByName(url2);
//			if(null != address)
//			{			
//				String ip = address.getHostAddress();
//				
//				RouterIP = ip;
//				
//				GenieDebug.error("debug","1 httpUrlConnection ip = "+ip);
//			}
//			
//			String pathUrl = "http://"+RouterIP+":" + port + "/soap/server_sa/";
			
			
			
			//String pathUrl = "http://routerlogin.net:" + port + "/soap/server_sa/";
			
			String pathUrl = "http://"+m_GateWayIp+":" + port + "/soap/server_sa/";
			
			//String pathUrl = "http://10.0.0.6:"+ "9402" + "/soap/server_sa/";
			
			GenieDebug.error("httpUrlConnection","-httpUrlConnection-pathUrl = "+pathUrl);

			m_url=new URL(pathUrl);

			
			
			GenieDebug.error("httpUrlConnection","-httpUrlConnection-m_url.getHost(); = "+m_url.getHost());
			
			
			m_httpConn=(HttpURLConnection)m_url.openConnection();


			
			m_httpConn.setDoOutput(true);
			m_httpConn.setDoInput(true);
			m_httpConn.setUseCaches(false);
			m_httpConn.setRequestMethod("POST");
			
			
			


			byte[] requestStringBytes = requestString.getBytes("UTF-8");
			
			
			//m_httpConn.setRequestProperty("Connection", "Keep-Alive");// ά�ֳ�����
			m_httpConn.setRequestProperty("Accept", "text/xml");
			m_httpConn.setRequestProperty("SOAPAction", soapAction);
			m_httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			m_httpConn.setRequestProperty("Content-Length", Integer.toString(requestStringBytes.length));
			//m_httpConn.setRequestProperty("Connection", "Keep-Alive");
			//m_httpConn.setRequestProperty("Content-Length", "" + requestStringBytes.length);
			
//			m_httpConn.setRequestProperty("Host", m_GateWayIp+":" + port );
//			
//			
//			m_httpConn.setRequestProperty("Host",m_GateWayIp);
			//m_httpConn.setRequestProperty("User-Agent", "SOAP Toolkit 3.0");
			
			if(GenieGlobalDefines.checkAvailabelPort == soapType
					|| soapType == GenieGlobalDefines.ESoapRequestConfigStart 
					|| soapType == GenieGlobalDefines.ESoapRequestConfigFinish)
			{
				m_httpConn.setConnectTimeout(15000);
				m_httpConn.setReadTimeout(15000);
			}else
			{
				m_httpConn.setConnectTimeout(http_timeout);
				m_httpConn.setReadTimeout(http_timeout);
			}
			
			
			GenieDebug.error("httpUrlConnection","soapAction = "+soapAction);
			
			GenieDebug.error("httpUrlConnection","requestString = "+requestString);

			OutputStream outputStream = m_httpConn.getOutputStream();
			
			GenieDebug.error("httpUrlConnection","requestString 11 ");
			
			outputStream.write(requestStringBytes);
			
			GenieDebug.error("httpUrlConnection","requestString 22 ");
			outputStream.close();
			
			GenieDebug.error("httpUrlConnection","requestString 33 ");
			
			httpresponseCode = -1;
			support = true;
			int responseCode = -2;
			
			try{
				responseCode = m_httpConn.getResponseCode();
			}catch (NullPointerException e) {
				// TODO: handle exception
				
				SoapRequestStatus = false;
				netisable = false;
				if(soapType != GenieGlobalDefines.ESoapRequestConfigStart &&
						soapType != GenieGlobalDefines.ESoapRequestConfigFinish)
				{
					if(GenieGlobalDefines.GenieView_MainView == parentview)
					{
						GenieDebug.error("debug","soapType = "+soapType);
	//					pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						if(GenieGlobalDefines.checkAvailabelPort != soapType)
						{
							if(GenieGlobalDefines.checkAvailabelPort != soapType)
							{
								if(soapType == GenieGlobalDefines.ESoapAuthenticate )
								{
									pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestLoginRouterFailure);
								}else
								{
									pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
								}
								
							}
	//						if(soapType == GenieGlobalDefines.ESoapAuthenticate )
	//						{
	//							pMainView.ShowLoginBad_passwordVisible();
	//						}else
	//						{
	//
	//							if(GenieGlobalDefines.ESoapRequestVerityUser == soapType)
	//							{
	//								pMainView.IsNetgearRouter = false;
	//							}
	//							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotGenie);
	//						}
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						}
					}else if(GenieGlobalDefines.GenieView_ListView == parentview)
					{
						pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
					}else if(GenieGlobalDefines.GenieView_LPCView == parentview)
					{
						pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
					}
					else if(GenieGlobalDefines.GenieView_Map == parentview)
					{
						if(GenieGlobalDefines.ESoapRequestRouterMap == soapType)
						{
							pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						}
					}
				}
				
				e.printStackTrace();
				cancelhttpUrlConnection();
				return  null;
			}
			GenieDebug.error("httpUrlConnection","requestString 44 ");
			
			GenieDebug.error("httpUrlConnection","responseCode = "+responseCode);
			
			httpresponseCode = responseCode;
			
			if(HttpURLConnection.HTTP_OK == responseCode)
			{
				netisable = true; 

				StringBuffer recString = new StringBuffer();
				String readLine;
				BufferedReader responseReader;

				responseReader = new BufferedReader(new InputStreamReader(m_httpConn.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null)
				{
					recString.append(readLine).append("\n");
				}
				
				responseReader.close();
				cancelhttpUrlConnection();
				
				GenieDebug.error("httpUrlConnection","responseReader+"+recString.toString());
				GenieDebug.error("httpUrlConnection","needParser+"+needParser);
				
				String Code = getXMLText(recString.toString(),"<ResponseCode>","</ResponseCode>");
				if(Code != null && Code.indexOf("000") == -1)
				{
					if(soapType == GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus)
					{
						dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE, GenieGlobalDefines.NULL);
						
					}
				}
				if(Code != null && Code.indexOf("401") != -1)
				{
					if(soapType != GenieGlobalDefines.ESoapRequestConfigStart && soapType != GenieGlobalDefines.ESoapRequestConfigFinish)
					{
						if(soapType == GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus)
						{
							dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE, GenieGlobalDefines.NULL);
							
						}
						
						if(GenieGlobalDefines.GenieView_ListView == parentview)
						{
							support = false;
							SoapRequestStatus = true;

								pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestnotsupport);

							
							cancelhttpUrlConnection();
							
							return recString.toString();
						}else if(GenieGlobalDefines.GenieView_Map == parentview)
						{
							support = false;
							SoapRequestStatus = true;
							
							if(soapType == GenieGlobalDefines.ESoapRequestRouterMap)
							{
								//if(GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus != soapType)
								//{
									pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestnotsupport);
								//}
							}
							cancelhttpUrlConnection();
							return recString.toString();
						}
						
						
						
					}
				}
				
				if(needParser)
				{
					parseXml(new ByteArrayInputStream(recString.toString().getBytes()));
					
					cancelhttpUrlConnection();
					
					return null;
				}else
				{
					SoapRequestStatus = true;
				}
				cancelhttpUrlConnection();
				
				return recString.toString();
			}else
			{
				if(soapType != GenieGlobalDefines.ESoapRequestConfigStart &&
						soapType != GenieGlobalDefines.ESoapRequestConfigFinish)
				{

					if(responseCode == 401)
					{
						port = (port == 80) ? 5000 : 80; 
					}
					SoapRequestStatus = false;
					
					if(GenieGlobalDefines.GenieView_MainView == parentview)
					{
						if(soapType == GenieGlobalDefines.ESoapAuthenticate &&
								responseCode == 401)
						{
							//pMainView.ShowLoginBad_passwordVisible();
							
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestLoginRouterFailure);
						}else
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						}
					}else if(GenieGlobalDefines.GenieView_ListView == parentview)
					{
						pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
	
					}else if(GenieGlobalDefines.GenieView_LPCView == parentview)
					{
						pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
					}
					else if(GenieGlobalDefines.GenieView_Map == parentview)
					{
						if(GenieGlobalDefines.ESoapRequestRouterMap == soapType)
						{
							pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						}
					}
				}
				
//				if(responseCode == 401)
//				{
//					port = (port == 80) ? 5000 : 80; 
//					cancelhttpUrlConnection();
//					return "Code == 401";
//				}
			}
			cancelhttpUrlConnection();
		}
		catch(ConnectException ex)
		{
			if(soapType != GenieGlobalDefines.ESoapRequestConfigStart &&
					soapType != GenieGlobalDefines.ESoapRequestConfigFinish)
			{
				port = (port == 80) ? 5000 : 80;
				
				SoapRequestStatus = false;
				netisable = false;
				
				if(GenieGlobalDefines.GenieView_MainView == parentview)
				{
					GenieDebug.error("debug","soapType = "+soapType);
	//				pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
					if(GenieGlobalDefines.checkAvailabelPort != soapType)
					{
						if(soapType == GenieGlobalDefines.ESoapAuthenticate )
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestLoginRouterFailure);
						}else
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						}
	//					if(soapType == GenieGlobalDefines.ESoapAuthenticate )
	//					{
	//						pMainView.ShowLoginBad_passwordVisible();
	//					}else
	//					{
	//
	//						if(GenieGlobalDefines.ESoapRequestVerityUser == soapType)
	//						{
	//							pMainView.IsNetgearRouter = false;
	//						}
	//						pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotGenie);
	//					}
					}
				}else if(GenieGlobalDefines.GenieView_ListView == parentview)
				{
					pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
				}else if(GenieGlobalDefines.GenieView_LPCView == parentview)
				{
					pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
				}
				else if(GenieGlobalDefines.GenieView_Map == parentview)
				{
					if(GenieGlobalDefines.ESoapRequestRouterMap == soapType)
					{
						pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
					}
				}
			}
			
			ex.printStackTrace();
			cancelhttpUrlConnection();
		}
		catch(UnknownHostException ex)
		{
			//port = (port == 80) ? 5000 : 80; 
			
			m_GateWayIp = getGateWay();
			SoapRequestStatus = false;
			netisable = false;
			
			if(soapType != GenieGlobalDefines.ESoapRequestConfigStart &&
					soapType != GenieGlobalDefines.ESoapRequestConfigFinish)
			{
			
				if(GenieGlobalDefines.GenieView_MainView == parentview)
				{
	//				pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
					if(GenieGlobalDefines.checkAvailabelPort != soapType)
					{
						if(soapType == GenieGlobalDefines.ESoapAuthenticate )
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestLoginRouterFailure);
						}else
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						}
	//					if(soapType == GenieGlobalDefines.ESoapAuthenticate )
	//					{
	//						pMainView.ShowLoginBad_passwordVisible();
	//					}else
	//					{	
	//						if(GenieGlobalDefines.ESoapRequestVerityUser == soapType)
	//						{
	//							pMainView.IsNetgearRouter = false;
	//						}
	//						pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotGenie);
	//					}
					}
				}else if(GenieGlobalDefines.GenieView_ListView == parentview)
				{
					pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
				}else if(GenieGlobalDefines.GenieView_LPCView == parentview)
				{
					pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
				}
				else if(GenieGlobalDefines.GenieView_Map == parentview)
				{
					if(GenieGlobalDefines.ESoapRequestRouterMap == soapType)
					{
						pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
					}
				}
			}
			ex.printStackTrace();
			
			cancelhttpUrlConnection();
		
		}
		catch(SocketException ex)
		{
			//port = (port == 80) ? 5000 : 80; 
			
			SoapRequestStatus = false;
			netisable = false;
			if(soapType != GenieGlobalDefines.ESoapRequestConfigStart &&
					soapType != GenieGlobalDefines.ESoapRequestConfigFinish)
			{
				if(GenieGlobalDefines.GenieView_MainView == parentview)
				{
					if(GenieGlobalDefines.checkAvailabelPort != soapType)
					{
						if(soapType == GenieGlobalDefines.ESoapAuthenticate )
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestLoginRouterFailure);
						}else
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						}
					}
	//				if(soapType == GenieGlobalDefines.ESoapAuthenticate )
	//				{
	//					pMainView.ShowLoginBad_passwordVisible();
	//				}else
	//				{
	//					pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
	//				}
				}else if(GenieGlobalDefines.GenieView_ListView == parentview)
				{
					pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
				}else if(GenieGlobalDefines.GenieView_LPCView == parentview)
				{
					pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
				}else if(GenieGlobalDefines.GenieView_Map == parentview)
				{
					if(GenieGlobalDefines.ESoapRequestRouterMap == soapType)
					{
						pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
					}
				}
			}
			ex.printStackTrace();
			cancelhttpUrlConnection();
			
		}catch(Exception ex)
		{
			GenieDebug.error("debug","http Exception ex");
			//port = (port == 80) ? 5000 : 80;
			SoapRequestStatus = false;
			netisable = false;
			if(soapType != GenieGlobalDefines.ESoapRequestConfigStart &&
					soapType != GenieGlobalDefines.ESoapRequestConfigFinish)
			{
				if(GenieGlobalDefines.GenieView_MainView == parentview)
				{
					if(GenieGlobalDefines.checkAvailabelPort != soapType)
					{
						if(soapType == GenieGlobalDefines.ESoapAuthenticate )
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestLoginRouterFailure);
						}else
						{
							pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
						}
					}
	//				if(soapType == GenieGlobalDefines.ESoapAuthenticate )
	//				{
	//					pMainView.ShowLoginBad_passwordVisible();
	//				}else
	//				{
	//					pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
	//				}
				}else if(GenieGlobalDefines.GenieView_ListView == parentview)
				{
					pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
				}else if(GenieGlobalDefines.GenieView_LPCView == parentview)
				{
					pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
				}else if(GenieGlobalDefines.GenieView_Map == parentview)
				{
					if(GenieGlobalDefines.ESoapRequestRouterMap == soapType)
					{
						pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
					}
				}
			}
			ex.printStackTrace();
			cancelhttpUrlConnection();
		}
//			finally {    
//			
//			cancelhttpUrlConnection();
//			}
		
		return null;
		}
	}
	
	public String getXMLText(String source,String tagS, String tagE)
	{
		if(null == source) return null;
		if(source.length() == 0) return null;
		
		int posS = source.indexOf(tagS);
		int posE = source.indexOf(tagE);

		return source.substring(posS + tagS.length(), posE);
	}
	
	public void getParentalControlSupport(String settings)
	{
		int pos = settings.indexOf(GenieGlobalDefines.DICTIONARY_KEY_PC_SUPPORT)+GenieGlobalDefines.DICTIONARY_KEY_PC_SUPPORT.length();
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_PC_SUPPORT, settings.substring(pos, pos+1));
	}
	
	private HttpGet request = null;
	public void  CanclegetRouterType()
	{
		GenieDebug.error("CanclegetRouterType", "CanclegetRouterType --0--");
		if(request != null && !request.isAborted())
		{
			GenieDebug.error("CanclegetRouterType", "CanclegetRouterType --1--");
			request.abort();
		}
		GenieDebug.error("CanclegetRouterType", "CanclegetRouterType --1--");
		request = null;
	}
	public boolean getRouterType()
	{
		m_GateWayIp = getGateWay();
		
		GenieDebug.error("getRouterType", "getRouterType --0--");
		
		
        //HttpGet request = new HttpGet("http://routerlogin.net/currentsetting.htm");
		
		request = new HttpGet("http://"+m_GateWayIp+"/currentsetting.htm");
        
//        
//        String url2 = "routerlogin.net";
//		InetAddress address = null;
//		String RouterIP = null;
//		
//		
//		try {
//			address = InetAddress.getByName(url2);
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		if(null != address)
//		{			
//			String ip = address.getHostAddress();
//			
//			RouterIP = ip;
//			
//			GenieDebug.error("debug","1 httpUrlConnection ip = "+ip);
//		}
//		
//		
//		request.addHeader("Host", RouterIP+":" + port);
//        request.addHeader("Cache-Control", "no-cache");
        
        HttpParams httpParams = new BasicHttpParams();
        

        
        HttpConnectionParams.setConnectionTimeout(httpParams,10000);
        HttpConnectionParams.setSoTimeout(httpParams,10000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        

        
        GenieDebug.error("getRouterType", "getRouterType --1--");
        
        try
        {
        	HttpResponse response = httpClient.execute(request);
            
        	GenieDebug.error("getRouterType", "getRouterType --2--");
        	
        	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        	{
        		GenieDebug.error("getRouterType", "getRouterType --3--");
        		
        		String recBuffer = EntityUtils.toString(response.getEntity());
        		
        		GenieDebug.error("getRouterType", "getRouterType --4--");
        		GenieDebug.error("getRouterType", "getRouterType = "+recBuffer);
        		
        		if(recBuffer.indexOf("Model=") > 0)
        		{
        			if(recBuffer.indexOf("SOAPVersion=V1.9") > 0 || recBuffer.indexOf("SOAPVersion=") < 0)
        			{
        				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE, GenieGlobalDefines.ROUTER_TYPE_OLD_NETGEAR);
        			}
        			else
        			{
        				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE, GenieGlobalDefines.ROUTER_TYPE_NEW_NETGEAR);
        			}
        			
        			GenieDebug.error("getRouterType", "getRouterType --5--");
        			
        			pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestIsGenie); 
       				
        		}
        		else
        		{
        			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE, GenieGlobalDefines.ROUTER_TYPE_NOT_NETGEAR);
        			GenieDebug.error("getRouterType", "getRouterType --6--");
        			
        			pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotNETGEARRouter); 
        			
        		}
        		
        		return true;
            }else
            {
            	pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotNETGEARRouter);
            }
        	
        }
        catch (UnknownHostException e)
        {
        	e.printStackTrace();
        	pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotNETGEARRouter);
        }
        catch (HttpHostConnectException e)
        {
        	e.printStackTrace();
        	pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotNETGEARRouter);
        }
        catch (ConnectTimeoutException e)
        {
        	e.printStackTrace();
        	pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotNETGEARRouter);
        }
        catch (ClientProtocolException e)
        {
        	e.printStackTrace();
        	pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotNETGEARRouter);
        }
        catch (IOException e)
        {
        	e.printStackTrace();
        	pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotNETGEARRouter);
        }
        
        request = null;
        
		return false;
	}
	
	public void checkAvailabelPort()
	{
		WhetherGetStartConf();  //test
		
		setSoapType(GenieGlobalDefines.checkAvailabelPort);
		
		ArrayList<String> element = new ArrayList<String>();
		//element.add("ModelName");
		//element.add("null");
		
		port = 80;
		
		GenieDebug.error("checkAvailabelPort","checkAvailabelPort 1 port = "+port);
		
		try{
			String res = sendRequest2Router("DeviceInfo","GetInfo",element,false);
			
			GenieDebug.error("checkAvailabelPort "," checkAvailabelPort 2 res = "+res);
			GenieDebug.error("checkAvailabelPort "," checkAvailabelPort 2 port = "+port);
			//WhetherSetFinishConf();
			
			if(res == null  && 80 == port)
			{
				port = 5000;
			}else if(res == null  && 5000 == port)
			{
				port = 80;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if(port == 80)
			{
				port = 5000;
			}else
			{
				port = 80;
			}
		}
		GenieDebug.error("checkAvailabelPort","checkAvailabelPort 3 port = "+port);
		WhetherGetFinishedConf();
		GenieDebug.error("checkAvailabelPort","checkAvailabelPort 4 port = "+port);
		
	}
	
	public Boolean getRouterModelInfo()
	{
		SoapRequestStatus = false;
		
		WhetherGetStartConf();  //test
		setSoapType(GenieGlobalDefines.ESoapRequestRouterInfo);
		
		ArrayList<String> element = new ArrayList<String>();
		//element.add("ModelName");
		//element.add("null");
		
		GenieDebug.error("getRouterModelInfo","getRouterModelInfo 0");
		
		sendRequest2Router("DeviceInfo","GetInfo",element,true);
		
		WhetherGetFinishedConf();
		
		return SoapRequestStatus;
	}
	
	public Boolean getGetWan()
	{
		SoapRequestStatus = false;
		
		WhetherGetStartConf();  //test
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigWan);
		
		ArrayList<String> element = new ArrayList<String>();
		//element.add("ModelName");
		//element.add("null");
		
		//sendRequest2Router("DeviceInfo","GetInfo",element,true);
		
		String res = sendOpenDNSRequest2Router("WANIPConnection","GetInfo",element,false);
		String responseCode = getXMLText(res,"<ResponseCode>","</ResponseCode>");
		
		GenieDebug.error("getGetWan", "getGetWan responseCode ="+responseCode);
		
		//WhetherSetFinishConf();
		
		if(responseCode.indexOf("000") != -1)
		{
			String macaddress = getXMLText(res,"<NewMACAddress>","</NewMACAddress>");
			
			GenieDebug.error("getGetWan", "getGetWan macaddress ="+macaddress);
			
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS, macaddress);
			
			SoapRequestStatus = true;
			WhetherGetFinishedConf();
			return SoapRequestStatus;
		}
		else
		{
			WhetherGetFinishedConf();
			return  SoapRequestStatus;
		}
		
	}
	
	public void getWLanInfo()
	{
		WhetherGetStartConf();  //test
		
		GenieDebug.error("debug","getWLanInfo ");
		setSoapType(GenieGlobalDefines.ESoapRequestWLanInfo);
		
		ArrayList<String> element = new ArrayList<String>();
//		element.add("NewSSID");
//		element.add("null");
		
		sendRequest2Router("WLANConfiguration","GetInfo",element,true);
		
		WhetherGetFinishedConf();
	}
	
	void getWLanPassword()
	{
		WhetherGetStartConf();  //test
		
		GenieDebug.error("debug","getWLanPassword ");
		
		setSoapType(GenieGlobalDefines.ESoapRequestWLanWEPKey);
		
		ArrayList<String> element = new ArrayList<String>();
//		element.add("NewWPAPassphrase");
//		element.add("null");
		
		sendRequest2Router("WLANConfiguration","GetWPASecurityKeys",element,true);
		
		WhetherGetFinishedConf();
	}
	
	
	void getWEPWLanPassword()
	{
		WhetherGetStartConf();  //test
		
		GenieDebug.error("debug","getWEPWLanPassword ");
		
		setSoapType(GenieGlobalDefines.ESoapRequestWEPKey);
		
		ArrayList<String> element = new ArrayList<String>();		
		element.add("NewWEPKey");
		element.add("null");
		element.add("NewWPAPassphrase");
		element.add("null");
		
		sendRequest2Router("WLANConfiguration","GetWEPSecurityKeys",element,true);
		
		WhetherGetFinishedConf();
	}
	
	void getGuestAccessEnabled()
	{
		GenieDebug.error("debug","getGuestAccessEnabled ");
		
		WhetherGetStartConf();  //test
		
		setSoapType(GenieGlobalDefines.ESoapRequestGuestEnable);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewGuestAccessEnabled");
		element.add("null");

		sendRequest2Router("WLANConfiguration","GetGuestAccessEnabled",element,true);
		
		WhetherGetFinishedConf();
	}

	void getGuestAccessInfo()
	{
		WhetherGetStartConf();  //test
		
		GenieDebug.error("debug","getGuestAccessInfo ");
		
		setSoapType(GenieGlobalDefines.ESoapRequestGuestInfo);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewSSID");
		element.add("null");

		sendRequest2Router("WLANConfiguration","GetGuestAccessNetworkInfo",element,true);
		
		WhetherGetFinishedConf();
	}
	
	public void EnableBlockDeviceForAll()
	{
		GenieDebug.error("debug","SetBlockDeviceEnable ");
		
		configurationstarted();

		setSoapType(GenieGlobalDefines.ESoapRequestEnableBlockDeviceForAll);
		
		ArrayList<String> element = new ArrayList<String>();
//		element.add("NewBlockDeviceEnable");
//		element.add(Status);

		sendRequest2Router("DeviceConfig","EnableBlockDeviceForAll",element,false);
		
		configurationfinished();
	}
	
	public void SetBlockDeviceEnable(String Status)
	{
		GenieDebug.error("debug","SetBlockDeviceEnable ");
		
		configurationstarted();

		setSoapType(GenieGlobalDefines.ESoapRequestSetBlockDeviceEnable);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewBlockDeviceEnable");
		element.add(Status);

		sendRequest2Router("DeviceConfig","SetBlockDeviceEnable",element,false);
		
		configurationfinished();
	}
	
	
	public void SetBlockDeviceByMAC(String mac,String Status)
	{
		GenieDebug.error("debug","SetBlockDeviceByMAC ");
		
		configurationstarted();

		setSoapType(GenieGlobalDefines.ESoapRequestSetBlockDeviceByMac);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewMACAddress");
		element.add(mac);
		element.add("NewAllowOrBlock");
		element.add(Status);

		sendRequest2Router("DeviceConfig","SetBlockDeviceByMAC",element,false);
		
		configurationfinished();
	}
	
	public void GetBlockDeviceEnableStatus()
	{
		GenieDebug.error("debug","GetBlockDeviceEnableStatus ");
		
		WhetherGetStartConf();  //test
		
		setSoapType(GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus);
		
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE, GenieGlobalDefines.NULL);
		
		ArrayList<String> element = new ArrayList<String>();
//		element.add("NewAttachDevice");
//		element.add("null");

		sendRequest2Router("DeviceConfig","GetBlockDeviceEnableStatus",element,true);
		
		WhetherGetFinishedConf();
	}
	
	public void getRouterMap()
	{
		GenieDebug.error("debug","getRouterMap ");
		
		WhetherGetStartConf();  //test
		
		setSoapType(GenieGlobalDefines.ESoapRequestRouterMap);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewAttachDevice");
		element.add("null");

		sendRequest2Router("DeviceInfo","GetAttachDevice",element,true);
		WhetherGetFinishedConf();
	}
	
	public void getTrafficMeterEnable()
	{
		WhetherGetStartConf();  //test
		
		GenieDebug.error("debug","getTrafficMeterEnable ");
		
		setSoapType(GenieGlobalDefines.ESoapReqiestTrafficEnable);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewTrafficMeterEnable");
		element.add("null");
		
		sendRequest2Router("DeviceConfig","GetTrafficMeterEnabled",element,true);
		WhetherGetFinishedConf();
	}
	
	public Boolean getTrafficMeterOptions()
	{
		WhetherGetStartConf();  //test
		
		GenieDebug.error("debug","getTrafficMeterOptions ");
		SoapRequestStatus = false;
		
		setSoapType(GenieGlobalDefines.ESoapRequestTrafficOptions);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewTrafficMeterEnable");
		element.add("null");

		sendRequest2Router("DeviceConfig","GetTrafficMeterOptions",element,true);
		WhetherGetFinishedConf();
		return SoapRequestStatus;
	}
	
	
	public Boolean getTrafficMeter()
	{
		GenieDebug.error("debug","getTrafficMeter ");
		
		WhetherGetStartConf();  //test
		
		SoapRequestStatus = false;
		
		//GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC);
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC,"");
		
		setSoapType(GenieGlobalDefines.ESoapRequestTrafficMeter);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewTodayConnectionTime");
		element.add("null");

		sendRequest2Router("DeviceConfig","GetTrafficMeterStatistics",element,true);
		WhetherGetFinishedConf();
		return SoapRequestStatus;
	}
	
	public Boolean getDNSMasqDeviceID()
	{
		GenieDebug.error("debug","getDNSMasqDeviceID ");
		
		WhetherGetStartConf();  //test
		
		SoapRequestStatus = false;
		
		setSoapType(GenieGlobalDefines.ESoapRequestDeviceID);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewMACAddress");
		element.add("default");
		
		

		sendOpenDNSRequest2Router("ParentalControl","GetDNSMasqDeviceID",element,true);
		
		GenieDebug.error("debug","getDNSMasqDeviceID return "+SoapRequestStatus);
		
		WhetherGetFinishedConf();
		
		SoapRequestStatus = true;
		
		return SoapRequestStatus;
	}
	

	
//	public void getParentalControlStatus()
//	{
//		GenieDebug.error("debug","getParentalControlStatus ");
//		
//		setSoapType(GenieGlobalDefines.ESoapRequestPCStatus);
//		
//		ArrayList<String> element = new ArrayList<String>();
//		element.add("NewEnable");
//		element.add("null");
//
//		sendRequest2Router("ParentalControl","GetEnableStatus",element,true);
//	}
	
	public Boolean AutoRouterLogin(String username ,String password)
	{
		
		configurationfinished();
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewPassword");
		element.add(password);
		element.add("NewUsername");
		element.add(username);

		
		setSoapType(GenieGlobalDefines.ESoapAuthenticate);
		String res = sendOpenDNSRequest2Router("ParentalControl","Authenticate",element,false);
		String responseCode = getXMLText(res,"<ResponseCode>","</ResponseCode>");

		if(null != responseCode)
		{	
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_VERYTI_OK, responseCode);
		
			if(responseCode.indexOf("000") == -1)
			{
				return true;
			}
			else
			{
				return  false;
			}
		}else
		{
			return  false;
		}
	}
	
	public void authenticateUserAndPassword(String username ,String password)
	{
		
		configurationfinished();
		
		//password = "213132132";
		
		
		setSoapType(GenieGlobalDefines.ESoapRequestVerityUser);
		
		GenieDebug.error("debug","authenticateUserAndPassword 0");

		ArrayList<String> element = new ArrayList<String>();
		element.add("NewPassword");
		element.add(password);
		element.add("NewUsername");
		element.add(username);
		
		GenieDebug.error("debug","authenticateUserAndPassword 1");

		
		
		//setSoapType(GenieGlobalDefines.ESoapAuthenticate);
		
		String res = sendRequest2Router("ParentalControl","Authenticate",element,false);
		if(res == null)
		{
			if(null != pMainView)
			{
				pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestLoginRouterFailure);
			}
			return;
		}
		
		
		String responseCode = getXMLText(res,"<ResponseCode>","</ResponseCode>");

		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_VERYTI_OK, responseCode);
		
		if(null == responseCode || (null != responseCode && responseCode.indexOf("000") == -1))
		{
			GenieDebug.error("debug","authenticateUserAndPassword 2");
			
			if(null == pMainView)
			{
				GenieDebug.error("debug","the pMainView = null!!!!!");
			}
			else
			{
				pMainView.Auto_Login(true);
				//pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
				//pMainView.ShowLoginBad_passwordVisible();
				
			}
		}
		else
		{
			GenieDebug.error("debug","authenticateUserAndPassword 3");
			
			//if(pMainView.IsNetgearRouter)
			//{
				pMainView.SaveUserinfo2File(password);
				
				GenieDebug.error("debug","authenticateUserAndPassword 4");
				//getRouterModelInfo(); //qicheng.ai change
				if(GetCurrentSetting())
				{
					GenieDebug.error("debug","authenticateUserAndPassword 5");
				}else
				{
					GenieDebug.error("debug","authenticateUserAndPassword 6");
					//pMainView.ShowLoginBad_passwordVisible();
					pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestLoginRouterFailure);
					
				}
			//}
		}
	}
	
	private boolean GetCurrentSetting()
	{
		String mFV = "";
		String mMN = "";
		String InternetStatus = "";
		String LpcSupport = "";
		
		m_GateWayIp = getGateWay();
		
		GenieDebug.error("debug","GetCurrentSetting 0");
		
		
		HttpGet request = new HttpGet("http://"+m_GateWayIp+"/currentsetting.htm");
		//HttpGet request = new HttpGet("http://routerlogin.net/currentsetting.htm");
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,http_timeout);
        HttpConnectionParams.setSoTimeout(httpParams,http_timeout);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        
        
        GenieDebug.error("debug","GetCurrentSetting 1");
        
        try
        {
        	GenieDebug.error("debug","GetCurrentSetting 2");
        	
        	HttpResponse response = httpClient.execute(request);
        	
        	
        	GenieDebug.error("debug","GetCurrentSetting 3");
        	
        	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        	{
        		GenieDebug.error("debug","GetCurrentSetting 4");
        		String recBuffer = EntityUtils.toString(response.getEntity());
        		
        		GenieDebug.error("debug","GetCurrentSetting 5 recBuffer = "+recBuffer);
        		
        		if(recBuffer.startsWith("Firmware="))
        		{
        			GenieDebug.error("debug","GetCurrentSetting 5.1");
        			int d = recBuffer.indexOf("=");
        			GenieDebug.error("debug","GetCurrentSetting 5.2 d = "+d);
        			int r = recBuffer.indexOf("\r");
        			if(r == -1)
        			{
        				r = recBuffer.indexOf("\n");
        			}
        			GenieDebug.error("debug","GetCurrentSetting 5.3 r = "+r);
        			mFV = recBuffer.substring(d + 1, r).replaceAll("\\s*", "");
        			//mFV.replaceAll("\\s*", "");
        			GenieDebug.error("debug","GetCurrentSetting 5.4 mFV = ["+mFV+"]");
        			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION, mFV);
        		}
        		GenieDebug.error("debug","GetCurrentSetting 6");
        		int mindex = recBuffer.indexOf("Model=");
        		if(mindex > 0)
        		{
        			GenieDebug.error("debug","GetCurrentSetting 6.1");
        			
        			String s = recBuffer.substring(mindex);
        			
        			GenieDebug.error("debug","GetCurrentSetting 6.2");
        			
        			int d = s.indexOf("=");
        			GenieDebug.error("debug","GetCurrentSetting 6.3");
        			
        			int r = s.indexOf("\r");
        			if(r == -1)
        			{
        				r = s.indexOf("\n");
        			}
        			GenieDebug.error("debug","GetCurrentSetting 6.4");
        			mMN = s.substring(d + 1, r).replaceAll("\\s*", "");
        			//mMN.replaceAll("\\s*", "");
        			GenieDebug.error("debug","GetCurrentSetting 6.5 mMN =["+mMN+"]");
        			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME,mMN);
        		}
        		
        		int InternetConnectionStatus = recBuffer.indexOf("InternetConnectionStatus=");
        		if(InternetConnectionStatus > 0)
        		{
        			GenieDebug.error("debug","GetCurrentSetting 7.1");
        			
        			String s = recBuffer.substring(InternetConnectionStatus);
        			
        			GenieDebug.error("debug","GetCurrentSetting 7.2");
        			
        			int d = s.indexOf("=");
        			GenieDebug.error("debug","GetCurrentSetting 7.3");
        			
        			int r = s.indexOf("\r");
        			if(r == -1)
        			{
        				r = s.indexOf("\n");
        			}
        			GenieDebug.error("debug","GetCurrentSetting 7.4");
        			InternetStatus = s.substring(d + 1, r).replaceAll("\\s*", "");
        			//InternetStatus.replaceAll("\\s*", "");
        			GenieDebug.error("debug","GetCurrentSetting 7.5 InternetStatus = ["+InternetStatus+"]");
        			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS,InternetStatus);
        			
        			
        		}
        		
        		
        		int ParentalControlSupported = recBuffer.indexOf("ParentalControlSupported=");
        		if(ParentalControlSupported > 0)
        		{
        			GenieDebug.error("debug","GetCurrentSetting 8.1");
        			
        			String s = recBuffer.substring(ParentalControlSupported);
        			
        			GenieDebug.error("debug","GetCurrentSetting 8.2");
        			
        			int d = s.indexOf("=");
        			GenieDebug.error("debug","GetCurrentSetting 8.3");
        			
        			int r = s.indexOf("\r");
        			if(r == -1)
        			{
        				r = s.indexOf("\n");
        			}
        			GenieDebug.error("debug","GetCurrentSetting 8.4");
        			LpcSupport = s.substring(d + 1, r).replaceAll("\\s*", "");
        			//LpcSupport.replaceAll("\\s*", "");
        			GenieDebug.error("debug","GetCurrentSetting 8.5 LpcSupport = ["+LpcSupport+"]");
        			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED,LpcSupport);
        			
        			
        		}
        		
        		
        		
        		GenieDebug.error("debug","GetCurrentSetting 7");
        		getWLanMACAddress();
        		
        		GenieDebug.error("debug","GetCurrentSetting 8");
        		pMainView.setModelNameAndVersion();
        		
        		GenieDebug.error("debug","GetCurrentSetting 9");
        	}
        }
        catch (UnknownHostException e)
        {
        	e.printStackTrace();
        	return false;
        }
        catch (HttpHostConnectException e)
        {
        	e.printStackTrace();
        	return false;
        }
        catch (ConnectTimeoutException e)
        {
        	e.printStackTrace();
        	return false;
        }
        catch (ClientProtocolException e)
        {
        	e.printStackTrace();
        	return false;
        }
        catch (IOException e)
        {
        	e.printStackTrace();
        	return false;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	return false;
        }
        return true;
	}
	
	
	String getConfigurationStartResponce()
	{
		GenieDebug.error("debug","getConfigurationStartResponce ");
		soapType = GenieGlobalDefines.ESoapRequestConfigStart;
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewSessionID");
		element.add("123456789A123456789A");
		
		String res = sendRequest2Router("DeviceConfig","ConfigurationStarted",element,false);
		return getXMLText(res,"<ResponseCode>","</ResponseCode>");
	}
	
	public void configurationStarted(int requestIndex)
	{
		if(getConfigurationStartResponce().indexOf("000") != -1)
		{
			timer.schedule(task, 6000);   
		}
	}

	public void startConfigGuestSettings()
	{
		configurationStarted(GenieGlobalDefines.ESoapRequestConfigGuest);
	}
	
	public void timerConfigurationFinish()
	{
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {   
			public void run() 
			{ 
				configurationFinish();
			}},6000);
	}
	
	public void timerForFinishConfiguration()
	{	
		Timer timer = new Timer();
		
		GenieDebug.error("debug","timerForFinishConfiguration activeSetItem = "+activeSetItem);

		switch (activeSetItem) 
		{
			case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
				timer.schedule(new TimerTask() {   
					public void run()
					{ 
						getWLanInfo();
					}},10000);
				break;
				
			case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
				timer.schedule(new TimerTask() {
					public void run()
					{
						getGuestAccessEnabled();
						//getGuestAccessInfo();//qicheng.ai add
					}},10000);
				break;
				
			case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
				timer.schedule(new TimerTask() {
					public void run()
					{
						getTrafficMeterEnable();
					}},10000);
				break;
				
			case GenieGlobalDefines.ACTIVE_FUNCTION_PARENT_CONTROL:
				break;
				
			default:
				break;
		}
	}
	
	public void getConfigurationFinishResponce()
	{
		soapType = GenieGlobalDefines.ESoapRequestConfigFinish;
		ArrayList<String> element = new ArrayList<String>();
		element.add("NewStatus");
		element.add("123456789A123456789A");
		
		sendRequest2Router("DeviceConfig","ConfigurationFinished",element,false);
	}
	
	public void configurationFinish()
	{
		getConfigurationFinishResponce();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {   
			public void run() 
			{ 
				timerForFinishConfiguration();
			}},20000);
	}
	
	public Boolean setWLANNoSecurity()
	{
		
		WhetherSetStartConf();
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigNoSecurity);
		
		SoapRequestStatus = false;
		
		GenieDebug.error("setWLANNoSecurity","setWLANNoSecurity --strart--");
		ArrayList<String> element = new ArrayList<String>();
		element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE));
		
		for(int i = 0; i < element.size(); i ++)
		{
			GenieDebug.error("setWLANNoSecurity",element.get(i));
		}
		
		sendRequest2Router("WLANConfiguration","SetWLANNoSecurity",element,false);
		
		configurationfinished();  //test
		
		return SoapRequestStatus;
	}
	
	
//	public void setParentalControlStatus()
//	{
//		soapType = GenieGlobalDefines.ESoapRequestConfigPCStatus;
//		
//		ArrayList<String> element = new ArrayList<String>();
//		element.add(GenieGlobalDefines.DICTIONARY_KEY_PC_ENABLE);
//		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_PC_ENABLE));
//		
//		sendRequest2Router("ParentalControl","EnableParentalControl",element,false);
//		timerConfigurationFinish();
//	}


	
	public Boolean setWLANWPAPSKByPassphrase()
	{
		WhetherSetStartConf();
		
		SoapRequestStatus = false;
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigWLan);
		activeSetItem = GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS;
		
		if(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY_SET).equals("None"))
		{
			setWLANNoSecurity();
		}
		else 
		{
			ArrayList<String> element = new ArrayList<String>();
			element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
			element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID_SET));
			element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION);
			//element.add("US");
			element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION));
			element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL);
			//element.add("6");
			element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL_SET));
			element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE);			
			//element.add("g &amp; b");
			element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE));
			element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY);
			element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY_SET));
			element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY);
			element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY_SET));
			
			sendRequest2Router("WLANConfiguration","SetWLANWPAPSKByPassphrase",element,false);
		}
		
		//timerConfigurationFinish();
		
		configurationfinished();  //test
		
		return SoapRequestStatus;
	}
	
	public Boolean setGuestAccessEnabled()
	{
		WhetherSetStartConf();
		
		SoapRequestStatus = false;
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigGuestEnable);
		activeSetItem = GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS;
		
		ArrayList<String> element = new ArrayList<String>();
		element.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE);
		element.add("0");
		
		sendRequest2Router("WLANConfiguration","SetGuestAccessEnabled",element,false);
		
		configurationfinished();  //test
		
		return SoapRequestStatus ;
	}
	
	
	public Boolean SetGuestAccessNetwork()
	{
		WhetherSetStartConf();
		
		SoapRequestStatus = false;
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigGuestAccessNetwork);
		activeSetItem = GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS;
		
		ArrayList<String> element = new ArrayList<String>();
		//element.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE);
		//element.add("1");
		element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET));
		element.add("NewKey1");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY_SET));
		element.add("NewKey2");
		element.add("0");
		element.add("NewKey3");
		element.add("0");
		element.add("NewKey4");
		element.add("0");
		
		sendRequest2Router("WLANConfiguration","SetGuestAccessNetwork",element,false);
		
		configurationfinished();  //test
		
		return SoapRequestStatus ;
	}
	
	public Boolean setGuestAccessEnabled2()
	{
		WhetherSetStartConf();
		
		SoapRequestStatus = false;
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigGuestEnable2);
		activeSetItem = GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS;
		
		ArrayList<String> element = new ArrayList<String>();
		element.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE);
		element.add("1");
		element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET));
		element.add("NewKey1");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY_SET));
		element.add("NewKey2");
		element.add("0");
		element.add("NewKey3");
		element.add("0");
		element.add("NewKey4");
		element.add("0");
		
		sendRequest2Router("WLANConfiguration","SetGuestAccessEnabled2",element,false);
		
		configurationfinished();  //test
		
		return SoapRequestStatus ;
	}
	
	void setGuestAccessNetwork()
	{
		WhetherSetStartConf();
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigGuest);
		activeSetItem = GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS;
		
		if(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).equals("1"))
		{
			
		}
		
		ArrayList<String> element = new ArrayList<String>();
		element.add(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE_SET));
		element.add("NewKey1");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY_SET));
		element.add("NewKey2");
		element.add("0");
		element.add("NewKey3");
		element.add("0");
		element.add("NewKey4");
		element.add("0");
		
		sendRequest2Router("WLANConfiguration","SetGuestAccessNetwork",element,false);
		
		configurationfinished();  //test
	}
	
	public Boolean setEnableTrafficMeter()
	{
		WhetherSetStartConf();
		
		SoapRequestStatus = false;
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigTrafficEnable);
		activeSetItem = GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC;
		
		ArrayList<String> element = new ArrayList<String>();
		element.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE_SET));
		
		sendRequest2Router("DeviceConfig","EnableTrafficMeter",element,false);
		
		configurationfinished();  //test
		
		return SoapRequestStatus;
	}
	
	public Boolean setTrafficMeterOptions()
	{
		WhetherSetStartConf();
		
		SoapRequestStatus = false;
		
		setSoapType(GenieGlobalDefines.ESoapRequestConfigTraffic);
		activeSetItem = GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC;
		
		ArrayList<String> element = new ArrayList<String>();
		element.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE_SET));
		element.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY_SET));
		
		sendRequest2Router("DeviceConfig","SetTrafficMeterOptions",element,false);
		
		configurationfinished();  //test
		
		return SoapRequestStatus;
	}

	//
	private void WhetherGetStartConf()
	{
		String md = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
		if(md == null)
			return ;
		if(md.startsWith("CG") || md.startsWith("DG"))
			configurationstarted();	
	}
	private void WhetherGetFinishedConf()
	{
		String md = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
		if(md == null)
			return ;
		if(md.startsWith("CG") || md.startsWith("DG"))
			configurationfinished();	
	}

	private void WhetherSetStartConf()
	{
		//String md = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
		//if(md.startsWith("CG") || md.startsWith("DG"))
		//	;
		//else
			configurationstarted();	  
	}
	
	//


	public void parserStringForNetworkMap(String string)
	{
		char num = 0;
		if(string.length() == 0 || string.charAt(0) == '\0') return;

		num = string.charAt(0);
		
		GenieDebug.error("parserStringForNetworkMap","num = "+num);
		GenieDebug.error("parserStringForNetworkMap","string = "+string);
		
		//string = "8@1;192.168.1.3;LIANG-PC;EC:55:F9:19:9D:26;wireless@2;192.168.1.4;PC-200911061155;00:92:C3:84:19:72;wireless@3;192.168.1.5;--;28:37:37:12:C6:9D;wireless@4;192.168.1.6;NEWMAC;60:33:4B:0D:D1:A1;wireless@5;192.168.1.7;ANDROID_9774D56D682E549Cdsfgdgfdgfdgdfg2132;B4:B3:62:BB:39:38;wireless@6;192.168.1.10;GKY-S-ITOUCH;8C:7B:9D:AD:BE:BB;wireless@7;192.168.1.11;ANDROID_357710047293728;64:A7:69:3F:E6:E9;wireless@8;192.168.1.12;YASUFEIDEIPAD;D8:A2:5E:31:4A:2D;wireless@";
		//string = "9@1;192.168.1.5;ANDROID_357710047293728;64:A7:69:3F:E6:E9;wireless@2;192.168.1.23;SITEVIEW;00:11:11:45:53:B0;wired@3;192.168.1.9;LIANG-PC;EC:55:F9:19:9D:26;wireless@4;192.168.1.17;SHENGLANPC;00:13:CE:33:AB:F3;wireless@5;192.168.1.100;JOHN;E8:06:88:8F:49:FC;wireless@6;192.168.1.14;ALIENWARE;F0:7B:CB:8F:8A:30;wireless@7;192.168.1.11;NEWMAC;60:33:4B:0D:D1:A1;wireless@8;192.168.1.3;ANDROID_FCD0EEBC1C7452A1;98:4B:4A:7E:33:EA;wireless@9;192.168.1.19;MACMINI-032D7D;28:37:37:12:C6:9D;wireless";
		//string = "7@1;172.16.0.13;ANDROID_9774D56D682E549C;B4:B3:62:BB:39:38;wireless;58;90;Allow@2;172.16.0.5;ANDROID_357710047293728;64:A7:69:3F:E6:E9;wireless;54;70;Allow@3;172.16.0.9;PC-200911061155;00:92:C3:84:19:72;wireless;108;94;Allow@4;172.16.0.10;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless;54;92;Allow@5;172.16.0.6;EASYFLYS-IPAD;D8:A2:5E:31:4A:2D;wireless;65;100;Allow@6;172.16.0.2;<unknown>;28:37:37:12:C6:9D;wireless;130;90;Allow@7;172.16.0.8;GKY-S-ITOUCH;8C:7B:9D:AD:BE:BB;wireless;65;76;Allow";
		routerMap.clear();
		StringBuffer cmpKey = new StringBuffer();
		
		int i = 1;
		int posS = 0;
		int posE = 0;
		
		cmpKey.replace(0, cmpKey.length(), "@"+i+";");
		
		while((i <= num) && ((posS = string.indexOf(cmpKey.toString())) != -1)) 
		{
			i++;
			cmpKey.replace(0, cmpKey.length(), "@"+i+";");
			
			if((posE = string.indexOf(cmpKey.toString())) == -1)
			{
				posE = string.length();
			}
			
			String devicesinfo = string.substring(posS + cmpKey.length(), posE);
			if(devicesinfo != null)
			{	GenieDebug.error("debug","test  = "+devicesinfo);
				int n = 0;
				HashMap<String, String> detail = new HashMap<String, String>();
				
				for (String word : devicesinfo.split(";")) 
				{
					GenieDebug.error("debug","test word = "+word);
					if(word.endsWith("@"))
					{
						GenieDebug.error("debug","word.endsWith(@)");
						word = word.substring(0,(word.length()-1));
					}
					
					switch(n)
					{
					case 0:
						detail.put(GenieGlobalDefines.DEVICE_IP,word);
						break;
					case 1:
						detail.put(GenieGlobalDefines.DEVICE_NAME,word);
						break;
					case 2:
						detail.put(GenieGlobalDefines.DEVICE_MAC,word);
						break;
					case 3:
						detail.put(GenieGlobalDefines.DEVICE_CONNECTTYPE,word);
						break;
					case 4:
						detail.put(GenieGlobalDefines.DEVICE_SPEED,word);
						break;
					case 5:
						detail.put(GenieGlobalDefines.DEVICE_INTENSITY,word);
						break;
					case 6:
						detail.put(GenieGlobalDefines.DEVICE_BLOCK,word);
						break;
					}
					
					n++;
				}
				routerMap.add(detail);
			}	
			
			
//			
//			StringBuffer device = new StringBuffer(string.substring(posS + cmpKey.length(), posE));
//			HashMap<String, String> detail = new HashMap<String, String>();
//
//			posE = device.indexOf(";");
//			detail.put(GenieGlobalDefines.DEVICE_IP, device.substring(0, posE));
//			device.replace(0, posE + 1, "");
//			
//			GenieDebug.error("parserStringForNetworkMap","device = "+device.toString());
//			
//			posE = device.indexOf(";");
//			detail.put(GenieGlobalDefines.DEVICE_NAME, device.substring(0, posE));
//			device.replace(0, posE + 1, "");
//			
//			GenieDebug.error("parserStringForNetworkMap","device = "+device.toString());
//			
//			posE = device.indexOf(";");
//			GenieDebug.error("parserStringForNetworkMap","i = "+i);
//			GenieDebug.error("parserStringForNetworkMap","posE = "+posE);
//			GenieDebug.error("parserStringForNetworkMap","device = "+device.toString());
//			if(posE == -1)
//			{
//				posE = device.length();
//			}
//			detail.put(GenieGlobalDefines.DEVICE_MAC, device.substring(0, posE));
//			device.replace(0, posE + 1, "");
//			
//			posE = device.indexOf(";");
//			GenieDebug.error("parserStringForNetworkMap","i = "+i);
//			GenieDebug.error("parserStringForNetworkMap","posE = "+posE);
//			GenieDebug.error("parserStringForNetworkMap","device = "+device.toString());
//			if(posE == -1)
//			{
//				posE = device.length();
//			}
//			detail.put(GenieGlobalDefines.DEVICE_CONNECTTYPE, device.substring(0, posE));
//			device.replace(0, posE + 1, "");
//			
//			posE = device.indexOf(";");
//			if(posE == -1)
//			{
//				posE = device.length();
//			}
//			detail.put(GenieGlobalDefines.DEVICE_SPEED, device.substring(0, posE));
//			device.replace(0, posE + 1, "");
//			
//			posE = device.indexOf(";");
//			if(posE == -1)
//			{
//				posE = device.length();
//			}
//			detail.put(GenieGlobalDefines.DEVICE_INTENSITY, device.substring(0, posE));
//			device.replace(0, posE + 1, "");
//			
//			routerMap.add(detail);
		}
	}
	
	public void isParseTextTag(String elementName)
	{
		GenieDebug.error("isParseTextTag","elementName = "+elementName);
		GenieDebug.error("isParseTextTag","soapType = "+soapType);
		switch (soapType) 
		{
			case GenieGlobalDefines.ESoapRequestIsGenie:
				break;
				
			case GenieGlobalDefines.ESoapRequestVerityUser:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_RESPONCE))
				{
					recordResults = 1;
				}
				break;
				
			case GenieGlobalDefines.ESoapRequestRouterInfo:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME))
				{
					elementIndex =GenieGlobalDefines.ROUTER_INFO_ROUTER_NAME;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION))
				{
					elementIndex = GenieGlobalDefines.ROUTER_INFO_FIRMWAREVERSIN;
					recordResults = 1;
				}
				break;
				
			case GenieGlobalDefines.ESoapRequestWLanInfo:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_ENABLE))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_ENABLE;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_SSID;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_CHANNEL;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_REGION;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_WIRELESS_MODES;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_WPA_MODES;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_MAC_ADDRESS;
					recordResults = 1;
				}else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_STATUS))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_NET_STATUS;
					recordResults = 1;
				}else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES))
				{
					elementIndex = GenieGlobalDefines.WLAN_INFO_BASIC_MODES;
					recordResults = 1;
				}
				break;
				
			case GenieGlobalDefines.ESoapRequestWLanWEPKey:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY))
				{
					recordResults = 1;
				}
				break;
			case GenieGlobalDefines.ESoapRequestWEPKey:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY))
				{
					recordResults = 1;
				}
				break;	
				
			case GenieGlobalDefines.ESoapRequestGuestEnable:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE))
				{
					recordResults = 1;
				}
				
				break;

			case GenieGlobalDefines.ESoapRequestGuestInfo:
				
				GenieDebug.error("GUEST_INFO_SSID", " 99999 parse ESoapRequestGuestInfo elementName = "+elementName);
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID))
				{
					elementIndex = GenieGlobalDefines.GUEST_INFO_SSID;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE))
				{
					elementIndex = GenieGlobalDefines.GUEST_INFO_SECURITY_MODE;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY))
				{
					elementIndex = GenieGlobalDefines.GUEST_INFO_KEY;
					recordResults = 1;
				}
				
				GenieDebug.error("GUEST_INFO_SSID", " 99999 parse ESoapRequestGuestInfo elementIndex = "+elementIndex);
				GenieDebug.error("GUEST_INFO_SSID", " 99999 parse ESoapRequestGuestInfo recordResults = "+recordResults);
				break;
			case GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus:
				GenieDebug.error("isParseTextTag","BlockDeviceEnableStatus elementName = "+elementName);
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE))
				{
					recordResults = 1;
				}
				break;
			case GenieGlobalDefines.ESoapRequestRouterMap:
				
				GenieDebug.error("isParseTextTag","elementName = "+elementName);				
				
				if(elementName.equals("NewAttachDevice") || elementName.equals("AttachDevice"))
				{
					recordResults = 1;
				}
				break;
				
			case GenieGlobalDefines.ESoapReqiestTrafficEnable:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE))
				{
					recordResults = 1;
				}
				break;
				
			case GenieGlobalDefines.ESoapRequestTrafficMeter:
				if(elementName.indexOf("NewToday") >= 0	|| elementName.indexOf("NewYesterday") >= 0
						|| elementName.indexOf("NewWeek") >= 0 || elementName.indexOf("NewMonth") >= 0
						|| elementName.indexOf("NewLastMonth") >= 0)
				{
					recordResults = 1;
				}
				break;

			case GenieGlobalDefines.ESoapRequestTrafficOptions:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL))
				{
					elementIndex = GenieGlobalDefines.TRAFFIC_OPTION_CONTROL;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT))
				{
					elementIndex = GenieGlobalDefines.TRAFFIC_OPTION_LIMIT;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR))
				{
					elementIndex = GenieGlobalDefines.TRAFFIC_OPTION_HOUR;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE))
				{
					elementIndex = GenieGlobalDefines.TRAFFIC_OPTION_MINUTE;
					recordResults = 1;
				}
				else if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY))
				{
					elementIndex = GenieGlobalDefines.TRAFFIC_OPTION_DAY;
					recordResults = 1;
				}
				break;

			case GenieGlobalDefines.ESoapRequestDeviceID:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID))
				{
					recordResults = 1;
				}
				break;
				
			case GenieGlobalDefines.ESoapRequestPCStatus:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS))
				{
					recordResults = 1;
				}
				break;
				
			default:
				if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_RESPONCE))
				{
					recordResults = GenieGlobalDefines.IS_RESPNCE_VALUE;
				}
				break;
		}
	}
	
	public void tackleXmlText(String string)
	{
		if(0 == recordResults || string.equals("\n"))
		{
			recordResults = 0;
			return;
		}
		GenieDebug.error("tackleXmlText","soapType = "+soapType);
		GenieDebug.error("tackleXmlText","elementIndex = "+elementIndex);
		GenieDebug.error("tackleXmlText","string = "+string);
		
		switch (soapType) 
		{
		case GenieGlobalDefines.ESoapRequestRouterInfo:
			switch (elementIndex) 
			{
			case GenieGlobalDefines.ROUTER_INFO_ROUTER_NAME:
				SoapRequestStatus = true;
				GenieDebug.error("tackleXmlText","string = "+string);
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME, string);
				break;
						
			case GenieGlobalDefines.ROUTER_INFO_FIRMWAREVERSIN:
				SoapRequestStatus = true;
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION, string);
				break;
			}
				
			break;
				
		case GenieGlobalDefines.ESoapRequestWLanInfo:
			GenieDebug.error("ESoapRequestWLanInfo","ESoapRequestWLanInfo elementIndex = "+elementIndex);
			GenieDebug.error("ESoapRequestWLanInfo","ESoapRequestWLanInfo elementIndex = "+string);
			switch (elementIndex) 
			{
			case GenieGlobalDefines.WLAN_INFO_ENABLE:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_ENABLE, string);
				break;
					
			case GenieGlobalDefines.WLAN_INFO_SSID:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID, string);
				break;
						
			case GenieGlobalDefines.WLAN_INFO_CHANNEL:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL, string);
				break;
						
			case GenieGlobalDefines.WLAN_INFO_REGION:
				if(string.equals("USA"))
				{
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION, "US");
				}else
				{
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION, string);
				}
				break;
						
			case GenieGlobalDefines.WLAN_INFO_WIRELESS_MODES:
				//string = "54Mbps";  //test
				GenieDebug.error("","WLAN_INFO_WIRELESS_MODES = "+string);				
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE, string);
				break;
						
			case GenieGlobalDefines.WLAN_INFO_WPA_MODES:
				GenieDebug.error("","WLAN_INFO_WPA_MODES = "+string);	
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY, string);
				break;
			case GenieGlobalDefines.WLAN_INFO_NET_STATUS:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_STATUS, string);
				break;
			case GenieGlobalDefines.WLAN_INFO_BASIC_MODES:
				GenieDebug.error("","wep777 WLAN_INFO_BASIC_MODES = "+string);
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, string);
				break;	
						
			case GenieGlobalDefines.WLAN_INFO_MAC_ADDRESS:
			{
				StringBuffer mac = new StringBuffer(string);
				
//				GenieDebug.error("WLAN_INFO_MAC_ADDRESS","mac = "+mac.toString());
//				for(int i = 10; i > 0; i -= 2)
//				{
//					mac.insert(i, ":");
//				}
				
				if(mac.length() <= 12)
				{
					for(int i = 10; i > 0; i -= 2)
					{
						mac.insert(i, ":");
					}
				}else
				{
//					for(int i = 10; i > 0; i -= 2)
//					{
//						
//						mac.deleteCharAt(i);
//						mac.insert(i, ":");
//					}
				}
				
				GenieDebug.error("WLAN_INFO_MAC_ADDRESS","mac = "+mac.toString());		
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC, mac.toString());
			}
			break;
			}
			
			break;

		case GenieGlobalDefines.ESoapRequestWLanWEPKey:
			if(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("None"))
			{
				GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, None)");
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, "None");
			}
			else
			{
				GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, string) string = "+string);
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, string);
			}
			break;
		case GenieGlobalDefines.ESoapRequestWEPKey:
			if(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES).equals("None"))
			{
				GenieDebug.error("debug", "wep777 dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, None)");
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, "None");
			}
			else
			{
				GenieDebug.error("debug", "wep777 dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, string) string = "+string);
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, string);
			}
			break;	
				
		case GenieGlobalDefines.ESoapRequestGuestEnable:
			if(recordResults == 1)
			{
				//string = "2";
				GenieDebug.error("DICTIONARY_KEY_GUEST_ABLE", "DICTIONARY_KEY_GUEST_ABLE = "+string);
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE, string);
			}
			break;
				
		case GenieGlobalDefines.ESoapRequestGuestInfo:
			GenieDebug.error("GUEST_INFO_SSID", " 99999 ESoapRequestGuestInfo elementIndex = "+elementIndex);
			switch (elementIndex) 
			{
			case GenieGlobalDefines.GUEST_INFO_SSID:
				GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_SSID = "+string);
				SoapRequestStatus = true;
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID, string);
				break;
					
			case GenieGlobalDefines.GUEST_INFO_SECURITY_MODE:
				GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_SECURITY_MODE = "+string);
				SoapRequestStatus = true;
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE, string);
				break;
					
			case GenieGlobalDefines.GUEST_INFO_KEY:
				
				GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_KEY = "+string); 
				
				if(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY).equals("None"))
				{
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY, "None");
				}
				else
				{
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY, string);
				}	

				break;
			}
				
			break;
		case GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus:
			GenieDebug.error("tackleXmlText","ESoapRequestBlockDeviceEnableStatus ="+string);
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE, string);
			break;				
		case GenieGlobalDefines.ESoapRequestRouterMap:
			//string = "3@1;192.168.1.7;<unknown>;00:13:CE:CE:91:6F@2;192.168.1.11;ALIENWARE;F0:7B:CB:8F:8A:30@3;192.168.1.2;MACMINI-0B23C3;60:33:4B:07:29:EF";
			//string = "8@1;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A7:69:3F:E6:E9;wireless@2;192.168.1.28;IPOD-TOUCH;F8:1E:DF:CA:7D:D0;wireless@3;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@4;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@5;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@6;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@7;192.168.1.7;<unknown>;60:33:4B:07:29:EF;wireless@8;192.168.1.7;<unknown>;60:33:4B:67:79:EF;wireless";
			//string = "24@1;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A7:59:3F:E6:E9;wireless@2;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@3;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@4;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@5;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@6;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@7;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A7:89:3F:E6:E9;wireless@8;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@9;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@10;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@11;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@12;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@13;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A5:69:3F:E6:E9;wireless@14;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@15;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@16;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@17;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@18;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@19;192.168.1.6;ANDROID_532894A0D7B65BF8;60:A7:69:3F:E6:E9;wireless@20;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@21;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@22;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@23;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@24;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless";
			//string = "3@1;192.168.1.2;BRAVIA07C094F7F644AA11;30:F9:ED:34:3B:5D@2;192.168.1.3;GENIE-PC;00:13:CE:CE:91:6F@3;192.168.1.4;ANDROID_8EB092BB95E89CF;C8:AA:21:80:A8:F1@";
			parserStringForNetworkMap(string);
			break;
				
		case GenieGlobalDefines.ESoapReqiestTrafficEnable:
			GenieDebug.error("tackleXmlText","ESoapReqiestTrafficEnable ="+string);
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE, string);
			break;
				
		case GenieGlobalDefines.ESoapRequestTrafficMeter:
			GenieDebug.error("tackleXmlText", string);
			StringBuffer temp = new StringBuffer();
			temp.append(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC));	
			temp.append(string);
			//temp.append("#");
			temp.append("\n");
			
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC, temp.toString());
			break;
				
		case GenieGlobalDefines.ESoapRequestTrafficOptions:
			GenieDebug.error("tackleXmlText", "ESoapRequestTrafficOptions string ="+string);
			GenieDebug.error("tackleXmlText", "ESoapRequestTrafficOptions elementIndex ="+elementIndex);
			switch (elementIndex) 
			{
			case GenieGlobalDefines.TRAFFIC_OPTION_CONTROL:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL, string);
				break;
					
			case GenieGlobalDefines.TRAFFIC_OPTION_LIMIT:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT, string);
				break;
				
			case GenieGlobalDefines.TRAFFIC_OPTION_HOUR:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR, string);
				break;
					
			case GenieGlobalDefines.TRAFFIC_OPTION_MINUTE:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE, string);
				break;
					
			case GenieGlobalDefines.TRAFFIC_OPTION_DAY:
				dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY, string);
				break;
			}
			
		case GenieGlobalDefines.ESoapRequestDeviceID:
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, string);
			GenieDebug.error("tackleXmlText","DICTIONARY_KEY_DEVICEID = "+string+"DICTIONARY_KEY_DEVICEID");
			SoapRequestStatus = true;
			GenieDebug.error("tackleXmlText","SoapRequestStatus = "+SoapRequestStatus);
			break;
			
		case GenieGlobalDefines.ESoapRequestPCStatus:
			SoapRequestStatus = true;
			GenieDebug.error("tackleXmlText","DICTIONARY_KEY_PC_STATUS = "+string);
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS, string);
			break;	
			
		default:
			break;
		}
		
		recordResults = 0;
	}
	
	public void getWLanMACAddress()
	{
		
		WhetherGetStartConf();
		try{
			ArrayList<String> element = new ArrayList<String>();
			//element.add("NewSSID");
			//element.add("null");
	
			//sendRequest2Router("WLANConfiguration","GetInfo",element,true);
			
			setSoapType(GenieGlobalDefines.ESoapRequestWLanInfo);
			
			String s = sendRequest2Router("WLANConfiguration","GetInfo",element,false);
			if(s == null || s == "")
				return;
			
			GenieDebug.error("getWLanMACAddress", " getWLanMACAddress  s =  "+s);
			
			String ResponseCode = getXMLText(s,"<ResponseCode>","</ResponseCode>");
			
			if(ResponseCode == null || ResponseCode.indexOf("000") == -1)
			{
				return ;
			}
	
			
			String wepmode =  getXMLText(s,"<NewBasicEncryptionModes>","</NewBasicEncryptionModes>");
			
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES,wepmode);
				
			GenieDebug.error("getWLanMACAddress", " wep777  wepmode =  "+wepmode);	
			
			String macaddress =  getXMLText(s,"<NewWLANMACAddress>","</NewWLANMACAddress>");
			
			if(macaddress == null)
				return ;
			
			StringBuffer mac = new StringBuffer(macaddress);
			if(mac.length() == 12)
			{
				for(int i = 10; i > 0; i -= 2)
				{
					mac.insert(i, ":");
				}
			}else
			{
	//			for(int i = 10; i > 0; i -= 2)
	//			{
	//				
	//				//mac.deleteCharAt(i);
	//				//mac.insert(i, ":");
	//				mac.
	//			}
			}
			//dg
			
			GenieDebug.error("getWLanMACAddress", "DICTIONARY_KEY_WLAN_MAC = "+mac.substring(0));
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC, mac.substring(0));
		}catch(Exception e){ 
			e.printStackTrace();
		}
		WhetherGetFinishedConf();
	}
	
	public void endParseXml()
	{
		GenieDebug.error("endParseXml", "endParseXml soapType = "+soapType);
		switch (soapType) 
		{
		case GenieGlobalDefines.ESoapRequestRouterInfo:
			if(GenieGlobalDefines.GenieView_MainView == parentview)
			{
				pMainView.setModelNameAndVersion();
				//new Thread()
		    	//{
		    	//	public void run() 
		    	//	{   
		    			getWLanMACAddress();
		    	//	}
		    	//}.start();
				
			}
			break;
					
		case GenieGlobalDefines.ESoapRequestWLanInfo:
			
			//new Thread()
	    	//{
	    	//	public void run() 
	    	//	{   
			try{	
				if(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES).equals("WEP"))
				{
					getWEPWLanPassword();
				}else
				{
		    		getWLanPassword();
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				getWLanPassword();
			}
			
	    	//	}
	    	//}.start();
	    			
	    			
	    	
			
			break;
					
		case GenieGlobalDefines.ESoapRequestWLanWEPKey:
			if(GenieGlobalDefines.GenieView_ListView == parentview)
			{
				pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestWLanInfo);
			}
			break;
		case GenieGlobalDefines.ESoapRequestWEPKey:
			if(GenieGlobalDefines.GenieView_ListView == parentview)
			{
				pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestWLanInfo);
			}
			break;	
					
		case GenieGlobalDefines.ESoapRequestGuestEnable:
			
			//new Thread()
	    	//{
	    	///	public void run() 
	    	//	{   
	    			getGuestAccessInfo();  //qicheng.ai change
	    	//	}
	    	//}.start();
			
			break;
					
		case GenieGlobalDefines.ESoapRequestGuestInfo:
			if(GenieGlobalDefines.GenieView_ListView == parentview)
			{
				pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestGuestInfo);
			}
			break;
					
		case GenieGlobalDefines.ESoapRequestRouterMap:
//			if([routerMap count])
//			{
//				[self startThead:0];
//			}
			if(GenieGlobalDefines.GenieView_Map == parentview)
			{
				pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestRouterMap);
			}
			break;
					
		case GenieGlobalDefines.ESoapReqiestTrafficEnable:
			//getTrafficMeter();
			break;
					
		case GenieGlobalDefines.ESoapRequestTrafficMeter:
			//getTrafficMeterOptions();
			if(GenieGlobalDefines.GenieView_ListView == parentview)
			{
				pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestTrafficMeter);
			}
			break;
			
		case GenieGlobalDefines.ESoapRequestTrafficOptions:
			SoapRequestStatus = true;
			break;
			
		default:
			break;
		}
	}
	
	public void parseXml(InputStream paseText) throws Exception 
	{   
		XmlPullParser parser = Xml.newPullParser();   
		parser.setInput(paseText, "utf-8");   

		int type = parser.getEventType();
		GenieDebug.error("parseXml", "parseXml type = "+type);
		while (type != XmlPullParser.END_DOCUMENT) 
		{
			switch (type) 
			{
			case XmlPullParser.START_DOCUMENT:   
				break;   

			case XmlPullParser.START_TAG:
				recordResults = 0;
				isParseTextTag(parser.getName());
				break;

			case XmlPullParser.TEXT:
				tackleXmlText(parser.getText());
				break;
					
			case XmlPullParser.END_TAG:
				break;
			}

			type = parser.next();   
		}

		paseText.close();
		endParseXml();
	}
	
	public void configurationRouterstarted()
	{
		soapType = GenieGlobalDefines.ESoapRequestConfigStart;
		
		GenieDebug.error("configurationstarted","--strart--");
		ArrayList<String> element = new ArrayList<String>();	
		
		GenieDebug.error("configurationstarted",""+element.size());
		
		//element.add("NewSessionID");
		//element.add(m_SessionID);
		
		sendRequest2Router("DeviceConfig","ConfigurationStarted",element,false);
	}
	
	
	public void configurationRouterfinished()
	{
		soapType = GenieGlobalDefines.ESoapRequestConfigFinish;
		
		GenieDebug.error("configurationfinished","--strart--");
		ArrayList<String> element = new ArrayList<String>();
		

		element.add("NewStatus");
		element.add("ChangesApplied");
		
		
		GenieDebug.error("configurationfinished",""+element.size());		
		
		sendRequest2Router("DeviceConfig","ConfigurationFinished",element,false);
	}
	
	public void configurationstarted()
	{
		soapType = GenieGlobalDefines.ESoapRequestConfigStart;
		
		GenieDebug.error("configurationstarted","--strart--");
		ArrayList<String> element = new ArrayList<String>();	
		
		GenieDebug.error("configurationstarted",""+element.size());
		
		//element.add("NewSessionID");
		//element.add("99999999999999999999");
		//element.add("E6A88AE69687E58D9A00");
		//element.add(m_SessionID);
		
		sendRequest2Router("DeviceConfig","ConfigurationStarted",element,false);
		
	}
	public void configurationfinished()
	{
		soapType = GenieGlobalDefines.ESoapRequestConfigFinish;
		
		GenieDebug.error("configurationfinished","--strart--");
		ArrayList<String> element = new ArrayList<String>();
		
		element.add("NewStatus");
		element.add("ChangesApplied");
		
		//element.add("NewSessionID");
		//element.add("99999999999999999999");
		//element.add(m_SessionID);
		
		GenieDebug.error("configurationfinished",""+element.size());		
		
		sendRequest2Router("DeviceConfig","ConfigurationFinished",element,false);
		
	}
	
	
	public Boolean getParentalControlStatus()
	{
		SoapRequestStatus = false;

		setSoapType(GenieGlobalDefines.ESoapRequestPCStatus);

		ArrayList<String> element = new ArrayList<String>();
		//sendRequest2Router("ParentalControl","GetEnableStatus",element,true);
		
		sendOpenDNSRequest2Router("ParentalControl","GetEnableStatus",element,true);
		
		return SoapRequestStatus;
	}
	
	public 	Boolean WrappedgetParentalControlStatus()
	{
		SoapRequestStatus = false;
		
		configurationstarted();
		
		getParentalControlStatus();
		
		configurationfinished();
		

		return SoapRequestStatus;
	}
	
	
	
	public Boolean setParentalControlStatus(String enable)
	{
		//configurationstarted();
		try{
			SoapRequestStatus = false;
			
			soapType = GenieGlobalDefines.ESoapRequestConfigPCStatus;
			
			ArrayList<String> element = new ArrayList<String>();
			element.add(GenieGlobalDefines.DICTIONARY_KEY_PC_ENABLE);
			element.add(enable);
			
			String res = sendOpenDNSRequest2Router("ParentalControl","EnableParentalControl",element,false);
			
			if(res.equals("") || res == null)
				return SoapRequestStatus;
			
			String responseCode = getXMLText(res,"<ResponseCode>","</ResponseCode>");
			
			if(responseCode.indexOf("000") != -1)
			{
				SoapRequestStatus = true;
			}
			else
			{
				SoapRequestStatus = false;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		//configurationfinished();
		
		return SoapRequestStatus;
	}
	
	public Boolean WrappedsetParentalControlStatus(String enable)
	{
		configurationstarted();
		
		setParentalControlStatus(enable);
		
		configurationfinished();
		
		return SoapRequestStatus;
	}
	
	public Boolean WrappedGetDNSMasqDeviceID()
	{
		GenieDebug.error("debug","getDNSMasqDeviceID ");
		
		configurationstarted();
		
		getDNSMasqDeviceID();  
		
		configurationfinished();
		
		return SoapRequestStatus;
	}
	
	public Boolean SetDNSMasqDeviceID()
	{
		
		WhetherSetStartConf();  //test
		
		SoapRequestStatus = false;
		

		
		setSoapType(GenieGlobalDefines.ESoapRequestSetDNSMasqDeviceID);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS);
		//element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS));
		element.add("default");
		
		element.add(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID));
		
		
		
		String res = sendOpenDNSRequest2Router("ParentalControl","SetDNSMasqDeviceID",element,false);
		String responseCode = getXMLText(res,"<ResponseCode>","</ResponseCode>");
		
		if(responseCode.indexOf("000") != -1)
		{
			SoapRequestStatus = true;
		}
		else
		{
			SoapRequestStatus = false;
		}

		configurationfinished();  //test
		
		return SoapRequestStatus;
	}
	
	public Boolean WrappedSetDNSMasqDeviceID()
	{
		configurationstarted();
		
		SetDNSMasqDeviceID(); 
		
		configurationfinished();
		
		
		return SoapRequestStatus;
	}
	
	
	
	public Boolean LookupHost(String Url)
	{
		InetAddress address = null;
		try {
			address = InetAddress.getByName(Url);
			if(null != address)
			{			
				GenieDebug.error("LookupHost",address.getHostName());
				GenieDebug.error("LookupHost","LookupHost rerurn true");
				return true;
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_NetError);
			e.printStackTrace();
		}
		
		GenieDebug.error("LookupHost","LookupHost rerurn false");
		return false;
	}
	

//	public Boolean LookupHost(String Url)
//	{
//		HttpGet request = new HttpGet(Url);
//        HttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams,2000);
//        HttpConnectionParams.setSoTimeout(httpParams,2000);
//        HttpClient httpClient = new DefaultHttpClient(httpParams);
//        
//        GenieDebug.error("LookupHost", "LookupHost --1--");
//        
//        try
//        {
//        	HttpResponse response = httpClient.execute(request);
//            
//        	GenieDebug.error("LookupHost", "LookupHost --2--");
//        	
//        	if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
//        	{
//        		pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
//            }else
//            {
//            	pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_Firmware);
//            }
//        	
//        }
//        catch (ClientProtocolException e)
//        {
//        	
//        	pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_NetError);
//            e.printStackTrace();
//        }
//        catch (IOException e)
//        {
//        	pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_NetError);
//            e.printStackTrace();
//        }
//        
//		return false;
//		
//	}
	
	public Boolean CheckRouterStatus()
	{
		m_GateWayIp = getGateWay();
		
		GenieDebug.error("CheckRouterStatus", "CheckRouterStatus --0--");
		String url1 = "using.netgear.opendns.com";
		//String url2 = "routerlogin.net";
		String url2 = m_GateWayIp;
		
		
		
		if(LookupHost(url1)&&LookupHost(url2))
		{
//			pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_intro);
			return true;
		}
		return  false;
	}	
	
	public boolean GetCurrentSettings()
	{
		m_GateWayIp = getGateWay();
		
		GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --0--");
		
		
		HttpGet request = new HttpGet("http://"+m_GateWayIp+"/currentsetting.htm");
		
        //HttpGet request = new HttpGet("http://routerlogin.net/currentsetting.htm");
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,2000);
        HttpConnectionParams.setSoTimeout(httpParams,2000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        
        String result = null;
        
        GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --1--");
        
        try
        {
        	HttpResponse response = httpClient.execute(request);
        	
            
        	GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --2--");
        	
        	if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
        	{
        		GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --3--");
        		
        		String recBuffer = EntityUtils.toString(response.getEntity());
        		
        		GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --4--");
        		GenieDebug.error("GetCurrentSettings", recBuffer);
        		GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --5--");
        		
        		String tag = "ParentalControlSupported=";

        		
        		int posS = recBuffer.indexOf(tag);
        		int posE = 0;  
        		
        		
        		if(posS > 0)
        		{
        			getParentalControlSupport(recBuffer);
        			posE = posS + tag.length() + 1;
        			
        			result = recBuffer.substring(posS + tag.length(), posE);
        			GenieDebug.error("GetCurrentSettings", "result = "+result);
        			if(result.equals("1"))
       				{        				
        				return true;
       				}
        		}
        		else
        		{
        			
        			GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --7--");
        		}
            }else
            {
            	
            }
        	
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        GenieDebug.error("GetCurrentSettings", "return false");
		return false;
	}
	
	public void setOpenDNSType(int type)
	{
		OpenDNSType = type;
	}
	
	
	public Boolean  OpenDNS_GetSetDeviceId()
	{
		OpenDNSRequestStatus = false ;
		
		GenieDebug.error("OpenDNS_GetSetDeviceId", "start");
		
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_GetSetDeviceId);
		
		
		
		if(!WrappedGetDNSMasqDeviceID())
		{
			return OpenDNSRequestStatus;
		}
			
		GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 1");
		
		if(!dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID).equals(""))
		{
			GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 2");
			
			if(!OpenDNS_GetLabel())
			{
				GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 3");
				
				String error = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
				GenieDebug.error("OpenDNS_GetSetDeviceId","error = "+error);
				if(error.equals("4001"))
				{
					/////////////////////////////////////////////
					OpenDNSRequestStatus = false;
					return OpenDNSRequestStatus;
				}if(error.equals("4003"))
				{
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, "");
				}
				
				OpenDNSRequestStatus = true;
			}
		}
		
		GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 4");
		if(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID).equals(""))
		{
			GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 5");
		  if(getRouterModelInfo()&&getGetWan())
		  {
			  GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 6");
			  if(OpenDNS_GetDevice())
			  {
				  GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 7");
				  if(WrappedSetDNSMasqDeviceID())
				  {
					  GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 8");
					  OpenDNSRequestStatus = true;
				  }
			  }else
			  {
				  GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 9");
				  if(OpenDNS_CreateDevice()&&WrappedSetDNSMasqDeviceID())
				  {
					  GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 10");
					  OpenDNSRequestStatus = true;
				  }
			  }
		  }
		}else
		{
			OpenDNSRequestStatus = true;
			GenieDebug.error("debug", "OpenDNS_GetSetDeviceId 11");
		}

		
		return OpenDNSRequestStatus;
	}
	
	public Boolean  OpenDNS_AccountRelay()
	{
		OpenDNSRequestStatus = false ;
		
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_AccountRelay);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("account_relay");		
		element.add("token");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));	
		
		
		sendRequest2OpenDNS("account_relay",element,true);
		
		return OpenDNSRequestStatus;
	}
	
	
	public Boolean  OpenDNS_GetLabel()
	{
		OpenDNSRequestStatus = false;
		
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_GetLabel);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("label_get");		
		element.add("token");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
		element.add("device_id");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID));
		
		
		sendRequest2OpenDNS("label_get",element,true);
		
		return OpenDNSRequestStatus;
	}
	
	public Boolean  OpenDNS_SetFilters()
	{
		
		OpenDNSRequestStatus = false;
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_SetFilters);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("filters_set");		
		element.add("token");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
		element.add("device_id");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID));
		element.add("bundle");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE));
		
		
		sendRequest2OpenDNS("filters_set",element,true);
		return OpenDNSRequestStatus;
	}
	
	public Boolean  OpenDNS_GetFilters()
	{
		OpenDNSRequestStatus = false;
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_GetFilters);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("filters_get");		
		element.add("token");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
		element.add("device_id");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID));
		
		
		
		sendRequest2OpenDNS("filters_get",element,true);
		
		return OpenDNSRequestStatus;
	}
	
	public Boolean  OpenDNS_CreateDevice()
	{
		String Devicekey = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME)+"-"+dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY,Devicekey);
		OpenDNSRequestStatus = false;
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_CreateDevice);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("device_create");		
		element.add("token");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
		element.add("device_key");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY));
		
		
		sendRequest2OpenDNS("device_create",element,true);
		
		return OpenDNSRequestStatus;
	}
	
	public Boolean  OpenDNS_GetDevice()
	{		
		
		String Devicekey = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME)+"-"+dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS);
		dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY,Devicekey);
		OpenDNSRequestStatus = false;
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_GetDevice);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("device_get");		
		element.add("token");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN));
		element.add("device_key");
		element.add(dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEKEY));
		
		
		sendRequest2OpenDNS("device_get",element,true);
		
		return OpenDNSRequestStatus;
	}
	
	public Boolean  OpenDNS_CheckNameAvailable()
	{
		
		OpenDNSRequestStatus = false;
		String createname = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEUSERNAME);
		
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("check_username");		
		element.add("username");
		element.add(createname);	
		
		sendRequest2OpenDNS("account_signin",element,true);
		
		return OpenDNSRequestStatus;

	}
	public Boolean  OpenDNS_CreateAccount()
	{
		OpenDNSRequestStatus = false;
		
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_CreateAccount);
		
		String name = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEUSERNAME);
	 	String password = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEPASSWORD);		
	 	String email = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CREATEEMAIL);
	 	
	 	ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("account_create");		
		element.add("username");
		element.add(name);
		element.add("password");
		element.add(password);
		element.add("email");
		element.add(email);
		
		sendRequest2OpenDNS("account_create",element,true);
		
		return OpenDNSRequestStatus;
		
	}
	public Boolean  OpenDNS_login()
	{
		GenieDebug.error("OpenDNS_login","--strart--");

		OpenDNSRequestStatus = false;
		
		String name = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_USERNAME);
		String password = dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_PASSWORD);
		
		setOpenDNSType(GenieGlobalDefines.EOpenDNSRequest_Login);
		
		ArrayList<String> element = new ArrayList<String>();
		element.add("api_key");
		element.add(API_KEY);
		element.add("method");
		element.add("account_signin");		
		element.add("username");
		element.add(name);		
		element.add("password");
		element.add(password);	
	
		
		sendRequest2OpenDNS("account_signin",element,true);
		
		return OpenDNSRequestStatus;
	}
	
	public String sendOpenDNSRequest2Router(String aServer, String aMothed, ArrayList<String> aElement,boolean needParser)
	{
		
		ClearErrorState();
		GenieDebug.error("sendRequest2Router","needParser  = "+needParser);
		
		String SOAP_TEMPLATE_P1 = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n<env:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n        xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <env:Body>\r\n    ";
		String SOAP_TEMPLATE_P2 = "\r\n  </env:Body> \r\n</env:Envelope>";
		
		GenieDebug.error("sendRequest2Router","--strart--");
		String soapAction = String.format("urn:NETGEAR-ROUTER:service:%s:1#%s",aServer,aMothed);

		StringBuffer soapMessage = new StringBuffer();
		
		soapMessage.append(SOAP_TEMPLATE_P1);
		
		soapMessage.append("<");		
		soapMessage.append(aMothed);
		soapMessage.append(">\r\n");
		
		
		for(int i = 0; i < aElement.size(); i += 2)
		{
			soapMessage.append("<");
			soapMessage.append(aElement.get(i));
			soapMessage.append(">");

			soapMessage.append(aElement.get(i + 1));

			soapMessage.append("</");
			soapMessage.append(aElement.get(i));
			soapMessage.append(">\r\n");	
			//soapMessage.append(">");

		}
		
		soapMessage.append("</");
		soapMessage.append(aMothed);
		soapMessage.append(">");		
		
		
		soapMessage.append(SOAP_TEMPLATE_P2);
		
		return httpUrlConnection(soapMessage.substring(0),soapAction,needParser);
	}
	
	public String sendRequest2OpenDNS(String aMothed, ArrayList<String> aElement,boolean needParser)
	{
		

		ClearErrorState();
		
		GenieDebug.error("sendRequest2OpenDNS","--strart--");

		StringBuffer soapMessage = new StringBuffer();
		
		
		for(int i = 0; i < aElement.size(); i += 2)
		{
			soapMessage.append(String.format("%s=%s&",aElement.get(i),aElement.get(i + 1)));
		}
		
		
		return httpUrlConnection_OpenDNS(soapMessage.substring(0),needParser);
	}
	

	private String httpUrlConnection_OpenDNS(String requestString,boolean needParser)
	{
		m_url=null;
		m_httpConn= null ;

		synchronized(mSyn2)
		{
		try{
			GenieDebug.error("httpUrlConnection_OpenDNS","--strart--");
			String pathUrl = "https://api.opendns.com/v1/";
//			
			m_url=new URL(pathUrl);
			m_httpConn=(HttpURLConnection)m_url.openConnection();
			
//			
			m_httpConn.setDoOutput(true);//
			m_httpConn.setDoInput(true);//
			m_httpConn.setUseCaches(false);//
			m_httpConn.setRequestMethod("POST");//

//			
//
			byte[] requestStringBytes = requestString.getBytes("UTF-8");
			m_httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
			m_httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			m_httpConn.setRequestProperty("Connection", "Keep-Alive");// ά�ֳ�����
			m_httpConn.setConnectTimeout(http_timeout);
			m_httpConn.setReadTimeout(http_timeout);
			
			GenieDebug.error("httpUrlConnection_OpenDNS","requestString = "+requestString);

			OutputStream outputStream = m_httpConn.getOutputStream();
			outputStream.write(requestStringBytes);
			outputStream.close();
			int responseCode = m_httpConn.getResponseCode();
			
			GenieDebug.error("httpUrlConnection_OpenDNS","responseCode = "+responseCode);
			
			if(HttpURLConnection.HTTP_OK == responseCode)
			{
				

				StringBuffer recString = new StringBuffer();
				String readLine;
				BufferedReader responseReader;
				responseReader = new BufferedReader(new InputStreamReader(m_httpConn.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null)
				{
					recString.append(readLine).append("\n");
				}
				
				responseReader.close();
				
				GenieDebug.error("httpUrlConnection_OpenDNS","responseReader+"+recString.toString());
				
				cancelhttpUrlConnection();
				if(needParser)
				{
					parsejson(recString.toString());

					return null;
				}
				OpenDNSRequestStatus = true;
				return recString.toString();
			}else
			{
				OpenDNSRequestStatus = false;
			}
			cancelhttpUrlConnection();
		}
		catch(Exception ex)
		{
			OpenDNSRequestStatus = false;
			pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
			ex.printStackTrace();
			
			cancelhttpUrlConnection();
		}
		
		return null;
		}
	}
	
	public void sendMessage2UI()
	{
		switch(pLpcview.getLpcFunctionType())
		{
		case GenieGlobalDefines.EFunctionParental_Firmware:
    		       		
    		break;
    	case GenieGlobalDefines.EFunctionParental_intro:
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_presignln:
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_signln:
    		if(OpenDNSRequestStatus)
			{
				pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success);
				pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_Success);
			}else
			{
				pLpcview.setToastMessageid(GenieGlobalDefines.EFunctionParental_failure);
				pLpcview.setSignin_again(1);
				pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
			}    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_Settings:
    		if(OpenDNSRequestStatus)
			{
				pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success);
				pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_Done);
			}else
			{
				pLpcview.setToastMessageid(GenieGlobalDefines.EFunctionParental_failure);
				pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_Settings);
			}    	
    		break;
    	case GenieGlobalDefines.EFunctionParental_Done:
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_CreateAccount:
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_NetError:
    		
    		break;
    	case GenieGlobalDefines.EFunctionParental_failure:
    		  		
    		break ;    		

		}
//		switch(OpenDNSType)
//		{
//		case GenieGlobalDefines.EOpenDNSRequest_Login:
//		case GenieGlobalDefines.EOpenDNSRequest_GetSetDeviceId:
//			if(OpenDNSRequestStatus)
//			{
//				pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success);
//				pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_Success);
//			}else
//			{
//				pLpcview.setToastMessageid(GenieGlobalDefines.EFunctionParental_failure);
//				pLpcview.setSignin_again(1);
//				pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
//			}
//			break ;
//		
//		}
	}
	
	public void parsejson(String json)
	{
		String response = null;
		JSONObject jsonobj_response =null;
		
		try {
			JSONObject jsonobj = new JSONObject(json);
			String status = jsonobj.getString("status");

			String error = null;
			String deviceid = null;
			String error_message = null;
			String bundle = null;
			
			GenieDebug.error("parsejson","status = "+status);
			if(status.equals("success"))
			{
				
				switch(OpenDNSType)
				{
				case GenieGlobalDefines.EOpenDNSRequest_Login:
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
					response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
					jsonobj_response = new JSONObject(response);
					String token = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN);
				
					GenieDebug.error("parsejson","token = "+token);
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN, token);
//					pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success);
//					pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_Success);
					break;
				case GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable:
					
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
					response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
					jsonobj_response = new JSONObject(response);
					String available = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_AVAILABLE);
				
					GenieDebug.error("parsejson","available = "+available);
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_AVAILABLE, available);					
					
					pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable);
					pLpcview.sendMessage2UI(GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable);
					break;
				case GenieGlobalDefines.EOpenDNSRequest_CreateAccount:					
					pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success);
					pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
					
					break ;
				case GenieGlobalDefines.EOpenDNSRequest_GetLabel:
					OpenDNSRequestStatus = true;
					break;
				case GenieGlobalDefines.EOpenDNSRequest_GetDevice:
					
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
					response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
					jsonobj_response = new JSONObject(response);
					deviceid = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEID);
					GenieDebug.error("parsejson","deviceid = "+deviceid);
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, deviceid);
					break;
				case GenieGlobalDefines.EOpenDNSRequest_CreateDevice:
					
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
					response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
					jsonobj_response = new JSONObject(response);
					deviceid = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEID);
				
					GenieDebug.error("parsejson","deviceid = "+deviceid);
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, deviceid);
					break;
				case GenieGlobalDefines.EOpenDNSRequest_GetFilters:
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
					GenieDebug.error("parsejson","333333 response get ");
					response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
					GenieDebug.error("parsejson","333333 response = "+response);
					jsonobj_response = new JSONObject(response);
					GenieDebug.error("parsejson","333333 bundle get ");
					bundle = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE);
				
					GenieDebug.error("parsejson","333333 bundle = "+bundle);
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE, bundle);
					break;
				case GenieGlobalDefines.EOpenDNSRequest_AccountRelay:
					
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
					response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
					jsonobj_response = new JSONObject(response);
					bundle = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RELAY_TOKEN);
				
					GenieDebug.error("parsejson","bundle = "+bundle);
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RELAY_TOKEN, bundle);
					
					break;
					
				}
				
				OpenDNSRequestStatus = true; 
			}else
			{
				switch(OpenDNSType)
				{
				case GenieGlobalDefines.EOpenDNSRequest_Login:
					error = jsonobj.getString("error");
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);	
					error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
					if(null != error_message)
					{
						dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
					}
					//pLpcview.setToastMessageid(GenieGlobalDefines.EFunctionParental_failure);
					//pLpcview.setSignin_again(1);
					//pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
					break;
				case GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable:
					break;
				case GenieGlobalDefines.EOpenDNSRequest_CreateAccount:
					pLpcview.setToastMessageid(GenieGlobalDefines.EFunctionParental_failure);
					pLpcview.setCreateAccount_again(1);
					error = jsonobj.getString("error");				
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);				
					error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
					if(null != error_message)
					{
						dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
					}	
					pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_CreateAccount);
					break ;	
				case GenieGlobalDefines.EOpenDNSRequest_GetLabel:
					error = jsonobj.getString("error");
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
					error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
					if(null != error_message)
					{
						dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
					}	
					break;	
				case GenieGlobalDefines.EOpenDNSRequest_GetDevice:
					break;	
				case GenieGlobalDefines.EOpenDNSRequest_GetFilters:
					error = jsonobj.getString("error");
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
					error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
					if(null != error_message)
					{
						dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
					}	
					break;	
				case GenieGlobalDefines.EOpenDNSRequest_AccountRelay:
					error = jsonobj.getString("error");
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
					error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
					if(null != error_message)
					{
						dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
					}	
					break;
					
				}
				
				OpenDNSRequestStatus = false;
			}
			
			
		} catch (JSONException e) {
			
			if(GenieGlobalDefines.EOpenDNSRequest_GetFilters == OpenDNSType)
			{
				
			
				GenieDebug.error("JSONException","response = "+response);
				try {
					jsonobj_response = new JSONObject(response);
					
					String categories = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CATEGORIES);
					if(null != categories)
					{
						OpenDNSRequestStatus = true; 					
						//32132

						dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE,GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CUSTOM);
						dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CUSTOM,categories);
						
						return ;
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					OpenDNSRequestStatus = false;
					
					// TODO Auto-generated catch block
					dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS,"JSONException");
					pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
					return ;
				}
				
			}
			
			OpenDNSRequestStatus = false;
			
			// TODO Auto-generated catch block
			dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS,"JSONException");
			pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
			e.printStackTrace();
		}
		
		
	}
	
}





