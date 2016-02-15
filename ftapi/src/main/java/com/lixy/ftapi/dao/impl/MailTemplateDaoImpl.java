package com.lixy.ftapi.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.MailTemplateDao;
import com.lixy.ftapi.domain.MailTemplate;

@Repository
public class MailTemplateDaoImpl extends GenericDaoHibernateImpl<MailTemplate, Long> implements MailTemplateDao {

	private static final long serialVersionUID = 3493790068018663172L;

	@Override
	public MailTemplate readByTag(String tag) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MailTemplate.class);
		criteria.add(Restrictions.eq("tag", tag));
			
		return (MailTemplate) criteria.getExecutableCriteria(getCurrentSession()).uniqueResult();
	}
	

}
