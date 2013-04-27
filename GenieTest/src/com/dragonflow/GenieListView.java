package com.dragonflow;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chart.widget.HistogramView;
import com.chart.widget.XAxisLable;
import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.GenieRequest.OnProgressCancelListener;
import com.dragonflow.GenieWIFIBroadcast.WIFIBroadcastCallBack;
import com.ewm.Create2Code;
import com.google.zxing.WriterException;

public class GenieListView extends Activity implements WIFIBroadcastCallBack {
	final int SET_GUESTACCESS_DISABLE = 11111;
	final int SHOWPROGRESSDIALOG = 11112;
	private int activeIndex = 0;
	private int activityresult = 0;

	private WifiManager wifiManager = null;
	private WifiInfo wifiInfo = null;

	// private GenieSoap soapCommand = null;
	private ProgressDialog progressDialog = null;
	public Button modifybutton = null;
	private static final int menuid = 100;
	// private ToggleButton toggleButton=null;
	private CheckBox toggleButton = null;
	private Boolean trafficstates = true;

	private RelativeLayout m_listitem01 = null;
	private RelativeLayout m_listitem02 = null;
	private RelativeLayout m_listitem03 = null;
	private RelativeLayout m_listitem04 = null;

	private LinearLayout layout_ll_signal = null;
	private LinearLayout layout_ll_link = null;

	private TextView list_tv_signal = null, list_tv_signal_value = null,
			list_tv_link = null, list_tv_link_value = null;
	private View list_vv_signal, list_vv_link;

	private boolean m_isback = false;

	MenuItem mModify = null; // qicheng.ai

	private LinearLayout Guestaccess_layout = null;

	private LinearLayout main_layout = null;

	private Boolean m_create = false;

	// public ToggleButton controlBT = null;
	public CheckBox controlBT = null;

	private Thread m_Thread = null;

	private static final String[] m_control = { "No Limit", "Download only",
			"Both directions" };
	private static final String[] m_control2 = { "Unlimited", "Download Only",
			"Download & Upload" };

	private static final String[] traffic_str = { "Today", "Yesterday",
			"This week", "This month", "Last month" };
	// private static final int traffic_id[] =
	// {R.id.tabletv_6,R.id.tabletv_7,R.id.tabletv_8,R.id.tabletv_9,R.id.tabletv_10,
	// R.id.tabletv_11,R.id.tabletv_12,R.id.tabletv_13,R.id.tabletv_14,R.id.tabletv_15,
	// R.id.tabletv_16,R.id.tabletv_17,R.id.tabletv_18,R.id.tabletv_19,R.id.tabletv_20,
	// R.id.tabletv_21,R.id.tabletv_22,R.id.tabletv_23,R.id.tabletv_24,R.id.tabletv_25,
	// R.id.tabletv_26,R.id.tabletv_27,R.id.tabletv_28,R.id.tabletv_29,R.id.tabletv_30};

	private static final int traffic_id[] = null;
	// {R.id.today_time,R.id.today_upload,R.id.today_download,R.id.today_total,
	// R.id.yesterday_time,R.id.yesterday_upload,R.id.yesterday_download,R.id.yesterday_total,
	// R.id.week_time,R.id.week_upload,R.id.week_download,R.id.week_total,
	// R.id.month_time,R.id.month_upload,R.id.month_download,R.id.month_total,
	// R.id.last_month_time,R.id.last_month_upload,R.id.last_month_download,R.id.last_month_total};

	public GenieWIFIBroadcast m_WIFIBroadcast = null;

	// 增加二维
	public Bitmap bitmap=null;
	public ImageView image_ewm=null;
	public TextView string_ewm=null;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String error = null;
			GenieDebug.error("handleMessage",
					"listview handleMessage msg.what = " + msg.what);

			if (m_isback) {
				return;
			}

