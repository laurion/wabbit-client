package com.hackathon.activity;

import com.hackathon.remoting.GameHelper;
import com.hackathon.remoting.GameHelper.GameHelperListener;
import com.yoero.base.IGameApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class BaseActivity extends Activity{
	protected GameHelper mGameHelper;
	@Override
	public void onCreate(Bundle savedExtras){
		super.onCreate(savedExtras);
		if(getApplication() instanceof IGameApp){
			IGameApp app = (IGameApp) getApplication();
			if (app.initializeAppIfNeeded(this)) {
				localInitialization();
			}
		}
		mGameHelper = new GameHelper(this);
		mGameHelper.setup(listener);
	}
	protected void localInitialization() {
		
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
	private GameHelper.GameHelperListener listener = new GameHelperListener() {
		@Override
		public void onSignInSucceeded() {
			Intent intent = new Intent(BaseActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		
		@Override
		public void onSignInFailed() { 
			Toast.makeText(BaseActivity.this, "Login failed. Please try again", Toast.LENGTH_LONG).show();
		}
	};
}