package com.retailoffer.repository;

import org.springframework.data.repository.CrudRepository;

import com.retailoffer.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
}
