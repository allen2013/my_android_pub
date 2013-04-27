package com.ewm;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieDlnaDeviceInfo;
import com.dragonflow.GneieDlna;

public class GetImageThread {

	private GetImageRunning m_IconRunning = null;
	public GetImageRunning m_bigIconRunning=null;
	private static GetImageThread imagethread = null;
	public ArrayList<GenieDlnaDeviceInfo> m_listdata=new ArrayList<GenieDlnaDeviceInfo>();
	public int selectitem = 0;
	public static LinkedHashMap<String, String> fileCacheList=null;
	/** 缓存容量 **/
	private static final int HARD_CACHE_CAPACITY = 40;
	 final public static int Size_M = (1024*1024);
	public static List<String> onLoadingImageList=null;
	private final int smallImageWidth=80;
	
	public GetImageThread() {
		
		fileCacheList=new LinkedHashMap<String, String>(HARD_CACHE_CAPACITY/2){
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, String> eldest) {
				if(this.size()>HARD_CACHE_CAPACITY){
					File f=new File(getCachePath()+eldest.getValue());
					if(f.exists()){
						f.delete();
					}
					return true;
				}else{
					return false;
				}
			}
			
		};
		onLoadingImageList=new ArrayList<String>();
		
	}

	public void setM_listdata(ArrayList<GenieDlnaDeviceInfo> m_listdata) {
		this.m_listdata = m_listdata;
	}

	public void setSelectitem(int selectitem) {
		this.selectitem = selectitem;
	}

	public static GetImageThread Imagethread() {
		if (imagethread == null) {
			imagethread = new GetImageThread();
		}
		return imagethread;
	}

	private void CancelIconThread() {
		if (m_IconRunning != null) {
			if (m_IconRunning!=null){
				m_IconRunning.isCancelThread=true;
				m_IconRunning=null;
			}
		}
	}
	
	public void CancelBigIconThread() {
		if (m_bigIconRunning != null) {
			if (m_bigIconRunning!=null){
				m_bigIconRunning.isCancelThread=true;
				m_bigIconRunning=null;
			}
		}
	}

	/**
	 * 取下面缩小图片
	 * 
	 * @param m_listdata
	 * @param selectitem
	 */
	public void GetIconOnThread() {

		if (m_listdata == null || selectitem<0 || ( m_listdata!=null && m_listdata.size() <selectitem+1))
			return;
		CancelIconThread();
		
		m_IconRunning=new GetImageRunning() {
			@Override
			public void run() {
				try{
					if(ImagesBrowseActivity.isback){
						return;
					}
					if (m_listdata == null || selectitem<0 ||( m_listdata!=null && m_listdata.size() <selectitem+1))
						return;
					
					Bitmap b=null;
					
					if (m_listdata.get(selectitem).downloading
							&& m_listdata.get(selectitem).m_iconUrl != null) {
						try {
							byte[] icon=null;
							if (GneieDlna.bitmap_Cache
									.containsKey(m_listdata
											.get(selectitem).m_iconUrl)) {

								b = GneieDlna.bitmap_Cache
										.get(m_listdata.get(selectitem).m_iconUrl);
							}
							//取文件缓存
							if(b==null){
								b=getBitmapByCache(m_listdata.get(selectitem).m_iconUrl.trim(),true);
							}
							
							if(b==null){
								GenieDebug.error("debug", "击取第 " + selectitem
										+ " 张图片开始");
								URL url = new URL(m_listdata.get(selectitem).m_iconUrl);
								URLConnection conn;
								conn = url.openConnection();
								conn.connect();
								InputStream in=null;
								try{
									in= conn.getInputStream();
								}catch(EOFException eof){
									eof.printStackTrace();
									return;
								}
								if(ImagesBrowseActivity.isback){
									return;
								}
								icon = getBytes(in,false);
								if (icon == null) {
									GenieDebug.error("debug", "击取第 " + selectitem
											+ " 张图片失败");
									return;
								}
								
								BitmapFactory.Options opt = new BitmapFactory.Options();
								opt.inPreferredConfig = Bitmap.Config.RGB_565;
								opt.inJustDecodeBounds = true;
								BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
								if (opt.outWidth > opt.outHeight) {
									opt.inSampleSize = opt.outWidth / smallImageWidth;
								} else {
									opt.inSampleSize = opt.outHeight / smallImageWidth;
								}
								opt.inJustDecodeBounds = false;
								b = BitmapFactory.decodeByteArray(icon, 0,icon.length, opt);
								
								//获取图片读取的原始大小
								BitmapFactory.Options opt1 = new BitmapFactory.Options();
								opt1.inPreferredConfig = Bitmap.Config.RGB_565;
								opt1.inJustDecodeBounds = true;
								BitmapFactory.decodeByteArray(icon, 0, icon.length, opt1);
								int w = 480;
								int h = 800;
								if (ImagesBrowseActivity.linear != null) {
									w = ImagesBrowseActivity.linear.getWidth();
									h = ImagesBrowseActivity.linear.getHeight();
								}
								final int SampleSize = w > h ? w : h;
								boolean flag = false;
								if (SampleSize!=0 && opt1.outWidth > SampleSize
										|| opt1.outHeight > SampleSize) {
									if (opt1.outWidth > opt1.outHeight) {
										opt1.inSampleSize = opt1.outWidth / SampleSize;
									} else {
										opt1.inSampleSize = opt1.outHeight / SampleSize;
									}
								} else {
									flag = true;
								}
								if(flag){
									m_listdata.get(selectitem).originalWidth=opt1.outWidth;
									m_listdata.get(selectitem).originalHeight=opt1.outHeight;
								}else{
									m_listdata.get(selectitem).originalWidth=opt1.outWidth * (1/opt1.inSampleSize);
									m_listdata.get(selectitem).originalHeight=opt1.outHeight * (1/opt1.inSampleSize);
								}
								
							}
							
							icon = null;
							if(ImagesBrowseActivity.isback){
								return;
							}
							if (b != null && selectitem<m_listdata.size()) {
								m_listdata.get(selectitem).m_iconflag = 1;
								m_listdata.get(selectitem).downloading = false;
								m_listdata.get(selectitem).downimage = b;
								
								 if(!GneieDlna.bitmap_Cache.containsKey(m_listdata.get(selectitem).m_iconUrl)){
									 GneieDlna.bitmap_Cache.put(m_listdata.get(selectitem).m_iconUrl, b);
					    	     }
								handler.post(new Runnable() {
									@Override
									public void run() {
										// ImagesBrowseActivity.adapter.notifyDataSetChanged();
										ImagesBrowseActivity.small_adapter.notifyDataSetChanged();
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
						} catch (Error e) {
							System.gc();
							e.printStackTrace();
							return;
						}
						System.gc();
	//					
						GenieDebug.error("debug", "击取第 " + selectitem + " 张图片完成");
						
					} else {
						if (GneieDlna.bitmap_Cache
								.containsKey(m_listdata
										.get(selectitem).m_iconUrl)) {

							b = GneieDlna.bitmap_Cache
									.get(m_listdata.get(selectitem).m_iconUrl);
						}
						//取文件缓存
						if(b==null){
							b=getBitmapByCache(m_listdata.get(selectitem).m_iconUrl.trim(),true);
						}
						if (b != null && selectitem<m_listdata.size()) {
							m_listdata.get(selectitem).m_iconflag = 1;
							m_listdata.get(selectitem).downloading = false;
							m_listdata.get(selectitem).downimage = b;
							
							if(!GneieDlna.bitmap_Cache.containsKey(m_listdata.get(selectitem).m_iconUrl)){
								 GneieDlna.bitmap_Cache.put(m_listdata.get(selectitem).m_iconUrl, b);
				    	     }
							
							handler.post(new Runnable() {
								@Override
								public void run() {
									// ImagesBrowseActivity.adapter.notifyDataSetChanged();
									ImagesBrowseActivity.small_adapter.notifyDataSetChanged();
								}
							});
						}
						GenieDebug.error("debug", "点击取第 " + selectitem
								+ " 张图片,存在或者读取失败");
					}

				}catch(Exception e){
					System.out.println("lh-----取下面缩小图片"+e.getMessage());
					
				}
				
			}
		};
		Thread m_IconThread = new Thread(m_IconRunning);
		m_IconThread.start();
	}
	
	/**
	 * 获取大图
	 * @param iconIndex 图片键值
	 */
	private void getBigIcon(int iconIndex,boolean isCancelThread){
		
		if(ImagesBrowseActivity.isback){
			return;
		}
		
		if (m_listdata == null || iconIndex==-1 || (m_listdata!=null && m_listdata.size() - iconIndex < 1 ))
			return;
		
		if ( (m_listdata.get(iconIndex).bigloading || m_listdata.get(iconIndex).bitimage==null)&& m_listdata.get(iconIndex).m_iconUrl != null) {
			
			try {
				boolean flag = false;
				Bitmap b=getBitmapByCache(m_listdata.get(iconIndex).m_iconUrl.trim(),false);
				if(b==null){
				
					if(onLoadingImageList.contains(m_listdata.get(iconIndex).m_iconUrl.trim())){
						return;
					}
					
					long resourceSize=m_listdata.get(iconIndex).m_long_ResourceSize;
					
					GenieDebug.error("debug", "上半部份 ~点击取第 " + iconIndex+ " 张图片开始");
					URL url = new URL(m_listdata.get(iconIndex).m_iconUrl);
					URLConnection conn;
					conn = url.openConnection();
					conn.connect();
					InputStream in=null;
					try{
						in= conn.getInputStream();
					}catch(EOFException eof){
						eof.printStackTrace();
						return;
					}
					
					//大于3M保存缓存
					if(resourceSize>3*Size_M){
						b=saveBitmapToCache(in, m_listdata.get(iconIndex),isCancelThread);
					}else{
					
						byte[] icon = getBytes(in,isCancelThread);
						if (icon == null) {
							GenieDebug.error("debug", "上半部份 ~点击击取第 "
									+ iconIndex + " 张图片失败");
							return;
						}
						
						if(ImagesBrowseActivity.isback || isCancelThread){
							return;
						}
						
						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inPreferredConfig = Bitmap.Config.RGB_565;
						opt.inJustDecodeBounds = true;
						BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
						int w = 480;
						int h = 800;
						if (ImagesBrowseActivity.linear != null) {
							w = ImagesBrowseActivity.linear.getWidth();
							h = ImagesBrowseActivity.linear.getHeight();
						}
						final int SampleSize = w > h ? w : h;
	//					boolean flag = false;
						if (SampleSize!=0 && opt.outWidth > SampleSize
								|| opt.outHeight > SampleSize) {
							if (opt.outWidth > opt.outHeight) {
								opt.inSampleSize = opt.outWidth / SampleSize;
							} else {
								opt.inSampleSize = opt.outHeight / SampleSize;
							}
						} else {
							flag = true;
		
						}
						
						opt.inJustDecodeBounds = false;
						b = BitmapFactory.decodeByteArray(icon, 0,icon.length, opt);
						icon = null;
					}
					
				}else{
					flag=true;
				}
				
				
				if (flag&& b!=null) {
					try {
//						float scaleWidth  = (float) w / b.getWidth(); 
//						float scaleHeight = (float) h / b.getHeight(); 
//						Matrix matrix = new Matrix();
//						matrix.postScale(scaleWidth, scaleHeight); // 长和宽放大缩小的比例
//						Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0,
//								b.getWidth(), b.getHeight(), matrix, true);
//						if (resizeBmp != null) {
						if(iconIndex<m_listdata.size()){
							m_listdata.get(iconIndex).bigloading = false;
							m_listdata.get(iconIndex).bitimage = b;
							m_listdata.get(iconIndex).originalWidth=b.getWidth();
							m_listdata.get(iconIndex).originalHeight=b.getHeight();
						}
							handler.post(new Runnable() {
								@Override
								public void run() {
									ImagesBrowseActivity.adapter.notifyDataSetChanged();
								}
							});
							
//						}
//						b=null;
						System.gc();
					} catch (Exception e) {
						GenieDebug.error("debug", "图片放大时错误   ~~~~~~~~~~~~~~~~");
					}
				}else {
					if (b != null && iconIndex<m_listdata.size()) {
						m_listdata.get(iconIndex).bigloading = false;
						m_listdata.get(iconIndex).bitimage = b;
						m_listdata.get(iconIndex).originalWidth=b.getWidth();
						m_listdata.get(iconIndex).originalHeight=b.getHeight();

					}
					
					handler.post(new Runnable() {
						@Override
						public void run() {
							ImagesBrowseActivity.adapter.notifyDataSetChanged();
						}
					});
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
//				System.gc();
				e.printStackTrace();
			}finally{
				System.gc();
			}
			GenieDebug.error("debug", "上半部份 ~点击取第 " + iconIndex
					+ " 张图片完成");
		} else {
			GenieDebug.error("debug", "上半部份 ~点击取第 " + iconIndex
					+ " 张图片,存在或者读取失败");
			handler.post(new Runnable() {
				@Override
				public void run() {
					ImagesBrowseActivity.adapter.notifyDataSetChanged();
				}
			});
		}
//		clearbigimage();
		
	}

	/**
	 * 上半部份 滑动取图片
	 * 
	 * @param m_listdata
	 * @param selectitem
	 */
	public void GetBigIconOnThread() {

		if (m_listdata == null || selectitem<0 || (m_listdata!=null && m_listdata.size() - selectitem < 1 ))
			return;
		if (!m_listdata.get(selectitem).bigloading
				&& m_listdata.get(selectitem).m_iconUrl == null) {
			return;
		}
		CancelBigIconThread();
		clearbigimage();
		m_bigIconRunning=new GetImageRunning() {
			@Override
			public void run() {
				
				try{
					int startIndex=selectitem-1;
					int endIndex=selectitem+1;
					if(startIndex<0){
						startIndex=0;
					}
					if(endIndex>=m_listdata.size()){
						endIndex=m_listdata.size()-1;
					}
					//优先加载当前选择图片
					getBigIcon(selectitem,isCancelThread);
					//加载前后两张
					for(int i=startIndex;i<=endIndex;i++){
						if(i!=selectitem){
							getBigIcon(i,isCancelThread);
						}
					}
				
				}catch(Exception e){
					System.out.println("lh------- 获取大图片!");
				}
				
			}
		};
		Thread m_bigIconThread = new Thread(m_bigIconRunning);
		m_bigIconThread.start();

	}

	/**
	 * 移动后 清除前后 两格的图片 释放内存
	 */
	public void clearbigimage() {

		if (null == m_listdata)
			return;
		for (int i = 0; i < m_listdata.size(); i++) {
			try {
				if (i == selectitem || i == selectitem - 1 || i == selectitem + 1) {
					continue;
				}
				if (m_listdata.get(i).bitimage != null) {
					if (!m_listdata.get(i).bitimage.isRecycled())
						m_listdata.get(i).bitimage.recycle();
					m_listdata.get(i).bitimage = null;
					m_listdata.get(i).bigloading = true;
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}

		}
		
		System.gc();
	}

	/**
	 * 移动后 清除前后50张以外的图片 释放内存
	 */
	public void clearsmallimage() {

		if (null == m_listdata)
			return;
		for (int i = 0; i < m_listdata.size(); i++) {
			try {
				if (i == selectitem || i == selectitem - 1
						|| i == selectitem + 1 || i == selectitem - 2
						|| i == selectitem + 2) {
					continue;
				}
				if (m_listdata.get(i) != null) {
					if (!m_listdata.get(i).bitimage.isRecycled())
						m_listdata.get(i).bitimage.recycle();
					m_listdata.get(i).bitimage = null;
					m_listdata.get(i).bigloading = true;
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}

		}
	}

	private byte[] getBytes(InputStream is,boolean isCancelThead){

		if(is==null)return null;
		
		// 读取成字节数组
		ByteArrayOutputStream baos=null;
		try{
			baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len = 0;
	
			while ((len = is.read(b, 0, 1024)) != -1 && !isCancelThead) {
				baos.write(b, 0, len);
				baos.flush();
			}
			byte[] bytes = baos.toByteArray();
			//baos.close();
			return bytes;
		}catch(Exception e1){
			e1.printStackTrace();
			return null;
		}finally{
			if(baos!=null){
				try {
					baos.flush();
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * 保存缓存文件并获得图片
	 * @param is
	 * @param deviceInfo
	 * @return
	 */
	private Bitmap saveBitmapToCache(InputStream is,GenieDlnaDeviceInfo deviceInfo,boolean isCancelThead){
		
		//boolean isBigImg=false;
		synchronized (onLoadingImageList) {
			if(onLoadingImageList!=null && deviceInfo!=null){
				if(!onLoadingImageList.contains(deviceInfo.m_iconUrl.trim())){
					onLoadingImageList.add(deviceInfo.m_iconUrl.trim());
				}else{
					return null;
				}
			}
		}
		
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		//大图片先缓存到本地
		if(sdCardExist){
			String path=Environment.getExternalStorageDirectory().getPath()+File.separator+"NetgearGenie"+File.separator+"cache"+File.separator;
			File f=new File(path);
			boolean flag=true;
			if(!f.exists()){
				flag=f.mkdirs();
			}
			if(flag){
				FileOutputStream fileWriter=null;
				BufferedInputStream bufferedReader=null;
				try{
					File newFile=new File(path+deviceInfo.m_objectId+"_"+deviceInfo.m_title);
					synchronized (newFile) {
						try{
							if(!newFile.exists()){
								newFile.createNewFile();
							}
							fileWriter=new FileOutputStream(newFile);
							bufferedReader=new BufferedInputStream(is);
							long current=0;
							long currentProNum=0;
							int d=0;
							byte[] bt=new byte[1024];
							while(bufferedReader.read(bt)!=-1 && !isCancelThead){
								fileWriter.write(bt);
								current+=new String(bt).length();
								if(deviceInfo.m_long_ResourceSize!=0){
									d=(int)((current/(deviceInfo.m_long_ResourceSize*1.0))*100);
									if((d>currentProNum || currentProNum>=100)&& d<=100){
										System.out.println("DownloadFile-->"+deviceInfo.m_iconUrl+"-->"+deviceInfo.m_long_ResourceSize+"-"+current+"-"+d+"%");
										deviceInfo.loadingProNum=d;
										currentProNum+=1.0;
										handler.post(new Runnable() {
											@Override
											public void run() {
												ImagesBrowseActivity.adapter.notifyDataSetChanged();
											}
										});
									}
								}
								if(ImagesBrowseActivity.isback || isCancelThead){
									break;
								}
							}
						}catch(Exception ex1){
							ex1.printStackTrace();
							if(newFile.exists()){
								newFile.delete();
							}
							return null;
						}finally{
							synchronized (onLoadingImageList) {
								if(onLoadingImageList!=null && deviceInfo!=null && onLoadingImageList.contains(deviceInfo.m_iconUrl.trim())){
									onLoadingImageList.remove(deviceInfo.m_iconUrl.trim());
								}
							}
							if(ImagesBrowseActivity.isback || isCancelThead){
								
								if(newFile.exists()){
									newFile.delete();
								}
							}
							if(is!=null){
								try {
									is.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
					
					
					if(ImagesBrowseActivity.isback || isCancelThead){
						if(fileCacheList.containsKey(deviceInfo.m_iconUrl.trim())){
							fileCacheList.remove(deviceInfo.m_iconUrl.trim());
						}
						if(newFile.exists()){
							newFile.delete();
						}
						return null;
					}
					
					if(newFile.exists() && newFile.length()>0){
						if(!fileCacheList.containsKey(deviceInfo.m_iconUrl.trim())){
							fileCacheList.put(deviceInfo.m_iconUrl.trim(), deviceInfo.m_objectId+"_"+deviceInfo.m_title);
						}
					}
					
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inPreferredConfig = Bitmap.Config.RGB_565;
					opt.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(newFile.getPath(), opt);
					int w = 480;
					int h = 800;
					if (ImagesBrowseActivity.linear != null) {
						w = ImagesBrowseActivity.linear.getWidth();
						h = ImagesBrowseActivity.linear.getHeight();
					}
					final int SampleSize = w > h ? w : h;
					if (SampleSize!=0 && opt.outWidth > SampleSize
							|| opt.outHeight > SampleSize) {
						if (opt.outWidth > opt.outHeight) {
							opt.inSampleSize = opt.outWidth / SampleSize;
						} else {
							opt.inSampleSize = opt.outHeight / SampleSize;
						}
					}
					
					opt.inJustDecodeBounds = false;
					opt.inTempStorage=new byte[1024];
					if(isCancelThead){
						return null;
					}
					return BitmapFactory.decodeFile(newFile.getPath(),opt);
					
				}catch(Exception ex){
					ex.printStackTrace();
				}finally{
					if(bufferedReader!=null){
						try {
							bufferedReader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(fileWriter!=null){
						try {
							fileWriter.flush();
							fileWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
		}
		
		return null;
		
	}
	
	/**
	 * 获取缓存目录
	 * @return
	 */
	public static String getCachePath(){
		
		return Environment.getExternalStorageDirectory().getPath()+File.separator+"NetgearGenie"+File.separator+"cache"+File.separator;
		
	}
	
	/**
	 * 从缓存中获取图片
	 * @param fileUrl
	 * @return
	 */
	public Bitmap getBitmapByCache(String fileUrl,boolean isPreview){
		
		try{
			if(fileCacheList!=null && fileUrl!=null && !"".equals(fileUrl)){
				if(fileCacheList.containsKey(fileUrl.trim())){
					String filename=fileCacheList.get(fileUrl.trim());
					File newFile=new File(getCachePath()+filename);
					if(newFile.exists()){
						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inPreferredConfig = Bitmap.Config.RGB_565;
						opt.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(newFile.getPath(), opt);
						int w = 480;
						int h = 800;
						if (ImagesBrowseActivity.linear != null) {
							w = ImagesBrowseActivity.linear.getWidth();
							h = ImagesBrowseActivity.linear.getHeight();
						}
						final int SampleSize =isPreview?160:(w > h ? w : h);
						if (SampleSize!=0 && opt.outWidth > SampleSize
								|| opt.outHeight > SampleSize) {
							if (opt.outWidth > opt.outHeight) {
								opt.inSampleSize = opt.outWidth / SampleSize;
							} else {
								opt.inSampleSize = opt.outHeight / SampleSize;
							}
						}
						
						opt.inJustDecodeBounds = false;
						opt.inTempStorage=new byte[1024];
						return BitmapFactory.decodeFile(newFile.getPath(),opt);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	abstract class GetImageRunning implements Runnable{
		public boolean isCancelThread=false;
		
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

		}
	};
	
}
