package com.lixy.ftapi.model;

import com.lixy.ftapi.exception.ApiException;
import com.lixy.ftapi.type.StatusType;
import com.lixy.ftapi.util.Util;

public class GResponse{
	private Long id;
	private Long uid; //if necessary user id
	
	private StatusType status;
	
	private String respCode;
	private String respMessage;
	
	private Object object;
	
	public GResponse(){
		this.status = StatusType.OK;	
		this.id = Util.getSimpleNumberUniqueId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public String getRespMessage() {
		return respMessage;
	}

	public void setRespMessage(String respMessage) {
		this.respMessage = respMessage;
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "GResponse [id=" + id + ", uid=" + uid + ", status=" + status + ", respCode=" + respCode + ", respMessage=" + respMessage + "]";
	}

	public void convertToGResponse(ApiException a){
		this.status = "0".equals(a.getRespCode()) ? StatusType.OK : StatusType.FAIL;
		this.respMessage = a.getMessage();
		this.respCode = a.getRespCode();
	}
	
	public void convertToGResponse(Exception a){
		
		if(a instanceof ApiException){
			convertToGResponse((ApiException) a);
		} else {
			this.status = StatusType.FAIL;
			this.respMessage = a.getCause() + "-" +a.getMessage();
		}
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	
}
