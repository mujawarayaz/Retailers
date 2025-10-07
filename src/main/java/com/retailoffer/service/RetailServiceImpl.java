package com.retailoffer.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.entity.Retailer;
import com.retailoffer.entity.Transaction;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.repository.RetailerRepository;
import com.retailoffer.repository.TransactionRepository;

@Service
public class RetailServiceImpl implements RetailService {


	private record MonthlyRetailerData(Integer retailerId, Retailer retailer, String monthKey, Integer points,
			Transaction transaction) {
	}

	private final RetailerRepository retailerRepository;
	private final TransactionRepository transactionRepository;
	private final ModelMapper modelMapper;
	private final TransactionValidator transactionValidator;
	private final RewardCalculatorService rewardCalculatorService;

	public RetailServiceImpl(RetailerRepository retailerRepository, TransactionRepository transactionRepository,
			ModelMapper modelMapper, TransactionValidator transactionValidator,
			RewardCalculatorService rewardCalculatorService) {
		this.retailerRepository = retailerRepository;
		this.transactionRepository = transactionRepository;
		this.modelMapper = modelMapper;
		this.transactionValidator = transactionValidator;
		this.rewardCalculatorService = rewardCalculatorService;
	}

	@Override
	public RetailerDTO recordTransaction(TransactionDTO transactionDTO) throws RetailerException {
		transactionValidator.validate(transactionDTO);

		Retailer retailer = retailerRepository.findById(transactionDTO.getRetailerId())
				.orElseThrow(() -> new RetailerException("retailer.not.found"));

		int earnedPoints = rewardCalculatorService.calculateRewardPoints(transactionDTO.getAmountSpent());

		retailer.setRewardPoint(retailer.getRewardPoint() + earnedPoints);

		Retailer updatedRetailer = retailerRepository.save(retailer);

		Transaction transaction = new Transaction();
		transaction.setRetailer(retailer);
		transaction.setAmountSpent(transactionDTO.getAmountSpent());
		transaction.setTransactionDate(transactionDTO.getTransactionDate());
		transactionRepository.save(transaction);

		return modelMapper.map(updatedRetailer, RetailerDTO.class);
	}

	@Override
	public Integer getMonthlyRewardPoint(Integer retailerId) throws RetailerException {
		if (retailerId == null || retailerId <= 0) {
			throw new RetailerException("retailer.id.required");
		}

		retailerRepository.findById(retailerId).orElseThrow(() -> new RetailerException("retailer.not.found"));

		LocalDate startFromDate = LocalDate.now().minusMonths(1);

		List<Transaction> transactions = transactionRepository
				.findByRetailerRetailerIdAndTransactionDateAfter(retailerId, startFromDate);

		return transactions.stream().mapToInt(t -> rewardCalculatorService.calculateRewardPoints(t.getAmountSpent()))
				.sum();
	}

	@Override
	public Integer getTotalRewardPoint(Integer retailerId) throws RetailerException {
		if (retailerId == null || retailerId <= 0) {
			throw new RetailerException("retailer.id.required");
		}

		Retailer retailer = retailerRepository.findById(retailerId)
				.orElseThrow(() -> new RetailerException("retailer.not.found"));

		return retailer.getRewardPoint();
	}

	@Override
	public List<Map<String, Object>> getThreeMonthRewardSummary() throws RetailerException {

		final LocalDate now = LocalDate.now();
		final LocalDate threeMonthsAgo = now.minusMonths(3);

		final Iterable<Transaction> allTransactions = transactionRepository.findAll();

	
		final List<MonthlyRetailerData> mappedData = StreamSupport.stream(allTransactions.spliterator(), false).filter(
				tran -> !tran.getTransactionDate().isBefore(threeMonthsAgo) && !tran.getTransactionDate().isAfter(now))
				.map(tran -> new MonthlyRetailerData(
						tran.getRetailer().getRetailerId(),
						tran.getRetailer(),
						YearMonth.from(tran.getTransactionDate()).toString(),
						rewardCalculatorService.calculateRewardPoints(tran.getAmountSpent()), 
						tran))
				.collect(Collectors.toList());

		
		return mappedData.stream()
				.collect(
						Collectors.groupingBy(MonthlyRetailerData::retailerId,
						Collectors.collectingAndThen(
								Collectors.toList(),
								dataList -> {
			
					final Map<String, Integer> monthlyPoints = 
							dataList.stream().
							collect(Collectors.groupingBy(
							MonthlyRetailerData::monthKey, 
							Collectors.summingInt(MonthlyRetailerData::points)));
					
					final List<Transaction> transactions = 
							dataList.stream().
							map(MonthlyRetailerData::transaction)
							.collect(Collectors.toList());

					final Retailer retailer = dataList.get(0).retailer();
					final int totalPoints = dataList.stream().
											mapToInt(MonthlyRetailerData::points).sum();

					Map<String, Object> retailerData = new LinkedHashMap<>();
					retailerData.put("retailer", retailer);
					retailerData.put("transactions", transactions);

					retailerData.put("monthlyPoints",
							monthlyPoints.entrySet().stream()
									.sorted(Map.Entry.comparingByKey())
									.collect(Collectors.
											toMap(Map.Entry::getKey, 
												  Map.Entry::getValue, 
												  (e1, e2) -> e1,
											LinkedHashMap::new)));

					retailerData.put("totalPoints", totalPoints);

					return retailerData;
				}))
			).values().stream().collect(Collectors.toList());
	}
}