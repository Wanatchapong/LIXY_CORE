package com.lixy.ftapi.dao;

import com.lixy.ftapi.domain.Authority;

public interface AuthorityDao extends GenericDao<Authority, Long> {

	public Authority readByName(String name);
	
}
