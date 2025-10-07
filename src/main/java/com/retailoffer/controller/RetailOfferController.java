package com.retailoffer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.service.RetailService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/retail")
@Validated
public class RetailOfferController {

	private final RetailService retailService;

	public RetailOfferController(RetailService retailService) {
		this.retailService = retailService;
	}

	@PostMapping("/transaction")
	public ResponseEntity<RetailerDTO> recordTransaction(@Valid @RequestBody TransactionDTO transactionDTO)
			throws RetailerException {

		RetailerDTO updatedRetailer = retailService.recordTransaction(transactionDTO);
		return ResponseEntity.ok(updatedRetailer);
	}

	@GetMapping("/rewardPoints/{retailerId}")
	public ResponseEntity<Integer> getRewardPoints(
			@PathVariable @NotNull(message = "{retailer.id.required}") @Min(value = 1, message = "{retailer.id.required}") Integer retailerId,
			@RequestParam(defaultValue = "total") String type) throws RetailerException {

		Integer points;

		if ("monthly".equalsIgnoreCase(type)) {
			points = retailService.getMonthlyRewardPoint(retailerId);
		} else if ("total".equalsIgnoreCase(type)) {
			points = retailService.getTotalRewardPoint(retailerId);
		} else {
			throw new IllegalArgumentException("Invalid reward type specified. Must be 'monthly' or 'total'.");
		}

		return ResponseEntity.ok(points);
	}

	@GetMapping("/summary")
	public ResponseEntity<List<Map<String, Object>>> getThreeMonthSummary() throws RetailerException {
		List<Map<String, Object>> summary = retailService.getThreeMonthRewardSummary();
		return ResponseEntity.ok(summary);
	}
}