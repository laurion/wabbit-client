package com.yoero.base.downloading;


import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import com.yoero.base.Download;
import com.yoero.base.ICallback;
import com.yoero.base.UtilFunctions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

public class DownloadRemote implements Observer {
	public DownloadRemote(String url, String localPath, ICallback onEnd) {
		_url = url;
		_localPath = localPath;
		_onEnd = onEnd;
	}

	public static boolean ScanNewFiles;
	public static boolean DeleteOldDownload;
	public static String ProgressDialogTitle = "Downloading Media...";

	private String _url;
	private String _localPath;
	private ICallback _onEnd;

	public void process(Activity act) {
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			Toast.makeText(act, "Please mount your SD card to perform this operation", Toast.LENGTH_LONG).show();
			return;
		}

		File locFile = new File(_localPath);
		if (locFile.exists()) {
			if (!DeleteOldDownload && locFile.exists() && locFile.length() > 0) {
				_onEnd.call(_localPath);
				return;
			} else {
				locFile.delete();
			}
		} else {
			int lastSlashIndex = _localPath.lastIndexOf("/");
			if (lastSlashIndex > -1) {
				String locFolderPath = _localPath.substring(0, lastSlashIndex);
				File localFolder = new File(locFolderPath);
				if (!localFolder.exists()) {
					localFolder.mkdirs();
				}
			}
		}

		progressBarInit(act, 0);

		try {
			// Toast.makeText(getBaseActivity(), "Processing request...", Toast.LENGTH_SHORT).show();
			Download download = new Download(new RandomAccessFile(_localPath, "rw"), new URL(_url), false);
			download.addObserver(this);
			download.download();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// ProgressBar fun times
	private ProgressDialog _progressDialog;

	public void progressBarInit(Activity act, int currProgress) {
		if (_progressDialog == null) {
			_progressDialog = new ProgressDialog(act);
		}

		_progressDialog.setCancelable(false);
		_progressDialog.setIndeterminate(false);
		_progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		_progressDialog.setProgress(currProgress);

		_progressDialog.setTitle(ProgressDialogTitle);
	}

	public void progressBarSet(int currentProgress, int maxSize) {
		if (!_progressDialog.isShowing()) {
			_progressDialog.setMax(maxSize);
			_progressDialog.show();
		}

		_progressDialog.setProgress(currentProgress);
	}

	public boolean progressBarEnd() {
		if (_progressDialog != null) {
			_progressDialog.dismiss();
			_progressDialog = null;
			return true;
		} else {
			return false;
		}
	}

	private static Handler mUpdateHandler = new Handler();

	@Override
	public void update(final Observable observable, Object data) {
		mUpdateHandler.post(new Runnable() {
			@Override
			public void run() {
				Download download = (Download) observable;
				processUpdate(download);
			}
		});
	}

	//
	@SuppressLint("NewApi")
	private void processUpdate(Download download) {
		if (download.getStatus() == Download.INIT) {
			progressBarSet(0, download.getSize());
		} else if (download.getStatus() >= Download.COMPLETE) {
			download.deleteObservers();
			progressBarEnd();
			// refresh media in gallery
			if (ScanNewFiles && UtilFunctions.froyoOrNewer() && _progressDialog != null) {
				MediaScannerConnection.scanFile(_progressDialog.getContext(), new String[] { "file://" + _localPath },
						null, new MediaScannerConnection.OnScanCompletedListener() {

							public void onScanCompleted(String path, Uri uri) {
								// Log.i("ExternalStorage", "Scanned " + path + ":");
								// Log.i("ExternalStorage", "-> uri=" + uri);
							}
						});
			}

			downloadCompleted();
		} else {
			int progress = download.getDownloaded();
			progressBarSet(progress, download.getSize());
		}
	}

	protected void downloadCompleted() {
		if (_onEnd != null) {
			_onEnd.call(_localPath);
			_onEnd = null;
		}
	}
}
