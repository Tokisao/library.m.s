package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static LocalDate validateDate(String dateStr, String format) {
        if (isEmpty(dateStr)) {
            throw new IllegalArgumentException("Date cannot be empty");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate parsedDate = LocalDate.parse(dateStr, formatter);

            if (parsedDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Date cannot be in the past");
            }

            return parsedDate;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use: " + format);
        }
    }

    public static String formatPhone(String phone) {
        if (isEmpty(phone)) return phone;

        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 11) {
            return "+7 (" + digits.substring(1, 4) + ") " + digits.substring(4, 7) + "-" +
                    digits.substring(7, 9) + "-" + digits.substring(9);
        }
        return phone;
    }
}
