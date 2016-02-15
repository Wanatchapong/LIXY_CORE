package com.lixy.ftapi.type;

public enum SwitchType {
	ACTIVE(0L), PASSIVE(1L);
	
	private long swithStatus;
	
	private SwitchType(long status){
		this.setSwithStatus(status);
	}

	public long getSwithStatus() {
		return swithStatus;
	}

	public void setSwithStatus(long swithStatus) {
		this.swithStatus = swithStatus;
	}
	
}
