package com.digitalbank.entity;

import com.digitalbank.entity.enums.NotificationStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "NOTIFICATION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_transaction_id")
    private BankTransaction bankTransaction;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationStatusEnum notificationType;

    @Column(name = "sent_at")
    private Instant sendAt;

    @Column(name = "is_sent")
    private boolean isSent;

    @PrePersist
    protected void onCreate() {
        sendAt = Instant.now();
    }

}
