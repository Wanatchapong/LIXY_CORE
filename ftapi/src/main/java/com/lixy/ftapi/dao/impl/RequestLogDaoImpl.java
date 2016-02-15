package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.RequestLogDao;
import com.lixy.ftapi.domain.HttpLog;

@Repository
public class RequestLogDaoImpl extends GenericDaoHibernateImpl<HttpLog, Long> implements RequestLogDao {

	private static final long serialVersionUID = 3493790068018663172L;


}
