package com.lixy.ftapi.model;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.type.AuthType;

public class UserAuthentication implements Authentication {
	private static final long serialVersionUID = 3399864372644089845L;
	private User user;
	private boolean authenticated;
	private Info info;
	private String ipAddress;
	private String token;
	private AuthType authType;

	public UserAuthentication(User user) {
		this.user = user;
		this.info = new Info();
	}
	
	@Override
	public String getName() {
		return user.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getAuthorities();
	}

	@Override
	public Object getCredentials() {
		return user.getPassword();
	}

	@Override
	public User getDetails() {
		return user;
	}

	@Override
	public Object getPrincipal() {
		return user.getUsername();
	}

	public Info getInfo() {
		return info;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException { // NOSONAR
		authenticated = isAuthenticated;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public AuthType getAuthType() {
		return authType;
	}

	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}
	
	
}
