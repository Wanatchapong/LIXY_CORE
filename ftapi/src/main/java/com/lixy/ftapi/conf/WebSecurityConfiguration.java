package com.lixy.ftapi.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.lixy.ftapi.filter.AuthFilter;
import com.lixy.ftapi.filter.LoginFilter;
import com.lixy.ftapi.service.TokenAuthenticationService;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.service.impl.TokenAuthenticationServiceImpl;
import com.lixy.ftapi.service.impl.UserServiceImpl;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private static final String API_V1 = "/api/v1";
	private final UserService userService;

	public WebSecurityConfiguration() {
		super(true);
		this.userService = new UserServiceImpl();
	}
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.exceptionHandling()
			.and()
			.anonymous()
			.and()
			.servletApi()
			.and()
			.headers().cacheControl()
			.and()
			.authorizeRequests().antMatchers(HttpMethod.OPTIONS,"/api/auth/login").permitAll()
			.anyRequest().authenticated()
			.and()
			.addFilterBefore(new LoginFilter(new AntPathRequestMatcher("/api/auth/login")), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new AuthFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(API_V1 + "/dummy/**", API_V1 + "/register/client", API_V1 + "/ua/**");		
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	@Override
	public UserService userDetailsService() {
		return userService;
	}

	@Bean
	public TokenAuthenticationService tokenAuthenticationService() {
		return new TokenAuthenticationServiceImpl();
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setMaxUploadSize(50000000);
		commonsMultipartResolver.setDefaultEncoding("iso-8859-1");
		return commonsMultipartResolver;
	}

}
