package com.example.instructions.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.instructions.model.*;
import com.example.instructions.service.KafkaListenerService;
import com.example.instructions.service.TradeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class TradeController {
	  
	Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);
	
	@Autowired
	TradeService tradeService;

	@PostMapping("/uploadTrades")
	public String upload(@RequestBody List<CanonicalTrade> trades) {
		logger.info("Recived paylod with trades as: "+ trades.size());
		return tradeService.upload(trades);
	}
}
