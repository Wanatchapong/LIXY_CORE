package com.lixy.ftapi.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lixy.ftapi.model.Info;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.util.CacheUtil;
import com.lixy.ftapi.util.CryptoUtils;

@RestController
@RequestMapping("/v1/manager")
@PreAuthorize("hasRole('ROLE_ROOT')")
public class ManagerController{
	
	@Autowired
	@Qualifier("queueManager")
	private QueueManager queueManager;
	
	@Autowired
	private UtilService utilService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/clearTokenCache")
	public Info clearTokenCache() {
		Info i = new Info();
		try {
			CacheUtil.cleanCache();
			CacheUtil.invalidateAllCache();
			
			i.setCode(0L);
			i.setDesc("ALL_CACHE_INVALIDATED");
		} catch (Exception e) {
			i.setCode(100L);
			i.setDesc(ExceptionUtils.getFullStackTrace(e));
		}
		
		return i;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/invalidate/{token}")
	public void invalidateToken(@PathVariable(value="token") String token) {
		CacheUtil.removeFromCache(token);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/sendMailFromPool")
	public void sendMailFromPool(@RequestParam(required = true) Long poolId) {
		queueManager.sendToMailQueue(poolId);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/refresh_property")
	public void refreshProperties() {
		utilService.recacheAllProperties();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/encode")
	public String encryptText(@RequestParam(required = true) String text) {
		return CryptoUtils.encrypt(text);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/decode")
	public String decryptText(@RequestParam(required = true) String text) {
		return CryptoUtils.decrypt(text);
	}
	
}
