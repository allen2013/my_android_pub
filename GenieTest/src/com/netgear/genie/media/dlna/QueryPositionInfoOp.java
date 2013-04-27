package com.netgear.genie.media.dlna;

import android.os.Handler;

public class QueryPositionInfoOp {

	public interface Callback
	{
		public abstract void onFinished(QueryPositionInfoOp op);
	}
	
	public void abort()
	{
		if (!mDisposed) {
			nativeAbort(mAdapter);
		}
	}
	
	public void dispose()
	{
		if (!mDisposed) {
			nativeDestroy(mAdapter);
			mAdapter = 0;
			mDisposed = true;
		}
	}
	
	public boolean isDisposed()
	{
		return mDisposed;
	}
	
	public boolean succeeded()
	{
		return mSucceeded;
	}
	
	public int getTrackTime()
	{
		return mTrackTime;
	}
	
	QueryPositionInfoOp(Handler disp, Callback callback)
	{
		mDisp = disp;
		mCallback = callback;
	}
	
	void hook_onCoreOpFinished(boolean succeeded, int trackTime)
	{
		if (succeeded) {
			mTrackTime = trackTime;
		}
		
		mSucceeded = succeeded;
		
		mDisp.post(new Runnable() {
			public void run()
			{
				QueryPositionInfoOp.this.onCoreOpFinished();
			}
		});
	}
	
	void onCoreOpFinished()
	{
		if (!mDisposed) {
			if (mCallback != null) {
				mCallback.onFinished(this);
			}
		}
	}
	
	private static native void nativeDestroy(long adapter);
	private static native void nativeAbort(long adapter);
	
	long mAdapter = 0;
	Handler mDisp;
	Callback mCallback;
	boolean mDisposed = false;
	boolean mSucceeded = false;
	int mTrackTime = 0;
}
