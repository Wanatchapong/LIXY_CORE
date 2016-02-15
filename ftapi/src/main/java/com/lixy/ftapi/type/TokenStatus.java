package com.lixy.ftapi.type;

public enum TokenStatus {
	ACTIVE(0L), EXPIRED_TIME(1L), EXPIRED_MANAGER(2L);
	
	private long status;
	
	private TokenStatus(long status){
		this.setTokenStatus(status);
	}

	public long getTokenStatus() {
		return status;
	}

	public void setTokenStatus(long tokenStatus) {
		this.status = tokenStatus;
	}
	
	
}
