package com.lixy.ftapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IPUtil {

	private static Logger logger = LogManager.getLogger(IPUtil.class);
	
	private IPUtil(){
		//nothing
	}
	
	public static String getIp() {
		BufferedReader in = null;
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			return in.readLine();
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (!Util.isNullObject(in)) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return null;
	}
	
	public static String getRemoteIp(ServletRequest request){
		return request.getRemoteAddr();
	}
}
