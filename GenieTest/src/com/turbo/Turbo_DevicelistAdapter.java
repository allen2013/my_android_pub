package com.turbo;

import java.util.List;

import com.dragonflow.genie.ui.R; 

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Turbo_DevicelistAdapter extends BaseAdapter{

	private List<TurboDeviceInfo> tbList=null;
	private Activity activity;
	
	public Turbo_DevicelistAdapter(Activity activity,List<TurboDeviceInfo> list) {
		// TODO Auto-generated constructor stub
		tbList=list;
		this.activity=activity;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(tbList!=null){
			return this.tbList.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//
		if(tbList!=null && this.activity!=null && position<tbList.size()){
			
			if(convertView==null){
				convertView=this.activity.getLayoutInflater().inflate(R.layout.turbo_deviceoflist, null);
			}
			TextView devicenameView=(TextView) convertView.findViewById(R.id.tb_list_devicename);
			TextView deviceipView=(TextView) convertView.findViewById(R.id.tb_list_ip);
			TurboDeviceInfo info=tbList.get(position);
			if(info!=null){
				devicenameView.setText(info.getDeviceName());
				deviceipView.setText(info.getIp());
				convertView.setTag(info);
			}
			
		}
		return convertView;
	}

}
