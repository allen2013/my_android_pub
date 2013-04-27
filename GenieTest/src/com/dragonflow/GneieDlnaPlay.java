package com.dragonflow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.text.method.SingleLineTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.dragonflow.genie.ui.R; 

import com.netgear.genie.media.dlna.DeviceDesc;

public class GneieDlnaPlay extends Activity {

	private ImageButton m_play=null;
	private Button m_stop=null;
	//private Button m_pause=null;
	private Button m_pre=null;
	private Button m_back=null;
	private SeekBar m_playseek=null;
	
	
	//private SeekBar m_voiceseek=null;
	
	private ImageButton m_volume_select=null;
	
	//private Button  m_voiceimage = null;
	
	private TextView m_time = null;
	private TextView m_totaltime = null;
	private TextView m_errorinfo = null;
	
	//private ImageView m_playbgpic = null;
	
	private MarqueeTextView m_playtitle = null;
	private LinearLayout m_playtitle2 = null;
	
	private LinearLayout m_layout_seek = null;
	//private LinearLayout m_layout_vulme = null;
	
	private String  m_timestr = null;
	private String  m_totaltimestr = null;
	private String m_title = null;
	
	private static int index = 0;
	
	private static boolean m_ispause = false;
	
	

	
	
	
	private Timer settimer = null;
	private TimerTask task = null;
	
	
	private Timer m_SlideTimer = null;
	private TimerTask m_SlideTask = null;
	
	private Boolean m_OnSlideModle = false;
	
	private Boolean m_create = false;
	
	private int m_SpeedIndex = -1;
	
	private Spinner m_SlideSpeed = null;
	private ArrayAdapter<String> m_Speedadapter = null;
	
	
	private ImageView  m_thumbnail = null;
	
	private String m_Speed[] = {
			"Slow",
			"Normal",
			"Fast",
	};
	
	final public static int m_Speed_s[] = {
		R.string.slide_slow,
		R.string.slide_normal,
		R.string.slide_rast,
	};
	final public static int m_Speed_i[] = {
		10000,
		5000,
		2000,
	};
	
	
	private GetThumbnailAsyncTask m_ThumbnailAsyncTask = null;
	
	
	
	
	
	final public static int MESSAGE_REFRESH = 103;
	final public static int MESSAGE_REFRESH_PLAYSEEK = 104;
	final public static int MESSAGE_VIEWDISABLED = 1100;
	final public static int MESSAGE_VIEWENABLED = 1111;
	
	final public static int MESSAGE_SHOWWAITDIALOG = 502;
	final public static int MESSAGE_CLOSEWAITDIALOG = 503;
	
	final public static int MESSAGE_PLAYFAIL = 505;
	final public static int MESSAGE_PLAYSUCCESS = 506;
	
	//public native int SetViewOnPlay();
	//public native int initPlayFieldAndMethod();
	
	//public native int OnRenderPause();
	//public native int OnRenderStop();
	//public native int OnRenderSeek(int pos);
	//public native int OnRenderVoiceSeek(int pos);
	//public native int OnRenderSetMute(int mute);	
	//public native int OnRenderNext();
	//public native int OnRenderPrevious();
	//public native int OnRenderPlay();
	//public native int OnRenderGetPositionInfo();
	//public native int GetAndRefreshUidata();
	
	//public native int GetWorkRenderStatus();
	
	//public native int OnRenderSlidePlay();
	
	
	//public native int SetOnSlidePlay();
	//public native int CanclOnSlidePlay();
	
	
	
	
	private ProgressDialog progressDialog = null;
	

	
	
	
		
	
	private Handler handler = new Handler()
	{   
        public void handleMessage(Message msg) 
        {   
        	String error = null;
        	GenieDebug.error("handleMessage", "GneieDlnaPlay handleMessage msg.what = "+msg.what);
        	switch(msg.what)
        	{
        	case MESSAGE_REFRESH:
        		RefreshUi();
        		break;	
        	case MESSAGE_REFRESH_PLAYSEEK:
        		SetPlaySeek();
        		return;
//        	case MESSAGE_VIEWDISABLED:
//        		DisableView();
//        		break;
        	case MESSAGE_VIEWENABLED:
        		EnableView();
        		break;
        	case GenieDlnaStatus.VIEW_MEDIAVIDEOPLAY:
        		ToVideoPlay();
        		break;
        	case GenieDlnaStatus.VIEW_IMAGEPLAYER:        		
        		ToImagePlayer();
        		break;	
        	case MESSAGE_SHOWWAITDIALOG:
        		//showWaitingDialog2();
        		return ;
        	case MESSAGE_CLOSEWAITDIALOG:
        		//closeWaitingDilalog2();
        		return ;
        	case MESSAGE_PLAYFAIL:
        		
//        		GenieDebug.error("handleMessage", "999999999999 MESSAGE_PLAYFAIL GenieDlnaStatus.m_OnSlidePlay = "+GenieDlnaStatus.m_OnSlidePlay);
//        		
//        		OnPlayFailed();
//        		if(GenieDlnaActionDefines.m_OnSlidePlay)
//        		{
//        			StartSlidePlay();
//        		}
//        		break;
        	case MESSAGE_PLAYSUCCESS:
//        		SetPlayTitle();
//        		GenieDebug.error("handleMessage", "999999999999 MESSAGE_PLAYSUCCESS GenieDlnaStatus.m_OnSlidePlay  = "+GenieDlnaStatus.m_OnSlidePlay);
//        		//if(GenieDlnaStatus.m_OnSlidePlay)
//        		//{
////        			Display d = getWindowManager().getDefaultDisplay();
////        			
//        			Animation a = new TranslateAnimation(0.0f,
//        	                d.getWidth(), 0.0f, 0.0f);
//        	        a.setDuration(1000);
//        	        a.setStartOffset(300);
//        	        a.setRepeatMode(Animation.RESTART);
//        	        a.setRepeatCount(0);
//
//
//        	        a.setInterpolator(AnimationUtils.loadInterpolator(GneieDlnaPlay.this,
//        	                        android.R.anim.accelerate_interpolator));
// 
//        	        m_playbgpic.startAnimation(a);
        		//	StartSlidePlay();
        		//}
        		break;
        	case GenieDlnaActionDefines.ACTION_RENDER_CHENGED:
        		InitUi();
        		break;
        	case GenieDlnaActionDefines.ACTION_RENDER_SETPLAYING:
        		if(null != GenieDlnaActionDefines.m_playurl)
        		{
        			if(GenieDlnaActionDefines.m_playStyle == 1 ||
        					GenieDlnaActionDefines.m_playStyle == 2)
        			{
        				ToVideoPlay();
        			}else if(GenieDlnaActionDefines.m_playStyle == 3)
        			{
        				ToImagePlayer();
        			}
        		}
        		break;	
        	case GenieDlnaActionDefines.ACTION_RENDER_TOVIDEOPLAY:
        		ToVideoPlay();
        		break;
        	case GenieDlnaActionDefines.ACTION_RENDER_TOIMAGEPLAY:
        		ToImagePlayer();
        		break;		
        	case GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE:
    			CancleControlActionProgressDialog();
    			break;
    		case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_SHOW:				
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_SHOW:			
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_SHOW:				
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_SHOW:				
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_SHOW:					
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_SHOW:				
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_SHOW:				
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_SHOW:
				ShowControlActionProgressDialog(msg.what);
				break;
			case GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH:
				RefreshUi();
				break;
			case GenieDlnaActionDefines.ACTION_RET_PLAYPOSIREFRESH:
				SetPlaySeek();
				break;
			case GenieDlnaActionDefines.ACTION_RET_PREVPLAY_START:
			case GenieDlnaActionDefines.ACTION_RET_NEXTPLAY_START:
			case GenieDlnaActionDefines.ACTION_RET_PLAY_START:
			case GenieDlnaActionDefines.ACTION_RET_SLIDEPLAY_START:
				GetPlayThumbnail(msg.what);
				break;	
			case GenieDlnaActionDefines.ACTION_RET_SELECT_RENDER:
				//ShowRenderDialog();
				break;
			case GenieDlnaActionDefines.ACTION_RET_SELECT_SOURCE:
				//ShowChooseSourceDialog();
				break;
        	}
        	closeWaitingDialog();
        }
	}; 
	
	
	
	private class ThumbnailPic{
		public   boolean  m_Thumbnailflag = false;
		public   String   m_Thumbnailurl  = null;
		public  Bitmap    m_ThumbnailBitmap = null;
		
	}
	
	private ThumbnailPic m_ThumbnailPic  = new ThumbnailPic();
	
