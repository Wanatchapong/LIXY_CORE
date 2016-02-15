package com.lixy.ftapi.queue.manager;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lixy.ftapi.domain.Alarm;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.util.Util;

@Service("queueManager")
public class QueueManager {
	private Logger logger = LogManager.getLogger(getClass());

	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@Autowired
	private AmqpTemplate requestTemplate;

	public void sendToTokenExpireQueue(String token) {
		try {
			if (!Util.isNullOrEmpty(token))
				rabbitTemplate.convertAndSend("TOKENEXPIRE", token);
		} catch (Exception ex) {
			logger.error("Error while send to token expire queue", ex);
		}
	}

	public void sendToNewUserQueue(User user) {
		try {
			if (!Util.isNullObject(user))
				rabbitTemplate.convertAndSend("NEWCUSTOMER", user);
		} catch (Exception ex) {
			logger.error("Error while send to new customer queue", ex);
		}
	}

	public void sendToMailQueue(Long poolId) {
		try {
			if (!Util.isNullObject(poolId))
				rabbitTemplate.convertAndSend("MAIL", poolId);
		} catch (Exception ex) {
			logger.error("Error while send to mail queue", ex);
		}
	}
	
	public void sendToNewFortuneQueue(Long requestId) {
		try {
			if (!Util.isNullObject(requestId))
				rabbitTemplate.convertAndSend("NEWFORTUNE", requestId);
		} catch (Exception ex) {
			logger.error("Error while send to new fortune queue", ex);
		}
	}
	
	public void sendToAlarmQueue(Alarm alarm) {
		try {
			if (!Util.isNullObject(alarm))
				rabbitTemplate.convertAndSend("ALARM", alarm);
		} catch (Exception ex) {
			logger.error("Error while send to alarm queue", ex);
		}
	}
	
	public void sendToUploadQueue(Long fileId) {
		try {
			if (!Util.isNullObject(fileId))
				rabbitTemplate.convertAndSend("FILEUPLOAD", fileId);
		} catch (Exception ex) {
			logger.error("Error while send to upload queue", ex);
		}
	}
	
	
	public void sendToRequestQueue(Map<String, String> requestInfo) {
		try {
			if (!Util.isNullOrEmptyHashMap(requestInfo))
				requestTemplate.convertAndSend("REQUEST", requestInfo);
		} catch (Exception ex) {
			logger.error("Error while send to request queue", ex);
		}
	}
	


}
