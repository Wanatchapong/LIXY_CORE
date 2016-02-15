package com.lixy.ftapi.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.model.UserAuthentication;

public class ServerUtils {
	private static Logger logger = LogManager.getLogger(ServerUtils.class.getName());
	
	private ServerUtils(){
		//make singleton
	}

	public static Map<String, String> convertHTTPServletRequestToHostMap(HttpServletRequest request) {
		HashMap<String, String> reqMap = new HashMap<>();

		if (request != null) {
			reqMap.put("CLIENT_IP", request.getRemoteAddr());
			reqMap.put("CLIENT_HOST", request.getRemoteHost());
			reqMap.put("CLIENT_USER", request.getRemoteUser());

			Principal p = request.getUserPrincipal();
			if (p != null) {
				reqMap.put("LDAP_USER", p.getName());
			}
		}

		return reqMap;
	}

	public static Map<String, String> convertHTTPServletRequestToHostMap(ServletRequest request) {
		HashMap<String, String> reqMap = new HashMap<>();

		if (request != null) {
			reqMap.put("CLIENT_IP", request.getRemoteAddr());
			reqMap.put("CLIENT_HOST", request.getRemoteHost());

			if (request instanceof HttpServletRequest) {
				HttpServletRequest servletRequest = (HttpServletRequest) request;
				reqMap.put("REQUEST_URL", servletRequest.getRequestURL().toString());
				reqMap.put(Constant.HEADER_AUTH_TOKEN, servletRequest.getHeader(Constant.HEADER_AUTH_TOKEN));
			}
		}
		
		try {
			InetAddress thisIp = InetAddress.getLocalHost();

			if (!Util.isNullObject(thisIp)) {
				reqMap.put("GLOB_HOST_NAME", thisIp.getHostName());
				reqMap.put("GLOB_CANONICAL_HOST_NAME", thisIp.getCanonicalHostName());
			}
		} catch (UnknownHostException e) {
			logger.error("Unknown host problem occured", e);
		}

		return reqMap;
	}

	public static Map<String, String> getServerInformation() {
		HashMap<String, String> serverInfo = new HashMap<>();

		try {
			InetAddress address = InetAddress.getLocalHost();
			if (address != null) {
				serverInfo.put("SERVER_HOST", address.getHostName());
				serverInfo.put("SERVER_ADDR", address.getHostAddress());
			}
		} catch (UnknownHostException e) {
			logger.error("UnknownHostException", e);
		}

		return serverInfo;
	}
	
	public static Principal getPrincipal(HttpServletRequest request){
		return request.getUserPrincipal();
	}
	
	public static UserAuthentication getAuth(){
		return (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
	}
	
	public static String getToken(HttpServletRequest request){
		return request.getHeader("X-AUTH-TOKEN");
	}
}
