package com.lixy.ftapi.type;

public enum ConversationStatusType {
	OPEN("O"),	
	CLOSED("C");
	
	private String shortCode;
	
	private ConversationStatusType(String code){
		this.setShortCode(code);
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	
	
}
