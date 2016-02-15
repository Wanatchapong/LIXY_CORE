package com.lixy.ftapi.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.GenericDao;
import com.lixy.ftapi.domain.BaseEntity;
import com.lixy.ftapi.util.ServerUtils;
import com.lixy.ftapi.util.Util;

public class GenericDaoHibernateImpl<T, PK extends Serializable> implements GenericDao<T, PK> { // NOSONAR

	private static final long serialVersionUID = 1L;
	private Class<T> entityClass;

	private Logger logger = LogManager.getLogger(GenericDaoHibernateImpl.class.getName());

	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public GenericDaoHibernateImpl() {
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PK create(T o) {
		try {
			getCurrentSession().flush();
			setMachine(o, 1);

			PK returnedClass = (PK) getCurrentSession().save(o);

			getCurrentSession().flush();
			return (PK) returnedClass;
		} catch (Exception exception) {
			logger.error("Hata alindi ", exception);
			getCurrentSession().clear();
			throw exception;
		}

	}

	@Override
	public T readById(PK id) {
		return (T) getCurrentSession().get(entityClass, id);
	}

	@Override
	public void update(T o) {
		try {
			getCurrentSession().flush();
			setMachine(o, 0);

			getCurrentSession().update(o);
			getCurrentSession().flush();
		} catch (Exception exception) {
			logger.error("Hata alindi ", exception);
		}

	}

	@Override
	public void delete(T o) {
		getCurrentSession().delete(o);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> loadAll() {
		return getCurrentSession().createQuery("FROM " + entityClass.getName() + " ORDER BY 1 ASC").list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> loadAllWithFetchColumns(List<String> fetchList) {
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);

		for (String fetchColumn : fetchList) {
			criteria.setFetchMode(fetchColumn, FetchMode.JOIN);
		}

		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> loadAllByOperatorWithFetchColumns(List<String> fetchList) {

		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);

		for (String fetchColumn : fetchList) {
			criteria.setFetchMode(fetchColumn, FetchMode.JOIN);
		}

		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

	@Override
	public void merge(T o) {
		getCurrentSession().merge(o);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BigInteger> getIdsDistinctList(String entityIdName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);
		criteria.setProjection(Projections.distinct(Projections.property(entityIdName)));
		criteria.addOrder(Order.asc(entityIdName));

		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BigInteger> getActiveIdsDistinctList(String entityIdName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);
		criteria.setProjection(Projections.distinct(Projections.property(entityIdName)));
		criteria.add(Restrictions.eq("status", "A"));
		criteria.addOrder(Order.asc(entityIdName));

		return criteria.getExecutableCriteria(getCurrentSession()).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Long getMaxId(String entityIdName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);
		criteria.setProjection(Projections.distinct(Projections.property(entityIdName)));
		criteria.setProjection(Projections.projectionList().add(Projections.max(entityIdName)));

		List<Long> maxIdList = criteria.getExecutableCriteria(getCurrentSession()).list();

		if (maxIdList == null)
			return null;
		else if (!Util.isNullObject(maxIdList) && !maxIdList.isEmpty())
			return maxIdList.get(0);
		else
			return 0L;
	}


	private void setMachine(T o, int opertype) { // 1 create 0 other
		try {
			Map<String, String> serverInfo = ServerUtils.getServerInformation();
			
			if(o instanceof BaseEntity){
				if (opertype == 1){
					((BaseEntity) o).setCreatedMachine(serverInfo.get("SERVER_ADDR") + ":" + serverInfo.get("SERVER_HOST"));
					((BaseEntity) o).setCreatedDate(new Date());
					((BaseEntity) o).setCreatedBy(Constant.CREATED_BY);
				}
				else{
					((BaseEntity) o).setModifiedMachine(serverInfo.get("SERVER_ADDR") + ":" + serverInfo.get("SERVER_HOST"));
					((BaseEntity) o).setModifiedDate(new Date());
					((BaseEntity) o).setModifiedBy(Constant.CREATED_BY);
				}
			}
		} catch (Exception e) {
			logger.warn("GenericDaoHibernateImpl.setMachine wrn ", e);
		}

		return;
	}

	@Override
	public void createOrUpdateAll(final List<T> entityList) {
		getCurrentSession().saveOrUpdate(entityList);
	}
}
