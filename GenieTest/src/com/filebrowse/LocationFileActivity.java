package com.filebrowse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R; 

public class LocationFileActivity extends Activity implements OnTouchListener, OnGestureListener  {

	private List<File> dataList=new ArrayList<File>();
	private LocationFileListDataAdapter listItemAdapter;
	private Thread fileListthread;
	private int fileNum=0;
	private ProgressDialog runProDlg=null;
	private Button filechooese=null;
	private Button fileupload=null;
	private String computer_ip="";
	private String computer_username="";
	private String computer_password="";
	private File currentPath;
	private String tempPath="/sdcard/genie/temp/";
	private NotificationManager noticeManager;
	private Notification notification;
	private int notification_id=19172439;
	private int listItemWidth=160;
	
	private boolean chooeseflag=false;
	private static List<String> fileselected=new ArrayList<String>();
	private GestureDetector mGestureDetector;
	private FileOperatesReceiver operatesReceiver=null; 
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
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
        
		setContentView(R.layout.filelocation);
		
		if (window_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && window_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		}
		
		InitTitleView();
		
        final GridView filelist=(GridView) this.findViewById(R.id.fileList);
        listItemAdapter=new LocationFileListDataAdapter(this, dataList);
        filelist.setAdapter(listItemAdapter);
        filelist.setNumColumns(window_w/listItemWidth);
        filelist.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View view, int position,
        			long arg3) {
        			Object object=view.getTag();
            		if(object!=null){
            			try{
    	        			File file=(File) object;
    	        			System.out.println("fileParent:-->"+file.getParent());
    	        			if(file.isDirectory() && file.canRead()){
    	        				if(FileBrowseTab.filebrowsetab!=null){
    	        					FileBrowseTab.filebrowsetab.txt_path.setText(file.getPath());
    	        				}
    	        				loadFileList(file.getPath());
    	        			}else if(file.isFile() && file.canRead()){
    	        				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
    	        					
    	        					Intent intent=new Intent(Intent.ACTION_VIEW);
    								intent.setData(Uri.fromFile(file));
    								startActivity(intent);
    	        				}else{
    	        					Toast.makeText(LocationFileActivity.this, "读取文件失败！", Toast.LENGTH_SHORT).show();
    	        				}
//    	        				noticeManager.notify(notification_id, notification);
//    	        				PendingIntent pintent=PendingIntent.getService(FileUploadActivity.this, 0, new Intent(""), 0);
//    	        				notification.contentView.setOnClickPendingIntent(R.id.cancel_downloadnotice, new PendingIntent());
    	        			}
            			}catch(Exception ex){
            				ex.printStackTrace();
            			}
            		}
        	}
		});
     
        
        //获取文件列表
        Bundle extras = getIntent().getExtras();
        String openPath=null;
        if(extras!=null){
        	openPath=extras.getString("OpenFilePath");
        }
        
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); 
        if(sdCardExist){
        	if(openPath!=null && !"".equals(openPath.trim())){
        		loadFileList(openPath.trim());
        	}else{
        		loadFileList(Environment.getExternalStorageDirectory().getPath().toString());
        	}
        		
        }else{
        	Toast.makeText(this, "SDCard不存在！", Toast.LENGTH_LONG);
        }
        
        //滑动
        mGestureDetector = new GestureDetector((OnGestureListener) this);    
        LinearLayout viewSnsLayout = (LinearLayout)findViewById(R.id.LinearLayout1);    
        viewSnsLayout.setOnTouchListener(this);    
        viewSnsLayout.setLongClickable(true);   
        
        //文件操作
        operatesReceiver=new FileOperatesReceiver();
        registerReceiver(operatesReceiver, new IntentFilter(FileOperatesActionDefinition.LOCATION_NEW));
        registerReceiver(operatesReceiver, new IntentFilter(FileOperatesActionDefinition.LOCATION_UP));
        registerReceiver(operatesReceiver, new IntentFilter(FileOperatesActionDefinition.LOCATION_SHOWGRID));
        registerReceiver(operatesReceiver, new IntentFilter(FileOperatesActionDefinition.LOCATION_SHOWLIST));
        
    }
    
    /**
     * 初始化标题栏
     */
    public void InitTitleView() {
		Button back = (Button) findViewById(R.id.back);
		Button about = (Button) findViewById(R.id.about);

		TextView title = (TextView) findViewById(R.id.netgeartitle);
		title.setText(R.string.file_location);

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
    protected void onResume() {
    	
    	if(operatesReceiver==null){
    		operatesReceiver=new FileOperatesReceiver();
    	}
    	registerReceiver(operatesReceiver, new IntentFilter(FileOperatesActionDefinition.LOCATION_NEW));
        registerReceiver(operatesReceiver, new IntentFilter(FileOperatesActionDefinition.LOCATION_UP));
        registerReceiver(operatesReceiver, new IntentFilter(FileOperatesActionDefinition.LOCATION_SHOWGRID));
        registerReceiver(operatesReceiver, new IntentFilter(FileOperatesActionDefinition.LOCATION_SHOWLIST));
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	
    	if(operatesReceiver!=null){
    		unregisterReceiver(operatesReceiver);
    	}
    	super.onPause();
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
        		if(FileBrowseTab.filebrowsetab!=null){
        			FileBrowseTab.filebrowsetab.txt_path.setText(currentPath.getParent());
        		}
//        		Toast.makeText(this, text.getText().toString(), Toast.LENGTH_LONG).show();
				loadFileList(currentPath.getParent());
				
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
          computer_ip=extras.getString("ComputerInfo_ip");
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
//	                	findViewById(R.id.chooesefile).setEnabled(true);
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
//		    		runOnUiThread(new Runnable() {
//						public void run() {
//							filechooese.setEnabled(false);
//						}
//		    		});
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
				         //排序
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
				         System.out.println( files.length + " files in " + t2 + "ms" );
			         }
			         
			         
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		final Exception m_e=e;
		    		runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(LocationFileActivity.this).setTitle(R.string.error).setMessage(m_e.getMessage()).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								
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
    	File tempFile=new File(tempPath);
    	if(tempFile.exists()){
    		for(File f:tempFile.listFiles()){
    			if(f.isDirectory()){
    				deleteDirectory(f);
    			}else{
    				f.delete();
    			}
    		}
    	}
//    	if(operatesReceiver!=null){
//    		unregisterReceiver(operatesReceiver);
//    	}
    	
    };
    
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
    
    /**
     * 新建
     */
    public void newFileOrFolder(){
		String[] items=new String[]{this.getResources().getString(R.string.file),this.getResources().getString(R.string.folder)};
		new AlertDialog.Builder(this).setTitle(R.string.radio_new).setIcon(R.drawable.toolbar_new).setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				final int m_which=which;
				LayoutInflater inflater=LayoutInflater.from(LocationFileActivity.this);
				final View view=inflater.inflate(R.layout.newfile_name, null);
				new AlertDialog.Builder(LocationFileActivity.this).setTitle(R.string.radio_new).setView(view).setNegativeButton(R.string.ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						EditText text=(EditText) view.findViewById(R.id.newfile_name_text);
						String filename=text.getText().toString().trim();
						String path=Environment.getExternalStorageDirectory().getPath().toString();
						if(currentPath!=null){
							path=currentPath.getPath().toString();
						}
						if(path!=null){
							if(!path.endsWith(File.separator)){
								path=path+File.separator;
							}
							switch (m_which) {
								case 0:{
									File f=new File(path+filename);
									try {
										if(f.createNewFile()){
											Toast.makeText(LocationFileActivity.this, "Create folder "+filename+" success!", Toast.LENGTH_SHORT).show();
										}else{
											Toast.makeText(LocationFileActivity.this, "Create folder "+filename+" failure!", Toast.LENGTH_SHORT).show();
										}
									} catch (IOException e) {
										Toast.makeText(LocationFileActivity.this, "Create folder "+filename+" failure!", Toast.LENGTH_SHORT).show();
										e.printStackTrace();
									}
									break;
								}
								case 1:{
									File f=new File(path+filename);
									if(f.mkdirs()){
										Toast.makeText(LocationFileActivity.this, "Create file "+filename+" success!", Toast.LENGTH_SHORT).show();
									}else{
										Toast.makeText(LocationFileActivity.this, "Create file "+filename+" failure!", Toast.LENGTH_SHORT).show();
									}
									break;
								}
							}
							listItemAdapter.notifyDataSetChanged();
						}
					}
				}).setPositiveButton(R.string.cancel, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
				
			}
		}).show();
		
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	private int verticalMinDistance = 60;  
	private int minVelocity         = 0;  
	private int minheigth_y =60;
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		boolean isleft=false;
		
		
		
		if (Math.abs(e1.getX() - e2.getX()) >Math.abs( e1.getY()-e2.getY()) && Math.abs(velocityX) > minVelocity && (e1.getX()-e2.getX())<0) {  
//	        Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();  
	        isleft=true;
	        if(FileBrowseTab.filebrowsetab!=null){
	        	FileBrowseTab.filebrowsetab.tabHost.setCurrentTabByTag(FileBrowseTab.FILESHARE);
	        	FileBrowseTab.filebrowsetab.but_type.setText("共享");
	        }
	    } else if (Math.abs(e2.getX() - e1.getX() )> Math.abs(e2.getY()-e1.getY()) && Math.abs(velocityX) > minVelocity && (e1.getX()-e2.getX())>0) {  
	    	
//	    	Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();  
	    	isleft=false;
	    	if(FileBrowseTab.filebrowsetab!=null){
	        	FileBrowseTab.filebrowsetab.tabHost.setCurrentTabByTag(FileBrowseTab.FILESHARE);
	        	FileBrowseTab.filebrowsetab.but_type.setText("共享");
	        }
	    }
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
    
	
	public boolean dispatchTouchEvent(MotionEvent ev) {  
	    mGestureDetector.onTouchEvent(ev);  
	    // scroll.onTouchEvent(ev);  
	    return super.dispatchTouchEvent(ev);  
	}  
	
	//文件操作广播接收器
	class FileOperatesReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action=intent.getAction();
			if(action!=null){
				if(FileOperatesActionDefinition.LOCATION_NEW.equals(action)){
					//新建
					newFileOrFolder();
					
				}else if(FileOperatesActionDefinition.LOCATION_UP.equals(action)){
					//返回上一级
					if(currentPath!=null){
		        		if(FileBrowseTab.filebrowsetab!=null){
		        			FileBrowseTab.filebrowsetab.txt_path.setText(currentPath.getParent());
		        		}
						loadFileList(currentPath.getParent());
					}
					
				}else if(FileOperatesActionDefinition.LOCATION_SHOWLIST.equals(action)){
					//文件列表显示为列表
				}else if(FileOperatesActionDefinition.LOCATION_SHOWGRID.equals(action)){
					//文件列表显示为方格
				}
			}
			
		}
		
	}
}
