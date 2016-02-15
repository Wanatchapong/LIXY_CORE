package com.lixy.ftapi.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "vcommenter", catalog = "")
public class VirtualCommenter implements Serializable {

	private static final long serialVersionUID = 5455321524419393043L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "surname")
	private String surname;
	
	@Column(name = "description")
	private String description;

	@JsonIgnore
	@Column(name = "status")
	private Long status;
	
	@JsonIgnore
	@Column(name = "total_fortune_count")
	private Long totalFortuneCount;

	@Column(name = "last_fortune_time")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date lastFortuneDate;
	
	public String getFullName(){
		return (name == null ? "": name) + " " + (surname == null ? "": surname);
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
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


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	public Long getStatus() {
		return status;
	}


	public void setStatus(Long status) {
		this.status = status;
	}


	public Long getTotalFortuneCount() {
		return totalFortuneCount;
	}


	public void setTotalFortuneCount(Long totalFortuneCount) {
		this.totalFortuneCount = totalFortuneCount;
	}


	public Date getLastFortuneDate() {
		return lastFortuneDate;
	}


	public void setLastFortuneDate(Date lastFortuneDate) {
		this.lastFortuneDate = lastFortuneDate;
	}


	@Override
	public String toString() {
		return "VirtualCommenter [id=" + id + ", name=" + name + "]";
	}
	
}
