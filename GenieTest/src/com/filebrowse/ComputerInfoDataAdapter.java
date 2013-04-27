package com.filebrowse;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragonflow.genie.ui.R;

public class ComputerInfoDataAdapter extends BaseAdapter {

	private List<ComputerConnInfo> datalist=new ArrayList<ComputerConnInfo>();
	private LayoutInflater inflater;
    
    public ComputerInfoDataAdapter(Activity activity,List<ComputerConnInfo> list){
    	datalist=list;
    	inflater=activity.getLayoutInflater();
    }
	
	public int getCount() {
		// TODO Auto-generated method stub
		return datalist.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
//		ViewHolder holder;
		if (convertView==null) {
//            holder=new ViewHolder();
            convertView=this.inflater.inflate(R.layout.computerinfo, null);
        }
		ImageView image=(ImageView) convertView.findViewById(R.id.computerico);
		TextView text=(TextView) convertView.findViewById(R.id.computername);
	 	image.setImageResource(R.drawable.conncomputer);
     	if(!"".equals(datalist.get(position).getName())){
     		String name=datalist.get(position).getName();
     		if(datalist.get(position).getName().length()>15){
     			name=name.substring(0,15)+"...";
     		}
     		text.setText(name);
     	}else{
     		text.setText(datalist.get(position).getIp());
     	}
     	convertView.setTag(datalist.get(position));
        return convertView;
	}
	
	 
	

}
