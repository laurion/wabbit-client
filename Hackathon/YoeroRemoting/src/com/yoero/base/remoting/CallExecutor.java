package com.yoero.base.remoting;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;


public class CallExecutor {
	public void executeCall(CommunicatorBase cb, RemoteCallHolder rch) {
		executeCall(cb, rch, null, null);
	}
	
	public <T> void executeCall(CommunicatorBase cb,RemoteCallHolder rch, Class<T> expectedClass) {
		executeCall(cb, rch, expectedClass, null);
	}
	
	public <T> void executeCall(CommunicatorBase cb,RemoteCallHolder rch, TypeReference<T> expectedArray) {
		executeCall(cb, rch, null, expectedArray);
	}
	
	private <T> void executeCall(final CommunicatorBase cb, final RemoteCallHolder rch, final Class<T> expectedClass, final TypeReference<T> expectedArray) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					rch.retryCall = false;
					Exception ex = cb.executeCall(rch, expectedClass, expectedArray);
					if (ex == null) {
						process(rch);
						//Log.d("CallExecutor", "executeCall, invokeCallback rch.method: "+ rch.method);
						rch.invokeCallback();
					} else {
						mErrorHandler.errorOccurred(rch, ex);
						try {
							//hehLog.d("CallExecutor", "executeCall, waitEnter");
							synchronized (rch) {
								rch.wait();	
							}
							Log.d("CallExecutor", "executeCall, waitExit");
						} catch (InterruptedException e) {
							e.printStackTrace();
							rch.retryCall = false;
						}
					}
				} while (rch.retryCall);
			}
		});
		t.start();
	}
	
	protected void process(RemoteCallHolder rch) {
		
	}
	
	public static IGlobalErrorHandler mErrorHandler;

	// LoginData = (LoginDataHolder) rch.parsedResult;
}
