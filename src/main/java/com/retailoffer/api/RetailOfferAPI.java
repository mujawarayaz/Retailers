package com.retailoffer.api;


import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.service.RetailService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retail")
public class RetailOfferAPI {

    @Autowired
    private RetailService retailService;

    /**
     * Record a transaction and update reward points.
     */
    @PostMapping("/transaction")
    public ResponseEntity<RetailerDTO> recordTransaction(
            @Valid @RequestBody TransactionDTO transactionDTO) throws RetailerException {

    	RetailerDTO updatedRetailer = retailService.recordTransaction(transactionDTO);
        return new ResponseEntity<>(updatedRetailer, HttpStatus.OK);
    }

   
}
