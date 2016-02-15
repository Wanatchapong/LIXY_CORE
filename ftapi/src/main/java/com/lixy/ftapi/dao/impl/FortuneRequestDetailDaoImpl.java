package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.FortuneRequestDetailDao;
import com.lixy.ftapi.domain.FortuneRequestDetail;

@Repository
public class FortuneRequestDetailDaoImpl extends GenericDaoHibernateImpl<FortuneRequestDetail, Long> implements FortuneRequestDetailDao {

	private static final long serialVersionUID = 7939438487878479095L;

	@SuppressWarnings("unchecked")
	@Override
	public List<FortuneRequestDetail> getFortuneRequestDetailsByRequestId(Long requestId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FortuneRequestDetail.class);
		criteria.add(Restrictions.eq("requestId", requestId));
		
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

}
