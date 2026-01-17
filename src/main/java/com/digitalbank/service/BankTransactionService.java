package com.digitalbank.service;

import com.digitalbank.dto.BankTransactionRequestDTO;
import com.digitalbank.dto.BankTransactionResponseDTO;
import com.digitalbank.entity.BankTransaction;
import com.digitalbank.entity.Customer;
import com.digitalbank.entity.enums.BankTransactionStatusEnum;
import com.digitalbank.exception.custom.InsufficientBalanceException;
import com.digitalbank.exception.custom.TransactionException;
import com.digitalbank.repository.BankTransactionRepository;
import com.digitalbank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankTransactionService {
    private final BankTransactionRepository bankTransactionRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BankTransactionResponseDTO process(BankTransactionRequestDTO request) {
        log.info("Processing transaction: {} -> {}", request.getOriginCustomerId(), request.getDestinationCustomerId());

        try {
            validateTransaction(request);

            Customer origin = customerRepository.findByIdWithLock(request.getOriginCustomerId())
                    .orElseThrow(() -> new TransactionException("Origin Customer not found"));

            Customer destination = customerRepository.findByIdWithLock(request.getDestinationCustomerId())
                    .orElseThrow(() -> new TransactionException("Destination Customer not found"));

            if (origin.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException(origin.getBalance(), request.getAmount());
            }

            origin.setBalance(origin.getBalance().subtract(request.getAmount()));
            destination.setBalance(destination.getBalance().add(request.getAmount()));

            customerRepository.save(origin);
            customerRepository.save(destination);

            BankTransaction bankTransaction = BankTransaction.builder()
                    .originCustomer(origin)
                    .destinationCustomer(destination)
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .status(BankTransactionStatusEnum.COMPLETED)
                    .build();

            BankTransaction savedBankTransaction = bankTransactionRepository.saveAndFlush(bankTransaction);

            notificationService.sendTransactionNotification(savedBankTransaction);

            log.info("Transaction {} succeeded", savedBankTransaction.getId());

            return BankTransactionResponseDTO.builder()
                    .id(savedBankTransaction.getId())
                    .originCustomerId(origin.getId())
                    .destinationCustomerId(destination.getId())
                    .amount(savedBankTransaction.getAmount())
                    .status(savedBankTransaction.getStatus())
                    .transactionDate(savedBankTransaction.getTransactionDate())
                    .description(savedBankTransaction.getDescription())
                    .message("Transaction succeeded")
                    .build();

        } catch (Exception e) {
            log.error("Error processing transaction", e);
            throw new TransactionException("Error processing transaction: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public BankTransactionResponseDTO findBankTransactionById(Long id) {
        BankTransaction bankTransaction = bankTransactionRepository.findById(id)
                .orElseThrow(() -> new TransactionException("Bank Transaction not found"));

        return mapToResponseDTO(bankTransaction);
    }

    //region Helpers
    private void validateTransaction(BankTransactionRequestDTO request) {
        if (request.getOriginCustomerId().equals(request.getDestinationCustomerId())) {
            throw new TransactionException("You cannot do a transaction to the same origin customer");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Transaction amount should be higher than 0");
        }
    }

    private BankTransactionResponseDTO mapToResponseDTO(BankTransaction bankTransaction) {
        return BankTransactionResponseDTO.builder()
                .id(bankTransaction.getId())
                .originCustomerId(bankTransaction.getOriginCustomer().getId())
                .destinationCustomerId(bankTransaction.getDestinationCustomer().getId())
                .amount(bankTransaction.getAmount())
                .status(bankTransaction.getStatus())
                .transactionDate(bankTransaction.getTransactionDate())
                .message(bankTransaction.getErrorMessage() != null ? "Failed: " + bankTransaction.getErrorMessage() : "Success")
                .build();
    }
    //endregion
}
