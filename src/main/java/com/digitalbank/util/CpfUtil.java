package com.digitalbank.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class CpfUtil {
    private static final Pattern CPF_NUMERIC_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern CPF_FORMATTED_PATTERN = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");

    private CpfUtil() {}

    /**
     * Normalize CPF, removing special characters
     */
    public static String normalize(String cpf) {
        if (StringUtils.isBlank(cpf)) {
            return cpf;
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Format CPF as 000.000.000-00
     */
    public static String format(String cpf) {
        if (StringUtils.isBlank(cpf)) {
            return cpf;
        }

        String normalized = normalize(cpf);

        if (normalized.length() != 11) {
            return cpf;
        }

        return String.format("%s.%s.%s-%s",
                normalized.substring(0, 3),
                normalized.substring(3, 6),
                normalized.substring(6, 9),
                normalized.substring(9, 11));
    }

    /**
     * Validate CPF
     */
    public static boolean isValid(String cpf) {
        if (StringUtils.isBlank(cpf)) {
            return false;
        }

        String normalized = normalize(cpf);

        if (normalized.length() != 11) {
            return false;
        }

        if (normalized.chars().allMatch(c -> c == normalized.charAt(0))) {
            return false;
        }

        try {
            return validateVerifierDigits(normalized);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Generate random CPF
     */
    public static String generate() {
        java.util.Random random = new java.util.Random();

        StringBuilder cpf = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            cpf.append(random.nextInt(10));
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(cpf.charAt(i));
            sum += digit * (10 - i);
        }

        int firstVerifier = 11 - (sum % 11);
        if (firstVerifier >= 10) {
            firstVerifier = 0;
        }
        cpf.append(firstVerifier);

        sum = 0;
        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(cpf.charAt(i));
            sum += digit * (11 - i);
        }

        int secondVerifier = 11 - (sum % 11);
        if (secondVerifier >= 10) {
            secondVerifier = 0;
        }
        cpf.append(secondVerifier);

        return cpf.toString();
    }

    //region Helpers
    private static boolean validateVerifierDigits(String cpf) {
        // Valida primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(cpf.charAt(i));
            sum += digit * (10 - i);
        }

        int firstVerifier = 11 - (sum % 11);
        if (firstVerifier >= 10) {
            firstVerifier = 0;
        }

        if (firstVerifier != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        // Valida segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(cpf.charAt(i));
            sum += digit * (11 - i);
        }

        int secondVerifier = 11 - (sum % 11);
        if (secondVerifier >= 10) {
            secondVerifier = 0;
        }

        return secondVerifier == Character.getNumericValue(cpf.charAt(10));
    }
    //endregion
}
