package com.dragonflow;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.dragonflow.genie.ui.R;
import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentSender.SendIntentException;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Xml;
import android.view.ViewDebug.ExportedProperty;
import android.widget.ProgressBar;

@SuppressLint("NewApi")
public class GenieRequest {
	
	private static int testint = 0;
	
	private Context m_context = null;
	private String m_GateWayIp = null;
	
	private ProgressDialog m_ProgressDialog = null;
	private boolean  m_IsShowProgress = false;
	private boolean  m_ShowProgress = true;
	private boolean  m_ProgressCancelFlag = true;
	
	private String  m_Progresstitle = null;
	private String  m_Progressmessage = null;
	
	private String  m_SmartUrl = null;
	
	//private static final String  m_SessionID = "10588AE69687E58D9A00";
	private static final String  m_SessionID = "A7D88AE69687E58D9A00";
	//private static final String  m_SessionID = "C128AE69687E58D9K77";
	
	public static final String API_KEY = "3443313B70772D2DD73B45D39F376199"; 
	
	private RequestTask m_RequestTask = null;
	
	private boolean m_CGDGflag = false;
	
	public static boolean m_SmartNetWork  = false;
	
	public static int m_RequestPort = 80;
	public static boolean m_First = false; ////是否未认证用户名和密码
	private boolean m_Cancleflag = false;
	
	public static GenieSmartNetWorkInfo m_SmartInfo = new GenieSmartNetWorkInfo();
	
	private  ArrayList<GenieRequestInfo> m_RequestInfo ;//= new ArrayList<GenieRequestInfo>();
	
	//int soapType;
	
	public interface  RequestFinish{
		void OnFinish(GenieRequest request);
	}
	
	public interface  OnProgressCancelListener{
		void OnProgressCancel();
	}
	
	private RequestFinish m_finishaction = null;
	
	private OnProgressCancelListener m_ProgressCancel = null;
	
	
	public void SetProgressCancelListener(OnProgressCancelListener action)
	{
		m_ProgressCancel = action;
	}
	
	
	public GenieRequest(Context context,ArrayList<GenieRequestInfo> requestinfo)
	{
		m_context = context;
		m_GateWayIp = getGateWay();
		m_IsShowProgress = false;
		m_Cancleflag = false;
		m_RequestInfo = requestinfo;
		
		m_SmartUrl = GetSmartNetworkUrl(context);
		
		WifiManager test = (WifiManager)m_context.getSystemService(Context.WIFI_SERVICE);
		
	}
	
	public void SetFinishAction(RequestFinish action)
	{
		m_finishaction = action;
	}
	
	public void SetProgressInfo(boolean show,boolean cancel)
	{
		m_ShowProgress = show;
		m_ProgressCancelFlag = cancel;
	}
	
	public void SetProgressText(String title,String message)
	{
		m_Progresstitle = title;
		m_Progressmessage = message;
	}
	
	public void Start()
	{
		if(null == m_context || m_RequestInfo == null)
		{
			return ;
		}
		
		if(null != m_context && m_ShowProgress)
		{
			if(m_ProgressCancelFlag)
			{
				if(m_Progressmessage == null)
				{
					m_Progressmessage = m_context.getResources().getString(R.string.pleasewait)+"...";
				}
				m_ProgressDialog =  ProgressDialog.show(m_context,m_Progresstitle, m_Progressmessage, true, true,new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						
						Stop();
						if(m_ProgressCancel != null)
						{
							m_ProgressCancel.OnProgressCancel();
							m_ProgressCancel = null;
						}
					}
				});
				
			}else
			{
				m_ProgressDialog =  ProgressDialog.show(m_context,null, m_context.getResources().getString(R.string.pleasewait)+"...", true, true);
			}
			
			m_IsShowProgress = true;
		}
		
		m_RequestTask = new RequestTask();
		m_RequestTask.execute(m_RequestInfo);
		GenieDebug.error("debug","GenieRequest.Start end");
	}
	
	public void Stop()
	{
		m_Cancleflag = true;
		if(m_IsShowProgress && m_ProgressDialog != null)
		{
			if(m_ProgressDialog.isShowing())
			{
				m_ProgressDialog.cancel();
			}
			m_ProgressDialog = null;
			m_IsShowProgress = false;
		}
		
		if(m_RequestTask != null)
		{
			GenieDebug.error("debug","GenieRequest Stop 0");
			m_RequestTask.cancelhttpUrlConnection();
			m_RequestTask.CancelRequest();
			m_RequestTask.CancleHttpGet();
			GenieDebug.error("debug","GenieRequest Stop 1");
			if(!m_RequestTask.isCancelled())
			{
				GenieDebug.error("debug","GenieRequest Stop 2");
				m_RequestTask.cancel(true);
			}
			m_RequestTask = null;
		}
		
	}
//	Intent intent = new Intent();
//	intent.setAction(Constant.fileReceiveStateUpdateAction);
//	sendBroadcast(intent);
	
	//private GenieSoap  Command = null;
	
	
	public void sendbroad(GenieRequestInfo value)
	{
		
		GenieDebug.error("debug","sendbroad 0");
		
		if(m_context == null || m_Cancleflag)
		{
			return ;
		}
		
		Intent intent = new Intent(GenieGlobalDefines.REQUEST_ACTION_RET_BROADCAST);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_LABLE, value.aRequestLable);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_ACTION_LABLE, value.aActionLable);
		
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE, value.aRequestType);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SERVER, value.aServer);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_METHOD, value.aMethod);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE, value.aResultType);
										    					
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSECODE, value.aResponseCode);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTPRESPONSECODE, value.aHttpResponseCode);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSE, value.aResponse);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTP_TYPE, value.aHttpType);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SOAP_TYPE, value.aSoapType);
		
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SMART_TYPE, value.aSmartType);
		
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_OPENDNS_TYPE, value.aOpenDNSType);
		intent.putExtra(GenieGlobalDefines.REQUEST_ACTION_RET_ERROR_CODE, value.errorcode);
		m_context.sendBroadcast(intent);
	}
	
	private class RequestTask extends AsyncTask< ArrayList<GenieRequestInfo>, Void, Void>
	{
		private  GenieRequestInfo m_RequestInfo = null;
		private HttpURLConnection m_httpConn= null ;
		private boolean m_Cancelfalg = false;
		
		private URL m_url = null;
		
		int recordResults = 0;
		int elementIndex;

		@Override
		protected Void doInBackground(ArrayList<GenieRequestInfo>... params) {
			// TODO Auto-generated method stub
			GenieDebug.error("debug","GenieRequest.RequestTask params.length="+params.length);
			
			m_Cancelfalg = false;
			
	         for (int i = 0; i < params.length; i++) {
	        	 ArrayList<GenieRequestInfo> RequestInfo = params[i];
	        	 for(GenieRequestInfo temp : RequestInfo)
	        	 {
	        		 GenieDebug.error("debug","GenieRequest.RequestTask m_Cancelfalg="+m_Cancelfalg);
	        		 if(m_Cancelfalg)
	        		 {
	        			 return null;
	        		 }
	        		 
	        		 if(m_RequestInfo != null)
	        		 {
	        			 m_RequestInfo = null;
	        		 }
	        		 m_RequestInfo = null;
	        		 m_RequestInfo = temp;
	        		 GenieDebug.error("debug","GenieRequest.RequestTask aServer="+temp.aServer);
	        		 GenieDebug.error("debug","GenieRequest.RequestTask aMethod="+temp.aMethod);
	        		 GenieDebug.error("debug","GenieRequest.RequestTask aRequestLable="+temp.aRequestLable);
	        		 sendRequest(temp);
	        	 }
	         }


			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			GenieDebug.error("debug","onPostExecute 0");
			if(m_finishaction != null)
			{
				GenieDebug.error("debug","onPostExecute 1");
				m_finishaction.OnFinish(GenieRequest.this);
				m_finishaction = null;
			}
			
			m_ProgressCancel = null;
			
			if(m_IsShowProgress && m_ProgressDialog != null)
			{
				GenieDebug.error("debug","onPostExecute 2");
				if(m_ProgressDialog.isShowing())
				{
					GenieDebug.error("debug","onPostExecute 3");
					m_ProgressDialog.cancel();
				}
				m_ProgressDialog = null;
				m_IsShowProgress = false;
			}
			
		}
		
		private static final int request_delay = 1000;
		public void sendRequest(GenieRequestInfo aRequestInfo)
		{
			if(aRequestInfo.aRequestType == GenieGlobalDefines.RequestActionType.Soap)
			{
				if(m_SmartNetWork)
	       		{
					sendSmartRequest(aRequestInfo);
	       		}else
	       		{
					String md = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
					if(md!= null &&(md.startsWith("CG") || md.startsWith("DG")))
					{
						m_CGDGflag = true;
						aRequestInfo.aNeedwrap = true;
					}else
					{
						m_CGDGflag = false;
					}
					
					m_CGDGflag = true;
	//				if(aRequestInfo.aSoapType != GenieGlobalDefines.ESoapRequestVerityUser 
	//						&&	aRequestInfo.aNeedwrap)
					if(aRequestInfo.aNeedwrap )
					{
						GenieRequestInfo start = new GenieRequestInfo();
						ArrayList<String> startelement = new ArrayList<String>();
						startelement.add("NewSessionID");
						startelement.add(""+m_SessionID);
						start.aNeedParser = false;
						start.aServer = "DeviceConfig";
						start.aMethod = "ConfigurationStarted";
						start.aSoapType = GenieGlobalDefines.ESoapRequestConfigStart;
						start.aTimeout = 15000;
						
						m_RequestInfo = start;
						sendRequest2Router(start.aServer,start.aMethod,startelement,start.aNeedParser,start.aTimeout,start.aSoapType);
						
						if(m_CGDGflag)
						{
							try {
								Thread.sleep(request_delay);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						m_RequestInfo = aRequestInfo;					
						if(m_RequestInfo.aSoapType == GenieGlobalDefines.ESoapRequestWLanWEPKey)
						{
							String wep = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES);
							if(wep != null && wep.equals("WEP"))
							{
								GenieRequestInfo WEPWLan = new GenieRequestInfo();
								ArrayList<String> WEPWLanelement = new ArrayList<String>();
								WEPWLanelement.add("NewWEPKey");
								WEPWLanelement.add("null");
								WEPWLanelement.add("NewWPAPassphrase");
								WEPWLanelement.add("null");
								WEPWLan.aNeedParser = false;
								WEPWLan.aServer = "WLANConfiguration";
								WEPWLan.aMethod = "GetWEPSecurityKeys";
								WEPWLan.aSoapType = GenieGlobalDefines.ESoapRequestWEPKey;
								WEPWLan.aTimeout = 20000;
								m_RequestInfo = WEPWLan;
								sendRequest2Router(WEPWLan.aServer,WEPWLan.aMethod,WEPWLanelement,WEPWLan.aNeedParser,WEPWLan.aTimeout,WEPWLan.aSoapType);
							}else
							{
								sendRequest2Router(aRequestInfo.aServer,aRequestInfo.aMethod,aRequestInfo.aElement,aRequestInfo.aNeedParser,aRequestInfo.aTimeout,aRequestInfo.aSoapType);
							}
							
						}else
						{
							sendRequest2Router(aRequestInfo.aServer,aRequestInfo.aMethod,aRequestInfo.aElement,aRequestInfo.aNeedParser,aRequestInfo.aTimeout,aRequestInfo.aSoapType);
						}
	
						if(m_CGDGflag)
						{
							try {
								Thread.sleep(request_delay);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						GenieRequestInfo finish = new GenieRequestInfo();
						ArrayList<String> finishelement = new ArrayList<String>();
						
						finishelement.add("NewStatus");
						finishelement.add("ChangesApplied");
						finish.aNeedParser = false;
						finish.aServer = "DeviceConfig";
						finish.aMethod = "ConfigurationFinished";
						finish.aSoapType = GenieGlobalDefines.ESoapRequestConfigFinish;
						finish.aTimeout = 15000;					
						m_RequestInfo = finish;
						sendRequest2Router(finish.aServer,finish.aMethod,finishelement,finish.aNeedParser,finish.aTimeout,finish.aSoapType);
						
						if(m_CGDGflag)
						{
							try {
								Thread.sleep(request_delay);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					
					}else
					{
	
							try {
								Thread.sleep(request_delay);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						sendRequest2Router(aRequestInfo.aServer,aRequestInfo.aMethod,aRequestInfo.aElement,aRequestInfo.aNeedParser,aRequestInfo.aTimeout,aRequestInfo.aSoapType);
					}
	       		}
			}else if(aRequestInfo.aRequestType == GenieGlobalDefines.RequestActionType.Http)
			{
				switch(aRequestInfo.aHttpType)
				{
				case GenieGlobalDefines.EHttpGetCurrentSetting:
					GetCurrentSettings(aRequestInfo.aTimeout);
					break;
				case GenieGlobalDefines.EHttpCheckLpcHost:
					ChekcLpcHost(aRequestInfo.aTimeout);
					break;	
				default:
					break;
				}
				
			}else if(aRequestInfo.aRequestType == GenieGlobalDefines.RequestActionType.OpenDNS)
			{
				m_RequestInfo = aRequestInfo;
				sendRequest2OpenDNS(aRequestInfo.aMethod,aRequestInfo.aElement,aRequestInfo.aTimeout,aRequestInfo.aOpenDNSType,aRequestInfo.aNeedParser);
			}else if(aRequestInfo.aRequestType == GenieGlobalDefines.RequestActionType.SmartNetWork)
			{
				GenieDebug.error("debug","aSmartType = "+aRequestInfo.aSmartType);
				GenieDebug.error("debug","aServer = "+aRequestInfo.aServer);
				
//				for(String temp : aRequestInfo.aElement)
//				{
//					GenieDebug.error("debug","temp = "+temp);
//				}
				
				sendRequestToSmartNetWork(aRequestInfo.aRequestLable,aRequestInfo.aSmartType,aRequestInfo.aServer,aRequestInfo.aElement);
			}
		}
		
		public void parsejson(String json,int OpenDNSType)
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
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						jsonobj_response = new JSONObject(response);
						String token = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN);
					
						GenieDebug.error("parsejson","token = "+token);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_TOKEN, token);
//						pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success);
//						pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_Success);
						break;
					case GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable:
						
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						jsonobj_response = new JSONObject(response);
						String available = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_AVAILABLE);
					
						GenieDebug.error("parsejson","available = "+available);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_AVAILABLE, available);					
						
						//pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable);
						//pLpcview.sendMessage2UI(GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable);
						break;
					case GenieGlobalDefines.EOpenDNSRequest_CreateAccount:					
						//pLpcview.setToastMessageid(GenieGlobalDefines.EOpenDNSRequest_Success);
						//pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
						
						break ;
					case GenieGlobalDefines.EOpenDNSRequest_GetLabel:
						//OpenDNSRequestStatus = true;
						break;
					case GenieGlobalDefines.EOpenDNSRequest_GetDevice:
						
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						jsonobj_response = new JSONObject(response);
						deviceid = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEID);
						GenieDebug.error("parsejson","deviceid = "+deviceid);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, deviceid);
						break;
					case GenieGlobalDefines.EOpenDNSRequest_CreateDevice:
						
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						jsonobj_response = new JSONObject(response);
						deviceid = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_DEVICEID);
					
						GenieDebug.error("parsejson","deviceid = "+deviceid);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, deviceid);
						break;
					case GenieGlobalDefines.EOpenDNSRequest_GetFilters:
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						GenieDebug.error("parsejson","333333 response get ");
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						GenieDebug.error("parsejson","333333 response = "+response);
						jsonobj_response = new JSONObject(response);
						GenieDebug.error("parsejson","333333 bundle get ");
						bundle = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE);
					
						GenieDebug.error("parsejson","333333 bundle = "+bundle);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE, bundle);
						break;
					case GenieGlobalDefines.EOpenDNSRequest_AccountRelay:
						
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						jsonobj_response = new JSONObject(response);
						bundle = jsonobj_response.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RELAY_TOKEN);
					
						GenieDebug.error("parsejson","bundle = "+bundle);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RELAY_TOKEN, bundle);
						
						break;
					case GenieGlobalDefines.ESoapDeviceChildUserName:
						
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						
//					
						GenieDebug.error("parsejson","response = "+response);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_ChildDeviceIDUserName, response);
						
						break;
					case GenieGlobalDefines.ESoapDeviceUserName:
						
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						
//					
						GenieDebug.error("parsejson","response = "+response);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceIDUserName, response);
						
						break;
					case GenieGlobalDefines.ESoapLoginBypassAccount:
						
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS, status);
						response = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_RESPONSE);
						
