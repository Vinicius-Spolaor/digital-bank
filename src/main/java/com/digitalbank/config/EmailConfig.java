package com.digitalbank.config;

import com.digitalbank.properties.EmailProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.email.support-email}")
    private String supportEmail;

    @Value("${app.email.company.name}")
    private String companyName;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(1025);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public EmailProperties emailProperties() {
        return EmailProperties.builder()
                .fromEmail(fromEmail)
                .fromName(fromName)
                .supportEmail(supportEmail)
                .companyName(companyName)
                .build();
    }
}
