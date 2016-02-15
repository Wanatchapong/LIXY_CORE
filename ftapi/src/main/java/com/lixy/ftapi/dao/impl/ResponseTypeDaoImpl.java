package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.ResponseTypeDao;
import com.lixy.ftapi.domain.ResponseType;

@Repository
public class ResponseTypeDaoImpl extends GenericDaoHibernateImpl<ResponseType, Long> implements ResponseTypeDao {

	private static final long serialVersionUID = 3493790068018663172L;


}
