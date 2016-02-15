package com.lixy.ftapi.type;

public enum FileFormatType {
	JPG(".jpg"),
	
	;
	
	private String format;
	
	private FileFormatType(String format){
		this.setFormat(format);
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	
}
