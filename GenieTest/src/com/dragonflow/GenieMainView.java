package com.dragonflow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.GenieLoginDialog.OnBackPressed;
import com.dragonflow.GenieLoginDialog.OnCanclePressed;
import com.dragonflow.GenieLoginDialog.OnLoginSucces;
import com.dragonflow.genie.ui.CaptureActivity;
import com.filebrowse.FileBrowseTab;
import com.filebrowse.FileService;
import com.filebrowse.ScanDeviceService;
import com.turbo.Turbo_DevicelistActivity;
import com.wifianalyzer.GenieChannels;
import com.wifianalyzer.GenieSignalGraph;
import com.wifianalyzer.GenieSignalHistogram;
import com.wifianalyzer.GenieWifiAct3;
import com.wifianalyzer.GenieWifiScan;
import com.wifianalyzer.WifiRoomSignal;
import com.dragonflow.genie.ui.R;

public class GenieMainView extends Activity implements Button.OnClickListener {
	// public GenieSoap soapCommand = null;
	public GenieUserInfo userInfo = null;
	public ProgressDialog progressDialog = null;
	public Toast toast = null;
	public Boolean wifiisable = false;
	public CheckBox box = null;
	private View login = null;

	private Boolean m_login_flag = true;

	private Boolean ClickFlag = false;

	private Boolean towifiset = false;

	private Boolean m_finish = false;

	private int MapPageIndex = 0;
	private LinearLayout pagelayout = null;

	public static PendingIntent pendingActivityIntent = null;
	public static AlarmManager alarmManager = null;

	private WifiManager.MulticastLock mMulticastLock = null;

	private int m_ClickID = -1;

	private ViewFlipper mViewFlipper;

	private View view_1 = null;
	private View view_2 = null;
	private ImageButton Wireless = null;
	private ImageButton GuestAccess = null;
	private ImageButton Map = null;
	private ImageButton Traffic = null;
	private ImageButton SpeedTest = null;
	private ImageButton Parental = null;

	private ImageButton ad2 = null;
	// private ImageButton search_frame = null;
	private Button search_bt = null;
	private EditText search_edit = null;

	private final int BaseID = 500;
	private final int LinearLayout_BaseID = 200;

	public Boolean IsNetgearRouter = true;

	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	private final int FP = ViewGroup.LayoutParams.FILL_PARENT;

	private ScrollLayout main_layout = null;

	private Boolean ishide = false;

	private Boolean m_create = false;

	public PopupWindow popupwindow = null;
	private View popupwindowview = null;

	// private GenieLoginDialog2 LoginDialog = null;

	private WifiManager wifiManager;//
	private WifiInfo wifiInfo;//
	public int ScreenWidth = 0;
	public int ScreenHeight = 0;

	public Intent intentfile = null;
	public Intent scanservice = null;

	public int ActivityResult[] = { GenieGlobalDefines.EFunctionResult_failure,
			GenieGlobalDefines.EFunctionResult_failure,
			GenieGlobalDefines.EFunctionResult_failure,
			GenieGlobalDefines.EFunctionResult_failure,
			GenieGlobalDefines.EFunctionResult_failure,
			GenieGlobalDefines.EFunctionResult_failure,
			GenieGlobalDefines.EFunctionResult_failure,
			GenieGlobalDefines.EFunctionResult_failure, };

	public String ActivityText[] = { "", "", "", "", "", "", "", "", };

	private GestureDetector mGestureDetector;

	private AlertDialog.Builder dialog_login = null;
	private AlertDialog.Builder dialog_disableguest = null;

	private GenieStatistician mGenieStatis = null;

	final public static int MESSAGE_DisableGuestTime = 9999;
	final public static int MESSAGE_SHOWOPENWIFIDIALOG = 10000;
	final public static int MESSAGE_INITDATA = 10001;

	private char m_savepassword;
	public static GenieMainView logingeniemainview=null;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			GenieDebug.error("debug", "main handleMessage!! msg.what = "
					+ msg.what);

			if (ishide || m_finish) {
				return;
			}

