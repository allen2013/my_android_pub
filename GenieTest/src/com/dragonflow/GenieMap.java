package com.dragonflow;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.dragonflow.genie.ui.R;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.GenieRequest.OnProgressCancelListener;
import com.dragonflow.GenieRequest.RequestFinish;
import com.filebrowse.FileUploadActivity;
import com.filebrowse.ScanDeviceService;

public class GenieMap extends Activity {

	private int activeIndex = 0;
	private int activityresult = 0;

	private int MapPageIndex = 0;

	private int m_wlan_ebable = 0;

	private String GenieMac = null;

	private View devicedetaildolg = null;

	private DeviceInfo device_genie = null;
	private DeviceInfo device_internet = null;
	private DeviceInfo device_route = null;

	private ScrollLayout main_layout = null;

	private Timer settimer = null;
	private TimerTask task = null;

	private DeviceInfo dialog_device = null;

	private LinearLayout pagelayout = null;

	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	private final int FP = ViewGroup.LayoutParams.FILL_PARENT;

	private int maxpagedevice = 0;

	// private GenieSoap soapCommand = null;
	private ProgressDialog progressDialog = null;
	public Button modifybutton = null;
	private static final int menuid = 100;

	public String m_RouterIP = null;

	private AlertDialog.Builder device_dialog = null;

	private Boolean m_modifyflag = false;

	private int SelecteType = 0;

	private int m_index = 0;

	private PopupWindow popupwindow = null;

	private PopupWindow mPopupblockswitch = null;

	private AlertDialog m_dialog = null;

	private WifiManager wifiManager;//
	private WifiInfo wifiInfo;//

	private ArrayList<DeviceInfo> m_devicelist = null;

	private String m_devicetype[] = null;

	private HashMap<String, String> m_MacMap = null;

	private Boolean isback = false;

	private boolean m_DviceNameEdit = false;

	private int m_devicesblockenable = -1;

	private String Own_device = "";
	public final static String SCANDEVICE = "SCAN_DEVICE";
	// public static int Device_type[] = {
	//
	// // R.drawable.internetnormal,
	// // R.drawable.hdm,
	// R.string.gatewaydev, // "Router", // R.drawable.gatewaydev,
	// R.string.networkdev, // "Network Device", // R.drawable.networkdev,
	// R.string.windowspc, // "Windows PC", // R.drawable.windowspc,
	// R.string.bridge, // "Bridge", // R.drawable.bridge,
	// R.string.cablestb, // "Cable STB", // R.drawable.cablestb,
	// R.string.cameradev, // "Camera", // R.drawable.cameradev,
	// R.string.gamedev, // "Gaming Console", // R.drawable.gamedev,
	// R.string.imacdev, // "iMac", // R.drawable.imacdev,
	// R.string.ipad, // "iPad", // R.drawable.ipad,
	// R.string.iphone, // "iPhone", // R.drawable.iphone,
	// R.string.ipodtouch, // "iPod Touch", // R.drawable.ipodtouch,
	// R.string.linuxpc, // "Linux PC", // R.drawable.linuxpc,
	// R.string.macbookdev, // "MacBook", // R.drawable.macbookdev,
	// R.string.macminidev, // "Mac Mini", // R.drawable.macminidev,
	// R.string.macprodev, // "Mac Pro", // R.drawable.macprodev,
	// R.string.mediadev, // "Media Device", // R.drawable.mediadev,
	// R.string.mobiledev, // "Smart Phone", // R.drawable.mobiledev,
	// R.string.netstoragedev, // "Storage", // R.drawable.netstoragedev,
	// R.string.switchdev, // "Switch", // R.drawable.switch,
	// R.string.printerdev, // "Printer", // R.drawable.printerdev,
	// R.string.repeater, // "Repeater", // R.drawable.repeater,
	// R.string.satellitestb, // "Satellite STB", // R.drawable.satellitestb,
	// R.string.scannerdev, // "Scanner", // R.drawable.scannerdev,
	// R.string.slingbox, // "SlingBox", // R.drawable.slingbox,
	// R.string.stb, // "Other STB", // R.drawable.stb,
	// R.string.tablepc, // "Tablet", // R.drawable.tablepc,
	// R.string.tv, // "TV", // R.drawable.tv,
	// R.string.androiddevice,
	// R.string.androidphone,
	// R.string.androidtablet,
	// R.string.unixpc, // "Unix PC", // R.drawable.unixpc,
	// };
	//

