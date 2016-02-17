package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.FortuneRequestDao;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.type.RequestStatusType;
import com.lixy.ftapi.util.Util;

@Repository
public class FortuneRequestDaoImpl extends GenericDaoHibernateImpl<FortuneRequest, Long> implements FortuneRequestDao {

	private static final long serialVersionUID = 7939438487878479095L;

	@SuppressWarnings("unchecked")
	@Override
	public List<FortuneRequest> getFortuneRequestByUserId(Long userId, RequestStatusType status) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FortuneRequest.class);
		criteria.add(Restrictions.eq("requesterId", userId));
		
		if(!Util.isNullObject(status)){
			criteria.add(Restrictions.eq("requestStatus", status.getShortCode()));	
		}
		
		return criteria.getExecutableCriteria(getCurrentSession()).setMaxResults(Constant.MAXIMUM_FORTUNE_REQUEST_RESULT).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FortuneRequest> getFortuneRequest(String status, Long start, Long limit) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FortuneRequest.class);
		
		if(!Util.isNullObject(status)){
			criteria.add(Restrictions.eq("requestStatus", status));	
		}
		
		Criteria executableCriteria = criteria.getExecutableCriteria(getCurrentSession());
		
		if(start != null)
			executableCriteria.setFirstResult(start.intValue());
		
		if(limit != null)
			executableCriteria.setMaxResults(limit.intValue());
		
		return executableCriteria.list();
	}



}
