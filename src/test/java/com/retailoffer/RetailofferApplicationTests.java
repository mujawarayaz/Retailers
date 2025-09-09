package com.retailoffer;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
        
        int retailerId = 999;
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setRetailerId(retailerId);
        transactionDTO.setAmountSpent(75.0);

        when(retailerRepository.findById(retailerId)).thenReturn(Optional.empty());

       
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
        transactionDTO.setAmountSpent(120.50); 
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
    
}
