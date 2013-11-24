package com.yoero.base.remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yoero.base.dataholders.BaseJsonObject;

public class CommunicatorBase {
	public static boolean DEBUG = true;

	public static String mBaseServiceUrl = "http://";
	public static String mFromApp;
	public static String mAuthCookie;

	//
	protected RemoteCallHolder buildRequest(IRemoteCallback call, String method, Object... params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (int i = 0; i < params.length; i += 2) {
			if (params[i + 1] != null) {
				nameValuePairs.add(new BasicNameValuePair(params[i] + "", params[i + 1] + ""));
			}
		}

		RemoteCallHolder rch = new RemoteCallHolder();
		if (DEBUG) {
			StringBuilder debugParts = new StringBuilder();
			debugParts.append(mBaseServiceUrl).append(method).append("?");
			for (NameValuePair nvp : nameValuePairs) {
				debugParts.append(nvp.getName()).append("=").append(nvp.getValue()).append("&");
			}

			rch.debugRequest = debugParts.toString();
			// we'll need to extract this to methods
			if(RemoteCallHolder.DEBUG == false)
				Log.d("Communicator", "executeCall, request: " + rch.debugRequest);
		}

		rch.caller = call;
		rch.method = method;
		try {
			rch.params = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return rch;
	}

	protected RemoteCallHolder buildRequestFile(IRemoteCallback call, String method, String fileParamName,
			byte[] fileContent, Object... params) {

		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		// add file
		entity.addPart(fileParamName, new ByteArrayBody(fileContent, fileParamName));

		StringBuilder debugParts = new StringBuilder();
		for (int i = 0; i < params.length; i += 2) {
			if (params[i + 1] != null) {
				try {
					if (params[i + 1] != null) {
						entity.addPart(params[i] + "", new StringBody(params[i + 1] + ""));
						debugParts.append(params[i]).append("=").append(params[i + 1]).append("&");
					}
				} catch (UnsupportedEncodingException e) {
				}
			}
		}

		RemoteCallHolder rch = new RemoteCallHolder();
		if (DEBUG) {
			String debugUrl = mBaseServiceUrl + "?" + fileParamName + "=[file]&" + debugParts;
			rch.debugRequest = debugUrl;
			Log.d("Communicator", "executeCall, request: " + debugUrl);
		}

		rch.caller = call;
		rch.method = method;
		rch.params = entity;

		return rch;
	}

	// public RemoteCallHolder genericCall(IRemoteCallback call, String method, Object... params) {
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	// for (int i = 0; i < params.length; i += 2) {
	// if (params[i + 1] != null) {
	// nameValuePairs.add(new BasicNameValuePair(params[i] + "", params[i + 1] + ""));
	// }
	// }
	//
	// RemoteCallHolder rch = new RemoteCallHolder();
	// rch.caller = call;
	// rch.method = method;
	// rch.params = nameValuePairs;
	//
	// rch.serverResponse = executeCall(rch.method, rch.params);
	//
	// return rch;
	// }

	public <T> Exception executeCall(RemoteCallHolder rch, Class<T> expectedClass, TypeReference<T> expectedArray) {
		String requestUrl = mBaseServiceUrl + rch.method;
		HttpPost post = new HttpPost(requestUrl);

		if (rch.headersToAdd != null) {
			for (String key : rch.headersToAdd.keySet()) {
				post.addHeader(key, rch.headersToAdd.get(key));
			}
		}

		post.addHeader("From-App", mFromApp);
		if (mAuthCookie != null) {
			post.addHeader("Auth-Cookie", mAuthCookie);			
		}

		try {
			post.setEntity(rch.params);

			HttpResponse resp = buildHttpClient().execute(post);
			String result = processResponse(resp);

			if (DEBUG) {
				rch.debugResponse = result;
				//Log.d("Communicator", "executeCall, response: " + result);
			}

			rch.serverResponse = result;
			if (expectedClass != null) {
				rch.parsedResult = BaseJsonObject.parseJson(rch.serverResponse, expectedClass);
			} else if (expectedArray != null) {
				rch.parsedResult = BaseJsonObject.parseJson(rch.serverResponse, expectedArray);
			}

			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ex;
		}
	}

	protected void preExecute(HttpPost post) {
		// Overload in child class if needed
	}

	private DefaultHttpClient buildHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();

		return client;
	}

	private String processResponse(HttpResponse response) throws IllegalStateException, IOException {
		// Check if server response is valid
		StatusLine status = response.getStatusLine();

		// Pull content stream from response
		HttpEntity entity = response.getEntity();
		InputStream inputStream = entity.getContent();

		ByteArrayOutputStream content = new ByteArrayOutputStream();

		// Read response into a buffered stream
		int readBytes = 0;
		byte[] sBuffer = new byte[512];
		while ((readBytes = inputStream.read(sBuffer)) != -1) {
			content.write(sBuffer, 0, readBytes);
		}

		// Return result from buffered stream
		String dataAsString = new String(content.toByteArray());

		if (status.getStatusCode() != 200) {
			throw new IOException("Invalid response from server: " + status.toString() + "\n\n" + dataAsString);
		}

		return dataAsString;
	}
}
