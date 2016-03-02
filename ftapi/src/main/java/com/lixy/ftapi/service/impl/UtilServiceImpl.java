package com.lixy.ftapi.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.AlarmDao;
import com.lixy.ftapi.dao.EventLogDao;
import com.lixy.ftapi.dao.MailPoolDao;
import com.lixy.ftapi.dao.ParameterDao;
import com.lixy.ftapi.dao.ServerDao;
import com.lixy.ftapi.dao.UFileDao;
import com.lixy.ftapi.domain.EventLog;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.domain.Server;
import com.lixy.ftapi.domain.UFile;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.service.FortuneService;
import com.lixy.ftapi.service.MailService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.EventType;
import com.lixy.ftapi.type.FileFormatType;
import com.lixy.ftapi.util.CacheUtil;
import com.lixy.ftapi.util.CryptoUtils;
import com.lixy.ftapi.util.FacebookUtil;
import com.lixy.ftapi.util.Util;

@Service("utilService")
@Transactional
public class UtilServiceImpl implements UtilService {

	private static Logger logger = LogManager.getLogger(UtilServiceImpl.class);

	@Autowired
	@Qualifier("parameterDaoImpl")
	private ParameterDao parameterDao;

	@Autowired
	@Qualifier("eventLogDaoImpl")
	private EventLogDao eventLogDao;

	@Autowired
	@Qualifier("mailService")
	private MailService mailService;

	@Autowired
	@Qualifier("mailPoolDaoImpl")
	private MailPoolDao mailPoolDao;

	@Autowired
	@Qualifier("alarmDaoImpl")
	private AlarmDao alarmDao;
	
	@Autowired
	@Qualifier("serverDaoImpl")
	private ServerDao serverDao;

	@Autowired
	@Qualifier("uFileDaoImpl")
	private UFileDao uFileDao;
	
	@Autowired
	private FortuneService fortuneService;

	@Autowired
	private MessageSource messageSource;

	@Override
	public String getParameterValue(String tag) {
		return parameterDao.getParameterValue(tag);
	}

	@Override
	public void recacheAllProperties() {

		try {
			logger.info("CRYPTO INIT START");
			CryptoUtils.init();
			logger.info("CRYPTO INIT END");
		} catch (Exception ex) {
			logger.error(ex);
			logger.info("CRYPTO INIT START FAIL");
		}

		try {
			logger.info("MAIL_SERVICE START");
			Constant.GMAIL_ADDR = CryptoUtils.decrypt(getParameterValue("GMAIL_SENDER")); // NOSONAR
			Constant.GMAIL_PASS = CryptoUtils.decrypt(getParameterValue("GMAIL_SENDER_PASS")); // NOSONAR
			Constant.GMAIL_USER = CryptoUtils.decrypt(getParameterValue("GMAIL_SENDER_USER")); // NOSONAR
			mailService.init();
			logger.info("MAIL_SERVICE END");
		} catch (NumberFormatException e) {
			logger.info("MAIL_SERVICE FAIL");
		}

		try {
			logger.info("ALARM INFORM RETRIEVE START");
			Constant.ALARM_INFORM_LIST = getParameterValue("ALARM_INFORM_LIST"); // NOSONAR
			logger.info("ALARM INFORM RETRIEVE END");
		} catch (NumberFormatException e) {
			logger.info("ALARM INFORM RETRIEVE FAIL");
		}

		try {
			logger.info("CACHE_TIMEOUT_IN_SEC START");
			Constant.CACHE_TIMEOUT_IN_SEC = Integer.valueOf(getParameterValue("CACHE_TIMEOUT_IN_SEC")); // NOSONAR
			logger.info("CACHE_TIMEOUT_IN_SEC END");
		} catch (NumberFormatException e) {
			Constant.CACHE_TIMEOUT_IN_SEC = 3600; // NOSONAR
			logger.info("CACHE_TIMEOUT_IN_SEC FAIL");
		}

		try {
			logger.info("CacheUtil initialize start");
			CacheUtil.cleanCache();
			CacheUtil.invalidateAllCache();
			CacheUtil.init();
			logger.info("CacheUtil initialize stop");
		} catch (Exception ex) {
			logger.error("CacheUtil initialize fail", ex);
		}

		try {
			logger.info("DB_REQUEST_LOG_ACTIVE initialize start");
			Constant.DB_REQUEST_LOG_ACTIVE = getParameterValue("DB_REQUEST_LOG_ACTIVE"); // NOSONAR
			Constant.ROOT_FILE_PATH = getParameterValue("ROOT_TEMP_FILE_PATH");
		} catch (Exception ex) {
			logger.error("DB_REQUEST_LOG_ACTIVE initialize fail", ex);
		}

		try {
			logger.info("FTAPI UPLOAD initialize start");
			Constant.UPLOAD_SERVER_ID = Long.valueOf(getParameterValue("UPLOAD_SERVER_ID")); 
		} catch (Exception ex) {
			logger.error("FTAPI UPLOAD initialize fail", ex);
		}
		
		FacebookUtil.APP_ID = CryptoUtils.decrypt(getParameterValue("FACEBOOK_APP_ID"));
		FacebookUtil.APP_SECRET = CryptoUtils.decrypt(getParameterValue("FACEBOOK_APP_SECRET"));

		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Void> task = new Callable<Void>() {
			public Void call() {
				try {
					logger.info("Lixy App FB initialize start");
					FacebookUtil.init();
				} catch (Exception ex) {
					logger.error("Lixy App FB initialize fail", ex);
				}
				return null;
			}
		};
		Future<Void> future = executor.submit(task);
		try {
			future.get(5, TimeUnit.SECONDS);
		} catch (TimeoutException ex) {
			logger.error("Timeout when fb init", ex);
		} catch (InterruptedException e) {
			logger.error("InterruptedException when fb init", e);
		} catch (ExecutionException e) {
			logger.error("ExecutionException when fb init", e);
		} finally {
			future.cancel(true); // may or may not desire this
		}

	}

