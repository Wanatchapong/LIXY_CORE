package com.lixy.ftapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.lixy.ftapi.dao.VirtualCommenterDao;
import com.lixy.ftapi.domain.VirtualCommenter;

@Repository
public class VirtualCommenterDaoImpl extends GenericDaoHibernateImpl<VirtualCommenter, Long> implements VirtualCommenterDao {

	private static final long serialVersionUID = 7939438487878479095L;



}
