package com.lixy.ftapi.controller;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.FortuneInfo;
import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.CommenterService;
import com.lixy.ftapi.service.FortuneService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.AuthorityType;
import com.lixy.ftapi.type.StatusType;
import com.lixy.ftapi.util.Util;

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

	@RequestMapping(value = "/get_fortune_list", method = RequestMethod.GET)
	public GResponse getFortuneRequestByStatus(
			UserAuthentication auth, 
			@RequestParam(value = "status", required = true) String status,
			@RequestParam(value = "start", required = false) Long start, 
			@RequestParam(value = "limit" , required = false) Long limit) {
		GResponse response = new GResponse();

		try {
			if (!Util.isNullObject(limit) && limit > 2500)
				throw new ApiException(utilService.getMessage("paging.limit.err2") + ". Max 2500");
			
			boolean isRoot = auth.getUser().hasAuthority(AuthorityType.ROLE_ROOT);
			
			List<FortuneInfo> fortuneInfo = null;
			if("I".equalsIgnoreCase(status)  || "A".equalsIgnoreCase(status) || isRoot){
				fortuneInfo = commenterService.getFortuneRequestWithInfo(status, start, limit);
			} else {
				fortuneInfo = commenterService.getFortuneRequestWithInfo(auth.getUser().getId(), status, start, limit);
			}
			
			response.setObject(fortuneInfo);
			
		} catch (Exception e) {
			response.convertToGResponse(e);
		}

		return response;
	}
	
	@RequestMapping(value = "/own_request/{id}", method = RequestMethod.GET)
	public GResponse ownFortuneRequest(
			UserAuthentication auth, 
			@PathVariable(value = "id") Long requestId,
			@RequestParam(value = "userId", required=false) Long ownerToBeWanted
		) {
		GResponse response = new GResponse();
		response.setUid(auth.getUser().getId());

		logger.info(auth.getUser().getUsername() + " asked for fortune request info for " + requestId);

		try {
			FortuneRequest request = fortuneService.getFortuneRequestById(requestId);
			
			if(Util.isNullObject(request)){
				throw new ApiException("101", utilService.getMessage("fortune.request.not.exist"));
			} 

			if(Util.isNullObject(ownerToBeWanted) || auth.getUser().isCommenter()) //commenter or null must be auth user
				ownerToBeWanted = auth.getUser().getId();
			
			if(auth.getUser().isCommenter() && request.getOwnerId() != null){
				if(request.getOwnerId().longValue() != auth.getUser().getId().longValue())
					throw new ApiException("102", utilService.getMessage("fortune.request.owner.already.exist"));
				else 
					throw new ApiException("103", utilService.getMessage("fortune.request.owner.already.you"));
			} else {
				request.setOwnerId(ownerToBeWanted);
				request.setModifiedDate(new Date());
				request.setModifiedBy(auth.getUser().getId()+"");
				fortuneService.updateFortuneRequest(request);
				
				response.setStatus(StatusType.OK);
			}

		} catch (Exception ex) {
			response.convertToGResponse(ex);
		}

		logger.info(response);
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
			
			if(Util.isNullObject(request)){
				throw new ApiException("101", utilService.getMessage("fortune.request.not.exist"));
			}
			
			if(!auth.getUser().isRoot() && !Util.isNullObject(request.getOwnerId()) && request.getOwnerId().longValue() != auth.getUser().getId()){
				throw new ApiException("100", utilService.getMessage("fortune.request.commenter.nomatch"));
			} else {
				fortuneService.convertToRequestModel(request, info);
			}

			response.setStatus(StatusType.OK);
			response.setObject(info);

		} catch (Exception ex) {
			response.convertToGResponse(ex);
		}

		logger.info(response);
		return response;
	}
	
}
