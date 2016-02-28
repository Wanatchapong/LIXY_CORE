package com.lixy.ftapi.dao;

import java.util.List;

import com.lixy.ftapi.domain.UFile;

public interface UFileDao extends GenericDao<UFile, Long> {
	
	public UFile readFileByIdentifier(String identifier);

	public List<UFile> readFileByStatus(Long status);
	
}
