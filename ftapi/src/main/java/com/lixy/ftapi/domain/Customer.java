package com.lixy.ftapi.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "customer", catalog = "")
public class Customer implements Serializable{
	
	private static final long serialVersionUID = -4446929343152142811L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "user_id")
    private Long userId;
	
	@Column(name = "credit")
	private BigDecimal credit;

	@Column(name = "total_credit_spent")
	private BigDecimal totalCreditSpent;
	
	@Column(name = "total_fortune")
	protected Long totalFortune;
	
	@Column(name = "total_payment_amount")
	private BigDecimal totalPaymentAmount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public BigDecimal getTotalCreditSpent() {
		return totalCreditSpent;
	}

	public void setTotalCreditSpent(BigDecimal totalCreditSpent) {
		this.totalCreditSpent = totalCreditSpent;
	}

	public Long getTotalFortune() {
		return totalFortune;
	}

	public void setTotalFortune(Long totalFortune) {
		this.totalFortune = totalFortune;
	}

	public BigDecimal getTotalPaymentAmount() {
		return totalPaymentAmount;
	}

	public void setTotalPaymentAmount(BigDecimal totalPaymentAmount) {
		this.totalPaymentAmount = totalPaymentAmount;
	}

}
