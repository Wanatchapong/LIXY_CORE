package com.lixy.ftapi.type;

public enum UserStatus {
	ACTIVE(0L), PASSIVE(1L);
	
	private long status;
	
	private UserStatus(long status){
		this.setUserStatus(status);
	}

	public long getUserStatus() {
		return status;
	}

	public void setUserStatus(long userStatus) {
		this.status = userStatus;
	}
	
	
}

