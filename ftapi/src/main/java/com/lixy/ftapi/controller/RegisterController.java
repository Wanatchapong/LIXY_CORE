package com.lixy.ftapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.model.Info;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.type.StatusType;
import com.lixy.ftapi.util.IPUtil;

@RestController
@RequestMapping("/v1/register")
public class RegisterController {

	private static Logger logger = LogManager.getLogger(RegisterController.class);

	@Autowired
	private UserService userService;

	/**
	 * Returns register information to the client. Creates a user with role
	 * ROLE_NEW_USER
	 *
	 * @param username
	 *            Register username
	 * @param password
	 *            Register password
	 * @return Information about registration
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/client")
	public Info registerClient(HttpServletRequest request, 
			@RequestParam(required = false) String username, @RequestParam(required = false) String password, 
			@RequestParam(required = false) Long profileId, @RequestParam(required = false) String accessToken, 
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String surname) {
		return userService.register(username, password, profileId, accessToken, name, surname, IPUtil.getRemoteIp(request));
	}

	@PreAuthorize("hasRole('ROLE_ROOT')")
	@RequestMapping(method = RequestMethod.GET, value = "/commenter")
	public GResponse registerCommenter(UserAuthentication auth, @RequestParam(required = true) Long userId) {
		GResponse response = new GResponse();
		response.setUid(auth.getUser().getId());

		try {
			Long authId = userService.makeUserAsCommenter(userId, auth.getToken());

			response.setStatus(StatusType.OK);
			response.setRespCode("0");
			response.setRespMessage(authId + " is added");
		} catch (Exception ex) {
			response.convertToGResponse(ex);
			logger.error("Errow when inserting new fortune request", ex);
		}

		return response;
	}

}
