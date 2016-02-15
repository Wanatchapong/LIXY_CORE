package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.AlarmDao;
import com.lixy.ftapi.domain.Alarm;

@Repository
public class AlarmDaoImpl extends GenericDaoHibernateImpl<Alarm, Long> implements AlarmDao {

	private static final long serialVersionUID = -2145152684894774546L;



}
