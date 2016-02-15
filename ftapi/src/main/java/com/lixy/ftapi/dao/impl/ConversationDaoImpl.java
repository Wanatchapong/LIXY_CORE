package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.ConversationDao;
import com.lixy.ftapi.domain.Conversation;

@Repository
public class ConversationDaoImpl extends GenericDaoHibernateImpl<Conversation, Long> implements ConversationDao {

	private static final long serialVersionUID = 3493790068018663172L;


}
