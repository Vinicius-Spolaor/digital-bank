package com.digitalbank.controller;

import com.digitalbank.dto.BankTransactionRequestDTO;
import com.digitalbank.dto.BankTransactionResponseDTO;
import com.digitalbank.service.BankTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bank-transaction")
@RequiredArgsConstructor
@Tag(name = "Bank Transaction", description = "API to handle Bank Transactions")
public class BankTransactionController {
    private final BankTransactionService bankTransactionService;

    @PostMapping("/transfer")
    @Operation(summary = "Do a transfer transaction between customers")
    public ResponseEntity<BankTransactionResponseDTO> transfer(@Valid @RequestBody BankTransactionRequestDTO request) {
        BankTransactionResponseDTO response = bankTransactionService.process(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find bank transaction by ID")
    public ResponseEntity<BankTransactionResponseDTO> findBankTransaction(@PathVariable Long id) {
        BankTransactionResponseDTO response = bankTransactionService.findBankTransactionById(id);
        return ResponseEntity.ok(response);
    }
}
