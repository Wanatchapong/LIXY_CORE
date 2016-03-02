package com.lixy.ftapi.service;

import java.util.List;
import java.util.Map;

import com.lixy.ftapi.domain.FortuneRequest;
import com.lixy.ftapi.domain.FortuneRequestDetail;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.model.ConversationModel;
import com.lixy.ftapi.model.FortuneInfo;
import com.lixy.ftapi.model.FortuneRequestModel;
import com.lixy.ftapi.type.ConversationStatusType;
import com.lixy.ftapi.type.RequestStatusType;

public interface FortuneService {
	
	public List<FortuneInfo> getFortuneRequestByUserId(Long userId, RequestStatusType status) throws ApiException;

	public FortuneRequest getFortuneRequestById(Long requestId) throws ApiException;
	
	public List<FortuneRequestDetail> getFortuneRequestDetailsByRequestId(Long requestId);

	public Long insertFortuneRequest(User user, FortuneRequestModel request) throws ApiException;

	public boolean hasNecessaryDetails(FortuneRequestModel model);

	public void updateFortuneRequest(FortuneRequest request);

	public void updateFortuneRequestDetail(FortuneRequestDetail detail);

	public Map<String, FortuneRequestDetail> getFortuneRequestDetails(Long requestId);
	
	public void convertToRequestModel(FortuneRequest request, FortuneInfo info) throws ApiException;

	public FortuneInfo convertToRequestModel(FortuneRequest request) throws ApiException;

	public void checkFortuneRequestContent(FortuneRequest request, Map<String, String> details) throws ApiException;
		
	public List<FortuneInfo> convertAllToFortuneInfo(List<FortuneRequest> request);
	
	public Long createConversation(Long requestId, Long transactionLimit, ConversationStatusType status) throws ApiException;
	
	public List<ConversationModel> getConversationList(Long requestId);

	public List<ConversationModel> getConversationListForRoot(Long requestId);

}
