package com.lixy.ftapi.dao;

import com.lixy.ftapi.domain.MailTemplate;

public interface MailTemplateDao extends GenericDao<MailTemplate, Long> {
	
	public MailTemplate readByTag(String tag);
	
}