	private void GetPlayThumbnail(int action)
	{
		
		
		
		GenieDebug.error("debug", "GetPlayThumbnail  0");
		SetPlayTitle();
		
		GenieDebug.error("debug", "GetPlayThumbnail  1");
		
		if(m_ThumbnailPic != null && !m_ThumbnailPic.m_Thumbnailflag)
		{
			InitThumbnailPic();

		}
		
		ProgressBar thumbnailprogress = (ProgressBar)findViewById(R.id.thumbnail_progressBar);
//		thumbnailprogress.setVisibility(View.VISIBLE);
		
		if(thumbnailprogress!=null){
			thumbnailprogress.setVisibility(View.VISIBLE);
		}
		
		GenieDebug.error("debug", "GetPlayThumbnail  2");
		

		
		if(null != GenieDlnaActionDefines.m_playItem)
		{
			String mimetype[] = {"image/png","image/jpg"};
			
			String url =  GenieDlnaActionDefines.m_playItem.findThumbnailURL(100, 100, mimetype);
			GenieDebug.error("debug", "GetPlayThumbnail url = "+url);
			GenieDebug.error("debug", "GetPlayThumbnail url.length() = "+url.length());
			
			GenieDebug.error("debug", "GetPlayThumbnail  3");
			
			if(url == null || url.length() <= 0)
			{
				GenieDebug.error("debug", "GetPlayThumbnail  4");
				InitThumbnailPic();
			}else
			{
				GenieDebug.error("debug", "GetPlayThumbnail  6");
				if(m_ThumbnailPic.m_Thumbnailurl == null)
				{
					GenieDebug.error("debug", "GetPlayThumbnail  7");
					m_ThumbnailPic.m_Thumbnailurl = url;
					m_ThumbnailPic.m_Thumbnailflag = false;
					
//					if(m_ThumbnailPic.m_ThumbnailBitmap != null)
//					{
//						GenieDebug.error("debug", "GetPlayThumbnail  8");
//						m_ThumbnailPic.m_ThumbnailBitmap.recycle();
//						m_ThumbnailPic.m_ThumbnailBitmap = null;
//					}
					
					if(m_ThumbnailAsyncTask != null)
					{
						GenieDebug.error("debug", "GetPlayThumbnail  9");
						m_ThumbnailAsyncTask.cancel(true);
						m_ThumbnailAsyncTask = null;
					}
					GenieDebug.error("debug", "GetPlayThumbnail  10");
					if(m_ThumbnailAsyncTask == null || m_ThumbnailAsyncTask.isCancelled())
					{
						GenieDebug.error("debug", "GetPlayThumbnail  11");
						 try {
							URL imageurl = new URL(url);
							m_ThumbnailAsyncTask = (GetThumbnailAsyncTask) new GetThumbnailAsyncTask().execute(imageurl);
							
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}    
						
					}
					
				}else
				{
					GenieDebug.error("debug", "GetPlayThumbnail  12");
					if(m_ThumbnailPic.m_Thumbnailurl.equals(url)
							&& m_ThumbnailPic.m_Thumbnailflag 
							&& m_ThumbnailPic.m_ThumbnailBitmap != null)
					{
						GenieDebug.error("debug", "GetPlayThumbnail  13");
						if( m_thumbnail != null)
						{
							GenieDebug.error("debug", "GetPlayThumbnail  14");
							m_thumbnail.setImageBitmap(m_ThumbnailPic.m_ThumbnailBitmap);
						}
						thumbnailprogress.setVisibility(View.GONE);
					}else
					{
						GenieDebug.error("debug", "GetPlayThumbnail  15");
						
						
						
						m_ThumbnailPic.m_Thumbnailurl = url;
						m_ThumbnailPic.m_Thumbnailflag = false;
						
//						if(m_ThumbnailPic.m_ThumbnailBitmap != null)
//						{
//							GenieDebug.error("debug", "GetPlayThumbnail  16");
//							m_ThumbnailPic.m_ThumbnailBitmap.recycle();
//							m_ThumbnailPic.m_ThumbnailBitmap = null;
//						}
						
						GenieDebug.error("debug", "GetPlayThumbnail  17");
						if(m_ThumbnailAsyncTask != null)
						{
							GenieDebug.error("debug", "GetPlayThumbnail  18");
							m_ThumbnailAsyncTask.cancel(true);
							m_ThumbnailAsyncTask = null;
						}
						
						GenieDebug.error("debug", "GetPlayThumbnail  19");
						
						if(m_ThumbnailAsyncTask == null || m_ThumbnailAsyncTask.isCancelled())
						{
							GenieDebug.error("debug", "GetPlayThumbnail  20");
							 try {
								 
								URL imageurl = new URL(url);
								m_ThumbnailAsyncTask = (GetThumbnailAsyncTask) new GetThumbnailAsyncTask().execute(imageurl);
								
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}    
							
						}
					}
				}
				

			}
		}else
		{
			GenieDebug.error("debug", "GetPlayThumbnail  21");
			InitThumbnailPic();
		}
		
	}
	
	
	private void InitThumbnailPic()
	{
		int style  = GetVideoStyle();
		
		GenieDebug.error("debug","InitThumbnailPic style = "+style);
		
		
		
		if(m_thumbnail != null)
		{
			GenieDebug.error("debug", "InitThumbnailPic  1");
			//m_thumbnail.setBackgroundResource(R.drawable.icon);
			int  Drawableid = 0;
			switch(style)
			{
			case 1:
				GenieDebug.error("debug", "InitThumbnailPic  case 1");
				Drawableid = R.drawable.playing_video_icon;
				//m_thumbnail.setImageBitmap(result);
				break;
			case 2:
				GenieDebug.error("debug", "InitThumbnailPic  case 2");
				Drawableid = R.drawable.playing_music_icon;
				break;
			case 3:
				GenieDebug.error("debug", "InitThumbnailPic  case 3");
				Drawableid = R.drawable.playing_pic_icon;
				break;
			default:
				GenieDebug.error("debug", "InitThumbnailPic  default");
				Drawableid = R.drawable.playing_video_icon;
				break;
			}
			Bitmap thumb = null;
			
			Drawable d = getResources().getDrawable(Drawableid);
			BitmapDrawable bd = (BitmapDrawable) d;
			thumb = bd.getBitmap();
			GenieDebug.error("debug", "InitThumbnailPic  2");
			if(null != thumb)
			{
				GenieDebug.error("debug", "InitThumbnailPic  null != thumb");
				m_thumbnail.setImageBitmap(thumb);
			}
			
			ProgressBar thumbnailprogress = (ProgressBar)findViewById(R.id.thumbnail_progressBar);
			GenieDebug.error("debug", "InitThumbnailPic  3");
			if(thumbnailprogress!=null){
				thumbnailprogress.setVisibility(View.GONE);
			}
//			thumbnailprogress.setVisibility(View.GONE);
		}
	}
	
	private byte[] getBytes(InputStream is) throws IOException {

    	ByteArrayOutputStream baos =new ByteArrayOutputStream();
    	byte[] b =new byte[1024];
    	int len =0;

    	while ((len = is.read(b, 0, 1024)) !=-1) 
    	{
    	baos.write(b, 0, len);
    	baos.flush();
    	}
    	byte[] bytes = baos.toByteArray();
    	return bytes;
    	}
	
	private  class GetThumbnailAsyncTask extends AsyncTask<URL,Void,Bitmap>
	{

		@Override
		protected Bitmap doInBackground(URL... params) {
			// TODO Auto-generated method stub
			
			GenieDebug.error("debug", "GetThumbnailAsyncTask params[0].toString() = "+params[0].toString());
			URL url = params[0];	
			
			if(null == url)
				return null;
			
			Bitmap mBitmap = null;
			
		try{	
			 URLConnection conn;     
 	        conn = url.openConnection();     
 	        conn.connect();     
 	        InputStream in = conn.getInputStream();     
 	        
 	      byte[] bt=getBytes(in); //
 	      

 	        System.gc();
 	        
 	        BitmapFactory.Options opt = new BitmapFactory.Options(); 
 	        //opt.inTempStorage = new byte[16*1024]; 
 	        
 	        opt.inJustDecodeBounds = true;
     		BitmapFactory.decodeByteArray(bt,0,bt.length, opt);
     		opt.inJustDecodeBounds = false;
     		
 			final int SampleSize =  200;
 			
 			GenieDebug.error("GetUrlBitmap", " GetUrlBitmap SampleSize="+SampleSize);

 			if(opt.outWidth > SampleSize || opt.outHeight > SampleSize)
 			{	
	        	if (opt.outWidth > opt.outHeight) {
	        		opt.inSampleSize = opt.outWidth / SampleSize;
	        	} else {
	        		opt.inSampleSize = opt.outHeight / SampleSize;
	        	}
	        	mBitmap =BitmapFactory.decodeByteArray(bt,0,bt.length,opt);
 			}else
 			{
 				if (opt.outWidth > opt.outHeight) {
	        		opt.inSampleSize = opt.outWidth / SampleSize;
	        	} else {
	        		opt.inSampleSize = opt.outHeight / SampleSize;
	        	}
 				Bitmap temp  =BitmapFactory.decodeByteArray(bt,0,bt.length,opt);
 				
 				int width = temp.getWidth();

 				int height = temp.getHeight();

 				Matrix matrix = new Matrix();
 				float scale = 0;
 				if(width > height)
 				{
 					scale  = ((float)200 / width);
 				}else
 				{
 					scale  = ((float)200 / height);
 				}

 				matrix.postScale(scale, scale);

 				mBitmap = Bitmap.createBitmap(temp, 0, 0, width, height, matrix, true);

 			}
 	      
 	        
 	      //  mBitmap = BitmapFactory.decodeStream(in); //濡傛灉閲囩敤杩欑瑙ｇ爜鏂瑰紡鍦ㄤ綆鐗堟湰鐨凙PI涓婁細鍑虹幇瑙ｇ爜闂

 	             
 	        in.close(); 
 	        opt = null;
 	        
 	        bt = null;
 	        if(mBitmap != null)
 	        	return mBitmap;
 	        
 	        System.gc();
 	       } catch (MalformedURLException e) 
 	       {
 	    	     e.printStackTrace(); 
 	       } catch (Exception e)
 	       {
 	    	     e.printStackTrace();
 	       }catch(Error e)
 	       {
 	    	   e.printStackTrace();
 	       }
			
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			
			
			
			
			if(result != null && m_thumbnail != null)
			{
				m_thumbnail.setImageBitmap(result);
			}
			ProgressBar thumbnailprogress = (ProgressBar)findViewById(R.id.thumbnail_progressBar);
			if(thumbnailprogress!=null){
				thumbnailprogress.setVisibility(View.GONE);
			}
			if(null != m_ThumbnailPic)
			{
				m_ThumbnailPic.m_Thumbnailflag = true;
				m_ThumbnailPic.m_ThumbnailBitmap = null;
				m_ThumbnailPic.m_ThumbnailBitmap = result;
			}
			
		}
		
		
	}
	
	
	private void OnPlayFailed()
	{
		Toast toast = Toast.makeText(this,
				 "play error !", Toast.LENGTH_LONG);
		   	   toast.setGravity(Gravity.CENTER, 0, 0);
		   	   toast.show();
	}
	
	public void PlayFailed_DlnaPlay()
	{
		sendMessage2UI(MESSAGE_PLAYFAIL);
	}
	
	
	public void PlaySuccess_DlnaPlay()
	{
		sendMessage2UI(MESSAGE_PLAYSUCCESS);
	}
	
	
	private void showWaitingDialog2()
	{
		closeWaitingDilalog2();
		
		progressDialog = ProgressDialog.show(GneieDlnaPlay.this, "Loading...", "Please wait...", true, true);   
	}
	 
	private void showWaitingDialog()
    {

		sendMessage2UI(MESSAGE_SHOWWAITDIALOG);
		return ;
		
	}
	
	
	private void closeWaitingDilalog2()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}   
	}
	
	private void closeWaitingDialog()
    {
		sendMessage2UI(MESSAGE_CLOSEWAITDIALOG);

    }
	
	private int  SendMssageToImagePlayer()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_IMAGEPLAYER);
		return 0;
	}
	private void ToImagePlayer()
	{
		
		if(m_ispause)
			return;
		GenieDebug.error("debug","dlnaPlay ToImagePlayer");
		
		Intent intent = new Intent();
    	
		//intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GneieDlnaPlay.this, GenieDlnaImagePlayer.class);
		startActivity(intent);
	}
	
	private int  SendMssageToVideoPlay()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_MEDIAVIDEOPLAY);
		
		return 0;
	}
	
	private void ToVideoPlay()
	{
		if(m_ispause)
			return;
		GenieDebug.error("debug","dlnaPlay ToVideoPlay");
		
		Intent intent = new Intent();
		        	
        //intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GneieDlnaPlay.this, GenieDlnaVideoPlay.class);
		startActivity(intent);
	}
	
	
	 private void sendMessage2UI(int msg)
	    {
	    	
	    	handler.sendEmptyMessage(msg);
	    }
	

	   
	   
	   
	   
	   private void CanclSlidePlay()
	   {
		   GenieDebug.error("debug","9999df999999 CanclSlidePlay 0");
		   
		   StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_STOP);
			
	   }


	   
	   
		private void StartSlidePlay()
		{		

			GenieDebug.error("debug","9999df999999 StartSlidePlay 0");

			
			GenieDlnaActionDefines.m_OnSlidePlaySpeedIndex = m_SpeedIndex;
			StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_PLAY);
			

