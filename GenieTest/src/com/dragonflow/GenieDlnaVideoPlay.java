package com.dragonflow;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;



public class GenieDlnaVideoPlay extends Activity implements MediaPlayer.OnErrorListener,
MediaPlayer.OnCompletionListener, OnAudioFocusChangeListener {
	
public static final String TAG = "VideoPlayer";
private VideoView mVideoView;
private Uri mUri = null;
private int mPositionWhenPaused = -1;
private Boolean m_create = false;
private AudioManager m_audioManager = null;



	private Timer settimer = null;
	private TimerTask task = null;
	private ProgressBar m_progress = null;
	
	private ImageView m_playbg = null;
	
	private Boolean  isplaying = false;
	private Boolean  stopanderror = false;
	
	private Boolean  misInit = false;
	
	public int m_duration = 0;
	public int m_maxvolume = 0;
	public int m_volume = 0;
	public int m_playpos = 0;
	public int m_mute = 0;
	
	private int m_VideoPlayVolumeBackup = 0;
	
	
	
	private MediaController mMediaController;

	//public native int SetViewOnVideoPlay();
	//public native int initVideoPlayFieldAndMethod();
	
	//public native int StateChangedOnPlaying();
	//public native int StateChangedOnStop();	
	//public native int StateChangedOnPause();
	//public native int StateChangedOnPrepared();
	//public native int StateChangedOnError();
	
	//public native int StateChangedOnTick(int pos,int duration,int mute,int volume);
	

	//public native String GetVideoUrl();
	
	//public native int GetPlayStyle();
	

	final private static int MESSAGE_ONREPLAY = 500;
	final private static int MESSAGE_ONPLAY = 501;
	final private static int MESSAGE_ONSTOP = 502;
	final private static int MESSAGE_ONPAUSE = 503;
	final private static int MESSAGE_ONSEEK = 504;
	final private static int MESSAGE_ONNEXT = 505;
	final private static int MESSAGE_ONPREVIOUS = 506;
	final private static int MESSAGE_ONVOLUME = 507;
	final private static int MESSAGE_ONMUTED = 508;
	
	

	private static boolean m_ispause = false;
	
	public static boolean m_playerror = false;

	private Handler handler = new Handler()
	{   
		public void handleMessage(Message msg) 
		{   
			String error = null;
			GenieDebug.error("handleMessage", "GenieDlnaVideoPlay handleMessage msg.what = "+msg.what);
			switch(msg.what)
			{
			case GenieDlnaActionDefines.ACTION_RENDER_SETPLAYING:
				VideoViewOnPlay();
				break;	
			case GenieDlnaActionDefines.ACTION_RENDER_SETSTOPPED:
				VideoViewOnStop();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_SETPAUSED:
				VideoViewOnPause();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_SETSEEKTO:
				VideoViewOnSeek();
				break;
//			case MESSAGE_ONNEXT:
//				VideoViewOnNext();
//				break;
			case GenieDlnaActionDefines.ACTION_RENDER_SETVOLUME:
				VideoViewOnVolume();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_SETMUTED:
				VideoViewOnMuted();
				break;
//			case MESSAGE_ONPREVIOUS:
//				VideoViewOnPrevious();
//				break;
			case GenieDlnaActionDefines.ACTION_RENDER_TOVIDEOREPLAY:
				VideoViewOnRePlay();
				break;	
        	case GenieDlnaActionDefines.ACTION_RENDER_TOIMAGEPLAY:
        		ToImagePlayer();
        		break;	
			}
		}
	}; 

	private void StartDlnaAction(int action)
	{		
		
		GenieDebug.error("debug","9999df999999 StartDlnaAction action = "+action);
		
		Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
		Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,action);
		startService(Dlna);
		GenieDebug.error("debug","9999df999999 StartDlnaAction end");
		
		

	}
	

	private class DLNAReceiver extends BroadcastReceiver{//

        @Override
	        public void onReceive(Context context, Intent intent) {//
       	 int action = -1;
       	 
       	//GenieDebug.error("debug","----GenieVideoPlay- intent.getAction()="+intent.getAction());
    		
       	 	//if(m_ispause)
       	 	//	return ;
    		action = intent.getIntExtra(GenieGlobalDefines.DLNA_ACTION_RET,-1);
    		
    		GenieDebug.error("DLNAReceiver", "onReceive DLNA_ACTION_RET ="+action);
    		sendMessage2UI(action);


        }
      }
	
	 private DLNAReceiver m_DLNAReceiver = null;

	
	private int  SendMssageToImagePlayer()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_IMAGEPLAYER);
		return 0;
	}
	private void ToImagePlayer()
	{
		Intent intent = new Intent();
    	
		//intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GenieDlnaVideoPlay.this, GenieDlnaImagePlayer.class);
		startActivity(intent);
		this.finish();
	}

