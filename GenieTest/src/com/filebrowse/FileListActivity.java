package com.filebrowse;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dragonflow.genie.ui.R;
import com.filebrowse.jcifs.JCIFSFileHandleUtil;

public class FileListActivity extends Activity {

	private List<SmbFile> dataList=new ArrayList<SmbFile>();
	private FileListDataAdapter listItemAdapter;
	private Thread fileListthread;
	private ProgressDialog runProDlg=null;
	private Button openButton=null;
//	private String computer_domain="";
	private String computer_ip="";
//	private String computer_username="";
//	private String computer_password="";
	private SmbFile currentPath;
	private String tempPath="/sdcard/FileShareApp/temp/";
	private NotificationManager noticeManager;
	private Notification notification;
	private int notification_id=19172439;
	private boolean isLoading=false;
	public final static String CANCEL_DOWNLOAD="CANCEL_DOWNLOAD";
	public static Boolean isCancelDownload=false;
	public static ExecutorService downloadpool=null;
	private NtlmPasswordAuthentication smbAuth=null;
	private final static int COPY_FILE_TO=1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.filelist);
        
        //获得上一个activity传过来的数据
        initIntentData();
        
        if(smbAuth==null){
        	return;
        }
        
        noticeManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notification=new Notification(R.drawable.file, getResources().getText(R.string.Download), System.currentTimeMillis());
        notification.flags|=Notification.FLAG_AUTO_CANCEL;
        notification.contentView = new RemoteViews(getPackageName(),R.layout.download_notification); 
    	//使用notification.xml文件作VIEW
    	notification.contentView.setProgressBar(R.id.pb, 100,0, false);
    	Intent notificationIntent = new Intent("CANCEL_DOWNLOADFILE");
    	notificationIntent.putExtra("NotificationUserAction", CANCEL_DOWNLOAD);
    	PendingIntent contentIntent = PendingIntent.getBroadcast( this,0,notificationIntent,0); 
    	notification.contentIntent = contentIntent;
        
        final EditText textView=(EditText) this.findViewById(R.id.url);
        textView.setText("smb://"+computer_ip+File.separator);
        textView.setTag("smb://"+computer_ip+File.separator);
        
        textView.setFocusable(false);
        textView.setEnabled(false);
        openButton=(Button) this.findViewById(R.id.openButton);
        final GridView filelist=(GridView) this.findViewById(R.id.fileList);
        listItemAdapter=new FileListDataAdapter(this, dataList);
        filelist.setAdapter(listItemAdapter);
        openButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				String url=textView.getTag().toString();
				if(url!=null&&!"".equals(url.trim())){
					if(!url.startsWith("smb://")){
						url="smb://"+url.trim();
					}
					loadFileList(url.trim(),smbAuth);
				}
			}
		});
        filelist.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View view, int arg2,
        			long arg3) {
        		
        		Object object=view.getTag();
        		if(object!=null){
        			try{
	        			SmbFile file=(SmbFile) object;
//	        			textView.setText(textView.getText()+file.getName()+(file.isDirectory()?"":File.separator));
	        			System.out.println("fileParent:-->"+file.getParent());
	        			if(file.isDirectory() && file.canRead()){
	        				String path=file.getPath();
	        				textView.setText(textView.getText()+(textView.getText().toString().endsWith(File.separator)?"":File.separator)+file.getName());
	        				loadFileList("smb://"+path.substring(path.indexOf(computer_ip)),smbAuth);
	        			}else if(file.isFile() && file.canRead()){
	        				String extension=file.getFileNameMap().getContentTypeFor(file.getName());
	        				System.out.println(extension);
	        				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
	        					asyncDownLoadFile(file,smbAuth,extension,null);
	                        }else{
	                        	Toast.makeText(FileListActivity.this, "读取文件失败！", Toast.LENGTH_SHORT).show();
	                        }
	        			}
        			}catch(Exception ex){
        				ex.printStackTrace();
        			}
        		}
        		
        	}
		});
        
        textView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
	 			LayoutInflater layoutInflater = LayoutInflater.from(FileListActivity.this);
	 			final View loginAdd = layoutInflater.inflate(R.layout.loginpc, null);
				new AlertDialog.Builder(FileListActivity.this).setTitle("输入配置").setView(loginAdd).setPositiveButton("确定",
					       new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int which) {
			        	   
			        	  EditText ip_text=(EditText) loginAdd.findViewById(R.id.login_ip);
			        	  EditText name_text=(EditText) loginAdd.findViewById(R.id.login_username);
			        	  EditText password_text=(EditText) loginAdd.findViewById(R.id.login_password);
			        	  textView.setText(name_text.getText().toString()+":"+password_text.getText().toString()+"@"+ip_text.getText().toString());
			        	  dialog.dismiss();
			           }
			       }).setNegativeButton("取消",
			       new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int which) {
			                dialog.cancel();
			           }
			       }).show();
				
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
        
        //返回
        this.findViewById(R.id.filelist_back).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if(currentPath!=null){
					EditText text=((EditText) findViewById(R.id.url));
	        		text.setText(text.getText().toString().substring(0, text.getText().toString().lastIndexOf(currentPath.getName())));
					String path=currentPath.getParent();
	        		loadFileList("smb://"+path.substring(path.indexOf(computer_ip)),smbAuth);
					
				}else{
		        	finish();
				}
				
			}
		});
        
        
        
        //获取文件列表
        String url=(String) textView.getTag();
        System.out.println("url-->"+url);
        if(url!=null&&!"".equals(url.trim())){
			if(!url.startsWith("smb://")){
				url="smb://"+url.trim();
			}
			loadFileList(url.trim(),smbAuth);
		}
        
        //注册上下文菜单
        registerForContextMenu(filelist);
        
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	 //注册取消下载广播
        IntentFilter myIntentFilter = new IntentFilter(); 
        myIntentFilter.addAction("CANCEL_DOWNLOADFILE"); 
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

   @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.operations);
		menu.setHeaderIcon(R.drawable.option);
		menu.add(0, COPY_FILE_TO, 0, "复制到...");
		
	}
    
   	@Override
   	public boolean onContextItemSelected(MenuItem item) {
   		AdapterContextMenuInfo menuinfo=(AdapterContextMenuInfo) item.getMenuInfo();
   		View view=menuinfo.targetView;
   		if(view!=null){
   			SmbFile file=(SmbFile) view.getTag();
   			switch(item.getItemId()){
	   			case COPY_FILE_TO:{
	   				
	   				break;
	   			}
   			}
   		}
   		return super.onContextItemSelected(item);
   	}
   
    /* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	 // 是否触发按键为back键    
        if (keyCode == KeyEvent.KEYCODE_BACK) { 
             if(isLoading){
            	 if(fileListthread.isAlive()){
            		 fileListthread.interrupt();
            	 }
            	 return true;
             }
//        	startActivity(new Intent(FileListActivity.this, ConnComputerListActivity.class));
        	if(currentPath!=null){
        		EditText text=((EditText) this.findViewById(R.id.url));
        		text.setText(text.getText().toString().substring(0, text.getText().toString().lastIndexOf(currentPath.getName())));
//        		Toast.makeText(this, text.getText().toString(), Toast.LENGTH_LONG).show();
				String parentPath=currentPath.getParent();
        		loadFileList("smb://"+parentPath.substring(parentPath.indexOf(computer_ip)),smbAuth);
				
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
    		  
    		  String computer_domain=extras.getString("ComputerInfo_domain");
	          computer_ip=extras.getString("ComputerInfo_ip");
	          String computer_username=extras.getString("ComputerInfo_username");
	          String computer_password=extras.getString("ComputerInfo_password");
	          smbAuth=new NtlmPasswordAuthentication(computer_domain, computer_username, computer_password);
	          
    	  }
    	
    }
    
    /**
     * 加载文件列表
     * @param url
     */
    public void loadFileList(String url,final NtlmPasswordAuthentication auth){
    	
    	if(fileListthread!=null && fileListthread.isAlive()){
    		return;
    	}
    	
    	final Handler handler = new Handler(){   
            public void handleMessage(Message msg) {  
                switch (msg.what) {      
	                case 1:{     
	                	findViewById(R.id.openButton).setEnabled(true);
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
							openButton.setEnabled(false);
						}
		    		});
		    		
		    		isLoading=true;
			    	 SmbFile file;
			         SmbFile[] files = new SmbFile[0];
			         System.out.println(m_url+"-"+auth.getUsername()+"-"+auth.getPassword());
			         file = new SmbFile(m_url,auth);
			         //判断文件是否存在
			         if(file.exists()){
				         //
				         System.out.println(file.getParent());
				         if("smb://".equals(file.getParent())){
				        	 currentPath=null;
				         }else{
				        	 currentPath=file;
				         }
				
				         long t1 = System.currentTimeMillis();
				         try {
				             files = file.listFiles();
				         } catch (Exception e) {
				             e.printStackTrace();
				             throw new Exception("文件打开失败！");
	//			             Log.e("错误", "--->"+e.getMessage());
				         }
				         long t2 = System.currentTimeMillis() - t1;
				        
				         if(files!=null){
					         for( int i = 0; i < files.length; i++ ) {
					            dataList.add(files[i]);
					         }
					         Collections.sort(dataList, new Comparator<SmbFile>() {
					        	 @Override
					        	public int compare(SmbFile file1, SmbFile file2) {
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
			         }
			         
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		final Exception m_e=e;
		    		runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(FileListActivity.this).setTitle("错误").setMessage("错误:"+m_e.getMessage()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).show();
						}
					});
		    		 Log.e("错误", "--->"+e.getMessage());
		    	}finally{
//		    		findViewById(R.id.openButton).setEnabled(true);
		    		 handler.sendEmptyMessage(1);
		    		 handler.sendEmptyMessage(2);
		    		 runOnUiThread(new Runnable() {
						public void run() {
							if(runProDlg!=null){
								runProDlg.dismiss();
							}
						}
					});
		    		isLoading=false;
		    	}
				
			}
		});
    	fileListthread.start();
    	runProDlg=ProgressDialog.show(this, "加载中...", "请稍等下...");
    	
    }
    
    private void asyncDownLoadFile(final SmbFile file,final NtlmPasswordAuthentication auth,final String extension,final String targetPath){
    	
    	isCancelDownload=false;
    	if(downloadpool==null || (downloadpool!=null &&downloadpool.isShutdown())){
    		downloadpool = Executors.newFixedThreadPool(1);
    	}
    	downloadpool.execute(new Runnable() {
			
			@Override
			public void run() {
				
				try{
					
					String path=targetPath;
					if(targetPath==null || "".equals(targetPath)){
						path=tempPath;
					}
					if(JCIFSFileHandleUtil.downloadFileToLocation(file.getURL().toString(),auth,path,noticeManager,notification,notification_id,isCancelDownload)){
						noticeManager.cancel(notification_id);
						final String m_path=path;
						runOnUiThread(new Runnable() {
							public void run() {
								Intent intent=new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.fromFile(new File(m_path+File.separator+file.getName())), extension);
								startActivity(intent);
							}
						});
					}
					
				}catch(Exception ex){
					ex.printStackTrace();
//					noticeManager.cancel(notification_id);
					runOnUiThread(new Runnable() {
						
						public void run() {
							
							Toast.makeText(FileListActivity.this, "打开文件错误！", Toast.LENGTH_SHORT).show();
							
						}
					});
				}finally{
					noticeManager.cancel(notification_id);
				}
				
			}
		});
		
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
    	//关闭下载线程
    	if(downloadpool!=null){
    		downloadpool.shutdown();
    	}
    	
    };
    
    @Override
    protected void onStop() {
    	
    	unregisterReceiver(mBroadcastReceiver);
    	super.onStop();
    }
    
    //删除文件目录
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
    
    //广播
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action=intent.getAction();
			if("CANCEL_DOWNLOADFILE".equals(action.trim())){
				
				new AlertDialog.Builder(FileListActivity.this).setTitle("下载").setMessage("是否取消任务?").setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(noticeManager!=null){
		        		  isCancelDownload=true;
		        		  if(downloadpool!=null){
		        			  downloadpool.shutdown();
		        		  }
		        		  noticeManager.cancel(notification_id);
			        	}
						dialog.dismiss();
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
			}
			
		}
	};
    
	/**
	 * 打开选择目录窗口
	 */
	private void showSelectDirectoryDialog(){
		
		
		
	}
	
}
