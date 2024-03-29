package com.wifianalyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.chart.widget.FoldLineHelp;
import com.chart.widget.NewHistogramView;
import com.dragonflow.GenieDebug;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class GenieSignalHistogram extends Activity implements OnTouchListener, OnGestureListener{
	private Timer autotimer = null;
	private final int defalutid = 2600;
	private String m_BSSID = null;
	private PopupWindow m_MoreDialog = null;
	private int m_color = 0;
	private WifiInfo m_conectwifiinfo = null;
	private LinearLayout m_hscrollview = null;
	private int m_id = 2600;
	private NewHistogramView m_signalgraph = null;
	private ArrayList<FoldLineHelp> m_signalgraphlist = null;
	public WifiManager mainWifi;
	public WifiReceiver receiverWifi;
	private TimerTask task = null;
	public List<ScanResult> wifiList = null;
	private android.widget.CompoundButton.OnCheckedChangeListener checkboxlistener;
	private GestureDetector mGestureDetector;
	
	public GenieSignalHistogram(){
		
		wifiList = null;
		m_conectwifiinfo = null;
		m_signalgraphlist = null;
		m_BSSID = null;
		m_signalgraph = null;
		m_color = 0;
		m_id = 2600;
		m_hscrollview = null;
		autotimer = null;
		task = null;
		m_MoreDialog = null;
		checkboxlistener = new android.widget.CompoundButton.OnCheckedChangeListener() {


			public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
			{
				int i = compoundbutton.getId();
				if (m_signalgraphlist != null){
					for(int j=0;j<m_signalgraphlist.size();j++){
						if (i == ((FoldLineHelp)m_signalgraphlist.get(j)).id){
							((FoldLineHelp)m_signalgraphlist.get(j)).drawflag = flag;
							break;
						}
					}
				}
			}

			
			
		};
		
	}
	
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
		Intent intent=new Intent();
		switch (paramInt) {
		case 0:
			intent.setClass(this, GenieWifiScan.class);
			startActivity(intent);
			finish();
			break;
		case 1:
			intent.setClass(this, GenieChannels.class);
			startActivity(intent);
			finish();
			break;
		case 2:
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
		  Button localButton1 = (Button)findViewById(R.id.back);
		  localButton1.setBackgroundResource(R.drawable.title_bt_bj);
		  Button localButton2 = (Button)findViewById(R.id.about);
		  localButton2.setBackgroundResource(R.drawable.title_more);
		  localButton2.setText(null);
		  localButton1.setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg0) {
				GenieSignalHistogram.this.onBackPressed();
			}
		  });
		  localButton2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GenieSignalHistogram.this.ShowMoreDialog();
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
//		 localListView.setItemChecked(-1, true);
		 localListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 GenieDebug.error("debug", "onItemClick arg3=" + arg3);
				 GenieSignalHistogram.this.GotoMore((int)arg3);
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
		setContentView(R.layout.wifisignalhistogram);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_big);
		}else{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		}
		GenieGlobalDefines.SaveWiFiScanDefaultEntry(this, 2);
		InitTitleView();
		
		mGestureDetector = new GestureDetector((OnGestureListener) this);    
	    View viewSnsLayout = this.findViewById(R.id.signalgraph);  
        viewSnsLayout.setLongClickable(true);
        viewSnsLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mGestureDetector.onTouchEvent(event);
				return false;
			}
		});
		
		m_hscrollview = (LinearLayout)findViewById(R.id.ssidlist);
		m_signalgraphlist = new ArrayList<FoldLineHelp>();
		mainWifi = (WifiManager)getSystemService("wifi");
		m_conectwifiinfo = mainWifi.getConnectionInfo();
		m_BSSID = m_conectwifiinfo.getBSSID();
		if (m_conectwifiinfo != null)
		{
			FoldLineHelp foldlinehelp;
			if (-2600 + m_id < GenieColorList.ColorList.length)
				m_color = GenieColorList.ColorList[-2600 + m_id];
			else
				m_color = GenieColorList.ColorList[-1 + GenieColorList.ColorList.length] + 0x186a0 * (-2600 + m_id);
			foldlinehelp = new FoldLineHelp(m_conectwifiinfo.getBSSID(), m_conectwifiinfo.getSSID(), m_conectwifiinfo.getRssi(), m_color, 0, true, -100);
			if (m_hscrollview != null && m_conectwifiinfo.getSSID()!=null && m_conectwifiinfo.getBSSID()!=null)
			{
				android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(-2, -2);
				layoutparams.setMargins(5, 0, 5, 0);
				CheckBox checkbox = new CheckBox(this);
				checkbox.setTextColor(m_color);
				checkbox.setTextSize(18F);
				checkbox.setTypeface(Typeface.create(Typeface.SERIF, 1));
				checkbox.setText(m_conectwifiinfo.getSSID());
				checkbox.setId(m_id);
				checkbox.setChecked(true);
				checkbox.setOnCheckedChangeListener(checkboxlistener);
				LinearLayout linearlayout = new LinearLayout(this);
				linearlayout.setBackgroundResource(R.drawable.more_bg);
				linearlayout.addView(checkbox, layoutparams);
				m_hscrollview.addView(linearlayout, layoutparams);
			}
			foldlinehelp.id = m_id;
			m_id = 1 + m_id;
			m_signalgraphlist.add(foldlinehelp);
		}
		m_signalgraph = (NewHistogramView)findViewById(R.id.signalgraph);
		m_signalgraph.setYLables(-30, -100, 7);
		m_signalgraph.setXLables(11, 0, 11);
		m_signalgraph.setYTitle("signal");
		m_signalgraph.setXTitle("Channel");
		receiverWifi = new WifiReceiver();
		registerReceiver(this.receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mainWifi.startScan();
		if(mainWifi.isWifiEnabled()){
			AutoScanWifi(2500, true);
		}else{
			Toast.makeText(GenieSignalHistogram.this, "Wifi is off!", Toast.LENGTH_SHORT).show();
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
				viewHolder.text.setText(GenieSignalHistogram.this.getResources().getString(R.string.s_wifi_more_1).toString());
				break;
			case 1:
				viewHolder.text.setText(GenieSignalHistogram.this.getResources().getString(R.string.s_wifi_more_2).toString());
				break;
			case 2:
				viewHolder.text.setText(GenieSignalHistogram.this.getResources().getString(R.string.s_wifi_more_3).toString());
				break;
			case 3:
				viewHolder.text.setText(GenieSignalHistogram.this.getResources().getString(R.string.s_wifi_more_4).toString());
				break;
			case 4:
				viewHolder.text.setText(GenieSignalHistogram.this.getResources().getString(R.string.s_wifi_more_5).toString());
				break;
			case 5:
				viewHolder.text.setText(GenieSignalHistogram.this.getResources().getString(R.string.s_wifi_more_6).toString());
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

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			wifiList = mainWifi.getScanResults();
			if (m_signalgraphlist != null && m_signalgraph != null){
				for(int i=0;i<m_signalgraphlist.size();i++){
					((FoldLineHelp)m_signalgraphlist.get(i)).SetActive(false);
				}
			
				Iterator<ScanResult> iterator = wifiList.iterator();
				while(iterator.hasNext()){
					ScanResult scanresult=(ScanResult)iterator.next();
					int k = m_signalgraphlist.size();
					boolean flag=false;
					for(int j=0;j<k;j++){
						if(scanresult.BSSID.equals(((FoldLineHelp)m_signalgraphlist.get(j)).BSSID)){
							flag = true;
							((FoldLineHelp)m_signalgraphlist.get(j)).SetLevel(scanresult.level);
							((FoldLineHelp)m_signalgraphlist.get(j)).SetActive(true);
							((FoldLineHelp)m_signalgraphlist.get(j)).SetChannel(GenieWifiScan.analysisChannel(1000 * scanresult.frequency));
						}
					}
					if(!flag){
						FoldLineHelp foldlinehelp;
						if (-2600 + m_id < GenieColorList.ColorList.length)
							m_color = GenieColorList.ColorList[-2600 + m_id];
						else
							m_color = GenieColorList.ColorList[-1 + GenieColorList.ColorList.length] + 0x186a0 * (-2600 + m_id);
						foldlinehelp = new FoldLineHelp(scanresult.BSSID, scanresult.SSID, scanresult.level, m_color, GenieWifiScan.analysisChannel(1000 * scanresult.frequency), true, -100);
						GenieSignalHistogram geniesignalhistogram;
						if (m_hscrollview != null && scanresult.SSID!=null && scanresult.BSSID!=null)
						{
							android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(-2, -2);
							layoutparams.setMargins(5, 0, 5, 0);
							CheckBox checkbox = new CheckBox(GenieSignalHistogram.this);
							checkbox.setTextColor(m_color);
							LinearLayout linearlayout;
							if (scanresult.BSSID.equals(m_BSSID))
							{
								checkbox.setTextSize(18F);
								checkbox.setTypeface(Typeface.create(Typeface.SERIF, 1));
							} else
							{
								checkbox.setTextSize(16F);
							}
							checkbox.setText(scanresult.SSID);
							checkbox.setId(m_id);
							checkbox.setChecked(true);
							checkbox.setOnCheckedChangeListener(checkboxlistener);
							linearlayout = new LinearLayout(GenieSignalHistogram.this);
							linearlayout.setBackgroundResource(R.drawable.more_bg);
							linearlayout.addView(checkbox, layoutparams);
							m_hscrollview.addView(linearlayout, layoutparams);
						}
						foldlinehelp.id = m_id;
						geniesignalhistogram = GenieSignalHistogram.this;
						geniesignalhistogram.m_id = 1 + geniesignalhistogram.m_id;
						m_signalgraphlist.add(foldlinehelp);
					}
				}
				for(int l=0;l<m_signalgraphlist.size();l++){
					if (!((FoldLineHelp)m_signalgraphlist.get(l)).GetActive()){
						((FoldLineHelp)m_signalgraphlist.get(l)).SetLevel(-100);
					}
				}
				m_signalgraph.setHistogramHelpData(m_signalgraphlist);
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
		
		if (Math.abs(e1.getX() - e2.getX() )> Math.abs(e1.getY()-e2.getY()) && (e1.getX()-e2.getX())<0 && Math.abs(velocityX) > minVelocity && (e2.getX()-e1.getX())>verticalMinDistance) {  
//	        Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show(); 
			Intent intent=new Intent(GenieSignalHistogram.this,GenieChannels.class);
	    	startActivity(intent);
	    	GenieSignalHistogram.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	    	finish();
	    } else if (Math.abs(e2.getX() - e1.getX() )>Math.abs( e2.getY()-e1.getY()) && (e1.getX()-e2.getX())>0 && Math.abs(velocityX) > minVelocity && (e1.getX()-e2.getX())>verticalMinDistance) {  
//	    	Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();  
	    	Intent intent=new Intent(GenieSignalHistogram.this,GenieSignalGraph.class);
	    	startActivity(intent);
	    	GenieSignalHistogram.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
	
//	public boolean dispatchTouchEvent(MotionEvent ev) {  
//	    mGestureDetector.onTouchEvent(ev);  
//	    return super.dispatchTouchEvent(ev);  
//	}  
	
}
