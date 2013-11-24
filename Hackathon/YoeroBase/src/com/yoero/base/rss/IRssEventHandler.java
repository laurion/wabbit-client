package com.yoero.base.rss;

public interface IRssEventHandler {
	public void rssUnreadMessages(int unread);
	public void rssLink(String link, boolean isCached);
}