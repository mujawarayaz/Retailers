package com.retailoffer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.retailoffer.dto.RewardDetailsDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.entity.Customer;
import com.retailoffer.entity.Retailer;
import com.retailoffer.entity.Transaction;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.repository.CustomerRepository;
import com.retailoffer.repository.RetailerRepository;
import com.retailoffer.repository.TransactionRepository;

@Service
public class RetailServiceImpl implements RetailService {

	private final CustomerRepository customerRepository;
	private final RetailerRepository retailerRepository;
	private final TransactionRepository transactionRepository;
	private final ModelMapper modelMapper;
	private final RewardCalculatorService rewardCalculatorService;
	private final RewardSummaryHelper rewardSummaryHelper;
	private final TransactionValidator transactionValidator;

	public RetailServiceImpl(CustomerRepository customerRepository, RetailerRepository retailerRepository,
			TransactionRepository transactionRepository, ModelMapper modelMapper,
			RewardCalculatorService rewardCalculatorService, RewardSummaryHelper rewardSummaryHelper,
			TransactionValidator transactionValidator) {
		this.customerRepository = customerRepository;
		this.retailerRepository = retailerRepository;
		this.transactionRepository = transactionRepository;
		this.modelMapper = modelMapper;
		this.rewardCalculatorService = rewardCalculatorService;
		this.rewardSummaryHelper = rewardSummaryHelper;
		this.transactionValidator = transactionValidator;
	}

	@Override
	public TransactionDTO recordTransaction(TransactionDTO transactionDTO) throws RetailerException {

		transactionValidator.validate(transactionDTO);

		Customer customer = customerRepository.findById(transactionDTO.getCustomerId()).orElseThrow(
				() -> new RetailerException("Customer not found with ID: " + transactionDTO.getCustomerId()));

		Retailer retailer = retailerRepository.findById(transactionDTO.getRetailerId()).orElseThrow(
				() -> new RetailerException("Retailer not found with ID: " + transactionDTO.getRetailerId()));

		Transaction transaction = new Transaction();
		transaction.setCustomer(customer);
		transaction.setRetailer(retailer);
		transaction.setAmountSpent(transactionDTO.getAmountSpent());
		transaction.setTransactionDate(transactionDTO.getTransactionDate());

		Integer points = rewardCalculatorService.calculateRewardPoints(transaction.getAmountSpent());
		transaction.setRewardPoint(points);

		Transaction savedTransaction = transactionRepository.save(transaction);
		return modelMapper.map(savedTransaction, TransactionDTO.class);
	}

	@Override
	public Integer getMonthlyRewardPointsForCustomer(Integer customerId) throws RetailerException {
		if (!customerRepository.existsById(customerId)) {
			throw new RetailerException("Customer not found with ID: " + customerId);
		}
		LocalDate lastMonth = LocalDate.now().minusMonths(1);
		List<Transaction> transactions = transactionRepository
				.findByCustomerCustomerIdAndTransactionDateAfter(customerId, lastMonth);

		return transactions.stream().mapToInt(t -> rewardCalculatorService.calculateRewardPoints(t.getAmountSpent()))
				.sum();
	}

	@Override
	public Integer getTotalRewardPointsForCustomer(Integer customerId) throws RetailerException {
		if (!customerRepository.existsById(customerId)) {
			throw new RetailerException("Customer not found with ID: " + customerId);
		}
		List<Transaction> transactions = transactionRepository.findAllByCustomerCustomerId(customerId);

		return transactions.stream().mapToInt(t -> rewardCalculatorService.calculateRewardPoints(t.getAmountSpent()))
				.sum();
	}

	@Override
	public RewardDetailsDTO getThreeMonthRewardSummary(Integer customerId) throws RetailerException {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RetailerException("Customer not found with ID: " + customerId));

		LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
		List<Transaction> transactions = transactionRepository
				.findByCustomerCustomerIdAndTransactionDateAfter(customerId, threeMonthsAgo);

		Map<String, Integer> monthlyPoints = rewardSummaryHelper.calculateMonthlyPoints(transactions);
		int totalPoints = rewardSummaryHelper.calculateTotalPoints(monthlyPoints);

		return RewardDetailsDTO.builder().customerId(customer.getCustomerId()).customerName(customer.getName())
				.monthlyPoints(monthlyPoints).totalRewardPoints(totalPoints).build();
	}
}