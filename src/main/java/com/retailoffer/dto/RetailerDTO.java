package com.retailoffer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RetailerDTO {

	private Integer retailerId;

	@NotBlank(message = "{retailer.name.notblank}")
	private String name;

	private Integer rewardPoint;

}
