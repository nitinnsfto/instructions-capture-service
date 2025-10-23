package com.example.instructions.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.PlatformTrade;

@Component
public class TradeTransformer {

	public List<PlatformTrade> normalize(List<CanonicalTrade> trades) {
		List<PlatformTrade> platformTrades = new ArrayList<PlatformTrade>();
		
		for (CanonicalTrade canonicalTrade: trades) {
			PlatformTrade platformTrade = new PlatformTrade();
			platformTrades.add(platformTrade);
			String account = canonicalTrade.getAccount();
			int length=account.length();
			canonicalTrade.setAccount("*".repeat(length-4)+account.substring(length-5));
			platformTrade.setTrade(canonicalTrade);
		}
		return platformTrades;
	}
	
}