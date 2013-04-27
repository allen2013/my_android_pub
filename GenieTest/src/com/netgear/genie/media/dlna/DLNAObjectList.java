package com.netgear.genie.media.dlna;

import java.util.ArrayList;

public class DLNAObjectList {

	public int size()
	{
		return mList.size();
	}
	
	public DLNAObject get(int index)
	{
		try{
			return mList.get(index);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public void dispose()
	{
		if (!mDisposed) {
			clearList();
			mDisposed = true;
		}
	}
	
	DLNAObjectList()
	{
	}
	
	public DLNAObjectList(DLNAObject[] arr)
	{
		for (DLNAObject mediaObj : arr) {
			// initialized from array, do not addRef()
			//mediaObj.addRef();
			mList.add(mediaObj);
		}
	}
	
	DLNAObjectList(DLNAObjectList other)
	{
		for (DLNAObject mediaObj : other.mList) {
			mediaObj.addRef();
			mList.add(mediaObj);
		}
	}
	
	public void append(DLNAObjectList ls)
	{
		for (DLNAObject mediaObj : ls.mList) {
			mediaObj.addRef();
			mList.add(mediaObj);
		}
	}

	private void clearList()
	{
		if (!mList.isEmpty()) {
			for (DLNAObject mediaObj : mList) {
				mediaObj.release();
			}
			mList.clear();
		}
	}
	
	boolean mDisposed = false;
	ArrayList<DLNAObject> mList = new ArrayList<DLNAObject>();
}
