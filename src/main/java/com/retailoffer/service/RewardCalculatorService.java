package com.retailoffer.service;

import org.springframework.stereotype.Component;

@Component
public class RewardCalculatorService {


	    public Integer calculateRewardPoints(Double amountSpent) {

			if (amountSpent == null || amountSpent <= 0) {
	            return 0; 
	        }

	        int points = 0;
	        int intAmount = (int) Math.floor(amountSpent);

	        if (intAmount > 100) {
	            points += 2 * (intAmount - 100);
	            points += 1 * 50; 
	        } 

	        else if (intAmount > 50) {
	            points += 1 * (intAmount - 50);
	        }
	        
	        return points;
	    
	}
}