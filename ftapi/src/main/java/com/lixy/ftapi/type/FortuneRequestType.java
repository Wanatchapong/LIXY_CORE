package com.lixy.ftapi.type;

public enum FortuneRequestType {
	PIC_TEXP(1L),
	VID_TEXP(2L),
	PIC_VEXP(3L),
	PIC_AEXP(4L);
	
	private Long id;
	
	private FortuneRequestType(Long id){
		this.setId(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	
}
