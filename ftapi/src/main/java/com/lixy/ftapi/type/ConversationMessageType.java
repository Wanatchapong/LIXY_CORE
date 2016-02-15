package com.lixy.ftapi.type;

public enum ConversationMessageType {
	TEXT(1L),
	VOICE(2L),
	VIDEO(3L);
	
	private long messageTypeId;
	
	private ConversationMessageType(long typeId){
		this.messageTypeId = typeId;
	}

	public long getMessageTypeId() {
		return messageTypeId;
	}

	public void setMessageTypeId(long messageTypeId) {
		this.messageTypeId = messageTypeId;
	}
	
	
}
