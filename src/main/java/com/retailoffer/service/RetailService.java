package com.retailoffer.service;

import com.retailoffer.dto.RewardDetailsDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;

public interface RetailService {

	TransactionDTO recordTransaction(TransactionDTO transactionDTO) throws RetailerException;

	Integer getMonthlyRewardPointsForCustomer(Integer customerId) throws RetailerException;

	Integer getTotalRewardPointsForCustomer(Integer customerId) throws RetailerException;

	RewardDetailsDTO getThreeMonthRewardSummary(Integer customerId) throws RetailerException;
}