package com.dragonflow.genie.ui;

import java.util.ArrayList;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.GenieMainView;
import com.dragonflow.GenieRequest;
import com.dragonflow.GenieRequestInfo; 
import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;

public class AGenieSplash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		//getWindow().setWindowAnimations(R.anim.menushow);
		
		GenieRequest.m_First = true;
		GenieRequest.m_SmartNetWork = false;
		
		new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent intent = new Intent(AGenieSplash.this,GenieMainView.class);
                startActivity(intent);
                AGenieSplash.this.finish();
            }
                        
        }, 1500);
	}
	
	
	public void CheckAvailabelPort()
    {
    	GenieDebug.error("debug", " 99999 getGuestAccessEnabledInfo --0--");
    	
    	ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
    	
    	GenieRequestInfo test1 = new GenieRequestInfo();
    	test1.aRequestLable="GenieSplash";
    	test1.aSoapType = GenieGlobalDefines.checkAvailabelPort;
    	test1.aServer = "DeviceInfo";
    	test1.aMethod = "GetInfo";
    	test1.aNeedwrap = false;
    	test1.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test1.aNeedParser=false;
    	test1.aTimeout = 20000;
    	test1.aElement = new ArrayList<String>();
    	requestinfo.add(test1);
    	
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieSplash";
    	test2.aSoapType = GenieGlobalDefines.ESoapRequestGuestEnable;
    	test2.aServer = "WLANConfiguration";
    	test2.aMethod = "GetGuestAccessEnabled";
    	test2.aNeedwrap = false;
    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test2.aNeedParser=true;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();
    	test2.aElement.add("NewGuestAccessEnabled");
    	test2.aElement.add("null");
    	
    	
    	requestinfo.add(test2);

    	GenieRequest check = new GenieRequest(this,requestinfo);
    	check.SetProgressInfo(false, false);
    	check.Start();
    }
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
	 GenieDebug.error("onConfigurationChanged", "onConfigurationChanged !!!!!!!!!");
	 GenieDebug.error("onConfigurationChanged", "newConfig.orientation = "+newConfig.orientation);
	 
	 
	 setContentView(R.layout.splash); 

	 super.onConfigurationChanged(newConfig);
	}

}
