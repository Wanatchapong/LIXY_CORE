package com.lixy.ftapi.model;

import java.util.List;

import com.lixy.ftapi.domain.Conversation;
import com.lixy.ftapi.domain.ConversationMessage;

public class ConversationModel {
	
	private Conversation conversation;
	private List<ConversationMessage> messages;
	
	public Conversation getConversation() {
		return conversation;
	}
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}
	public List<ConversationMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ConversationMessage> messages) {
		this.messages = messages;
	}
	
}
