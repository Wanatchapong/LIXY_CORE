package com.lixy.ftapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.dao.AlarmDao;
import com.lixy.ftapi.domain.Alarm;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.service.AlarmService;
import com.lixy.ftapi.type.AlarmType;


@Service("alarmService")
@Transactional
public class AlarmServiceImpl implements AlarmService {

	@Autowired
	@Qualifier("alarmDaoImpl")
	private AlarmDao alarmDao;
	
	@Autowired
	private QueueManager queueManager;
	
	@Override
	public Long addAlarm(Alarm alarm) {
		return alarmDao.create(alarm);
	}

	@Override
	public void addAlarm(String description, String details) {
		addAlarm(AlarmType.GENERAL, description, details);
	}

	@Override
	public void addAlarm(AlarmType type, String description, String details) {
		addAlarm(type, description, details, null);
	}

	@Override
	public void addAlarm(AlarmType type, String description, String details, String token) {
		Alarm alarm = new Alarm();
		alarm.setType(type.toString());
		alarm.setDescription(description);
		alarm.setDetails(details);
		alarm.setToken(token);
		
		queueManager.sendToAlarmQueue(alarm);
	}


}
