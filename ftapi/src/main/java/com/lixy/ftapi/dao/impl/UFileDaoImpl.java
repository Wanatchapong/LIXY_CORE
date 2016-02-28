package com.lixy.ftapi.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.UFileDao;
import com.lixy.ftapi.domain.UFile;

@Repository("uFileDaoImpl")
public class UFileDaoImpl extends GenericDaoHibernateImpl<UFile, Long> implements UFileDao {

	private static final long serialVersionUID = -2145152684894774546L;

	@Override
	public UFile readFileByIdentifier(String identifier) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UFile.class);
		criteria.add(Restrictions.eq("fileIdentifier", identifier));
			
		return (UFile) criteria.getExecutableCriteria(getCurrentSession()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UFile> readFileByStatus(Long status) {
		DetachedCriteria criteria = DetachedCriteria.forClass(UFile.class);
		criteria.add(Restrictions.eq("status", status));
		
		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}


}
