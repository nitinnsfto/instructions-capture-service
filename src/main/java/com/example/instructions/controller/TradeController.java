package com.example.instructions.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.instructions.model.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class TradeController {

	@PostMapping("/uploadTrades")
	public String upload(@RequestBody List<CanonicalTrade> trades) {
		return null;
	}
}
