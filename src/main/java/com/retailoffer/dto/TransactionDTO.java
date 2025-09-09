package com.retailoffer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Data
public class TransactionDTO {

    @NotNull(message = "{retailer.id.required}")
    private Integer retailerId;

    @NotNull(message = "{transaction.amount.required}")
    private Double amountSpent;

    private LocalDate transactionDate;

	public Integer getRetailerId() {
		return retailerId;
	}

	public void setRetailerId(Integer retailerId) {
		this.retailerId = retailerId;
	}

	public Double getAmountSpent() {
		return amountSpent;
	}

	public void setAmountSpent(Double amountSpent) {
		this.amountSpent = amountSpent;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	} 

    // Getters & Setters
    
}

