package validators;

import interfaces.IValidator;
import models.User;
import util.*;
import java.util.regex.Pattern;

public class UserValidator implements IValidator<User> {
    private static final Pattern USER_ID = Pattern.compile("^[A-Za-z0-9_]{5,25}$");
    private static final Pattern NAME = Pattern.compile("^[A-Za-z\\s-]{2,50}$");
    private static final String[] VALID_OPERATORS = {"701", "702", "705", "707", "708", "747", "771", "775", "776", "777", "778"};

    @Override
    public ValidationResult validate(User user) {
        ValidationResult result = new ValidationResult();

        if (user == null) {
            result.addError("User cannot be null");
            return result;
        }

        //User Id
        ValidationResult userIdResult = validateField("userId", user.getUserId());
        if (userIdResult.hasErrors()) {
            result.getErrors().addAll(userIdResult.getErrors());
        }

        // Name
        ValidationResult firstNameResult = validateField("firstName", user.getFirstName());
        if (firstNameResult.hasErrors()) {
            result.getErrors().addAll(firstNameResult.getErrors());
        }

        // Surname
        ValidationResult lastNameResult = validateField("lastName", user.getLastName());
        if (lastNameResult.hasErrors()) {
            result.getErrors().addAll(lastNameResult.getErrors());
        }

        // Phone
        ValidationResult phoneResult = validateField("phone", user.getPhoneNumber());
        if (phoneResult.hasErrors()) {
            result.getErrors().addAll(phoneResult.getErrors());
        }

        // Role
        ValidationResult roleResult = validateField("role", user.getRole());
        if (roleResult.hasErrors()) {
            result.getErrors().addAll(roleResult.getErrors());
        }

        return result;
    }

    @Override
    public ValidationResult validateField(String fieldName, String value) {
        ValidationResult result = new ValidationResult();

        try {
            switch (fieldName) {
                case "userId":
                    validateUserId(value, result);
                    break;
                case "firstName":
                case "lastName":
                    validateName(value, fieldName, result);
                    break;
                case "phone":
                    validatePhone(value, result);
                    break;
                case "role":
                    validateRole(value, result);
                    break;
                case "password":
                    validatePassword(value, result);
                    break;
                default:
                    result.addError("Unknown field: " + fieldName);
            }
        } catch (Exception e) {
            result.addError("Validation error for " + fieldName + ": " + e.getMessage());
        }

        return result;
    }

    private void validateUserId(String userId, ValidationResult result) {
        if (ValidationUtils.isEmpty(userId)) {
            result.addError("User ID cannot be empty");
            return;
        }

        if (!USER_ID.matcher(userId).matches()) {
            result.addError("User ID must be 5-25 characters (English letters, numbers, underscore)");
        }
    }

    private void validateName(String name, String fieldName, ValidationResult result) {
        if (ValidationUtils.isEmpty(name)) {
            result.addError(fieldName + " cannot be empty");
            return;
        }

        if (!NAME.matcher(name).matches()) {
            result.addError(fieldName + " must be 2-50 English letters, spaces or hyphens");
        }
    }

    private void validatePhone(String phone, ValidationResult result) {
        if (ValidationUtils.isEmpty(phone)) {
            result.addError("Phone number cannot be empty");
            return;
        }

        String cleanedPhone = phone.trim();
        String digitsOnly = cleanedPhone.replaceAll("[^0-9]", "");

        if (digitsOnly.length() != 11 && digitsOnly.length() != 12) {
            result.addError("Phone must be 11 digits (example: 77001234567) or 12 digits with +7");
            return;
        }

        if (!digitsOnly.startsWith("7")) {
            result.addError("Phone must start with 7 (Kazakhstan numbers)");
            return;
        }

        if (digitsOnly.length() >= 4) {
            String operatorCode = digitsOnly.substring(1, 4);
            boolean validOperator = false;
            for (String op : VALID_OPERATORS) {
                if (operatorCode.equals(op)) {
                    validOperator = true;
                    break;
                }
            }

            if (!validOperator) {
                result.addError("Invalid mobile operator code. Must be one of: 701, 702, 705, 707, 708, 747, 771, 775, 776, 777, 778");
            }
        }
    }

    private void validateRole(String role, ValidationResult result) {
        if (ValidationUtils.isEmpty(role)) {
            result.addError("Role cannot be empty");
            return;
        }

        String r = role.toUpperCase();
        if (!r.matches("USER|LIBRARIAN|ADMIN")) {
            result.addError("Role must be USER, LIBRARIAN or ADMIN");
        }
    }

    private void validatePassword(String password, ValidationResult result) {
        if (ValidationUtils.isEmpty(password)) {
            result.addError("Password cannot be empty");
        }
    }}
