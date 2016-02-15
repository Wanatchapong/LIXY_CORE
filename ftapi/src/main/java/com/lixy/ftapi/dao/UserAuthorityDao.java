package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.UserAuthority;
import com.lixy.ftapi.type.AuthorityType;

public interface UserAuthorityDao extends GenericDao<UserAuthority, Long> {
	
	public List<UserAuthority> getUserAuthoritiesByUserId(Long userId); 
	
	public boolean hasAuthorityByUser(Long userId, AuthorityType authority);

	public UserAuthority getAuthorityByUser(Long userId, AuthorityType authority);
	
}
