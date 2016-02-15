package com.lixy.ftapi.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "fortune_request", catalog = "")
public class FortuneRequest extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 5455321524419393043L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Basic(optional=false)
	@Column(name="requester_id")
	private Long requesterId;

	@Basic(optional=false)
	@Column(name="vcommenter_id")
	private Long virtualCommenterId;
	
	@Column(name="owner_id")
	private Long ownerId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="surname")
	private String surname;

	@Column(name="gender")
	private String gender;
	
	@Column(name="marital_status")
	private String maritalStatus;
	
	@Basic(optional=false)
	@Column(name="paid_credit")
	private BigDecimal paidCredit;
	
	@Column(name="request_status")
	private String requestStatus;
	
	@Basic(optional=false)

	@Column(name="request_type_id")
	private Long requestTypeId;
	
	@Column(name="request_locale")
	private String requestLocale;
	
	@Basic(optional=false)
	@Column(name="response_type_id")
	private Long responseTypeId;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "requestId", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<FortuneRequestDetail> requestDetails;

	public Long getResponseTypeId() {
		return responseTypeId;
	}

	public void setResponseTypeId(Long responseTypeId) {
		this.responseTypeId = responseTypeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(Long requesterId) {
		this.requesterId = requesterId;
	}

	public Long getVirtualCommenterId() {
		return virtualCommenterId;
	}

	public void setVirtualCommenterId(Long virtualCommenterId) {
		this.virtualCommenterId = virtualCommenterId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
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

	public String getRequestLocale() {
		return requestLocale;
	}

	public void setRequestLocale(String requestLocale) {
		this.requestLocale = requestLocale;
	}

	public BigDecimal getPaidCredit() {
		return paidCredit;
	}

	public void setPaidCredit(BigDecimal paidCredit) {
		this.paidCredit = paidCredit;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public Long getRequestTypeId() {
		return requestTypeId;
	}

	public void setRequestTypeId(Long requestTypeId) {
		this.requestTypeId = requestTypeId;
	}

	public Set<FortuneRequestDetail> getRequestDetails() {
		return requestDetails;
	}

	public void setRequestDetails(Set<FortuneRequestDetail> requestDetails) {
		this.requestDetails = requestDetails;
	}
	
	public FortuneRequestDetail searchDetailItem(String tag){
		for (FortuneRequestDetail fortuneRequestDetail : requestDetails) {
			if(fortuneRequestDetail.getTag().equals(tag))
				return fortuneRequestDetail;
		}
		return null;
	}
	
	public Map<String, FortuneRequestDetail> getDetails(){
		Map<String, FortuneRequestDetail> details = new HashMap<>();
		for (FortuneRequestDetail fortuneRequestDetail : requestDetails) {
			details.put(fortuneRequestDetail.getTag(), fortuneRequestDetail);
		}
		return details;
	}
	
	@Override
	public String toString() {
		return "FortuneRequest [id=" + id + ", requesterId=" + requesterId + ", requestDetails=" + requestDetails + "]";
	}


}
