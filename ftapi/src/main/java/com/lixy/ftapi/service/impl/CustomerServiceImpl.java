package com.lixy.ftapi.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.dao.CustomerDao;
import com.lixy.ftapi.dao.FortuneRequestDao;
import com.lixy.ftapi.dao.RequestTypeDao;
import com.lixy.ftapi.dao.ResponseTypeDao;
import com.lixy.ftapi.dao.VirtualCommenterDao;
import com.lixy.ftapi.dao.VirtualCommenterPriceDao;
import com.lixy.ftapi.domain.Customer;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.domain.RequestType;
import com.lixy.ftapi.domain.ResponseType;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.VirtualCommenter;
import com.lixy.ftapi.domain.VirtualCommenterPrice;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.VirtualCommenterModel;
import com.lixy.ftapi.service.CustomerService;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.AuthorityType;
import com.lixy.ftapi.type.EventType;
import com.lixy.ftapi.type.SwitchType;
import com.lixy.ftapi.type.UpdateCreditReason;
import com.lixy.ftapi.util.Util;

@Service("customerService")
@Transactional
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UtilService utilService;

	@Autowired
	@Qualifier("virtualCommenterDaoImpl")
	private VirtualCommenterDao virtualCommenterDao;
	
	@Autowired
	@Qualifier("virtualCommenterPriceDaoImpl")
	private VirtualCommenterPriceDao virtualCommenterPriceDao;
	
	@Autowired
	@Qualifier("requestTypeDaoImpl")
	private RequestTypeDao requestTypeDao;
	
	@Autowired
	@Qualifier("responseTypeDaoImpl")
	private ResponseTypeDao responseTypeDao;

	@Autowired
	@Qualifier("customerDaoImpl")
	private CustomerDao customerDao;
	
	@Autowired
	@Qualifier("fortuneRequestDaoImpl")
	private FortuneRequestDao fortuneRequestDao;
	
	@Override
	public List<VirtualCommenter> getVirtualCommenters(Long status) {
		List<VirtualCommenter> commenters = virtualCommenterDao.loadAll();
		
		if(!Util.isNullObject(status)){
			Iterator<VirtualCommenter> i = commenters.iterator();
			while (i.hasNext()) {
				VirtualCommenter commenter = i.next(); // must be called before you can call i.remove()
				if(commenter.getStatus().longValue() != status.longValue()){
					i.remove();
				}
			}
		}
		
		return commenters;
	}

	@Override
	public VirtualCommenter getVirtualCommenter(Long id) {
		return virtualCommenterDao.readById(id);
	}

	@Override
	public List<VirtualCommenterPrice> getVirtualCommenterPrices(Long vCommenterId, Long status) {
		return virtualCommenterPriceDao.getVirtualCommenterPrices(vCommenterId, status);
	}

	@Override
	public List<VirtualCommenterModel> getVirtualCommenterPrices() {
		List<VirtualCommenterModel> virtualCommenters = new ArrayList<>();
				
		List<VirtualCommenter> commenters = getVirtualCommenters(SwitchType.ACTIVE.getSwithStatus());		
		for (VirtualCommenter virtualCommenter : commenters) {
			List<VirtualCommenterPrice> prices = getVirtualCommenterPrices(virtualCommenter.getId(), SwitchType.ACTIVE.getSwithStatus());
			virtualCommenters.add(new VirtualCommenterModel(virtualCommenter, prices));
		}
		
		return virtualCommenters;
	}

	@Override
	public VirtualCommenterPrice getVirtualCommenterPriceByResponseType(Long vCommenterId, Long responseTypeId, Long status) {
		return virtualCommenterPriceDao.getVirtualCommenterPriceByResponseType(vCommenterId, responseTypeId, status);
	}

	@Override
	public RequestType getRequestType(Long id) {
		return requestTypeDao.readById(id);
	}

	@Override
	public Long addNewFortuneRequest(FortuneRequest request) {
		return fortuneRequestDao.create(request);		
	}

	@Override
	public Customer getCustomerById(Long id) throws ApiException {
		if(!Util.isNullObject(id)){
			Customer customer = customerDao.readById(id);
			if(Util.isNullObject(customer)){
				throw new ApiException("-909", utilService.getMessage("user.notfound"));
			} else {
				return customer;
			}
		}
		else {
			throw new ApiException("-909", utilService.getMessage("user.notfound"));
		}
	}

	@Override
	public Customer getCustomerByUserId(Long userId) throws ApiException {
		if(!Util.isNullObject(userId)){
			Customer customer = customerDao.getCustomerByUserId(userId);
			if(Util.isNullObject(customer)){
				throw new ApiException("-909", utilService.getMessage("user.notfound"));
			} else {
				return customer;
			}
		}
		else {
			throw new ApiException("-909", utilService.getMessage("user.notfound"));
		}
	}

	@Override
	public void updateCustomer(Customer customer) {
		customerDao.update(customer);
	}

	@Override
	public void updateVirtualCommenter(VirtualCommenter virtualCommenter) {
		virtualCommenterDao.update(virtualCommenter);
	}

	@Override
	public ResponseType getResponseType(Long responseTypeId) {
		return responseTypeDao.readById(responseTypeId);
	}

	@Override
	public Map<String, String> addCredit(Long adderId, Long toUserId, BigDecimal creditAmount, UpdateCreditReason reason) throws ApiException {
		Map<String, String> resultMap = new HashMap<>();
		
		User from = userService.getUserById(adderId);
		Customer to = getCustomerByUserId(toUserId);
		
		BigDecimal currentBalance = to.getCredit();
		BigDecimal newBalance = to.getCredit().add(creditAmount);
		
		if(newBalance.compareTo(BigDecimal.ZERO) == -1){ //less than zero
			throw new ApiException("-109", utilService.getMessage("balance.notenaugh"));
		}
		
		if(from.hasAuthority(AuthorityType.ROLE_ROOT)){ //eklemeyi yapan kisi root ise
			utilService.addEventLog(toUserId, EventType.CREDIT_CHANGE, currentBalance + "->" + newBalance + "(" + adderId + ") ->" + reason.toString());			
		} else {
			utilService.addEventLog(toUserId, EventType.CREDIT_CHANGE, currentBalance + "->" + newBalance + "->" + reason.toString());
		}

		to.setCredit(newBalance);
		updateCustomer(to);
		
		resultMap.put("RECENT_CREDIT", currentBalance + "");
		resultMap.put("FINAL_CREDIT", newBalance + "");
		
		return resultMap;
	}

	@Override
	public Map<String, String> substractCredit(Long adder, Long toUser, BigDecimal creditAmount,
			UpdateCreditReason reason) throws ApiException {
		return addCredit(adder, toUser, creditAmount.multiply(new BigDecimal(-1)), reason);
	}
	

}
