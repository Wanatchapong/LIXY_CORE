package com.lixy.ftapi.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lixy.ftapi.domain.Customer;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.FortuneInfo;
import com.lixy.ftapi.model.FortuneRequestModel;
import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.CustomerService;
import com.lixy.ftapi.service.FortuneService;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.AuthorityType;
import com.lixy.ftapi.type.StatusType;
import com.lixy.ftapi.type.UpdateCreditReason;
import com.lixy.ftapi.util.Util;

@RestController
@RequestMapping("/v1/customer")
@PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ROOT')")
public class CustomerController {

	private static Logger logger = LogManager.getLogger(CustomerController.class);

	@Autowired
	private UtilService utilService;

	@Autowired
	private FortuneService fortuneService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService customerService;
	
	@RequestMapping(value = "/get_customer_info/{uid}", method = RequestMethod.GET)
	public GResponse getCustomerInformation(UserAuthentication auth, @PathVariable(value = "uid") Long userId) {
		GResponse response = new GResponse();
		try {
			Customer customer = null;
			
			if(!auth.getUser().hasAuthority(AuthorityType.ROLE_ROOT) &&auth.getUser().getId().longValue() != userId){
				throw new ApiException("100", utilService.getMessage("user.id.mismatch "));
			} else {
				customer = customerService.getCustomerByUserId(userId);
			}
			response.setObject(customer);
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

			if (!auth.getUser().hasAuthority(AuthorityType.ROLE_ROOT) && request.getRequesterId().longValue() != auth.getUser().getId().longValue()) {
				throw new ApiException("-100", utilService.getMessage("fortune.request.not.same.user"));
			} else {
				fortuneService.convertToRequestModel(request, info);

				response.setStatus(StatusType.OK);
				response.setObject(info);
			}

		} catch (Exception ex) {
			response.convertToGResponse(ex);
		}

		logger.info(response);
		return response;
	}

	@RequestMapping(value = "/new_fortune_request", method = RequestMethod.POST)
	public GResponse insertFortuneRequest(UserAuthentication auth, @RequestBody FortuneRequestModel request) {
		GResponse response = new GResponse();
		response.setUid(auth.getUser().getId());

		logger.info("New fortune request from " + auth.getUser() + ".");

		try {
			Long requestId = fortuneService.insertFortuneRequest(auth.getUser(), request);
			String accepted = utilService.getMessage("fortune.request.added").replace("#REQID#", Util.getSimpleUniqueId() + "-" + requestId);

			Map<String, String> results = new HashMap<>();
			results.put("REQUEST_ID", requestId.toString());
			results.put("UNIQUE_ID", Util.getSimpleUniqueId());
			
			response.setStatus(StatusType.OK);
			response.setRespMessage(accepted);
			response.setObject(results);

		} catch (Exception ex) {
			response.convertToGResponse(ex);
			logger.error(utilService.getMessage("error.insert.newfortune"), ex);
		}

		logger.info(response);
		return response;
	}

	@RequestMapping(value = "/fortunes", method = RequestMethod.GET)
	public GResponse getFortuneRequestList(UserAuthentication auth) {
		GResponse response = new GResponse();
		response.setUid(auth.getUser().getId());
		
		User user = auth.getUser();
		try {
			userService.checkUserSuitableForProcess(user.getId());
			
			response.setObject(fortuneService.getFortuneRequestByUserId(user.getId(), null));
			response.setStatus(StatusType.OK);
		} catch (Exception ex) {
			response.convertToGResponse(ex);
			logger.error("Errow when inserting new fortune request", ex);
		}

		return response;
	}

	@PreAuthorize("hasRole('ROLE_ROOT')")
	@RequestMapping(value = "/fortunes/{userId}", method = RequestMethod.GET)
	public GResponse getFortuneRequestROOT(UserAuthentication auth, @PathVariable(value = "userId") Long userId) {
		GResponse response = new GResponse();
		response.setUid(auth.getUser().getId());
		
		User user = auth.getUser();
		try {
			userService.checkUserSuitableForProcess(user.getId());
			
			response.setObject(fortuneService.getFortuneRequestByUserId(userId, null));
			response.setStatus(StatusType.OK);
		} catch (Exception ex) {
			response.convertToGResponse(ex);
			logger.error("Errow when inserting new fortune request", ex);
		}

		return response;
	}
	
	@PreAuthorize("hasRole('ROLE_ROOT')")
	@RequestMapping(value = "/credit/{userId}/{creditAmount:.+}", method = RequestMethod.POST)
	public GResponse addCreditToCustomer(UserAuthentication auth, @PathVariable(value = "userId") Long userId, @PathVariable(value = "creditAmount") BigDecimal creditAmount) {
		GResponse response = new GResponse();
		response.setUid(auth.getUser().getId());
		
		User user = auth.getUser();
		try {
			userService.checkUserSuitableForProcess(user.getId());
			
			response.setObject(customerService.addCredit(user.getId(), userId, creditAmount, UpdateCreditReason.BY_ROOT));
			response.setStatus(StatusType.OK);
		} catch (Exception ex) {
			response.convertToGResponse(ex);
			logger.error("Errow when inserting new fortune request", ex);
		}

		return response;
	}
	
	
}
