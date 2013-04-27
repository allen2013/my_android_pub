package com.turbo;

import java.security.acl.Owner;

import com.dragonflow.DeviceInfo;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.GenieMap;
import com.dragonflow.genie.ui.R; 
import com.filebrowse.FileUploadActivity;

import android.app.Activity;
import android.content.Intent;
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
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Turbo_SelectFileActivity extends Activity {
	
	private TurboDeviceInfo ownDeviceInfo=null;
	private TurboDeviceInfo receivedDeviceInfo=null;
	private boolean isSelection=false;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 Display d = getWindowManager().getDefaultDisplay();
			final int window_w = d.getWidth();
			final int window_h = d.getHeight();
			
			if (window_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && window_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
				setTheme(R.style.bigactivityTitlebarNoSearch);
			} else {
				setTheme(R.style.activityTitlebarNoSearch); 
			}
			requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE);
	        
			setContentView(R.layout.turbo_selectfile);
			
			if (window_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && window_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
			} else {
				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
			}
			
			//
			Bundle bundle=getIntent().getExtras();
			if(bundle!=null){
				this.ownDeviceInfo=(TurboDeviceInfo) bundle.getSerializable("OwnDeviceInfo");
				this.receivedDeviceInfo=(TurboDeviceInfo) bundle.getSerializable("ReceivedDeviceInfo");
			}
			
			InitTitleView();
			
			initFileTypeControl();
			
			//设置需要传输的设备名称
			if(this.receivedDeviceInfo!=null){
				((TextView)this.findViewById(R.id.tb_otherdevice_name)).setText(this.receivedDeviceInfo.getDeviceName().trim());
			}
			
		
	}
	
	/**
	 * 初始化选择文件类型控件
	 */
	private void initFileTypeControl(){
		
		final ImageView file_select=(ImageView) this.findViewById(R.id.tb_select_file);
		file_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!isSelection){
					setRotateAnimation(v);
					if(receivedDeviceInfo!=null){
						//延迟执行
						new Handler().postDelayed(new Runnable() {
							public void run() {
								Intent intent=new Intent();
								intent.setClass(Turbo_SelectFileActivity.this, Turbo_AllFileActivity.class);
								intent.putExtra("OwnDeviceInfo", ownDeviceInfo);
								intent.putExtra("ReceivedDeviceInfo", receivedDeviceInfo);
								startActivity(intent);
								isSelection=false;
							}
						}, 1000);
						
					}
				}
				
			}
		});
		
		final ImageView music_select=(ImageView) this.findViewById(R.id.tb_select_music);
		music_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isSelection){
					setRotateAnimation(v);
					if(receivedDeviceInfo!=null){
						//延迟执行
						new Handler().postDelayed(new Runnable() {
							public void run() {
								Intent intent=new Intent();
								intent.setClass(Turbo_SelectFileActivity.this, Turbo_AudioActivity.class);
								intent.putExtra("OwnDeviceInfo", ownDeviceInfo);
								intent.putExtra("ReceivedDeviceInfo", receivedDeviceInfo);
								startActivity(intent);
								isSelection=false;
							}
						}, 1000);
						
					}
				}
			
			}
		});
		
		final ImageView video_select=(ImageView) this.findViewById(R.id.tb_select_video);
		video_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!isSelection){
					setRotateAnimation(v);
					if(receivedDeviceInfo!=null){
						//延迟执行
						new Handler().postDelayed(new Runnable() {
							public void run() {
								isSelection=false;
							}
						}, 1000);
						
					}
				}
			
			}
		});
		
		final ImageView image_select=(ImageView) this.findViewById(R.id.tb_select_image);
		image_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!isSelection){
					setRotateAnimation(v);
					if(receivedDeviceInfo!=null){
						//延迟执行
						new Handler().postDelayed(new Runnable() {
							public void run() {
								isSelection=false;
							}
						}, 1000);
						
					}
				}
			
			}
		});
		
		final ImageView message_select=(ImageView) this.findViewById(R.id.tb_select_message);
		message_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!isSelection){
					setRotateAnimation(v);
					if(receivedDeviceInfo!=null){
						//延迟执行
						new Handler().postDelayed(new Runnable() {
							public void run() {
								isSelection=false;
							}
						}, 1000);
						
					}
				}
			
			}
		});
		
		final ImageView history_select=(ImageView) this.findViewById(R.id.tb_select_history);
		history_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!isSelection){
					setRotateAnimation(v);
					if(receivedDeviceInfo!=null){
						//延迟执行
						new Handler().postDelayed(new Runnable() {
							public void run() {
								isSelection=false;
							}
						}, 1000);
						
					}
				}
			
			}
		});
		
	}
	
	/**
	 * 设置旋转动画
	 * @param v
	 */
	private void setRotateAnimation(final View v){
		
		isSelection=true;
		final Animation animation=new RotateAnimation(0.0f, +360.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(1000);
		v.post(new Runnable() {
			
			@Override
			public void run() {
				
				v.startAnimation(animation);
			}
		});
		
		
		
	}
	
	
	 /**
     * 初始化标题栏
     */
    public void InitTitleView() {
		Button back = (Button) findViewById(R.id.back);
		Button about = (Button) findViewById(R.id.about);

		TextView title = (TextView) findViewById(R.id.netgeartitle);
		title.setText("Select File");

		back.setBackgroundResource(R.drawable.title_bt_bj);
		//about.setBackgroundResource(R.drawable.title_bt_bj);
		//about.setText(R.string.refresh);
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
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	 if (keyCode == KeyEvent.KEYCODE_BACK) { 
    		 this.finish();
    	 }
    	return super.onKeyDown(keyCode, event);
    }
	

}