			switch (msg.what) {
			case GenieGlobalDefines.ESoapRequestWLanInfo:
			case GenieGlobalDefines.ESoapRequestGuestInfo:
			case GenieGlobalDefines.ESoapRequestRouterMap:
			case GenieGlobalDefines.ESoapRequestTrafficMeter:
				activityresult = GenieGlobalDefines.EFunctionResult_Success;
				fillData2List();
				cancelProgressDialog();
				if (histogram != null && histogram1 != null) {
					histogram.refreshHistogram();
					histogram1.refreshHistogram();
				}
				break;
			case GenieGlobalDefines.ESoapRequestfailure:
				activityresult = GenieGlobalDefines.EFunctionResult_failure;
				cancelProgressDialog();
				error = getResources().getString(R.string.failure);
				// showToast(error); // qicheng.ai add
				break;
			case GenieGlobalDefines.ESoapRequestsuccess:
				activityresult = GenieGlobalDefines.EFunctionResult_Success;
				cancelProgressDialog();
				error = getResources().getString(R.string.success);
				// qicheng.ai add
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						GenieListView.this)
				// .setIcon(R.drawable.icon)
				// .setTitle(" ")
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										setResult(GenieGlobalDefines.WIRELESSSETTING_RESULT);
										finish();
									}
								}).setMessage(R.string.wirelsssettinrelogin);
				dialog.show();

				// showToast(error);
				break;
			case GenieGlobalDefines.ESoapRequestnotsupport:
				activityresult = GenieGlobalDefines.EFunctionResult_failure;
				cancelProgressDialog();
				if (controlBT != null) {
					GenieDebug.error("handleMessage", "ESoapRequestnotsupport"
							+ controlBT);
					controlBT.setChecked(false);
					controlBT.setEnabled(false);
				}
				if (toggleButton != null) {
					GenieDebug.error("handleMessage", "ESoapRequestnotsupport"
							+ toggleButton);
					toggleButton.setEnabled(false);
					toggleButton.setClickable(false);
				}
				GenieDebug.error("handleMessage",
						"1111111111111111+ESoapRequestnotsupport");
				// controlBT.setEnabled(false);
				error = getResources().getString(R.string.notsupport);
				ShowMessageDialog(error);
				// showToast(error);
				break;
			case SET_GUESTACCESS_DISABLE:
				cancelProgressDialog();
				controlBT.setChecked(true);
				break;
			case SHOWPROGRESSDIALOG:
				showProgressDialog();
				break;
			default:
				cancelProgressDialog();
				break;
			}
		}
	};

	private Dialog messagedialog = null;

	private void ShowMessageDialog(String message) {
		if (messagedialog != null) {
			if (messagedialog.isShowing()) {
				// messagedialog.dismiss();
				return;
			}
			messagedialog = null;
		}

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setMessage(message);
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						GenieListView.this.onBackPressed();
					}
				});

		dialog.setCancelable(false);

		messagedialog = dialog.create();
		messagedialog.show();
	}

	public void showToast(String error) {
		// Toast toast = Toast.makeText(this,error, Toast.LENGTH_LONG);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		// toast.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GenieGlobalDefines.WIRELESS_REQUESTCODE
				|| requestCode == GenieGlobalDefines.GUESTACCESS_REQUESTCODE) {
			if (resultCode == GenieGlobalDefines.WIRELESSSETTING_RESULT) {
				setResult(GenieGlobalDefines.WIRELESSSETTING_RESULT);
				finish();
			}
			if (resultCode == GenieGlobalDefines.WIRELESSSETTING_REFRESH) {
				getModelInfo(false);
			}
		}

		if (requestCode == GenieGlobalDefines.TRAFFIC_REQUESTCODE) {
			if (resultCode == GenieGlobalDefines.TRAFFICSETTING_RESULT) {
				getModelInfo(false);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		// if(!soapCommand.support)
		// {
		// return true;
		// }

		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.menu, menu);
		// mModify = menu.findItem(R.id.item01).setTitle("modify");
		// menu.findItem(R.id.item02).setTitle("Refresh");
		//
		// //qicheng.ai add
		// if(!trafficstates)
		// mModify.setVisible(false);
		//
		// if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_MAP)
		// {
		// menu.findItem(R.id.item01).setVisible(false);
		// //menu.clear();
		// }

		// if(activeIndex == GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS)
		// {
		// String title = null;
		// if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1")
		// > -1)
		// {
		// title = getResources().getString(R.string.off);
		// menu.add(Menu.NONE, menuid, Menu.NONE, title);
		// }
		// //else
		// //{
		// // title = getResources().getString(R.string.on);
		// //}
		//
		// //menu.add(Menu.NONE, menuid, Menu.NONE, title);
		//
		// //menu.clear();
		// }

		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		m_isback = true;
		// soapCommand.cancelhttpUrlConnection();

		StopRequest();

		cancelThread();
		Bundle extra = new Bundle();

		Intent i = new Intent();
		int resultcode = 0;

		switch (activeIndex) {
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
			if (activityresult == GenieGlobalDefines.EFunctionResult_Success) {

				resultcode = GenieGlobalDefines.EFunctionResult_Success;
			} else {
				resultcode = GenieGlobalDefines.EFunctionResult_failure;
			}
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
			if (activityresult == GenieGlobalDefines.EFunctionResult_Success) {

				resultcode = GenieGlobalDefines.EFunctionResult_Success;
			} else {
				resultcode = GenieGlobalDefines.EFunctionResult_failure;
			}
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_MAP:
			if (activityresult == GenieGlobalDefines.EFunctionResult_Success) {
				int count = GenieSoap.routerMap.size();
				extra.putInt(GenieGlobalDefines.EFunctionMap_Result, count);
				resultcode = GenieGlobalDefines.EFunctionResult_Success;
			} else {
				resultcode = GenieGlobalDefines.EFunctionResult_failure;
			}
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
			if (activityresult == GenieGlobalDefines.EFunctionResult_Success) {

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

	// public Boolean setGuestAccessDisabled()
	// {
	// Boolean Status = false;
	//
	// //soapCommand.configurationstarted();
	// Status = soapCommand.setGuestAccessEnabled();
	// //soapCommand.configurationfinished();
	//
	// return true;
	// }

	public void setGuestAccessDisabled() {
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieListView";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigGuestEnable;
		request0.aServer = "WLANConfiguration";
		request0.aMethod = "SetGuestAccessEnabled";
		request0.aNeedwrap = true;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = false;
		request0.aTimeout = 25000;
		request0.aElement = new ArrayList<String>();
		request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE);
		request0.aElement.add("0");
		requestinfo.add(request0);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.guestaccess) + " "
				+ getResources().getString(R.string.info);

		m_GetGuestAcessInfo = new GenieRequest(this, requestinfo);
		m_GetGuestAcessInfo.SetProgressInfo(true, true);
		m_GetGuestAcessInfo.SetProgressText(title, null);
		m_GetGuestAcessInfo.Start();
	}

	public void DeleteGuesstTime() {
		if ((GenieMainView.alarmManager != null)
				&& (GenieMainView.pendingActivityIntent != null)) {
			GenieMainView.alarmManager
					.cancel(GenieMainView.pendingActivityIntent);
			GenieMainView.alarmManager = null;
			GenieMainView.pendingActivityIntent = null;
		}

		HashMap<String, String> accesstime = GenieSerializ.ReadMap(
				GenieListView.this, "guestaccess");

		if (accesstime == null) {
			accesstime = new HashMap<String, String>();
		}

		accesstime.put("GUESTABLE", "0");
		accesstime.put("GUESTTIME", GenieGlobalDefines.NULL);

		accesstime.put("GUESTSTARTTIME", GenieGlobalDefines.NULL);

		accesstime.put("GUESTENDTIME", GenieGlobalDefines.NULL);

		accesstime.put("GUESTTIMEMAC", GenieGlobalDefines.NULL);

		GenieSerializ.WriteMap(GenieListView.this, accesstime, "guestaccess");

	}

	private void onMenuid_100() {

		setGuestAccessDisabled();
		// sendMessage2UI(SHOWPROGRESSDIALOG);
		// GenieDebug.error("debug", "onMenuid_100 --1--");
		// cancelThread();
		// m_Thread = new Thread()
		// {
		// public void run()
		// {
		// GenieDebug.error("debug", "onMenuid_100 --run--");
		//
		// DeleteGuesstTime();
		// if(setGuestAccessDisabled())
		// {
		// //getModelInfo(); qicheng.ai change must re-join
		// sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);
		// }else
		// {
		// sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
		// sendMessage2UI(SET_GUESTACCESS_DISABLE);
		// }
		// }
		// };
		// m_Thread.start();
	}

	private void onRefresh() {
		getModelInfo(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.item01:
			onModifyMenu();
			return true;
		case R.id.item02:
			onRefresh();
			return true;
			// case menuid:
			// onMenuid_100();
			// return true;
		}
		return false;
	}

	public void onModifyMenu() {
		Intent intent = new Intent();
		switch (activeIndex) {
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:

			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
			intent.setClass(GenieListView.this, GenieWifiModify.class);
			startActivityForResult(intent,
					GenieGlobalDefines.WIRELESS_REQUESTCODE);
			break;

		case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS);
			intent.setClass(GenieListView.this, GenieWifiModify.class);
			startActivityForResult(intent,
					GenieGlobalDefines.GUESTACCESS_REQUESTCODE);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
			intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
					GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC);
			intent.setClass(GenieListView.this, GenieTrafficSetting.class);
			startActivityForResult(intent,
					GenieGlobalDefines.TRAFFIC_REQUESTCODE);
			break;

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();

		// ConstantDefine.SCREEN_Heigth = d.getHeight();
		// ConstantDefine.SCREEN_Width = d.getWidth();

		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);

		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			setTheme(R.style.bigactivityTitlebarNoSearch);
		} else {
			setTheme(R.style.activityTitlebarNoSearch); // qicheng.ai add
		}

		// setTheme(R.style.activityTitlebarNoSearch);//qicheng.ai add

		activeIndex = getIntent().getIntExtra(
				GenieGlobalDefines.LIST_TYPE_INDEX, 0);

		GenieDebug.error("debug", "onCreate --0--");

		requestWindowFeature(Window.FEATURE_NO_TITLE
				| Window.FEATURE_CUSTOM_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
	
		
		switch (activeIndex) {
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
		case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS_EMPTY:
			// case GenieGlobalDefines.ACTIVE_FUNCTION_MAP:
			setContentView(R.layout.list_2);
			image_ewm = (ImageView) findViewById(R.id.image_ewm);
			string_ewm = (TextView) findViewById(R.id.string_ewm);
			string_ewm.setVisibility(View.GONE);
			image_ewm.setVisibility(View.GONE);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
			trafficstates = false;
			// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setContentView(R.layout.trafficmap);
			toggleButton = (CheckBox) findViewById(R.id.ToggleButton01);
			break;
		}
		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
		}
		GenieDebug.error("debug", "onCreate --1--");

		m_isback = false;
		m_Thread = null;
		m_create = true;

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// TelephonyManager Tel = ( TelephonyManager
		// )getSystemService(Context.TELEPHONY_SERVICE);
		// Tel.listen(new MyPhoneStateListener(),
		// PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		
		
		InitTitleView();
		InitView();

		activityresult = GenieGlobalDefines.EFunctionResult_failure;

		// soapCommand = new GenieSoap(this);
		// soapCommand.pListView = this;
		// soapCommand.parentview = GenieGlobalDefines.GenieView_ListView;
		getModelInfo(true);

		toggleButton = (CheckBox) findViewById(R.id.ToggleButton01);

		// m_WIFIBroadcast = new GenieWIFIBroadcast(this);
		// if(null != m_WIFIBroadcast)
		// {
		// m_WIFIBroadcast.RegisterBroadcastReceiver();
		// m_WIFIBroadcast.SetOnWIFIBroadcastCallBack(this);
		// }
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
		StopRequest();
		UnRegisterBroadcastReceiver();

		// if(null != m_WIFIBroadcast)
		// {
		// m_WIFIBroadcast.UnRegisterBroadcastReceiver();
		// m_WIFIBroadcast = null;
		// }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (m_create) {
			m_create = false;
		} else {
			// getModelInfo();

		}
	}

	public void InitView() {
		switch (activeIndex) {
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
			initWirelessView();
			break;

		case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
			initGuestAccessView();
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS_EMPTY:
			initEmptyWirelessView();
			break;
		}
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
		Button back = (Button) this.findViewById(R.id.back);
		Button about = (Button) this.findViewById(R.id.about);

		TextView title = (TextView) this.findViewById(R.id.netgeartitle);

		switch (activeIndex) {
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS_EMPTY:
			title.setText(R.string.settings);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
			title.setText(R.string.guestaccess);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
			title.setText(R.string.traffic);
			break;
		}

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
				// OnClickAbout();
				getModelInfo(false);
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
					v.setBackgroundResource(R.drawable.title_bt_fj);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
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

	public void cancelProgressDialog() {
		if (null != progressDialog) {
			progressDialog.dismiss();
			progressDialog = null;
		}

	}

	public void showProgressDialog() {
		if (null != progressDialog) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		String temp = null;

		switch (activeIndex) {
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
			temp = getResources().getString(R.string.wireless);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
			temp = getResources().getString(R.string.guestaccess);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
			temp = getResources().getString(R.string.traffic);
			break;
		}

		String title = getResources().getString(R.string.loading) + " " + temp
				+ " " + getResources().getString(R.string.info);

		progressDialog = ProgressDialog.show(GenieListView.this, title,
				getResources().getString(R.string.wait), true, true);

		// progressDialog = ProgressDialog.show(GenieListView.this,
		// "Loading...", "Please wait...", true, true);

	}

	private void cancelThread() {
		// if(null != m_Thread)
		// {
		// m_Thread.destroy();
		// m_Thread.
		// m_Thread = null;
		// }
	}

	// public void getModelInfo()
	// {
	// GenieDebug.error("debug", "getModelInfo --0--");
	//
	// sendMessage2UI(SHOWPROGRESSDIALOG);
	//
	//
	// GenieDebug.error("debug", "getModelInfo --1--");
	// cancelThread();
	// m_Thread = new Thread()
	// {
	// public void run()
	// {
	// GenieDebug.error("debug", "getModelInfo --run--");
	//
	// switch(activeIndex)
	// {
	// case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
	//
	//
	// soapCommand.getWLanInfo();
	// break;
	//
	// case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
	// soapCommand.getGuestAccessEnabled();
	// soapCommand.getGuestAccessInfo(); //qicheng.ai add
	// //soapCommand.getWLanInfo();
	// break;
	// //
	// // case GenieGlobalDefines.ACTIVE_FUNCTION_MAP:
	// // soapCommand.getRouterMap();
	// // break;
	// case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
	// soapCommand.getTrafficMeterEnable();
	// soapCommand.getTrafficMeterOptions(); //qicheng.ai
	// //soapCommand.getTrafficMeterOptions();
	// soapCommand.getTrafficMeter();
	// // if(!soapCommand.getTrafficMeter())
	// // {
	// // if(soapCommand.httpresponseCode == 401 )
	// // {
	// // sendMessage2UI(GenieGlobalDefines.ESoapRequestnotsupport);
	// // }
	// // }
	//
	// break;
	//
	// }
	// }
	// };
	// m_Thread.start();
	//
	//
	//
	// }

	public void getModelInfo(boolean first) {
		
		System.out.println("!!!@@@ 刷新");
		GenieDebug.error("debug", "getModelInfo --0--");

		switch (activeIndex) {
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
			setWifiInfo();
			GetRouterWlanInfo(first);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
			GetGuestAcessInfo(first);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
			GetTrafficMeterInfo(first);
			break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS_EMPTY:
			setWifiInfo();
			break;

		}

	}

	private GenieRequest m_GetRouterWlanInfo = null;
	private GenieRequest m_GetGuestAcessInfo = null;
	private GenieRequest m_GetTrafficMeterInfo = null;

	private void StopRequest() {
		if (null != m_GetRouterWlanInfo) {
			m_GetRouterWlanInfo.Stop();
			m_GetRouterWlanInfo = null;
		}
		if (null != m_GetGuestAcessInfo) {
			m_GetGuestAcessInfo.Stop();
			m_GetGuestAcessInfo = null;
		}
		if (null != m_GetTrafficMeterInfo) {
			m_GetTrafficMeterInfo.Stop();
			m_GetTrafficMeterInfo = null;
		}
	}

	private void GetRouterWlanInfo(boolean first) {
		// soapCommand.getWLanInfo();

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieListView";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestWLanInfo;
		request0.aServer = "WLANConfiguration";
		request0.aMethod = "GetInfo";
		request0.aNeedwrap = false;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = true;
		request0.aTimeout = 25000;
		request0.aElement = new ArrayList<String>();
		requestinfo.add(request0);

		GenieRequestInfo request1 = new GenieRequestInfo();
		request1.aRequestLable = "GenieListView";
		request1.aSoapType = GenieGlobalDefines.ESoapRequestWLanWEPKey;
		request1.aServer = "WLANConfiguration";
		request1.aMethod = "GetWPASecurityKeys";
		request1.aNeedwrap = false;
		request1.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request1.aNeedParser = true;
		request1.aTimeout = 25000;
		request1.aElement = new ArrayList<String>();
		request1.aElement.add("NewWPAPassphrase");
		request1.aElement.add("null");
		requestinfo.add(request1);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.wireless) + " "
				+ getResources().getString(R.string.info);

		m_GetRouterWlanInfo = new GenieRequest(this, requestinfo);
		m_GetRouterWlanInfo.SetProgressInfo(true, true);
		m_GetRouterWlanInfo.SetProgressText(title, null);

		if (first) {
			m_GetRouterWlanInfo
					.SetProgressCancelListener(new OnProgressCancelListener() {

						@Override
						public void OnProgressCancel() {
							// TODO Auto-generated method stub
							GenieListView.this.finish();
						}
					});
		}
		m_GetRouterWlanInfo.Start();
	}

	private void GetGuestAcessInfo(boolean first) {

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo request = new GenieRequestInfo();
		request.aRequestLable = "GenieListView";
		request.aSoapType = GenieGlobalDefines.ESoapRequestWLanInfo;
		request.aServer = "WLANConfiguration";
		request.aMethod = "GetInfo";
		request.aNeedwrap = false;
		request.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request.aNeedParser = true;
		request.aTimeout = 25000;
		request.aElement = new ArrayList<String>();
		requestinfo.add(request);

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieListView";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestGuestEnable;
		request0.aServer = "WLANConfiguration";
		request0.aMethod = "GetGuestAccessEnabled";
		request0.aNeedwrap = false;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = true;
		request0.aTimeout = 25000;
		request0.aElement = new ArrayList<String>();
		request0.aElement.add("NewGuestAccessEnabled");
		request0.aElement.add("null");
		requestinfo.add(request0);

		GenieRequestInfo request1 = new GenieRequestInfo();
		request1.aRequestLable = "GenieListView";
		request1.aSoapType = GenieGlobalDefines.ESoapRequestGuestInfo;
		request1.aServer = "WLANConfiguration";
		request1.aMethod = "GetGuestAccessNetworkInfo";
		request1.aNeedwrap = false;
		request1.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request1.aNeedParser = true;
		request1.aTimeout = 25000;
		request1.aElement = new ArrayList<String>();
		request1.aElement.add("NewSSID");
		request1.aElement.add("null");
		requestinfo.add(request1);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.guestaccess) + " "
				+ getResources().getString(R.string.info);

		m_GetGuestAcessInfo = new GenieRequest(this, requestinfo);
		m_GetGuestAcessInfo.SetProgressInfo(true, true);
		m_GetGuestAcessInfo.SetProgressText(title, null);

		if (first) {
			m_GetGuestAcessInfo
					.SetProgressCancelListener(new OnProgressCancelListener() {

						@Override
						public void OnProgressCancel() {
							// TODO Auto-generated method stub
							GenieListView.this.finish();
						}
					});
		}

		m_GetGuestAcessInfo.Start();

	}

	private void GetTrafficMeterInfo(boolean first) {
		// soapCommand.getTrafficMeterEnable();
		// soapCommand.getTrafficMeterOptions(); //qicheng.ai
		// soapCommand.getTrafficMeter();

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieListView";
		request0.aSoapType = GenieGlobalDefines.ESoapReqiestTrafficEnable;
		request0.aServer = "DeviceConfig";
		request0.aMethod = "GetTrafficMeterEnabled";
		request0.aNeedwrap = false;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = true;
		request0.aTimeout = 25000;
		request0.aElement = new ArrayList<String>();
		request0.aElement.add("NewTrafficMeterEnable");
		request0.aElement.add("null");
		requestinfo.add(request0);

		GenieRequestInfo request1 = new GenieRequestInfo();
		request1.aRequestLable = "GenieListView";
		request1.aSoapType = GenieGlobalDefines.ESoapRequestTrafficOptions;
		request1.aServer = "DeviceConfig";
		request1.aMethod = "GetTrafficMeterOptions";
		request1.aNeedwrap = false;
		request1.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request1.aNeedParser = true;
		request1.aTimeout = 25000;
		request1.aElement = new ArrayList<String>();
		request1.aElement.add("NewTrafficMeterEnable");
		request1.aElement.add("null");
		requestinfo.add(request1);

		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC, "");

		GenieRequestInfo request2 = new GenieRequestInfo();
		request2.aRequestLable = "GenieListView";
		request2.aSoapType = GenieGlobalDefines.ESoapRequestTrafficMeter;
		request2.aServer = "DeviceConfig";
		request2.aMethod = "GetTrafficMeterStatistics";
		request2.aNeedwrap = false;
		request2.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request2.aNeedParser = true;
		request2.aTimeout = 25000;
		request2.aElement = new ArrayList<String>();
		request2.aElement.add("NewTodayConnectionTime");
		request2.aElement.add("null");
		requestinfo.add(request2);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.traffic) + " "
				+ getResources().getString(R.string.info);

		m_GetTrafficMeterInfo = new GenieRequest(this, requestinfo);
		m_GetTrafficMeterInfo.SetProgressInfo(true, true);
		m_GetTrafficMeterInfo.SetProgressText(title, null);

		if (first) {
			m_GetTrafficMeterInfo
					.SetProgressCancelListener(new OnProgressCancelListener() {

						@Override
						public void OnProgressCancel() {
							// TODO Auto-generated method stub
							GenieListView.this.finish();
						}
					});
		}

		m_GetTrafficMeterInfo.Start();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		boolean ret = super.dispatchTouchEvent(ev);

		if (ev.getAction() == MotionEvent.ACTION_UP) {
			if (null != m_listitem01) {
				m_listitem01.setBackgroundDrawable(null);
			}
			if (null != m_listitem02) {
				m_listitem02.setBackgroundDrawable(null);
			}
			if (null != m_listitem03) {
				m_listitem03.setBackgroundDrawable(null);
			}
			if (null != m_listitem04) {
				m_listitem04.setBackgroundDrawable(null);
			}
		}

		return ret;
	}

	public void setWifiInfo() {

		if (GenieRequest.m_SmartNetWork)
			return;
		if (wifiManager != null) {
			wifiInfo = wifiManager.getConnectionInfo();
			if (list_tv_signal_value != null && list_tv_link_value != null) {
				// int signal = Math.abs(wifiInfo.getRssi());
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
				list_tv_signal_value.setText(signal * 10 + " %");// (rssi+100)
				list_tv_link_value.setText(wifiInfo.getLinkSpeed() + " Mbps");
			}
		}
	}

	public void initEmptyWirelessView() {
		Guestaccess_layout = (LinearLayout) findViewById(R.id.LinearLayout_01);
		Guestaccess_layout.setVisibility(View.GONE);

		m_listitem01 = (RelativeLayout) findViewById(R.id.layout_listitem01);
		m_listitem02 = (RelativeLayout) findViewById(R.id.layout_listitem02);
		m_listitem03 = (RelativeLayout) findViewById(R.id.layout_listitem03);

		list_tv_signal = (TextView) findViewById(R.id.list_tv_signal);
		list_tv_signal_value = (TextView) findViewById(R.id.list_tv_signal_value);
		list_tv_link = (TextView) findViewById(R.id.list_tv_link);
		list_tv_link_value = (TextView) findViewById(R.id.list_tv_link_value);

		list_tv_signal.setText(getResources()
				.getString(R.string.signalstrength));
		list_tv_link.setText(getResources().getString(R.string.linkrate));

		setWifiInfo();

		m_listitem01.setClickable(false);
		m_listitem02.setClickable(false);
		m_listitem03.setClickable(false);

		main_layout = (LinearLayout) findViewById(R.id.layout_02);
		main_layout.setVisibility(View.VISIBLE);

		TextView temp1 = (TextView) findViewById(R.id.list_tv_01);
		temp1.setText(getResources().getString(R.string.wirelessname));

		TextView temp3 = (TextView) findViewById(R.id.list_tv_03);
		temp3.setText(getResources().getString(R.string.channel));

		TextView temp5 = (TextView) findViewById(R.id.list_tv_05);
		temp5.setText(getResources().getString(R.string.networkkey));

		TextView temp2 = (TextView) findViewById(R.id.list_tv_02);
		temp2.setText(GenieSoap.KEY_NULL);

		TextView temp4 = (TextView) findViewById(R.id.list_tv_04);
		temp4.setText(GenieSoap.KEY_NULL);

		TextView temp6 = (TextView) findViewById(R.id.list_tv_06);
		temp6.setText(GenieSoap.KEY_NULL);

		ImageView arrows1 = (ImageView) findViewById(R.id.list_arrows1);
		arrows1.setVisibility(View.GONE);

		ImageView arrows2 = (ImageView) findViewById(R.id.list_arrows2);
		arrows2.setVisibility(View.GONE);

		ImageView arrows3 = (ImageView) findViewById(R.id.list_arrows3);
		arrows3.setVisibility(View.GONE);
		
		
		
	}

	public void initWirelessView() {
		Guestaccess_layout = (LinearLayout) findViewById(R.id.LinearLayout_01);
		Guestaccess_layout.setVisibility(View.GONE);

		m_listitem01 = (RelativeLayout) findViewById(R.id.layout_listitem01);
		m_listitem02 = (RelativeLayout) findViewById(R.id.layout_listitem02);
		m_listitem03 = (RelativeLayout) findViewById(R.id.layout_listitem03);

		if (GenieRequest.m_SmartNetWork) {
			layout_ll_signal = (LinearLayout) findViewById(R.id.layout_ll_signal);
			layout_ll_link = (LinearLayout) findViewById(R.id.layout_ll_link);
			list_vv_signal = findViewById(R.id.list_vv_signal);
			list_vv_link = findViewById(R.id.list_vv_link);
			layout_ll_signal.setVisibility(View.GONE);
			layout_ll_link.setVisibility(View.GONE);
			list_vv_signal.setVisibility(View.GONE);
			list_vv_link.setVisibility(View.GONE);
		} else {
			list_tv_signal = (TextView) findViewById(R.id.list_tv_signal);
			list_tv_signal_value = (TextView) findViewById(R.id.list_tv_signal_value);
			list_tv_link = (TextView) findViewById(R.id.list_tv_link);
			list_tv_link_value = (TextView) findViewById(R.id.list_tv_link_value);

			list_tv_signal.setText(getResources().getString(
					R.string.signalstrength));
			list_tv_link.setText(getResources().getString(R.string.linkrate));
		}
		setWifiInfo();

		m_listitem01.setOnClickListener(onGuestAccessclick);
		m_listitem02.setOnClickListener(onGuestAccessclick);
		m_listitem03.setOnClickListener(onGuestAccessclick);

		m_listitem01.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				GenieDebug.error("onTouch ", "onTouch event.getAction() = "
						+ event.getAction());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj_dz);
