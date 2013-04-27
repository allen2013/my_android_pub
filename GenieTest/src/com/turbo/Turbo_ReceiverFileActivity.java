package com.turbo;

import com.dragonflow.FileTransfer;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 
import com.filebrowse.FileService;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Turbo_ReceiverFileActivity extends Activity {
	
	private TurboDeviceInfo sendDeviceInfo;
	private TurboDeviceInfo receivedDeviceInfo;
	private FileTransfer rf;
	public ProgressBar progressBar;
	public TextView progressNum;
	public TextView progressSize;
	private Button cancelButton;
	
	
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

		setContentView(R.layout.turbo_receivefile);

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
			this.sendDeviceInfo=(TurboDeviceInfo) bundle.getSerializable("SendDeviceInfo");
			this.receivedDeviceInfo=(TurboDeviceInfo) bundle.getSerializable("ReceivedDeviceInfo");
		}
		
		rf=FileTransfer.getInstance();
		
		initContentView();
		
	}
	
	/**
	 * 初始化标题栏
	 */
	public void InitTitleView() {
		Button back = (Button) findViewById(R.id.back);
		Button about = (Button) findViewById(R.id.about);

		TextView title = (TextView) findViewById(R.id.netgeartitle);
		title.setText("Receive File");

		back.setBackgroundResource(R.drawable.title_bt_bj);
		// about.setBackgroundResource(R.drawable.title_bt_bj);
		// about.setText(R.string.refresh);
		about.setVisibility(View.GONE);

		back.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
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
	
	/**
	 * 初始化主视图
	 */
	private void initContentView(){
		
		//确定
		this.findViewById(R.id.tb_receive_okbutton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(rf!=null){
					rf.setReceivedActivity(Turbo_ReceiverFileActivity.this);
					rf.setSuflag(false);
					v.setEnabled(false);
					cancelButton.setText(R.string.close);
					
				}
				
			}
		});
		
		//取消
		cancelButton=(Button) this.findViewById(R.id.tb_receive_cancelbtn);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
				
			}
		});
		
		progressBar=(ProgressBar) this.findViewById(R.id.tb_receive_progessbar);
		progressNum=(TextView) this.findViewById(R.id.tb_receive_progressnum);
		progressSize=(TextView) this.findViewById(R.id.tb_receive_progresssize);
		
//		if(rf!=null){
//			rf.setReceivedActivity(Turbo_ReceiverFileActivity.this);
//		}
		
		if(sendDeviceInfo!=null){
			TextView sendDeviceName=(TextView) this.findViewById(R.id.tb_receive_otherdevicename);
			sendDeviceName.setText(sendDeviceInfo.getDeviceName());
		}
		if(receivedDeviceInfo!=null){
			TextView receivedDeviceName=(TextView) this.findViewById(R.id.tb_receive_owndevicename);
			receivedDeviceName.setText(receivedDeviceInfo.getDeviceName());
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		rf.setReceivedActivity(null);
		rf.setRetuaccept("REJECT");
		rf.setSuflag(false);
		super.finish();
	}
	

}
