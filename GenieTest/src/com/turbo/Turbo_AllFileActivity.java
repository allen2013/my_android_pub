package com.turbo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 
import com.filebrowse.FileService;

public class Turbo_AllFileActivity extends Activity {

	private List<File> dataList=new ArrayList<File>();
	private Turbo_AllFileDataAdapter listItemAdapter;
	private Thread fileListthread;
	private int fileNum=0;
	private ProgressDialog runProDlg=null;
	private Button filechooese=null;
	
	private ImageView optMultfile = null;
	boolean multFile = false;
	private static ArrayList<String> arrayListFileSelected = new ArrayList<String>();
	
	private Button fileupload=null;
	private String receivedComputer_ip="";
	private String computer_name="";
	private File currentPath;
	private NotificationManager noticeManager;
	private Notification notification;
	private int notification_id=19172423;
	private int listItemWidth=160;
	private boolean isSingleFolder=false;
	
	private TurboDeviceInfo ownDeviceInfo=null;
	private TurboDeviceInfo receivedDeviceInfo=null;
	
	//private static List<String> fileselected=new ArrayList<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
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
		
        setContentView(R.layout.turbo_allfilelist);
        
        if (window_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& window_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
		}

		InitTitleView();
        
        arrayListFileSelected.clear();
        //获得上一个activity传过来的数据
        initIntentData();
        
        noticeManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notification=new Notification(R.drawable.file, getResources().getString(R.string.Download), System.currentTimeMillis());
//        notification.flags|=Notification.FLAG_NO_CLEAR;
        notification.contentView = new RemoteViews(getPackageName(),R.layout.download_notification); 
    	//使用notification.xml文件作VIEW
    	notification.contentView.setProgressBar(R.id.pb, 100,0, false);
    	//设置进度条，最大值 为100,当前值为0，最后一个参数为true时显示条纹
    	//（就是在Android Market下载软件，点击下载但还没获取到目标大小时的状态）
    	Intent notificationIntent = new Intent(this,Turbo_AllFileActivity.class); 
    	PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0); 
    	notification.contentIntent = contentIntent;  
        
        final EditText textView=(EditText) this.findViewById(R.id.tb_file_url);
        textView.setText( Environment.getExternalStorageDirectory().getPath().toString() +File.separator);
        textView.setFocusable(false);
        textView.setEnabled(false);
        filechooese=(Button) this.findViewById(R.id.tb_file_chooesefile);
        final GridView filelist=(GridView) this.findViewById(R.id.tb_file_fileList);
        listItemAdapter=new Turbo_AllFileDataAdapter(this, dataList);
        filelist.setAdapter(listItemAdapter);
        filelist.setNumColumns(window_w/listItemWidth);
        filechooese.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if(currentPath!=null){
					EditText text=((EditText) findViewById(R.id.tb_file_url));
	        		text.setText(text.getText().toString().substring(0, text.getText().toString().lastIndexOf(currentPath.getName())));
					loadFileList(currentPath.getParent());
					listItemAdapter.setSeclection(-1);
					fileupload.setEnabled(false);
					fileupload.setTextColor(Color.GRAY);
					fileupload.setBackgroundResource(R.drawable.title_bt_bj);
				}
				
			}
		});
        
        
