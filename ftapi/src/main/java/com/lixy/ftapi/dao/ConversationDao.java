package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.Conversation;

public interface ConversationDao extends GenericDao<Conversation, Long> {

	public List<Conversation> getConversationListByRequestIdForRoot(Long requestId);

	public List<Conversation> getConversationListByRequestId(Long requestId);
	
}
