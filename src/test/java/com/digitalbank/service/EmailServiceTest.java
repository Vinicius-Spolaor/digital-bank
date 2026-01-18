package com.digitalbank.service;

import com.digitalbank.dto.EmailDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void shouldSendTestEmail() {
        EmailDTO request = EmailDTO.builder()
                .to("test@example.com")
                .subject("Test Email")
                .template("transfer-received")
                .variables(Map.of(
                        "recipientName", "Test User",
                        "amount", "100.00",
                        "senderName", "Test Sender",
                        "transactionId", "TEST123",
                        "date", "2024-01-17"
                ))
                .async(false)
                .build();

        EmailDTO response = emailService.sendEmail(request);

        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
    }
}