	public static int Device_type[] = {

			// R.drawable.internetnormal,
			// R.drawable.hdm,

			R.string.imacdev, // "iMac", // R.drawable.imacdev,
			R.string.ipad, // "iPad", // R.drawable.ipad,
			R.string.iphone, // "iPhone", // R.drawable.iphone,
			R.string.ipodtouch, // "iPod Touch", // R.drawable.ipodtouch,
			R.string.androiddevice, R.string.androidphone,
			R.string.androidtablet, R.string.blurayplayer, R.string.bridge, // "Bridge",
																			// //
																			// R.drawable.bridge,
			R.string.cablestb, // "Cable STB", // R.drawable.cablestb,
			R.string.cameradev, // "Camera", // R.drawable.cameradev,
			R.string.dvr, // "DVR", // R.drawable.dvr,
			R.string.gamedev, // "Gaming Console", // R.drawable.gamedev,
			R.string.linuxpc, // "Linux PC", // R.drawable.linuxpc,
			R.string.macminidev, // "Mac Mini", // R.drawable.macminidev,
			R.string.macprodev, // "Mac Pro", // R.drawable.macprodev,
			R.string.macbookdev, // "MacBook", // R.drawable.macbookdev,
			R.string.mediadev, // "Media Device", // R.drawable.mediadev,
			R.string.networkdev, // "Network Device", // R.drawable.networkdev,
			R.string.stb, // "Other STB", // R.drawable.stb,
			R.string.printerdev, // "Printer", // R.drawable.printerdev,
			R.string.repeater, // "Repeater", // R.drawable.repeater,
			R.string.gatewaydev, // "Router", // R.drawable.gatewaydev,
			R.string.satellitestb, // "Satellite STB", //
									// R.drawable.satellitestb,
			R.string.scannerdev, // "Scanner", // R.drawable.scannerdev,
			R.string.slingbox, // "SlingBox", // R.drawable.slingbox,
			R.string.netstoragedev, // "Storage", // R.drawable.netstoragedev,
			R.string.mobiledev, // "Smart Phone", // R.drawable.mobiledev,
			R.string.switchdev, // "Switch", // R.drawable.switch,
			R.string.tv, // "TV", // R.drawable.tv,
			R.string.tablepc, // "Tablet", // R.drawable.tablepc,
			R.string.unixpc, // "Unix PC", // R.drawable.unixpc,
			R.string.windowspc, // "Windows PC", // R.drawable.windowspc,
	};
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
			"JNR3000", "JNR3210", "JWNR2000", "R6300", "R6200", };

	private int m_RouterTypeId[] = { GenieGlobalDefines.Device_Route_CG3300,
			GenieGlobalDefines.Device_Route_CGD24G,
			GenieGlobalDefines.Device_Route_DG834GT,
			GenieGlobalDefines.Device_Route_DG834GV,
			GenieGlobalDefines.Device_Route_DG834G,
			GenieGlobalDefines.Device_Route_DG834N,
			GenieGlobalDefines.Device_Route_DG834PN,
			GenieGlobalDefines.Device_Route_DG834,
			GenieGlobalDefines.Device_Route_DGN1000_RN,
			GenieGlobalDefines.Device_Route_DGN2200M,
			GenieGlobalDefines.Device_Route_DGN2200,
			GenieGlobalDefines.Device_Route_DGN2000,
			GenieGlobalDefines.Device_Route_DGN3500,
			GenieGlobalDefines.Device_Route_DGNB2100,
			GenieGlobalDefines.Device_Route_DGND3300,
			GenieGlobalDefines.Device_Route_DGND3700,
			GenieGlobalDefines.Device_Route_DM111PSP,
			GenieGlobalDefines.Device_Route_DM111P,
			GenieGlobalDefines.Device_Route_JWNR2000t,
			GenieGlobalDefines.Device_Route_MBM621,
			GenieGlobalDefines.Device_Route_MBR624GU,
			GenieGlobalDefines.Device_Route_MBR1210_1BMCNS,
			GenieGlobalDefines.Device_Route_MBRN3000,
			GenieGlobalDefines.Device_Route_RP614,
			GenieGlobalDefines.Device_Route_WGR612,
			GenieGlobalDefines.Device_Route_WGR614L,
			GenieGlobalDefines.Device_Route_WGR614,
			GenieGlobalDefines.Device_Route_WGT624,
			GenieGlobalDefines.Device_Route_WNB2100,
			GenieGlobalDefines.Device_Route_WNDR37AV,
			GenieGlobalDefines.Device_Route_WNDR3300,
			GenieGlobalDefines.Device_Route_WNDR3400,
			GenieGlobalDefines.Device_Route_WNDR3700,
			GenieGlobalDefines.Device_Route_WNDR3800,
			GenieGlobalDefines.Device_Route_WNDR4000,
			GenieGlobalDefines.Device_Route_WNDRMAC,
			GenieGlobalDefines.Device_Route_WNR612,
			GenieGlobalDefines.Device_Route_WNR834B,
			GenieGlobalDefines.Device_Route_WNR834M,
			GenieGlobalDefines.Device_Route_WNR854T,
			GenieGlobalDefines.Device_Route_WNR1000,
			GenieGlobalDefines.Device_Route_WNR2000,
			GenieGlobalDefines.Device_Route_WNR2200,
			GenieGlobalDefines.Device_Route_WNR3500L,
			GenieGlobalDefines.Device_Route_WNR3500,
			GenieGlobalDefines.Device_Route_WNXR2000,
			GenieGlobalDefines.Device_Route_WPN824EXT,
			GenieGlobalDefines.Device_Route_WPN824N,
			GenieGlobalDefines.Device_Route_WPN824,
			GenieGlobalDefines.Device_Route_WNDR4500,
			GenieGlobalDefines.Device_Route_WNDR4700,
			GenieGlobalDefines.Device_Route_DGND4000,
			GenieGlobalDefines.Device_Route_WNR500,
			GenieGlobalDefines.Device_Route_JNR3000,
			GenieGlobalDefines.Device_Route_JNR3210,
			GenieGlobalDefines.Device_Route_JWNR2000,
			GenieGlobalDefines.Device_Route_R6300,
			GenieGlobalDefines.Device_Route_R6200, };

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String error = null;
			GenieDebug.error("handleMessage", "map handleMessage msg.what = "
					+ msg.what);

			if (isback)
				return;

			// msg.what = GenieGlobalDefines.ESoapRequestfailure;
			switch (msg.what) {

			case GenieGlobalDefines.ESoapRequestRouterMap:
				activityresult = GenieGlobalDefines.EFunctionResult_Success;
				InitMapView();
				// testview();
				CancleProgressDlg();
				int count = GenieSoap.routerMap.size();
				if (count <= 0 && m_index < 3) {
					StartTimer();
				}
				break;
			case GenieGlobalDefines.ESoapRequestfailure:
				activityresult = GenieGlobalDefines.EFunctionResult_failure;
				CancleProgressDlg();
				error = getResources().getString(R.string.failure);
				InitFailureView();

				GenieDebug.error("handleMessage", " 7778 m_index = " + m_index);
				if (m_index < 3) {
					StartTimer();
				}
				break;
			case GenieGlobalDefines.ESoapRequestsuccess:
				activityresult = GenieGlobalDefines.EFunctionResult_Success;
				CancleProgressDlg();
				error = getResources().getString(R.string.success);
				showToast(error);
				break;
			case GenieGlobalDefines.ESoapRequestnotsupport:
				activityresult = GenieGlobalDefines.EFunctionResult_failure;
				CancleProgressDlg();
				error = getResources().getString(R.string.notsupport);
				showToast(error);
				break;
			case GenieGlobalDefines.PopupWindow:
				// PopupPage();
				break;
			}
		}
	};

	public void CanclTimer() {
		if (null != settimer) {
			settimer.cancel();
			settimer = null;
		}
		if (null != task) {
			task = null;
		}
	}

	public void StartTimer() {

		GenieDebug
				.error("StartTimer", " 7778  StartTimer m_index = " + m_index);
		CanclTimer();

		settimer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				GenieDebug.error("run", " 7778 run m_index = " + m_index);

				try {

					if (m_index >= 3) {
						CanclTimer();
						return;
					}
					m_index++;
					GetDevicesMapInfo(false, false);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}

		};
		settimer.schedule(task, 3000);
	}

	public void showToast(String error) {
		Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
		/*
		 * GenieDebug.error("InitMapView",
		 * "onPrepareOptionsMenu 0 m_devicesblockenable = "
		 * +m_devicesblockenable);
		 * 
		 * if(m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE ||
		 * m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
		 * if(m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE ) {
		 * menu
		 * .findItem(R.id.item01).setTitle(getResources().getString(R.string.
		 * turnoffaccesscontrol)); }else if(m_devicesblockenable ==
		 * GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
		 * menu.findItem(R.id.item01).
		 * setTitle(getResources().getString(R.string.turnonaccesscontrol)); }
		 * 
		 * menu.findItem(R.id.item02).setVisible(false); }
		 * 
		 * return true;
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
		/*
		 * GenieDebug.error("InitMapView",
		 * "onCreateOptionsMenu 0 m_devicesblockenable = "
		 * +m_devicesblockenable);
		 * 
		 * 
		 * if(m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE ||
		 * m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
		 * MenuInflater inflater = getMenuInflater();
		 * inflater.inflate(R.menu.menu, menu);
		 * 
		 * if(m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE ) {
		 * menu
		 * .findItem(R.id.item01).setTitle(getResources().getString(R.string.
		 * turnoffaccesscontrol)); }else if(m_devicesblockenable ==
		 * GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
		 * menu.findItem(R.id.item01).
		 * setTitle(getResources().getString(R.string.turnonaccesscontrol)); }
		 * 
		 * menu.findItem(R.id.item02).setVisible(false); } // // MenuInflater
		 * inflater = getMenuInflater(); // inflater.inflate(R.menu.menu, menu);
		 * // menu.findItem(R.id.item01).setTitle("modify"); //
		 * menu.findItem(R.id.item02).setTitle("Refresh"); // // //
		 * if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_MAP) // { //
		 * menu.findItem(R.id.item01).setVisible(false); // //menu.clear(); // }
		 * // // return true;
		 */
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		CanclTimer();
		Bundle extra = new Bundle();

		Intent i = new Intent();
		int resultcode = 0;

		isback = true;

		switch (activeIndex) {

		case GenieGlobalDefines.ACTIVE_FUNCTION_MAP:
			if (activityresult == GenieGlobalDefines.EFunctionResult_Success) {
				int count = GenieSoap.routerMap.size();
				extra.putInt(GenieGlobalDefines.EFunctionMap_Result,
						(count == 0) ? 1 : count);
				resultcode = GenieGlobalDefines.EFunctionResult_Success;
			} else {
				resultcode = GenieGlobalDefines.EFunctionResult_failure;
			}
			break;

		}

		i.putExtras(extra);
		setResult(resultcode, i);

		extra = null;

		i = null;

		finish();
		// super.onBackPressed();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CanclTimer();
	}

	private void onRefresh() {
		CanclTimer();
		getModelInfo(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		GenieDebug.error("debug", "onOptionsItemSelected item.getItemId() = "
				+ item.getItemId());
		switch (item.getItemId()) {
		case R.id.item01:
			// onTurnOnOrOffDevicesBlock();
			return true;
		case R.id.item02:
			// onRefresh();
			return true;
		}
		return false;
	}

	private void SetBlockDeviceEnableAndRefresh() {

		m_devicesblockenable = -1;

		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE,
				GenieGlobalDefines.NULL);

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo request = new GenieRequestInfo();
		request.aRequestLable = "GenieMap";
		request.aSoapType = GenieGlobalDefines.ESoapRequestSetBlockDeviceEnable;
		request.aServer = "DeviceConfig";
		request.aMethod = "SetBlockDeviceEnable";
		request.aNeedwrap = true;
		request.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request.aNeedParser = false;
		request.aTimeout = 20000;
		request.aElement = new ArrayList<String>();
		request.aElement.add("NewBlockDeviceEnable");
		request.aElement.add("0");
		requestinfo.add(request);

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieMap";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus;
		request0.aServer = "DeviceConfig";
		request0.aMethod = "GetBlockDeviceEnableStatus";
		request0.aNeedwrap = false;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = true;
		request0.aTimeout = 20000;
		request0.aElement = new ArrayList<String>();
		requestinfo.add(request0);

		GenieRequestInfo request1 = new GenieRequestInfo();
		request1.aRequestLable = "GenieMap";
		request1.aSoapType = GenieGlobalDefines.ESoapRequestWLanInfo;
		request1.aServer = "WLANConfiguration";
		request1.aMethod = "GetInfo";
		request1.aNeedwrap = false;
		request1.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request1.aNeedParser = true;
		request1.aTimeout = 20000;
		request1.aElement = new ArrayList<String>();
		requestinfo.add(request1);

		GenieRequestInfo request2 = new GenieRequestInfo();
		request2.aRequestLable = "GenieMap";
		request2.aSoapType = GenieGlobalDefines.ESoapRequestRouterMap;
		request2.aServer = "DeviceInfo";
		request2.aMethod = "GetAttachDevice";
		request2.aNeedwrap = false;
		request2.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request2.aNeedParser = true;
		request2.aTimeout = 20000;
		request2.aElement = new ArrayList<String>();
		request2.aElement.add("NewAttachDevice");
		request2.aElement.add("null");
		requestinfo.add(request2);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.map) + " "
				+ getResources().getString(R.string.info);

		m_MapRequest = new GenieRequest(this, requestinfo);
		m_MapRequest.SetProgressInfo(true, true);
		m_MapRequest.SetProgressText(title, null);
		m_MapRequest.Start();
	}

	private void SetBlockDeviceDisableAndRefresh() {
		m_devicesblockenable = -1;

		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE,
				GenieGlobalDefines.NULL);

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo enableall = new GenieRequestInfo();
		enableall.aRequestLable = "GenieMap";
		enableall.aSoapType = GenieGlobalDefines.ESoapRequestEnableBlockDeviceForAll;
		enableall.aServer = "DeviceConfig";
		enableall.aMethod = "EnableBlockDeviceForAll";
		enableall.aNeedwrap = true;
		enableall.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		enableall.aNeedParser = false;
		enableall.aTimeout = 20000;
		enableall.aElement = new ArrayList<String>();
		requestinfo.add(enableall);

		GenieRequestInfo request = new GenieRequestInfo();
		request.aRequestLable = "GenieMap";
		request.aSoapType = GenieGlobalDefines.ESoapRequestSetBlockDeviceEnable;
		request.aServer = "DeviceConfig";
		request.aMethod = "SetBlockDeviceEnable";
		request.aNeedwrap = true;
		request.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request.aNeedParser = false;
		request.aTimeout = 20000;
		request.aElement = new ArrayList<String>();
		request.aElement.add("NewBlockDeviceEnable");
		request.aElement.add("1");
		requestinfo.add(request);

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieMap";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus;
		request0.aServer = "DeviceConfig";
		request0.aMethod = "GetBlockDeviceEnableStatus";
		request0.aNeedwrap = false;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = true;
		request0.aTimeout = 20000;
		request0.aElement = new ArrayList<String>();
		requestinfo.add(request0);

		GenieRequestInfo request1 = new GenieRequestInfo();
		request1.aRequestLable = "GenieMap";
		request1.aSoapType = GenieGlobalDefines.ESoapRequestWLanInfo;
		request1.aServer = "WLANConfiguration";
		request1.aMethod = "GetInfo";
		request1.aNeedwrap = false;
		request1.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request1.aNeedParser = true;
		request1.aTimeout = 20000;
		request1.aElement = new ArrayList<String>();
		requestinfo.add(request1);

		GenieRequestInfo request2 = new GenieRequestInfo();
		request2.aRequestLable = "GenieMap";
		request2.aSoapType = GenieGlobalDefines.ESoapRequestRouterMap;
		request2.aServer = "DeviceInfo";
		request2.aMethod = "GetAttachDevice";
		request2.aNeedwrap = false;
		request2.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request2.aNeedParser = true;
		request2.aTimeout = 20000;
		request2.aElement = new ArrayList<String>();
		request2.aElement.add("NewAttachDevice");
		request2.aElement.add("null");
		requestinfo.add(request2);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.map) + " "
				+ getResources().getString(R.string.info);

		m_MapRequest = new GenieRequest(this, requestinfo);
		m_MapRequest.SetProgressInfo(true, true);
		m_MapRequest.SetProgressText(title, null);
		m_MapRequest.Start();
	}

	public void onTurnOnOrOffDevicesBlock() {
		GenieDebug.error("debug", "onTurnOnOrOffDevicesBlock 0");
		if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE
				|| m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
			GenieDebug.error("debug", "onTurnOnOrOffDevicesBlock 1");

			if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE) {
				SetBlockDeviceEnableAndRefresh();
			} else if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
				SetBlockDeviceDisableAndRefresh();
			}

			// ShowProgressDlg();
			// GenieDebug.error("debug","onTurnOnOrOffDevicesBlock 2");
			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// GenieDebug.error("debug","run onTurnOnOrOffDevicesBlock 3 m_devicesblockenable ="+m_devicesblockenable);
			// if(m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE
			// )
			// {
			// soapCommand.SetBlockDeviceEnable("0");
			// GetDevicesMapInfo(false);
			// }else if(m_devicesblockenable ==
			// GenieGlobalDefines.BLOCKDEVICES_DISABLE)
			// {
			// soapCommand.EnableBlockDeviceForAll();
			// soapCommand.SetBlockDeviceEnable("1");
			// GetDevicesMapInfo(false);
			//
			//
			// }
			// }
			// }).start();
		}
	}

	public void onModifyMenu() {
		// Intent intent = new Intent();
		// switch(activeIndex)
		// {
		// case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
		//
		// intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
		// GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		// intent.setClass(GenieMap.this, GenieWifiModify.class);
		// startActivity(intent);
		// break;
		//
		// case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
		// intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
		// GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS);
		// intent.setClass(GenieMap.this, GenieWifiModify.class);
		// startActivity(intent);
		// break;
		// case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
		// intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
		// GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC);
		// intent.setClass(GenieMap.this, GenieTrafficSetting.class);
		// startActivity(intent);
		// break;
		//
		// }
	}

	public void InitDeviceTypeListStr() {
		int size = 0;
		if (null != m_devicetype) {
			m_devicetype = null;
		}

		size = Device_type.length;

		m_devicetype = new String[size];

		for (int i = 0; i < size; i++) {
			m_devicetype[i] = getResources().getString(Device_type[i]);

			GenieDebug.error("map", "onCreate m_devicetype[i] = "
					+ m_devicetype[i]);
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// GenieDebug.error("dispatchTouchEvent", "dispatchTouchEvent 1111111");
		boolean ret = super.dispatchTouchEvent(ev);

		// GenieDebug.error("dispatchTouchEvent", "dispatchTouchEvent 2222222");

		setPage();

		return ret;
	}

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

	public void PopupPage() {

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

	// public void PopupPage()
	// {
	// if(isback)
	// return ;
	//
	// DismissPage();
	//
	// if(MapPageIndex > 0)
	// {
	// pagelayout = new LinearLayout(this);
	// pagelayout.setOrientation(LinearLayout.HORIZONTAL);
	//
	// for(int j = 0; j < MapPageIndex; j++)
	// {
	// ImageView page = new ImageView(this);
	//
	// page.setId(j);
	// if(j == main_layout.getCurScreen())
	// {
	// page.setBackgroundResource(R.drawable.page_on);
	// }else
	// {
	// page.setBackgroundResource(R.drawable.page_off);
	// }
	// pagelayout.addView(page,new ViewGroup.LayoutParams(WC, WC));
	// }
	//
	// //popupwindowview =View.inflate(this,R.layout.popupwindowview, null);
	// popupwindow=new
	// PopupWindow(pagelayout,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,false);
	// //popupwindow.setHeight(100);
	// // popupwindow.setWidth(100);
	//
	// // popupwindow.setBackgroundDrawable(new BitmapDrawable());
	//
	// popupwindow.showAtLocation(main_layout, Gravity.BOTTOM, 0, 15);
	// }
	//
	// }

	private void PopupPageWindow() {
		if (MapPageIndex > 0) {
			Timer timer = new Timer();
			timer.schedule(new initPopupWindow(), 300);
		}
	}

	private class initPopupWindow extends TimerTask {
		@Override
		public void run() {

			sendMessage2UI(GenieGlobalDefines.PopupWindow);
		}
	}

	protected void onStart() {
		// TODO Auto-generated method stub

		super.onStart();

		RegisterBroadcastReceiver();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		unregisterReceiver(mBroadcastReceiver);

		StopRequest();
		UnRegisterBroadcastReceiver();

		DismissBlockDeviceSwitch();
		// DismissPage();
		if (null != main_layout) {
			main_layout.removeAllViews();
			main_layout = null;
		}
	}

	public void DismissBlockDeviceSwitch() {
		if (null != mPopupblockswitch) {
			mPopupblockswitch.dismiss();
			mPopupblockswitch = null;
		}
	}

	public void PopupBlockDeviceSwitch() {
		GenieDebug.error("debug", "PopupBlockDeviceSwitch 0");
		if (main_layout == null)
			return;

		if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE
				|| m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {

			GenieDebug.error("debug", "PopupBlockDeviceSwitch 1");

			DismissBlockDeviceSwitch();

			// String block_ebable =
			// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE);

			// View
			// view=LayoutInflater.from(GenieMap.this).inflate(R.layout.switchblockdevice,null);

			View view = getLayoutInflater().inflate(R.layout.switchblockdevice,
					null);

			// mPopupblockswitch=new PopupWindow(GenieMap.this);
			// mPopupblockswitch=new PopupWindow(view);
			// mPopupblockswitch.setContentView(view);

			mPopupblockswitch = new PopupWindow(view,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

			mPopupblockswitch.setFocusable(true);

			mPopupblockswitch.setTouchable(true);

			mPopupblockswitch.setOutsideTouchable(true);

			mPopupblockswitch.setBackgroundDrawable(new BitmapDrawable());

			mPopupblockswitch.showAtLocation(main_layout, Gravity.CENTER, 10,
					10);
		}

		GenieDebug.error("debug", "PopupBlockDeviceSwitch 2");

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();

		// 注意广播
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(SCANDEVICE);
		registerReceiver(mBroadcastReceiver, myIntentFilter);

		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);

		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			setTheme(R.style.bigactivityTitlebarNoSearch);
		} else {
			setTheme(R.style.activityTitlebarNoSearch); // qicheng.ai add
		}

		// setTheme(R.style.activityTitlebarNoSearch); //qichemg.ai add
		activeIndex = getIntent().getIntExtra(
				GenieGlobalDefines.LIST_TYPE_INDEX, 0);

		GenieDebug.error("map", "onCreate --0--");

		requestWindowFeature(Window.FEATURE_NO_TITLE
				| Window.FEATURE_CUSTOM_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// switch(activeIndex)
		// {
		// case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
		// case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
		// case GenieGlobalDefines.ACTIVE_FUNCTION_MAP:
		// setContentView(R.layout.list);
		// break;
		// case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
		// setContentView(R.layout.traffictable);
		// break;
		// }
		setContentView(R.layout.map);

		m_devicesblockenable = -1;

		isback = false;
		m_index = 0;

		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
		}
		GenieDebug.error("map", "onCreate --1--");

		m_MacMap = GenieSerializ.ReadMap(this,
				GenieGlobalDefines.USER_MAP_FILENAME);

		if (null == m_MacMap) {
			m_MacMap = new HashMap<String, String>();
		}

		// String url1 = "using.netgear.opendns.com";
		// String url2 = "routerlogin.net";
		//
		// InetAddress address = null;
		// try {
		// address = InetAddress.getByName(url2);
		// if(null != address)
		// {
		// String ip = address.getHostAddress();
		//
		// m_RouterIP = ip;
		//
		// GenieDebug.error("debug","1 host ip = "+ip);
		// }
		//
		//
		// } catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		//
		// e.printStackTrace();
		// }

		// if(null != m_MacMap)
		// {
		// GenieDebug.error("map", "onCreate null != m_MacMap ");
		//
		// Iterator<String> it=m_MacMap.keySet().iterator();
		//
		// while(it.hasNext()){
		// //System.out.println(it.next());
		// GenieDebug.error("map", "onCreate map = "+it.next());
		// }
		// }

		m_RouterIP = getGateWay();

		pagelayout = (LinearLayout) findViewById(R.id.popuppage);

		InitTitleView();

		InitDeviceTypeListStr();

		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);// 锟斤拷取Wifi锟斤拷锟斤拷

		GenieDebug.error("onCreate", "onCreate wifiManager");

		if (null != wifiManager) {

			wifiInfo = wifiManager.getConnectionInfo();

			GenieDebug.error("onCreate", "onCreate wifiInfo");

			if (null != wifiInfo) {
				GenieMac = wifiInfo.getMacAddress();
				if (null != GenieMac)
					GenieMac = GenieMac.toUpperCase();
				GenieDebug.error("onCreate", "onCreate mac = " + GenieMac);
			}

		}

		activityresult = GenieGlobalDefines.EFunctionResult_failure;

		// soapCommand = new GenieSoap(this);
		// soapCommand.pmap = this;
		// soapCommand.parentview = GenieGlobalDefines.GenieView_Map;
		//
		GenieDebug.error("map", "onCreate --2--");

		// testview();
		getModelInfo(true);
		GenieDebug.error("map", "onCreate --3--");
	}

	public void testview() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				main_layout = (ScrollLayout) findViewById(R.id.ScrollLayout_map);
				ArrayList<DeviceInfo> devicelist = new ArrayList<DeviceInfo>();
				mapview temp2 = new mapview(GenieMap.this, devicelist,
						m_wlan_ebable,
						GenieGlobalDefines.BLOCKDEVICES_NOSUPPORT);
				temp2.setClickable(true);
				main_layout.addView(temp2);

			}
		});
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

		String text = getResources().getString(R.string.version) + " "
				+ versionName + "." + versionCode + "\n"
				+ getResources().getString(R.string.about_info);

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.about, null);

		TextView aboutinfo = (TextView) view.findViewById(R.id.about_tv);
		aboutinfo.setTextSize(22);
		aboutinfo.setText(text);

		TextView model = (TextView) view.findViewById(R.id.about_model);
		model.setTextSize(22);
		model.setText("Router Model:"
				+ GenieSoap.dictionary
						.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME));

		TextView version = (TextView) view.findViewById(R.id.about_version);
		version.setTextSize(22);
		String Version_text = "Firmware Version:"
				+ GenieSoap.dictionary
						.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION);

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

		Button close = (Button) view.findViewById(R.id.about_cancel);
		close.setText(R.string.close);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != aboutdialog) {
					aboutdialog.dismiss();
					aboutdialog = null;
				}
			}

		});

		AlertDialog.Builder dialog_login = new AlertDialog.Builder(this)
				.setView(view).setTitle(R.string.about)
				.setIcon(R.drawable.icon);

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

	public void InitTitleView() {
		Button back = (Button) findViewById(R.id.back);
		Button about = (Button) findViewById(R.id.about);

		TextView title = (TextView) findViewById(R.id.netgeartitle);
		title.setText(R.string.map);

		back.setBackgroundResource(R.drawable.title_bt_bj);
		about.setBackgroundResource(R.drawable.title_bt_bj);
		about.setText(R.string.refresh);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GetActivity().onBackPressed();
			}
		});

		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ScanDeviceService.scanDevice != null) {
					ScanDeviceService.scanDevice.findservice();
				}
				onRefresh();
				// OnClickAbout();
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

		// TextView text = (TextView)findViewById(R.id.model);
		// text.setText("Router:" +
		// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME));
		//
		// text = (TextView)findViewById(R.id.version);
		// text.setText("Firmware:" +
		// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION));
		//
	}

	public void changeScreenOrientation(Configuration newConfig) {
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();

		GenieDebug.error("changeScreenOrientation",
				"changeScreenOrientation !!!!!!!!!");
	}

	public void sendMessage2UI(int msg) {
		handler.sendEmptyMessage(msg);
	}

	// public void GetDevicesMapInfo()
	// {
	// m_devicesblockenable = -1;
	// soapCommand.GetBlockDeviceEnableStatus();
	// soapCommand.getWLanInfo();
	// soapCommand.getRouterMap();
	// }

	private GenieRequest m_MapRequest = null;

	private void StopRequest() {
		if (null != m_MapRequest) {
			m_MapRequest.Stop();
			m_MapRequest = null;
		}
	}

	public void GetDevicesMapInfo(boolean show, boolean first) {
		m_devicesblockenable = -1;

		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE,
				GenieGlobalDefines.NULL);
		GenieSoap.routerMap.clear();

		// soapCommand.GetBlockDeviceEnableStatus();
		// soapCommand.getWLanInfo();
		// soapCommand.getRouterMap();

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieMap";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus;
		request0.aServer = "DeviceConfig";
		request0.aMethod = "GetBlockDeviceEnableStatus";
		request0.aNeedwrap = false;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = true;
		request0.aTimeout = 20000;
		request0.aElement = new ArrayList<String>();
		requestinfo.add(request0);

		GenieRequestInfo request1 = new GenieRequestInfo();
		request1.aRequestLable = "GenieMap";
		request1.aSoapType = GenieGlobalDefines.ESoapRequestWLanInfo;
		request1.aServer = "WLANConfiguration";
		request1.aMethod = "GetInfo";
		request1.aNeedwrap = false;
		request1.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request1.aNeedParser = true;
		request1.aTimeout = 20000;
		request1.aElement = new ArrayList<String>();
		requestinfo.add(request1);

		GenieRequestInfo request2 = new GenieRequestInfo();
		request2.aRequestLable = "GenieMap";
		request2.aSoapType = GenieGlobalDefines.ESoapRequestRouterMap;
		request2.aServer = "DeviceInfo";
		request2.aMethod = "GetAttachDevice";
		request2.aNeedwrap = false;
		request2.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request2.aNeedParser = true;
		request2.aTimeout = 20000;
		request2.aElement = new ArrayList<String>();
		request2.aElement.add("NewAttachDevice");
		request2.aElement.add("null");
		requestinfo.add(request2);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.map) + " "
				+ getResources().getString(R.string.info);

		m_MapRequest = new GenieRequest(this, requestinfo);
		m_MapRequest.SetProgressInfo(show, true);
		m_MapRequest.SetProgressText(title, null);

		if (first) {
			m_MapRequest
					.SetProgressCancelListener(new OnProgressCancelListener() {

						@Override
						public void OnProgressCancel() {
							// TODO Auto-generated method stub
							GenieMap.this.finish();
						}
					});
		}

		m_MapRequest.Start();
	}

	public void CancleProgressDlg() {
		if (null != progressDialog) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void ShowProgressDlg() {
		CancleProgressDlg();

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.map) + " "
				+ getResources().getString(R.string.info);

		progressDialog = ProgressDialog.show(this, title, getResources()
				.getString(R.string.wait), true, true);

	}

	// public void getModelInfo()
	// {
	// GenieDebug.error("debug", "getModelInfo --0--");
	//
	// ShowProgressDlg();
	//
	// GenieDebug.error("debug", "getModelInfo --1--");
	//
	// new Thread()
	// {
	// public void run()
	// {
	// GenieDebug.error("debug", "getModelInfo --run--");
	//
	// switch(activeIndex)
	// {
	// case GenieGlobalDefines.ACTIVE_FUNCTION_MAP:
	// GetDevicesMapInfo();
	// break;
	// }
	// }
	// }.start();
	// }

	public void getModelInfo(boolean first) {
		GenieDebug.error("debug", "getModelInfo --0--");

		GetDevicesMapInfo(true, first);

		// new Thread()
		// {
		// public void run()
		// {
		// GenieDebug.error("debug", "getModelInfo --run--");
		//
		// switch(activeIndex)
		// {
		// case GenieGlobalDefines.ACTIVE_FUNCTION_MAP:
		// GetDevicesMapInfo();
		// break;
		// }
		// }
		// }.start();
	}

	private void SetBlockDeviceByMACAndRefresh(String mac, String Status) {
		m_devicesblockenable = -1;

		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE,
				GenieGlobalDefines.NULL);

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo enableall = new GenieRequestInfo();
		enableall.aRequestLable = "GenieMap";
		enableall.aSoapType = GenieGlobalDefines.ESoapRequestSetBlockDeviceByMac;
		enableall.aServer = "DeviceConfig";
		enableall.aMethod = "SetBlockDeviceByMAC";
		enableall.aNeedwrap = true;
		enableall.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		enableall.aNeedParser = false;
		enableall.aTimeout = 20000;
		enableall.aElement = new ArrayList<String>();
		enableall.aElement.add("NewMACAddress");
		enableall.aElement.add(mac);
		enableall.aElement.add("NewAllowOrBlock");
		enableall.aElement.add(Status);
		requestinfo.add(enableall);

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieMap";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestBlockDeviceEnableStatus;
		request0.aServer = "DeviceConfig";
		request0.aMethod = "GetBlockDeviceEnableStatus";
		request0.aNeedwrap = false;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = true;
		request0.aTimeout = 20000;
		request0.aElement = new ArrayList<String>();
		requestinfo.add(request0);

		GenieRequestInfo request1 = new GenieRequestInfo();
		request1.aRequestLable = "GenieMap";
		request1.aSoapType = GenieGlobalDefines.ESoapRequestWLanInfo;
		request1.aServer = "WLANConfiguration";
		request1.aMethod = "GetInfo";
		request1.aNeedwrap = false;
		request1.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request1.aNeedParser = true;
		request1.aTimeout = 20000;
		request1.aElement = new ArrayList<String>();
		requestinfo.add(request1);

		GenieRequestInfo request2 = new GenieRequestInfo();
		request2.aRequestLable = "GenieMap";
		request2.aSoapType = GenieGlobalDefines.ESoapRequestRouterMap;
		request2.aServer = "DeviceInfo";
		request2.aMethod = "GetAttachDevice";
		request2.aNeedwrap = false;
		request2.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request2.aNeedParser = true;
		request2.aTimeout = 20000;
		request2.aElement = new ArrayList<String>();
		request2.aElement.add("NewAttachDevice");
		request2.aElement.add("null");
		requestinfo.add(request2);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.map) + " "
				+ getResources().getString(R.string.info);

		m_MapRequest = new GenieRequest(this, requestinfo);
		m_MapRequest.SetProgressInfo(true, true);
		m_MapRequest.SetProgressText(title, null);
		m_MapRequest.Start();

		m_MapRequest.SetFinishAction(new RequestFinish() {

			@Override
			public void OnFinish(GenieRequest request) {
				// TODO Auto-generated method stub
				CancleDeviceDialog();
			}
		});

	}

	public void ChangeDeviceBlockStatus() {

		// ShowProgressDlg();

		if (dialog_device != null && dialog_device.blockstatus != null) {
			if (dialog_device.blockstatus.equals("Allow")) {
				SetBlockDeviceByMACAndRefresh(dialog_device.mac, "Block");
			} else if (dialog_device.blockstatus.equals("Block")) {
				SetBlockDeviceByMACAndRefresh(dialog_device.mac, "Allow");
			}
			// handler.post(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// CancleDeviceDialog();
			// }
			// });
		}

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// if(dialog_device!= null&& dialog_device.blockstatus != null)
		// {
		// if(dialog_device.blockstatus.equals("Allow"))
		// {
		// soapCommand.SetBlockDeviceByMAC(dialog_device.mac,"Block");
		// GetDevicesMapInfo(false);
		// }else if(dialog_device.blockstatus.equals("Block"))
		// {
		// soapCommand.SetBlockDeviceByMAC(dialog_device.mac,"Allow");
		// GetDevicesMapInfo(false);
		// }
		// handler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// CancleDeviceDialog();
		// }
		// });
		// }
		// }
		// }).start();

		GenieDebug.error("debug", "ChangDeviceBlockStatus ");
	}

	private void CancleDeviceDialog() {
		if (m_dialog != null && m_dialog.isShowing()) {
			m_dialog.dismiss();
			m_dialog = null;
		}
	}

	private void ShowDeviceDialog(int index, int type, boolean gateway) {
		// LayoutInflater inflater = getLayoutInflater();
		// View login = inflater.inflate(R.layout.login,(ViewGroup)
		// findViewById(R.layout.login));

		GenieDebug.error("onTouchEvent", "onTouchEvent  index = " + index);
		GenieDebug.error("onTouchEvent", "onTouchEvent type = " + type);
		GenieDebug.error("onTouchEvent", "onTouchEvent m_devicelist.size() = "
				+ m_devicelist.size());

		// int maxpageofdevice = m_devicelist.size()>(mapview.MaxDeviceSize -
		// 3)?(mapview.MaxDeviceSize - 3):m_devicelist.size();
		// GenieDebug.error("onTouchEvent","onTouchEvent maxpageofdevice = "+maxpageofdevice);

		if (GenieGlobalDefines.Device_Internet == type) {
			return;
		}

		if (GenieRequest.m_SmartNetWork && gateway) {
			return;
		}

		CancleDeviceDialog();

		if (null != devicedetaildolg) {
			devicedetaildolg = null;
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		devicedetaildolg = inflater.inflate(R.layout.devicedetaildlg, null);

		// Dialog test = new Dialog(this);
		// test.

		if (null != device_dialog) {
			device_dialog.create().dismiss();
			device_dialog = null;
		}

		device_dialog = new AlertDialog.Builder(this);
		device_dialog.setView(devicedetaildolg);

		m_modifyflag = false;

		// DeviceInfo device = null;

		if (null != dialog_device) {
			dialog_device = null;
		}

		GenieDebug.error("onTouchEvent", "onTouchEvent type = " + type);

		if (gateway) {
			dialog_device = device_route;
		} else {
			if (GenieGlobalDefines.Device_Genie == type) {

				dialog_device = device_genie;
			} else {
				GenieDebug.error("onTouchEvent",
						"onTouchEvent  main_layout.getCurScreen() = "
								+ main_layout.getCurScreen());
				GenieDebug.error("onTouchEvent",
						"onTouchEvent maxpagedevice = " + maxpagedevice);
				GenieDebug.error("onTouchEvent", "onTouchEvent index = "
						+ index);

				dialog_device = m_devicelist.get(main_layout.getCurScreen()
						* maxpagedevice + index);
			}
		}

		GenieDebug.error("onTouchEvent", "onTouchEvent dialog_device.Ip = "
				+ dialog_device.Ip);

		int textsize = 19;
		// dialog_login.setCancelable(false);
		MarqueeTextView devicename = (MarqueeTextView) devicedetaildolg
				.findViewById(R.id.devicename);

		devicename.setTransformationMethod(SingleLineTransformationMethod
				.getInstance());
		devicename.setSingleLine(true);
		devicename.setEllipsize(TruncateAt.MARQUEE);
		devicename.setMarqueeRepeatLimit(-1);
		devicename.setFocusable(true);

		devicename.setTextSize(textsize);
		devicename.setText(dialog_device.name);

		EditText devicenameedit = (EditText) devicedetaildolg
				.findViewById(R.id.devicenameedit);

		devicenameedit.setTextSize(textsize);
		devicenameedit.setText(dialog_device.name);

		if (gateway) // (GenieGlobalDefines.Device_Route == type || ((type >=
						// GenieGlobalDefines.Device_Route_CG3300) && (type <
						// GenieGlobalDefines.Device_Route_MAX)))
		{
			TextView customnameedtv = (TextView) devicedetaildolg
					.findViewById(R.id.customname);
			customnameedtv.setText(getResources().getString(
					R.string.routermodel));
		}

		if (devicenameedit.getText().toString().length() > 0) {
			m_DviceNameEdit = true;
		} else {
			m_DviceNameEdit = false;
		}
		devicenameedit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				EditText temp = (EditText) devicedetaildolg
						.findViewById(R.id.devicenameedit);
				if (temp.getText().toString().length() > 0) {
					m_DviceNameEdit = true;
				} else {
					m_DviceNameEdit = false;
				}

				Button modefiy = (Button) devicedetaildolg
						.findViewById(R.id.Modifybtn);

				if (m_modifyflag) {
					if (m_DviceNameEdit) {

						modefiy.setEnabled(true);
					} else {
						modefiy.setEnabled(false);
					}
				} else {
					modefiy.setEnabled(true);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		devicename.setVisibility(View.VISIBLE);
		devicenameedit.setVisibility(View.GONE);

		TextView devicetype = (TextView) devicedetaildolg
				.findViewById(R.id.devicetype);
		// devicetype.setText(m_devicetype[device.type - ]);634131

		Spinner devicenameSpinner = (Spinner) devicedetaildolg
				.findViewById(R.id.devicetypespinner);
		// devicename.setText(device.name);
		if (gateway || (GenieGlobalDefines.Device_Genie == type))// ||
																	// GenieGlobalDefines.Device_Route
																	// == type
																	// || ((type
																	// >=
																	// GenieGlobalDefines.Device_Route_CG3300)
																	// && (type
																	// <
																	// GenieGlobalDefines.Device_Route_MAX)))
		{
			TextView devicetypetv = (TextView) devicedetaildolg
					.findViewById(R.id.devicetypetv);

			LinearLayout devicename_tbr = (LinearLayout) devicedetaildolg
					.findViewById(R.id.Linear_type);
			devicename_tbr.setVisibility(View.GONE);

			devicetypetv.setVisibility(View.GONE);
			devicetype.setVisibility(View.GONE);
			devicenameSpinner.setVisibility(View.GONE);
		} else {
			devicetype.setTextSize(textsize);

			GenieDebug.error("ShowDeviceDialog", "dialog_device.type = "
					+ dialog_device.type);
			devicetype.setText(m_devicetype[dialog_device.type
					- GenieGlobalDefines.Device_imacdev]);

			devicetype.setVisibility(View.VISIBLE);
			devicenameSpinner.setVisibility(View.GONE);

		}

		TextView deviceip = (TextView) devicedetaildolg
				.findViewById(R.id.deviceip);

		deviceip.setTextSize(textsize);
		if (dialog_device.Ip != null)
			deviceip.setText(dialog_device.Ip);

		TextView devicemac = (TextView) devicedetaildolg
				.findViewById(R.id.devicemac);

		devicemac.setTextSize(textsize);
		if (dialog_device.mac != null)
			devicemac.setText(dialog_device.mac);

		if (dialog_device.ConnectionType != null
				&& dialog_device.ConnectionType.equals("wireless")
				&& dialog_device.intensity != null) {
			TextView devicesig = (TextView) devicedetaildolg
					.findViewById(R.id.signal);
			devicesig.setText(dialog_device.intensity + "%");
			devicesig.setTextSize(textsize);
			LinearLayout devicesignal = (LinearLayout) devicedetaildolg
					.findViewById(R.id.devicesignal);
			devicesignal.setVisibility(View.VISIBLE);
		} else {
			LinearLayout devicesignal = (LinearLayout) devicedetaildolg
					.findViewById(R.id.devicesignal);
			devicesignal.setVisibility(View.GONE);
		}

		if (dialog_device.ConnectionType != null
				&& dialog_device.ConnectionType.equals("wireless")
				&& dialog_device.speed != null) {
			TextView deviceint = (TextView) devicedetaildolg
					.findViewById(R.id.linkrate);
			deviceint.setText(dialog_device.speed + "Mbps");
			deviceint.setTextSize(textsize);

			TextView whatisit = (TextView) devicedetaildolg
					.findViewById(R.id.whatisit);
			// whatisit.setTextColor(Color.BLUE);
			String html = "<a href=\"http://support.netgear.com/search/link%20rate\">"
					+ getResources().getString(R.string.whatisit) + "</a>";

			whatisit.setText(Html.fromHtml(html));
			whatisit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Uri uri = Uri
							.parse("http://support.netgear.com/app/answers/list/kw/link%20rate");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
			});

			LinearLayout devicelinkrate = (LinearLayout) devicedetaildolg
					.findViewById(R.id.devicelink);
			devicelinkrate.setVisibility(View.VISIBLE);
		} else {
			LinearLayout devicelinkrate = (LinearLayout) devicedetaildolg
					.findViewById(R.id.devicelink);
			devicelinkrate.setVisibility(View.GONE);
		}

		// if(GenieGlobalDefines.Device_Genie != type ||
		// GenieGlobalDefines.Device_Route != type || ((type <=
		// GenieGlobalDefines.Device_Route_CG3300) && (type >=
		// GenieGlobalDefines.Device_Route_WNDR4500)))
		// {
		//
		// device_dialog.setPositiveButton(R.string.modify, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		//
		// /* User clicked Yes so do some stuff */
		// OnModifyBtn();
		// }
		// });
		//
		// }
		//
		// device_dialog.setNegativeButton(R.string.close, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		//
		// /* User clicked No so do some stuff */
		// OnCloseBtn();
		// }
		// });

		Button modefiy = (Button) devicedetaildolg.findViewById(R.id.Modifybtn);
		modefiy.setText(R.string.modify);
		modefiy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GenieDebug.error("onClick", "onClick close");
				// m_dialog.dismiss();

				OnModifyBtn();
			}
		});

		if (m_modifyflag) {
			if (m_DviceNameEdit) {

				modefiy.setEnabled(true);
			} else {
				modefiy.setEnabled(false);
			}
		} else {
			modefiy.setEnabled(true);
		}

		Button close = (Button) devicedetaildolg.findViewById(R.id.closebtn);

		Button block = (Button) devicedetaildolg.findViewById(R.id.blockbtn);

		if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE) {
			if (dialog_device.blockstatus != null
					&& dialog_device.blockstatus.equals("Block")) {
				block.setText(getResources().getString(R.string.device_allow));
			} else if (dialog_device.blockstatus != null
					&& dialog_device.blockstatus.equals("Allow")) {
				block.setText(getResources().getString(R.string.device_block));
			} else {
				block.setVisibility(View.GONE);
			}
		} else {
			block.setVisibility(View.GONE);
		}

		block.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChangeDeviceBlockStatus();
			}
		});

		// if(gateway || (GenieGlobalDefines.Device_Genie == type)) //
		// ||GenieGlobalDefines.Device_Route == type || ((type >=
		// GenieGlobalDefines.Device_Route_CG3300) && (type <
		// GenieGlobalDefines.Device_Route_MAX)))
		// {
		// modefiy.setVisibility(View.GONE);
		// //modefiy.setEnabled(false);
		// }

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GenieDebug.error("onClick", "onClick close");
				OnCloseBtn();
			}
		});

		// 文件上传
		Button fileupload = (Button) devicedetaildolg
				.findViewById(R.id.fileupload);
		fileupload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GenieDebug.error("onClick", "onClick close");
				if (m_dialog != null && m_dialog.isShowing()) {
					m_dialog.dismiss();
				}
				Intent intent = new Intent();
				intent.setClass(GenieMap.this, FileUploadActivity.class);
				intent.putExtra("ComputerInfo_ip", dialog_device.Ip);
				intent.putExtra("ComputerInfo_name", Own_device);
				// startActivity(intent);
				startActivity(intent);

				// LayoutInflater layoutInflater =
				// LayoutInflater.from(GenieMap.this);
				// final View loginAdd =
				// layoutInflater.inflate(R.layout.loginpc, null);
				// new
				// AlertDialog.Builder(GenieMap.this).setTitle("输入配置").setView(loginAdd).setPositiveButton("确定",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int which) {
				// EditText name_text=(EditText)
				// loginAdd.findViewById(R.id.login_name);
				// EditText ip_text=(EditText)
				// loginAdd.findViewById(R.id.login_ip);
				// EditText username_text=(EditText)
				// loginAdd.findViewById(R.id.login_username);
				// EditText password_text=(EditText)
				// loginAdd.findViewById(R.id.login_password);
				// dialog.dismiss();
				// }
				// }).setNegativeButton("取消",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int which) {
				// dialog.cancel();
				// }
				// }).show();

			}
		});

		if (ScanDeviceService.discovered_services.contains(dialog_device.Ip)) {
			fileupload.setVisibility(View.VISIBLE);
		} else {
			fileupload.setVisibility(View.GONE);
		}

		if (gateway || (GenieGlobalDefines.Device_Genie == type)) // ||GenieGlobalDefines.Device_Route
																	// == type
																	// || ((type
																	// >=
																	// GenieGlobalDefines.Device_Route_CG3300)
																	// && (type
																	// <
																	// GenieGlobalDefines.Device_Route_MAX)))
		{
			modefiy.setVisibility(View.GONE);
			fileupload.setVisibility(View.GONE);
			// modefiy.setEnabled(false);
		}

		device_dialog.setIcon(mapview.image[type - mapview.imagebase]);

		GenieDebug.error("onClick", " dialog_device.name.length() = "
				+ dialog_device.name.length());

		// if(dialog_device.name.length() < 21)
		// {
		// String title = dialog_device.name;
		// for(int i = dialog_device.name.length(); i< 21; i++)
		// {
		// GenieDebug.error("onClick"," title.length = "+title.length());
		// GenieDebug.error("onClick"," i = "+i);
		// title += " ";
		// }
		// //GenieDebug.error("onClick"," title = "+title+"t");
		// device_dialog.setTitle(title);
		// }else
		// {
		device_dialog.setTitle(dialog_device.name);
		// }

		// device_dialog.setNegativeButton("1",null);
		// device_dialog.setNeutralButton("2",null);
		// device_dialog.setPositiveButton("3",null);

		m_dialog = device_dialog.create();

		m_dialog.show();

		m_dialog.getButton(AlertDialog.BUTTON_POSITIVE)
				.setVisibility(View.GONE);
		m_dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setVisibility(View.GONE);
		m_dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
				.setVisibility(View.GONE);

	}

	private void OnCloseBtn() {
		if (m_dialog != null && m_dialog.isShowing()) {
			m_dialog.dismiss();
		}
		m_dialog = null;

		// if(m_modifyflag)
		// {
		InitMapView();
		// }
	}

	private void OnModifyBtn() {
		if (!m_modifyflag) {
			// modify
			TextView devicename = (TextView) devicedetaildolg
					.findViewById(R.id.devicename);
			EditText devicenameedit = (EditText) devicedetaildolg
					.findViewById(R.id.devicenameedit);

			devicename.setVisibility(View.GONE);
			devicenameedit.setVisibility(View.VISIBLE);

			TextView devicetype = (TextView) devicedetaildolg
					.findViewById(R.id.devicetype);

			Spinner devicenameSpinner = (Spinner) devicedetaildolg
					.findViewById(R.id.devicetypespinner);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, m_devicetype);

			// m_devicetype[dialog_device.type -
			// GenieGlobalDefines.Device_Route]

			// 锟斤拷锟斤拷锟斤拷锟斤拷锟叫憋拷锟斤拷
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// 锟斤拷Adapter锟斤拷拥锟絘dapter_channel
			devicenameSpinner.setAdapter(adapter);

			devicenameSpinner.setSelection(
					(dialog_device.type - GenieGlobalDefines.Device_imacdev),
					true);

			devicenameSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							GenieDebug.error("onItemSelected",
									"onItemSelected 0 arg2 = " + arg2);

							SelecteType = GenieGlobalDefines.Device_Internet
									+ arg2 + 2;
							m_dialog.setIcon(mapview.image[arg2 + 2]);
							// m_dialog.show();

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}
					});

			devicetype.setVisibility(View.GONE);
			devicenameSpinner.setVisibility(View.VISIBLE);

			Button modefiy = (Button) devicedetaildolg
					.findViewById(R.id.Modifybtn);
			modefiy.setText(R.string.apply);

			m_modifyflag = true;
			SelecteType = 0;
		} else {
			// apply

			GenieDebug.error("OnModifyBtn", "apply ");

			String Str = null;

			EditText devicenameedit = (EditText) devicedetaildolg
					.findViewById(R.id.devicenameedit);

			Str = devicenameedit.getText().toString();

			dialog_device.name = Str;
			if (0 == SelecteType) {
				SelecteType = dialog_device.type;
			} else {
				dialog_device.type = SelecteType;
			}

			// Str = Str+"#"+SelecteType+"#";
			Str = Str + "\n" + SelecteType + "\n";

			GenieDebug.error("OnModifyBtn", "Str = " + Str);
			m_MacMap.put(dialog_device.mac, Str);

			GenieSerializ.WriteMap(this, m_MacMap,
					GenieGlobalDefines.USER_MAP_FILENAME);
			m_modifyflag = false;
			SelecteType = 0;
			// //////////////////////////////////////////

			TextView devicename = (TextView) devicedetaildolg
					.findViewById(R.id.devicename);

			devicename.setText(dialog_device.name);

			devicenameedit.setText(dialog_device.name);

			devicename.setVisibility(View.VISIBLE);
			devicenameedit.setVisibility(View.GONE);

			TextView devicetype = (TextView) devicedetaildolg
					.findViewById(R.id.devicetype);
			// devicetype.setText(m_devicetype[device.type - ]);634131

			Spinner devicenameSpinner = (Spinner) devicedetaildolg
					.findViewById(R.id.devicetypespinner);

			devicetype.setText(m_devicetype[dialog_device.type
					- GenieGlobalDefines.Device_imacdev]);

			devicetype.setVisibility(View.VISIBLE);
			devicenameSpinner.setVisibility(View.GONE);

			Button modefiy = (Button) devicedetaildolg
					.findViewById(R.id.Modifybtn);
			modefiy.setText(R.string.modify);
			// //////////////////////////////////////////////
		}
	}

	private void InitFailureView() {

		String wlan_ebable = GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS);
		if (GenieRequest.m_SmartNetWork || wlan_ebable.equals("Up")) {
			m_wlan_ebable = 1;
		} else {
			m_wlan_ebable = 0;
		}

		GenieDebug.error("InitMapView", "InitMapView 0 wlan_ebable = "
				+ wlan_ebable);

		if (null != main_layout) {
			main_layout.removeAllViews();
			main_layout = null;
		}
		main_layout = (ScrollLayout) findViewById(R.id.ScrollLayout_map);

		int count = GenieSoap.routerMap.size();

		m_devicelist = new ArrayList<DeviceInfo>();

		device_genie = null;
		device_internet = null;
		device_route = null;
		if (null == device_genie) {

			device_genie = new DeviceInfo();
			device_genie.type = GenieGlobalDefines.Device_Genie;
			device_genie.name = "ANDROID";// wifiInfo;

			GenieDebug.error("InitMapView", " wifiInfo.getIpAddress() = "
					+ wifiInfo.getIpAddress());
			// device_genie.Ip = String.valueOf(wifiInfo.getIpAddress());
			device_genie.Ip = ipIntToString(wifiInfo.getIpAddress()); // GenieSoap.m_GateWayIp;//
			device_genie.mac = GenieMac;
			device_genie.ConnectionType = "wireless";
			device_genie.isturbo = false;

		}

		if (null == device_internet) {

			device_internet = new DeviceInfo();
			device_internet.type = GenieGlobalDefines.Device_Internet;
			device_internet.name = "internet";
			device_internet.Ip = "192.168.1.1";
			device_internet.mac = "";
			device_internet.ConnectionType = "internet";
			device_internet.isturbo = false;

		}

		if (null == device_route) {

			device_route = new DeviceInfo();
			device_route.type = GetRouterTypeID();
			if (-1 == device_route.type) {
				device_route.type = GenieGlobalDefines.Device_Route;
			}
			device_route.name = GenieSoap.dictionary
					.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
			if (null != m_RouterIP) {
				device_route.Ip = m_RouterIP;
			} else {
				device_route.Ip = "192.168.1.1";
			}
			device_route.mac = GenieSoap.dictionary
					.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
			device_route.ConnectionType = "route";

		}

		ArrayList<DeviceInfo> devicelist = new ArrayList<DeviceInfo>();

		if (!GenieRequest.m_SmartNetWork) {
			m_devicelist.add(device_genie);
			devicelist.add(device_genie);
		}

		m_devicelist.add(device_internet);

		devicelist.add(device_internet);

		m_devicelist.add(device_route);

		devicelist.add(device_route);

		mapview view = new mapview(this, devicelist, m_wlan_ebable,
				GenieGlobalDefines.BLOCKDEVICES_NOSUPPORT);
		view.setClickable(true);

		OnMapClickListener listener1 = new OnMapClickListener() {

			@Override
			public void onClick(mapview view, int Index, int Value,
					boolean gateway) {
				// TODO Auto-generated method stub
				ShowDeviceDialog(Index, Value, gateway);
			}
		};

		view.addClickListener(listener1);
		view.setTag("mainscroll");

		main_layout.addView(view);

	}

	private int GetRouterTypeID() {
		int ret = -1;
		String routertype = GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);

		if (routertype == null)
			return ret;

		// routertype = "R6200";

		routertype = routertype.toUpperCase();

		GenieDebug.error("debug", "GetRouterTypeID routertype = " + routertype);

		for (int i = 0; i < m_Routertype.length; i++) {
			// if(routertype.indexOf(m_Routertype[i]) != -1)
			if (routertype.startsWith(m_Routertype[i])) {
				return m_RouterTypeId[i];
			}
		}

		return ret;
	}

	private void InitMapView() {

		String wlan_ebable = GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS);
		if (GenieRequest.m_SmartNetWork
				|| (wlan_ebable != null && wlan_ebable.equals("Up"))) {
			m_wlan_ebable = 1;
		} else {
			m_wlan_ebable = 0;
		}
		GenieDebug.error("InitMapView", "InitMapView 0 wlan_ebable = "
				+ wlan_ebable);

		String block_ebable = GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_BLOCKDEVICE_EABLE);

		if (block_ebable != null
				&& !block_ebable.equals(GenieGlobalDefines.NULL)) {

			if (block_ebable.equals("1")) {
				m_devicesblockenable = GenieGlobalDefines.BLOCKDEVICES_ENABLE;

			} else if (block_ebable.equals("0")) {
				m_devicesblockenable = GenieGlobalDefines.BLOCKDEVICES_DISABLE;

			} else {
				m_devicesblockenable = GenieGlobalDefines.BLOCKDEVICES_NOSUPPORT;
			}
		} else {
			m_devicesblockenable = GenieGlobalDefines.BLOCKDEVICES_NOSUPPORT;
		}
		GenieDebug.error("InitMapView", "InitMapView 0 block_ebable = "
				+ block_ebable);
		GenieDebug.error("InitMapView", "InitMapView 0 m_devicesblockenable = "
				+ m_devicesblockenable);

		if (null != main_layout) {
			main_layout.removeAllViews();
			main_layout = null;
		}
		main_layout = (ScrollLayout) findViewById(R.id.ScrollLayout_map);

		int count = GenieSoap.routerMap.size();

		// for(int test = 0; test < count;test++)
		// {
		// GenieDebug.error("InitMapView"," 7777 mac  = "+GenieSoap.routerMap.get(test).get(GenieGlobalDefines.DEVICE_MAC));
		// }

		GenieDebug.error("InitMapView", "InitMapView 0 count = " + count);
		int testi = 0;

		device_genie = null;
		device_internet = null;
		device_route = null;

		m_devicelist = new ArrayList<DeviceInfo>();
		for (int j = 0; j < count; j++) {
			if (!GenieRequest.m_SmartNetWork
					&& null != GenieMac
					&& GenieSoap.routerMap.get(j)
							.get(GenieGlobalDefines.DEVICE_MAC)
							.equals(GenieMac)) {
				device_genie = new DeviceInfo();

				device_genie.type = GenieGlobalDefines.Device_Genie;
				device_genie.name = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_NAME);
				device_genie.Ip = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_IP);
				device_genie.mac = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_MAC);
				device_genie.ConnectionType = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_CONNECTTYPE);
				device_genie.speed = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_SPEED);
				device_genie.intensity = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_INTENSITY);
				Own_device = device_genie.name;
				device_genie.isturbo = false;
				if ("--".equals(Own_device) || "<unknown>".equals(Own_device)) {
					Own_device = "IP " + device_genie.Ip;
				}
				GenieDebug.error("InitMapView", "ConnectionType = "
						+ device_genie.ConnectionType);

			} else {
				DeviceInfo device = new DeviceInfo();

				device.Ip = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_IP);
				device.mac = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_MAC);
				device.ConnectionType = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_CONNECTTYPE);
				device.speed = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_SPEED);
				device.intensity = GenieSoap.routerMap.get(j).get(
						GenieGlobalDefines.DEVICE_INTENSITY);
				GenieDebug.error("InitMapView", "ConnectionType = "
						+ device.ConnectionType);
				GenieDebug.error("InitMapView", " 666 mac = " + device.mac);

				if (null != m_MacMap && m_MacMap.size() > 0) {
					String mapstr = m_MacMap.get(device.mac);

					// if(null != m_MacMap)
					// {
					// GenieDebug.error("map", "onCreate null != m_MacMap ");
					//
					// Iterator<String> it=m_MacMap.keySet().iterator();
					//
					// while(it.hasNext()){
					// //System.out.println(it.next());
					// GenieDebug.error("InitMapView", " map mac = "+it.next());
					// }
					// }

					GenieDebug.error("InitMapView", "mapstr = " + mapstr);
					GenieDebug.error("InitMapView", "device.mac = "
							+ device.mac);
					if (null != mapstr) {
						int posS = 0;
						int posE = 0;

						StringBuffer temp = new StringBuffer(mapstr);

						posE = temp.indexOf("\n");// temp.indexOf("#");
						GenieDebug.error("debug", temp.substring(0, posE));
						device.name = temp.substring(0, posE);
						temp.replace(0, posE + 1, "");

						posE = temp.indexOf("\n");// temp.indexOf("#");
						GenieDebug.error("debug", temp.substring(0, posE));
						device.type = Integer.valueOf(temp.substring(0, posE));
						// temp.replace(0, posE + 1, "");

					} else {
						device.type = GenieGlobalDefines.Device_networkdev;
						device.name = GenieSoap.routerMap.get(j).get(
								GenieGlobalDefines.DEVICE_NAME);
					}
				} else {
					device.type = GenieGlobalDefines.Device_networkdev;
					device.name = GenieSoap.routerMap.get(j).get(
							GenieGlobalDefines.DEVICE_NAME);
				}

				if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE
						|| m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
					device.blockstatus = GenieSoap.routerMap.get(j).get(
							GenieGlobalDefines.DEVICE_BLOCK);
				}

				testi = testi + 1;
				GenieDebug.error("InitMapView", "InitMapView 0 testi = "
						+ testi);
				m_devicelist.add(device);
			}
		}

		GenieDebug.error("InitMapView",
				"InitMapView ip = " + String.valueOf(wifiInfo.getIpAddress()));

		if (null == device_genie) {

			device_genie = new DeviceInfo();
			device_genie.type = GenieGlobalDefines.Device_Genie;
			device_genie.name = "ANDROID";// wifiInfo;
			GenieDebug.error("InitMapView", " wifiInfo.getIpAddress() = "
					+ wifiInfo.getIpAddress());
			// device_genie.Ip = String.valueOf(wifiInfo.getIpAddress());
			device_genie.Ip = ipIntToString(wifiInfo.getIpAddress()); // GenieSoap.m_GateWayIp;//
			device_genie.mac = GenieMac;
			device_genie.ConnectionType = "wireless";
			device_genie.isturbo = false;

		}

		GenieDebug
				.error("InitMapView", " device_genie.Ip = " + device_genie.Ip);

		if (null == device_internet) {

			device_internet = new DeviceInfo();
			device_internet.type = GenieGlobalDefines.Device_Internet;
			device_internet.name = "internet";
			device_internet.Ip = "192.168.1.1";
			device_internet.mac = "";
			device_internet.ConnectionType = "internet";
			device_internet.isturbo = false;
		}

		if (null == device_route) {

			device_route = new DeviceInfo();
			device_route.type = GetRouterTypeID();
			if (-1 == device_route.type) {
				device_route.type = GenieGlobalDefines.Device_Route;
			}
			device_route.name = GenieSoap.dictionary
					.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
			if (null != m_RouterIP) {
				device_route.Ip = m_RouterIP;
			} else {
				device_route.Ip = "192.168.1.1";
			}
			device_route.mac = GenieSoap.dictionary
					.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
			device_route.ConnectionType = "route";
			device_route.isturbo = false;
		}

		// for(int test = 0; test < m_devicelist.size();test++)
		// {
		// GenieDebug.error("InitMapView"," 555 mac  = "+m_devicelist.get(test).mac);
		// }

		if (!GenieRequest.m_SmartNetWork) {
			maxpagedevice = (mapview.MaxDeviceSize - 3);
		} else {
			maxpagedevice = (mapview.MaxDeviceSize - 2);
		}
		int n = 0;

		count = m_devicelist.size();

		GenieDebug.error("InitMapView", "InitMapView 0 count = " + count);
		GenieDebug.error("InitMapView", "InitMapView 0 devicemax = " + count);
		if (count <= maxpagedevice) {
			GenieDebug.error("InitMapView", "InitMapView 1  ");
			MapPageIndex = 0;
			n = count;
			ArrayList<DeviceInfo> devicelist = new ArrayList<DeviceInfo>();
			for (int j = 0; j < n; j++) {
				DeviceInfo device = new DeviceInfo();
				device.type = m_devicelist.get(j).type;
				device.name = m_devicelist.get(j).name;
				device.Ip = m_devicelist.get(j).Ip;
				device.mac = m_devicelist.get(j).mac;
				device.ConnectionType = m_devicelist.get(j).ConnectionType;
				device.speed = m_devicelist.get(j).speed;
				device.intensity = m_devicelist.get(j).intensity;

				if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE
						|| m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
					device.blockstatus = m_devicelist.get(j).blockstatus;
				}
				devicelist.add(device);
			}

			if (!GenieRequest.m_SmartNetWork) {
				devicelist.add(device_genie);
			}

			devicelist.add(device_internet);

			devicelist.add(device_route);

			GenieDebug.error("InitMapView", "InitMapView 11  ");

			mapview view = new mapview(this, devicelist, m_wlan_ebable,
					m_devicesblockenable);
			view.setClickable(true);

			OnMapClickListener listener1 = new OnMapClickListener() {

				@Override
				public void onClick(mapview view, int Index, int Value,
						boolean gateway) {
					// TODO Auto-generated method stub
					ShowDeviceDialog(Index, Value, gateway);
				}
			};

			view.addClickListener(listener1);
			view.setTag("mainscroll");

			if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE
					|| m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
				GenieDebug.error("debug", "12345678987654321");
				FrameLayout temp = new FrameLayout(this);
				View viewblock = getLayoutInflater().inflate(
						R.layout.switchblockdevice, null);

				CheckBox turnonoroff = (CheckBox) viewblock
						.findViewById(R.id.switchblock);

				if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE) {
					turnonoroff.setChecked(true);

				} else if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
					turnonoroff.setChecked(false);

				}
				turnonoroff
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								GenieDebug.error("debug",
										"12345678987654321 onCheckedChanged isChecked = "
												+ isChecked);
								onTurnOnOrOffDevicesBlock();
							}
						});

				GenieDebug.error("debug",
						"12345678987654321 m_devicesblockenable = "
								+ m_devicesblockenable);

				temp.addView(view, new ViewGroup.LayoutParams(WC, WC));
				temp.addView(viewblock, new FrameLayout.LayoutParams(WC, WC,
						Gravity.BOTTOM | Gravity.RIGHT));
				main_layout.addView(temp);
			} else {
				main_layout.addView(view);
			}

			GenieDebug.error("InitMapView", "InitMapView 12  ");

		} else {
			if (!GenieRequest.m_SmartNetWork) {
				MapPageIndex = count / (mapview.MaxDeviceSize - 3);

				if ((count % (mapview.MaxDeviceSize - 3)) > 0) {
					MapPageIndex = MapPageIndex + 1;
				}
			} else {
				MapPageIndex = count / (mapview.MaxDeviceSize - 2);

				if ((count % (mapview.MaxDeviceSize - 2)) > 0) {
					MapPageIndex = MapPageIndex + 1;
				}
			}

			GenieDebug.error("InitMapView", "InitMapView 2 MapPageIndex = "
					+ MapPageIndex);

			for (int i = 0; i < MapPageIndex; i++) {
				if (maxpagedevice > (count - i * maxpagedevice)) {
					n = (count - i * maxpagedevice);
				} else {
					n = maxpagedevice;
				}
				GenieDebug.error("InitMapView", "InitMapView 21 n = " + n);

				ArrayList<DeviceInfo> devicelist = new ArrayList<DeviceInfo>();
				for (int j = 0; j < n; j++) {
					DeviceInfo device = new DeviceInfo();
					device.type = m_devicelist.get((i * maxpagedevice) + j).type;
					device.name = m_devicelist.get((i * maxpagedevice) + j).name;
					device.Ip = m_devicelist.get((i * maxpagedevice) + j).Ip;
					device.mac = m_devicelist.get((i * maxpagedevice) + j).mac;
					device.ConnectionType = m_devicelist
							.get((i * maxpagedevice) + j).ConnectionType;
					device.speed = m_devicelist.get((i * maxpagedevice) + j).speed;
					device.intensity = m_devicelist
							.get((i * maxpagedevice) + j).intensity;
					if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE
							|| m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
						device.blockstatus = m_devicelist
								.get((i * maxpagedevice) + j).blockstatus;
					}

					// device.type = GenieGlobalDefines.Device_Pc;
					// device.name =
					// GenieSoap.routerMap.get((i*n)+j).get(GenieGlobalDefines.DEVICE_NAME);
					// device.Ip =
					// GenieSoap.routerMap.get((i*n)+j).get(GenieGlobalDefines.DEVICE_IP);
					GenieDebug.error("InitMapView", "InitMapView 999 i = " + i);
					GenieDebug.error("InitMapView",
							"InitMapView 999 (i*MapPageIndex)+j = "
									+ ((i * MapPageIndex) + j));
					GenieDebug.error("InitMapView",
							"InitMapView 999 device.name = " + device.name);
					GenieDebug.error("InitMapView",
							"InitMapView 999 device.mac = " + device.mac);
					devicelist.add(device);
				}

				if (!GenieRequest.m_SmartNetWork) {
					DeviceInfo genie = new DeviceInfo();
					genie.type = device_genie.type;
					genie.name = device_genie.name;
					genie.Ip = device_genie.Ip;
					genie.mac = device_genie.mac;
					genie.ConnectionType = device_genie.ConnectionType;
					genie.isturbo = false;
					devicelist.add(genie);
				}

				DeviceInfo internet = new DeviceInfo();
				internet.type = device_internet.type;
				internet.name = device_internet.name;
				internet.Ip = device_internet.Ip;
				internet.mac = device_internet.mac;
				internet.ConnectionType = device_internet.ConnectionType;
				devicelist.add(internet);

				DeviceInfo route = new DeviceInfo();
				route.type = device_route.type;
				route.name = device_route.name;
				route.Ip = device_route.Ip;
				route.mac = device_route.mac;
				route.ConnectionType = device_route.ConnectionType;
				devicelist.add(route);
				mapview view = new mapview(this, devicelist, m_wlan_ebable,
						m_devicesblockenable);
				view.setClickable(true);

				OnMapClickListener listener1 = new OnMapClickListener() {

					@Override
					public void onClick(mapview view, int Index, int Value,
							boolean gateway) {
						// TODO Auto-generated method stub
						ShowDeviceDialog(Index, Value, gateway);
					}
				};

				view.addClickListener(listener1);
				view.setTag("mainscroll");

				if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE
						|| m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
					GenieDebug.error("debug", "12345678987654321");
					FrameLayout temp = new FrameLayout(this);
					View viewblock = getLayoutInflater().inflate(
							R.layout.switchblockdevice, null);
					CheckBox turnonoroff = (CheckBox) viewblock
							.findViewById(R.id.switchblock);

					if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_ENABLE) {
						turnonoroff.setChecked(true);

					} else if (m_devicesblockenable == GenieGlobalDefines.BLOCKDEVICES_DISABLE) {
						turnonoroff.setChecked(false);

					}

					turnonoroff
							.setOnCheckedChangeListener(new OnCheckedChangeListener() {

								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									// TODO Auto-generated method stub
									GenieDebug.error("debug",
											"12345678987654321 onCheckedChanged isChecked = "
													+ isChecked);
									onTurnOnOrOffDevicesBlock();
								}
							});

					GenieDebug.error("debug",
							"12345678987654321 m_devicesblockenable = "
									+ m_devicesblockenable);

					temp.addView(view, new ViewGroup.LayoutParams(WC, WC));
					temp.addView(viewblock, new FrameLayout.LayoutParams(WC,
							WC, Gravity.BOTTOM | Gravity.RIGHT));
					main_layout.addView(temp);
				} else {
					main_layout.addView(view);
				}

				GenieDebug.error("InitMapView", "InitMapView 22 n = " + n);
			}
		}

		// if(MapPageIndex > 0)
		// {
		PopupPage();
		// }

		// PopupBlockDeviceSwitch();
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

			setContentView(R.layout.map);
			// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);

			Log.i("onConfigurationChanged", "onConfigurationChanged --1--");

			InitMapView();

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

			setContentView(R.layout.map);
			// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);

			Log.i("onConfigurationChanged", "onConfigurationChanged --1--");

			InitMapView();
		}

		super.onConfigurationChanged(newConfig);
	}

	private String ipIntToString(int ip) {
		try {
			byte[] bytes = new byte[4];
			bytes[0] = (byte) (0xff & ip);
			bytes[1] = (byte) ((0xff00 & ip) >> 8);
			bytes[2] = (byte) ((0xff0000 & ip) >> 16);
			bytes[3] = (byte) ((0xff000000 & ip) >> 24);
			return Inet4Address.getByAddress(bytes).getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}

	public String getGateWay() {

		WifiManager test = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo = test.getDhcpInfo();

		GenieDebug.error("debug", "gateway = "
				+ ipIntToString(dhcpInfo.gateway));

		return ipIntToString(dhcpInfo.gateway);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
			return true;
		}
		return super.dispatchKeyEvent(event);
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

			// if(aSoapType == GenieGlobalDefines.ESoapRequestGuestEnable
			// || aSoapType == GenieGlobalDefines.ESoapReqiestTrafficEnable)
			// {
			// aResponseCode = "401";
			// aResultType = RequestResultType.failed;
			// }

			if (lable != null && lable.equals("GenieMap")) {
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

				if (null != ActionType && ActionType == RequestActionType.Soap) {
					if (null != aResultType) {
						if (aResultType == RequestResultType.Succes) {
							switch (aSoapType) {
							case GenieGlobalDefines.ESoapRequestRouterMap:
								InitMapView();
								int count = GenieSoap.routerMap.size();
								if (count <= 0 && m_index < 3) {
									StartTimer();
								}
								break;
							default:
								break;
							}
						} else {
							switch (aSoapType) {
							case GenieGlobalDefines.ESoapRequestRouterMap:
								InitFailureView();
								if (m_index < 3) {
									StartTimer();
								}
								break;
							default:
								break;
							}
						}
					}
				}
			}

		}
	}

	// 广播
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

//			System.out.println("~~~~~~~~~~~~~~广播进来!!");
			String action = intent.getAction();
			if (SCANDEVICE.equals(action.trim())) {
				if (ScanDeviceService.discovered_services != null
						&& ScanDeviceService.discovered_services.size() > 0) {
					for (String str : ScanDeviceService.discovered_services) {
//						System.out.println("~~~~~~~~~~~~现有开启服务的IP 是：" + str);
					}
					if (mapview.mview != null) {
						mapview.mview.invalidate();
					}
					if (main_layout != null)
						main_layout.invalidate();
				}

			}
			ScanDeviceService.scaning = true;
		}
	};
}