//			if(null != m_SlideTimer)
//			{
//				m_SlideTimer.cancel();
//				m_SlideTimer = null;
//			}
//			if(null != m_SlideTask)
//			{
//				m_SlideTask = null;
//			}
//			
//			
//			m_SlideTimer  = new Timer(); 
//			m_SlideTask = new TimerTask()
//			{
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					
//					GenieDebug.error("debug","999999999999 StartSlidePlay run OnRenderSlidePlay()");	
//					OnRenderSlidePlay();
//					
//				}
//				
//			};
//			
//			int speed ;
//			if(m_SpeedIndex >= 0 && m_SpeedIndex <= 2)
//			{
//				speed = m_Speed_i[m_SpeedIndex];
//			}else
//			{
//				speed = m_Speed_i[1];
//			}
//			
//			
//			m_SlideTimer.schedule(m_SlideTask,6000);
//			
//			GenieDebug.error("debug","999999999999 StartSlidePlay 1");			
//			
//			//m_OnSlideModle = true; 
//			GenieDlnaStatus.m_OnSlidePlay = true;
//			SetOnSlidePlay();
//			SetSlidePlayView();
		}
	

    
	public boolean GetWorkRenderStatus()
	{
		if(GenieDlnaActionDefines.m_WorkRenderUUID != null)
			return true;
		
		return false;
	}
		
    private void onViewChanged()
    {

    	GenieDebug.error("debug","onViewChanged 0");
		 
			int style =  GetVideoStyle();
			
			GenieDebug.error("debug","9999 style = "+style);
			
			switch(style)
			{
			case 1:
//				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//				{
//					m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg);
//				}else
//				{
//					m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg_h);
//				}
				m_play.setVisibility(View.VISIBLE);
				m_stop.setVisibility(View.VISIBLE);
				m_test1.setVisibility(View.VISIBLE);
				
				//m_pause.setVisibility(View.VISIBLE);
				m_pre.setVisibility(View.VISIBLE);
				m_back.setVisibility(View.VISIBLE);
				m_playtitle.setVisibility(View.VISIBLE);
				m_playtitle2.setVisibility(View.GONE);    			
				m_layout_seek.setVisibility(View.VISIBLE);
				m_volume_select.setVisibility(View.VISIBLE);
				GenieDlnaActionDefines.m_OnSlidePlay = false;
				break;
			case 2:
//				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//				{
//					m_playbgpic.setBackgroundResource(R.drawable.playmusic_bg);
//				}else
//				{
//					m_playbgpic.setBackgroundResource(R.drawable.playmusic_bg_h);
//				}
				m_play.setVisibility(View.VISIBLE);
				m_stop.setVisibility(View.VISIBLE);
				
				m_test1.setVisibility(View.VISIBLE);
				
				//m_pause.setVisibility(View.VISIBLE);
				m_pre.setVisibility(View.VISIBLE);
				m_back.setVisibility(View.VISIBLE);
				m_playtitle.setVisibility(View.VISIBLE);
				m_playtitle2.setVisibility(View.GONE);    			
				m_layout_seek.setVisibility(View.VISIBLE);
				m_volume_select.setVisibility(View.VISIBLE);
				GenieDlnaActionDefines.m_OnSlidePlay = false;
				break;
			case 3:
//				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//				{
//					m_playbgpic.setBackgroundResource(R.drawable.playimage_bg);
//				}else
//				{
//					m_playbgpic.setBackgroundResource(R.drawable.playimage_bg_h);
//				}
				
				
				m_play.setVisibility(View.VISIBLE);
				m_stop.setVisibility(View.GONE);
				m_test1.setVisibility(View.GONE);
				//m_pause.setVisibility(View.INVISIBLE);
				m_pre.setVisibility(View.VISIBLE);
				m_back.setVisibility(View.VISIBLE);
				m_playtitle.setVisibility(View.VISIBLE);
				m_playtitle2.setVisibility(View.VISIBLE);      			
				m_layout_seek.setVisibility(View.GONE);
				m_volume_select.setVisibility(View.GONE);

				if(GenieDlnaActionDefines.m_OnSlidePlay)
				{				
					SetSlidePlayView();
					m_play.setBackgroundResource(R.drawable.dlna_slidestop_select);
				}else
				{
					CanclSlidePlayView();
					m_play.setBackgroundResource(R.drawable.dlna_slideplaying_select);
				}
				
				break;
			default:
//				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//				{
//					m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg);
//				}else
//				{
//					m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg_h);
//				}
				m_play.setVisibility(View.VISIBLE);
				m_stop.setVisibility(View.VISIBLE);
				m_test1.setVisibility(View.VISIBLE);
				
				//m_pause.setVisibility(View.VISIBLE);
				m_pre.setVisibility(View.VISIBLE);
				m_back.setVisibility(View.VISIBLE);
				m_playtitle.setVisibility(View.VISIBLE);
				m_playtitle2.setVisibility(View.GONE);    			
				m_layout_seek.setVisibility(View.VISIBLE);
				m_volume_select.setVisibility(View.VISIBLE);
				GenieDlnaActionDefines.m_OnSlidePlay = false;
				break;
				
				}
    }
	
			
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		
		DismissPopVolumeView();
		
   	 if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE )
	  {
		 GenieDebug.error("onConfigurationChanged", "onConfigurationChanged --0--");
		 setContentView(R.layout.play_dlna_h);
		 InitView();
		 

		 
	  }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT )
	  {

	    	GenieDebug.error("onConfigurationChanged", "onConfigurationChanged --1--");
			
	    	setContentView(R.layout.play_dlna);
	    	InitView();

	  }
   	 
		onViewChanged();

		super.onConfigurationChanged(newConfig);
		
		
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
        //setTheme(R.style.activityTitlebarNoSearch);//qicheng.ai add        
        //requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			setContentView(R.layout.play_dlna);
		}else
		{
			setContentView(R.layout.play_dlna_h);			
		}
		
    	
        
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        
        
		
		
		//initPlayFieldAndMethod();
		
		InitTitleView();
		
		m_create = true;
		m_ispause = true;
		
		m_SpeedIndex = 1;
		
		m_ThumbnailPic.m_Thumbnailflag = false;
		m_ThumbnailPic.m_ThumbnailBitmap = null;
		m_ThumbnailPic.m_Thumbnailurl = null;
		
		//m_OnSlideModle = false;
		GenieDlnaActionDefines.m_OnSlidePlay = false;
		//SetOnSlidePlay();
		InitView();
		
		GenieDebug.error("debug","----GneieDlnaPlay- onCreate------ ");
		
		GenieDlnaActionDefines.m_PlayPosition =  0;
		
		//m_timestr = getTimeFormatValue((long)1000)+"/"+getTimeFormatValue((long)1000);
		//GenieDebug.error("debug","----onCreate- m_timestr = "+m_timestr);
		
		//SetViewOnPlay();
		
		GenieDlnaStatus.m_thisview = this;
		
		if(!GetWorkRenderStatus())
		{
			DisableView();
		}else
		{
			GenieDebug.error("debug","----onCreate- EnableView---in--- ");
			EnableView();
			
			
			int style = GetVideoStyle();
			
			//GenieDebug.error("debug","----onCreate- EnableView---style =  "+style);
		}
	}
	
	   @Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			//super.onBackPressed();
		   
		   
			   
				GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
				GenieDlnaTab.m_radio0.setChecked(true);
			
		}
	
	   public void InitTitleView()
		{
	    	if(GenieDlnaTab.m_about == null || GenieDlnaTab.m_back == null || GenieDlnaTab.m_dlnatitle == null)
	    	{
	    		return ;    		
	    	}
	    	GenieDlnaTab.m_dlnatitle.setText(R.string.playingtitle);
	    	GenieDlnaTab.m_about.setText(R.string.refresh);
	    	GenieDlnaTab.m_about.setVisibility(View.VISIBLE);
	    	
	    	
	    	GenieDlnaTab.m_back.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//GneieDlnaPlay.this.onBackPressed();
					GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
					GenieDlnaTab.m_radio0.setChecked(true);
				}
			});
			
	    	GenieDlnaTab.m_about.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					//StartSlidePlay();
				}
			});
			
			GenieDlnaTab.m_back.setOnTouchListener(new OnTouchListener(){      
	            @Override     
	            public boolean onTouch(View v, MotionEvent event) {      
	                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
	                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
	                           v.setBackgroundResource(R.drawable.title_bt_fj);
	                    		
	                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
	                            //锟斤拷为抬锟斤拷时锟斤拷图片      
	                            v.setBackgroundResource(R.drawable.title_bt_bj);
	                    		
	                    }      
	                    return false;      
	            }      
			});
			
			GenieDlnaTab.m_about.setOnTouchListener(new OnTouchListener(){      
	            @Override     
	            public boolean onTouch(View v, MotionEvent event) {      
	                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
	                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
	                            v.setBackgroundResource(R.drawable.title_bt_fj);
	                    		
	                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
	                            //锟斤拷为抬锟斤拷时锟斤拷图片      
	                            v.setBackgroundResource(R.drawable.title_bt_bj);
	                    		
	                    }      
	                    return false;      
	            }      
			});
		}
	
	
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		////
//		Intent slide = new Intent("com.dragonflow.GenieSlideService");
//		slide.putExtra(GenieGlobalDefines.SLIDE_ACTION, 5);
//		startService(slide);
//		GenieDebug.error("GenieDlnaTab", "startService(slide)");
//		
//		Intent slide2 = new Intent("com.dragonflow.GenieSlideService");
//		slide2.putExtra(GenieGlobalDefines.SLIDE_ACTION, 101);
//		startService(slide2);
//		GenieDebug.error("GenieDlnaTab", "startService(slide2)");
		///
		
		
		GenieDlnaStatus.m_thisview = this;
		
		
		
		//SetViewOnPlay();
		
		GenieDebug.error("debug","----GneieDlnaPlay- onResume------0 ");
		if(m_create)
		{
			GenieDebug.error("debug","----GneieDlnaPlay- onResume------ 1");
			m_create = false;
		}else
		{
			
			GenieDebug.error("debug","----GneieDlnaPlay- onResume------ 2");
			//initPlayFieldAndMethod();
			//InitView();	
			try{
			   	 if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE )
				  {
					 GenieDebug.error("onConfigurationChanged", "onConfigurationChanged --0--");
					 setContentView(R.layout.play_dlna_h);
					 InitView();
					 
	
					 
				  }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT )
				  {
	
				    	GenieDebug.error("onConfigurationChanged", "onConfigurationChanged --1--");
						
				    	setContentView(R.layout.play_dlna);
				    	InitView();
	
				  }
	
			   	GenieDebug.error("debug","----GneieDlnaPlay- onResume------ 3");
				onViewChanged();
				GenieDebug.error("debug","----GneieDlnaPlay- onResume------ 4");
			}catch(Exception e)
			{
				e.printStackTrace();
			}catch(Error e)
			{
				e.printStackTrace();
			}
			
		}
		
		GenieDebug.error("debug","----GneieDlnaPlay- onResume------ 5");
		InitTitleView();
		
		GenieDebug.error("debug","----GneieDlnaPlay- onResume------ 6");
		
		if(GetWorkRenderStatus())
		{		
			GenieDebug.error("debug","----onResume- EnableView---in--- ");
			EnableView();
			RefreshUi();
			
			
		}else
		{
			DisableView();
		}
		
		m_ispause = false;
		
		//CheckSourceAndRender();
		
	}
	
	private void CheckSourceAndRender()
	{
		GenieDebug.error("debug", "CheckSourceAndRender 0");
		if(CheckSoure())
		{
			GenieDebug.error("debug", "CheckSourceAndRender 1");
			CheckRender();
		}
		GenieDebug.error("debug", "CheckSourceAndRender 2");
	}
	
	
	private boolean CheckSoure()
	{
		if(GenieDlnaActionDefines.m_playItem == null)
		{
			ShowChooseSourceDialog();
			return false;
		}
		return true;
	}
	private void CheckRender()
	{
		if(null == GenieDlnaActionDefines.m_WorkRenderUUID
				|| !GenieDlnaActionDefines.findRenderByList(GenieDlnaActionDefines.m_WorkRenderUUID))
		{
			ShowRenderDialog();
		}
	}
	
	
	private void ShowChooseSourceDialog()
	{
		AlertDialog.Builder dialog_wifiset =  new AlertDialog.Builder(this);
		
		
		dialog_wifiset.setIcon(android.R.drawable.ic_dialog_alert);
		//dialog_wifiset.setTitle(" ");
		dialog_wifiset.setMessage(R.string.choosesource);
		dialog_wifiset.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
				GenieDlnaTab.m_radio0.setChecked(true);
			}
		});
		
		
		
		dialog_wifiset.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		
		
		
		dialog_wifiset.setCancelable(false);
		dialog_wifiset.show();
		
		
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		DismissPopVolumeView();
		
		if(null != settimer)
		{
			settimer.cancel();
			settimer = null;
		}
		if(null != task)
		{
			task = null;
		}
		GenieDebug.error("debug","999999999999 onPause call CanclSlidePlay ");
		//CanclSlidePlay();
		
		m_ispause = true;
	}
	
	
	public void DisableView()
	{
		synchronized("ui")
		{
			GenieDebug.error("debug"," 7777 DisableView");
			m_play.setEnabled(false);
			m_play.setBackgroundResource(R.drawable.play_play_invalid);
			m_stop.setEnabled(false);
			m_stop.setBackgroundResource(R.drawable.play_stop_invalid);
			//m_test1.setVisibility(View.VISIBLE);
			//m_pause.setEnabled(false);
			//m_pause.setBackgroundResource(R.drawable.play_pause_invalid);
			m_pre.setEnabled(false);
			m_pre.setBackgroundResource(R.drawable.play_pre_invalid);
			m_back.setEnabled(false);
			m_back.setBackgroundResource(R.drawable.play_back_invalid);
			m_playseek.setEnabled(false);
			//m_voiceseek.setEnabled(false);
		}
	}
	
	public void EnableView()
	{
		synchronized("ui")
		{
			GenieDebug.error("debug"," 7777 EnableView");
			m_play.setEnabled(true);
			//m_play.setBackgroundResource(R.drawable.play_play_off);
			m_play.setBackgroundResource(R.drawable.dlna_playing_select);
			
			m_stop.setEnabled(true);
			m_stop.setBackgroundResource(R.drawable.play_stop_off);
			//m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setEnabled(true);
			//m_pause.setBackgroundResource(R.drawable.play_pause_off);
			m_pre.setEnabled(true);
			m_pre.setBackgroundResource(R.drawable.play_pre_off);
			m_back.setEnabled(true);
			m_back.setBackgroundResource(R.drawable.play_back_off);
			m_playseek.setEnabled(true);
			//m_voiceseek.setEnabled(true);
		}
	}
	
	
	
	
	
	public int SetViewEnable(int Enable)
	{
		GenieDebug.error("debug","----SetViewEnable- Enable =  "+Enable);
		synchronized("SetViewEnable")
		{
			if(Enable == 1)
			{
				sendMessage2UI(MESSAGE_VIEWENABLED);
			}else
			{
				sendMessage2UI(MESSAGE_VIEWDISABLED);
			}
		}
		return 0;
	}
	
	
	
	
	
	
	
	public void SetPlaySeek()
	{
		if(null == m_playseek)
		{
			return ;
		}
		
    	synchronized("ui")
		{
    		//if (GenieDlnaStatus.m_playing == 1)
    		//{
    			m_playseek.setProgress(GenieDlnaActionDefines.m_PlayPosition);
    			
    			GenieDebug.error("debug","----SetPlaySeek- GenieDlnaActionDefines.m_PlayPosition =  "+GenieDlnaActionDefines.m_PlayPosition);
    			
    			//m_timestr = getTimeFormatValue((long)(GenieDlnaStatus.m_PlayPosition*1000))+"/"+getTimeFormatValue((long)(GenieDlnaStatus.m_duration*1000));
    			//GenieDebug.error("debug","----SetPlaySeek- m_timestr = "+m_timestr);
    			
    			//m_time.setText(m_timestr);    			
    			
    			m_timestr = getTimeFormatValue((long)(GenieDlnaActionDefines.m_PlayPosition*1000));
    			//m_totaltimestr = getTimeFormatValue((long)(GenieDlnaStatus.m_duration*1000));
    			
    			GenieDebug.error("debug","----SetPlaySeek- m_timestr = "+m_timestr);
    			m_time.setText(m_timestr);
    			//m_totaltime.setText(m_totaltimestr);
    		//}
		}
		
	}
