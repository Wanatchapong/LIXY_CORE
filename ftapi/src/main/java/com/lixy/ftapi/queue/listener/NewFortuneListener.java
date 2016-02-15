package com.lixy.ftapi.queue.listener;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.domain.MailTemplate;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.VirtualCommenterPrice;
import com.lixy.ftapi.service.AlarmService;
import com.lixy.ftapi.service.CustomerService;
import com.lixy.ftapi.service.FortuneService;
import com.lixy.ftapi.service.MailService;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.type.AlarmType;
import com.lixy.ftapi.type.ConversationStatusType;
import com.lixy.ftapi.type.RequestStatusType;
import com.lixy.ftapi.type.SwitchType;
import com.lixy.ftapi.util.Util;

@Service("newFortuneListener")
public class NewFortuneListener implements MessageListener {
	private static Logger logger = LogManager.getLogger(NewFortuneListener.class);

	@Autowired
	private FortuneService fortuneService;

	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MailService mailService;

	@Override
	@Transactional
	public void onMessage(Message message) {
		logger.info("New Fortune Message Received ---> " + message);

		try {
			Long id = new BigInteger(message.getBody()).longValue();

			FortuneRequest request = fortuneService.getFortuneRequestById(id);
			request.setRequestStatus(RequestStatusType.APPROVED.getShortCode());			

			fortuneService.updateFortuneRequest(request);
			
			VirtualCommenterPrice price = customerService.getVirtualCommenterPriceByResponseType(request.getVirtualCommenterId(), request.getResponseTypeId(), SwitchType.ACTIVE.getSwithStatus());
			fortuneService.createConversation(request.getId(), price == null ? 1L : price.getMaxTransactionCount(), ConversationStatusType.OPEN);

			User user = userService.getUserById(request.getRequesterId());
			
			MailTemplate template = mailService.readByTag("NEW_FORTUNE");
			Map<String, String> details = new HashMap<>();
			details.put("REQ_ID", request.getId().toString());
			
			mailService.addToPool(SwitchType.ACTIVE.getSwithStatus(), template.getId(), template.getFromAddress(), user.getUsername(), template.getSubject(), details);
			
			logger.info("NewFortuneListener -> Request accepted. " + Util.getInputLogsSimple("request", request));

		} catch (Exception ex) {
			logger.error("NewFortuneListener ->" + message, ex);
			alarmService.addAlarm(AlarmType.NEW_FORTUNE_APPROVE_ERROR, "Fortune request could not approved.", ExceptionUtils.getFullStackTrace(ex));
		}

	}

}
