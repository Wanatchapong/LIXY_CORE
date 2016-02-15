package com.lixy.ftapi.listener;

import java.util.Map;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.conf.common.ApplicationContextProvider;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.util.ServerUtils;

public class RequestListener implements ServletRequestListener {

	private static final Logger logger = LogManager.getLogger(RequestListener.class.getName());

	@Override
	public void requestDestroyed(ServletRequestEvent reqEvent) {
		// do nothing for now
	}

	@Override
	public void requestInitialized(ServletRequestEvent reqEvent) {
		Map<String, String> requestInfo = ServerUtils.convertHTTPServletRequestToHostMap(reqEvent.getServletRequest());
		logger.info(requestInfo);
		
		if("A".equals(Constant.DB_REQUEST_LOG_ACTIVE)){
			QueueManager queueManager = (QueueManager) ApplicationContextProvider.getApplicationContext().getBean("queueManager");
			queueManager.sendToRequestQueue(requestInfo);
		}

	}

}
