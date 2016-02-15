package com.lixy.ftapi.type;

public enum AuthType {
	BASIC("B"),
	FACEBOOK("F");
	
	private String loginType;
	
	private AuthType(String lType){
		this.setLoginType(lType);
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	
	
}
