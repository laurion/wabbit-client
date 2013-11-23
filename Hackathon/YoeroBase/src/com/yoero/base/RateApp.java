package com.yoero.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class RateApp extends StartCounter {
	final String KEY_RATE_TIMES_ASKED = "KEY_RATE_TIMES_ASKED";

	public static int rateAskInHours = 24;
	public static int paidAppOfferInHours = 0;
	public static String paidAppPackage;
	public static String packageName;

	public void executeOnStart(final Context ctx) {
		int hoursSinceFirstStart = UtilSettings.hoursSinceFirstStart(ctx);
		int timesAsked = UtilSettings.loadSettingsInt(ctx, KEY_RATE_TIMES_ASKED);

		if (hoursSinceFirstStart > rateAskInHours && timesAsked < 2) {
			int timesStarted = Current(ctx);

			OnClickListener okClicked = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (packageName == null) {
						packageName = ctx.getPackageName();
					}
					
					LinksUtil.linkToApp(ctx, packageName);
					incrementTimesAsked(ctx, 1);
				}
			};

			if (timesStarted > 3 && timesAsked == 0) {
				UtilFunctions.ShowMessageBox(ctx, getString(ctx, R.string.ratings_title),
						getString(ctx, R.string.ratings_body1), getString(ctx, R.string.yes), okClicked,
						getString(ctx, R.string.no), null);
				incrementTimesAsked(ctx, timesAsked);
			} else if (timesStarted > 7 && timesAsked == 1) {
				UtilFunctions.ShowMessageBox(ctx, getString(ctx, R.string.ratings_title),
						getString(ctx, R.string.ratings_body2), getString(ctx, R.string.yes), okClicked,
						getString(ctx, R.string.no), null);
				incrementTimesAsked(ctx, timesAsked);
			}
		} else if (paidAppOfferInHours > 0 && paidAppPackage != null && hoursSinceFirstStart > paidAppOfferInHours
				&& timesAsked == 2) {
			OnClickListener okClicked = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LinksUtil.linkToApp(ctx, paidAppPackage);
				}
			};

			UtilFunctions.ShowMessageBox(ctx, getString(ctx, R.string.ratings_title),
					getString(ctx, R.string.ratings_body1), getString(ctx, R.string.yes), okClicked,
					getString(ctx, R.string.no), null);
			incrementTimesAsked(ctx, timesAsked);
		}
	}

	private String getString(Context ctx, int id) {
		return ctx.getResources().getString(id);
	}

	private void incrementTimesAsked(Context ctx, int timesAsked) {
		timesAsked++;
		UtilSettings.saveSettingsInt(ctx, KEY_RATE_TIMES_ASKED, timesAsked);
	}
}
