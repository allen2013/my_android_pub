package com.turbo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.dragonflow.genie.ui.R; 

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class Turbo_AudioAdapter extends BaseAdapter {

	private List<MediaInfo> mList=null;
	private Activity activity;
	private MediaInfo info;
	private List<String> selectedFilepath=null;
	
	public Turbo_AudioAdapter(Activity activity,List<MediaInfo> list){
		this.activity=activity;
		this.mList=list;
		selectedFilepath=new ArrayList<String>();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mList!=null){
			return this.mList.size();
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
		if(mList!=null && this.activity!=null && position<mList.size()){
			
			if(convertView==null){
				convertView=this.activity.getLayoutInflater().inflate(R.layout.turbo_audioadapter, null);
			}
			TextView audioname=(TextView) convertView.findViewById(R.id.tb_list_devicename);
			TextView audiosize=(TextView) convertView.findViewById(R.id.tb_list_ip);
			final CheckBox selectedbox=(CheckBox) convertView.findViewById(R.id.audio_check);
			info=mList.get(position);
			if(info!=null){
				audioname.setText(info.getFilename());
				audiosize.setText(getfile_size(info.getFilesize()));
				selectedbox.setChecked(info.selected);
				selectedbox.setTag(info);
				convertView.setTag(info);
			}
			selectedbox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox checkBox=(CheckBox) v;
					MediaInfo minfo=(MediaInfo) checkBox.getTag();
					if(minfo!=null){
						minfo.setSelected(checkBox.isChecked());
						if(checkBox.isChecked()){
							if(!selectedFilepath.contains(minfo.getFilepath())){
								selectedFilepath.add(minfo.getFilepath());
							}
						}else{
							selectedFilepath.remove(minfo.getFilepath());
						}
						if(((Turbo_AudioActivity)activity).okButton!=null){
							if(selectedFilepath.size()>0){
								((Turbo_AudioActivity)activity).okButton.setText(activity.getResources().getString(R.string.ok)+"("+selectedFilepath.size()+")");
							}else{
								((Turbo_AudioActivity)activity).okButton.setText(R.string.ok);
							}
						}
					}
				}
			});
			
		}
		return convertView;
	}

	
	public String getfile_size(String audiosize){
		Double dousize=Double.valueOf(audiosize.trim());
		DecimalFormat df = new DecimalFormat("#.##");
		Double totol=dousize/1024.00d/1024.00d;
		return df.format(totol)+"M";
	}
	
	public List<String> getSelectedFilepath(){
		
		return this.selectedFilepath;
	}
	
}
