package com.retailoffer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.retailoffer.dto.PointsDTO;
import com.retailoffer.dto.RewardDetailsDTO;
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

    @PostMapping("/transactions")
    public ResponseEntity<TransactionDTO> recordTransaction(@Valid @RequestBody TransactionDTO transactionDTO) throws RetailerException {
        TransactionDTO savedTransaction = retailService.recordTransaction(transactionDTO);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/rewards/customers/{customerId}")
    public ResponseEntity<Object> getCustomerRewards(@PathVariable @NotNull @Min(1) Integer customerId, @RequestParam(name = "view", defaultValue = "summary") String viewType) throws RetailerException {

        switch (viewType.toLowerCase()) {
            case "total":
                Integer totalPoints = retailService.getTotalRewardPointsForCustomer(customerId);
                PointsDTO totalPointsResponse = new PointsDTO(customerId, totalPoints);
                return ResponseEntity.ok(totalPointsResponse);

            case "monthly":
                Integer monthlyPoints = retailService.getMonthlyRewardPointsForCustomer(customerId);
                PointsDTO monthlyPointsResponse = new PointsDTO(customerId, monthlyPoints);
                return ResponseEntity.ok(monthlyPointsResponse);

            default:
                RewardDetailsDTO summary = retailService.getThreeMonthRewardSummary(customerId);
                return ResponseEntity.ok(summary);
        }
    }
}