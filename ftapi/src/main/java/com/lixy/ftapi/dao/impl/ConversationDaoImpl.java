package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.ConversationDao;
import com.lixy.ftapi.domain.Conversation;

@Repository
public class ConversationDaoImpl extends GenericDaoHibernateImpl<Conversation, Long> implements ConversationDao {

	private static final long serialVersionUID = 3493790068018663172L;

	@SuppressWarnings("unchecked")
	@Override
	public List<Conversation> getConversationListByRequestIdForRoot(Long requestId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Conversation.class);
		criteria.add(Restrictions.eq("fortuneRequest.id", requestId));
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Conversation> getConversationListByRequestId(Long requestId) {
		String[] readableStatus = {"O", "C"}; 
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Conversation.class);
		criteria.add(Restrictions.eq("fortuneRequest.id", requestId));
		criteria.add(Restrictions.in("status", readableStatus));
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}


}
