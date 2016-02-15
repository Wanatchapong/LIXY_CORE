package com.lixy.ftapi.util;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalNotification;
import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.conf.common.ApplicationContextProvider;
import com.lixy.ftapi.queue.manager.QueueManager;

public class CacheUtil {
	private static Logger logger = LogManager.getLogger(CacheUtil.class);
	public static final int TIMEOUT_IN_SECONDS = Constant.CACHE_TIMEOUT_IN_SEC; // 60
	// Minutes
	private static ConcurrentMap<String, Object> CONCURRENT_MAP; // NOSONAR
	private static Cache<String, Object> CACHE; // NOSONAR

	private CacheUtil() {
		// singleton
	}

	public static void init() {
		CACHE = CacheBuilder.newBuilder()
				.expireAfterAccess(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
				.removalListener((RemovalNotification<String, Object> notification) -> {
						if (notification.getCause() == RemovalCause.EXPIRED) {
							logger.info("This data expired:" + notification.getKey());
						} else {
							logger.info("This data didn't expired but evacuated intentionally. Token:" + notification.getKey());
						}
			
						QueueManager manager = (QueueManager) ApplicationContextProvider.getApplicationContext().getBean("queueManager");
						try {
							manager.sendToTokenExpireQueue(notification.getKey());
						} catch (Exception e) {
							logger.error("An error accured while sending token to expire queue. Token:" + notification.getKey(), e);
						}
			
					}).build();

		CONCURRENT_MAP = CACHE.asMap();
	}

	public static void addToCache(String tag, Object value) {
		CONCURRENT_MAP.put(tag, value);
	}

	public static Object getFromCache(String tag) {
		if (CONCURRENT_MAP != null)
			return CONCURRENT_MAP.get(tag);
		else
			return null;
	}

	public static void removeFromCache(String tag) {
		if (CONCURRENT_MAP != null)
			CONCURRENT_MAP.remove(tag);
	}

	public static void cleanCache() {
		if (CACHE != null)
			CACHE.cleanUp();
	}

	public static void invalidateAllCache() {
		if (CACHE != null)
			CACHE.invalidateAll();
	}

}