//	public int SetSeekPosition(int position)
//	{
//		GenieDebug.error("debug","----SetSeekPosition- position =  "+position);
//    	synchronized("SetSeekPosition")
//		{
//    		GenieDlnaStatus.m_PlayPosition = position;
//    		
//    		GenieDebug.error("debug","----SetSeekPosition- GenieDlnaStatus.m_PlayPosition =  "+GenieDlnaStatus.m_PlayPosition);
//    		
//    		sendMessage2UI(MESSAGE_REFRESH_PLAYSEEK);
//		}
//		return 0;
//	}
	
	public void InitUi()
	{
		if(null == m_play || null == m_stop 
				|| null == m_pre
				|| null == m_back  || null == m_volume_select 
				|| null == m_playseek  //|| null == m_playbgpic
				|| GenieDlnaActionDefines.m_DLNARenderStatus == null)
		{
			return ;
		}
		
		
		synchronized("ui")
		{
    		
		//GenieDebug.error("debug","----RefreshUi---style =  "+style);	
    		
		
		//m_voiceseek.setMax(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_MaxVolume);
		//m_voiceseek.setProgress(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Volume);
		
		if(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Mute == 1)
		{
			//m_voiceseek.setEnabled(false);
			//m_voiceseek.setClickable(false);
			
			m_volume_select.setBackgroundResource(R.drawable.dlna_mute_select);
		}else
		{
			//m_voiceseek.setEnabled(true);
			//m_voiceseek.setClickable(true);
			
			m_volume_select.setBackgroundResource(R.drawable.dlna_volume_select);
		}
		

			
			GenieDlnaActionDefines.m_PlayPosition = 0; 
			
			m_play.setEnabled(true);
			//m_play.setBackgroundResource(R.drawable.play_play_off);
			m_play.setBackgroundResource(R.drawable.dlna_playing_select);
			m_stop.setEnabled(false);
			m_stop.setBackgroundResource(R.drawable.play_stop_invalid);
			m_test1.setVisibility(View.VISIBLE);
			//m_pause.setEnabled(false);
			//m_pause.setBackgroundResource(R.drawable.play_pause_invalid);
			
			
			m_pre.setEnabled(true);
			m_pre.setBackgroundResource(R.drawable.play_pre_off);
			m_back.setEnabled(true);
			m_back.setBackgroundResource(R.drawable.play_back_off);
			
			CanclePlayPositioninfo();
			sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAYPOSIREFRESH);
		
		
		if (null !=GenieDlnaActionDefines.m_DLNARenderStatus &&
				null !=GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport &&
				null !=GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus
				&& GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus.equals("ERROR_OCCURRED"))
		{
			//OnPlayFailed();
		}
		
		



		
		m_playseek.setMax((int)(GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis/1000));	
		
		//GenieDebug.error("debug","----RefreshUi- GenieDlnaStatus.m_PlayPosition =  "+GenieDlnaStatus.m_PlayPosition);
		
		m_timestr = getTimeFormatValue((long)(GenieDlnaActionDefines.m_PlayPosition*1000));
		m_totaltimestr = getTimeFormatValue(GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis);
		
		GenieDebug.error("debug","----RefreshUi- m_timestr = "+m_timestr);
		m_time.setText(m_timestr);
		m_totaltime.setText(m_totaltimestr);
		
		
		SetPlayTitle();
		


		
		int style  = GetVideoStyle();
		
		GenieDebug.error("debug","9999 style = "+style);
		
		switch(style)
		{
		case 1:
//			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg);
//			}else
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg_h);
//			}
			m_play.setVisibility(View.VISIBLE);
			m_stop.setVisibility(View.VISIBLE);
			m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setVisibility(View.VISIBLE);
			m_pre.setVisibility(View.VISIBLE);
			m_back.setVisibility(View.VISIBLE);
			m_playtitle.setVisibility(View.VISIBLE);
			
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle2.setVisibility(View.GONE);    			
			m_layout_seek.setVisibility(View.VISIBLE);
			m_volume_select.setVisibility(View.VISIBLE);
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			break;
		case 2:
//			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playmusic_bg);
//			}else
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playmusic_bg_h);
//			}
			m_play.setVisibility(View.VISIBLE);
			m_stop.setVisibility(View.VISIBLE);
			m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setVisibility(View.VISIBLE);
			m_pre.setVisibility(View.VISIBLE);
			m_back.setVisibility(View.VISIBLE);
			m_playtitle.setVisibility(View.VISIBLE);
			
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle2.setVisibility(View.GONE);    			
			m_layout_seek.setVisibility(View.VISIBLE);
			m_volume_select.setVisibility(View.VISIBLE);
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			break;
		case 3:
			
			m_play.setEnabled(true);
			
