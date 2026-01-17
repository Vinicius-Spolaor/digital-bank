package com.digitalbank.entity;

import com.digitalbank.entity.enums.BankTransactionStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "BANK_TRANSACTION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_customer_id", nullable = false)
    private Customer originCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_customer_id", nullable = false)
    private Customer destinationCustomer;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private BankTransactionStatusEnum status;

    @Column(name = "transaction_date")
    private Instant transactionDate;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "description")
    private String description;

    @PrePersist
    protected void onCreate() {
        transactionDate = Instant.now();

        if (status == null) {
            status = BankTransactionStatusEnum.PENDING;
        }
    }

}
