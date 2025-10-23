package com.example.instructions.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.instructions.model.*;
import com.example.instructions.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class TradeController {
	
	@Autowired
	TradeService tradeService;

	@PostMapping("/uploadTrades")
	public String upload(@RequestBody List<CanonicalTrade> trades) {
		return tradeService.upload(trades);
	}
}
