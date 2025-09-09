package com.retailoffer.repository;


import org.springframework.data.repository.CrudRepository;

import com.retailoffer.entity.Retailer;

public interface RetailerRepository extends CrudRepository<Retailer, Integer> {
}
