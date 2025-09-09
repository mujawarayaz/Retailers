package com.retailoffer.dto;

import lombok.Data;


import jakarta.validation.constraints.NotBlank;


@Data
public class RetailerDTO {

    private Integer retailerId; 
    
    @NotBlank(message = "{retailer.name.notblank}")
    private String name;

    private Integer rewardPoint;

	public Integer getRetailerId() {
		return retailerId;
	}

	public void setRetailerId(Integer retailerId) {
		this.retailerId = retailerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRewardPoint() {
		return rewardPoint;
	}

	public void setRewardPoint(Integer rewardPoint) {
		this.rewardPoint = rewardPoint;
	} 
    
    
    
    
}
