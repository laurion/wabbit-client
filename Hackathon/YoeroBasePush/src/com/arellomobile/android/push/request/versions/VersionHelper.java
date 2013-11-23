//
//  VersionHelper.java
//
// Pushwoosh Push Notifications SDK
// www.pushwoosh.com
//
// MIT Licensed
package com.arellomobile.android.push.request.versions;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.arellomobile.android.push.data.PushZoneLocation;

/**
 * Date: 17.08.12
 * Time: 10:27
 *
 * @author mig35
 */
public interface VersionHelper
{
	Map<String, Object> getRegistrationUnregistrationData(Context context, String deviceRegistrationID);

	Map<String, Object> getSendPushStatData(Context context, String hash);

	Map<String,Object> getSendTagsData(Context context);

	Map<String,Object> getNearestZoneData(Context context, Location location);

	PushZoneLocation getPushZoneLocationFromData(JSONObject resultData) throws JSONException;

	Map<String, Object> getSendAppOpenData(Context context);
}
