package com.retailoffer.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retailoffer.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	List<Transaction> findByRetailerRetailerIdAndTransactionDateAfter(Integer retailerId, LocalDate startFromDate);

}
