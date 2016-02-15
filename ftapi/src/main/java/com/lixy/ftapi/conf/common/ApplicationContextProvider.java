package com.lixy.ftapi.conf.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {
	
	
	private static ApplicationContext ctx = null;  

	public static ApplicationContext getApplicationContext() {           
		return ctx;      
	}  

	@Override
	public void setApplicationContext(ApplicationContext context) { 
		ctx = context;  // NOSONAR
	}


}
