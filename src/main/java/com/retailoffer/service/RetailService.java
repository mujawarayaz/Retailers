package com.retailoffer.service;

import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;

public interface RetailService {
	
	RetailerDTO recordTransaction(TransactionDTO transactionDTO)throws RetailerException;


}
