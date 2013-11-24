package com.yoero.base.highscore;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;

import com.yoero.base.UtilFunctions;
import com.yoero.base.UtilSettings;
import com.yoero.base.crypto.TimeEncrypter;
import com.yoero.base.http.HttpRequest;

public class HighScoreController {
	private static final String KEY_DEVICEID = "KEY_DEVICEID";
	// private static final String KEY_PLAYERID = "KEY_PLAYERID";

	private String BASE_SERVICE_URL;

	private TimeEncrypter _te;
	private String _iv;

	private int _cid;
	private int _gameId;
	private String _email;
	private String _uuid;

	private int _deviceId;

	// private int _playerId;

	public HighScoreController(String env, String iv, String key, int cid, int gameId, Context ctx) {
		BASE_SERVICE_URL = "http://" + env + "/HighScores/";

		_te = new TimeEncrypter(iv, key, true);
		_iv = iv;

		_cid = cid;
		_gameId = gameId;
		String strEmail = UtilFunctions.getAccountEmail(ctx);
		_email = _te.Encrypt(strEmail);
		String strId = UtilFunctions.getDeviceId(ctx);
		_uuid = _te.Encrypt(strId);

		// _deviceId = UtilSettings.loadSettingsInt(ctx, KEY_DEVICEID);
		DeviceIdSelectOrAdd(ctx);
	}

	public String getToken(int modeId) {
		String toEnc = _cid + "|" + _gameId + "|" + modeId + "|" + _deviceId;
		return _te.Encrypt(toEnc);
	}

	public void DeviceIdSelectOrAdd(final Context ctx) {
		Runnable proc = new Runnable() {
			@Override
			public void run() {
				_deviceId = DeviceIdSelectOrAddProcess(ctx);
			}
		};
		Thread t = new Thread(proc);
		t.start();
	}

	private int DeviceIdSelectOrAddProcess(Context ctx) {
		int deviceId = 0;
		try {
			String result = HttpRequest.get(BASE_SERVICE_URL + "DeviceIdSelectOrAdd", false, "gameId", _gameId, "iv",
					_iv, "deviceName", _uuid, "email", _email).body();

			deviceId = Integer.parseInt(result);
			UtilSettings.saveSettingsInt(ctx, KEY_DEVICEID, deviceId);
		} catch (Exception ex) {
			deviceId = -1;
			ex.printStackTrace();
		}

		return deviceId;
	}

	public void HighScoreListSave(final Context ctx, final String list, final int modeId, final ICallback call) {
		// dialogShow(ctx);
		Runnable proc = new Runnable() {
			@Override
			public void run() {
				boolean res = HighScoreListSaveProcess(ctx, list, modeId);
				if (call != null) {
					call.onCallBack(ctx, res);
				}
				// dialogHide();
			}
		};
		Thread t = new Thread(proc);
		t.start();
	}

	private boolean HighScoreListSaveProcess(Context ctx, String list, int modeId) {
		boolean res = false;
		try {
			deviceIdRequired(ctx);

			if (_deviceId > 0) {
				String token = getToken(modeId);
				String httpRes = HttpRequest.get(BASE_SERVICE_URL + "HighScoreListSave", true, "gameId", _gameId, "iv",
						_iv, "token", token, "list", list, "ver", UtilFunctions.getVersionCode(ctx)).connectTimeout(10000).body();
				int httpResInt = Integer.parseInt(httpRes);
				if (httpResInt > 0) {
					res = true;
				}
			}

		} catch (Exception ex) {
			res = false;
		}

		return res;
	}

	private void deviceIdRequired(Context ctx) {
		if (_deviceId <= 0) {
			_deviceId = DeviceIdSelectOrAddProcess(ctx);
		}
	}

	public void HighScoreList(final Context ctx, final int modeId, final int days, final IHighScoreListCallback call) {
		dialogShow(ctx);
		Runnable proc = new Runnable() {
			@Override
			public void run() {
				ArrayList<HighScoreItem> res = HighScoreListProcess(ctx, modeId, days);
				call.HighScoreListResult(res);
				dialogHide();
			}
		};
		Thread t = new Thread(proc);
		t.start();
	}

	public ArrayList<HighScoreItem> HighScoreListProcess(Context ctx, int modeId, int days) {
		ArrayList<HighScoreItem> items = null;
		try {
			deviceIdRequired(ctx);

			if (_deviceId > 0) {
				String result = HttpRequest.get(BASE_SERVICE_URL + "HighScoreList", true, "gameId", _gameId, "iv", _iv,
						"token", getToken(modeId), "days", days, "ver", UtilFunctions.getVersionCode(ctx)).body();
				
				items = new ArrayList<HighScoreItem>();
				if (result.compareTo("UPDATE_NOW") == 0) {
					items.add(HighScoreItem.createUpdateItem());
				} else if (result.trim().length() > 0) {
					String[] lines = result.split("\n");
					for (String l : lines) {
						String[] data = l.trim().split(",");
						HighScoreItem si = new HighScoreItem(data[0], Integer.parseInt(data[1]));
						items.add(si);
					}
				}
			}
		} catch (Exception ex) {
			items = null;
			ex.printStackTrace();
		}

		return items;
	}

	public void SaveListAndDownloadOnline(final Context ctx, final String list, final int modeId, final int days,
			final ICallback onSave, final IHighScoreListCallback call) {
		dialogShow(ctx);
		Runnable proc = new Runnable() {
			@Override
			public void run() {
				ArrayList<HighScoreItem> res = null;
				if (list == null || HighScoreListSaveProcess(ctx, list, modeId)) {
					if (list != null && onSave != null) {
						onSave.onCallBack(ctx, true);
					}
					res = HighScoreListProcess(ctx, modeId, days);
				}

				call.HighScoreListResult(res);
				dialogHide();
			}
		};
		Thread t = new Thread(proc);
		t.start();
	}

	private ProgressDialog _pd;

	private void dialogShow(Context ctx) {
		dialogHide();
		_pd = ProgressDialog.show(ctx, "Processing", "Please wait...", true, true);
	}

	private void dialogHide() {
		if (_pd != null) {
			_pd.dismiss();
		}
	}

	public String encrypt(String toEnc, int minutes) {
		return _te.Encrypt(toEnc, minutes);
	}

	public String decrypt(String toDec) throws Exception {
		return _te.Decrypt(toDec);
	}
}
