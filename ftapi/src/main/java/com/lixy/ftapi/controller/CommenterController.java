package com.lixy.ftapi.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.FortuneInfo;
import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.CommenterService;
import com.lixy.ftapi.service.FortuneService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.StatusType;

@RestController
@RequestMapping("/v1/commenter")
@PreAuthorize("hasRole('ROLE_ROOT') or hasRole('ROLE_COMMENTER')")
public class CommenterController {
	
	private static Logger logger = LogManager.getLogger(CommenterController.class);
	
	@Autowired
	private CommenterService commenterService;
	
	@Autowired
	private UtilService utilService;
	
	@Autowired
	private FortuneService fortuneService;

	@RequestMapping(value = "/get_fortune_list/{status}/{start}/{limit}", method = RequestMethod.GET)
	public GResponse getFortuneRequestByStatus(
			UserAuthentication auth, 
			@PathVariable(value = "status") String status,
			@PathVariable(value = "start") Long start, 
			@PathVariable(value = "limit") Long limit) {
		GResponse response = new GResponse();

		try {
			if(start == null)
				throw new ApiException(utilService.getMessage("paging.start.err1"));
			
			if(limit == null)
				throw new ApiException(utilService.getMessage("paging.limit.err1"));
			else if ( limit > 500)
				throw new ApiException(utilService.getMessage("paging.limit.err2") + ". Max 500");
			
			response.setObject(commenterService.getFortuneRequestWithInfo(status, start, limit));
			
		} catch (Exception e) {
			response.convertToGResponse(e);
		}

		return response;
	}
	
	@RequestMapping(value = "/get_fortune_request/{id}", method = RequestMethod.GET)
	public GResponse getFortuneRequest(UserAuthentication auth, @PathVariable(value = "id") Long requestId) {
		GResponse response = new GResponse();
		response.setUid(auth.getUser().getId());

		logger.info(auth.getUser().getUsername() + " asked for fortune request info for " + requestId);

		try {
			FortuneInfo info = new FortuneInfo();

			FortuneRequest request = fortuneService.getFortuneRequestById(requestId);
			fortuneService.convertToRequestModel(request, info);

			response.setStatus(StatusType.OK);
			response.setObject(info);

		} catch (Exception ex) {
			response.convertToGResponse(ex);
		}

		logger.info(response);
		return response;
	}

}
