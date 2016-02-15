package com.lixy.ftapi.util;

import java.math.BigInteger;

import org.apache.commons.codec.digest.DigestUtils;

public class SecurityUtil {
	
	private SecurityUtil(){
		//do nothing
	}
	
	public static String md5(String original){
		byte[] digest = DigestUtils.md5(original);
		BigInteger bigInt = new BigInteger(1,digest);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
		return hashtext;
	}
	
}
