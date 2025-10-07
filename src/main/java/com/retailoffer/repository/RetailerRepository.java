package com.retailoffer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retailoffer.entity.Retailer;

public interface RetailerRepository extends JpaRepository<Retailer, Integer> {
}