private void sendMessage2UI(int msg)
{
	
	handler.sendEmptyMessage(msg);
}


public int RendererOnRePlay()
{
	sendMessage2UI(MESSAGE_ONREPLAY);
	return 0;
}

public int RendererOnPlay()
{
	sendMessage2UI(MESSAGE_ONPLAY);
	return 0;
}

public int RendererOnStop()
{
	sendMessage2UI(MESSAGE_ONSTOP);
	return 0;
}
public int RendererOnPause()
{
	sendMessage2UI(MESSAGE_ONPAUSE);
	return 0;
}
public int RendererOnSeek(int pos)
{
	GenieDlnaStatus.m_VideoPlaySeekPos = pos;
	
	sendMessage2UI(MESSAGE_ONSEEK);
	return 0;
}

public int RendererOnVolume(int volume)
{	

	
	GenieDlnaStatus.m_VideoPlaySetVolume = volume;
	
	GenieDebug.error("RendererOnVolume","999 m_VideoPlaySetVolume = "+GenieDlnaStatus.m_VideoPlaySetVolume);
	
	sendMessage2UI(MESSAGE_ONVOLUME);
	return 0;
}
public int RendererOnMuted(int mute)
{
	GenieDebug.error("RendererOnMuted","999 RendererOnMuted mute = "+mute);
	GenieDlnaStatus.m_VideoPlaySetMute = mute;
	
	GenieDebug.error("RendererOnMuted","999 RendererOnMuted GenieDlnaStatus.m_VideoPlaySetMute = "+GenieDlnaStatus.m_VideoPlaySetMute);
	
	sendMessage2UI(MESSAGE_ONMUTED);
	return 0;
}




public int RendererOnNext()
{
	sendMessage2UI(MESSAGE_ONNEXT);
	return 0;
}

public int RendererOnPrevious()
{
	sendMessage2UI(MESSAGE_ONPREVIOUS);
	return 0;
}






	
private void VideoViewOnMuted()
{
	GenieDebug.error("VideoViewOnMuted","999 GenieDlnaStatus.m_VideoPlaySetMute = "+GenieDlnaStatus.m_VideoPlaySetMute);
	GenieDebug.error("VideoViewOnMuted","999 m_VideoPlaySetVolume = "+GenieDlnaStatus.m_VideoPlaySetVolume);
	GenieDebug.error("VideoViewOnMuted","999 m_VideoPlayVolumeBackup = "+m_VideoPlayVolumeBackup);
	
	GenieDebug.error("VideoViewOnMuted","999 m_maxvolume = "+m_maxvolume);
	
	if(null != m_audioManager)
	{
		if(GenieDlnaActionDefines.m_PlayViewSetMuted)
		{
			m_VideoPlayVolumeBackup = (m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)*(100/m_maxvolume));
			m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		}else
		{
			m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (m_VideoPlayVolumeBackup/(100/m_maxvolume)), 0);
		}
	
	}
}	