	@Override
	public Long addEventLog(Long userId, EventType type, String description) {
		EventLog eventLog = new EventLog();
		eventLog.setUserId(userId);
		eventLog.setType(type.toString());
		eventLog.setDescription(description);

		return eventLogDao.create(eventLog);
	}

	@Override
	public Long addEventLog(EventType type, String description) {
		return addEventLog(null, type, description);
	}

	@Override
	public String getMessage(String tag) {
		Locale locale = getLocale();
		return getMessage(tag, locale);
	}

	@Override
	public String getMessage(String tag, Locale locale) {
		try {
			return messageSource.getMessage(tag, null, locale);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	@Override
	public String getTempFilePath(FileFormatType format) {
		return System.getProperty("catalina.base") + Constant.ROOT_FILE_PATH + File.pathSeparator
				+ Util.getSimpleUniqueId() + format.getFormat();
	}

	@Override
	public String getTempFilePath(String file) {
		return System.getProperty("catalina.base") + File.separator + Constant.ROOT_FILE_PATH + File.separator + file;
	}

	@Override
	public String getTempFilePath() {
		return System.getProperty("catalina.base") + File.separator + Constant.ROOT_FILE_PATH + File.separator
				+ Util.getSimpleUniqueId();
	}

	@Override
	public Long addUFile(UFile ufile) {
		return uFileDao.create(ufile);
	}

	@Override
	public Server getServerById(Long id) throws ApiException {
		Server server = serverDao.readById(id);
		if(Util.isNullObject(server))
			throw new ApiException("100", "Server not found!");
		else
			return server;
	}

	@Override
	public UFile readFileById(Long id) throws ApiException {
		UFile file = uFileDao.readById(id);
		if(Util.isNullObject(file))
			throw new ApiException("F100", "File Not Found!");
		else 
			return file;
	}

	@Override
	public UFile readFileByIdentifier(String identifier) throws ApiException {
		UFile file = uFileDao.readFileByIdentifier(identifier);
		
		if(Util.isNullObject(file))
			throw new ApiException("F100", "File Not Found!");
		else 
			return file;
	}

	@Override
	public List<UFile> readFileByStatus(Long status) {
		return uFileDao.readFileByStatus(status);
	}

	@Override
	public Map<String, String> getUFileInfo(String identifier) throws ApiException {
		Map<String, String> info = new HashMap<>();
		
		UFile file = readFileByIdentifier(identifier);
		Server server = file.getServer();
		
		info.put("FSP", file.getFullServerPath());
		info.put("IDTFR", file.getFileIdentifier());
		info.put("MIM", file.getMimeType());
		info.put("CURL", server.getHttpPath()+"/file_r3.php?f="+file.getFullServerPath());
		info.put("KKTR", UUID.randomUUID().toString());
		
		return info;
	}

	@Override
	public boolean isFortuneConversationVisibleToTheUser(Long requestId, User user) throws ApiException {
		FortuneRequest request = fortuneService.getFortuneRequestById(requestId);
		
		if(user.isCommenter()){
			if(request.getOwnerId() != null && user.getId().compareTo(request.getOwnerId()) == 0){ //commenter(s) can only view messages if they have ownership
				return true;
			}
		} else if(user.isRoot()){
			return true;
		} else { //user
			if(request.getRequesterId().compareTo(user.getId()) == 0){
				return true;
			}
		}
		
		return false;
	}

}
