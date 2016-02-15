package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.EventLogDao;
import com.lixy.ftapi.domain.EventLog;

@Repository
public class EventLogDaoImpl extends GenericDaoHibernateImpl<EventLog, Long> implements EventLogDao {

	private static final long serialVersionUID = 3493790068018663172L;


}