//			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playimage_bg);
//			}else
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playimage_bg_h);
//			}
			m_play.setVisibility(View.VISIBLE);
			m_stop.setVisibility(View.GONE);
			m_test1.setVisibility(View.GONE);
			
			//m_pause.setVisibility(View.INVISIBLE);
			m_pre.setVisibility(View.VISIBLE);
			m_back.setVisibility(View.VISIBLE);
			m_playtitle.setVisibility(View.VISIBLE);
			
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle2.setVisibility(View.VISIBLE);   			
			m_layout_seek.setVisibility(View.GONE);
			m_volume_select.setVisibility(View.GONE);
			
			
			
			
			if(GenieDlnaActionDefines.m_OnSlidePlay)
			{				
				SetSlidePlayView();
				m_play.setBackgroundResource(R.drawable.dlna_slidestop_select);
			}else
			{
				CanclSlidePlayView();
				m_play.setBackgroundResource(R.drawable.dlna_slideplaying_select);
			}
			
		
			
			break;
		default:
//			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg);
//			}else
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg_h);
//			}
			m_play.setVisibility(View.VISIBLE);
			m_stop.setVisibility(View.VISIBLE);
			m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setVisibility(View.VISIBLE);
			m_pre.setVisibility(View.VISIBLE);
			m_back.setVisibility(View.VISIBLE);
			m_playtitle.setVisibility(View.VISIBLE);
			
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle2.setVisibility(View.GONE);    			
			m_layout_seek.setVisibility(View.VISIBLE);
			m_volume_select.setVisibility(View.VISIBLE);
			
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			break;
			
			}
		
		}
	}
	
	public void RefreshUi()
	{
				
		if(null == m_play || null == m_stop 
				|| null == m_pre
				|| null == m_back|| null == m_volume_select 
				|| null == m_playseek  //|| null == m_playbgpic
				|| GenieDlnaActionDefines.m_DLNARenderStatus == null)
		{
			return ;
		}
    	synchronized("ui")
		{
    		
		//GenieDebug.error("debug","----RefreshUi---style =  "+style);	
    		
		
		//m_voiceseek.setMax(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_MaxVolume);
		//m_voiceseek.setProgress(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Volume);
		
		if(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Mute == 1)
		{
			//m_voiceseek.setEnabled(false);
			//m_voiceseek.setClickable(false);
			m_volume_select.setBackgroundResource(R.drawable.dlna_mute_select);
		}else
		{
			//m_voiceseek.setEnabled(true);
			//m_voiceseek.setClickable(true);
			m_volume_select.setBackgroundResource(R.drawable.dlna_volume_select);
		}
		
		if (null != GenieDlnaActionDefines.m_DLNARenderStatus &&
				null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport
				&& null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState && (GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PLAYING")
				|| GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("TRANSITIONING"))) {
			
			m_play.setEnabled(true);
			m_play.setBackgroundResource(R.drawable.dlna_pause_select);
			m_stop.setEnabled(true);
			m_stop.setBackgroundResource(R.drawable.play_stop_off);
			m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setEnabled(true);
			//m_pause.setBackgroundResource(R.drawable.play_pause_off);
			m_pre.setEnabled(true);
			m_pre.setBackgroundResource(R.drawable.play_pre_off);
			m_back.setEnabled(true);
			m_back.setBackgroundResource(R.drawable.play_back_off);
			
			GetPlayPositioninfo();
			sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAYPOSIREFRESH);
			
		} else if (null != GenieDlnaActionDefines.m_DLNARenderStatus &&
				null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport
				&& null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState
				&& GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PAUSED_PLAYBACK")) {
			
			m_play.setEnabled(true);
			//m_play.setBackgroundResource(R.drawable.play_play_off);
			m_play.setBackgroundResource(R.drawable.dlna_playing_select);
			m_stop.setEnabled(true);
			m_stop.setBackgroundResource(R.drawable.play_stop_off);
			m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setEnabled(false);
			//m_pause.setBackgroundResource(R.drawable.play_pause_invalid);
			m_pre.setEnabled(true);
			m_pre.setBackgroundResource(R.drawable.play_pre_off);
			m_back.setEnabled(true);
			m_back.setBackgroundResource(R.drawable.play_back_off);
			
			
		} else {
			
			GenieDlnaActionDefines.m_PlayPosition = 0; 
			
			m_play.setEnabled(true);
			//m_play.setBackgroundResource(R.drawable.play_play_off);
			m_play.setBackgroundResource(R.drawable.dlna_playing_select);
			m_stop.setEnabled(false);
			m_stop.setBackgroundResource(R.drawable.play_stop_invalid);
			m_test1.setVisibility(View.VISIBLE);
			//m_pause.setEnabled(false);
			//m_pause.setBackgroundResource(R.drawable.play_pause_invalid);
			
			
			m_pre.setEnabled(true);
			m_pre.setBackgroundResource(R.drawable.play_pre_off);
			m_back.setEnabled(true);
			m_back.setBackgroundResource(R.drawable.play_back_off);
			
			CanclePlayPositioninfo();
			sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAYPOSIREFRESH);
		}
		
		if (null != GenieDlnaActionDefines.m_DLNARenderStatus &&
				null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport
				&& null !=GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus
				&& GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportStatus.equals("ERROR_OCCURRED"))
		{
			//OnPlayFailed();
		}
		
		

		
//		if(m_errorinfo != null)
//		{
//			if(GenieDlnaStatus.m_Status == 1)
//			{
//				m_errorinfo.setText("Error !");
//				m_errorinfo.setVisibility(View.VISIBLE);
//			}else
//			{
//				m_errorinfo.setVisibility(View.INVISIBLE);
//			}
//		}
		

		if(null != GenieDlnaActionDefines.m_DLNARenderStatus &&
				null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport)
				m_playseek.setMax((int)(GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis/1000));	
		
		//GenieDebug.error("debug","----RefreshUi- GenieDlnaStatus.m_PlayPosition =  "+GenieDlnaStatus.m_PlayPosition);
		
		m_timestr = getTimeFormatValue((long)(GenieDlnaActionDefines.m_PlayPosition*1000));
		m_totaltimestr = getTimeFormatValue(GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TrackDurationInMillis);
		
		GenieDebug.error("debug","----RefreshUi- m_timestr = "+m_timestr);
		m_time.setText(m_timestr);
		m_totaltime.setText(m_totaltimestr);
		
		
		//SetPlayTitle();
		
		
		//StartSlidePlay();
		
		//onViewChanged();
		

		
		int style  = GetVideoStyle();
		
		GenieDebug.error("debug","9999 style = "+style);
		
		switch(style)
		{
		case 1:
//			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg);
//			}else
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg_h);
//			}
			m_play.setVisibility(View.VISIBLE);
			m_stop.setVisibility(View.VISIBLE);
			m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setVisibility(View.VISIBLE);
			m_pre.setVisibility(View.VISIBLE);
			m_back.setVisibility(View.VISIBLE);
			m_playtitle.setVisibility(View.VISIBLE);
			
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle2.setVisibility(View.GONE);    			
			m_layout_seek.setVisibility(View.VISIBLE);
			m_volume_select.setVisibility(View.VISIBLE);
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			break;
		case 2:
//			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playmusic_bg);
//			}else
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playmusic_bg_h);
//			}
			m_play.setVisibility(View.VISIBLE);
			m_stop.setVisibility(View.VISIBLE);
			m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setVisibility(View.VISIBLE);
			m_pre.setVisibility(View.VISIBLE);
			m_back.setVisibility(View.VISIBLE);
			m_playtitle.setVisibility(View.VISIBLE);
			
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle2.setVisibility(View.GONE);    			
			m_layout_seek.setVisibility(View.VISIBLE);
			m_volume_select.setVisibility(View.VISIBLE);
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			break;
		case 3:
			
			m_play.setEnabled(true);
			
//			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playimage_bg);
//			}else
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playimage_bg_h);
//			}
			m_play.setVisibility(View.VISIBLE);
			m_stop.setVisibility(View.GONE);
			m_test1.setVisibility(View.GONE);
			
			//m_pause.setVisibility(View.INVISIBLE);
			m_pre.setVisibility(View.VISIBLE);
			m_back.setVisibility(View.VISIBLE);
			m_playtitle.setVisibility(View.VISIBLE);
			
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle2.setVisibility(View.VISIBLE);   			
			m_layout_seek.setVisibility(View.GONE);
			m_volume_select.setVisibility(View.GONE);
			
			
			
			
			if(GenieDlnaActionDefines.m_OnSlidePlay)
			{				
				SetSlidePlayView();
				m_play.setBackgroundResource(R.drawable.dlna_slidestop_select);
			}else
			{
				CanclSlidePlayView();
				m_play.setBackgroundResource(R.drawable.dlna_slideplaying_select);
			}
			break;
		default:
