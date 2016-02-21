package com.lixy.ftapi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.dao.FortuneRequestDao;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.model.FortuneInfo;
import com.lixy.ftapi.service.CommenterService;
import com.lixy.ftapi.service.FortuneService;

@Service("commenterService")
@Transactional
public class CommenterServiceImpl implements CommenterService{
	
	@Autowired
	private FortuneService fortuneService;

	@Autowired
	@Qualifier("fortuneRequestDaoImpl")
	private FortuneRequestDao fortuneRequestDao;

	@Override
	public List<FortuneRequest> getFortuneRequest(Long ownerId, String status, Long start, Long limit) {
		return fortuneRequestDao.getFortuneRequest(ownerId, status, start, limit);
	}

	@Override
	public List<FortuneInfo> getFortuneRequestWithInfo(String status, Long start, Long limit) {
		return fortuneService.convertAllToFortuneInfo(getFortuneRequest(null ,status, start, limit));
	}

	@Override
	public List<FortuneInfo> getFortuneRequestWithInfo(Long ownerId, String status, Long start, Long limit) {
		return fortuneService.convertAllToFortuneInfo(getFortuneRequest(ownerId, status, start, limit));
	}
	
	
	
}
