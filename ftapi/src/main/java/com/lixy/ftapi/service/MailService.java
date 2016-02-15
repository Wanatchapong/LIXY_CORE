package com.lixy.ftapi.service;

import java.util.Map;

import javax.mail.MessagingException;

import com.lixy.ftapi.domain.MailPool;
import com.lixy.ftapi.domain.MailTemplate;

public interface MailService {

	public void init();
	
	public void sendToQueue(MailPool poolRecord);

	public void send(Object identifier, String from, String to, String subject, String content) throws MessagingException;
	
	public void addToPool(Long status, Long templateId, String fromAddress, String toAddress, String subject, Map<String, String> additional);
	
	public MailTemplate readByTag(String tag);
}
