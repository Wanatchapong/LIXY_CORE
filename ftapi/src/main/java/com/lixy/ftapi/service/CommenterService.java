package com.lixy.ftapi.service;

import java.util.List;

import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.model.FortuneInfo;

public interface CommenterService {

	public List<FortuneRequest> getFortuneRequest(Long ownerId, String status, Long start, Long limit);
	
	public List<FortuneInfo> getFortuneRequestWithInfo(String status, Long start, Long limit);

	public List<FortuneInfo> getFortuneRequestWithInfo(Long ownerId, String status, Long start, Long limit);

}
