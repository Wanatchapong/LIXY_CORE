package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.type.RequestStatusType;

public interface FortuneRequestDao extends GenericDao<FortuneRequest, Long> {

	public List<FortuneRequest> getFortuneRequestByUserId(Long userId, RequestStatusType status);
	
	public List<FortuneRequest> getFortuneRequest(String status, Long start, Long limit);

}
