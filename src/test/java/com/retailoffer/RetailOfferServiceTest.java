package com.retailoffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.entity.Retailer;
import com.retailoffer.entity.Transaction;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.repository.RetailerRepository;
import com.retailoffer.repository.TransactionRepository;
import com.retailoffer.service.RetailServiceImpl;
import com.retailoffer.service.RewardCalculatorService;
import com.retailoffer.service.TransactionValidator;

@ExtendWith(MockitoExtension.class)
public class RetailOfferServiceTest {

	@Mock
	private RetailerRepository retailerRepository;
	@Mock
	private TransactionRepository transactionRepository;
	@Mock
	private ModelMapper modelMapper;

	@Mock
	private TransactionValidator transactionValidator;
	@Mock
	private RewardCalculatorService rewardCalculatorService;

	@InjectMocks
	private RetailServiceImpl retailService;

	private void mockRewardCalculation(Double inputAmount, Integer outputPoints) {
		when(rewardCalculatorService.calculateRewardPoints(inputAmount)).thenReturn(outputPoints);
	}

	@Test
	void recordTransaction_RetailerNotFound_ThrowsRetailerException() {
		int retailerId = 1;
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setRetailerId(retailerId);
		transactionDTO.setAmountSpent(50.0);

		when(retailerRepository.findById(retailerId)).thenReturn(Optional.empty());

		RetailerException exception = assertThrows(RetailerException.class, () -> {
			retailService.recordTransaction(transactionDTO);
		});

		assertEquals("retailer.not.found", exception.getMessage());
	}

	@Test
	void recordTransaction_ValidInput_ReturnsUpdatedRetailerDTO() throws RetailerException {
		int retailerId = 1;
		Retailer retailer = new Retailer();
		retailer.setRetailerId(retailerId);
		retailer.setRewardPoint(50);
		RetailerDTO expectedDTO = new RetailerDTO();
		expectedDTO.setRewardPoint(140);

		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setRetailerId(retailerId);
		transactionDTO.setAmountSpent(120.0);

		int earnedPoints = 90;
		mockRewardCalculation(120.0, earnedPoints);

		when(retailerRepository.findById(retailerId)).thenReturn(Optional.of(retailer));
		when(retailerRepository.save(any(Retailer.class))).thenReturn(retailer);

		when(modelMapper.map(any(Retailer.class), any())).thenReturn(expectedDTO);

		RetailerDTO updatedRetailerDTO = retailService.recordTransaction(transactionDTO);

		assertEquals(140, updatedRetailerDTO.getRewardPoint());
		assertEquals(140, retailer.getRewardPoint());
	}

	@Test
	void getMonthlyRewardPoint_ValidRetailer_ReturnsCorrectPoints() throws RetailerException {
		int retailerId = 1;
		LocalDate thisMonthdate = LocalDate.now();
		LocalDate startFromDate = thisMonthdate.minusMonths(1);
		Retailer retailer = new Retailer();
		retailer.setRetailerId(retailerId);

		Transaction transaction = new Transaction();
		transaction.setRetailer(retailer);
		transaction.setAmountSpent(120.0);
		transaction.setTransactionDate(thisMonthdate);

		List<Transaction> transactionList = Collections.singletonList(transaction);

		mockRewardCalculation(120.0, 90);
		when(retailerRepository.findById(retailerId)).thenReturn(Optional.of(retailer));
		when(transactionRepository.findByRetailerRetailerIdAndTransactionDateAfter(any(Integer.class),
				any(LocalDate.class))).thenReturn(transactionList);

		Integer rewardPoint = retailService.getMonthlyRewardPoint(retailerId);

		assertEquals(90, rewardPoint);
	}

	@Test
	void getMonthlyRewardPoint_RetailerNotFound_ThrowsRetailerException() {
		int retailerId = 1;
		when(retailerRepository.findById(retailerId)).thenReturn(Optional.empty());

		RetailerException exception = assertThrows(RetailerException.class, () -> {
			retailService.getMonthlyRewardPoint(retailerId);
		});

		assertEquals("retailer.not.found", exception.getMessage());
	}

	@Test
	void getTotalRewardPoint_ValidRetailer_ReturnsCorrectPoints() throws RetailerException {
		int retailerId = 1;
		Retailer retailer = new Retailer();
		retailer.setRetailerId(retailerId);
		retailer.setRewardPoint(50);

		when(retailerRepository.findById(retailerId)).thenReturn(Optional.of(retailer));

		Integer rewardPoint = retailService.getTotalRewardPoint(retailerId);

		assertEquals(50, rewardPoint);
	}

	@Test
	void getTotalRewardPoint_RetailerNotFound_ThrowsRetailerException() {
		int retailerId = 1;

		when(retailerRepository.findById(retailerId)).thenReturn(Optional.empty());

		RetailerException exception = assertThrows(RetailerException.class, () -> {
			retailService.getTotalRewardPoint(retailerId);
		});

		assertEquals("retailer.not.found", exception.getMessage());
	}

	@Test
	void getThreeMonthRewardSummary_ValidTransactions_CalculatesCorrectTotal() throws RetailerException {

		Retailer retailer = new Retailer();
		retailer.setRetailerId(1);

		Transaction t1 = new Transaction();
		t1.setRetailer(retailer);
		t1.setAmountSpent(120.0);
		t1.setTransactionDate(LocalDate.now());
		Transaction t2 = new Transaction();
		t2.setRetailer(retailer);
		t2.setAmountSpent(75.0);
		t2.setTransactionDate(LocalDate.now().minusMonths(2).withDayOfMonth(15));

		List<Transaction> transactions = Arrays.asList(t1, t2);

		when(transactionRepository.findAll()).thenReturn(transactions);
		mockRewardCalculation(120.0, 90);
		mockRewardCalculation(75.0, 25);

		List<Map<String, Object>> result = retailService.getThreeMonthRewardSummary();

		assertEquals(1, result.size());

		Map<String, Object> retailerData = result.get(0);
		Integer totalPoints = (Integer) retailerData.get("totalPoints");

		assertEquals(115, totalPoints);
	}

	@Test
	void getThreeMonthRewardSummary_OldTransaction_ReturnsEmptyList() throws RetailerException {

		Retailer retailer = new Retailer();
		retailer.setRetailerId(2);

		Transaction oldTx = new Transaction();
		oldTx.setRetailer(retailer);
		oldTx.setAmountSpent(200.0);
		oldTx.setTransactionDate(LocalDate.now().minusMonths(4).withDayOfMonth(1));

		when(transactionRepository.findAll()).thenReturn(Collections.singletonList(oldTx));

		List<Map<String, Object>> result = retailService.getThreeMonthRewardSummary();

		assertTrue(result.isEmpty());
	}
}