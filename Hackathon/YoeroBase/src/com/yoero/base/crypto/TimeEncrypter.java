package com.yoero.base.crypto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeEncrypter {
	public TimeEncrypter() {
		this("bM90FTADGws=", "DkhdKzZDSggTC9PUjzrDsKeelebd8v3W", false);
	}
	
	public TimeEncrypter(String ivString, String keyString, boolean urlSafe) {
		// freaking unsined integers
		byte[] key = Base64.decode(keyString, Base64.DEFAULT);
		byte[] iv = Base64.decode(ivString, Base64.DEFAULT);

		_tdes = new DesEncrypter(key, "DESede", iv, "DESede/CBC/PKCS5Padding", urlSafe);
		// _tdes = new DesEncrypter();

		df.setTimeZone(TimeZone.getTimeZone("GTM"));
	}

	private DesEncrypter _tdes;
	private int _validForHours = 48;
	private String dateFormat = "yyyyMMddHHmmss";
	SimpleDateFormat df = new SimpleDateFormat(dateFormat);

	public String Encrypt(String toEncrypt) {
		return Encrypt(toEncrypt, _validForHours * 60);
	}

	public String Encrypt(String toEncrypt, int validForMinutes) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.add(Calendar.MINUTE, validForMinutes);
		toEncrypt = df.format(cal.getTime()) + toEncrypt;

		return _tdes.encrypt(toEncrypt);
	}

	public String Decrypt(String toDecrypt) throws Exception {
		return Decrypt(toDecrypt, true);
	}

	public String Decrypt(String toDecrypt, Boolean exceptionOnExpire) throws Exception {
		String fromDes = _tdes.decrypt(toDecrypt);

		String validUntilString = fromDes.substring(0, dateFormat.length());
		String encoded = fromDes.substring(dateFormat.length());

		Date validUntil = df.parse(validUntilString);
		Date now = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
		if (exceptionOnExpire && validUntil.compareTo(now) < 0)
			throw new Exception("Crypted data signature expired");

		return encoded;
	}

	public Object getIv() {
		// TODO Auto-generated method stub
		return null;
	}
}
