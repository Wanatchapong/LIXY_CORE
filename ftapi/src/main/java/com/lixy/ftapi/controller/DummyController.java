package com.lixy.ftapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.lixy.ftapi.domain.Alarm;
import com.lixy.ftapi.queue.manager.QueueManager;

@RestController
@RequestMapping("/v1/dummy")
public class DummyController {
	
	@Autowired
	private QueueManager queueManager;
	
	@RequestMapping(method = RequestMethod.GET, value = "/check")
	public String checkApi() {
		return "Hey Client! This is Lixy Lab's FT Api!";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/alarm")
	public void alarm() {
		Alarm alarm = new Alarm();
		alarm.setDescription("description deneme");
		alarm.setDetails("details deneme");
		alarm.setType("ABC");
		
		queueManager.sendToAlarmQueue(alarm);
		
	}
	
	@RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
	public ModelAndView hello(@PathVariable("name") String name) {

		ModelAndView model = new ModelAndView();
		model.setViewName("hello");
		model.addObject("name", name);

		return model;

	}

	
}