//			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg);
//			}else
//			{
//				m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg_h);
//			}
			m_play.setVisibility(View.VISIBLE);
			m_stop.setVisibility(View.VISIBLE);
			m_test1.setVisibility(View.VISIBLE);
			
			//m_pause.setVisibility(View.VISIBLE);
			m_pre.setVisibility(View.VISIBLE);
			m_back.setVisibility(View.VISIBLE);
			m_playtitle.setVisibility(View.VISIBLE);
			
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle2.setVisibility(View.GONE);    			
			m_layout_seek.setVisibility(View.VISIBLE);
			m_volume_select.setVisibility(View.VISIBLE);
			
			GenieDlnaActionDefines.m_OnSlidePlay = false;
			break;
			
			}
		
		}
	}

	
	
	int refreshUidata(int min_value,int max_value,int value,int muted,int playing,int paused,int duration,int Status)
	{
		GenieDebug.error("debug","----refreshUidata- min_value =  "+min_value);
		GenieDebug.error("debug","----refreshUidata- max_value =  "+max_value);
		GenieDebug.error("debug","----refreshUidata- value =  "+value);
		GenieDebug.error("debug","----refreshUidata- muted =  "+muted);
		GenieDebug.error("debug","----refreshUidata- playing =  "+playing);
		GenieDebug.error("debug","----refreshUidata- paused =  "+paused);
		GenieDebug.error("debug","----refreshUidata- duration =  "+duration);
		GenieDebug.error("debug","----refreshUidata- Status =  "+Status);
		
		
    	synchronized("RefreshSaverDirList")
		{
    		GenieDlnaStatus.m_min_value = min_value;
    		GenieDlnaStatus.m_max_value = max_value;
    		GenieDlnaStatus.m_value = value;
    		GenieDlnaStatus.m_muted = muted;
    		GenieDlnaStatus.m_playing = playing;
    		GenieDlnaStatus.m_paused = paused;
    		GenieDlnaStatus.m_duration = duration;
    		GenieDlnaStatus.m_Status = Status;
    		
    		sendMessage2UI(MESSAGE_REFRESH);
		}
    	
		return 0;
	}
	
	
	void CanclePlayPositioninfo()
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
	
	public void sendbroad(int value)
	{
		GenieDebug.error("sendbroad", "GenieDlnaplay---- sendbroad value ="+value);
		Intent slide = new Intent(GenieGlobalDefines.DLNA_ACTION_BROADCAST);
		slide.putExtra(GenieGlobalDefines.DLNA_ACTION_RET, value);
		sendBroadcast(slide);
	}
	
	void GetPlayPositioninfo()
	{
		
		CanclePlayPositioninfo();	
		
		index = 0;
		
		settimer  = new Timer(); 
		task = new TimerTask()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				int style = GetVideoStyle();
				if(3 == style)
					return;
				
				if (null != GenieDlnaActionDefines.m_DLNARenderStatus &&
						null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport && 
						null !=GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState
						&& GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PLAYING"))
				{
					StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_QUERYPOSIINFO);
					
//					index += 1;
//					
//					if(index == 2)
//					{
//						StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_QUERYPOSIINFO);
//						index = 0;
//					}else
//					{
//						GenieDlnaActionDefines.m_PlayPosition += 1;
//						sendbroad(GenieDlnaActionDefines.ACTION_RET_PLAYPOSIREFRESH);
//						
//					}
				}				
				
			}
			
		};
		
		
		settimer.schedule(task,1000,1000);
	}
	
	private void SetSlidePlayView()
	{

		if(null != m_pre)
		{
			m_pre.setEnabled(false);
			m_pre.setBackgroundResource(R.drawable.play_pre_invalid);
		}
		
		if(null != m_back)
		{
			m_back.setEnabled(false);
			m_back.setBackgroundResource(R.drawable.play_back_invalid);	
		}
		if(null != m_SlideSpeed)
		{
			m_SlideSpeed.setEnabled(false);
		}
			
		
		
			
	}
	private void CanclSlidePlayView()
	{
		
		if(null != m_pre)
		{
			m_pre.setEnabled(true);
			m_pre.setBackgroundResource(R.drawable.play_pre_off);
		}
		
		if(null != m_back)
		{
			m_back.setEnabled(true);
			m_back.setBackgroundResource(R.drawable.play_back_off);	
		}
		if(null != m_SlideSpeed)
		{
			m_SlideSpeed.setEnabled(true);
		}
	}
	
	public void SetPlayTitle()
	{
		m_title = GetVideoTitle();
		
		GenieDebug.error("debug","GetVideoTitle = "+m_title);
		
		if(null != m_title && null != m_playtitle)
		{
			m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			m_playtitle.setSingleLine(true);
			m_playtitle.setEllipsize(TruncateAt.MARQUEE);
			m_playtitle.setMarqueeRepeatLimit(-1);
			m_playtitle.setFocusable(true);
			
			m_playtitle.setText(m_title);
			
		}
	}
	
	private ImageView m_test1 = null;
	
	void InitView()
	{
		
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 0");

		m_test1 = (ImageView)findViewById(R.id.Button_play_test2);
		
		m_thumbnail = (ImageView)findViewById(R.id.play_thumbnail);
		m_play = (ImageButton)findViewById(R.id.Button_play);
		m_stop = (Button)findViewById(R.id.Button_stop);
		//m_pause = (Button)findViewById(R.id.Button_pause);
		m_pre = (Button)findViewById(R.id.Button_pre);
		m_back = (Button)findViewById(R.id.Button_back);
		m_playseek = (SeekBar)findViewById(R.id.play_seek);
		//m_voiceseek = (SeekBar)findViewById(R.id.voice_seek);
		//m_voiceimage = (Button)findViewById(R.id.ImageView_music);
		m_time = (TextView)findViewById(R.id.playtime);
		m_totaltime = (TextView)findViewById(R.id.playtime_total);
		
		
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 1");
		
		//m_playbgpic =  (ImageView)findViewById(R.id.play_bg_pic);
		//m_errorinfo = (TextView)findViewById(R.id.errorinfo);
		//m_playbgpic.setBackgroundResource(R.drawable.playvideo_bg);
		
		m_layout_seek = (LinearLayout)findViewById(R.id.Layout_play_seek);
		//m_layout_vulme = (LinearLayout)findViewById(R.id.Layout_play_vulme);
		
		
		m_SlideSpeed = (Spinner)findViewById(R.id.slidespeed);
	 	
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 2");
		
		m_title = GetVideoTitle();
		
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 3");
		
		m_playtitle = (MarqueeTextView)findViewById(R.id.play_title);
		m_playtitle2 = (LinearLayout)findViewById(R.id.play_title2);
		
		
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 4");
		
//		m_playtitle.setTransformationMethod(SingleLineTransformationMethod.getInstance());
//		m_playtitle.setSingleLine(true);
//		m_playtitle.setEllipsize(TruncateAt.MARQUEE);
//		m_playtitle.setMarqueeRepeatLimit(-1);
//		m_playtitle.setFocusable(true);
//		
//		m_playtitle2.setTransformationMethod(SingleLineTransformationMethod.getInstance());
//		m_playtitle2.setSingleLine(true);
//		m_playtitle2.setEllipsize(TruncateAt.MARQUEE);
//		m_playtitle2.setMarqueeRepeatLimit(-1);
//		m_playtitle2.setFocusable(true);
		
		
		
//		if(null != m_title && null != m_playtitle)
//		{
//			m_playtitle.setText(m_title);
//			
//		}
		
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 5");
		
		SetPlayTitle();
		
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 6");
		
		
		
		
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 7");
		
//		m_play.setOnTouchListener(new OnTouchListener(){      
//            @Override     
//            public boolean onTouch(View v, MotionEvent event) {   
//            		int style = GetVideoStyle();
//            		
//                    if(event.getAction() == MotionEvent.ACTION_DOWN){ 
//                    	
//                    	if(3 == style)
//                		{	
//                			if(GenieDlnaActionDefines.m_OnSlidePlay)
//                			{
//                				v.setBackgroundResource(R.drawable.play_stop_on);
//                			}else
//                			{
//                				v.setBackgroundResource(R.drawable.play_play_on);
//                			}
//                		}else
//                		{
//                			//锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
//                			v.setBackgroundResource(R.drawable.play_play_on);
//                		}
//                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
//                    	if(3 == style)
//                		{
//                    		if(GenieDlnaActionDefines.m_OnSlidePlay)
//                			{
//                    			v.setBackgroundResource(R.drawable.play_play_off);
//                			}else
//                			{
//                				v.setBackgroundResource(R.drawable.play_stop_off);
//                			}
//                		}else
//                		{                    	
//                    		//锟斤拷为抬锟斤拷时锟斤拷图片      
//                            v.setBackgroundResource(R.drawable.play_play_off);
//                		}
//                    }      
//                    return false;      
//            }      
//		});
		m_play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int style = GetVideoStyle();
				
				GenieDebug.error("onClick", "onClick play style ="+style);
				if(3 == style)
				{
					if(GenieDlnaActionDefines.m_OnSlidePlay)
					{
						CanclSlidePlay();
						
						//GenieDlnaStatus.m_OnSlidePlay = false;
					}else
					{
						StartSlidePlay();
						//GenieDlnaStatus.m_OnSlidePlay = true;
					}
				}else
				{
					GenieDebug.error("debug","m_TransportState = "+GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState);
					if (null != GenieDlnaActionDefines.m_DLNARenderStatus &&
							null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport
							&& null != GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState
							&& (GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PLAYING")
									|| GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("TRANSITIONING"))) 
					{
						StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE);
					}else
					{
						StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY);
					}
				}
			}
		});
		
		GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 8");
		
		m_stop.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
                            v.setBackgroundResource(R.drawable.play_stop_on);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //锟斤拷为抬锟斤拷时锟斤拷图片      
                            v.setBackgroundResource(R.drawable.play_stop_off);
                    		
                    }      
                    return false;      
            }      
		});
		
		m_stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP);
			}
		});
		/*
		m_pause.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
                            v.setBackgroundResource(R.drawable.play_pause_on);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //锟斤拷为抬锟斤拷时锟斤拷图片      
                            v.setBackgroundResource(R.drawable.play_pause_off);
                    		
                    }      
                    return false;      
            }      
		});
		
		m_pause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE);
			}
		});
		*/		
		
		m_pre.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
                            v.setBackgroundResource(R.drawable.play_pre_on);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //锟斤拷为抬锟斤拷时锟斤拷图片      
                            v.setBackgroundResource(R.drawable.play_pre_off);
                    		
                    }      
                    return false;      
            }      
		});
		m_pre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT);
				
			}
		});
		
		m_back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
                            v.setBackgroundResource(R.drawable.play_back_on);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //锟斤拷为抬锟斤拷时锟斤拷图片      
                            v.setBackgroundResource(R.drawable.play_back_off);
                    		
                    }      
                    return false;      
            }      
		});
		
		
		m_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV);
			}
		});
		
		
		
		
		m_playseek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				GenieDebug.error("debug","onStopTrackingTouch progress = "+m_playseek.getProgress());
				
				if(GenieDlnaActionDefines.m_DLNARenderStatus != null &&
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport != null &&
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState != null &&
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PLAYING"))
				{
					GenieDlnaActionDefines.m_SeekTimeInMillis = (m_playseek.getProgress()*1000);
					StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK);
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				GenieDebug.error("debug","onStartTrackingTouch progress = "+m_playseek.getProgress());
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				GenieDebug.error("debug","onProgressChanged progress = "+progress);
				
			}
		});
		
		

		
		
		
//		for(int i=0;i<m_Speed_s.length;i++)
//		{
//			m_Speed[i]=getResources().getString(m_Speed_s[i]).toString();
//			GenieDebug.error("debug","speed = "+m_Speed[i]);
//		}
		
		m_Speedadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,m_Speed);
        
		
        // 锟斤拷锟斤拷锟斤拷锟斤拷锟叫憋拷锟斤拷
		m_Speedadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //锟斤拷Adapter锟斤拷拥锟絘dapter_01
    	m_SlideSpeed.setAdapter(m_Speedadapter);
    	
    	m_SlideSpeed.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				m_SpeedIndex = arg2;
				GenieDebug.error("debug","m_SlideSpeed  m_SpeedIndex = "+m_SpeedIndex);
				arg0.setVisibility(View.VISIBLE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 9");
    	
    	if(m_SpeedIndex >= 0 && m_SpeedIndex <= 2)
    	{
    		m_SlideSpeed.setSelection(m_SpeedIndex, true);
    	}else
    	{
    		m_SlideSpeed.setSelection(1, true);
    	}
    	
    	GetPlayThumbnail(0);
    	
    	GenieDebug.error("debug","----GneieDlnaPlay- InitView------ 10");
    	
    	
    	///////////test/////////
    	
    	
    	
    	m_volume_select = (ImageButton)findViewById(R.id.volume_select);
    	
    	m_volume_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopVolumeView();
			}
		});
    	
    	DismissPopVolumeView();
    	/////////////////////////
	}
	
