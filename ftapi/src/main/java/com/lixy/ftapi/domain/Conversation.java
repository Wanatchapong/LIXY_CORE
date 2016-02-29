package com.lixy.ftapi.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "conversation", catalog = "")
public class Conversation implements Serializable {

	private static final long serialVersionUID = -4446929343152142811L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@JoinColumn(name = "request_id", referencedColumnName = "id")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private FortuneRequest fortuneRequest;

	@Column(name = "status")
	private String status;

	@Column(name = "transaction_limit")
	private Long transactionLimit;

	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createdDate;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "conversation", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ConversationMessage> conversationMessages;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ConversationMessage> getConversationMessages() {
		return conversationMessages;
	}

	public void setConversationMessages(List<ConversationMessage> conversationMessages) {
		this.conversationMessages = conversationMessages;
	}

	public FortuneRequest getFortuneRequest() {
		return fortuneRequest;
	}

	public void setFortuneRequest(FortuneRequest fortuneRequest) {
		this.fortuneRequest = fortuneRequest;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

	public Long getTransactionLimit() {
		return transactionLimit;
	}

	public void setTransactionLimit(Long transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	
}
