package com.lixy.ftapi.queue.listener;

import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.conf.Constant;
import com.lixy.ftapi.dao.AuthorityDao;
import com.lixy.ftapi.dao.CustomerDao;
import com.lixy.ftapi.dao.MailPoolDao;
import com.lixy.ftapi.dao.MailTemplateDao;
import com.lixy.ftapi.dao.UserAuthorityDao;
import com.lixy.ftapi.dao.UserDao;
import com.lixy.ftapi.domain.Authority;
import com.lixy.ftapi.domain.Customer;
import com.lixy.ftapi.domain.MailPool;
import com.lixy.ftapi.domain.MailTemplate;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.UserAuthority;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.AuthorityType;
import com.lixy.ftapi.type.EventType;
import com.lixy.ftapi.type.SwitchType;
import com.lixy.ftapi.type.UserStatus;
import com.lixy.ftapi.util.Util;

@Service("newCustomerListener")
public class NewCustomerListener implements MessageListener{
	private static Logger logger = LogManager.getLogger(NewCustomerListener.class);
	
	@Autowired
	@Qualifier("mailTemplateDaoImpl")
	private MailTemplateDao mailTemplateDao;
	
	@Autowired
	@Qualifier("mailPoolDaoImpl")
	private MailPoolDao mailPoolDao;
	
	@Autowired
	@Qualifier("userAuthorityDaoImpl")
	private UserAuthorityDao userAuthorityDao;
	
	@Autowired
	@Qualifier("userDaoImpl")
	private UserDao userDao;
	
	@Autowired
	@Qualifier("authorityDaoImpl")
	private AuthorityDao authorityDao;
	
	@Autowired
	@Qualifier("customerDaoImpl")
	private CustomerDao customerDao;
	
	@Autowired
	private UtilService utilService;
	
	@Override
	@Transactional
	public void onMessage(Message message) {
		logger.info("New Customer Message Received ---> " + message);
		
		Object o = Util.fromByteArray(message.getBody());
		if(Util.isNullObject(o))
			return;			
		
		if(!(o instanceof User))
			return;
		
		User createdUser = (User) o;		
		
		try{
			controlUser(createdUser);
			
			createdUser = userDao.readById(createdUser.getId());
			
			if(Util.isNullObject(createdUser))
				return;
			
			logger.info(loggerHeader(createdUser) + " Creating customer authorities...");
			Long userAuthorityId = makeUserCustomer(createdUser);
			logger.info(loggerHeader(createdUser) + " Creating customer authorities DONE. UAID->"+userAuthorityId);
			
			logger.info(loggerHeader(createdUser) + " Creating welcome mail pool record...");
			Long mailPoolId = createMailForNewUser(createdUser);
			logger.info(loggerHeader(createdUser) + " Creating welcome mail pool record DONE. MPID->"+mailPoolId);
			
			logger.info(loggerHeader(createdUser) + " Activating customer account...");
			activateUserAccount(createdUser);
			logger.info(loggerHeader(createdUser) + " Activating customer account DONE...");
			
		} catch(Exception ex){
			logger.error("Error in new user listener", ex);
		}
	}
	
	private Long createMailForNewUser(User createdUser) {
		MailTemplate template = mailTemplateDao.readByTag("WELCOME_CLIENT");
		
		if(!Util.isNullObject(template)){
			MailPool pool = new MailPool();
			pool.setUserId(createdUser.getId());
			pool.setStatus(SwitchType.ACTIVE.getSwithStatus());
			pool.setTemplateId(template.getId());
			pool.setFromAddress(template.getFromAddress());
			pool.setToAddress(createdUser.getUsername());
			pool.setSubject(template.getSubject());
			pool.setCreatedDate(new Date());
			pool.setIdentifier(UUID.randomUUID().toString());
			
			return mailPoolDao.create(pool);					
		}
		
		return null;
	}

	private static String loggerHeader(User createdUser){
		return Util.getInputLogsSimple("id", createdUser.getId());
	}
	
	private void activateUserAccount(User createdUser){
		createdUser.setStatus(UserStatus.ACTIVE.getUserStatus());
		userDao.update(createdUser);
		
		utilService.addEventLog(createdUser.getId(), EventType.USER_CHANGE, "User is activated.");
	}

	private Long makeUserCustomer(User createdUser) {
		Authority authority = authorityDao.readByName(AuthorityType.ROLE_CUSTOMER.toString());
		UserAuthority userAuthority = new UserAuthority();
		userAuthority.setUser(createdUser);
		userAuthority.setAuthority(authority);
		userAuthority.setCreatedBy(Constant.CREATED_BY);
		userAuthority.setCreatedDate(new Date());
		
		Long created = userAuthorityDao.create(userAuthority);
		
		Customer customer = new Customer();
		customer.setUserId(createdUser.getId());
		
		customerDao.create(customer);
		
		if(!Util.isNullObject(created)){
			UserAuthority newUserAuthority = userAuthorityDao.getAuthorityByUser(createdUser.getId(), AuthorityType.ROLE_NEW_USER);
			if(!Util.isNullObject(newUserAuthority)){
				newUserAuthority.setStatus(SwitchType.PASSIVE.getSwithStatus());
				userAuthorityDao.update(newUserAuthority);
			}
		}
		
		utilService.addEventLog(createdUser.getId(), EventType.AUTHORITY_CHANGE, "New user role is deactivated and CLIENT role given.");
		
		return created;
	}

	private void controlUser(User createdUser) throws ApiException {
		if(Util.isNullObject(createdUser)){
			throw new ApiException("User does not exist!");
		} else if(Util.isNullObject(createdUser.getId())){
			throw new ApiException("User is unacceptable by code 100");
		} 
		
		boolean hasNewUserAuthority = userAuthorityDao.hasAuthorityByUser(createdUser.getId(), AuthorityType.ROLE_NEW_USER);
		
		if(!hasNewUserAuthority){
			throw new ApiException("This user does not have new user authority!");
		}
		
	}

}