//	m_voiceimage.setOnClickListener(new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			
//			GenieDebug.error("debug", "onClick GenieDlnaStatus.m_muted = "+GenieDlnaStatus.m_muted);
//			showWaitingDialog();
//			if(null != GenieDlnaActionDefines.m_DLNARenderStatus &&
//					null != GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl )
//			{
//				if(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Mute == 1)
//				{
//					GenieDebug.error("debug", "onClick OnRenderSetMute(0)");
//					GenieDlnaActionDefines.m_Mute = false;
//					StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE);
//				}else
//				{
//					GenieDebug.error("debug", "onClick OnRenderSetMute(1)");
//					GenieDlnaActionDefines.m_Mute = true;
//					StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE);
//				}
//			}
//		}
//	});
//	
//	
//	m_voiceseek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//		
//		@Override
//		public void onStopTrackingTouch(SeekBar seekBar) {
//			// TODO Auto-generated method stub
//			GenieDebug.error("debug","onStopTrackingTouch progress = "+m_playseek.getProgress());
//			
//			if(GenieDlnaActionDefines.m_DLNARenderStatus != null &&
//					GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport != null &&
//					GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState != null &&
//					GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PLAYING"))
//			{
//				GenieDlnaActionDefines.m_Volume = m_voiceseek.getProgress();
//				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME);
//			}
//		}
//		
//		@Override
//		public void onStartTrackingTouch(SeekBar seekBar) {
//			// TODO Auto-generated method stub
//			GenieDebug.error("debug","onStartTrackingTouch progress = "+m_playseek.getProgress());
//		}
//		
//		@Override
//		public void onProgressChanged(SeekBar seekBar, int progress,
//				boolean fromUser) {
//			// TODO Auto-generated method stub
//			
//			GenieDebug.error("debug","onProgressChanged progress = "+progress);
//			
//		}
//	});
	
	////////test/////////
	
	private PopupWindow m_volumepopupwindow = null;
	
	private void DismissPopVolumeView()
	{
		if(m_volumepopupwindow != null && m_volumepopupwindow.isShowing())
		{
			m_volumepopupwindow.dismiss();
			//m_volumepopupwindow = null; //new PopupWindow(GneieDlnaPlay.this);
		}
	}
	private void PopVolumeView()
	{
		
		DismissPopVolumeView();
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.playing_volume,null);
		
		
		//popupwindow.setContentView(view);

		//VerticalSeekBar seekbar = (VerticalSeekBar)view.findViewById(R.id.volumeseekbar);
		
		SeekBar seekbar = (SeekBar)view.findViewById(R.id.volumeseekbar);
		
		GenieDebug.error("debug", "PopVolumeView m_MaxVolume = "+GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_MaxVolume);
		GenieDebug.error("debug", "PopVolumeView m_Volume = "+GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Volume);
		
		if(null != GenieDlnaActionDefines.m_DLNARenderStatus
				&& null != GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl)
		{
			seekbar.setMax(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_MaxVolume);
			seekbar.setProgress(GenieDlnaActionDefines.m_DLNARenderStatus.m_RenderingControl.m_Volume);
		}
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
				GenieDebug.error("debug","seekbar progress = "+seekBar.getProgress());
				
				if(GenieDlnaActionDefines.m_DLNARenderStatus != null &&
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport != null &&
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState != null &&
						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PLAYING"))
				{
					GenieDlnaActionDefines.m_Volume = seekBar.getProgress();
					StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME);
				}
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
//		seekbar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
//			
//
//			
//			@Override
//			public void onProgressChanged(VerticalSeekBar VerticalSeekBar,
//					int progress, boolean fromUser) {
//				// TODO Auto-generated method stub
//				
//				GenieDebug.error("debug", "VerticalSeekBar onProgressChanged progress = "+progress);
//				GenieDebug.error("debug", "VerticalSeekBar onProgressChanged fromUser = "+fromUser);
//				
//				GenieDebug.error("debug","VerticalSeekBar progress = "+VerticalSeekBar.getProgress());
//				
//				if(GenieDlnaActionDefines.m_DLNARenderStatus != null &&
//						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport != null &&
//						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState != null &&
//						GenieDlnaActionDefines.m_DLNARenderStatus.m_AVTransport.m_TransportState.equals("PLAYING"))
//				{
//					GenieDlnaActionDefines.m_Volume = VerticalSeekBar.getProgress();
//					StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME);
//				}
//			}
//
//			@Override
//			public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {
//				// TODO Auto-generated method stub
//				GenieDebug.error("debug", "VerticalSeekBar onProgressChanged ");
//			}
//
//			@Override
//			public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {
//				// TODO Auto-generated method stub
//				GenieDebug.error("debug", "VerticalSeekBar onProgressChanged ");
//			}
//		});
		


		//popupwindow.showAtLocation(getWindow().getDecorView(),Gravity.TOP,0,0);
		
		m_volumepopupwindow=new PopupWindow(view,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT,true);
       
		m_volumepopupwindow.setFocusable(true);

		m_volumepopupwindow.setTouchable(true);

		m_volumepopupwindow.setOutsideTouchable(true);
		
		m_volumepopupwindow.setBackgroundDrawable(new BitmapDrawable());
		
		//Button test2 = (Button)findViewById(R.id.testbutton2);
//		
//		int[] location = new  int[2] ;
//		//test2.getLocationInWindow(location); //鑾峰彇鍦ㄥ綋鍓嶇獥鍙ｅ唴鐨勭粷瀵瑰潗鏍�
//		test2.getLocationOnScreen(location);//鑾峰彇鍦ㄦ暣涓睆骞曞唴鐨勭粷瀵瑰潗鏍�
//
//		GenieDebug.error("debug","location[0] = "+location[0]);
//		GenieDebug.error("debug","location[1] = "+location[1]);
//		
//		popupwindow.showAtLocation(getWindow().getDecorView(),Gravity.BOTTOM,location[0],location[1]);
//		

		//if(null != m_volume_select)
		
		LayoutInflater inflater2 = LayoutInflater.from(this);
		
		
		
		
		
		m_volumepopupwindow.showAsDropDown(findViewById(R.id.view_top));
	}
	
	
	/////////////////////
	private  String getTimeFormatValue(long time) {
		return MessageFormat.format(
				"{0,number,00}:{1,number,00}:{2,number,00}",
				time / 1000 / 60 / 60, time / 1000 / 60 % 60, time / 1000 % 60);
	}
	
	private void StartDlnaAction(int action)
	{		
		
		GenieDebug.error("debug","9999df999999 StartDlnaAction action = "+action);
		
		Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
		Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,action);
		startService(Dlna);
		GenieDebug.error("debug","9999df999999 StartDlnaAction end");
		
		

	}
	
	public  String GetVideoTitle()
	{
		if(GenieDlnaActionDefines.m_playItem != null)
		{
			return GenieDlnaActionDefines.m_playItem.getTitle();
		}
		return null;
	}
	
	
	public  int GetVideoStyle()
	{
		GenieDebug.error("debug","GetVideoStyle 0");
		if(GenieDlnaActionDefines.m_playItem != null)
		{
			GenieDebug.error("debug","GetVideoStyle 1");
			
			GenieDebug.error("debug","GetVideoStyle 1 getParentId = "+GenieDlnaActionDefines.m_playItem.getParentId());

			
			String upnpclass = GenieDlnaActionDefines.m_playItem.getUpnpClass();
			
			GenieDebug.error("debug","GetVideoStyle 2");
			if(upnpclass.startsWith("object.item.videoItem"))
			{
				return 1;
			}else if(upnpclass.startsWith("object.item.audioItem"))
			{
				return 2;
			}
			else if(upnpclass.startsWith("object.item.imageItem"))
			{
				return 3;
			}
			GenieDebug.error("debug","GetVideoStyle 3");
		}
		GenieDebug.error("debug","GetVideoStyle 4");
		return -1;
	}
	
	private  ControlActionProgressDialog m_ControlActionProgressDialog = null;
	 public void CancleControlActionProgressDialog()
	    {
	    	if(null != m_ControlActionProgressDialog)
	    	{
	    		if(m_ControlActionProgressDialog.isShowing())
	    			m_ControlActionProgressDialog.dismiss();
	    		m_ControlActionProgressDialog = null;
	    	}
	    }
	    
	    
	    public void ShowControlActionProgressDialog(int action)
	    {
	    	CancleControlActionProgressDialog();
	    	
	    	m_ControlActionProgressDialog = new ControlActionProgressDialog(this,action);
	    	m_ControlActionProgressDialog.setMessage("Please wait...");
	    	m_ControlActionProgressDialog.show();
	    }
	
	class ControlActionProgressDialog extends ProgressDialog{

		private int m_Action = -1;
		public ControlActionProgressDialog(Context context ,int action) {
			super(context);
			
			m_Action = action;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void dismiss() {
			// TODO Auto-generated method stub
			super.dismiss();
			
		}

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			
			switch(m_Action)
			{
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_SHOW:		
				//sendMessage2UI(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_SHOW);
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_SHOW:
				//sendMessage2UI(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_SHOW);
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_SHOW:
				//sendMessage2UI(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_SHOW);
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_SHOW:
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_SHOW:
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_SHOW:
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_SHOW:
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_CANCLE);
				break;	
			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_SHOW:
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_CANCLE);
				break;	
			}
			
			//StartDlnaAction(GenieDlnaActionDefines.ACTION_PLAYMEDIAITEM_CANCLE);
			
			GenieDebug.error("debug", "PlayItemProgressDialog onBackPressed");
		}
		
		
		
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
			registerReceiver(m_DLNAReceiver, filter);//
	 }

	
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
    	
		super.onStart();
					
		RegisterBroadcastReceiver();
	
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		UnRegisterBroadcastReceiver();
		
		m_ispause = true;
	}
	
	private class DLNAReceiver extends BroadcastReceiver{//


        @Override
	        public void onReceive(Context context, Intent intent) {//
    
       	 int action = -1;
    		
       	 	
    		action = intent.getIntExtra(GenieGlobalDefines.DLNA_ACTION_RET,-1);
    		
    		GenieDebug.error("DLNAReceiver", "GneieDlnaPlay onReceive DLNA_ACTION_RET ="+action);
    		//if(m_ispause)
    		//	return;
    		sendMessage2UI(action);
//    		switch(action)
//    		{
//    		case GenieDlnaActionDefines.ACTION_RET_CONTROLPROGRESSDIALOG_CANCLE:
//    			sendMessage2UI(action);
//    			//CancleControlActionProgressDialog();
//    			break;
//    		case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PLAY_SHOW:				
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_STOP_SHOW:			
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PAUSE_SHOW:				
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_PREV_SHOW:				
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_NEXT_SHOW:					
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_MUTE_SHOW:				
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_CHANGEVOLUME_SHOW:				
//			case GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SEEK_SHOW:
//				ShowControlActionProgressDialog(action);
//				break;
//			case GenieDlnaActionDefines.ACTION_CONTROL_VIEW_REFRESH:
//				RefreshUi();
//				break;
//			case GenieDlnaActionDefines.ACTION_RET_PLAYPOSIREFRESH:
//				SetPlaySeek();
//				break;
//    		}

        }
      }
	
	 private DLNAReceiver m_DLNAReceiver = null;

	 public ListView m_renderlist = null;
		public EfficientAdapter_render m_renderlistItemAdapter = null;
		public ArrayList<GenieDlnaRenderListItem> m_renderlistdata = null;
		private int  m_chooseindex = -1;
		private Dialog m_RenderDialog = null;
		
	    private void ShowRenderDialog()
	    {
	    	GenieDebug.error("debug","ShowRenderDialog start");
	    	
	    	if(GenieDlnaActionDefines.m_Rendererlist.isEmpty())
	    	{
	    		GenieDebug.error("debug","ShowRenderDialog GenieDlnaActionDefines.m_Rendererlist.isEmpty()");
	    		return ;
	    	}    	
	    	
	    	if(m_renderlistdata == null)
	    	{
	    		m_renderlistdata = new  ArrayList<GenieDlnaRenderListItem>();
	    	}else
	    	{
	    		m_renderlistdata.clear(); 
	    	}
	    	int n = 0;
	    	m_chooseindex = 0;
	    	for (DeviceDesc desc : GenieDlnaActionDefines.m_Rendererlist)
     	{
 			GenieDlnaRenderListItem temp = new GenieDlnaRenderListItem();
 			temp.m_FriendlyName = desc.getFriendlyName();
 			
 			if(desc.getIconCount() > 0)
     		{
     			temp.m_iconflag = true;
     			temp.m_icon = desc.getIcon(0).getIconData();
     		}else
     		{
     			temp.m_iconflag = false;
     		}
 			
 			
// 			GenieDebug.error("debug","desc.getUuid() ="+desc.getUuid().toString());
// 			if(null != GenieDlnaActionDefines.m_WorkRenderUUID)
// 			{
// 				
// 				GenieDebug.error("debug","GenieDlnaActionDefines.m_WorkRenderUUID ="+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
// 				if(desc.getUuid().equals(GenieDlnaActionDefines.m_WorkRenderUUID))
// 				{
// 					GenieDebug.error("debug","m_chooseIndex ="+n);
// 					m_chooseindex = n;
// 					temp.m_select = true;
// 				}else
// 				{
// 					temp.m_select = false;
// 				}
// 				
// 			}else
// 			{
// 				temp.m_select = false;
// 				GenieDebug.error("debug","null == m_workrender ");
// 			}
 			//n++;
 			
 			temp.m_select = false;
 			m_renderlistdata.add(temp);

     		
 		}
 		
 	
	    	if(null != m_renderlistItemAdapter)
	    	{
	    		m_renderlistItemAdapter = null;
	    	}
	    	
	    	
	    		
	    	if(m_RenderDialog != null)
	    	{
	    		m_RenderDialog.dismiss();
	    		m_RenderDialog = null;
	    	}
	    	
	    	
	    	
	    	
	    	

			View view = LayoutInflater.from(this).inflate(R.layout.renderview,null);
	    	
			m_renderlist = (ListView)view.findViewById(R.id.ListView01);
			
			m_renderlistItemAdapter = new EfficientAdapter_render(this);
			m_renderlist.setAdapter(m_renderlistItemAdapter);
			
		    m_renderlist.setItemsCanFocus(false);
		    m_renderlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		    m_renderlist.setItemChecked(-1, true);
		    
		    
		    m_renderlist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					
					GenieDebug.error("onItemClick", "onItemClick arg3 = "+arg3);
					
					
					
					int size = m_renderlistdata.size();
					
					GenieDebug.error("onItemClick", "onItemClick size = "+size);
					
					if(arg3 >= 0 && arg3 < size)
					{
						int Index = (int)arg3;
						
						GenieDebug.error("onItemClick", "onItemClick Index = "+Index);
						
						//if(Index >=0 && Index < size)
						//{	
							GenieDebug.error("onItemClick", "onItemClick 0 m_chooseindex = "+m_chooseindex);
							
							m_chooseindex = Index;
							for(int i = 0; i < m_renderlistdata.size();i++)
							{
								if(i == m_chooseindex)
								{
									m_renderlistdata.get(i).m_select = true;
								}else
								{
									m_renderlistdata.get(i).m_select = false;
								}
							}
							//m_chooseindex = Index;
							GenieDebug.error("onItemClick", "onItemClick 1 m_chooseindex = "+m_chooseindex);
							m_renderlistItemAdapter.notifyDataSetChanged();
							GenieDebug.error("onItemClick", "onItemClick m_renderlistItemAdapter.notifyDataSetChanged()");
						//}
					}
				}
			});
		    
	    	
	    	m_RenderDialog = new AlertDialog.Builder(this)
	        .setIcon(R.drawable.icon)
	        .setTitle(R.string.chooserender)
	        .setView(view)
	        //.setSingleChoiceItems(m_dialogItemAdapter, 0, new DialogInterface.OnClickListener() {
