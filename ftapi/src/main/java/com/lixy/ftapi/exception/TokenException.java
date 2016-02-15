package com.lixy.ftapi.exception;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class TokenException extends Exception{
	private static final long serialVersionUID = -3073888833289428868L;
	
	private final String exceptionId;
	private final String token;
	private final String description;
	private final Throwable t;
	
	public TokenException(String description){
		super(description);
		this.description = description;
		this.exceptionId = UUID.randomUUID().toString();
		this.t = null;
		this.token = null;
	}
	
	public TokenException(String description, Throwable t){
		super(description, t);
		this.description = description;
		this.exceptionId = UUID.randomUUID().toString();
		this.t = t;
		this.token = null;
	}
	
	public TokenException(String token, String description, Throwable t){
		super(description, t);
		this.description = description;
		this.exceptionId = UUID.randomUUID().toString();
		this.t = t;
		this.token = token;
	}
	
	public String getExceptionId() {
		return exceptionId;
	}
	public String getToken() {
		return token;
	}
	public String getDescription() {
		return description;
	}
	public Throwable getT() {
		return t;
	}

	@Override
	public String toString() {
		return "TokenException [token=" + token + ", description=" + description + "]";
	}
	
}
