package com.lixy.ftapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.CommenterService;
import com.lixy.ftapi.service.UtilService;

@RestController
@RequestMapping("/v1/commenter")
@PreAuthorize("hasRole('ROLE_ROOT') or hasRole('ROLE_COMMENTER')")
public class CommenterController {
	
	@Autowired
	private CommenterService commenterService;
	
	@Autowired
	private UtilService utilService;

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

}
