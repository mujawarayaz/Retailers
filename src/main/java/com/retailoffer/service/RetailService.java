package com.retailoffer.service;

import java.util.List;
import java.util.Map;

import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;

public interface RetailService {

	RetailerDTO recordTransaction(TransactionDTO transactionDTO) throws RetailerException;

	Integer getMonthlyRewardPoint(Integer retailerId) throws RetailerException;

	Integer getTotalRewardPoint(Integer retailerId) throws RetailerException;

	List<Map<String, Object>> getThreeMonthRewardSummary() throws RetailerException;

}
