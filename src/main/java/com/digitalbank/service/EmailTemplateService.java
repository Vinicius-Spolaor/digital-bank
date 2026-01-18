package com.digitalbank.service;

import com.digitalbank.exception.custom.EmailTemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {
    private final TemplateEngine templateEngine;

    public String buildHtml(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context(Locale.getDefault());

            context.setVariable("currentYear", java.time.Year.now().getValue());
            context.setVariable("companyName", "Digital Bank");
            context.setVariable("supportEmail", "support@digitalbank.com");

            if (variables != null) {
                variables.forEach(context::setVariable);
            }

            String templatePath = "email/" + templateName + ".html";
            return templateEngine.process(templatePath, context);

        } catch (Exception e) {
            log.error("Error building email template: {}", templateName, e);
            throw new EmailTemplateException("Failed to build email template: " + templateName, e);
        }
    }

    public String buildText(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context(Locale.getDefault());

            if (variables != null) {
                variables.forEach(context::setVariable);
            }

            String templatePath = "email/" + templateName + ".txt";
            return templateEngine.process(templatePath, context);

        } catch (Exception e) {
            log.error("Error building text template: {}", templateName, e);
            throw new EmailTemplateException("Failed to build text template: " + templateName, e);
        }
    }
}
