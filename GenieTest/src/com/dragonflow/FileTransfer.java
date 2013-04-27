package com.dragonflow;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.filebrowse.FileService;
import com.turbo.TurboDeviceInfo;
import com.turbo.Turbo_ReceiverFileActivity;
import com.turbo.Turbo_SendfileActivity;
import com.dragonflow.genie.ui.R; 

public class FileTransfer implements Serializable{

	private String receiveFolder;
	public static boolean suflag=true;
	public String retuaccept="REJECT";
	private String filename="";
	private DecimalFormat df=new DecimalFormat("#0");
	private double progressCurrentNum=0.0;
	private boolean isFirst=true;
	private boolean isSendFile=true;
	private boolean isCancelTransfer=false;
	public boolean isSending=false;
	private Timer autotime = null;
	private int totalTime = 5;
	private Notification notification=null;
	private static Turbo_SendfileActivity sendActivity;
	private static Turbo_ReceiverFileActivity receivedActivity;
	
	private static FileTransfer single=null;
	
	public FileTransfer(){
		receiveFolder = "/mnt/sdcard/";
	}
	
	public FileTransfer(Activity activity){
		receiveFolder = "/mnt/sdcard/";
		this.sendActivity=(Turbo_SendfileActivity) activity;
	}
	
	/*
	 * 接受文件传送请求时，回调
	 */
    public String onAccept(String host, String filename){
    	System.out.println("------onAccept:" + host + " 请求文件路径  : " + filename);
    	isSendFile=false;
    	isFirst=true;
    	isCancelTransfer=false;
    	if(isSending){
    		return "REJECT";
    	}
    	String[] str=filename.split("\\\\");
    	if(str!=null){
    		if(str.length==2){
	    		suflag=true;
	        	if(FileService.fileservice!=null){
	        		String tip=FileService.fileservice.getResources().getText(R.string.file_accept_requesttitle).toString();
	        		tip=tip.replace("{ip}", str[1]);
	        		tip=tip.replace("{filename}", str[0]);
	//        		tip=tip.replace("{ip}", host);
	//        		tip=tip.replace("{filename}", filename);
	        		FileService.fileservice.setSendmessage(tip);
	        		FileService.fileservice.getHandler().sendEmptyMessage(1);
	        		while(suflag){}
	        	}
	        	this.filename=str[0];
	//        	this.filename=filename;
	        	if("REJECT".equals(retuaccept)){
	        		isSending=false;
	        	}else{
	        		totalTime=5;
	        	}
    		}else if(str.length==3){
    			suflag=true;
	        	if(FileService.fileservice!=null){
	        		TurboDeviceInfo info=new TurboDeviceInfo();
	        		info.setIp(host);
	        		info.setDeviceName(str[1].trim());
	        		FileService.fileservice.setSendDeviceInfo(info);
	        		FileService.fileservice.getHandler().sendEmptyMessage(FileService.FileonTransfer);
	        		while(suflag){}
	        	}
	        	this.filename=str[0];
	//        	this.filename=filename;
	        	if("REJECT".equals(retuaccept)){
	        		isSending=false;
	        	}else{
	        		totalTime=5;
	        	}
    		}
    	}
    	System.out.println("请求是否接受文件:"+retuaccept);
    	return retuaccept;
    }
    
    /*
     * 传送结束时回调
     */
    public void onFinished(String msg){
    	NotificationManager manager=FileService.fileservice.noticeManager;
    	if(manager!=null){
    		manager.cancel(FileService.NOTIFICATION_ID);
    		System.out.println("关闭通知栏显示");
    	}
    	FileService.fileservice.getHandler().sendEmptyMessage(FileService.FileFinished);
    	isFirst=true;
    	progressCurrentNum=0.0;
    	isSendFile=true;
    	isCancelTransfer=false;
    	isSending=false;
    	this.filename="";
    	totalTime=5;
    	if(autotime!=null)autotime.cancel();
    	autotime=null;
    	NotificationManager noticeManager=FileService.fileservice.noticeManager;
    	notification=FileService.fileservice.notification;
    	notification.icon=R.anim.down_icon;
    	
    	
    }
    
