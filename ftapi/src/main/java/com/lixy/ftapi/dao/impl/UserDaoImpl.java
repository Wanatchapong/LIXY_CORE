package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.UserDao;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.type.UserStatus;
import com.lixy.ftapi.util.Util;

@Repository
public class UserDaoImpl extends GenericDaoHibernateImpl<User, Long> implements UserDao {

	private static final long serialVersionUID = 3493790068018663172L;

	@Override
	public User readUserByUsername(String username, UserStatus status) {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		criteria.add(Restrictions.eq("username", username));
		
		if(!Util.isNullObject(status))
			criteria.add(Restrictions.eq("status", status.getUserStatus()));

		@SuppressWarnings("unchecked")
		List<User> parametersList = criteria.getExecutableCriteria(getCurrentSession()).list();

		if (Util.isNullOrEmptyList(parametersList)) {
			return null;
		}

		return parametersList.get(0);
	}

	@Override
	public User readUserByProfileId(Long profileId, UserStatus status) {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		criteria.add(Restrictions.eq("fbProfileId", profileId));
		
		if(!Util.isNullObject(status))
			criteria.add(Restrictions.eq("status", status.getUserStatus()));

		@SuppressWarnings("unchecked")
		List<User> parametersList = criteria.getExecutableCriteria(getCurrentSession()).list();

		if (Util.isNullOrEmptyList(parametersList)) {
			return null;
		}

		return parametersList.get(0);
	}

}
