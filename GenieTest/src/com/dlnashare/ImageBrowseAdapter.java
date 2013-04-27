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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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

public class ImageBrowseAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private Bitmap file_image;
	private int selectItem = 0;
	public ArrayList<GenieDlnaDeviceInfo> m_listdata = null;

	public ImageBrowseAdapter(Context context) {
		this.mContext = context;

	}

	public ImageBrowseAdapter(Context context,
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
		Bitmap bit = null;
		if (m_listdata.get(position).bigloading) {
			bit = file_image;
			imageview = new MyImageView(mContext, 120, 120);
			imageview.setImageBitmap(bit);
		} else {
			if (m_listdata.get(position).bitimage != null) {
				bit = m_listdata.get(position).bitimage;
				if (bit != null) {
					imageview = new MyImageView(mContext, bit.getWidth(),
							bit.getHeight());
					imageview.setImageBitmap(bit);
				} else {
					bit = file_image;
					imageview = new MyImageView(mContext, 120, 120);
					imageview.setImageBitmap(bit);
				}
			}
		}
		if (bit!=null&&bit.isRecycled()) {
			bit.recycle();
			System.gc();
		}
		// imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageview.setLayoutParams(new Gallery.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return imageview;
	
	}

	class ViewHolder {
		ImageView icon;
	}
}
