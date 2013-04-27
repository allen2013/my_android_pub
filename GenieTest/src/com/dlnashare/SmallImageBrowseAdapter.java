package com.dlnashare;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieDlnaActionDefines;
import com.dragonflow.GenieDlnaDeviceInfo;
import com.dragonflow.genie.ui.R;

public class SmallImageBrowseAdapter extends BaseAdapter {

	private Context mContext;
	private Bitmap file_image;
	private LayoutInflater mInflater;
	public ArrayList<GenieDlnaDeviceInfo> m_listdata = null;
	private int selectItem = 0;
	public SmallImageBrowseAdapter(Context context) {
		this.mContext = context;

	}

	public SmallImageBrowseAdapter(Context context,
			ArrayList<GenieDlnaDeviceInfo> m_listdata) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		this.mContext = context;
		this.m_listdata = m_listdata;
		mInflater = LayoutInflater.from(context);
		file_image = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.file_image);
	}

	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 * 
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		return m_listdata.size();
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

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_downshow, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Bitmap bit = null;
		if (m_listdata.get(position).downloading) {
			bit = file_image;
			holder.icon.setImageBitmap(bit);
		} else {

			if (m_listdata.get(position).downimage != null) {
				bit = m_listdata.get(position).downimage;
				if (bit != null) {
					holder.icon.setImageBitmap(bit);
				}

			}
		}
		if (bit == null) {
			bit = file_image;
			holder.icon.setImageBitmap(bit);
		}
		// bit.recycle();
		// bit=null;
		if (bit.isRecycled()) {
			bit.recycle();
			System.gc();
		}
		if (selectItem == position) {
			convertView.setBackgroundColor(Color.WHITE);
//			Animation animation=new RotateAnimation(0.0f, +350.0f,
//		               Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
//			animation.setDuration(3000);
//////			Animation animation = AnimationUtils.loadAnimation(mContext,
//////					R.anim.); // 实现动画效果
//////			convertView.setLayoutParams(new Gallery.LayoutParams(70, 170));
//			convertView.startAnimation(animation); // 选中时，这是设置的比较大
		} 
		return convertView;
	}

	class ViewHolder {
		ImageView icon;
	}

}
