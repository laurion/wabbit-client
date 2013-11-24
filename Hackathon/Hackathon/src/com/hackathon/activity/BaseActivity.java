package com.hackathon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hackathon.remoting.GameHelper;
import com.yoero.base.UtilFunctions;
import com.yoero.base.UtilFunctionsFroyo;
import com.yoero.base.fmb.ActivityLogger;

public class BaseActivity extends Activity implements GameHelper.GameHelperListener{
	protected GameHelper mGameHelper;
	protected static ActivityLogger mActivityLogger;
	
	@Override
	public void onCreate(Bundle savedExtras){
		super.onCreate(savedExtras);
		// activity logger
		if (mActivityLogger == null && UtilFunctions.froyoOrNewer()) {
			try {
				// Activity public key stays the same
				mActivityLogger = new ActivityLogger("freemailblast.com", "B/uc6YJnxKI=",
						"DkhdKzZDSggTC9PUjzrDsKeelebd8v3W", 8, UtilFunctionsFroyo.getAccountEmail(this),
						UtilFunctions.getDeviceId(this));
			} catch (Exception ex) {

			}
		}
		mGameHelper = new GameHelper(this);
		mGameHelper.setup(this);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		mGameHelper.onStart();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		mGameHelper.onStop();
	}
	
	protected void startMain(){
		Intent intent = new Intent(BaseActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	@Override
	public void onSignInSucceeded() {
	}
	@Override
	public void onSignInFailed() { 
		
	}
}
