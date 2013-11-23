package com.yoero.base.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DesEncrypter {
	Cipher ecipher;
	Cipher dcipher;

	public boolean UrlSafe;

	public DesEncrypter() {
		this(new byte[] { -17, 4, 23, -101, 85, -128, -32, -29 }, "DES");
	}
	
	public DesEncrypter(String keyString, String ivString, boolean urlSafe) {
		this(Base64.decode(keyString, Base64.DEFAULT), "DESede", Base64.decode(ivString, Base64.DEFAULT), "DESede/CBC/PKCS5Padding", urlSafe);
	}

	public DesEncrypter(byte[] key, String algorithm) {
		this(key, algorithm, new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A },
				"DES/CBC/PKCS5Padding", false);
	}

	public DesEncrypter(byte[] keyBytes, String algorithm, byte[] iv, String cipherName, boolean urlSafe) {
		SecretKey key = new SecretKeySpec(keyBytes, algorithm);
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		UrlSafe = urlSafe;
		try {
			ecipher = Cipher.getInstance(cipherName);
			dcipher = Cipher.getInstance(cipherName);

			// CBC requires an initialization vector
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (java.security.InvalidAlgorithmParameterException e) {
		} catch (javax.crypto.NoSuchPaddingException e) {
		} catch (java.security.NoSuchAlgorithmException e) {
		} catch (java.security.InvalidKeyException e) {
		}
	}

	// Buffer used to transport the bytes from one stream to another
	byte[] buf = new byte[20*1024];

	public void encrypt(InputStream in, OutputStream out) {
		try {
			// Bytes written to out will be encrypted
			out = new CipherOutputStream(out, ecipher);

			// Read in the cleartext bytes and write to out to encrypt
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.flush();
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	public void decrypt(InputStream in, OutputStream out) {
		try {
			// Bytes read from in will be decrypted
			in = new CipherInputStream(in, dcipher);

			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.flush();
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] encrypt(byte[] input) {
		try {
			return ecipher.doFinal(input);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public byte[] decrypt(byte[] input) {
		try {
			return dcipher.doFinal(input);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return null;
	}

	private String _codepage = "UTF-8";

	public String encrypt(String input) {
		byte[] buf;
		try {
			buf = input.getBytes(_codepage);
			buf = encrypt(buf);
			String res = Base64.encodeToString(buf, Base64.NO_WRAP).trim();

			if (UrlSafe)
				res = res.replaceAll("\\+", "\\-").replaceAll("/", "_").replaceAll("=", "");

			return res;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String decrypt(String input) {
		try {
			int pad = (input.length() % 4);
			if (pad == 3)
				pad = 1;
			for (int i = 0; i < pad; i++) {
				input += "=";
			}

			if (UrlSafe) {
				input = input.replaceAll("-", "+").replaceAll("_", "/");
			}

			byte[] buf = null;
			try {
				buf = Base64.decode(input, Base64.NO_WRAP);
			} catch (Exception ex) {
				buf = Base64.decode(input, Base64.DEFAULT);
			}
			buf = decrypt(buf);

			String fromDes = new String(buf, _codepage);
			return fromDes;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}