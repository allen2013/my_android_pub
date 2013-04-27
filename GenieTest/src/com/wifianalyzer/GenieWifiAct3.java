package com.wifianalyzer;

import java.util.ArrayList;
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
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 
import com.filebrowse.FileBrowseTab;

public class GenieWifiAct3 extends Activity implements OnTouchListener, OnGestureListener{
	  private Timer autotimer = null;
	  private ArrayList<Act3signal> m_Act3signallist = null;
	  private String m_BSSID = null;
	  private PopupWindow m_MoreDialog = null;
	  private ImageView m_arrow = null;
	  private WifiInfo m_conectwifiinfo = null;
	  private int m_dbm = 40;
	  private Spinner m_selectssid = null;
	  private ArrayAdapter<String> m_ssidAdapter = null;
	  private List<String> m_ssidlist = new ArrayList<String>();
	  public WifiManager mainWifi;
	  public WifiReceiver receiverWifi;
	  private TimerTask task = null;
	  public List<ScanResult> wifiList = null;
	  private GestureDetector mGestureDetector;

	  
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
						 if (mainWifi != null){
							 mainWifi.startScan();
							 return;
						 }
					}
				};
				autotimer.schedule(task, paramInt, paramInt);
			}
		}
	  
	  public void GotoMore(int paramInt){
			 Intent intent = new Intent();
			 switch (paramInt) {
			 case 1:
				 intent.setClass(this, GenieChannels.class);
				 startActivity(intent);
				 finish();
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
		  Button localButton1 = (Button)findViewById(R.id.back);
		  localButton1.setBackgroundResource(R.drawable.title_bt_bj);
		  Button localButton2 = (Button)findViewById(R.id.about);
		  localButton2.setBackgroundResource(R.drawable.title_more);
		  localButton2.setText(null);
		  localButton1.setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg0) {
				GenieWifiAct3.this.onBackPressed();
			}
		  });
		  localButton2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GenieWifiAct3.this.ShowMoreDialog();
			}
		});
		  localButton1.setOnTouchListener(new OnTouchListener() {
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
	  
	  private void ShowMoreDialog(){
			 Button localButton = (Button)findViewById(R.id.about);
			 View localView = LayoutInflater.from(this).inflate(R.layout.dialog_more, null);
			 ListView localListView = (ListView)localView.findViewById(R.id.list);
			 localListView.setAdapter(new MoreListAdapter(this));
//			 localListView.setItemChecked(-1, true);
			 localListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					 GenieDebug.error("debug", "onItemClick arg3=" + arg3);
					 GenieWifiAct3.this.GotoMore((int)arg3);
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
	  
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
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
		setContentView(R.layout.wifi_act3);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_big);
		}else{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		}
		GenieGlobalDefines.SaveWiFiScanDefaultEntry(this, 4);
		InitTitleView();
		
		mGestureDetector = new GestureDetector((OnGestureListener) this);    
	    View viewSnsLayout = this.getWindow().getDecorView();    
        viewSnsLayout.setOnTouchListener(this);    
        viewSnsLayout.setLongClickable(true); 
		
		receiverWifi = new WifiReceiver();
	    registerReceiver(receiverWifi, new IntentFilter(WIFI_SERVICE));
	    mainWifi = (WifiManager)getSystemService(WIFI_SERVICE);
		m_conectwifiinfo = mainWifi.getConnectionInfo();
		m_BSSID = m_conectwifiinfo.getBSSID();
		m_Act3signallist = new ArrayList<Act3signal>();
		
		if (m_conectwifiinfo != null && m_conectwifiinfo.getSSID()!=null && m_conectwifiinfo.getBSSID()!=null)
		{
			Act3signal act3signal = new Act3signal();
			act3signal.bssid = m_conectwifiinfo.getBSSID()==null?"":m_conectwifiinfo.getBSSID();
			act3signal.ssid = m_conectwifiinfo.getSSID()==null?"":m_conectwifiinfo.getSSID();
			m_Act3signallist.add(act3signal);
			((TextView)findViewById(R.id.act3ssidmac)).setText((new StringBuilder(String.valueOf(m_conectwifiinfo.getSSID()))).append("\n").append(m_conectwifiinfo.getBSSID()).toString());
		}
		m_arrow = (ImageView)findViewById(R.id.tempimage);
		m_ssidlist.clear();
		
		for(int k=0;k<m_Act3signallist.size();k++){
			m_ssidlist.add(((Act3signal)m_Act3signallist.get(k)).toString());
		}
		m_selectssid = (Spinner)findViewById(R.id.ssidselect);
		if(m_selectssid!=null && (m_ssidlist==null || m_ssidlist.size()==0)){
			m_selectssid.setEnabled(false);
		}
		m_ssidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_ssidlist);
		m_ssidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_selectssid.setAdapter(m_ssidAdapter);
		m_selectssid.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterview, View view, int l, long l1)
			{
				SetDefaultSSID(l);
				adapterview.setVisibility(AdapterView.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> adapterview)
			{
			}

	
			}
		);
		
		mainWifi.startScan();
		if(mainWifi.isWifiEnabled()){
			AutoScanWifi(1500, true);
		}else{
			Toast.makeText(GenieWifiAct3.this, "Wifi is off!", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	
	private void MoveArrow(int i)
	{
		GenieDebug.error("debug", (new StringBuilder("MoveArrow dbm=")).append(i).toString());
		if (m_arrow == null){
			m_arrow = (ImageView)findViewById(R.id.tempimage);
		}
		if(m_arrow!=null){
			int j = Math.abs(i);
			if (j != m_dbm)
			{
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				GenieDebug.error("debug", "MoveArrow ################################");
				GenieDebug.error("debug", (new StringBuilder("MoveArrow m_arrow.getHeight()=")).append(m_arrow.getHeight()).toString());
				GenieDebug.error("debug", (new StringBuilder("MoveArrow m_arrow.getWidth()=")).append(m_arrow.getWidth()).toString());
				ImageView imageview = (ImageView)findViewById(R.id.signalmeter);
				GenieDebug.error("debug", (new StringBuilder("MoveArrow meter.getHeight()=")).append(imageview.getHeight()).toString());
				GenieDebug.error("debug", (new StringBuilder("MoveArrow meter.getWidth()=")).append(imageview.getWidth()).toString());
				GenieDebug.error("debug", "MoveArrow ################################");
				int k = (int)(((70-(Math.abs(j) - 40F)) / 70F) * (float)(imageview.getHeight() - m_arrow.getHeight()));
				android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(20, 14);
				layoutparams.setMargins(0, 0, 0, k);
				m_arrow.setLayoutParams(layoutparams);
				TextView textview = (TextView)findViewById(R.id.act5dbm);
				Object aobj[] = new Object[1];
				aobj[0] = Integer.valueOf(i);
				textview.setText(String.format("%s dBm", aobj));
			}
		}

	}
	
	 protected void onDestroy()
	  {
		 AutoScanWifi(1000, false);
	    super.onDestroy();
	  }

	  protected void onPause()
	  {
	    unregisterReceiver(this.receiverWifi);
	    super.onPause();
	  }

	  protected void onResume()
	  {
	    registerReceiver(this.receiverWifi, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
	    super.onResume();
	  }
	
	  public void SetDefaultSSID(int i)
	  {
			if (m_ssidlist != null && m_Act3signallist != null && m_ssidlist.size()>0 && i < m_ssidlist.size())
			{
				m_BSSID = ((Act3signal)m_Act3signallist.get(i)).bssid;
				((TextView)findViewById(R.id.act3ssidmac)).setText((new StringBuilder(String.valueOf(((Act3signal)m_Act3signallist.get(i)).ssid))).append("\n").append(((Act3signal)m_Act3signallist.get(i)).bssid).toString());
			}
	  }
	  
	
	private class MoreListAdapter extends BaseAdapter{
		private LayoutInflater mInflater;

	    public MoreListAdapter(Context context)
	    {
	      mInflater = LayoutInflater.from(context);
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
				viewHolder.text.setText(GenieWifiAct3.this.getResources().getString(R.string.s_wifi_more_1).toString());
				break;
			case 1:
				viewHolder.text.setText(GenieWifiAct3.this.getResources().getString(R.string.s_wifi_more_2).toString());
				break;
			case 2:
				viewHolder.text.setText(GenieWifiAct3.this.getResources().getString(R.string.s_wifi_more_3).toString());
				break;
			case 3:
				viewHolder.text.setText(GenieWifiAct3.this.getResources().getString(R.string.s_wifi_more_4).toString());
				break;
			case 4:
				viewHolder.text.setText(GenieWifiAct3.this.getResources().getString(R.string.s_wifi_more_5).toString());
				break;
			case 5:
				viewHolder.text.setText(GenieWifiAct3.this.getResources().getString(R.string.s_wifi_more_6).toString());
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
		public void onReceive(Context context, Intent intent) {
			wifiList = mainWifi.getScanResults();
			if (wifiList != null && m_Act3signallist != null && m_ssidlist != null){
//				for(int i=0;i<m_Act3signallist.size();i++){
//					((Act3signal)m_Act3signallist.get(i)).SetActive(false);
//				}
				m_Act3signallist.clear();
				Iterator<ScanResult> iterator = wifiList.iterator();
				while(iterator.hasNext()){
					ScanResult scanresult=(ScanResult)iterator.next();
					int j=0;
					int k = m_Act3signallist.size();
					boolean flag = false;
					if (j < k)
					{
						if (scanresult.BSSID.equals(((Act3signal)m_Act3signallist.get(j)).bssid)){
							flag = true;
						}
					}
					if (!flag)
					{
						Act3signal act3signal = new Act3signal();
						act3signal.bssid = scanresult.BSSID;
						act3signal.ssid = scanresult.SSID;
						m_Act3signallist.add(act3signal);
					}
					j++;
				}
				//调整刻度
				Iterator<ScanResult> iterator1 = wifiList.iterator();
				boolean flag2;
				flag2 = false;
				while(iterator1.hasNext()){
					ScanResult scanresult1 = (ScanResult)iterator1.next();
					if (scanresult1.BSSID.equals(m_BSSID)){
						flag2 = true;
						MoveArrow(scanresult1.level);
						if (!flag2){
							MoveArrow(-110);
						}
						break;
					}
				}
				//
				
				m_ssidlist.clear();
				int l = 0;
				while (l < m_Act3signallist.size()) 
				{
					m_ssidlist.add(((Act3signal)m_Act3signallist.get(l)).toString());
					l++;
				}
				if(m_ssidlist.size()>0){
					m_selectssid.setEnabled(true);
				}else{
					m_selectssid.setEnabled(false);
				}
				m_ssidAdapter.notifyDataSetChanged();
			}
		}
		
	}
	
	class Act3signal{
		
		boolean active;
		String bssid;
		String ssid;

		public boolean GetActive()
		{
			return active;
		}

		public void SetActive(boolean flag)
		{
			active = flag;
		}

		public String toString()
		{
			Object aobj[] = new Object[2];
			aobj[0] = ssid;
			aobj[1] = bssid;
			return String.format("%s\n(%s)", aobj);
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
		if (Math.abs(e1.getX() - e2.getX() )> Math.abs(e1.getY()-e2.getY()) && (e1.getX()-e2.getX())<0 && Math.abs(velocityX) > minVelocity && (e2.getX()-e1.getX())>verticalMinDistance) {  
//	        Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show(); 
			Intent intent=new Intent(GenieWifiAct3.this,GenieSignalGraph.class);
	    	startActivity(intent);
	    	GenieWifiAct3.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	    	finish();
	    } else if (Math.abs(e2.getX() - e1.getX() )>Math.abs( e2.getY()-e1.getY()) && Math.abs(velocityX) > minVelocity && (e1.getX()-e2.getX())>verticalMinDistance) {  
//	    	Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();  
	    	
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
