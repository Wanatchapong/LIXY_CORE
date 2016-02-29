package com.lixy.ftapi.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixy.ftapi.dao.ConversationDao;
import com.lixy.ftapi.dao.FortuneRequestDao;
import com.lixy.ftapi.dao.FortuneRequestDetailDao;
import com.lixy.ftapi.domain.Conversation;
import com.lixy.ftapi.domain.Customer;
import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.domain.FortuneRequestDetail;
import com.lixy.ftapi.domain.RequestType;
import com.lixy.ftapi.domain.ResponseType;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.VirtualCommenter;
import com.lixy.ftapi.domain.VirtualCommenterPrice;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.FortuneInfo;
import com.lixy.ftapi.model.FortuneRequestModel;
import com.lixy.ftapi.queue.manager.QueueManager;
import com.lixy.ftapi.service.CustomerService;
import com.lixy.ftapi.service.FortuneService;
import com.lixy.ftapi.service.UserService;
import com.lixy.ftapi.service.UtilService;
import com.lixy.ftapi.type.ConversationStatusType;
import com.lixy.ftapi.type.EventType;
import com.lixy.ftapi.type.FortuneRequestType;
import com.lixy.ftapi.type.RequestStatusType;
import com.lixy.ftapi.type.SwitchType;
import com.lixy.ftapi.type.UpdateCreditReason;
import com.lixy.ftapi.util.Util;

@Service("fortuneService")
@Transactional
public class FortuneServiceImpl implements FortuneService {

	private static Logger logger = LogManager.getLogger(FortuneServiceImpl.class);

	@Autowired
	@Qualifier("fortuneRequestDaoImpl")
	private FortuneRequestDao fortuneRequestDao;

	@Autowired
	@Qualifier("fortuneRequestDetailDaoImpl")
	private FortuneRequestDetailDao fortuneRequestDetailDao;
	
	@Autowired
	@Qualifier("conversationDaoImpl")
	private ConversationDao conversationDao;

	@Autowired
	private UtilService utilService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private UserService userService;

	@Autowired
	private QueueManager queueManager;

	@Override
	public FortuneRequest getFortuneRequestById(Long requestId) throws ApiException {
		FortuneRequest request = fortuneRequestDao.readById(requestId);
		if (Util.isNullObject(request)) {
			throw new ApiException("100", "Request Not Found");
		}
		return request;
	}
	
	@Override
	public void checkFortuneRequestContent(FortuneRequest request, Map<String, String> details) throws ApiException{ // NOSONAR
		if (Util.isNullObject(request))
			throw new ApiException("100", utilService.getMessage("fortune.request.not.exist"));
		else if (Util.isNullOrEmptyHashMap(details))
			throw new ApiException("101", utilService.getMessage("fortune.details.not.exist"));
		else if (Util.isNullObject(request.getVirtualCommenterId()))
			throw new ApiException("102", utilService.getMessage("fortune.vcommenter.not.exist"));
		else if (Util.isNullObject(request.getPaidCredit()))
			throw new ApiException("104", utilService.getMessage("fortune.credit.not.exist"));
		else if (Util.isNullObject(request.getResponseTypeId()))
			throw new ApiException("105", utilService.getMessage("fortune.responsetype.not.exist"));
		else if (Util.isNullObject(request.getRequestTypeId()))
			throw new ApiException("108", utilService.getMessage("fortune.requesttype.not.exist"));
	}

