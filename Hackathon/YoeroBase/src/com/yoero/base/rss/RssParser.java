package com.yoero.base.rss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RssParser {
	public String title;
	public String link;
	ArrayList<RssItem> rssItems;
	
	public ArrayList<RssItem> parseXml(String uri) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		ArrayList<RssItem> items = new ArrayList<RssItem>();

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document dom = db.parse(uri);
			
			Element channelElement = (Element)dom.getElementsByTagName("channel").item(0);
			title = getTextValue(channelElement, "title");
			link = getTextValue(channelElement, "link");			
			
			NodeList nl = dom.getElementsByTagName("item");

			if (nl != null) {
				for (int i = 0; i < nl.getLength(); i++) {
					Element node = (Element) nl.item(i);

					RssItem ri = new RssItem();

					ri.Title = getTextValue(node, "title");
					ri.Guid = getTextValue(node, "guid");
					ri.Description = getTextValue(node, "description");
					ri.PubDate = getDateValue(node, "pubDate");
					
					items.add(ri);
				}
			}
			
			rssItems = items;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}

		return items;
	}

	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	@SuppressWarnings("unused")
	private static int getIntValue(Element ele, String tagName) {
		// in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele, tagName));
	}

	private static Date getDateValue(Element node, String tagName) {
		String str = getTextValue(node, tagName);

		SimpleDateFormat df = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss Z");

		try {
			return df.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return new Date();
		}
	}
}

