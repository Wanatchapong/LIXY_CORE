package com.lixy.ftapi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/commenter/")
@PreAuthorize("hasRole('ROLE_ROOT') or hasRole('ROLE_COMMENTER')'")
public class CommenterController{
	
	
	
}
