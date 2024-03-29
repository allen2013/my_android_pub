package com.dragonflow;

import android.content.Context;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
/**
 *
 * @author
 *
 */
public class TouchView extends ImageView
{
    static final int NONE = 0;
    static final int DRAG = 1;	   //
    static final int ZOOM = 2;     //
    static final int BIGGER = 3;   //
    static final int SMALLER = 4;  //
    private int mode = NONE;	   //

    private float beforeLenght;   //
    private float afterLenght;    //
    private float scale = 0.04f;  //
   
    private int screenW;
    private int screenH;
    
    /* */
    private int start_x;
    private int start_y;
	private int stop_x ;
	private int stop_y ;
	
    private TranslateAnimation trans; //
    
    private OnTouchCallBack m_callback = null;
    
    public interface OnTouchCallBack{
    	public void OnTouchAction(MotionEvent event);
    };
    
    public void SetOnTouchCallBack(OnTouchCallBack callback)
    {
    	m_callback = callback;
    }
	
    public TouchView(Context context,int w,int h)
	{
		super(context);
		this.setPadding(0, 0, 0, 0);
		
		screenW = w;
		screenH = h;
	}
	
	/**
	 *
	 */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    
    /**
     *
     */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{	
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
        		mode = DRAG;
    	    	stop_x = (int) event.getRawX();
    	    	stop_y = (int) event.getRawY();
        		start_x = (int) event.getX();
            	start_y = stop_y - this.getTop();
            	
            	GenieDebug.error("dsm", "start_x>>>>>"+start_x+">>>>>>>start_y>>>>>>"+start_y+">>>>>>>>stop_x>>>>>>>>"+stop_x+">>>>>>>>>stop_y>>>>>>>>>>"+stop_y);
            	
            	if(event.getPointerCount()==2)
            		beforeLenght = spacing(event);
                break;
        case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > 10f) {
                        mode = ZOOM;
                		beforeLenght = spacing(event);
                }
                break;
        case MotionEvent.ACTION_UP:
        	/*�ж��Ƿ񳬳���Χ     ������*/
//        		int disX = 0;
//        		int disY = 0;
//	        	if(getHeight()<=screenH || this.getTop()<0)
//	        	{
//		        	if(this.getTop()<0 )
//		        	{
//		        		int dis = getTop();
//	                	this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
//	            		disY = dis - getTop();
//		        	}
//		        	else if(this.getBottom()>screenH)
//		        	{
//		        		disY = getHeight()- screenH+getTop();
//	                	this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
//		        	}
//	        	}
//	        	if(getWidth()<=screenW)
//	        	{
//		        	if(this.getLeft()<0)
//		        	{
//		        		disX = getLeft();
//	                	this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
//		        	}
//		        	else if(this.getRight()>screenW)
//		        	{
//		        		disX = getWidth()-screenW+getLeft();
//	                	this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
//		        	}
//	        	}
//	        	if(disX!=0 || disY!=0)
//	        	{
//            		trans = new TranslateAnimation(disX, 0, disY, 0);
//            		trans.setDuration(500);
//            		this.startAnimation(trans);
//	        	}
	        	mode = NONE;
        		break;
        case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        case MotionEvent.ACTION_MOVE:
        		/**/
                if (mode == DRAG) {
                	if(Math.abs(stop_x-start_x-getLeft())<88 && Math.abs(stop_y - start_y-getTop())<85)
                	{
                    	this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y - start_y + this.getHeight());           	
                    	stop_x = (int) event.getRawX();
                    	stop_y = (int) event.getRawY();
                	}
                } 
                /**/
                else if (mode == ZOOM) {
                	
                	GenieDebug.error("debug", " setScale mode == ZOOM");
                	
                	if(spacing(event)>10f)
                	{
                		GenieDebug.error("debug", " setScale spacing(event)>10f");
                		
                        afterLenght = spacing(event);
                        float gapLenght = afterLenght - beforeLenght;  
                        
                        GenieDebug.error("debug", " setScale gapLenght = "+gapLenght);
                        
                        if(gapLenght == 0) {  
                           break;
                        }
                        else if(Math.abs(gapLenght)>5f)
                        {
                            if(gapLenght>0) { 
                                this.setScale(scale,BIGGER);   
                            }else {  
                                this.setScale(scale,SMALLER);   
                            }                             
                            beforeLenght = afterLenght; 
                        }
                	}
                }
                break;
        }
        
        if(null != m_callback)
        	m_callback.OnTouchAction(event);
        return true;	
	}
	
	public void onBigger()
	{
		this.setScale(scale,BIGGER);
	}
	public void onSmaller()
	{
		this.setScale(scale,SMALLER);
	}
	
	
	
	/**
	 *
	 */
    private void setScale(float temp,int flag) {   
        
    	GenieDebug.error("debug", " setScale temp = "+temp);
    	GenieDebug.error("debug", " setScale flag = "+flag);
    	
    	


    	
        if(flag==BIGGER) {   
        	
        	int left = this.getLeft()-(int)(temp*this.getWidth());
        	int top = this.getTop()-(int)(temp*this.getHeight());
        	int right = this.getRight()+(int)(temp*this.getWidth());
        	int bottom = this.getBottom()+(int)(temp*this.getHeight());
        	GenieDebug.error("debug", " setScale left = "+left);
        	GenieDebug.error("debug", " setScale top = "+top);
        	GenieDebug.error("debug", " setScale right = "+right);
        	GenieDebug.error("debug", " setScale bottom = "+bottom);
        	GenieDebug.error("debug", " setScale this.getTop() = "+this.getTop());
        	
        	
        	
        	this.setFrame(left,top,right,bottom);
        	
        	//if(Math.abs(this.getTop()) > 2000f)
        	//	return ;       	
        	
            //this.setFrame(this.getLeft()-(int)(temp*this.getWidth()),    
            //              this.getTop()-(int)(temp*this.getHeight()),    
             //             this.getRight()+(int)(temp*this.getWidth()),    
              //            this.getBottom()+(int)(temp*this.getHeight()));      
        }else if(flag==SMALLER){   
        	
        	if((int)(temp*this.getWidth()) > 1 && 
        			(int)(temp*this.getHeight()) > 1
        			&& (int)(temp*this.getWidth()) > 1
        			&& (int)(temp*this.getHeight()) > 1)
        	{
        		int left = this.getLeft()+(int)(temp*this.getWidth());
        	   	int top = this.getTop()+(int)(temp*this.getHeight());
	        	int right = this.getRight()-(int)(temp*this.getWidth());
	        	int bottom = this.getBottom()-(int)(temp*this.getHeight());
	        	GenieDebug.error("debug", " setScale left = "+left);
	        	GenieDebug.error("debug", " setScale top = "+top);
	        	GenieDebug.error("debug", " setScale right = "+right);
	        	GenieDebug.error("debug", " setScale bottom = "+bottom);
	        	
	
	        	
	        	this.setFrame(left,top,right,bottom);
        	}	
        	//if(temp*(right-left) < 2 || temp*(bottom-top) < 2)
        	//{
        	//	return ;
        	//}
        	
            //this.setFrame(this.getLeft()+(int)(temp*this.getWidth()),    
            //              this.getTop()+(int)(temp*this.getHeight()),    
            //              this.getRight()-(int)(temp*this.getWidth()),    
            //              this.getBottom()-(int)(temp*this.getHeight()));   
        }   
    }
    
	/**
	 *
	 */
    private void setPosition(int left,int top,int right,int bottom) {  
    	this.layout(left,top,right,bottom);           	
    }

}