//					
						GenieDebug.error("parsejson","response = "+response);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_LoginBypassAccount, response);
						
						break;
						
					}
					m_RequestInfo.aResponse = json;
					m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
					//OpenDNSRequestStatus = true; 
				}else
				{
					switch(OpenDNSType)
					{
					case GenieGlobalDefines.EOpenDNSRequest_Login:
						error = jsonobj.getString("error");
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);	
						error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
						if(null != error_message)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
						}
						
						m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						//pLpcview.setToastMessageid(GenieGlobalDefines.EFunctionParental_failure);
						//pLpcview.setSignin_again(1);
						//pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_signln);
						break;
					case GenieGlobalDefines.EOpenDNSRequest_CheckNameAvailable:
						//m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						break;
					case GenieGlobalDefines.EOpenDNSRequest_CreateAccount:
						//pLpcview.setToastMessageid(GenieGlobalDefines.EFunctionParental_failure);
						//pLpcview.setCreateAccount_again(1);
						error = jsonobj.getString("error");				
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);				
						error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
						if(null != error_message)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
						}	
						m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						//pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_CreateAccount);
						break ;	
					case GenieGlobalDefines.EOpenDNSRequest_GetLabel:
						error = jsonobj.getString("error");
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
						error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
						if(null != error_message)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
						}	
						m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						break;	
					case GenieGlobalDefines.EOpenDNSRequest_GetDevice:
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						break;	
					case GenieGlobalDefines.EOpenDNSRequest_GetFilters:
						error = jsonobj.getString("error");
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
						error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
						if(null != error_message)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
						}	
						m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						break;	
					case GenieGlobalDefines.EOpenDNSRequest_AccountRelay:
						error = jsonobj.getString("error");
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
						error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
						if(null != error_message)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
						}	
						m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						break;
						
					case GenieGlobalDefines.ESoapDeviceChildUserName:
						error = jsonobj.getString("error");
						GenieDebug.error("parsejson","response = "+error);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
						error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
											
						GenieDebug.error("parsejson","response = "+error_message);
						if(null != error_message)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
						}	
						m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						break;
					case GenieGlobalDefines.ESoapDeviceUserName:
						error = jsonobj.getString("error");
						GenieDebug.error("parsejson","response = "+error);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
						error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
											
						GenieDebug.error("parsejson","response = "+error_message);
						if(null != error_message)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
						}	
						m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						break;
					case GenieGlobalDefines.ESoapLoginBypassAccount:
						error = jsonobj.getString("error");
						GenieDebug.error("parsejson","response = "+error);
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR, error);
						error_message = jsonobj.getString(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE);
											
						GenieDebug.error("parsejson","response = "+error_message);
						if(null != error_message)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE, error_message);
						}	
						m_RequestInfo.aResponse = error_message;
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
						
						break;
						
					}
					m_RequestInfo.aResponse = json;
					m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
					//OpenDNSRequestStatus = false;
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
							//OpenDNSRequestStatus = true; 					
							//32132

							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_BUNDLE,GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CUSTOM);
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_CUSTOM,categories);
							m_RequestInfo.aResponse = json;
							m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
							
							return ;
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						//OpenDNSRequestStatus = false;
						
						// TODO Auto-generated catch block
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS,"JSONException");
						//pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
						
						m_RequestInfo.aResponse = e1.getMessage();
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
						
						return ;
					}
					
				}
				
				//OpenDNSRequestStatus = false;
				
				// TODO Auto-generated catch block
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_STATUS,"JSONException");
				//pLpcview.sendMessage2UI(GenieGlobalDefines.EFunctionParental_failure);
				e.printStackTrace();
				m_RequestInfo.aResponse = e.getMessage();
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
				
			}
			
			
		}
		private void httpUrlConnection_OpenDNS(String requestString,int timeout,int OpenDNSType,boolean needParser)
		{
			cancelhttpUrlConnection();

			
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
				m_httpConn.setRequestProperty("Connection", "Keep-Alive");// 维锟街筹拷锟斤拷锟斤拷
				m_httpConn.setConnectTimeout(timeout);
				m_httpConn.setReadTimeout(timeout);
				
				GenieDebug.error("httpUrlConnection_OpenDNS","requestString = "+requestString);

				OutputStream outputStream = m_httpConn.getOutputStream();
				outputStream.write(requestStringBytes);
				outputStream.close();
				
				int responseCode = -2;
				
				try{
					responseCode = m_httpConn.getResponseCode();
				}catch (Exception e) {
					// TODO: handle exception
					
					GenieDebug.error("httpUrlConnection","requestString 33 Exception");
					
					m_RequestInfo.aHttpResponseCode = responseCode;
					m_RequestInfo.aResponseCode = e.getMessage();
					m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
					
					sendbroad(m_RequestInfo);
					e.printStackTrace();
					cancelhttpUrlConnection();
					return  ;
				}
				
				m_RequestInfo.aHttpResponseCode = responseCode;
				
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
					
					m_RequestInfo.aResponse = recString.toString();
					m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
					if(needParser)
					{
						parsejson(recString.toString(), OpenDNSType);
						sendbroad(m_RequestInfo);
						return ;
					}
					
					sendbroad(m_RequestInfo);
					//OpenDNSRequestStatus = true;
					//return recString.toString();
					return ;
				}else
				{
					m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
					//OpenDNSRequestStatus = false;
				}
				cancelhttpUrlConnection();
			}
			catch(Exception ex)
			{
				m_RequestInfo.aResponse = ex.getMessage();
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
				ex.printStackTrace();
				
			}finally{
				cancelhttpUrlConnection();
			}
			sendbroad(m_RequestInfo);
			return ;
		}
		
		public void sendRequest2OpenDNS(String aMothed, ArrayList<String> aElement,int timeout,int OpenDNSType,boolean needParser)
		{
			

			
			GenieDebug.error("sendRequest2OpenDNS","--strart--");

			StringBuffer soapMessage = new StringBuffer();
			
			GenieDebug.error("sendRequest2OpenDNS","aElement.size() = "+aElement.size());
			for(int i = 0; i < aElement.size(); i += 2)
			{
				GenieDebug.error("sendRequest2OpenDNS","aElement.get(i) = "+aElement.get(i));
				GenieDebug.error("sendRequest2OpenDNS","aElement.get(i+1) = "+aElement.get(i+1));
				soapMessage.append(String.format("%s=%s&",aElement.get(i),aElement.get(i + 1)));
			}
			
			
			httpUrlConnection_OpenDNS(soapMessage.substring(0),timeout,OpenDNSType,needParser);
		}
		
		
