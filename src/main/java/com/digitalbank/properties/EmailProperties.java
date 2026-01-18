package com.digitalbank.properties;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailProperties {
    private final String fromEmail;
    private final String fromName;
    private final String supportEmail;
    private final String companyName;
}