    /*
     * 传送文件中回调
     */
    public void onTransfer(long sum,long current){
    	
    	System.out.println("传送文件开始....总大小："+sum+"  传送大小:"+current );
    	NotificationManager noticeManager=FileService.fileservice.noticeManager;
    	notification=FileService.fileservice.notification;
    	
    	
    	if(autotime==null){
    		System.out.println("计时闪烁开始!!!");
    		autotime = new Timer();
    		autotime.schedule(new TimerTask() {
    			@Override
    			public void run() {
    				totalTime--;
    				if (totalTime == 0) {
    					notification.icon=R.drawable.icon2;
    					autotime.cancel();
    				} 
    			}
    		}, 0, 1000);
    	}
    	if(noticeManager!=null && !isCancelTransfer){
    		double d=((double)current/sum)*100;
    		if(current==0){
    			if(noticeManager!=null){
    				noticeManager.cancel(FileService.NOTIFICATION_ID);
    			}
    			return;
    		}
    		
    		if(isFirst){
    			notification.contentView.setProgressBar(R.id.pb, (int)sum, 0, false);
    			notification.contentView.setTextViewText(R.id.download_fileinfo, (isSendFile?FileService.fileservice.getResources().getText(R.string.send_file):FileService.fileservice.getResources().getText(R.string.receive_file))+filename);
    			notification.contentView.setTextViewText(R.id.download_progressValue, "0%");
    			notification.contentView.setTextViewText(R.id.file_size, getfile_size(sum,current));
    			noticeManager.notify(FileService.NOTIFICATION_ID, FileService.fileservice.notification);
    			isFirst=false;
    			System.out.println("传送通知栏显示1");
    			
    			initTurboView(sum,current);
    			
    		}
//    		if(progressCurrentNum!=0){
//    			notification.contentView.setViewVisibility(R.id.down_tv,View.VISIBLE);
//    			notification.contentView.setViewVisibility(R.id.wait_bar,View.INVISIBLE);
//    		}
    		
    		if((d>progressCurrentNum || progressCurrentNum>=100)&& d<=100){
    			progressCurrentNum+=4.0;
    			notification.icon=R.drawable.icon;
    			notification.contentView.setTextViewText(R.id.download_fileinfo, (isSendFile?FileService.fileservice.getResources().getText(R.string.send_file):FileService.fileservice.getResources().getText(R.string.receive_file))+filename);
    			notification.contentView.setTextViewText(R.id.download_progressValue, df.format(d)+"%");
    			notification.contentView.setProgressBar(R.id.pb, (int)sum, (int)current, false);
    			notification.contentView.setTextViewText(R.id.file_size, getfile_size(sum,current));
        		noticeManager.notify(FileService.NOTIFICATION_ID, FileService.fileservice.notification);
    			System.out.println("传送通知栏显示2");
    			
    			reflashTurboView(sum,current, d);
    			
    		}
    		
    		
    	}
//    	System.out.println("------onTransfer:" + sum + ":" + current);
    }
    
    //开始接受文件传送
    public native int listenFileSend(int port);
    
    //停止接受文件传送
    public native int stopListen();
    
    //发送文件
    public native int sendFile(String host,int port,String filename);

    static {
        System.loadLibrary("udt");
    }

	public void setSuflag(boolean suflag) {
		FileTransfer.suflag = suflag;
	}

	public void setRetuaccept(String retuaccept) {
		this.retuaccept = retuaccept;
	}
	
