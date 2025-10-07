package com.retailoffer.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionDTO {

	@NotNull(message = "{retailer.id.required}")
	private Integer retailerId;

	@NotNull(message = "{transaction.amount.required}")
	@Positive(message = "{amount.spent.invalid}")
	private Double amountSpent;

	@NotNull(message = "{transaction.date.required}")
	private LocalDate transactionDate;

}
