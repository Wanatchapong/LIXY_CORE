package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.ServerDao;
import com.lixy.ftapi.domain.Server;

@Repository
public class ServerDaoImpl extends GenericDaoHibernateImpl<Server, Long> implements ServerDao {

	private static final long serialVersionUID = -2145152684894774546L;


}
