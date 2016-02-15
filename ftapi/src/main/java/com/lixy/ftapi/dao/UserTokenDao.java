package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.UserToken;
import com.lixy.ftapi.type.TokenStatus;

public interface UserTokenDao extends GenericDao<UserToken, Long> {
	
	public int changeTokenStatus(String token, TokenStatus status);

	public UserToken getByToken(String token);
	
	public List<UserToken> getTokenToBeExpire(Long userId, int expireSeconds);

	public List<Long> getUserTokensByStatus(long swithStatus);

	public int deactivateAllTokensByUser(Long userId); 
	
}