private void VideoViewOnVolume()
{
	if(null != m_audioManager)
	{
		//GenieDlnaStatus.m_VideoPlayVolumeBackup = GenieDlnaStatus.m_VideoPlaySetVolume;
		
		GenieDebug.error("VideoViewOnVolume","999 m_VideoPlaySetVolume = "+GenieDlnaStatus.m_VideoPlaySetVolume);
		GenieDebug.error("VideoViewOnVolume","999 m_VideoPlayVolumeBackup = "+m_VideoPlayVolumeBackup);
		
		m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (GenieDlnaActionDefines.m_PlayViewSetVolume/(100/m_maxvolume)), 0);
	}
}

private void VideoViewOnRePlay()
{
	if(null != mVideoView)
	{

		misInit = true;
		GenieDebug.error("debug","VideoViewOnRePlay misInit = true");
		
			GenieDebug.error("debug","@@@@@ VideoViewOnRePlay");
			if(mVideoView.isPlaying())
			{
				GenieDebug.error("debug","@@@@@ mVideoView.isPlaying()");
				mVideoView.stopPlayback();
				
//			
				
			}
		
		String url = GetVideoUrl();
		if(url != null)
		{
			mUri = Uri.parse(url);
		}else
		{
			return ;
		}
		int style = GetPlayStyle();
		GenieDebug.error("VideoViewOnRePlay","GetPlayStyle = "+style);
		if(style == 2)
		{
			m_playbg.setVisibility(View.VISIBLE);
		}else
		{
			m_playbg.setVisibility(View.GONE);
		}
		m_playerror = false;
		mVideoView.setVideoURI(mUri);
		mVideoView.start();	
		m_duration = mVideoView.getDuration();
		//StartTimer();
		GenieDlnaStatus.m_VideoPlaySeekPos = 0;
		GenieDlnaStatus.m_VideoPlaySetMute = 0;
		GenieDlnaStatus.m_VideoPlaySetVolume = 0;
		m_VideoPlayVolumeBackup = 0;
		stopanderror = false;
		
		if(null != mMediaController)
		{
			if(mVideoView.isShown()){
				mMediaController.show();
			}
		}
	}
}

private void VideoViewOnPlay()
{
	if(null != mVideoView)
	{		
		
		GenieDebug.error("debug","VideoViewOnPlay misInit = true");
		
		if(stopanderror)
		{
			misInit = true;
			String url = GetVideoUrl();
			
			int style = GetPlayStyle();
			GenieDebug.error("VideoViewOnPlay","GetPlayStyle = "+style);
			if(style == 2)
			{
				m_playbg.setVisibility(View.VISIBLE);
			}else
			{
				m_playbg.setVisibility(View.GONE);
			}
			if(null != url)
			{
				mUri = Uri.parse(url);
				mVideoView.setVideoURI(mUri);
			}else
			{
				return ;
			}
			
		}
		m_playerror = false;
		mVideoView.start();	
		m_duration = mVideoView.getDuration();
		//StartTimer();
		GenieDlnaStatus.m_VideoPlaySeekPos = 0;
		GenieDlnaStatus.m_VideoPlaySetMute = 0;
		GenieDlnaStatus.m_VideoPlaySetVolume = 0;
		m_VideoPlayVolumeBackup = 0;
		stopanderror = false;
		if(null != mMediaController)
		{
			if(mVideoView.isShown()){
				mMediaController.show();
			}
		}
		
		//misInit = false;
	}
}

private void VideoViewOnStop()
{
	misInit = true;
	GenieDebug.error("debug","VideoViewOnStop misInit = true");
	if(null != mVideoView)
	{
		
		mVideoView.stopPlayback();
		stopanderror = true;
		//CanclTimer();
		//mVideoView = null;
		//misInit = true
	}
	
}
private void VideoViewOnPause()
{
	if(null != mVideoView)
	{
		mVideoView.pause();
		stopanderror = false;
		if(null != mMediaController)
		{
			if(mVideoView.isShown()){
				mMediaController.show();
			}
		}
		GenieDebug.error("debug","VideoViewOnPause misInit = false");
		misInit = false;
	}
}
private void VideoViewOnSeek()
{
	if(null != mVideoView)
	{
		GenieDebug.error("debug","mVideoView.seekTo "+GenieDlnaStatus.m_VideoPlaySeekPos);
		mVideoView.seekTo((int)GenieDlnaActionDefines.m_PlayViewSeekTimeInMillis);
		if(null != mMediaController)
		{
			if(mVideoView.isShown()){
				mMediaController.show();
			}
		}
		misInit = false;
		
		
		
		GenieDebug.error("debug","VideoViewOnSeek misInit = false");
	}

}
private void VideoViewOnNext()
{
	
}

