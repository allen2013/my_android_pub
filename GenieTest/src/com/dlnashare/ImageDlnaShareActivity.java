package com.dlnashare;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
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
import com.dragonflow.GenieDlnaShare;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R;

public class ImageDlnaShareActivity extends Activity implements
		OnTouchListener, OnItemClickListener {

	public boolean stopthread = true;
	public MyGallery gallery;
	public static ImageBrowseAdapter adapter = null;
	public static SmallImageBrowseAdapter small_adapter = null;
	public static LinearLayout linear;
	public LinearLayout downlinear;
	public static boolean flag = false;
	private Bitmap mBitmap;
	private Gallery g;
	public ArrayList<GenieDlnaDeviceInfo> m_listdata = null;
	public int selectitem = -1;
	public static int totalcount = 1;
	// bigimage ��ʾ��ǰͼƬ��λ��
	public int bitimagecount = -1;
	public int bitimagecountnow = -1;
	public Button m_back = null;
	public SpinnerButton m_auto = null;
	private RelativeLayout m_titler = null;
	public TextView m_dlnatitle = null;
	public static int autotime = 0;
	private Timer autoGallery = null;
	public static String imageurl = "";
	public BrowseProgressDialog m_BrowseprogressDialog = null;
	public GetImageThread getoneimage = null;
	public static boolean openplay=false;

	public static String firsturl = ""; // ��һ�μ��ص�ͼƬ��·��
	public static UUID uuid=null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.imagedlnashare);
		m_listdata = new ArrayList<GenieDlnaDeviceInfo>();
		openplay=true;
