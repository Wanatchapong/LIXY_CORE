package com.lixy.ftapi.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.dao.FortuneRequestDao;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.FortuneInfo;
import com.lixy.ftapi.service.CommenterService;
import com.lixy.ftapi.service.FortuneService;
import com.lixy.ftapi.util.Util;

@Service("commenterService")
@Transactional
public class CommenterServiceImpl implements CommenterService{
	
	private static Logger logger = LogManager.getLogger(CommenterServiceImpl.class);
	
	@Autowired
	private FortuneService fortuneService;

	@Autowired
	@Qualifier("fortuneRequestDaoImpl")
	private FortuneRequestDao fortuneRequestDao;

	@Override
	public List<FortuneRequest> getFortuneRequest(String status, Long start, Long limit) {
		return fortuneRequestDao.getFortuneRequest(status, start, limit);
	}

	@Override
	public List<FortuneInfo> getFortuneRequestWithInfo(String status, Long start, Long limit) {
		List<FortuneInfo> info = new ArrayList<>();
		
		for (FortuneRequest request : getFortuneRequest(status, start, limit)) {
			try {
				info.add(fortuneService.convertToRequestModel(request));
			} catch (ApiException e) {
				//DO NOTHING PASS THIS REC
				logger.error("getFortuneRequestWithInfo err." + Util.getInputLogsSimple("status, start, limit", status, start, limit) , e);
			}
		}
		
		return info;
	}
	
	
	
}
