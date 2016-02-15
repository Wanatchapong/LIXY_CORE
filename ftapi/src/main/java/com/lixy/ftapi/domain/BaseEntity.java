package com.lixy.ftapi.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 3967453158129709276L;
	
	@JsonIgnore
	@Column(name = "CREATED_BY", length = 20)
    protected String createdBy;
    
	
	@Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdDate;
    
	@JsonIgnore
	@Column(name = "CREATED_MACHINE", length = 50)
    protected String createdMachine;
    
    @JsonIgnore
    @Column(name = "MODIFIED_BY", length = 20)
    protected String modifiedBy;
    
    @Column(name = "MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedDate;
    
    @JsonIgnore
    @Column(name = "MODIFIED_MACHINE", length = 50)
    protected String modifiedMachine;
    
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedMachine() {
		return createdMachine;
	}
	public void setCreatedMachine(String createdMachine) {
		this.createdMachine = createdMachine;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedMachine() {
		return modifiedMachine;
	}
	public void setModifiedMachine(String modifiedMachine) {
		this.modifiedMachine = modifiedMachine;
	}
    
   

}