package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.ParameterDao;
import com.lixy.ftapi.domain.Parameter;
import com.lixy.ftapi.util.Util;

@Repository
public class ParameterDaoImpl extends GenericDaoHibernateImpl<Parameter, Long> implements ParameterDao{

	private static final long serialVersionUID = -7080596859675239853L;
	
	@Override
	public String getParameterValue(String tag) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Parameter.class);
		criteria.add(Restrictions.eq("tag",tag));
			
		@SuppressWarnings("unchecked")
		List<Parameter> parametersList = criteria.getExecutableCriteria(getCurrentSession()).list();
		
		if ( Util.isNullOrEmptyList(parametersList) ) {
			return null;
		}
		
		return parametersList.get(0).getValue();
	}

}
