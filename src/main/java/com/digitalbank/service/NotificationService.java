package com.digitalbank.service;

import com.digitalbank.entity.BankTransaction;
import com.digitalbank.entity.Customer;
import com.digitalbank.entity.Notification;
import com.digitalbank.entity.enums.NotificationStatusEnum;
import com.digitalbank.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Async()
    public CompletableFuture<Void> sendTransactionNotification(BankTransaction bankTransaction) {
        return CompletableFuture.runAsync(() -> {
            String threadName = Thread.currentThread().getName();
            log.info("[{}] Starting notification for transaction {}", threadName, bankTransaction.getId());

            try {
                saveNotificationsToDatabase(bankTransaction);
                sendEmailNotifications(bankTransaction);

                log.info("[{}] Notification completed for transaction {}", threadName, bankTransaction.getId());

            } catch (Exception e) {
                log.error("[{}] Error in notification for transaction {}", threadName, bankTransaction.getId(), e);
            }
        });
    }

    private void saveNotificationsToDatabase(BankTransaction bankTransaction) {
        try {
            Notification destinationNotification = Notification.builder()
                    .customer(bankTransaction.getDestinationCustomer())
                    .bankTransaction(bankTransaction)
                    .message(String.format("You received a transaction of $ %.2f from %s",
                            bankTransaction.getAmount(), bankTransaction.getOriginCustomer().getName()))
                    .notificationType(NotificationStatusEnum.TRANSFER_RECEIVED)
                    .isSent(true)
                    .build();

            Notification originNotification = Notification.builder()
                    .customer(bankTransaction.getOriginCustomer())
                    .bankTransaction(bankTransaction)
                    .message(String.format("Transaction of $ %.2f to %s completed",
                            bankTransaction.getAmount(), bankTransaction.getOriginCustomer().getName()))
                    .notificationType(NotificationStatusEnum.TRANSFER_SENT)
                    .isSent(true)
                    .build();

            notificationRepository.saveAll(List.of(destinationNotification, originNotification));

        } catch (Exception e) {
            log.error("Failed to save notifications to database", e);
            throw e;
        }
    }

    private void sendEmailNotifications(BankTransaction transaction) {
        try {
            Customer sender = transaction.getOriginCustomer();
            Customer recipient = transaction.getDestinationCustomer();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
            String formattedDate = LocalDateTime.ofInstant(
                    transaction.getTransactionDate(),
                    ZoneId.systemDefault()
            ).format(formatter);

            emailService.sendTransferEmail(
                    "vinisspy@gmail.com",
                    recipient.getName(),
                    sender.getName(),
                    String.format("%.2f", transaction.getAmount()),
                    transaction.getId().toString(),
                    formattedDate
            );
        } catch (Exception e) {
            log.error("Failed to send email notifications", e);
        }
    }
}
