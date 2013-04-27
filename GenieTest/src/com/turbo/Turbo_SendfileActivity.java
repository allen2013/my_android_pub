package com.turbo;

import com.dragonflow.FileTransfer;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 
import com.filebrowse.FileService;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Turbo_SendfileActivity extends Activity {
	
	public ProgressBar progressBar;
	public TextView progressNum;
	public TextView progressSize;
	private TurboDeviceInfo ownDeviceInfo;
	private TurboDeviceInfo receivedDeviceInfo;
	private boolean isSending=false;
	private static Button okButton;
	private static Button cancelButton;
	public final static int TRANSFER_START=0; 
	public final static int TRANSFER_FINISHED=1; 
	
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

		setContentView(R.layout.turbo_sendfile);

		if (window_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& window_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
		}

		InitTitleView();
		
		//获取上一个activity传送的数据
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			this.ownDeviceInfo=(TurboDeviceInfo) bundle.getSerializable("OwnDeviceInfo");
			if(ownDeviceInfo==null){
				SharedPreferences deviceinfo = getSharedPreferences("OwnDeviceInfo", 0); 
				if(deviceinfo!=null){
					ownDeviceInfo=new TurboDeviceInfo();
					ownDeviceInfo.setIp(deviceinfo.getString("DeviceIP",""));  
					ownDeviceInfo.setDeviceName(deviceinfo.getString("DeviceName","")); 
				}
			}
			this.receivedDeviceInfo=(TurboDeviceInfo) bundle.getSerializable("ReceivedDeviceInfo");
		}
		
		initContentView();
		
		
	}
	
	/**
	 * 初始化标题栏
	 */
	public void InitTitleView() {
		Button back = (Button) findViewById(R.id.back);
		Button about = (Button) findViewById(R.id.about);

		TextView title = (TextView) findViewById(R.id.netgeartitle);
		title.setText("Send File");

		back.setBackgroundResource(R.drawable.title_bt_bj);
		// about.setBackgroundResource(R.drawable.title_bt_bj);
		// about.setText(R.string.refresh);
		about.setVisibility(View.GONE);

		back.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				backPage();
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
	
	/**
	 * 初始化主视图
	 */
	private void initContentView(){
		
		okButton=(Button) this.findViewById(R.id.tb_send_okbutton);
		cancelButton=(Button) this.findViewById(R.id.tb_send_cancelbtn);
		//确定
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				FileService fileservice=FileService.fileservice;
				if(fileservice!=null){
					fileservice.setSendActivity(Turbo_SendfileActivity.this);
					isSending=true;
					okButton.setEnabled(false);
					fileservice.sendTurboFile();
					isSending=false;
				}
				
			}
		});
		
		//取消
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				backPage();
				
			}
		});
		
		progressBar=(ProgressBar) this.findViewById(R.id.tb_send_progessbar);
		progressNum=(TextView) this.findViewById(R.id.tb_send_progressnum);
		progressSize=(TextView) this.findViewById(R.id.tb_send_progresssize);
		
		if(ownDeviceInfo!=null){
			TextView ownDeviceName=(TextView) this.findViewById(R.id.tb_send_owndevicename);
			ownDeviceName.setText(ownDeviceInfo.getDeviceName());
		}
		if(receivedDeviceInfo!=null){
			TextView receivedDeviceName=(TextView) this.findViewById(R.id.tb_send_otherdevicename);
			receivedDeviceName.setText(receivedDeviceInfo.getDeviceName());
		}
		
		
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			backPage();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch(msg.what){
				case TRANSFER_START:{
					if(okButton!=null){
						okButton.setEnabled(false);
					}
					if(cancelButton!=null){
						cancelButton.setEnabled(false);
					}
					break;
				}
				case TRANSFER_FINISHED:{
					if(okButton!=null){
						okButton.setEnabled(false);
					}
					if(cancelButton!=null){
						cancelButton.setEnabled(true);
						cancelButton.setText(R.string.close);
					}
					break;
				}
			}
			
		};
	};
	
	/**
	 * 返回
	 */
	private void backPage(){
		
		if(!isSending){
			this.finish();
		}
		
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		FileService fileService=FileService.fileservice;
		if(fileService!=null){
			fileService.setSendActivity(null);
			FileTransfer fileTransfer=FileService.fileservice.getFileTransfer();
			if(fileTransfer!=null){
				fileTransfer.setSendActivity(null);
			}
		}
		super.finish();
	}

}
