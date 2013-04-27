package com.filebrowse;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dragonflow.genie.ui.R;
import com.filebrowse.db.DatabaseHelper;

public class ConnComputerListActivity extends Activity implements OnTouchListener, OnGestureListener {

	private  List<ComputerConnInfo> datalist;
	private ComputerInfoDataAdapter listAdapter;
	private DatabaseHelper dbHelper=null;
	private static final int REQUEST_FILEPAGE=1;
	private static final int DIALOG_COMPUTEREDIT=1;
	private ComputerConnInfo currentConnInfo;
	private final static int EDIT_COMPUTER=0;
	private final static int DELETE_COMPUTER=1;
	private GestureDetector mGestureDetector;
	public static ConnComputerListActivity conn;
	
	public ConnComputerListActivity() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
//	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//	    setContentView(R.layout.computerlist);
//	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.computer_title);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE); 
    	setContentView(R.layout.computerlist);
        
	    
	    dbHelper=new DatabaseHelper(this, "ComputerInfo", null, 3);
	    GridView computerView=(GridView) this.findViewById(R.id.computerlist_gridview);
	    //注册上下文菜单
	    registerForContextMenu(computerView);
	    datalist=new ArrayList<ComputerConnInfo>();
	    listAdapter=new ComputerInfoDataAdapter(this, datalist);
	    computerView.setAdapter(listAdapter);
	    computerView.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> arg0, View view, int arg2,
	    			long arg3) {
	    		ComputerConnInfo info=(ComputerConnInfo) view.getTag();
	    		if(info!=null){
		    		Intent intent = new Intent();
		    		intent.putExtra("ComputerInfo_ip",info.getIp());
		    		intent.putExtra("ComputerInfo_username", info.getUsername());
		    		intent.putExtra("ComputerInfo_password", info.getPassword());
		    		intent.putExtra("ComputerInfo_domain", "");	
	                intent.setClass(ConnComputerListActivity.this, FileListActivity.class);  
	                startActivityForResult(intent,REQUEST_FILEPAGE);
//	                finish();
	    		}
	    		
	    	}
		});
