package com.yoero;

import com.yoero.base.IGameApp;

import android.app.Activity;
import android.app.Application;
public class GameApp extends Application implements IGameApp {
	private static boolean _initialized; 
	public boolean initializeAppIfNeeded(Activity ctx) {
		if (!_initialized) {
			initializeApp(ctx, this);
			_initialized = true;
			return true;
		} else {
			return false;
		}
	}

	public static void initializeApp(Activity ctx, Application app) {
		// FREE Google Play Version
		
		// ads
//		int daysUsingApp = UtilSettings.hoursSinceFirstStart(ctx) / 24;
//		if (daysUsingApp > 5)
//			daysUsingApp = 5;
//		
//		AdControllerFullscreen.AdFullViewFloor = 11 - daysUsingApp; // lower to 20 after couple days
//		AdControllerFullscreen.AdFullViewVariance = 3;
//		
//		AdControllerBanner.AdViewFloor = 3;
//		AdControllerBanner.AdViewVariance = 3;
		
//		AdConfig.TEST_MODE = true;
//		AdNetworkBase.ADS_TEST_MODE = true;
//		InmobiController.INMOBI_KEY = "4028cbff3b93b240013bf7fb482906ac";
		
	}
}