package com.dragonflow;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.dragonflow.genie.ui.R; 

public class GenieWirelessSignal extends Activity {

	private WifiManager wifiManager = null;
	private WifiInfo wifiInfo = null;

	private TextView wireless_tv_name_value = null,
			wireless_tv_signal_value = null;
	private ImageView wireless_iv_signal = null;

	private int signalStrength;
	private String ssid;

	private ProgressDialog progressDialog = null;
	private Timer timer = null;
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				setModelInfo();
				break;

			default:
				break;
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Display d = getWindowManager().getDefaultDisplay();
		int woindow_w = d.getWidth();
		int woindow_h = d.getHeight();

		// ConstantDefine.SCREEN_Heigth = d.getHeight();
		// ConstantDefine.SCREEN_Width = d.getWidth();

		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);

		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			setTheme(R.style.bigactivityTitlebarNoSearch);
		} else {
			setTheme(R.style.activityTitlebarNoSearch); // qicheng.ai add
		}

		GenieDebug.error("debug", "onCreate --0--");

		requestWindowFeature(Window.FEATURE_NO_TITLE
				| Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.wireless_signal);
		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
		}
		GenieDebug.error("debug", "onCreate --1--");

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// TelephonyManager Tel = ( TelephonyManager
		// )getSystemService(Context.TELEPHONY_SERVICE);
		// Tel.listen(new MyPhoneStateListener(),
		// PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		InitTitleView();
		initView();
		setWaitTimeAndValue();

	}
	
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
	}



	public void InitTitleView() {
		Button back = (Button) this.findViewById(R.id.back);
		Button about = (Button) this.findViewById(R.id.about);
		about.setVisibility(View.INVISIBLE);
		TextView title = (TextView) this.findViewById(R.id.netgeartitle);

		title.setText(R.string.signal);

		back.setBackgroundResource(R.drawable.title_bt_bj);
		about.setBackgroundResource(R.drawable.title_bt_bj);
		about.setText(R.string.refresh);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GenieWirelessSignal.this.finish();
			}
		});

		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// OnClickAbout();

				setWaitTimeAndValue();
			}
		});

		back.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�
					v.setBackgroundResource(R.drawable.title_bt_fj);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 锟斤拷为抬锟斤拷时锟斤拷图片
					v.setBackgroundResource(R.drawable.title_bt_bj);

				}
				return false;
			}
		});

		about.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�
					v.setBackgroundResource(R.drawable.title_bt_fj);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 锟斤拷为抬锟斤拷时锟斤拷图片
					v.setBackgroundResource(R.drawable.title_bt_bj);

				}
				return false;
			}
		});
	}

	public void initView() {
		wireless_tv_name_value = (TextView) findViewById(R.id.wireless_tv_name_value);
		wireless_tv_signal_value = (TextView) findViewById(R.id.wireless_tv_signal_value);
		wireless_iv_signal = (ImageView) findViewById(R.id.wireless_iv_signal);
	}

	public void setModelInfo() {
		getWifiInfo();
		wireless_tv_name_value.setText(ssid);
		wireless_tv_signal_value.setText(signalStrength + " %");
		if (signalStrength >= 80) {
			wireless_iv_signal.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.signal5));
		} else if (signalStrength >= 60) {
			wireless_iv_signal.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.signal4));
		} else if (signalStrength >= 40) {
			wireless_iv_signal.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.signal3));
		} else if (signalStrength >= 20) {
			wireless_iv_signal.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.signal2));
		} else if (signalStrength >= 0) {
			wireless_iv_signal.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.signal1));
		}
	}

	public void setWaitTimeAndValue() {
		// ShowProgressDialog();
		// Handler handler = new Handler();
		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
//		setModelInfo();
		// CancelProgressDialog();
		// }
		// }, 2000);
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(0);
				
			}
		}, 0,3000);

	}

	public void getWifiInfo() {
		if (wifiManager != null) {
			wifiInfo = wifiManager.getConnectionInfo();
			int signal = 0;
			GenieDebug.error("setWifiInfo",
					"111111111111111111" + wifiInfo.getRssi() + ":Link: "
							+ wifiInfo.getLinkSpeed());
			int rssi = wifiInfo.getRssi();
			GenieDebug.error("setWifiInfo  rssi", "222222   rssi" + rssi);
			try {
				signal = WifiManager.calculateSignalLevel(rssi, 10);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GenieDebug.error("setWifiInfo  signal", "4444444444   signal"
					+ signal);

			if (signal != 0) {
				signal += 1;
			}
			GenieDebug.error("setWifiInfo  signal", "555555555   signal"
					+ signal);

			signalStrength = signal * 10;
			ssid = wifiInfo.getSSID();
			GenieDebug.error("setWifiInfo  ssid", "555555555   ssid" + ssid);
		}
	}

	private void CancelProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		progressDialog = null;
	}

	private void ShowProgressDialog() {
		CancelProgressDialog();
		progressDialog = ProgressDialog.show(this, null, getResources()
				.getString(R.string.pleasewait) + "...", true, true);
	}

}
