package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.ConversationMessageDetailDao;
import com.lixy.ftapi.domain.ConversationMessageDetail;

@Repository
public class ConversationMessageDetailDaoImpl extends GenericDaoHibernateImpl<ConversationMessageDetail, Long> implements ConversationMessageDetailDao {

	private static final long serialVersionUID = 3493790068018663172L;


}
