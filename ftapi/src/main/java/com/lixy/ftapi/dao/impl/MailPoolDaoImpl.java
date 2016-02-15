package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.MailPoolDao;
import com.lixy.ftapi.domain.MailPool;

@Repository
public class MailPoolDaoImpl extends GenericDaoHibernateImpl<MailPool, Long> implements MailPoolDao {

	private static final long serialVersionUID = 3493790068018663172L;

	
	@Override
	@SuppressWarnings("unchecked")
	public List<MailPool> readPoolByStatus(long status) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MailPool.class);
		criteria.add(Restrictions.eq("status", status));
		criteria.addOrder(Order.asc("id"));
		
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}


}
