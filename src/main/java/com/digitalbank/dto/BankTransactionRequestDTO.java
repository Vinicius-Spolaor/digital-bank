package com.digitalbank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransactionRequestDTO {

    @NotNull(message = "Origin Customer ID must not be null")
    private Long originCustomerId;

    @NotNull(message = "Destination Customer ID must not be null")
    private Long destinationCustomerId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be higher than 0")
    private BigDecimal amount;

    private String description;

}
