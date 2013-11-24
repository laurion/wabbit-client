package com.yoero.base.remoting;

public interface IGlobalErrorHandler {
	public void errorOccurred(RemoteCallHolder rch, Exception ex);
}
