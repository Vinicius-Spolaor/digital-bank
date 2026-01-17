package com.digitalbank.exception.custom;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requestedAmount) {
        super(String.format("Insufficient Balance. Current Balance: $ %.2f, Requeste Amount: R$ %.2f", currentBalance, requestedAmount));
    }
}
