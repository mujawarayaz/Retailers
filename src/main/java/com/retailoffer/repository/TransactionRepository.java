package com.retailoffer.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.retailoffer.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
	
	List<Transaction> findByRetailerRetailerIdAndTransactionDateAfter(Integer retailerId, LocalDate startFromDate);

	
}
