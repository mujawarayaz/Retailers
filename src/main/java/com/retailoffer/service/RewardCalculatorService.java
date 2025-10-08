package com.retailoffer.service;

import org.springframework.stereotype.Component;

@Component
public class RewardCalculatorService {

	public Integer calculateRewardPoints(Double amountSpent) {
		if (amountSpent == null || amountSpent < 50) {
			return 0;
		}

		int wholeDollars = amountSpent.intValue();
		int points = 0;

		if (wholeDollars > 100) {
			points += (wholeDollars - 100) * 2;
			points += 50;
		} else if (wholeDollars > 50) {
			points += (wholeDollars - 50);
		}

		return points;
	}
}
