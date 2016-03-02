package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.ConversationMessage;

public interface ConversationMessageDao extends GenericDao<ConversationMessage, Long> {

	public List<ConversationMessage> readByConversationId(Long conversationId);

	public List<ConversationMessage> readByConversationIdForRoot(Long conversationId);
	
}
