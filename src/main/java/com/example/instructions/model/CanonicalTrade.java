package com.example.instructions.model;

import java.sql.Timestamp;

public class CanonicalTrade{

	@Override
	public String toString() {
		return "CanonicalTrade [account=" + account + ", security=" + security + ", type=" + type + ", amount=" + amount
				+ ", timstamp=" + timstamp + "]";
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Timestamp getTimstamp() {
		return timstamp;
	}

	public void setTimstamp(Timestamp timstamp) {
		this.timstamp = timstamp;
	}

	String account;
	
	String security;
	
	String type;
	
	double amount;
	
	Timestamp timstamp;
	
	
}