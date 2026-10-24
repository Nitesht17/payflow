package com.payflow.payflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantRequestDTO {
    @NotBlank(message = "name is required")
    private String name;
}