//					 v.setBackgroundColor(R.color.Black);
					// v.setBackgroundResource(R.drawable.deviceiphone);
					// SetBackgroundResource_down(v);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// v.setBackgroundResource(R.drawable.deviceipad);
					// SetBackgroundResource_up(v);

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj);
					// v.setBackgroundColor(R.color.blackcolor);
					v.setBackgroundDrawable(null);
				}
				return false;
			}
		});

		m_listitem02.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				GenieDebug.error("onTouch ", "onTouch event.getAction() = "
						+ event.getAction());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj_dz);
					// v.setBackgroundColor(R.color.Black);
					// v.setBackgroundResource(R.drawable.deviceiphone);
					// SetBackgroundResource_down(v);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// v.setBackgroundResource(R.drawable.deviceipad);
					// SetBackgroundResource_up(v);

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj);
					// v.setBackgroundColor(R.color.blackcolor);
					v.setBackgroundDrawable(null);
				}
				return false;
			}
		});

		m_listitem03.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				GenieDebug.error("onTouch ", "onTouch event.getAction() = "
						+ event.getAction());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj_dz);
					// v.setBackgroundColor(R.color.Black);
					// v.setBackgroundResource(R.drawable.deviceiphone);
					// SetBackgroundResource_down(v);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// v.setBackgroundResource(R.drawable.deviceipad);
					// SetBackgroundResource_up(v);

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj);
					// v.setBackgroundColor(R.color.blackcolor);
					v.setBackgroundDrawable(null);
				}
				return false;
			}
		});

		main_layout = (LinearLayout) findViewById(R.id.layout_02);
		main_layout.setVisibility(View.VISIBLE);

		TextView temp1 = (TextView) findViewById(R.id.list_tv_01);
		temp1.setText(getResources().getString(R.string.wirelessname));

		TextView temp3 = (TextView) findViewById(R.id.list_tv_03);
		temp3.setText(getResources().getString(R.string.channel));

		TextView temp5 = (TextView) findViewById(R.id.list_tv_05);
		temp5.setText(getResources().getString(R.string.networkkey));
	}

	public void initWirelessData() {
		GenieDebug.error("debug", "initWirelessData 0");
		if (null == Guestaccess_layout) {
			Guestaccess_layout = (LinearLayout) findViewById(R.id.LinearLayout_01);
		}
		if (null != Guestaccess_layout) {
			Guestaccess_layout.setVisibility(View.GONE);
		}

		if (null == main_layout) {
			main_layout = (LinearLayout) findViewById(R.id.layout_02);
		}
		main_layout.setVisibility(View.VISIBLE);

		TextView temp1 = (TextView) findViewById(R.id.list_tv_01);
		temp1.setText(getResources().getString(R.string.wirelessname));

		TextView temp3 = (TextView) findViewById(R.id.list_tv_03);
		temp3.setText(getResources().getString(R.string.channel));

		TextView temp5 = (TextView) findViewById(R.id.list_tv_05);
		temp5.setText(getResources().getString(R.string.networkkey));

		TextView temp2 = (TextView) findViewById(R.id.list_tv_02);
		temp2.setText(GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID));

		GenieDebug
				.error("debug",
						"initWirelessData DICTIONARY_KEY_WLAN_SSID = "
								+ GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID));

		TextView temp4 = (TextView) findViewById(R.id.list_tv_04);
		temp4.setText(GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL));

		GenieDebug
				.error("debug",
						"initWirelessData DICTIONARY_KEY_WLAN_CHANNEL = "
								+ GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL));

		TextView temp6 = (TextView) findViewById(R.id.list_tv_06);

		if (GenieSoap.dictionary.get(
				GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES)
				.equals("WEP")) {
			if (GenieSoap.dictionary.get(
					GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY).equals(
					"None")
					|| (GenieSoap.dictionary
							.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY)
							.equals(GenieGlobalDefines.NULL))) {
				temp6.setText("");
			} else {
				temp6.setText(GenieSoap.dictionary
						.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY));
				
			
			}

		} else {
			if (((GenieSoap.dictionary
					.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY))
					.equals("None"))
					|| (GenieSoap.dictionary
							.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY)
							.equals(GenieGlobalDefines.NULL))) {
				temp6.setText("");
			} else {
				temp6.setText(GenieSoap.dictionary
						.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY));
				
				
			
			}

		}
		
