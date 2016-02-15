package com.lixy.ftapi.listener;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrintCommandListener implements ProtocolCommandListener{
	
	private static Logger logger = LogManager.getLogger(PrintCommandListener.class);

	@Override
	public void protocolCommandSent(ProtocolCommandEvent event) {
		logger.info(event.getMessage());
	}

	@Override
	public void protocolReplyReceived(ProtocolCommandEvent event) {
		logger.info(event.getMessage());
	}
}
