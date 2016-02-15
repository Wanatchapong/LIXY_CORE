package com.lixy.ftapi.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.MailPoolDao;
import com.lixy.ftapi.dao.MailTemplateDao;
import com.lixy.ftapi.domain.MailPool;
import com.lixy.ftapi.domain.MailTemplate;
import com.lixy.ftapi.queue.listener.MailListener;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.service.MailService;
import com.lixy.ftapi.util.Util;

@Service("mailService")
@Transactional
public class MailServiceImpl implements MailService {

	private static Logger logger = LogManager.getLogger(MailListener.class);

	private Properties props;

	@Autowired
	@Qualifier("mailPoolDaoImpl")
	private MailPoolDao mailPoolDao;
	
	@Autowired
	@Qualifier("mailTemplateDaoImpl")
	private MailTemplateDao mailTemplateDao;

	@Autowired
	@Qualifier("queueManager")
	private QueueManager queueManager;

	@Override
	public void init() {
		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

	}

	@Override
	public void send(Object identifier, String from, String to, String subject, String content) throws MessagingException {
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() { // NOSONAR

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Constant.GMAIL_ADDR, Constant.GMAIL_PASS);
			}

		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Constant.GMAIL_USER));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(content, "text/html; charset=utf-8");

			Transport.send(message);

			logger.info("Mail successfully sent. IDTF:" + identifier);

		} catch (MessagingException e) {
			throw e;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sendToQueue(MailPool poolRecord) {
		try {
			poolRecord.setStatus(1L); // in queue
			poolRecord.setDescription("IN_QUEUE");
			mailPoolDao.update(poolRecord);
			queueManager.sendToMailQueue(poolRecord.getId());
			logger.info("Successfully sent to queue! PID : " + poolRecord.getId());
		} catch (Exception e) {
			poolRecord.setStatus(3L); // error sending to queue
			poolRecord.setDescription(e.getMessage());
			mailPoolDao.update(poolRecord);
			logger.error("Could not sent to queue! PID : " + poolRecord.getId(), e);
		}
	}

	@Override
	public void addToPool(Long status, Long templateId, String fromAddress, String toAddress, String subject,
			Map<String, String> additional) {
		MailPool pool = new MailPool();
		pool.setStatus(status);
		pool.setTemplateId(templateId);
		pool.setFromAddress(fromAddress);
		pool.setToAddress(toAddress);
		pool.setSubject(subject);
		pool.setCreatedDate(new Date());
		pool.setIdentifier(UUID.randomUUID().toString());
		
		if(!Util.isNullOrEmptyHashMap(additional)){
			ObjectMapper mapper = new ObjectMapper();
			try {
				pool.setAdditionalInfo(mapper.writeValueAsString(additional));
			} catch (JsonProcessingException e) {
				logger.error("JSON PROCESS ERR", e);
			}
		}
		
		mailPoolDao.create(pool);
	}

	@Override
	public MailTemplate readByTag(String tag) {
		return mailTemplateDao.readByTag(tag);
	}

}
