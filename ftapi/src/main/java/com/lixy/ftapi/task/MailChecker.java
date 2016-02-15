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
import com.lixy.ftapi.dao.MailPoolDao;
import com.lixy.ftapi.domain.MailPool;
import com.lixy.ftapi.service.MailService;
import com.lixy.ftapi.type.SwitchType;
import com.lixy.ftapi.util.Util;

@Service
@Transactional
public class MailChecker {

	private Logger logger = LogManager.getLogger(getClass().getName());

	@Autowired
	@Qualifier("mailService")
	private MailService mailService;

	@Autowired
	@Qualifier("mailPoolDaoImpl")
	private MailPoolDao mailPoolDao;

	@Scheduled(fixedRate = Constant.MAIL_CHECKER_FIXED_RATE, initialDelay = 10000)
	public void sendOldWaitingMailToQueue() throws InterruptedException {

		List<MailPool> mailToBeSent = mailPoolDao.readPoolByStatus(SwitchType.ACTIVE.getSwithStatus());

		if (Util.isNullOrEmptyList(mailToBeSent))
			return;

		for (MailPool mailPool : mailToBeSent) {
			try {
				mailService.sendToQueue(mailPool);
			} catch (Exception ex) {
				logger.error("Mail pool error", ex);
			}
		}
	}

}
