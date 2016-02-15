package com.lixy.ftapi.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.UserTokenDao;
import com.lixy.ftapi.domain.UserToken;
import com.lixy.ftapi.type.TokenStatus;
import com.lixy.ftapi.util.DateUtils;

@Repository
public class UserTokenDaoImpl extends GenericDaoHibernateImpl<UserToken, Long> implements UserTokenDao {

	private static final long serialVersionUID = 120749441897598133L;

	@Override
	public int changeTokenStatus(String token, TokenStatus status) {
		Query query = getCurrentSession().createQuery("update UserToken set status = :status" + " where token = :token");
		query.setParameter("token", token);
		query.setParameter("status", status.getTokenStatus()); // NOSONAR
		
		return query.executeUpdate();
	}

	@Override
	public UserToken getByToken(String token) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UserToken.class);
		criteria.add(Restrictions.eq("token", token));
			
		return (UserToken) criteria.getExecutableCriteria(getCurrentSession()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserToken> getTokenToBeExpire(Long userId, int expireSeconds) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UserToken.class);
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("status", 0L));
		criteria.add(Restrictions.le("createdDate", DateUtils.substractTime(new Date(), Calendar.SECOND, expireSeconds)));
		
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getUserTokensByStatus(long switchStatus) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UserToken.class);
		criteria.add(Restrictions.eq("status", switchStatus));
		criteria.setProjection( Projections.distinct( Projections.property( "user.id" ) ) );
		
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

	@Override
	public int deactivateAllTokensByUser(Long userId) {
		Query query = getCurrentSession().createQuery("update UserToken set status = 1 where user.id = :userId");
		query.setParameter("userId", userId);
		return query.executeUpdate();
	}


}