			switch (msg.what) {
			// case GenieGlobalDefines.ESoapRequestIsGenie:
			// IsNetgearRouter = true;
			// //showLoginDialog(false);
			// //ShowLoginProgressBarInVisible(); qicheng.ai
			// //ShowLoginProgressBarVisible();
			// if(m_ClickID !=
			// LinearLayout_BaseID+GenieGlobalDefines.EFunctionMyMedia)
			// {
			// Auto_Login(false);
			// }else
			// {
			// closeGetSettingWaitingDialog();
			// GotoActivity(m_ClickID);
			// }
			//
			//
			// break;
			//
			// case GenieGlobalDefines.ESoapRequestVerityUser:
			// IsNetgearRouter = true;
			// // TextView text = (TextView)findViewById(R.id.model);
			// // text.setText("Router:" +
			// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME));
			// //
			// // text = (TextView)findViewById(R.id.version);
			// // text.setText("Firmware:" +
			// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION));
			// // //closeWaitingDialog();
			// //CloseLoginDialog();
			//
			//
			//
			// InitBackText();
			//
			// DisableGuestTime();
			// closeGetSettingWaitingDialog();
			// if(!m_login_flag)
			// {
			// m_login_flag = true;
			// InitBackText();
			// GotoActivity(m_ClickID);
			// }
			//
			//
			// break;
			// // case GenieGlobalDefines.ESoapRequestNotGenie:
			// // IsNetgearRouter = false;
			// // ShowLoginAlartInfoVisible();
			// // //showLogin();
			// // break;
			// case GenieGlobalDefines.ESoapRequestNotNETGEARRouter:
			// IsNetgearRouter = false;
			// closeGetSettingWaitingDialog();
			// if(m_ClickID !=
			// LinearLayout_BaseID+GenieGlobalDefines.EFunctionMyMedia)
			// {
			// ShowLoginAlartInfoVisible(R.string.notnetgearrouter);
			// }else
			// {
			// ShowNotNetgearRouterDialog(R.string.notnetgearrouter);
			// }
			// GenieDebug.error("debug","router is not netgear");
			//
			// break;
			// case GenieGlobalDefines.ESoapRequestLoginRouterFailure:
			// //case GenieGlobalDefines.ESoapRequestfailure:
			// if(IsNetgearRouter)
			// {
			// ShowLoginBad_passwordVisible();
			// }else
			// {
			// ShowLoginAlartInfoVisible(R.string.notnetgearrouter);
			// }
			// break;
			case GenieGlobalDefines.ESoapRequestfailure:
				// ShowLoginAlartInfoVisible();
				// closeWaitingDialog();
				// ShowToast();
				break;
			case GenieGlobalDefines.ESoapRequestsuccess:
				// ShowLoginAlartInfoVisible();
				// closeWaitingDialog();
				break;
			case GenieGlobalDefines.PopupWindow:
				PopupPage();
				break;
			case MESSAGE_SHOWOPENWIFIDIALOG:
				ShowWiFiSetDialog();
				break;
			case MESSAGE_INITDATA:
				// getGuestAccessEnabledInfo();
				// mGenieStatis = new GenieStatistician(GenieMainView.this);
				break;
			// case MESSAGE_DisableGuestTime:
			// DisableGuestTime();
			// break;
			}
		}
	};

	public void getGuestAccessEnabledInfo() {
		GenieDebug.error("debug", " 99999 getGuestAccessEnabledInfo --0--");

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo test1 = new GenieRequestInfo();
		test1.aRequestLable = "GenieMainView";
		test1.aSoapType = GenieGlobalDefines.checkAvailabelPort;
		test1.aServer = "DeviceInfo";
		test1.aMethod = "GetInfo";
		test1.aNeedwrap = false;
		test1.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		test1.aNeedParser = false;
		test1.aTimeout = 20000;
		test1.aElement = new ArrayList<String>();
		requestinfo.add(test1);

		GenieRequestInfo currentsetting = new GenieRequestInfo();
		currentsetting.aRequestLable = "GenieMainView";
		currentsetting.aHttpType = GenieGlobalDefines.EHttpGetCurrentSetting;
		currentsetting.aRequestType = GenieGlobalDefines.RequestActionType.Http;
		currentsetting.aTimeout = 20000;
		requestinfo.add(currentsetting);

		GenieRequestInfo test = new GenieRequestInfo();
		test.aRequestLable = "GenieMainView";
		test.aSoapType = GenieGlobalDefines.ESoapRequestConfigFinish;
		test.aServer = "DeviceConfig";
		test.aMethod = "ConfigurationFinished";
		test.aNeedwrap = false;
		test.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		test.aNeedParser = false;
		test.aElement = new ArrayList<String>();
		test.aTimeout = 10000;
		requestinfo.add(test);

		GenieRequestInfo test2 = new GenieRequestInfo();
		test2.aRequestLable = "GenieMainView";
		test2.aSoapType = GenieGlobalDefines.ESoapRequestGuestEnable;
		test2.aServer = "WLANConfiguration";
		test2.aMethod = "GetGuestAccessEnabled";
		test2.aNeedwrap = false;
		test2.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		test2.aNeedParser = true;
		test.aTimeout = 20000;
		test2.aElement = new ArrayList<String>();
		test2.aElement.add("NewGuestAccessEnabled");
		test2.aElement.add("null");

		requestinfo.add(test2);
		requestinfo.add(test2);
		requestinfo.add(test2);
		requestinfo.add(test2);

		new GenieRequest(this, requestinfo).Start();

		// new Thread()
		// {
		// public void run()
		// {
		//
		// //GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME,"DGN2200");
		// //soapCommand.getWLanPassword();
		//
		// if(null != soapCommand)
		// {
		// soapCommand.checkAvailabelPort();
		// }
		// if(null != soapCommand)
		// {
		// soapCommand.getGuestAccessEnabled();
		// }
		//
		// GenieDebug.error("debug",
		// " 99999  DICTIONARY_KEY_GUEST_ABLE = "+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE));
		// //
		// if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1")
		// > -1)
		// // {
		// // sendMessage2UI(MESSAGE_DisableGuestTime);
		// // }
		//
		// }
		// }.start();
		//

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// qicheng.ai add if need stop it
		// **********************************************
		// if(mGenieStatis != null)
		// mGenieStatis.Stop();
		// **********************************************

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// GenieDebug.error("dispatchTouchEvent", "dispatchTouchEvent 1111111");
		boolean ret = false;

		try {
			ret = super.dispatchTouchEvent(ev);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			return false;
		} catch (Error e) {
			e.printStackTrace();
			return false;
		}

		// GenieDebug.error("dispatchTouchEvent",
		// "dispatchTouchEvent ClickFlag = "+ClickFlag);
		// GenieDebug.error("dispatchTouchEvent",
		// "dispatchTouchEvent ev.getAction() = "+ev.getAction());

		if (ClickFlag && (ev.getAction() == MotionEvent.ACTION_UP)) {

			for (int i = 0; i <= GenieGlobalDefines.EFunctionMax; i++) {
				LinearLayout temp = (LinearLayout) findViewById((LinearLayout_BaseID + i));
				temp.setBackgroundResource(R.drawable.bj_dz);
			}
			ClickFlag = false;
		}
		// GenieDebug.error("dispatchTouchEvent", "dispatchTouchEvent 2222222");

		setPage();

		return ret;
	}

	// private int pageid[] = {
	// R.drawable.page1,
	// R.drawable.page2,
	// };
	public void setPage() {
		if ((MapPageIndex > 0) && (null != main_layout) && (null != pagelayout)) {
			for (int i = 0; i < MapPageIndex; i++) {
				ImageView page = (ImageView) pagelayout.findViewById(i);
				if (i == main_layout.getCurScreen()) {
					page.setBackgroundResource(R.drawable.page_on);
				} else {
					page.setBackgroundResource(R.drawable.page_off);
				}
			}
		}
	}

	public void DismissPage() {
		if (null != popupwindow) {
			popupwindow.dismiss();
			popupwindow = null;
		}
	}

	private void PopupPageWindow() {
		if (MapPageIndex > 0) {
			Timer timer = new Timer();
			timer.schedule(new initPopupWindow(), 500);
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	private class initPopupWindow extends TimerTask {
		@Override
		public void run() {

			sendMessage2UI(GenieGlobalDefines.PopupWindow);
		}
	}

	public void PopupPage() {

		// 3321
		// DismissPage();

		// getWindow().getWindowManager().

		// if(pagelayout == null)
		// {
		// pagelayout = (LinearLayout)findViewById(R.id.popuppage);
		// if(pagelayout == null)
		// return ;
		// }

		pagelayout = (LinearLayout) findViewById(R.id.popuppage);
		pagelayout.removeAllViews();

		if (MapPageIndex > 0) {
			for (int j = 0; j < MapPageIndex; j++) {
				ImageView page = new ImageView(this);
				page.setId(j);
				if (j == main_layout.getCurScreen()) {
					page.setBackgroundResource(R.drawable.page_on);
				} else {
					page.setBackgroundResource(R.drawable.page_off);
				}

				pagelayout.addView(page, new ViewGroup.LayoutParams(WC, WC));
			}
			pagelayout.setVisibility(View.VISIBLE);
		} else {
			pagelayout.setVisibility(View.GONE);
		}
	}

	// public Boolean setGuestAccessDisabled()
	// {
	// Boolean Status = false;
	//
	// //soapCommand.configurationstarted();
	// Status = soapCommand.setGuestAccessEnabled();
	// //soapCommand.configurationfinished();
	//
	// //return Status;
	// return true;
	// }

	// public void DisableGuestAccess()
	// {
	//
	// showWaitingDialog();
	// GenieDebug.error("debug", "onMenuid_100 --1--");
	//
	// new Thread()
	// {
	// public void run()
	// {
	// GenieDebug.error("debug", "onMenuid_100 --run--");
	//
	// if(setGuestAccessDisabled())
	// {
	// sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
	// }else
	// {
	// sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
	// }
	//
	// }
	// }.start();
	//
	// }

	// public void ShowDisableGuestDialog()
	// {
	// dialog_disableguest = new AlertDialog.Builder(this)
	// .setIcon(R.drawable.icon)
	// .setTitle(R.string.guestaccess)
	// .setMessage(R.string.disableguest_text);
	//
	// dialog_disableguest.setPositiveButton(R.string.ok, new
	// DialogInterface.OnClickListener(){
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// DisableGuestAccess();
	// }});
	// dialog_disableguest.setNegativeButton(R.string.cancel, new
	// DialogInterface.OnClickListener(){
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	//
	// }});
	//
	//
	//
	// dialog_disableguest.setCancelable(false);
	//
	//
	// dialog_disableguest.show();
	// }

	public void DisableGuestTime() {
		long currenttime = 0;
		long time = 0;

		GenieDebug.error("debug", " 99999 DisableGuestTime 0");

		// if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1")
		// > -1)
		// {

		String GuestMac = null, DisableTime = null;

		HashMap<String, String> accesstime = GenieSerializ.ReadMap(this,
				"guestaccess");

		if (accesstime == null) {
			return;
		}

		GuestMac = accesstime.get("GUESTTIMEMAC");

		GenieDebug.error("DisableGuestTime",
				" 99999 DisableGuestTime  GuestMac = " + GuestMac);

		if (GuestMac == null || GuestMac.equals(GenieGlobalDefines.NULL)) {
			return;
		}

		DisableTime = accesstime.get("GUESTENDTIME");
		if (DisableTime == null || GuestMac.equals(GenieGlobalDefines.NULL)) {
			return;
		}

		GenieDebug.error("DisableGuestTime",
				" 99999 DisableGuestTime  DisableTime = " + DisableTime);

		String wlan_mac = GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
		GenieDebug.error("DisableGuestTime",
				" 99999 DisableGuestTime  wlan_mac = " + wlan_mac);

		// String TimeMac = new String(userInfo.TimeMac,0,userInfo.TimeMac[39]);

		String TimeMac = GuestMac;
		GenieDebug.error("DisableGuestTime",
				" 99999 DisableGuestTime  TimeMac = " + TimeMac);

		String disabletime = DisableTime;
		// String disabletime = new
		// String(userInfo.disabletime,0,userInfo.disabletime[39]);
		GenieDebug.error("DisableGuestTime",
				" 99999 DisableGuestTime  disabletime = " + disabletime);

		if (null != wlan_mac && null != TimeMac && null != disabletime
				&& wlan_mac.equals(TimeMac)
				&& !disabletime.equals(GenieGlobalDefines.NULL)) {
			currenttime = System.currentTimeMillis();
			GenieDebug.error("DisableGuestTime",
					" 99999 DisableGuestTime  currenttime = " + currenttime);
			// String disabletime = new
			// String(userInfo.disabletime,0,userInfo.disabletime[39]);
			// GenieDebug.error("DisableGuestTime","DisableGuestTime  disabletime = "+disabletime);

			time = Long.parseLong(disabletime);
			int second;
			if (time > currenttime) {
				second = (int) ((time - currenttime) / 1000);
			} else {
				second = 5;
			}

			// second = 30;

			GenieDebug.error("DisableGuestTime",
					" 99999 DisableGuestTime  second = " + second);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, second);

			if ((GenieMainView.alarmManager != null)
					&& (GenieMainView.pendingActivityIntent != null)) {
				GenieMainView.alarmManager
						.cancel(GenieMainView.pendingActivityIntent);
				GenieMainView.alarmManager = null;
				GenieMainView.pendingActivityIntent = null;
			}

			Intent intent = new Intent(this, GenieAlarmActivity.class);
			pendingActivityIntent = PendingIntent.getActivity(this, 0, intent,
					0);
			alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			// alarmManager.setRepeating(AlarmManager.RTC,0,5000,
			// pendingActivityIntent);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), pendingActivityIntent);

			// ShowDisableGuestDialog();

		}
		// }

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		closeWaitingDialog();
		// DismissPage();
		ishide = true;
		GenieDebug.error("onBackPressed", "closeWaitingDialog");
		// soapCommand = null;
		try {
			super.onBackPressed();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private int keysize = 0;
	private boolean m_key = false;
	private boolean m_media = false;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub

		GenieDebug.error("debug", "KeyEventCode = " + event.getKeyCode());

		boolean ret = super.dispatchKeyEvent(event);

		GenieDebug.error("debug", "KeyEventAction = " + event.getAction());

		if (event.getAction() == KeyEvent.ACTION_UP) {
			if (keysize < 3 && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
				keysize++;
			} else if (keysize >= 3 && keysize < 6
					&& event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
				keysize++;
				if (keysize == 6) {
					GenieDebug.error("debug", "KeyEvent keysize = 6");
					m_key = true;
				}
			} else {
				keysize = 0;
				m_key = false;
			}
			GenieDebug.error("debug", "KeyEvent keysize = " + keysize);
		}

		return ret;
	}

	// @Override
	// public void onCreate(Bundle savedInstanceState)
	// {
	// super.onCreate(savedInstanceState);
	//
	//
	// ScrollView scrollview = new ScrollView(this);
	//
	// LinearLayout layout = new LinearLayout(this);
	// layout.setOrientation(LinearLayout.VERTICAL);
	//
	// LinearLayout.LayoutParams layoutParams = new
	// LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth()/3,2*getWindowManager().getDefaultDisplay().getWidth()/5);
	// //
	// LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	//
	// int i , j;
	// for(i = 0; i < 3; i++)
	// {
	// LinearLayout layout1 = new LinearLayout(this);
	// layout1.setOrientation(LinearLayout.HORIZONTAL);
	//
	// for(j = 0; j < 3; j++)
	// {
	// LinearLayout layout3 = new LinearLayout(this);
	// layout3.setOrientation(LinearLayout.VERTICAL);
	//
	// Button button = new Button(this);
	// TextView view = new TextView(this);
	//
	// button.setId(3*i + j);
	// button.setOnClickListener(this);
	// view.setWidth(getWindowManager().getDefaultDisplay().getWidth()/3);
	// view.setGravity(Gravity.CENTER);
	//
	// int imageID = 0;
	// int textID = 0;
	//
	// switch(3*i + j)
	// {
	// case GenieGlobalDefines.EFunctionWireless:
	// imageID = R.drawable.wireless;
	// textID = R.string.wireless;
	// break;
	//
	// case GenieGlobalDefines.EFunctionGuestAccess:
	// imageID = R.drawable.guestaccess;
	// textID = R.string.guestaccess;
	// break;
	//
	// case GenieGlobalDefines.EFunctionMap:
	// imageID = R.drawable.map;
	// textID = R.string.map;
	// break;
	//
	// case GenieGlobalDefines.EFunctionTraffic:
	// imageID = R.drawable.traffic;
	// textID = R.string.traffic;
	// break;
	//
	// case GenieGlobalDefines.EFunctionSpeedTest:
	// imageID = R.drawable.speedtest;
	// textID = R.string.speedtest;
	// break;
	//
	// case GenieGlobalDefines.EFunctionReadyShare:
	// imageID = R.drawable.readyshare;
	// textID = R.string.readyshare;
	// break;
	//
	// case GenieGlobalDefines.EFunctionParental:
	// imageID = R.drawable.parentalcontrols;
	// textID = R.string.parentalcontrl;
	// break;
	//
	// }
	//
	// button.setBackgroundResource(imageID);
	// view.setText(textID);
	//
	// layout3.addView(button);
	// layout3.addView(view);
	//
	// layout1.addView(layout3, layoutParams);
	//
	// if(i == 2) break;
	// }
	//
	// layout.addView(layout1);
	// }
	//
	// scrollview.addView(layout);
	//
	// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	// setContentView(scrollview);
	// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
	//
	// Log.i("onCreate", "onCreate --0--");
	//
	// showWaitingDialog();
	//
	// Log.i("onCreate", "onCreate --1--");
	//
	// getInitialData();
	//
	// Log.i("onCreate", "onCreate --2--");
	// }

	// public static class Netgear_IpAddressTranfer {
	// public static int str2Ip(String ip) throws UnknownHostException {
	// InetAddress address = InetAddress.getByName(ip);//
	// 鍦ㄧ粰瀹氫富鏈哄悕鐨勬儏鍐典笅纭畾涓绘満鐨�// // IP 鍧��
	// byte[] bytes = address.getAddress();// 杩斿洖姝�InetAddress 瀵硅薄鐨勫師濮�IP 鍦板潃
	// int a, b, c, d;
	// a = byte2int(bytes[0]);
	// b = byte2int(bytes[1]);
	// c = byte2int(bytes[2]);
	// d = byte2int(bytes[3]);
	// int result = (a << 24) | (b << 16) | (c << 8) | d;
	// return result;
	// }
	// public static int byte2int(byte b) {
	// int l = b & 0x07f;
	// if (b < 0) {
	// l |= 0x80;
	// }
	// return l;
	// }
	// public long ip2long(String ip) throws UnknownHostException {
	// int ipNum = str2Ip(ip);
	// return int2long(ipNum);
	// }
	// public long int2long(int i) {
	// long l = i & 0x7fffffffL;
	// if (i < 0) {
	// l |= 0x080000000L;
	// }
	// return l;
	// }
	// public String long2ip(long ip) {
	// int[] b = new int[4];
	// b[0] = (int) ((ip >> 24) & 0xff);
	// b[1] = (int) ((ip >> 16) & 0xff);
	// b[2] = (int) ((ip >> 8) & 0xff);
	// b[3] = (int) (ip & 0xff);
	// String x;
	// x = Integer.toString(b[3]) + "." + Integer.toString(b[2]) + "."
	// + Integer.toString(b[1]) + "." + Integer.toString(b[0]);
	// return x;
	// }
	// /****
	// *
	// *
	// * @return
	// * @throws Exception
	// */
	// public String getDefaultGatewayIp() throws Exception {
	// try {
	// Process result = Runtime.getRuntime().exec("su");
	// BufferedReader output = new BufferedReader(new InputStreamReader(
	// result.getInputStream()));
	// String line = output.readLine();
	// while (line != null) {
	// Log.e("new line is ", line);
	// line = output.readLine();
	// }
	// } catch (Exception e) {
	// System.out.println(e.toString());
	// }
	// return null;
	// }
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * 启动传送文件服务
		 */

		// //创建一个Intent对象
		intentfile = new Intent();
		// //第一个参数是自己的这个类的对象，第二个参数是要调用的Service的对象
		intentfile.setClass(GenieMainView.this, FileService.class);
		// //启动服务
		startService(intentfile);

		/**
		 * 启动扫描设备服务
		 */

		// //创建一个Intent对象
		scanservice = new Intent();
		// //第一个参数是自己的这个类的对象，第二个参数是要调用的Service的对象
		scanservice.setClass(GenieMainView.this, ScanDeviceService.class);
		// //启动服务
		startService(scanservice);

		// setTheme(R.style.maintitle);
		//
		// getWindow().setFlags(flags, mask)

		// Intent it = getIntent();
		// if (it != null && it.getAction() != null &&
		// it.getAction().equals(Intent.ACTION_SEND))
		// {
		// Bundle extras = it.getExtras();
		// if (extras.containsKey("android.intent.extra.STREAM"))
		// {
		// GenieDebug.error("debug","Intent extras ="+
		// extras.get("android.intent.extra.STREAM"));
		// Uri uri = (Uri) extras.get("android.intent.extra.STREAM");
		// GenieDebug.error("debug","Intent uri ="+ uri.toString());
		//
		//
		// Cursor cursor = this.getContentResolver().query(uri, null, null,
		// null, null);
		//
		// cursor.moveToFirst();
		// for (int i = 0; i < cursor.getColumnCount(); i++) {
		// /*_data*/
		// GenieDebug.error("debug","Intent uri ="+i+"-"+cursor.getColumnName(i)+"-"+cursor.getString(i));
		// }
		//
		//
		// }
		// }
		//
		// //Bundle Extras = getIntent().getExtras();
		// Set<String> Categories = getIntent().getCategories();
		// String Type = getIntent().getType();
		// String Action = getIntent().getAction();
		// String Scheme = getIntent().getScheme();
		// String data = getIntent().getDataString();
		//
		//
		// //GenieDebug.error("debug","Intent Extras.toString() = "+Extras.toString());
		// if(null != Categories)
		// {
		// for(String test : Categories)
		// {
		// GenieDebug.error("debug","Intent Categories test = "+test);
		// }
		// }
		// GenieDebug.error("debug","Intent Type = "+Type);
		// GenieDebug.error("debug","Intent Action = "+Action);
		// GenieDebug.error("debug","Intent Scheme = "+Scheme);
		// GenieDebug.error("debug","Intent Data = "+data);

		// Build bd = new Build();
		//
		// String RELEASE = Build.VERSION.RELEASE;
		// String CODENAME = Build.VERSION.CODENAME;
		// String SDK = Build.VERSION.SDK;
		// String INCREMENTAL = Build.VERSION.INCREMENTAL;
		// GenieDebug.error("debug","Build RELEASE = "+RELEASE);
		// GenieDebug.error("debug","Build CODENAME = "+CODENAME);
		// GenieDebug.error("debug","Build SDK = "+SDK);
		// GenieDebug.error("debug","Build INCREMENTAL = "+INCREMENTAL);
		//
		// String BOARD = bd.BOARD;
		// GenieDebug.error("debug","Build BOARD = "+BOARD);
		// String BOOTLOADER = bd.BOOTLOADER;
		// GenieDebug.error("debug","Build BOOTLOADER = "+BOOTLOADER);
		// String BRAND = bd.BRAND;
		// GenieDebug.error("debug","Build BRAND = "+BRAND);
		// String CPU_ABI = bd.CPU_ABI;
		// GenieDebug.error("debug","Build CPU_ABI = "+CPU_ABI);
		// String CPU_ABI2 = bd.CPU_ABI2;
		// GenieDebug.error("debug","Build CPU_ABI2 = "+CPU_ABI2);
		// String DEVICE = bd.DEVICE;
		// GenieDebug.error("debug","Build DEVICE = "+DEVICE);
		// String DISPLAY = bd.DISPLAY;
		// GenieDebug.error("debug","Build DISPLAY = "+DISPLAY);
		// String FINGERPRINT = bd.FINGERPRINT;
		// GenieDebug.error("debug","Build FINGERPRINT = "+FINGERPRINT);
		// String HARDWARE = bd.HARDWARE;
		// GenieDebug.error("debug","Build HARDWARE = "+HARDWARE);
		// String HOST = bd.HOST;
		// GenieDebug.error("debug","Build HOST = "+HOST);
		// String ID = bd.ID;
		// GenieDebug.error("debug","Build ID = "+ID);
		// String MANUFACTURER = bd.MANUFACTURER;
		// GenieDebug.error("debug","Build MANUFACTURER = "+MANUFACTURER);
		// String MODEL = bd.MODEL;
		// GenieDebug.error("debug","Build MODEL = "+MODEL);
		// String PRODUCT = bd.PRODUCT;
		// GenieDebug.error("debug","Build PRODUCT = "+PRODUCT);
		// String RADIO = bd.RADIO;
		// GenieDebug.error("debug","Build RADIO = "+RADIO);
		// String TAGS = bd.TAGS;
		// GenieDebug.error("debug","Build TAGS = "+TAGS);
		// String TYPE = bd.TYPE;
		// GenieDebug.error("debug","Build TYPE = "+TYPE);
		// String UNKNOWN = bd.UNKNOWN;
		// GenieDebug.error("debug","Build UNKNOWN = "+UNKNOWN);
		// String USER = bd.USER;
		// GenieDebug.error("debug","Build USER = "+USER);
		//

		// getWindow().setBackgroundDrawableResource(R.color.Black);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();

		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);

		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			setTheme(R.style.bigactivityTitlebar);
		} else {
			setTheme(R.style.activityTitlebar);
		}
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		m_login_flag = false;
		ishide = false;
		m_ClickID = -1;

		m_finish = false;

		m_create = true;

		keysize = 0;
		m_key = false;
		m_media = false;

		GenieDebug.error("onCreate", "onCreate --00--");
		requestWindowFeature(Window.FEATURE_NO_TITLE
				| Window.FEATURE_CUSTOM_TITLE);
		GenieDebug.error("onCreate", "onCreate --01--");
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		GenieDebug.error("onCreate", "onCreate --02--");
		setContentView(R.layout.main);
		GenieDebug.error("onCreate", "onCreate --03--");
		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.maintitlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.maintitlebar);
		}
		GenieDebug.error("onCreate", "onCreate --04--");

		// CookieManager cookieManager = new CookieManager();

		// CookieSyncManager.createInstance(this);
		// CookieManager cookieManager = CookieManager.getInstance();
		// cookieManager.removeAllCookie();

		pagelayout = (LinearLayout) findViewById(R.id.popuppage);

		InitMain();
		logingeniemainview=this;
		// getGuestAccessEnabledInfo();

		// InitTitleView();
		//
		// LinearLayout titler = (LinearLayout)findViewById(R.id.titler);
		// int Width = titler.getWidth();
		// int Height= titler.getHeight();
		//
		// GenieDebug.error("titler ","Width = "+Width);
		// GenieDebug.error("titler","Height = "+Height);
		//
		//
		// String Clickhere = getResources().getString(R.string.Clickhere);
		//
		// for(int i = 0;i < ActivityText.length;i++)
		// {
		// ActivityText[i] = Clickhere;
		// }
		//
		//
		// GenieDebug.error("onCreate", "onCreate --0--");
		//
		// GenieDebug.error("onCreate", "onCreate --1--");
		//
		// wifiManager = (WifiManager)
		// this.getSystemService(Context.WIFI_SERVICE);// 锟斤拷取Wifi锟斤拷锟斤拷
		// //
		// wifiInfo = wifiManager.getConnectionInfo();
		//
		// if(wifiManager.isWifiEnabled())
		// {
		// Log.i("onCreate", "onCreate --2--");
		// wifiisable = true;
		//
		// }else
		// {
		// Log.i("onCreate", "onCreate --3--");
		// wifiisable = false;
		//
		// }
		//
		// //view.setWidth(getWindowManager().getDefaultDisplay().getWidth()/3);
		//
		// Log.i("onCreate",
		// "orientation ="+getResources().getConfiguration().orientation);
		//
		// //InitView();
		// InitMainView();
		// showWaitingDialog();
		//
		// userInfo = GenieShareAPI.ReadDataFromFile(this,
		// GenieGlobalDefines.USER_INFO_FILENAME);
		//
		// towifiset = false;
		//
		// if(wifiManager.isWifiEnabled())
		// {
		// Log.i("onCreate", "onCreate --2--");
		// wifiisable = true;
		// getInitialData();
		// }else
		// {
		// Log.i("onCreate", "onCreate --3--");
		// wifiisable = false;
		// closeWaitingDialog();
		// //toast();
		// ShowWiFiSetDialog();
		// }
		//
		//
		// Log.i("onCreate", "onCreate --4--");
	}

	private void InitMain() {

		IsNetgearRouter = true;
		InitTitleView();

		RelativeLayout titler = (RelativeLayout) findViewById(R.id.titler);
		int Width = titler.getWidth();
		int Height = titler.getHeight();

		GenieDebug.error("titler ", "Width = " + Width);
		GenieDebug.error("titler", "Height = " + Height);

		String Clickhere = "";// qicheng.ai change
								// getResources().getString(R.string.Clickhere);

		for (int i = 0; i < ActivityText.length; i++) {
			ActivityText[i] = "";// qicheng.ai change Clickhere;
		}

		GenieDebug.error("onCreate", "onCreate --0--");

		GenieDebug.error("onCreate", "onCreate --1--");

		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		mMulticastLock = wifiManager.createMulticastLock("dlna_mc_lock");
		mMulticastLock.acquire();

		wifiInfo = wifiManager.getConnectionInfo();

		GenieDebug.error("onCreate",
				"onCreate wifiInfo getRssi = " + wifiInfo.getRssi());
		GenieDebug.error("onCreate", "onCreate wifiInfo getLinkSpeed = "
				+ wifiInfo.getLinkSpeed());

		// WifiManagerment test = new WifiManagerment(this);
		// test.ConnectWifi("NETGEAR_WNDR3800_Online1", "12345678");
		//
		// try {
		// Thread.sleep(10000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// GenieDebug.error("onCreate", "---------------------------------");

		GenieSoap.initialDictionaryElements();

		// if(wifiManager.isWifiEnabled())
		// {
		// GenieDebug.error("onCreate", "onCreate --2--");
		// wifiisable = true;
		//
		// GenieSoap.initialDictionaryElements();
		// }else
		// {
		// GenieDebug.error("onCreate", "onCreate --3--");
		// wifiisable = false;
		//
		// }

		// view.setWidth(getWindowManager().getDefaultDisplay().getWidth()/3);

		GenieDebug.error("onCreate", "orientation ="
				+ getResources().getConfiguration().orientation);

		// InitView();
		InitMainView();
		// showWaitingDialog();

		userInfo = GenieShareAPI.ReadDataFromFile(this,
				GenieGlobalDefines.USER_INFO_FILENAME);

		towifiset = false;

		wifiisable = true;

		mGenieStatis = new GenieStatistician(this);

		// if(wifiManager.isWifiEnabled())
		// {
		// GenieDebug.error("onCreate", "onCreate --2--");
		// wifiisable = true;
		//
		// mGenieStatis = new GenieStatistician(this);
		//
		// }else
		// {
		// GenieDebug.error("onCreate", "onCreate --3--");
		// wifiisable = false;
		// closeWaitingDialog();
		//
		// sendMessage2UI(MESSAGE_SHOWOPENWIFIDIALOG);
		//
		// }

		GenieDebug.error("onCreate", "onCreate --4--");
		/****************** Allen 添加测试代码      **************/
		ToLoginView(-1);
		/****************** Allen 结束  *******************/
	}

	public void CheckAvailabelPort() {
		GenieDebug.error("debug", " 99999 getGuestAccessEnabledInfo --0--");

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		// GenieRequestInfo test1 = new GenieRequestInfo();
		// test1.aRequestLable="GenieMainView";
		// test1.aSoapType = GenieGlobalDefines.checkAvailabelPort;
		// test1.aServer = "DeviceInfo";
		// test1.aMethod = "GetInfo";
		// test1.aNeedwrap = true;
		// test1.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		// test1.aNeedParser=false;
		// test1.aTimeout = 20000;
		// test1.aElement = new ArrayList<String>();
		// requestinfo.add(test1);

		GenieRequestInfo test = new GenieRequestInfo();
		test.aRequestLable = "GenieMainView";
		test.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
		test.aServer = "ParentalControl";
		test.aMethod = "Authenticate";
		test.aNeedwrap = false;
		test.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		test.aNeedParser = false;
		test.aTimeout = 20000;
		test.aElement = new ArrayList<String>();
		test.aElement.add("NewUsername");
		test.aElement.add("admin");
		test.aElement.add("NewPassword");
		test.aElement.add("password");

		requestinfo.add(test);

		GenieRequest check = new GenieRequest(this, requestinfo);
		check.SetProgressInfo(true, true);
		check.Start();
	}

	// private void PopupPageWindow()
	// {
	// if(MapPageIndex > 0)
	// {
	// Timer timer = new Timer();
	// timer.schedule(new initPopupWindow(), 500);
	// // try {
	// // Thread.sleep(2000);
	// // } catch (InterruptedException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// }
	// }

	private void sendinitdateMessage() {

		Timer timer = new Timer();
		timer.schedule(new initdata(), 1000);

	}

	private class initdata extends TimerTask {
		@Override
		public void run() {

			sendMessage2UI(MESSAGE_INITDATA);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (towifiset) {
			InitMain();
			towifiset = false;
			//
		}
		ishide = false;
		GenieDebug.error("onResume", "onResume --0---------------0--");
		// PopupPageWindow();

		if (m_create && wifiisable) {
			// getGuestAccessEnabledInfo();
			// mGenieStatis = new GenieStatistician(this);

			// sendinitdateMessage();

		}

		if (m_create) {
			m_create = false;

		} else {
			// getGuestAccessEnabledInfo();
		}

	}

	protected void onStart() {
		// TODO Auto-generated method stub

		super.onStart();

		RegisterBroadcastReceiver();

	}

	public void onDestroy() {
		super.onDestroy();
		stopService(intentfile);
		UnRegisterBroadcastReceiver();
		stopService(scanservice);
		Intent Dlna = new Intent("com.netgear.ComNETGEARGenieDlnaService");
		stopService(Dlna);

		// DismissPage();
		if (mMulticastLock != null) {
			mMulticastLock.release();
		}

	}

	private void ShowWiFiSetDialog() {
		AlertDialog.Builder dialog_wifiset = new AlertDialog.Builder(this);

		dialog_wifiset.setIcon(android.R.drawable.ic_dialog_alert);
		dialog_wifiset.setTitle(R.string.login);
		dialog_wifiset.setMessage(R.string.wifisettext);
		dialog_wifiset.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						towifiset = true;
						Intent intent = new Intent(
								Settings.ACTION_WIFI_SETTINGS);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						// thisfinish();
					}
				});

		dialog_wifiset.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						thisfinish();
					}
				});

		dialog_wifiset.setCancelable(false);
		dialog_wifiset.show();

	}

	public void showLicenceDialog() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.aboutlicense, null);

		AlertDialog.Builder dialog = new AlertDialog.Builder(this)
				.setIcon(R.drawable.icon).setTitle(R.string.license)
				// .setMessage(content)
				.setView(view)
				.setNegativeButton(R.string.close,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});

		String license_genie = null;
		Resources resources = this.getResources();
		InputStream is = null;
		try {
			is = resources.openRawResource(R.raw.genie_license);
			byte buffer[] = new byte[is.available()];
			is.read(buffer);
			license_genie = new String(buffer);
			GenieDebug.error("licence", "read:" + license_genie);
		} catch (IOException e) {
			// Log.e("licence", "write file",e);
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// Log.e("licence", "close file",e);
					e.printStackTrace();
				}
			}
		}

		TextView aboutinfo = (TextView) view.findViewById(R.id.license_genie);
		aboutinfo.setText(license_genie);

		String license_neptune = null;
		Resources resources_neptune = this.getResources();
		InputStream is_neptune = null;
		InputStream is_zxing = null;
		try {
			is_neptune = resources_neptune.openRawResource(R.raw.license);
			byte buffer[] = new byte[is_neptune.available()];
			is_neptune.read(buffer);
			license_neptune = new String(buffer);
			GenieDebug.error("licence", "read:" + license_neptune);

			// is_zxing=resources_neptune.openRawResource(R.raw.zxinglicense);
			// byte buffer2[]=new byte[is_zxing.available()];
			// is_neptune.read(buffer2);
			// license_neptune=license_neptune + new String(buffer2);
		} catch (IOException e) {
			// Log.e("licence", "write file",e);
			e.printStackTrace();
		} finally {
			if (is_neptune != null) {
				try {
					is_neptune.close();
				} catch (IOException e) {
					// Log.e("licence", "close file",e);
					e.printStackTrace();
				}
			}
		}

		TextView aboutinfo2 = (TextView) view.findViewById(R.id.license_other);
		aboutinfo2.setText(license_neptune);

		dialog.show();

	}

	private AlertDialog aboutdialog = null;

	public void OnClickAbout() {

		// /////////////////////////////////////////////////////////
		PackageManager manager = this.getPackageManager();

		PackageInfo info = null;
		String packageName = null;
		int versionCode = 0;
		String versionName = null;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);

			packageName = info.packageName;

			versionCode = info.versionCode;

			versionName = info.versionName;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GenieDebug.error("OnClickAbout", "packageName = " + packageName);
		GenieDebug.error("OnClickAbout", "versionCode = " + versionCode);
		GenieDebug.error("OnClickAbout", "versionName = " + versionName);

		// //////////////////////////////////////////////////////////////

		// String text =
		// getResources().getString(R.string.version)+" "+versionName+"."+versionCode+"\n"+getResources().getString(R.string.about_info);
		String text = getResources().getString(R.string.version) + " "
				+ versionName + "\n"
				+ getResources().getString(R.string.about_info);

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.about, null);

		TextView aboutinfo = (TextView) view.findViewById(R.id.about_tv);
		aboutinfo.setTextSize(16);
		aboutinfo.setText(text);

		// 隐私权限
		String linkstr = "<a href=\"http://www.netgear.com/about/privacypolicy/\">"
				+ getResources().getString(R.string.privacy_policy) + "</a>";
		TextView pp = (TextView) view.findViewById(R.id.about_privacypolicy);
		pp.setTextSize(16);
		pp.setText(Html.fromHtml(linkstr));
		pp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Uri uri = Uri
						.parse("http://www.netgear.com/about/privacypolicy/");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});

		// 技术支持
		String poweredby = getResources().getString(R.string.powered_by);
		TextView pb = (TextView) view.findViewById(R.id.about_poweredby);
		pb.setTextSize(16);
		pb.setText(poweredby);

		TextView model = (TextView) view.findViewById(R.id.about_model);
		model.setTextSize(22);
		if (m_login_flag) {
			model.setText(getResources().getString(R.string.routermodel)
					+ ": "
					+ GenieSoap.dictionary
							.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME));
		} else {
			model.setText(getResources().getString(R.string.routermodel) + ": "
					+ GenieGlobalDefines.NULL);
		}
		TextView version = (TextView) view.findViewById(R.id.about_version);
		version.setTextSize(22);

		String Version_text = getResources()
				.getString(R.string.frimwareversion)
				+ ": "
				+ GenieSoap.dictionary
						.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION);

		GenieDebug.error("debug", "Version_text = " + Version_text);

		int posE = 0;
		StringBuffer temp = new StringBuffer(Version_text);
		String str = null;

		posE = temp.indexOf("-");
		if (posE > -1) {
			str = temp.substring(0, posE);
		} else {
			posE = temp.indexOf("_");
			if (posE > -1) {
				str = temp.substring(0, posE);
			} else {
				str = temp.substring(0, temp.length());
			}
		}

		if (null != str) {
			version.setText(str);
		}

		if (!m_login_flag) {
			version.setText(getResources().getString(R.string.frimwareversion)
					+ ": " + GenieGlobalDefines.NULL);
		}

		// Button close = (Button)view.findViewById(R.id.about_cancel);
		// close.setText(R.string.close);
		// close.setOnClickListener(new OnClickListener(){
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if(null != aboutdialog)
		// {
		// aboutdialog.dismiss();
		// aboutdialog = null;
		// }
		// }
		//
		// });

		AlertDialog.Builder dialog_login = new AlertDialog.Builder(this)
				.setView(view)
				.setTitle(R.string.about)
				.setIcon(R.drawable.icon)
				.setNegativeButton(R.string.license,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								showLicenceDialog();
							}
						})
				.setPositiveButton(R.string.close,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});
		// dialog_login.setNegativeButton(R.string.cancel,new
		// DialogInterface.OnClickListener()
		// {

		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub

		// }

		// });

		aboutdialog = dialog_login.create();
		aboutdialog.show();

		// Button tempp =
		// (Button)testdialog.getWindow().findViewById(AlertDialog.BUTTON_NEGATIVE);
		// GenieDebug.error("OnClickAbout","OnClickAbout 0");
		// Button tempp = testdialog.getButton(AlertDialog.BUTTON_NEGATIVE);

		// Button tempp = testdialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		// GenieDebug.error("OnClickAbout","OnClickAbout 1");
		// tempp.setEnabled(false);
		// tempp.setLayoutParams(new
		// ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT));
		GenieDebug.error("OnClickAbout", "OnClickAbout 2");
		// testdialog.show();
	}

	private Activity GetActivity() {
		return this;
	}

	public void InitBackText() {
		Button back = (Button) findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.title_bt_bj);
		if (m_login_flag) {
			back.setText(R.string.logout);
		} else {
			back.setText(R.string.login);
		}
	}

	// qicheng.ai add
	public void OnBackClick() {
		if (m_login_flag) {
			m_login_flag = false;
			GenieRequest.m_SmartNetWork = false;
			InitBackText();

			String Clickhere = "";// qicheng.ai change
									// getResources().getString(R.string.Clickhere);
			for (int i = 0; i < ActivityText.length; i++) {
				ActivityText[i] = "";// qicheng.ai change Clickhere;
			}

			for (int i = 0; i < ActivityResult.length; i++) {
				ActivityResult[i] = GenieGlobalDefines.EFunctionResult_failure;
			}

			// onActivityResult_setview(null,100);

		} else {
			m_ClickID = -1;
			// ShowLoginDialog();

		}
	}

	public void InitTitleView() {

		ImageView netgearlog = (ImageView) findViewById(R.id.netgear);

		if (GenieGlobalDefines.GetSmartNetworkFlag(this) == 1) {
			netgearlog.setBackgroundResource(R.drawable.genie_new);
		} else {
			netgearlog.setBackgroundResource(R.drawable.genie);
		}

		Button back = (Button) findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.title_bt_bj);

		Button about = (Button) findViewById(R.id.about);
		about.setBackgroundResource(R.drawable.title_bt_bj);

		if (m_login_flag) {
			back.setText(R.string.logout);
		} else {
			back.setText(R.string.login);
		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// OnBackClick();

				ShowSmartNetworkLoginView();

			}
		});

		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				OnClickAbout();
				// CheckAvailabelPort();
			}
		});

		back.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					//
					v.setBackgroundResource(R.drawable.title_bt_fj);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					//
					v.setBackgroundResource(R.drawable.title_bt_bj);

				}
				return false;
			}
		});

		about.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					//
					v.setBackgroundResource(R.drawable.title_bt_fj);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					//
					v.setBackgroundResource(R.drawable.title_bt_bj);

				}
				return false;
			}
		});

	}

	public void SetBackgroundResource_down(View v) {
		int imageID = -1;
		switch (v.getId()) {
		case GenieGlobalDefines.EFunctionWireless:
			imageID = R.drawable.wireless;
			break;

		case GenieGlobalDefines.EFunctionGuestAccess:
			imageID = R.drawable.guestaccess;
			break;

		case GenieGlobalDefines.EFunctionMap:
			imageID = R.drawable.map;
			break;

		case GenieGlobalDefines.EFunctionTraffic:
			imageID = R.drawable.traffic;
			break;

		case GenieGlobalDefines.EFunctionQRCode:
			imageID = R.drawable.qrcode;
			break;
		// case GenieGlobalDefines.EFunctionSignalStrength:
		// imageID = R.drawable.signal_strength;
		// break;
		// wifi analyzer
		// case GenieGlobalDefines.EFunctionWifiAnalyzer:{
		// imageID=R.drawable.signal_strength;
		// break;
		// }
		case GenieGlobalDefines.EFunctionGenieAppStaging:
			imageID = R.drawable.appstore;
			break;
		case GenieGlobalDefines.EFunctionSpeedTest:
			break;

		case GenieGlobalDefines.EFunctionMyMedia:
			imageID = R.drawable.mymedia;
			break;

		/**
		 * 新增加文件浏览功能
		 */
		// case GenieGlobalDefines.EFunctionFileBrowse:
		// imageID = R.drawable.file;
		// break;


		/**
		 * 新增文件传输功能
		 */

//		case GenieGlobalDefines.EFunctionFileTransfer:
//			imageID = R.drawable.transfer_wifi_icon;
//			break;

		case GenieGlobalDefines.EFunctionParental:
			imageID = R.drawable.parentalcontrols;
			break;

		}

		if (-1 != imageID) {
			v.setBackgroundResource(imageID);
		}
	}

	public void SetBackgroundResource_up(View v) {
		int imageID = -1;
		switch (v.getId()) {
		case GenieGlobalDefines.EFunctionWireless:
			imageID = R.drawable.wireless;
			break;

		case GenieGlobalDefines.EFunctionGuestAccess:
			imageID = R.drawable.guestaccess;
			break;

		case GenieGlobalDefines.EFunctionMap:
			imageID = R.drawable.map;
			break;

		case GenieGlobalDefines.EFunctionTraffic:
			imageID = R.drawable.traffic;
			break;

		case GenieGlobalDefines.EFunctionQRCode:
			imageID = R.drawable.qrcode;
			break;
		// case GenieGlobalDefines.EFunctionSignalStrength:
		// imageID = R.drawable.signal_strength;
		// break;
		// wifi analyzer
		// case GenieGlobalDefines.EFunctionWifiAnalyzer:{
		// imageID=R.drawable.signal_strength;
		// break;
		// }
		case GenieGlobalDefines.EFunctionGenieAppStaging:
			imageID = R.drawable.appstore;
			break;
		case GenieGlobalDefines.EFunctionSpeedTest:
			break;

		case GenieGlobalDefines.EFunctionMyMedia:
			imageID = R.drawable.mymedia;
			break;

		/**
		 * 新增加文件浏览功能
		 */
		// case GenieGlobalDefines.EFunctionFileBrowse:
		// imageID = R.drawable.file;
		// break;

		/**
		 * 新增文件传输功能
		 */
//		case GenieGlobalDefines.EFunctionFileTransfer:
//			imageID = R.drawable.transfer_wifi_icon;
//			break;

		case GenieGlobalDefines.EFunctionParental:
			imageID = R.drawable.parentalcontrols;
			break;

		}

		if (-1 != imageID) {
			v.setBackgroundResource(imageID);
		}
	}

	public void InitLandView2() {
		// DismissPage();

		search_bt = (Button) this.findViewById(R.id.search);
		search_edit = (EditText) this.findViewById(R.id.main_input);
		search_bt.setOnClickListener(this);

		main_layout = (ScrollLayout) findViewById(R.id.ScrollLayout);

		int Width = main_layout.getWidth();
		int Height = main_layout.getHeight();

		GenieDebug.error("main_layout ", "Width = " + Width);
		GenieDebug.error("main_layout", "Height = " + Height);

		// layout.setGravity(Gravity.CENTER);

		int row, column;
		row = 2;
		column = 3;

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int titlerhight = (int) (100 * metrics.scaledDensity);

		Display d = getWindowManager().getDefaultDisplay();
		final int Window_w = d.getWidth();
		final int Window_h = d.getHeight();

		int temp_w, temp_h, x;

		float textsize;

		GenieDebug.error("InitLandView2", "Window_w = " + Window_w);
		GenieDebug.error("InitLandView2", "Window_h = " + Window_h);

		if (Window_w < 480) {
			temp_w = 30;
			x = 1;
			// textsize = 18;
		} else if (Window_w >= 480 && Window_w <= 600) {
			// temp_w = 40;
			temp_w = 130;
			x = 1;
			// textsize = 20;
		} else if (Window_w > 600 && Window_w <= 854) {
			// temp_w = 60;
			temp_w = 200;
			x = 2;
			// textsize = 23;
		} else if (Window_w > 854) {
			temp_w = 400;
		} else {
			temp_w = 100;
			x = 1;
			// textsize = 20;
		}

		GenieDebug.error("InitLandView2", "temp_w = " + temp_w);

		int view_w = (getWindowManager().getDefaultDisplay().getWidth() - temp_w)
				/ column;

		int view_h = view_w; // (getWindowManager().getDefaultDisplay().getHeight()
								// - titlerhight -temp_h)/2;

		GenieDebug.error("InitLandView2", "view_w = " + view_w);

		textsize = (float) (view_w / 16);

		int margin_w = (Window_w - (view_h * column)) / 8;
		int margin_h = ((getWindowManager().getDefaultDisplay().getHeight() - titlerhight) - view_h
				* row) / 6;

		GenieDebug.error("InitLandView2", "margin_w = " + margin_w);
		GenieDebug.error("InitLandView2", "margin_h = " + margin_h);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				view_w, view_h);

		LinearLayout layout = (LinearLayout) addLandView2(0, row, column,
				Window_w, metrics, view_h, layoutParams, margin_w, margin_h);

		main_layout.addView(layout);

		LinearLayout layout1 = (LinearLayout) addLandView2(6, row, column,
				Window_w, metrics, view_h, layoutParams, margin_w, margin_h);

		main_layout.addView(layout1);
		MapPageIndex = 2;

		// onActivityResult_setview(null,100);

		PopupPage();

	}

	//
	public View addLandView2(int count, int row, int column, int Window_w,
			DisplayMetrics metrics, int view_h,
			LinearLayout.LayoutParams layoutParams, int margin_w, int margin_h) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < row; i++) {
			LinearLayout layout1 = new LinearLayout(this);
			layout1.setOrientation(LinearLayout.HORIZONTAL);
			layout1.setGravity(Gravity.CENTER);

			for (int j = 0; j < column; j++) {
				LinearLayout layout3 = new LinearLayout(this);
				layout3.setOrientation(LinearLayout.VERTICAL);
				layout3.setWeightSum(1);
				layout3.setGravity(Gravity.CENTER);

				layout3.setId(LinearLayout_BaseID + (column * i + j) + count);

				layout3.setOnClickListener(this);

				ImageView button = new ImageView(this);
				TextView view = new TextView(this);
				// TextView view_2 = new TextView(this);
				TextView view_2 = new MarqueeTextView(this);

				view.setSingleLine(true);
				// view.setTextSize(textsize);

				button.setId(column * i + j + count);
				// button.setOnClickListener(this);
				// view.setWidth(getWindowManager().getDefaultDisplay().getWidth()/3);
				view.setGravity(Gravity.CENTER);
				view_2.setGravity(Gravity.CENTER);
				view_2.setId((column * i + j + count) + BaseID);

				view_2.setTransformationMethod(SingleLineTransformationMethod
						.getInstance());
				view_2.setSingleLine(true);
				view_2.setEllipsize(TruncateAt.MARQUEE);
				view_2.setMarqueeRepeatLimit(-1);
				view_2.setFocusable(true);

				int imageID = 0;
				int textID = 0;

				switch (column * i + j + count) {
				case GenieGlobalDefines.EFunctionWireless:
					imageID = R.drawable.wireless;
					textID = R.string.wireless;
					break;

				case GenieGlobalDefines.EFunctionGuestAccess:
					imageID = R.drawable.guestaccess;
					textID = R.string.guestaccess;
					break;

				case GenieGlobalDefines.EFunctionMap:
					imageID = R.drawable.map;
					textID = R.string.map;
					break;

				case GenieGlobalDefines.EFunctionTraffic:
					imageID = R.drawable.traffic;
					textID = R.string.traffic;
					break;
				case GenieGlobalDefines.EFunctionMyMedia:
					imageID = R.drawable.mymedia;
					textID = R.string.mymedia;
					break;

				/**
				 * 新增加文件浏览功能
				 */
				// case GenieGlobalDefines.EFunctionFileBrowse:
				// imageID = R.drawable.file;
				// textID = R.string.filebrowse;
				// break;

				/**
				 * 新增文件传输功能
				 */
//				case GenieGlobalDefines.EFunctionFileTransfer:
//					imageID = R.drawable.transfer_wifi_icon;
//					textID = R.string.file_transfer;
//					break;

				case GenieGlobalDefines.EFunctionQRCode:
					imageID = R.drawable.qrcode;
					textID = R.string.qrcode;
					break;
				// case GenieGlobalDefines.EFunctionSignalStrength:
				// imageID = R.drawable.signal_strength;
				// textID = R.string.signal;
				// break;
				// case GenieGlobalDefines.EFunctionWifiAnalyzer:{
				// imageID = R.drawable.signal_strength;
				// textID = R.string.signal;
				// break;
				// }
				case GenieGlobalDefines.EFunctionGenieAppStaging:
					imageID = R.drawable.appstore;
					textID = R.string.marketplace;
					if (GenieGlobalDefines.GetSmartNetworkFlag(this) == 1) {
						layout3.setVisibility(View.VISIBLE);
					} else {
						layout3.setVisibility(View.INVISIBLE);
					}
					break;
				case GenieGlobalDefines.EFunctionSpeedTest:
					imageID = R.drawable.readyshare;
					textID = R.string.readyshare;
					layout3.setVisibility(View.INVISIBLE);
					break;
				case GenieGlobalDefines.EFunctionParental:
					imageID = R.drawable.parentalcontrols;
					textID = R.string.parentalcontrl;
					break;
				default:
					imageID = R.drawable.readyshare;
					textID = R.string.readyshare;
					layout3.setVisibility(View.INVISIBLE);
					break;
				}

				button.setBackgroundResource(imageID);

				layout3.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							ClickFlag = true;
							v.setBackgroundResource(R.drawable.bj);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							ClickFlag = false;
							v.setBackgroundResource(R.drawable.bj_dz);
						}
						return false;
					}
				});

				TextPaint tp = view.getPaint();
				tp.setFakeBoldText(true);
				view.setTextColor(getResources().getColor(R.color.DimGray));
				if (Window_w > GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
					float rate = 5 * (float) (getWindowManager()
							.getDefaultDisplay().getHeight() > Window_w ? Window_w
							: getWindowManager().getDefaultDisplay()
									.getHeight()) / 320;
					view.setTextSize(rate);
				}
				view.setText(textID);

				if (view_2.getId() == (BaseID + GenieGlobalDefines.EFunctionWireless)) {
					if (wifiisable) {
						// view_2.setTextColor(getResources().getColor(R.color.limegreen));
						// view_2.setText(getResources().getString(R.string.connected));
					} else {
						// view_2.setTextColor(getResources().getColor(R.color.crimson));
						// view_2.setText(getResources().getString(R.string.notenabled));
					}
				} else {
					view_2.setTextColor(getResources().getColor(
							R.color.limegreen));
					// view_2.setTextColor((Color.GREEN));
					// view_2.setText(getResources().getString(R.string.Clickhere));
					view_2.setText("");// qicheng.ai change
				}

				layout3.addView(button, new ViewGroup.LayoutParams(
						(int) (view_h - (45 * metrics.scaledDensity)),
						(int) (view_h - (45 * metrics.scaledDensity))));
				layout3.addView(view, new ViewGroup.LayoutParams(FP, WC));
				layout3.addView(view_2, new ViewGroup.LayoutParams(FP, WC));
				layout3.setBackgroundResource(R.drawable.bj_dz);

				// if((j%column) == 0)
				// {
				// layoutParams.setMargins(margin_w, margin_h, margin_w,
				// margin_h);
				// }else if((j%column) == 1)
				// {
				// layoutParams.setMargins(margin_w, 5*x, 5*x, 5*x);
				// }else if((j%column) == 2)
				// {
				// layoutParams.setMargins(5*x, 5*x, 10*x, 5*x);
				// }

				GenieDebug.error("port", "land margin_h = " + margin_h);
				GenieDebug.error("port", "land margin_w = " + margin_w);
				layoutParams.setMargins(margin_w, margin_h, margin_w, margin_h);

				layout1.addView(layout3, layoutParams);
			}

			layout.addView(layout1);
		}
		return layout;
	}

	public void InitPortView() {

		// DismissPage();

		search_bt = (Button) this.findViewById(R.id.search);
		search_edit = (EditText) this.findViewById(R.id.main_input);
		// search_edit.
		// setBackgroundDrawable(android.R.drawable.editbox_background);

		search_bt.setOnClickListener(this);

		// LinearLayout layout =
		// (LinearLayout)findViewById(R.id.LinearLayout_01);

		main_layout = (ScrollLayout) findViewById(R.id.ScrollLayout);

		int Width = main_layout.getWidth();
		int Height = main_layout.getHeight();

		GenieDebug.error("main_layout ", "Width = " + Width);
		GenieDebug.error("main_layout", "Height = " + Height);

		// layout.setOrientation(LinearLayout.VERTICAL);

		// LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

		// layout.setGravity(Gravity.CENTER);

		int row, column;
		row = 3;
		column = 2;

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		GenieDebug.error("main_layout ", " 888 metrics.scaledDensity = "
				+ metrics.scaledDensity);

		int titlerhight = (int) (100 * metrics.scaledDensity);

		Display d = getWindowManager().getDefaultDisplay();
		final int Window_w = d.getWidth();
		final int Window_h = d.getHeight();

		// int temp_w,
		int temp_h, x;
		float textsize;
		if (Window_w < 480) {
			temp_h = 30;
			x = 1;
			// textsize = 18;
		} else if (Window_w >= 480 && Window_w <= 600) {
			// temp_w = 40;
			temp_h = 130;
			x = 1;
			// textsize = 16;
		} else if (Window_w > 600 && Window_w <= 854) {
			// temp_w = 60;
			temp_h = 280;
			x = 2;
			// textsize = 23;
		} else if (Window_w > 854) {
			temp_h = 400;
		} else {
			temp_h = 100;
			x = 1;
			// textsize = 20;
		}

		GenieDebug.error("main_layout ", " 888 titlerhight = " + titlerhight);
		GenieDebug.error("main_layout ", " 888 temp_h = " + temp_h);

		int view_h = (getWindowManager().getDefaultDisplay().getHeight()
				- titlerhight - temp_h) / 3;
		int margin_w = (Window_w - (view_h * column)) / 6;
		int margin_h = ((getWindowManager().getDefaultDisplay().getHeight() - titlerhight) - (view_h * row)) / 8;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				view_h, view_h);

		textsize = (float) (view_h / 16);
		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams((getWindowManager().getDefaultDisplay().getWidth()-
		// temp_w)/column,(getWindowManager().getDefaultDisplay().getHeight()-titlerhight
		// - temp_h)/3);

		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		// layoutParams.setMargins(10, 5, 10, 5);

		LinearLayout layout = (LinearLayout) addPortView(0, row, column,
				Window_w, metrics, view_h, layoutParams, margin_w, margin_h);

		main_layout.addView(layout);

		LinearLayout layout1 = (LinearLayout) addPortView(6, row, column,
				Window_w, metrics, view_h, layoutParams, margin_w, margin_h);

		main_layout.addView(layout1);

		MapPageIndex = 2;

		// onActivityResult_setview(null,100);

		PopupPage();

	}

	public View addPortView(int count, int row, int column, int Window_w,
			DisplayMetrics metrics, int view_h,
			LinearLayout.LayoutParams layoutParams, int margin_w, int margin_h) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		for (int i = 0; i < row; i++) {
			LinearLayout layout1 = new LinearLayout(this);
			layout1.setOrientation(LinearLayout.HORIZONTAL);
			layout1.setGravity(Gravity.CENTER);
			// layout1.setPadding(0, margin_h, 0, margin_h);

			// LinearLayout.LayoutParams temp = new
			// LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			// temp.setMargins(0, margin_h, 0, margin_h);
			//
			// layout1.setLayoutParams(temp);

			// layout1.setBackgroundResource(R.drawable.layout_indexbar);

			for (int j = 0; j < column; j++) {
				LinearLayout layout3 = new LinearLayout(this);
				layout3.setOrientation(LinearLayout.VERTICAL);
				layout3.setWeightSum(1);
				layout3.setGravity(Gravity.CENTER);

				layout3.setId(LinearLayout_BaseID + (column * i + j) + count);

				layout3.setOnClickListener(this);

				// layout3.setBackgroundResource(R.drawable.layout_indexbar);

				// Button button = new Button(this);
				ImageView button = new ImageView(this);
				TextView view = new TextView(this);
				// TextView view_2 = new TextView(this);
				TextView view_2 = new MarqueeTextView(this);

				view.setSingleLine(true);
				// view.setTextSize(textsize);

				button.setId(column * i + j + count);
				// button.setOnClickListener(this);
				// view.setWidth(getWindowManager().getDefaultDisplay().getWidth()/3);
				view.setGravity(Gravity.CENTER);
				view_2.setGravity(Gravity.CENTER);
				view_2.setId((column * i + j + count) + BaseID);

				// view_2.setEllipsize(TruncateAt.MARQUEE);
				// view_2.setFocusable(true);
				// view_2.setMarqueeRepeatLimit(-1);
				// view_2.setFocusableInTouchMode(true);

				view_2.setTransformationMethod(SingleLineTransformationMethod
						.getInstance());
				view_2.setSingleLine(true);
				view_2.setEllipsize(TruncateAt.MARQUEE);
				view_2.setMarqueeRepeatLimit(-1);
				view_2.setFocusable(true);

				int imageID = 0;
				int textID = 0;

				switch (column * i + j + count) {
				case GenieGlobalDefines.EFunctionWireless:
					imageID = R.drawable.wireless;
					textID = R.string.wireless;
					break;

				case GenieGlobalDefines.EFunctionGuestAccess:
					imageID = R.drawable.guestaccess;
					textID = R.string.guestaccess;
					break;

				case GenieGlobalDefines.EFunctionMap:
					imageID = R.drawable.map;
					textID = R.string.map;
					break;

				case GenieGlobalDefines.EFunctionTraffic:
					imageID = R.drawable.traffic;
					textID = R.string.traffic;
					break;

				case GenieGlobalDefines.EFunctionQRCode:
					imageID = R.drawable.qrcode;
					textID = R.string.qrcode;
					break;
				// case GenieGlobalDefines.EFunctionSignalStrength:
				// imageID = R.drawable.signal_strength;
				// textID = R.string.signal;
				// break;
				// case GenieGlobalDefines.EFunctionWifiAnalyzer:{
				// imageID = R.drawable.signal_strength;
				// textID = R.string.signal;
				// break;
				// }
				case GenieGlobalDefines.EFunctionGenieAppStaging:
					imageID = R.drawable.appstore;
					textID = R.string.marketplace;
					if (GenieGlobalDefines.GetSmartNetworkFlag(this) == 1) {
						layout3.setVisibility(View.VISIBLE);
					} else {
						layout3.setVisibility(View.INVISIBLE);
					}
					break;
				case GenieGlobalDefines.EFunctionSpeedTest:
					imageID = R.drawable.readyshare;
					textID = R.string.readyshare;
					layout3.setVisibility(View.INVISIBLE);
					break;
				case GenieGlobalDefines.EFunctionMyMedia:
					imageID = R.drawable.mymedia;
					textID = R.string.mymedia;
					break;
				// case GenieGlobalDefines.EFunctionReadyShare:
				// imageID = R.drawable.readyshare;
				// textID = R.string.readyshare;
				// break;
				/**
				 * 新增加文件浏览功能
				 */
				// case GenieGlobalDefines.EFunctionFileBrowse:
				// imageID = R.drawable.file;
				// textID = R.string.filebrowse;
				// break;

				/**
				 * 新增文件传输功能
				 */
//				case GenieGlobalDefines.EFunctionFileTransfer:
//					imageID = R.drawable.transfer_wifi_icon;
//					textID = R.string.file_transfer;
//					break;

				case GenieGlobalDefines.EFunctionParental:
					imageID = R.drawable.parentalcontrols;
					textID = R.string.parentalcontrl;
					break;
				default:
					imageID = R.drawable.readyshare;
					textID = R.string.readyshare;
					layout3.setVisibility(View.INVISIBLE);
					break;

				}

				button.setBackgroundResource(imageID);

				layout3.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {

						// GenieDebug.error("onTouch ","onTouch event.getAction() = "+event.getAction());
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							//
							ClickFlag = true;

							// LinearLayout temp =
							// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
							v.setBackgroundResource(R.drawable.bj);
							// v.setBackgroundResource(R.drawable.deviceiphone);
							// SetBackgroundResource_down(v);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							//
							// v.setBackgroundResource(R.drawable.deviceipad);
							// SetBackgroundResource_up(v);
							ClickFlag = false;

							// LinearLayout temp =
							// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
							v.setBackgroundResource(R.drawable.bj_dz);
						}
						return false;
					}
				});

				// view.
				TextPaint tp = view.getPaint();
				tp.setFakeBoldText(true);
				view.setTextColor(getResources().getColor(R.color.DimGray));
				if (Window_w > GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
					float rate = 5 * (float) (getWindowManager()
							.getDefaultDisplay().getHeight() > Window_w ? Window_w
							: getWindowManager().getDefaultDisplay()
									.getHeight()) / 320;
					view.setTextSize(rate);
				}
				view.setText(textID);

				// if (Window_w >= 480) {
				// view.setTextSize((19 / metrics.scaledDensity));
				// }

				if (view_2.getId() == (BaseID + GenieGlobalDefines.EFunctionWireless)) {
					if (wifiisable) {
						view_2.setTextColor(getResources().getColor(
								R.color.limegreen));
						// view_2.setText(getResources().getString(R.string.connected));
					} else {
						view_2.setTextColor(getResources().getColor(
								R.color.crimson));
						// view_2.setText(getResources().getString(R.string.notenabled));
					}
				} else {
					view_2.setTextColor(getResources().getColor(
							R.color.limegreen));
					// view_2.setTextColor((Color.GREEN));
					// view_2.setText(getResources().getString(R.string.Clickhere));
					view_2.setText("");// qicheng.ai change
				}

				// if(i == 2)
				// {
				// view.setVisibility(View.INVISIBLE);
				// button.setVisibility(View.INVISIBLE);
				// }

				layout3.addView(button, new ViewGroup.LayoutParams(
						(int) (view_h - (45 * metrics.scaledDensity)),
						(int) (view_h - (45 * metrics.scaledDensity))));
				layout3.addView(view, new ViewGroup.LayoutParams(FP, WC));
				layout3.addView(view_2, new ViewGroup.LayoutParams(FP, WC));
				layout3.setBackgroundResource(R.drawable.bj_dz);

				// if((column%2) == 0)
				// {
				GenieDebug.error("port", "margin_h = " + margin_h);
				layoutParams.setMargins(margin_w, margin_h, margin_w, margin_h);
				// }else
				// {
				// layoutParams.setMargins(margin_w, 0, margin_w, 0);
				// }

				layout1.addView(layout3, layoutParams);

				// if(i == 2) break;
			}

			layout.addView(layout1);
		}
		return layout;
	}

	public void InitLandView() {

		search_bt = (Button) this.findViewById(R.id.search);
		search_edit = (EditText) this.findViewById(R.id.main_input);
		// search_edit.
		// setBackgroundDrawable(android.R.drawable.editbox_background);

		search_bt.setOnClickListener(this);

		main_layout = (ScrollLayout) findViewById(R.id.ScrollLayout);

		MapPageIndex = 0;

		// layout.setOrientation(LinearLayout.VERTICAL);

		int Width = main_layout.getWidth();
		int Height = main_layout.getHeight();

		GenieDebug.error("main_layout ", "Width = " + Width);
		GenieDebug.error("main_layout", "Height = " + Height);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		GenieDebug.error("main_layout ", " 888 metrics.scaledDensity = "
				+ metrics.scaledDensity);

		int titlerhight = (int) (90 * metrics.scaledDensity);

		GenieDebug.error("main_layout ", " 888 titlerhight = " + titlerhight);

		Display d = getWindowManager().getDefaultDisplay();
		final int Window_w = d.getWidth();
		final int Window_h = d.getHeight();

		int temp_w, temp_h, x;

		float textsize;

		if (Window_w < 480) {
			temp_w = 30;
			x = 1;
			// textsize = 18;
		} else if (Window_w >= 480 && Window_w <= 600) {
			// temp_w = 40;
			temp_w = 130;
			x = 1;
			// textsize = 20;
		} else if (Window_w > 600 && Window_w <= 854) {
			// temp_w = 60;
			temp_w = 230;
			x = 2;
			// textsize = 23;
		} else if (Window_w > 854) {
			temp_w = 400;
		} else {
			temp_w = 100;
			x = 1;
			// textsize = 20;
		}

		GenieDebug.error("InitLandView ", " 888 temp_w = " + temp_w);

		int layoutParams_Width = (getWindowManager().getDefaultDisplay()
				.getWidth() - temp_w) / 3;
		// int layoutParams_Height =
		// (getWindowManager().getDefaultDisplay().getHeight()- titlerhight);

		// int margin = ((getWindowManager().getDefaultDisplay().getHeight()-
		// titlerhight) - layoutParams_Width)/2;
		int layoutParams_Height = layoutParams_Width;

		textsize = (float) (layoutParams_Width / 16);

		int margin_w = (Window_w - (layoutParams_Height * 3)) / 6;
		// int margin_h =
		// ((getWindowManager().getDefaultDisplay().getHeight()-titlerhight -
		// temp_h) - (view_h*row))/8;

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				layoutParams_Width, layoutParams_Height);
		// LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

		// LinearLayout layout = new LinearLayout(this);
		// layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout layout1 = (LinearLayout) addLandView(0, Window_w,
				layoutParams_Height, layoutParams, metrics, margin_w);

		main_layout.addView(layout1);

		LinearLayout layout2 = (LinearLayout) addLandView(1, Window_w,
				layoutParams_Height, layoutParams, metrics, margin_w);

		main_layout.addView(layout2);

		LinearLayout layout3 = (LinearLayout) addLandView(2, Window_w,
				layoutParams_Height, layoutParams, metrics, margin_w);

		main_layout.addView(layout3);

		// //
		// LinearLayout layout4 = (LinearLayout) addLandView(3, Window_w,
		// layoutParams_Height, layoutParams, metrics, margin_w);
		//
		// main_layout.addView(layout4);
		MapPageIndex = 4;

		// onActivityResult_setview(null,100);
		PopupPage();
	}

	// 横屏
	public View addLandView(int pageId, int Window_w, int layoutParams_Height,
			LinearLayout.LayoutParams layoutParams, DisplayMetrics metrics,
			int margin_w) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);

		// layout2.setBackgroundResource(R.drawable.layout_indexbar);

		int i = pageId;
		for (int j = 0; j < 3; j++) {
			LinearLayout layout3 = new LinearLayout(this);
			layout3.setOrientation(LinearLayout.VERTICAL);
			layout3.setWeightSum(5);
			layout3.setGravity(Gravity.CENTER);

			layout3.setId(LinearLayout_BaseID + (3 * i + j));
			layout3.setOnClickListener(this);

			// layout3.setBackgroundResource(R.drawable.layout_indexbar);

			ImageView button = new ImageView(this);
			TextView view = new TextView(this);
			// TextView view_2 = new TextView(this);
			TextView view_2 = new MarqueeTextView(this);

			view.setSingleLine(true);
			// view.setTextSize(textsize);

			button.setId(3 * i + j);
			// button.setOnClickListener(this);
			// view.setWidth(getWindowManager().getDefaultDisplay().getWidth()/3);
			view.setGravity(Gravity.CENTER);
			view_2.setGravity(Gravity.CENTER);
			view_2.setId((3 * i + j) + BaseID);

			view_2.setTransformationMethod(SingleLineTransformationMethod
					.getInstance());
			view_2.setSingleLine(true);
			view_2.setEllipsize(TruncateAt.MARQUEE);
			view_2.setMarqueeRepeatLimit(-1);
			view_2.setFocusable(true);

			int imageID = 0;
			int textID = 0;

			switch (3 * i + j) {
			case GenieGlobalDefines.EFunctionWireless:
				imageID = R.drawable.wireless;
				textID = R.string.wireless;
				break;

			case GenieGlobalDefines.EFunctionGuestAccess:
				imageID = R.drawable.guestaccess;
				textID = R.string.guestaccess;
				break;

			case GenieGlobalDefines.EFunctionMap:
				imageID = R.drawable.map;
				textID = R.string.map;
				break;

			case GenieGlobalDefines.EFunctionTraffic:
				imageID = R.drawable.traffic;
				textID = R.string.traffic;
				break;

			case GenieGlobalDefines.EFunctionQRCode:
				imageID = R.drawable.qrcode;
				textID = R.string.qrcode;
				break;
			// case GenieGlobalDefines.EFunctionSignalStrength:
			// imageID = R.drawable.signal_strength;
			// textID = R.string.signal;
			// break;
			// case GenieGlobalDefines.EFunctionWifiAnalyzer:
			// imageID = R.drawable.signal_strength;
			// textID = R.string.signal;
			// break;
			case GenieGlobalDefines.EFunctionGenieAppStaging:
				imageID = R.drawable.appstore;
				textID = R.string.marketplace;
				if (GenieGlobalDefines.GetSmartNetworkFlag(this) == 1) {
					layout3.setVisibility(View.VISIBLE);
				} else {
					layout3.setVisibility(View.INVISIBLE);
				}
				break;
			case GenieGlobalDefines.EFunctionSpeedTest:
				imageID = R.drawable.readyshare;
				textID = R.string.readyshare;
				layout3.setVisibility(View.INVISIBLE);
				break;
			case GenieGlobalDefines.EFunctionMyMedia:
				imageID = R.drawable.mymedia;
				textID = R.string.mymedia;
				break;

			/**
			 * 新增加文件浏览功能
			 */
			// case GenieGlobalDefines.EFunctionFileBrowse:
			// imageID = R.drawable.file;
			// textID = R.string.filebrowse;
			// break;

			/**
			 * 新增文件传输功能
			 */
//			case GenieGlobalDefines.EFunctionFileTransfer:
//				imageID = R.drawable.transfer_wifi_icon;
//				textID = R.string.file_transfer;
//				break;

			case GenieGlobalDefines.EFunctionReadyShare:
				imageID = R.drawable.readyshare;
				textID = R.string.readyshare;
				break;

			case GenieGlobalDefines.EFunctionParental:
				imageID = R.drawable.parentalcontrols;
				textID = R.string.parentalcontrl;
				break;
			default:
				imageID = R.drawable.readyshare;
				textID = R.string.readyshare;
				layout3.setVisibility(View.INVISIBLE);
				break;
			}

			button.setBackgroundResource(imageID);

			layout3.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// GenieDebug.error("onTouch ","onTouch event.getAction() = "+event.getAction());
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						ClickFlag = true;

						// LinearLayout temp =
						// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
						v.setBackgroundResource(R.drawable.bj);
						// v.setBackgroundResource(R.drawable.deviceiphone);
						// SetBackgroundResource_down(v);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						// v.setBackgroundResource(R.drawable.deviceipad);
						// SetBackgroundResource_up(v);
						ClickFlag = false;

						// LinearLayout temp =
						// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
						v.setBackgroundResource(R.drawable.bj_dz);
					}
					return false;
				}
			});

			TextPaint tp = view.getPaint();
			tp.setFakeBoldText(true);
			view.setTextColor(getResources().getColor(R.color.DimGray));
			if (Window_w > GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
				float rate = 5 * (float) (getWindowManager()
						.getDefaultDisplay().getHeight() > Window_w ? Window_w
						: getWindowManager().getDefaultDisplay().getHeight()) / 320;
				view.setTextSize(rate);
			}
			view.setText(textID);

			// if (Window_w > 854) {
			// view.setTextSize((19 / metrics.scaledDensity));
			// }

			if (view_2.getId() == (BaseID + GenieGlobalDefines.EFunctionWireless)) {
				if (wifiisable) {
					view_2.setTextColor(getResources().getColor(
							R.color.limegreen));
					// view_2.setText(getResources().getString(R.string.connected));
				} else {
					view_2.setTextColor(getResources()
							.getColor(R.color.crimson));
					// view_2.setText(getResources().getString(R.string.notenabled));
				}
			} else {
				view_2.setTextColor(getResources().getColor(R.color.limegreen));
				// view_2.setTextColor((Color.GREEN));
				// view_2.setText(getResources().getString(R.string.Clickhere));
				view_2.setText("");// qicheng.ai change
			}
			// if(i == 2)
			// {
			// view.setVisibility(View.INVISIBLE);
			// button.setVisibility(View.INVISIBLE);
			// }

			layout3.addView(button, new ViewGroup.LayoutParams(
					(int) (layoutParams_Height - (45 * metrics.scaledDensity)),
					(int) (layoutParams_Height - (45 * metrics.scaledDensity))));
			layout3.addView(view, new ViewGroup.LayoutParams(FP, WC));
			layout3.addView(view_2, new ViewGroup.LayoutParams(FP, WC));

			layout3.setBackgroundResource(R.drawable.bj_dz);

			// if((j%3) == 0)
			// {
			// layoutParams.setMargins(10*x, 5*x, 5*x, 5*x);
			// }else if((j%3) == 1)
			// {
			// layoutParams.setMargins(5*x, 5*x, 5*x, 5*x);
			// }else if((j%3) == 2)
			// {
			// layoutParams.setMargins(5*x, 5*x, 10*x, 5*x);
			// }

			layoutParams.setMargins(margin_w, 0, margin_w, 0);

			layout.addView(layout3, layoutParams);

		}
		return layout;
	}

	public void InitMainView() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			GenieDebug.error("InitMainView", " in InitPortView");
			InitPortView();
		} else {
			Display d = getWindowManager().getDefaultDisplay();
			final int w = d.getWidth();
			final int h = d.getHeight();

			final double ar = w > h ? (double) w / (double) h : (double) h
					/ (double) w;
			if (ar > 1.4) {
				GenieDebug.error("InitMainView", " in InitLandView");
				InitLandView();
			} else {
				GenieDebug.error("InitMainView", " in InitLandView2");
				InitLandView2();
			}

		}

	}

	public void InitView() {

		// mGestureDetector = new GestureDetector(this,new MyGestureListener());
		//
		//
		// mViewFlipper = (ViewFlipper) findViewById(R.id.layoutswitcher);
		//
		// LayoutInflater inflater_1 = LayoutInflater.from(this);
		// view_1 = inflater_1.inflate(R.layout.main_view_1,null);
		// mViewFlipper.addView(view_1);
		//
		// LayoutInflater inflater_2 = LayoutInflater.from(this);
		// view_2 = inflater_2.inflate(R.layout.main_view_2,null);
		// mViewFlipper.addView(view_2);
		//
		// Wireless = (ImageButton)view_1.findViewById(R.id.wireless);
		// GuestAccess = (ImageButton)view_1.findViewById(R.id.guestaccess);
		// Map = (ImageButton)view_1.findViewById(R.id.map);
		// Traffic = (ImageButton)view_1.findViewById(R.id.traffic);
		//
		// SpeedTest = (ImageButton)view_2.findViewById(R.id.speedtest);
		// Parental = (ImageButton)view_2.findViewById(R.id.parentalcontrols);
		//
		//
		// Wireless.setOnClickListener(this);
		// GuestAccess.setOnClickListener(this);
		// Map.setOnClickListener(this);
		// Traffic.setOnClickListener(this);
		// SpeedTest.setOnClickListener(this);
		// Parental.setOnClickListener(this);
		//
		//
		//
		//
		// // search_frame = (ImageButton)this.findViewById(R.id.search_frame);
		// // search_frame.setOnClickListener(this);
		//
		//
		//
		// search_bt = (Button)this.findViewById(R.id.search);
		// search_edit = (EditText)this.findViewById(R.id.main_input);
		//
		// search_bt.setOnClickListener(this);
		//
		//
		//
		//
		// TextView Wireless_t =
		// (TextView)view_1.findViewById(R.id.wireless_text);
		// TextView GuestAccess_t =
		// (TextView)view_1.findViewById(R.id.guestaccess_text);
		// TextView Map_t = (TextView)view_1.findViewById(R.id.map_text);
		// TextView Traffic_t =
		// (TextView)view_1.findViewById(R.id.traffic_text);
		// TextView SpeedTest_t =
		// (TextView)view_2.findViewById(R.id.speedtest_text);
		// TextView Parental_t =
		// (TextView)view_2.findViewById(R.id.parentalcontrols_text);
		//
		// Wireless_t.setText(R.string.wireless);
		// GuestAccess_t.setText(R.string.guestaccess);
		// Map_t.setText(R.string.map);
		// Traffic_t.setText(R.string.traffic);
		// SpeedTest_t.setText(R.string.speedtest);
		// Parental_t.setText(R.string.parentalcontrl);
		//
		//
		//
		//
		// int Width = getWindowManager().getDefaultDisplay().getWidth();
		// int Height = getWindowManager().getDefaultDisplay().getHeight();
		//
		// GenieDebug.error("onCreate", "Width --=--"+Width);
		// GenieDebug.error("onCreate", "Height --=--"+Height);
		//
	}

	// @Override
	//
	// public boolean onTouchEvent(MotionEvent event) {
	//
	// return mGestureDetector.onTouchEvent(event);
	//

	//
	//
	// }
	//
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent ev) {

			GenieDebug.error("onSingleTapUp", ev.toString());

			return true;

		}

		@Override
		public void onShowPress(MotionEvent ev) {

			GenieDebug.error("onShowPress", ev.toString());

		}

		@Override
		public void onLongPress(MotionEvent ev) {

			GenieDebug.error("onLongPress", ev.toString());

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			GenieDebug.error("debug", "...onFling...");
			if (e1.getX() > e2.getX()) {// move to left
				mViewFlipper.showNext();
			} else if (e1.getX() < e2.getX()) {
				mViewFlipper.setInAnimation(getApplicationContext(),
						R.anim.push_right_in);
				mViewFlipper.setOutAnimation(getApplicationContext(),
						R.anim.push_right_out);
				mViewFlipper.showPrevious();
				mViewFlipper.setInAnimation(getApplicationContext(),
						R.anim.push_left_in);
				mViewFlipper.setOutAnimation(getApplicationContext(),
						R.anim.push_left_out);
			} else {
				return false;
			}
			return true;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		GenieDebug.error("onConfigurationChanged",
				"onConfigurationChanged !!!!!!!!!");
		GenieDebug.error("onConfigurationChanged", "newConfig.orientation = "
				+ newConfig.orientation);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

			setContentView(R.layout.main);
			// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);

			Log.i("onConfigurationChanged", "onConfigurationChanged --1--");

			Display d = getWindowManager().getDefaultDisplay();
			final int w = d.getWidth();
			final int h = d.getHeight();
			final double ar = w > h ? (double) w / (double) h : (double) h
					/ (double) w;
			if (ar > 1.4)
				InitLandView();
			else
				InitLandView2();

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

			setContentView(R.layout.main);
			// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);

			Log.i("onConfigurationChanged", "onConfigurationChanged --1--");

			InitPortView();
		}

		// PopupPageWindow();

		super.onConfigurationChanged(newConfig);
	}

	// public void changeScreenOrientation(Configuration newConfig)
	// {
	// WindowManager windowManager = (WindowManager)
	// getSystemService(WINDOW_SERVICE);
	// Display display = windowManager.getDefaultDisplay();
	//
	// super.onConfigurationChanged(newConfig);
	// if (this.getResources().getConfiguration().orientation ==
	// Configuration.ORIENTATION_LANDSCAPE) {
	// // land do nothing is ok
	// } else if (this.getResources().getConfiguration().orientation ==
	// Configuration.ORIENTATION_PORTRAIT) {
	// // port do nothing is ok
	// }
	//
	//
	// GenieDebug.error("changeScreenOrientation",
	// "changeScreenOrientation !!!!!!!!!");
	// }
	//
	public void sendMessage2UI(int msg) {
		handler.sendEmptyMessage(msg);
	}

	public void showWaitingDialog() {
		closeWaitingDialog();
		// progressDialog = ProgressDialog.show(GenieMainView.this,
		// "Loading...", "Please wait...", true, false);
		progressDialog = ProgressDialog.show(GenieMainView.this, null,
				getResources().getString(R.string.pleasewait) + "...", true,
				true);
	}

	public void closeWaitingDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void setModelNameAndVersion() {
		if (GenieSoap.dictionary.get(
				GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION).indexOf(
				"N/A") == -1) {
			sendMessage2UI(GenieGlobalDefines.ESoapRequestVerityUser);
		}
	}

	// public void showLoginDialog(boolean need)
	// {
	//
	//
	// if(!need && userInfo.isSave == '1')
	// {
	// soapCommand.authenticateUserAndPassword(new
	// String(userInfo.username,0,userInfo.username[39]),
	// new String(userInfo.password,0,userInfo.password[39]));
	// }
	// else
	// {
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// showLogin();
	// }
	// });
	// }
	// }
	//
	//

	// private void showLogin()
	// {
	// //LayoutInflater inflater = getLayoutInflater();
	// //View login = inflater.inflate(R.layout.login,(ViewGroup)
	// findViewById(R.layout.login));
	//
	// LayoutInflater inflater = LayoutInflater.from(this);
	// login = inflater.inflate(R.layout.login,null);
	//
	// dialog_login = new AlertDialog.Builder(this)
	// .setTitle(R.string.login)
	// .setView(login)
	// .setPositiveButton(R.string.ok, this)
	// .setNegativeButton(R.string.cancel, this);
	//
	//
	// dialog_login.setCancelable(false);
	// box = (CheckBox)login.findViewById(R.id.remeberpassword);
	//
	// if(userInfo.isSave == '1')
	// {
	// box.setChecked(true);
	// }
	// else
	// {
	// box.setChecked(false);
	// }
	//
	// TextView alertinfo = (TextView)login.findViewById(R.id.alertinfo);
	//
	// if(!IsNetgearRouter)
	// {
	// alertinfo.setVisibility(View.VISIBLE);
	// }else
	// {
	// alertinfo.setVisibility(View.GONE);
	// }
	//
	//
	// //AlertDialog testdialog = dialog_login.create();
	// //testdialog.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(new
	// ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	// ViewGroup.LayoutParams.WRAP_CONTENT));
	//
	//
	// // dialog_login.show();
	//
	// AlertDialog login = dialog_login.create();
	// login.show();
	//
	// if(!wifiisable)
	// {
	// login.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
	// //login.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
	// }
	//
	//
	// }

	// public void getInitialData()
	// {
	// //userInfo = GenieShareAPI.ReadDataFromFile(this,
	// GenieGlobalDefines.USER_INFO_FILENAME);
	//
	// //userInfo.isSave = '0'; //test
	//
	// //ShowLoginProgressBarVisible(); qicheng.ai change move to another please
	//
	// GenieDebug.error("debug", "getInitialData --1--");
	//
	//
	//
	//
	// new Thread()
	// {
	// public void run()
	// {
	// GenieDebug.error("Thread", "checkAvailabelPort --1--");
	// soapCommand.checkAvailabelPort(); //qicheng.ai change
	// soapCommand.getGuestAccessEnabled();
	// //soapCommand.getGuestAccessInfo();
	// GenieDebug.error("Thread", "getRouterType --1--");
	//
	// //qicheng.ai change move to another please
	// // if(!soapCommand.getRouterType())
	// // {
	// // return;
	// // //sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
	// // }
	// GenieDebug.error("Thread", "getWLanMACAddress --1--");
	//
	//
	// // soapCommand.getWLanMACAddress();
	// // GenieDebug.error("Thread", "getWLanPassword --1--");
	// // soapCommand.getWLanPassword();
	// // GenieDebug.error("Thread", "getGuestAccessInfo --1--");
	// // soapCommand.getGuestAccessInfo();
	// // GenieDebug.error("Thread", "getGuestAccessInfo --2--");
	// //
	//
	// }
	// }.start();
	//
	// }

	private void thisfinish() {

		this.finish();

		m_finish = true;
		if (null != mGenieStatis) {
			mGenieStatis.Stop();
		}
		System.gc();
	}

	//
	// public void onClick(DialogInterface dialog,int which)
	// {
	// 32131
	// switch(which)
	// {
	// case DialogInterface.BUTTON_POSITIVE:
	// {
	// EditText edit = (EditText)login.findViewById(R.id.password);
	// soapCommand.authenticateUserAndPassword("admin",edit.getText().toString());
	// }
	// break;
	// case DialogInterface.BUTTON_NEGATIVE:
	// {
	// thisfinish();
	// }
	// break;
	//
	// default:
	// break;
	// }
	// }

	private void goToUrl(String text) {
		String url = "http://support.netgear.com/search/" + text;
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	public void onClickSearch() {
		String search_text = null;
		search_text = search_edit.getText().toString();

		if (search_text != null && search_text.length() > 0) {
			goToUrl(search_text);
		}
	}

	// private Thread m_GetRouterType_thread = null;

	public void GetRouterType() {

		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE,
				GenieSoap.KEY_NULL);
		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION,
				GenieSoap.KEY_NULL);
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME,
				GenieSoap.KEY_NULL);
		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS,
				GenieSoap.KEY_NULL);
		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED,
				GenieSoap.KEY_NULL);

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo currentsetting = new GenieRequestInfo();
		currentsetting.aRequestLable = "GenieMainView";
		currentsetting.aHttpType = GenieGlobalDefines.EHttpGetCurrentSetting;
		currentsetting.aRequestType = GenieGlobalDefines.RequestActionType.Http;
		currentsetting.aTimeout = 20000;
		requestinfo.add(currentsetting);

		GenieRequest GetCurrentSeting = new GenieRequest(this, requestinfo);
		GetCurrentSeting.SetProgressInfo(true, true);
		GetCurrentSeting.Start();

		// m_GetRouterType_thread = new Thread()
		// {
		// public void run()
		// {
		// try{
		// GenieDebug.error("Thread", "checkAvailabelPort --1--");
		// //soapCommand.checkAvailabelPort(); //qicheng.ai change
		// soapCommand.getRouterType();
		// //soapCommand.getGuestAccessEnabled();
		// }catch (Exception e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// }
		// }
		// };
		// m_GetRouterType_thread.start();
	}

	public void onClick(View v) {
		Intent intent = new Intent();

		// GenieDebug.error("debug",
		// "onClick!soapCommand.netisable "+soapCommand.netisable);

		GenieDebug.error("onClick", "v.getId() = " + v.getId());

		if (v.getId() == R.id.search) {
			GenieDebug.error("onClick", "R.id.search");
			onClickSearch();
			return;
		}

		if (v.getId() == LinearLayout_BaseID
				+ GenieGlobalDefines.EFunctionMyMedia) {
			if (m_key) {
				m_media = true;
			}
			if (m_media) {
				intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
						GenieGlobalDefines.ACTIVE_FUNCTION_MYMEDIA);
				intent.setClass(GenieMainView.this, GenieDlnaTab.class);
				// startActivity(intent);
				ishide = true;
				startActivity(intent);
				// startActivityForResult(intent,GenieGlobalDefines.EFunctionMyMedia);
				return;
			}
		}
		/**
		 * 新增加文件浏览功能
		 */
		// if (v.getId() == LinearLayout_BaseID
		// + GenieGlobalDefines.EFunctionFileBrowse) {
		// intent.setClass(GenieMainView.this, FileBrowseTab.class);
		// ishide = true;
		// startActivity(intent);
		// return;
		// }
		
		
		/**
		 * 新增文件传输功能
		 */
