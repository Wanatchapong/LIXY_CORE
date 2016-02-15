package com.lixy.ftapi.queue.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import com.lixy.ftapi.dao.MailPoolDao;
import com.lixy.ftapi.dao.MailTemplateDao;
import com.lixy.ftapi.domain.Alarm;
import com.lixy.ftapi.domain.MailPool;
import com.lixy.ftapi.domain.MailTemplate;
import com.lixy.ftapi.service.AlarmService;
import com.lixy.ftapi.type.SwitchType;
import com.lixy.ftapi.util.Util;

@Service("alarmListener")
public class AlarmListener implements MessageListener{
	private static Logger logger = LogManager.getLogger(AlarmListener.class);
	
	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	@Qualifier("mailTemplateDaoImpl")
	private MailTemplateDao mailTemplateDao;
	
	@Autowired
	@Qualifier("mailPoolDaoImpl")
	private MailPoolDao mailPoolDao;
	
	@Override
	@Transactional
	public void onMessage(Message message) {
		logger.info("ALARM Message Received ---> " + message);
		
		Object o = Util.fromByteArray(message.getBody());
		if(Util.isNullObject(o))
			return;			
		
		if(!(o instanceof Alarm))
			return;
		
		Alarm alarm = (Alarm) o;
		
		if(Util.isNullObject(alarm))
			return;
		
		try {
			alarm.setCode(Util.getUniqueId());
			Long alarmId = alarmService.addAlarm(alarm);
			
			logger.info("Alarm added." + alarmId + "-> code:" + alarm.getCode() );
			
			MailTemplate template = mailTemplateDao.readByTag("SYSTEM_ALARM");
			
			MailPool pool = new MailPool();
			pool.setStatus(SwitchType.ACTIVE.getSwithStatus());
			pool.setTemplateId(template.getId());
			pool.setFromAddress(template.getFromAddress());
			pool.setToAddress(Constant.ALARM_INFORM_LIST);
			pool.setSubject(template.getSubject());
			pool.setCreatedDate(new Date());
			pool.setIdentifier(UUID.randomUUID().toString());
			
			Map<String, String> additional = new HashMap<>();
			additional.put("ALARMID", ""+alarm.getId());
			
			ObjectMapper mapper = new ObjectMapper();
			pool.setAdditionalInfo(mapper.writeValueAsString(additional));
			
			mailPoolDao.create(pool);	
			
		} catch (Exception e) {
			logger.error("error when inserting alarm.", e);
		}
		
	}

}
