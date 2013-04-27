package com.turbo;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.Timer;

import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.DeviceInfo;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 
import com.filebrowse.ScanDeviceService;

public class Turbo_DevicelistActivity extends Activity {

	private WifiManager mainWifi;
	public WifiReceiver receiverWifi;
	public static String TurboFile = "TURBOFILE";
	private Turbo_DevicelistAdapter devicelistAdapter;
	private List<TurboDeviceInfo> devicelist = new ArrayList<TurboDeviceInfo>();
	private TurboDeviceInfo currentDeviceInfo = null;
	private ListView devicelistview;
	private ImageView imageview_devicesearch;
	private Timer scanningTimer = null;
	private static int times = 0;
	private ImageView reflashBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Display d = getWindowManager().getDefaultDisplay();
		final int window_w = d.getWidth();
		final int window_h = d.getHeight();

		if (window_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& window_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			setTheme(R.style.bigactivityTitlebarNoSearch);
		} else {
			setTheme(R.style.activityTitlebarNoSearch);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE
				| Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.turbo_devicelist);

		if (window_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& window_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
		}

		InitTitleView();
		imageview_devicesearch = (ImageView) this
				.findViewById(R.id.tb_scanning_img);

		// 注册wifi广播
		receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, new IntentFilter(
				WifiManager.NETWORK_STATE_CHANGED_ACTION));
		// wifi信息
		mainWifi = ((WifiManager) getSystemService(WIFI_SERVICE));
		WifiInfo wifiInfo = mainWifi.getConnectionInfo();

		initWifiInfo(wifiInfo);

		// 切换wifi
		TextView changewifi = (TextView) this.findViewById(R.id.change_wifi);
		changewifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(
						android.provider.Settings.ACTION_WIFI_SETTINGS));

			}
		});

		// 注册设备发现广播
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(TurboFile);
		registerReceiver(discoveryReceiver, myIntentFilter);

		// 设备列表适配器
		devicelistview = (ListView) this.findViewById(R.id.tb_devicelist);
		devicelistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				TurboDeviceInfo info = (TurboDeviceInfo) view.getTag();
				if (info != null) {
					Intent intent = new Intent(Turbo_DevicelistActivity.this,
							Turbo_SelectFileActivity.class);
					intent.putExtra("ReceivedDeviceInfo", info);
					intent.putExtra("OwnDeviceInfo", currentDeviceInfo);
					startActivity(intent);
				}

			}
		});
		devicelistAdapter = new Turbo_DevicelistAdapter(
				Turbo_DevicelistActivity.this, devicelist);
		devicelistview.setAdapter(devicelistAdapter);

		// 加载设备列表
		if (mainWifi != null && mainWifi.isWifiEnabled()) {
			String ownip = intToIp(wifiInfo.getIpAddress()).trim();
			for (Entry<String, String> entry : ScanDeviceService.turbo_map
					.entrySet()) {

				System.out.println("lh~~~~~~~~设备名称:" + entry.getValue()
						+ "    IP:" + entry.getKey());
				try {
					TurboDeviceInfo info = new TurboDeviceInfo();
					info.setIp(entry.getKey());
					info.setDeviceName(entry.getValue());
					if (wifiInfo != null && entry.getKey().equals(ownip)) {
						currentDeviceInfo = info;
						SharedPreferences.Editor sharedata = getSharedPreferences("OwnDeviceInfo", 0).edit();  
						sharedata.putString("DeviceIP",currentDeviceInfo.getIp());  
						sharedata.putString("DeviceName",currentDeviceInfo.getDeviceName());  
						sharedata.commit();
					} else {
						devicelist.add(info);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			devicelistAdapter.notifyDataSetChanged();
		} else {
			Toast.makeText(Turbo_DevicelistActivity.this, "Wifi is off!",
					Toast.LENGTH_SHORT).show();
		}

		// 当前设备名称
		if (currentDeviceInfo != null) {
			TextView ownDevicenameView = (TextView) this
					.findViewById(R.id.tb_own_devicename);
			ownDevicenameView.setText(currentDeviceInfo.getDeviceName().trim());
		}

		// 重新刷新
		reflashBtn = (ImageView) this.findViewById(R.id.tb_reflashBtn);
		reflashBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				doScanning();

			}
		});
		
		//执行扫描
		doScanning();

	}

	/**
	 * 初始化标题栏
	 */
	public void InitTitleView() {
		Button back = (Button) findViewById(R.id.back);
		Button about = (Button) findViewById(R.id.about);

		TextView title = (TextView) findViewById(R.id.netgeartitle);
		title.setText("Device List");

		back.setBackgroundResource(R.drawable.title_bt_bj);
		// about.setBackgroundResource(R.drawable.title_bt_bj);
		// about.setText(R.string.refresh);
		about.setVisibility(View.GONE);

		back.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor sharedata = getSharedPreferences("OwnDeviceInfo", 0).edit();  
				sharedata.putString("DeviceIP","");  
				sharedata.putString("DeviceName","");  
				sharedata.commit();
				finish();
			}

		});

		back.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.title_bt_fj);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(R.drawable.title_bt_bj);

				}
				return false;
			}
		});

	}
	
	
	private void doScanning(){
		
		if (mainWifi != null && mainWifi.isWifiEnabled()) {
			ScanDeviceService scanDeviceService = ScanDeviceService.scanDevice;
			if (scanDeviceService != null) {
				// 信号动画
				imageview_devicesearch.setBackgroundResource(R.anim.turbo_devicelist);
				final AnimationDrawable animationDrawable = (AnimationDrawable) imageview_devicesearch.getBackground();
				imageview_devicesearch.post(new Runnable() {
					@Override
					public void run() {
						animationDrawable.start();
					}
				});
				// 搜索动画
				reflashBtn.setImageResource(R.anim.turbo_search);
				final AnimationDrawable animationDrawable_search = (AnimationDrawable) reflashBtn.getDrawable();
				reflashBtn.post(new Runnable() {
					@Override
					public void run() {
						animationDrawable_search.start();
					}
				});

				// 定时扫描5秒
				if (scanningTimer != null) {
					scanningTimer.cancel();
					scanningTimer = null;
				}
				scanningTimer = new Timer();
				times = 0;
				scanningTimer.schedule(new TimerTask() {

					@Override
					public void run() {

						times++;
						if (times == 5) {
							// 取消动画
							runOnUiThread(new Runnable() {
								public void run() {
									if (imageview_devicesearch
											.getBackground() instanceof AnimationDrawable) {
										final AnimationDrawable animationDrawable = (AnimationDrawable) imageview_devicesearch
												.getBackground();
										imageview_devicesearch.post(new Runnable() {
													@Override
													public void run() {
														if (animationDrawable.isRunning()) {
															animationDrawable.stop();
														}
													}
												});

										final AnimationDrawable animationDrawable_search = (AnimationDrawable) reflashBtn
												.getDrawable();
										reflashBtn.post(new Runnable() {
											@Override
											public void run() {
												if (animationDrawable_search.isRunning()) {
													animationDrawable_search.stop();
												}
											}
										});

										System.out
												.println("执行scanningTimer");
										imageview_devicesearch
												.setBackgroundDrawable(null);
										reflashBtn.setImageResource(R.drawable.turbo_search_1);
									}
								}
							});
							scanningTimer.cancel();
							scanningTimer = null;
							times = 0;
						}

					}
				}, 0, 1000);

				scanDeviceService.findservice();

			}
		}
		
	}

	/**
	 * 初始化wifi信息
	 * 
	 * @param info
	 */
	private void initWifiInfo(WifiInfo info) {

		if (this.mainWifi.isWifiEnabled()) {
			if (info != null) {
				TextView wifiname = (TextView) this
						.findViewById(R.id.tb_wifiname);
				wifiname.setText(info.getSSID());
				ImageView wifiico = (ImageView) this
						.findViewById(R.id.tb_wifiico);
				wifiico.setImageResource(R.drawable.wirelessflag4);
				return;
			}
		}

		ImageView wifiico = (ImageView) this.findViewById(R.id.tb_wifiico);
		wifiico.setImageDrawable(null);
		TextView wifiname = (TextView) this.findViewById(R.id.tb_wifiname);
		wifiname.setText("");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SharedPreferences.Editor sharedata = getSharedPreferences("OwnDeviceInfo", 0).edit();  
			sharedata.putString("DeviceIP","");  
			sharedata.putString("DeviceName","");  
			sharedata.commit();
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 接收wifi广播类
	 * 
	 */
	class WifiReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (mainWifi != null) {
				WifiInfo wifiInfo = mainWifi.getConnectionInfo();
				initWifiInfo(wifiInfo);
			}

		}

	}

	/**
	 * 设备发现广播类
	 */
	private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (TurboFile.equals(action.trim())) {

				// 清除所有设备
				devicelist.clear();
				// 刷新设备列表
				String ownip = intToIp(
						mainWifi.getConnectionInfo().getIpAddress()).trim();
				if (mainWifi != null && mainWifi.isWifiEnabled()) {
					for (Entry<String, String> entry : ScanDeviceService.turbo_map
							.entrySet()) {

						System.out.println("lh~~~~~~~~设备名称:" + entry.getValue()
								+ "     IP:" + entry.getKey());
						try {
							TurboDeviceInfo info = new TurboDeviceInfo();
							info.setIp(entry.getKey());
							info.setDeviceName(entry.getValue());
							if (mainWifi.getConnectionInfo() != null
									&& entry.getKey().equals(ownip)) {
								currentDeviceInfo = info;
								SharedPreferences.Editor sharedata = getSharedPreferences("OwnDeviceInfo", 0).edit();  
								sharedata.putString("DeviceIP",currentDeviceInfo.getIp());  
								sharedata.putString("DeviceName",currentDeviceInfo.getDeviceName());  
								sharedata.commit();
							} else {
								devicelist.add(info);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					devicelistAdapter.notifyDataSetChanged();
					if (currentDeviceInfo != null) {
						TextView ownDevicenameView = (TextView) findViewById(R.id.tb_own_devicename);
						ownDevicenameView.setText(currentDeviceInfo.getDeviceName().trim());
					}
				}

				if (devicelistview.getBackground() instanceof AnimationDrawable) {
					final AnimationDrawable animationDrawable = (AnimationDrawable) devicelistview
							.getBackground();
					devicelistview.post(new Runnable() {
						@Override
						public void run() {
							if (animationDrawable.isRunning()) {
								animationDrawable.stop();
								animationDrawable.setVisible(false, false);
							}
						}
					});
				}
				// scanningTimer.cancel();
				// times=0;

			}
			ScanDeviceService.scaning = true;
		}
	};

	/**
	 * int类型ip地址转换为ipv4
	 * 
	 * @param i
	 * @return
	 */
	public String intToIp(int i) {
		String ip = null;
		try {
			ip = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
					+ ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ip == null ? "" : ip.trim();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiverWifi);
		unregisterReceiver(discoveryReceiver);

	}

}
