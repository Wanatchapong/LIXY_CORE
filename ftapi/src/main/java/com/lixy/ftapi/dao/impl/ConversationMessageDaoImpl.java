package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.ConversationMessageDao;
import com.lixy.ftapi.domain.ConversationMessage;

@Repository
public class ConversationMessageDaoImpl extends GenericDaoHibernateImpl<ConversationMessage, Long> implements ConversationMessageDao {

	private static final long serialVersionUID = 3493790068018663172L;

	@Override
	public List<ConversationMessage> readByConversationIdForRoot(Long conversationId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ConversationMessage.class);
		criteria.add(Restrictions.eq("conversationId", conversationId));
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

	@Override
	public List<ConversationMessage> readByConversationId(Long conversationId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ConversationMessage.class);
		criteria.add(Restrictions.eq("conversationId", conversationId));
		criteria.add(Restrictions.eq("status", 0L));
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

}
