package com.lixy.ftapi.dao;

import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.type.UserStatus;

public interface UserDao extends GenericDao<User, Long> {
	
	public User readUserByUsername(String username, UserStatus status);

	public User readUserByProfileId(Long profileId, UserStatus status);

}
