package com.retailoffer.service;


import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RetailerRepository retailerRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    
//   ModelMapper modelMapper = new ModelMapper();

    @Override
    public RetailerDTO recordTransaction(TransactionDTO transactionDTO) throws RetailerException{
       
        Retailer retailer = retailerRepository.findById(transactionDTO.getRetailerId())
                .orElseThrow(() -> new RetailerException("retailer.not.found"));

        
        int earnedPoints = calculateRewardPoints(transactionDTO.getAmountSpent());

      
        retailer.setRewardPoint(retailer.getRewardPoint() + earnedPoints);

        // Save updated retailer
        Retailer updatedRetailer = retailerRepository.save(retailer);

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setRetailer(retailer);
        transaction.setAmountSpent(transactionDTO.getAmountSpent());
        transaction.setTransactionDate(
        transactionDTO.getTransactionDate());
        transactionRepository.save(transaction);

        // Return updated RetailerDT
        RetailerDTO dto = 
//        modelMapper.map(updatedRetailer , RetailerDTO.class);
         new RetailerDTO();
        dto.setRetailerId(updatedRetailer.getRetailerId());
        dto.setName(updatedRetailer.getName());
        dto.setRewardPoint(updatedRetailer.getRewardPoint());
        
        
        return dto;
    }

    // Helper function to calculate reward point.
    public Integer calculateRewardPoints(Double amountSpent) {
        int points = 0;
        if (amountSpent > 100) {
            points += 2 * (amountSpent.intValue() - 100);
            points += 1 * 50; // For $50–$100
        } else if (amountSpent > 50) {
            points += 1 * (amountSpent.intValue() - 50);
        }
        return points;
    }

	@Override
	public Integer getMonthlyRewardPoint(Integer retailerId) throws RetailerException {
		
		Retailer retailer = retailerRepository.findById(retailerId)
                .orElseThrow(() -> new RetailerException("retailer.not.found"));
		
		LocalDate thisMonthdate = LocalDate.now();
		LocalDate startFromDate = thisMonthdate.minusMonths(1);
		
		List<Transaction> transactions = transactionRepository.findByRetailerRetailerIdAndTransactionDateAfter(retailerId, startFromDate);
		
		int rewardPoint = transactions.stream()
				.mapToInt(transation -> calculateRewardPoints(transation.getAmountSpent()))
				.sum();
		
		return rewardPoint;
	}

	@Override
	public Integer getTotalRewardPoint(Integer retailerId) throws RetailerException {
		
		Retailer retailer = retailerRepository.findById(retailerId)
                .orElseThrow(() -> new RetailerException("retailer.not.found"));
		return retailer.getRewardPoint();
	}

	@Override
	public List<Map<String, Object>> getThreeMonthRewardSummary() throws RetailerException {
	    
	    Map<Integer, Map<String, Object>> retailerSummaryMap = new HashMap<>();

	    Iterable<Transaction> transactions = transactionRepository.findAll();
	    LocalDate now = LocalDate.now();
	    LocalDate threeMonthsAgo = now.minusMonths(3);

	    for (Transaction tran : transactions) {
	        LocalDate txDate = tran.getTransactionDate();

	        // Only include transactions in the last 3 months
	        if (!txDate.isBefore(threeMonthsAgo) && txDate.isBefore(now)) {
	            int retailerId = tran.getRetailer().getRetailerId();
	            String month = txDate.getMonth().toString();

	            int points = calculateRewardPoints(tran.getAmountSpent());

	    
	            if (!retailerSummaryMap.containsKey(retailerId)) {
	               
	                Map<String, Object> newRetailerData = new LinkedHashMap<>();
	                retailerSummaryMap.put(retailerId, newRetailerData);
	            }

	        
	            Map<String, Object> retailerData = retailerSummaryMap.get(retailerId);

	            if (!retailerData.containsKey("retailer")) {
	                retailerData.put("retailer", tran.getRetailer());
	            }


	            List<Transaction> txList;
	            if (retailerData.containsKey("transactions")) {
	                txList = (List<Transaction>) retailerData.get("transactions");
	            } else {
	                txList = new ArrayList<>();
	            }

	            txList.add(tran);

	            retailerData.put("transactions", txList);

	            Map<String, Integer> monthlyPoints;
	            if (retailerData.containsKey("monthlyPoints")) {

	                monthlyPoints = (Map<String, Integer>) retailerData.get("monthlyPoints");
	            } else {
	
	                monthlyPoints = new LinkedHashMap<>();
	            }

	            int existingPoints = monthlyPoints.getOrDefault(month, 0);

	            monthlyPoints.put(month, existingPoints + points);

	            retailerData.put("monthlyPoints", monthlyPoints);


	            // Total points
	            int totalPoints = (int) retailerData.getOrDefault("totalPoints", 0);
	            retailerData.put("totalPoints", totalPoints + points);
	        }
	    }

	    // Convert the map to a list
	    return new ArrayList<>(retailerSummaryMap.values());
	}

    

}
