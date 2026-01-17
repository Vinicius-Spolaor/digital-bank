package com.digitalbank.exception.custom;

public class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }
}
