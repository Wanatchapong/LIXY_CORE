package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.VirtualCommenterPriceDao;
import com.lixy.ftapi.domain.VirtualCommenterPrice;
import com.lixy.ftapi.util.Util;

@Repository
public class VirtualCommenterPriceDaoImpl extends GenericDaoHibernateImpl<VirtualCommenterPrice, Long> implements VirtualCommenterPriceDao {

	private static final long serialVersionUID = 7939438487878479095L;

	@SuppressWarnings("unchecked")
	@Override
	public List<VirtualCommenterPrice> getVirtualCommenterPrices(Long vCommenterId, Long status) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VirtualCommenterPrice.class, "vcommprice");
		
		criteria.createAlias("vcommprice.responseType", "respType");
		criteria.add(Restrictions.eq("vcommenterId", vCommenterId));
		criteria.add(Restrictions.eq("respType.status", 0L));

		if (!Util.isNullObject(status)) {
			criteria.add(Restrictions.eq("status", status));
		}

		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}
	
	@Override
	public VirtualCommenterPrice getVirtualCommenterPriceByResponseType(Long vCommenterId, Long responseTypeId, Long status) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VirtualCommenterPrice.class, "vcommprice");
		
		criteria.createAlias("vcommprice.responseType", "respType");
		criteria.add(Restrictions.eq("vcommenterId", vCommenterId));
		criteria.add(Restrictions.eq("respType.status", 0L));
		criteria.add(Restrictions.eq("respType.id", responseTypeId));

		if (!Util.isNullObject(status)) {
			criteria.add(Restrictions.eq("status", status));
		}

		return (VirtualCommenterPrice) criteria.getExecutableCriteria(getCurrentSession()).uniqueResult();
	}

}
