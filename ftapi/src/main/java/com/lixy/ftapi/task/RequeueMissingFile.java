package com.lixy.ftapi.task;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.MailPoolDao;
import com.lixy.ftapi.dao.UFileDao;
import com.lixy.ftapi.domain.MailPool;
import com.lixy.ftapi.domain.UFile;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.service.MailService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.SwitchType;
import com.lixy.ftapi.util.Util;

@Service
@Transactional
public class RequeueMissingFile {

	private Logger logger = LogManager.getLogger(getClass().getName());

	@Autowired
	private UtilService utilService;
	
	@Autowired
	@Qualifier("uFileDaoImpl")
	private UFileDao uFileDao;
	
	@Autowired
	private QueueManager queueManager;


	@Scheduled(fixedRate = Constant.MAIL_CHECKER_FIXED_RATE, initialDelay = 10000)
	public void requeueMissingFile() throws InterruptedException {

		List<UFile> fileWithProblem = utilService.readFileByStatus(3L);

		if (Util.isNullOrEmptyList(fileWithProblem))
			return;
		
		for (UFile uFile : fileWithProblem) {
			uFile.setStatus(0L);
			uFileDao.update(uFile);

			logger.info("Requeue broken file. (" + uFile.getId() +")");
			queueManager.sendToUploadQueue(uFile.getId());
		}

	}

}
