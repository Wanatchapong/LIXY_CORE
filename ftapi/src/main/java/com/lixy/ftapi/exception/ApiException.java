package com.lixy.ftapi.exception;

public class ApiException extends Exception {

	private static final long serialVersionUID = -6689843248685556065L;
	
	private String respCode; // NOSONAR
	private Object respObject; // NOSONAR

	public ApiException() {
		//true
	}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(Throwable cause) {
		super(cause);
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ApiException(String respCode, String message) {
		super(message);
		setRespCode(respCode);
	}
	
	public ApiException(String respCode, String message, Object o) {
		super(message);
		setRespCode(respCode);
		setRespObject(o);
	}

	public ApiException(String respCode, String message, Throwable cause) {
		super(message, cause);
		setRespCode(respCode);
	}
	
	public ApiException(String respCode, String message, Throwable cause, Object o) {
		super(message, cause);
		setRespCode(respCode);
		setRespObject(o);
	}
	
	public Object getRespObject() {
		return respObject;
	}

	public void setRespObject(Object respObject) {
		this.respObject = respObject;
	}
	
	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

}
