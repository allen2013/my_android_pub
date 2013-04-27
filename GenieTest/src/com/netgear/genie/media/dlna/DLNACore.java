package com.netgear.genie.media.dlna;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.os.Handler;

public class DLNACore {
	
	public static final int FUNCTION_CONTROL_POINT = 0;
	public static final int FUNCTION_MEDIA_SERVER = 1;
	public static final int FUNCTION_MEDIA_RENDERER = 2;
	public static final int FLUSH_MODE_ALL = 0;
	public static final int FLUSH_MODE_MEDIA_RENDERER_ONLY = 1;
	public static final int FLUSH_MODE_MEDIA_SERVER_ONLY = 2;
	public static final int DMR_STATE_STOPPED = 0;
	public static final int DMR_STATE_PLAYING = 1;
	public static final int DMR_STATE_LOADING = 2;
	public static final int DMR_STATE_PAUSED = 3;
	public static final int DMR_STATE_NO_MEDIA_PRESENT = 4;

	public interface Callback
	{
		public abstract void onMediaServerListChanged();
		public abstract void onMediaRendererListChanged();
		public abstract void onMediaServerStateVariablesChanged(UUID deviceUuid, String serviceId, String[] names, String[] values);
		public abstract void onMediaRendererStateVariablesChanged(UUID deviceUuid, String serviceId, String[] names, String[] values);
		public abstract void dmrOpen(String url, String mimeType, String metaData);
		public abstract void dmrPlay();
		public abstract void dmrPause();
		public abstract void dmrStop();
		public abstract void dmrSeekTo(long timeInMillis);
		public abstract void dmrSetMute(boolean mute);
		public abstract void dmrSetVolume(int volume);
	}
	
	public static long parseTrackDuration(String text)
	{
		return nativeParseTrackDuration(text);
	}
	
	public DLNACore(Callback callback)
	{
		mCallback = callback;
		mCore = coreInit(this);
	}
	
	public synchronized void dispose()
	{
		if (!mDisposed) {
			coreDestroy(mCore);
			mCore = 0;
			mDisposed = true;
		}
	}
	
	public synchronized boolean isDisposed()
	{
		return mDisposed;
	}
	
	public void setProperty(String name, String value)
	{
		checkDisposed();
		coreSetProperty(mCore, name, value);
	}
	
	public void importFileSystemToMediaServer(String dir, String name, boolean ignoreDot)
	{
		checkDisposed();
		coreImportFileSystemToMediaServer(mCore, dir, name, ignoreDot);
	}
	
	public void clearMediaServerContent()
	{
		checkDisposed();
		coreClearMediaServerContent(mCore);
	}
	
	public void loadConfig(byte[] configData)
	{
		checkDisposed();
		coreLoadConfig(mCore, configData);
	}
	
	public byte[] saveConfig()
	{
		checkDisposed();
		return coreSaveConfig(mCore);
	}
	
	public boolean start()
	{
		checkDisposed();
		if (!mStarted) {
			mStarted = coreStart(mCore);
		}
		return mStarted;
	}
	
	public boolean started()
	{
		return mStarted;
	}
	
	public void stop()
	{
		checkDisposed();
		if (mStarted) {
			coreStop(mCore);
			mStarted = false;
		}
	}
	
	public void enableFunction(int func, boolean enable)
	{
		checkDisposed();
		coreEnableFunction(mCore, func, enable);
	}
	
	public boolean isFunctionEnabled(int func)
	{
		checkDisposed();
		return coreIsFunctionEnabled(mCore, func);
	}
	
	public List<DeviceDesc> snapshotMediaServerList()
	{
		checkDisposed();
		return Arrays.asList(coreSnapshotMediaServerList(mCore));
	}

	public List<DeviceDesc> snapshotMediaRendererList()
	{
		checkDisposed();
		return Arrays.asList(coreSnapshotMediaRendererList(mCore));
	}
	
	public void flushDeviceList(int flushMode)
	{
		checkDisposed();
		coreFlushDeviceList(mCore, flushMode);
	}
	
	public void searchDevices(int mx)
	{
		checkDisposed();
		coreSearchDevices(mCore, mx);
	}
	
