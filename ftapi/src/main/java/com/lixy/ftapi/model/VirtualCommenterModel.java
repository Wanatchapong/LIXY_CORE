package com.lixy.ftapi.model;

import java.util.List;

import com.lixy.ftapi.domain.VirtualCommenter;
import com.lixy.ftapi.domain.VirtualCommenterPrice;

public class VirtualCommenterModel {
	
	private VirtualCommenter virtualCommenter;
	private List<VirtualCommenterPrice> prices;
	
	public VirtualCommenterModel(VirtualCommenter commenter){
		this.virtualCommenter = commenter;
	}
	
	public VirtualCommenterModel(VirtualCommenter commenter, List<VirtualCommenterPrice> prices){
		this.virtualCommenter = commenter;
		this.prices = prices;
	}
	
	public VirtualCommenter getVirtualCommenter() {
		return virtualCommenter;
	}
	public void setVirtualCommenter(VirtualCommenter virtualCommenter) {
		this.virtualCommenter = virtualCommenter;
	}
	public List<VirtualCommenterPrice> getPrices() {
		return prices;
	}
	public void setPrices(List<VirtualCommenterPrice> prices) {
		this.prices = prices;
	}
	
}
