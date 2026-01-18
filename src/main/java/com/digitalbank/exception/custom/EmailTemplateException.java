package com.digitalbank.exception.custom;

public class EmailTemplateException extends RuntimeException {
    public EmailTemplateException(String message) {
        super(message);
    }

    public EmailTemplateException(String message, Exception ex) {
        super(message, ex);
    }
}
