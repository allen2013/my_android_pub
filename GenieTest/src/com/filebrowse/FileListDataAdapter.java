package com.filebrowse;

import java.util.List;

import jcifs.smb.SmbFile;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragonflow.genie.ui.R;

public class FileListDataAdapter extends BaseAdapter {

	private List<SmbFile> datalist=null;
	private LayoutInflater inflater=null;
	
	public FileListDataAdapter(Activity activity,List<SmbFile> list) {
		// TODO Auto-generated constructor stub
		this.datalist=list;
		this.inflater=activity.getLayoutInflater();
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		if(datalist!=null){
			return datalist.size();
		}else{
			return 0;
		}
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		try{
			if(view==null){
				view=this.inflater.inflate(R.layout.fileoflist, null);
			}
			SmbFile file=datalist.get(position);
			ImageView imageview=(ImageView)view.findViewById(R.id.file_ico);
			TextView textView=(TextView) view.findViewById(R.id.file_name);
			if(file.isDirectory()){
				imageview.setBackgroundResource(R.drawable.folder);
			}else{
				imageview.setBackgroundResource(R.drawable.file);
			}
			String name=file.getName();
			if(name.length()>20){
				name=name.substring(0,20)+"...";
			}
			textView.setText(name);
			view.setTag(file);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return view;
	}

}
