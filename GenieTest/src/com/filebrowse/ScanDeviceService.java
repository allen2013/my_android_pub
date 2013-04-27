package com.filebrowse;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import ros.zeroconf.jmdns.DiscoveredService;
import ros.zeroconf.jmdns.Logger;
import ros.zeroconf.jmdns.Zeroconf;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.FileTransfer;
import com.dragonflow.GenieDebug;
import com.dragonflow.GenieMap;
import com.dragonflow.genie.ui.R; 
import com.turbo.Turbo_DevicelistActivity;

public class ScanDeviceService extends Service {

	private Timer autotime = null;
	private Zeroconf zeroconf;
	public final static String SCANDEVICE="SCAN_DEVICE";
	public static Set<String> discovered_services=new HashSet<String>();
	public static HashMap<String,String> turbo_map=new HashMap<String,String>();
	public static boolean scaning=true;
	public static String SERVICETYPE="_ros-master._tcp";
	public static ScanDeviceService scanDevice=null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		System.out.println("停止文件传输服务");
		zeroconf.removeListener(SERVICETYPE,"local");
		zeroconf.removeAllServices();
		this.scanDevice=null;
	}

	@Override
	public boolean stopService(Intent name) {
		if(autotime!=null)
		autotime.cancel();
		zeroconf.removeListener(SERVICETYPE,"local");
		zeroconf.removeAllServices();
		this.scanDevice=null;
		return super.stopService(name);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		System.out.println("扫描设备服务开始");
		this.scanDevice=this;
		discovered_services.clear();
		Logger logger = new Logger();
		zeroconf = new Zeroconf(logger);
		String imei = getDevicename();
		if(imei==null||"".equals(imei)){
			imei="android_name";
		}
		zeroconf.addService(imei, SERVICETYPE, "local", 8888, "Dude's test master");
		zeroconf.addListener(SERVICETYPE, "local");
//		autotime = new Timer();
//		autotime.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				if(discovered_services!=null){
//					discovered_services=null;
//				}
//				discovered_services = zeroconf
//						.listDiscoveredServices();
//				if (discovered_services.size() > 0) {
//					if(scaning){
//						scaning=false;
//						Intent mIntent = new Intent(GenieMap.SCANDEVICE); 
//		                //发送广播 
//		                sendBroadcast(mIntent); 
//		               
//					}
//				}
//			}
//		}, 0, 5000);

	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	public void sendMessage(){
		Intent mIntent = new Intent(GenieMap.SCANDEVICE); 
        //发送广播 
        sendBroadcast(mIntent); 
        
        Intent mIntent1 = new Intent(Turbo_DevicelistActivity.TurboFile); 
        //发送广播 
        sendBroadcast(mIntent1); 
	}

	
	public void findservice(){
	
		if(zeroconf!=null){
			 zeroconf.findallservice();
		}
	}
	
	 
	public String getDevicename(){
		int version = 3;
        String manufacturer = "";
        String device = "";
        try {
                Class<android.os.Build.VERSION> build_version_class = android.os.Build.VERSION.class;
                Field field = build_version_class.getField("SDK_INT");
                version = (Integer) field.get(new android.os.Build.VERSION());

                Class<android.os.Build> build_class = android.os.Build.class;
                Field manu_field = build_class.getField("MANUFACTURER");
                manufacturer = (String) manu_field.get(new android.os.Build());
                

                Field device_field = build_class.getField("DEVICE");
                device = (String) device_field.get(new android.os.Build());
        } catch (NoSuchFieldException e) {
                version = 3;
        } catch (Exception e) {
                version = 3;
        }
        return device;
	}
	
}
