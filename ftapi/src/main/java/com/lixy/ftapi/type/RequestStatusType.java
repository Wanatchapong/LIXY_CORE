package com.lixy.ftapi.type;

public enum RequestStatusType {
	INITIAL("I"), 
	APPROVED("A"), 
	OPEN("O"),
	SOLUTION("S"),
	REOPEN("RO"),
	ERROR("E"),
	REJECT("RE")	
	; 
	
	private String shortCode;
	
	private RequestStatusType(String code){
		this.setShortCode(code);
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	
	
}
