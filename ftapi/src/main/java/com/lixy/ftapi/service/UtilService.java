package com.lixy.ftapi.service;

import java.util.Locale;

import com.lixy.ftapi.domain.UFile;
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

	public Locale getLocale();

}
