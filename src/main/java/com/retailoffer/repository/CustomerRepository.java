package com.retailoffer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retailoffer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
