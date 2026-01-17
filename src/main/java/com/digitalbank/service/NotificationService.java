package com.digitalbank.service;

import com.digitalbank.entity.Notification;
import com.digitalbank.entity.BankTransaction;
import com.digitalbank.entity.enums.NotificationStatusEnum;
import com.digitalbank.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Async
    public void sendTransactionNotification(BankTransaction bankTransaction) {
        try {
            log.info("Sending notification for transaction {}", bankTransaction.getId());

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

            log.info("Notifications sent for transaction {}", bankTransaction.getId());

        } catch (Exception e) {
            log.error("Error sending notification", e);

            Notification errorNotification = Notification.builder()
                    .customer(bankTransaction.getOriginCustomer())
                    .bankTransaction(bankTransaction)
                    .message("Error sending notification: " + e.getMessage())
                    .notificationType(NotificationStatusEnum.ALERT)
                    .isSent(false)
                    .build();

            notificationRepository.save(errorNotification);
        }
    }

    public List<Notification> findNotificationByCustomer(Long customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }
}
