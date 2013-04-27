package com.netgear.genie.media.dlna;

import android.os.Handler;
import android.util.Log;

public class ProgressiveBrowseOp {

	public interface Callback
	{
		public abstract void onBrowseResult(ProgressiveBrowseOp op, int startingIndex, int numberReturned, int totalMatches, DLNAObject[] arr);
		public abstract void onFinished(ProgressiveBrowseOp op);
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
	
	ProgressiveBrowseOp(Handler disp, Callback callback)
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
				ProgressiveBrowseOp.this.onCoreOpFinished();
			}
		});
	}
	
	void hook_onBrowseResult(int startingIndex, int numberReturned, int totalMatches, DLNAObject[] arr)
	{
		final int startingIndex1 = startingIndex;
		final int numberReturned1 = numberReturned;
		final int totalMatches1 = totalMatches;
		final DLNAObject[] arr1 = arr;
		mDisp.post(new Runnable() {
			public void run()
			{
				ProgressiveBrowseOp.this.onBrowseResult(startingIndex1, numberReturned1, totalMatches1, arr1);
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
	
	void onBrowseResult(int startingIndex, int numberReturned, int totalMatches, DLNAObject[] arr)
	{
		if (!mDisposed) {
			if (mCallback != null) {
				mCallback.onBrowseResult(this, startingIndex, numberReturned, totalMatches, arr);
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