//	    computerView.setOnItemLongClickListener(new OnItemLongClickListener() {
//	    	public boolean onItemLongClick(AdapterView<?> arg0, View view,
//	    			int arg2, long arg3) {
////	    		
////	        	ComputerConnInfo info=(ComputerConnInfo)view.getTag();
////	        	if(info!=null){
////		    		Bundle bundle=new Bundle();
////		    		bundle.putSerializable("ComputerInfo", info);
////		    		showDialog(DIALOG_COMPUTEREDIT, bundle);
////	        	}
//	    		
//	    		return true;
//	    	}
//		});
	    
	    Button addButton=(Button) this.findViewById(R.id.addComputer);
	    addButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				LayoutInflater layoutInflater = LayoutInflater.from(ConnComputerListActivity.this);
	 			final View loginAdd = layoutInflater.inflate(R.layout.loginpc, null);
				AlertDialog.Builder builder=new AlertDialog.Builder(ConnComputerListActivity.this).setTitle("输入配置").setView(loginAdd).setPositiveButton("确定",
					       new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int which) {
			        	  EditText name_text=(EditText) loginAdd.findViewById(R.id.login_name);
			        	  EditText ip_text=(EditText) loginAdd.findViewById(R.id.login_ip);
			        	  EditText username_text=(EditText) loginAdd.findViewById(R.id.login_username);
			        	  EditText password_text=(EditText) loginAdd.findViewById(R.id.login_password);
			        	  ComputerConnInfo info=new ComputerConnInfo();
			        	  info.setName(name_text.getText().toString().trim());
			        	  info.setIp(ip_text.getText().toString().trim());
			        	  info.setUsername(username_text.getText().toString().trim());
			        	  info.setPassword(password_text.getText().toString().trim());
			        	  ContentValues values=new ContentValues();
			        	  values.put("id", info.getId());
			        	  values.put("name", info.getName());
			        	  values.put("ip", info.getIp());
			        	  values.put("username", info.getUsername());
			        	  values.put("password", info.getPassword());
			        	  SQLiteDatabase db=null;
			        	  try{
				        	  db = dbHelper.getWritableDatabase();
				        	  long flag=db.insert("ComputerInfo", "id=?", values);
				        	  if(flag!=-1){
				        		  initComputerList();
				        		  Toast.makeText(ConnComputerListActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
				        	  }else{
				        		  Toast.makeText(ConnComputerListActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
				        	  }
			        	  }catch(Exception ex){
			        		  ex.printStackTrace();
			        	  }finally{
			        		  if(db!=null){
			        			  db.close();
			        		  }
			        	  }
			        	  dialog.dismiss();
			           }
			       }).setNegativeButton("取消",
			       new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int which) {
			                dialog.cancel();
			           }
			       });
				AlertDialog dialog=builder.create();
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				
			}
		});
	    
	    this.findViewById(R.id.computerlist_btnback).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				finish();
				
			}
		});
	    
	  //加载列表
	    initComputerList();
	    
	    mGestureDetector = new GestureDetector((OnGestureListener) this);    
        LinearLayout viewSnsLayout = (LinearLayout)findViewById(R.id.Linear_conn);    
        viewSnsLayout.setOnTouchListener(this);    
        viewSnsLayout.setLongClickable(true);   
        
        conn=this;
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		Dialog dialog=null;
		switch(id){
			case DIALOG_COMPUTEREDIT:{
				dialog=this.createEditComputerDlg();
				dialog.setCanceledOnTouchOutside(false);
				break;
			}
		}
		return dialog;
	}
	
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		// TODO Auto-generated method stub
		switch(id){
			case DIALOG_COMPUTEREDIT:{
				if(bundle!=null){
					ComputerConnInfo info=(ComputerConnInfo) bundle.getSerializable("ComputerInfo");
					if(info!=null){
						currentConnInfo=info;
						EditText name_text=(EditText) dialog.findViewById(R.id.login_name);
						EditText ip_text=(EditText) dialog.findViewById(R.id.login_ip);
						EditText username_text=(EditText) dialog.findViewById(R.id.login_username);
						EditText password_text=(EditText) dialog.findViewById(R.id.login_password);
						name_text.setText(info.getName());
						ip_text.setText(info.getIp());
						username_text.setText(info.getUsername());
						password_text.setText(info.getPassword());
					}
				}
				break;
			}
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_FILEPAGE){
			if(requestCode==RESULT_CANCELED){
				initComputerList();
			}
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		System.out.println(v);
		menu.setHeaderTitle(R.string.operations);
		menu.setHeaderIcon(R.drawable.option);
		menu.add(0,EDIT_COMPUTER,0,R.string.edit_computer);
		menu.add(0,DELETE_COMPUTER,0,R.string.delete_computer);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		 AdapterView.AdapterContextMenuInfo menuInfo=(AdapterContextMenuInfo) item.getMenuInfo();
		 View view=menuInfo.targetView;
		 if(view!=null){
			ComputerConnInfo info=(ComputerConnInfo)view.getTag();
			switch(item.getItemId()){
				case EDIT_COMPUTER:{
					if(info!=null){
			    		Bundle bundle=new Bundle();
			    		bundle.putSerializable("ComputerInfo", info);
			    		showDialog(DIALOG_COMPUTEREDIT, bundle);
					}
					break;
				}
				case DELETE_COMPUTER:{
					final ComputerConnInfo _info=info;
					String msg=getResources().getText(R.string.determine_delete_msg).toString();
					msg=msg.replace("{name}", (info.getName()==null||"".equals(info.getName()))?info.getIp():info.getName());
					new AlertDialog.Builder(ConnComputerListActivity.this).setTitle(R.string.delete_computer).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if(_info!=null){
								deleteComputer(_info);
							}
							dialog.dismiss();
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
					
					break;
				}
			}
		 }
		return super.onContextItemSelected(item);
	}
	
	private void initComputerList(){
		datalist.clear();
		listAdapter.notifyDataSetChanged();
		
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db = dbHelper.getReadableDatabase();
			cursor = db.query("ComputerInfo", new String[]{"id","name","ip","username","password"}, null, null, null, null, "ip asc");
			System.out.println("查到的数据为：");
			while(cursor.moveToNext()){
			    String id = cursor.getString(cursor.getColumnIndex("id"));
			    String name = cursor.getString(cursor.getColumnIndex("name"));
			    String ip = cursor.getString(cursor.getColumnIndex("ip"));
			    String username = cursor.getString(cursor.getColumnIndex("username"));
			    String password = cursor.getString(cursor.getColumnIndex("password"));
			    System.out.println("-->"+id+"::::::::::"+name+":::::"+ip);
			    ComputerConnInfo info=new ComputerConnInfo();
			    info.setId(id);
			    info.setIp(ip);
			    info.setName(name);
			    info.setUsername(username);
			    info.setPassword(password);
				datalist.add(info);
				listAdapter.notifyDataSetChanged();
			}
		}catch(Exception ex){
			ex.printStackTrace();
			
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	
	private void deleteComputer(ComputerConnInfo info){
		if(info!=null){
			SQLiteDatabase db=null;
			try{
				db = dbHelper.getWritableDatabase();
				long flag=db.delete("ComputerInfo", "id="+info.getId(), null);
				if(flag>0){
					initComputerList();
					 Toast.makeText(ConnComputerListActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
					 listAdapter.notifyDataSetChanged();
				}else{
					 Toast.makeText(ConnComputerListActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				if(db!=null){
					db.close();
				}
			}
		}
		
	}
	
	private Dialog createEditComputerDlg(){
		
		LayoutInflater layoutInflater = LayoutInflater.from(ConnComputerListActivity.this);
		final View loginAdd = layoutInflater.inflate(R.layout.loginpc, null);
		AlertDialog dialog=new AlertDialog.Builder(ConnComputerListActivity.this).setTitle(R.string.edit_computer).setView(loginAdd).setPositiveButton(R.string.ok,
			   new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int which) {
	        	   
	        	  EditText ip_text=(EditText) loginAdd.findViewById(R.id.login_ip);
	        	  EditText username_text=(EditText) loginAdd.findViewById(R.id.login_username);
	        	  EditText password_text=(EditText) loginAdd.findViewById(R.id.login_password);
	        	  EditText name_text=(EditText) loginAdd.findViewById(R.id.login_name);
	        	  if(currentConnInfo!=null){
	        		  currentConnInfo.setName(name_text.getText().toString());
	        		  currentConnInfo.setIp(ip_text.getText().toString());
	        		  currentConnInfo.setUsername(username_text.getText().toString());
	        		  currentConnInfo.setPassword(password_text.getText().toString());
		        	  ContentValues values=new ContentValues();
	//	        	  values.put("id", info.getId());
		        	  values.put("name", currentConnInfo.getName());
		        	  values.put("ip", currentConnInfo.getIp());
		        	  values.put("username", currentConnInfo.getUsername());
		        	  values.put("password", currentConnInfo.getPassword());
		        	  System.out.println(currentConnInfo.getId());
		        	  SQLiteDatabase db=null;
		        	  try{
		        		  db = dbHelper.getWritableDatabase();
			        	  long flag=db.update("ComputerInfo",values, "id=?", new String[]{currentConnInfo.getId()});
			        	  if(flag>0){
			        		  initComputerList();
			        		  Toast.makeText(ConnComputerListActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
			        	  }else{
			        		  Toast.makeText(ConnComputerListActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
			        	  }
		        	  }catch(Exception ex){
		        		  ex.printStackTrace();
		        		  
		        	  }finally{
		        		  if(db!=null){
		        			  db.close();
		        		  }
		        	  }
		        	  currentConnInfo=null;
	        	  }
	        	  dialog.dismiss();
	           }
	       }).setNegativeButton(R.string.cancel,
	       new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int which) {
	                dialog.cancel();
	           }
	       }).create();
		return dialog;
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	private int verticalMinDistance = 60;  
	private int minVelocity         = 0;  
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		boolean isleft=false;
		if (Math.abs(e1.getX() - e2.getX() )> Math.abs(e1.getY()-e2.getY()) && Math.abs(velocityX) > minVelocity) {  
//	        Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show(); 
	        isleft=true;
	        if(FileBrowseTab.filebrowsetab!=null){
	        	FileBrowseTab.filebrowsetab.tabHost.setCurrentTabByTag(FileBrowseTab.FILELOCATION);
	        	FileBrowseTab.filebrowsetab.but_type.setText("本地");
	        }
	        
	    } else if (Math.abs(e2.getX() - e1.getX() )>Math.abs( e2.getY()-e1.getY()) && Math.abs(velocityX) > minVelocity) {  
	    	
//	    	Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();  
	    	if(FileBrowseTab.filebrowsetab!=null){
	        	FileBrowseTab.filebrowsetab.tabHost.setCurrentTabByTag(FileBrowseTab.FILELOCATION);
	        	FileBrowseTab.filebrowsetab.but_type.setText("本地");
	        }
	    	isleft=false;
	    }
//		if(FileBrowseTab.filebrowsetab!=null){
////			if(isleft){
////				FileBrowseTab.filebrowsetab.tabHost.setAnimation(AnimationUtils.makeInAnimation(this, false));
//////				FileBrowseTab.filebrowsetab.tabHost.setAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
////			}else{
////				FileBrowseTab.filebrowsetab.tabHost.setAnimation(AnimationUtils.makeInAnimation(this, true));
//////				FileBrowseTab.filebrowsetab.tabHost.setAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
//////				FileBrowseTab.filebrowsetab.tabHost.setAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
////			}
//        	FileBrowseTab.filebrowsetab.tabHost.setCurrentTabByTag(FileBrowseTab.FILELOCATION);
//        }
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
    
	
	public boolean dispatchTouchEvent(MotionEvent ev) {  
	    mGestureDetector.onTouchEvent(ev);  
	    // scroll.onTouchEvent(ev);  
	    return super.dispatchTouchEvent(ev);  
	}  

}
