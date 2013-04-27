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
 * @Description TODO ��android4.0 Spinner����Ч��
 * @author kenny
 * @date 2012-8-14
 */
public class SpinnerButton extends Button {
	
	private Context mContext;
	/** ����PopupWindow */
	private UMSpinnerDropDownItems mPopupWindow;
	/** ���������ļ�ResourceId */
	private int mResId;
	/** ���������ļ����������� */
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
		// UMSpinnerButton�����¼�
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
	 * @Description: TODO   ������������
	 */
	public void dismiss(){
		mPopupWindow.dismiss();
	}
	/**
	 * @Description: TODO  �������������ļ�,�������ļ�����������
	 * @param @param mResId ���������ļ�ID
	 * @param @param mViewCreatedListener  �����ļ�����������
	 */
	public void setResIdAndViewCreatedListener(int mResId, ViewCreatedListener mViewCreatedListener) {
		this.mViewCreatedListener = mViewCreatedListener;
		// ���������ļ�id
		this.mResId = mResId;
		// ��ʼ��PopupWindow
		mPopupWindow = new UMSpinnerDropDownItems(mContext);
	}

	/**
	 * UMSpinnerButton�ĵ���¼�
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
					// ����PopupWindow����,�˳���ʽ
					mPopupWindow.setAnimationStyle(R.style.Animation_dropdown);
					// ����popupWindow����x���λ��
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
	 * @Description TODO ��������
	 * @author kenny
	 * @date 2012-8-14
	 */
	public class UMSpinnerDropDownItems extends PopupWindow {

		private Context mContext;
		/** ������ͼ�Ŀ�� */
		private int mViewWidth;
		/** ������ͼ�ĸ߶� */
		private int mViewHeight;

		public UMSpinnerDropDownItems(Context context) {
			super(context);
			this.mContext = context;
			loadViews();
		}

		/**
		 * @Description: TODO ���ز����ļ�
		 * @param
		 * @return void
		 * @throws
		 */
		private void loadViews() {
			// ���ּ��������ز����ļ�
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(mResId, null);
			// ����view���
			onMeasured(view);

			// ��������
			setWidth(LayoutParams.WRAP_CONTENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setContentView(view);
			setFocusable(true);
			
			// ���ò��ִ������������Ա���ʵ�������ֿؼ�����
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
		 * @Description: TODO ����View����
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
	 * @Description TODO  ���ִ�����������ʵ�������ֿؼ�����
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
