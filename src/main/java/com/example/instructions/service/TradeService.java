package com.example.instructions.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.util.TradeTransformer;

@Service
public class TradeService{
	
	Logger logger = LoggerFactory.getLogger(TradeService.class);

	@Autowired
	TradeTransformer tradeTransformer;
	
	@Autowired
	KafkaPublisher kafkaPublisher;
	
	public String upload(List<CanonicalTrade> trades) {
		try {
			kafkaPublisher.publish(tradeTransformer.normalize(trades));
		} catch (Exception e) {
			e.printStackTrace();
			return "failure";
		}
		return "success";
	}
	
}