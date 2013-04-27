package com.filebrowse.jcifs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import android.app.Notification;
import android.app.NotificationManager;

import com.dragonflow.genie.ui.R;
import com.filebrowse.FileListActivity;

public class JCIFSFileHandleUtil {
	
	/**
	 * 复制文件
	 * @param sourceUrl
	 * @param targetUrl
	 */
	public static void copyFileTo(String sourceUrl,String targetUrl){
		
		if(sourceUrl==null || "".equals(sourceUrl)){
			
		}
		if(targetUrl==null || "".equals(targetUrl)){
			
		}
		try{
			SmbFile sourceFile=new SmbFile(sourceUrl);
			SmbFile targetFile=new SmbFile(targetUrl);
			sourceFile.copyTo(targetFile);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * 下载文件到本地
	 * @param sourceUrl
	 * @param locationPath
	 * @return
	 * @throws Exception 
	 */
	public static boolean downloadFileToLocation(String sourceUrl,NtlmPasswordAuthentication sourceAuth,String locationPath,NotificationManager manager,Notification notification,int noticeID,Boolean isCancel) throws Exception{
		
		InputStream in = null;   
		OutputStream out = null; 
		boolean flag=false;
		try{
			SmbFile sourceFile=new SmbFile(sourceUrl,sourceAuth);
			String filename=sourceFile.getName();
			File tempMdr=new File(locationPath);
			File locationfile=new File(locationPath+File.separator+filename);
			if(!locationfile.exists()){
				if(!tempMdr.exists()){
					if(tempMdr.mkdirs()){
						locationfile.createNewFile();
					}
				}else{
					locationfile.createNewFile();
				}
			}
			
			in=new BufferedInputStream(new SmbFileInputStream(sourceFile));
			out=new BufferedOutputStream(new FileOutputStream(locationfile));
			int totalSize=sourceFile.getContentLength();
			byte[] buffer=new byte[1024];
			int i=0;
			double num=0.0;
			notification.contentView.setProgressBar(R.id.pb, totalSize, 0, false);
			notification.contentView.setTextViewText(R.id.download_fileinfo, sourceFile.getName()+"复制中...");
			notification.contentView.setTextViewText(R.id.download_progressValue, "0%");
			manager.notify(noticeID, notification);
			DecimalFormat df=new DecimalFormat("#0");
			while(in.read(buffer)!=-1 && !FileListActivity.isCancelDownload){
				i+=buffer.length;
				out.write(buffer);
				
				double d=((double)i/totalSize)*100;
				String state=df.format(d)+"%";
				
				if(d>num && d<=100){
					num+=1.0;
					notification.contentView.setProgressBar(R.id.pb, totalSize, i, false);
					notification.contentView.setTextViewText(R.id.download_progressValue, state);
					manager.notify(noticeID, notification);
				}
				buffer=new byte[1024];
			}
			if(FileListActivity.isCancelDownload){
				flag=false;
			}else{
				flag=true;
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			manager.cancel(noticeID);
		}
		return flag;
		
	}

	public SmbFile[] getFileList(String url,NtlmPasswordAuthentication auth) throws Exception{
		
		SmbFile[] arr=new SmbFile[0];
		try {
			SmbFile files=new SmbFile(url,auth);
			arr=files.listFiles();
		} catch (MalformedURLException e) {
			throw e;
		} catch (SmbException e) {
			throw e;
		}
		return arr;
		
	}
	
	
}
