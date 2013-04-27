package com.dragonflow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GenieShareAPI 
{
	final public static void strncpy(char[]des,char[]src,int start,int len)
	{
		for(int i = 0; i < len; i++)
		{
			if(src[start+i] == 0)
			{
				des[39] = (char)i;
				break;
			}
			
			des[i] = src[start+i];
		}
	}
	
	final public static GenieUserInfo ReadDataFromFile(Context context,String filename)
	{
		FileInputStream fIn = null;       
		InputStreamReader isr = null;         
		GenieUserInfo userinfo = null;
		
		try
		{
			fIn = context.openFileInput(filename);                 
			isr = new InputStreamReader(fIn);    

			char[] inputBuffer = new char[512];
			userinfo = new GenieUserInfo();
			
			isr.read(inputBuffer);       
			userinfo.starttime = inputBuffer[0];  
			userinfo.worktime = inputBuffer[1];
			userinfo.isSave = inputBuffer[2];
			strncpy(userinfo.username,inputBuffer,3,40);
			strncpy(userinfo.password,inputBuffer,3 + 40,40);
			strncpy(userinfo.routerMac,inputBuffer,3 + 80,40);
			strncpy(userinfo.PCUsername,inputBuffer,3 + 120,40);
			strncpy(userinfo.PCPassword,inputBuffer,3 + 160,40);
			strncpy(userinfo.PCDeviceID,inputBuffer,3 + 200,40);
			strncpy(userinfo.PCLoginToken,inputBuffer,3 + 240,40);
			strncpy(userinfo.TimeMac,inputBuffer,3 + 280,40);
			strncpy(userinfo.start_time,inputBuffer,3 + 320,40);
			strncpy(userinfo.disabletime,inputBuffer,3 + 360,40);
			
			isr.close();
			fIn.close();
		}
		catch (Exception e)
		{ 
			userinfo = new GenieUserInfo();
			userinfo.isSave = '1';
			
			strncpy(userinfo.username,"admin".toCharArray(),0,5);
			strncpy(userinfo.password,"password".toCharArray(),0,8);
			GenieDebug.error("ReadDataFromFile", "GenieSoap.KEY_NULL.toCharArray().length = "+GenieSoap.KEY_NULL.toCharArray().length);
			strncpy(userinfo.routerMac,GenieSoap.KEY_NULL.toCharArray(),0,GenieSoap.KEY_NULL.toCharArray().length);
			strncpy(userinfo.PCUsername,GenieSoap.KEY_NULL.toCharArray(),0,GenieSoap.KEY_NULL.toCharArray().length);
			strncpy(userinfo.PCPassword,GenieSoap.KEY_NULL.toCharArray(),0,GenieSoap.KEY_NULL.toCharArray().length);
			strncpy(userinfo.PCDeviceID,GenieSoap.KEY_NULL.toCharArray(),0,GenieSoap.KEY_NULL.toCharArray().length);
			strncpy(userinfo.PCLoginToken,GenieSoap.KEY_NULL.toCharArray(),0,GenieSoap.KEY_NULL.toCharArray().length);
			strncpy(userinfo.TimeMac,GenieSoap.KEY_NULL.toCharArray(),0,GenieSoap.KEY_NULL.toCharArray().length);
			strncpy(userinfo.start_time,GenieSoap.KEY_NULL.toCharArray(),0,GenieSoap.KEY_NULL.toCharArray().length);
			strncpy(userinfo.disabletime,GenieSoap.KEY_NULL.toCharArray(),0,GenieSoap.KEY_NULL.toCharArray().length);
			
			WriteData2File(userinfo,context,filename);
		}                    
		
		return userinfo;
	}
	
	final public static void WriteData2File(GenieUserInfo data,Context context,String filename)
	{
		FileOutputStream fOut = null;      
		OutputStreamWriter osw = null;      
		
		try
		{
			fOut = context.openFileOutput(filename,Context.MODE_WORLD_WRITEABLE);                  
			osw = new OutputStreamWriter(fOut);         
			
			osw.write(data.starttime); 
			osw.write(data.worktime); 
			osw.write(data.isSave); 
			osw.write(data.username,0,40); 
			osw.write(data.password,0,40); 
			osw.write(data.routerMac,0,40); 
			osw.write(data.PCUsername,0,40); 
			osw.write(data.PCPassword,0,40); 
			osw.write(data.PCDeviceID,0,40); 
			osw.write(data.PCLoginToken,0,40);
			osw.write(data.TimeMac,0,40); 
			osw.write(data.start_time,0,40); 
			osw.write(data.disabletime,0,40);
			
			osw.flush();  
			osw.close();
			fOut.close();                     
		}    
		catch (Exception e) 
		{
			e.printStackTrace();  
		}                     
	}

}
