package com.dragonflow;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class GenieStatistician 
{
	private Context mContext;
	private int mHour; 
	private int mMinute; 
	private int mYear; 
	private int mMonth; 
	private int mDay;
	private int http_timeout = 60000;
	private int mTaskTimeOut = 20000;
	private String StatServer = "lrs.netgear.com";//"192.168.7.200";

	final Calendar c = Calendar.getInstance();
	HashMap<String,String> map = null;
	
	public Boolean m_back = false;
	
	Timer timer = new Timer();
	TimerTask task;
	
	String mPM = null;
	String mD = null;
	String mT = null;
	String mOS = null;
	String mGV = null;
	String mRM = null;
	String mSN = "00";
	String mMN = null;
	String mFV = null;
	
	boolean mIsRuning = false;
	boolean isSuccess = false;
	
	
	
	public GenieStatistician(Context context)
	{
		mContext = context;
		map = GenieSerializ.ReadMap(mContext, "Stat");
		if(map == null)
		{
			map = new HashMap<String,String>();
			map.put("Sucess", "false");
			GenieSerializ.WriteMap(mContext, map, "Stat");
		}
		else
		{
			if(map.get("Sucess") == "true")
			{
				isSuccess = true;
			}
		}
		
		final Thread T = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				while(true)
				{
					if(m_back)
					{
						return ;
					}
					
					try 
					{
						Thread.sleep(mTaskTimeOut);
					} 
					catch(InterruptedException e) {
						return;
					}
					
					if(mIsRuning || isSuccess)
						continue;
					
					map = GenieSerializ.ReadMap(mContext, "Stat");
					if(map == null)
					{
						continue;
					}
					
					if(map.get("Sucess") != "true")
					{
						mIsRuning = true;
						if(SendRecord())
						{
							map = new HashMap<String,String>();
							map.put("Sucess", "true");
							GenieSerializ.WriteMap(mContext, map, "Stat");
							isSuccess = true;
							mIsRuning = false;
							return;
						}
					}
				}
			}
		});

		
		if(!isSuccess)
		{
			if(m_back)
			{
				return ;
			}
			new Thread()
			{
				public void run()
				{
					if(SendRecord())
					{
						map = new HashMap<String,String>();
						map.put("Sucess", "true");
						GenieSerializ.WriteMap(mContext, map, "Stat");
						isSuccess = true;
					}
					else
					{
						T.start();
					}
				}
			}.start();
		}
		
	}
	
	public void Stop()
	{
		m_back = true;
	}
	
	private boolean SendRecord() 
	{
		if(m_back)
		{
			return false;
		}
		
		mPM = GetPM();
		if(mPM == null || mPM.equals(""))
			return false;
		mD = this.GetD();
		if(mD == null || mD.equals(""))
			return false;
		mT = this.GetT();
		if(mT == null || mT.equals(""))
			return false;
		mOS = this.GetOS();
		if(mOS == null || mOS.equals(""))
			return false;
		mGV = this.GetGV();
		if(mGV == null || mGV.equals(""))
			return false;
		
		mRM = this.GetRM();
		if(mRM == null || mRM.equals(""))
			return false;
		if(!CurrntSetting())
			return false;
		
		GenieDebug.error("Statistician","********start stat******");
		if(!StatRouteInfo())
			return false;
		if(!StatSetup())
			return false;
			
		
		return true;
	}
	
	private String GetPM() 
	{
		if(m_back)
		{
			return null;
		}
		
		WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);   
        WifiInfo info = wifi.getConnectionInfo();   
        return info.getMacAddress(); 
	}
	
	private String GetD()
	{
		if(m_back)
		{
			return null;
		}
		
		String d = null;
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        d = String.valueOf(mYear) + 
        	"-" +
        	((mMonth > 9 ? String.valueOf(mMonth) : "0" + String.valueOf(mMonth))) +
        	"-" +
        	((mDay > 9 ? String.valueOf(mDay) : "0" + String.valueOf(mDay)));
		return d;
	}
	
	private String GetT()
	{
		if(m_back)
		{
			return null;
		}
		
		String t = null;
		mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        t = (mHour > 9 ? String.valueOf(mHour) : "0"+String.valueOf(mHour)) +
        ":" + 
        ((mMinute > 9 ? String.valueOf(mMinute) : "0" + String.valueOf(mMinute))) +
        ":00";
     
		return t;
	}
	
	private String GetOS()
	{
		if(m_back)
		{
			return null;
		}
		
		return "Android" + android.os.Build.VERSION.RELEASE; 
	}
	
	private String GetGV()
	{
		
		if(m_back)
		{
			return null;
		}
		
		String gv = null;
		

		PackageManager manager = mContext.getPackageManager();  

		PackageInfo info = null;
		String packageName = null;
		int versionCode = 0;
		String versionName = null ;
		try {
			info = manager.getPackageInfo(mContext.getPackageName(), 0);
			
			packageName = info.packageName;  

			versionCode = info.versionCode;  

			versionName = info.versionName;  
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "2.2.22";
		}  
		
		gv = versionName+"."+versionCode;
		return gv;
	}
	
	
	private String GetRM()
	{
		if(m_back)
		{
			return null;
		}
		String mac = null;
		mac = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
		if(mac == null || mac == "")
		{
			GenieSoap command = new GenieSoap(mContext);
			command.getWLanMACAddress();
			mac = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
		}
		return mac;
	}
	private String GetSN()
	{
		
		return "00";
	}
	private String GetMN()
	{
		if(m_back)
		{
			return null;
		}
		return mMN;
	}
	private String GetFV()
	{
		if(m_back)
		{
			return null;
		}
		return mFV;
	}
	
	private boolean CurrntSetting()
	{
		if(m_back)
		{
			return false;
		}
		mFV = "";
		mMN = "";
		HttpGet request = new HttpGet("http://routerlogin.net/currentsetting.htm");
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,http_timeout);
        HttpConnectionParams.setSoTimeout(httpParams,http_timeout);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        
        try
        {
        	HttpResponse response = httpClient.execute(request);
        	
        	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        	{
        		String recBuffer = EntityUtils.toString(response.getEntity());
        		
        		if(recBuffer.startsWith("Firmware="))
        		{
        			int d = recBuffer.indexOf("=");
        			int r = recBuffer.indexOf("\r");
        			if(r == -1)
        			{
        				r = recBuffer.indexOf("\n");
        			}
        			//mFV = recBuffer.split("-")[0].split("=")[1];
        			mFV = recBuffer.substring(d + 1, r);
        		}
        		int mindex = recBuffer.indexOf("Model=");
        		if(mindex > 0)
        		{
        			String s = recBuffer.substring(mindex);
        			int d = s.indexOf("=");
        			int r = s.indexOf("\r");
        			if(r == -1)
        			{
        				r = s.indexOf("\n");
        			}
        			mMN = s.substring(d + 1, r);
        			//mMN = s.split("\r")[0].split("=")[1];
        		}
        		return true;
        	}
        }
        catch (UnknownHostException e)
        {
        	return false;
        }
        catch (HttpHostConnectException e)
        {
        	return false;
        }
        catch (ConnectTimeoutException e)
        {
        	return false;
        }
        catch (ClientProtocolException e)
        {
        	return false;
        }
        catch (IOException e)
        {
        	return false;
        }
        catch (Exception e)
        {
        	return false;
        }
        return false;
	}
	
	private boolean StatSetup()
	{
		if(m_back)
		{
			return false;
		}
		String url = "http://" + StatServer + "/I?PM=" + mPM + 
		"&D=" + mD +"&T=" + mT + "&OS=" + mOS + "&GV=" + mGV;
		if(!HttpResponse(url))
			return false;
		return true;
	}
	private boolean StatRouteInfo()
	{
		if(m_back)
		{
			return false;
		}
		String url = "http://" + StatServer + "/R?PM=" + mPM + 
		"&RM=" + mRM + "&SN=" + mSN + "&MN=" + mMN + "&FV=" + mFV;
		if(!HttpResponse(url))
			return false;
		return true;
	}
	private boolean HttpResponse(String url)
	{
		if(m_back)
		{
			return false;
		}
		
        try
        {
        	GenieDebug.error("Stat", "Stat send url = "+url);
		
        	HttpGet request = new HttpGet(url);
        	HttpParams httpParams = new BasicHttpParams();
        	HttpConnectionParams.setConnectionTimeout(httpParams,http_timeout);
        	HttpConnectionParams.setSoTimeout(httpParams,http_timeout);
        	HttpClient httpClient = new DefaultHttpClient(httpParams);
        
        
        

        	HttpResponse response = httpClient.execute(request);
        	
        	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        	{
        		String recBuffer = EntityUtils.toString(response.getEntity());
        		
        		if(recBuffer.contains("errorcode"))
        			return false;
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

}
