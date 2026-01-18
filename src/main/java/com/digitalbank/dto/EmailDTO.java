package com.digitalbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String template;
    private Map<String, Object> variables;
    private boolean async;
    private boolean success;
}
