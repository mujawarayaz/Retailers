package com.retailoffer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerDTO {
    private Integer customerId;

    @NotBlank(message = "{customer.name.notblank}")
    private String name;
}