package com.netgear.genie.media.dlna;

import android.os.Handler;
import android.util.Log;

public class BrowseOp {

	public interface Callback
	{
		public abstract void onFinished(BrowseOp op);
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
			synchronized (this) {
				if (mObjList != null) {
					mObjList.dispose();
					mObjList = null;
				}
			}
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
	
	public DLNAObjectList getObjectList()
	{
		if (mObjList != null) {
			return new DLNAObjectList(mObjList);
		}
		return new DLNAObjectList();
	}
	
	BrowseOp(Handler disp, Callback callback)
	{
		mDisp = disp;
		mCallback = callback;
	}
	
	void hook_onCoreOpFinished(boolean succeeded, DLNAObject[] arr)
	{
		if (succeeded) {
			synchronized (this) {
				mObjList = new DLNAObjectList(arr);
			}
		} else {
			Log.i("JNICore", "BrowseOp failed!!");
		}
		
		mSucceeded = succeeded;
		
		mDisp.post(new Runnable() {
			public void run()
			{
				BrowseOp.this.onCoreOpFinished();
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
	DLNAObjectList mObjList = null;
}
