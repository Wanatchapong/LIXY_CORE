package com.lixy.ftapi.filter;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lixy.ftapi.conf.common.ApplicationContextProvider;
import com.lixy.ftapi.domain.UserToken;
import com.lixy.ftapi.exception.UserAuthenticationException;
import com.lixy.ftapi.model.Info;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.TokenAuthenticationService;
import com.lixy.ftapi.util.Util;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
	private Logger loginFilterLogger = LogManager.getLogger(LoginFilter.class);

	@Autowired
	@Qualifier("tokenAuthenticationServiceImpl")
	private TokenAuthenticationService tokenAuthenticationService;

	public LoginFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);

		tokenAuthenticationService = (TokenAuthenticationService) ApplicationContextProvider.getApplicationContext()
				.getBean("tokenAuthenticationServiceImpl");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException { // NOSONAR
		UserAuthentication auth = (UserAuthentication) tokenAuthenticationService.getAuthenticationForLogin(request,
				response);
		if (!auth.isAuthenticated()) {
			throw new UserAuthenticationException("Auth to FTAPI is Failed.", auth);
		}

		return auth;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			javax.servlet.FilterChain chain, Authentication authResult) throws IOException, ServletException {
		UserToken token = null;
		try {
			UserAuthentication authResultObject = (UserAuthentication) authResult;
			token = tokenAuthenticationService.addAuthentication(response, authResultObject);

			loginFilterLogger.info("Authentication SUCCESS. "
					+ Util.getInputLogsSimple("userId, ip, token", authResultObject.getUser().getId(), authResultObject.getIpAddress(), token.getToken()));

			// Add the authentication to the Security context
			SecurityContextHolder.getContext().setAuthentication(authResult);

			HashMap<String, Object> information = new HashMap<>();
			information.put("USER", authResultObject.getUser());
			information.put("AUTH_TYPE", authResultObject.getAuthType());
			information.put("INFO", authResultObject.getInfo());
			
			ObjectMapper mapper = new ObjectMapper();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(mapper.writeValueAsString(information));
		} catch (Exception ex) {
			Info i = new Info();
			i.setCode(109L);
			i.setDesc("UNWANTED_EXCEPTION_RELOGIN_NEEDED");

			loginFilterLogger.fatal("Authentication EXCEPTION. " + Util.getInputLogsSimple("token, exception", token,
					org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex)));

			ObjectMapper mapper = new ObjectMapper();
			response.getWriter().write(mapper.writeValueAsString(i));
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		UserAuthenticationException authException = (UserAuthenticationException) failed;
		Info info = authException.getAuthentication().getInfo();

		response.setHeader("X-AUTH-ERR-DESC", info.getCode() + "-" + info.getDesc());

		ObjectMapper mapper = new ObjectMapper();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(mapper.writeValueAsString(authException.getAuthentication().getInfo()));

		loginFilterLogger.info("Authentication FAIL. " + Util.getInputLogsSimple("ip, username, reason",
				authException.getAuthentication().getIpAddress(), request.getHeader("username"), info));
	}

}
