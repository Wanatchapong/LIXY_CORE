package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.FortuneRequestDetail;

public interface FortuneRequestDetailDao extends GenericDao<FortuneRequestDetail, Long> {

	public List<FortuneRequestDetail> getFortuneRequestDetailsByRequestId(Long requestId);
	
	
}
