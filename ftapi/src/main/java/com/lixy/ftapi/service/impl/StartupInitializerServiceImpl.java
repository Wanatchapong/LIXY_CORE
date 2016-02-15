package com.lixy.ftapi.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.service.StartupInitializerService;
import com.lixy.ftapi.service.UtilService;


@Service
public class StartupInitializerServiceImpl implements StartupInitializerService, ApplicationListener<ContextRefreshedEvent> {

	private static final long serialVersionUID = 3016594242531372643L;
	
	private transient Logger logger = LogManager.getLogger(getClass().getName());
	
	@Autowired
	private UtilService utilService;  
	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
		initialize();
	}

	private void initialize() {
		logger.info("********************************************************************************************");
		logger.info("Welcome to Lixy Lab's FT Api!");
		logger.info("********************************************************************************************");
		
		logger.info("Connection Check. EMERGENCY_PARAM_CHECK:" + utilService.getParameterValue("EMERGENCY_EXIST"));
		
		utilService.recacheAllProperties();
		
	}

}
