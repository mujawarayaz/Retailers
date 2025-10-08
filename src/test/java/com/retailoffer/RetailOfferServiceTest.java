package com.retailoffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.retailoffer.dto.RewardDetailsDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.entity.Customer;
import com.retailoffer.entity.Retailer;
import com.retailoffer.entity.Transaction;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.repository.CustomerRepository;
import com.retailoffer.repository.RetailerRepository;
import com.retailoffer.repository.TransactionRepository;
import com.retailoffer.service.RetailServiceImpl;
import com.retailoffer.service.RewardCalculatorService;
import com.retailoffer.service.RewardSummaryHelper;
import com.retailoffer.service.TransactionValidator;

@ExtendWith(MockitoExtension.class)
public class RetailOfferServiceTest {

	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private RetailerRepository retailerRepository;
	@Mock
	private TransactionRepository transactionRepository;
	@Mock
	private RewardCalculatorService rewardCalculatorService;
	@Mock
	private RewardSummaryHelper rewardSummaryHelper;
	@Mock
	private ModelMapper modelMapper;
	@Mock
	private TransactionValidator transactionValidator;

	@InjectMocks
	private RetailServiceImpl retailService;

	private Customer mockCustomer;
	private TransactionDTO transactionDTO;
	private List<Transaction> mockTransactionList;

	@BeforeEach
	void setUp() {

		mockCustomer = new Customer();
		mockCustomer.setCustomerId(101);
		mockCustomer.setName("Ayaz Mujawar");

		transactionDTO = new TransactionDTO();
		transactionDTO.setCustomerId(101);
		transactionDTO.setRetailerId(101);
		transactionDTO.setAmountSpent(120.0);

		Transaction t1 = new Transaction();
		t1.setAmountSpent(120.0);
		Transaction t2 = new Transaction();
		t2.setAmountSpent(75.0);
		mockTransactionList = List.of(t1, t2);
	}

	@Test
	void recordTransaction_Success_ShouldValidateAndSave() throws RetailerException {
		when(customerRepository.findById(transactionDTO.getCustomerId())).thenReturn(Optional.of(mockCustomer));
		when(retailerRepository.findById(transactionDTO.getRetailerId())).thenReturn(Optional.of(new Retailer()));
		when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
		when(modelMapper.map(any(Transaction.class), eq(TransactionDTO.class))).thenReturn(transactionDTO);

		retailService.recordTransaction(transactionDTO);

		verify(transactionValidator, times(1)).validate(transactionDTO);
		verify(transactionRepository, times(1)).save(any(Transaction.class));
	}

	@Test
	void getMonthlyRewardPointsForCustomer_Success_ShouldReturnCorrectSum() throws RetailerException {
		when(customerRepository.existsById(mockCustomer.getCustomerId())).thenReturn(true);
		when(transactionRepository.findByCustomerCustomerIdAndTransactionDateAfter(eq(mockCustomer.getCustomerId()),
				any(LocalDate.class))).thenReturn(mockTransactionList);
		when(rewardCalculatorService.calculateRewardPoints(120.0)).thenReturn(90);
		when(rewardCalculatorService.calculateRewardPoints(75.0)).thenReturn(25);

		Integer points = retailService.getMonthlyRewardPointsForCustomer(mockCustomer.getCustomerId());

		assertEquals(115, points);
	}

	@Test
	void getTotalRewardPointsForCustomer_Success_ShouldReturnCorrectSum() throws RetailerException {
		when(customerRepository.existsById(mockCustomer.getCustomerId())).thenReturn(true);
		when(transactionRepository.findAllByCustomerCustomerId(mockCustomer.getCustomerId()))
				.thenReturn(mockTransactionList);
		when(rewardCalculatorService.calculateRewardPoints(120.0)).thenReturn(90);
		when(rewardCalculatorService.calculateRewardPoints(75.0)).thenReturn(25);

		Integer totalPoints = retailService.getTotalRewardPointsForCustomer(mockCustomer.getCustomerId());

		assertEquals(115, totalPoints);
	}

	@Test
	void getThreeMonthRewardSummary_Success_ShouldReturnCorrectDTO() throws RetailerException {
		Map<String, Integer> monthlyPointsMap = Map.of("OCTOBER", 115);
		int totalPoints = 115;

		when(customerRepository.findById(mockCustomer.getCustomerId())).thenReturn(Optional.of(mockCustomer));
		when(transactionRepository.findByCustomerCustomerIdAndTransactionDateAfter(eq(mockCustomer.getCustomerId()),
				any(LocalDate.class))).thenReturn(mockTransactionList);
		when(rewardSummaryHelper.calculateMonthlyPoints(mockTransactionList)).thenReturn(monthlyPointsMap);
		when(rewardSummaryHelper.calculateTotalPoints(monthlyPointsMap)).thenReturn(totalPoints);

		RewardDetailsDTO summary = retailService.getThreeMonthRewardSummary(mockCustomer.getCustomerId());

		assertEquals(mockCustomer.getCustomerId(), summary.getCustomerId());
		assertEquals(mockCustomer.getName(), summary.getCustomerName());
		assertEquals(totalPoints, summary.getTotalRewardPoints());
	}

	@Test
	void recordTransaction_CustomerNotFound_ShouldThrowRetailerException() {
		when(customerRepository.findById(transactionDTO.getCustomerId())).thenReturn(Optional.empty());

		assertThrows(RetailerException.class, () -> {
			retailService.recordTransaction(transactionDTO);
		});
	}

	@Test
	void getMonthlyRewardPoints_CustomerNotFound_ShouldThrowRetailerException() {
		when(customerRepository.existsById(mockCustomer.getCustomerId())).thenReturn(false);

		assertThrows(RetailerException.class, () -> {
			retailService.getMonthlyRewardPointsForCustomer(mockCustomer.getCustomerId());
		});
	}

	@Test
	void getTotalRewardPoints_CustomerNotFound_ShouldThrowRetailerException() {
		when(customerRepository.existsById(mockCustomer.getCustomerId())).thenReturn(false);

		assertThrows(RetailerException.class, () -> {
			retailService.getTotalRewardPointsForCustomer(mockCustomer.getCustomerId());
		});
	}

	@Test
	void getThreeMonthRewardSummary_CustomerNotFound_ShouldThrowRetailerException() {
		when(customerRepository.findById(mockCustomer.getCustomerId())).thenReturn(Optional.empty());

		assertThrows(RetailerException.class, () -> {
			retailService.getThreeMonthRewardSummary(mockCustomer.getCustomerId());
		});
	}
}