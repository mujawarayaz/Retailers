package com.retailoffer.service;

import org.springframework.stereotype.Component;

import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;

@Component
public class TransactionValidator {

	public void validate(TransactionDTO transactionDTO) throws RetailerException {
		if (transactionDTO == null) {
			throw new RetailerException("transaction.dto.null");
		}
		if (transactionDTO.getCustomerId() == null || transactionDTO.getCustomerId() <= 0) {
			throw new RetailerException("customer.id.required");
		}
		if (transactionDTO.getRetailerId() == null || transactionDTO.getRetailerId() <= 0) {
			throw new RetailerException("retailer.id.required");
		}
		if (transactionDTO.getAmountSpent() == null || transactionDTO.getAmountSpent() < 0) {
			throw new RetailerException("transaction.amount.required");
		}
	}
}