//		public void sendRequest2Router(String aServer, String aMothed, ArrayList<String> aElement,boolean needParser,int timeout,int soaptype)
//		{
//			
//			GenieDebug.error("sendRequest2Router","needParser  = "+needParser);
//			GenieDebug.error("sendRequest2Router","aServer  = "+aServer);
//			GenieDebug.error("sendRequest2Router","aMothed  = "+aMothed);
//			
//		
//			String SOAP_TEMPLATE_P1 = null;
//			String SOAP_TEMPLATE_P2 = null;
//			String headerstr = null;
//			String soapAction = String.format("\"urn:NETGEAR-ROUTER:service:%s:1#%s\"",aServer,aMothed);
//			String SOAP_TEMPLATE_P3 = " xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"";
//			StringBuffer soapMessage = null;
//			String SOAP_TEMPLATE_P4 = null;
//			String SOAP_TEMPLATE_P5 = null;
//			
//			
//			if(m_RequestInfo.aSoapType == GenieGlobalDefines.ESoapRequestVerityUser)
//			{
//				SOAP_TEMPLATE_P1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
//				SOAP_TEMPLATE_P2 = "</SOAP-ENV:Envelope>";
//				headerstr = "<SOAP-ENV:Header><SessionID"+SOAP_TEMPLATE_P3+">"+m_SessionID+"</SessionID></SOAP-ENV:Header>";
//				soapMessage = new StringBuffer();
//				soapMessage.append(SOAP_TEMPLATE_P1);
//				soapMessage.append(headerstr);
//				soapMessage.append("<SOAP-ENV:Body>");
//				soapMessage.append("<Authenticate>");
//				
//				
//				for(int i = 0; i < aElement.size(); i += 2)
//				{
//					soapMessage.append("<");				
//					soapMessage.append(aElement.get(i));
//					soapMessage.append(SOAP_TEMPLATE_P3);
//					soapMessage.append(">");
//
//					soapMessage.append(aElement.get(i + 1));
//
//					soapMessage.append("</");
//					soapMessage.append(aElement.get(i));
//					soapMessage.append(">");
//					
//				}
//				soapMessage.append("</Authenticate>");
//				soapMessage.append("</SOAP-ENV:Body>");
//				soapMessage.append(SOAP_TEMPLATE_P2);
//				
//			}else if(m_RequestInfo.aSoapType == GenieGlobalDefines.ESoapRequestConfigStart)
//			{
//				SOAP_TEMPLATE_P1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
//				SOAP_TEMPLATE_P2 = "</SOAP-ENV:Envelope>";
//				SOAP_TEMPLATE_P4 = String.format("<M1:%s xmlns:M1=\"urn:NETGEAR-ROUTER:service:%s:1\">",aMothed,aServer);
//				SOAP_TEMPLATE_P5 = String.format("</M1:%s>",aMothed);
//				soapMessage = new StringBuffer();
//				soapMessage.append(SOAP_TEMPLATE_P1);
//				soapMessage.append("<SOAP-ENV:Body>");
//				soapMessage.append(SOAP_TEMPLATE_P4);
//				soapMessage.append("<NewSessionID>"+m_SessionID+"</NewSessionID>");
//				soapMessage.append(SOAP_TEMPLATE_P5);
//				soapMessage.append("</SOAP-ENV:Body>");
//				soapMessage.append(SOAP_TEMPLATE_P2);
//							
//				
//			}else if(m_RequestInfo.aSoapType == GenieGlobalDefines.ESoapRequestConfigFinish)
//			{
//				SOAP_TEMPLATE_P1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
//				SOAP_TEMPLATE_P2 = "</SOAP-ENV:Envelope>";
//				SOAP_TEMPLATE_P4 = String.format("<M1:%s xmlns:M1=\"urn:NETGEAR-ROUTER:service:%s:1\">",aMothed,aServer);
//				SOAP_TEMPLATE_P5 = String.format("</M1:%s>",aMothed);
//				soapMessage = new StringBuffer();
//				soapMessage.append(SOAP_TEMPLATE_P1);
//				soapMessage.append("<SOAP-ENV:Body>");
//				soapMessage.append(SOAP_TEMPLATE_P4);
//				soapMessage.append("<NewSessionID>"+m_SessionID+"</NewSessionID>");
//				soapMessage.append("<NewStatus>ChangesApplied</NewStatus>");
//				soapMessage.append(SOAP_TEMPLATE_P5);
//				soapMessage.append("</SOAP-ENV:Body>");
//				soapMessage.append(SOAP_TEMPLATE_P2);
//			}else
//			{
//				SOAP_TEMPLATE_P1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
//				SOAP_TEMPLATE_P2 = "</SOAP-ENV:Envelope>";
//				headerstr = "<SOAP-ENV:Header><SessionID>"+m_SessionID+"</SessionID></SOAP-ENV:Header>";
//				SOAP_TEMPLATE_P4 = String.format("<M1:%s xmlns:M1=\"urn:NETGEAR-ROUTER:service:%s:1\">",aMothed,aServer);
//				SOAP_TEMPLATE_P5 = String.format("</M1:%s>",aMothed);
//				soapMessage = new StringBuffer();
//				soapMessage.append(SOAP_TEMPLATE_P1);
//				soapMessage.append(headerstr);
//				soapMessage.append("<SOAP-ENV:Body>");
//				soapMessage.append(SOAP_TEMPLATE_P4);
//				
//				for(int i = 0; i < aElement.size(); i += 2)
//				{
//					soapMessage.append("<");
//					soapMessage.append(aElement.get(i));
//					soapMessage.append(">");
//
//					soapMessage.append(aElement.get(i + 1));
//
//					soapMessage.append("</");
//					soapMessage.append(aElement.get(i));
//					soapMessage.append(">");
//					
//				}
//				soapMessage.append(SOAP_TEMPLATE_P5);
//				soapMessage.append("</SOAP-ENV:Body>");
//				soapMessage.append(SOAP_TEMPLATE_P2);
//			}
//			
//			httpUrlConnection(soapMessage.substring(0),soapAction,needParser,timeout,soaptype);
//			
//			return ;
//		}
		

		
		public void sendRequest2Router(String aServer, String aMothed, ArrayList<String> aElement,boolean needParser,int timeout,int soaptype)
		{
			
			GenieDebug.error("debug","sendRequest2Router needParser  = "+needParser);
			GenieDebug.error("debug","sendRequest2Router aServer  = "+aServer);
			GenieDebug.error("debug","sendRequest2Router aMothed  = "+aMothed);
			
			
			String tmpl =
				"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n"
				+"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
				+"<SOAP-ENV:Header>\r\n"
				+"<SessionID>%s</SessionID>\r\n"
				+"</SOAP-ENV:Header>\r\n"
				+"<SOAP-ENV:Body>\r\n"
				+"%s\r\n"
				+"</SOAP-ENV:Body>\r\n"
				+"</SOAP-ENV:Envelope>";

			String body1 =
				"<M1:%s xmlns:M1=\"%s\">\r\n"
				+"%s"
				+"</M1:%s>";

			String body2 =
				"<%s>\r\n"
				+"%s"
				+"</%s>";

			boolean actionNS = true;
			
			
			//if(m_RequestInfo.aSoapType == GenieGlobalDefines.ESoapRequestVerityUser)
			if(aServer.equals("ParentalControl"))
			{
				actionNS = false;
			}
			GenieDebug.error("debug","sendRequest2Router aElement.size()  = "+aElement.size());
			
			StringBuffer paramXml = new StringBuffer();;
			for(int i = 0; i < aElement.size(); i += 2)
			{
				
				String name = aElement.get(i);
				String value = aElement.get(i + 1);		
				

				GenieDebug.error("debug","sendRequest2Router aElement name  = "+name);
				GenieDebug.error("debug","sendRequest2Router aElement value  = "+value);
				
				if(!value.equals("null"))
				{
					paramXml.append(String.format("  <%s>%s</%s>\r\n", name,value,name));
				}
//				else
//				{
//					paramXml.append(String.format("  <%s>%s</%s>\r\n", name,"",name));
//				}
			}
			
			String ns = String.format("urn:NETGEAR-ROUTER:service:%s:1",aServer);
			
			String bodyXml;
			if (actionNS) {
				if(null != paramXml)
				{
					bodyXml = String.format(body1,aMothed,ns,paramXml.substring(0),aMothed);
				}else
				{
					bodyXml = String.format(body1,aMothed,ns,"",aMothed);
				}
			} else {
				if(null != paramXml)
				{
					bodyXml = String.format(body2,aMothed,paramXml.substring(0),aMothed);
				}else
				{
					bodyXml = String.format(body2,aMothed,"",aMothed);
				}
			}
			
			String soapMessage = String.format(tmpl,m_SessionID,bodyXml);
			
			String soapaction = String.format("\"urn:NETGEAR-ROUTER:service:%s:1#%s\"",aServer,aMothed);
			httpUrlConnection(soapMessage,soapaction,needParser,timeout,soaptype);
			
			return ;
		}
		
		public void CancelRequest()
		{
			m_Cancelfalg = true;
		}
		
		public void cancelhttpUrlConnection()
		{
			if(null != m_httpConn)
			{
				m_httpConn.disconnect();
				
			}
			m_httpConn = null;
			m_url = null;
//			m_Cancelfalg = true;
		}
		
		private void httpUrlConnection(String requestString,String soapAction,boolean needParser,int timeout,int soaptype)
		{
			
			//cancelhttpUrlConnection();
			
			//synchronized(this)
			//{
			try{
				
				
//				if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
//					GenieDebug.error("httpUrlConnection","--strart-- m_GateWayIp = "+m_GateWayIp);
//			        System.setProperty("http.keepAlive", "false");
//			    }
				
				//port = 5000;
				
				m_GateWayIp = getGateWay();
				
				GenieDebug.error("httpUrlConnection","--strart-- m_GateWayIp = "+m_GateWayIp);
				GenieDebug.error("httpUrlConnection","-httpUrlConnection-m_RequestPort = "+m_RequestPort);
			
				
				String pathUrl = "http://routerlogin.net:" + m_RequestPort + "/soap/server_sa/";
				
				//String pathUrl = "http://"+m_GateWayIp+":" + m_RequestPort + "/soap/server_sa/";
				
				//String pathUrl = "http://10.0.0.5:"+5000+"/soap/server_sa/";
				
				GenieDebug.error("httpUrlConnection","-httpUrlConnection-pathUrl = "+pathUrl);

				m_url=new URL(pathUrl);
				
				GenieDebug.error("httpUrlConnection","-httpUrlConnection-m_url.getHost(); = "+m_url.getHost());
								
				m_httpConn=(HttpURLConnection)m_url.openConnection();
			
				m_httpConn.setDoOutput(true);
				m_httpConn.setDoInput(true);
				m_httpConn.setUseCaches(false);
				m_httpConn.setRequestMethod("POST");
				
				GenieDebug.error("httpUrlConnection","-httpUrlConnection-soapAction = "+soapAction);
				
				byte[] requestStringBytes = requestString.getBytes("UTF-8");
								
				//m_httpConn.setRequestProperty("Connection", "Keep-Alive");// 维锟街筹拷锟斤拷锟斤拷
				m_httpConn.setRequestProperty("Accept", "text/xml");
				m_httpConn.setRequestProperty("SOAPAction", soapAction);
				m_httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
				m_httpConn.setRequestProperty("Content-Length", Integer.toString(requestStringBytes.length));
				//m_httpConn.setRequestProperty("Connection", "Keep-Alive");
				//m_httpConn.setRequestProperty("Content-Length", "" + requestStringBytes.length);
				
				
				m_httpConn.setConnectTimeout(timeout);
				m_httpConn.setReadTimeout(timeout);
				
				
				GenieDebug.error("httpUrlConnection","soapAction = "+soapAction);
				
				GenieDebug.error("httpUrlConnection","requestString = "+requestString);

				OutputStream outputStream = m_httpConn.getOutputStream();
				
				GenieDebug.error("httpUrlConnection","requestString 11 ");
				
				outputStream.write(requestStringBytes);
				
				GenieDebug.error("httpUrlConnection","requestString 22 ");
				outputStream.close();
				
				GenieDebug.error("httpUrlConnection","requestString 33 ");
				
				int responseCode = -2;
				
				try{
					responseCode = m_httpConn.getResponseCode();
				}catch (Exception e) {
					// TODO: handle exception
					
					GenieDebug.error("httpUrlConnection","requestString 33 Exception");
					
					m_RequestInfo.aHttpResponseCode = responseCode;
					m_RequestInfo.aResponseCode = e.getMessage();
					m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
					
					sendbroad(m_RequestInfo);
					e.printStackTrace();
					cancelhttpUrlConnection();
					return  ;
				}
				GenieDebug.error("httpUrlConnection","requestString 44 ");
				
				GenieDebug.error("httpUrlConnection","responseCode = "+responseCode);
				
				//httpresponseCode = responseCode;
				m_RequestInfo.aHttpResponseCode = responseCode;
				
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
					cancelhttpUrlConnection();
					
					GenieDebug.error("httpUrlConnection","responseReader+"+recString.toString());
					GenieDebug.error("httpUrlConnection","needParser+"+needParser);
					
					String Code = getXMLText(recString.toString(),"<ResponseCode>","</ResponseCode>");
					int soapresponsecode = -1;
					if(Code != null)
					{
						try{
							soapresponsecode = Integer.valueOf(Code);
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
					GenieDebug.error("httpUrlConnection","soapresponsecode+"+soapresponsecode);
					
					if(soapresponsecode == 0)
					{
						m_RequestInfo.aResponseCode = Code;
						
						if(needParser)
						{
							m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
							parseXml(new ByteArrayInputStream(recString.toString().getBytes()),soaptype);
							
							sendbroad(m_RequestInfo);
							cancelhttpUrlConnection();
							
							return ;
						}else
						{
							m_RequestInfo.aResponse = recString.toString();
							m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
							//SoapRequestStatus = true;
						}
						
					}else{
						if(m_RequestInfo.aSoapType == GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus)
						{
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE, GenieGlobalDefines.NULL);
							
						}
						if(Code != null )
						{
							m_RequestInfo.aResponseCode = Code;
						}
						
						m_RequestInfo.aResponse = recString.toString();
						m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
					}
					
					
					cancelhttpUrlConnection();
					
					sendbroad(m_RequestInfo);
					//return recString.toString();
					return ;
				}else
				{
					if(responseCode == 401)
					{
						m_RequestPort = (m_RequestPort == 80) ? 5000 : 80; 
					}
					
					m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
				}
				cancelhttpUrlConnection();
			}
			catch(ConnectException ex)
			{
				//if(soaptype == GenieGlobalDefines.checkAvailabelPort)
				//{
					m_RequestPort = (m_RequestPort == 80) ? 5000 : 80; 
				//}
				
				m_RequestInfo.aResponse = ex.getMessage();
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
				ex.printStackTrace();
				cancelhttpUrlConnection();
			}
			catch(UnknownHostException ex)
			{
				m_RequestInfo.aResponse = ex.getMessage();
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
				ex.printStackTrace();				
				cancelhttpUrlConnection();
			
			}
			catch(SocketException ex)
			{
				m_RequestInfo.aResponse = ex.getMessage();
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
				ex.printStackTrace();
				cancelhttpUrlConnection();
				
			}catch(Exception ex)
			{
				GenieDebug.error("debug","http Exception ex");
				m_RequestInfo.aResponse = ex.getMessage();
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
				ex.printStackTrace();
				cancelhttpUrlConnection();
			}finally{
				cancelhttpUrlConnection();
				
//				String RouterMode =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
//				if(RouterMode != null 
//						&& (RouterMode.indexOf("DGND3700") != -1))
//				{
//					
//					URL url = null;
//					HttpURLConnection httpConn = null;
//					try{
//						String pathUrl = "http://routerlogin.net:" + m_RequestPort + "/soap/server_sa/";
//					
//						url=new URL(pathUrl);
//										
//						httpConn=(HttpURLConnection)url.openConnection();
//					
//						httpConn.setRequestMethod("POST");
//						DataInputStream input = new DataInputStream(new BufferedInputStream(httpConn.getInputStream()));
//						byte[] date = new byte[1024*16];
//						input.read(date);
//						input.close();
//						
//					}catch(Exception e)
//					{
//						e.printStackTrace();
//					}finally{
//						if(null != httpConn)
//						{
//							httpConn.disconnect();
//						}
//					}
//				}
			}
			sendbroad(m_RequestInfo);
			return ;
			//}
		}
		
		
		
		public String getXMLText(String source,String tagS, String tagE)
		{
			if(null == source) return null;
			if(source.length() == 0) return null;
			
			int posS = source.indexOf(tagS);
			int posE = source.indexOf(tagE);

			return source.substring(posS + tagS.length(), posE);
		}
		
		public void parseXml(InputStream paseText,int soaptype) throws Exception 
		{   
			XmlPullParser parser = Xml.newPullParser();   
			parser.setInput(paseText, "utf-8");   

			int type = parser.getEventType();
			GenieDebug.error("parseXml", "parseXml type = "+type);
			while (type != XmlPullParser.END_DOCUMENT) 
			{
				GenieDebug.error("parseXml", "while type = "+type);
				GenieDebug.error("parseXml", "while parser.getName() = "+parser.getName());
				switch (type) 
				{
				case XmlPullParser.START_DOCUMENT:   
					break;   

				case XmlPullParser.START_TAG:
					recordResults = 0;
					isParseTextTag(parser.getName(),soaptype);
					break;

				case XmlPullParser.TEXT:
					tackleXmlText(parser.getText(),soaptype);
					break;
						
				case XmlPullParser.END_TAG:
					break;
				}

				type = parser.next();   
			}

			paseText.close();
			endParseXml(soaptype);
		}
		
		public void endParseXml(int soaptype)
		{
			GenieDebug.error("endParseXml", "endParseXml soapType = "+m_RequestInfo.aSoapType);
			switch (soaptype) 
			{
			case GenieGlobalDefines.ESoapRequestRouterInfo:
//				if(GenieGlobalDefines.GenieView_MainView == parentview)
//				{				
//			    	getWLanMACAddress();					
//				}
				break;
						
			case GenieGlobalDefines.ESoapRequestWLanInfo:
				
				 
//				try{	
//					if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES).equals("WEP"))
//					{
//						getWEPWLanPassword();
//					}else
//					{
//			    		getWLanPassword();
//					}
//				}catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					getWLanPassword();
//				}
				
				break;
						
			case GenieGlobalDefines.ESoapRequestWLanWEPKey:
//				if(GenieGlobalDefines.GenieView_ListView == parentview)
//				{
//					pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestWLanInfo);
//				}
				break;
			case GenieGlobalDefines.ESoapRequestWEPKey:
//				if(GenieGlobalDefines.GenieView_ListView == parentview)
//				{
//					pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestWLanInfo);
//				}
				break;	
						
			case GenieGlobalDefines.ESoapRequestGuestEnable:
				
		    	//getGuestAccessInfo();  //qicheng.ai change
		    
				break;
						
			case GenieGlobalDefines.ESoapRequestGuestInfo:
//				if(GenieGlobalDefines.GenieView_ListView == parentview)
//				{
//					pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestGuestInfo);
//				}
				break;
						
			case GenieGlobalDefines.ESoapRequestRouterMap:
//				if([routerMap count])
//				{
//					[self startThead:0];
//				}
//				if(GenieGlobalDefines.GenieView_Map == parentview)
//				{
//					pmap.sendMessage2UI(GenieGlobalDefines.ESoapRequestRouterMap);
//				}
				break;
						
			case GenieGlobalDefines.ESoapReqiestTrafficEnable:
				//getTrafficMeter();
				break;
						
			case GenieGlobalDefines.ESoapRequestTrafficMeter:
				//getTrafficMeterOptions();
//				if(GenieGlobalDefines.GenieView_ListView == parentview)
//				{
//					pListView.sendMessage2UI(GenieGlobalDefines.ESoapRequestTrafficMeter);
//				}
				break;
				
			case GenieGlobalDefines.ESoapRequestTrafficOptions:
				//SoapRequestStatus = true;
				break;
				
			default:
				break;
			}
		}
		
		public void isParseTextTag(String elementName,int soaptype)
		{
			GenieDebug.error("isParseTextTag","elementName = "+elementName);
			GenieDebug.error("isParseTextTag","soapType = "+m_RequestInfo.aSoapType);
			switch (soaptype) 
			{
				case GenieGlobalDefines.ESoapRequestIsGenie:
					break;
					
				case GenieGlobalDefines.ESoapRequestVerityUser:
					if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_RESPONCE))
					{
						recordResults = 1;
					}
					break;
				case GenieGlobalDefines.ESoapRequestConfigWan:
					if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS))
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
						GenieDebug.error("debug","99899 ESoapRequestTrafficMeter elementName="+elementName);
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
				case GenieGlobalDefines.ESoapDNSMasqDeviceID:
					if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_LPC_NewDeviceID)){
						elementIndex = GenieGlobalDefines.ESoapDNSMasqNewDeviceID;
						recordResults = 1;
					}
					break;
				case GenieGlobalDefines.ESoapMyDNSMasqDeviceID:
					if(elementName.equals(GenieGlobalDefines.DICTIONARY_KEY_LPC_MyNewDeviceID)){
						elementIndex = GenieGlobalDefines.ESoapMyDNSMasqNewDeviceID;
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
		
		
		
		public void tackleXmlText(String string,int soaptype)
		{
			if(0 == recordResults || string.equals("\n"))
			{
				recordResults = 0;
				return;
			}
			GenieDebug.error("tackleXmlText","soapType = "+m_RequestInfo.aSoapType);
			GenieDebug.error("tackleXmlText","elementIndex = "+elementIndex);
			GenieDebug.error("tackleXmlText","string = "+string);
			
			switch (soaptype) 
			{
			case GenieGlobalDefines.ESoapRequestRouterInfo:
				switch (elementIndex) 
				{
				case GenieGlobalDefines.ROUTER_INFO_ROUTER_NAME:
					//SoapRequestStatus = true;
					GenieDebug.error("tackleXmlText","123456789 string = "+string);
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME, string);
					break;
							
				case GenieGlobalDefines.ROUTER_INFO_FIRMWAREVERSIN:
					//SoapRequestStatus = true;
					GenieDebug.error("tackleXmlText","123456789 string = "+string);
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION, string);
					break;
				}
					
				break;
					
			case GenieGlobalDefines.ESoapRequestWLanInfo:
				GenieDebug.error("ESoapRequestWLanInfo","ESoapRequestWLanInfo elementIndex = "+elementIndex);
				GenieDebug.error("ESoapRequestWLanInfo","ESoapRequestWLanInfo elementIndex = "+string);
				switch (elementIndex) 
				{
				case GenieGlobalDefines.WLAN_INFO_ENABLE:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_ENABLE, string);
					break;
						
				case GenieGlobalDefines.WLAN_INFO_SSID:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID, string);
					break;
							
				case GenieGlobalDefines.WLAN_INFO_CHANNEL:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL, string);
					break;
							
				case GenieGlobalDefines.WLAN_INFO_REGION:
					if(string.equals("USA"))
					{
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION, "US");
					}else
					{
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION, string);
					}
					break;
							
				case GenieGlobalDefines.WLAN_INFO_WIRELESS_MODES:
					//string = "54Mbps";  //test
					GenieDebug.error("","WLAN_INFO_WIRELESS_MODES = "+string);				
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE, string);
					break;
							
				case GenieGlobalDefines.WLAN_INFO_WPA_MODES:
					GenieDebug.error("","WLAN_INFO_WPA_MODES = "+string);	
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY, string);
					break;
				case GenieGlobalDefines.WLAN_INFO_NET_STATUS:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_STATUS, string);
					break;
				case GenieGlobalDefines.WLAN_INFO_BASIC_MODES:
					GenieDebug.error("","wep777 WLAN_INFO_BASIC_MODES = "+string);
					//GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, string);
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES, string);					
					break;	
							
				case GenieGlobalDefines.WLAN_INFO_MAC_ADDRESS:
				{
					StringBuffer mac = new StringBuffer(string);
					
//					GenieDebug.error("WLAN_INFO_MAC_ADDRESS","mac = "+mac.toString());
//					for(int i = 10; i > 0; i -= 2)
//					{
//						mac.insert(i, ":");
//					}
					
					if(mac != null &&  mac.length() == 12)
					{
						for(int i = 10; i > 0; i -= 2)
						{
							mac.insert(i, ":");
						}
					}					
					
					GenieDebug.error("WLAN_INFO_MAC_ADDRESS","mac = "+mac.toString());		
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC, mac.toString());
				}
				break;
				}
				
				break;

			case GenieGlobalDefines.ESoapRequestWLanWEPKey:
				if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("None"))
				{
					GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, None)");
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, "None");
				}
				else
				{
					GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, string) string = "+string);
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, string);
				}
				break;
			case GenieGlobalDefines.ESoapRequestWEPKey:
				if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES).equals("None"))
				{
					GenieDebug.error("debug", "wep777 dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, None)");
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, "None");
				}
				else
				{
					GenieDebug.error("debug", "wep777 dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, string) string = "+string);
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY, string);
				}
				break;	
			case GenieGlobalDefines.ESoapRequestConfigWan:
				if(recordResults == 1)
				{
					GenieDebug.error("debug", "DICTIONARY_KEY_MAC_ADDRESS = "+string);
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS, string);
				}
				break;		
			case GenieGlobalDefines.ESoapRequestGuestEnable:
				if(recordResults == 1)
				{
					//string = "2";
					GenieDebug.error("DICTIONARY_KEY_GUEST_ABLE", "DICTIONARY_KEY_GUEST_ABLE = "+string);
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE, string);
				}
				break;
					
			case GenieGlobalDefines.ESoapRequestGuestInfo:
				GenieDebug.error("GUEST_INFO_SSID", " 99999 ESoapRequestGuestInfo elementIndex = "+elementIndex);
				switch (elementIndex) 
				{
				case GenieGlobalDefines.GUEST_INFO_SSID:
					GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_SSID = "+string);
					//SoapRequestStatus = true;
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID, string);
					break;
						
				case GenieGlobalDefines.GUEST_INFO_SECURITY_MODE:
					GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_SECURITY_MODE = "+string);
					//SoapRequestStatus = true;
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE, string);
					break;
						
				case GenieGlobalDefines.GUEST_INFO_KEY:
					
					GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_KEY = ["+string+"]"+string.length()); 
					
					if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY).equals("None"))
					{
						GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY, "None");
					}
					else
					{
						if(parserStringIsEmpty(string))
						{
							GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_KEY is empty");
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY, "");
						}else
						{
							GenieDebug.error("GUEST_INFO_SSID", " 99999 GUEST_INFO_KEY not empty");
							GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY, string);
						}
					}	

					break;
				}
					
				break;
			case GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus:
				GenieDebug.error("tackleXmlText","ESoapRequestBlockDeviceEnableStatus ="+string);
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE, string);
				break;				
			case GenieGlobalDefines.ESoapRequestRouterMap:
				
