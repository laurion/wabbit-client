package com.yoero.base.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class EncryptedStream {
	// Make sure to update IV and Key for Encrypter when needed
	private DesEncrypter _enc;

	public EncryptedStream(String key, String iv) {
		_enc = new DesEncrypter(key, iv, false);
	}

	public InputStream openInputStream(Context ctx, String root, String path)
			throws IOException {
		File f = new File(root + path);
		FileInputStream fis = new FileInputStream(f);

		return openInputStreamEncrypted(fis);
	}

	public InputStream openInputStreamEncrypted(InputStream is) {
		// dec version
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		synchronized (_enc) {
			_enc.decrypt(is, bos);
		}

		return new ByteArrayInputStream(bos.toByteArray());
	}

	public DefaultHandler parseXml(Context ctx, String path,
			DefaultHandler xmlParser) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();

		XMLReader xr = sp.getXMLReader();
		xr.setContentHandler(xmlParser);

		InputStream is = openInputStream(ctx, "", path);
		xr.parse(new InputSource(is));

		return xmlParser;
	}

	// extra code
	// public static Bitmap openBitmap(Context ctx, String path) {
	// InputStream is = null;
	// Bitmap bmp = null;
	// try {
	// is = openInputStream(ctx, path);
	// bmp = BitmapFactory.decodeStream(is);
	// // throw new Exception("test");
	// } catch (Exception e) {
	// e.printStackTrace();
	// // TODO: decrypting of images sometimes fails because of unknown
	// // reasons
	// bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444);
	// }
	//
	// return bmp;
	// }
}
