package com.yoero.base.push;

import android.content.Context;

import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.preference.SoundType;
import com.yoero.base.UtilFunctions;
import com.yoero.base.UtilSettings;

public class PushFacade {

	private static PushManager _pushManager;

	public static void setPushWooshId(Context ctx, String appId, String senderId) {
		try {
			if (_pushManager == null && UtilFunctions.froyoOrNewer()) {
				String oldId = UtilSettings.loadSettingsString(ctx, "PUSH_WOOSH_ID");
				if (oldId != null && (appId == null || appId.compareTo(oldId) != 0)) {
					_pushManager = new PushManager(ctx, oldId, senderId);
					_pushManager.unregister();
				}

				UtilSettings.saveSettingsString(ctx, "PUSH_WOOSH_ID", appId);

				if (appId != null) {
					_pushManager = new PushManager(ctx, appId, senderId);
					_pushManager.onStartup(ctx, true);
					
					String soundOff = UtilSettings.loadSettingsString(ctx, "PUSHWOOSH_ISSOUNDOFF");
					if (soundOff != null && soundOff.equalsIgnoreCase("YESOFF")) {
						soundEnabled(ctx, false);
					}
				}
			}
		} catch (Exception ex) {
			// Devices without Google IDs (like Kindle)
		}
	}
	
	public static void soundEnabled(Context ctx, boolean enabled) {
		// Doesn't seem to work when you are outside the app
		if (_pushManager != null) {
			UtilSettings.saveSettingsString(ctx, "PUSHWOOSH_ISSOUNDOFF", enabled ? null : "YESOFF");
			_pushManager.setSoundNotificationType(enabled ? SoundType.DEFAULT_MODE : SoundType.NO_SOUND);
		}
	}
	
	public static PushManager getPushManager(){
		return _pushManager;
	}
}
