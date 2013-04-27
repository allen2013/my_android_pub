package com.turbo;

import java.util.ArrayList;
import java.util.List;

import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 
import com.filebrowse.FileService;
import com.filebrowse.FileUploadActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Turbo_AudioActivity extends Activity {

	private ListView audiolistview;
	private Turbo_AudioAdapter audioadapter;
	private List<MediaInfo> medialist = new ArrayList<MediaInfo>();
	public Thread m_IconThread = null;
	private boolean isCancelLoad=false;
	public Button okButton=null;
	private TurboDeviceInfo ownDeviceInfo=null;
	private TurboDeviceInfo receivedDeviceInfo=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		setContentView(R.layout.turbo_audio);

		if (window_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& window_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
		}

		InitTitleView();
		
		Bundle extras = getIntent().getExtras();
   	    if(extras!=null){
   	    	ownDeviceInfo=(TurboDeviceInfo) extras.getSerializable("OwnDeviceInfo");
   	    	if(ownDeviceInfo==null){
   	    		SharedPreferences deviceinfo = getSharedPreferences("OwnDeviceInfo", 0); 
   	    		if(deviceinfo!=null){
   	    			ownDeviceInfo=new TurboDeviceInfo();
   	    			ownDeviceInfo.setIp(deviceinfo.getString("DeviceIP",""));  
   	    			ownDeviceInfo.setDeviceName(deviceinfo.getString("DeviceName","")); 
   	    		}
   	    	}
   	    	receivedDeviceInfo=(TurboDeviceInfo) extras.getSerializable("ReceivedDeviceInfo");
   	    }
		
		//列表
		audiolistview=(ListView) this.findViewById(R.id.turbo_audiolist);
		audiolistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				//MediaInfo info = (MediaInfo) view.getTag();
				//info.setSelected(!info.selected);
			}
		});
		audioadapter = new Turbo_AudioAdapter(
				Turbo_AudioActivity.this, medialist);
		audiolistview.setAdapter(audioadapter);
		Allaudio();
		
		this.okButton=(Button) this.findViewById(R.id.tb_audio_okbtn);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(receivedDeviceInfo!=null){
					FileService fileService=FileService.fileservice;
					fileService.setReceicedDevice_IP(receivedDeviceInfo.getIp());
					if(ownDeviceInfo!=null){
						fileService.setDevice_Name(ownDeviceInfo.getDeviceName());
					}
					fileService.setFilepath(audioadapter.getSelectedFilepath());
					
		    		Intent sendIntent=new Intent();
		    		sendIntent.setClass(Turbo_AudioActivity.this, Turbo_SendfileActivity.class);
		    		sendIntent.putExtra("OwnDeviceInfo", ownDeviceInfo);
		    		sendIntent.putExtra("ReceivedDeviceInfo", receivedDeviceInfo);
		    		startActivity(sendIntent);
				}
				
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
		 title.setText(R.string.turbo_music);

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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		CancelIconThread();
	}
	/**
	 * 取出所有音乐文件
	 */
	public void Allaudio(){
		CancelIconThread();
		m_IconThread = new Thread(new Runnable() {

			@Override
			public void run() {
				 //指定获取的列  
		        String columns[] = new String[]{  
		                Media.DATA,Media._ID,Media.TITLE,Media.DISPLAY_NAME ,Media.SIZE
		        };  
		        //cursor = this.managedQuery(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);  
		        Cursor cursor = Turbo_AudioActivity.this.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, null, null, Media.DISPLAY_NAME);  
		        int pathIndex = cursor.getColumnIndexOrThrow(Media.DATA);  
		        //titleIndex = cursor.getColumnIndexOrThrow(Media.TITLE);  
		        int nameIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME);  
		        int sizeIndex = cursor.getColumnIndexOrThrow(Media.SIZE);  
		          
		        //显示第一张图片，但是首先要判断一下，Cursor是否有值  
		        if(cursor!=null){  
		        	String filename="";
		        	while(!isCancelLoad && cursor.moveToNext()){
		        		filename=cursor.getString(nameIndex);
		        		if(filename!=null&&!"".equals(filename)){
		        			MediaInfo mediainfo=new MediaInfo();
		        			mediainfo.setFilename(filename);
		        			mediainfo.setFilesize(cursor.getString(sizeIndex));
		        			mediainfo.setFilepath(cursor.getString(pathIndex));
		        			medialist.add(mediainfo);
		        		}
		        		audioadapter.notifyDataSetChanged();
		        	}
		        	
				} else {
					Toast.makeText(Turbo_AudioActivity.this, "Not found audio file!",
							Toast.LENGTH_SHORT).show();
				}
		        cursor.close();
		        isCancelLoad=false;
			}
		});
	
		m_IconThread.start();
	}
	
	public void CancelIconThread() {
		if (m_IconThread != null) {
			if (m_IconThread.isAlive())
				m_IconThread.interrupt();
			isCancelLoad=true;
			m_IconThread = null;
		}
	}
}
