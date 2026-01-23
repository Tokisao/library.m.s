package com.library.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationHelper {

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        String phoneRegex = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$";
        return Pattern.compile(phoneRegex).matcher(phone).matches();
    }

    public static boolean isValidISBN(String isbn) {
        if (isbn == null) return false;

        String cleanIsbn = isbn.replaceAll("[\\s-]+", "");

        if (cleanIsbn.length() == 10) {
            return isValidISBN10(cleanIsbn);
        } else if (cleanIsbn.length() == 13) {
            return isValidISBN13(cleanIsbn);
        }
        return false;
    }

    private static boolean isValidISBN10(String isbn) {
        if (!isbn.matches("^\\d{9}[\\dX]$")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }

        char lastChar = isbn.charAt(9);
        if (lastChar == 'X' || lastChar == 'x') {
            sum += 10;
        } else {
            sum += lastChar - '0';
        }

        return sum % 11 == 0;
    }

    private static boolean isValidISBN13(String isbn) {
        if (!isbn.matches("^\\d{13}$")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        return sum % 10 == 0;
    }

    public static boolean isValidPublicationYear(int year) {
        int currentYear = LocalDate.now().getYear();
        return year > 0 && year <= currentYear;
    }

    public static boolean isValidAge(LocalDate birthDate, int minAge) {
        if (birthDate == null) return false;

        LocalDate now = LocalDate.now();
        int age = now.getYear() - birthDate.getYear();

        if (now.getMonthValue() < birthDate.getMonthValue() ||
                (now.getMonthValue() == birthDate.getMonthValue() &&
                        now.getDayOfMonth() < birthDate.getDayOfMonth())) {
            age--;
        }

        return age >= minAge;
    }

    public static boolean isValidPageCount(int pages) {
        return pages > 0 && pages <= 10000;
    }

    public static boolean isValidRating(double rating) {
        return rating >= 0 && rating <= 5;
    }

    public static boolean isValidCopyCount(int copies) {
        return copies > 0 && copies <= 1000;
    }

    public static String formatValidationErrors(String field, String error) {
        return String.format(" Validation error for '%s': %s", field, error);
    }
}