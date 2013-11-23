package com.yoero.base.remoting;

import java.util.Hashtable;

import android.os.Handler;
import ch.boye.httpclientandroidlib.HttpEntity;

public class RemoteCallHolder {
	public IRemoteCallback caller;
	public String method;
	public HttpEntity params;
	public String serverResponse;
	public Object parsedResult;
	public Hashtable<String, String> headersToAdd;
	public boolean retryCall;
	public String debugRequest;
	public String debugResponse;
	
	private static Handler mHandler;
	
	public static boolean DEBUG = false;
	
	public static void initHandler() {
		mHandler = new Handler();
	}
	
	public void invokeCallback() {
		final RemoteCallHolder ref = this;
		if(DEBUG){
			if(caller != null)
				caller.onResult(ref);
		}
		else{
			mHandler.post(new Runnable() {			
				@Override
				public void run() {
					if (caller != null) {
						caller.onResult(ref);
					}
				}
			});
		}
	}
}