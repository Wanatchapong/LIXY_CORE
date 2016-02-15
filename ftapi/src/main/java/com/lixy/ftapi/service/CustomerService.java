package com.lixy.ftapi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.lixy.ftapi.domain.Customer;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.domain.RequestType;
import com.lixy.ftapi.domain.ResponseType;
import com.lixy.ftapi.domain.VirtualCommenter;
import com.lixy.ftapi.domain.VirtualCommenterPrice;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.VirtualCommenterModel;
import com.lixy.ftapi.type.UpdateCreditReason;

public interface CustomerService {

	public Customer getCustomerByUserId(Long userId) throws ApiException;
	
	public Customer getCustomerById(Long id) throws ApiException;
	
	public void updateCustomer(Customer customer);
	
	public VirtualCommenter getVirtualCommenter(Long id);
	
	public List<VirtualCommenter> getVirtualCommenters(Long status);

	public List<VirtualCommenterPrice> getVirtualCommenterPrices(Long vCommenterId, Long status);
	
	public List<VirtualCommenterModel> getVirtualCommenterPrices();
	
	public VirtualCommenterPrice getVirtualCommenterPriceByResponseType(Long vCommenterId, Long responseTypeId, Long status);
	
	public RequestType getRequestType(Long id);

	public Long addNewFortuneRequest(FortuneRequest request);

	public void updateVirtualCommenter(VirtualCommenter virtualCommenter);

	public ResponseType getResponseType(Long responseTypeId);

	public Map<String, String> addCredit(Long adder, Long toUser, BigDecimal creditAmount, UpdateCreditReason reason) throws ApiException;
	
	public Map<String, String> substractCredit(Long adder, Long toUser, BigDecimal creditAmount, UpdateCreditReason reason) throws ApiException;

}
