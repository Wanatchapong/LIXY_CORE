package com.lixy.ftapi.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.Info;
import com.lixy.ftapi.type.AuthorityType;
import com.lixy.ftapi.type.UserStatus;

public interface UserService extends UserDetailsService {

	public Info register(String username, String password, Long profileId, String accessToken, String name, String surname, String ip);
	
	public User  getUserById(Long id) throws ApiException;
	
	public User  getUserByProfileId(Long profileId) throws ApiException;
	
	public void checkUserSuitableForProcess(Long userId) throws ApiException;

	public void changeUserStatus(Long userId, UserStatus status);

	public void changeUserStatus(User user, UserStatus status);
	
	public Long addUserAuthority(User user, AuthorityType authorityType);

	public Long makeUserAsCommenter(Long userId, String token) throws ApiException;

	public void updateUser(User user);
}
