package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.ConversationMessageDao;
import com.lixy.ftapi.domain.ConversationMessage;

@Repository
public class ConversationMessageDaoImpl extends GenericDaoHibernateImpl<ConversationMessage, Long> implements ConversationMessageDao {

	private static final long serialVersionUID = 3493790068018663172L;


}
