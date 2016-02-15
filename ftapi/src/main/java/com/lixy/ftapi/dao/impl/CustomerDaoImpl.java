package com.lixy.ftapi.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.CustomerDao;
import com.lixy.ftapi.domain.Customer;

@Repository
public class CustomerDaoImpl extends GenericDaoHibernateImpl<Customer, Long> implements CustomerDao {

	private static final long serialVersionUID = -2145152684894774546L;

	@Override
	public Customer getCustomerByUserId(Long userId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Customer.class);
		criteria.add(Restrictions.eq("userId", userId));
		
		return (Customer) criteria.getExecutableCriteria(getCurrentSession()).uniqueResult();
	}


}