	public List<String> queryStateVariables(UUID deviceUuid, String serviceId, String[] nameArr)
	{
		checkDisposed();
		String[] valueArr = new String[nameArr.length];
		if (coreQueryStateVariables(mCore, deviceUuid.getMostSignificantBits(), deviceUuid.getLeastSignificantBits(), serviceId, nameArr, valueArr)) {
			return Arrays.asList(valueArr);
		}
		throw new RuntimeException();
	}
	
	public BrowseOp browseMediaServer(UUID mediaServerUuid, String objectId, boolean deep, BrowseOp.Callback callback)
	{
		checkDisposed();
		BrowseOp op = new BrowseOp(mDisp, callback);
		if (coreBrowseMediaServer(mCore, mediaServerUuid.getMostSignificantBits(), mediaServerUuid.getLeastSignificantBits(), objectId, deep, op)) {
			return op;
		}
		throw new RuntimeException();
	}

	public ProgressiveBrowseOp browseMediaServerEx(UUID mediaServerUuid, String objectId, int step, ProgressiveBrowseOp.Callback callback)
	{
		checkDisposed();
		ProgressiveBrowseOp op = new ProgressiveBrowseOp(mDisp, callback);
		if (coreBrowseMediaServerEx(mCore, mediaServerUuid.getMostSignificantBits(), mediaServerUuid.getLeastSignificantBits(), objectId, step, op)) {
			return op;
		}
		throw new RuntimeException();
	}
	
