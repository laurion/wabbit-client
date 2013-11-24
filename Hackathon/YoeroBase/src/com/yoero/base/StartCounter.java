package com.yoero.base;

import android.content.Context;

public class StartCounter {
	protected String KEY_START_COUNTER_CURR_VALUE = "KEY_START_COUNTER_CURR_VALUE";
	
	public int Current(Context ctx) {
		// TODO: Remove in production
//		if (true)
//			return 3;
		
		int prevStarts = UtilSettings.loadSettingsInt(ctx, KEY_START_COUNTER_CURR_VALUE);
				
		prevStarts++;
		UtilSettings.saveSettingsInt(ctx, KEY_START_COUNTER_CURR_VALUE, prevStarts);
		
		return prevStarts;
	}
	
	public int CurrentDontIncrement(Context ctx) {
		return UtilSettings.loadSettingsInt(ctx, KEY_START_COUNTER_CURR_VALUE);
	}
	
	public void Reset(Context ctx) {
		UtilSettings.saveSettingsInt(ctx, KEY_START_COUNTER_CURR_VALUE, 0);
	}
	
	public void ResetTo(Context ctx, int to) {
		UtilSettings.saveSettingsInt(ctx, KEY_START_COUNTER_CURR_VALUE, to);
	}
}