package com.example.instructions.model;

public class PlatformTrade{

	@Override
	public String toString() {
		return "PlatformTrade [platform_id=" + platform_id + ", trade=" + trade + "]";
	}

	public String getPlatform_id() {
		return platform_id;
	}

	public void setPlatform_id(String platform_id) {
		this.platform_id = platform_id;
	}

	public CanonicalTrade getTrade() {
		return trade;
	}

	public void setTrade(CanonicalTrade trade) {
		this.trade = trade;
	}

	String platform_id;
	
	CanonicalTrade trade;
}