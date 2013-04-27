package com.wifianalyzer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.ItemMenuDialog;
import com.dragonflow.genie.ui.R; 
import com.wifianalyzer.bo.WifiRoomSignalInfo;
import com.wifianalyzer.db.RoomSignalDatabaseHelper;

public class WifiRoomSignal extends Activity implements OnTouchListener, OnGestureListener{
	
	
	private PopupWindow m_MoreDialog = null;
	private WifiManager mainWifi=null;
	public WifiReceiver receiverWifi= null;
	private List<ScanResult> wifiList=null;
	private ArrayAdapter<String> m_ssidAdapter = null;
	private Spinner m_selectssid = null;
	private List<String> ssidlist = new ArrayList<String>();
	private String current_BSSID;
	private RoomSignalDatabaseHelper dbhHelper;
	private ListView roomlistview=null;
	private List<WifiRoomSignalInfo> roomlist=new ArrayList<WifiRoomSignalInfo>();
	private RoomListAdapter roomlistaAdapter=null;
	private GestureDetector mGestureDetector;
	private View LastSelectedItem=null;
	private WifiRoomSignalInfo currentSignalInfo=null;
	private boolean isTestSignal=false;
	private int selectedIndex=-1;
	private boolean isScanning=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Display d=getWindowManager().getDefaultDisplay();
		final int woindow_w=d.getWidth();
		final int woindow_h=d.getHeight();
		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			setTheme(R.style.bigactivityTitlebarNoSearch);
		}else{			
			setTheme(R.style.activityTitlebarNoSearch);
		} 
		requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.wifiroomsignal);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_big);
		}else{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		}
		GenieGlobalDefines.SaveWiFiScanDefaultEntry(this, 5);
		InitTitleView();
		
		mGestureDetector = new GestureDetector((OnGestureListener) this);    
	    View viewSnsLayout = this.getWindow().getDecorView();    
        viewSnsLayout.setOnTouchListener(this);    
        viewSnsLayout.setLongClickable(true);   
		
		//
		dbhHelper=new RoomSignalDatabaseHelper(this);
		//wifi列表
		m_selectssid = (Spinner) this.findViewById(R.id.rs_wifiselector);
		m_ssidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ssidlist);
		m_ssidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_selectssid.setAdapter(m_ssidAdapter);
		m_selectssid.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterview, View view, int l, long l1)
			{
				setSelectedSSID(l);
//				adapterview.setVisibility(AdapterView.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> adapterview)
			{
			}

	
		});
		
		//房间列表
		roomlistview=(ListView) this.findViewById(R.id.rs_roomlist);
		roomlistaAdapter=new RoomListAdapter(this);
		roomlistview.setAdapter(roomlistaAdapter);
		roomlistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if(LastSelectedItem!=null){
					LastSelectedItem.setBackgroundDrawable(null);
					LastSelectedItem.findViewById(R.id.rs_test).setVisibility(View.INVISIBLE);
				}
				selectedIndex=index;
				LastSelectedItem=view;
				LastSelectedItem.findViewById(R.id.rs_test).setVisibility(View.VISIBLE);
				LastSelectedItem.setBackgroundColor(Color.GRAY);
				WifiRoomSignal.this.findViewById(R.id.rs_room_edit).setVisibility(View.VISIBLE);
				Button testButton=(Button) LastSelectedItem.findViewById(R.id.rs_test);
				currentSignalInfo=(WifiRoomSignalInfo) testButton.getTag();
				if(currentSignalInfo!=null && dbhHelper.isExistRoom(currentSignalInfo.getId())){
					WifiRoomSignal.this.findViewById(R.id.rs_room_delete).setVisibility(View.VISIBLE);
				}else{
					WifiRoomSignal.this.findViewById(R.id.rs_room_delete).setVisibility(View.INVISIBLE);
				}
				testButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						isTestSignal=true;
						startScan();
					}
				});
				testButton.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN){
							v.setBackgroundResource(R.drawable.title_bt_fj);
						}else if(event.getAction() == MotionEvent.ACTION_UP){
							v.setBackgroundResource(R.drawable.title_bt_bj);
						}
						return false;
					}
				});
				
			}
			
		});
		
		//初始化工具栏
		initToolbar();
		
		//刷新按钮
		this.findViewById(R.id.rs_refresh).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startScan();
				
			}
		});
		this.findViewById(R.id.rs_refresh).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.title_bt_fj);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.title_bt_bj);
				}
				return false;
			}
		});
		
		//视图按钮
		this.findViewById(R.id.bt_roomview).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(WifiRoomSignal.this,WifiRoomSignalMap.class);
				startActivity(intent);
				
			}
		});
		
		this.findViewById(R.id.bt_roomview).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.title_bt_fj);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.title_bt_bj);
				}
				return false;
			}
		});
		
		
		
		this.mainWifi = ((WifiManager)getSystemService(WIFI_SERVICE));
		this.receiverWifi = new WifiReceiver();
	    registerReceiver(this.receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    startScan();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(isScanning){
				CancelLoadingDialog();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	protected void onPause(){
	    unregisterReceiver(this.receiverWifi);
	    if(isScanning){
	    	CancelLoadingDialog();
	    }
	    super.onPause();
	}

	protected void onResume(){
	    registerReceiver(this.receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    super.onResume();
	}
	
	/**
	 * 选择下一项
	 */
	private void selectedNextItem(){
		if(LastSelectedItem!=null){
			LastSelectedItem.setBackgroundDrawable(null);
			LastSelectedItem.findViewById(R.id.rs_test).setVisibility(View.INVISIBLE);
			LastSelectedItem=null;
			currentSignalInfo=null;
			selectedIndex=-1;
		}
//		if(selectedIndex>=roomlistview.getChildCount()-1){
//			LastSelectedItem=roomlistview.getChildAt(roomlistview.getChildCount()-1);
//		}else{
//			LastSelectedItem=roomlistview.getChildAt(selectedIndex);
//		}
		if(LastSelectedItem!=null){
			LastSelectedItem.findViewById(R.id.rs_test).setVisibility(View.VISIBLE);
			LastSelectedItem.setBackgroundColor(Color.GRAY);
			WifiRoomSignal.this.findViewById(R.id.rs_room_edit).setVisibility(View.VISIBLE);
			WifiRoomSignal.this.findViewById(R.id.rs_room_delete).setVisibility(View.VISIBLE);
			Button testButton=(Button) LastSelectedItem.findViewById(R.id.rs_test);
			currentSignalInfo=(WifiRoomSignalInfo) testButton.getTag();
			testButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					isTestSignal=true;
					startScan();
				}
			});
			testButton.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN){
						v.setBackgroundResource(R.drawable.title_bt_fj);
					}else if(event.getAction() == MotionEvent.ACTION_UP){
						v.setBackgroundResource(R.drawable.title_bt_bj);
					}
					return false;
				}
			});
		}else{
			WifiRoomSignal.this.findViewById(R.id.rs_room_edit).setVisibility(View.INVISIBLE);
			WifiRoomSignal.this.findViewById(R.id.rs_room_delete).setVisibility(View.INVISIBLE);
		}
		
	}
	
	/**
	 * 初始化工具条
	 */
	private void initToolbar(){
		
		Button addButton=(Button) this.findViewById(R.id.rs_room_add);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mainWifi==null || (mainWifi!=null && !mainWifi.isWifiEnabled())){
					Toast.makeText(WifiRoomSignal.this, "Wifi is off!", Toast.LENGTH_SHORT).show();
					return;
				}
				LayoutInflater layoutInflater = LayoutInflater.from(WifiRoomSignal.this);
	 			final View loginAdd = layoutInflater.inflate(R.layout.roomsignal_info, null);
	 			AlertDialog dlg=new AlertDialog.Builder(WifiRoomSignal.this).setTitle(R.string.add_room).setView(loginAdd).setPositiveButton(R.string.ok,
	 					new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,int which) {
								EditText editText=(EditText) loginAdd.findViewById(R.id.edit_roomname);
								String roomname=editText.getText().toString().trim();
								if(roomname!=null && !"".equals(roomname)){
									setDialogClose(dialog,true);
									if(current_BSSID!=null &&!"".equals(current_BSSID.trim())){
										Date d=new Date();
										WifiRoomSignalInfo info=new WifiRoomSignalInfo();
										info.setBSSID(current_BSSID);
										info.setRoomName(roomname);
										info.setSignalLevel(-1);
										info.setCreateDateTime(d);
										info.setLastModifyDateTime(d);
										if(dbhHelper.addRoom(info)){
											loadRoomList();
											Toast.makeText(WifiRoomSignal.this, "Add Success", Toast.LENGTH_SHORT).show();
											dialog.dismiss();
										}else{
											Toast.makeText(WifiRoomSignal.this, "Add Failure", Toast.LENGTH_SHORT).show();
										}
									}
								}else{
									setDialogClose(dialog,false);
									Toast.makeText(WifiRoomSignal.this, "Room name can't be empty", Toast.LENGTH_SHORT).show();
								}
								
							}
	 				
	 				}).setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							setDialogClose(dialog,true);
							dialog.cancel();
							
						}
		 				
		 			}).create();
	 			dlg.setCanceledOnTouchOutside(false);
	 			dlg.show();
				
				
			}
		});
		addButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.title_bt_fj);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.title_bt_bj);
				}
				return false;
			}
		});
		
		Button editButton=(Button) this.findViewById(R.id.rs_room_edit);
		editButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentSignalInfo!=null){
					LayoutInflater layoutInflater = LayoutInflater.from(WifiRoomSignal.this);
		 			final View loginAdd = layoutInflater.inflate(R.layout.roomsignal_info, null);
		 			EditText editText=(EditText) loginAdd.findViewById(R.id.edit_roomname);
		 			editText.setText(currentSignalInfo.getRoomName()!=null?currentSignalInfo.getRoomName():"");
		 			AlertDialog dlg=new AlertDialog.Builder(WifiRoomSignal.this).setTitle(R.string.edit_room).setView(loginAdd).setPositiveButton(R.string.ok,
		 					new DialogInterface.OnClickListener(){
	
								@Override
								public void onClick(DialogInterface dialog,int which) {
									
									EditText editText=(EditText) loginAdd.findViewById(R.id.edit_roomname);
									String roomname=editText.getText().toString().trim();
									if(roomname!=null && !"".equals(roomname)){
										setDialogClose(dialog,true);
										if(currentSignalInfo!=null){
											currentSignalInfo.setRoomName(roomname);
											if(dbhHelper.isExistRoom(currentSignalInfo.getId().trim())){
												if(dbhHelper.updateRoom(currentSignalInfo)){
													loadRoomList();
													Toast.makeText(WifiRoomSignal.this, "Edit Success", Toast.LENGTH_SHORT).show();
													dialog.dismiss();
												}else{
													Toast.makeText(WifiRoomSignal.this, "Edit Failure", Toast.LENGTH_SHORT).show();
												}
											}else{
												if(dbhHelper.addRoom(currentSignalInfo)){
													WifiRoomSignal.this.findViewById(R.id.rs_room_delete).setVisibility(View.VISIBLE);
													roomlistaAdapter.notifyDataSetChanged();
													Toast.makeText(WifiRoomSignal.this, "Edit Success", Toast.LENGTH_SHORT).show();
													dialog.dismiss();
												}else{
													Toast.makeText(WifiRoomSignal.this, "Edit Failure", Toast.LENGTH_SHORT).show();
												}
											}
										}
									}else{
										setDialogClose(dialog,false);
										Toast.makeText(WifiRoomSignal.this, "Room name can't be empty", Toast.LENGTH_SHORT).show();
									}
									
								}
		 				
		 				}).setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								setDialogClose(dialog,true);
								dialog.cancel();
								
							}
			 				
			 			}).create();
		 			dlg.setCanceledOnTouchOutside(false);
		 			dlg.show();
				}
				
			}
		});
		editButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.title_bt_fj);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.title_bt_bj);
				}
				return false;
			}
		});
		
		Button deleteButton=(Button) this.findViewById(R.id.rs_room_delete);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentSignalInfo!=null){
					String msg=getResources().getString(R.string.delete_room_msg);
					if(msg!=null){
						msg=msg.replace("{roomname}", currentSignalInfo.getRoomName());
					}
					AlertDialog dlg=new AlertDialog.Builder(WifiRoomSignal.this).setMessage(msg).setTitle(R.string.delete_room).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							if(currentSignalInfo!=null && dbhHelper.isExistRoom(currentSignalInfo.getId().trim())){
								if(dbhHelper.deleteRoomById(currentSignalInfo.getId())){
									loadRoomList();
									selectedNextItem();
									Toast.makeText(WifiRoomSignal.this, "Delete Success", Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(WifiRoomSignal.this, "Delete Failure", Toast.LENGTH_SHORT).show();
								}
							}
							dialog.dismiss();
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					}).create();
					dlg.setCanceledOnTouchOutside(false);
					dlg.show();
				
				}
			}
		});
		deleteButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.title_bt_fj);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.title_bt_bj);
				}
				return false;
			}
		});
		
	}
	
	/**
	 * 设置弹出窗口是否点击按钮后关闭
	 * @param dialog
	 * @param isclose
	 */
	private void setDialogClose(DialogInterface dialog,boolean isclose){
		if(isclose){
		  try {
             Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
             boolean flag=field.isAccessible();
             field.setAccessible(true);
             field.set(dialog, isclose);
             field.setAccessible(flag);
          } catch (Exception e) {
        	  e.printStackTrace();
          }
		}else{
			try {
				Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
				boolean flag=field.isAccessible();
				field.setAccessible(true);
				field.set(dialog, isclose);
				field.setAccessible(flag);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 开始wifi扫描
	 */
	private void startScan(){
		if(mainWifi!=null ){
			if(mainWifi.isWifiEnabled()){
				mainWifi.startScan();
				ShowLoadingDialog();
			}else{
				Toast.makeText(this, "Wifi is off!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void ShowLoadingDialog() {
		RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(R.id.rs_progress);
		if(isTestSignal){
			((TextView)findViewById(R.id.rs_loading)).setText(getResources().getString(R.string.test_wifisignal) + "...");
		}else{
			((TextView)findViewById(R.id.rs_loading)).setText(getResources().getString(R.string.loading) + "...");
		}
	    localRelativeLayout.setVisibility(View.VISIBLE);
	    isScanning=true;
	}
	
	private void CancelLoadingDialog(){
	    RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(R.id.rs_progress);
//	    ((TextView)findViewById(R.id.rs_loading)).setText(getResources().getString(R.string.loading) + "...");
	    localRelativeLayout.setVisibility(View.GONE);
	    isScanning=false;
	}	
	
	/**
	 * 初始化标题栏
	 */
	 public void InitTitleView(){
		 
		 Button button1 = (Button)findViewById(R.id.back);
		 button1.setBackgroundResource(R.drawable.title_bt_bj);
		 Button button2 = (Button)findViewById(R.id.about);
		 button2.setBackgroundResource(R.drawable.title_more);
		 button2.setText(null);
		 button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		 button2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ShowMoreDialog();
			}
		});
		 button1.setOnTouchListener(new OnTouchListener() {
			 public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN){
						v.setBackgroundResource(R.drawable.title_bt_fj);
					}else if(event.getAction() == MotionEvent.ACTION_UP){
						v.setBackgroundResource(R.drawable.title_bt_bj);
					}
					return false;
				}
		});
		 
	 }
	 
	 /**
	  * 打开功能列表
	  */
	 private void ShowMoreDialog(){
		 Button localButton = (Button)findViewById(R.id.about);
		 View localView = LayoutInflater.from(this).inflate(R.layout.dialog_more, null);
		 ListView localListView = (ListView)localView.findViewById(R.id.list);
		 localListView.setAdapter(new MoreListAdapter(this));
//		 localListView.setItemChecked(-1, true);
		 localListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				 WifiRoomSignal.this.GotoMore((int)arg3);
				 if(m_MoreDialog!=null){
					 m_MoreDialog.dismiss();
				 }
			}
			 
		 });
		 m_MoreDialog = new PopupWindow(localView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		 m_MoreDialog.setBackgroundDrawable(new BitmapDrawable());
		 m_MoreDialog.setOnDismissListener(new PopupWindow.OnDismissListener(){
			public void onDismiss() {
				m_MoreDialog=null;
			}
			 
		 });
		 m_MoreDialog.showAsDropDown(localButton);
	 }
	 
	 private void setSelectedSSID(int index){
		 
		 if(wifiList!=null && index<wifiList.size()){
			 if(wifiList.get(index)!=null){
				 current_BSSID=wifiList.get(index).BSSID;
				 loadRoomList();
				 this.findViewById(R.id.rs_room_edit).setVisibility(View.INVISIBLE);
				 this.findViewById(R.id.rs_room_delete).setVisibility(View.INVISIBLE);
				 this.currentSignalInfo=null;
				 LastSelectedItem=null;
				 selectedIndex=-1;
			 }
		 }
		 
	 }
	 
	 private void loadRoomList(){
		 
		 if(current_BSSID!=null && !"".equals(current_BSSID.trim())){
			  this.roomlist=dbhHelper.getRoomsByBSSID(current_BSSID.trim());
			  if(this.roomlist==null || (this.roomlist!=null&&this.roomlist.size()==0)){
				  //Living room
				  WifiRoomSignalInfo info=new WifiRoomSignalInfo();
				  info.setBSSID(current_BSSID.trim());
				  info.setRoomName("Living Room");
				  info.setSignalLevel(-1);
				  this.roomlist.add(info);
				  //bed room 1
				  WifiRoomSignalInfo info1=new WifiRoomSignalInfo();
				  info1.setBSSID(current_BSSID.trim());
				  info1.setRoomName("Bedroom1");
				  info1.setSignalLevel(-1);
				  this.roomlist.add(info1);
				  //bed room2
				  WifiRoomSignalInfo info2=new WifiRoomSignalInfo();
				  info2.setBSSID(current_BSSID.trim());
				  info2.setRoomName("Bedroom2");
				  info2.setSignalLevel(-1);
				  this.roomlist.add(info2);
				  //Kitchen
				  WifiRoomSignalInfo info3=new WifiRoomSignalInfo();
				  info3.setBSSID(current_BSSID.trim());
				  info3.setRoomName("Kitchen");
				  info3.setSignalLevel(-1);
				  this.roomlist.add(info3);
					  
			  }
			  roomlistaAdapter.notifyDataSetChanged();
		 }
		 
	 }
	 
	 /**
	  * 打开相应功能视图
	  * @param paramInt
	  */
	 public void GotoMore(int paramInt){
		 Intent intent = new Intent();
		 switch (paramInt) {
		 case 0:
			 intent.setClass(this, GenieWifiScan.class);
			 startActivity(intent);
			 finish();
			 break;
		 case 1:
			 intent.setClass(this, GenieChannels.class);
			 startActivity(intent);
			 finish();
			 break;
		 case 2:
			 intent.setClass(this, GenieSignalHistogram.class);
			 startActivity(intent);
			 finish();
			 break;
		 case 3:
			 intent.setClass(this, GenieSignalGraph.class);
			 startActivity(intent);
			 finish();
			 break;
		 case 4:
			 intent.setClass(this, GenieWifiAct3.class);
			 startActivity(intent);
			 finish();
			 break;
		 case 5:
//			 intent.setClass(this, WifiRoomSignal.class);
//			 startActivity(intent);
//			 finish();
			 break;
		}
	 }

	 /**
	  * 功能选择列表适配器
	  * @author dy
	  *
	  */
	 private class MoreListAdapter extends BaseAdapter{
			private LayoutInflater mInflater;

		    public MoreListAdapter(Context arg2)
		    {
		      this.mInflater = LayoutInflater.from(arg2);
		    }

		    public int getCount() {
				return 6;
			}

			public Object getItem(int position) {
				return position;
			}

			public long getItemId(int position) {
				return position;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder;
				if(convertView==null){
					convertView = this.mInflater.inflate(R.layout.dialog_more_item, null);
					viewHolder = new ViewHolder();
					viewHolder.text = ((TextView)convertView.findViewById(R.id.text));
			        convertView.setTag(viewHolder);
				}else{
					viewHolder=(ViewHolder)convertView.getTag();
				}
				switch (position) {
				case 0:
					viewHolder.text.setText(WifiRoomSignal.this.getResources().getString(R.string.s_wifi_more_1).toString());
					break;
				case 1:
					viewHolder.text.setText(WifiRoomSignal.this.getResources().getString(R.string.s_wifi_more_2).toString());
					break;
				case 2:
					viewHolder.text.setText(WifiRoomSignal.this.getResources().getString(R.string.s_wifi_more_3).toString());
					break;
				case 3:
					viewHolder.text.setText(WifiRoomSignal.this.getResources().getString(R.string.s_wifi_more_4).toString());
					break;
				case 4:
					viewHolder.text.setText(WifiRoomSignal.this.getResources().getString(R.string.s_wifi_more_5).toString());
					break;
				case 5:
					viewHolder.text.setText(WifiRoomSignal.this.getResources().getString(R.string.s_wifi_more_6).toString());
					break;
				}
				return convertView;
			}
			class ViewHolder{
		      TextView text;

		      ViewHolder(){
		      }
		    }
		}
	 
	 /**
	  * wifi扫描广播接收器
	  * @author dy
	  *
	  */
	 class WifiReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			CancelLoadingDialog();
			wifiList=mainWifi.getScanResults();
			if(wifiList!=null){
				if(isTestSignal){
					if(current_BSSID!=null && currentSignalInfo!=null){
						for(ScanResult result:wifiList){
							if(result.BSSID.equals(current_BSSID) && result.BSSID.equals(currentSignalInfo.getBSSID().trim())){
								currentSignalInfo.setSignalLevel(WifiManager.calculateSignalLevel(result.level, 100));
								if(dbhHelper.isExistRoom(currentSignalInfo.getId().trim())){
									if(dbhHelper.updateRoom(currentSignalInfo)){
										Log.d("updateRoom", "id="+currentSignalInfo.getId());
										roomlistaAdapter.notifyDataSetChanged();
									}else{
										Log.d("updateRoom", "id="+currentSignalInfo.getId());
										Toast.makeText(WifiRoomSignal.this, "Test Failure", Toast.LENGTH_SHORT).show();
									}
								}else{
									if(dbhHelper.addRoom(currentSignalInfo)){
										Log.d("addRoom", "id="+currentSignalInfo.getId());
										WifiRoomSignal.this.findViewById(R.id.rs_room_delete).setVisibility(View.VISIBLE);
										roomlistaAdapter.notifyDataSetChanged();
									}else{
										Log.d("addRoom", "id="+currentSignalInfo.getId());
										Toast.makeText(WifiRoomSignal.this, "Test Failure", Toast.LENGTH_SHORT).show();
									}
								}
								break;
							}
						}
					}
					isTestSignal=false;
				}else{
					ssidlist.clear();
					for(ScanResult result:wifiList){
						ssidlist.add(result.SSID+"("+result.BSSID+")");
					}
					if(ssidlist.size()>0){
						m_selectssid.setEnabled(true);
					}else{
						m_selectssid.setEnabled(false);
					}
					m_ssidAdapter.notifyDataSetChanged();
				}
			}
			
		}
		 
	 }
	 
	 //房间列表适配器
	 class RoomListAdapter extends BaseAdapter{
		 
		 private Context context=null;
		 private LayoutInflater mlayoutInflater;
		 
		public RoomListAdapter(Context context) {
			this.context=context;
			this.mlayoutInflater=LayoutInflater.from(this.context);
		}
		 
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(WifiRoomSignal.this.roomlist!=null){
				return WifiRoomSignal.this.roomlist.size();
			}else{
				return 0;
			}
		}

		@Override
		public Object getItem(int i) {
			// TODO Auto-generated method stub
			if(i>=WifiRoomSignal.this.roomlist.size() || i<0){
				return null;
			}else{
				return WifiRoomSignal.this.roomlist.get(i);
			}
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int index, View view, ViewGroup viewGroup) {
			ViewHolder holder;
			if(view==null){
				view=this.mlayoutInflater.inflate(R.layout.roomlist_item,  null);
				holder=new ViewHolder();
				holder.roomname=(TextView) view.findViewById(R.id.roomname);
				holder.roomsignal=(TextView) view.findViewById(R.id.roomsignal);
				holder.testbutton=(Button) view.findViewById(R.id.rs_test);
				holder.testbutton.setFocusable(false);
				view.setTag(holder);
			}else{
				holder=(ViewHolder) view.getTag();
				if(holder==null){
					holder=new ViewHolder();
					view.setTag(holder);
				}
				if(holder.roomname==null){
					holder.roomname=(TextView) view.findViewById(R.id.roomname);
				}
				if(holder.roomsignal==null){
					holder.roomsignal=(TextView) view.findViewById(R.id.roomsignal);
				}
				if(holder.testbutton==null){
					holder.testbutton=(Button) view.findViewById(R.id.rs_test);
					holder.testbutton.setFocusable(false);
				}
			}
			if(selectedIndex!=index){
				view.setBackgroundDrawable(null);
				holder.testbutton.setVisibility(View.INVISIBLE);
			}
			WifiRoomSignalInfo info=WifiRoomSignal.this.roomlist.get(index);
			if(info!=null){
				holder.roomname.setText(info.getRoomName());
				if(info.getSignalLevel()==-1){
					holder.roomsignal.setText("N/A");
				}else{
					holder.roomsignal.setText(String.valueOf(info.getSignalLevel()<0?0:info.getSignalLevel())+"%");
				}
				holder.testbutton.setTag(info);
			}
			return view;
		}
		
		class ViewHolder{
			TextView roomname;
			TextView roomsignal;
			Button testbutton;
		}
		 
	 }

	 
	private int verticalMinDistance = 60;  
	private int minVelocity         = 0;  
	 
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (Math.abs(e1.getX() - e2.getX() )> Math.abs(e1.getY()-e2.getY())&& (e1.getX()-e2.getX())<0 && Math.abs(velocityX) > minVelocity && e2.getX()-e1.getX()>verticalMinDistance) {  
	    
		} else if (Math.abs(e2.getX() - e1.getX() )>Math.abs( e2.getY()-e1.getY()) && (e1.getX()-e2.getX())>0 && Math.abs(velocityX) > minVelocity && e1.getX()-e2.getX()>verticalMinDistance) {  
//	    	Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();  
	    	Intent intent=new Intent(WifiRoomSignal.this,GenieWifiScan.class);
	    	startActivity(intent);
	    	WifiRoomSignal.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	    	finish();
	    }
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	};
	
	public boolean dispatchTouchEvent(MotionEvent ev) {  
	    mGestureDetector.onTouchEvent(ev);  
	    return super.dispatchTouchEvent(ev);  
	}  
}