//				if(testint%2 == 0)
//				{
//					string = "24@1;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A7:59:3F:E6:E9;wireless@2;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@3;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@4;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@5;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@6;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@7;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A7:89:3F:E6:E9;wireless@8;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@9;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@10;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@11;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@12;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@13;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A5:69:3F:E6:E9;wireless@14;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@15;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@16;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@17;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@18;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@19;192.168.1.6;ANDROID_532894A0D7B65BF8;60:A7:69:3F:E6:E9;wireless@20;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@21;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@22;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@23;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@24;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless";
//				}else
//				{
//					string = "3@1;192.168.1.7;<unknown>;00:13:CE:CE:91:6F@2;192.168.1.11;ALIENWARE;F0:7B:CB:8F:8A:30@3;192.168.1.2;MACMINI-0B23C3;60:33:4B:07:29:EF";
//				}
//				testint++;
				//string = "3@1;192.168.1.7;<unknown>;00:13:CE:CE:91:6F@2;192.168.1.11;ALIENWARE;F0:7B:CB:8F:8A:30@3;192.168.1.2;MACMINI-0B23C3;60:33:4B:07:29:EF";
				//string = "8@1;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A7:69:3F:E6:E9;wireless@2;192.168.1.28;IPOD-TOUCH;F8:1E:DF:CA:7D:D0;wireless@3;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@4;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@5;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@6;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@7;192.168.1.7;<unknown>;60:33:4B:07:29:EF;wireless@8;192.168.1.7;<unknown>;60:33:4B:67:79:EF;wireless";
				//string = "24@1;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A7:59:3F:E6:E9;wireless@2;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@3;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@4;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@5;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@6;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@7;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A7:89:3F:E6:E9;wireless@8;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@9;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@10;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@11;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@12;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@13;192.168.1.6;ANDROID_532894A0D7B65BF8;64:A5:69:3F:E6:E9;wireless@14;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@15;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@16;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@17;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@18;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless@19;192.168.1.6;ANDROID_532894A0D7B65BF8;60:A7:69:3F:E6:E9;wireless@20;192.168.1.32;EASYROCK;00:21:5C:02:69:21;wireless@21;192.168.1.11;ANDROID_9774D56D682E549C;00:03:7F:48:0C:60;wireless@22;192.168.1.18;SHENGLANPC;00:13:CE:33:AB:F3;wireless@23;192.168.1.16;NEWMAC;60:33:4B:0D:D1:A1;wireless@24;192.168.1.7;MACMINI-0B23C3;60:33:4B:07:29:EF;wireless";
				//string = "3@1;192.168.1.2;BRAVIA07C094F7F644AA11;30:F9:ED:34:3B:5D@2;192.168.1.3;GENIE-PC;00:13:CE:CE:91:6F@3;192.168.1.4;ANDROID_8EB092BB95E89CF;C8:AA:21:80:A8:F1@";
				parserStringForNetworkMap(string);
				break;
					
			case GenieGlobalDefines.ESoapReqiestTrafficEnable:
				GenieDebug.error("tackleXmlText","ESoapReqiestTrafficEnable ="+string);
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE, string);
				break;
					
			case GenieGlobalDefines.ESoapRequestTrafficMeter:
				GenieDebug.error("tackleXmlText", string);
				StringBuffer temp = new StringBuffer();
				temp.append(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC));	
				temp.append(string);
				//temp.append("#");
				temp.append("\n");
				
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC, temp.toString());
				break;
					
			case GenieGlobalDefines.ESoapRequestTrafficOptions:
				GenieDebug.error("tackleXmlText", "ESoapRequestTrafficOptions string ="+string);
				GenieDebug.error("tackleXmlText", "ESoapRequestTrafficOptions elementIndex ="+elementIndex);
				switch (elementIndex) 
				{
				case GenieGlobalDefines.TRAFFIC_OPTION_CONTROL:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL, string);
					break;
						
				case GenieGlobalDefines.TRAFFIC_OPTION_LIMIT:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT, string);
					break;
					
				case GenieGlobalDefines.TRAFFIC_OPTION_HOUR:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR, string);
					break;
						
				case GenieGlobalDefines.TRAFFIC_OPTION_MINUTE:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE, string);
					break;
						
				case GenieGlobalDefines.TRAFFIC_OPTION_DAY:
					GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY, string);
					break;
				}
				
			case GenieGlobalDefines.ESoapRequestDeviceID:
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, string);
				GenieDebug.error("tackleXmlText","DICTIONARY_KEY_DEVICEID = "+string+"DICTIONARY_KEY_DEVICEID");
				//SoapRequestStatus = true;
				//GenieDebug.error("tackleXmlText","SoapRequestStatus = "+SoapRequestStatus);
				break;
				
			case GenieGlobalDefines.ESoapRequestPCStatus:
				//SoapRequestStatus = true;
				GenieDebug.error("tackleXmlText","DICTIONARY_KEY_PC_STATUS = "+string);
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS, string);
				break;	
				
			default:
				break;
			}
			
			recordResults = 0;
		}
		public boolean parserStringIsEmpty(String str)
		{
			boolean ret = true;
			GenieDebug.error("debug","parserStringIsEmpty 0");
			if(str == null || str.length() <= 0)
			{
				return true;
			}
			
			StringBuffer  string = new StringBuffer(str);
			
			for(int i = 0;i<string.length();i++)
			{
				if(string.charAt(i) != ' ' && 
						string.charAt(i) != '\n' &&
						string.charAt(i) != '\t')
				{
					return false;
				}
			}
			
			return  ret;
		}
		
		
		
		private HttpGet request = null;
		
		public void CancleHttpGet()
		{
			if(request != null)
			{
				if(!request.isAborted())
					request.abort();
				request = null;
			}
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
		
		private void ChekcLpcHost(int timeout)
		{
			
			System.setProperty("networkaddress.cache.ttl", "0");
			System.setProperty("networkaddress.cache.negative.ttl", "0");
			
			String url1 = "using.netgear.opendns.com";
			String url2 = "routerlogin.net";
			
			boolean ret = false;
			if(m_SmartNetWork)
			{
				ret = LookupHost(url1);
			}else
			{
				ret = LookupHost(url1)&&LookupHost(url2);
			}
			
			if(ret)
			{
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
            }else
            {
            	m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
            }
			
			
			sendbroad(m_RequestInfo);
		}
		
		public void GetCurrentSettings(int timeout)
		{
			String mFV = "";
			String mMN = "";
			String InternetStatus = "";
			String LpcSupport = "";
			
			
			
			m_GateWayIp = getGateWay();
			
			GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --0--");
			
			CancleHttpGet();
			
			request = new HttpGet("http://"+m_GateWayIp+"/currentsetting.htm");
			
	        //HttpGet request = new HttpGet("http://routerlogin.net/currentsetting.htm");
	        HttpParams httpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParams,timeout);
	        HttpConnectionParams.setSoTimeout(httpParams,timeout);
	        HttpClient httpClient = new DefaultHttpClient(httpParams);
	        
	        String result = null;
	        
	        GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --1--");
	        
	        try
	        {
	        	HttpResponse response = httpClient.execute(request);
	        	
	            
	        	GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --2--");
	        	
	        	m_RequestInfo.aHttpResponseCode = response.getStatusLine().getStatusCode();
	        	
	        	if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
	        	{
	        		GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --3--");
	        		
	        		String recBuffer = EntityUtils.toString(response.getEntity());
	        		
	        		m_RequestInfo.aResponse = recBuffer;
	        		
	        		GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --4--");
	        		GenieDebug.error("GetCurrentSettings", recBuffer);
	        		GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --5--");
	        		
	        		if(recBuffer.indexOf("Model=") > 0)
	        		{
	        			if(recBuffer.indexOf("SOAPVersion=V1.9") > 0 || recBuffer.indexOf("SOAPVersion=") < 0)
	        			{
	        				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE, GenieGlobalDefines.ROUTER_TYPE_OLD_NETGEAR);
	        			}
	        			else
	        			{
	        				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE, GenieGlobalDefines.ROUTER_TYPE_NEW_NETGEAR);
	        			}
	        			
	        			GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --6--");
	        			
	        			//pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestIsGenie); 
	       				
	        		}
	        		else
	        		{
	        			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE, GenieGlobalDefines.ROUTER_TYPE_NOT_NETGEAR);
	        			GenieDebug.error("GetCurrentSettings", "GetCurrentSettings --7--");
	        			
	        			//pMainView.sendMessage2UI(GenieGlobalDefines.ESoapRequestNotNETGEARRouter); 
	        			
	        		}
	        		
	        		
	        		
	        		if(recBuffer.startsWith("Firmware="))
	        		{
	        			GenieDebug.error("debug","GetCurrentSettings 7.1");
	        			int d = recBuffer.indexOf("=");
	        			GenieDebug.error("debug","GetCurrentSetting 7.2 d = "+d);
	        			int r = recBuffer.indexOf("\r");
	        			if(r == -1)
	        			{
	        				r = recBuffer.indexOf("\n");
	        			}
	        			GenieDebug.error("debug","GetCurrentSetting 7.3 r = "+r);
	        			mFV = recBuffer.substring(d + 1, r).replaceAll("\\s*", "");
	        			//mFV.replaceAll("\\s*", "");
	        			GenieDebug.error("debug","GetCurrentSetting 7.4 mFV = ["+mFV+"]");
	        			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION, mFV);
	        		}
	        		GenieDebug.error("debug","GetCurrentSetting 8");
	        		int mindex = recBuffer.indexOf("Model=");
	        		if(mindex > 0)
	        		{
	        			GenieDebug.error("debug","GetCurrentSetting 8.1");
	        			
	        			String s = recBuffer.substring(mindex);
	        			
	        			GenieDebug.error("debug","GetCurrentSetting 8.2");
	        			
	        			int d = s.indexOf("=");
	        			GenieDebug.error("debug","GetCurrentSetting 6.3");
	        			
	        			int r = s.indexOf("\r");
	        			if(r == -1)
	        			{
	        				r = s.indexOf("\n");
	        			}
	        			GenieDebug.error("debug","GetCurrentSetting 8.4");
	        			mMN = s.substring(d + 1, r).replaceAll("\\s*", "");
	        			//mMN.replaceAll("\\s*", "");
	        			GenieDebug.error("debug","GetCurrentSetting 8.5 mMN =["+mMN+"]");
	        			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME,mMN);
	        		}
	        		
	        		int InternetConnectionStatus = recBuffer.indexOf("InternetConnectionStatus=");
	        		if(InternetConnectionStatus > 0)
	        		{
	        			GenieDebug.error("debug","GetCurrentSetting 9.1");
	        			
	        			String s = recBuffer.substring(InternetConnectionStatus);
	        			
	        			GenieDebug.error("debug","GetCurrentSetting 9.2");
	        			
	        			int d = s.indexOf("=");
	        			GenieDebug.error("debug","GetCurrentSetting 9.3");
	        			
	        			int r = s.indexOf("\r");
	        			if(r == -1)
	        			{
	        				r = s.indexOf("\n");
	        			}
	        			GenieDebug.error("debug","GetCurrentSetting 9.4");
	        			InternetStatus = s.substring(d + 1, r).replaceAll("\\s*", "");
	        			//InternetStatus.replaceAll("\\s*", "");
	        			GenieDebug.error("debug","GetCurrentSetting 9.5 InternetStatus = ["+InternetStatus+"]");
	        			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS,InternetStatus);
	        			
	        			
	        		}
	        		
	        		
	        		int ParentalControlSupported = recBuffer.indexOf("ParentalControlSupported=");
	        		if(ParentalControlSupported > 0)
	        		{
	        			GenieDebug.error("debug","GetCurrentSetting 10.1");
	        			
	        			String s = recBuffer.substring(ParentalControlSupported);
	        			
	        			GenieDebug.error("debug","GetCurrentSetting 10.2");
	        			
	        			int d = s.indexOf("=");
	        			GenieDebug.error("debug","GetCurrentSetting 10.3");
	        			
	        			int r = s.indexOf("\r");
	        			if(r == -1)
	        			{
	        				r = s.indexOf("\n");
	        			}
	        			GenieDebug.error("debug","GetCurrentSetting 10.4");
	        			LpcSupport = s.substring(d + 1, r).replaceAll("\\s*", "");
	        			//LpcSupport.replaceAll("\\s*", "");
	        			GenieDebug.error("debug","GetCurrentSetting 10.5 LpcSupport = ["+LpcSupport+"]");
	        			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED,LpcSupport);
	        			
	        			
	        		}
	        		
	        		String tempstr = recBuffer.toLowerCase();
	        		GenieDebug.error("debug","GetCurrentSetting 11.0 recBuffer = "+recBuffer);
	        		GenieDebug.error("debug","GetCurrentSetting 11.0 tempstr = "+tempstr);
	        		int SmartNetworkSupported = tempstr.indexOf("SmartNetworkSupported=".toLowerCase());
	        		if(SmartNetworkSupported > 0)
	        		{
	        			GenieDebug.error("debug","GetCurrentSetting 11.1");
	        			
	        			String s = recBuffer.substring(SmartNetworkSupported);
	        			
	        			GenieDebug.error("debug","GetCurrentSetting 11.2");
	        			
	        			int d = s.indexOf("=");
	        			GenieDebug.error("debug","GetCurrentSetting 11.3 d ="+d);
	        			GenieDebug.error("debug","GetCurrentSetting 11.3 s.length() ="+s.length());
	        			
	        			int r = s.indexOf("\r");
	        			if(r == -1)
	        			{
	        				GenieDebug.error("debug","GetCurrentSetting 11.31 d ="+d);
	        				r = s.indexOf("\n");
	        				if(r == -1)
	        				{
	        					GenieDebug.error("debug","GetCurrentSetting 11.32 d ="+d);
	        					if((s.length() - d ) == 2)
	        					{
	        						GenieDebug.error("debug","GetCurrentSetting 11.33 d ="+d);
	        						r = s.length();
	        					}
	        				}
	        			}
	        			GenieDebug.error("debug","GetCurrentSetting 11.4 r="+r);
	        			String SmartSupport  = null ;
	        			try{
	        				SmartSupport = s.substring(d + 1, r).replaceAll("\\s*", "");
	        			}catch(Exception e){
	        				e.printStackTrace();
	        			}
	        			//LpcSupport.replaceAll("\\s*", "");
	        			
	        			GenieDebug.error("debug","GetCurrentSetting 11.5 SmartSupport = ["+SmartSupport+"]");
	        			//GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED,LpcSupport);
	        			int ssupport = 0;
	        			
	        			try{
	        				ssupport = Integer.valueOf(SmartSupport);
	        			}catch(Exception e){
	        				e.printStackTrace();
	        			}
	        			
	        			if(GenieGlobalDefines.GetSmartNetworkFlag(m_context) != 1)
	        			{
	        				GenieGlobalDefines.SaveSmartNetworkFlag(m_context, ssupport);
	        			}
	        			
	        		}
	        		
	        		
	        		
	        		m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Succes;
	            }else
	            {
	            	m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.failed;
	            }
	        	
	        }
	        catch (ClientProtocolException e)
	        {
	        	m_RequestInfo.aResponse = e.getMessage();
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
	            e.printStackTrace();
	        }
	        catch (IOException e)
	        {
	        	m_RequestInfo.aResponse = e.getMessage();
				m_RequestInfo.aResultType = GenieGlobalDefines.RequestResultType.Exception;
	            e.printStackTrace();
	        }
	        
	        GenieDebug.error("GetCurrentSettings", "return ");
	        CancleHttpGet();
	        sendbroad(m_RequestInfo);
			return ;
		}
		
		
		public void sendRequestToSmartNetWork(String lab,int smarttype,String aServer, ArrayList<String> aElement)
		{
			GenieRequestInfo RequestInfo = new GenieRequestInfo();
			RequestInfo.aRequestLable = lab;
			RequestInfo.aRequestType = RequestActionType.SmartNetWork;
			RequestInfo.aSmartType = smarttype;
			
			if(smarttype == GenieGlobalDefines.ESMARTNETWORKAUTHENTICATE)
			{
				 CookieManager cookieManager = new CookieManager();
				 CookieHandler.setDefault(cookieManager);
				 String path = "/fcp/authenticate";
				 
				
				 ArrayList<XmlParameter> parameterlist = new ArrayList<XmlParameter>();
					
					XmlParameter para1 = new XmlParameter();
					para1.Tag="authenticate";
					para1.attribute = new ArrayList<XmlAttribute>();
					XmlAttribute attr1_1 = new XmlAttribute("type","basic");
					para1.attribute.add(attr1_1);			
					XmlAttribute attr1_2 = new XmlAttribute("username",aElement.get(0));
					para1.attribute.add(attr1_2);
					XmlAttribute attr1_3 = new XmlAttribute("password",aElement.get(1));
					para1.attribute.add(attr1_3);
					
					parameterlist.add(para1);
					String data =  BuildXml(parameterlist);
					
				// String data = String.format("<authenticate type=\"basic\" username=\"%s\" password=\"%s\"/>", aElement.get(0),aElement.get(1));
				 
				 m_SmartInfo.username = aElement.get(0);
				 m_SmartInfo.password = aElement.get(1);
				 if(SmartNetWorkOpen(path,data,smarttype,true,RequestInfo))
				 {
					 path = "/fcp/init";
					 //data = "<init type=\"ui\" fcmb=\"true\"/>";
					 
					 ArrayList<XmlParameter> parameterlist2 = new ArrayList<XmlParameter>();
						
						XmlParameter para2 = new XmlParameter();
						para2.Tag="init";
						para2.attribute = new ArrayList<XmlAttribute>();
						XmlAttribute attr2_1 = new XmlAttribute("type","ui");
						para2.attribute.add(attr2_1);			
						XmlAttribute attr2_2 = new XmlAttribute("fcmb","true");
						para2.attribute.add(attr2_2);
						
						parameterlist2.add(para2);
						data =  BuildXml(parameterlist2);
						
					 if(SmartNetWorkOpen(path,data,GenieGlobalDefines.ESMARTNETWORK_INIT,true,RequestInfo))
					 {
						 ArrayList<XmlParameter> parameterlist3 = new ArrayList<XmlParameter>();
							
							XmlParameter para3 = new XmlParameter();
							para3.Tag="fcml";
							para3.attribute = new ArrayList<XmlAttribute>();
							XmlAttribute attr3_1 = new XmlAttribute("to",String.format("router@%s", m_SmartInfo.domain));
							para3.attribute.add(attr3_1);			
							XmlAttribute attr3_2 = new XmlAttribute("from",String.format("%s@%s",m_SmartInfo.uiid, m_SmartInfo.domain));
							para3.attribute.add(attr3_2);
							XmlAttribute attr3_3 = new XmlAttribute("_tracer",String.format("%s", ++m_SmartInfo.trace));
							para3.attribute.add(attr3_3);
							
							para3.child = new ArrayList<XmlParameter>();
							XmlParameter para23 = new XmlParameter();
							para23.Tag="get";
							
							para3.child.add(para23);
							parameterlist3.add(para3);
							
							String content =  BuildXml(parameterlist3);
						 
						 if(SmartNetWorkOpen_fcml(m_SmartInfo.uiid,content,GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST,RequestInfo))
						 {
							 RequestInfo.aSmartType = GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST;
							 RequestInfo.aResultType = RequestResultType.Succes;
						 }else
						 {
							 
						 }
					 }else
					 {
						 
					 }
				 }else
				 {
					 
				 }
			}else if(smarttype == GenieGlobalDefines.ESMARTNETWORK_StartRouterSession)
			{
				ArrayList<XmlParameter> parameterlist3 = new ArrayList<XmlParameter>();
				
				XmlParameter para3 = new XmlParameter();
				para3.Tag="fcml";
				para3.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr3_1 = new XmlAttribute("to",String.format("netrouter@%s", m_SmartInfo.workcpid));
				para3.attribute.add(attr3_1);			
				XmlAttribute attr3_2 = new XmlAttribute("from",String.format("%s@%s",m_SmartInfo.uiid, m_SmartInfo.domain));
				para3.attribute.add(attr3_2);
				XmlAttribute attr3_3 = new XmlAttribute("_tracer",String.format("%s", ++m_SmartInfo.trace));
				para3.attribute.add(attr3_3);
				
				para3.child = new ArrayList<XmlParameter>();
				XmlParameter para23 = new XmlParameter();
				para23.Tag="SessionManagement.startSession";
				para23.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr23_1 = new XmlAttribute("username",aElement.get(0));
				para23.attribute.add(attr23_1);			
				XmlAttribute attr23_2 = new XmlAttribute("password",aElement.get(1));
				para23.attribute.add(attr23_2);
				
				
				para3.child.add(para23);
				parameterlist3.add(para3);
				
				String content =  BuildXml(parameterlist3);
				
//				String content = String.format("<fcml to=\"netrouter@%s\" from=\"%s@%s\" _tracer=\"%s\"><SessionManagement.startSession username=\"%s\" password=\"%s\"/></fcml>",
//						m_SmartInfo.workcpid,m_SmartInfo.uiid,m_SmartInfo.domain,++m_SmartInfo.trace, aElement.get(0),aElement.get(1));
//				
				 m_SmartInfo.GetWorkSmartRouterInfo(m_SmartInfo.workcpid).username = aElement.get(0);
				 m_SmartInfo.GetWorkSmartRouterInfo(m_SmartInfo.workcpid).password = aElement.get(1);
				 
				if(SmartNetWorkOpen_fcml(m_SmartInfo.uiid,content,smarttype,RequestInfo))
				 {
					 RequestInfo.aResultType = RequestResultType.Succes;
				 }else
				 {
					 
				 }
			}else if(smarttype == GenieGlobalDefines.ESMARTNETWORK_EndRouterSession)
			{
				ArrayList<XmlParameter> parameterlist3 = new ArrayList<XmlParameter>();
				
				XmlParameter para3 = new XmlParameter();
				para3.Tag="fcml";
				para3.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr3_1 = new XmlAttribute("to",String.format("netrouter@%s", m_SmartInfo.workcpid));
				para3.attribute.add(attr3_1);			
				XmlAttribute attr3_2 = new XmlAttribute("from",String.format("%s@%s",m_SmartInfo.uiid, m_SmartInfo.domain));
				para3.attribute.add(attr3_2);
				XmlAttribute attr3_3 = new XmlAttribute("_tracer",String.format("%s", ++m_SmartInfo.trace));
				para3.attribute.add(attr3_3);
				
				para3.child = new ArrayList<XmlParameter>();
				XmlParameter para23 = new XmlParameter();
				para23.Tag="SessionManagement.endSession";
				para23.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr23_1 = new XmlAttribute("sessionId",m_SmartInfo.sessionid);
				para23.attribute.add(attr23_1);		

				
				para3.child.add(para23);
				parameterlist3.add(para3);
				
				String content =  BuildXml(parameterlist3);
				
			 
				if(SmartNetWorkOpen_fcml(m_SmartInfo.uiid,content,smarttype,RequestInfo))
				 {
					 RequestInfo.aResultType = RequestResultType.Succes;
				 }else
				 {
					 
				 }
			}else if(smarttype == GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST)
			{
				ArrayList<XmlParameter> parameterlist3 = new ArrayList<XmlParameter>();
				
				XmlParameter para3 = new XmlParameter();
				para3.Tag="fcml";
				para3.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr3_1 = new XmlAttribute("to",String.format("router@%s", m_SmartInfo.domain));
				para3.attribute.add(attr3_1);			
				XmlAttribute attr3_2 = new XmlAttribute("from",String.format("%s@%s",m_SmartInfo.uiid, m_SmartInfo.domain));
				para3.attribute.add(attr3_2);
				XmlAttribute attr3_3 = new XmlAttribute("_tracer",String.format("%s", ++m_SmartInfo.trace));
				para3.attribute.add(attr3_3);
				
				para3.child = new ArrayList<XmlParameter>();
				XmlParameter para23 = new XmlParameter();
				para23.Tag="get";
				
				para3.child.add(para23);
				parameterlist3.add(para3);
				
				String content =  BuildXml(parameterlist3);
			 
				 if(SmartNetWorkOpen_fcml(m_SmartInfo.uiid,content,GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST,RequestInfo))
				 {
					 RequestInfo.aSmartType = GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST;
					 RequestInfo.aResultType = RequestResultType.Succes;
				 }else
				 {
					 
				 }
			}
			
			sendbroad(RequestInfo);
		}
		
		
		private boolean SmartNetWorkOpen(String path,String data,int smarttype,boolean receive,GenieRequestInfo RequestInfo)
		{
			//URL url = null;
			//HttpURLConnection conn = null; 
			
			try {
				String site = String.format("%s%s",m_SmartUrl,path);
				
				GenieDebug.error("debug","SmartNetWorkOpen site="+site);
				GenieDebug.error("debug","SmartNetWorkOpen data="+data);
				GenieDebug.error("debug","SmartNetWorkOpen m_SmartInfo.trace = "+m_SmartInfo.trace);
			
				m_url = new URL(site);
				//m_httpConn = (HttpURLConnection)m_url.openConnection();
				
				 if (m_url.getProtocol().toLowerCase().equals("https")) {
                     trustAllHosts();
                     HttpsURLConnection https = (HttpsURLConnection) m_url
                                    .openConnection();
                     https.setHostnameVerifier(DO_NOT_VERIFY);
                     m_httpConn = https;
				 } else {
					 m_httpConn = (HttpURLConnection) m_url.openConnection();
				 }
				
//				if (m_url.getProtocol().toLowerCase().equals("https")) {  
//	                trustAllHosts();  
//	                m_httpConn = (HttpsURLConnection) m_url.openConnection();  
//	                ((HttpsURLConnection) m_httpConn).setHostnameVerifier(DO_NOT_VERIFY);// 涓嶈繘琛屼富鏈哄悕纭  
//	  
//	            } else {  
//	            	m_httpConn = (HttpURLConnection) m_url.openConnection();  
//	            }  
				
				m_httpConn.setRequestMethod("POST");
				m_httpConn.setRequestProperty("Content-Type", "text/html");
				m_httpConn.setDoOutput(true);
				m_httpConn.setConnectTimeout(120000);
				m_httpConn.setReadTimeout(120000);
				PrintWriter outputStream = new PrintWriter(m_httpConn.getOutputStream());
				outputStream.write(data);
				outputStream.flush();
				outputStream.close();
				
				int responsecode =  m_httpConn.getResponseCode();
				GenieDebug.error("debug","SmartNetWorkOpen responsecode="+responsecode);
		
				InputStreamReader reader = new InputStreamReader((InputStream)m_httpConn.getContent());
				StringBuffer sb = new StringBuffer();
				BufferedReader buff = new BufferedReader(reader);
				String line = buff.readLine();
				while (line != null) {
					sb.append(line);
					line = buff.readLine();
				}
				reader.close();
				
				GenieDebug.error("debug","SmartNetWorkOpen sb.length()="+sb.length());
				GenieDebug.error("debug","SmartNetWorkOpen sb="+sb.toString());
				if(sb.length() <= 0)
				{
					if(receive)
					{
						RequestInfo.aResultType = RequestResultType.failed;
						return false;
					}
					else
					{
						RequestInfo.aResultType = RequestResultType.Succes;
						return true;
					}
				}else
				{
					if(parsefcml(sb.toString(),smarttype,receive,RequestInfo))
					{
						RequestInfo.aResultType = RequestResultType.Succes;
						return true;
					}else
					{
						//RequestInfo.aResultType = RequestResultType.failed;
						return false;
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				RequestInfo.aResultType = RequestResultType.Exception;
				return false;
			}finally{
				cancelhttpUrlConnection();
			}
			//return true;
		}
		
		private boolean SmartNetWorkStartRouterSession()
		{
			GenieRequestInfo RequestInfo = new GenieRequestInfo();
			//RequestInfo.aRequestLable = lab;
			RequestInfo.aRequestType = RequestActionType.SmartNetWork;
			RequestInfo.aSmartType = GenieGlobalDefines.ESMARTNETWORK_StartRouterSession;
			
			
			 CookieManager cookieManager = new CookieManager();
			 CookieHandler.setDefault(cookieManager);
			 String path = "/fcp/authenticate";
			 
	
			 String username = m_SmartInfo.GetWorkSmartRouterInfo(m_SmartInfo.workcpid).username;
			 String password = m_SmartInfo.GetWorkSmartRouterInfo(m_SmartInfo.workcpid).password;
			 
			 
			 ArrayList<XmlParameter> parameterlist3 = new ArrayList<XmlParameter>();
				
			XmlParameter para3 = new XmlParameter();
			para3.Tag="fcml";
			para3.attribute = new ArrayList<XmlAttribute>();
			XmlAttribute attr3_1 = new XmlAttribute("to",String.format("netrouter@%s", m_SmartInfo.workcpid));
			para3.attribute.add(attr3_1);			
			XmlAttribute attr3_2 = new XmlAttribute("from",String.format("%s@%s",m_SmartInfo.uiid, m_SmartInfo.domain));
			para3.attribute.add(attr3_2);
			XmlAttribute attr3_3 = new XmlAttribute("_tracer",String.format("%s", ++m_SmartInfo.trace));
			para3.attribute.add(attr3_3);
			
			para3.child = new ArrayList<XmlParameter>();
			XmlParameter para23 = new XmlParameter();
			para23.Tag="SessionManagement.startSession";
			para23.attribute = new ArrayList<XmlAttribute>();
			XmlAttribute attr23_1 = new XmlAttribute("username",username);
			para23.attribute.add(attr23_1);			
			XmlAttribute attr23_2 = new XmlAttribute("password",password);
			para23.attribute.add(attr23_2);
			
			
			para3.child.add(para23);
			parameterlist3.add(para3);
			
			String content =  BuildXml(parameterlist3);
				
			
			if(SmartNetWorkOpen_fcml(m_SmartInfo.uiid,content,GenieGlobalDefines.ESMARTNETWORK_StartRouterSession,RequestInfo))
			 {
				 RequestInfo.aResultType = RequestResultType.Succes;
				 return  true;
			 }else
			 {
				 return false;
			 }
		}
		
		private boolean SmartNetWorkBackgroundAuthorize()
		{
			GenieRequestInfo RequestInfo = new GenieRequestInfo();
			//RequestInfo.aRequestLable = lab;
			RequestInfo.aRequestType = RequestActionType.SmartNetWork;
			RequestInfo.aSmartType = GenieGlobalDefines.ESMARTNETWORKAUTHENTICATE;
			
			
//			 CookieManager cookieManager = new CookieManager();
//			 CookieHandler.setDefault(cookieManager);
			 String path = "/fcp/authenticate";
			 
			 ArrayList<XmlParameter> parameterlist = new ArrayList<XmlParameter>();
				
				XmlParameter para1 = new XmlParameter();
				para1.Tag="authenticate";
				para1.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr1_1 = new XmlAttribute("type","basic");
				para1.attribute.add(attr1_1);			
				XmlAttribute attr1_2 = new XmlAttribute("username",m_SmartInfo.username);
				para1.attribute.add(attr1_2);
				XmlAttribute attr1_3 = new XmlAttribute("password",m_SmartInfo.password);
				para1.attribute.add(attr1_3);
				
				parameterlist.add(para1);
				String data =  BuildXml(parameterlist);
			 //String data = String.format("<authenticate type=\"basic\" username=\"%s\" password=\"%s\"/>",  m_SmartInfo.username,m_SmartInfo.password);
			 
			 if(SmartNetWorkOpen(path,data,RequestInfo.aSmartType,true,RequestInfo))
			 {
				 path = "/fcp/init";
				 //data = "<init type=\"ui\" fcmb=\"true\"/>";
				 ArrayList<XmlParameter> parameterlist2 = new ArrayList<XmlParameter>();
					
				XmlParameter para2 = new XmlParameter();
				para2.Tag="init";
				para2.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr2_1 = new XmlAttribute("type","ui");
				para2.attribute.add(attr2_1);			
				XmlAttribute attr2_2 = new XmlAttribute("fcmb","true");
				para2.attribute.add(attr2_2);
				
				parameterlist2.add(para2);
				data =  BuildXml(parameterlist2);
					
				 RequestInfo.aSmartType = GenieGlobalDefines.ESMARTNETWORK_INIT;
				 if(SmartNetWorkOpen(path,data,GenieGlobalDefines.ESMARTNETWORK_INIT,true,RequestInfo))
				 {
					 //GenieGlobalDefines.ESMARTNETWORK_StartRouterSession
					 
					 String username = m_SmartInfo.GetWorkSmartRouterInfo(m_SmartInfo.workcpid).username;
					 String password = m_SmartInfo.GetWorkSmartRouterInfo(m_SmartInfo.workcpid).password;
					 
					 
					 ArrayList<XmlParameter> parameterlist3 = new ArrayList<XmlParameter>();
						
						XmlParameter para3 = new XmlParameter();
						para3.Tag="fcml";
						para3.attribute = new ArrayList<XmlAttribute>();
						XmlAttribute attr3_1 = new XmlAttribute("to",String.format("netrouter@%s", m_SmartInfo.workcpid));
						para3.attribute.add(attr3_1);			
						XmlAttribute attr3_2 = new XmlAttribute("from",String.format("%s@%s",m_SmartInfo.uiid, m_SmartInfo.domain));
						para3.attribute.add(attr3_2);
						XmlAttribute attr3_3 = new XmlAttribute("_tracer",String.format("%s", ++m_SmartInfo.trace));
						para3.attribute.add(attr3_3);
						
						para3.child = new ArrayList<XmlParameter>();
						XmlParameter para23 = new XmlParameter();
						para23.Tag="SessionManagement.startSession";
						para23.attribute = new ArrayList<XmlAttribute>();
						XmlAttribute attr23_1 = new XmlAttribute("username",username);
						para23.attribute.add(attr23_1);			
						XmlAttribute attr23_2 = new XmlAttribute("password",password);
						para23.attribute.add(attr23_2);
						
						
						para3.child.add(para23);
						parameterlist3.add(para3);
						
						String content =  BuildXml(parameterlist3);
						
					//String content = String.format("<fcml to=\"netrouter@%s\" from=\"%s@%s\" _tracer=\"%s\"><SessionManagement.startSession username=\"%s\" password=\"%s\"/></fcml>",
					//		m_SmartInfo.workcpid,m_SmartInfo.uiid,m_SmartInfo.domain,++m_SmartInfo.trace, username,password);
					
					RequestInfo.aSmartType = GenieGlobalDefines.ESMARTNETWORK_StartRouterSession;
					
					if(SmartNetWorkOpen_fcml(m_SmartInfo.uiid,content,GenieGlobalDefines.ESMARTNETWORK_StartRouterSession,RequestInfo))
					 {
						 RequestInfo.aResultType = RequestResultType.Succes;
						 return  true;
					 }else
					 {
						 return false;
					 }
				 }else
				 {
					 return false;
				 }
			 }else
			 {
				 return false;
			 }
		}
		
		
		private boolean SmartNetWorkBackgroundAuthorize2()
		{
			GenieRequestInfo RequestInfo = new GenieRequestInfo();
			//RequestInfo.aRequestLable = lab;
			RequestInfo.aRequestType = RequestActionType.SmartNetWork;
			RequestInfo.aSmartType = GenieGlobalDefines.ESMARTNETWORKAUTHENTICATE;
			
			
//			 CookieManager cookieManager = new CookieManager();
//			 CookieHandler.setDefault(cookieManager);
			 String path = "/fcp/authenticate";
			 
			 ArrayList<XmlParameter> parameterlist = new ArrayList<XmlParameter>();
				
				XmlParameter para1 = new XmlParameter();
				para1.Tag="authenticate";
				para1.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr1_1 = new XmlAttribute("type","basic");
				para1.attribute.add(attr1_1);			
				XmlAttribute attr1_2 = new XmlAttribute("username",m_SmartInfo.username);
				para1.attribute.add(attr1_2);
				XmlAttribute attr1_3 = new XmlAttribute("password",m_SmartInfo.password);
				para1.attribute.add(attr1_3);
				
				parameterlist.add(para1);
				String data =  BuildXml(parameterlist);
			 //String data = String.format("<authenticate type=\"basic\" username=\"%s\" password=\"%s\"/>",  m_SmartInfo.username,m_SmartInfo.password);
			 
			 if(SmartNetWorkOpen(path,data,RequestInfo.aSmartType,true,RequestInfo))
			 {
				 path = "/fcp/init";
				 //data = "<init type=\"ui\" fcmb=\"true\"/>";
				 ArrayList<XmlParameter> parameterlist2 = new ArrayList<XmlParameter>();
					
				XmlParameter para2 = new XmlParameter();
				para2.Tag="init";
				para2.attribute = new ArrayList<XmlAttribute>();
				XmlAttribute attr2_1 = new XmlAttribute("type","ui");
				para2.attribute.add(attr2_1);			
				XmlAttribute attr2_2 = new XmlAttribute("fcmb","true");
				para2.attribute.add(attr2_2);
				
				parameterlist2.add(para2);
				data =  BuildXml(parameterlist2);
					
				 RequestInfo.aSmartType = GenieGlobalDefines.ESMARTNETWORK_INIT;
				 if(SmartNetWorkOpen(path,data,GenieGlobalDefines.ESMARTNETWORK_INIT,true,RequestInfo))
				 {
					RequestInfo.aResultType = RequestResultType.Succes;
					return  true;
					
				 }else
				 {
					 return false;
				 }
			 }else
			 {
				 return false;
			 }
		}
		
		private boolean SmartNetWorkOpen_fcml(String uuid,String content,int smarttype,GenieRequestInfo RequestInfo)
		{
			boolean ret = false;
			int n = 0;
			StringBuffer trace = new StringBuffer();
			String send = String.format("/fcp/send?n=%s",uuid);
			
			boolean result =  SmartNetWorkOpen(send,content,smarttype,false,RequestInfo);
			
			
			if(!result && RequestInfo.aResultType == RequestResultType.Unauthorized)
			{
				GenieDebug.error("debug","SmartNetWorkOpen_fcml Unauthorized=");
				
				boolean Authorizeresult = false;
				n = 0;
				while(!Authorizeresult && n < 3 && !m_Cancleflag)
				{
					if(RequestInfo.aSmartType == GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST)
					{
						Authorizeresult = SmartNetWorkBackgroundAuthorize2();
					}else
					{
						Authorizeresult = SmartNetWorkBackgroundAuthorize();
					}
				}
				
				if(Authorizeresult)
				{
					sendSmartRouterRequest(RequestInfo);
					return true;					
				}else
				{
					return false;
				}
			}
			
			if(!result && RequestInfo.aResultType == RequestResultType.error
					&& RequestInfo.errorcode == 16)
			{
				GenieDebug.error("debug","SmartNetWorkOpen_fcml Unauthorized=");
				
				
				boolean Authorizeresult = false;
				n = 0;
				while(!Authorizeresult && n < 3 && !m_Cancleflag)
				{
					Authorizeresult = SmartNetWorkStartRouterSession();
				}
				
				if(Authorizeresult)
				{
					sendSmartRouterRequest(RequestInfo);
					return true;					
				}else
				{
					return false;
				}
			}
			
			
			
			String receive = String.format("/fcp/receive?n=%s",uuid);
			int index = 0;
			
			while(true && !m_Cancleflag && index < 20)
			{
				index++;
				
				ret = SmartNetWorkReceive(receive,smarttype,trace,RequestInfo);
				
				GenieDebug.error("debug","SmartNetWorkOpen_fcml ret="+ret);
				GenieDebug.error("debug","SmartNetWorkOpen_fcml trace="+trace.toString());
				GenieDebug.error("debug","SmartNetWorkOpen_fcml ResultType="+RequestInfo.aResultType);
				GenieDebug.error("debug","SmartNetWorkOpen_fcml errorcode="+RequestInfo.errorcode);
				
				if(  smarttype == GenieGlobalDefines.ESMARTNETWORK_SoapRequest 
						&& RequestInfo.aSoapType == GenieGlobalDefines.ESoapRequestConfigFinish)
				{
					return true;
				}
				
				
				if(!ret && RequestInfo.aResultType == RequestResultType.Unauthorized)
				{
					GenieDebug.error("debug","SmartNetWorkOpen_fcml Unauthorized= 0");
					
					boolean Authorizeresult = false;
					n = 0;
					while(!Authorizeresult && n < 3)
					{
						Authorizeresult = SmartNetWorkBackgroundAuthorize();
					}
					
					GenieDebug.error("debug","SmartNetWorkOpen_fcml Unauthorized= 1 Authorizeresult= "+Authorizeresult);
					
					if(Authorizeresult)
					{
						GenieDebug.error("debug","SmartNetWorkOpen_fcml Unauthorized= 2");
						sendSmartRouterRequest(RequestInfo);
						return true;					
					}else
					{
						GenieDebug.error("debug","SmartNetWorkOpen_fcml Unauthorized= 3");
						return false;
					}
				}
				
				if(trace == null || trace.length() <= 0 ||trace.toString() == null)
				{
					if(RequestInfo.aResultType == RequestResultType.Exception)
						return false;
					else
						continue;
				}
				
				int t ;
				
				try{
					t = Integer.valueOf(trace.toString());
				}catch(Exception e)
				{
					e.printStackTrace();
					continue;
				}
				
				GenieDebug.error("debug","SmartNetWorkOpen_fcml t="+t);
				GenieDebug.error("debug","SmartNetWorkOpen_fcml m_SmartInfo.trace="+m_SmartInfo.trace);
				
				if(t == m_SmartInfo.trace)
				{
					return ret ;
				}else if(t < m_SmartInfo.trace)
				{
					continue;
				}else if(t > m_SmartInfo.trace)
				{
					return false;
				}else
				{
					return false;
				}
				
			}
			
			return ret ;
		}
		
		
		public void sendSmartRouterRequest(GenieRequestInfo aRequestInfo)
		{
			GenieDebug.error("debug","sendSmartRouterRequest 0");
			
			ArrayList<XmlParameter> parameterlist = new ArrayList<XmlParameter>();
			
			XmlParameter para1 = new XmlParameter();
			para1.Tag="fcml";
			para1.attribute = new ArrayList<XmlAttribute>();
			XmlAttribute attr1_1 = new XmlAttribute("to",String.format("netrouter@%s",m_SmartInfo.workcpid));
			para1.attribute.add(attr1_1);			
			XmlAttribute attr1_2 = new XmlAttribute("from",String.format("%s@%s",m_SmartInfo.uiid,m_SmartInfo.domain));
			para1.attribute.add(attr1_2);
			XmlAttribute attr1_3 = new XmlAttribute("_tracer",String.format("%s",++m_SmartInfo.trace));
			para1.attribute.add(attr1_3);
			
			para1.child = new ArrayList<XmlParameter>();
			XmlParameter para2 = new XmlParameter();
			para2.Tag=String.format("%s.%s", aRequestInfo.aServer,aRequestInfo.aMethod);
			para2.attribute = new ArrayList<XmlAttribute>();
			
			XmlAttribute attr2 = new XmlAttribute("_sessionId",m_SmartInfo.sessionid);
			para2.attribute.add(attr2);
			
			if(aRequestInfo.aElement != null)
			{
				for(int i = 0; i < aRequestInfo.aElement.size(); i += 2)
				{
					
					String name = aRequestInfo.aElement.get(i);
					String value = aRequestInfo.aElement.get(i + 1);
					
					GenieDebug.error("debug","sendSmartRouterRequest aElement name  = "+name);
					GenieDebug.error("debug","sendSmartRouterRequest aElement value  = "+value);
					
					if(!value.equals("null"))
					{
						XmlAttribute attr12 = new XmlAttribute(name,value);
						para2.attribute.add(attr12);
					}
				}
			}
			para1.child.add(para2);
			parameterlist.add(para1);
			String content =  BuildXml(parameterlist);
			
			if(content == null)
				return ;
			

//				StringWriter paramXml = new StringWriter();
//				
//				try {
//					
//					XmlSerializer serializer = Xml.newSerializer();				
//					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//					serializer = factory.newSerializer();
//					serializer.setOutput(paramXml);
//					serializer.startTag(null,"fcml");
//					serializer.attribute(null, "to", String.format("netrouter@%s",m_SmartInfo.workcpid));
//					serializer.attribute(null, "from", String.format("%s@%s",m_SmartInfo.uiid,m_SmartInfo.domain));
//					serializer.attribute(null, "_tracer", String.format("%s",++m_SmartInfo.trace));
//					
//					
//					serializer.startTag(null,String.format("%s.%s", aRequestInfo.aServer,aRequestInfo.aMethod));
//					serializer.attribute(null, "_sessionId",m_SmartInfo.sessionid);
//					
//					for(int i = 0; i < aRequestInfo.aElement.size(); i += 2)
//					{
//						
//						String name = aRequestInfo.aElement.get(i);
//						String value = aRequestInfo.aElement.get(i + 1);
//						
//						GenieDebug.error("debug","sendSmartRouterRequest aElement name  = "+name);
//						GenieDebug.error("debug","sendSmartRouterRequest aElement value  = "+value);
//						
//						
//						if(!value.equals("null"))
//						{
//							serializer.attribute(null, name,value);
//						}
//					}
//					serializer.endTag(null,String.format("%s.%s", aRequestInfo.aServer,aRequestInfo.aMethod));
//					serializer.endTag(null,"fcml");
//					serializer.flush();
//					
//				}catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//				
				
				GenieDebug.error("debug","sendSmartRouterRequest content = "+content);
				
				SmartNetWorkOpen_fcml(m_SmartInfo.uiid,content,GenieGlobalDefines.ESMARTNETWORK_SoapRequest,aRequestInfo);
				
				sendbroad(aRequestInfo);
				
		}
		
		
		private boolean SmartNetWorkReceive(String path,int smarttype,StringBuffer trace,GenieRequestInfo RequestInfo)
		{
			//URL url = null;
			//HttpURLConnection conn = null; 
			
			try {
				String site = String.format("%s%s",m_SmartUrl,path);
				
				GenieDebug.error("debug","SmartNetWorkReceive site="+site);
			
				m_url = new URL(site);
				//m_httpConn = (HttpURLConnection)m_url.openConnection();
				
				 if (m_url.getProtocol().toLowerCase().equals("https")) {
                     trustAllHosts();
                     HttpsURLConnection https = (HttpsURLConnection) m_url
                                    .openConnection();
                     https.setHostnameVerifier(DO_NOT_VERIFY);
                     m_httpConn = https;
				 } else {
					 m_httpConn = (HttpURLConnection) m_url.openConnection();
				 }
				
				m_httpConn.setRequestMethod("GET");
				m_httpConn.setDoOutput(true);
				m_httpConn.setConnectTimeout(120000);
				m_httpConn.setReadTimeout(120000);
				PrintWriter outputStream = new PrintWriter(m_httpConn.getOutputStream());
				outputStream.flush();
				outputStream.close();
				
				
				int responsecode =  m_httpConn.getResponseCode();
				GenieDebug.error("debug","SmartNetWorkOpen responsecode="+responsecode);
		
				InputStreamReader reader = new InputStreamReader((InputStream)m_httpConn.getContent());
				StringBuffer sb = new StringBuffer();
				BufferedReader buff = new BufferedReader(reader);
				String line = buff.readLine();
				while (line != null) {
					sb.append(line);
					line = buff.readLine();
				}
				reader.close();
				
				GenieDebug.error("debug","SmartNetWorkReceive sb.length()="+sb.length());
				GenieDebug.error("debug","SmartNetWorkReceive sb="+sb.toString());
				
				if(sb.length()>0)
				{
					return ParseReceiveFcml(sb.toString(),smarttype,trace,RequestInfo);
				}else
				{
					RequestInfo.aResultType = RequestResultType.failed;
					return false;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				RequestInfo.aResultType = RequestResultType.Exception;
				return false;
			}finally{
				cancelhttpUrlConnection();
			}
		}
		
		
		public void sendSmartRequest(GenieRequestInfo aRequestInfo)
		{
			GenieSmartRouterInfo  workrouter = m_SmartInfo.GetWorkSmartRouterInfo(m_SmartInfo.workcpid);
			if(workrouter != null)
			{
				String md = workrouter.model;
				if(md!= null &&(md.startsWith("CG") || md.startsWith("DG")))
				{
					
					aRequestInfo.aNeedwrap = true;
				}
			}
			if(aRequestInfo.aNeedwrap )
			{
				GenieRequestInfo start = new GenieRequestInfo();
				start.aElement = new ArrayList<String>();
				start.aElement.add("NewSessionID");
				start.aElement.add(m_SmartInfo.sessionid);
				start.aNeedParser = false;
				start.aServer = "DeviceConfig";
				start.aMethod = "ConfigurationStarted";
				start.aSoapType = GenieGlobalDefines.ESoapRequestConfigStart;
				start.aTimeout = 15000;
				
				
				sendSmartRouterRequest(start);
				
				
				if(aRequestInfo.aSoapType == GenieGlobalDefines.ESoapRequestWLanWEPKey)
				{
					String wep = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES);
					if(wep != null && wep.equals("WEP"))
					{
						GenieRequestInfo WEPWLan = new GenieRequestInfo();
						WEPWLan.aElement = new ArrayList<String>();
						WEPWLan.aElement.add("NewWEPKey");
						WEPWLan.aElement.add("null");
						WEPWLan.aElement.add("NewWPAPassphrase");
						WEPWLan.aElement.add("null");
						WEPWLan.aNeedParser = false;
						WEPWLan.aServer = "WLANConfiguration";
						WEPWLan.aMethod = "GetWEPSecurityKeys";
						WEPWLan.aSoapType = GenieGlobalDefines.ESoapRequestWEPKey;
						WEPWLan.aTimeout = 20000;
						
						sendSmartRouterRequest(WEPWLan);
						
					}else
					{
						sendSmartRouterRequest(aRequestInfo);
					}
					
				}else
				{
					sendSmartRouterRequest(aRequestInfo);
				}

			
				
				GenieRequestInfo finish = new GenieRequestInfo();
				finish.aElement = new ArrayList<String>();
				finish.aElement.add("NewStatus");
				finish.aElement.add("ChangesApplied");
				finish.aNeedParser = false;
				finish.aServer = "DeviceConfig";
				finish.aMethod = "ConfigurationFinished";
				finish.aSoapType = GenieGlobalDefines.ESoapRequestConfigFinish;
				finish.aTimeout = 15000;
				
				sendSmartRouterRequest(finish);
				
			
			}else
			{
				sendSmartRouterRequest(aRequestInfo);
			}
		}
	}
	
	
	
	
	
	
	private boolean ParseReceiveFcml(String fcml,int smarttype,StringBuffer trace,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		switch(smarttype)
		{
		case GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST:
			ret = ParseReceive_GetRouterList(fcml,smarttype,trace,RequestInfo);
			break;
		case GenieGlobalDefines.ESMARTNETWORK_StartRouterSession:
			ret = ParseReceive_StartSession(fcml,smarttype,trace,RequestInfo);
			break;
		case GenieGlobalDefines.ESMARTNETWORK_SoapRequest:
			ret = ParseReceive_SoapRequest(fcml,smarttype,trace,RequestInfo);
			break;
		case GenieGlobalDefines.ESMARTNETWORK_EndRouterSession:
			ret = ParseReceive_EndSession(fcml,smarttype,trace,RequestInfo);
			break;
		default:
			break;
		}
		
		return ret;
	}
	
	
	
	
	
	private boolean parsefcml(String data,int smarttype,boolean receive,GenieRequestInfo RequestInfo)
	{
        try{
        	XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        	parser.setInput( new StringReader(data) );
        	

        	int eventType = parser.getEventType();
        	 GenieDebug.error("debug","eventType "+eventType);
        	 
            while (eventType != XmlPullParser.END_DOCUMENT) {
             if(eventType == XmlPullParser.START_DOCUMENT) {
            	 GenieDebug.error("debug","Start document "+parser.getName());
             } else if(eventType == XmlPullParser.END_DOCUMENT) {
            	 GenieDebug.error("debug","End document "+parser.getName());
             } else if(eventType == XmlPullParser.START_TAG) {
            	
            	 
            	 String tag = parser.getName();
            	 GenieDebug.error("debug","Start tag "+tag);
            	 if(tag != null && 
            			 tag.equals("authenticate"))
            	 {
            		 if(smarttype == GenieGlobalDefines.ESMARTNETWORK_SoapRequest)
            		 {
            			 String result = parser.getAttributeValue(null,"result");
            			 try{
            				 int code = Integer.valueOf(result);
            				 if(code == 401)
            				 {
            					 RequestInfo.aResultType = RequestResultType.Unauthorized;
            					 return false;
            				 }
            			 }catch(Exception e)
            			 {
            				 e.printStackTrace();
            			 }
            		 }else
            		 {
	            		 String auth = parser.getAttributeValue(null,
	            				 GenieGlobalDefines.SMARTNETWORK_KEY_AUTHENTICATED);
	                	 GenieDebug.error("xiaotech", "auth:"+auth);
	                	 if(null == auth)
	                	 {
	                		 GenieSoap.dictionary.put(GenieGlobalDefines.SMARTNETWORK_KEY_AUTHENTICATED, 
	                				 GenieGlobalDefines.NULL);
	                		 RequestInfo.aResultType = RequestResultType.Unauthorized;
	                		 return false;
	                	 }else
	                	 {
	                		 GenieSoap.dictionary.put(GenieGlobalDefines.SMARTNETWORK_KEY_AUTHENTICATED, 
	                				 auth);
	                		 if(smarttype == GenieGlobalDefines.ESMARTNETWORKAUTHENTICATE
	                				 && auth.equals("true"))
	                		 {
	                			 RequestInfo.aResultType = RequestResultType.Succes;
	                			 return true;
	                		 }else
	                		 {
	                			 RequestInfo.aResultType = RequestResultType.Unauthorized;
	                			 return false;
	                		 }
	                	 }
            		 }
                	 

            	 }else if(tag != null && tag.equals("init"))
            	 {
            		 String domain = parser.getAttributeValue(null,"domain");
            		 String name = parser.getAttributeValue(null,"name");
                	 GenieDebug.error("xiaotech", "domain:"+domain);
                	 GenieDebug.error("xiaotech", "name:"+name);
                	 if(null != domain && null != name &&
                			 smarttype == GenieGlobalDefines.ESMARTNETWORK_INIT )
                	 {
                		 m_SmartInfo.domain = domain;
                		 m_SmartInfo.uiid = name;
                		 RequestInfo.aResultType = RequestResultType.Succes;
                		 return true;
                	 }else
                	 {
                		 RequestInfo.aResultType = RequestResultType.failed;
                		 return false;
                	 }
            	 }
            	 
            	 
            	 
            	 
             } else if(eventType == XmlPullParser.END_TAG) {
            	 GenieDebug.error("debug","End tag "+parser.getName());
             } else if(eventType == XmlPullParser.TEXT) {
            	 GenieDebug.error("debug","Text "+parser.getText());
             }
             eventType = parser.next();
             GenieDebug.error("debug","eventType "+eventType);
            }
        	
        	
        }catch(Exception e)
        {
        	e.printStackTrace();
        	RequestInfo.aResultType = RequestResultType.failed;
        	return false;
        }
        RequestInfo.aResultType = RequestResultType.Succes;
		return  true;
	}
	
	
	private boolean ParseReceive_GetRouterList(String fcml,int smarttype,StringBuffer trace,GenieRequestInfo RequestInfo)
	{
		
		int itrace = 0;
		
		if(m_SmartInfo.routerlist == null)
			return false;
		
		m_SmartInfo.routerlist.clear();
		
		try{
        	XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        	parser.setInput( new StringReader(fcml) );
        	

        	int eventType = parser.getEventType();
        	 GenieDebug.error("debug","eventType "+eventType);
        	 
            while (eventType != XmlPullParser.END_DOCUMENT) {
             if(eventType == XmlPullParser.START_DOCUMENT) {
            	 GenieDebug.error("debug","Start document "+parser.getName());
             } else if(eventType == XmlPullParser.END_DOCUMENT) {
            	 GenieDebug.error("debug","End document "+parser.getName());
             } else if(eventType == XmlPullParser.START_TAG) {
            	
            	 String tag = parser.getName();
            	 GenieDebug.error("debug","Start tag "+tag);
            	 
            	 if(tag != null && 
            			 tag.equals("authenticate"))
            	 {
            		 
        			 String result = parser.getAttributeValue(null,"result");
        			 try{
        				 int code = Integer.valueOf(result);
        				 if(code == 401)
        				 {
        					 RequestInfo.aResultType = RequestResultType.Unauthorized;
        					 return false;
        				 }
        			 }catch(Exception e)
        			 {
        				 e.printStackTrace();
        			 }
            	  }
            	 
            	 if(tag != null && 
            			 tag.equals("fcml"))
            	 {
            		 String  _trace  = parser.getAttributeValue(null,"_tracer");
            		 
            		 trace.delete(0,trace.length());
            		 trace.append(_trace);
            		 if(null != _trace)
            		 {
            			 try{
            				 itrace =  Integer.valueOf(_trace);
            			 }catch(Exception e)
            			 {
            				 e.printStackTrace();
            			 }
            			 
            			 GenieDebug.error("debug","Start itrace: "+itrace);
            		 }
            	 }

            	 if(itrace == m_SmartInfo.trace && 
            			 tag != null && 
            			 tag.startsWith("portal.router.") &&
            			 tag.endsWith(".is_all"))
            	 {
            		 GenieSmartRouterInfo router = new GenieSmartRouterInfo();
            		 router.cpid = tag.substring("portal.router.".length(),tag.length()-".is_all".length());
            		 router.active = parser.getAttributeValue(null,"active");
            		 router.friendly_name = parser.getAttributeValue(null,"friendly_name");
            		 router.model = parser.getAttributeValue(null,"model");
            		 router.owner = parser.getAttributeValue(null,"owner");
            		 router.serial = parser.getAttributeValue(null,"serial");
            		 router.type = parser.getAttributeValue(null,"type");
            		 
            		 //if(router.active.equals("true"))
            		 if(!router.model.toLowerCase().startsWith("rnd"))
            		 	 m_SmartInfo.routerlist.add(router);
            		 
            		 GenieDebug.error("debug","Start router.cpid: "+router.cpid);
            		 GenieDebug.error("debug","Start router.active: "+router.active);
            		 GenieDebug.error("debug","Start router.friendly_name: "+router.friendly_name);
            		 GenieDebug.error("debug","Start router.model: "+router.model);
            		 GenieDebug.error("debug","Start router.owner: "+router.owner);
            		 GenieDebug.error("debug","Start router.serial: "+router.serial);
            		 GenieDebug.error("debug","Start router.type: "+router.type);
            		 
            		 RequestInfo.aResultType = RequestResultType.Succes;
            	 }
            	 
             } else if(eventType == XmlPullParser.END_TAG) {
            	 GenieDebug.error("debug","End tag "+parser.getName());
             } else if(eventType == XmlPullParser.TEXT) {
            	 GenieDebug.error("debug","Text "+parser.getText());
             }
             eventType = parser.next();
             GenieDebug.error("debug","eventType "+eventType);
            }
        	
        	
        }catch(Exception e)
        {
        	e.printStackTrace();
        	RequestInfo.aResultType = RequestResultType.Exception;
        	return false;
        }
        
        if(m_SmartInfo.routerlist.size() >1)
        {
        	SortRouterList(m_SmartInfo.routerlist);
        }
        
        if(itrace == m_SmartInfo.trace)
        	return true;
        else
        	return false;
	}
	
	
	public void SortRouterList(ArrayList<GenieSmartRouterInfo> list)
	 {
		 if(list == null)
			 return ;
		 
		 Collections.sort(list,new Comparator<GenieSmartRouterInfo>(){

			@Override
			public int compare(GenieSmartRouterInfo object1,
					GenieSmartRouterInfo object2) {
				// TODO Auto-generated method stub
				if(object1.active.equals("true") && object2.active.equals("true"))
				{
					return 0;
				}else if(object1.active.equals("false") && object2.active.equals("true"))
				{
					return 1;
				}else
				{
					return -1;
				}
			}
			 
		 });
	 }
	
	private boolean ParseReceive_SoapRequest(String fcml,int smarttype,StringBuffer trace,GenieRequestInfo RequestInfo)
	{
		
		boolean ret = false;
		int itrace = 0;
		
		
		try{
        	XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        	parser.setInput( new StringReader(fcml) );
        	

        	int eventType = parser.getEventType();
        	 GenieDebug.error("debug","eventType "+eventType);
        	 
            while (eventType != XmlPullParser.END_DOCUMENT) {
             if(eventType == XmlPullParser.START_DOCUMENT) {
            	 GenieDebug.error("debug","Start document "+parser.getName());
             } else if(eventType == XmlPullParser.END_DOCUMENT) {
            	 GenieDebug.error("debug","End document "+parser.getName());
             } else if(eventType == XmlPullParser.START_TAG) {
            	
            	 String tag = parser.getName();
            	 GenieDebug.error("debug","Start tag "+tag);
            	 
            	 
            	 if(tag != null && 
            			 tag.equals("authenticate"))
            	 {
            		 
        			 String result = parser.getAttributeValue(null,"result");
        			 try{
        				 int code = Integer.valueOf(result);
        				 if(code == 401)
        				 {
        					 RequestInfo.aResultType = RequestResultType.Unauthorized;
        					 return false;
        				 }
        			 }catch(Exception e)
        			 {
        				 e.printStackTrace();
        			 }
            	  }
            	 
            	 if(tag != null && 
            			 tag.equals("fcml"))
            	 {
            		 String  _trace  = parser.getAttributeValue(null,"_tracer");
            		 
            		 trace.delete(0,trace.length());
            		 trace.append(_trace);
            		 if(null != _trace)
            		 {
            			 try{
            				 itrace =  Integer.valueOf(_trace);
            			 }catch(Exception e)
            			 {
            				 e.printStackTrace();
            			 }
            			 
            			 GenieDebug.error("debug","Start itrace: "+itrace);
            		 }
            	 }
            	 
            	 if(tag != null && 
            			 tag.equals("error"))
            	 {
            		 String  errorcode  = parser.getAttributeValue(null,"code");
            		 
            		 
            		 if(null != errorcode)
            		 {
            			 try{
            				 int code =  Integer.valueOf(errorcode);
            				 RequestInfo.errorcode = code;
            			 }catch(NumberFormatException e){
            				 e.printStackTrace();
            			 }
            			 GenieDebug.error("debug","Start itrace: "+itrace);
            		 }
            		 RequestInfo.aResultType = RequestResultType.error;
            		 ret = false;
            		 
            		 return ret;
            	 }
            	 
            	 GenieDebug.error("debug",String.format("ParseReceive_SoapRequest itrace = %s,m_SmartInfo.trace=%s ",
            			 itrace,m_SmartInfo.trace));
            	 
            	 GenieDebug.error("debug",String.format("ParseReceive_SoapRequest tag = %s,==%s ",
            			 tag,String.format("%s.netrouter.%s.%s",m_SmartInfo.workcpid,RequestInfo.aServer,RequestInfo.aMethod)));

            	 if(itrace == m_SmartInfo.trace && 
            			 tag != null && 
            			 tag.equals(String.format("%s.netrouter.%s.%s",m_SmartInfo.workcpid,RequestInfo.aServer,RequestInfo.aMethod)))
            	 {
            		 String responseCode = parser.getAttributeValue(null,"_responseCode");
        			if(null != responseCode)
        			{
        				ret = true;
        				GenieDebug.error("debug","ParseReceive_SoapRequest responseCode="+responseCode);
        				
        				try{
        					int code = Integer.valueOf(responseCode);
        					if(code == 0)
        					{
        						RequestInfo.aResultType = RequestResultType.Succes;
        						RequestInfo.aResponseCode = responseCode;
        						RequestInfo.aResponse = fcml;
        						if(RequestInfo.aNeedParser)
        						{
        							ParseReceive_SoapRequest_GetAttributeValue(parser,RequestInfo);
        						}
        					}else
        					{
        						RequestInfo.aResultType = RequestResultType.failed;
        						RequestInfo.aResponse = fcml;
        						RequestInfo.aResponseCode = responseCode;
        					}
        				}catch(Exception e)
        				{
        					RequestInfo.aResultType = RequestResultType.Exception;
        					e.printStackTrace();
        				}
        				
        			}
            			
            		      		
            	 }
            	 
             } else if(eventType == XmlPullParser.END_TAG) {
            	 GenieDebug.error("debug","End tag "+parser.getName());
             } else if(eventType == XmlPullParser.TEXT) {
            	 GenieDebug.error("debug","Text "+parser.getText());
             }
             eventType = parser.next();
             GenieDebug.error("debug","eventType "+eventType);
            }
        	
        	
        }catch(Exception e)
        {
        	e.printStackTrace();
        	
        	RequestInfo.aResultType = RequestResultType.Exception;
        	RequestInfo.aResponse = fcml;
        	ret = false;
   		 	return ret;
        }
        
        if(itrace == m_SmartInfo.trace)
        	return ret;
        else
        	return false;
        

	}
	
	
	private boolean ParseReceive_SoapRequest_GetAttributeValue(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_GetAttributeValue  RequestInfo.aSoapType ="+RequestInfo.aSoapType);
		
		switch(RequestInfo.aSoapType)
		{
		case GenieGlobalDefines.ESoapRequestWLanInfo:
			ret = ParseReceive_SoapRequest_GetInfo(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestWLanWEPKey:
			ret = ParseReceive_SoapRequest_GetWLanWEPKey(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestGuestEnable:
			ret = ParseReceive_SoapRequest_GetGuestEnable(parser,RequestInfo);
			break;	
		case GenieGlobalDefines.ESoapRequestGuestInfo:
			ret = ParseReceive_SoapRequest_GetGuestInfo(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapReqiestTrafficEnable:
			ret = ParseReceive_SoapRequest_GetTrafficEnable(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestTrafficOptions:
			ret = ParseReceive_SoapRequest_GetTrafficOptions(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestTrafficMeter:
			ret = ParseReceive_SoapRequest_GetTrafficData(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestDeviceID:
			ret = ParseReceive_SoapRequest_GetLpcDeviceID(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestRouterInfo:
			ret = ParseReceive_SoapRequest_GetRouterInfo(parser,RequestInfo);
			break ;
		case GenieGlobalDefines.ESoapRequestConfigWan:
			ret = ParseReceive_SoapRequest_GetConfigWan(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestPCStatus:
			ret = ParseReceive_SoapRequest_GetLPCStatus(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus:
			ret = ParseReceive_SoapRequest_BlockDeviceEnableStatus(parser,RequestInfo);
			break;
		case GenieGlobalDefines.ESoapRequestRouterMap:
			ret = ParseReceive_SoapRequest_RouterMap(parser,RequestInfo);
			break;
		default:
			break;
		}
		
		return ret;
	}
	
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
		GenieSoap.routerMap.clear();
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
				GenieSoap.routerMap.add(detail);
			}	
			
			
		}
	}
	
	private boolean ParseReceive_SoapRequest_RouterMap(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_RouterMap 0");
		
		String map = parser.getAttributeValue(null, "NewAttachDevice");
		if(null != map)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_RouterMap map="+map);
			parserStringForNetworkMap(map);
		}else
		{
			map = parser.getAttributeValue(null, "AttachDevice");
			if(null != map)
			{
				ret = true;
				GenieDebug.error("debug","ParseReceive_SoapRequest_RouterMap map="+map);
				parserStringForNetworkMap(map);
			}
		}
	
		
		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_BlockDeviceEnableStatus(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;

		GenieDebug.error("debug","ParseReceive_SoapRequest_BlockDeviceEnableStatus 0");
		
		String Status = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE);
		if(null != Status)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_BlockDeviceEnableStatus Status="+Status);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE, Status);
		}
	
		
		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_GetLPCStatus(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;

		GenieDebug.error("debug","ParseReceive_SoapRequest_GetLPCStatus 0");
		
		String LPCStatus = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS);
		if(null != LPCStatus)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetLPCStatus LPCStatus="+LPCStatus);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS, LPCStatus);
		}
	
		
		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_GetConfigWan(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;

		GenieDebug.error("debug","ParseReceive_SoapRequest_GetConfigWan 0");
		
		String mac = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS);
		if(null != mac)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetConfigWan mac="+mac);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MAC_ADDRESS, mac);
		}
	
		
		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_GetRouterInfo(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;

		GenieDebug.error("debug","ParseReceive_SoapRequest_GetRouterInfo 0");
		
		String modename = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
		if(null != modename)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetRouterInfo modename="+modename);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME, modename);
		}
		
		String firmware = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION);
																			
		if(null != firmware)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetRouterInfo firmware="+firmware);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION, firmware);
		}
		
		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_GetLpcDeviceID(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_GetLpcDeviceID 0");
		
		String DeviceID = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
		if(null != DeviceID)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetLpcDeviceID DeviceID="+DeviceID);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID, DeviceID);
		}
		
		return ret;		
	}
	
	
	
	private boolean ParseReceive_SoapRequest_GetTrafficData(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData 0");
		
		StringBuffer temp = new StringBuffer();
		
		
		
		
		String NewTodayConnectionTime = parser.getAttributeValue(null, "NewTodayConnectionTime");
		if(null != NewTodayConnectionTime)
		{
			ret = true;
			temp.append(NewTodayConnectionTime);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewTodayConnectionTime="+NewTodayConnectionTime);
			
		}
		
		String NewTodayUpload = parser.getAttributeValue(null, "NewTodayUpload");
		if(null != NewTodayUpload)
		{
			ret = true;
			temp.append(NewTodayUpload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewTodayUpload="+NewTodayUpload);
			
		}
		
		String NewTodayDownload = parser.getAttributeValue(null, "NewTodayDownload");
		if(null != NewTodayDownload)
		{
			ret = true;
			temp.append(NewTodayDownload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewTodayDownload="+NewTodayDownload);
			
		}
		
		String NewYesterdayConnectionTime = parser.getAttributeValue(null, "NewYesterdayConnectionTime");
		if(null != NewYesterdayConnectionTime)
		{
			ret = true;
			temp.append(NewYesterdayConnectionTime);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewYesterdayConnectionTime="+NewYesterdayConnectionTime);
			
		}
		
		
		String NewYesterdayUpload = parser.getAttributeValue(null, "NewYesterdayUpload");
		if(null != NewYesterdayUpload)
		{
			ret = true;
			temp.append(NewYesterdayUpload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewYesterdayUpload="+NewYesterdayUpload);
			
		}
		
		String NewYesterdayDownload = parser.getAttributeValue(null, "NewYesterdayDownload");
		if(null != NewYesterdayDownload)
		{
			ret = true;
			temp.append(NewYesterdayDownload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewYesterdayDownload="+NewYesterdayDownload);
			
		}
		
		String NewWeekConnectionTime = parser.getAttributeValue(null, "NewWeekConnectionTime");
		if(null != NewWeekConnectionTime)
		{
			ret = true;
			temp.append(NewWeekConnectionTime);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewWeekConnectionTime="+NewWeekConnectionTime);
			
		}
		String NewWeekUpload = parser.getAttributeValue(null, "NewWeekUpload");
		if(null != NewWeekUpload)
		{
			ret = true;
			temp.append(NewWeekUpload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewWeekUpload="+NewWeekUpload);
			
		}
		String NewWeekDownload = parser.getAttributeValue(null, "NewWeekDownload");
		if(null != NewWeekDownload)
		{
			ret = true;
			temp.append(NewWeekDownload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewWeekDownload="+NewWeekDownload);
			
		}
		String NewMonthConnectionTime = parser.getAttributeValue(null, "NewMonthConnectionTime");
		if(null != NewMonthConnectionTime)
		{
			ret = true;
			temp.append(NewMonthConnectionTime);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewMonthConnectionTime="+NewMonthConnectionTime);
			
		}
		String NewMonthUpload = parser.getAttributeValue(null, "NewMonthUpload");
		if(null != NewMonthUpload)
		{
			ret = true;
			temp.append(NewMonthUpload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewMonthUpload="+NewMonthUpload);
			
		}
		
		String NewMonthDownload = parser.getAttributeValue(null, "NewMonthDownload");
		if(null != NewMonthDownload)
		{
			ret = true;
			temp.append(NewMonthDownload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewMonthDownload="+NewMonthDownload);
			
		}
		String NewLastMonthConnectionTime = parser.getAttributeValue(null, "NewLastMonthConnectionTime");
		if(null != NewLastMonthConnectionTime)
		{
			ret = true;
			temp.append(NewLastMonthConnectionTime);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewLastMonthConnectionTime="+NewLastMonthConnectionTime);
			
		}
		String NewLastMonthUpload = parser.getAttributeValue(null, "NewLastMonthUpload");
		if(null != NewLastMonthUpload)
		{
			ret = true;
			temp.append(NewLastMonthUpload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewLastMonthUpload="+NewLastMonthUpload);
			
		}
		String NewLastMonthDownload = parser.getAttributeValue(null, "NewLastMonthDownload");
		if(null != NewLastMonthDownload)
		{
			ret = true;
			temp.append(NewLastMonthDownload);
			temp.append("\n");
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficData NewLastMonthDownload="+NewLastMonthDownload);
			
		}
		
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC, temp.toString());
		
		return ret;		
	}
	
	
	private boolean ParseReceive_SoapRequest_GetTrafficOptions(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficOptions 0");
		
		String control = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL);
		if(null != control)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficOptions control="+control);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL, control);
		}

		
		
		String limit = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT);
		if(null != limit)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficOptions limit="+limit);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT, limit);
		}
		
		
		String hour = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR);
		if(null != hour)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficOptions hour="+hour);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR, hour);
		}
		
		
		String minute = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE);
		if(null != minute)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficOptions minute="+minute);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE, minute);
		}
		
		String day = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY);
		if(null != day)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficOptions day="+day);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY, day);
		}
		

		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_GetTrafficEnable(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficEnable 0");
		
		String TrafficEnable = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE);
		if(null != TrafficEnable)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetTrafficEnable TrafficEnable="+TrafficEnable);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE, TrafficEnable);
		}
		

		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_GetGuestInfo(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		
		
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_GetGuestInfo 0");
		
		String ssid = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
		if(null != ssid)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetGuestInfo ssid="+ssid);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID, ssid);
		}
		
		String guest_mode = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE);
		if(null != guest_mode)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetGuestInfo guest_mode="+guest_mode);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE, guest_mode);
		}
		
		String guest_key = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY);
		if(null != guest_key)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetGuestInfo guest_key="+guest_key);
			
			if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY).equals("None"))
			{
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY, "None");
			}
			else
			{
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY, guest_key);
			}	
		}

		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_GetGuestEnable(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		


		GenieDebug.error("debug","ParseReceive_SoapRequest_GetGuestEnable 0");
		
		String guest_able = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE);
		if(null != guest_able)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetGuestEnable guest_able="+guest_able);
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE, guest_able);
		}

		return ret;		
	}
	
	private boolean ParseReceive_SoapRequest_GetWLanWEPKey(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_GetWLanWEPKey 0");
		
		String wpa_key = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY);
		if(null != wpa_key)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetWLanWEPKey wpa_key="+wpa_key);
			
			if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY).equals("None"))
			{
				GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, None)");
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, "None");
			}
			else
			{
				GenieDebug.error("debug", "dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, string) string = "+wpa_key);
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY, wpa_key);
			}
		}

		return ret;		
	}
	
	
	private boolean ParseReceive_SoapRequest_GetInfo(XmlPullParser parser,GenieRequestInfo RequestInfo)
	{
		boolean ret = false;
		
		GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo 0");
		

		
		
		String enable = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_ENABLE);
		if(null != enable)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo enable="+enable);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_ENABLE, enable);
		}
		
		String ssid = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID);
		if(null != ssid)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo ssid="+ssid);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID, ssid);
		}
		
		String channel = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL);
		if(null != channel)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo channel="+channel);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL, channel);
		}
		
		String region = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION);
		if(null != region)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo region="+region);
			if(region.equals("USA"))
			{
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION, "US");
			}else
			{
				GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_REGION, region);
			}
		}
		
		String wire_mode = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE);
		if(null != wire_mode)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo wire_mode="+wire_mode);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WIRE_MODE, wire_mode);
		}
		
		String wpa_key = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY);
		if(null != wpa_key)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo wpa_key="+wpa_key);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY, wpa_key);
		}
		
		String wlan_mac = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
		if(null != wlan_mac)
		{
			ret = true;
			StringBuffer mac = new StringBuffer(wlan_mac);
			
			if(mac != null &&  mac.length() == 12)
			{
				for(int i = 10; i > 0; i -= 2)
				{
					mac.insert(i, ":");
				}
			}
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo mac.toString()="+mac.toString());		
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC, mac.toString());
		}
		
		
		String wlan_status = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_STATUS);
		if(null != wlan_status)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo wlan_status="+wlan_status);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_STATUS, wlan_status);
		}
		
		
		String basic_mode = parser.getAttributeValue(null, GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES);
		if(null != basic_mode)
		{
			ret = true;
			GenieDebug.error("debug","ParseReceive_SoapRequest_GetInfo basic_mode="+basic_mode);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES, basic_mode);
		}
		
		
		return ret;		
	}
	
	
	
	
	
	private boolean ParseReceive_EndSession(String fcml,int smarttype,StringBuffer trace,GenieRequestInfo RequestInfo)
	{
		
		int itrace = 0;
		
		
		try{
        	XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        	parser.setInput( new StringReader(fcml) );
        	

        	int eventType = parser.getEventType();
        	 GenieDebug.error("debug","eventType "+eventType);
        	 
            while (eventType != XmlPullParser.END_DOCUMENT) {
             if(eventType == XmlPullParser.START_DOCUMENT) {
            	 GenieDebug.error("debug","Start document "+parser.getName());
             } else if(eventType == XmlPullParser.END_DOCUMENT) {
            	 GenieDebug.error("debug","End document "+parser.getName());
             } else if(eventType == XmlPullParser.START_TAG) {
            	
            	 String tag = parser.getName();
            	 GenieDebug.error("debug","Start tag "+tag);
            	 
            	 if(tag != null && 
            			 tag.equals("authenticate"))
            	 {
            		 
        			 String result = parser.getAttributeValue(null,"result");
        			 try{
        				 int code = Integer.valueOf(result);
        				 if(code == 401)
        				 {
        					 RequestInfo.aResultType = RequestResultType.Unauthorized;
        					 return false;
        				 }
        			 }catch(Exception e)
        			 {
        				 e.printStackTrace();
        			 }
            	  }
            	 
            	 if(tag != null && 
            			 tag.equals("fcml"))
            	 {
            		 String  _trace  = parser.getAttributeValue(null,"_tracer");
            		 
            		 trace.delete(0,trace.length());
            		 trace.append(_trace);
            		 if(null != _trace)
            		 {
            			 try{
            				 itrace =  Integer.valueOf(_trace);
            			 }catch(Exception e)
            			 {
            				 e.printStackTrace();
            			 }
            			 GenieDebug.error("debug","Start itrace: "+itrace);
            		 }
            	 }
            	 
            	 if(tag != null && 
            			 tag.equals("error"))
            	 {
            		 String  errorcode  = parser.getAttributeValue(null,"code");
            		 
            		 int code = 0;
            		 if(null != errorcode)
            		 {
            			 try{
            				 code =  Integer.valueOf(errorcode);
            				 RequestInfo.errorcode = code;
            			 }catch(NumberFormatException e){
            				 e.printStackTrace();
            			 }
            			 GenieDebug.error("debug","Start itrace: "+itrace);
            		 }
            		 RequestInfo.aResultType = RequestResultType.error;
            		 return false;
            	 }

            	
            	 
             } else if(eventType == XmlPullParser.END_TAG) {
            	 GenieDebug.error("debug","End tag "+parser.getName());
             } else if(eventType == XmlPullParser.TEXT) {
            	 GenieDebug.error("debug","Text "+parser.getText());
             }
             eventType = parser.next();
             GenieDebug.error("debug","eventType "+eventType);
            }
        	
        	
        }catch(Exception e)
        {
        	e.printStackTrace();
        	RequestInfo.aResultType = RequestResultType.Exception;
        	return false;
        }
        
        if(itrace == m_SmartInfo.trace)
        	return true;
        else
        	return false;
	}
	
	private boolean ParseReceive_StartSession(String fcml,int smarttype,StringBuffer trace,GenieRequestInfo RequestInfo)
	{
		
		int itrace = 0;
		
		
		try{
        	XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        	parser.setInput( new StringReader(fcml) );
        	

        	int eventType = parser.getEventType();
        	 GenieDebug.error("debug","eventType "+eventType);
        	 
            while (eventType != XmlPullParser.END_DOCUMENT) {
             if(eventType == XmlPullParser.START_DOCUMENT) {
            	 GenieDebug.error("debug","Start document "+parser.getName());
             } else if(eventType == XmlPullParser.END_DOCUMENT) {
            	 GenieDebug.error("debug","End document "+parser.getName());
             } else if(eventType == XmlPullParser.START_TAG) {
            	
            	 String tag = parser.getName();
            	 GenieDebug.error("debug","Start tag "+tag);
            	 
            	 if(tag != null && 
            			 tag.equals("authenticate"))
            	 {
            		 
        			 String result = parser.getAttributeValue(null,"result");
        			 try{
        				 int code = Integer.valueOf(result);
        				 if(code == 401)
        				 {
        					 RequestInfo.aResultType = RequestResultType.Unauthorized;
        					 return false;
        				 }
        			 }catch(Exception e)
        			 {
        				 e.printStackTrace();
        			 }
            	  }
            	 
            	 if(tag != null && 
            			 tag.equals("fcml"))
            	 {
            		 String  _trace  = parser.getAttributeValue(null,"_tracer");
            		 
            		 trace.delete(0,trace.length());
            		 trace.append(_trace);
            		 if(null != _trace)
            		 {
            			 try{
            				 itrace =  Integer.valueOf(_trace);
            			 }catch(Exception e)
            			 {
            				 e.printStackTrace();
            			 }
            			 GenieDebug.error("debug","Start itrace: "+itrace);
            		 }
            	 }
            	 
            	 if(tag != null && 
            			 tag.equals("error"))
            	 {
            		 String  errorcode  = parser.getAttributeValue(null,"code");
            		 
            		 int code = 0;
            		 if(null != errorcode)
            		 {
            			 try{
            				 code =  Integer.valueOf(errorcode);
            				 RequestInfo.errorcode = code;
            			 }catch(NumberFormatException e){
            				 e.printStackTrace();
            			 }
            			 GenieDebug.error("debug","Start itrace: "+itrace);
            		 }
            		 RequestInfo.aResultType = RequestResultType.error;
            		 return false;
            	 }

            	 if(itrace == m_SmartInfo.trace && 
            			 tag != null && 
            			 tag.equals(String.format("%s.netrouter.SessionManagement.startSession",m_SmartInfo.workcpid)))
            	 {
            		
            		 String session =  parser.getAttributeValue(null,"sessionId");
            		 
            		 if(session != null)
            		 {
            			 m_SmartInfo.sessionid = session;
            			 GenieDebug.error("debug","Start session: "+session);
            		 }
            		 RequestInfo.aResultType = RequestResultType.Succes;
            	 }
            	 
             } else if(eventType == XmlPullParser.END_TAG) {
            	 GenieDebug.error("debug","End tag "+parser.getName());
             } else if(eventType == XmlPullParser.TEXT) {
            	 GenieDebug.error("debug","Text "+parser.getText());
             }
             eventType = parser.next();
             GenieDebug.error("debug","eventType "+eventType);
            }
        	
        	
        }catch(Exception e)
        {
        	e.printStackTrace();
        	RequestInfo.aResultType = RequestResultType.Exception;
        	return false;
        }
        
        if(itrace == m_SmartInfo.trace)
        	return true;
        else
        	return false;
	}
	
	//Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
	
	
	
	
	
	
	
	private String BuildXml(ArrayList<XmlParameter> parameterlist)
	{
		if(parameterlist == null)
			return null;
		
		StringWriter paramXml = new StringWriter();
		
		try {
			
			XmlSerializer serializer = Xml.newSerializer();				
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			serializer = factory.newSerializer();
			serializer.setOutput(paramXml);
			
			for(XmlParameter parameter : parameterlist)
			{
				if(parameter.Tag == null )
					continue;
				serializer.startTag(null,parameter.Tag);
				if(parameter.attribute != null)
				{
					for(XmlAttribute attr : parameter.attribute)
					{
						if(null != attr)
						{
							serializer.attribute(null, attr.name,attr.value);
						}
					}
				}
				if(parameter.text != null)
				{
					serializer.text(parameter.text);
				}
				if(parameter.child != null)
				{
					serializer.flush();
					paramXml.append(BuildXml(parameter.child));
				}
				
				serializer.endTag(null,parameter.Tag);
			}
				
			
			serializer.flush();
			
			GenieDebug.error("debug","BuildXml paramXml.toString() = "+paramXml.toString());
			return paramXml.toString();
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	private static String ipIntToString(int ip)      
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
		
		WifiManager test = (WifiManager)m_context.getSystemService(Context.WIFI_SERVICE);
			DhcpInfo   dhcpInfo=test.getDhcpInfo();

			
			GenieDebug.error("debug","gateway = "+ipIntToString(dhcpInfo.gateway));
		
		return ipIntToString(dhcpInfo.gateway);
	}  
	
	
	private static final String SmartNetworkUrl = "https://genie.netgear.com";  //appgenie-staging
	
	public static String GetSmartNetworkUrl(Context context)
    {
  	  		GenieDebug.error("debug","GetSmartNetworkUrl ");
			SharedPreferences settings = context.getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     String url = settings.getString(GenieGlobalDefines.SMARTNETWORKURL,SmartNetworkUrl);
			     GenieDebug.error("debug","GetSmartNetworkUrl url = "+url);
			     return url;
			}else
			{
				return SmartNetworkUrl;
			}
    }
    public static void SaveSmartNetworkUrl(Context context ,String url)
    {
  	   
  	
  	  
  	  SharedPreferences settings = context.getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0);
	  if(null != settings)
	  {
	   	  settings.edit().putString(GenieGlobalDefines.SMARTNETWORKURL, url).commit();
	  }

    }
    
    
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
                return true;
        }
    };

	/**
	 * 淇′换鎵�湁涓绘満-瀵逛簬浠讳綍璇佷功閮戒笉鍋氭鏌�
	 */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android 閲囩敤X509鐨勮瘉涔︿俊鎭満鍒�
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[] {};
                }

                public void checkClientTrusted(X509Certificate[] chain,
                                String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                String authType) throws CertificateException {
                }
        } };

        // Install the all-trusting trust manager
        try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection
                                .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
                e.printStackTrace();
        }
	}


   
//   static TrustManager[] xtmArray = new MytmArray[] { new MytmArray() };  
//   
//   /** 
//    * 淇′换鎵�湁涓绘満-瀵逛簬浠讳綍璇佷功閮戒笉鍋氭鏌�
//    */  
//   private static void trustAllHosts() {  
//       // Create a trust manager that does not validate certificate chains  
//       // Android 閲囩敤X509鐨勮瘉涔︿俊鎭満鍒� 
//       // Install the all-trusting trust manager  
//       try {  
//           SSLContext sc = SSLContext.getInstance("TLS");  
//           sc.init(null, xtmArray, new java.security.SecureRandom());  
//           HttpsURLConnection  
//                   .setDefaultSSLSocketFactory(sc.getSocketFactory());  
//           // HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);//  
//           // 涓嶈繘琛屼富鏈哄悕纭  
//       } catch (Exception e) {  
//           e.printStackTrace();  
//       }  
//   }  
// 
//   static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {  
//       @Override  
//       public boolean verify(String hostname, SSLSession session) {  
//           // TODO Auto-generated method stub  
//           // System.out.println("Warning: URL Host: " + hostname + " vs. "  
//           // + session.getPeerHost());  
//           return true;  
//       }  
//   };  
//   
   
	
}