//        optMultfile = (ImageView) this.findViewById(R.id.optmultfile);
//		optMultfile.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				multOrSingle(!multFile);
//				listItemAdapter.setData(arrayListFileSelected);
//				listItemAdapter.notifyDataSetChanged();
//			}
//		});
        
        filelist.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View view, int position,
        			long arg3) {
        		//未选 定状态
//        		if(!chooeseflag){
        			Object object=view.getTag();
            		if(object!=null){
            			try{
    	        			File file=(File) object;
    						if (multFile) {
    							if(arrayListFileSelected.size()==0 && file.isDirectory()){
    								isSingleFolder=true;
    							}
    							String abpath = file.getAbsolutePath();
    							if(isSingleFolder){
    								if(file.isDirectory()){
    									if(arrayListFileSelected.size()>0){
    										arrayListFileSelected.clear();
    									}
    									arrayListFileSelected.add(abpath);
    								}else{
    									Toast.makeText(Turbo_AllFileActivity.this, "Select folder only!", Toast.LENGTH_SHORT).show();
    								}
    							}else{
    								if(file.isFile()){
		    							if (arrayListFileSelected.contains(abpath)) {
		    								arrayListFileSelected.remove(abpath);
		    							} else {
		    								arrayListFileSelected.add(file.getAbsolutePath());
		    								// 保存多选的路径
		    							}
    								}else{
    									Toast.makeText(Turbo_AllFileActivity.this, "Select file only!", Toast.LENGTH_SHORT).show();
    								}
    							}
    							listItemAdapter.setData(arrayListFileSelected);
    							listItemAdapter.notifyDataSetChanged();
    							
    						}else{
	    	        			System.out.println("fileParent:-->"+file.getParent());
	    	        			if(file.isDirectory() && file.canRead()){
	    	        				try{
		    	        				SharedPreferences settings = Turbo_AllFileActivity.this.getSharedPreferences("DEFAULT_PATH", 0);
			            				if(null != settings)
			            				{
			            				   	settings.edit().putString("DEFAULT_PATH", file.getPath()).commit();
			            				}
		    	        				
		    	        				textView.setText(file.getPath());
		    	        				loadFileList(file.getPath());
		    	        				listItemAdapter.setSeclection(-1);
		    	        				fileupload.setEnabled(false);
		    	        				fileupload.setTextColor(Color.GRAY);
		    	        				arrayListFileSelected.clear();
		    	        				fileupload.setBackgroundResource(R.drawable.title_bt_bj);
	    	        				}catch(Exception ex1){
	    	        					ex1.printStackTrace();
	    	        				}
	    	        			}else if(file.isFile() && file.canRead()){
	    	        				
	    	            			try{
	    	            				 SharedPreferences settings = Turbo_AllFileActivity.this.getSharedPreferences("DEFAULT_PATH", 0);
	    	            				  if(null != settings)
	    	            				  {
	    	            				   	  settings.edit().putString("DEFAULT_PATH", file.getParent()).commit();
	    	            				  }
	    	            				  
	    	            				
	    	    	        			String filepath=file.getPath();
	//    	    	        			if(fileselected.contains(filepath)){
	//    	    	        				fileselected.remove(filepath);
	//    	    	        			}else{
	//    	    	        			}
	    	    	        			if(position==listItemAdapter.getSelection()){
	    	    	        				listItemAdapter.setSeclection(-1);
	    	    	        				fileupload.setEnabled(false);
	    	    	        				fileupload.setTextColor(Color.GRAY);
	    	    	        				arrayListFileSelected.clear();
	    	    	        				fileupload.setBackgroundResource(R.drawable.title_bt_bj);
	    	    	        			}else{
	    	    	        				fileupload.setEnabled(true);
	    	    	        				fileupload.setTextColor(Color.WHITE);
	    	    	        				listItemAdapter.setSeclection(position);
	    	    	        				fileupload.setBackgroundResource(R.drawable.title_bt_fj);
	    	    	        				arrayListFileSelected.clear();
	    	    	        				arrayListFileSelected.add(filepath);
	    	    	        			}
	    		        				listItemAdapter.notifyDataSetChanged();
	    	            			}catch(Exception ex){
	    	            				ex.printStackTrace();
	    	            			}
	    	        			}
    	        			
    						}
            			}catch(Exception ex){
            				ex.printStackTrace();
            			}
            		}
            		//选定状态
//        		}else{
//        			Object object=view.getTag();
//            		if(object!=null){
//            			try{
//    	        			File file=(File) object;
//    	        			String filepath=file.getPath();
////    	        			if(fileselected.contains(filepath)){
////    	        				fileselected.remove(filepath);
////    	        			}else{
//    	        				fileselected.clear();
//    	        				fileselected.add(filepath);
////    	        			}
//    	        			listItemAdapter.setSeclection(position);
//	        				listItemAdapter.notifyDataSetChanged();
//            			}catch(Exception ex){
//            				ex.printStackTrace();
//            			}
//            			
//            		}
////        		
//        		}
        		
        		
        		
        	}
		});
        
        
        
        textView.setOnTouchListener(new OnTouchListener() {  
            
        	public boolean onTouch(View v, MotionEvent event) {
        		int inType = textView.getInputType(); // backup the input type  
                textView.setInputType(InputType.TYPE_NULL); // disable soft input      
                textView.onTouchEvent(event); // call native handler      
                textView.setInputType(inType); // restore input type     
                textView.setSelection(textView.getText().length());  
                return true;  
        	}
        });  
        
        
        
        
        //获取文件列表
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); 
        if(sdCardExist){
        	String pathurl=Environment.getExternalStorageDirectory().getPath().toString();
        	SharedPreferences settings = this.getSharedPreferences("DEFAULT_PATH", 0); 
			if(settings != null)
			{
			     String url = settings.getString("DEFAULT_PATH","");
			     File file=new File(url);
			     if(file!=null&&file.exists()&&url!=null&&!"".equals(url)){
			    	 pathurl=url;
			     }
			}
			textView.setText(pathurl);
        	loadFileList(pathurl);
        }else{
        	Toast.makeText(this, "SDCard不存在！", Toast.LENGTH_LONG);
        }
        
        
        //上传文件
        fileupload=(Button) this.findViewById(R.id.tb_file_fileupload);
        fileupload.setEnabled(false);
        fileupload.setTextColor(Color.GRAY);
        fileupload.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				findViewById(R.id.tb_file_fileupload).setBackgroundResource(R.drawable.title_bt_fj);
				if(fileListthread!=null && arrayListFileSelected.size()>0){
					if(FileService.fileservice!=null){
						fileupload.setEnabled(false);
						fileupload.setTextColor(Color.GRAY);
						fileupload.setBackgroundResource(R.drawable.title_bt_bj);
						FileService fileService=FileService.fileservice;
						fileService.setReceicedDevice_IP(receivedComputer_ip);
						fileService.setDevice_Name(computer_name);
						
						fileService.setFilepath(arrayListFileSelected);
//						fileService.sendFile();
						
//			    		FileService.fileservice.getHandler().sendEmptyMessage(FileService.FileSend);
			    		listItemAdapter.setSeclection(-1);
//			    		filechooese.setBackgroundResource(R.drawable.title_bt_bj);
			    		listItemAdapter.notifyDataSetChanged();
			    		
//			    		chooeseflag=false;
			    		Intent sendIntent=new Intent();
			    		sendIntent.setClass(Turbo_AllFileActivity.this, Turbo_SendfileActivity.class);
			    		sendIntent.putExtra("OwnDeviceInfo", ownDeviceInfo);
			    		sendIntent.putExtra("ReceivedDeviceInfo", receivedDeviceInfo);
			    		startActivity(sendIntent);
				
					}
				}else{
					new AlertDialog.Builder(Turbo_AllFileActivity.this).setTitle(R.string.error).setMessage(R.string.notselected_file_msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub]\
							listItemAdapter.setSeclection(-1);
//				    		filechooese.setBackgroundResource(R.drawable.title_bt_bj);
				    		listItemAdapter.notifyDataSetChanged();
//				    		chooeseflag=false;
							dialog.dismiss();
						}
					}).show();
				}
			}
		});
