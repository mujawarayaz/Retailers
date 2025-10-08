package com.retailoffer.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer transactionId;

	@ManyToOne
	@JoinColumn(name = "retailer_id", nullable = false)
	private Retailer retailer;

    @ManyToOne 
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

	private Double amountSpent;

	private Integer rewardPoint = 0;

	private LocalDate transactionDate;
}