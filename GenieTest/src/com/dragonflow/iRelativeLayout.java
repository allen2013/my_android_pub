package com.dragonflow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public  class iRelativeLayout extends RelativeLayout
{
    private Paint    mPaint;
    private Rect    mRect;
    
    public iRelativeLayout( Context context, AttributeSet attrs ) 
    {
        super(context, attrs);
        
        mRect = new Rect( );
        mPaint = new Paint( );
        
        mPaint.setStyle( Paint.Style.FILL_AND_STROKE );
        mPaint.setColor( 0xFFCBD2D8 );
    }
    
    @Override
    protected void onDraw( Canvas canvas )
    {
        super.onDraw( canvas );

        canvas.drawColor( 0xFFC5CCD4 );
        
        this.getDrawingRect( mRect );
        
        for( int i = 0; i < mRect.right; i += 7 )//绘制屏幕背景的纹理效果
        {
            canvas.drawRect( mRect.left + i, mRect.top, mRect.left + i + 2, mRect.bottom, mPaint );
        }

    }
}


