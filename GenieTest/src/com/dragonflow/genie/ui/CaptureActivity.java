package com.dragonflow.genie.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dragonflow.GenieDebug;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.netgear.qrcode.camera.CameraManager;
import com.netgear.qrcode.decoding.CaptureActivityHandler;
import com.netgear.qrcode.decoding.InactivityTimer;
import com.netgear.qrcode.view.ViewfinderView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private TextView txtResult;
	private TextView tv_qr_scribe = null;

	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	
	private final int requestCode = -1;
	
	private boolean closeScreen = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// /////////////////////////////////////////////////////////
		Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();

		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ///////////////
		setContentView(R.layout.qrmain);

		InitTitleView();

		// CameraManager

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		txtResult = (TextView) findViewById(R.id.txtResult);
		tv_qr_scribe = (TextView) findViewById(R.id.tv_qr_scribe);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		if (!checkCamerHardware()) {
			viewfinderView.setVisibility(View.INVISIBLE);
			// viewfinderView.drawResultBitmap(BitmapFactory.decodeResource(getResources(),
			// Color.TRANSPARENT));
			Toast.makeText(this,
					getResources().getString(R.string.unqrcodedescribe), 0)
					.show();
			tv_qr_scribe.setText(getResources().getString(
					R.string.unqrcodedescribe));
			return;
		}

		try {
			CameraManager.init(getApplication());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			viewfinderView.setVisibility(View.INVISIBLE);
			// viewfinderView.drawResultBitmap(BitmapFactory.decodeResource(getResources(),
			// Color.TRANSPARENT));
			Toast.makeText(this,
					getResources().getString(R.string.unqrcodedescribe), 0)
					.show();
			tv_qr_scribe.setText(getResources().getString(
					R.string.unqrcodedescribe));
		} catch (Error e) {
			// TODO: handle exception
			e.printStackTrace();
			viewfinderView.setVisibility(View.INVISIBLE);
			// viewfinderView.drawResultBitmap(BitmapFactory.decodeResource(getResources(),
			// Color.TRANSPARENT));
			Toast.makeText(this,
					getResources().getString(R.string.unqrcodedescribe), 0)
					.show();
			tv_qr_scribe.setText(getResources().getString(
					R.string.unqrcodedescribe));
		}
		
		
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
	
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == this.requestCode){
//			startQR();
			GenieDebug.error("onActivityResult=>", closeScreen+""+requestCode);
			closeScreen = true;
		}else{
		}
	}



	private boolean checkCamerHardware() {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

			return true;
		} else {
			return false;
		}
	}
	
	
	
	

	@Override
	protected void onResume() {
		super.onResume();
		GenieDebug.error("onResume=>", "handler onResume: " +closeScreen);
		if(closeScreen){
			GenieDebug.error("onResume=>", "handler onResume: 111111111" +closeScreen);
			startQR();
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		GenieDebug.error("onPause=>", "handler onPause: " + handler+closeScreen);
		if(closeScreen){
			stopQR();
		}
		closeScreen = false;
	}
	
	

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GenieDebug.error("onStart=>", "handler onPause: " + handler+closeScreen);
		startQR();
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		GenieDebug.error("onStop=>", "handler onPause: " + handler+closeScreen);
		stopQR();
	}



	@Override
	protected void onDestroy() {
		GenieDebug
				.error("onDestroy=>", "handler onDestroy: " + inactivityTimer);
		inactivityTimer.shutdown();
		if(m_ConnectTask != null && !m_ConnectTask.isCancelled())
		{
			m_ConnectTask.cancel(true);
			m_ConnectTask = null;
		}
		stopQR();
		super.onDestroy();
	}

	public void startQR() {
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		GenieDebug.error("onResume=>", "handler  surfaceView: " + surfaceView
				+ "  hasSurface:" + hasSurface);
		// //Log.e("debug","onResume surfaceView.w="+surfaceView.getWidth());
		// //Log.e("debug","onResume surfaceView.h"+surfaceView.getHeight());
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			GenieDebug.error("startQR", "initCamera"+"1111111111");
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	public void stopQR() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		if(checkCamerHardware()){
			CameraManager.get().closeDriver();
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		GenieDebug.error("initCamera 111=>", "handler  surfaceHolder: "
				+ surfaceHolder + "  handler:" + handler);
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (Exception e) {
			GenieDebug.error("Exception", e.toString());
			viewfinderView.setVisibility(View.INVISIBLE);
			// viewfinderView.drawResultBitmap(BitmapFactory.decodeResource(getResources(),
			// Color.TRANSPARENT));
			Toast.makeText(this,
					getResources().getString(R.string.unqrcodedescribe), 0)
					.show();
			tv_qr_scribe.setText(getResources().getString(
					R.string.unqrcodedescribe));
			return;
		} catch (Error e) {
			e.printStackTrace();
			viewfinderView.setVisibility(View.INVISIBLE);
			// viewfinderView.drawResultBitmap(BitmapFactory.decodeResource(getResources(),
			// Color.TRANSPARENT));
			Toast.makeText(this,
					getResources().getString(R.string.unqrcodedescribe), 0)
					.show();
			tv_qr_scribe.setText(getResources().getString(
					R.string.unqrcodedescribe));
			return;
		}

		if (handler != null) {
			handler = null;
		}

		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
			GenieDebug.error("initCamera 22222=>", "handler  surfaceHolder: "
					+ surfaceHolder + "  handler:" + handler);
		}
		GenieDebug.error("initCamera 3333=>", "handler  surfaceHolder: "
				+ surfaceHolder + "  handler:" + handler);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		GenieDebug.error("surfaceCreated=>", "handler 11111111111:" + hasSurface);
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);
		playBeepSoundAndVibrate();
		txtResult.setText(obj.getBarcodeFormat().toString() + ":"
				+ obj.getText());
		GenieDebug.error("handleDecode>", obj.getText());
		// ConnectToWifi(obj.getText()); //test
		try {
			getWiFiInfo(obj.getText(), obj.getBarcodeFormat().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String qr_genie = "Genie";
	
	
	private final String qr_wireless = "WIRELESS:";
	private final String qr_password = ";PASSWORD:";
	
	private final String qr_http = "http://";
	private final String qr_https = "https://";
	private final String z_qr_http = "[hH][tT][tT][pP](s)?://";//"^(http://|https://)";//"[a-zA-Z]*://[^s]*";//(?!([\\w-]+\\.[\\w-]+$))([\\w-]+\\.)+[\\w-]+(/[\\w-   ./?%&=]*)?$
	
	private final String qr_other = "other";
	
	private boolean parseGenieQRCodeInfo(String text){
		int text_length = text.length();
		if(text.contains(qr_password)){
			if(text_length>(qr_wireless.length()+qr_password.length())){
				int wireless_index = text.indexOf(qr_wireless);
				int password_index = text.indexOf(qr_password);
				GenieDebug.error("parseGenieQRCodeInfo =>", "wireless_index: "+wireless_index+"  password_index: "+password_index);
				String ssid = text.substring(wireless_index+qr_wireless.length(), password_index);
				String password = text.substring(password_index+qr_password.length(), text_length);
				m_SSID = ssid;
				m_PASSWORD = password;
				GenieDebug.error("parseGenieQRCodeInfo =>", "ssid: "+ssid+"  password: "+password);
				GenieDebug.error("parseGenieQRCodeInfo =>", "m_SSID: "+m_SSID+"  m_PASSWORD: "+m_PASSWORD);
				GenieDebug.error("parseGenieQRCodeInfo =>", "m_SSID length: "+m_SSID.length()+"  m_PASSWORD length: "+m_PASSWORD.length());
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
		
	}

	private void getWiFiInfo(String text, String format) {
		StringBuffer sb = null;
		if(text == null){
			return;
		}
		if (text.startsWith(qr_wireless)) {
			// »ñÈ¡SSIDºÍPassword ²¢Á¬½Óµ½Õâ¸öÎÞÏßÍøÖÐ
//			int index = startWith.length();
//			int number_length = 6 + index;
//			int text_int_length = -1; // SSID and Password length get int
//			int text_string_length = -1; // SSID and Password length get String
//			int ssid_length = -1;
//			int password_length = -1;
//			try {
//				text_int_length = Integer.parseInt(text.substring(index,
//						index + 2));
//				text_string_length = text.substring(number_length,
//						text.length()).length();
//				ssid_length = Integer.parseInt(text.substring(index + 2,
//						index + 4));
//				password_length = Integer.parseInt(text.substring(index + 4,
//						number_length));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				// Router information Format error
//				ShowConnectWifiDialog(text, "other", "");
//				return;
//			}
//
//			GenieDebug.error("getWiFiInfo = >", "text_int_length: "
//					+ text_int_length + "  text_string_length: "
//					+ text_string_length + " ssid_length: " + ssid_length
//					+ " password_length: " + password_length);
//
//			if (text_int_length == -1 && text_string_length == -1
//					|| text_int_length != text_string_length) {
//				ShowConnectWifiDialog(text, "other", "");
//				return;
//			}
//			String ssid = text.substring(number_length, number_length
//					+ ssid_length);
//			String password = text.substring(number_length + ssid_length,
//					number_length + ssid_length + password_length);
//			txtResult.setText(getResources().getString(R.string.name) + ": "
//					+ ssid + "\n" + getResources().getString(R.string.key)
//					+ ": " + password);
//			GenieDebug.error("CaptureActivity=> ", "ssid: " + ssid
//					+ "password: " + password);
//			m_SSID = ssid;
//			m_PASSWORD = password;
//			if (m_SSID == null || m_PASSWORD == null) {
//				// Log.e("debug","user == null || password == null");
//				return;
//			}
			if(parseGenieQRCodeInfo(text)){
				sb = new StringBuffer();
				sb.append(getResources().getString(R.string.name) + ": " + m_SSID);
				sb.append("\n");
				sb.append(getResources().getString(R.string.key) + ": " + m_PASSWORD);
				sb.append("\n\n");
				sb.append(getResources().getString(R.string.qr_connect_router)
						+ " " + m_SSID + " ?");
				ShowConnectWifiDialog(sb.toString(), qr_genie, "");
				return;
			}else{
				ShowGetQRCodeFailDialog(text, qr_other, "");
				return;
			}

			

		} 
		
		if(text.length()<=qr_http.length()){
			ShowGetQRCodeFailDialog(text,qr_other, "");
			return ;
		}
		String temp= text.substring(0,qr_http.length());
		String temp2 = null;
		
		GenieDebug.error("parseGenieQRCodeInfo =>", "temp"+temp);
		
		if(temp.toLowerCase().equals(qr_http))
		{
			temp2 = text.substring(qr_http.length(),text.length());
			text = temp.toLowerCase()+temp2;
			String note = String.format("%s\n%s\n%s",getResources().getString(R.string.qr_url_found),
					text,
					getResources().getString(R.string.qr_connect_http));
			GenieDebug.error("parseGenieQRCodeInfo =>", "00"+temp.toLowerCase());
			ShowConnectWifiDialog(note, qr_http, text);
			return ;
		}else 
		{
			
			if(text.length()<=qr_https.length()){
				ShowGetQRCodeFailDialog(text,qr_other, "");
				return ;
			}
			
			
			temp= text.substring(0,qr_https.length());
			
			
			
			if(temp.toLowerCase().equals(qr_https))
			{
				temp2 = text.substring(qr_https.length(),text.length());
				text = temp.toLowerCase()+temp2;
				GenieDebug.error("parseGenieQRCodeInfo =>", "11"+temp.toLowerCase());
				String note = String.format("%s\n%s\n%s",getResources().getString(R.string.qr_url_found),
						text,
						getResources().getString(R.string.qr_connect_http));
				ShowConnectWifiDialog(note, qr_https, text);
				return ;
			}else
			{
				ShowGetQRCodeFailDialog(text,qr_other, "");
				return ;
			}
		}
		

		
//		Pattern pat = Pattern.compile(z_qr_http);
//		Matcher mat = pat.matcher(text);
//		
//		if (mat.find()) {
//			sb = new StringBuffer();
//			sb.append(getResources().getString(R.string.qr_url_found));
//			sb.append("\n");
//			sb.append(text);
//			sb.append("\n");
//			sb.append(getResources().getString(R.string.qr_connect_http));
//			ShowConnectWifiDialog(sb.toString(), qr_http, text);
//			return;
//		} else {
//			// ¸Ã¶þÎ¬Âë²»ÊÇNetgearÂ·ÓÉÆ÷
//			ShowGetQRCodeFailDialog(text,qr_other, "");
//			return;
//		}
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		// if (playBeep && mediaPlayer != null) {
		// mediaPlayer.start();
		// }
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	private WifiManager wifiManager;//
	private WifiInfo wifiInfo;//
	private List<ScanResult> scanResultList; //
	private List<WifiConfiguration> wifiConfigList;//
	private WifiLock wifiLock;// Wifi
	private String m_SSID = null;
	private String m_PASSWORD = null;

	private void ConnectToWifi(String Str) {
		// String ssid = null;
		// String password = null;

		int j = 0;
		for (String word : Str.split(";")) {
			int n = 0;
			boolean flag = false;
			for (String word0 : word.split(":")) {
				switch (n) {
				case 0:
					if (j == 0 && word0.equals("SSID")) {
						flag = true;
					}
					if (j == 1 && word0.equals("PASSWORD")) {
						flag = true;
					}
					break;
				case 1:
					if (j == 0 && flag) {
						m_SSID = word0;
					}
					if (j == 1 && flag) {
						m_PASSWORD = word0;
					}
					break;
				}
				n++;
			}
			j++;
		}

		if (m_SSID == null || m_PASSWORD == null) {
			// Log.e("debug","user == null || password == null");
			return;
		}
		ShowConnectWifiDialog(m_SSID, "Genie", "");

	}
	private ConnectTask m_ConnectTask = null;
	
	private class ConnectTask extends AsyncTask< String , Void, Void>
	{

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			
			int count = params.length;
			if(count != 2)
				return null;
	        String ssid = params[0];
	        String password = params[1];
	        GenieDebug.error("debug","ssid = "+ssid);
	        GenieDebug.error("debug","password = "+password);
	        ConnectWifi(ssid, password);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					CancelProgressDialog();
					CaptureActivity.this.onBackPressed();
				}
			}, 8000);
			m_ConnectTask = null;
			
		}
		
	}

	private void ConnectWifi(String ssid, String password) {
		// Log.e("debug","SSID ="+ssid);
		// Log.e("debug","PASSWORD ="+password);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);// ï¿½ï¿½È¡Wifiï¿½ï¿½ï¿½ï¿½
		if(!wifiManager.isWifiEnabled()){
			wifiManager.setWifiEnabled(true);
		};
		
		wifiInfo = wifiManager.getConnectionInfo();
		wifiConfigList = wifiManager.getConfiguredNetworks(); //

		int index = -1;
		for (int i = 0; i < wifiConfigList.size(); i++) {
			if (wifiConfigList.get(i).SSID.equals("\"" + ssid + "\"")) {
				// Log.e("boolean", "equals  i " + i );
				index = i;
				break;
			}

		}
		WifiConfiguration wc = null;
		if (index > -1 && index < wifiConfigList.size()) {
			wifiManager.disconnect();

			wc = wifiConfigList.get(index);
			if(password!=null&&(password.length()!=0)){
			wc.preSharedKey = "\"" + password + "\"";
			}
			wc.status = WifiConfiguration.Status.ENABLED;
			wc.hiddenSSID = true;
			wc.status = WifiConfiguration.Status.ENABLED;
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			if(password!=null&&(password.length()==0)){
				wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			}else{
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			}
			wc.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			wc.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			// Log.e("boolean", "index = "+index);

			int update = wifiManager.updateNetwork(wc);
			// Log.e("boolean", "0 enableNetwork update = " + update );
			boolean save = wifiManager.saveConfiguration();
			// Log.e("boolean", "0 enableNetwork save = " + save );

			boolean b = wifiManager.enableNetwork(index, true);
			// Log.e("boolean", "0 enableNetwork returned " + b );
		} else {
			wifiManager.disconnect();

			wc = new WifiConfiguration();
			wc.SSID = "\"" + ssid + "\"";
			if(password!=null&&(password.length()!=0)){
			wc.preSharedKey = "\"" + password + "\"";
			}
			wc.hiddenSSID = true;
			wc.status = WifiConfiguration.Status.ENABLED;
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			if(password!=null&&(password.length()==0)){
				wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			}else{
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			}
			wc.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			wc.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			int res = wifiManager.addNetwork(wc);
			// Log.e("boolean", "1 enableNetwork add Network returned " + res );

			boolean save = wifiManager.saveConfiguration();
			// Log.e("boolean", "1 enableNetwork 1 save = " + save );

			int update = wifiManager.updateNetwork(wc);
			// Log.e("boolean", "1 enableNetwork update = " + update );
			if (update == -1) {
				update = wifiManager.updateNetwork(wc);
				// Log.e("boolean", "1 enableNetwork update 2 = " + update );
			}

			save = wifiManager.saveConfiguration();
			// Log.e("boolean", "1 enableNetwork 2 save = " + save );

			boolean b = wifiManager.enableNetwork(res, true);
			// Log.e("boolean", "1 enableNetwork returned " + b );
		}
		
		int number = 0;
		boolean isconnect = false;
		while(!isconnect)
		{
			if(number > 3000)
				break;
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WifiInfo temp = wifiManager.getConnectionInfo();
			GenieDebug.error("debug","temp.getBSSID="+temp.getBSSID()+"number="+number);
			if(temp != null && temp.getBSSID() != null)
			{
				isconnect = true;
			}
			number++;
		}
		

	}

	private void ShowConnectWifiDialog(final String note, final String type,
			final String url) {
		AlertDialog.Builder connectdialog = new AlertDialog.Builder(this)
		// .setIcon(R.drawable.icon)
		// .setTitle(" ")
				.setMessage(note);

		connectdialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (type.equals(qr_genie)) {
							ShowProgressDialog();
							m_ConnectTask = new ConnectTask();
							m_ConnectTask.execute(m_SSID, m_PASSWORD);
							
						} else if (type.equals(qr_http)) {
							try {
//								if(url.startsWith("HTTP://"))
//								url.replace(oldChar, newChar)
								GenieDebug.error("ShowConnectWifiDialog=>", "url: "+url+" type: "+type);
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(url));
								startActivityForResult(intent, requestCode);
								// CaptureActivity.this.finish();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								drawViewfinder();
								startQR();
								e.printStackTrace();
							}
						} else if(type.equals(qr_https)){
							try {
//								if(url.startsWith("HTTP://"))
//								url.replace(oldChar, newChar)
								GenieDebug.error("ShowConnectWifiDialog=>", "url: "+url+" type: "+type);
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(url));
								startActivityForResult(intent, requestCode);
								// CaptureActivity.this.finish();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								drawViewfinder();
								startQR();
								e.printStackTrace();
							}
						}else {
							drawViewfinder();
							startQR();
						}

					}
				});
		connectdialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// CaptureActivity.this.onBackPressed();
						drawViewfinder();
						startQR();
					}
				});

		connectdialog.setCancelable(true);
		connectdialog.show();

	}
	
	private void ShowGetQRCodeFailDialog(final String note, final String type,
			final String url) {
		AlertDialog.Builder connectdialog = new AlertDialog.Builder(this)
		// .setIcon(R.drawable.icon)
		// .setTitle(" ")
				.setMessage(note);
		connectdialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// CaptureActivity.this.onBackPressed();
						drawViewfinder();
						startQR();
					}
				});

		connectdialog.setCancelable(true);
		connectdialog.show();

	}

	private ProgressDialog progressDialog = null;

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

	public Button m_back = null;
	public Button m_about = null;

	public void InitTitleView() {

		m_back = (Button) findViewById(R.id.back);
		m_about = (Button) findViewById(R.id.about);

		TextView title = (TextView) this.findViewById(R.id.netgeartitle);
		title.setText(getResources().getString(R.string.qrcode));

		m_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CaptureActivity.this.onBackPressed();
			}
		});

		m_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		m_back.setOnTouchListener(new OnTouchListener() {
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

		m_about.setOnTouchListener(new OnTouchListener() {
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

		m_about.setVisibility(View.GONE);
	}

}
