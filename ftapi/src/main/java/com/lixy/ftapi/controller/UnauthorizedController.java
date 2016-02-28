package com.lixy.ftapi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lixy.ftapi.domain.Server;
import com.lixy.ftapi.domain.UFile;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.VirtualCommenter;
import com.lixy.ftapi.domain.VirtualCommenterPrice;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.GResponse;
import com.lixy.ftapi.model.UserAuthentication;
import com.lixy.ftapi.service.CustomerService;
import com.lixy.ftapi.service.TokenAuthenticationService;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.StatusType;
import com.lixy.ftapi.type.SwitchType;
import com.lixy.ftapi.util.FacebookUtil;
import com.lixy.ftapi.util.Util;

@RestController
@RequestMapping("/v1/ua/")
public class UnauthorizedController{
	
	private static Logger logger = LogManager.getLogger(UnauthorizedController.class);
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	@Qualifier("tokenAuthenticationServiceImpl")
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UtilService utilService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/check/token")	
	public GResponse checkToken(HttpServletRequest request, HttpServletResponse response){
		GResponse gresponse = new GResponse();
		
		try {
			UserAuthentication authentication = (UserAuthentication) tokenAuthenticationService.getAuthentication(request, response);
			if(authentication == null || !authentication.isAuthenticated()){
				gresponse.setStatus(StatusType.FAIL);
			}
		} catch (Exception e) {
			gresponse.convertToGResponse(e);
		}
		
		return gresponse;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/update/token/fb")	
	public GResponse updateFBToken(@RequestParam(required = false) Long profileId, @RequestParam(required = false) String accessToken){
		GResponse response = new GResponse();
		
		try {
			User user = userService.getUserByProfileId(profileId);
			FacebookUtil.getUser(profileId, accessToken); //check if token exist
			
			user.setFbUserAccessToken(accessToken);
			userService.updateUser(user);
		} catch (Exception e) {
			response.convertToGResponse(e);
		}
		
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/vcommenter/{id}")	
	public VirtualCommenter getCommenter(@PathVariable(value="id") Long virtualCommenterId){
		return customerService.getVirtualCommenter(virtualCommenterId);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/active_vcommenter")	
	public List<VirtualCommenter> getActiveVirtualCommenters(){
		return customerService.getVirtualCommenters(SwitchType.ACTIVE.getSwithStatus());
	}
		
	@RequestMapping(method = RequestMethod.GET, value = "/deactive_vcommenter")	
	public List<VirtualCommenter> getDeactiveVirtualCommenters(){
		return customerService.getVirtualCommenters(SwitchType.PASSIVE.getSwithStatus());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/commenter/price/{id}")	
	public GResponse getPriceOfVirtualCommenter(@PathVariable(value="id") Long virtualCommenterId){
		GResponse response = new GResponse();
		try {
			VirtualCommenter commenter = customerService.getVirtualCommenter(virtualCommenterId);
			
			if(Util.isNullObject(commenter)){
				throw new ApiException("100", "COMM_NOT_FOUND");
			} else if(commenter.getStatus().longValue() != 0){
				throw new ApiException("101", "COMM_NOT_ACTIVE");
			}
			
			List<VirtualCommenterPrice> prices = customerService.getVirtualCommenterPrices(commenter.getId(), SwitchType.ACTIVE.getSwithStatus());
			if(Util.isNullOrEmptyList(prices)){
				throw new ApiException("102", "This commenter does not have any price list.");
			}
			
			response.setStatus(StatusType.OK);
			response.setObject(prices);
			
		} catch(Exception e){
			response.convertToGResponse(e);
		}
		
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/commenter/price/all")	
	public GResponse getPricesOfVirtualCommenters(){
		GResponse response = new GResponse();
		try {
			response.setStatus(StatusType.OK);
			response.setObject(customerService.getVirtualCommenterPrices());
		} catch(Exception e){
			response.convertToGResponse(e);
			logger.error("getPricesOfVirtualCommenters error", e);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/get_file", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getFile(@RequestParam("f") String identifier, HttpServletRequest request,
			HttpServletResponse response) {

		InputStream inputStream = null;
		OutputStream outStream = null;

		try {
			UFile file = utilService.readFileByIdentifier(identifier);
			Server server = file.getServer();
			/*
			RedirectView redirectView = new RedirectView();
		    redirectView.setUrl(server.getHttpPath() +"/upload"+file.getFullServerPath());
		    return redirectView;
		    */			
			
			URL url = new URL(server.getHttpPath() +"/upload"+file.getFullServerPath());
			
		    HttpHeaders respHeaders = new HttpHeaders();

		    respHeaders.setContentLength(file.getSize().intValue());
		    respHeaders.setContentType(MediaType.parseMediaTypes(file.getMimeType()).get(0));
		    //respHeaders.setContentDispositionFormData("attachment", file.getOriginalName());
		    
		    InputStreamResource isr = new InputStreamResource(url.openStream());
		    return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
		    
		    /*

			 // URLConnection connection = url.openConnection();
			inputStream = url.openStream();
	        outStream = response.getOutputStream();
	        IOUtils.copy(inputStream, outStream);
	        
	        
	        
	        logger.info("File read success. identifier " + identifier);
	        	*/

		} catch (Exception e) {
			logger.error("Exception for get_file(1) identifier " + identifier, e);
		} finally {
			try {
				if (null != inputStream)
					inputStream.close();
				if (null != inputStream)
					outStream.close();
			} catch (IOException e) {
				logger.error("Exception for get_file(2) identifier " + identifier, e);
			}
		}
		return null;
	}
	
}