private void VideoViewOnPrevious()
{
	
}

private String GetVideoUrl()
{
	if(null != GenieDlnaActionDefines.m_playurl)
 	{
 		return GenieDlnaActionDefines.m_playurl;
 	}
 	
	return null;
}

private int GetPlayStyle()
{
	return GenieDlnaActionDefines.m_playStyle;
}


/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	
	setContentView(R.layout.dlnavideoplay);
	
	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	
//	SetViewOnVideoPlay();
//	initVideoPlayFieldAndMethod();
	m_create = true;
	isplaying = false;
	stopanderror = false;
	m_playerror = false;
	m_ispause = true;
	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

	mVideoView = (VideoView)findViewById(R.id.video_view);
	m_playbg =  (ImageView)findViewById(R.id.music_bg);
	
	m_progress = (ProgressBar)findViewById(R.id.progress);
	m_progress.setVisibility(View.VISIBLE);
	
	
	m_audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	//int result = m_audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	//if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) 
	//{    // could not get audio focus.
	//	GenieDebug.error("onCreate","!!!!result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED");
	//}
	
	//m_audioManager.setRingerMode(AudioManager.STREAM_MUSIC);
	
	
	m_maxvolume = m_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

	m_volume = m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	
	String url = GetVideoUrl();
	int style = GetPlayStyle();
	GenieDebug.error("onCreate","GetPlayStyle = "+style);
	if(style == 2)
	{
		m_playbg.setVisibility(View.VISIBLE);
	}else
	{
		m_playbg.setVisibility(View.GONE);
	}
	
//	GenieDebug.error("onCreate","url = "+url);
	
	//mUri = Uri.parse(Environment.getExternalStorageDirectory() + "/1.3gp");
	//mUri = Uri.parse("http://wangjun.easymorse.com/wp-content/video/mp4/mp4.3gp");
	
	if(null != url)
	{
		mUri = Uri.parse(url);
	
	
		misInit = true;
		GenieDebug.error("debug","onCreate misInit = true");
		
		
		GenieDebug.error("debug","----GenieVideoPlay- onCreate");
		
		//Ã½Ìå¿ØÖÆÆ÷
		mMediaController = new MediaController(this,false);
		//mMediaController.set
		mVideoView.setMediaController(mMediaController);
		
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				GenieDebug.error("debug","----onPrepared =  ");
				
				GenieDebug.error("debug","----GenieVideoPlay- onPrepared");
				
	//			StateChangedOnPrepared();
				m_progress.setVisibility(View.GONE);
				StartTimer();
				misInit = false;
				GenieDebug.error("debug","onPrepared misInit = false");
				
				if(null != mMediaController)
				{
					if(mVideoView.isShown()){
						mMediaController.show();
					}
				}
	
				StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONPLAYING);
			}
		});
		
		mVideoView.setVideoURI(mUri);
		
		mVideoView.start();
		
		if(null != mMediaController)
		{
			if(mVideoView.isShown()){
				mMediaController.show();
			}
		}
		
		GenieDlnaActionDefines.m_PlayViewLoading = false;
	}
	
	//mMediaController.set
	
}