	public void setCancelTransfer(boolean b){
		this.isCancelTransfer=b;
	}

	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String name){
		this.filename=name;
	}
    
	public String getfile_size(long sum,long current){
		float totol=sum/1024.00f/1024.00f;
		DecimalFormat df = new DecimalFormat("#.##");
		float tr_totol=current/1024.00f/1024.00f;
		return df.format(tr_totol)+"M/"+df.format(totol)+"M";
	}
	
	public void setSendActivity(Activity activity){
		
		sendActivity=(Turbo_SendfileActivity) activity;
		
	}
	
	public void setReceivedActivity(Activity activity){
		
		receivedActivity=(Turbo_ReceiverFileActivity) activity;
		
	}
	
	/**
	 * 初始化turbo传输界面
	 * @param m_sum
	 */
	private void initTurboView(final long m_sum,final long m_current){
		
		if(isSendFile){
			//设置传送页面
			if(sendActivity!=null){
				sendActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ProgressBar turbo_pb=sendActivity.progressBar;
						TextView turbo_pdNum=sendActivity.progressNum;
						TextView turbo_pdSize=sendActivity.progressSize;
						if(turbo_pb!=null){
		    				turbo_pb.setMax((int)m_sum);
		    				turbo_pb.setProgress((int)m_current);
		    			}
		    			if(turbo_pdNum!=null){
		    				turbo_pdNum.setText(""+df.format(((double)m_current/m_sum)*100)+"%");
		    			}
		    			if(turbo_pdSize!=null){
		    				turbo_pdSize.setText("("+getfile_size(m_sum, m_current)+")");
		    			}
					}
				});
			}
		}else{
			//设置接收页面
			if(receivedActivity!=null){
				receivedActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ProgressBar turbo_pb=receivedActivity.progressBar;
						TextView turbo_pdNum=receivedActivity.progressNum;
						TextView turbo_pdSize=receivedActivity.progressSize;
						if(turbo_pb!=null){
		    				turbo_pb.setMax((int)m_sum);
		    				turbo_pb.setProgress((int)m_current);
		    			}
		    			if(turbo_pdNum!=null){
		    				turbo_pdNum.setText("0%");
		    			}
		    			if(turbo_pdSize!=null){
		    				turbo_pdSize.setText("("+getfile_size(m_sum, m_current)+")");
		    			}
					}
				});
			}
		}
		
	}
	
	/**
	 * 刷新turbo传输界面
	 * @param m_current
	 * @param m_d
	 */
	private void reflashTurboView(final long m_sum,final long m_current,final double m_d){
		
		if(isSendFile){
			//设置传输页面
			if(sendActivity!=null){
				sendActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ProgressBar turbo_pb=sendActivity.progressBar;
						TextView turbo_pdNum=sendActivity.progressNum;
						TextView turbo_pdSize=sendActivity.progressSize;
						if(turbo_pb!=null){
		    				turbo_pb.incrementProgressBy(((int)m_current)- turbo_pb.getProgress());
		    			}
		    			if(turbo_pdNum!=null){
		    				turbo_pdNum.setText(df.format(m_d)+"%");
		    			}
		    			if(turbo_pdSize!=null){
		    				turbo_pdSize.setText("("+getfile_size(m_sum,m_current)+")");
		    			}
					}
				});
			}
			
		}else{
			//设置接收页面
			if(receivedActivity!=null){
				receivedActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ProgressBar turbo_pb=receivedActivity.progressBar;
						TextView turbo_pdNum=receivedActivity.progressNum;
						TextView turbo_pdSize=receivedActivity.progressSize;
						if(turbo_pb!=null){
		    				turbo_pb.incrementProgressBy(((int)m_current)- turbo_pb.getProgress());
		    			}
		    			if(turbo_pdNum!=null){
		    				turbo_pdNum.setText(df.format(m_d)+"%");
		    			}
		    			if(turbo_pdSize!=null){
		    				turbo_pdSize.setText("("+getfile_size(m_sum,m_current)+")");
		    			}
					}
				});
			}
		}
		
	}
	
	public synchronized static FileTransfer getInstance(){
		
		if(single==null){
			single=new FileTransfer();
		}
		return single;
		
	}
	
}
