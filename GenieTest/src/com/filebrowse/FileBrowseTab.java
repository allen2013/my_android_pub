package com.filebrowse;

import java.io.File;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieGlobalDefines;
import com.dragonflow.genie.ui.R;
import com.ewm.SpinnerButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

public class FileBrowseTab extends TabActivity {
	private RadioGroup group;
	// private TabHost tabHost;
	public TabHost tabHost;
	public RadioButton m_radio0;
	public RadioButton m_radio1;
	public RadioButton m_radio2;
	public RadioButton m_radio3;
	public RadioButton m_radio4;
	public RadioButton m_radio5;
	public RadioButton m_radio6;
	public static final String FILELOCATION = "FileLocation";
	public static final String FILESHARE = "FileShare";
	public static final String TAB_PLAY = "tab_play";
	public static final String TAB_OPTION = "tab_option";

	public static FileBrowseTab filebrowsetab = null;

	public static Button m_back = null;
	public static Button m_about = null;

	public static TextView m_dlnatitle = null;
	public EditText txt_path=null;
	public RadioButton but_type=null;
	private PopupWindow type_window;
	private boolean isMultiChoice=false;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		m_back = null;
		m_about = null;

	}

	// public native int StopServer();

	// static{
	// System.loadLibrary("genieupnp");
	// }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub

		super.onConfigurationChanged(newConfig);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		filebrowsetab = null;
		Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();
		GenieDebug.error("onCreate", "onCreate --woindow_w == " + woindow_w);

		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			setTheme(R.style.bigactivityTitlebarNoSearch);
		} else {
			setTheme(R.style.activityTitlebarNoSearch);
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE
				| Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.filebrowsetabs);

		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.filebrosetitlebar);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.filebrosetitlebar);
		}

		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(FILELOCATION)
				.setIndicator(FILELOCATION)
				.setContent(new Intent(this, LocationFileActivity.class)));
		tabHost.addTab(tabHost.newTabSpec(FILESHARE).setIndicator(FILESHARE)
				.setContent(new Intent(this, ConnComputerListActivity.class)));
		// tabHost.addTab(tabHost.newTabSpec(TAB_PLAY)
		// .setIndicator(TAB_PLAY)
		// .setContent(new Intent(this,GneieDlnaPlay.class)));
		// tabHost.addTab(tabHost.newTabSpec(TAB_OPTION)
		// .setIndicator(TAB_OPTION)
		// .setContent(new Intent(this,GenieDlnaOption.class)));
		ReuiTabhost();
		this.setDefaultTab(FILELOCATION);
		
		txt_path=(EditText) this.findViewById(R.id.txt_path);
		but_type=(RadioButton) this.findViewById(R.id.radiobutton_type);
		
		txt_path.setText(Environment.getExternalStorageDirectory().getPath().toString());
		txt_path.setFocusable(false);
		but_type.setText("本地");
		but_type.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){      
                      v.setBackgroundResource(R.drawable.title_bt_fj);
               		
               }else if(event.getAction() == MotionEvent.ACTION_UP){      
                       v.setBackgroundResource(R.drawable.title_bt_bj);
                      
               }      
				return false;
			}
		});
		
		but_type.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 if (type_window != null) {
    				if (!type_window.isShowing()) {
    					// 设置PopupWindow弹出,退出样式
    					type_window.setAnimationStyle(R.style.Animation_dropdown);
    					// 计算popupWindow下拉x轴的位置
    					int lx = (but_type.getWidth() - type_window.getContentView().getMeasuredWidth() - 7) / 2;
    					// showPopupWindow
    					type_window.showAsDropDown(but_type,lx,-5);
    				}else{
    					type_window.dismiss();
    				}
    			}else{
//                    type_window.setHeight(82);
//                    type_window.setWidth(100);
               		LayoutInflater layoutInflater=LayoutInflater.from(FileBrowseTab.this);
               		View contentView=layoutInflater.inflate(R.layout.filebrowser_type_dropdown, null);
               		type_window=new PopupWindow(FileBrowseTab.this.getBaseContext());
               		type_window.setWidth(LayoutParams.WRAP_CONTENT);
               		type_window.setHeight(LayoutParams.WRAP_CONTENT);
               		type_window.setContentView(contentView);
               		type_window.setFocusable(true);
               		type_window.setAnimationStyle(R.style.Animation_dropdown);
               		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
               		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
               		contentView.measure(w, h);
					// 计算popupWindow下拉x轴的位置
					int lx = (but_type.getWidth() - contentView.getMeasuredWidth() - 7) / 2;
					// showPopupWindow
					type_window.showAsDropDown(but_type,lx,-5);
    			}
			}
		});
		
		filebrowsetab = this;
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		GenieDebug.error("group", " -tab-onResume---");
		super.onResume();
	}

	public void ReuiTabhost() {
		RadioGroup group = (RadioGroup) findViewById(R.id.main_radio);
		m_radio0 = (RadioButton) findViewById(R.id.radio_chooesefile);
		m_radio1 = (RadioButton) findViewById(R.id.radio_copy);
		m_radio1.setVisibility(View.GONE);
		m_radio2 = (RadioButton) findViewById(R.id.radio_cut);
		m_radio2.setVisibility(View.GONE);
		m_radio3 = (RadioButton) findViewById(R.id.radio_paste);
		m_radio3.setVisibility(View.GONE);
		m_radio4 = (RadioButton) findViewById(R.id.radio_up);
		m_radio5 = (RadioButton) findViewById(R.id.radio_new);
		m_radio6 = (RadioButton) findViewById(R.id.radio_list);

		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				boolean isLocation=true;
				String tabTag=tabHost.getCurrentTabTag();
				if(FileBrowseTab.FILESHARE.equals(tabTag)){
					isLocation=false;
				}
				Intent intent=null;
				switch (checkedId) {
					case R.id.radio_chooesefile:{
						if(isMultiChoice){
							isMultiChoice=false;
							m_radio1.setVisibility(View.GONE);
							m_radio2.setVisibility(View.GONE);
							m_radio3.setVisibility(View.GONE);
						}else{
							isMultiChoice=true;
							m_radio1.setVisibility(View.VISIBLE);
							m_radio2.setVisibility(View.VISIBLE);
							m_radio3.setVisibility(View.VISIBLE);
						}
						group.clearCheck();
						break;
					}
					case R.id.radio_copy:{
						break;
					}
					case R.id.radio_cut:{
						break;
					}
					case R.id.radio_paste:{
						break;
					}
					case R.id.radio_up:{
						if(isLocation){
							intent=new Intent(FileOperatesActionDefinition.LOCATION_UP);
							sendBroadcast(intent);
						}else{
							
						}
						group.clearCheck();
						break;
					}
					case R.id.radio_new:{
						if(isLocation){
							intent=new Intent(FileOperatesActionDefinition.LOCATION_NEW);
							sendBroadcast(intent);
						}else{
							
						}
						group.clearCheck();
						break;
					}
					case R.id.radio_list:{
						break;
					}
					default:
						break;
				}
				
			}
		});
		
	}
	
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}
	
}
