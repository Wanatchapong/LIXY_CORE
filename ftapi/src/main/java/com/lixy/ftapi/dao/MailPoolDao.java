package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.MailPool;

public interface MailPoolDao extends GenericDao<MailPool, Long> {

	public List<MailPool> readPoolByStatus(long status);
	
	
}
