package com.yoero.base.fmb;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.yoero.base.Download;
import com.yoero.base.UtilFunctions;
import com.yoero.base.crypto.TimeEncrypter;

public class ActivityLogger {
	private static String URL_BASE;

	public final static String ST_EMPTY = "EMPTY";
	public final static String ST_INVALID = "INVALID";
	public final static String ST_USED_UP = "USED_UP";
	public final static String ST_EXPIRED = "EXPIRED";
	
	private Handler _h = new Handler();

//	private String _iv;
//	private String _key;
	private TimeEncrypter _te;

	private int _cid;
	private String _email;
	private String _uuid;

	public ActivityLogger(String env, String iv, String key, int cid, String email, String uuid) {
		URL_BASE = "http://"+ env +"/Api/";
		
//		_iv = iv;
//		_key = key;
		_te = new TimeEncrypter(iv, key, true);

		_cid = cid;
		_email = email;
		_uuid = uuid;
	}

	public void logStart(Context ctx, IActivityLoggerResult onComplete) {
		String details = UtilFunctions.getPackageAndVersion(ctx);
		activityCallback(ctx, "start", details, onComplete);
	}

	public void logEnd(Context ctx) {
		activityNoCallback(ctx, "end", UtilFunctions.getPackageAndVersion(ctx));
	}

	public void codeCheck(Context ctx, String code, IActivityLoggerResult onComplete) {
		String rssPath = generateRequestUrl(ctx, "CodeCheck", code, null);
		executeReq(rssPath, new StartResult(onComplete));
	}
	
	//
	
	public void activityCallback(Context ctx, String name, String details, IActivityLoggerResult onComplete) {
		String rssPath = generateRequestUrl(ctx, "Activity", name, details);
		executeReq(rssPath, new StartResult(onComplete));
	}

	private class StartResult extends ActivityLoggerBase {
		public StartResult(IActivityLoggerResult onComplete) {
			super(onComplete);
		}

		public void onResult(JSONObject json) throws JSONException {
			if (_invokeOnEnd != null) {
				String str = json.getString("result");
				_invokeOnEnd.onCallCompleted(str, null);
			}
		}

		public void reportProblem(Exception ex) {
			if (_invokeOnEnd != null) {
				_invokeOnEnd.onCallCompleted(null, ex);
			}
		}
	}

	public void activityNoCallback(Context ctx, String name, String details) {
		String rssPath = generateRequestUrl(ctx, "Activity", name, details);
		executeReq(rssPath, null);
	}

	//

	private String generateRequestUrl(Context ctx, String action, String name, String details) {
		String url = URL_BASE + action + "?"+ generateSig(ctx) + "&name=" + name;
		if (details != null)
			url += "&details=" + details;

		return url;
	}

	private String generateSig(Context ctx) {
		String eid = null;
		if (_email != null)
			eid = _te.Encrypt(_email, 60 * 24 * 30);

		String uid = null;
		if (_uuid != null)
			uid = _te.Encrypt(_uuid, 60 * 24 * 30);

		return "eid=" + eid + "&uid=" + uid + "&cid=" + _cid;
	}

	private void executeReq(final String url, final ActivityLoggerBase han) {
		Runnable runMe = new Runnable() {
			public void run() {
				final String json = Download.fetch(url);

				if (han != null) {
					_h.post(new Runnable() {
						public void run() {
							Exception prob = null;
							try {
								if (json != null && json.trim().length() > 0) {
									JSONObject myJson = new JSONObject(json);
									if (myJson.has("error")) { 
										han.reportProblem(new Exception(myJson.getString("error")));
									} else {
										han.onResult(myJson);
									}
								} else {
									prob = new Exception(ST_EMPTY);
								}
							} catch (Exception ex) {
								prob = ex;
							}

							if (prob != null)
								han.reportProblem(prob);
						}
					});
				}
			}
		};

		Thread t = new Thread(runMe);
		t.start();
	}
}
