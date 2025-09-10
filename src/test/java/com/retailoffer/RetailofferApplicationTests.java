package com.retailoffer;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.entity.Retailer;
import com.retailoffer.entity.Transaction;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.repository.RetailerRepository;
import com.retailoffer.repository.TransactionRepository;
import com.retailoffer.service.RetailServiceImpl;


@SpringBootTest
public class RetailofferApplicationTests {

    @Mock
    private RetailerRepository retailerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RetailServiceImpl retailService;

    
    @Test
    void testRecordTransaction_RetailerNotFound() throws RetailerException{
        
        int retailerId = 1;
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setRetailerId(retailerId);
        transactionDTO.setAmountSpent(50.0);

        when(retailerRepository.findById(transactionDTO.getRetailerId())).thenReturn(Optional.empty());

       
        RetailerException exception = assertThrows(RetailerException.class, () -> {
            retailService.recordTransaction(transactionDTO);
        });

        assertEquals("retailer.not.found", exception.getMessage());
    }
    
    @Test
    void testRecordTransaction_RetailerFound() throws RetailerException {
    	
        int retailerId = 1; 
        Retailer retailer = new Retailer();
        retailer.setRetailerId(retailerId);
        retailer.setName("Test Retailer");
        retailer.setRewardPoint(50); 

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setRetailerId(retailerId);
        transactionDTO.setAmountSpent(120.0); 
        transactionDTO.setTransactionDate(LocalDate.parse("2025-09-09"));
        
        Transaction mockTransaction = new Transaction();
        mockTransaction.setRetailer(retailer);
        mockTransaction.setAmountSpent(transactionDTO.getAmountSpent());
        mockTransaction.setTransactionDate(transactionDTO.getTransactionDate());
        
        when(retailerRepository.findById(retailerId)).thenReturn(Optional.of(retailer));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);
        when(retailerRepository.save(any(Retailer.class))).thenReturn(retailer);
       
        RetailerDTO updatedRetailerDTO = retailService.recordTransaction(transactionDTO);

        assertEquals(140, updatedRetailerDTO.getRewardPoint()); 
  
    	
    }
    
    @Test
    void getValidMonthlyRewardPoint() throws RetailerException {
    	
    	 int retailerId = 1; 
    	 LocalDate thisMonthdate = LocalDate.now();
 		LocalDate startFromDate = thisMonthdate.minusMonths(1);
         Retailer retailer = new Retailer();
         retailer.setRetailerId(retailerId);
         retailer.setName("Test Retailer");
         retailer.setRewardPoint(50); 
         
         Transaction transaction = new Transaction();
         transaction.setRetailer(retailer);
         transaction.setAmountSpent(120.0);
         transaction.setTransactionDate(thisMonthdate);
         
         List<Transaction>transactionList = new ArrayList<>();
         transactionList.add(transaction);
         
         
         when(retailerRepository.findById(retailerId)).thenReturn(Optional.of(retailer));
         when(transactionRepository.findByRetailerRetailerIdAndTransactionDateAfter(retailerId ,startFromDate))
         .thenReturn(transactionList);
         
         Integer rewardPoint = retailService.getMonthlyRewardPoint(retailerId);

         assertEquals(90, rewardPoint);

         
         
    }
    
    @Test
    void getInValidMonthlyRewardPoint() throws RetailerException {
    	
    	  int retailerId = 1;
          TransactionDTO transactionDTO = new TransactionDTO();
          transactionDTO.setRetailerId(retailerId);
          transactionDTO.setAmountSpent(50.0);

          when(retailerRepository.findById(transactionDTO.getRetailerId())).thenReturn(Optional.empty());
          
          RetailerException exception = assertThrows(RetailerException.class, () -> {
              retailService.getMonthlyRewardPoint(retailerId);
          });

          assertEquals("retailer.not.found", exception.getMessage());
         
         
    }
    
    @Test
    void getValidTotalRewardPoint() throws RetailerException {
    	
    	 int retailerId = 1; 
         Retailer retailer = new Retailer();
         retailer.setRetailerId(retailerId);
         retailer.setName("Test Retailer");
         retailer.setRewardPoint(50);

        when(retailerRepository.findById(retailerId)).thenReturn(Optional.of(retailer));

        Integer rewardPoint = retailService.getTotalRewardPoint(retailerId);

        assertEquals(50, rewardPoint); 
       
       
  }
    
    @Test
    void getInValidTotalRewardPoint() throws RetailerException {
    	
  	  int retailerId = 1;

        when(retailerRepository.findById(retailerId)).thenReturn(Optional.empty());
        
        RetailerException exception = assertThrows(RetailerException.class, () -> {
            retailService.getTotalRewardPoint(retailerId);
        });

        assertEquals("retailer.not.found", exception.getMessage());
       
       
  }
    
    @Test
    void validgetgetThreeMonthRewardSummary() throws RetailerException {
        // Arrange
        Retailer retailer = new Retailer();
        retailer.setRetailerId(1);
        retailer.setName("John");

        Transaction t1 = new Transaction();
        t1.setTransactionId(101);
        t1.setRetailer(retailer);
        t1.setAmountSpent((double) 120);
        t1.setTransactionDate(LocalDate.now().minusWeeks(2));  // Within 1 month

        Transaction t2 = new Transaction();
        t2.setTransactionId(102);
        t2.setRetailer(retailer);
        t2.setAmountSpent((double) 75);
        t2.setTransactionDate(LocalDate.now().minusMonths(2));  // Within 3 months

        List<Transaction> transactions = Arrays.asList(t1, t2);

        when(transactionRepository.findAll()).thenReturn(transactions);
        
        List<Map<String, Object>> result = retailService.getThreeMonthRewardSummary();

        assertEquals(1, result.size()); 

        Map<String, Object> retailerData = result.get(0);
        Integer totalPoints = (Integer) retailerData.get("totalPoints");

        assertEquals(115, totalPoints); 


    }

    @Test
    void invalidgetgetThreeMonthRewardSummary() throws RetailerException {
     
        Retailer retailer = new Retailer();
        retailer.setRetailerId(2);

        Transaction oldTx = new Transaction();
        oldTx.setTransactionId(201);
        oldTx.setRetailer(retailer);
        oldTx.setAmountSpent((double) 200);
        oldTx.setTransactionDate(LocalDate.now().minusMonths(4));

        when(transactionRepository.findAll()).thenReturn(Collections.singletonList(oldTx));

        List<Map<String, Object>> result = retailService.getThreeMonthRewardSummary();

        assertTrue(result.isEmpty());
    }


    
    
}