//		Toast.makeText(this, "WIRELESS:"+GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL)+";PASSWORD:"+temp6.getText().toString(), Toast.LENGTH_SHORT).show();
		
		// 增加二维码
		ImageView image_ewm = (ImageView) findViewById(R.id.image_ewm);
		try {
//			bitmap = Create2Code.Create2DCode("WIRELESS:Wireless001;PASSWORD:siteview");
			bitmap =Create2Code.Create2DCode("WIRELESS:"+temp2.getText().toString()+";PASSWORD:"+temp6.getText().toString());
			image_ewm.setImageBitmap(bitmap);
			image_ewm.setVisibility(View.VISIBLE);
			string_ewm.setVisibility(View.VISIBLE);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bitmap = null;
		
		
		
		GenieDebug
				.error("debug",
						"initWirelessData DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES = "
								+ GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES));
		GenieDebug
				.error("debug",
						"initWirelessData DICTIONARY_KEY_WLAN_WEP_KEY = "
								+ GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WEP_KEY));
		GenieDebug
				.error("debug",
						"initWirelessData DICTIONARY_KEY_WLAN_KEY = "
								+ GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY));

	}

	// public void initWirelessData()
	// {
	// GenieDebug.error("debug", "initWirelessData --0--");
	//
	// ListView list = (ListView) findViewById(R.id.ListView);
	//
	//
	// ArrayList<HashMap<String, Object>> listItem = new
	// ArrayList<HashMap<String, Object>>();
	//
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// map.put("ItemTitle", getResources().getString(R.string.wirelessname));
	// map.put("ItemText",
	// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_SSID));
	// listItem.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("ItemTitle", getResources().getString(R.string.channel));
	// map.put("ItemText",
	// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_CHANNEL));
	// listItem.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("ItemTitle", getResources().getString(R.string.networkkey));
	// map.put("ItemText",
	// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_KEY));
	// listItem.add(map);
	//
	// SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,
	// new String[] {"ItemTitle", "ItemText"},
	// new int[] {R.id.ItemTitle,R.id.ItemText});
	// list.setAdapter(listItemAdapter);
	// }

	private OnClickListener onGuestAccessclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onModifyMenu();
		}
	};

	public void initGuestAccessView() {
		Guestaccess_layout = (LinearLayout) findViewById(R.id.LinearLayout_01);
		Guestaccess_layout.setVisibility(View.VISIBLE);

		layout_ll_signal = (LinearLayout) findViewById(R.id.layout_ll_signal);
		layout_ll_link = (LinearLayout) findViewById(R.id.layout_ll_link);
		list_vv_signal = findViewById(R.id.list_vv_signal);
		list_vv_link = findViewById(R.id.list_vv_link);
		layout_ll_signal.setVisibility(View.GONE);
		layout_ll_link.setVisibility(View.GONE);
		list_vv_signal.setVisibility(View.GONE);
		list_vv_link.setVisibility(View.GONE);

		m_listitem01 = (RelativeLayout) findViewById(R.id.layout_listitem01);
		m_listitem02 = (RelativeLayout) findViewById(R.id.layout_listitem02);
		m_listitem03 = (RelativeLayout) findViewById(R.id.layout_listitem03);

		// m_listitem01.setVisibility(View.GONE);

		m_listitem01.setOnClickListener(onGuestAccessclick);
		m_listitem02.setOnClickListener(onGuestAccessclick);
		m_listitem03.setOnClickListener(onGuestAccessclick);

		m_listitem01.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// GenieDebug.error("onTouch ","onTouch event.getAction() = "+event.getAction());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj_dz);
					// v.setBackgroundColor(R.color.Black);
					// v.setBackgroundResource(R.drawable.deviceiphone);
					// SetBackgroundResource_down(v);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// v.setBackgroundResource(R.drawable.deviceipad);
					// SetBackgroundResource_up(v);

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj);
					v.setBackgroundDrawable(null);
				}
				return false;
			}
		});
		m_listitem02.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// GenieDebug.error("onTouch ","onTouch event.getAction() = "+event.getAction());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj_dz);
					// v.setBackgroundColor(R.color.Black);
					// v.setBackgroundResource(R.drawable.deviceiphone);
					// SetBackgroundResource_down(v);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// v.setBackgroundResource(R.drawable.deviceipad);
					// SetBackgroundResource_up(v);

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj);
					v.setBackgroundDrawable(null);
				}
				return false;
			}
		});

		m_listitem03.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// GenieDebug.error("onTouch ","onTouch event.getAction() = "+event.getAction());
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj_dz);
					// v.setBackgroundColor(R.color.Black);
					// v.setBackgroundResource(R.drawable.deviceiphone);
					// SetBackgroundResource_down(v);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// v.setBackgroundResource(R.drawable.deviceipad);
					// SetBackgroundResource_up(v);

					// LinearLayout temp =
					// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
					// v.setBackgroundResource(R.drawable.bj);
					v.setBackgroundDrawable(null);
				}
				return false;
			}
		});

		main_layout = (LinearLayout) findViewById(R.id.layout_02);
		main_layout.setVisibility(View.GONE);

		// controlBT = (ToggleButton) findViewById(R.id.titleBT);
		controlBT = (CheckBox) findViewById(R.id.titleBT);

		TextView temp1 = (TextView) findViewById(R.id.list_tv_01);
		// temp1.setText(getResources().getString(R.string.guestaccess));qicheng.ai
		// change
		// temp1.setVisibility(View.GONE);

		TextView temp2 = (TextView) findViewById(R.id.list_tv_02);
		// temp2.setVisibility(View.GONE);
		View tempv2 = (View) findViewById(R.id.list_vv_01);
		// /tempv2.setVisibility(View.GONE);

		TextView temp3 = (TextView) findViewById(R.id.list_tv_03);

		TextView temp5 = (TextView) findViewById(R.id.list_tv_05);

		temp1.setText(getResources().getString(R.string.wirelessname));
		temp3.setText(getResources().getString(R.string.networkkey));
		temp5.setText(getResources().getString(R.string.guestaccess_time));

		if (GenieRequest.m_SmartNetWork) {
			m_listitem03.setVisibility(View.GONE);
		} else {
			m_listitem03.setVisibility(View.VISIBLE);
		}
		
	
	}

	private String[] m_accesstime = new String[] { "Always", "1 hour",
			"5 hours", "10 hours", "1 day", "1 week" };

	public void initGuestAccessData() {
		if (null == Guestaccess_layout) {
			Guestaccess_layout = (LinearLayout) findViewById(R.id.LinearLayout_01);
		}
		if (null != Guestaccess_layout)
			Guestaccess_layout.setVisibility(View.VISIBLE);

		if (null == main_layout) {
			main_layout = (LinearLayout) findViewById(R.id.layout_02);
		}

		if (null == controlBT) {
			controlBT = (CheckBox) findViewById(R.id.titleBT);
		}

		if (GenieSoap.dictionary.get(
				GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1") > -1) {
			controlBT.setChecked(true);

			main_layout.setVisibility(View.VISIBLE);

			TextView temp1 = (TextView) findViewById(R.id.list_tv_01);
			TextView temp3 = (TextView) findViewById(R.id.list_tv_03);
			TextView temp5 = (TextView) findViewById(R.id.list_tv_05);

			temp1.setText(getResources().getString(R.string.wirelessname));
			temp3.setText(getResources().getString(R.string.networkkey));
			temp5.setText(getResources().getString(R.string.guestaccess_time));

			TextView temp2 = (TextView) findViewById(R.id.list_tv_02);
			// temp2.setText(getResources().getString(R.string.on)); qicheng.ai
			// change
			// temp2.setVisibility(View.GONE);

			temp2.setText(GenieSoap.dictionary
					.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID));
			TextView temp4 = (TextView) findViewById(R.id.list_tv_04);

			TextView temp6 = (TextView) findViewById(R.id.list_tv_06);
			GenieDebug
					.error("Accesskey",
							"99999 "
									+ GenieSoap.dictionary
											.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY));
			GenieDebug
					.error("Accesskey",
							"99999 "
									+ GenieSoap.dictionary
											.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE));

			if (GenieSoap.dictionary.get(
					GenieGlobalDefines.DICTIONARY_KEY_GUEST_MODE)
					.equals("None")) {
				temp4.setText("");
			} else {
				if (GenieSoap.dictionary.get(
						GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY).equals(
						GenieGlobalDefines.NULL)) {
					temp4.setText("");
				} else {
					temp4.setText(GenieSoap.dictionary
							.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY));
				}
			}
			//生成二维码
			if(!"".equals(temp2.getText().toString())){
				try {
//					bitmap = Create2Code.Create2DCode("WIRELESS:Wireless001;PASSWORD:siteview");
					bitmap =Create2Code.Create2DCode("WIRELESS:"+temp2.getText().toString()+";PASSWORD:"+temp4.getText().toString());
					image_ewm.setImageBitmap(bitmap);
					image_ewm.setVisibility(View.VISIBLE);
					string_ewm.setVisibility(View.VISIBLE);
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bitmap = null;
			}
			

			String guesttime = null;
			String guestMac = null;

			HashMap<String, String> accesstime = GenieSerializ.ReadMap(this,
					"guestaccess");

			if (accesstime != null) {
				guesttime = accesstime.get("GUESTTIME");
				guestMac = accesstime.get("GUESTTIMEMAC");
			}

			String wlan_mac = GenieSoap.dictionary
					.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
			int index = 0;

			for (int i = 0; i < m_accesstime.length; i++) {
				if (guesttime != null && guestMac != null) {
					if (guestMac.equals(wlan_mac)
							&& guesttime.equals(m_accesstime[i])) {
						index = i;
						break;
					}
				}
			}
			temp6.setText(m_accesstime[index]);

		} else {
			main_layout.setVisibility(View.GONE);
			controlBT.setChecked(false);
		}

		controlBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (GenieSoap.dictionary.get(
						GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf(
						"1") > -1) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							GenieListView.this)
							// .setIcon(R.drawable.icon)
							// .setTitle(" ")
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											onMenuid_100();
										}
									})
							.setMessage(R.string.wirelsssettin)
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											controlBT.setChecked(true);
										}
									});
					dialog.show();
				} else {
					Intent intent = new Intent();
					intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX,
							GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS);
					intent.setClass(GenieListView.this, GenieWifiModify.class);
					startActivityForResult(intent,
							GenieGlobalDefines.GUESTACCESS_REQUESTCODE);
					controlBT.setChecked(false);
				}
			}
		});
	}

	// public void initGuestAccessData()
	// {
	// GenieDebug.error("debug", "initGuestAccessData --0--");
	//
	// if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("2")
	// > -1)
	// {
	// soapCommand.support = false;
	// sendMessage2UI(GenieGlobalDefines.ESoapRequestnotsupport);
	// return ;
	// }
	//
	// ListView list = (ListView) findViewById(R.id.ListView);
	//
	//
	//
	// ArrayList<HashMap<String, Object>> listItem = new
	// ArrayList<HashMap<String, Object>>();
	//
	// HashMap<String, Object> map = null;
	//
	//
	//
	//
	//
	//
	// /**********************/
	// /**********************/
	//
	// if(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE).indexOf("1")
	// > -1)
	// {
	// map = new HashMap<String, Object>();
	// map.put("ItemTitle", getResources().getString(R.string.guestaccess));
	// map.put("ItemText", getResources().getString(R.string.on));
	// listItem.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("ItemTitle", getResources().getString(R.string.wirelessname));
	// map.put("ItemText",
	// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_SSID));
	// listItem.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("ItemTitle", getResources().getString(R.string.networkkey));
	// map.put("ItemText",
	// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_GUEST_KEY));
	// listItem.add(map);
	// }
	// else
	// {
	// map = new HashMap<String, Object>();
	// map.put("ItemTitle", getResources().getString(R.string.guestaccess));
	// map.put("ItemText", getResources().getString(R.string.off));
	// listItem.add(map);
	// }
	//
	// SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,
	// new String[] {"ItemTitle", "ItemText"},
	// new int[] {R.id.ItemTitle,R.id.ItemText});
	// list.setAdapter(listItemAdapter);
	//
	// }

	public void initAttachDevicesData() {
		GenieDebug.error("debug", "initAttachDevicesData --0--");

		ListView list = (ListView) findViewById(R.id.ListView);

		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = null;
		int count = GenieSoap.routerMap.size();

		for (int i = 0; i < count; i++) {
			map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.devicepc);
			map.put("ItemText",
					GenieSoap.routerMap.get(i).get(
							GenieGlobalDefines.DEVICE_NAME));
			listItem.add(map);
		}

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.mapitem,//
				new String[] { "ItemImage", "ItemText" }, new int[] {
						R.id.ItemImage, R.id.ItemText });
		list.setAdapter(listItemAdapter);
	}

	// public void initTrafficData()
	// {
	// GenieDebug.error("debug", "initAttachDevicesData --0--");
	//
	// ListView list = (ListView) findViewById(R.id.ListView);
	//
	//
	//
	// ArrayList<HashMap<String, Object>> listItem = new
	// ArrayList<HashMap<String, Object>>();
	//
	// HashMap<String, Object> map = null;
	// map = new HashMap<String, Object>();
	//
	// map.put("ItemPeriod",getResources().getString(R.string.Period));
	// map.put("ItemTime",getResources().getString(R.string.Time));
	// map.put("ItemUpload",getResources().getString(R.string.Upload));
	// map.put("ItemDownload",getResources().getString(R.string.Download));
	// map.put("ItemTotal", getResources().getString(R.string.Total));
	// listItem.add(map);
	//
	//
	// StringBuffer temp = new
	// StringBuffer(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC));
	//
	// GenieDebug.error("debug",temp.toString());
	//
	// int posS = 0;
	// int posE = 0;
	// for(int i = 0;i<5;i++)
	// {
	// map = new HashMap<String, Object>();
	//
	// map.put("ItemPeriod",traffic_str[i]);
	//
	// posE = temp.indexOf("#");
	// GenieDebug.error("debug",temp.substring(0, posE));
	// map.put("ItemTime",temp.substring(0, posE));
	// temp.replace(0, posE + 1, "");
	//
	// posE = temp.indexOf("#");
	// GenieDebug.error("debug",temp.substring(0, posE));
	// map.put("ItemUpload",temp.substring(0, posE));
	// temp.replace(0, posE + 1, "");
	//
	// posE = temp.indexOf("#");
	// GenieDebug.error("debug",temp.substring(0, posE));
	// map.put("ItemDownload",temp.substring(0, posE));
	// temp.replace(0, posE + 1, "");
	//
	// map.put("ItemTotal", "0");
	// listItem.add(map);
	// }
	//
	//
	//
	//
	// SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,
	// new String[]
	// {"ItemPeriod","ItemTime","ItemUpload","ItemDownload","ItemTotal"},
	// new int[]
	// {R.id.ItemPeriod,R.id.ItemTime,R.id.ItemUpload,R.id.ItemDownload,R.id.ItemTotal});
	// list.setAdapter(listItemAdapter);
	// }

	public void parsestr(char array[]) {
		int j = 0, flag = 0;
		for (int i = 0; i < array.length; i++) {
			if (flag == 0 && array[i] == ',') {
				flag = 1;
				continue;
			}
			if (flag == 1) {
				array[j] = array[i];
			}
			j++;
		}
		if (flag == 1) {
			array[j] = 0;
		}
	}

	private void DisableTrafficView() {
		// qicheng.ai add
		if (mModify != null)
			mModify.setVisible(false);
		LinearLayout setting = (LinearLayout) findViewById(R.id.layout_traffic_setting);
		LinearLayout today = (LinearLayout) findViewById(R.id.layout_today);
		LinearLayout yesterday = (LinearLayout) findViewById(R.id.layout_yesterday);
		LinearLayout week = (LinearLayout) findViewById(R.id.layout_week);
		LinearLayout month = (LinearLayout) findViewById(R.id.layout_month);
		LinearLayout lastmonth = (LinearLayout) findViewById(R.id.layout_lastmonth);

		setting.setVisibility(View.GONE);
		today.setVisibility(View.GONE);
		yesterday.setVisibility(View.GONE);
		week.setVisibility(View.GONE);
		month.setVisibility(View.GONE);
		lastmonth.setVisibility(View.GONE);
	}

	private void EnableTrafficView() {
		// qicheng.ai add
		if (mModify != null)
			mModify.setVisible(true);
		LinearLayout setting = (LinearLayout) findViewById(R.id.layout_traffic_setting);
		LinearLayout today = (LinearLayout) findViewById(R.id.layout_today);
		LinearLayout yesterday = (LinearLayout) findViewById(R.id.layout_yesterday);
		LinearLayout week = (LinearLayout) findViewById(R.id.layout_week);
		LinearLayout month = (LinearLayout) findViewById(R.id.layout_month);
		LinearLayout lastmonth = (LinearLayout) findViewById(R.id.layout_lastmonth);

		setting.setVisibility(View.VISIBLE);
		today.setVisibility(View.VISIBLE);
		yesterday.setVisibility(View.VISIBLE);
		week.setVisibility(View.VISIBLE);
		month.setVisibility(View.VISIBLE);
		lastmonth.setVisibility(View.VISIBLE);
	}

	private void TrafficMeterSetEnableSender() {

		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE_SET, "1");

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieListView";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigTrafficEnable;
		request0.aServer = "DeviceConfig";
		request0.aMethod = "EnableTrafficMeter";
		request0.aNeedwrap = true;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = false;
		request0.aTimeout = 25000;
		request0.aElement = new ArrayList<String>();
		request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE);
		request0.aElement.add(GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE_SET));
		requestinfo.add(request0);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.traffic) + " "
				+ getResources().getString(R.string.info);

		m_GetGuestAcessInfo = new GenieRequest(this, requestinfo);
		m_GetGuestAcessInfo.SetProgressInfo(true, true);
		m_GetGuestAcessInfo.SetProgressText(title, null);
		m_GetGuestAcessInfo.Start();

	}

	// private Boolean TrafficMeterSetDisableSender()
	// {
	// Boolean status = false;
	//
	// GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE_SET,
	// "0");
	//
	//
	// //soapCommand.configurationstarted();
	// status = soapCommand.setEnableTrafficMeter();
	// //soapCommand.configurationfinished();
	//
	// return status;
	//
	// }

	private void TrafficMeterSetDisableSender() {

		GenieSoap.dictionary.put(
				GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE_SET, "0");

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		GenieRequestInfo request0 = new GenieRequestInfo();
		request0.aRequestLable = "GenieListView";
		request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigTrafficEnable;
		request0.aServer = "DeviceConfig";
		request0.aMethod = "EnableTrafficMeter";
		request0.aNeedwrap = true;
		request0.aRequestType = GenieGlobalDefines.RequestActionType.Soap;
		request0.aNeedParser = false;
		request0.aTimeout = 25000;
		request0.aElement = new ArrayList<String>();
		request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE);
		request0.aElement.add(GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE_SET));
		requestinfo.add(request0);

		String title = getResources().getString(R.string.loading) + " "
				+ getResources().getString(R.string.traffic) + " "
				+ getResources().getString(R.string.info);

		m_GetGuestAcessInfo = new GenieRequest(this, requestinfo);
		m_GetGuestAcessInfo.SetProgressInfo(true, true);
		m_GetGuestAcessInfo.SetProgressText(title, null);
		m_GetGuestAcessInfo.Start();

	}

	private void DisableTrafficMeter() {
		GenieDebug.error("debug", "DisableTrafficMeter --0--");

		TrafficMeterSetDisableSender();

		// sendMessage2UI(SHOWPROGRESSDIALOG);
		//
		// GenieDebug.error("debug", "DisableTrafficMeter --1--");
		// cancelThread();
		// m_Thread = new Thread()
		// {
		// public void run()
		// {
		// TrafficMeterSetDisableSender();
		//
		//
		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// getModelInfo();
		// }
		// },1500);
		//
		//
		//
		// }
		// };
		// m_Thread.start();
	}

	private void EnableTrafficMeter() {
		GenieDebug.error("debug", "DisableTrafficMeter --0--");

		TrafficMeterSetEnableSender();

		// if(null != progressDialog)
		// {
		// progressDialog.dismiss();
		// progressDialog = null;
		// }
		//
		// progressDialog = ProgressDialog.show(GenieListView.this,
		// "Loading...", "Please wait...", true, true);
		//
		// sendMessage2UI(SHOWPROGRESSDIALOG);
		//
		// GenieDebug.error("debug", "DisableTrafficMeter --1--");
		// cancelThread();
		// m_Thread = new Thread()
		// {
		// public void run()
		// {
		// TrafficMeterSetEnableSender();
		//
		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// getModelInfo();
		// }
		// },1500);
		// // runOnUiThread(new Runnable() {
		// //
		// // @Override
		// // public void run() {
		// // // TODO Auto-generated method stub
		// // getModelInfo();
		// // }
		// // });
		// }
		// };
		// m_Thread.start();
	}

	private void OnChangTrafficStates() {

		GenieDebug.error("OnChangTrafficStates", "OnChangTrafficStates = ");

		// String able =
		// GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE);

		GenieDebug.error("OnChangTrafficStates", "trafficstates = "
				+ trafficstates);

		AlertDialog.Builder dialog_Chang = new AlertDialog.Builder(this);

		dialog_Chang.setTitle(R.string.traffic);
		dialog_Chang.setIcon(R.drawable.icon);

		if (trafficstates) {
			dialog_Chang.setMessage(R.string.traffic_disable_text);
		} else {
			dialog_Chang.setMessage(R.string.traffic_able_text);
		}

		dialog_Chang.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (trafficstates) {
							DisableTrafficMeter();
						} else {
							EnableTrafficMeter();
						}
					}
				});
		dialog_Chang.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						initTrafficData();
					}
				});
		dialog_Chang.show();
	}

	private OnTouchListener TrafficSettingclick = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			// GenieDebug.error("onTouch ","onTouch event.getAction() = "+event.getAction());
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				// LinearLayout temp =
				// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
				// v.setBackgroundResource(R.drawable.bj_dz);
				// v.setBackgroundColor(R.color.Black);
				// v.setBackgroundResource(R.drawable.deviceiphone);
				// SetBackgroundResource_down(v);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// v.setBackgroundResource(R.drawable.deviceipad);
				// SetBackgroundResource_up(v);

				// LinearLayout temp =
				// (LinearLayout)findViewById((LinearLayout_BaseID+v.getId()));
				// v.setBackgroundResource(R.drawable.bj);
				v.setBackgroundDrawable(null);
			}
			return false;
		}
	};

	private OnClickListener onTrafficSettingclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onModifyMenu();
		}
	};

	// qicheng.ai
	public void initTrafficSetting() {
		TextView temp = null;
		temp = (TextView) findViewById(R.id.setting_traffic_2);

		String str = GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL);

		// str.equalsIgnoreCase(string)

		for (int i = 0; i < m_control.length; i++) {
			if (str.equalsIgnoreCase(m_control[i])) {
				temp.setText(m_control2[i]);
				break;
			}
		}

		// temp.setText(GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_CONTROL));

		temp = (TextView) findViewById(R.id.setting_traffic_4);
		temp.setText(GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_LIMIT));

		temp = (TextView) findViewById(R.id.setting_traffic_6);
		temp.setText(GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_HOUR)
				+ " : "
				+ GenieSoap.dictionary
						.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_MINUTE));

		temp = (TextView) findViewById(R.id.setting_traffic_8);
		temp.setText(GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_DAY));

		m_listitem01 = (RelativeLayout) findViewById(R.id.setting_item01);
		m_listitem02 = (RelativeLayout) findViewById(R.id.setting_item02);
		m_listitem03 = (RelativeLayout) findViewById(R.id.setting_item03);
		m_listitem04 = (RelativeLayout) findViewById(R.id.setting_item04);

		m_listitem01.setOnTouchListener(TrafficSettingclick);
		m_listitem02.setOnTouchListener(TrafficSettingclick);
		m_listitem03.setOnTouchListener(TrafficSettingclick);
		m_listitem04.setOnTouchListener(TrafficSettingclick);

		m_listitem01.setOnClickListener(onTrafficSettingclick);
		m_listitem02.setOnClickListener(onTrafficSettingclick);
		m_listitem03.setOnClickListener(onTrafficSettingclick);
		m_listitem04.setOnClickListener(onTrafficSettingclick);

	}

	private static int testint = 0;

	private void settestdate(List data) {
		testint++;

		if (testint % 3 == 0) {
			TrafficMeterObject tem = (TrafficMeterObject) data.get(0);
			tem.setDownLoad("586.48    ");
			tem.setDownLoadAvg("");
			tem.setDownLoadMax("");
			tem.setUpLoad("123");
			tem.setUpLoadAvg("");
			tem.setUpLoadMax("");

			tem = (TrafficMeterObject) data.get(1);
			tem.setDownLoad("12345");
			tem.setDownLoadAvg("");
			tem.setDownLoadMax("");
			tem.setUpLoad("6789 ");
			tem.setUpLoadAvg("");
			tem.setUpLoadMax("");

			tem = (TrafficMeterObject) data.get(2);
			tem.setDownLoad("2,711/677.76");
			tem.setDownLoadAvg("677.76");
			tem.setDownLoadMax("2711");
			tem.setUpLoad("372.94/93.24");
			tem.setUpLoadAvg("93.24");
			tem.setUpLoadMax("372.94");

			tem = (TrafficMeterObject) data.get(3);
			tem.setDownLoad("2,711/ 677.76");
			tem.setDownLoadAvg("677.76");
			tem.setDownLoadMax("2711");
			tem.setUpLoad("372.94/93.24");
			tem.setUpLoadAvg("93.24");
			tem.setUpLoadMax("372.94");

			tem = (TrafficMeterObject) data.get(4);
			tem.setDownLoad("152,711/315677.76");
			tem.setDownLoadAvg("315677.76");
			tem.setDownLoadMax("152711");
			tem.setUpLoad("878,372.94/2593.24");
			tem.setUpLoadAvg("2593.24");
			tem.setUpLoadMax("878372.94");

		} else if (testint % 3 == 1) {
			TrafficMeterObject tem = (TrafficMeterObject) data.get(0);
			tem.setDownLoad("186.10");
			tem.setDownLoadAvg("");
			tem.setDownLoadMax("");
			tem.setUpLoad("123.00");
			tem.setUpLoadAvg("");
			tem.setUpLoadMax("");

			tem = (TrafficMeterObject) data.get(1);
			tem.setDownLoad("145.01");
			tem.setDownLoadAvg("");
			tem.setDownLoadMax("");
			tem.setUpLoad("69.10");
			tem.setUpLoadAvg("");
			tem.setUpLoadMax("");

			tem = (TrafficMeterObject) data.get(2);
			tem.setDownLoad("2,701/67.76");
			tem.setDownLoadAvg("67.76");
			tem.setDownLoadMax("2701");
			tem.setUpLoad("10372.94/93.24");
			tem.setUpLoadAvg("93.24");
			tem.setUpLoadMax("10372.94");

			tem = (TrafficMeterObject) data.get(3);
			tem.setDownLoad("2,7211/6771.76");
			tem.setDownLoadAvg("6771.76");
			tem.setDownLoadMax("27211");
			tem.setUpLoad("372.94/193.24");
			tem.setUpLoadAvg("193.24");
			tem.setUpLoadMax("372.94");

			tem = (TrafficMeterObject) data.get(4);
			tem.setDownLoad("711/77.76");
			tem.setDownLoadAvg("77.76");
			tem.setDownLoadMax("711");
			tem.setUpLoad("8,372.94/293.24");
			tem.setUpLoadAvg("293.24");
			tem.setUpLoadMax("8372.94");
		} else if (testint % 3 == 2) {
			TrafficMeterObject tem = (TrafficMeterObject) data.get(0);
			tem.setDownLoad("56.48");
			tem.setDownLoadAvg("");
			tem.setDownLoadMax("");
			tem.setUpLoad("12");
			tem.setUpLoadAvg("");
			tem.setUpLoadMax("");

			tem = (TrafficMeterObject) data.get(1);
			tem.setDownLoad("15");
			tem.setDownLoadAvg("");
			tem.setDownLoadMax("");
			tem.setUpLoad("69");
			tem.setUpLoadAvg("");
			tem.setUpLoadMax("");

			tem = (TrafficMeterObject) data.get(2);
			tem.setDownLoad("99/77.76");
			tem.setDownLoadAvg("77.76");
			tem.setDownLoadMax("99");
			tem.setUpLoad("45/69");
			tem.setUpLoadAvg("69");
			tem.setUpLoadMax("45");

			tem = (TrafficMeterObject) data.get(3);
			tem.setDownLoad("87/77.76");
			tem.setDownLoadAvg("77.76");
			tem.setDownLoadMax("87");
			tem.setUpLoad("93.94/80.24");
			tem.setUpLoadAvg("80.24");
			tem.setUpLoadMax("93.94");

			tem = (TrafficMeterObject) data.get(4);
			tem.setDownLoad("51/45.76");
			tem.setDownLoadAvg("45.76");
			tem.setDownLoadMax("51");
			tem.setUpLoad("72.94/59.24");
			tem.setUpLoadAvg("59.24");
			tem.setUpLoadMax("72.94");
		}

	}

	public void initTrafficData() {
		// toggleButton = (ToggleButton)findViewById(R.id.ToggleButton01);
		// toggleButton = (CheckBox) findViewById(R.id.ToggleButton01);

		if (toggleButton == null) {
			toggleButton = (CheckBox) findViewById(R.id.ToggleButton01);
			if (toggleButton == null) {
				return;
			}
		}

		String able = GenieSoap.dictionary
				.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC_ABLE);

		trafficstates = false;
		GenieDebug.error("initTrafficData", "able = " + able);

		// toggleButton.setChecked(!trafficstates);

		// toggleButton.setOnCheckedChangeListener(new
		// OnCheckedChangeListener(){
		// public void onCheckedChanged(CompoundButton buttonView,boolean
		// isChecked)
		// {
		// GenieDebug.error("initTrafficData"," onCheckedChanged 5555 ");
		// //toggleButton.setChecked(isChecked);
		// OnChangTrafficStates();
		// }
		// });

		toggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				OnChangTrafficStates();
			}
		});

		if (null != able && able.equals("0")) {
			GenieDebug.error("initTrafficData", " onCheckedChanged 0000 ");
			trafficstates = false;
			// toggleButton.setChecked(true);
			toggleButton.setChecked(false);
			DisableTrafficView();
			return;
		}

		if (null != able && able.equals("2")) {
			GenieDebug.error("initTrafficData", " onCheckedChanged 1111 ");
			trafficstates = false;
			toggleButton.setEnabled(false);
			toggleButton.setClickable(false);
			DisableTrafficView();
			sendMessage2UI(GenieGlobalDefines.ESoapRequestnotsupport);
			return;
		}

		if (null != able && able.equals("1")) {
			GenieDebug.error("initTrafficData", " onCheckedChanged 2222 ");
			trafficstates = true;
			toggleButton.setChecked(true);
		} else {
			GenieDebug.error("initTrafficData", " onCheckedChanged 3333 ");
			trafficstates = false;
			toggleButton.setChecked(false);
			DisableTrafficView();
			return;
		}

		GenieDebug.error("initTrafficData", " onCheckedChanged 4444 ");
		toggleButton.setChecked(true);

		EnableTrafficView();

		initTrafficSetting(); // qicheng.ai add

		TextView temp = null;

		StringBuffer temp_str = new StringBuffer(
				GenieSoap.dictionary
						.get(GenieGlobalDefines.DICTIONARY_KEY_TRAFFIC));

		if (temp_str == null || temp_str.length() == 0) {
			return;
		}
		List data = getValue(temp_str.toString());

		// settestdate(data);
		//
		// int n = data.size();
		//
		// for(int i= 0;i<n;i++)
		// {
		// TrafficMeterObject tem = (TrafficMeterObject)data.get(i);
		// GenieDebug.error("debug", "111222 getTime = "+tem.getTime());
		// GenieDebug.error("debug",
		// "111222 getDownLoadMax = "+tem.getDownLoadMax());
		// GenieDebug.error("debug", "111222 getDownLoad = "+tem.getDownLoad());
		// GenieDebug.error("debug",
		// "111222 getDownLoadAvg = "+tem.getDownLoadAvg());
		// GenieDebug.error("debug",
		// "111222 getDownLoadMax = "+tem.getDownLoadMax());
		// GenieDebug.error("debug", "111222 getUpLoad = "+tem.getUpLoad());
		// GenieDebug.error("debug",
		// "111222 getUpLoadAvg = "+tem.getUpLoadAvg());
		// GenieDebug.error("debug",
		// "111222 getUpLoadMax = "+tem.getUpLoadMax());
		//
		// }

		histogram = (HistogramView) findViewById(R.id.histogramView1);
		histogram.setXAxisLable(getTotalTrafficmeter(data));// getDayTrafficmeter(data)
		if (max > max1) {
			histogram.setYAxisLable(max, 5,
					getResources().getString(R.string.s_mbytes));
		} else {
			histogram.setYAxisLable(max1, 5,
					getResources().getString(R.string.s_mbytes));
		}
		// histogram.setColumnarShape(true);
		histogram.setTitleText(getResources().getString(R.string.s_total));
		histogram.setColorNoteText(getResources()
				.getString(R.string.s_download),
				getResources().getString(R.string.s_upload));
		histogram.setTitleNameSize(18);
		histogram.setTitleNamePaintColor(Color.BLACK);
		// histogram.setDownLoadColumnarPaintColor(Color.BLUE);
		histogram.setUPLoadColumnarPaintColor(Color.GRAY);

		histogram1 = (HistogramView) findViewById(R.id.histogramView2);
		histogram1.setXAxisLable(getAVGTrafficmeter(data));
		histogram1.setYAxisLable(max2, 5,
				getResources().getString(R.string.s_mbytes));

		// histogram1.setColumnarShape(true);
		histogram1.setTitleText(getResources().getString(R.string.s_average));
		histogram1.setColorNoteText(
				getResources().getString(R.string.s_download), getResources()
						.getString(R.string.s_upload));
		histogram1.setTitleNameSize(18);
		histogram1.setTitleNamePaintColor(Color.BLACK);
		// histogram1.setDownLoadColumnarPaintColor(Color.BLUE);
		histogram1.setUPLoadColumnarPaintColor(Color.GRAY);

	}

	private float max, max1, max2;
	private HistogramView histogram = null, histogram1 = null,
			histogram2 = null;

	public List getDayTrafficmeter(List data) {
		List week = new ArrayList();
		for (int i = 0; i < data.size(); i++) {
			TrafficMeterObject trafficMeter = (TrafficMeterObject) data.get(i);
			// System.out.println("Time: "+i+"  "+trafficMeter.getTime());
			// System.out.println("UpLoad: "+i+"  "+trafficMeter.getUpLoad());
			// System.out.println("DownLoad: "+i+"  "+trafficMeter.getDownLoad());
			// System.out.println("DownLoadAvg: "+i+"  "+trafficMeter.getDownLoadAvg());
			// System.out.println("DownLoadMax: "+i+"  "+trafficMeter.getDownLoadMax());
			// System.out.println("UpLoadAvg: "+i+"  "+trafficMeter.getUpLoadAvg());
			// System.out.println("UpLoadMax: "+i+"  "+trafficMeter.getUpLoadMax());

			if (i == 0) {
				double dowanLoad = Double.valueOf(trafficMeter.getDownLoad()
						.trim());
				double upLoad = Double.valueOf(trafficMeter.getUpLoad().trim());
				XAxisLable lable = new XAxisLable(getResources().getString(
						R.string.today), dowanLoad, upLoad);
				week.add(lable);
				max1 = (float) (dowanLoad + upLoad);
			}
			if (i == 1) {
				double dowanLoad = Double.valueOf(trafficMeter.getDownLoad()
						.trim());
				double upLoad = Double.valueOf(trafficMeter.getUpLoad().trim());
				XAxisLable lable = new XAxisLable(getResources().getString(
						R.string.yesterday), dowanLoad, upLoad);
				week.add(lable);
				max = (float) (dowanLoad + upLoad);
			}
			// if(i == 2){
			// double dowanLoad =
			// Double.valueOf(trafficMeter.getDownLoadMax().trim());
			// double upLoad =
			// Double.valueOf(trafficMeter.getUpLoadMax().trim());
			// XAxisLable lable = new XAxisLable("This Week",dowanLoad, upLoad);
			// week.add(lable);
			// max = (float)(dowanLoad+upLoad+100);
			// }

			if (i > 1) {
				break;
			}

		}
		return week;
	}

	public double getFloat(double data) {
		data = new Float(new DecimalFormat("0.00").format(data)).floatValue();
		return data;
	}

	public List getTotalTrafficmeter(List data) {

		if (data == null)
			return null;
		try {
			List week = new ArrayList();
			for (int i = 0; i < data.size(); i++) {
				TrafficMeterObject trafficMeter = (TrafficMeterObject) data
						.get(i);
				// System.out.println("Time: "+i+"  "+trafficMeter.getTime());
				// System.out.println("UpLoad: "+i+"  "+trafficMeter.getUpLoad());
				// System.out.println("DownLoad: "+i+"  "+trafficMeter.getDownLoad());
				// System.out.println("DownLoadAvg: "+i+"  "+trafficMeter.getDownLoadAvg());
				// System.out.println("DownLoadMax: "+i+"  "+trafficMeter.getDownLoadMax());
				// System.out.println("UpLoadAvg: "+i+"  "+trafficMeter.getUpLoadAvg());
				// System.out.println("UpLoadMax: "+i+"  "+trafficMeter.getUpLoadMax());

				GenieDebug.error("debug", "111222 i=" + i);
				GenieDebug.error("debug",
						"111222 getTime = " + trafficMeter.getTime());
				GenieDebug.error("debug", "111222 getDownLoadMax = "
						+ trafficMeter.getDownLoadMax());
				GenieDebug.error("debug", "111222 getDownLoad = "
						+ trafficMeter.getDownLoad());
				GenieDebug.error("debug", "111222 getDownLoadAvg = "
						+ trafficMeter.getDownLoadAvg());
				GenieDebug.error("debug", "111222 getDownLoadMax = "
						+ trafficMeter.getDownLoadMax());
				GenieDebug.error("debug",
						"111222 getUpLoad = " + trafficMeter.getUpLoad());
				GenieDebug.error("debug", "111222 getUpLoadAvg = "
						+ trafficMeter.getUpLoadAvg());
				GenieDebug.error("debug", "111222 getUpLoadMax = "
						+ trafficMeter.getUpLoadMax());

				if (i == 0) {
					double dowanLoad = Double.valueOf(trafficMeter
							.getDownLoad().trim());
					double upLoad = Double.valueOf(trafficMeter.getUpLoad()
							.trim());
					XAxisLable lable = new XAxisLable(getResources().getString(
							R.string.today), dowanLoad, upLoad);
					week.add(lable);

					GenieDebug.error("debug", "111222 i = " + i);
					GenieDebug
							.error("debug", "111222 dowanLoad = " + dowanLoad);
					GenieDebug.error("debug", "111222 dowanLoad = " + upLoad);

					max1 = (float) (dowanLoad + upLoad);
				}
				if (i == 1) {
					double dowanLoad = Double.valueOf(trafficMeter
							.getDownLoad().trim());
					double upLoad = Double.valueOf(trafficMeter.getUpLoad()
							.trim());
					XAxisLable lable = new XAxisLable(getResources().getString(
							R.string.yesterday), dowanLoad, upLoad);
					week.add(lable);

					GenieDebug.error("debug", "111222 i = " + i);
					GenieDebug
							.error("debug", "111222 dowanLoad = " + dowanLoad);
					GenieDebug.error("debug", "111222 dowanLoad = " + upLoad);

					max = (float) (dowanLoad + upLoad);
				}
				if (i == 2) {
					double dowanLoad = Double.valueOf(trafficMeter
							.getDownLoadMax().trim());
					double upLoad = Double.valueOf(trafficMeter.getUpLoadMax()
							.trim());
					XAxisLable lable = new XAxisLable(getResources().getString(
							R.string.this_week), dowanLoad, upLoad);
					week.add(lable);
				}
				if (i == 3) {
					double dowanLoad = Double.valueOf(trafficMeter
							.getDownLoadMax().trim());
					double upLoad = Double.valueOf(trafficMeter.getUpLoadMax()
							.trim());
					XAxisLable lable = new XAxisLable(getResources().getString(
							R.string.this_month), dowanLoad, upLoad);
					week.add(lable);
					max1 = (float) (dowanLoad + upLoad);
				}
				if (i == 4) {
					double dowanLoad = Double.valueOf(trafficMeter
							.getDownLoadMax().trim());
					double upLoad = Double.valueOf(trafficMeter.getUpLoadMax()
							.trim());
					XAxisLable lable = new XAxisLable(getResources().getString(
							R.string.last_month), dowanLoad, upLoad);
					week.add(lable);

					GenieDebug.error("debug", "111222 i = " + i);
					GenieDebug
							.error("debug", "111222 dowanLoad = " + dowanLoad);
					GenieDebug.error("debug", "111222 dowanLoad = " + upLoad);

					max = (float) (dowanLoad + upLoad);
				}

			}
			return week;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List getAVGTrafficmeter(List data) {

		if (data == null)
			return null;
		try {
			List week = new ArrayList();
			for (int i = 0; i < data.size(); i++) {
				TrafficMeterObject trafficMeter = (TrafficMeterObject) data
						.get(i);
				// System.out.println("Time: " + i + "  " +
				// trafficMeter.getTime());
				// System.out
				// .println("UpLoad: " + i + "  " + trafficMeter.getUpLoad());
				// System.out.println("DownLoad: " + i + "  "
				// + trafficMeter.getDownLoad());
				// System.out.println("DownLoadAvg: " + i + "  "
				// + trafficMeter.getDownLoadAvg());
				// System.out.println("DownLoadMax: " + i + "  "
				// + trafficMeter.getDownLoadMax());
				// System.out.println("UpLoadAvg: " + i + "  "
				// + trafficMeter.getUpLoadAvg());
				// System.out.println("UpLoadMax: " + i + "  "
				// + trafficMeter.getUpLoadMax());

				// if(i == 0){
				// double dowanLoad =
				// Double.valueOf(trafficMeter.getDownLoad().trim());
				// double upLoad =
				// Double.valueOf(trafficMeter.getUpLoad().trim());
				// XAxisLable lable = new XAxisLable("Today",dowanLoad, upLoad);
				// week.add(lable);
				// max1 = (float)(dowanLoad+upLoad+100);
				// }
				// if(i == 1){
				// double dowanLoad =
				// Double.valueOf(trafficMeter.getDownLoad().trim());
				// double upLoad =
				// Double.valueOf(trafficMeter.getUpLoad().trim());
				// XAxisLable lable = new XAxisLable("Yesterday",dowanLoad,
				// upLoad);
				// week.add(lable);
				// max = (float)(dowanLoad+upLoad+100);
				// }
				if (i == 2) {
					double dowanLoad = Double.valueOf(trafficMeter
							.getDownLoadAvg().trim());
					double upLoad = Double.valueOf(trafficMeter.getUpLoadAvg()
							.trim());
					XAxisLable lable = new XAxisLable(getResources().getString(
							R.string.this_week), dowanLoad, upLoad);
					max2 = (float) (dowanLoad + upLoad);

					GenieDebug.error("debug", "111222 i = " + i);
					GenieDebug
							.error("debug", "111222 dowanLoad = " + dowanLoad);
					GenieDebug.error("debug", "111222 dowanLoad = " + upLoad);

					week.add(lable);
				}
				if (i == 3) {
					double dowanLoad = Double.valueOf(trafficMeter
							.getDownLoadAvg().trim());
					double upLoad = Double.valueOf(trafficMeter.getUpLoadAvg()
							.trim());
					XAxisLable lable = new XAxisLable(getResources().getString(
							R.string.this_month), dowanLoad, upLoad);
					week.add(lable);
					max1 = (float) (dowanLoad + upLoad);
					if (max2 < max1) {
						max2 = max1;
					}
				}
				if (i == 4) {
					double dowanLoad = Double.valueOf(trafficMeter
							.getDownLoadAvg().trim());
					double upLoad = Double.valueOf(trafficMeter.getUpLoadAvg()
							.trim());
					XAxisLable lable = new XAxisLable(getResources().getString(
							R.string.last_month), dowanLoad, upLoad);
					week.add(lable);
					max = (float) (dowanLoad + upLoad);
					if (max2 < max) {
						max2 = max;
					}
				}

			}
			return week;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List getValue(String str) {
		List data = new ArrayList();
		int str1 = -1;
		String str11 = null;
		String str22;
		String value = null;
		int str2 = -1;

		str11 = getInteger(str);

		if (null == str11)
			return null;

		TrafficMeterObject trafficMeter = null;
		for (int i = 0; i < 5; i++) {
			trafficMeter = new TrafficMeterObject();
			for (int j = 0; j < 3; j++) {

				if ((!str.equals("") || str != null)) {
					str1 = str11.indexOf("\n");
					if (str1 != -1) {
						// System.out.println("get index : "+str1);
						str22 = str11.substring(str1 + 1);
						value = str11.substring(0, str1);
						str11 = str22;
						// System.out.println("Value: "+value);
						str2 = value.indexOf("/");
						if (str2 != -1) {
							String max = value.substring(0, str2);
							String avg = value.substring(str2 + 1,
									value.length());
							max = getInteger(max.trim());
							avg = getInteger(avg.trim());
							// System.out.println("MAX: "+max+" AVG: "+avg);
							if (j == 1 && max != null && avg != null) {
								trafficMeter.setUpLoadMax(max);
								trafficMeter.setUpLoadAvg(avg);
							}
							if (j == 2 && max != null && avg != null) {
								trafficMeter.setDownLoadMax(max);
								trafficMeter.setDownLoadAvg(avg);
							}
						}
						if (j == 0) {
							trafficMeter.setTime(value.trim());
						} else if (j == 1) {
							trafficMeter.setUpLoad(value.trim());
						} else if (j == 2) {
							trafficMeter.setDownLoad(value.trim());
						}

					}

				}

			}
			data.add(trafficMeter);
		}
		return data;
	}

	public String getInteger(String str) {
		if (str != null) {
			StringBuffer sb = new StringBuffer();
			int index = str.indexOf(",");
			if (index != -1) {
				sb.append(str.subSequence(0, index));
				sb.append(str.subSequence(index + 1, str.length()));
				str = sb.toString();
				if (str.indexOf(",") != -1) {
					return getInteger(str);
				} else {
					return str;
				}
			} else {
				return str;
			}
		} else {
			return null;
		}

	}

	/*
	 * public void initTrafficData() { GenieDebug.error("debug",
	 * "initAttachDevicesData --0--");
	 * 
	 * TextView temp = null; temp = (TextView) findViewById(R.id.tabletv_1);
	 * temp.setText(getResources().getString(R.string.Period)); temp = null;
	 * 
	 * temp = (TextView) findViewById(R.id.tabletv_2);
	 * temp.setText(getResources().getString(R.string.Time)); temp = null;
	 * 
	 * temp = (TextView) findViewById(R.id.tabletv_3);
	 * temp.setText(getResources().getString(R.string.Upload)); temp = null;
	 * 
	 * temp = (TextView) findViewById(R.id.tabletv_4);
	 * temp.setText(getResources().getString(R.string.Download)); temp = null;
	 * 
	 * temp = (TextView) findViewById(R.id.tabletv_5);
	 * temp.setText(getResources().getString(R.string.Total)); temp = null;
	 * 
	 * 
	 * 
	 * 
	 * StringBuffer temp_str = new
	 * StringBuffer(GenieSoap.dictionary.get(GenieGlobalDefines
	 * .DICTIONARY_KEY_TRAFFIC));
	 * 
	 * if(temp_str == null || temp_str.length() == 0) { return ; }
	 * GenieDebug.error("debug",""+traffic_id.length);
	 * 
	 * int posS = 0; int posE = 0; int pos = 0; float temp1 = 0,temp2 = 0,temp3
	 * = 0,temp4 = 0,result1 = 0,result2 = 0; Boolean flag = false; String str =
	 * null,str_1 = null,str_2 = null,str_3 = null,str_4 = null; char array1[] =
	 * null,array2[] = null,array3[] = null,array4[] = null;
	 * 
	 * for(int i = 0;i<traffic_id.length;i = i+5) { flag = false; temp1 = 0;
	 * temp2 = 0; temp3 = 0; temp4 = 0; pos = 0; result1 = 0; result2 = 0;
	 * GenieDebug.error("debug",""+i);
	 * 
	 * temp = (TextView) findViewById(traffic_id[i]);
	 * temp.setText(traffic_str[i/5]); temp = null;
	 * 
	 * 
	 * 
	 * posE = temp_str.indexOf("#");
	 * GenieDebug.error("debug",temp_str.substring(0, posE)); temp = (TextView)
	 * findViewById(traffic_id[i+1]); temp.setText(temp_str.substring(0, posE));
	 * temp = null; temp_str.replace(0, posE + 1, "");
	 * 
	 * posE = temp_str.indexOf("#");
	 * GenieDebug.error("debug",temp_str.substring(0, posE)); temp = (TextView)
	 * findViewById(traffic_id[i+2]); str = temp_str.substring(0, posE);
	 * temp.setText(str);
	 * 
	 * 
	 * 
	 * pos = str.indexOf("/"); GenieDebug.error("debug","pos = "+pos); if(pos >=
	 * 0) { flag = true; str_1 = str.substring(0, pos); str_2 =
	 * str.substring(pos+1,str.length());
	 * 
	 * GenieDebug.error("debug","str_1 = "+str_1);
	 * GenieDebug.error("debug","str_2 = "+str_2);
	 * 
	 * if(str_1.indexOf(",") >= 0) { array1 = str_1.toCharArray();
	 * parsestr(array1); //str_1 = array1.toString(); str_1 =
	 * String.valueOf(array1); } if(str_2.indexOf(",") >= 0) { array2 =
	 * str_2.toCharArray(); parsestr(array2); //str_2 = array2.toString(); str_2
	 * = String.valueOf(array2); } GenieDebug.error("debug","str_1 = "+str_1);
	 * GenieDebug.error("debug","str_2 = "+str_2); temp1 =
	 * Float.parseFloat(str_1); temp2 = Float.parseFloat(str_2);
	 * 
	 * 
	 * }else { flag = false; GenieDebug.error("debug","str = "+str);
	 * if(str.indexOf(",") >= 0) { GenieDebug.error("debug","in , = "); array1 =
	 * str.toCharArray(); parsestr(array1); //str = array1.toString(); str =
	 * String.valueOf(array1); } GenieDebug.error("debug","str = "+str);
	 * 
	 * temp1 = Float.parseFloat(str); }
	 * GenieDebug.error("debug","temp1 = "+temp1);
	 * GenieDebug.error("debug","temp2 = "+temp2);
	 * 
	 * temp = null; temp_str.replace(0, posE + 1, "");
	 * 
	 * posE = temp_str.indexOf("#");
	 * GenieDebug.error("debug",temp_str.substring(0, posE)); temp = (TextView)
	 * findViewById(traffic_id[i+3]); //temp.setText(temp_str.substring(0,
	 * posE)); str = temp_str.substring(0, posE); temp.setText(str);
	 * 
	 * pos = str.indexOf("/"); GenieDebug.error("debug","pos = "+pos); if(pos >=
	 * 0) { flag = true; str_3 = str.substring(0, pos); str_4 =
	 * str.substring(pos+1,str.length());
	 * GenieDebug.error("debug","str_3 = "+str_3);
	 * GenieDebug.error("debug","str_4 = "+str_4);
	 * 
	 * if(str_3.indexOf(",") >= 0) { array3 = str_3.toCharArray();
	 * parsestr(array3); //str_3 = array3.toString(); str_3 =
	 * String.valueOf(array3); } if(str_4.indexOf(",") >= 0) { array4 =
	 * str_4.toCharArray(); parsestr(array4); //str_4 = array4.toString(); str_4
	 * = String.valueOf(array4); }
	 * 
	 * GenieDebug.error("debug","str_3 = "+str_3);
	 * GenieDebug.error("debug","str_4 = "+str_4);
	 * 
	 * temp3 = Float.parseFloat(str_3); temp4 = Float.parseFloat(str_4);
	 * 
	 * 
	 * }else { flag = false; GenieDebug.error("debug","str = "+str);
	 * if(str.indexOf(",") >= 0) { GenieDebug.error("debug","in , = "); array3 =
	 * str.toCharArray(); parsestr(array3); //str = array3.toString(); str =
	 * String.valueOf(array3); } GenieDebug.error("debug","str = "+str); temp3 =
	 * Float.parseFloat(str); }
	 * 
	 * GenieDebug.error("debug","temp3 = "+temp3);
	 * GenieDebug.error("debug","temp4 = "+temp4);
	 * 
	 * temp = null; temp_str.replace(0, posE + 1, "");
	 * 
	 * temp = (TextView) findViewById(traffic_id[i+4]);
	 * 
	 * if(flag) { result1 = temp1+temp3; result2 = temp2+temp4;
	 * 
	 * temp.setText(String.valueOf(result1)+"/"+String.valueOf(result2)); }else
	 * { result1 = temp1+temp3; temp.setText(String.valueOf(result1)); }
	 * //temp.setText("0"); temp = null;
	 * 
	 * }
	 * 
	 * 
	 * // StringBuffer temp = new
	 * StringBuffer(GenieSoap.dictionary.get(GenieGlobalDefines
	 * .DICTIONARY_KEY_TRAFFIC)); // //
	 * GenieDebug.error("debug",temp.toString()); // // int posS = 0; // int
	 * posE = 0; // for(int i = 0;i<5;i++) // { // map = new HashMap<String,
	 * Object>(); // // map.put("ItemPeriod",traffic_str[i]); // // posE =
	 * temp.indexOf("#"); // GenieDebug.error("debug",temp.substring(0, posE));
	 * // map.put("ItemTime",temp.substring(0, posE)); // temp.replace(0, posE +
	 * 1, ""); // // posE = temp.indexOf("#"); //
	 * GenieDebug.error("debug",temp.substring(0, posE)); //
	 * map.put("ItemUpload",temp.substring(0, posE)); // temp.replace(0, posE +
	 * 1, ""); // // posE = temp.indexOf("#"); //
	 * GenieDebug.error("debug",temp.substring(0, posE)); //
	 * map.put("ItemDownload",temp.substring(0, posE)); // temp.replace(0, posE
	 * + 1, ""); // // map.put("ItemTotal", "0"); // listItem.add(map); // } //
	 * //
	 * 
	 * 
	 * 
	 * }
	 */

	public void fillData2List() {
		switch (activeIndex) {
		case GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS:
			initWirelessData();
			break;

		case GenieGlobalDefines.ACTIVE_FUNCTION_GUESTACESS:
			initGuestAccessData();
			break;

		// case GenieGlobalDefines.ACTIVE_FUNCTION_MAP:
		// initAttachDevicesData();
		// break;
		case GenieGlobalDefines.ACTIVE_FUNCTION_TRAFFIC:
			initTrafficData();
			break;
		}
	}

	@Override
	public void CallBack(String action, String ip, int Value, int n) {
		// TODO Auto-generated method stub

		GenieDebug.error("debug", "WIFI CallBack action = " + action);
		GenieDebug.error("debug", "WIFI CallBack ip = " + ip);
		GenieDebug.error("debug", "WIFI CallBack Value = " + Value);
		GenieDebug.error("debug", "WIFI CallBack n = " + n);

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	public void ShowNoSupport() {
		if (controlBT != null) {
			GenieDebug.error("handleMessage", "ESoapRequestnotsupport"
					+ controlBT);
			controlBT.setChecked(false);
			controlBT.setEnabled(false);
		}
		if (toggleButton != null) {
			GenieDebug.error("handleMessage", "ESoapRequestnotsupport"
					+ toggleButton);
			toggleButton.setEnabled(false);
			toggleButton.setClickable(false);
		}
		GenieDebug.error("handleMessage",
				"1111111111111111+ESoapRequestnotsupport");
		// controlBT.setEnabled(false);
		String error = getResources().getString(R.string.notsupport);
		ShowMessageDialog(error);
	}

	public void ShowWirelsssettinreLogin() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(GenieListView.this)
		// .setIcon(R.drawable.icon)
		// .setTitle(" ")
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setResult(GenieGlobalDefines.WIRELESSSETTING_RESULT);
								finish();
							}
						}).setMessage(R.string.wirelsssettinrelogin);
		dialog.show();
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
		IntentFilter filter = new IntentFilter();//
		filter.addAction(GenieGlobalDefines.REQUEST_ACTION_RET_BROADCAST);
		registerReceiver(m_RequestReceiver, filter);//
	}

	private class RequestReceiver extends BroadcastReceiver {//

		@Override
		public void onReceive(Context context, Intent intent) {//

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

			if (lable != null && lable.equals("GenieListView")) {
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
							case GenieGlobalDefines.ESoapRequestWLanInfo:
								String wepmode = GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES);
								String mac = GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_MAC);
								String wlan_wpa = GenieSoap.dictionary
										.get(GenieGlobalDefines.DICTIONARY_KEY_WLAN_WPA_KEY);

								GenieDebug.error("debug",
										"RequestReceiver onReceive wepmode ="
												+ wepmode);
								GenieDebug
										.error("debug",
												"RequestReceiver onReceive mac ="
														+ mac);
								GenieDebug.error("debug",
										"RequestReceiver onReceive wlan_wpa ="
												+ wlan_wpa);
								break;
							case GenieGlobalDefines.ESoapRequestWLanWEPKey:
							case GenieGlobalDefines.ESoapRequestWEPKey:
								initWirelessData();
								break;
							case GenieGlobalDefines.ESoapRequestGuestInfo:
								initGuestAccessData();
								break;
							case GenieGlobalDefines.ESoapRequestTrafficMeter:
								initTrafficData();
								if (histogram != null && histogram1 != null) {
									histogram.refreshHistogram();
									histogram1.refreshHistogram();
								}
								break;
							case GenieGlobalDefines.ESoapRequestConfigTrafficEnable:
								GetTrafficMeterInfo(false);
								break;
							case GenieGlobalDefines.ESoapRequestConfigGuestEnable:
								if (GenieRequest.m_SmartNetWork) {
									getModelInfo(false);
								} else {
									ShowWirelsssettinreLogin();
								}
								break;
							default:
								break;
							}
							// fillData2List();
						} else {
							switch (aSoapType) {
							case GenieGlobalDefines.ESoapRequestWLanInfo:
								break;
							case GenieGlobalDefines.ESoapRequestGuestEnable:
							case GenieGlobalDefines.ESoapRequestGuestInfo:
								if (aResponseCode != null
										&& aResponseCode.indexOf("401") != -1) {
									StopRequest();
									ShowNoSupport();
								} else {

								}
								break;
							case GenieGlobalDefines.ESoapReqiestTrafficEnable:
							case GenieGlobalDefines.ESoapRequestTrafficOptions:
							case GenieGlobalDefines.ESoapRequestTrafficMeter:
								if (aResponseCode != null
										&& aResponseCode.indexOf("401") != -1) {
									StopRequest();
									ShowNoSupport();
								} else {

								}
								break;
							case GenieGlobalDefines.ESoapRequestConfigTrafficEnable:
								GetTrafficMeterInfo(false);
								break;
							case GenieGlobalDefines.ESoapRequestConfigGuestEnable:
								ShowWirelsssettinreLogin();
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
}
