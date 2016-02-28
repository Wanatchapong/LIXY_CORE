package com.lixy.ftapi.queue.listener;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.UFileDao;
import com.lixy.ftapi.domain.Server;
import com.lixy.ftapi.domain.UFile;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.util.CryptoUtils;
import com.lixy.ftapi.util.FTPUploader;
import com.lixy.ftapi.util.Util;

@Service("fileUploadListener")
public class FileUploadListener implements MessageListener {
	private static Logger logger = LogManager.getLogger(FileUploadListener.class);

	@Autowired
	@Qualifier("uFileDaoImpl")
	private UFileDao uFileDao;
	
	@Autowired
	private UtilService utilService;

	@Override
	@Transactional
	public void onMessage(Message message) { // NOSONAR
		logger.info("UPLOAD Message Received ---> " + message);

		Object o = Util.fromByteArray(message.getBody());
		if (Util.isNullObject(o))
			return;

		if (!(o instanceof Long))
			return;

		Long fileId = (Long) o;

		if (Util.isNullObject(fileId))
			return;

		UFile file = uFileDao.readById(fileId);
		FTPUploader uploader = null;

		try {
			if (Util.isNullObject(file))
				throw new ApiException("File not found.");
			else if (file.getStatus().longValue() != 0L && file.getStatus().longValue() != 3L)
				throw new ApiException("status != 0,3");

			Server server = utilService.getServerById(Constant.UPLOAD_SERVER_ID);
			
			file.setStatus(1L);
			file.setServer(server);
			uFileDao.update(file);

			String uploadPath = file.getOwner() == null ? "/" : "/" + file.getOwner() + "/";

			uploader = new FTPUploader(server.getIp(), server.getUser(), CryptoUtils.decrypt(server.getPassword()));
			uploader.uploadFile(file.getFullTempPath(), file.getTempName(), uploadPath);

			file.setStatus(2L);
			file.setFullServerPath(uploadPath + file.getTempName());
			file.setServerUploadDate(new Date());

			logger.info(file + " upload success...");

		} catch (ApiException e) {
			logger.error("error when upload queue.", e);
			if (file != null)
				file.setStatus(3L);
		} catch (Exception e) {
			logger.error("error when upload queue.", e);
			if (file != null)
				file.setStatus(4L);
		} finally {
			if (uploader != null) {
				uploader.disconnect();
			}

			if (file != null)
				uFileDao.update(file);
		}

		logger.info("UPLOAD Message Received DONE ---> " + message);

	}

}
