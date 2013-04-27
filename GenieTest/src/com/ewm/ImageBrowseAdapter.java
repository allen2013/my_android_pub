package com.ewm;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.dragonflow.GenieDlnaDeviceInfo;
import com.dragonflow.genie.ui.R; 

public class ImageBrowseAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private Bitmap file_image;
	private int selectItem = 0;
//	private int screenWidth=0;
	public ArrayList<GenieDlnaDeviceInfo> m_listdata = new ArrayList<GenieDlnaDeviceInfo>();

	public ImageBrowseAdapter(Context context) {
		this.mContext = context;

	}

	public ImageBrowseAdapter(Context context,
			ArrayList<GenieDlnaDeviceInfo> m_listdata) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		this.mContext = context;
		this.m_listdata = m_listdata;
		mInflater = LayoutInflater.from(context);
//		screenWidth=mContext.getResources().getDisplayMetrics().widthPixels;
		file_image = BitmapFactory.decodeResource(context.getResources(),R.drawable.file_image);
	}

	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 * 
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		if(m_listdata!=null){
			return m_listdata.size();
		}else{
			return 0;
		}
		// return m_listdata.length;
	}

	/**
	 * Since the data comes from an array, just returning the index is sufficent
	 * to get at the data. If we were using a more complex data structure, we
	 * would return whatever object represents one row in the list.
	 * 
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Object getItem(int position) {
		return position;
		// return m_listdata.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 * 
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
	}
	public void setSelectItem(int selectItem) {

		if (this.selectItem != selectItem) {
			this.selectItem = selectItem;
			notifyDataSetChanged();
		}
	}
	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		MyImageView imageview = null;
		try{
			if(m_listdata==null || (m_listdata!=null && m_listdata.size()<position+1)){
				if (convertView==null) {
					imageview = new MyImageView(mContext, 120, 120);
				}else{
					imageview=(MyImageView) convertView;
				}
				imageview.setImageBitmap(file_image);
				imageview.setScaleType(ScaleType.CENTER_INSIDE);
				imageview.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				return imageview;
			}
			
			Bitmap bit = null;
			if (m_listdata.get(position).bigloading) {
				bit = m_listdata.get(position).downimage;
				if(bit==null){
					bit=file_image;
					if(convertView==null){
						imageview = new MyImageView(mContext, 120, 120);
					}else{
						imageview=(MyImageView) convertView;
						imageview.setOriginalSize(120, 120);
					}
					imageview.setImageBitmap(bit);
					imageview.setScaleType(ScaleType.CENTER_INSIDE);
				}else{
					if(convertView==null){
						imageview = new MyImageView(mContext, m_listdata.get(position).originalWidth,  m_listdata.get(position).originalHeight);
					}else{
						imageview=(MyImageView) convertView;
						imageview.setOriginalSize(m_listdata.get(position).originalWidth,  m_listdata.get(position).originalHeight);
					}
					imageview.setImageBitmap(bit);
					imageview.setScaleType(ScaleType.MATRIX);
				}
				
			} else {
				if (m_listdata.get(position).bitimage != null) {
					bit = m_listdata.get(position).bitimage;
					if (bit != null) {
						if(convertView==null){
							imageview = new MyImageView(mContext, bit.getWidth(),bit.getHeight());
						}else{
							imageview=(MyImageView) convertView;
						}
						imageview.setImageBitmap(bit);
					} else {
						bit = file_image;
						imageview = new MyImageView(mContext, 120, 120);
						imageview.setImageBitmap(bit);
						imageview.setScaleType(ScaleType.CENTER_INSIDE);
					}
	
				}else{
					bit = m_listdata.get(position).downimage;
					if(bit==null){
						bit = file_image;
						if(convertView==null){
							imageview = new MyImageView(mContext, 120, 120);
						}else{
							imageview=(MyImageView) convertView;
						}
						imageview.setImageBitmap(bit);
						imageview.setScaleType(ScaleType.CENTER_INSIDE);
					}else{
						if(convertView==null){
							imageview = new MyImageView(mContext,  m_listdata.get(position).originalWidth,  m_listdata.get(position).originalHeight);
						}else{
							imageview=(MyImageView) convertView;
							imageview.setOriginalSize(m_listdata.get(position).originalWidth,  m_listdata.get(position).originalHeight);
						}
						imageview.setImageBitmap(bit);
						imageview.setScaleType(ScaleType.MATRIX);
					}
				}
			}
		
	//		 imageview.setScaleType(ImageView.ScaleType.MATRIX);
			imageview.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			
			return imageview;
		}catch(Exception e){
			System.out.println("lh------------ImageBrowseAdapter ȡͼƬ");
			imageview.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			imageview.setScaleType(ScaleType.CENTER_INSIDE);
		}
		return imageview;
	}

	class ViewHolder {
		MyImageView iconView;
		RelativeLayout progressLayout;
	}
}
