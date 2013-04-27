package com.ewm;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieDlnaActionDefines;
import com.dragonflow.GenieDlnaDeviceInfo;
import com.dragonflow.GenieDlnaService;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.GneieDlna;
import com.dragonflow.genie.ui.R; 

public class ImagesBrowseActivity extends Activity implements OnTouchListener,
		OnItemClickListener {

	public boolean stopthread = true;
	// public static int width;
	// public static int height;
	public MyGallery gallery;
	public static ImageBrowseAdapter adapter = null;
	public static SmallImageBrowseAdapter small_adapter = null;
	public static LinearLayout linear;
	public LinearLayout downlinear;
	public static boolean imagescolse = false;
	public static boolean flag = false;
	// private Bitmap mBitmap;
	private Gallery g;
	public ArrayList<GenieDlnaDeviceInfo> m_listdata = new ArrayList<GenieDlnaDeviceInfo>();
	public int selectitem = -1; //当前选择的
	public static GetImageThread getoneimage = null;
	public static int totalcount = 1;
	// bigimage 显示当前图片的位置
	public int bitimagecount = -1;
	public Button m_back = null;
	public SpinnerButton m_auto = null;
	private RelativeLayout m_titler = null;
	public TextView m_dlnatitle = null;
	public static int autotime = 0;
	private Timer autoGallery = null;
	public static boolean isopen = false;
	private boolean isClickSmallPic = false;
	public static boolean isback = false;
	 final public static int Size_M = (1024*1024);
	 private final int smallImageWidth=80;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.isopen = true;
		this.isback = false;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.imagesbrowse);
		m_listdata = GneieDlna.m_listdata;
		// 点击取一张图片
		getoneimage = GetImageThread.Imagethread();
		getoneimage.setM_listdata(m_listdata);

		// 加载界面
		InitTitleView();
		Initgallery();

		// 第一次默认加载
		// GetIconOnThread(GenieDlnaActionDefines.m_BrowseItemId, 0);
		// bitimagecount=GenieDlnaActionDefines.m_BrowseItemId;
		getoneimage.setSelectitem(GenieDlnaActionDefines.m_BrowseItemId);
		getoneimage.GetIconOnThread();
		getoneimage.GetBigIconOnThread();
		// gallery.setSelection(0);
		gallery.setSelection(GenieDlnaActionDefines.m_BrowseItemId);
		g.setSelection(GenieDlnaActionDefines.m_BrowseItemId);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		imagescolse = true;
		this.isopen = false;
		this.isback = false;
		autotime = 0;
		// ClearListData();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// if(m_listdata!=null)
		// m_listdata.clear();
		super.onResume();
		imagescolse = true;
		// totalcount = 0;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		imagescolse = true;
		super.onStop();
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
		is.close();
		return bytes;
	}

	public Thread m_IconThread = null;

	public void CancelIconThread() {
		if (m_IconThread != null) {
			if (m_IconThread.isAlive())
				m_IconThread.interrupt();
			m_IconThread = null;
		}
	}

	/**
	 * 获取缩略图
	 * 
	 * @param selecone
	 * @param nowcount
	 */
	public void GetIconOnThread(final int selecone, final int nowcount) {

		if (m_listdata == null)
			return;
		CancelIconThread();

		m_IconThread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					if (isback) {
						return;
					}

					int number = 11;
					int size = m_listdata.size();
					int selectitems = selecone;
					if (selecone < 0 || size <= selecone) {
						return;
					}

					// 向前获取5个
					selectitems -= 5;
					GenieDebug.error("debug", "滑动停止后第 " + nowcount + " 次取图片开始");
					while (number > 0) {
						if (isback) {
							return;
						}
						if (selectitems >= size) {
							break;
						}
						if (selectitems < 0) {
							number--;
							selectitems++;
							continue;
						}
						
						Bitmap mBitmap = null;

						if (selectitems < m_listdata.size()
								&& selectitems > -1
								&& m_listdata.get(selectitems).downloading
								&& m_listdata.get(selectitems).m_iconUrl != null) {

							byte[] icon = null;
							try {

								GenieDebug.error("debug", "滑动停止后第 " + nowcount
										+ " 次取图片开始...取第 " + selectitems + " 张");

								if (GneieDlna.bitmap_Cache
										.containsKey(m_listdata
												.get(selectitems).m_iconUrl)) {

									mBitmap = GneieDlna.bitmap_Cache
											.get(m_listdata.get(selectitems).m_iconUrl);

								}
								//获取文件缓存
								if(mBitmap==null){
									mBitmap=getoneimage.getBitmapByCache(m_listdata.get(selectitems).m_iconUrl.trim(),true);
								}

								if (mBitmap == null) {
									
									long resourceSize=m_listdata.get(selectitems).m_long_ResourceSize;
									if(resourceSize>(3*Size_M)){
										return;
									}
									
									URL url = new URL(m_listdata
											.get(selectitems).m_iconUrl);
									URLConnection conn;
									conn = url.openConnection();
									conn.connect();
									if (isback) {
										return;
									}
									InputStream in=null;
									try{
										in= conn.getInputStream();
									}catch(EOFException eof){
										eof.printStackTrace();
										return;
									}
									
									icon = getBytes(in);
									
									if (isback) {
										return;
									}
									
									if (icon == null) {
										GenieDebug.error("debug", "滑动停止后第 "
												+ nowcount + " 次取图片开始...取第 "
												+ selectitems + " 张. 失败");
										number--;
										continue;
									}
									BitmapFactory.Options opt = new BitmapFactory.Options();
									opt.inPreferredConfig = Bitmap.Config.RGB_565;
									opt.inJustDecodeBounds = true;
									BitmapFactory.decodeByteArray(icon, 0,
											icon.length, opt);
									if (opt.outWidth > opt.outHeight) {
										opt.inSampleSize = opt.outWidth / smallImageWidth;
									} else {
										opt.inSampleSize = opt.outHeight / smallImageWidth;
									}
									opt.inJustDecodeBounds = false;
									mBitmap = BitmapFactory.decodeByteArray(
											icon, 0, icon.length, opt);
									
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

								if (isback) {
									return;
								}
								if (mBitmap != null) {
									m_listdata.get(selectitems).downloading = false;
									m_listdata.get(selectitems).downimage = mBitmap;
									
									 if(!GneieDlna.bitmap_Cache.containsKey(m_listdata.get(selectitems).m_iconUrl)){
										 GneieDlna.bitmap_Cache.put(m_listdata.get(selectitems).m_iconUrl, mBitmap);
						    	     }

									GenieDebug.error("debug", "滑动停止后第 "
											+ nowcount + " 次取图片开始...取第 "
											+ selectitems + " 张.成功!!!");
								}

							} catch (Exception e) {
								e.printStackTrace();
							} catch (Error e) {
								e.printStackTrace();
							} finally {
								handler.post(new Runnable() {
									@Override
									public void run() {
										ImagesBrowseActivity.small_adapter
												.notifyDataSetChanged();

//										if (GneieDlna.genieDlna != null) {
//											GneieDlna.genieDlna
//													.re_m_listItemAdapter();
//										}

									}
								});
								icon = null;
								selectitems++;
								number--;
								System.gc();
							}

						} else {
							
							if (GneieDlna.bitmap_Cache
									.containsKey(m_listdata
											.get(selectitems).m_iconUrl)) {

								mBitmap = GneieDlna.bitmap_Cache
										.get(m_listdata.get(selectitems).m_iconUrl);
							}
							//取文件缓存
							if(mBitmap==null){
								mBitmap=getoneimage.getBitmapByCache(m_listdata.get(selectitems).m_iconUrl.trim(),true);
							}
							if (mBitmap != null) {
								m_listdata.get(selectitems).downloading = false;
								m_listdata.get(selectitems).downimage = mBitmap;
								
								 if(!GneieDlna.bitmap_Cache.containsKey(m_listdata.get(selectitems).m_iconUrl)){
									 GneieDlna.bitmap_Cache.put(m_listdata.get(selectitems).m_iconUrl, mBitmap);
					    	     }
								
								GenieDebug.error("debug", "滑动停止后第 "
										+ nowcount + " 次取图片开始...取第 "
										+ selectitems + " 张.成功!!!");
							}
							
							selectitems++;
							number--;
							GenieDebug.error("debug", "滑动停止后第 " + nowcount
									+ " 次取图片开始...取第 " + selectitems
									+ " 张.失敗!!!");
						}

					}

				} catch (Exception e) {
					System.out.println("lh------ ImagesBrowseActivity 取小图片失败");
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						// ImagesBrowseActivity.adapter.notifyDataSetChanged();
						if (isClickSmallPic && selecone != selectitem) {
							// 取消点击小图标识
							isClickSmallPic = false;
							if (selectitem != -1) {
								Log.e("selected", "Handler选择：" + selectitem);
								g.setSelection(selectitem);
							}
						}
						// ImagesBrowseActivity.small_adapter.notifyDataSetChanged();
					}
				});

			}
		});

		m_IconThread.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		GenieDebug.error("mahua", "ItemSelected==" + arg2);

	}

	float beforeLenght = 0.0f; // 两触点距离
	float afterLenght = 0.0f; // 两触点距离
	boolean isScale = false;
	float currentScale = 1.0f;// 当前图片的缩放比率

	private class GalleryChangeListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			float scaleWidth = ImagesBrowseActivity.linear.getWidth()
					/ (float) ((MyImageView) view).image.getWidth();
			float scaleHeight = ImagesBrowseActivity.linear.getHeight()
					/ (float) ((MyImageView) view).image.getHeight();
			if (scaleHeight > 1 || scaleWidth > 1) {
				currentScale = Math.min(scaleWidth, scaleHeight);
			} else {
				currentScale = 1.0f;
			}
			isScale = false;
			beforeLenght = 0.0f;
			afterLenght = 0.0f;

		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:// 多点缩放
			beforeLenght = spacing(event);
			if (beforeLenght > 5f) {
				isScale = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isScale) {
				afterLenght = spacing(event);
				if (afterLenght < 5f)
					break;
				float gapLenght = afterLenght - beforeLenght;
				if (gapLenght == 0) {
					break;
				} else if (Math.abs(gapLenght) > 5f) {
					float scaleRate = gapLenght / 854;// 缩放比例
					Animation myAnimation_Scale = new ScaleAnimation(
							currentScale, currentScale + scaleRate,
							currentScale, currentScale + scaleRate,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					myAnimation_Scale.setDuration(100);
					myAnimation_Scale.setFillAfter(true);
					myAnimation_Scale.setFillEnabled(true);
					currentScale = currentScale + scaleRate;
					gallery.getSelectedView()
							.setLayoutParams(
									new Gallery.LayoutParams(
											(int) (gallery.getWidth() * (currentScale)),
											(int) (gallery.getHeight() * (currentScale))));
					beforeLenght = afterLenght;
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			isScale = false;
			break;
		}

		return false;
	}

	/**
	 * 就算两点间的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	public void ClearListData() {
		if (null == m_listdata)
			return;
		for (GenieDlnaDeviceInfo device : m_listdata) {
			try {
				device.downloading = true;
				if (device.downimage != null) {
					if (!device.downimage.isRecycled())
						device.downimage.recycle();
					device.downimage = null;
				}
				device.bigloading = true;
				if (device.bitimage != null) {
					if (!device.bitimage.isRecycled())
						device.bitimage.recycle();
					device.bitimage = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}

		}
		System.gc();
		// m_listdata.clear();
		// m_listdata=null;
		// m_listdata = GneieDlna.m_listdata;
	}

	/**
	 * 加载两个滑动窗口
	 */
	public void Initgallery() {
		linear = (LinearLayout) findViewById(R.id.linear1);
		// RelativeLayout relative = (RelativeLayout)
		// findViewById(R.id.relative);
		// relative.setBackgroundColor(Color.GRAY);
		gallery = (MyGallery) findViewById(R.id.gallery1);
		gallery.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
		gallery.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
		adapter = new ImageBrowseAdapter(this, m_listdata);
		gallery.setAdapter(adapter);
		// width =
		// getWindow().getWindowManager().getDefaultDisplay().getWidth();
		// height =
		// getWindow().getWindowManager().getDefaultDisplay().getHeight();
		small_adapter = new SmallImageBrowseAdapter(this, m_listdata);
		g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(small_adapter);
		g.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Log.e("selected", "click选择：" + position);
				if (selectitem == position)
					return;
				// Toast.makeText(ImagesBrowseActivity.this,
				// "你选择了" + (position + 1) + " 号图片", Toast.LENGTH_SHORT)
				// .show();
				selectitem = position;
				small_adapter.setSelectItem(selectitem);
				// 加载小图片
				if (position < m_listdata.size()
						&& m_listdata.get(position).downloading) {
					getoneimage.setSelectitem(position);
					// getoneimage.GetIconOnThread();
				}
				isClickSmallPic = true;
				gallery.setSelection(position);
			}
		});
		g.setCallbackDuringFling(false);
		g.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.e("selected", "selected选择：" + arg2);
				// selectitem=arg2;
				// small_adapter.setSelectItem(selectitem);

				GenieDebug.error("debug", "滑动停止后第 " + totalcount
						+ " 次~~~~~~~~~~~~~~~~~~~~");
				GetIconOnThread(arg2, totalcount);
				totalcount++;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		// gallery.setOnItemClickListener(new OnItemClickListener() {
		// public void onItemClick(AdapterView<?> parent, View v,
		// int position, long id) {
		// getoneimage.setSelectitem(position);
		// getoneimage.GetBigIconOnThread();
		// }
		// });
		gallery.setCallbackDuringFling(false);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int totalcount, long arg3) {

				if (bitimagecount != totalcount
						&& totalcount < m_listdata.size()) {
					// 设置当前选择的图片编号
					m_dlnatitle.setText((totalcount + 1) + "/"
							+ GenieDlnaActionDefines.m_ListToalItem);
					bitimagecount = totalcount;
					Log.e("selected", "gallery选择：" + totalcount);
					// 选择缩略图
					g.setSelection(totalcount);
					selectitem = totalcount;
					small_adapter.setSelectItem(totalcount);

					GenieDebug.error("debug", "bigImage滑动停止后第  " + totalcount
							+ " 图片 ");
					if (GneieDlna.genieDlna != null) {
						GenieDlnaService
								.SetProgressBrowseLoadingCallback(GneieDlna.genieDlna);
						GneieDlna.genieDlna.StartDlnaAction(
								GenieDlnaActionDefines.ACTION_BROWSE_ITEM,
								totalcount, true);

						// GenieDlnaActionDefines.m_BrowseItemId=totalcount;
						// Intent Dlna = new
						// Intent("com.netgear.genie.GenieDlnaService");
						// Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,GenieDlnaActionDefines.ACTION_BROWSE_ITEM);
						// startService(Dlna);
					}
					if (getoneimage != null) {
						getoneimage.setSelectitem(totalcount);
						getoneimage.GetBigIconOnThread();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	/**
	 * 加载上面标题栏
	 */
	public void InitTitleView() {
		m_titler = (RelativeLayout) findViewById(R.id.titler);
		// GenieDlnaTab.m_about.setText(R.string.refresh);
		m_dlnatitle = (TextView) findViewById(R.id.txt_total);
		if (m_listdata == null
				|| (m_listdata != null && m_listdata.size() == 0)) {
			m_dlnatitle.setText("0/" + GenieDlnaActionDefines.m_ListToalItem);
		} else {
			m_dlnatitle.setText("1/" + GenieDlnaActionDefines.m_ListToalItem);
		}
		m_back = (Button) findViewById(R.id.but_back);
		m_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isback = true;
				CancelIconThread();
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_STOP);
				ImagesBrowseActivity.this.onBackPressed();
			}
		});
		m_back.setOnTouchListener(new OnTouchListener() {
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
		m_auto = (SpinnerButton) findViewById(R.id.but_auto);
		m_auto.setOnTouchListener(new OnTouchListener() {
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
		m_auto.setResIdAndViewCreatedListener(R.layout.spinner_dropdown,
				new SpinnerButton.ViewCreatedListener() {
					public void onViewCreated(View v) {
						v.findViewById(R.id.auto_close).setOnClickListener(
								new View.OnClickListener() {
									public void onClick(View v) {
										flag = false;
										autotime = 0;
										m_auto.dismiss();
										if (autoGallery != null) {
											autoGallery.cancel();
										}
									}
								});
						v.findViewById(R.id.auto_5s).setOnClickListener(
								new View.OnClickListener() {
									public void onClick(View v) {
										flag = true;
										autotime = 5000;
										if (autoGallery != null) {
											autoGallery.cancel();
										}
										handleClick(autotime);
									}
								});
						v.findViewById(R.id.auto_10s).setOnClickListener(
								new View.OnClickListener() {
									public void onClick(View v) {
										flag = true;
										autotime = 10000;
										if (autoGallery != null) {
											autoGallery.cancel();
										}
										handleClick(autotime);
									}
								});
						v.findViewById(R.id.auto_15s).setOnClickListener(
								new View.OnClickListener() {
									public void onClick(View v) {
										flag = true;
										autotime = 15000;
										if (autoGallery != null) {
											autoGallery.cancel();
										}
										handleClick(autotime);
									}
								});
						// v.findViewById(R.id.auto_20s).setOnClickListener(new
						// View.OnClickListener() {
						// public void onClick(View v) {
						// flag=true;
						// autotime=20000;
						// if(autoGallery!=null){
						// autoGallery .cancel();
						// }
						// handleClick(autotime);
						// }
						// });
						// v.findViewById(R.id.auto_25s).setOnClickListener(new
						// View.OnClickListener() {
						// public void onClick(View v) {
						// flag=true;
						// autotime=25000;
						// if(autoGallery!=null){
						// autoGallery .cancel();
						// }
						// handleClick(autotime);
						// }
						// });
						// v.findViewById(R.id.auto_30s).setOnClickListener(new
						// View.OnClickListener() {
						// public void onClick(View v) {
						// flag=true;
						// autotime=30000;
						// if(autoGallery!=null){
						// autoGallery .cancel();
						// }
						// handleClick(autotime);
						// }
						// });
					}
				});
	}

	private void handleClick(int auto) {
		m_auto.dismiss();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					gallery.setSelection(msg.getData().getInt("pos"));
					break;
				}
			};
		};
		autoGallery = new Timer();
		autoGallery.schedule(new TimerTask() {
			int gallerypisition = gallery.getSelectedItemPosition();

			@Override
			public void run() {
				if (flag) {
					if (gallerypisition < m_listdata.size() - 1) {
						gallerypisition = gallery.getSelectedItemPosition() + 1;
					} else {
						gallerypisition = 0;
					}

					Message msg = new Message();
					Bundle date = new Bundle();// 存放数据
					date.putInt("pos", gallerypisition);
					msg.setData(date);
					msg.what = 1;// 消息标识
					handler.sendMessage(msg);
				} else {
					return;
				}
			}
		}, auto, auto);
	}

	private void StartDlnaAction(int action) {
		GenieDebug.error("debug", "9999df999999 StartDlnaAction action = "
				+ action);
		Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
		Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION, action);
		startService(Dlna);
		GenieDebug.error("debug", "9999df999999 StartDlnaAction end");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		getoneimage.CancelBigIconThread();
		StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_STOP);
		super.onBackPressed();
		finish();

	}

}
