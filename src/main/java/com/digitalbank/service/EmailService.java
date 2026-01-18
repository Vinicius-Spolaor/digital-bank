package com.digitalbank.service;

import com.digitalbank.dto.EmailDTO;
import com.digitalbank.properties.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;
    private final EmailProperties emailProperties;

    /**
     * Send transfer notification email
     */
    @Async
    public void sendTransferEmail(String recipientEmail, String recipientName,
                                  String senderName, String amount,
                                  String transactionId, String date) {

        Map<String, Object> variables = Map.of(
                "recipientName", recipientName,
                "senderName", senderName,
                "amount", amount,
                "transactionId", transactionId,
                "date", date
        );

        EmailDTO request = EmailDTO.builder()
                .to(recipientEmail)
                .subject(String.format("Transfer Received: $%s from %s", amount, senderName))
                .template("transfer-received")
                .variables(variables)
                .async(true)
                .build();

        sendEmailAsync(request)
                .thenAccept(response -> {
                    if (response.isSuccess()) {
                        log.info("Transfer email sent to: {}", recipientEmail);
                    } else {
                        log.error("Failed to send transfer email to: {}", recipientEmail);
                    }
                });
    }

    /**
     * Send email asynchronously
     */
    @Async("emailTaskExecutor")
    private CompletableFuture<EmailDTO> sendEmailAsync(EmailDTO request) {
        return CompletableFuture.supplyAsync(() -> sendEmail(request));
    }

    @Retryable(
            retryFor = {MailException.class, MessagingException.class},
            backoff = @Backoff(delay = 5000, multiplier = 2)
    )
    public EmailDTO sendEmail(EmailDTO request) {
        try {
            log.info("Sending email to: {}, Subject: {}", request.getTo(), request.getSubject());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(request.getTo());

            if (StringUtils.isNotBlank(request.getCc())) {
                helper.setCc(request.getCc());
            }

            if (StringUtils.isNotBlank(request.getBcc())) {
                helper.setBcc(request.getBcc());
            }

            helper.setFrom(emailProperties.getFromEmail(), emailProperties.getFromName());
            helper.setSubject(request.getSubject());
            helper.setSentDate(new Date());

            String htmlContent = templateService.buildHtml(request.getTemplate(), request.getVariables());
            String textContent = templateService.buildText(request.getTemplate(), request.getVariables());

            helper.setText(textContent, htmlContent);

            mailSender.send(mimeMessage);

            log.info("Email sent successfully to: {}", request.getTo());

            request.setSuccess(true);
            return request;

        } catch (Exception e) {
            log.error("Failed to send email to: {}", request.getTo(), e);
            request.setSuccess(false);
            return request;
        }
    }
}
