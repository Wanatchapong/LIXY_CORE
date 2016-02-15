package com.lixy.ftapi.queue.listener;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import org.apache.commons.lang.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.AlarmDao;
import com.lixy.ftapi.dao.MailPoolDao;
import com.lixy.ftapi.dao.MailTemplateDao;
import com.lixy.ftapi.dao.UserDao;
import com.lixy.ftapi.domain.Alarm;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.domain.MailPool;
import com.lixy.ftapi.domain.MailTemplate;
import com.lixy.ftapi.domain.ResponseType;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.VirtualCommenter;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.service.AlarmService;
import com.lixy.ftapi.service.CustomerService;
import com.lixy.ftapi.service.FortuneService;
import com.lixy.ftapi.service.MailService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.AlarmType;
import com.lixy.ftapi.util.Util;

@Service("mailListener")
public class MailListener implements MessageListener {
	private static Logger logger = LogManager.getLogger(MailListener.class);

	Pattern MAIL_LANG_PROP_PATTERN = Pattern.compile("##:(.+?):##");
	
	@Autowired
	@Qualifier("mailTemplateDaoImpl")
	private MailTemplateDao mailTemplateDao;

	@Autowired
	@Qualifier("mailPoolDaoImpl")
	private MailPoolDao mailPoolDao;

	@Autowired
	@Qualifier("userDaoImpl")
	private UserDao userDao;

	@Autowired
	@Qualifier("alarmDaoImpl")
	private AlarmDao alarmDao;

	@Autowired
	private MailService mailService;
	
	@Autowired
	private UtilService utilService;
	
	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	private FortuneService fortuneService;
	
	@Autowired
	private CustomerService customerService;


	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void onMessage(Message message) { // NOSONAR
		logger.info("Mail Message Received ---> " + message);

		try {
			Locale userLocale = null;
			
			Long poolId = new BigInteger(message.getBody()).longValue();

			MailPool mail = mailPoolDao.readById(poolId);

			if (Util.isNullObject(mail)) {
				logger.info("Pool record not found. Passing.");
				return;
			}

			User user = null;
			if (mail.getUserId() != null) {
				user = userDao.readById(mail.getUserId());
				if (Util.isNullObject(user)) {
					logger.info("User not found. Passing.");
					return;
				}else {
					String locale = user.getLocale();
					userLocale = LocaleUtils.toLocale(locale);
					
					if(Util.isNullObject(userLocale))
						userLocale = LocaleUtils.toLocale(Constant.DEFAULT_LOCALE);
				}
			}

			if (Util.isNullOrEmpty(mail.getIdentifier()))
				mail.setIdentifier(UUID.randomUUID().toString());

			MailTemplate template = mailTemplateDao.readById(mail.getTemplateId());

			String mailBase = "";
			if (!Util.isNullOrEmpty(mail.getMailContent())) { // bir mail metni
																// varsa
				mailBase = mail.getMailContent();
			} else if (!Util.isNullObject(template)) { // template varsa
														// template i kullan
				mailBase = template.getTemplate();
			}

			if (Util.isNullOrEmpty(mailBase)) {
				logger.error("NO MAIL CONTENT FOUND. Id :" + mail.getId());
				return;
			}

			Map<String, Object> detailMapper = new HashMap<>();

			if (!Util.isNullOrEmpty(mail.getAdditionalInfo())) {
				String additionalInfo = mail.getAdditionalInfo();

				ObjectMapper mapper = new ObjectMapper();
				detailMapper = mapper.readValue(additionalInfo, Map.class);
			}

			Matcher m = MAIL_LANG_PROP_PATTERN.matcher(mailBase);
			while (m.find()) {
			    String tag = m.group(1);
			    String templateMessage = utilService.getMessage(tag, userLocale);
			    
			    mailBase = mailBase.replace(m.group(0), templateMessage);
			}
			
			mail.setMailContent(mailBase);
			mailTagMapper(mail, user, detailMapper);

			tryToSend(mail);

		} catch (Exception ex) {
			logger.error("Mail Listener throw exception", ex);
		}
	}

	private void tryToSend(MailPool mail) {
		try {
			mailService.send(mail.getId(), mail.getFromAddress(), mail.getToAddress(), mail.getSubject(), mail.getMailContent());
			mail.setSentDate(new Date());
			mail.setStatus(2L);
			mail.setDescription("SUCCESS");

			logger.info("SEND_MAIL_SUCCESS. -> " + mail.getId());
		} catch (MessagingException e) {
			mail.setStatus(3L);
			mail.setDescription(e.getMessage());

			logger.error("SEND_MAIL_FAIL. -> " + mail.getId(), e);
		} catch (Exception e) {
			mail.setStatus(3L);
			mail.setDescription(e.getMessage());

			logger.error("SEND_MAIL_FAIL. -> " + mail.getId(), e);
		}

		mailPoolDao.update(mail);
	}

	private void mailTagMapper(MailPool mail, User user, Map<String, Object> info) {
		String mailContent = mail.getMailContent();

		if (mailContent.contains("##UUID##")) {
			mailContent = mailContent.replace("##UUID##", mail.getIdentifier());
		}

		if (mailContent.contains("##USERNAME##") && !Util.isNullObject(user)) {
			mailContent = mailContent.replace("##USERNAME##", user.getUsername());
		}

		if (!Util.isNullOrEmptyHashMap(info)) {
			for (Map.Entry<String, Object> entry : info.entrySet()) {
				mailContent = mailContent.replace("##" + entry.getKey() + "##", entry.getValue().toString());
			}
		}

		if (mail.getTemplateId() != null) {
			MailTemplate template = mailTemplateDao.readById(mail.getTemplateId());
			if ("SYSTEM_ALARM".equals(template.getTag())) { // NOSONAR
				Long alarmId = Long.valueOf((String) info.get("ALARMID"));

				Alarm alarm = alarmDao.readById(alarmId);

				if (!Util.isNullObject(alarm)) {
					mailContent = mailContent.replace("##ALARMCODE##", alarm.getCode());
					mailContent = mailContent.replace("##EXPLANATION##", alarm.getDescription());
					mailContent = mailContent.replace("##DETAILS##", alarm.getDetails());
				}

			} else if("NEW_FORTUNE".equals(template.getTag())){//Yeni fal talebi
				try {
					Long fortuneRequestId = Long.valueOf((String) info.get("REQ_ID"));
					FortuneRequest request = fortuneService.getFortuneRequestById(fortuneRequestId);
					VirtualCommenter commenter = customerService.getVirtualCommenter(request.getVirtualCommenterId());
					ResponseType responseType = customerService.getResponseType(request.getResponseTypeId());
					if(!Util.isNullObject(request)){
						mailContent = mailContent.replace("##RESPONSE_TYPE##", responseType.getDescription());
						mailContent = mailContent.replace("##PAID_CREDIT##", request.getPaidCredit().toString());
						mailContent = mailContent.replace("##VCOMMENTER_NAME##", commenter.getFullName());
					}
				} catch (NumberFormatException e) {					
					alarmService.addAlarm(AlarmType.CODE_BASED, "Mail pool id ->" + mail.getId()  , "Number format exception." + e.getMessage());
					logger.error("Mail pool id ->" + mail.getId(), e);
				} catch (ApiException e) {
					logger.error("Mail pool id ->" + mail.getId(), e);
				}
				
			}
		}

		mail.setMailContent(mailContent);
	}

}
