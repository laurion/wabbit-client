package com.yoero.base.rss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.yoero.base.UtilFunctions;
import com.yoero.base.UtilSettings;

public class RssChecker {
	private static RssChecker _instance;

	public static RssChecker Instance() {
		if (_instance == null)
			_instance = new RssChecker();
		return _instance;
	}

	public static int MESSAGE_RELEVANT_FOR_DAYS = 30;
	public static int START_DISPLAYING_MESSAGES_AFTER_HOURS = 24;
	public static Boolean TESTING_DISPLAY_ALWAYS = false;

	// public void PerformCheck(Context ctx, String rssPath) {
	// }

	public void PerformCheck(Context ctx, String rssPath, IRssEventHandler onLink) {
		int hoursSince = UtilSettings.hoursSinceFirstStart(ctx);
		if (hoursSince < START_DISPLAYING_MESSAGES_AFTER_HOURS) {
			return;
		}

		// _ctx = ctx;
		// _rssPath = rssPath;
		// _onLink = onLink;
		Thread t = new Thread(createRunnable(ctx, rssPath, onLink));
		t.start();
	}

	// Context _ctx;
	// String _rssPath;
	// IRssItemLink _onLink;
	Handler _h = new Handler();
	RssParser rp = new RssParser();

	private Runnable createRunnable(final Context ctx, final String rssPath, final IRssEventHandler onLink) {
		Runnable _fetchTwitter = new Runnable() {

			public void run() {
				final ArrayList<RssItem> items = rp.parseXml(rssPath);

				_h.post(new Runnable() {
					public void run() {
						boolean newLinkSet = false;
						
						if (items.size() > 0) {
							RssItem item = rp.rssItems.get(0);
							String guid = // item.Guid; - WILL NOT ACCEPT /
							"GID" + item.Guid.substring(item.Guid.lastIndexOf("/") + 1);

							Boolean isRelevant = UtilSettings.addToDate(item.PubDate, Calendar.DATE,
									MESSAGE_RELEVANT_FOR_DAYS).after(new Date());
							Boolean isNotProcessed = UtilSettings.loadSettingsString(ctx, guid) == null;
							if (TESTING_DISPLAY_ALWAYS)
								isNotProcessed = true;

							if (isRelevant && isNotProcessed) {
								UtilSettings.saveSettingsString(ctx, guid, "processed");

								String tweetId = rp.link.substring(rp.link.lastIndexOf("/") + 1);
								//String clearedText = item.Title.replaceFirst(tweetId + ": ", "");
								String clearedText = item.Description.replaceFirst(tweetId + ": ", "")
										.replaceAll("<br>", "\n")
										.replaceAll("<p>", "\n\n")
										.replaceAll("</p>", "\n\n")
										.trim();

								if (clearedText.startsWith("!")) {
									// message just for web
									clearedText = null;
								} else if (clearedText.startsWith("[")) {
									// decode message
									clearedText = decodeMessage(clearedText, "]");
								} else if (clearedText.startsWith("(")) {
									// message only for specific app
									int indexOfEnd = clearedText.indexOf(")");
									String packageName = clearedText.substring(1, indexOfEnd);

									if (packageName.compareTo(ctx.getPackageName()) == 0) {
										clearedText = decodeMessage(clearedText, "\\)");
									} else {
										clearedText = null;
									}
								}

								if (clearedText != null) {
									String link = identifyLink(clearedText, "http://", "https://");
									if (link != null && onLink != null) {
										clearedText += " (tap on Follow the Link)";
										onLink.rssLink(link, false);
										UtilSettings.saveSettingsString(ctx, "SAVED_FOLLOW_THE_LINK", link);
										newLinkSet = true;
									} else {
										UtilSettings.saveSettingsString(ctx, "SAVED_FOLLOW_THE_LINK", null);
									}

									Toast toast = Toast.makeText(ctx, clearedText, Toast.LENGTH_LONG);
									UtilFunctions.FireLongToast(toast, 10);
								}
							}
						}

						String savedLink = UtilSettings.loadSettingsString(ctx, "SAVED_FOLLOW_THE_LINK");
						if (!newLinkSet && savedLink != null) {
							onLink.rssLink(savedLink, true);
						}

						// _ctx = null;
						// _onLink = null;
					}

					private String identifyLink(String text, String linkId) {
						String res = null;

						int linkStarts = text.indexOf(linkId);
						if (linkStarts > -1) {
							int linkEnds = text.indexOf(" ", linkStarts);
							if (linkEnds == -1)
								res = text.substring(linkStarts);
							else
								res = text.substring(linkStarts, linkEnds);
						}

						return res;
					}

					private String identifyLink(String text, String... str) {
						String res = null;
						for (String s : str) {
							res = identifyLink(text, s);
							if (res != null)
								break;
						}
						return res;
					}

					private String decodeMessage(String clearedText, String spl) {
						// String toDecrypt = clearedText.split(spl)[1].trim();

						// TimeEncrypter te = new TimeEncrypter();
						// try {
						// clearedText = te.Decrypt(toDecrypt, false);
						// } catch (Exception e) {
						// e.printStackTrace();
						// clearedText = null;
						// }

						return clearedText;
					}
				});
			}
		};

		return _fetchTwitter;
	}

}
