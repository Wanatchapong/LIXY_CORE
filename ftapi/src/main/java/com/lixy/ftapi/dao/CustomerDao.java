package com.lixy.ftapi.dao;

import com.lixy.ftapi.domain.Customer;

public interface CustomerDao extends GenericDao<Customer, Long> {

	public Customer getCustomerByUserId(Long userId);
	
}
