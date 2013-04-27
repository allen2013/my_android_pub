package com.dragonflow.genie.busi.process;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.dragonflow.GenieRequest;
import com.dragonflow.GenieSmartRouterInfo;
import com.dragonflow.util.FileUtils;

/**
 * <p>
 * Title: TestTask
 * </p>
 * <p>
 * Description:���Ե�½������
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: ��������
 * </p>
 * 
 * @author <a href='mailto:allen_lhl@sina.cn'>allen </a>
 * @version 2.0
 * @since 2013-04-09
 */
public class TestTask 
{

	private static Timer loginTimer = null;
	private static TimerTask loginTask = null;
	
	private static int interval = 60; //���ʱ�䣺��
	private static String loginUser = "admin";
	private static String loginPsw = "password";
	
	//��½�ɹ���ʧ�ܴ���
	private static int loginSuccess = 0;
	private static int loginFailure = 0;
	public static int loginTimes = 1;
	private static boolean isRunning = false;
	private static Handler mHandler;
	//�ļ���ַ
	private static String fileName = null;
	private static String loginResult = "";
	
	private static String routerName = null;
	private static boolean isNeedProcInfo = true; //�Ƿ���Ҫ������Ϣ 
	
	public static void initLoginTask(String userName, String password, int xinterval, Handler mHandlerx)
	{
		if(isRunning)
			return;
		loginUser = userName;
		loginPsw = password;
		interval = xinterval > 20 ? xinterval : 20; //������20
		mHandler = mHandlerx; 
		
		fileName = Environment.getExternalStorageDirectory().getPath() + "/genie_login_test.txt"; 
		loginTask = new TimerTask()
		{
			@Override
            public void run()
            { 
				//֪ͨ��Ϣ
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg); //����֪ͨ��Ϣ
				
				loginTimes ++;
            }			
		};
		loginTimer = new Timer();
		loginTimer.schedule(loginTask, 10 * 1000, interval * 1000);
		isRunning = true;
		
		StringBuffer bufLog = new StringBuffer("Now start login test, ");
		bufLog.append("Test interval" + interval);
		
		saveLoginInfo(bufLog.toString());
	}
	
	/**
	 * ֹͣ��½�����߳�
	 */
	public static void stopLoginTask()
	{
		//loginTask.cancel();
		if(loginTimer != null)
		{
			loginTimes ++;
			loginTimer.cancel();
			loginTimer = null;
			isRunning = false;
		}
	}
	
	public static void resetAll()
	{
		stopLoginTask();
		loginTimes = 1;
		loginSuccess = 0;
		loginFailure = 0;
		loginResult = "";
		routerName = null;
		if(fileName != null)
		{
			try
			{
				FileUtils.writeSdcardFile(fileName, "", false);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	
	public static void setRouterName(String response)
	{
		if(response != null)
		{
			int pos = response.indexOf("Model=");
			if(pos != -1)
			{
				int endPos = response.indexOf("\n", pos);
				if(endPos != -1)
				{
					routerName = response.substring(pos + 6, endPos-1).replace("\t", "");
				}
			}
		}
	} 
	
	/**
	 * �����½���
	 * @param isSucess
	 * @param loginInfo
	 */
	public static void saveLoginResult(boolean isSucess, String loginInfo)
	{
		if(isSucess)
		{
			loginSuccess ++;
		}
		else
		{
			loginFailure ++;
		}
		loginResult = "login times:" + loginTimes + ". success:" + loginSuccess + "; faile:" + loginFailure
        		+ ". curr login info:"+ loginInfo;
		if(loginInfo != null)
		{
			try
           {
	            FileUtils.writeSdcardFile(fileName, loginResult + "\n", true);
           }
           catch (IOException e)
           {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
           }
		}
	}
	
	/**
	 * ��¼��½�����̣���Ϣ
	 * @param loginInfo
	 */
	public static void saveLoginInfo(String loginInfo)
	{
		if(isNeedProcInfo && loginInfo != null)
		{
			try
           {
	            FileUtils.writeSdcardFile(fileName, "login times:" + loginTimes + ". procedure info:" + loginInfo + "\n", true);
           }
           catch (IOException e)
           {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
           }
		}
	}
	
	public static String getLoginResult()
	{
		return routerName == null ? "" : routerName + ": " + loginResult;
	}
}
