package com.digitalbank.service;

import com.digitalbank.dto.BankTransactionRequestDTO;
import com.digitalbank.entity.BankTransaction;
import com.digitalbank.entity.Customer;
import com.digitalbank.exception.custom.TransactionException;
import com.digitalbank.repository.BankTransactionRepository;
import com.digitalbank.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankTransactionServiceTest {

    @Mock
    private BankTransactionRepository bankTransactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BankTransactionService bankTransactionService;

    private Customer origin;
    private Customer destination;
    private BankTransactionRequestDTO request;

    @BeforeEach
    void setUp() {
        origin = Customer.builder()
                .id(1L)
                .name("João Silva")
                .balance(new BigDecimal("1000.00"))
                .build();

        destination = Customer.builder()
                .id(2L)
                .name("Maria Santos")
                .balance(new BigDecimal("500.00"))
                .build();

        request = BankTransactionRequestDTO.builder()
                .originCustomerId(1L)
                .destinationCustomerId(2L)
                .amount(new BigDecimal("200.00"))
                .build();
    }

    @Test
    void shouldProcessTransactionWithSuccess() {
        // Arrange
        BankTransaction bt = BankTransaction.builder().id(1L).amount(request.getAmount()).build();
        when(customerRepository.findByIdWithLock(1L)).thenReturn(Optional.of(origin));
        when(customerRepository.findByIdWithLock(2L)).thenReturn(Optional.of(destination));
        when(bankTransactionRepository.saveAndFlush(any())).thenReturn(bt);

        // Act
        var response = bankTransactionService.process(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getOriginCustomerId());
        assertEquals(2L, response.getDestinationCustomerId());
        assertEquals(new BigDecimal("200.00"), response.getAmount());

        verify(customerRepository, times(2)).save(any(Customer.class));
        verify(bankTransactionRepository, times(1)).saveAndFlush(any());
        verify(notificationService, times(1)).sendTransactionNotification(any());
    }

    @Test
    void shouldThrowTransactionException() {
        // Arrange
        request.setAmount(new BigDecimal("1500.00"));
        when(customerRepository.findByIdWithLock(1L)).thenReturn(Optional.of(origin));
        when(customerRepository.findByIdWithLock(2L)).thenReturn(Optional.of(destination));

        // Act & Assert
        assertThrows(TransactionException.class, () -> bankTransactionService.process(request));

        verify(customerRepository, never()).save(any(Customer.class));
    }
}
