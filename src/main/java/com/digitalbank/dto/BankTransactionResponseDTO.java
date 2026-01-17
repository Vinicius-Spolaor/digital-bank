package com.digitalbank.dto;

import com.digitalbank.entity.enums.BankTransactionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransactionResponseDTO {
    private Long id;
    private Long originCustomerId;
    private Long destinationCustomerId;
    private BigDecimal amount;
    private BankTransactionStatusEnum status;
    private Instant transactionDate;
    private String message;
    private String description;
}
