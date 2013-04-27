package com.turbo;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragonflow.genie.ui.R; 

public class Turbo_AllFileDataAdapter extends BaseAdapter {

	private static Turbo_AllFileActivity allfileActivity; //
	private List<String> fileMutiList;// 
	protected final static int COLOR_SELECTED = 0xff009500;

	private List<File> datalist = null;
	private LayoutInflater inflater = null;
	private int clickTemp = -1;

	public Turbo_AllFileDataAdapter(Turbo_AllFileActivity activity, List<File> list) {

		allfileActivity = activity;
		this.datalist = list;
		this.inflater = activity.getLayoutInflater();
	}

	public void setData(List<String> list) {
		this.fileMutiList = list;
	}

	public int getCount() {
		if (datalist != null) {
			return datalist.size();
		} else {
			return 0;
		}
	}

	public Object getItem(int position) {
		return datalist.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void setSeclection(int position) {
		clickTemp = position;
	}

	public int getSelection() {
		return clickTemp;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {

		try {
			if (view == null) {
				view = this.inflater.inflate(R.layout.fileoflist, viewGroup,
						false);
			}

			File file = datalist.get(position);
			ImageView imageview = (ImageView) view.findViewById(R.id.file_ico);
			TextView textView = (TextView) view.findViewById(R.id.file_name);
			String filename=file.getName();
			if (file.isDirectory()) {
				imageview.setBackgroundResource(R.drawable.folder_upload);
			} else {
				if(filename.lastIndexOf(".")!=-1){
					String suffix=filename.substring(filename.lastIndexOf(".")+1,filename.length());
					if(suffix!=null){
						suffix=suffix.toLowerCase().trim();
						if("apk".equals(suffix)){
							Drawable apk_drawable=getAPKDrawable(file.getPath().toString(),allfileActivity);
							if(apk_drawable!=null){
								imageview.setBackgroundDrawable(apk_drawable);
							}else{
								imageview.setBackgroundResource(R.drawable.filebrowse_apk);
							}
						}else if("mp3".equals(suffix) || "wav".equals(suffix) || "wma".equals(suffix)){
							imageview.setBackgroundResource(R.drawable.filebrowse_music);
						}else if("rmvb".equals(suffix) || "rmb".equals(suffix) || "avi".equals(suffix) || "wmv".equals(suffix) || "mp4".equals(suffix) || "3pg".equals(suffix) || "flv".equals(suffix)){
							imageview.setBackgroundResource(R.drawable.filebrowse_video);
						}else if("jpg".equals(suffix) || "jpeg".equals(suffix) || "bmp".equals(suffix) || "gif".equals(suffix) || "png".equals(suffix)){
							imageview.setBackgroundResource(R.drawable.filebrowse_photo);
						}else if("zip".equals(suffix) || "tar".equals(suffix) || "bar".equals(suffix) || "rar".equals(suffix) || "bz2".equals(suffix) || "bz".equals(suffix) || "gz".equals(suffix)){
							imageview.setBackgroundResource(R.drawable.filebrowse_zip);
						}else{
							imageview.setBackgroundResource(R.drawable.file_upload);
						}
					}else{
						imageview.setBackgroundResource(R.drawable.file_upload);
					}
				}else{
					imageview.setBackgroundResource(R.drawable.file_upload);
				}
			}

			String name = file.getName();
			if (name.length() > 20) {
				name = name.substring(0, 20) + "...";
			}

			if (allfileActivity.multFile && fileMutiList.contains(file.getAbsolutePath())) {
				textView.setTextColor(COLOR_SELECTED);
				view.setBackgroundResource(R.drawable.grid_selector_background_pressed);
				textView.setText(name);
			} else {
				textView.setTextColor(Color.WHITE);
			}
			textView.setText(name);

			view.setTag(file);

			if (clickTemp == position) {
				view.setBackgroundResource(R.drawable.grid_selector_background_pressed);
			} else {
				view.setBackgroundDrawable(null);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return view;
	}


	public Drawable getAPKDrawable(String filePath,Context context){
		Drawable dr = null;
		if (filePath != null){
			/**
			 * ���÷����ȡ����API����ȡAPK��ͼ�ꡣ
			 * ֮ǰ������Ĵ������ʵ�֣����ڻ���ROM��ֻ�ܻ�ȡ�Ѱ�װ�ģ��У˵�ͼ�ꡣ
			 * ���������ϵ�����������룬ֱ���õġ�
			 * */
			String PATH_PackageParser = "android.content.pm.PackageParser";  
	        String PATH_AssetManager = "android.content.res.AssetManager";  
	        try {  
	            // apk�����ļ�·��  
	            // ����һ��Package ������, �����ص�  
	            // ���캯���Ĳ���ֻ��һ��, apk�ļ���·��  
	            // PackageParser packageParser = new PackageParser(apkPath);  
	            Class pkgParserCls = Class.forName(PATH_PackageParser);  
	            Class[] typeArgs = new Class[1];  
	            typeArgs[0] = String.class;  
	            Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);  
	            Object[] valueArgs = new Object[1];  
	            valueArgs[0] = filePath;  
	            Object pkgParser = pkgParserCt.newInstance(valueArgs);  
	            // ���������ʾ�йص�, �����漰��һЩ������ʾ�ȵ�, ����ʹ��Ĭ�ϵ����  
	            DisplayMetrics metrics = new DisplayMetrics();  
	            metrics.setToDefaults();  
	            // PackageParser.Package mPkgInfo = packageParser.parsePackage(new  
	            // File(apkPath), apkPath,  
	            // metrics, 0);  
	            typeArgs = new Class[4];  
	            typeArgs[0] = File.class;  
	            typeArgs[1] = String.class;  
	            typeArgs[2] = DisplayMetrics.class;  
	            typeArgs[3] = Integer.TYPE;  
	            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage",  
	                    typeArgs);  
	            valueArgs = new Object[4];  
	            valueArgs[0] = new File(filePath);  
	            valueArgs[1] = filePath;  
	            valueArgs[2] = metrics;  
	            valueArgs[3] = 0;  
	            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);  
	            // Ӧ�ó�����Ϣ��, ���������, ������Щ����, ����û����  
	            // ApplicationInfo info = mPkgInfo.applicationInfo;  
	            Field appInfoFld = pkgParserPkg.getClass().getDeclaredField("applicationInfo");  
	            ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);  
	            // uid ���Ϊ"-1"��ԭ����δ��װ��ϵͳδ������Uid��  
	            // Resources pRes = getResources();  
	            // AssetManager assmgr = new AssetManager();  
	            // assmgr.addAssetPath(apkPath);  
	            // Resources res = new Resources(assmgr, pRes.getDisplayMetrics(),  
	            // pRes.getConfiguration());  
	            Class assetMagCls = Class.forName(PATH_AssetManager);  
	            Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);  
	            Object assetMag = assetMagCt.newInstance((Object[]) null);  
	            typeArgs = new Class[1];  
	            typeArgs[0] = String.class;  
	            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",  
	                    typeArgs);  
	            valueArgs = new Object[1];  
	            valueArgs[0] = filePath;  
	            assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);  
	            Resources res = context.getResources();  
	            typeArgs = new Class[3];  
	            typeArgs[0] = assetMag.getClass();  
	            typeArgs[1] = res.getDisplayMetrics().getClass();  
	            typeArgs[2] = res.getConfiguration().getClass();  
	            Constructor resCt = Resources.class.getConstructor(typeArgs);  
	            valueArgs = new Object[3];  
	            valueArgs[0] = assetMag;  
	            valueArgs[1] = res.getDisplayMetrics();  
	            valueArgs[2] = res.getConfiguration();  
	            res = (Resources) resCt.newInstance(valueArgs);  
	            CharSequence label = null;  
	            if (info.labelRes != 0) {  
	                label = res.getText(info.labelRes);  
	            }  
	            // if (label == null) {  
	            // label = (info.nonLocalizedLabel != null) ? info.nonLocalizedLabel  
	            // : info.packageName;  
	            // }  
	            // ������Ƕ�ȡһ��apk�����ͼ��  
	            if (info.icon != 0) {  
	            	dr = res.getDrawable(info.icon);
	            }
	            
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
		}
		return dr;
	}
	
}
