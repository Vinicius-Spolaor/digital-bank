package com.digitalbank.entity;

import com.digitalbank.util.CpfUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "CUSTOMER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void preValidations() {
        if (this.cpf != null) {
            this.cpf = CpfUtil.normalize(this.cpf);
        }

        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();

        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }
}
