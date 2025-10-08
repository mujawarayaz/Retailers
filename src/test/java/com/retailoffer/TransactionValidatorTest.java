package com.retailoffer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.service.TransactionValidator;

class TransactionValidatorTest {

	private TransactionValidator transactionValidator;
	private TransactionDTO transactionDTO;

	@BeforeEach
	void setUp() {
		transactionValidator = new TransactionValidator();
		transactionDTO = new TransactionDTO();
		transactionDTO.setCustomerId(101);
		transactionDTO.setRetailerId(101);
		transactionDTO.setAmountSpent(100.0);
	}

	@Test
	void validate_withValidDTO_shouldNotThrowException() {
		assertDoesNotThrow(() -> transactionValidator.validate(transactionDTO));
	}

	@Test
	void validate_withNullDTO_shouldThrowException() {
		assertThrows(RetailerException.class, () -> transactionValidator.validate(null));
	}

	@Test
	void validate_withNullCustomerId_shouldThrowException() {
		transactionDTO.setCustomerId(null);
		assertThrows(RetailerException.class, () -> transactionValidator.validate(transactionDTO));
	}

	@Test
	void validate_withInvalidCustomerId_shouldThrowException() {
		transactionDTO.setCustomerId(0);
		assertThrows(RetailerException.class, () -> transactionValidator.validate(transactionDTO));
	}

	@Test
	void validate_withNullRetailerId_shouldThrowException() {
		transactionDTO.setRetailerId(null);
		assertThrows(RetailerException.class, () -> transactionValidator.validate(transactionDTO));
	}

	@Test
	void validate_withNegativeAmount_shouldThrowException() {
		transactionDTO.setAmountSpent(-50.0);
		assertThrows(RetailerException.class, () -> transactionValidator.validate(transactionDTO));
	}
}