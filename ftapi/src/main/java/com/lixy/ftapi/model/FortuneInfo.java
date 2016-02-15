package com.lixy.ftapi.model;

import java.math.BigDecimal;
import java.util.Map;

import com.lixy.ftapi.domain.FortuneRequestDetail;
import com.lixy.ftapi.domain.RequestType;
import com.lixy.ftapi.domain.ResponseType;
import com.lixy.ftapi.domain.User;
import com.lixy.ftapi.domain.VirtualCommenter;

public class FortuneInfo {
	
	private Long requestId;
	private String requestStatus;
	
	private String name;
	private String surname;
	private String gender;
	private String maritalStatus;
	private BigDecimal paidCredit;
	
	private User requester;
	private User owner;
	
	private VirtualCommenter virtualCommenter;
	
	private RequestType requestType;
	private ResponseType responseType;
	
	private Map<String, FortuneRequestDetail> details;

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public BigDecimal getPaidCredit() {
		return paidCredit;
	}

	public void setPaidCredit(BigDecimal paidCredit) {
		this.paidCredit = paidCredit;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public VirtualCommenter getVirtualCommenter() {
		return virtualCommenter;
	}

	public void setVirtualCommenter(VirtualCommenter virtualCommenter) {
		this.virtualCommenter = virtualCommenter;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public Map<String, FortuneRequestDetail> getDetails() {
		return details;
	}

	public void setDetails(Map<String, FortuneRequestDetail> details) {
		this.details = details;
	}
	
	
}
