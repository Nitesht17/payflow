package com.payflow.payflow.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class MerchantResponseDTO implements Serializable {
    private Long id;
    private String name;
    private String apiKey;
    private LocalDateTime createdAt;
}
