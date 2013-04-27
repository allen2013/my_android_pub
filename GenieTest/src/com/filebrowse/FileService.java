package com.filebrowse;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.FileTransfer;
import com.dragonflow.GenieDebug;
import com.dragonflow.genie.ui.R;
import com.turbo.TurboDeviceInfo;
import com.turbo.Turbo_ReceiverFileActivity;
import com.turbo.Turbo_SendfileActivity;

public class FileService extends Service {

	private FileTransfer rf = null;
	private Context mContext;
	public static FileService fileservice = null;
	final public static int FileonAccept = 1; // �����ļ�
	final public static int FileSend = 2; // �����ļ�
	final public static int FileonTransfer = 3; // �ļ����ͽ���
	final public static int FilestopListen = 4; // ֹͣ����
	final public static int FileFinished = 5; // ���ͳɹ�
	final public static int StopTimer = 6; // ֹͣ��ʱ��
	final public static int Timershow = 7; // ��������ʱ
	final public static int CancelTransfer=8;
	private int totalTime = 30;
	private Button canclButton;
	private Timer autotime = null;
	private AlertDialog dialog;
	private String receivedDevice_IP="";
	private List<String> filepath=null;
	public NotificationManager noticeManager=null;
	public Notification notification;
	public final static String CANCEL_TRANSFER="CANCEL_TRANSFER";
	public final static int NOTIFICATION_ID=543210;
	public String Device_Name="";
	
	private String sendmessage="";
	private String savePath="";
	private static boolean isAcceptFile=false;
//	private Thread filethread;
	private Activity sendActivity;
	private TurboDeviceInfo sendDeviceInfo;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		fileservice = this;
		rf = FileTransfer.getInstance();
		rf.listenFileSend(7777);
		System.out.println("�����ļ��������");
		//����֪ͨ
		noticeManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notification=new Notification(R.drawable.icon, "", System.currentTimeMillis());
        notification.flags|=Notification.FLAG_AUTO_CANCEL;
        notification.contentView = new RemoteViews(getPackageName(),R.layout.download_notification); 
        notification.icon=R.anim.down_icon;
//        notification.contentView.set
        
//        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);  
//        alphaAnimation1.setDuration(3000);  
//        alphaAnimation1.setRepeatCount(Animation.INFINITE);  
//        alphaAnimation1.setRepeatMode(Animation.REVERSE);  
//        iv.setAnimation(alphaAnimation1);  
//        alphaAnimation1.start();  
        