public void onStart() {
	// 
	
	GenieDebug.error("debug","----GenieVideoPlay- onStart------ ");
	
	//mVideoView.start();
	//m_duration = mVideoView.getDuration();
	
	
	//StartTimer();
	stopanderror = false;

	super.onStart();
	
	RegisterBroadcastReceiver();
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	
	GenieDebug.error("debug","----GenieVideoPlay- onKeyDown--keyCode =  "+keyCode);
	
	//if(mVideoView.isPlaying())
	//{
	//	GenieDebug.error("debug","----mVideoView.isPlaying() = true ");
	//}else
	//{
	//	GenieDebug.error("debug","----mVideoView.isPlaying() = false ");
	//}
	
	
	if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
		//mVideoView.pause();
		return false;
		
	}else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){

		//mVideoView.start();
		return false;
	}else if(keyCode==KeyEvent.KEYCODE_BACK){
		
		if(mVideoView!=null){
			if(mVideoView.isPlaying())
				mVideoView.stopPlayback();
		}
		this.finish();
		
	}

	return super.onKeyDown(keyCode, event);
}
public void onPause() {
	
	GenieDebug.error("debug","----GenieVideoPlay- onPause------ ");
	// 
	//mPositionWhenPaused = mVideoView.getCurrentPosition();
	//mVideoView.stopPlayback();
	//GenieDebug.error("debug", "OnStop: mPositionWhenPaused = " + mPositionWhenPaused);
	//GenieDebug.error("debug", "OnStop: getDuration = " + mVideoView.getDuration());
	//CanclTimer();
	super.onPause();
	
	m_ispause = true;
}

public void onResume() {
	
//	SetViewOnVideoPlay();
	
	GenieDlnaActionDefines.m_isVideoPlayVIew = true;
	
	GenieDebug.error("debug","----GenieVideoPlay- onResume------ ");
	if(m_create)
	{
		m_create = false;
	}else
	{
//		initVideoPlayFieldAndMethod();
		
	}

	//if(mPositionWhenPaused >= 0) {
	//	mVideoView.seekTo(mPositionWhenPaused);
	//	stopanderror = false;
	//	//StartTimer();
		
	//	mPositionWhenPaused = -1;
	//}
	
	

	super.onResume();
	
	m_ispause = false;
}

public void CanclTimer()
{
	if(null != settimer)
	{
		settimer.cancel();
		settimer = null;
	}
	if(null != task)
	{
		task = null;
	}
}

public boolean playing()
{
	GenieDebug.error("debug","$$-- playing ");
	
	if(misInit)
	{
		GenieDebug.error("debug","$$--playing misInit = true ");
		return  false;
	}
	
	if(null != mVideoView)
	{
		GenieDebug.error("debug","$$-- mVideoView.isPlaying() ");
		return mVideoView.isPlaying();
	}else
	{
		return false;
	}
}


public void StartTimer()
{
	
	CanclTimer();
	
	settimer  = new Timer(); 
	task = new TimerTask()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			GenieDebug.error("debug","$$--run ---0--- ");
			
			try {  
			
				if(playing() && !isplaying)
				{
					GenieDebug.error("debug","$$--run ---1--- ");
//					StateChangedOnPlaying();
					StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONPLAYING);
					isplaying = true;
					if(stopanderror)
					{
						GenieDebug.error("debug","$$--run ---2--- ");
						stopanderror = false;
					}
				}else if(!playing() && isplaying)
				{
					GenieDebug.error("debug","$$--run ---3--- ");
					if(!stopanderror)
					{
						GenieDebug.error("debug","$$--run ---4--- ");
//						StateChangedOnPause();
						StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONPAUSED);
					}
					isplaying = false;
				}else if(!playing() && !isplaying && stopanderror)
				{
					GenieDebug.error("debug","$$--run88 !playing() && !isplaying");
//					StateChangedOnStop();
					StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONSTOPPED);
				}
						
			
				if(!stopanderror)
				{
					if(null != m_audioManager && null != mVideoView)
					{
						m_maxvolume = m_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

						m_volume = m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
						if(m_volume == 0)
						{
							GenieDlnaActionDefines.m_PlayViewMuted = true;
						}else
						{
							GenieDlnaActionDefines.m_PlayViewMuted = false;
						}
						
						
						GenieDebug.error("debug","999 m_volume = "+m_volume);
						
						//GenieDlnaStatus.m_VideoPlayVolumeBackup = m_volume;
						//GenieDlnaStatus.m_VideoPlaySetVolume = m_volume;
					
						//m_playpos = mVideoView.getCurrentPosition();
						//m_duration = mVideoView.getDuration();
						GenieDlnaActionDefines.m_PlayViewCurrentMillis = mVideoView.getCurrentPosition();
						GenieDlnaActionDefines.m_PlayViewTotalMillis = mVideoView.getDuration();
						GenieDlnaActionDefines.m_PlayViewVolume = (int)((100/m_maxvolume)*m_volume);
//						StateChangedOnTick((m_playpos/1000),(m_duration/1000),m_mute,(int)((100/m_maxvolume)*m_volume));
						StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONSEEK);
						StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONVOLUME);
					}
					//m_duration					
				}
			}catch (Exception e)
 	       {
	    	     e.printStackTrace();
	    	     return ;
	       } 
		}
		
	};
	settimer.schedule(task,1000,2000);
}


