package com.retailoffer.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RewardDetailsDTO {

    Integer customerId;

    String customerName;

    Integer totalRewardPoints;

    Map<String, Integer> monthlyPoints;
}