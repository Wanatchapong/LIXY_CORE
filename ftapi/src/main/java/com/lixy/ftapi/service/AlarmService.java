package com.lixy.ftapi.service;

import com.lixy.ftapi.domain.Alarm;
import com.lixy.ftapi.type.AlarmType;


public interface AlarmService {

	public Long addAlarm(Alarm alarm);
	
	public void addAlarm(String description, String details);
	
	public void addAlarm(AlarmType type, String description, String details);
	
	public void addAlarm(AlarmType type, String description, String details, String token);
}
