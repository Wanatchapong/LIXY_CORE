package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.UserAuthorityDao;
import com.lixy.ftapi.domain.UserAuthority;
import com.lixy.ftapi.type.AuthorityType;

@Repository
public class UserAuthorityDaoImpl extends GenericDaoHibernateImpl<UserAuthority, Long> implements UserAuthorityDao {

	private static final long serialVersionUID = -7467573311096580148L;

	@Override
	@SuppressWarnings("unchecked")
	public List<UserAuthority> getUserAuthoritiesByUserId(Long userId ) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UserAuthority.class);
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("status", 0L));

		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

	@Override
	public boolean hasAuthorityByUser(Long userId, AuthorityType authority) {
		List<UserAuthority> authorities = getUserAuthoritiesByUserId(userId);
		boolean hasAuthority = false;
		
		for (UserAuthority userAuthority : authorities) {
			if (authority.toString().equals(userAuthority.getAuthority().getAuthorityName())) {
				hasAuthority = true;
				break;
			}
		}
		
		return hasAuthority;
	}
	
	@Override
	public UserAuthority getAuthorityByUser(Long userId, AuthorityType authority) {
		List<UserAuthority> authorities = getUserAuthoritiesByUserId(userId);
		
		for (UserAuthority userAuthority : authorities) {
			if (authority.toString().equals(userAuthority.getAuthority().getAuthorityName())) {
				return userAuthority;
			}
		}
		
		return null;
	}

}
