package com.wifianalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 
import com.filebrowse.FileBrowseTab;

public class GenieWifiScan extends Activity implements OnTouchListener, OnGestureListener{
	private PopupWindow m_MoreDialog = null;
	private WifiScanAdapter m_listItemAdapter = null;
	private ExpandableListView m_scanlist = null;
	public TextView mainText;
	public WifiManager mainWifi;
//	private ProgressDialog progressDialog = null;
	public WifiReceiver receiverWifi;
	public StringBuilder sb = new StringBuilder();
	private TimerTask task = null;
	public List<ScanResult> wifiList = null;
	private ArrayList<ArrayList<ScanResult>> ScanResultList = null;
	private Timer autotimer = null;
	private GestureDetector mGestureDetector;
	private boolean isScanning=false;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				StartScan();
				break;

			default:
				break;
			}
		}; 
	};
	
	private void AutoScanWifi(int paramInt, boolean paramBoolean){
		if(!paramBoolean){
			if(autotimer!=null){
				autotimer.cancel();
				autotimer = null;
				return;
			}
		}else{
			autotimer = new Timer();
			task = new TimerTask(){
				public void run() {
					handler.sendEmptyMessage(0);
				}
			};
			autotimer.schedule(task, paramInt, paramInt);
		}
	}
	
	private void StartScan(){
	    if (this.mainWifi != null){
	    	if(this.mainWifi.isWifiEnabled()){
		    	ShowLoadingDialog();
		    	mainWifi.startScan();
	    	}else{
	    		if(autotimer!=null){
	    			autotimer.cancel();
	    			autotimer=null;
	    			((CheckBox)findViewById(R.id.radioButton1)).setChecked(false);
	    		}
	    		Toast.makeText(GenieWifiScan.this, "Wifi is off!", Toast.LENGTH_SHORT).show();
	    	}
//	    	Intent intent=new Intent("android.net.wifi.SCAN_RESULTS");
//	    	sendBroadcast(intent);
	    }
	      return;
	}
	
	public static int Rssi2signal(int paramInt){
	    return 2 * (paramInt + 100);
	}
	
	private void RefreshList()
	  {
	    if (this.m_listItemAdapter != null){	    	
	    	m_listItemAdapter.notifyDataSetChanged();
	    }
	    return;
	  }
	
	 private void ShowLoadingDialog()
	  {
	    RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(R.id.progress);
	    ((TextView)findViewById(R.id.loading)).setText(getResources().getString(R.string.loading) + "...");
	    localRelativeLayout.setVisibility(View.VISIBLE);
	    isScanning=true;
	  }
	 
	 private void ShowMoreDialog(){
		 Button localButton = (Button)findViewById(R.id.about);
		 View localView = LayoutInflater.from(this).inflate(R.layout.dialog_more, null);
		 ListView localListView = (ListView)localView.findViewById(R.id.list);
		 localListView.setAdapter(new MoreListAdapter(this));
//		 localListView.setItemChecked(0, true);
		 localListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 GenieDebug.error("debug", "onItemClick arg3=" + arg3);
				 GotoMore((int)arg3);
				 if(m_MoreDialog!=null){
					 m_MoreDialog.dismiss();
				 }
			}
			 
		 });
		 m_MoreDialog = new PopupWindow(localView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		 m_MoreDialog.setBackgroundDrawable(new BitmapDrawable());
		 m_MoreDialog.setOnDismissListener(new PopupWindow.OnDismissListener(){
			public void onDismiss() {
				m_MoreDialog=null;
			}
			 
		 });
		 m_MoreDialog.showAsDropDown(localButton);
	 }
	 
	 
	 //将搜索到的无线信号进行排序
	  @SuppressWarnings("unchecked")
	private void SortWifiList(ArrayList<ArrayList<ScanResult>> paramArrayList){
		  if(paramArrayList==null)
			  return;
		  for(int i=0;i<paramArrayList.size();i++){
			  Collections.sort(paramArrayList,new Comparator<Object>() {
				  public int compare(Object lhs, Object rhs) {
					  return ((ScanResult)((ArrayList<ScanResult>)rhs).get(0)).level-((ScanResult)((ArrayList<ScanResult>)lhs).get(0)).level;
				  }
			  });
			  Collections.sort(paramArrayList.get(i), new Comparator<Object>() {
				  public int compare(Object lhs, Object rhs) {
					  return ((ScanResult)rhs).level-((ScanResult)lhs).level;
				  }
			  });
		  }
	  }
	  
	  public static int analysisChannel(int paramInt){
		  
		  int j=0;
		  if (paramInt <= 2400000 || paramInt >= 2484000){
		  
			  if (paramInt >= 2484000 && paramInt <= 2495000)
			  {
				  j = 14;
			  } else
			  {
				  j = 0;
				  if (paramInt > 5000000)
				  {
					  j = 0;
					  if (paramInt < 5900000)
						  j = (paramInt - 5000000) / 5000;
				  }
			  }
			  
		  }else{
			  j = (paramInt - 2407000) / 5000;
		  }
		  return j;
		  
//		 int channel = 0;
//         switch(paramInt/1000){
//	         case 2412:
//	                 channel = 1;
//	                 break;
//	         case 2417:
//	                 channel = 2;
//	                 break;
//	         case 2422:
//	                 channel = 3;
//	                 break;
//	         case 2427:
//	                 channel = 4;
//	                 break;
//	         case 2432:
//	                 channel = 5;
//	                 break;
//	         case 2437:
//	                 channel = 6;
//	                 break;
//	         case 2442:
//	                 channel = 7;
//	                 break;
//	         case 2447:
//	                 channel = 8;
//	                 break;
//	         case 2452:
//	                 channel = 9;
//	                 break;
//	         case 2457:
//	                 channel = 10;
//	                 break;
//	         case 2462:
//	                 channel = 11;
//	                 break;
//	         case 2467:
//	                 channel = 12;
//	                 break;
//	         case 2472:
//	                 channel = 13;
//	                 break;
//	         case 2484:
//	                 channel = 14;
//	                 break;
//         }
//         return channel;
	  }
	 
	 public void GotoMore(int paramInt){
		 Intent intent = new Intent();
		 switch (paramInt) {
		 case 0:
			 break;
		 case 1:
			 intent.setClass(this, GenieChannels.class);
			 startActivity(intent);
			 finish();
			 break;
		 case 2:
			 intent.setClass(this, GenieSignalHistogram.class);
			 startActivity(intent);
			 finish();
			 break;
		 case 3:
			 intent.setClass(this, GenieSignalGraph.class);
			 startActivity(intent);
			 finish();
			 break;
		 case 4:
			 intent.setClass(this, GenieWifiAct3.class);
			 startActivity(intent);
			 finish();
			break;
		 case 5:
			 intent.setClass(this, WifiRoomSignal.class);
			 startActivity(intent);
			 finish();
			 break;
		}
	 }
	 
	  public void InitTitleView(){
		  GenieDebug.error("debug", "InitTitleView 0000");
		  Button but1 = (Button)findViewById(R.id.back);
		  but1.setBackgroundResource(R.drawable.title_bt_bj);
		  Button localButton2 = (Button)findViewById(R.id.about);
		  localButton2.setBackgroundResource(R.drawable.title_more);
		  localButton2.setText(null);
		  but1.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				GenieWifiScan.this.onBackPressed();
			}
		  });
		  localButton2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GenieWifiScan.this.ShowMoreDialog();
			}
		});
		  but1.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.title_bt_fj);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.title_bt_bj);
				}
				return false;
			}
		});
	  }
	  
	  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent){
	    boolean bool = super.dispatchKeyEvent(paramKeyEvent);
	    if ((paramKeyEvent.getAction() == KeyEvent.ACTION_UP) && (paramKeyEvent.getKeyCode() == KeyEvent.FLAG_KEEP_TOUCH_MODE))
	    {
	      RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(R.id.progress);
	      if (localRelativeLayout.getVisibility() != 8)
	      {
	        localRelativeLayout.setVisibility(8);
	        bool = true;
	      }
	    }
	    return bool;
	  }
	 
	  private void CancelLoadingDialog()
	  {
	    RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(R.id.progress);
	    ((TextView)findViewById(R.id.loading)).setText(getResources().getString(R.string.loading) + "...");
	    localRelativeLayout.setVisibility(View.GONE);
	    isScanning=false;
	  }
	  
	public void onConfigurationChanged(Configuration newConfig) {
		GenieDebug.error("debug", "onConfigurationChanged 0");
		super.onConfigurationChanged(newConfig);
		if(m_MoreDialog==null){
			GenieDebug.error("debug", "onConfigurationChanged 1");
			return;
		}
		if(!m_MoreDialog.isShowing()){
			GenieDebug.error("debug", "onConfigurationChanged 2");
			m_MoreDialog.update();
			return;
		}
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display d=getWindowManager().getDefaultDisplay();
		final int woindow_w=d.getWidth();
		final int woindow_h=d.getHeight();
		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			setTheme(R.style.bigactivityTitlebarNoSearch);
		}else{			
			setTheme(R.style.activityTitlebarNoSearch);
		} 
		requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.wifiscan);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_big);
		}else{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		}
		GenieGlobalDefines.SaveWiFiScanDefaultEntry(this, 0);
	    InitTitleView();
	    
	    mGestureDetector = new GestureDetector((OnGestureListener) this);    
	    View viewSnsLayout = this.getWindow().getDecorView();    
        viewSnsLayout.setOnTouchListener(this);    
        viewSnsLayout.setLongClickable(true);   
	    
	    ScanResultList = new ArrayList<ArrayList<ScanResult>>();
	    m_scanlist = ((ExpandableListView)findViewById(R.id.wifiscanlist));
	    receiverWifi = new WifiReceiver();
	    registerReceiver(receiverWifi, new IntentFilter(WIFI_SERVICE));
	    m_listItemAdapter = new WifiScanAdapter(this);
	    m_scanlist.setAdapter(this.m_listItemAdapter);
	    m_scanlist.setGroupIndicator(null);
	    mainWifi = ((WifiManager)getSystemService(WIFI_SERVICE));
	    WifiInfo info=mainWifi.getConnectionInfo();
	    ((TextView)findViewById(R.id.connectstatus)).setText(getResources().getString(R.string.s_conectstatus) + " " + (info.getSSID()==null?"":info.getSSID()));
	    //不自动弹出输入法
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	    //扫描WIFI
	    StartScan();
	    ((Button)findViewById(R.id.nowscan)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg0) {
				StartScan();
			}
	    });
	    ((CheckBox)findViewById(R.id.radioButton1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				String str = ((EditText)GenieWifiScan.this.findViewById(R.id.autoscantime)).getText().toString();
				int i = 5000;
				int j = Integer.valueOf(str).intValue();
				i = j*1000;
				AutoScanWifi(i, arg1);
			}
	    	
	    });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(isScanning){
				CancelLoadingDialog();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onDestroy() {
		if(autotimer!=null){
			autotimer.cancel();
			autotimer=null;
		}		
		super.onDestroy();
	}
	
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiverWifi);
	}
	
	protected void onResume() {
		super.onResume();
		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	
	private class MoreListAdapter extends BaseAdapter{
		private LayoutInflater mInflater;

		public MoreListAdapter(Context arg2){
		    this.mInflater = LayoutInflater.from(arg2);
		}
		public int getCount() {
			return 6;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(convertView==null){
				convertView = this.mInflater.inflate(R.layout.dialog_more_item, null);
				viewHolder = new ViewHolder();
				viewHolder.text = ((TextView)convertView.findViewById(R.id.text));
		        convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder)convertView.getTag();
			}
			switch (position) {
			case 0:
				viewHolder.text.setText(GenieWifiScan.this.getResources().getString(R.string.s_wifi_more_1).toString());
				break;
			case 1:
				viewHolder.text.setText(GenieWifiScan.this.getResources().getString(R.string.s_wifi_more_2).toString());
				break;
			case 2:
				viewHolder.text.setText(GenieWifiScan.this.getResources().getString(R.string.s_wifi_more_3).toString());
				break;
			case 3:
				viewHolder.text.setText(GenieWifiScan.this.getResources().getString(R.string.s_wifi_more_4).toString());
				break;
			case 4:
				viewHolder.text.setText(GenieWifiScan.this.getResources().getString(R.string.s_wifi_more_5).toString());
				break;
			case 5:
				viewHolder.text.setText(GenieWifiScan.this.getResources().getString(R.string.s_wifi_more_6).toString());
				break;
			}
			return convertView;
		}
		class ViewHolder{
	      TextView text;
	      ViewHolder(){
	      }
	    }
	}
	
	class WifiReceiver extends BroadcastReceiver{
		WifiReceiver(){
			
		}
		public void onReceive(Context context, Intent intent) {
			GenieWifiScan.this.wifiList = GenieWifiScan.this.mainWifi.getScanResults();
		    GenieWifiScan.this.CancelLoadingDialog();
		    WifiInfo info=GenieWifiScan.this.mainWifi.getConnectionInfo();
		    if((GenieWifiScan.this.ScanResultList == null) || (GenieWifiScan.this.wifiList != null)){
		    	GenieWifiScan.this.ScanResultList.clear();
		    	Iterator<ScanResult> iterator = GenieWifiScan.this.wifiList.iterator();
		    	while(iterator.hasNext()){
		            ScanResult  scanResult1 = (ScanResult)iterator.next();
		            if(GenieWifiScan.this.ScanResultList.size()>0){
		            	int i=0;
			            for (int j = 0; j < GenieWifiScan.this.ScanResultList.size(); j++) {
			            		
		            		ScanResult scanResult2 = (ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(j)).get(0);		    			
		            		if(scanResult1.SSID.equals(scanResult2.SSID)){
		            			((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(j)).add(scanResult1);
		            			i=1;
		            		}
			            	
			            }
			            if(i==0){
			            	ArrayList<ScanResult> list = new ArrayList<ScanResult>();
		            		list.add(scanResult1);
		            		GenieWifiScan.this.ScanResultList.add(list);
			            }
		            }else{
		            	ArrayList<ScanResult> list = new ArrayList<ScanResult>();
	            		list.add(scanResult1);
	            		GenieWifiScan.this.ScanResultList.add(list);
		            }
		    	}
		    	
		    	((TextView)findViewById(R.id.connectstatus)).setText(getResources().getString(R.string.s_conectstatus) + " " + (info.getSSID()==null?"":info.getSSID()));
		    	GenieWifiScan.this.SortWifiList(GenieWifiScan.this.ScanResultList);
	            GenieWifiScan.this.RefreshList();
		    }
		}
		
	}
	
	 class WifiScanAdapter extends BaseExpandableListAdapter{
		 Context context;
		 private Bitmap mIcon1;
		 private Bitmap mIcon2;
		 private Bitmap mIcon3;
		 LayoutInflater mlayoutInflater;
		 
		 WifiScanAdapter(Context context){
			this.context = context;
		    this.mlayoutInflater = LayoutInflater.from(context);
		    this.mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.expander_maximized);
		    this.mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.expander_minimized);
		 }
		 
		public Object getChild(int arg0, int arg1) {
			 return ((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(arg0)).get(arg1);
		}

		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		public View getChildView(int paramInt1, int paramInt2, boolean arg2, View arg3,ViewGroup arg4) {
			ViewHolder localViewHolder;
			if(arg3==null){
				arg3 = this.mlayoutInflater.inflate(R.layout.list_item_wifiscan_result2, null);
				arg3.setBackgroundResource(R.drawable.more_item_s);
				localViewHolder = new ViewHolder();
		        localViewHolder.text = ((TextView)arg3.findViewById(R.id.text01));
		        localViewHolder.channel = ((TextView)arg3.findViewById(R.id.channel));
		        localViewHolder.ssid = ((TextView)arg3.findViewById(R.id.ssid));
		        localViewHolder.level = ((TextView)arg3.findViewById(R.id.level));
		        localViewHolder.bssid = ((TextView)arg3.findViewById(R.id.bssid));
		        localViewHolder.wep = ((TextView)arg3.findViewById(R.id.textwep));
		        localViewHolder.signal = ((ImageView)arg3.findViewById(R.id.signal));
		        localViewHolder.probar = ((ProgressBar)arg3.findViewById(R.id.progressBar1));
		        arg3.setTag(localViewHolder);
			}else{
				localViewHolder = (ViewHolder)arg3.getTag();
			}
			
		    int i = WifiManager.calculateSignalLevel(((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).level, 4);
		    if ((((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(0)).capabilities != null) && (((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(0)).capabilities.length() > 0)){
		    	this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), 2130837789);
		    }
	    	//text
			localViewHolder.text.setText("Ch");
			//ssid
			localViewHolder.ssid.setText(((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).SSID);
			//channel
	 		TextView localTextView1 = localViewHolder.channel;
	 		Object[] arrayOfObject1 = new Object[1];
	 		arrayOfObject1[0] = Integer.valueOf(GenieWifiScan.analysisChannel(1000 * ((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).frequency));
      		localTextView1.setText(String.format("%2d", arrayOfObject1));
      		//level
      		localViewHolder.level.setText(((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).level + "dBm");
      		//bssid
      		TextView localTextView2 = localViewHolder.bssid;
            Object[] arrayOfObject2 = new Object[1];
            arrayOfObject2[0] = ((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).BSSID;
            localTextView2.setText(String.format("(%s)", arrayOfObject2));
            //wep
            boolean isLock=false;
            if ((((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).capabilities != null) && (((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).capabilities.length() > 0)){
            	localViewHolder.wep.setText("WPA2");
            	localViewHolder.wep.setTextColor(Color.RED);
            	isLock=true;
            }else{
            	localViewHolder.wep.setText("OPEN");
            	localViewHolder.wep.setTextColor(Color.GREEN);
            	isLock=false;
            }
            //signal
            this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43);
            switch (i){
				 case 0:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_13s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_13);
					 }
					 break;
				 }
				 case 1:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_23s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_23);
					 }
					 break;
				 }
				 case 2:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_33s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_33);
					 }
					 break;
				 }
				 case 3:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43);
					 }
					 break;
				 }
				 default:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43);
					 }
					 break;
				 }
            }
            localViewHolder.signal.setImageBitmap(this.mIcon3);
            //probar
            if(((ScanResult)(ScanResultList.get(paramInt1)).get(paramInt2)).level > -40){
            	localViewHolder.probar.setProgress(60);
            	
            }else if (((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).level < -100){
                localViewHolder.probar.setProgress(0);
            }else{
            	localViewHolder.probar.setProgress(100 + ((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt1)).get(paramInt2)).level);
            }
		    
            
			return arg3;
		}

		public int getChildrenCount(int arg0) {
			int j;
			if ((ScanResultList.get(arg0)).size() <= 1)
				j = 0;
			else
				j = (ScanResultList.get(arg0)).size();
			return j;
		}

		public Object getGroup(int arg0) {
			return GenieWifiScan.this.ScanResultList.get(arg0);
		}

		public int getGroupCount() {
			return GenieWifiScan.this.ScanResultList.size();
		}

		public long getGroupId(int arg0) {
			return arg0;
		}

		public View getGroupView(int paramInt, boolean paramBoolean, View arg2,
				ViewGroup arg3) {
			ViewHolderGroup localViewHolderGroup;
			if (arg2 == null){
		        arg2 = this.mlayoutInflater.inflate(R.layout.list_item_wifiscan_result, null);
		        localViewHolderGroup = new ViewHolderGroup();
		        localViewHolderGroup.text = ((TextView)arg2.findViewById(R.id.text01));
		        localViewHolderGroup.channel = ((TextView)arg2.findViewById(R.id.channel));
		        localViewHolderGroup.ssid = ((TextView)arg2.findViewById(R.id.ssid));
		        localViewHolderGroup.level = ((TextView)arg2.findViewById(R.id.level));
		        localViewHolderGroup.bssid = ((TextView)arg2.findViewById(R.id.bssid));
		        localViewHolderGroup.wep = ((TextView)arg2.findViewById(R.id.textwep));
		        localViewHolderGroup.signal = ((ImageView)arg2.findViewById(R.id.signal));
		        localViewHolderGroup.arrow = ((ImageView)arg2.findViewById(R.id.arrow));
		        localViewHolderGroup.probar = ((ProgressBar)arg2.findViewById(R.id.progressBar1));
		        arg2.setTag(localViewHolderGroup);
		      }else{
		    	  localViewHolderGroup=(ViewHolderGroup)arg2.getTag();
		      }
			 int i = WifiManager.calculateSignalLevel(((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).level, 4);
			 
			 //text
			 localViewHolderGroup.text.setText("Ch");
			 //channel
			 TextView localTextView1 = localViewHolderGroup.channel;
			 if (((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).size() <= 1){
		         Object[] arrayOfObject1 = new Object[1];
		         arrayOfObject1[0] = Integer.valueOf(GenieWifiScan.analysisChannel(1000 * ((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).frequency));
		         localTextView1.setText(String.format("%2d", arrayOfObject1));
			 }else{
				 localTextView1.setText("...");
			 }
			 //ssid
	         localViewHolderGroup.ssid.setText(((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).SSID);
	         //level
	         localViewHolderGroup.level.setText(((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).level + "dBm");
	         //bssid
	         TextView localTextView2 = localViewHolderGroup.bssid;
	         if (((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).size() <= 1){
		         Object[] arrayOfObject2 = new Object[1];
		         arrayOfObject2[0] = ((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).BSSID;
		         localTextView2.setText(String.format("(%s)", arrayOfObject2));
	         }else{
	        	 localTextView2.setText("(...)");
	         }
			 //wep
	         boolean isLock=false;
	         if ((((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).capabilities != null) && (((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).capabilities.length() > 0)){
	        	 localViewHolderGroup.wep.setText("WPA2");
	        	 localViewHolderGroup.wep.setTextColor(Color.RED);
	        	 isLock=true;
	         }else{
	        	 localViewHolderGroup.wep.setText("OPEN");
	        	 localViewHolderGroup.wep.setTextColor(Color.GREEN);
	        	 isLock=false;
	         }
	         //signal
			 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43);
			 switch (i){
				 case 0:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_13s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_13);
					 }
					 break;
				 }
				 case 1:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_23s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_23);
					 }
					 break;
				 }
				 case 2:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_33s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_33);
					 }
					 break;
				 }
				 case 3:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43);
					 }
					 break;
				 }
				 default:{
					 if(isLock){
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43s);
					 }else{
						 this.mIcon3 = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.signal_43);
					 }
					 break;
				 }
			 }
			 localViewHolderGroup.signal.setImageBitmap(this.mIcon3);
			 
			 //arrow
		     if (((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).size() <= 1){
		    	 localViewHolderGroup.arrow.setVisibility(View.GONE);
		     }else{
		    	 localViewHolderGroup.arrow.setVisibility(View.VISIBLE);
		     }
	          if (!paramBoolean){
	        	  localViewHolderGroup.arrow.setImageBitmap(this.mIcon2);
	          }else{
	        	  localViewHolderGroup.arrow.setImageBitmap(this.mIcon1);
	          }
			 //probar
	         if (((ScanResult)(ScanResultList.get(paramInt)).get(0)).level > -40){
	        	 localViewHolderGroup.probar.setProgress(60);
		     }else if (((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).level < -100){
	             localViewHolderGroup.probar.setProgress(0);
	    	 }else{
	    		 localViewHolderGroup.probar.setProgress(100 + ((ScanResult)((ArrayList<ScanResult>)GenieWifiScan.this.ScanResultList.get(paramInt)).get(0)).level);
	    	 }
			
			return arg2;
		}

		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int groupInt, int childInt) {
			
	    	return true;
		}
		
	    class ViewHolder
	    {
	      TextView bssid;
	      TextView ch;
	      TextView channel;
	      TextView level;
	      ProgressBar probar;
	      ImageView signal;
	      TextView ssid;
	      TextView text;
	      TextView wep;

	      ViewHolder()
	      {
	      }
	    }

	    class ViewHolderGroup
	    {
	      ImageView arrow;
	      TextView bssid;
	      TextView ch;
	      TextView channel;
	      TextView level;
	      ProgressBar probar;
	      ImageView signal;
	      TextView ssid;
	      TextView text;
	      TextView wep;

	      ViewHolderGroup()
	      {
	      }
	    }
		 
	 }

	 
	private int verticalMinDistance = 60;  
	private int minVelocity         = 0;  
	 
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		if (Math.abs(e1.getX() - e2.getX() )> Math.abs(e1.getY()-e2.getY())&& (e1.getX()-e2.getX())<0 && Math.abs(velocityX) > minVelocity && (e2.getX()-e1.getX())>verticalMinDistance) {  
			Intent intent=new Intent(GenieWifiScan.this,WifiRoomSignal.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			finish();
		    
		} else if (Math.abs(e2.getX() - e1.getX() )>Math.abs( e2.getY()-e1.getY()) && (e1.getX()-e2.getX())>0 && Math.abs(velocityX) > minVelocity && (e1.getX()-e2.getX())>verticalMinDistance) {  
	    	Intent intent=new Intent(GenieWifiScan.this,GenieChannels.class);
	    	startActivity(intent);
	    	GenieWifiScan.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	    	finish();
	    }
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {  
	    mGestureDetector.onTouchEvent(ev);  
	    return super.dispatchTouchEvent(ev);  
	}  
}