//		 if (v.getId() == LinearLayout_BaseID
//				 + GenieGlobalDefines.EFunctionFileTransfer) {
//				 intent.setClass(GenieMainView.this, Turbo_DevicelistActivity.class);
//				 ishide = true;
//				 startActivity(intent);
//				 return;
//				 }
		


		m_key = false;

		m_ClickID = v.getId();

		// m_login_flag=true;
		if (wifiisable) {

			switch (v.getId()) {
			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionWireless):
				if (!m_login_flag) {
					ToLoginView(m_ClickID);
					// ShowLoginDialog();
					break;
				}
				intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
						GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
				intent.setClass(GenieMainView.this, GenieListView.class);
				// startActivity(intent);
				ishide = true;
				startActivityForResult(intent,
						GenieGlobalDefines.EFunctionWireless);
				break;

			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionGuestAccess):
				if (!m_login_flag) {
					ToLoginView(m_ClickID);
					// ShowLoginDialog();
					break;
				}
				intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
						GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS);
				intent.setClass(GenieMainView.this, GenieListView.class);
				// startActivity(intent);
				ishide = true;
				startActivityForResult(intent,
						GenieGlobalDefines.EFunctionGuestAccess);
				break;

			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionMap):
				if (!m_login_flag) {
					ToLoginView(m_ClickID);
					// ShowLoginDialog();
					break;
				}
				intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
						GenieGlobalDefines.ACTIVE_FUNCTION_MAP);
				intent.setClass(GenieMainView.this, GenieMap.class);
				// startActivity(intent);
				ishide = true;
				startActivityForResult(intent, GenieGlobalDefines.EFunctionMap);
				break;

			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionTraffic):
				if (!m_login_flag) {
					ToLoginView(m_ClickID);
					// ShowLoginDialog();
					break;
				}
				intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
						GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC);
				intent.setClass(GenieMainView.this, GenieListView.class);
				// startActivity(intent);
				ishide = true;
				startActivityForResult(intent,
						GenieGlobalDefines.EFunctionTraffic);
				break;

			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionQRCode):
				intent.setClass(GenieMainView.this, CaptureActivity.class);
				// startActivity(intent);
				ishide = true;
				startActivityForResult(intent,
						GenieGlobalDefines.EFunctionQRCode);
				break;
			// case (LinearLayout_BaseID +
			// GenieGlobalDefines.EFunctionSignalStrength):
			// intent.setClass(GenieMainView.this, GenieWirelessSignal.class);
			// // startActivity(intent);
			// ishide = true;
			// startActivity(intent);
			// //
			// startActivityForResult(intent,GenieGlobalDefines.EFunctionSignalStrength);
			// break;
			// wifi analyzer
			// case (LinearLayout_BaseID +
			// GenieGlobalDefines.EFunctionWifiAnalyzer):{
			// int
			// defaultEntry=GenieGlobalDefines.GetWiFiScanDefaultEntry(GenieMainView.this);
			// switch(defaultEntry){
			// case 0:{
			// intent.setClass(GenieMainView.this, GenieWifiScan.class);
			// break;
			// }
			// case 1:{
			// intent.setClass(GenieMainView.this, GenieChannels.class);
			// break;
			// }
			// case 2:{
			// intent.setClass(GenieMainView.this, GenieSignalHistogram.class);
			// break;
			// }
			// case 3:{
			// intent.setClass(GenieMainView.this, GenieSignalGraph.class);
			// break;
			// }
			// case 4:{
			// intent.setClass(GenieMainView.this, GenieWifiAct3.class);
			// break;
			// }
			// case 5:{
			// intent.setClass(GenieMainView.this, WifiRoomSignal.class);
			// break;
			// }
			// default:{
			// intent.setClass(GenieMainView.this, WifiRoomSignal.class);
			// break;
			// }
			// }
			// ishide = true;
			// startActivity(intent);
			// break;
			// }
			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionGenieAppStaging):
				Uri uri = Uri
						.parse("https://genie.netgear.com/UserProfile/#AppStorePlace:"); // appgenie-staging
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionSpeedTest):
				break;

			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionMyMedia):
				intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
						GenieGlobalDefines.ACTIVE_FUNCTION_MYMEDIA);

				intent.setClass(GenieMainView.this, GenieDlnaTab.class);
				// startActivity(intent);
				ishide = true;
				startActivity(intent);
				// startActivityForResult(intent,GenieGlobalDefines.EFunctionMyMedia);
				break;

			/**
			 * 新增加 文件浏览功能
			 */
			// case (LinearLayout_BaseID +
			// GenieGlobalDefines.EFunctionFileBrowse):
			// intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
			// GenieGlobalDefines.ACTIVE_FUNCTION_MYMEDIA);
			//
			// intent.setClass(GenieMainView.this, FileBrowseTab.class);
			// // startActivity(intent);
			// ishide = true;
			// startActivity(intent);
			// //
			// startActivityForResult(intent,GenieGlobalDefines.EFunctionMyMedia);
			// break;
				
				
				/**
				 * 新增加 文件传输功能
				 */
