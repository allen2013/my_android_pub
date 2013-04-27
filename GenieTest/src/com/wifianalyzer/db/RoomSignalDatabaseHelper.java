package com.wifianalyzer.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wifianalyzer.bo.WifiRoomSignalInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RoomSignalDatabaseHelper extends SQLiteOpenHelper {

	public final static String TABLE_NAME="WifiRoomSignal";
	private final static int DATABASE_VERSION=3;
	
	public RoomSignalDatabaseHelper(Context context) {
		super(context, TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sql="create table if not exists "+TABLE_NAME+"(Id varchar(100),BSSID varchar(100),RoomName varchar(100),SignalLevel interger,CreateDateTime datetime,LastModifyDateTime datetime);";
		db.execSQL(sql);
		System.out.println("create a database");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		
		String sql="DROP TABLE IF EXISTS "+TABLE_NAME;
		db.execSQL(sql);
		System.out.println("");

	}
	
	
	/**
	 * 添加房间
	 * @param bssid 
	 */
	public boolean addRoom(WifiRoomSignalInfo info){
		
		boolean t=false;
		if(info!=null){
			SQLiteDatabase db=null;
			try{
				ContentValues values=new ContentValues();
				values.put("Id", info.getId().trim());
				values.put("BSSID", info.getBSSID().trim());
				values.put("RoomName", info.getRoomName().trim());
				values.put("SignalLevel", info.getSignalLevel());
				if(info.getCreateDateTime()!=null){
					values.put("CreateDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getCreateDateTime()));
				}else{
					values.put("CreateDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				}
				if(info.getLastModifyDateTime()!=null){
					values.put("LastModifyDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getLastModifyDateTime()));
				}else{
					values.put("LastModifyDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				}
				db=this.getWritableDatabase();
				if(db!=null){
					long flag=db.insert(TABLE_NAME, "id=?", values);
		        	  if(flag!=-1){
		        		  t=true;
		        	  }
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(db!=null){
					db.close();
				}
			}
		}
		return t;
		
	}
	
	public boolean isExistRoom(String id){
		
		boolean flag=false;
		if(id!=null && !"".equals(id.trim())){
			
			SQLiteDatabase db=null;
			Cursor cursor=null;
			try{
				db=this.getReadableDatabase();
				if(db!=null){
					cursor=db.query(TABLE_NAME, new String[]{"Id","BSSID","RoomName"}, "Id=?", new String[]{id.trim()}, null, null, "CreateDateTime ASC");
					if(cursor!=null){
						if(cursor.getCount()>0){
							flag=true;
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null){
					cursor.close();
				}
				if(db!=null){
					db.close();
				}
			}
			
		}
		return flag;
		
	}
	
	/**
	 * 通过ID删除房间
	 * @param id
	 * @return
	 */
	public boolean deleteRoomById(String id){
		
		boolean t=false;
		if(id!=null && !"".equals(id.trim())){
			
			SQLiteDatabase db=null;
			try{
				db=this.getWritableDatabase();
				if(db!=null){
					long flag=db.delete(TABLE_NAME, "Id=?", new String[]{id.trim()});
					if(flag>0){
						t=true;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(db!=null){
					db.close();
				}
			}
			
		}
		return t;
		
	}
	
	/**
	 * 删除相应无线网络的测试房间
	 * @param helper
	 * @param bssid
	 * @param roomName
	 * @return
	 */
	public boolean deleteRoomBy_NameAndBSSID(String bssid,String roomName){
		
		boolean t=false;
		if(bssid!=null && !"".equals(bssid.trim()) && roomName!=null && !"".equals(roomName.trim())){
			
			SQLiteDatabase db=null;
			try{
				db=this.getWritableDatabase();
				if(db!=null){
					long flag=db.delete(TABLE_NAME, "BSSID=? and RoomName=?", new String[]{bssid.trim(),roomName.trim()});
					if(flag>0){
						t=true;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(db!=null){
					db.close();
				}
			}
			
		}
		return t;
	}
	
	/**
	 * 更新房间信息
	 * @param helper
	 * @param info
	 * @return
	 */
	public boolean updateRoom(WifiRoomSignalInfo info){
		
		boolean t=false;
		if(info!=null){
			SQLiteDatabase db=null;
			try{
				ContentValues values=new ContentValues();
				values.put("BSSID", info.getBSSID().trim());
				values.put("RoomName", info.getRoomName().trim());
				values.put("SignalLevel", info.getSignalLevel());
				if(info.getLastModifyDateTime()!=null){
					values.put("LastModifyDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getLastModifyDateTime()));
				}
				db=this.getWritableDatabase();
				if(db!=null){
					long flag=db.update(TABLE_NAME, values, "id=?", new String[]{info.getId().trim()});
	        	  	if(flag>0){
	        		  	t=true;
	        	  	}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(db!=null){
					db.close();
				}
			}
		}
		return t;
		
	}
	
	/**
	 * 获得房间列表通过BSSID
	 * @param bssid
	 * @return
	 */
	public List<WifiRoomSignalInfo> getRoomsByBSSID(String bssid){
		
		List<WifiRoomSignalInfo> list=new ArrayList<WifiRoomSignalInfo>();
		if(bssid!=null && !"".equals(bssid.trim())){
			SQLiteDatabase db=null;
			Cursor cursor=null;
			try{
				db=this.getReadableDatabase();
				if(db!=null){
					cursor=db.query(TABLE_NAME, new String[]{"Id","BSSID","RoomName","SignalLevel","CreateDateTime","LastModifyDateTime"}, "BSSID=?", new String[]{bssid.trim()}, null, null, "CreateDateTime ASC");
					if(cursor!=null){
						while(cursor.moveToNext()){
							String id=cursor.getString(cursor.getColumnIndex("Id"));
							String bssid1=cursor.getString(cursor.getColumnIndex("BSSID"));
							String roomname=cursor.getString(cursor.getColumnIndex("RoomName"));
							int signallevel=cursor.getInt(cursor.getColumnIndex("SignalLevel"));
							Date createdt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("CreateDateTime")));
							Date modifydt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("LastModifyDateTime")));
							WifiRoomSignalInfo info=new WifiRoomSignalInfo();
							info.setId(id);
							info.setBSSID(bssid1);
							info.setRoomName(roomname);
							info.setSignalLevel(signallevel);
							info.setCreateDateTime(createdt);
							info.setLastModifyDateTime(modifydt);
							list.add(info);
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null){
					cursor.close();
				}
				if(db!=null){
					db.close();
				}
			}
		}
		return list;
		
	}

	
	

}
