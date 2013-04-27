package com.dragonflow;

import java.util.ArrayList;

import android.view.View;
import android.widget.TextView;
import com.netgear.*;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;
import com.dragonflow.genie.ui.R.layout;
import com.dragonflow.FileData;
import com.dragonflow.FileManager;

public class FileGridAdapter extends FileListAdapter
			implements FileAdapter{

	public FileGridAdapter(FileManager context, FileData infos, int style) {
		super(context, infos, style);
		// TODO Auto-generated constructor stub
	}
	protected void setViewExceptIcon(Viewholder holder, FileInfo fInfo) {
		TextView tv = holder.getName();
		if (tv == null) return;
		tv.setText(fInfo.name);
	}
	@Override
	public int getIconId() {
		// TODO Auto-generated method stub
		return R.id.gridicon;
	}
	@Override
	public int getFileNameTextId() {
		// TODO Auto-generated method stub
		return R.id.gridname;
	}
	@Override
	public int getLayoutId() {
		return R.layout.gridfileitem;
	}
	protected final int getStartSelfUpdateCount() { return 19;}
}
