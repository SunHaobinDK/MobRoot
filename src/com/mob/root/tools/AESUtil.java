package com.mob.root.tools;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class AESUtil {
	
	private static String AES_KEY = "EWERWRREW4567i8o";

	public static String encrypt(String data) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();

			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}

			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(AES_KEY.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);

			return Base64.encodeToString(encrypted, Base64.CRLF).trim();

		} catch (Exception e) {
			return null;
		}
	}

	public static String desEncrypt(String data) {
		try {
			byte[] encrypted1 = Base64.decode(data, Base64.CRLF);
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(AES_KEY.getBytes());

			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			return null;
		}
	}
}
