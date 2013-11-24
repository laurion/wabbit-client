package com.yoero.base.fmb;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ActivityLoggerBase {	
	public ActivityLoggerBase(IActivityLoggerResult invokeOnEnd) {
		_invokeOnEnd = invokeOnEnd;
	}

	protected IActivityLoggerResult _invokeOnEnd;

	public abstract void onResult(JSONObject json) throws JSONException;
	public abstract void reportProblem(Exception ex);
}