//				 case (LinearLayout_BaseID +
//				 GenieGlobalDefines.EFunctionFileTransfer):
//				 intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
//				 GenieGlobalDefines.ACTIVE_FUNCTION_MYMEDIA);
//				
//				 intent.setClass(GenieMainView.this, FileBrowseTab.class);
//				 // startActivity(intent);
//				 ishide = true;
//				 startActivity(intent);
//				 //
//				 startActivityForResult(intent,GenieGlobalDefines.EFunctionMyMedia);
//				 break;
				
				
			// case (LinearLayout_BaseID+GenieGlobalDefines.EFunctionMyMedia):
			// intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
			// GenieGlobalDefines.ACTIVE_FUNCTION_MYMEDIA);
			// intent.setClass(GenieMainView.this, GenieDlnaTab.class);
			// //startActivity(intent);
			// ishide = true;
			// //startActivity(intent);
			// startActivityForResult(intent,GenieGlobalDefines.EFunctionMyMedia);
			// break;

			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionParental):
				if (!m_login_flag) {
					ToLoginView(m_ClickID);
					// ShowLoginDialog();
					break;
				}
				intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
						GenieGlobalDefines.ACTIVE_FUNCTION_PARENT_CONTROL);
				intent.setClass(GenieMainView.this, GenieLPCmanage.class);
				// startActivity(intent);
				ishide = true;
				startActivityForResult(intent,
						GenieGlobalDefines.EFunctionParental);
				break;

			}

		} else {
			toast();
		}

	}

	private boolean IsConnectWIFI() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo(); // WIFI

		// GenieDebug.error("debug","getTypeName="+activeNetInfo.getTypeName());

		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return activeNetInfo.isConnected();
		} else {
			return false;
		}
	}

	private void GotoActivity(int id) {

		GenieDebug.error("debug", "GotoActivity!! id = " + id);

		Intent intent = new Intent();

		switch (id) {
		case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionWireless):
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
			intent.setClass(GenieMainView.this, GenieListView.class);
			// startActivity(intent);
			ishide = true;
			startActivityForResult(intent, GenieGlobalDefines.EFunctionWireless);
			break;

		case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionGuestAccess):
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS);
			intent.setClass(GenieMainView.this, GenieListView.class);
			// startActivity(intent);
			ishide = true;
			startActivityForResult(intent,
					GenieGlobalDefines.EFunctionGuestAccess);
			break;

		case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionMap):
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_MAP);
			intent.setClass(GenieMainView.this, GenieMap.class);
			// startActivity(intent);
			ishide = true;
			startActivityForResult(intent, GenieGlobalDefines.EFunctionMap);
			break;

		case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionTraffic):
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC);
			intent.setClass(GenieMainView.this, GenieListView.class);
			// startActivity(intent);
			ishide = true;
			startActivityForResult(intent, GenieGlobalDefines.EFunctionTraffic);
			break;

		case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionQRCode):
			intent.setClass(GenieMainView.this, CaptureActivity.class);
			// startActivity(intent);
			ishide = true;
			startActivityForResult(intent, GenieGlobalDefines.EFunctionQRCode);
			break;
		// case (LinearLayout_BaseID +
		// GenieGlobalDefines.EFunctionSignalStrength):
		// intent.setClass(GenieMainView.this, GenieWirelessSignal.class);
		// // startActivity(intent);
		// ishide = true;
		// startActivity(intent);
		// //
		// startActivityForResult(intent,GenieGlobalDefines.EFunctionSignalStrength);
		// break;
		// case (LinearLayout_BaseID +
		// GenieGlobalDefines.EFunctionWifiAnalyzer):{
		// int
		// defaultEntry=GenieGlobalDefines.GetWiFiScanDefaultEntry(GenieMainView.this);
		// switch(defaultEntry){
		// case 0:{
		// intent.setClass(GenieMainView.this, GenieWifiScan.class);
		// break;
		// }
		// case 1:{
		// intent.setClass(GenieMainView.this, GenieChannels.class);
		// break;
		// }
		// case 2:{
		// intent.setClass(GenieMainView.this, GenieSignalHistogram.class);
		// break;
		// }
		// case 3:{
		// intent.setClass(GenieMainView.this, GenieSignalGraph.class);
		// break;
		// }
		// case 4:{
		// intent.setClass(GenieMainView.this, GenieWifiAct3.class);
		// break;
		// }
		// case 5:{
		// intent.setClass(GenieMainView.this, WifiRoomSignal.class);
		// break;
		// }
		// default:{
		// intent.setClass(GenieMainView.this, WifiRoomSignal.class);
		// break;
		// }
		// }
		// ishide = true;
		// startActivity(intent);
		// break;
		//
		// }
		case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionGenieAppStaging):
			Uri uri = Uri
					.parse("https://genie.netgear.com/UserProfile/#AppStorePlace:"); // appgenie-staging
			intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			break;

		case GenieGlobalDefines.EFunctionSpeedTest:
			break;

		case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionMyMedia):
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_MYMEDIA);

			intent.setClass(GenieMainView.this, GenieDlnaTab.class);
			// startActivity(intent);
			ishide = true;
			startActivity(intent);
			// startActivityForResult(intent,GenieGlobalDefines.EFunctionMyMedia);
			break;

		/**
		 * 新增文件浏览功能
		 */
		// case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionFileBrowse):
		// intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
		// GenieGlobalDefines.ACTIVE_FUNCTION_MYMEDIA);
		//
		// intent.setClass(GenieMainView.this, FileBrowseTab.class);
		// // startActivity(intent);
		// ishide = true;
		// startActivity(intent);
		// //
		// startActivityForResult(intent,GenieGlobalDefines.EFunctionMyMedia);
		// break;

		case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionParental):
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_PARENT_CONTROL);
			intent.setClass(GenieMainView.this, GenieLPCmanage.class);
			// startActivity(intent);
			ishide = true;
			startActivityForResult(intent, GenieGlobalDefines.EFunctionParental);
			break;
		}

		m_ClickID = -1;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		GenieDebug.error("onActivityResult", "onActivityResult requestCode = "
				+ requestCode);
		GenieDebug.error("onActivityResult", "onActivityResult resultCode = "
				+ resultCode);

		ImageView netgearlog = (ImageView) findViewById(R.id.netgear);

		LinearLayout appstore = (LinearLayout) findViewById(LinearLayout_BaseID
				+ GenieGlobalDefines.EFunctionGenieAppStaging);
		if (GenieGlobalDefines.GetSmartNetworkFlag(this) == 1) {
			netgearlog.setBackgroundResource(R.drawable.genie_new);
			appstore.setVisibility(View.VISIBLE);
		} else {
			netgearlog.setBackgroundResource(R.drawable.genie);
			appstore.setVisibility(View.INVISIBLE);
		}

		Button back = (Button) findViewById(R.id.back);

		switch (requestCode) {
		case GenieGlobalDefines.EFunctionWireless:
			ActivityResult[GenieGlobalDefines.EFunctionWireless] = resultCode;
			if (resultCode == GenieGlobalDefines.WIRELESSSETTING_RESULT) {
				OnBackClick();
			}
			break;

		case GenieGlobalDefines.EFunctionGuestAccess:
			ActivityResult[GenieGlobalDefines.EFunctionGuestAccess] = resultCode;
			if (resultCode == GenieGlobalDefines.WIRELESSSETTING_RESULT) {
				OnBackClick();
			}
			break;

		case GenieGlobalDefines.EFunctionMap:
			ActivityResult[GenieGlobalDefines.EFunctionMap] = resultCode;
			break;

		case GenieGlobalDefines.EFunctionTraffic:
			ActivityResult[GenieGlobalDefines.EFunctionTraffic] = resultCode;
			break;

		case GenieGlobalDefines.EFunctionSpeedTest:
			break;
		case GenieGlobalDefines.EFunctionQRCode:
			ActivityResult[GenieGlobalDefines.EFunctionQRCode] = resultCode;
			break;

		// case GenieGlobalDefines.EFunctionSignalStrength:
		// ActivityResult[GenieGlobalDefines.EFunctionSignalStrength] =
		// resultCode;
		// break;
		// wifi analyzer
		// case GenieGlobalDefines.EFunctionWifiAnalyzer:
		// ActivityResult[GenieGlobalDefines.EFunctionWifiAnalyzer]=requestCode;
		// break;

		case GenieGlobalDefines.EFunctionMyMedia:
			ActivityResult[GenieGlobalDefines.EFunctionReadyShare] = resultCode;
			break;

		/**
		 * 新增加文件浏览功能
		 */
		// case GenieGlobalDefines.EFunctionFileBrowse:
		// ActivityResult[GenieGlobalDefines.EFunctionFileBrowse] = resultCode;
		// break;

		/**
		 * 新增文件传输功能
		 */
//		case GenieGlobalDefines.EFunctionFileTransfer:
//			ActivityResult[GenieGlobalDefines.EFunctionFileTransfer] = resultCode;
//			break;

		case GenieGlobalDefines.EFunctionParental:
			ActivityResult[GenieGlobalDefines.EFunctionParental] = resultCode;
			break;
		case GenieGlobalDefines.SMARTNETWORKLOGIN_REQUESTCODE:
			switch (resultCode) {
			case GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_SUCEESS:
				m_login_flag = true;
				// Button back = (Button)findViewById(R.id.back);
				back.setBackgroundResource(R.drawable.title_bt_bj);
				back.setText(R.string.back);
				break;
			case GenieGlobalDefines.LOCALLOGIN_RESULT_SUCEESS:
				m_login_flag = true;
				// Button back = (Button)findViewById(R.id.back);
				back.setBackgroundResource(R.drawable.title_bt_bj);
				back.setText(R.string.logout);
				break;
			case GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_FAILED:
				m_login_flag = false;
				GenieRequest.m_SmartNetWork = false;
				InitBackText();
				break;
			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionWireless):
			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionGuestAccess):
			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionMap):
			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionTraffic):
			case (LinearLayout_BaseID + GenieGlobalDefines.EFunctionParental):
				m_login_flag = true;
				// Button back = (Button)findViewById(R.id.back);
				back.setBackgroundResource(R.drawable.title_bt_bj);
				if (GenieRequest.m_SmartNetWork) {
					back.setText(R.string.back);
				} else {
					back.setText(R.string.logout);
				}
				GotoActivity(resultCode);
				break;
			}
		}
		// super.onActivityResult(requestCode, resultCode, data);
		// onActivityResult_setview(data,requestCode);
	}

	public void onActivityResult_setview(Intent data, int requestCode) {
		TextView textview = null;
		Bundle bundle = null;

		GenieDebug.error("onActivityResult_setview",
				"onActivityResult_setview length =" + ActivityResult.length);

		for (int i = 0; i < ActivityResult.length; i++) {

			switch (i) {
			case GenieGlobalDefines.EFunctionWireless:
				if (wifiisable) {
					if (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success) {
						// textview =
						// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionWireless));
						// textview.setTextColor(getResources().getColor(R.color.limegreen));

						// if(!GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID).equals(GenieGlobalDefines.NULL))
						// {
						// textview.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID));
						// }else
						// {
						// textview.setText(getResources().getString(R.string.connected));
						// }

						// textview.setText("321dsfsd1f32sd1fsd321f32sd");
					} else {
						// textview =
						// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionWireless));
						// textview.setTextColor(getResources().getColor(R.color.limegreen));
						// textview.setText(getResources().getString(R.string.Clickhere));
						// textview.setText(""); // qicheng.ai change
					}
				} else {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionWireless));
					// textview.setTextColor(getResources().getColor(R.color.crimson));
					// textview.setText(getResources().getString(R.string.notenabled));
				}
				break;

			case GenieGlobalDefines.EFunctionGuestAccess:
				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success)) {
					if (!GenieSoap.dictionary.get(
							GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE)
							.equals(GenieGlobalDefines.NULL)) {
						if (GenieSoap.dictionary.get(
								GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE)
								.indexOf("1") > -1) {
							ActivityText[i] = GenieSoap.dictionary
									.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID);
						} else {

							ActivityText[i] = getResources().getString(
									R.string.notenabled);
						}
					} else {
						ActivityText[i] = "";// qicheng.ai change
												// getResources().getString(R.string.Clickhere);
					}
					// ActivityText[i] = ;
				}
				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_failure)) {
					ActivityText[i] = "";// qicheng.ai change
											// getResources().getString(R.string.Clickhere);
				}
				if (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success) {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionGuestAccess));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);
				} else {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionGuestAccess));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);
				}
				break;

			case GenieGlobalDefines.EFunctionMap:
				GenieDebug.error("onActivityResult_setview", " EFunctionMap");
				GenieDebug.error("onActivityResult_setview",
						"onActivityResult_setview ActivityResult[i] ="
								+ ActivityResult[i]);

				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success)) {
					int count = 0;
					bundle = data.getExtras();
					count = bundle
							.getInt(GenieGlobalDefines.EFunctionMap_Result);
					ActivityText[i] = getResources().getString(R.string.number)
							+ ":" + String.valueOf(count);
				}
				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_failure)) {
					ActivityText[i] = "";// qicheng.ai change
											// getResources().getString(R.string.Clickhere);
				}
				if (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success) {

					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionMap));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);
				} else {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionMap));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);

				}
				break;

			case GenieGlobalDefines.EFunctionTraffic:
				GenieDebug.error("debug", "EFunctionTraffic requestCode = "
						+ requestCode);
				GenieDebug.error("debug",
						"EFunctionTraffic ActivityResult[i] = "
								+ ActivityResult[i]);
				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success)) {
					GenieDebug
							.error("debug",
									"EFunctionTraffic requeststr = "
											+ GenieSoap.dictionary
													.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE));
					if (!GenieSoap.dictionary.get(
							GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE)
							.equals(GenieGlobalDefines.NULL)) {
						if (GenieSoap.dictionary.get(
								GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE)
								.equals("1")) {
							ActivityText[i] = getResources().getString(
									R.string.Enable);
						}

						if (GenieSoap.dictionary.get(
								GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE)
								.equals("0")) {
							ActivityText[i] = getResources().getString(
									R.string.notenabled);
						}

						// if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE).equals("2"))
						// {
						// ActivityText[i] =
						// getResources().getString(R.string.notsupport);
						// }
					} else {
						ActivityText[i] = "";// qicheng.ai change
												// getResources().getString(R.string.Clickhere);
					}
				}
				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_failure)) {
					ActivityText[i] = "";// qicheng.ai change
											// getResources().getString(R.string.Clickhere);
				}

				if (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success) {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionTraffic));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);
				} else {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionTraffic));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);
				}
				break;

			case GenieGlobalDefines.EFunctionSpeedTest:
				break;

			// case GenieGlobalDefines.EFunctionSignalStrength:
			//
			// break;
			// case GenieGlobalDefines.EFunctionWifiAnalyzer:
			// break;
			case GenieGlobalDefines.EFunctionQRCode:
				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success)) {
					// ActivityResult[i] = ;
				}
				if (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success) {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionSpeedTest));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);
				} else {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionSpeedTest));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);

				}
				break;

			// case GenieGlobalDefines.EFunctionReadyShare:
			// TextView textview =
			// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionWireless));
			// textview.setTextColor(getResources().getColor(R.color.limegreen));
			// textview.setText(getResources().getString(R.string.Clickhere));
			// break;

			case GenieGlobalDefines.EFunctionParental:
				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success)) {

					if (!GenieSoap.dictionary.get(
							GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS)
							.equals(GenieGlobalDefines.NULL)) {
						if (GenieSoap.dictionary.get(
								GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS)
								.equals("1")) {
							ActivityText[i] = getResources().getString(
									R.string.Enable);
						}

						if (GenieSoap.dictionary.get(
								GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS)
								.equals("0")) {
							ActivityText[i] = getResources().getString(
									R.string.notenabled);
						}

					} else {
						ActivityText[i] = "";// qicheng.ai change
												// getResources().getString(R.string.Clickhere);
					}
				}
				if ((requestCode == i)
						&& (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_failure)) {
					ActivityText[i] = "";// qicheng.ai change
											// getResources().getString(R.string.Clickhere);
				}
				if (ActivityResult[i] == GenieGlobalDefines.EFunctionResult_Success) {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionParental));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);
				} else {
					// textview =
					// (TextView)findViewById((BaseID+GenieGlobalDefines.EFunctionParental));
					// textview.setTextColor(getResources().getColor(R.color.limegreen));
					// textview.setText(ActivityText[i]);
				}
				break;
			}
		}
	}

	public GenieMainView GetMainView() {
		return this;
	}

	public void toast() {
		// GenieDebug.error("toast","  --toast-- in");
		// // toast = Toast.makeText(getApplicationContext(),
		// String error = null;
		//
		// if(!wifiisable)
		// {
		// error = getResources().getString(R.string.main_wifi_disable);
		// }else if(wifiisable && !soapCommand.netisable)
		// {
		// //error = getResources().getString(R.string.main_net_disable);
		// //error = "connect error";
		// return ;
		// }else
		// {
		// //error = "Unknownerror";
		// return ;
		// }
		//
		// toast = Toast.makeText(this,
		// error, Toast.LENGTH_LONG);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		// toast.show();
	}

	public void ShowToast() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// GenieDebug.error("debug",
				// "showtoast!!progressDialog "+progressDialog.isShowing());

				closeWaitingDialog();
				toast();

			}
		});

	}

	public void SaveUserinfo2File(String password) {

		GenieDebug.error("debug", "SaveUserinfo2File password =" + password);
		if (userInfo.isSave == '1') {
			SaveLoginPassword(password);
			// GenieShareAPI.strncpy(userInfo.password,
			// password.toCharArray(),0,password.length());
		}

		GenieShareAPI.WriteData2File(userInfo, this,
				GenieGlobalDefines.USER_INFO_FILENAME);
	}

	public void Auto_Login(boolean need) {
		// if(!need && userInfo.isSave == '1')
		// {
		// new Thread()
		// {
		// public void run()
		// {
		// m_loginpassword = GetLoginPassword();
		//
		// soapCommand.authenticateUserAndPassword("admin",
		// m_loginpassword);
		// }
		// }.start();
		//
		// // soapCommand.authenticateUserAndPassword(new
		// String(userInfo.username,0,userInfo.username[39]),
		// // new String(userInfo.password,0,userInfo.password[39]));
		// }
		// else
		// {
		// runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// //showLogin();
		// ShowLoginProgressBarInVisible();
		// }
		// });
		// }
	}

	// private void ShowLoginProgressBarVisible()
	// {
	// if(null == LoginDialog)
	// return ;
	// LoginDialog.GetLoginDialogProgressBar().setVisibility(View.VISIBLE);
	// LoginDialog.GetLoginDialogWaitinfo().setVisibility(View.VISIBLE);
	// LoginDialog.GetLoginDialoglogin_ok().setEnabled(false);
	// LoginDialog.GetLoginDialogPassWord().setEnabled(false);
	// ShowLoginAlartInfoInVisible();
	// ShowLoginBad_passwordInVisible();
	//
	// }
	// private void ShowLoginProgressBarInVisible()
	// {
	// if(null == LoginDialog)
	// return ;
	// LoginDialog.GetLoginDialogProgressBar().setVisibility(View.INVISIBLE);
	// LoginDialog.GetLoginDialogWaitinfo().setVisibility(View.INVISIBLE);
	// LoginDialog.GetLoginDialoglogin_ok().setEnabled(true);
	// LoginDialog.GetLoginDialogPassWord().setEnabled(true);
	// ShowLoginAlartInfoInVisible();
	// ShowLoginBad_passwordInVisible();
	// }
	//
	// private void ShowLoginAlartInfoVisible(int id)
	// {
	// GenieDebug.error("debug","ShowLoginAlartInfoVisible 0");
	// if(null == LoginDialog)
	// return ;
	// GenieDebug.error("debug","ShowLoginAlartInfoVisible 1");
	//
	// ShowLoginProgressBarInVisible();
	// LoginDialog.GetLoginDialogAlartinfo().setVisibility(View.VISIBLE);
	// LoginDialog.GetLoginDialogAlartinfo().setText(id);
	// LoginDialog.GetLoginDialoglogin_ok().setEnabled(false);
	//
	// LoginDialog.GetLoginDialogWaitinfo().setVisibility(View.GONE);
	// LoginDialog.GetLoginDialogbad_password().setVisibility(View.GONE);
	// }
	//
	//
	// private void ShowLoginAlartInfoInVisible()
	// {
	// if(null == LoginDialog)
	// return ;
	//
	// LoginDialog.GetLoginDialogAlartinfo().setVisibility(View.GONE);
	// }
	//
	//
	// public void ShowLoginBad_passwordVisible()
	// {
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	//
	//
	// if(null == LoginDialog)
	// return ;
	//
	//
	// LoginDialog.GetLoginDialogbad_password().setVisibility(View.VISIBLE);
	// LoginDialog.GetLoginDialogAlartinfo().setVisibility(View.GONE);
	// ShowLoginProgressBarInVisible();
	// LoginDialog.GetLoginDialogWaitinfo().setVisibility(View.GONE);
	// }
	// }
	// );
	// }
	//
	//
	// private void ShowLoginBad_passwordInVisible()
	// {
	// if(null == LoginDialog)
	// return ;
	//
	// LoginDialog.GetLoginDialogAlartinfo().setVisibility(View.GONE);
	// }

	private String LoginPassword = null;

	// private void OnLoginOK()
	// {
	// LoginPassword =
	// LoginDialog.GetLoginDialogPassWord().getText().toString();
	//
	// new Thread()
	// {
	// public void run()
	// {
	// GenieDebug.error("onClick","ShowLoginDialog run LoginPassword ="+LoginPassword);
	//
	// //soapCommand.getRouterType();
	// soapCommand.authenticateUserAndPassword("admin",LoginPassword);
	// }
	// }.start();
	// }

	private String m_loginpassword = null;

	public String GetLoginPassword() {
		GenieDebug.error("debug", "settings GetLoginPassword ");
		SharedPreferences settings = getSharedPreferences(
				GenieGlobalDefines.SETTING_INFO, 0);
		if (settings != null) {
			String password = settings.getString(GenieGlobalDefines.PASSWORD,
					"password");

			GenieDebug.error("debug",
					"settings GetLoginPassword settings != null password = "
							+ password);
			return password;
		} else {
			GenieDebug
					.error("debug",
							"settings GetLoginPassword settings == null return password  ");
			return "password";
		}
	}

	public void SaveLoginPassword(String password) {
		GenieDebug.error("debug", "settings SaveLoginPassword password = "
				+ password);
		SharedPreferences settings = getSharedPreferences(
				GenieGlobalDefines.SETTING_INFO, 0);
		if (null != settings) {
			GenieDebug.error("debug",
					"settings SaveLoginPassword null != settings");
			settings.edit().putString(GenieGlobalDefines.PASSWORD, password)
					.commit();
		}

	}

	private void ToLoginView(int ClickId) {
		Intent intent = new Intent();
		intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
				GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_FAILED);
		intent.putExtra(GenieGlobalDefines.CLICK_ID, ClickId);
		intent.setClass(GenieMainView.this, GenieSmartNetworkLogin.class);
		startActivityForResult(intent,
				GenieGlobalDefines.SMARTNETWORKLOGIN_REQUESTCODE);
	}

	private void ShowLoginDialog() {
		GenieLoginDialog LoginDialog = new GenieLoginDialog(this);
		LoginDialog.show();
		LoginDialog.SetOnLoginSucces(new OnLoginSucces() {

			@Override
			public void OnSucces(GenieLoginDialog dialog) {
				// TODO Auto-generated method stub
				GenieDebug.error("debug", "GenieMainView LoginSucces ");
				dialog.dismiss();

				IsNetgearRouter = true;

				InitBackText();
				DisableGuestTime();

				if (!m_login_flag) {
					m_login_flag = true;
					GenieRequest.m_SmartNetWork = false;
					InitBackText();
					GotoActivity(m_ClickID);
				}
			}
		});
		LoginDialog.SetOnBackPressed(new OnBackPressed() {

			@Override
			public void OnBack(GenieLoginDialog dialog) {
				// TODO Auto-generated method stub
				thisfinish();
			}
		});

		LoginDialog.SetOnCanclePressed(new OnCanclePressed() {

			@Override
			public void OnCancle(GenieLoginDialog dialog) {
				// TODO Auto-generated method stub

				GenieDebug.error("debug", "OnCanclePressed m_ClickID = "
						+ m_ClickID);
				GenieDebug.error("debug",
						"OnCanclePressed dialog.m_notnetgearrouter = "
								+ dialog.m_notnetgearrouter);
				if (m_ClickID == LinearLayout_BaseID
						+ GenieGlobalDefines.EFunctionWireless
						&& dialog.m_notnetgearrouter == 0) {
					Intent intent = new Intent();

					intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
							GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS_EMPTY);
					intent.setClass(GenieMainView.this, GenieListView.class);
					// startActivity(intent);
					ishide = true;
					startActivityForResult(intent,
							GenieGlobalDefines.EFunctionWireless);
				}
			}
		});
	}

	// private void ShowLoginDialog()
	// {
	//
	//
	// if(null != LoginDialog)
	// return;
	//
	// GenieDebug.error("onClick","ShowLoginDialog 0");
	//
	// LoginDialog = new GenieLoginDialog(this);
	// LoginDialog.show();
	//
	// GenieDebug.error("onClick","ShowLoginDialog 1");
	//
	// m_loginpassword = GetLoginPassword();
	//
	//
	//
	// GenieDebug.error("onClick","ShowLoginDialog userInfo.isSave = "+userInfo.isSave);
	// GenieDebug.error("onClick","ShowLoginDialog m_loginpassword = "+m_loginpassword);
	//
	//
	// if(userInfo.isSave == '1')
	// {
	// LoginDialog.GetLoginDialogCheckBox().setChecked(true);
	// LoginDialog.GetLoginDialogPassWord().setText(m_loginpassword);
	// }
	// else
	// {
	// LoginDialog.GetLoginDialogCheckBox().setChecked(false);
	// LoginDialog.GetLoginDialogPassWord().setText("");
	// }
	//
	// GenieDebug.error("onClick","ShowLoginDialog 2");
	//
	// LoginDialog.GetLoginDialogCheckBox().setOnCheckedChangeListener(new
	// CheckBox.OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView, boolean
	// isChecked) {
	// // TODO Auto-generated method stub
	// if(LoginDialog.GetLoginDialogCheckBox().isChecked())
	// {
	// userInfo.isSave = '1';
	// }else
	// {
	// userInfo.isSave = '0';
	// }
	// }
	// });
	//
	//
	// GenieDebug.error("onClick","ShowLoginDialog 3");
	//
	// LoginDialog.GetLoginDialoglogin_ok().setOnClickListener(new
	// OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	//
	// GenieDebug.error("onClick","ShowLoginDialog onClick 00");
	//
	// ShowLoginProgressBarVisible();
	//
	// ShowLoginBad_passwordInVisible();
	// ShowLoginAlartInfoInVisible();
	//
	// GenieDebug.error("onClick","ShowLoginDialog onClick 01");
	//
	// GenieDebug.error("onClick","ShowLoginDialog 0");
	//
	// OnLoginOK();
	//
	// // new Thread()
	// // {
	// // public void run()
	// // {
	// // GenieDebug.error("onClick","ShowLoginDialog run 0");
	// //
	// //
	// soapCommand.authenticateUserAndPassword("admin",LoginDialog.GetLoginDialogPassWord().getText().toString());
	// // }
	// // }.start();
	//
	// }
	// });
	//
	// GenieDebug.error("onClick","ShowLoginDialog 4");
	//
	// LoginDialog.GetLoginDialogIconlogin_cancel().setOnClickListener(new
	// OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// //thisfinish();
	// CloseLoginDialog();
	// m_login_flag = false;
	// InitBackText();
	// }
	// });
	//
	//
	//
	// LoginDialog.GetLoginDialogPassWord().setOnEditorActionListener(new
	// OnEditorActionListener() {
	//
	// @Override
	// public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	// // TODO Auto-generated method stub
	//
	// InputMethodManager imm = (InputMethodManager)
	// getSystemService(INPUT_METHOD_SERVICE);
	//
	// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	//
	// GenieDebug.error("debug", "onEditorAction actionId =  "+actionId);
	// GenieDebug.error("onClick","onEditorAction onClick 00");
	//
	// ShowLoginProgressBarVisible();
	//
	// GenieDebug.error("onClick","onEditorAction onClick 01");
	//
	// GenieDebug.error("onClick","onEditorAction 0");
	//
	// OnLoginOK();
	// return false;
	// }
	// });
	//
	// ShowLoginProgressBarVisible();
	//
	//
	// //ShowLoginAlartInfoVisible(R.string.noconnectrouter);
	// }

	// private void CloseLoginDialog()
	// {
	// if(null == LoginDialog)
	// return ;
	// LoginDialog.dismiss();
	// LoginDialog = null;
	// }
	//
	//
	// public class GenieLoginDialog2 extends Dialog
	// {
	// private ImageView icon = null;
	// private TextView title = null;
	//
	// private TextView waitinfo = null;
	// private TextView alartinfo = null;
	// private TextView bad_password = null;
	// private Button login_ok = null;
	// private Button login_cancel = null;
	//
	// private CheckBox login_box = null;
	//
	// private ProgressBar login_ProgressBar = null;
	//
	// private EditText Login_username = null;
	//
	// private EditText Login_password = null;
	// private boolean m_inputpassword = false;
	//
	// public GenieLoginDialog2(Context context) {
	// super(context);
	// // TODO Auto-generated constructor stub
	//
	//
	// requestWindowFeature(Window.FEATURE_NO_TITLE);
	// setContentView(R.layout.login_dialog);
	//
	// // int width = getWindowManager().getDefaultDisplay().getWidth();
	// // int height = getWindowManager().getDefaultDisplay().getHeight();
	// //
	// //
	// GenieDebug.error("GenieLoginDialog","GenieLoginDialog width = "+width);
	// //
	// GenieDebug.error("GenieLoginDialog","GenieLoginDialog height = "+height);
	// //
	// // if(getResources().getConfiguration().orientation ==
	// Configuration.ORIENTATION_LANDSCAPE)
	// // {
	// // this.getWindow().setLayout(((width/5)*3), height);
	// // }
	//
	// icon = (ImageView)findViewById(R.id.login_icon);
	// title = (TextView)findViewById(R.id.login_title);
	// icon.setBackgroundResource(android.R.drawable.ic_dialog_alert);
	// title.setText(R.string.login);
	//
	// login_box = (CheckBox)findViewById(R.id.remeberpassword);
	//
	// login_ok = (Button)findViewById(R.id.login_ok);
	// login_cancel = (Button)findViewById(R.id.login_cancel);
	//
	// waitinfo = (TextView)findViewById(R.id.wait_text);
	// alartinfo = (TextView)findViewById(R.id.alertinfo);
	// bad_password = (TextView)findViewById(R.id.bad_password);
	//
	// login_ProgressBar = (ProgressBar)findViewById(R.id.progress);
	//
	// Login_username = (EditText)findViewById(R.id.username);
	// Login_password = (EditText)findViewById(R.id.password);
	//
	// if(Login_password.getText().toString().length() > 0)
	// {
	// m_inputpassword = true;
	// }else
	// {
	// m_inputpassword = false;
	// }
	// Login_password.addTextChangedListener(new TextWatcher() {
	//
	// @Override
	// public void onTextChanged(CharSequence s, int start, int before, int
	// count) {
	// // TODO Auto-generated method stub
	// EditText temp = (EditText)findViewById(R.id.password);
	// if(temp.getText().toString().length() > 0)
	// {
	// m_inputpassword = true;
	// }else
	// {
	// m_inputpassword = false;
	// }
	// if(m_inputpassword )
	// {
	// GetLoginDialoglogin_ok().setEnabled(true);
	// }else
	// {
	// GetLoginDialoglogin_ok().setEnabled(false);
	// }
	//
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence s, int start, int count,
	// int after) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void afterTextChanged(Editable s) {
	// // TODO Auto-generated method stub
	//
	// }
	// });
	//
	//
	//
	// if(m_inputpassword )
	// {
	// GetLoginDialoglogin_ok().setEnabled(true);
	// }else
	// {
	// GetLoginDialoglogin_ok().setEnabled(false);
	// }
	//
	// }
	//
	//
	//
	// public ImageView GetLoginDialogIcon()
	// {
	// return icon ;
	// }
	// public TextView GetLoginDialogTitle()
	// {
	// return title ;
	// }
	//
	// public TextView GetLoginDialogWaitinfo()
	// {
	// return waitinfo;
	// }
	// public TextView GetLoginDialogAlartinfo()
	// {
	// return alartinfo ;
	// }
	// public TextView GetLoginDialogbad_password()
	// {
	// return bad_password ;
	// }
	//
	//
	// private Button GetLoginDialoglogin_ok()
	// {
	// return login_ok;
	// }
	// public Button GetLoginDialogIconlogin_cancel()
	// {
	// return login_cancel;
	// }
	//
	// public CheckBox GetLoginDialogCheckBox()
	// {
	// return login_box ;
	// }
	//
	// public ProgressBar GetLoginDialogProgressBar()
	// {
	// return login_ProgressBar;
	// }
	//
	// public EditText GetLoginDialogUsername()
	// {
	// return Login_username;
	// }
	//
	// public EditText GetLoginDialogPassWord()
	// {
	// return Login_password;
	// }
	//
	//
	// @Override
	// public void onBackPressed() {
	// // TODO Auto-generated method stub
	// super.onBackPressed();
	// thisfinish();
	// }
	//
	//
	// @Override
	// public boolean dispatchKeyEvent(KeyEvent event) {
	// // TODO Auto-generated method stub
	// if(event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)
	// {
	// return true;
	// }
	// return super.dispatchKeyEvent(event);
	// }
	//
	//
	// }

	public int getGateWay() {

		WifiManager test = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo = test.getDhcpInfo();

		GenieDebug.error("debug", "gateway = " + dhcpInfo.gateway);

		return -1;
	}

	private AlertDialog Dialog_NotNetgearRouter = null;

	private void DismissNotNetgearRouterDialog() {
		if (Dialog_NotNetgearRouter != null) {
			if (Dialog_NotNetgearRouter.isShowing()) {
				Dialog_NotNetgearRouter.dismiss();
			}
			Dialog_NotNetgearRouter = null;
		}
	}

	private void ShowNotNetgearRouterDialog(int messageid) {
		DismissNotNetgearRouterDialog();

		AlertDialog.Builder dialog_wifiset = new AlertDialog.Builder(this);

		// dialog_wifiset.setIcon(android.R.drawable.ic_dialog_alert);
		// dialog_wifiset.setTitle(" ");
		dialog_wifiset.setMessage(messageid);
		dialog_wifiset.setPositiveButton(R.string.close,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});

		Dialog_NotNetgearRouter = dialog_wifiset.create();

		Dialog_NotNetgearRouter.show();

	}

	private ProgressDialog m_getsettingprogressDialog = null;

	public void showGetSettingWaitingDialog() {
		closeGetSettingWaitingDialog();
		// progressDialog = ProgressDialog.show(GenieMainView.this,
		// "Loading...", "Please wait...", true, false);
		m_getsettingprogressDialog = ProgressDialog.show(GenieMainView.this,
				null, getResources().getString(R.string.pleasewait) + "...",
				true, false);
	}

	public void closeGetSettingWaitingDialog() {
		if (m_getsettingprogressDialog != null) {
			m_getsettingprogressDialog.dismiss();
			m_getsettingprogressDialog = null;
		}
	}

	private RequestReceiver m_RequestReceiver = null;

	private void UnRegisterBroadcastReceiver() {
		if (null != m_RequestReceiver) {
			unregisterReceiver(m_RequestReceiver);
			m_RequestReceiver = null;
		}
	}

	private void RegisterBroadcastReceiver() {
		UnRegisterBroadcastReceiver();

		m_RequestReceiver = new RequestReceiver();
		IntentFilter filter = new IntentFilter();// 鍒涘缓IntentFilter瀵硅薄
		filter.addAction(GenieGlobalDefines.REQUEST_ACTION_RET_BROADCAST);
		registerReceiver(m_RequestReceiver, filter);// 娉ㄥ唽Broadcast Receiver
	}

	private class RequestReceiver extends BroadcastReceiver {// 缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�

		@Override
		public void onReceive(Context context, Intent intent) {// 閲嶅啓onReceive鏂规硶

			String lable = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_LABLE);
			RequestActionType ActionType = (RequestActionType) intent
					.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE);
			RequestResultType aResultType = (RequestResultType) intent
					.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE);
			String aServer = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SERVER);
			String aMethod = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_METHOD);
			String aResponseCode = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSECODE);
			int aHttpResponseCode = intent.getIntExtra(
					GenieGlobalDefines.REQUEST_ACTION_RET_HTTPRESPONSECODE, -2);
			int aHttpType = intent.getIntExtra(
					GenieGlobalDefines.REQUEST_ACTION_RET_HTTP_TYPE, -2);
			int aSoapType = intent.getIntExtra(
					GenieGlobalDefines.REQUEST_ACTION_RET_SOAP_TYPE, -2);
			String aResponse = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSE);

			GenieDebug.error("debug", "RequestReceiver onReceive lable ="
					+ lable);

			if (lable != null && lable.equals("GenieMainView")) {
				GenieDebug.error("debug", "RequestReceiver onReceive aServer ="
						+ aServer);
				GenieDebug.error("debug", "RequestReceiver onReceive aMethod ="
						+ aMethod);
				GenieDebug.error("debug",
						"RequestReceiver onReceive aResponseCode ="
								+ aResponseCode);
				GenieDebug.error("debug",
						"RequestReceiver onReceive aHttpResponseCode ="
								+ aHttpResponseCode);
				GenieDebug.error("debug",
						"RequestReceiver onReceive aResponse =" + aResponse);
				GenieDebug.error("debug",
						"RequestReceiver onReceive ActionType =" + ActionType);
				GenieDebug
						.error("debug",
								"RequestReceiver onReceive aResultType ="
										+ aResultType);
				if (null != ActionType && ActionType == RequestActionType.Http) {
					GenieDebug.error("debug",
							"GenieLoginDialog onReceive aHttpType ="
									+ aHttpType);
					if (aHttpType == GenieGlobalDefines.EHttpGetCurrentSetting) {
						if (null != aResultType) {
							if (aResultType == RequestResultType.Succes) {
								String isgenie = GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE); // ,
																							// GenieGlobalDefines.ROUTER_TYPE_OLD_NETGEAR);
								String mFV = GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION);
								String mMN = GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
								String InternetStatus = GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS);
								String LpcSupport = GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED);

								GenieDebug.error("debug",
										"GenieLoginDialog CurrentSetting isgenie ="
												+ isgenie);
								GenieDebug.error("debug",
										"GenieLoginDialog CurrentSetting mFV ="
												+ mFV);
								GenieDebug.error("debug",
										"GenieLoginDialog CurrentSetting mMN ="
												+ mMN);
								GenieDebug.error("debug",
										"GenieLoginDialog CurrentSetting InternetStatus ="
												+ InternetStatus);
								GenieDebug.error("debug",
										"GenieLoginDialog CurrentSetting LpcSupport ="
												+ LpcSupport);
								if (isgenie
										.equals(GenieGlobalDefines.ROUTER_TYPE_NEW_NETGEAR)
										|| isgenie
												.equals(GenieGlobalDefines.ROUTER_TYPE_OLD_NETGEAR)) {
									GotoActivity(m_ClickID);
								} else {
									ShowNotConnectNetgearRouterDialog();
								}
							} else {
								ShowNotConnectNetgearRouterDialog();
							}
						}

					}
				} else if (null != ActionType
						&& ActionType == RequestActionType.Soap) {

				} else if (null != ActionType
						&& ActionType == RequestActionType.SmartNetWork) {

					int aSmartType = intent.getIntExtra(
							GenieGlobalDefines.REQUEST_ACTION_RET_SMART_TYPE,
							-2);

					switch (aSmartType) {
					case GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST:
						ShowSmartNetworkRouterListView();
						break;
					case GenieGlobalDefines.ESMARTNETWORK_StartRouterSession:
						if (null != aResultType
								&& aResultType == RequestResultType.Succes) {
							SaveSmartNetworkRouterInfo(GenieRequest.m_SmartInfo
									.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid));
							DismissSmartNetworkRouterListView();
							GenieRequest.m_SmartNetWork = true;
							m_login_flag = true;
						} else {
							GenieRequest.m_SmartNetWork = false;
						}
						break;
					default:
						break;
					}
				}
			}

		}
	}

	private AlertDialog Dialog_NotConnectNetgearRouter = null;

	private void DismissNotConnectNetgearRouterDialog() {
		if (Dialog_NotConnectNetgearRouter != null) {
			if (Dialog_NotConnectNetgearRouter.isShowing()) {
				Dialog_NotConnectNetgearRouter.dismiss();
			}
			Dialog_NotConnectNetgearRouter = null;
		}
	}

	private void ShowNotConnectNetgearRouterDialog() {
		DismissNotConnectNetgearRouterDialog();

		// R.string.notnetgearrouter

		LayoutInflater inflater = LayoutInflater.from(this);
		View devicedetaildolg = inflater.inflate(R.layout.lpcintro, null);

		TextView info = (TextView) devicedetaildolg.findViewById(R.id.Lpcintro);

		SpannableString sp = new SpannableString(getResources().getString(
				R.string.notnetgearrouter));

		sp.setSpan(
				new URLSpan(
						"http://www.netgear.com/landing/en-us/netgear-genie-routers.aspx"),
				38, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		//
		// String html =
		// "333313131313131313131"+"<a href=\"http://support.netgear.com/app/answers/list/kw/link%20rate\">"+getResources().getString(R.string.whatisit)+"</a>";
		//
		// info.setText(Html.fromHtml(html));

		info.setText(sp);
		info.setMovementMethod(LinkMovementMethod.getInstance());

		AlertDialog.Builder dialog_wifiset = new AlertDialog.Builder(this);

		// dialog_wifiset.setIcon(android.R.drawable.ic_dialog_alert);
		// dialog_wifiset.setTitle(" ");
		dialog_wifiset.setView(devicedetaildolg);
		dialog_wifiset.setPositiveButton(R.string.close,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});

		Dialog_NotConnectNetgearRouter = dialog_wifiset.create();

		Dialog_NotConnectNetgearRouter.show();

	}

	private PopupWindow smartnetworklogin = null;
	private EditText smartusernameview = null;
	private EditText smartpasswordview = null;

	public void ShowSmartNetworkLoginView() {
		if (m_login_flag) {
			if (!GenieRequest.m_SmartNetWork) {
				m_login_flag = false;
				GenieRequest.m_SmartNetWork = false;
				InitBackText();
			} else {
				Intent intent = new Intent();
				intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
						GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_SUCEESS);
				intent.setClass(GenieMainView.this,
						GenieSmartNetworkLogin.class);
				startActivityForResult(intent,
						GenieGlobalDefines.SMARTNETWORKLOGIN_REQUESTCODE);
			}

		} else {
			Intent intent = new Intent();
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_FAILED);
			intent.setClass(GenieMainView.this, GenieSmartNetworkLogin.class);
			startActivityForResult(intent,
					GenieGlobalDefines.SMARTNETWORKLOGIN_REQUESTCODE);
		}
	}

	// public void ShowSmartNetworkLoginView()
	// {
	// LayoutInflater inflater = LayoutInflater.from(this);
	// View smartlogin = inflater.inflate(R.layout.smartnetworklogin,null);
	// Button login_ok = (Button)smartlogin.findViewById(R.id.button_ok);
	// Button login_cancel =
	// (Button)smartlogin.findViewById(R.id.button_cancel);
	// smartusernameview =
	// (EditText)smartlogin.findViewById(R.id.smartusername);
	// smartpasswordview =
	// (EditText)smartlogin.findViewById(R.id.smartpassword);
	//
	// String user = GetSmartNetworkUsername();
	// String pass = GetSmartNetworkPassword();
	//
	// GenieDebug.error("debug","ShowSmartNetworkLoginView user="+user);
	// GenieDebug.error("debug","ShowSmartNetworkLoginView pass="+pass);
	//
	// if(null != user && null != pass )
	// {
	// smartusernameview.setText(user);
	// smartpasswordview.setText(pass);
	// }
	//
	// login_ok.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(null != smartusernameview
	// && null != smartpasswordview)
	// {
	// SmartNetWorkInitAndAuth(smartusernameview.getText().toString(),
	// smartpasswordview.getText().toString());
	// }
	// }
	// });
	//
	// login_cancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(null != smartnetworklogin)
	// {
	// if(smartnetworklogin.isShowing())
	// {
	// smartnetworklogin.dismiss();
	// }
	// smartnetworklogin = null;
	// }
	// smartusernameview = null;
	// smartpasswordview = null;
	// }
	// });
	//
	// smartnetworklogin = new
	// PopupWindow(smartlogin,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,true);
	//
	// smartnetworklogin.setAnimationStyle(R.style.smartpopwindow_anim_style);
	//
	// smartnetworklogin.showAtLocation(this.getWindow().getDecorView(),
	// Gravity.BOTTOM, 0, 0);
	// }

	private void DismissSmartNetWorkLoginView() {
		if (smartnetworklogin != null) {
			if (smartnetworklogin.isShowing())
				smartnetworklogin.dismiss();
			smartnetworklogin = null;
		}
	}

	private void SmartNetWorkInitAndAuth(String username, String password) {
		GenieDebug.error("debug", "SmartNetWorkInitAndAuth username = "
				+ username);
		GenieDebug.error("debug", "SmartNetWorkInitAndAuth password = "
				+ password);

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo test = new GenieRequestInfo();
		test.aRequestLable = "GenieMainView";
		test.aSmartType = GenieGlobalDefines.ESMARTNETWORKAUTHENTICATE;
		test.aServer = "/fcp/authenticate";
		// test.aMethod = "Authenticate";
		// test.aNeedwrap = false;
		test.aRequestType = GenieGlobalDefines.RequestActionType.SmartNetWork;
		// test.aNeedParser=false;
		test.aTimeout = 20000;
		test.aElement = new ArrayList<String>();
		test.aElement.add(username);
		test.aElement.add(password);

		requestinfo.add(test);

		GenieRequest check = new GenieRequest(this, requestinfo);
		check.SetProgressInfo(true, true);
		check.Start();

	}

	private void SmartRouterStartSession(String username, String password) {
		GenieDebug.error("debug", "SmartRouterStartSession username="
				+ username);
		GenieDebug.error("debug", "SmartRouterStartSession password="
				+ password);

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo test = new GenieRequestInfo();
		test.aRequestLable = "GenieMainView";
		test.aSmartType = GenieGlobalDefines.ESMARTNETWORK_StartRouterSession;
		// test.aServer = "/fcp/authenticate";
		// test.aMethod = "Authenticate";
		// test.aNeedwrap = false;
		test.aRequestType = GenieGlobalDefines.RequestActionType.SmartNetWork;
		// test.aNeedParser=false;
		// test.aTimeout = 20000;
		test.aElement = new ArrayList<String>();
		test.aElement.add(username);
		test.aElement.add(password);

		requestinfo.add(test);

		GenieRequest check = new GenieRequest(this, requestinfo);
		check.SetProgressInfo(true, true);
		check.Start();
	}

	// ////////////////////////////////////////////////////////////////////////

	private PopupWindow smartnetworkrouterlist = null;
	private View m_SmartRouterLoginView = null;

	public void DismissSmartNetworkRouterListView() {
		if (smartnetworkrouterlist != null) {
			if (smartnetworkrouterlist.isShowing())
				smartnetworkrouterlist.dismiss();
			smartnetworkrouterlist = null;

		}
		m_SmartRouterLoginView = null;
	}

	private ViewFlipper m_ViewFlipper = null;

	public void ShowSmartNetworkRouterListView() {
		DismissSmartNetWorkLoginView();

		SaveSmartNetworkInfo(GenieRequest.m_SmartInfo.username,
				GenieRequest.m_SmartInfo.password);

		LayoutInflater inflater = LayoutInflater.from(this);
		m_SmartRouterLoginView = inflater.inflate(R.layout.smartrouterview,
				null);

		m_ViewFlipper = (ViewFlipper) m_SmartRouterLoginView
				.findViewById(R.id.viewFlipper);
		m_ViewFlipper.addView(LayoutInflater.from(this).inflate(
				R.layout.smartrouterlist, null));
		m_ViewFlipper.addView(LayoutInflater.from(this).inflate(
				R.layout.smartrouterlogin, null));

		LinearLayout smartlistview = (LinearLayout) m_SmartRouterLoginView
				.findViewById(R.id.smartlistview);
		RelativeLayout Smartrouterloginview = (RelativeLayout) m_SmartRouterLoginView
				.findViewById(R.id.Smartrouterloginview);

		// smartlistview.setVisibility(View.VISIBLE);
		// Smartrouterloginview.setVisibility(View.GONE);

		ListView routerlist = (ListView) m_SmartRouterLoginView
				.findViewById(R.id.routerlist);

		EfficientAdapter_smartrouter listItemAdapter = new EfficientAdapter_smartrouter(
				this);
		routerlist.setAdapter(listItemAdapter);
		routerlist.setItemChecked(-1, true);

		routerlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (GenieRequest.m_SmartInfo != null
						&& GenieRequest.m_SmartInfo.routerlist != null) {
					GenieSmartRouterInfo routerinfo = GenieRequest.m_SmartInfo.routerlist
							.get((int) arg3);
					if (routerinfo != null) {
						GenieRequest.m_SmartInfo.workcpid = routerinfo.cpid;
						ShowSmartRouterLoginView(routerinfo);
					}
				}
			}
		});

		smartnetworkrouterlist = new PopupWindow(m_SmartRouterLoginView,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);

		smartnetworkrouterlist.setBackgroundDrawable(new BitmapDrawable());
		smartnetworkrouterlist
				.setAnimationStyle(R.style.smartpopwindow_anim_style);

		smartnetworkrouterlist.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				smartnetworkrouterlist = null;
				m_ViewFlipper = null;
			}
		});
		// if(main_layout != null)
		// {
		// smartnetworkrouterlist.showAtLocation(main_layout, Gravity.BOTTOM, 0,
		// 0);
		// }
		smartnetworkrouterlist.showAtLocation(this.getWindow().getDecorView(),
				Gravity.BOTTOM, 0, 0);
	}

	private void ShowSmartRouterLoginView(GenieSmartRouterInfo routerinfo) {
		if (null == m_SmartRouterLoginView || null == routerinfo)
			return;

		LinearLayout smartlistview = (LinearLayout) m_SmartRouterLoginView
				.findViewById(R.id.smartlistview);
		RelativeLayout Smartrouterloginview = (RelativeLayout) m_SmartRouterLoginView
				.findViewById(R.id.Smartrouterloginview);

		TextView friendly_name = (TextView) Smartrouterloginview
				.findViewById(R.id.friendly_name);
		TextView active = (TextView) Smartrouterloginview
				.findViewById(R.id.active);
		TextView serial = (TextView) Smartrouterloginview
				.findViewById(R.id.serial);
		ImageView icon = (ImageView) Smartrouterloginview
				.findViewById(R.id.router_icon);
		TextView model = (TextView) Smartrouterloginview
				.findViewById(R.id.model);

		EditText username = (EditText) Smartrouterloginview
				.findViewById(R.id.smartrouterusername);
		EditText password = (EditText) Smartrouterloginview
				.findViewById(R.id.smartrouterpassword);

		String routeruser = GetSmartNetworkRouterUsername(routerinfo.serial);
		String routerpass = GetSmartNetworkRouterPassword(routerinfo.serial);

		GenieDebug.error("debug", "ShowSmartRouterLoginView routeruser = "
				+ routeruser);
		GenieDebug.error("debug", "ShowSmartRouterLoginView routerpass = "
				+ routerpass);

		if (null != routeruser && null != routerpass) {
			username.setText(routeruser);
			password.setText(routerpass);
		}

		friendly_name.setText(routerinfo.friendly_name);
		model.setText(routerinfo.model);
		active.setText(routerinfo.active);
		serial.setText(routerinfo.serial);

		int routertype = GetRouterTypeID(routerinfo.model);
		if (routertype != -1) {
			icon.setBackgroundResource(routertype);
		} else {
			icon.setBackgroundResource(R.drawable.gatewaydev);
		}

		Button login_ok = (Button) Smartrouterloginview
				.findViewById(R.id.smartrouterlogin_ok);
		Button login_cancel = (Button) Smartrouterloginview
				.findViewById(R.id.smartrouterlogin_cancel);

		login_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != m_SmartRouterLoginView) {
					RelativeLayout Smartrouterloginview = (RelativeLayout) m_SmartRouterLoginView
							.findViewById(R.id.Smartrouterloginview);

					EditText username = (EditText) Smartrouterloginview
							.findViewById(R.id.smartrouterusername);
					EditText password = (EditText) Smartrouterloginview
							.findViewById(R.id.smartrouterpassword);
					SmartRouterStartSession(username.getText().toString(),
							password.getText().toString());
				}
			}
		});

		login_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != m_SmartRouterLoginView) {

					if (null != m_ViewFlipper) {
						m_ViewFlipper.showPrevious();
					}
					// LinearLayout smartlistview =
					// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
					// RelativeLayout Smartrouterloginview =
					// (RelativeLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
					//
					// smartlistview.setVisibility(View.VISIBLE);
					// Smartrouterloginview.setVisibility(View.GONE);
				}
			}
		});

		if (null != m_ViewFlipper) {
			m_ViewFlipper.showNext();
		}
		// smartlistview.setVisibility(View.GONE);
		// Smartrouterloginview.setVisibility(View.VISIBLE);

	}

	// private void ShowSmartRouterLoginView(GenieSmartRouterInfo routerinfo)
	// {
	// if(null == m_SmartRouterLoginView || null == routerinfo )
	// return;
	//
	// LinearLayout smartlistview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
	// RelativeLayout Smartrouterloginview =
	// (RelativeLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	//
	//
	//
	// smartlistview.setVisibility(View.VISIBLE);
	// Smartrouterloginview.setVisibility(View.VISIBLE);
	//
	// TextView friendly_name = (TextView)
	// Smartrouterloginview.findViewById(R.id.friendly_name);
	// TextView active = (TextView)
	// Smartrouterloginview.findViewById(R.id.active);
	// TextView serial = (TextView)
	// Smartrouterloginview.findViewById(R.id.serial);
	// ImageView icon = (ImageView)
	// Smartrouterloginview.findViewById(R.id.router_icon);
	// TextView model = (TextView)
	// Smartrouterloginview.findViewById(R.id.model);
	//
	// EditText username =
	// (EditText)Smartrouterloginview.findViewById(R.id.smartrouterusername);
	// EditText password =
	// (EditText)Smartrouterloginview.findViewById(R.id.smartrouterpassword);
	//
	// String routeruser = GetSmartNetworkRouterUsername(routerinfo.serial);
	// String routerpass = GetSmartNetworkRouterPassword(routerinfo.serial);
	//
	// GenieDebug.error("debug","ShowSmartRouterLoginView routeruser = "+routeruser);
	// GenieDebug.error("debug","ShowSmartRouterLoginView routerpass = "+routerpass);
	//
	//
	// if(null != routeruser &&
	// null != routerpass)
	// {
	// username.setText(routeruser);
	// password.setText(routerpass);
	// }
	//
	// friendly_name.setText(routerinfo.friendly_name);
	// model.setText(routerinfo.model);
	// active.setText(routerinfo.active);
	// serial.setText(routerinfo.serial);
	//
	// int routertype = GetRouterTypeID(routerinfo.model);
	// if(routertype != -1)
	// {
	// icon.setBackgroundResource(routertype);
	// }else
	// {
	// icon.setBackgroundResource(R.drawable.gatewaydev);
	// }
	//
	// Button login_ok =
	// (Button)Smartrouterloginview.findViewById(R.id.smartrouterlogin_ok);
	// Button login_cancel =
	// (Button)Smartrouterloginview.findViewById(R.id.smartrouterlogin_cancel);
	//
	// login_ok.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(null != m_SmartRouterLoginView )
	// {
	// RelativeLayout Smartrouterloginview =
	// (RelativeLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	// EditText username =
	// (EditText)Smartrouterloginview.findViewById(R.id.smartrouterusername);
	// EditText password =
	// (EditText)Smartrouterloginview.findViewById(R.id.smartrouterpassword);
	// SmartRouterStartSession(username.getText().toString(),password.getText().toString());
	// }
	// }
	// });
	//
	// login_cancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if(null != m_SmartRouterLoginView )
	// {
	//
	// LinearLayout smartlistview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
	// RelativeLayout Smartrouterloginview =
	// (RelativeLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	// Animation rightInAnimation =
	// AnimationUtils.loadAnimation(getApplicationContext(),
	// R.anim.push_right_in);
	// Animation rightOutAnimation =
	// AnimationUtils.loadAnimation(getApplicationContext(),
	// R.anim.push_right_out);
	// smartlistview.setAnimation(rightInAnimation);
	// Smartrouterloginview.setAnimation(rightOutAnimation);
	// rightInAnimation.setAnimationListener(new AnimationListener() {
	// @Override
	// public void onAnimationStart(Animation animation) {
	//
	// if(null != m_SmartRouterLoginView )
	// {
	// LinearLayout smartlistview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
	// //LinearLayout Smartrouterloginview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	// smartlistview.setVisibility(View.VISIBLE);
	// //Smartrouterloginview.setVisibility(View.VISIBLE);
	// }
	//
	// }
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// if(null != m_SmartRouterLoginView )
	// {
	// LinearLayout smartlistview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
	// //LinearLayout Smartrouterloginview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	// smartlistview.setVisibility(View.VISIBLE);
	// //Smartrouterloginview.setVisibility(View.VISIBLE);
	// }
	// }
	// });
	//
	// rightOutAnimation.setAnimationListener(new AnimationListener() {
	// @Override
	// public void onAnimationStart(Animation animation) {
	//
	// }
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// if(null != m_SmartRouterLoginView )
	// {
	// //LinearLayout smartlistview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
	// RelativeLayout Smartrouterloginview =
	// (RelativeLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	// //smartlistview.setVisibility(View.GONE);
	// Smartrouterloginview.setVisibility(View.INVISIBLE);
	// }
	// }
	// });
	// }
	// }
	// });
	//
	//
	//
	// Animation leftInAnimation =
	// AnimationUtils.loadAnimation(getApplicationContext(),
	// R.anim.push_left_in);
	// Animation leftOutAnimation =
	// AnimationUtils.loadAnimation(getApplicationContext(),
	// R.anim.push_left_out);
	//
	// //Animation leftInAnimation =
	// AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
	// //Animation leftOutAnimation =
	// AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hold);
	//
	// smartlistview.setAnimation(leftOutAnimation);
	// Smartrouterloginview.setAnimation(leftInAnimation);
	// leftInAnimation.setAnimationListener(new AnimationListener() {
	// @Override
	// public void onAnimationStart(Animation animation) {
	//
	// GenieDebug.error("debug","ShowSmartRouterLoginView leftInAnimation.onAnimationStart 0");
	//
	// if(null != m_SmartRouterLoginView )
	// {
	// GenieDebug.error("debug","ShowSmartRouterLoginView leftInAnimation.onAnimationStart 1");
	// //LinearLayout smartlistview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
	// RelativeLayout Smartrouterloginview =
	// (RelativeLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	// //smartlistview.setVisibility(View.VISIBLE);
	// Smartrouterloginview.setVisibility(View.VISIBLE);
	// }
	//
	// }
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	// @Override
	// public void onAnimationEnd(Animation animation) {
	//
	// GenieDebug.error("debug","ShowSmartRouterLoginView leftInAnimation.onAnimationEnd 0");
	//
	// if(null != m_SmartRouterLoginView )
	// {
	// GenieDebug.error("debug","ShowSmartRouterLoginView leftInAnimation.onAnimationEnd 1");
	// //LinearLayout smartlistview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
	// RelativeLayout Smartrouterloginview =
	// (RelativeLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	// //smartlistview.setVisibility(View.VISIBLE);
	// Smartrouterloginview.setVisibility(View.VISIBLE);
	// }
	//
	// if(null != m_SmartRouterLoginView )
	// {
	// GenieDebug.error("debug","ShowSmartRouterLoginView leftOutAnimation.onAnimationEnd 1");
	// LinearLayout smartlistview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.smartlistview);
	// //LinearLayout Smartrouterloginview =
	// (LinearLayout)m_SmartRouterLoginView.findViewById(R.id.Smartrouterloginview);
	//
	// smartlistview.setVisibility(View.INVISIBLE);
	// //Smartrouterloginview.setVisibility(View.VISIBLE);
	// }
	//
	// if(null != smartnetworkrouterlist)
	// {
	// smartnetworkrouterlist.update();
	// }
	// }
	// });
	//
	// leftOutAnimation.setAnimationListener(new AnimationListener() {
	// @Override
	// public void onAnimationStart(Animation animation) {
	//
	// GenieDebug.error("debug","ShowSmartRouterLoginView leftOutAnimation.onAnimationEnd 0");
	// }
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// GenieDebug.error("debug","ShowSmartRouterLoginView leftOutAnimation.onAnimationEnd 0");
	//
	//
	//
	// }
	// });
	//
	//
	// }

	private class EfficientAdapter_smartrouter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap mIcon1;

		private Bitmap file_video;
		private Bitmap file_audio;
		private Bitmap file_image;

		public EfficientAdapter_smartrouter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);

			// Icons bound to the rows.
			// mIcon1 = BitmapFactory.decodeResource(context.getResources(),
			// R.drawable.renderer);
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			if (GenieRequest.m_SmartInfo != null
					&& GenieRequest.m_SmartInfo.routerlist != null) {
				return GenieRequest.m_SmartInfo.routerlist.size();
			} else {
				return 0;
			}
			// return m_listdata.length;
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return position;
			// return m_listdata.get(position);
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_smartrouter,
						null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.friendly_name = (TextView) convertView
						.findViewById(R.id.friendly_name);
				holder.active = (TextView) convertView
						.findViewById(R.id.active);
				holder.serial = (TextView) convertView
						.findViewById(R.id.serial);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.router_icon);
				holder.model = (TextView) convertView.findViewById(R.id.model);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// GenieDebug.error("debug", "View getView position= "+position);
			// GenieDebug.error("debug",
			// "View getView m_renderlistdata.get(position).m_select= "+m_renderlistdata.get(position).m_select);

			// Bind the data efficiently with the holder.

			String active = null;
			String friendly_name = null;
			String model = null;
			String serial = null;

			if (GenieRequest.m_SmartInfo != null
					&& GenieRequest.m_SmartInfo.routerlist != null
					&& GenieRequest.m_SmartInfo.routerlist.get(position) != null) {
				active = GenieRequest.m_SmartInfo.routerlist.get(position).active;
				friendly_name = GenieRequest.m_SmartInfo.routerlist
						.get(position).friendly_name;
				model = GenieRequest.m_SmartInfo.routerlist.get(position).model;
				serial = GenieRequest.m_SmartInfo.routerlist.get(position).serial;
			}
			if (friendly_name != null) {
				holder.friendly_name.setText(friendly_name);
			}
			if (model != null) {
				holder.model.setText(model);
			}

			if (active != null) {
				holder.active.setText(active);
			}
			if (serial != null) {
				holder.serial.setText(serial);
			}

			int routertype = GetRouterTypeID(model);
			if (routertype != -1) {
				holder.icon.setBackgroundResource(routertype);
			} else {
				holder.icon.setBackgroundResource(R.drawable.gatewaydev);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView icon;
			TextView friendly_name;
			TextView serial;
			TextView active;
			TextView model;
		}
	}

	private int GetRouterTypeID(String routertype) {
		int ret = -1;

		if (routertype == null)
			return ret;

		// routertype = "R6300";

		routertype = routertype.toUpperCase();

		// GenieDebug.error("debug","GetRouterTypeID routertype = "+routertype);

		for (int i = 0; i < m_Routertype.length; i++) {
			if (routertype.startsWith(m_Routertype[i])) {
				return image[i];
			}
		}

		return ret;
	}

	private String m_Routertype[] = { "CG3300", "CGD24G", "DG834GT", "DG834GV",
			"DG834G", "DG834N", "DG834PN", "DG834", "DGN1000_RN", "DGN2200M",
			"DGN2200", "DGN2000", "DGN3500", "DGNB2100", "DGND3300",
			"DGND3700", "DM111PSP", "DM111P", "JWNR2000T", "MBM621",
			"MBR624GU", "MBR1210-1BMCNS", "MBRN3000", "RP614", "WGR612",
			"WGR614L", "WGR614", "WGT624", "WNB2100", "WNDR37AV", "WNDR3300",
			"WNDR3400", "WNDR3700", "WNDR3800", "WNDR4000", "WNDRMAC",
			"WNR612", "WNR834B", "WNR834M", "WNR854T", "WNR1000", "WNR2000",
			"WNR2200", "WNR3500L", "WNR3500", "WNXR2000", "WPN824EXT",
			"WPN824N", "WPN824", "WNDR4500", "WNDR4700", "DGND4000", "WNR500",
			"JNR3000", "JNR3210", "JWNR2000", "R6300", };

	private int image[] = {

	R.drawable.cg3300, R.drawable.cgd24g, R.drawable.dg834gt,
			R.drawable.dg834gv, R.drawable.dg834g, R.drawable.dg834n,
			R.drawable.dg834pn, R.drawable.dg834, R.drawable.dgn1000_rn,
			R.drawable.dgn2200m, R.drawable.dgn2200, R.drawable.dgn2000,
			R.drawable.dgn3500, R.drawable.dgnb2100, R.drawable.dgnd3300,
			R.drawable.dgnd3700, R.drawable.dm111psp, R.drawable.dm111p,
			R.drawable.jwnr2000t, R.drawable.mbm621, R.drawable.mbr624gu,
			R.drawable.mbr1210_1bmcns, R.drawable.mbrn3000, R.drawable.rp614,
			R.drawable.wgr612, R.drawable.wgr614l, R.drawable.wgr614,
			R.drawable.wgt624, R.drawable.wnb2100, R.drawable.wndr37av,
			R.drawable.wndr3300, R.drawable.wndr3400, R.drawable.wndr3700,
			R.drawable.wndr3800, R.drawable.wndr4000, R.drawable.wndrmac,
			R.drawable.wnr612, R.drawable.wnr834b, R.drawable.wnr834m,
			R.drawable.wnr854t, R.drawable.wnr1000, R.drawable.wnr2000,
			R.drawable.wnr2200, R.drawable.wnr3500l, R.drawable.wnr3500,
			R.drawable.wnxr2000, R.drawable.wpn824ext, R.drawable.wpn824n,
			R.drawable.wpn824, R.drawable.wndr4500, R.drawable.wndr4700,
			R.drawable.dgnd4000, R.drawable.wnr500, R.drawable.jnr3000,
			R.drawable.jnr3210, R.drawable.jwnr2000, R.drawable.r6300, };

	private String GetSmartNetworkRouterUsername(String serial) {
		GenieDebug.error("debug", "GetSmartNetworkRouterUsername ");
		SharedPreferences settings = getSharedPreferences(
				GenieGlobalDefines.SMARTNETWORK, 0);
		if (settings != null) {
			String username = settings.getString(
					GenieGlobalDefines.SMARTROUTERUSERRNAME + serial, null);
			return username;
		} else {
			return null;
		}
	}

	private String GetSmartNetworkRouterPassword(String serial) {
		GenieDebug.error("debug", "GetSmartNetworkRouterPassword ");
		SharedPreferences settings = getSharedPreferences(
				GenieGlobalDefines.SMARTNETWORK, 0);
		if (settings != null) {
			String password = settings.getString(
					GenieGlobalDefines.SMARTROUTERPASSWORD + serial, null);
			return password;
		} else {
			return null;
		}
	}

	private void SaveSmartNetworkRouterInfo(GenieSmartRouterInfo router) {
		if (router == null)
			return;
		GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.active"
				+ router.active);
		GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.cpid"
				+ router.cpid);
		GenieDebug.error("debug",
				"SaveSmartNetworkRouterInfo router.friendly_name"
						+ router.friendly_name);
		GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.model"
				+ router.model);
		GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.owner"
				+ router.owner);
		GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.password"
				+ router.password);
		GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.username"
				+ router.username);
		GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.serial"
				+ router.serial);
		GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.type"
				+ router.type);

		SharedPreferences settings = getSharedPreferences(
				GenieGlobalDefines.SMARTNETWORK, 0);
		if (null != settings) {
			settings.edit()
					.putString(
							GenieGlobalDefines.SMARTROUTERUSERRNAME
									+ router.serial, router.username).commit();
			settings.edit()
					.putString(
							GenieGlobalDefines.SMARTROUTERPASSWORD
									+ router.serial, router.password).commit();
		}

	}

	private String GetSmartNetworkUsername() {
		GenieDebug.error("debug", "GetSmartNetworkUsername ");
		SharedPreferences settings = getSharedPreferences(
				GenieGlobalDefines.SMARTNETWORK, 0);
		if (settings != null) {
			String username = settings.getString(
					GenieGlobalDefines.SMARTUSERRNAME, null);
			return username;
		} else {
			return null;
		}
	}

	private String GetSmartNetworkPassword() {
		GenieDebug.error("debug", "GetSmartNetworkPassword ");
		SharedPreferences settings = getSharedPreferences(
				GenieGlobalDefines.SMARTNETWORK, 0);
		if (settings != null) {
			String password = settings.getString(
					GenieGlobalDefines.SMARTPASSWORD, null);
			return password;
		} else {
			return null;
		}
	}

	private void SaveSmartNetworkInfo(String username, String password) {
		GenieDebug.error("debug", "SaveSmartNetworkInfo username=" + username);
		GenieDebug.error("debug", "SaveSmartNetworkInfo password=" + password);

		if (username == null || password == null)
			return;

		SharedPreferences settings = getSharedPreferences(
				GenieGlobalDefines.SMARTNETWORK, 0);
		if (null != settings) {
			settings.edit()
					.putString(GenieGlobalDefines.SMARTUSERRNAME, username)
					.commit();
			settings.edit()
					.putString(GenieGlobalDefines.SMARTPASSWORD, password)
					.commit();
		}

	}

	/**
	 *  登录超时 重新  初始化数据
	 */
	
	public void Initdata(){
		m_login_flag = false;
		IsNetgearRouter = true;
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		mMulticastLock = wifiManager.createMulticastLock("dlna_mc_lock");
		mMulticastLock.acquire();
		wifiInfo = wifiManager.getConnectionInfo();
		GenieSoap.initialDictionaryElements();
		userInfo = GenieShareAPI.ReadDataFromFile(this,
				GenieGlobalDefines.USER_INFO_FILENAME);
		towifiset = false;
		wifiisable = true;
		mGenieStatis = new GenieStatistician(this);

	}
}