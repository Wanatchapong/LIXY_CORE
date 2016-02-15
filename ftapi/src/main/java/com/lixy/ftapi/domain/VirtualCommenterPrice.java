package com.lixy.ftapi.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "vcommenter_price", catalog = "")
public class VirtualCommenterPrice implements Serializable {

	private static final long serialVersionUID = 5455321524419393043L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "vcommenter_id")
	private Long vcommenterId;
	
	@JoinColumn(name = "response_type", referencedColumnName = "id")
	@ManyToOne(optional = false, fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private ResponseType responseType;

	@Column(name = "credit")
	private BigDecimal credit;
	
	@JsonIgnore
	@Column(name = "status")
	private Long status;
	
	@Column(name = "max_transaction_count")
	private Long maxTransactionCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVcommenterId() {
		return vcommenterId;
	}

	public void setVcommenterId(Long vcommenterId) {
		this.vcommenterId = vcommenterId;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public Long getMaxTransactionCount() {
		return maxTransactionCount;
	}

	public void setMaxTransactionCount(Long maxTransactionCount) {
		this.maxTransactionCount = maxTransactionCount;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "VirtualCommenterPrice [id=" + id + ", commenter=" + vcommenterId + ", responseType=" + responseType + ", credit=" + credit + ", status=" + status + "]";
	}
	
}
