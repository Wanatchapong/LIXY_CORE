package com.lixy.ftapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lixy.ftapi.domain.Customer;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.CustomerService;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.AuthorityType;
import com.lixy.ftapi.type.StatusType;
import com.lixy.ftapi.util.SecurityUtil;
import com.lixy.ftapi.util.Util;

@RestController
@RequestMapping("/v1/user")
@PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_COMMENTER') or hasRole('ROLE_ROOT')")
public class UserController {

	@Autowired
	private UtilService utilService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService customerService;

	@RequestMapping(value = "/get_user_info/{uid}", method = RequestMethod.GET)
	public GResponse getUserInformation(UserAuthentication auth, @PathVariable(value = "uid") Long userId,
			@RequestParam(value = "full", required=false) String full) {
		GResponse response = new GResponse();
		HashMap<String, Object> infoMap = new HashMap<>();
		
		try {
			boolean isRoot = auth.getUser().hasAuthority(AuthorityType.ROLE_ROOT);
			boolean isCommenter = auth.getUser().hasAuthority(AuthorityType.ROLE_COMMENTER);
			boolean idMatch = (auth.getUser().getId().longValue() == userId);
			
			User user = null;

			if (!(isRoot || isCommenter) && !idMatch) {
				throw new ApiException("100", utilService.getMessage("user.id.mismatch "));
			} else {
				user = userService.getUserById(userId);
				
				if(!isRoot && !idMatch){
					user.setFbProfileId(null);
					user.setMobilePhone(null);
					user.setStatus(null);
					user.setUserAuthorityList(null);
					user.setUsername(null);
				} else if(isRoot && "f".equalsIgnoreCase(full)) { //only root users may want additional info
					Customer customer = customerService.getCustomerByUserId(userId);
					infoMap.put("CUSTOMER", customer);
				} 
				
				infoMap.put("USER", user);
				response.setObject(infoMap);
			}
		} catch (Exception e) {
			response.convertToGResponse(e);
		}

		return response;
	}

	@RequestMapping(value = "/change_password/{uid}", method = RequestMethod.POST)
	public GResponse changeUserPassword(UserAuthentication auth, @PathVariable(value = "uid") Long userId,
			@RequestBody(required = true) Map<String, String> passwordInfo) {
		GResponse response = new GResponse();
		try {
			User user = null;

			if (!auth.getUser().hasAuthority(AuthorityType.ROLE_ROOT) && auth.getUser().getId().longValue() != userId) {
				throw new ApiException("100", utilService.getMessage("user.id.mismatch "));
			} else {
				user = userService.getUserById(userId);
			}

			if (Util.isNullOrEmptyHashMap(passwordInfo))
				throw new ApiException("101", utilService.getMessage("password.detail.notfound"));

			String currentPassword = passwordInfo.get("CURRENT_PASSWORD");
			String newPassword = passwordInfo.get("NEW_PASSWORD");
			String newPasswordRe = passwordInfo.get("NEW_PASSWORD_RE");

			if (Util.isNullOrEmpty(currentPassword)) {
				if (!user.getPassword().isEmpty())
					throw new ApiException("102", utilService.getMessage("password.mismatch"));
			} else {
				if (!SecurityUtil.md5(currentPassword).equals(user.getPassword()))
					throw new ApiException("103", utilService.getMessage("password.wrong"));
			}

			if (Util.isNullOrEmpty(newPassword) || Util.isNullOrEmpty(newPasswordRe))
				throw new ApiException("104", utilService.getMessage("password.empty"));
			else if (!newPassword.equals(newPasswordRe))
				throw new ApiException("105", utilService.getMessage("password.notequal"));
			else if (newPassword.length() < 5)
				throw new ApiException("106", utilService.getMessage("password.atleast").replace("##minlength##", "5"));

			user.setPassword(SecurityUtil.md5(newPassword));

			userService.updateUser(user);
			
			response.setStatus(StatusType.OK);
			response.setRespMessage(utilService.getMessage("password.change.success"));

		} catch (Exception e) {
			response.convertToGResponse(e);
		}

		return response;
	}

}
