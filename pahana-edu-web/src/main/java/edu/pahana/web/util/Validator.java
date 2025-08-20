package edu.pahana.web.util;

import java.util.HashMap;
import java.util.Map;

public class Validator {

    public static Map<String, String> validateCustomer(String accountNumber, String name, String address, String phone) {
        Map<String, String> errors = new HashMap<>();

        if (isBlank(accountNumber) || !accountNumber.matches("^[A-Za-z0-9-]{3,30}$")) {
            errors.put("accountNumber", "Account Number must be 3-30 chars, letters/digits/dash.");
        }
        if (isBlank(name) || name.length() < 2 || name.length() > 120) {
            errors.put("name", "Name must be 2-120 characters.");
        }
        if (address != null && address.length() > 255) {
            errors.put("address", "Address must be at most 255 characters.");
        }
        if (phone != null && !phone.isBlank() && !phone.matches("^[+0-9][0-9\\-\\s]{6,20}$")) {
            errors.put("phone", "Phone must be digits (optional +), 7-21 chars.");
        }

        return errors;
    }

    public static Map<String, String> validateItem(String code, String description, String unitPriceStr) {
        Map<String, String> errors = new HashMap<>();

        if (isBlank(code) || !code.matches("^[A-Za-z0-9-]{2,30}$")) {
            errors.put("code", "Code must be 2-30 chars, letters/digits/dash.");
        }
        if (isBlank(description) || description.length() < 2 || description.length() > 255) {
            errors.put("description", "Description must be 2-255 characters.");
        }
        if (isBlank(unitPriceStr)) {
            errors.put("unitPrice", "Unit Price is required.");
        } else {
            try {
                double v = Double.parseDouble(unitPriceStr);
                if (v < 0) errors.put("unitPrice", "Unit Price must be >= 0.");
            } catch (NumberFormatException nfe) {
                errors.put("unitPrice", "Unit Price must be a number.");
            }
        }

        return errors;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
