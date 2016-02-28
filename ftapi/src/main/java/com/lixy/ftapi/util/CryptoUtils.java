package com.lixy.ftapi.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.codec.Base64;

import com.lixy.ftapi.exception.CryptoInitException;

public class CryptoUtils {
	private static Logger logger = LogManager.getLogger(CryptoUtils.class);

	private static Cipher ecipher;
	private static Cipher dcipher;

	private static String cipherKey = "SOLFPS4501431991"; // 128 bit key
	
	private CryptoUtils(){
		//make singleton
	}

	public static void init() throws CryptoInitException  {
		logger.info("Initializing Crypto Utility...");
		Key key = new SecretKeySpec(cipherKey.getBytes(), "AES");

		try {
			ecipher = Cipher.getInstance("AES");
			dcipher = Cipher.getInstance("AES");

			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			logger.error("Crypto Init Error IVK", e);
			throw new CryptoInitException("InvalidKeyException");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Crypto Init Error NSA", e);
			throw new CryptoInitException("NoSuchAlgorithmException");
		} catch (NoSuchPaddingException e) {
			logger.error("Crypto Init Error NSP", e);
			throw new CryptoInitException("NoSuchPaddingException");
		}

		logger.info("Initializing Crypto Utility... DONE");
	}

	public static String encrypt(String str) {
		try {
			// encode the string into a sequence of bytes using the named charset
			// storing the result into a new byte array. 
			byte[] utf8 = str.getBytes("UTF8");
			byte[] enc = ecipher.doFinal(utf8);
			// encode to base64
			enc = Base64.encode(enc);

			return new String(enc);
		} catch (Exception e) {
			logger.error("Encrypt failed for " + str, e);
		}
		return null;
	}

	public static String decrypt(String str) {
		try {
			// decode with base64 to get bytes
			byte[] dec = Base64.decode(str.getBytes());
			byte[] utf8 = dcipher.doFinal(dec);
			// create new string based on the specified charset
			return new String(utf8, "UTF8");
		}

		catch (Exception e) {
			logger.error("Decrypt failed for " + str + "- e " + e.toString(), e);
		}

		return null;
	}
	
	public static void main(String[] args) throws CryptoInitException {
		CryptoUtils.init();
		System.out.println(CryptoUtils.encrypt("2637lixy"));
	}
	
}
