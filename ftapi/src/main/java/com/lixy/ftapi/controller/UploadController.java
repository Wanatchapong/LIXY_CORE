package com.lixy.ftapi.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lixy.ftapi.domain.UFile;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.AuthorityType;
import com.lixy.ftapi.type.StatusType;
import com.lixy.ftapi.util.Util;

@RestController
@RequestMapping("/v1/upload/")
@PreAuthorize("hasRole('ROLE_ROOT') or hasRole('ROLE_COMMENTER') or hasRole('ROLE_CUSTOMER')")
public class UploadController {

	private static Logger logger = LogManager.getLogger(UploadController.class);
	
	@Autowired
	private QueueManager queueManager;

	@Autowired
	private UserService userService;

	@Autowired
	private UtilService utilService;

	@RequestMapping(value = "/file", method = RequestMethod.POST)
	public @ResponseBody GResponse handleFileUpload(UserAuthentication auth, @RequestParam("uid") Long uid, @RequestParam("file") MultipartFile file) {
		GResponse response = new GResponse();
		response.setUid(auth.getUser().getId());

		try {
			if (Util.isNullObject(uid))
				throw new ApiException("100", utilService.getMessage("user.id.empty"));
			if (file == null || file.isEmpty())
				throw new ApiException("101", utilService.getMessage("file.notexist"));
			
			User user = userService.getUserById(uid);
			
			if(!auth.getUser().hasAuthority(AuthorityType.ROLE_ROOT) && user.getId().longValue() != auth.getUser().getId().longValue())
				throw new ApiException("102", utilService.getMessage("user.id.mismatch"));
			
			String tempFileName = Util.getSimpleUniqueId() + ".ft";
			String tempFilePath = utilService.getTempFilePath(tempFileName);
			String originalFile = file.getOriginalFilename();

			byte[] bytes = file.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(tempFilePath)));
			stream.write(bytes);
			stream.close();
			
			UFile uFile = new UFile();
			uFile.setUploader(auth.getUser().getId());
			uFile.setOwner(user.getId()); //for now uploader and owner is same
			uFile.setOriginalName(originalFile);
			uFile.setMimeType(file.getContentType());
			uFile.setTempName(tempFileName);
			uFile.setFullTempPath(tempFilePath);
			uFile.setSize(file.getSize());
			uFile.setFileIdentifier(UUID.randomUUID().toString());
			uFile.setStatus(0L);
			
			Long createdFileId = utilService.addUFile(uFile);
			
			queueManager.sendToUploadQueue(createdFileId);
			
			response.setStatus(StatusType.OK);
			response.setRespMessage(utilService.getMessage("upload.complete"));
			response.setId(createdFileId);
			response.setObject(uFile.getFileIdentifier());

		} catch (ApiException e) {
			response.convertToGResponse(e);
			logger.error("Error while uploading fortune.", e);
		} catch (IOException e) {
			response.convertToGResponse(e);
			logger.error("Error while uploading fortune.", e);
		}

		return response;
	}
	
	

}
