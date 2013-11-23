package com.yoero.base.dataholders;

import android.annotation.SuppressLint;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class JacksonSimpleDateFormat extends SimpleDateFormat {
	public JacksonSimpleDateFormat() {
		if (mParser == null) {
			mParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			mParser.setTimeZone(TimeZone.getTimeZone("UTC"));
		}
	}
	
	@Override
	public StringBuffer format(Date date, StringBuffer buffer, FieldPosition field) {
		return mParser.format(date, buffer, field);
	}

	private static SimpleDateFormat mParser;
	@Override
	public Date parse(String string, ParsePosition position) {
		String str = string.split("\\.")[0];
		
		Date date = null;
		try {
			date = mParser.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		position.setIndex(string.length() - 1);
		return date;
	}

	@Override
	public Object clone() {
		return new JacksonSimpleDateFormat();
	}

}
