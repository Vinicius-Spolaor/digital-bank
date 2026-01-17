package com.digitalbank.controller;

import com.digitalbank.dto.CustomerDTO;
import com.digitalbank.entity.Customer;
import com.digitalbank.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "API to handle Customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create new Customer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find Customer by ID")
    public ResponseEntity<Customer> findCustomer(@PathVariable Long id) {
        Customer customer = customerService.findCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    @Operation(summary = "List all Customers")
    public ResponseEntity<List<Customer>> listCustomers() {
        List<Customer> clientes = customerService.listAllCustomers();
        return ResponseEntity.ok(clientes);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
