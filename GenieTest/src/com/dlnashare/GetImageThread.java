package com.dlnashare;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.view.Display;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieDlnaDeviceInfo;

public class GetImageThread {

	public Thread m_IconThread = null;
	private static GetImageThread imagethread = null;
	public ArrayList<GenieDlnaDeviceInfo> m_listdata;
	public int selectitem = 0;

	public GetImageThread() {

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

	public void CancelIconThread() {
		if (m_IconThread != null) {
			if (m_IconThread.isAlive())
				m_IconThread.interrupt();
			m_IconThread = null;
		}
	}

	/**
	 * 取下面缩小图片
	 * 
	 * @param m_listdata
	 * @param selectitem
	 */
	public void GetIconOnThread() {

		if (m_listdata == null && m_listdata.size() > 1)
			return;
		CancelIconThread();

		m_IconThread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (m_listdata.get(selectitem).downloading
						&& m_listdata.get(selectitem).m_iconUrl != null) {
					try {
						GenieDebug.error("debug", "击取第 " + selectitem
								+ " 张图片开始");
						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inPreferredConfig = Bitmap.Config.RGB_565;
						opt.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(m_listdata.get(selectitem).m_iconUrl, opt);
						opt.inJustDecodeBounds = false;
						// opt.inSampleSize = 160;
						Bitmap b = BitmapFactory.decodeFile(m_listdata.get(selectitem).m_iconUrl, opt);
						if (b != null) {
							m_listdata.get(selectitem).downloading = false;
							m_listdata.get(selectitem).downimage = b;
							handler.post(new Runnable() {
								@Override
								public void run() {
									// ImageDlnaShareActivity.adapter.notifyDataSetChanged();
									ImageDlnaShareActivity.small_adapter
											.notifyDataSetChanged();
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
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					GenieDebug.error("debug", "击取第 " + selectitem + " 张图片完成");
				} else {
					GenieDebug.error("debug", "点击取第 " + selectitem
							+ " 张图片,存在或者读取失败");
				}

			}
		});

		m_IconThread.start();
	}

	/**
	 * 上半部份 滑动取图片
	 * 
	 * @param m_listdata
	 * @param selectitem
	 */
	public void GetBigIconOnThread() {

		if (m_listdata == null && m_listdata.size() - selectitem > 1)
			return;
		if (!m_listdata.get(selectitem).bigloading
				&& m_listdata.get(selectitem).m_iconUrl == null) {
			return;
		}
		CancelIconThread();

		m_IconThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (m_listdata.get(selectitem).bigloading
						&& m_listdata.get(selectitem).m_iconUrl != null) {
					try {
						GenieDebug.error("debug", "上半部份 ~点击取第 " + selectitem
								+ " 张图片开始");
						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inPreferredConfig = Bitmap.Config.RGB_565;
						opt.inJustDecodeBounds = true;
						BitmapFactory
						.decodeFile(m_listdata.get(selectitem).m_iconUrl, opt);
						opt.inJustDecodeBounds = false;
						int w = 480;
						int h = 800;
						if (ImageDlnaShareActivity.linear != null) {
							w = ImageDlnaShareActivity.linear.getWidth();
							h = ImageDlnaShareActivity.linear.getHeight();
						}
						final int SampleSize = w > h ? h : w;
						boolean flag = false;
						if (opt.outWidth > SampleSize
								|| opt.outHeight > SampleSize) {
							if (opt.outWidth > opt.outHeight) {
								opt.inSampleSize = opt.outWidth / SampleSize;
							} else {
								opt.inSampleSize = opt.outHeight / SampleSize;
							}
						} else {
							flag = true;

						}
						Bitmap b = BitmapFactory.decodeFile(m_listdata.get(selectitem).m_iconUrl, opt);
						if (flag&& b!=null) {
							try {
								float scaleWidth  = (float) w / b.getWidth(); 
								float scaleHeight = (float) h / b.getHeight(); 
								Matrix matrix = new Matrix();
								matrix.postScale(scaleWidth, scaleHeight); // 长和宽放大缩小的比例
								Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0,
										b.getWidth(), b.getHeight(), matrix, true);
								if (resizeBmp != null) {
									m_listdata.get(selectitem).bigloading = false;
									m_listdata.get(selectitem).bitimage = resizeBmp;
									handler.post(new Runnable() {
										@Override
										public void run() {
											ImageDlnaShareActivity.adapter
													.notifyDataSetChanged();
										}
									});

									if (resizeBmp.isRecycled()) {
										resizeBmp.recycle();
										System.gc();
									}
									
								}
								if (b.isRecycled()) {
									b.recycle();
									System.gc();
								}
							} catch (Exception e) {
								GenieDebug.error("debug", "图片放大时错误   ~~~~~~~~~~~~~~~~");
							}
						}else {
							if (b != null) {
								m_listdata.get(selectitem).bigloading = false;
								m_listdata.get(selectitem).bitimage = b;
								handler.post(new Runnable() {
									@Override
									public void run() {
										ImageDlnaShareActivity.adapter
												.notifyDataSetChanged();
									}
								});

								if (b.isRecycled()) {
									b.recycle();
									System.gc();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} catch (Error e) {
						System.gc();
						e.printStackTrace();
						return;
					}
					System.gc();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					GenieDebug.error("debug", "上半部份 ~点击取第 " + selectitem
							+ " 张图片完成");
				} else {
					GenieDebug.error("debug", "上半部份 ~点击取第 " + selectitem
							+ " 张图片,存在或者读取失败");
				}
				clearbigimage();
			}
		});
		m_IconThread.start();

	}

	/**
	 * 移动后 清除前后 两格的图片 释放内存
	 */
	public void clearbigimage() {

		if (null == m_listdata)
			return;
		for (int i = 0; i < m_listdata.size(); i++) {
			try {
				if (i == selectitem || i == selectitem - 1
						|| i == selectitem + 1) {
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

	private byte[] getBytes(InputStream is) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;

		while ((len = is.read(b, 0, 1024)) != -1) {
			baos.write(b, 0, len);
			baos.flush();
		}
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

		}
	};
}
