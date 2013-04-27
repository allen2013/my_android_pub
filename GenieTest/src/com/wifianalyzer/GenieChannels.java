package com.wifianalyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 

public class GenieChannels extends Activity implements OnTouchListener, OnGestureListener{
	private int m_ChannelMaxSSID = 0;
	private ListView m_Channellist = null;
	private int m_MaxChannel = 0;
	private PopupWindow m_MoreDialog = null;
	private List<ArrayList<wifichannel>> m_channelarray = new ArrayList<ArrayList<wifichannel>>();
	private WifiInfo m_conectwifiinfo = null;
	private wifichannel m_currentwifi = new wifichannel();
	private ChannelAdapter m_listItemAdapter = null;
	public WifiManager mainWifi;
	private ProgressDialog progressDialog = null;
	public WifiReceiver receiverWifi;
	public List<ScanResult> wifiList = null;
	private GestureDetector mGestureDetector;
	private boolean isScanning=false;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				StartScan();
				break;
			}
		}
	};
	
	private void StartScan()
	{
	    if (this.mainWifi != null){
	    	if(this.mainWifi.isWifiEnabled()){
		    	ShowLoadingDialog();
		    	this.mainWifi.startScan();
	    	}else{
	    		Toast.makeText(GenieChannels.this, "Wifi is off!", Toast.LENGTH_SHORT).show();
	    	}
	    }
	}

	private void ShowLoadingDialog() {
		RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(R.id.progress2);
	    ((TextView)findViewById(R.id.loading)).setText(getResources().getString(R.string.loading) + "...");
	    localRelativeLayout.setVisibility(View.VISIBLE);
	    isScanning=true;
	}
	
	private void CancelLoadingDialog(){
	    RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(R.id.progress2);
	    ((TextView)findViewById(R.id.loading)).setText(getResources().getString(R.string.loading) + "...");
	    localRelativeLayout.setVisibility(View.GONE);
	    isScanning=false;
	}	
	  
	 public void GotoMore(int paramInt){
		 Intent intent = new Intent();
		 switch (paramInt) {
		 case 1:
			 break;
		 case 0:
			 intent.setClass(this, GenieWifiScan.class);
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
		 case 5:{
			 intent.setClass(this, WifiRoomSignal.class);
			 startActivity(intent);
			 finish();
			 break;
		 }
		}
	 }
	 
	 public void InitTitleView(){
		 GenieDebug.error("debug", "InitTitleView 0000");
		 Button button1 = (Button)findViewById(R.id.back);
		 button1.setBackgroundResource(R.drawable.title_bt_bj);
		 Button button2 = (Button)findViewById(R.id.about);
		 button2.setBackgroundResource(R.drawable.title_more);
		 button2.setText(null);
		 button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		 button2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ShowMoreDialog();
			}
		});
		 button1.setOnTouchListener(new OnTouchListener() {
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
	 
	 private void RefreshView(){
		
		 int currentChannel=0;
		 m_ChannelMaxSSID=0;
		 StringBuffer buffer=new StringBuffer();
		 int recomendenNum=0;
		 for(int j=0;j<this.m_channelarray.size();j++){
			 ArrayList<wifichannel> wifi_list=this.m_channelarray.get(j);
			 if(wifi_list.size()>m_ChannelMaxSSID){
				 m_ChannelMaxSSID=wifi_list.size();
			 }
			 if((wifi_list==null || (wifi_list!=null&&wifi_list.size()==0)) && recomendenNum<14){
				 if(recomendenNum==13){
					 buffer.append("...");
				 }else{
					 buffer.append(j+1).append(",");
				 }
				 recomendenNum++;
			 }
		 }
		 if(this.m_conectwifiinfo!=null){
			 ((TextView)this.findViewById(R.id.ssid_name)).setText(this.m_conectwifiinfo.getSSID());
			 ((TextView)this.findViewById(R.id.current_ch)).setText(m_currentwifi.channel<=0?"":String.valueOf(this.m_currentwifi.channel));
			 currentChannel=this.m_currentwifi.channel;
			 if(currentChannel>0){
				 int i=this.m_channelarray.get(currentChannel-1).size();
				 ((ProgressBar)this.findViewById(R.id.rating_bar)).setMax(10);
				 ((ProgressBar)this.findViewById(R.id.rating_bar)).setProgress(i>10?10:(i<0?0:i));
				 ((TextView)this.findViewById(R.id.rating_number)).setText(String.valueOf(i));
				 TextView tv_ratingstr= ((TextView)this.findViewById(R.id.rating_string));
				 if (i > 10){
		            tv_ratingstr.setText(getResources().getString(R.string.s_label_poor));
		            tv_ratingstr.setTextColor(getResources().getColor(R.color.limegreen));
		        }else if(i>=0){
		        	
		        	int k = m_ChannelMaxSSID / 2;
		        	if (m_ChannelMaxSSID % 2 > 0){
						k++;
		        	}
		        	if (i != 0){
			        	if (i == m_ChannelMaxSSID)
						{
			        		tv_ratingstr.setText(getResources().getString(R.string.s_label_poor));
			        		tv_ratingstr.setTextColor(Color.RED);
						} else if (i == k)
						{
							tv_ratingstr.setText(getResources().getString(R.string.s_label_average));
							tv_ratingstr.setTextColor(GenieColorList.ColorList[2]);
						} else if (i > k && i < m_ChannelMaxSSID)
						{
							tv_ratingstr.setText(getResources().getString(R.string.s_label_good));
							tv_ratingstr.setTextColor(GenieColorList.ColorList[1]);
						} else if (i > 0 && i < k)
						{
							tv_ratingstr.setText(getResources().getString(R.string.s_label_great));
							tv_ratingstr.setTextColor(GenieColorList.ColorList[11]);
						}
		        	}else{
		        		tv_ratingstr.setText(getResources().getString(R.string.s_label_best));
		        		tv_ratingstr.setTextColor(GenieColorList.ColorList[3]);
		        	}
		        	
		        }else{
		        	tv_ratingstr.setText(getResources().getString(R.string.s_label_best));
	        		tv_ratingstr.setTextColor(Color.GREEN);
		        }
			        	
//				 if(i==0){
//	        		tv_ratingstr.setText(R.string.s_label_poor);
//	        		tv_ratingstr.setTextColor(Color.RED);
//	        	}else if(i>0&&i<5){
//	        		tv_ratingstr.setText(R.string.s_label_good);
//	        		tv_ratingstr.setTextColor(Color.GRAY);
//	        	}else if(i==5){
//	        		tv_ratingstr.setText(R.string.s_label_average);
//	        		tv_ratingstr.setTextColor(Color.YELLOW);
//	        	}else if(i>5&&i<=8){
//	        		tv_ratingstr.setText(R.string.s_label_great);
//	        		tv_ratingstr.setTextColor(Color.BLUE);
//	        	}else{
//	        		tv_ratingstr.setText(R.string.s_label_best);
//	        		tv_ratingstr.setTextColor(Color.GREEN);
//	        	}
			 }
		 }
		
		 if(buffer.toString().endsWith(",")){
			 buffer=buffer.replace(buffer.length()-1, buffer.length(), "");
		 }
	 	((TextView)this.findViewById(R.id.recomended_string)).setText(buffer.toString());
		 
	 }
	 
	 private void ShowMoreDialog(){
		 Button localButton = (Button)findViewById(R.id.about);
		 View localView = LayoutInflater.from(this).inflate(R.layout.dialog_more, null);
		 ListView localListView = (ListView)localView.findViewById(R.id.list);
		 localListView.setAdapter(new MoreListAdapter(this));
//		 localListView.setItemChecked(-1, true);
		 localListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 GenieDebug.error("debug", "onItemClick arg3=" + arg3);
				 GenieChannels.this.GotoMore((int)arg3);
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
	 
	 public boolean dispatchKeyEvent(KeyEvent paramKeyEvent){
	    boolean bool = super.dispatchKeyEvent(paramKeyEvent);
	    if ((paramKeyEvent.getAction() == 1) && (paramKeyEvent.getKeyCode() == 4)){
	      RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(R.id.progress2);
	      if (localRelativeLayout.getVisibility() != 8){
	        localRelativeLayout.setVisibility(8);
	        bool = true;
	      }
	    }
	    return bool;
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
		setContentView(R.layout.currentchannels);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_big);
		}else{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		}
		GenieGlobalDefines.SaveWiFiScanDefaultEntry(this, 1);
		InitTitleView();
		
		mGestureDetector = new GestureDetector((OnGestureListener) this);    
	    View viewSnsLayout = this.getWindow().getDecorView();    
        viewSnsLayout.setOnTouchListener(this);    
        viewSnsLayout.setLongClickable(true);   
		
		this.mainWifi = ((WifiManager)getSystemService(WIFI_SERVICE));
	    this.m_conectwifiinfo = this.mainWifi.getConnectionInfo();
	    ((TextView)findViewById(R.id.ssid_name)).setText(this.m_conectwifiinfo.getSSID());
	    this.m_Channellist = ((ListView)findViewById(R.id.channels_list));
	    this.m_listItemAdapter = new ChannelAdapter(this);
	    this.m_Channellist.setAdapter(this.m_listItemAdapter);
		this.receiverWifi = new WifiReceiver();
	    registerReceiver(this.receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    StartScan();
       ((Button)findViewById(R.id.refresh)).setOnClickListener(new View.OnClickListener()
       {
	        public void onClick(View paramView)
	        {
	          GenieChannels.this.handler.sendEmptyMessage(0);
	        }
       });
       ((Button)findViewById(R.id.buttonScan)).setOnClickListener(new View.OnClickListener()
       {
	        public void onClick(View paramView)
	        {
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
	
	protected void onPause(){
	    unregisterReceiver(this.receiverWifi);
	    if(isScanning){
	    	CancelLoadingDialog();
	    }
	    super.onPause();
	}

	protected void onResume(){
	    registerReceiver(this.receiverWifi, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
	    super.onResume();
	}
	
	private class ChannelAdapter extends BaseAdapter{
		
		Context context = null;
	    private LayoutInflater mInflater;

	    public ChannelAdapter(Context localContext)
	    {
	      this.mInflater = LayoutInflater.from(localContext);
	      this.context = localContext;
	    }
		
		public int getCount() {
			if (GenieChannels.this.m_channelarray != null){
				return GenieChannels.this.m_channelarray.size();
			}else{
				return 0;
			}
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			
			ViewHolder localViewHolder;
		    if (paramView == null || (paramView !=null&&paramView.getTag()==null) )
		    {
		        paramView = this.mInflater.inflate(R.layout.list_item_wifi_channel, null);
		        localViewHolder = new ViewHolder();
		        localViewHolder.channelindex = ((TextView)paramView.findViewById(R.id.channel_number));
		        localViewHolder.ratingnumber = ((TextView)paramView.findViewById(R.id.rating_number));
		        localViewHolder.ratingstr = ((TextView)paramView.findViewById(R.id.rating_string));
		        localViewHolder.ratingbar = ((ProgressBar)paramView.findViewById(R.id.rating_bar));
		        paramView.setTag(localViewHolder);

		    }else{
		    	localViewHolder = (ViewHolder)paramView.getTag();
		    }
	        int i = GenieChannels.this.m_channelarray.get(paramInt).size();
	        localViewHolder.channelindex.setText(String.valueOf(paramInt + 1));
	        localViewHolder.ratingnumber.setText(String.valueOf(i));
	        localViewHolder.ratingbar.setMax(10);
	        if (i > 10){
	           localViewHolder.ratingstr.setText(this.context.getResources().getString(R.string.s_label_poor));
	           localViewHolder.ratingstr.setTextColor(getResources().getColor(R.color.limegreen));
	           localViewHolder.ratingbar.setProgress(10);
	           localViewHolder.ratingnumber.setText(String.valueOf(i));
	        }else if(i>=0){
	        	
	        	int k = m_ChannelMaxSSID / 2;
	        	if (m_ChannelMaxSSID % 2 > 0){
					k++;
	        	}
	        	if (i != 0){
		        	if (i == m_ChannelMaxSSID)
					{
		        		localViewHolder.ratingstr.setText(context.getResources().getString(R.string.s_label_poor));
		        		localViewHolder.ratingstr.setTextColor(Color.RED);
					} else if (i == k)
					{
						localViewHolder.ratingstr.setText(context.getResources().getString(R.string.s_label_average));
						localViewHolder.ratingstr.setTextColor(GenieColorList.ColorList[2]);
					} else if (i > k && i < m_ChannelMaxSSID)
					{
						localViewHolder.ratingstr.setText(context.getResources().getString(R.string.s_label_good));
						localViewHolder.ratingstr.setTextColor(GenieColorList.ColorList[1]);
					} else if (i > 0 && i < k)
					{
						localViewHolder.ratingstr.setText(context.getResources().getString(R.string.s_label_great));
						localViewHolder.ratingstr.setTextColor(GenieColorList.ColorList[11]);
					}
	        	}else{
	        		localViewHolder.ratingstr.setText(this.context.getResources().getString(R.string.s_label_best));
	        		localViewHolder.ratingstr.setTextColor(GenieColorList.ColorList[3]);
	        	}
	        	
//	        	if(i==0){
//	        		localViewHolder.ratingstr.setText(this.context.getResources().getString(R.string.s_label_poor));
//	        		localViewHolder.ratingstr.setTextColor(Color.RED);
//	        	}else if(i>0&&i<5){
//	        		localViewHolder.ratingstr.setText(this.context.getResources().getString(R.string.s_label_good));
//	        		localViewHolder.ratingstr.setTextColor(Color.GRAY);
//	        	}else if(i==5){
//	        		localViewHolder.ratingstr.setText(this.context.getResources().getString(R.string.s_label_average));
//	        		localViewHolder.ratingstr.setTextColor(Color.YELLOW);
//	        	}else if(i>5&&i<=8){
//	        		localViewHolder.ratingstr.setText(this.context.getResources().getString(R.string.s_label_great));
//	        		localViewHolder.ratingstr.setTextColor(Color.BLUE);
//	        	}else{
//	        		localViewHolder.ratingstr.setText(this.context.getResources().getString(R.string.s_label_best));
//	        		localViewHolder.ratingstr.setTextColor(Color.GREEN);
//	        	}
	        	localViewHolder.ratingbar.setProgress(i);
	        	localViewHolder.ratingnumber.setText(String.valueOf(i));
	        	
	        }else{
	        	localViewHolder.ratingstr.setText(this.context.getResources().getString(R.string.s_label_best));
        		localViewHolder.ratingstr.setTextColor(Color.GREEN);
        		localViewHolder.ratingbar.setProgress(0);
        		localViewHolder.ratingnumber.setText(String.valueOf(i));
	        }
			
	        paramView.setTag(localViewHolder);
			return paramView;
		}
		class ViewHolder{
			TextView channelindex;
			ProgressBar ratingbar;
			TextView ratingnumber;
			TextView ratingstr;
			
			ViewHolder(){
			}
		}
	}
	
	private class MoreListAdapter extends BaseAdapter{
		private LayoutInflater mInflater;

	    public MoreListAdapter(Context arg2)
	    {
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
				viewHolder.text.setText(GenieChannels.this.getResources().getString(R.string.s_wifi_more_1).toString());
				break;
			case 1:
				viewHolder.text.setText(GenieChannels.this.getResources().getString(R.string.s_wifi_more_2).toString());
				break;
			case 2:
				viewHolder.text.setText(GenieChannels.this.getResources().getString(R.string.s_wifi_more_3).toString());
				break;
			case 3:
				viewHolder.text.setText(GenieChannels.this.getResources().getString(R.string.s_wifi_more_4).toString());
				break;
			case 4:
				viewHolder.text.setText(GenieChannels.this.getResources().getString(R.string.s_wifi_more_5).toString());
				break;
			case 5:{
				viewHolder.text.setText(GenieChannels.this.getResources().getString(R.string.s_wifi_more_6).toString());
				break;
			}
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
			 GenieChannels.this.wifiList = GenieChannels.this.mainWifi.getScanResults();
		     GenieChannels.this.CancelLoadingDialog();
		    
		     m_conectwifiinfo = mainWifi.getConnectionInfo();
			 if(m_conectwifiinfo!=null){
		    	 Iterator<ScanResult> iterator1 = wifiList.iterator();
		    	 while(iterator1.hasNext()){
		    		 ScanResult scanresult1 = (ScanResult)iterator1.next();
		 			GenieDebug.error("debug", (new StringBuilder("obj.ssid=")).append(scanresult1.SSID).toString());
		 			GenieDebug.error("debug", (new StringBuilder("wifi.ssid=")).append(m_conectwifiinfo.getSSID()).toString());
		 			if (scanresult1.SSID.equals(m_conectwifiinfo.getSSID()))
		 			{
		 				m_currentwifi.bssid = scanresult1.BSSID;
		 				m_currentwifi.ssid = scanresult1.SSID;
		 				m_currentwifi.channel = GenieWifiScan.analysisChannel(1000 * scanresult1.frequency);
		 				((TextView)findViewById(R.id.current_ch)).setText(m_currentwifi.channel<=0?"":(new StringBuilder()).append(m_currentwifi.channel).toString());
		 			}
		 			GenieDebug.error("debug", (new StringBuilder("m_MaxChannel=")).append(m_MaxChannel).toString());
		    	 }
		     }
			 for(int i=0;i<m_channelarray.size();i++){
	    		 m_channelarray.get(i).clear();
	    	 }
			 
		     Iterator<ScanResult> localIterator2;
		     if (GenieChannels.this.m_conectwifiinfo != null){
		    	 localIterator2 = GenieChannels.this.wifiList.iterator();
		    	 int j=0;
		    	 while(localIterator2.hasNext()){
		    		 ScanResult result1=localIterator2.next();
		    		 j = GenieWifiScan.analysisChannel(1000 * result1.frequency);
		    		 if(j>0){
			    		 GenieChannels.wifichannel localwifichannel3 = new GenieChannels.wifichannel(GenieChannels.this);
			    		 localwifichannel3.bssid = result1.BSSID;
			    		 localwifichannel3.ssid = result1.SSID;
			    		 localwifichannel3.channel = j;
			    		 while(GenieChannels.this.m_channelarray.size()<j || GenieChannels.this.m_channelarray.size()<14){
			    			 ArrayList<wifichannel> localArrayList3 = new ArrayList<wifichannel>();
				             GenieChannels.this.m_channelarray.add(localArrayList3);
			    		 }
			    		 if(GenieChannels.this.m_channelarray.get(j-1)==null){
				    		 ArrayList<wifichannel> localArrayList3 = new ArrayList<wifichannel>();
				             localArrayList3.add(localwifichannel3);
				    		 GenieChannels.this.m_channelarray.add(j-1, localArrayList3);
			    		 }else{
			    			 boolean isExist=false;
			    			 for(int l=0;l<GenieChannels.this.m_channelarray.get(j-1).size();l++){
			    				 if(result1.BSSID.equals(GenieChannels.this.m_channelarray.get(j-1).get(l).bssid)){
			    					 isExist=true;
			    					 break;
			    				 }
			    			 }
			    			 if(!isExist){
			    				 GenieChannels.this.m_channelarray.get(j-1).add(localwifichannel3);
			    			 }
			    		 }
		    		 }
		    	 }
		    	 
		     }
		     GenieChannels.this.RefreshView();
		     if(GenieChannels.this.m_listItemAdapter!=null){
		    	 GenieChannels.this.m_listItemAdapter.notifyDataSetChanged();
		     }
		}
	}
	
	class wifichannel{
		String bssid;
	    int channel;
	    String ssid;

	    wifichannel(){
	    }
	    
	    wifichannel(Context context){
	    }
	    
	    public int GetChannel(){
	      return this.channel;
	    }

	    public void SetChannel(int paramInt){
	      this.channel = paramInt;
	    }

	    public String toString(){
	      Object[] arrayOfObject = new Object[2];
	      arrayOfObject[0] = this.ssid;
	      arrayOfObject[1] = this.bssid;
	      return String.format("%s\n(%s)", arrayOfObject);
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
//	        Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show(); 
			Intent intent=new Intent(GenieChannels.this,GenieWifiScan.class);
	    	startActivity(intent);
	    	overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	    	finish();
	    } else if (Math.abs(e2.getX() - e1.getX() )>Math.abs( e2.getY()-e1.getY()) && (e1.getX()-e2.getX())>0 && Math.abs(velocityX) > minVelocity && (e1.getX()-e2.getX())>verticalMinDistance) {  
//	    	Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();  
	    	Intent intent=new Intent(GenieChannels.this,GenieSignalHistogram.class);
	    	startActivity(intent);
	    	GenieChannels.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
