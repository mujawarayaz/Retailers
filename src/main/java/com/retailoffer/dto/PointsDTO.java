package com.retailoffer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointsDTO {
    private Integer customerId;
    private Integer points;
}