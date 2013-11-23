//
//  MessageActivity.java
//
// Pushwoosh Push Notifications SDK
// www.pushwoosh.com
//
// MIT Licensed

package com.arellomobile.android.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.arellomobile.android.push.utils.GeneralUtils;
import com.arellomobile.android.push.utils.notification.BannerNotificationFactory;
import com.arellomobile.android.push.utils.notification.NotificationFactory;
import com.arellomobile.android.push.utils.notification.SimpleNotificationFactory;
import com.google.android.gcm.GCMBaseIntentService;

public class PushGCMIntentService extends GCMBaseIntentService
{
	private static final String TAG = "GCMIntentService";
	private static boolean mSimpleNotification = true;

	private Handler mHandler;

	public PushGCMIntentService()
	{
		String senderId = PushManager.mSenderId;
		Boolean simpleNotification = PushManager.sSimpleNotification;
		if (null != simpleNotification)
		{
			mSimpleNotification = simpleNotification;
		}
		if (null == senderId)
		{
			senderId = "";
		}
		mSenderId = senderId;
		mHandler = new Handler();
	}

	@Override
	protected void onRegistered(Context context, String registrationId)
	{
		Log.i(TAG, "Device registered: regId = " + registrationId);
		DeviceRegistrar.registerWithServer(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId)
	{
		Log.i(TAG, "Device unregistered");
		DeviceRegistrar.unregisterWithServer(context, registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent)
	{
		Log.i(TAG, "Received message");
		// notifies user
		generateNotification(context, intent, mHandler);
	}

	@Override
	protected void onDeletedMessages(Context context, int total)
	{
		Log.i(TAG, "Received deleted messages notification");
	}

	@Override
	protected void onError(Context context, String errorId)
	{
		Log.e(TAG, "Messaging registration error: " + errorId);
		PushEventsTransmitter.onRegisterError(context, errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId)
	{
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	private static void generateNotification(Context context, Intent intent, Handler handler)
	{
		Bundle extras = intent.getExtras();
		if (extras == null)
		{
			return;
		}

		extras.putBoolean("foregroud", GeneralUtils.isAppOnForeground(context));

		String title = (String) extras.get("title");
		String link = (String) extras.get("l");

		// empty message with no data
		Intent notifyIntent;
		if (link != null)
		{
			// we want main app class to be launched
			notifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		else
		{
			notifyIntent = new Intent(context, PushHandlerActivity.class);
			notifyIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// pass all bundle
			notifyIntent.putExtra("pushBundle", extras);
		}

		// first string will appear on the status bar once when message is added
		CharSequence appName = context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
		if (null == appName)
		{
			appName = "";
		}

		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationFactory notificationFactory;
		
		//is this banner notification?
		String bannerUrl = (String) extras.get("b");
		
		//also check that notification layout has been placed in layout folder
		int layoutId =
				context.getResources().getIdentifier(BannerNotificationFactory.sNotificationLayout, "layout", context.getPackageName());

		if (layoutId != 0 && bannerUrl != null)
		{
			notificationFactory =
					new BannerNotificationFactory(context, extras, appName.toString(), title, PushManager.sSoundType, PushManager.sVibrateType);
		}
		else
		{
			notificationFactory =
					new SimpleNotificationFactory(context, extras, appName.toString(), title, PushManager.sSoundType,
							PushManager.sVibrateType);
		}
		notificationFactory.generateNotification();
		notificationFactory.addSoundAndVibrate();
		notificationFactory.addCancel();

		Notification notification = notificationFactory.getNotification();

		notification.contentIntent =
				PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (mSimpleNotification)
		{
			manager.notify(PushManager.MESSAGE_ID, notification);
		}
		else
		{
			manager.notify(PushManager.MESSAGE_ID++, notification);
		}

		generateBroadcast(context, extras);
	}

	private static void generateBroadcast(Context context, Bundle extras)
	{
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(context.getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
		broadcastIntent.putExtras(extras);

		JSONObject dataObject = new JSONObject();
		try
		{
			if (extras.containsKey("title"))
			{
				dataObject.put("title", extras.get("title"));
			}
			if (extras.containsKey("u"))
			{
				dataObject.put("userdata", new JSONObject(extras.getString("u")));
			}
		}
		catch (JSONException e)
		{
			// pass
		}
		
		broadcastIntent.putExtra(BasePushMessageReceiver.DATA_KEY, dataObject.toString());

		context.sendBroadcast(broadcastIntent, context.getPackageName() + ".permission.C2D_MESSAGE");
	}
}

