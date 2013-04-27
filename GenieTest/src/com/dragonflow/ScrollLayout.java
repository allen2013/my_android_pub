package com.dragonflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


public class ScrollLayout extends ViewGroup {

	private static final String TAG = "ScrollLayout";
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	
	private int mCurScreen;
	private int mDefaultScreen = 0;
	
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	
	private static final int SNAP_VELOCITY = 600;
	
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	
	
    private Paint    mPaint;
    private Rect    mRect;
    
 
	public ScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(context);
		
		///////////////////////////////////////////
        mRect = new Rect( );
        mPaint = new Paint( );
        
        mPaint.setStyle( Paint.Style.FILL_AND_STROKE );
        mPaint.setColor( 0xFFCBD2D8 );
        ///////////////////////////////////////////////
		//setBackgroundResource(R.drawable.scrollbackground);
		setBackgroundColor(0xFFE5E5FB);
		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		GenieDebug.error(TAG, "onLayout");
		GenieDebug.error(TAG, "onLayout changed = "+changed);
		GenieDebug.error(TAG, "onLayout getChildCount() ="+getChildCount());
		//if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();
			
			for (int i=0; i<childCount; i++) {
				GenieDebug.error(TAG, "onLayout i = "+i);
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					GenieDebug.error(TAG, "onLayout 1 childWidth = "+childWidth);
					childView.layout(childLeft, 0, 
							childLeft+childWidth, childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		//}
	}


    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
    	GenieDebug.error(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
  
        final int width = MeasureSpec.getSize(widthMeasureSpec);   
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);   
        if (widthMode != MeasureSpec.EXACTLY) {   
            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!"); 
        }   
  
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);   
        if (heightMode != MeasureSpec.EXACTLY) {   
            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
        }   
  
        // The children are given the same width and height as the scrollLayout   
        final int count = getChildCount();   
        for (int i = 0; i < count; i++) {   
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);   
        }   
        // GenieDebug.error(TAG, "moving to screen "+mCurScreen);   
        scrollTo(mCurScreen * width, 0);         
    }  
    
    /**
     * According to the position of current layout
     * scroll to the destination page.
     */
    public void snapToDestination() {
    	final int screenWidth = getWidth();
    	final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;
    	snapToScreen(destScreen);
    }
    
    public void snapToScreen(int whichScreen) {
    	// get the valid layout page
    	whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));
    	if (getScrollX() != (whichScreen*getWidth())) {
    		
    		final int delta = whichScreen*getWidth()-getScrollX();
    		mScroller.startScroll(getScrollX(), 0, 
    				delta, 0, Math.abs(delta)*2);
    		mCurScreen = whichScreen;
    		invalidate();		// Redraw the layout
    	}
    }
    
    public void setToScreen(int whichScreen) {
    	whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));
    	mCurScreen = whichScreen;
    	scrollTo(whichScreen*getWidth(), 0);
    }
    
    public int getCurScreen() {
    	return mCurScreen;
    }
    
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		GenieDebug.error("onTouchEvent","onTouchEvent 222 3 Action = "+event.getAction());
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			GenieDebug.error(TAG, "event down!");
			if (!mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;
			
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int)(mLastMotionX - x);
			mLastMotionX = x;
			
            scrollBy(deltaX, 0);
//            if(null != m_TouchUpCallBack)
//            {
//            	m_TouchUpCallBack.onTouchEvent(event);
//            }
//            
            if(null != getChildAt(mCurScreen))
            	getChildAt(mCurScreen).onTouchEvent(event);
			break;
			
		case MotionEvent.ACTION_UP:
			GenieDebug.error(TAG, "event : up");   
            // if (mTouchState == TOUCH_STATE_SCROLLING) {   
            final VelocityTracker velocityTracker = mVelocityTracker;   
            velocityTracker.computeCurrentVelocity(1000);   
            int velocityX = (int) velocityTracker.getXVelocity();   

            GenieDebug.error(TAG, "velocityX:"+velocityX); 
            
            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {   
                // Fling enough to move left   
            	GenieDebug.error(TAG, "snap left");
                snapToScreen(mCurScreen - 1);   
            } else if (velocityX < -SNAP_VELOCITY   
                    && mCurScreen < getChildCount() - 1) {   
                // Fling enough to move right   
            	GenieDebug.error(TAG, "snap right");
                snapToScreen(mCurScreen + 1);   
            } else {   
                snapToDestination();   
            }   

            if (mVelocityTracker != null) {   
                mVelocityTracker.recycle();   
                mVelocityTracker = null;   
            }   
            // }   
            mTouchState = TOUCH_STATE_REST;   
            
//            if(null != m_TouchUpCallBack)
//            {
//            	m_TouchUpCallBack.onTouchEvent(event);
//            }
            
            //view.setTag("mainscroll");
            
            try{
            	if(getChildAt(mCurScreen).findViewWithTag("mainscroll")!=null){
            		getChildAt(mCurScreen).findViewWithTag("mainscroll").onTouchEvent(event);
            	}
            }catch(Exception e)
            {
            	e.printStackTrace();
            }
            //for(int i = 0;i<(ViewGroup)getChildAt(mCurScreen).;)
            
            //getChildAt(mCurScreen).onTouchEvent(event);
            //return false;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		//postInvalidate();
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		GenieDebug.error(TAG, "onInterceptTouchEvent-slop:"+mTouchSlop);
		
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && 
				(mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		
		final float x = ev.getX();
		final float y = ev.getY();
		
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int)Math.abs(mLastMotionX-x);
			if (xDiff>mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
				
			}
			break;
			
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished()? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;
			
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		
		return mTouchState != TOUCH_STATE_REST;
	}

//	@Override
//	protected void onDraw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		super.onDraw(canvas);
//		
//		//getWindowManager().getDefaultDisplay().getHeight();
//		
//	       canvas.drawColor( 0xFFC5CCD4 );
//	        
//	        this.getDrawingRect( mRect );
//	        
//	        for( int i = 0; i < mRect.right+getWidth(); i += 7 )//缁樺埗灞忓箷鑳屾櫙鐨勭汗鐞嗘晥鏋�//	        {
//	            canvas.drawRect( mRect.left + i, mRect.top, mRect.left + i + 3, mRect.bottom, mPaint );
//	        }
//
//
//	}
	
}
