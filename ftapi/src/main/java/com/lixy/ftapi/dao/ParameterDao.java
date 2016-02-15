package com.lixy.ftapi.dao;

import com.lixy.ftapi.domain.Parameter;

public interface ParameterDao extends GenericDao<Parameter, Long> {
	
	public String getParameterValue(String tag);

}
