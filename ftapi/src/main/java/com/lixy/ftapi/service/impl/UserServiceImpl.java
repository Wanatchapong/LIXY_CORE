package com.lixy.ftapi.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.AuthorityDao;
import com.lixy.ftapi.dao.UserAuthorityDao;
import com.lixy.ftapi.dao.UserDao;
import com.lixy.ftapi.domain.Authority;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.UserAuthority;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.Info;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.AuthType;
import com.lixy.ftapi.type.AuthorityType;
import com.lixy.ftapi.type.EventType;
import com.lixy.ftapi.type.UserStatus;
import com.lixy.ftapi.util.FacebookUtil;
import com.lixy.ftapi.util.SecurityUtil;
import com.lixy.ftapi.util.Util;
import com.lixy.ftapi.util.ValidationUtils;

import facebook4j.FacebookException;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService{

	private Logger logger = LogManager.getLogger(UserServiceImpl.class);

	@Autowired
	@Qualifier("queueManager")
	private QueueManager queueManager;

	@Autowired
	@Qualifier("userDaoImpl")
	private UserDao userDao;

	@Autowired
	@Qualifier("authorityDaoImpl")
	private AuthorityDao authorityDao;

	@Autowired
	@Qualifier("userAuthorityDaoImpl")
	private UserAuthorityDao userAuthorityDao;
	
	@Autowired
	private UtilService utilService;	

	@Override
	public Info register(String username, String password, Long profileId, String accessToken, String name, String surname, String ip) {
		Info info = new Info();
		
		AuthType authType = null; 
		boolean isUsernameEmpty = Util.isNullOrEmpty(username);
		boolean isProfileIdEmpty = Util.isNullObject(profileId);
		
		User user = null;
		
		if(isUsernameEmpty && isProfileIdEmpty){ //minimum one of them should be as input
			info.setCode(100L);
			
			if(isUsernameEmpty){
				info.setDesc(utilService.getMessage("user.emptyusername"));
			} else {
				info.setDesc(utilService.getMessage("user.emptyprofileid"));
			}
		} else if (Util.isNullOrEmpty(accessToken) && Util.isNullOrEmpty(password)) {
			info.setCode(-102L);
			info.setDesc(utilService.getMessage("register.atleasttokenpass"));
		} else {
			if(isUsernameEmpty){ //o zaman facebook login
				authType = AuthType.FACEBOOK;
				user = userDao.readUserByProfileId(profileId, null);
			} else {
				if (!ValidationUtils.isValidMailAddress(username)) {
					info.setCode(-101L);
					info.setDesc(utilService.getMessage("invalid.email"));
				} else {
					authType = AuthType.BASIC;
					user = userDao.readUserByUsername(username, null);
				}
			}
			
			if (!Util.isNullObject(user)) { // there is a user
				info.setCode(-100L);
				info.setDesc(utilService.getMessage("user.exists"));
			} else if(info.getCode().longValue() == 0L) { //sorunsuz gelmis
				
				user = new User();
				
				if(authType == AuthType.FACEBOOK){ //last minute check 
					try {
						facebook4j.User fbUser = FacebookUtil.getUser(profileId, accessToken); //token check
						user.setFbProfileId(Long.valueOf(fbUser.getId()));
						user.setFbUserAccessToken(accessToken);
						user.setFbProfilePictureUrl(fbUser.getPicture() != null ? fbUser.getPicture().getURL().toString() : null );
						user.setUsername(fbUser.getEmail());
						user.setName(fbUser.getFirstName().trim());
						user.setSurname(Util.nvl(fbUser.getMiddleName(), "") + " " + Util.nvl(fbUser.getLastName(), "").trim());
						user.setFbUserAccessToken(accessToken);
						
					} catch (FacebookException e) {
						info.setCode(Long.valueOf(e.getErrorCode()));
						info.setDesc(e.getErrorSubcode() + "-"+ e.getErrorMessage());
						info.setObject(e);
					}
				} else { //BASIC LOGIN
					user.setUsername(username);
					user.setName(name);
					user.setSurname(surname);
					user.setPassword(SecurityUtil.md5(password));
				}
				
				if(info.getCode().longValue() == 0L){
				
					user.setStatus(UserStatus.PASSIVE.getUserStatus());
					user.setCreatedDate(new Date());
					user.setRegisterIp(ip);
					user.setLocale(utilService.getLocale() != null ? utilService.getLocale().getLanguage() : Constant.DEFAULT_LOCALE);
					user.setCreatedBy(Constant.CREATED_BY);
					user.setRegisterChannel(authType.getLoginType());
	
					Long uid = userDao.create(user);
					
					addUserAuthority(user, AuthorityType.ROLE_NEW_USER);
	
					info.setDesc("User is created!");
					info.setCode(0L);
					info.setKey(Util.getUniqueId() + "->" + uid);
	
					queueManager.sendToNewUserQueue(user);
	
					logger.info("New user created. " + Util.getInputLogsSimple("userId, info", uid, info));
					
					utilService.addEventLog(uid, EventType.NEW_USER, "Yeni Kullanici Kaydi Yapildi");
				}
			}
			
		}
		
		logger.info(info);

		return info;
	}

	@Override
	public void changeUserStatus(Long userId, UserStatus status) {
		User user = userDao.readById(userId);
		changeUserStatus(user, status);
	}

	@Override
	public void changeUserStatus(User user, UserStatus status) {
		user.setStatus(status.getUserStatus());
		userDao.update(user);
	}
	
	@Override
	public void updateUser(User user) {
		userDao.update(user);
	}


	@Override
	public User loadUserByUsername(String username) {
		return userDao.readUserByUsername(username, null);
	}

	@Override
	public User getUserById(Long id) throws ApiException {
		if(!Util.isNullObject(id)){
			User user = userDao.readById(id);
			if(Util.isNullObject(user)){
				throw new ApiException("-909", utilService.getMessage("user.notfound"));
			} else {
				return user;
			}
		}
		else {
			throw new ApiException("-909", utilService.getMessage("user.notfound"));
		}
	}
	
	@Override
	public User getUserByProfileId(Long profileId) throws ApiException {
		if(!Util.isNullObject(profileId)){
			User user = userDao.readUserByProfileId(profileId, null);
			if(Util.isNullObject(user)){
				throw new ApiException("-909", utilService.getMessage("user.notfound"));
			} else {
				return user;
			}
		}
		else {
			throw new ApiException("-909", utilService.getMessage("user.notfound"));
		}
	}

	@Override
	public void checkUserSuitableForProcess(Long userId) throws ApiException {
		User user = getUserById(userId);
		boolean hasNegativeAuthority = user.hasAuthority(AuthorityType.ROLE_RESTRICTED) || user.hasAuthority(AuthorityType.ROLE_NEW_USER);
		
		if(hasNegativeAuthority){
			throw new ApiException("NEG_AUTHORITY", utilService.getMessage("user.authority.negative"));
		}
	}
	
	@Override
	public Long addUserAuthority(User user, AuthorityType authorityType) {
		Authority authority = authorityDao.readByName(authorityType.toString());

		UserAuthority userAuthority = new UserAuthority();
		userAuthority.setAuthority(authority);
		userAuthority.setUser(user);
		userAuthority.setCreatedDate(new Date());
		userAuthority.setCreatedBy(Constant.CREATED_BY);

		Long id = userAuthorityDao.create(userAuthority);
		
		utilService.addEventLog(user.getId(), EventType.AUTHORITY_CHANGE, "User Authority Changed To " + authorityType.name());
		
		return id;
	}

	@Override
	public Long makeUserAsCommenter(Long userId, String token) throws ApiException {
		User user = getUserById(userId);
		if(!user.hasAuthority(AuthorityType.ROLE_NEW_USER)){
			throw new ApiException("100", utilService.getMessage("user.negative"));
		}
		
		return addUserAuthority(user, AuthorityType.ROLE_COMMENTER);
	}

	

}
