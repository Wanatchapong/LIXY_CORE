package com.lixy.ftapi.task;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.UserTokenDao;
import com.lixy.ftapi.domain.UserToken;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.type.SwitchType;

@Service
@Transactional
public class TokenExpireChecker {
	
	private Logger logger = LogManager.getLogger(getClass().getName());
	
	@Autowired
	@Qualifier("userTokenDaoImpl")
	private UserTokenDao userTokenDao;
	
	@Autowired
	private QueueManager queueManager;

	
	@Scheduled(fixedRate = Constant.TOKEN_EXPIRE_CHECKER_FIXED_RATE, initialDelay = 10000)
	public void expireTokens() throws InterruptedException {

		List<Long> userIdList = userTokenDao.getUserTokensByStatus(SwitchType.ACTIVE.getSwithStatus());
		
		for (Long uid : userIdList) {
			List<UserToken> userToken = userTokenDao.getTokenToBeExpire(uid, Constant.CACHE_TIMEOUT_IN_SEC);
			for (UserToken token : userToken) {
				try {
					queueManager.sendToTokenExpireQueue(token.getToken());
				} catch (Exception e) {
					logger.error("Error when sending expire queue.", e);
				}
			}
		}
		
		

		
	}


}