//        fileupload.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				if(event.getAction()==MotionEvent.ACTION_DOWN){
//					fileupload.setBackgroundResource(R.drawable.title_bt_fj);
//				}else if(event.getAction()==MotionEvent.ACTION_UP){
//					fileupload.setBackgroundResource(R.drawable.title_bt_bj);
//					
//				}
//				return false;
//			}
//		});
    }
    
    
    /**
	 * 初始化标题栏
	 */
	public void InitTitleView() {
		Button back = (Button) findViewById(R.id.back);
		Button about = (Button) findViewById(R.id.about);

		TextView title = (TextView) findViewById(R.id.netgeartitle);
		title.setText("File List");

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
	 * true for multiple false for single
	 * */
	private void multOrSingle(boolean ms) {
		if (ms) {
			optMultfile.setImageDrawable(getResources().getDrawable(
					R.drawable.multfile));
			arrayListFileSelected.clear();
			multFile = true;
		} else {
			optMultfile.setImageDrawable(getResources().getDrawable(
					R.drawable.singlefile));
			arrayListFileSelected.clear();
			multFile = false;
		}
		isSingleFolder=false;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	 // 是否触发按键为back键    
        if (keyCode == KeyEvent.KEYCODE_BACK) { 
             
//        	startActivity(new Intent(FileListActivity.this, ConnComputerListActivity.class));
        	if(currentPath!=null){
        		EditText text=((EditText) this.findViewById(R.id.tb_file_url));
        		text.setText(currentPath.getParent());
        		//保存当前路径
        		SharedPreferences settings = Turbo_AllFileActivity.this.getSharedPreferences("DEFAULT_PATH", 0);
        		if(null != settings)
        		{
        			settings.edit().putString("DEFAULT_PATH", currentPath.getParent()).commit();
        		}
//        		Toast.makeText(this, text.getText().toString(), Toast.LENGTH_LONG).show();
				loadFileList(currentPath.getParent());
				
				listItemAdapter.setSeclection(-1);
				fileupload.setEnabled(false);
				fileupload.setTextColor(Color.GRAY);
				fileupload.setBackgroundResource(R.drawable.title_bt_bj);
			}else{
	        	this.finish();
			}
        	return true; 
        }else { 
            return super.onKeyDown(keyCode, event); 
        } 
    }
    
    /**
     * 获取上一个activity传过来的值
     */
    private void initIntentData(){
    	
    	  Bundle extras = getIntent().getExtras();
    	  if(extras!=null){
    		  ownDeviceInfo=(TurboDeviceInfo) extras.getSerializable("OwnDeviceInfo");
    		  receivedDeviceInfo=(TurboDeviceInfo) extras.getSerializable("ReceivedDeviceInfo");
    		  receivedComputer_ip=extras.getString("ComputerInfo_ip");
    		  computer_name=extras.getString("ComputerInfo_name");
    		  if(receivedDeviceInfo!=null){
    			  receivedComputer_ip=receivedDeviceInfo.getIp();
    			  computer_name=ownDeviceInfo.getDeviceName();
    		  }
    	  }
//          computer_username=extras.getString("ComputerInfo_username");
//          computer_password=extras.getString("ComputerInfo_password");
    	
    }
    
    /**
     * 加载文件列表
     * @param url
     */
    public void loadFileList(String url){
    	
    	if(fileListthread!=null && fileListthread.isAlive()){
    		return;
    	}
    	
    	final Handler handler = new Handler(){   
            public void handleMessage(Message msg) {  
                switch (msg.what) {      
	                case 1:{     
	                	findViewById(R.id.tb_file_chooesefile).setEnabled(true);
	                    break; 
	                }
	                case 2:{
//	                	Toast.makeText(FileListActivity.this, "文件数:"+fileNum, Toast.LENGTH_SHORT).show();
	                	break;
	                }
	                case 3:{
	                	listItemAdapter.notifyDataSetChanged();
	                	break;
	                }
                }
                super.handleMessage(msg);  
            }  
              
        };  
    	
    	dataList.clear();
    	listItemAdapter.notifyDataSetChanged();
    	
    	final String m_url=url;
    	fileListthread=new Thread(new Runnable() {
			
			public void run() {
				
				//加载文件
		    	try{
		    		runOnUiThread(new Runnable() {
						public void run() {
							filechooese.setEnabled(false);
						}
		    		});
		    		fileNum=0;
			    	 File file;
			         File[] files = new File[0];
			
			         file = new File(m_url);
			         System.out.println("------>"+file.getParent());
			         //判断文件是否存在
			         if(!file.exists()){
			        	 throw new Exception("文件不存在！");
			         }
			         //
			         System.out.println(file.getParent());
			         if( Environment.getExternalStorageDirectory().getPath().toString().equals(file.getPath()) || Environment.getExternalStorageDirectory().getPath().toString().indexOf(file.getPath())!=-1){
			        	 currentPath=null;
			         }else{
			        	 currentPath=file;
			         }
			
			         long t1 = System.currentTimeMillis();
			         try {
			             files = file.listFiles();
			         } catch (Exception e) {
			             e.printStackTrace();
			             throw new Exception("文件加载失败！");
//			             Log.e("错误", "--->"+e.getMessage());
			         }
			         long t2 = System.currentTimeMillis() - t1;
			        
			         if(files!=null){
				         for( int i = 0; i < files.length; i++ ) {
				            dataList.add(files[i]);
				           
				         }
				         
				         Collections.sort(dataList, new Comparator<File>() {
				        	 @Override
				        	public int compare(File file1, File file2) {
				        		// TODO Auto-generated method stub
				        		 try{
				        			 String filename1=file1.getName().toLowerCase(Locale.getDefault());
				        			 String filename2=file2.getName().toLowerCase(Locale.getDefault());
					        		 if(file1.isDirectory() && file2.isDirectory()){
					        			 return filename1.compareTo(filename2);
					        		 }else if(file1.isDirectory() && file2.isFile()){
					        			 return -1;
					        		 }else if(file1.isFile() && file2.isFile()){
					        			 return filename1.compareTo(filename2);
					        		 }else if(file1.isFile() && file2.isDirectory()){
					        			 return 1;
					        		 }else{
					        			 return 0;
					        		 }
				        		 }catch(Exception ex){
				        			 Log.e("smbfile sort", ex.getMessage());
				        			 return 0;
				        		 }
				        	}
				         });
				         
				         handler.sendEmptyMessage(3);
				         
			         }
//			         Map<String,Object> map1=new HashMap<String,Object>();
//			         map1.put("ICO", R.drawable.file);
//           		 map1.put("NAME", "文件数量"+files.length);
//           		 dataList.add(map1);
//           		 listItemAdapter.notifyDataSetChanged();
           			 
			         System.out.println( files.length + " files in " + t2 + "ms" );
			         
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		final Exception m_e=e;
		    		runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(Turbo_AllFileActivity.this).setTitle(R.string.error).setMessage(m_e.getMessage()).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).show();
						}
					});
		    		 Log.e("错误", "--->"+e.getMessage());
		    	}finally{
//		    		findViewById(R.id.filechooese).setEnabled(true);
		    		 handler.sendEmptyMessage(1);
		    		 handler.sendEmptyMessage(2);
		    		 runOnUiThread(new Runnable() {
						public void run() {
							if(runProDlg!=null){
								runProDlg.dismiss();
							}
						}
					});
		    	}
				
			}
		});
    	fileListthread.start();
    	runProDlg=ProgressDialog.show(this, "加载中...", "请稍等下...");
    	
    }
    
    
    protected void onDestroy() {
    	super.onDestroy();
    	
    }
    
    private void deleteDirectory(File directory){
    	
    	if(directory.exists()){
    		for(File f:directory.listFiles()){
    			if(f.isDirectory()){
    				deleteDirectory(f);
    			}else{
    				f.delete();
    			}
    		}
    	}
    	
    }
    
}
