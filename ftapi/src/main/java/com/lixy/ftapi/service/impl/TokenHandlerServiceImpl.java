package com.lixy.ftapi.service.impl;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.lixy.ftapi.dao.UserTokenDao;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.UserToken;
import com.lixy.ftapi.exception.TokenException;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.service.TokenHandlerService;
import com.lixy.ftapi.type.TokenStatus;
import com.lixy.ftapi.util.CacheUtil;

@Service
public class TokenHandlerServiceImpl implements TokenHandlerService{
	
	@Autowired
	@Qualifier("userTokenDaoImpl")
	private UserTokenDao userTokenDao;
	
	@Autowired
	@Qualifier("queueManager")
	private QueueManager queueManager;

	Logger logger = LogManager.getLogger(getClass());
	 
	@Override
	public User parseUserFromToken(String token) throws TokenException {
		Object user = CacheUtil.getFromCache(token);
		
		if(user == null){ //User does not exist in the cache. Expire if exist in db
			queueManager.sendToTokenExpireQueue(token);
			throw new TokenException(token, "TOKEN_NOT_FOUND_IN_CACHE", null);
		}
		else {
			UserToken userToken = userTokenDao.getByToken(token);
			
			if(userToken == null){
				throw new TokenException(token, "UNALLOWED_TOKEN", null);
			} else if(userToken.getStatus().longValue() == TokenStatus.ACTIVE.getTokenStatus()){
				return (User) user;
			} else {
				throw new TokenException(token, "TOKEN_IS_NOT_ACTIVE", null);
			}
			
		}
		
	}

	@Override
	public String calculateTokenForUser(User srcUser) {
		return UUID.randomUUID().toString();
	}

	@Override
	public void insertToCache(String token, User user) {
		CacheUtil.addToCache(token, user);
	}
}