	public AsyncOp playMedia(UUID mediaRendererUuid, DLNAItem mediaItem, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (corePlayMedia(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), mediaItem, op)) {
			return op;
		}
		throw new RuntimeException();
	}
	
	public AsyncOp playMediaList(UUID mediaRendererUuid, DLNAObject[] mediaItemArray, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (corePlayMediaList(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), mediaItemArray, op)) {
			return op;
		}
		throw new RuntimeException();
	}

	public AsyncOp playFile(UUID mediaRendererUuid, String path, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (corePlayFile(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), path, op)) {
			return op;
		}
		throw new RuntimeException();
	}
	
	public AsyncOp stopMedia(UUID mediaRendererUuid, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (coreStopMedia(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), op)) {
			return op;
		}
		throw new RuntimeException();
	}

	public AsyncOp pauseMedia(UUID mediaRendererUuid, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (corePauseMedia(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), op)) {
			return op;
		}
		throw new RuntimeException();
	}
	
	public AsyncOp prevMedia(UUID mediaRendererUuid, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (corePrevMedia(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), op)) {
			return op;
		}
		throw new RuntimeException();
	}

	public AsyncOp nextMedia(UUID mediaRendererUuid, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (coreNextMedia(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), op)) {
			return op;
		}
		throw new RuntimeException();
	}

	public AsyncOp muteMedia(UUID mediaRendererUuid, boolean mute, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (coreMuteMedia(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), mute, op)) {
			return op;
		}
		throw new RuntimeException();
	}

	public AsyncOp changeMediaVolume(UUID mediaRendererUuid, int volume, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (coreChangeMediaVolume(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), volume, op)) {
			return op;
		}
		throw new RuntimeException();
	}
	
	public AsyncOp seekMedia(UUID mediaRendererUuid, int timeInMillis, AsyncOp.Callback callback)
	{
		checkDisposed();
		AsyncOp op = new AsyncOp(mDisp, callback);
		if (coreSeekMedia(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), timeInMillis, op)) {
			return op;
		}
		throw new RuntimeException();
	}

	public QueryPositionInfoOp queryMediaPositionInfo(UUID mediaRendererUuid, QueryPositionInfoOp.Callback callback)
	{
		checkDisposed();
		QueryPositionInfoOp op = new QueryPositionInfoOp(mDisp, callback);
		if (coreQueryMediaPositionInfo(mCore, mediaRendererUuid.getMostSignificantBits(), mediaRendererUuid.getLeastSignificantBits(), op)) {
			return op;
		}
		throw new RuntimeException();
	}
	
	public void dmrReportState(int state)
	{
		checkDisposed();
		coreDmrReportState(mCore, state);
	}
	
	public void dmrReportErrorStatus(boolean error)
	{
		checkDisposed();
		coreDmrReportErrorStatus(mCore, error);
	}
	
	public void dmrReportProgress(long currentMillis, long totalMillis)
	{
		checkDisposed();
		coreDmrReportProgress(mCore, currentMillis, totalMillis);
	}
	
	public void dmrReportVolume(int volume, boolean muted)
	{
		checkDisposed();
		coreDmrReportVolume(mCore, volume, muted);
	}
	
	public UUID getMediaRendererUuid()
	{
		return coreGetMediaRendererUuid(mCore);
	}

	public UUID getMediaServerUuid()
	{
		return coreGetMediaRendererUuid(mCore);
	}
	
	void hook_onMediaServerListChanged()
	{
		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.onMediaServerListChanged();
				}
			}
		});
	}

	void hook_onMediaRendererListChanged()
	{
		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.onMediaRendererListChanged();
				}
			}
		});
	}
	
	void hook_onMediaServerStateVariablesChanged(UUID deviceUuid, String serviceId, String[] names, String[] values)
	{
		final UUID aDeviceUuid = deviceUuid;
		final String aServiceId = serviceId;
		final String[] aNames = names;
		final String[] aValues = values;
		
		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.onMediaServerStateVariablesChanged(aDeviceUuid, aServiceId, aNames, aValues);
				}
			}
		});
	}

	void hook_onMediaRendererStateVariablesChanged(UUID deviceUuid, String serviceId, String[] names, String[] values)
	{
		final UUID aDeviceUuid = deviceUuid;
		final String aServiceId = serviceId;
		final String[] aNames = names;
		final String[] aValues = values;
		
		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.onMediaRendererStateVariablesChanged(aDeviceUuid, aServiceId, aNames, aValues);
				}
			}
		});
	}
	
	void hook_dmrOpen(String url, String mimeType, String metaData)
	{
		final String aUrl = url;
		final String aMimeType = mimeType;
		final String aMetaData = metaData;
		
		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.dmrOpen(aUrl, aMimeType, aMetaData);
				}
			}
		});
	}
	
	void hook_dmrPlay()
	{
		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.dmrPlay();
				}
			}
		});
	}
	
	void hook_dmrPause()
	{
		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.dmrPause();
				}
			}
		});
	}
	
	void hook_dmrStop()
	{
		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.dmrStop();
				}
			}
		});
	}
	
	void hook_dmrSeekTo(long timeInMillis)
	{
		final long aTimeInMillis = timeInMillis;

		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.dmrSeekTo(aTimeInMillis);
				}
			}
		});
	}
	
	void hook_dmrSetMute(boolean mute)
	{
		final boolean aMute = mute;

		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.dmrSetMute(aMute);
				}
			}
		});
	}
	
	void hook_dmrSetVolume(int volume)
	{
		final int aVolume = volume;

		mDisp.post(new Runnable() {
			public void run()
			{
				if (!DLNACore.this.isDisposed()) {
					DLNACore.this.dmrSetVolume(aVolume);
				}
			}
		});
	}
	
	void onMediaServerListChanged()
	{
		if (mCallback != null) {
			mCallback.onMediaServerListChanged();
		}
	}
	
	void onMediaRendererListChanged()
	{
		if (mCallback != null) {
			mCallback.onMediaRendererListChanged();
		}
	}

	void onMediaServerStateVariablesChanged(UUID deviceUuid, String serviceId, String[] names, String[] values)
	{
		if (mCallback != null) {
			mCallback.onMediaServerStateVariablesChanged(deviceUuid, serviceId, names, values);
		}
	}
	
	void onMediaRendererStateVariablesChanged(UUID deviceUuid, String serviceId, String[] names, String[] values)
	{
		if (mCallback != null) {
			mCallback.onMediaRendererStateVariablesChanged(deviceUuid, serviceId, names, values);
		}
	}
	
	void dmrOpen(String url, String mimeType, String metaData)
	{
		if (mCallback != null) {
			mCallback.dmrOpen(url, mimeType, metaData);
		}
	}
	
	void dmrPlay()
	{
		if (mCallback != null) {
			mCallback.dmrPlay();
		}
	}

	void dmrPause()
	{
		if (mCallback != null) {
			mCallback.dmrPause();
		}
	}

	void dmrStop()
	{
		if (mCallback != null) {
			mCallback.dmrStop();
		}
	}

	void dmrSeekTo(long timeInMillis)
	{
		if (mCallback != null) {
			mCallback.dmrSeekTo(timeInMillis);
		}
	}

	void dmrSetMute(boolean mute)
	{
		if (mCallback != null) {
			mCallback.dmrSetMute(mute);
		}
	}

	void dmrSetVolume(int volume)
	{
		if (mCallback != null) {
			mCallback.dmrSetVolume(volume);
		}
	}
	
	private void checkDisposed()
	{
		if (mDisposed) {
			throw new RuntimeException();
		}
	}
	
	private static native long nativeParseTrackDuration(String text);
	private static native long coreInit(Object delegate);
	private static native void coreDestroy(long core);
	private static native void coreSetProperty(long core, String name, String value);
	private static native void coreImportFileSystemToMediaServer(long core, String dir, String name, boolean ignoreDot);
	private static native void coreClearMediaServerContent(long core);
	private static native void coreLoadConfig(long core, byte[] configData);
	private static native byte[] coreSaveConfig(long core);
	private static native boolean coreStart(long core);
	private static native void coreStop(long core);
	private static native void coreEnableFunction(long core, int func, boolean enable);
	private static native boolean coreIsFunctionEnabled(long core, int func);
	private static native DeviceDesc[] coreSnapshotMediaServerList(long core);
	private static native DeviceDesc[] coreSnapshotMediaRendererList(long core);
	private static native void coreFlushDeviceList(long core, int flushMode);
	private static native void coreSearchDevices(long core, int mx);
	private static native boolean coreQueryStateVariables(long core, long uuidMSB, long uuidLSB, String serviceId, String[] nameArr, String[] valueArr);
	private static native boolean coreBrowseMediaServer(long core, long uuidMSB, long uuidLSB, String objectId, boolean deep, BrowseOp op);
	private static native boolean coreBrowseMediaServerEx(long core, long uuidMSB, long uuidLSB, String objectId, int step, ProgressiveBrowseOp op);
	private static native boolean corePlayMedia(long core, long uuidMSB, long uuidLSB, DLNAItem mediaItem, AsyncOp op);
	private static native boolean corePlayMediaList(long core, long uuidMSB, long uuidLSB, DLNAObject[] mediaItemArray, AsyncOp op);
	private static native boolean corePlayFile(long core, long uuidMSB, long uuidLSB, String path, AsyncOp op);
	private static native boolean coreStopMedia(long core, long uuidMSB, long uuidLSB, AsyncOp op);
	private static native boolean corePauseMedia(long core, long uuidMSB, long uuidLSB, AsyncOp op);
	private static native boolean corePrevMedia(long core, long uuidMSB, long uuidLSB, AsyncOp op);
	private static native boolean coreNextMedia(long core, long uuidMSB, long uuidLSB, AsyncOp op);
	private static native boolean coreMuteMedia(long core, long uuidMSB, long uuidLSB, boolean mute, AsyncOp op);
	private static native boolean coreChangeMediaVolume(long core, long uuidMSB, long uuidLSB, int volume, AsyncOp op);
	private static native boolean coreSeekMedia(long core, long uuidMSB, long uuidLSB, int timeInMillis, AsyncOp op);
	private static native boolean coreQueryMediaPositionInfo(long core, long uuidMSB, long uuidLSB, QueryPositionInfoOp op);
	private static native void coreDmrReportState(long core, int state);
	private static native void coreDmrReportErrorStatus(long core, boolean error);
	private static native void coreDmrReportProgress(long core, long currentMillis, long totalMillis);
	private static native void coreDmrReportVolume(long core, int volume, boolean muted);
	private static native UUID coreGetMediaServerUuid(long core);
	private static native UUID coreGetMediaRendererUuid(long core);
	
	Callback mCallback = null;
	boolean mDisposed = false;
	boolean mStarted = false;
	Handler mDisp = new Handler();
	long mCore = 0;
	
	static {
		System.loadLibrary("DLNACore");
	}
}
