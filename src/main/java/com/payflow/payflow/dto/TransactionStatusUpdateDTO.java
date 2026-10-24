package com.payflow.payflow.dto;

import com.payflow.payflow.entity.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionStatusUpdateDTO {
    @NotNull(message = "status is required")
    private TransactionStatus status;
}
