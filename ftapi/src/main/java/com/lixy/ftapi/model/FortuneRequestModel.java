package com.lixy.ftapi.model;

import java.util.Map;

import com.lixy.ftapi.domain.FortuneRequest;

public class FortuneRequestModel {
	private FortuneRequest fortuneRequest;
	private Map<String, String> details;

	public FortuneRequest getFortuneRequest() {
		return fortuneRequest;
	}

	public void setFortuneRequest(FortuneRequest fortuneRequest) {
		this.fortuneRequest = fortuneRequest;
	}

	public Map<String, String> getDetails() {
		return details;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}

}
