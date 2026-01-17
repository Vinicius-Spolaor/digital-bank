package com.digitalbank.service;

import com.digitalbank.dto.CustomerDTO;
import com.digitalbank.entity.Customer;
import com.digitalbank.exception.custom.CustomerNotFoundException;
import com.digitalbank.exception.custom.DuplicateResourceException;
import com.digitalbank.exception.custom.InvalidCpfException;
import com.digitalbank.repository.CustomerRepository;
import com.digitalbank.util.CpfUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(CustomerDTO customerDTO) {
        log.info("Creating new customer: {}", customerDTO.getName());

        String normalizedCpf = CpfUtil.normalize(customerDTO.getCpf());

        if (!CpfUtil.isValid(customerDTO.getCpf())) {
            throw new InvalidCpfException(customerDTO.getCpf());
        }

        customerRepository.findByCpf(normalizedCpf).ifPresent(c -> {
            throw new DuplicateResourceException("CPF already exists");
        });

        customerRepository.findByEmail(customerDTO.getEmail()).ifPresent(c -> {
            throw new DuplicateResourceException("Email already exists");
        });

        Customer customer = Customer.builder()
                .name(customerDTO.getName())
                .email(customerDTO.getEmail())
                .cpf(normalizedCpf)
                .balance(customerDTO.getBalance() != null ? customerDTO.getBalance() : BigDecimal.ZERO)
                .build();

        return customerRepository.save(customer);
    }

    public Customer findCustomerById(Long id) {
        log.debug("Searching customer with ID: {}", id);
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Customer> listAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer updateBalance(Long customerId, BigDecimal newBalance) {
        Customer customer = findCustomerById(customerId);
        customer.setBalance(newBalance);

        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }

        customerRepository.deleteById(id);
        log.info("Deleted customer with ID {}", id);
    }
}
