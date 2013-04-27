package com.ewm;
import com.dragonflow.GenieDebug;
import com.dragonflow.genie.ui.R; 

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import com.dragonflow.genie.ui.R; 

/**
 * @ClassName SpinnerButton
 * @Description TODO 防android4.0 Spinner下拉效果
 * @author kenny
 * @date 2012-8-14
 */
public class SpinnerButton extends Button {
	
	private Context mContext;
	/** 下拉PopupWindow */
	private UMSpinnerDropDownItems mPopupWindow;
	/** 下拉布局文件ResourceId */
	private int mResId;
	/** 下拉布局文件创建监听器 */
	private ViewCreatedListener mViewCreatedListener;
	
	private TextView auto_close;
	private TextView auto_5s;
	private TextView auto_10s;
	private TextView auto_15s;
	private TextView auto_20s;
	private TextView auto_25s;
	private TextView auto_30s;

	public SpinnerButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initButton(context);
	}

	public SpinnerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initButton(context);
	}
	public SpinnerButton(Context context, final int resourceId,
			ViewCreatedListener mViewCreatedListener) {
		super(context);
		setResIdAndViewCreatedListener(resourceId, mViewCreatedListener);
		initButton(context);
	}

	private void initButton(Context context) {
		this.mContext = context;
		// UMSpinnerButton监听事件
		setOnClickListener(new UMSpinnerButtonOnClickListener());
	}

	public PopupWindow getPopupWindow() {
		return mPopupWindow;
	}

	public void setPopupWindow(UMSpinnerDropDownItems mPopupWindow) {
		this.mPopupWindow = mPopupWindow;
	}

	public int getResId() {
		return mResId;
	}
	/**
	 * @Description: TODO   隐藏下拉布局
	 */
	public void dismiss(){
		mPopupWindow.dismiss();
	}
	/**
	 * @Description: TODO  设置下拉布局文件,及布局文件创建监听器
	 * @param @param mResId 下拉布局文件ID
	 * @param @param mViewCreatedListener  布局文件创建监听器
	 */
	public void setResIdAndViewCreatedListener(int mResId, ViewCreatedListener mViewCreatedListener) {
		this.mViewCreatedListener = mViewCreatedListener;
		// 下拉布局文件id
		this.mResId = mResId;
		// 初始化PopupWindow
		mPopupWindow = new UMSpinnerDropDownItems(mContext);
	}

	/**
	 * UMSpinnerButton的点击事件
	 */
	class UMSpinnerButtonOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if(ImagesBrowseActivity.autotime==5000){
				setColor();
				auto_5s.setBackgroundColor(Color.rgb(128, 128, 128));
			}else if(ImagesBrowseActivity.autotime==10000){
				setColor();
				auto_10s.setBackgroundColor(Color.rgb(128, 128, 128));
			}else if(ImagesBrowseActivity.autotime==15000){
				setColor();
				auto_15s.setBackgroundColor(Color.rgb(128, 128, 128));
			}
//			else if(ImagesBrowseActivity.autotime==20000){
//				setColor();
//				auto_20s.setBackgroundColor(Color.rgb(40, 199, 202));
//			}else if(ImagesBrowseActivity.autotime==25000){
//				setColor();
//				auto_25s.setBackgroundColor(Color.rgb(40, 199, 202));
//			}else if(ImagesBrowseActivity.autotime==30000){
//				setColor();
//				auto_30s.setBackgroundColor(Color.rgb(40, 199, 202));
//			}
			else{
				setColor();
				auto_close.setBackgroundColor(Color.rgb(128, 128, 128));
			}
			if (mPopupWindow != null) {
				if (!mPopupWindow.isShowing()) {
					// 设置PopupWindow弹出,退出样式
					mPopupWindow.setAnimationStyle(R.style.Animation_dropdown);
					// 计算popupWindow下拉x轴的位置
					int lx = (SpinnerButton.this.getWidth()
							- mPopupWindow.getmViewWidth() - 7) / 2;
					// showPopupWindow
					mPopupWindow.showAsDropDown(SpinnerButton.this, lx, -5);
				}
			}
		}
	}

	/**
	 * @ClassName UMSpinnerDropDownItems
	 * @Description TODO 下拉界面
	 * @author kenny
	 * @date 2012-8-14
	 */
	public class UMSpinnerDropDownItems extends PopupWindow {

		private Context mContext;
		/** 下拉视图的宽度 */
		private int mViewWidth;
		/** 下拉视图的高度 */
		private int mViewHeight;

		public UMSpinnerDropDownItems(Context context) {
			super(context);
			this.mContext = context;
			loadViews();
		}

		/**
		 * @Description: TODO 加载布局文件
		 * @param
		 * @return void
		 * @throws
		 */
		private void loadViews() {
			// 布局加载器加载布局文件
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(mResId, null);
			// 计算view宽高
			onMeasured(view);

			// 必须设置
			setWidth(LayoutParams.WRAP_CONTENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setContentView(view);
			setFocusable(true);
			
			// 设置布局创建监听器，以便在实例化布局控件对象
			if (mViewCreatedListener != null) {
				mViewCreatedListener.onViewCreated(view);
			}
			auto_close=(TextView) view.findViewById(R.id.auto_close);
			auto_5s=(TextView) view.findViewById(R.id.auto_5s);
			auto_10s=(TextView) view.findViewById(R.id.auto_10s);
			auto_15s=(TextView) view.findViewById(R.id.auto_15s);
//			auto_20s=(TextView) view.findViewById(R.id.auto_20s);
//			auto_25s=(TextView) view.findViewById(R.id.auto_25s);
//			auto_30s=(TextView) view.findViewById(R.id.auto_30s);
			
		}

		/**
		 * @Description: TODO 计算View长宽
		 * @param @param v
		 */
		private void onMeasured(View v) {
			int w = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			v.measure(w, h);
			mViewWidth = v.getMeasuredWidth();
			mViewHeight = v.getMeasuredHeight();
		}

		public int getmViewWidth() {
			return mViewWidth;
		}

		public void setmViewWidth(int mViewWidth) {
			this.mViewWidth = mViewWidth;
		}

		public int getmViewHeight() {
			return mViewHeight;
		}

		public void setmViewHeight(int mViewHeight) {
			this.mViewHeight = mViewHeight;
		}

	}
	/**
	 * @ClassName ViewCreatedListener  
	 * @Description TODO  布局创建监听器，实例化布局控件对象
	 * @author kenny  
	 * @date 2012-8-15
	 */
	public interface ViewCreatedListener {
		void onViewCreated(View v);
	}
	
	@SuppressLint("ResourceAsColor")
    public void setColor(){
		auto_close.setBackgroundColor(R.color.limegreen);
		auto_5s.setBackgroundColor(R.color.limegreen);
		auto_10s.setBackgroundColor(R.color.limegreen);
		auto_15s.setBackgroundColor(R.color.limegreen);
//		auto_20s.setBackgroundColor(R.color.limegreen);
//		auto_25s.setBackgroundColor(R.color.limegreen);
//		auto_30s.setBackgroundColor(R.color.limegreen);
	}
	
}
