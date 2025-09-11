package controller;


import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.service.RetailService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retail")
public class RetailOfferController {

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
    
    @GetMapping("/transactionPerMonth/{retailerId}")
    public ResponseEntity<Integer>getMonthlyRewardPoint(
    		@PathVariable Integer retailerId) throws RetailerException{
			
    	Integer rewardPoint = retailService.getMonthlyRewardPoint(retailerId);
      return new ResponseEntity<>(rewardPoint, HttpStatus.OK);
    	
    }
    @GetMapping("/totalReward/{retailerId}")
    public ResponseEntity<Integer>getTotalRewardPoint(
    		@PathVariable Integer retailerId) throws RetailerException{
			
    	Integer rewardPoint = retailService.getTotalRewardPoint(retailerId);
      return new ResponseEntity<>(rewardPoint, HttpStatus.OK);
    	
    }
    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getThreeMonthSummary() throws RetailerException {
        List<Map<String, Object>> summary = retailService.getThreeMonthRewardSummary();
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
   
}
