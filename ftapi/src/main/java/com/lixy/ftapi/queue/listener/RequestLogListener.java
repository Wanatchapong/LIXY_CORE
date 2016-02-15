package com.lixy.ftapi.queue.listener;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.dao.RequestLogDao;
import com.lixy.ftapi.domain.HttpLog;
import com.lixy.ftapi.util.Util;

@Service("requestLogListener")
public class RequestLogListener implements MessageListener {
	private static Logger logger = LogManager.getLogger(RequestLogListener.class);

	@Autowired
	@Qualifier("requestLogDaoImpl")
	private RequestLogDao requestLogDao;

	@Override
	@Transactional
	public void onMessage(Message message) { // NOSONAR

		try {
			Object o = Util.fromByteArray(message.getBody());
			if(Util.isNullObject(o))
				return;			
			
			if(o instanceof Map == false)
				return;
			
			@SuppressWarnings("unchecked")
			Map<String, String> requestInfo = (Map<String, String>) o;		
			
			HttpLog rLog = new HttpLog();
			rLog.setAuthToken(requestInfo.get("X-AUTH-TOKEN"));
			rLog.setRemoteAddress(requestInfo.get("CLIENT_IP"));
			rLog.setRemoteHost(requestInfo.get("CLIENT_HOST"));
			rLog.setRequestURL(requestInfo.get("REQUEST_URL"));
			rLog.setCanonicalHostName(requestInfo.get("GLOB_CANONICAL_HOST_NAME"));
			rLog.setHostName(requestInfo.get("GLOB_HOST_NAME"));
			
			requestLogDao.create(rLog);
			
		} catch (Exception ex) {
			logger.error("RequestLogListener throw exception", ex);
		}
	}
	
	

}
