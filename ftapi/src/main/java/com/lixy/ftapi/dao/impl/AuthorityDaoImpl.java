package com.lixy.ftapi.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.AuthorityDao;
import com.lixy.ftapi.domain.Authority;

@Repository
public class AuthorityDaoImpl extends GenericDaoHibernateImpl<Authority, Long> implements AuthorityDao{

	private static final long serialVersionUID = 4309809017459260212L;

	@Override
	public Authority readByName(String name) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Authority.class);
		criteria.add(Restrictions.eq("authorityName", name));
			
		return (Authority) criteria.getExecutableCriteria(getCurrentSession()).uniqueResult();
	}


}