public boolean onError(MediaPlayer player, int arg1, int arg2) {
	
	GenieDebug.error("debug","----GenieVideoPlay- onError-arg1 arg2 =  "+arg1+arg2);
	
	m_progress.setVisibility(View.GONE);
	
	stopanderror = true;
//	StateChangedOnError();
	StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONERROR);
	CanclTimer();
	
	m_playerror = true;
	return false;
	}

public void onCompletion(MediaPlayer mp) {
	
	GenieDebug.error("debug","----GenieVideoPlay- onCompletion------ ");
	stopanderror = true;
//	StateChangedOnStop();
	StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONSTOPPED);
	if(null != mVideoView)
	{
		if(mVideoView.isPlaying())
		{
			mVideoView.stopPlayback();
			//mVideoView = null;
		}
	}
	m_progress.setVisibility(View.GONE);
	//StateChangedOnTick(0,(m_duration/1000),m_mute,(int)((100/m_maxvolume)*m_volume));
	
	if(m_playerror)
	{
		CanclTimer();
		this.finish();
	}
	//this.finish();
	}


private void UnRegisterBroadcastReceiver()
{
	 if(null != m_DLNAReceiver)
		{
			unregisterReceiver(m_DLNAReceiver);
			m_DLNAReceiver = null;
		}
}

private void RegisterBroadcastReceiver()
{
	 UnRegisterBroadcastReceiver();
	 
		m_DLNAReceiver = new DLNAReceiver();
		IntentFilter filter = new IntentFilter();//		
		filter.addAction(GenieGlobalDefines.DLNA_ACTION_BROADCAST);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(m_DLNAReceiver, filter);//oadcast Receiver
}

@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
//	StateChangedOnStop();
	
	GenieDebug.error("debug","----GenieVideoPlay- onDestroy");
	
	CanclTimer();
	if(null != mVideoView)
	{
		if(mVideoView.isPlaying())
		{
			mVideoView.stopPlayback();
			//mVideoView = null;
		}
	}
	stopanderror = true;
	super.onDestroy();
	
	UnRegisterBroadcastReceiver();
	
	GenieDlnaActionDefines.m_isVideoPlayVIew = false;
	m_ispause = true;
}
@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
//	StateChangedOnStop();
	StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONSTOPPED);
	CanclTimer();
	stopanderror = true;
	super.onBackPressed();
}
@Override
public void onAudioFocusChange(int focusChange) {
	// TODO Auto-generated method stub
	GenieDebug.error("debug","----GenieVideoPlay- onAudioFocusChange----focusChange = "+focusChange);
	
	switch (focusChange) 
	{        
	case AudioManager.AUDIOFOCUS_GAIN:            // resume playback 
		          
		break;        
	case AudioManager.AUDIOFOCUS_LOSS:            // Lost focus for an unbounded amount of time: stop playback and release media player            
		      
		break;        
	case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:            // Lost focus for a short time, but we have to stop            // playback. We don't release the media player because playback            // is likely to resume            
		         
		break;        
	case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:            // Lost focus for a short time, but it's ok to keep playing            // at an attenuated level            
		         
		break;    
		
	}
	
}	
}