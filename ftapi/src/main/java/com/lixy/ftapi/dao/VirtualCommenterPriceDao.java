package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.VirtualCommenterPrice;

public interface VirtualCommenterPriceDao extends GenericDao<VirtualCommenterPrice, Long> {
	
	public List<VirtualCommenterPrice> getVirtualCommenterPrices(Long vCommenterId, Long status);

	public VirtualCommenterPrice getVirtualCommenterPriceByResponseType(Long vCommenterId, Long responseTypeId, Long status);
	
}
