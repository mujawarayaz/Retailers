package com.retailoffer.service;


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

    @Override
    public RetailerDTO recordTransaction(TransactionDTO transactionDTO) throws RetailerException {
       
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
        RetailerDTO dto = new RetailerDTO();
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


}