	@Override
	public Long insertFortuneRequest(User authUser, FortuneRequestModel request) throws ApiException { // NOSONAR
		FortuneRequest main = request.getFortuneRequest();
		Map<String, String> detail = request.getDetails();

		checkFortuneRequestContent(main, detail);

		main.setId(null); // it must be
		main.setRequesterId(authUser.getId()); // it must be
		main.setRequestStatus(RequestStatusType.INITIAL.getShortCode()); // it must be
		
		if(Util.isNullOrEmpty(main.getRequestLocale()))
			main.setRequestLocale(utilService.getLocale() == null ? "tr" : utilService.getLocale().getLanguage());

		RequestType requestType = customerService.getRequestType(main.getRequestTypeId());
		if (Util.isNullObject(requestType))
			throw new ApiException("109", utilService.getMessage("fortune.requesttype.not.exist"));
		else if (requestType.getStatus().longValue() != 0L)
			throw new ApiException("110", utilService.getMessage("fortune.teller.not.accept.requesttype"));

		VirtualCommenter virtualCommenter = customerService.getVirtualCommenter(main.getVirtualCommenterId());
		if (Util.isNullObject(virtualCommenter))
			throw new ApiException("103", utilService.getMessage("fortune.unknown.vcommenter"));
		else if (virtualCommenter.getStatus().longValue() != 0L)
			throw new ApiException("114", utilService.getMessage("fortune.deactive.vcommenter"));

		if (Util.isNullOrEmptyHashMap(detail))
			throw new ApiException("115", utilService.getMessage("fortune.detail.not.exist"));
		if (!hasNecessaryDetails(request))
			throw new ApiException("116", utilService.getMessage("fortune.details.not.enaugh"));

		// price operations
		VirtualCommenterPrice price = customerService.getVirtualCommenterPriceByResponseType(virtualCommenter.getId(), main.getResponseTypeId(), SwitchType.ACTIVE.getSwithStatus());
		if (Util.isNullObject(price))
			throw new ApiException("106", utilService.getMessage("fortune.price.not.exist"));

		if (price.getCredit().compareTo(main.getPaidCredit()) != 0) { // prices are different
			throw new ApiException("107", utilService.getMessage("fortune.price.different"));
		}

		User user = userService.getUserById(authUser.getId());

		if (Util.isNullObject(user))
			throw new ApiException("111", utilService.getMessage("user.notfound"));

		userService.checkUserSuitableForProcess(user.getId());

		Customer customer = customerService.getCustomerByUserId(user.getId());

		if (Util.isNullObject(customer))
			throw new ApiException("112", utilService.getMessage("customer.notfound"));

		BigDecimal customerBalance = customer.getCredit();

		if (customerBalance.compareTo(price.getCredit()) < 0)
			throw new ApiException("113", utilService.getMessage("balance.notenaugh"));
		
		customerService.substractCredit(user.getId(), user.getId(), price.getCredit(), UpdateCreditReason.NEW_FORTUNE);
		
		customer.setTotalCreditSpent(customer.getTotalCreditSpent().add(price.getCredit()));
		customer.setTotalFortune(customer.getTotalFortune().longValue() + 1);

		customerService.updateCustomer(customer);

		Long id = customerService.addNewFortuneRequest(main);

		for (String key : detail.keySet()) { // NOSONAR
			FortuneRequestDetail subItem = new FortuneRequestDetail();
			subItem.setRequestId(id);
			subItem.setTag(key);
			subItem.setValue(detail.get(key));

			fortuneRequestDetailDao.create(subItem);
		}				

		virtualCommenter.setTotalFortuneCount(virtualCommenter.getTotalFortuneCount() + 1L);
		customerService.updateVirtualCommenter(virtualCommenter);
		
		utilService.addEventLog(user.getId(), EventType.NEW_FORTUNE, "Fortune with " + id + " is added. Paid credit : " + main.getPaidCredit());

		try {
			queueManager.sendToNewFortuneQueue(id);
		} catch (Exception ex) {
			logger.error("Could not be sent to new fortune queue. -> " + id, ex);
		}

		logger.info("New fortune request created." + user + ", request id" + id);

		return id;
	}

	@Override
	public boolean hasNecessaryDetails(FortuneRequestModel model) {
		RequestType requestType = customerService.getRequestType(model.getFortuneRequest().getRequestTypeId());
		if (requestType.getId().longValue() == FortuneRequestType.PIC_TEXP.getId().longValue()) {
			// Has to have PIC1, PIC2, PIC3, DESC
			String[] keys = { "PIC1", "PIC2", "PIC3", "DESC" };
			return model.getDetails().keySet().containsAll(Arrays.asList(keys));
		} else if (requestType.getId().longValue() == FortuneRequestType.PIC_AEXP.getId().longValue()) {
			// Has to have PIC1, PIC2, PIC3, AUDIO
			String[] keys = { "PIC1", "PIC2", "PIC3", "AUDIO" };
			return model.getDetails().keySet().containsAll(Arrays.asList(keys));
		} else if (requestType.getId().longValue() == FortuneRequestType.PIC_VEXP.getId().longValue()) {
			// Has to have PIC1, PIC2, PIC3, VIDEO
			String[] keys = { "PIC1", "PIC2", "PIC3", "VIDEO" };
			return model.getDetails().keySet().containsAll(Arrays.asList(keys));
		} else if (requestType.getId().longValue() == FortuneRequestType.VID_TEXP.getId().longValue()) {
			// Has to have VIDEO, DESC
			String[] keys = { "VIDEO", "DESC" };
			return model.getDetails().keySet().containsAll(Arrays.asList(keys));
		}

		return false;
	}

