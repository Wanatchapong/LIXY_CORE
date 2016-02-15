package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.RequestTypeDao;
import com.lixy.ftapi.domain.RequestType;

@Repository
public class RequestTypeDaoImpl extends GenericDaoHibernateImpl<RequestType, Long> implements RequestTypeDao {

	private static final long serialVersionUID = 3493790068018663172L;


}
