package com.lixy.ftapi.queue.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.dao.UserTokenDao;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.type.TokenStatus;
import com.lixy.ftapi.util.CacheUtil;
import com.lixy.ftapi.util.Util;

@Service("tokenExpireListener")
public class TokenExpireListener implements MessageListener{
	private static Logger logger = LogManager.getLogger(TokenExpireListener.class);
	
	@Autowired
	@Qualifier("userTokenDaoImpl")
	private UserTokenDao userTokenDao;
	
	@Override
	@Transactional
	public void onMessage(Message message) {
		logger.info("Token Expire Message Received ---> " + message);
		String token = new String(message.getBody());
		
		User u = (User) CacheUtil.getFromCache(token);
		if(!Util.isNullObject(u)){
			CacheUtil.removeFromCache(token);
		}
		
		if(!Util.isNullOrEmpty(token))
			userTokenDao.changeTokenStatus(token, TokenStatus.EXPIRED_TIME);
		
		logger.info(token + " is expired.");
	}

}