	@Override
	public void updateFortuneRequest(FortuneRequest request) {
		fortuneRequestDao.update(request);
	}

	@Override
	public void updateFortuneRequestDetail(FortuneRequestDetail detail) {
		fortuneRequestDetailDao.update(detail);
	}

	@Override
	public List<FortuneRequestDetail> getFortuneRequestDetailsByRequestId(Long requestId) {
		return fortuneRequestDetailDao.getFortuneRequestDetailsByRequestId(requestId);
	}

	@Override
	public Map<String, FortuneRequestDetail> getFortuneRequestDetails(Long requestId) {
		List<FortuneRequestDetail> details = getFortuneRequestDetailsByRequestId(requestId);
		Map<String, FortuneRequestDetail> detailsMap = new HashMap<>();

		for (FortuneRequestDetail fortuneRequestDetail : details) {
			detailsMap.put(fortuneRequestDetail.getTag(), fortuneRequestDetail);
		}

		return detailsMap;
	}

	@Override
	public List<FortuneInfo> getFortuneRequestByUserId(Long userId, RequestStatusType status) throws ApiException {
		userService.getUserById(userId); //check user exist
		
		List<FortuneInfo> infos = new ArrayList<>();
		List<FortuneRequest> requests = fortuneRequestDao.getFortuneRequestByUserId(userId, status);
		for (FortuneRequest	 fortuneRequest : requests) {
			infos.add(convertToRequestModel(fortuneRequest));
		}

		return infos;
	}

	@Override
	public FortuneInfo convertToRequestModel(FortuneRequest request) throws ApiException {
		FortuneInfo info = new FortuneInfo();
		convertToRequestModel(request, info);
		return info;
	}

	@Override
	public void convertToRequestModel(FortuneRequest request, FortuneInfo info) throws ApiException {
		User requester = userService.getUserById(request.getRequesterId());

		if (!Util.isNullObject(requester))
			requester.getAuthorities().clear();

		if (!Util.isNullObject(request.getOwnerId())) {
			User owner = userService.getUserById(request.getOwnerId());
			if (!Util.isNullObject(owner)){
				owner.getAuthorities().clear();
				info.setOwner(owner);
			}
		}

		VirtualCommenter virtualCommenter = customerService.getVirtualCommenter(request.getVirtualCommenterId());

		RequestType requestType = customerService.getRequestType(request.getRequestTypeId());
		ResponseType responseType = customerService.getResponseType(request.getResponseTypeId());

		info.setRequestId(request.getId());
		info.setRequestStatus(request.getRequestStatus());
		info.setName(request.getName());
		info.setSurname(request.getSurname());
		info.setGender(request.getGender());
		info.setMaritalStatus(request.getMaritalStatus());
		info.setPaidCredit(request.getPaidCredit());

		info.setRequester(requester);
		info.setVirtualCommenter(virtualCommenter);

		info.setRequestType(requestType);
		info.setResponseType(responseType);

		info.setDetails(request.getDetails());
		
		info.setCreatedDate(request.getCreatedDate());
		info.setLastModifiedDate(request.getModifiedDate());
	}

	@Override
	public Long createConversation(Long requestId, Long transactionLimit, ConversationStatusType status) throws ApiException {
		FortuneRequest request = getFortuneRequestById(requestId);
		
		Conversation conversation = new Conversation();
		conversation.setFortuneRequest(request);
		conversation.setStatus(status.getShortCode());
		conversation.setTransactionLimit(transactionLimit);
		
		return conversationDao.create(conversation);
	}
	
	@Override
	public List<Conversation> getConversationList(Long requestId){
		return conversationDao.getConversationListByRequestId(requestId);
	}

	@Override
	public List<FortuneInfo> convertAllToFortuneInfo(List<FortuneRequest> request) {
		if(Util.isNullObject(request))
			return null;
		
		List<FortuneInfo> infos = new ArrayList<>();
		for (FortuneRequest fortuneRequest : request) {
			try {
				infos.add(convertToRequestModel(fortuneRequest));
			} catch (ApiException e) {
				logger.error("APIEX", e);
			}
		}
		
		return infos;
	}
}
