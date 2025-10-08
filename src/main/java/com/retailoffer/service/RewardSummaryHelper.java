package com.retailoffer.service;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.retailoffer.entity.Transaction;

@Component
public class RewardSummaryHelper {

	private final RewardCalculatorService rewardCalculatorService;

	public RewardSummaryHelper(RewardCalculatorService rewardCalculatorService) {
		this.rewardCalculatorService = rewardCalculatorService;
	}

	public Map<String, Integer> calculateMonthlyPoints(List<Transaction> transactions) {
		return transactions.stream()
				.collect(Collectors.groupingBy(
						t -> t.getTransactionDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
						Collectors.summingInt(t -> rewardCalculatorService.calculateRewardPoints(t.getAmountSpent()))));
	}

	public int calculateTotalPoints(Map<String, Integer> monthlyPoints) {
		return monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
	}
}