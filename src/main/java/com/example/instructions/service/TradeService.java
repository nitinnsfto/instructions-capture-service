package com.example.instructions.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.util.TradeTransformer;

public class TradeService{

	@Autowired
	TradeTransformer tradeTransformer;
	
	@Autowired
	KafkaPublisher kafkaPublisher;
	
	public String upload(List<CanonicalTrade> trades) {
		kafkaPublisher.publish(tradeTransformer.normalize(trades));
		return "success";
	}
	
}