    	//ʹ��notification.xml�ļ���VIEW
//        notification.contentView.setViewVisibility(R.id.wait_bar, View.VISIBLE);
//        notification.contentView.setViewVisibility(R.id.down_tv,View.GONE);
    	notification.contentView.setProgressBar(R.id.pb, 100,0, false);
    	Intent notificationIntent = new Intent(CANCEL_TRANSFER); 
    	PendingIntent contentIntent = PendingIntent.getBroadcast(this,0,notificationIntent,0); 
    	notification.contentIntent = contentIntent;   
    	
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		System.out.println("ֹͣ�ļ��������");
	}

	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		if(noticeManager!=null){
			noticeManager.cancel(NOTIFICATION_ID);
		}
		return super.stopService(name);
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		System.out.println("��ʼ�ļ��������");
	}
	
	public void CancelFileThread() {
//		if (filethread != null) {
//			if (filethread.isAlive())
//				filethread.interrupt();
//			filethread = null;
//		}
	}
	
	public void sendFile(){
//		CancelFileThread();
		if(!rf.isSending){
			Thread filethread=new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(filepath!=null&&filepath.size()>0){
						
						String path=filepath.get(0);
						System.out.println("�ļ�·��  ��"+path);
						if(path.lastIndexOf(File.separator)!=-1){
							rf.setFilename(path.substring(path.lastIndexOf(File.separator)+1));
						}
						rf.setSendActivity(sendActivity);
					    int returnCode=rf.sendFile(receivedDevice_IP, 7777,path+"\\"+Device_Name); 
					    
//					    int returnCode=rf.sendFile(Device_IP, 7777,path); 
//					    if(returnCode==0){
//					    	rf.isSending=true;
//					    }else{
//					    	rf.isSending=false;
//					    }
					    System.out.println("���ͷ��ر�ʶ��"+returnCode);
					    if(returnCode==-1){
					    	handler.sendEmptyMessage(FileService.CancelTransfer);
					    }
					    filepath.clear();
					}
				}
			});
			filethread.start();
		}
		
	}
	
	public void sendTurboFile(){
		
		if(!rf.isSending){
			Thread filethread=new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(filepath!=null&&filepath.size()>0){
						
						String path=filepath.get(0);
						System.out.println("�ļ�·��  ��"+path);
						if(path.lastIndexOf(File.separator)!=-1){
							rf.setFilename(path.substring(path.lastIndexOf(File.separator)+1));
						}
						rf.setSendActivity(sendActivity);
						Turbo_SendfileActivity.handler.sendEmptyMessage(Turbo_SendfileActivity.TRANSFER_START);
					    int returnCode=rf.sendFile(receivedDevice_IP, 7777,path+"\\"+Device_Name+"\\GENIETURBO"); 
					    
					    Turbo_SendfileActivity.handler.sendEmptyMessage(Turbo_SendfileActivity.TRANSFER_FINISHED);
					    System.out.println("���ͷ��ر�ʶ��"+returnCode);
					    if(returnCode==-1){
					    	handler.sendEmptyMessage(FileService.CancelTransfer);
					    	sendActivity.finish();
					    }
					    filepath.clear();
					}
				}
			});
			filethread.start();
		}
		
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			GenieDebug.error("GenieDlnaService",
					"handler-��ʾ��Ϣ = " + msg.what);
			switch (msg.what) {
				// public static int FileonAccept=1; //�����ļ�
				// public static int FileSend=2; //�����ļ�
				// public static int FileonTransfer=3; //�ļ����ͽ���
				// public static int FilestopListen=4; //ֹͣ����
				// public static int FileFinished=5; //���ͳɹ�
				// public static int StopTimer=6; //ֹͣ��ʱ��
				// public static int Timershow=7; //��������ʱ
				case FileService.FileonAccept:
					showFileAccept();
					break;
				case FileService.FileSend:
	//				sendFile();
					break;
				case FileService.FileonTransfer:
					showTurboAcceptView();
					break;
				case FileService.FilestopListen:
					showFileAccept();
					break;
				case FileService.FileFinished:
					showfinshed();
					break;
				case FileService.StopTimer:
					if (autotime != null) {
						autotime.cancel();
					}
					dialog.dismiss();
					break;
				case FileService.Timershow:
					canclButton.setText(getResources().getText(R.string.cancel)+"(" + totalTime + " s)");
					break;
				case FileService.CancelTransfer:{
					Toast.makeText(FileService.this, R.string.turbo_cancel_info, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
	};
	
	public void showTurboAcceptView(){
		
		SharedPreferences deviceinfo = getSharedPreferences("OwnDeviceInfo", 0); 
		TurboDeviceInfo ownDeviceInfo=new TurboDeviceInfo();
		if(deviceinfo!=null){
			ownDeviceInfo.setIp(deviceinfo.getString("DeviceIP",""));  
			ownDeviceInfo.setDeviceName(deviceinfo.getString("DeviceName","")); 
		}
		
		String path=Environment.getExternalStorageDirectory().getPath().toString();
		path=path.endsWith(File.separator)?(path+"NetgearGenie"+File.separator):(path+File.separator+"NetgearGenie"+File.separator);
		savePath=path;
		try{
			File f1=new File(path);
			if(!f1.exists()){
				f1.mkdirs();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		rf.setRetuaccept(path.endsWith(File.separator)?path:path+File.separator);
		
		Intent intent=new Intent();
		intent.setClass(FileService.this, Turbo_ReceiverFileActivity.class);
		intent.putExtra("SendDeviceInfo", this.sendDeviceInfo);
		intent.putExtra("ReceivedDeviceInfo", ownDeviceInfo);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		
		
	}

	public void showFileAccept() {

		rf.isSending=true;
		// Looper.prepare();
		View view = View.inflate(mContext, R.layout.filechooese, null);
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		TextView txt_mess = (TextView) view.findViewById(R.id.txt_mess);
		txt_mess.setText(sendmessage);
		b.setView(view);
		dialog = b.create();
		// d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		// //ϵͳ�йػ��Ի�������������
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); // ���ڿ��Ի�ý��㣬��Ӧ����
		// d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
		// //���ڲ����Ի�ý��㣬���ʱ��Ӧ���ں���Ľ������¼�
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
		//�ж��Ƿ���ܻ��Ǵ��ͱ�ʶ
		FileService.isAcceptFile=true;
		
		Button yesButton = (Button) view.findViewById(R.id.butt_ok);
		canclButton = (Button) view.findViewById(R.id.butt_cannel);

		yesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(Intent.ACTION_SHUTDOWN);
				// intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);
				String path=Environment.getExternalStorageDirectory().getPath().toString();
				path=path.endsWith(File.separator)?(path+"NetgearGenie"+File.separator):(path+File.separator+"NetgearGenie"+File.separator);
				savePath=path;
				try{
					File f1=new File(path);
					if(!f1.exists()){
						f1.mkdirs();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				rf.setRetuaccept(path.endsWith(File.separator)?path:path+File.separator);
				autotime.cancel();
				dialog.dismiss();
				rf.setSuflag(false);
				
			}
		});

		canclButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				autotime.cancel();
				dialog.dismiss();
				rf.setRetuaccept("REJECT");
				rf.setSuflag(false);
			}
		});

		autotime = new Timer();
		totalTime=30;
		autotime.schedule(new TimerTask() {
			@Override
			public void run() {

				totalTime--;
				if (totalTime == 0) {
					rf.setRetuaccept("REJECT");
					rf.setSuflag(false);
					System.out.println("��ʱ����");
					handler.sendEmptyMessage(FileService.StopTimer);
				} else {
					System.out.println("��ʱ��̨");
					handler.sendEmptyMessage(FileService.Timershow);
				}

			}
		}, 0, 1000);
	}

	public void showfinshed(){
		
		
		Toast.makeText(this,getResources().getString(R.string.transfer_finished_msg).replace("{filename}", rf.getFilename()), 
	            Toast.LENGTH_SHORT).show();
		
		if(FileService.isAcceptFile){
			//�������֪ͨ
			Notification finishNotice=new Notification();
	    	finishNotice.flags|=Notification.FLAG_AUTO_CANCEL;
	    	finishNotice.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;  
			finishNotice.icon=R.drawable.icon;
			
			Intent intent=new Intent(FileService.this, LocationFileActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("OpenFilePath", savePath);
			PendingIntent pIntent=PendingIntent.getActivity(FileService.this, 0, intent, 0);
			finishNotice.setLatestEventInfo(FileService.this, "Transfer Finished", "Save the file "+rf.getFilename()+" in the "+savePath+" directory", pIntent);
			noticeManager.notify(new Random(50000).nextInt(), finishNotice);
			
			FileService.isAcceptFile=false;
		}
		
	}
	
	public Handler getHandler() {
		return handler;
	}

	public void setReceicedDevice_IP(String device_IP) {
		receivedDevice_IP = device_IP;
	}

	public void setFilepath(List<String> filepath) {
		this.filepath = filepath;
	}

	public void setSendmessage(String sendmessage) {
		this.sendmessage = sendmessage;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//ע��㲥
		 IntentFilter myIntentFilter = new IntentFilter(); 
	     myIntentFilter.addAction(CANCEL_TRANSFER); 
	     registerReceiver(mBroadcastReceiver, myIntentFilter);
		return super.onStartCommand(intent, flags, startId);
	}
	
	//�㲥
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action=intent.getAction();
			if(CANCEL_TRANSFER.equals(action.trim())){
				
				AlertDialog.Builder builder=new AlertDialog.Builder(FileService.this).setTitle(R.string.task_cancel).setMessage(R.string.task_cancel_msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println(rf.stopListen());
						rf.setCancelTransfer(true);
						if(noticeManager!=null){
							noticeManager.cancel(NOTIFICATION_ID);
						}
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				AlertDialog dlg=builder.create();
				dlg.setCanceledOnTouchOutside(false);
				dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dlg.show();
			}
			
		}
	};

	public String getDevice_Name() {
		return Device_Name;
	}

	public void setDevice_Name(String device_Name) {
		Device_Name = device_Name;
	}
	
	public void setSendActivity(Activity activity){
		
		this.sendActivity=activity;
		
	}
	
	/**
	 * ��ȡ�ļ�������
	 * @return
	 */
	public FileTransfer getFileTransfer(){
		return this.rf;
	}
	
	public void setSendDeviceInfo(TurboDeviceInfo info){
		this.sendDeviceInfo=info;
	}
	
}
