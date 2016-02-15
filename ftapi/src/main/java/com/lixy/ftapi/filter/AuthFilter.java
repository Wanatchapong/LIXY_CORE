package com.lixy.ftapi.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lixy.ftapi.conf.common.ApplicationContextProvider;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.TokenAuthenticationService;
import com.lixy.ftapi.util.ServerUtils;
import com.lixy.ftapi.util.Util;

public class AuthFilter extends GenericFilterBean {
	private Logger filterLogger = LogManager.getLogger(AuthFilter.class);

	@Autowired
	@Qualifier("tokenAuthenticationServiceImpl")
	private TokenAuthenticationService tokenAuthenticationService;

	public AuthFilter() {
		tokenAuthenticationService = (TokenAuthenticationService) ApplicationContextProvider.getApplicationContext()
				.getBean("tokenAuthenticationServiceImpl");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		UserAuthentication auth = (UserAuthentication) tokenAuthenticationService.getAuthentication(httpRequest, httpResponse);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		if(auth.isAuthenticated())
			filterChain.doFilter(request, response);
		else{
			ObjectMapper mapper = new ObjectMapper();
			response.setCharacterEncoding("UTF-8");
			httpResponse.getWriter().write(mapper.writeValueAsString(auth.getInfo()));
			filterLogger.info("ACCESS_DENIED." + Util.getInputLogsSimple("ip, info, server", auth.getIpAddress(), auth.getInfo(), ServerUtils.convertHTTPServletRequestToHostMap(request)));
		}
		
	}

}
