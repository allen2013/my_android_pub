/**  
 * MyGallery.java
 * @version 1.0
 * @author Haven
 * @createTime 2011-12-9 下午03:42:53
 * android.widget.Gallery的子函数。此类很重要。建议仔细看
 */
package com.ewm;

import com.dragonflow.GenieDebug;
import com.dragonflow.GenieDlnaActionDefines;
import com.dragonflow.GenieDlnaService;
import com.dragonflow.GneieDlna;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.MailTo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.AbsListView.OnScrollListener;

public class MyGallery extends Gallery {
	private GestureDetector gestureScanner;
	private MyImageView imageView;

	public MyGallery(Context context) {
		super(context);

	}

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);

		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = MyGallery.this.getSelectedView();
				if (view instanceof MyImageView) {
					imageView = (MyImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
							// System.out.println("value:" + value);
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
								// scale the image
								imageView.zoomTo(originalScale * scale, x
										+ event.getX(1), y + event.getY(1));

							}
						}
					}
				}
				return false;
			}

		});
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		View view = MyGallery.this.getSelectedView();
		if (view instanceof MyImageView) {
			imageView = (MyImageView) view;

			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			// 图片实时的上下左右坐标
			float left, right;
			// 图片的实时宽，高
			float width, height;
			width = imageView.getScale() * imageView.getImageWidth();
			height = imageView.getScale() * imageView.getImageHeight();
			// 一下逻辑为移动图片和滑动gallery换屏的逻辑。如果没对整个框架了解的非常清晰，改动以下的代码前请三思！！！！！！
			if ((int) width <= ImagesBrowseActivity.linear.getWidth()
					&& (int) height <= ImagesBrowseActivity.linear.getHeight())// 如果图片当前大小<屏幕大小，直接处理滑屏事件
			{
				super.onScroll(e1, e2, distanceX , distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);

				if (distanceX > 0)// 向左滑动
				{
					if (r.left > 0) {// 判断当前ImageView是否显示完全
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (right < ImagesBrowseActivity.linear.getWidth()) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0)// 向右滑动
				{
					if (r.right < ImagesBrowseActivity.linear.getWidth()) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				}

			}

		} else {
			super.onScroll(e1, e2, distanceX , distanceY);
		}
		return false;
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int keyCode;
		if (isScrollingLeft(e1, e2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(keyCode, null);
		return true;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界
			View view = MyGallery.this.getSelectedView();
			if (view instanceof MyImageView) {
				imageView = (MyImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale()
						* imageView.getImageHeight();
				if ((int) width <= ImagesBrowseActivity.linear.getWidth()
						&& (int) height <= ImagesBrowseActivity.linear
								.getHeight())// 如果图片当前大小<屏幕大小，判断边界
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				if (top < 0 && bottom <ImagesBrowseActivity.linear.getHeight()) {
					imageView.postTranslateDur(-top, 200f);
				}
				Log.i("manga", "bottom:" + bottom);
				if (top>0 && bottom > ImagesBrowseActivity.linear.getHeight()) {
					imageView.postTranslateDur(
							ImagesBrowseActivity.linear.getHeight() - bottom,
							200f);
				}

			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private class MySimpleGesture extends SimpleOnGestureListener {
		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {
			View view = MyGallery.this.getSelectedView();
			if (view instanceof MyImageView) {
				imageView = (MyImageView) view;
				if (imageView.getScale() > imageView.getScaleRate()) {
					imageView.zoomTo(imageView.getScaleRate(),
							ImagesBrowseActivity.linear.getWidth() / 2,
							ImagesBrowseActivity.linear.getHeight() / 2, 200f);
					// imageView.layoutToCenter();
				} else {
					imageView.zoomTo(1.0f,
							ImagesBrowseActivity.linear.getWidth() / 2,
							ImagesBrowseActivity.linear.getHeight() / 2, 200f);
				}

			} else {

			}
			// return super.onDoubleTap(e);
			return true;
		}
	}
}
