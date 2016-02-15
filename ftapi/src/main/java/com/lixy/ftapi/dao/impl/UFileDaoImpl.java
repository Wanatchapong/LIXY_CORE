package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.UFileDao;
import com.lixy.ftapi.domain.UFile;

@Repository("uFileDaoImpl")
public class UFileDaoImpl extends GenericDaoHibernateImpl<UFile, Long> implements UFileDao {

	private static final long serialVersionUID = -2145152684894774546L;


}
