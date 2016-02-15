package com.lixy.ftapi.service;

import java.io.Serializable;

import org.springframework.context.event.ContextRefreshedEvent;

@FunctionalInterface
public interface StartupInitializerService extends Serializable { 

    public void onApplicationEvent(ContextRefreshedEvent event);
	
}
