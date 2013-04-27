package com.dragonflow;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GenieSpeedTest extends Activity implements Button.OnClickListener
{
	private String url = null;
	private ProgressDialog progressDialog = null;
	private TextView  textview = null;
	private Handler handler = new Handler()
	{   
        @Override   
        public void handleMessage(Message msg) 
        {
        	GenieDebug.error("handleMessage","handleMessage");
        	Integer res = msg.what;
        	GenieDebug.error("handleMessage", res.toString());
        	textview.setText(getResources().getString(R.string.speedtest_text)+" "+res.toString()+" "+getResources().getString(R.string.Kbps));
        	progressDialog.dismiss();
        }
	};   
	
	@Override 
    public void onCreate(Bundle savedInstanceState) 
    {   
        super.onCreate(savedInstanceState); 
        setTheme(R.style.activityTitlebarNoSearch); //qicheng.ai add
        
        requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
        setContentView(R.layout.speedtest); 
        
        GenieDebug.error("onCreate","onCreate");
        Button button = (Button) this.findViewById(R.id.speedbutton);
        button.setOnClickListener(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        
        InitTitleView();
        
        textview = (TextView) this.findViewById(R.id.speedtip);
        
 /*   	try {
			smbGet1("smb://192.168.9.21/backup/aaa.c");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/ 	
        
    }
	
	
//////////////////////////////////////////////////////////////////////////////
	  /**  
	29.     * 锟斤拷锟斤拷一锟斤拷  
	30.     *   
	31.     * @param remoteUrl  
	32.     *            远锟斤拷路锟斤拷 smb://192.168.75.204/test/锟铰斤拷 锟侥憋拷锟侥碉拷.txt  
	33.     * @throws IOException  
	34.     */  
	public static void smbGet1(String remoteUrl) throws IOException {   
	        SmbFile smbFile = new SmbFile(remoteUrl);
	        
	        GenieDebug.error("debug", "smbGet1  --0--");
	        int length = smbFile.getContentLength();//
	        GenieDebug.error("debug", "smbGet1  --1-length ="+length);
	        byte buffer[] = new byte[length];   
	        GenieDebug.error("debug", "smbGet1  --2--");
	        SmbFileInputStream in = new SmbFileInputStream(smbFile);   
	        //
	        while ((in.read(buffer)) != -1) {   
	  
	            System.out.write(buffer);   
	            System.out.println(buffer.length);   
	        }   
	        in.close();   
	    }   
	  
	    // 
	    /**  
	     * 锟斤拷锟斤拷锟斤拷锟斤拷  
	     *    路锟斤拷锟斤拷式锟斤拷smb://192.168.75.204/test/锟铰斤拷 锟侥憋拷锟侥碉拷.txt  
	     *              smb://username:password@192.168.0.77/test  
	     * @param remoteUrl  
	     *            远锟斤拷路锟斤拷  
	     * @param localDir  
	     *            要写锟斤拷谋锟斤拷锟铰凤拷锟� 
	     */  
	    public static void smbGet(String remoteUrl, String localDir) {   
	        InputStream in = null;   
	        OutputStream out = null;   
	        try {   
	            SmbFile remoteFile = new SmbFile(remoteUrl);   
	            if (remoteFile == null) {   
	                System.out.println("锟斤拷锟斤拷锟侥硷拷锟斤拷锟斤拷锟斤拷");   
	                return;   
	           }   
	            String fileName = remoteFile.getName();   
	            File localFile = new File(localDir + File.separator + fileName);   
	            in = new BufferedInputStream(new SmbFileInputStream(remoteFile));   
	            out = new BufferedOutputStream(new FileOutputStream(localFile));   
	            byte[] buffer = new byte[1024];   
	            while (in.read(buffer) != -1) {   
	                out.write(buffer);   
	                buffer = new byte[1024];   
	            }   
	        } catch (Exception e) {   
	            e.printStackTrace();   
	        } finally {   
	            try {   
	                out.close();   
	                in.close();   
	            } catch (IOException e) {   
	                e.printStackTrace();   
	            }   
	        }   
	    }   
	  
	    // 锟斤拷锟斤拷目录锟较达拷锟侥硷拷   
	    public static void smbPut(String remoteUrl, String localFilePath) {   
	        InputStream in = null;   
	        OutputStream out = null;   
	        try {   
	            File localFile = new File(localFilePath);   
	 
	           String fileName = localFile.getName();   
	            SmbFile remoteFile = new SmbFile(remoteUrl + "/" + fileName);   
	            in = new BufferedInputStream(new FileInputStream(localFile));   
	            out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));   
	            byte[] buffer = new byte[1024];   
	            while (in.read(buffer) != -1) {   
	                out.write(buffer);   
	                buffer = new byte[1024];   
	            }   
	        } catch (Exception e) {   
	            e.printStackTrace();   
	        } finally {   
	            try {   
	                out.close();   
	                in.close();   
	            } catch (IOException e) {   
	                e.printStackTrace();   
	            }   
	        }   
	    }   
	  
	    // 远锟斤拷url smb://192.168.0.77/test   
	    // 锟斤拷锟斤拷锟揭拷没锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷   
	    // smb://username:password@192.168.0.77/test   
	
	
//////////////////////////////////////////////////////////////////////////////	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		Bundle extra = new Bundle();
		
		Intent i = new Intent();
		int resultcode = 0;
		
		
	
        //if(activityresult == GenieGlobalDefines.EFunctionResult_Success)
      	//{
        		
        //	resultcode = GenieGlobalDefines.EFunctionResult_Success;
       // }else
        //{
        	resultcode = GenieGlobalDefines.EFunctionResult_failure;
        //}

		
		i.putExtras(extra);
		setResult(resultcode, i);

		extra = null;

		i = null;

		finish();
//		super.onBackPressed();
	}
	
	private AlertDialog aboutdialog = null;
	
	public void OnClickAbout()
	{
		
///////////////////////////////////////////////////////////	
    	PackageManager manager = this.getPackageManager();  

		PackageInfo info = null;
		String packageName = null;
		int versionCode = 0;
		String versionName = null ;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			
			packageName = info.packageName;  

			versionCode = info.versionCode;  

			versionName = info.versionName;  
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

  
    			
		GenieDebug.error("OnClickAbout", "packageName = "+packageName);
		GenieDebug.error("OnClickAbout", "versionCode = "+versionCode);
		GenieDebug.error("OnClickAbout", "versionName = "+versionName);

   ////////////////////////////////////////////////////////////////
		
		String text = getResources().getString(R.string.version)+" "+versionName+"."+versionCode+"\n"+getResources().getString(R.string.about_info);
    		
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.about,null);
		
		TextView aboutinfo = (TextView)view.findViewById(R.id.about_tv);
		aboutinfo.setTextSize(22);
		aboutinfo.setText(text);
		
		
		TextView model = (TextView)view.findViewById(R.id.about_model);
		model.setTextSize(22);
		model.setText("Router Model:" + GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME));
		
		TextView version = (TextView)view.findViewById(R.id.about_version);
		version.setTextSize(22);
		String Version_text = "Firmware Version:" + GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION);
		
		int posE = 0;
		StringBuffer temp = new StringBuffer(Version_text);
		String str = null;

		posE = temp.indexOf("-");
		if(posE > -1)
		{
			str = temp.substring(0, posE);
		}else 
		{
			posE = temp.indexOf("_");
			if(posE > -1)
			{
				str = temp.substring(0, posE);
			}else
			{
				str = temp.substring(0, temp.length());
			}
		}
		
		if(null != str)
		{
			version.setText(str);
		}
		
		
		
		Button close = (Button)view.findViewById(R.id.about_cancel);
		close.setText(R.string.close);
		close.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != aboutdialog)
				{
					aboutdialog.dismiss();
					aboutdialog = null;
				}
			}
			
		});



		AlertDialog.Builder dialog_login = new AlertDialog.Builder(this)
													.setView(view)
													.setTitle(R.string.about)
													.setIcon(R.drawable.icon);
													
		//dialog_login.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener()
		//{

		//	@Override
		//	public void onClick(DialogInterface dialog, int which) {
		//		// TODO Auto-generated method stub
				
		//	}
			
		//});		
		
		aboutdialog = dialog_login.create();
		aboutdialog.show();
		
		//Button tempp = (Button)testdialog.getWindow().findViewById(AlertDialog.BUTTON_NEGATIVE);
		//GenieDebug.error("OnClickAbout","OnClickAbout 0");
		//Button tempp = testdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		

		//Button tempp = testdialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		//GenieDebug.error("OnClickAbout","OnClickAbout 1");
		//tempp.setEnabled(false);
		//tempp.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		GenieDebug.error("OnClickAbout","OnClickAbout 2");
		//testdialog.show();
	}
	
	private Activity GetActivity()
	{
		return this;
	}
	public void InitTitleView()
	{
		Button  back = (Button)findViewById(R.id.back);
		Button  about = (Button)findViewById(R.id.about);
		
		back.setBackgroundResource(R.drawable.title_bt_bj);
		about.setBackgroundResource(R.drawable.title_bt_bj);
		
		back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GetActivity().onBackPressed();
			}
		});
		
		about.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				OnClickAbout();
			}
		});
		
		back.setOnTouchListener(new OnTouchListener(){      
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
		

		
		about.setOnTouchListener(new OnTouchListener(){      
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
		
//		TextView text = (TextView)findViewById(R.id.model);
//		text.setText("Router:" + GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME));
//		
//		text = (TextView)findViewById(R.id.version);
//		text.setText("Firmware:" + GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION));
//	
	}
	
//	public void changeScreenOrientation(Configuration newConfig)
//	{
//		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//		Display display = windowManager.getDefaultDisplay();
//
//		 GenieDebug.error("changeScreenOrientation", "changeScreenOrientation !!!!!!!!!");
//	}
//	
	
    public void showWaitingDialog()
    {
//    	progressDialog = ProgressDialog.show(GenieMainView.this, "Loading...", "Please wait...", true, false);   
    	progressDialog =  ProgressDialog.show(this, "Loading...", "Please wait...", true, true);
    }
    
	public void onClick(View v)
	{
		GenieDebug.error("onClick","onClick");
		
		EditText text = (EditText)this.findViewById(R.id.speedurl);
		url = text.getText().toString();
		
		if(url.equals("")) 
			return;
		if(url.toLowerCase().startsWith("http://"))
			url = url.toLowerCase().substring(7);
			
		showWaitingDialog();
		new Thread()
		{
			public void run()
			{
				GenieDebug.error("onClick","Thread run ");
				HttpGet request = new HttpGet("http://"+url);
		        HttpParams httpParams = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(httpParams,2000);
		        HttpConnectionParams.setSoTimeout(httpParams,2000);
		        HttpClient httpClient = new DefaultHttpClient(httpParams);
		        
		        try
		        {
		        	GenieDebug.error("onClick","Thread run 0");
		        	
		        	Long start = System.currentTimeMillis();
		        	
		        	GenieDebug.error("onClick","Thread run 1");
		        	
		        	HttpResponse response = httpClient.execute(request);
		            Log.i("........", start.toString());
		            
		            GenieDebug.error("onClick","Thread run 2");
		            
		            GenieDebug.error("onClick","Thread getStatusCode = "+response.getStatusLine().getStatusCode());
		            
		        	if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
		        	{
		        		String recBuffer = EntityUtils.toString(response.getEntity());
		        		Long end = System.currentTimeMillis();
		        		Log.i("........", end.toString());
		        		GenieDebug.error("recBuffer.length", " "+recBuffer.length());
		        		GenieDebug.error("....end-start...", " "+(end-start));
		        	
		        		handler.sendEmptyMessage((int)(recBuffer.length()/(end-start)));
		        		return ;
		            }
		        	
		        	GenieDebug.error("onClick","Thread run 3");
		        }
		        catch (ClientProtocolException e)
		        {
		            e.printStackTrace();
		        }catch (UnknownHostException e)
		        {
		        	e.printStackTrace();
		        }
		        catch (IOException e)
		        {
		            e.printStackTrace();
		        }
		        
		        GenieDebug.error("onClick","Thread run 4");
		        
		        handler.sendEmptyMessage(0);
			}
		}.start();
	}
}
