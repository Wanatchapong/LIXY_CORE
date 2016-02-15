package com.lixy.ftapi.service;

import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.exception.TokenException;

public interface TokenHandlerService {

	public User parseUserFromToken(String token) throws TokenException;
	
	public String calculateTokenForUser(User user);

	public void insertToCache(String token, User user);

	
}