//	            public void onClick(DialogInterface dialog, int whichButton) {
//	            	
//	            	m_chooseindex = whichButton;
//	            	
//	            	GenieDebug.error("debug","ShowRenderDialog m_chooseindex = "+m_chooseindex);
//	            	
//	                /* User clicked on a radio button do some stuff */
//	            }
//	        })
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {

	                /* User clicked Yes so do some stuff */
	            	
	            	
	            	int size = GenieDlnaActionDefines.m_Rendererlist.size();
					
					if(m_chooseindex >= 0 && m_chooseindex < size)
					{
						GenieDlnaActionDefines.m_WorkRenderUUID = GenieDlnaActionDefines.m_Rendererlist.get((int)m_chooseindex).getUuid();
						GenieDlnaActionDefines.m_DLNAConfig.SaveRenderUUID = GenieDlnaActionDefines.m_WorkRenderUUID.toString();
						GenieDebug.error("onItemClick", "GenieDlnaActionDefines.m_WorkRenderUUID = "+GenieDlnaActionDefines.m_WorkRenderUUID.toString());
						StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_SAVECONFIG);					
						StartDlnaAction(GenieDlnaActionDefines.ACTION_PLAYMEDIAITEM);
					}
	            	
//	            	if(m_chooseindex >= 0 && m_chooseindex < GenieDlnaStatus.m_dialogRendererlist.size())
//	            	{
//	            		
//	            		
//	            		String uuid = GenieDlnaStatus.m_dialogRendererlist.get(m_chooseindex).m_deviceId;
//	            		GenieDlnaStatus.m_chooserguid = uuid;
//	            		//GneieDlna.this.SetDlnaGuid(GenieDlnaStatus.m_chooserguid);
//	            		//ToPlayMedia(uuid);
//	            	}else
//	            	{
//	            		
//	            	}
	            }
	        })
	        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {

	                /* User clicked No so do some stuff */
	            }
	        })
	       .create();
	    	
	    	
	    	m_RenderDialog.show();

	    }
	    
	 private  class EfficientAdapter_render extends BaseAdapter {
	        private LayoutInflater mInflater;
	        private Bitmap mIcon1;
	        private Bitmap mIcon2;
	        private Bitmap mIcon0;
	        
	        private Bitmap file_video;
	        private Bitmap file_audio;
	        private Bitmap file_image;

	        public EfficientAdapter_render(Context context) {
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);

	            // Icons bound to the rows.
	            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
	        }

	        /**
	         * The number of items in the list is determined by the number of speeches
	         * in our array.
	         *
	         * @see android.widget.ListAdapter#getCount()
	         */
	        public int getCount() {
	        	return m_renderlistdata.size();
	            //return m_listdata.length;
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
	            //return m_listdata.get(position);
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
	            // A ViewHolder keeps references to children views to avoid unneccessary calls
	            // to findViewById() on each row.
	            ViewHolder holder;

	            // When convertView is not null, we can reuse it directly, there is no need
	            // to reinflate it. We only inflate a new View when the convertView supplied
	            // by ListView is null.
	            if (convertView == null) {
	                convertView = mInflater.inflate(R.layout.list_item_icon_text_radio, null);

	                // Creates a ViewHolder and store references to the two children views
	                // we want to bind data to.
	                holder = new ViewHolder();
	                holder.text = (TextView) convertView.findViewById(R.id.text);
	                holder.icon = (ImageView) convertView.findViewById(R.id.rendericon);
	                holder.select = (ImageView)convertView.findViewById(R.id.iconselect);

	                convertView.setTag(holder);
	            } else {
	                // Get the ViewHolder back to get fast access to the TextView
	                // and the ImageView.
	                holder = (ViewHolder) convertView.getTag();
	            }
	            
	            //GenieDebug.error("debug", "View getView position= "+position);
	            //GenieDebug.error("debug", "View getView m_renderlistdata.get(position).m_select= "+m_renderlistdata.get(position).m_select);

	            // Bind the data efficiently with the holder.
	            holder.text.setText(m_renderlistdata.get(position).m_FriendlyName);
	            if(m_renderlistdata.get(position).m_select)
	            {
	            	holder.select.setVisibility(View.VISIBLE);
	            }else
	            {
	            	holder.select.setVisibility(View.GONE);
	            }
	            
	            
	            if(m_renderlistdata.get(position).m_iconflag)
	        	{
	        		if(m_renderlistdata.get(position).m_icon.length > 0)
	        		{
	        			BitmapFactory.Options opt = new BitmapFactory.Options(); 
	        		            	    			
	            		opt.inPreferredConfig = Bitmap.Config.RGB_565;
	            		opt.inJustDecodeBounds = true;
	            		BitmapFactory.decodeByteArray(m_renderlistdata.get(position).m_icon, 0, m_renderlistdata.get(position).m_icon.length, opt);
	            		opt.inJustDecodeBounds = false;
	            		//Log.d(tag, "phSize: " + phSize + " pix_scale: " + PIX_SCALE);
	            		if (opt.outWidth > opt.outHeight) {
	            			opt.inSampleSize = opt.outWidth / 40;;
	            		} else {
	            			opt.inSampleSize = opt.outHeight / 40;
	            		}
	            		Bitmap b = 	BitmapFactory.decodeByteArray(m_renderlistdata.get(position).m_icon, 0, m_renderlistdata.get(position).m_icon.length, opt);
	            		  

	            		if(b != null)
	            		{
	            			holder.icon.setImageBitmap(b);
	            		}else{
	            			holder.icon.setImageBitmap(mIcon1);
	            		}	            		
	            		
	        		}else
	        		{
	        			holder.icon.setImageBitmap(mIcon1);
	        		}
	        		
	        	}
	          

	            return convertView;
	        }

	        class ViewHolder {
	            TextView text;
	            ImageView icon;
	            ImageView select;
	        }
	    }

}