//		
		System.out.println("����ҳ�����.....");
		// ȡͼƬ���ܸ���
		ShowBrowseProgressDialog();
		getFileDir(ImageDlnaShareActivity.imageurl);
		CancleBrowseProgressDialog();

		// ���ȡһ��ͼƬ
		getoneimage = GetImageThread.Imagethread();
		getoneimage.setM_listdata(m_listdata);

		// ���ؽ���
		InitTitleView();
		Initgallery();
		// //�����߳� ȡͼƬ
		// GetIconOnThread();

		// ��һ��Ĭ�ϼ���
		// GetIconOnThread(GenieDlnaActionDefines.m_BrowseItemId, 0);
		getoneimage.setSelectitem(bitimagecount);
		getoneimage.GetIconOnThread();
		getoneimage.GetBigIconOnThread();
		// gallery.setSelection(0);
		gallery.setSelection(bitimagecount);
		g.setSelection(bitimagecount);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ClearListData();
		ImageDlnaShareActivity.uuid=null;
		GenieDlnaShare.sendflag=false;
		openplay=false;
		autotime=0;
		GenieDlnaShare.openplay=false;
		GenieDlnaShare.open=false;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// if(m_listdata!=null)
		// m_listdata.clear();
		super.onResume();
		totalcount = 0;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		ImageDlnaShareActivity.uuid=null;
		openplay=false;
		GenieDlnaShare.sendflag=false;
		GenieDlnaShare.openplay=false;
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

	public void GetIconOnThread(final int selecone, final int nowcount) {

		if (m_listdata == null)
			return;
		CancelIconThread();

		m_IconThread = new Thread(new Runnable() {

			@Override
			public void run() {
				int number = 10;
				int size = m_listdata.size();
				int selectitems = selecone;
				GenieDebug.error("debug", "����ֹͣ��ʮ�ŵ� " + nowcount + " ��ȡͼƬ��ʼ");
				while (number > 0) {
					if (selectitems >= size||selectitems<0) {
						return;
					}
					if (m_listdata.get(selectitems).downloading
							&& m_listdata.get(selectitems).m_iconUrl != null) {
						GenieDebug.error("debug", "����ֹͣ��� " + nowcount
								+ " ��ȡͼƬ��ʼ...ȡ�� " + selectitems + " ��");
						try {
							BitmapFactory.Options opt = new BitmapFactory.Options();
							opt.inPreferredConfig = Bitmap.Config.RGB_565;
							opt.inJustDecodeBounds = true;
							BitmapFactory.decodeFile(
									m_listdata.get(selectitems).m_iconUrl, opt);
							opt.inJustDecodeBounds = false;
							if (opt.outWidth > opt.outHeight) {
								opt.inSampleSize = opt.outWidth / 50;
							} else {
								opt.inSampleSize = opt.outHeight / 50;
							}
							mBitmap = BitmapFactory.decodeFile(
									m_listdata.get(selectitems).m_iconUrl, opt);
							if (mBitmap != null) {
								m_listdata.get(selectitems).downloading = false;
								m_listdata.get(selectitems).downimage = mBitmap;
								handler.post(new Runnable() {
									@Override
									public void run() {
										// ImagesBrowseActivity.adapter.notifyDataSetChanged();
										small_adapter.notifyDataSetChanged();
									}
								});
								GenieDebug.error("debug", "����ֹͣ��� " + nowcount
										+ " ��ȡͼƬ��ʼ...ȡ�� " + selectitems
										+ " ��.�ɹ�!!!");
								if (mBitmap.isRecycled()) {
									mBitmap.recycle();
									System.gc();
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
						} catch (Error e) {
							System.gc();
							e.printStackTrace();
							return;
						} finally {
							selectitems++;
							number--;
						}
						System.gc();
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
					} else {
						selectitems++;
						number--;
						GenieDebug.error("debug", "����ֹͣ��� " + nowcount
								+ " ��ȡͼƬ��ʼ...ȡ�� " + selectitems + " ��.ʧ��!!!");
					}
				}
				int afternumber=10;
				int aftersetect=selecone-1;
				GenieDebug.error("debug", "����ֹͣǰ���ŵ� " + nowcount + " ��ȡͼƬ��ʼ");
				while (afternumber > 0) {
					if (aftersetect >= size||aftersetect<=0) {
						return;
					}
					if (m_listdata.get(aftersetect).downloading
							&& m_listdata.get(aftersetect).m_iconUrl != null) {
						GenieDebug.error("debug", "����ֹͣ��� " + nowcount
								+ " ��ȡͼƬ��ʼ...ȡ�� " + selectitems + " ��");
						try {
							BitmapFactory.Options opt = new BitmapFactory.Options();
							opt.inPreferredConfig = Bitmap.Config.RGB_565;
							opt.inJustDecodeBounds = true;
							BitmapFactory.decodeFile(
									m_listdata.get(aftersetect).m_iconUrl, opt);
							opt.inJustDecodeBounds = false;
							if (opt.outWidth > opt.outHeight) {
								opt.inSampleSize = opt.outWidth /50;
							} else {
								opt.inSampleSize = opt.outHeight / 50;
							}
							mBitmap = BitmapFactory.decodeFile(
									m_listdata.get(aftersetect).m_iconUrl, opt);
							if (mBitmap != null) {
								m_listdata.get(aftersetect).downloading = false;
								m_listdata.get(aftersetect).downimage = mBitmap;
								handler.post(new Runnable() {
									@Override
									public void run() {
										// ImagesBrowseActivity.adapter.notifyDataSetChanged();
										small_adapter
												.notifyDataSetChanged();
									}
								});
								GenieDebug.error("debug", "����ֹͣ��� " + nowcount
										+ " ��ȡͼƬ��ʼ...ȡ�� " + selectitems
										+ " ��.�ɹ�!!!");
								if (mBitmap.isRecycled()) {
									mBitmap.recycle();
									System.gc();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} catch (Error e) {
							System.gc();
							e.printStackTrace();
							return;
						} finally {
							aftersetect--;
							afternumber--;
						}
						System.gc();
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
					} else {
						aftersetect--;
						afternumber--;
						GenieDebug.error("debug", "����ֹͣ��� " + nowcount
								+ " ��ȡͼƬ��ʼ...ȡ�� " + selectitems + " ��.ʧ��!!!");
					}
				}
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

	/**
	 * ����������������
	 */
	public void Initgallery() {
		linear = (LinearLayout) findViewById(R.id.linear1);
		// RelativeLayout relative = (RelativeLayout)
		// findViewById(R.id.relative);
		// relative.setBackgroundColor(Color.GRAY);
		gallery = (MyGallery) findViewById(R.id.gallery1);
		gallery.setVerticalFadingEdgeEnabled(false);// ȡ����ֱ����߿�
		gallery.setHorizontalFadingEdgeEnabled(false);// ȡ��ˮƽ����߿�
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
				if (selectitem == position)
					return;
				// Toast.makeText(ImagesBrowseActivity.this,
				// "��ѡ����" + (position + 1) + " ��ͼƬ", Toast.LENGTH_SHORT)
				// .show();
				selectitem = position;
				// ����СͼƬ
				if (m_listdata.get(position).downloading) {
					getoneimage.setSelectitem(position);
					getoneimage.GetIconOnThread();
				}
				gallery.setSelection(position);
			}
		});
		g.setCallbackDuringFling(false);
		g.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				small_adapter.setSelectItem(arg2);
				GenieDebug.error("debug", "����ֹͣ��� " + totalcount
						+ " ��~~~~~~~~~~~~~~~~~~~~");
				GetIconOnThread(arg2, totalcount);
				totalcount++;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				getoneimage.setSelectitem(position);
				getoneimage.GetBigIconOnThread();
			}
		});
		gallery.setCallbackDuringFling(false);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int totalcount, long arg3) {

				if (bitimagecountnow != totalcount) {
					m_dlnatitle.setText((totalcount + 1) + "/"
							+ m_listdata.size());
					bitimagecountnow = totalcount;
					g.setSelection(totalcount);
					GenieDebug.error("debug", "bigImage����ֹͣ���  " + totalcount
							+ " ͼƬ ");
					 if (GenieDlnaShare.geniedlnashare!=null&&!m_listdata.get(totalcount).m_iconUrl.equals(GenieDlnaActionDefines.m_ShareFilePath)) {
						 if(ImageDlnaShareActivity.uuid!=null){
							 GenieDlnaActionDefines.m_WorkRenderUUID= ImageDlnaShareActivity.uuid;
							 GenieDlnaShare.geniedlnashare.sendImage(m_listdata.get(totalcount).m_iconUrl, true);
						 }
						
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
	 * �������������
	 */
	public void InitTitleView() {
		m_titler = (RelativeLayout) findViewById(R.id.titler);
		// GenieDlnaTab.m_about.setText(R.string.refresh);
		m_dlnatitle = (TextView) findViewById(R.id.txt_total);
		m_dlnatitle.setText(bitimagecount+"/" + m_listdata.size());
		m_back = (Button) findViewById(R.id.but_back);
		m_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_STOP);
				ImageDlnaShareActivity.this.onBackPressed();
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
//						v.findViewById(R.id.auto_20s).setOnClickListener(
//								new View.OnClickListener() {
//									public void onClick(View v) {
//										flag = true;
//										autotime = 20000;
//										if (autoGallery != null) {
//											autoGallery.cancel();
//										}
//										handleClick(autotime);
//									}
//								});
//						v.findViewById(R.id.auto_25s).setOnClickListener(
//								new View.OnClickListener() {
//									public void onClick(View v) {
//										flag = true;
//										autotime = 25000;
//										if (autoGallery != null) {
//											autoGallery.cancel();
//										}
//										handleClick(autotime);
//									}
//								});
//						v.findViewById(R.id.auto_30s).setOnClickListener(
//								new View.OnClickListener() {
//									public void onClick(View v) {
//										flag = true;
//										autotime = 30000;
//										if (autoGallery != null) {
//											autoGallery.cancel();
//										}
//										handleClick(autotime);
//									}
//								});
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
					Bundle date = new Bundle();// �������
					date.putInt("pos", gallerypisition);
					msg.setData(date);
					msg.what = 1;// ��Ϣ��ʶ
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
		StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_STOP);
		super.onBackPressed();

	}

	private FileInputStream inputStream;

	public void getFileDir(String filePath) {

		File f = new File(filePath);
		File[] files = f.listFiles();// �г������ļ�
		// �������ļ�����list��
		// �ļ�����
		String fileType = "";
		if (files != null) {
			int count = files.length;// �ļ�����
			for (int i = 0; i < count; i++) {
				File file = files[i];
				GenieDebug.error("debug", "�ļ���:   " + file.getName()
						+ " ~~~~~~" + file.getPath());
				try {
					fileType = file
							.getPath()
							.substring(file.getPath().lastIndexOf(".") + 1,
									file.getPath().length()).toUpperCase();
					if ("JPG".equals(fileType) || "GIF".equals(fileType)
							|| "BMP".equals(fileType) || "PNG".equals(fileType)) {
						AddGenieDlnaDeviceInfo(file.getPath(), -1, null);
					}
				} catch (Exception ex) {
					continue;
				}

			}
		}
	}

	public void AddGenieDlnaDeviceInfo(String path, int select, Bitmap bitmap) {
		if (select == -1) {
			GenieDlnaDeviceInfo genieinfo = new GenieDlnaDeviceInfo();
			genieinfo.m_iconUrl = path;
			m_listdata.add(genieinfo);
			if (firsturl.equals(path)) {
				bitimagecount = m_listdata.size() - 1;
			}
		} else {
			GenieDebug.error("debug", "ȡ���� " + select + " ͼƬ ");
			GenieDlnaDeviceInfo genieinfo = m_listdata.get(select);
			genieinfo.bigloading = false;
			genieinfo.bitimage = bitmap;
			genieinfo.downimage = bitmap;
			genieinfo.downloading = false;
		}

	}

	float beforeLenght = 0.0f; // ���������
	float afterLenght = 0.0f; // ���������
	boolean isScale = false;
	float currentScale = 1.0f;// ��ǰͼƬ�����ű���

	private class GalleryChangeListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			currentScale = 1.0f;
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
		case MotionEvent.ACTION_POINTER_DOWN:// �������
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
					float scaleRate = gapLenght / 854;// ���ű���
					Animation myAnimation_Scale = new ScaleAnimation(
							currentScale, currentScale + scaleRate,
							currentScale, currentScale + scaleRate,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					myAnimation_Scale.setDuration(100);
					myAnimation_Scale.setFillAfter(true);
					myAnimation_Scale.setFillEnabled(true);
					currentScale = currentScale + scaleRate;
					gallery.getSelectedView().setLayoutParams(
							new Gallery.LayoutParams(
									(int) (480 * (currentScale)),
									(int) (854 * (currentScale))));
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
	 * ���������ľ���
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	public void ClearListData() {
		if (null == m_listdata) {
			m_listdata = new ArrayList<GenieDlnaDeviceInfo>();
			return;
		}
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
		m_listdata.clear();
		m_listdata = null;
	}

	public void CancleBrowseProgressDialog() {
		if (null != m_BrowseprogressDialog) {
			if (m_BrowseprogressDialog.isShowing())
				m_BrowseprogressDialog.dismiss();
			m_BrowseprogressDialog = null;
		}
	}

	public void ShowBrowseProgressDialog() {
		CancleBrowseProgressDialog();

		m_BrowseprogressDialog = new BrowseProgressDialog(
				ImageDlnaShareActivity.this);
		m_BrowseprogressDialog.setMessage("Please wait...");
		m_BrowseprogressDialog.show();
	}
}

class BrowseProgressDialog extends ProgressDialog {

	public BrowseProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();

		GenieDebug.error("debug", "BrowseProgressDialog dismiss");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}
}
