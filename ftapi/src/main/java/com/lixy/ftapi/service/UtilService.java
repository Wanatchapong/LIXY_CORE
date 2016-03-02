package com.lixy.ftapi.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.lixy.ftapi.domain.Server;
import com.lixy.ftapi.domain.UFile;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.type.EventType;
import com.lixy.ftapi.type.FileFormatType;

public interface UtilService {

	public String getParameterValue(String tag);

	public void recacheAllProperties();

	public Long addEventLog(Long userId, EventType type, String description);

	public Long addEventLog(EventType type, String description);

	public String getMessage(String tag);
	
	public String getMessage(String tag, Locale locale);

	public String getTempFilePath(FileFormatType format);
	
	public String getTempFilePath(String format);
	
	public String getTempFilePath();
	
	public Long addUFile(UFile ufile);
	
	public UFile readFileById(Long id) throws ApiException;
	
	public UFile readFileByIdentifier(String identifier) throws ApiException;
	
	public List<UFile> readFileByStatus(Long status);

	public Locale getLocale();
	
	public Server getServerById(Long id) throws ApiException;
	
	public Map<String, String> getUFileInfo(String identifier) throws ApiException;
	
	public boolean isFortuneConversationVisibleToTheUser(Long requestId, User user) throws ApiException;

}
