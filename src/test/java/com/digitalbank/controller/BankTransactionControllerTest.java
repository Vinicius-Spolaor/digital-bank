package com.digitalbank.controller;

import com.digitalbank.dto.BankTransactionRequestDTO;
import com.digitalbank.service.BankTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankTransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BankTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BankTransactionService bankTransactionService;

    @Test
    void shouldReturnCreatedWhenTransactionSucceeded() throws Exception {
        // Arrange
        BankTransactionRequestDTO request = BankTransactionRequestDTO.builder()
                .originCustomerId(1L)
                .destinationCustomerId(2L)
                .amount(new BigDecimal("100.00"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/bank-transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidData() throws Exception {
        // Arrange
        BankTransactionRequestDTO request = BankTransactionRequestDTO.builder()
                .originCustomerId(1L)
                .destinationCustomerId(2L)
                .amount(new BigDecimal("-100.00"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/bank-transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
