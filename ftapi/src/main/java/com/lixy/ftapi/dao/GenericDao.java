package com.lixy.ftapi.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public interface GenericDao <T, PK extends Serializable> extends Serializable { // NOSONAR
	
	public PK create(T newInstance);

	public T readById(PK id);

	public void update(T transientObject);
    
	public void delete(T persistentObject);
    
	public List<T> loadAll();

	public void merge(T entity);
        
	public List<BigInteger> getIdsDistinctList(String entityIdName);
	
	public List<BigInteger> getActiveIdsDistinctList(String entityIdName);

	public Long getMaxId(String entityIdName);

	public List<T> loadAllByOperatorWithFetchColumns(List<String> fetchList);

	public List<T> loadAllWithFetchColumns(List<String> fetchList);

	public void createOrUpdateAll(List<T> entityList);

    
}
