package com.lixy.ftapi.exception;

import org.springframework.security.core.AuthenticationException;

import com.lixy.ftapi.model.UserAuthentication;

public class UserAuthenticationException extends AuthenticationException{

	private static final long serialVersionUID = 3356606184917713565L;
	
	private final UserAuthentication authentication;
	
	public UserAuthenticationException(String msg) {
		super(msg);
		this.authentication = null;
	}
	
	public UserAuthenticationException(String msg, UserAuthentication authentication) {
		super(msg);
		this.authentication = authentication;
		
	}

	public UserAuthentication getAuthentication() {
		return authentication;
	}


}
