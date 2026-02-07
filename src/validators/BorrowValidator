package validators;

import interfaces.IValidator;
import models.Borrows;
import util.*;

public class BorrowValidator implements IValidator<Borrows> {

    @Override
    public ValidationResult validate(Borrows borrow) {
        ValidationResult result = new ValidationResult();

        if (borrow == null) {
            result.addError("Borrow cannot be null");
            return result;
        }

        // Book Id
        ValidationResult bookIdResult = validateBookId(borrow.getBookId());
        if (bookIdResult.hasErrors()) {
            result.getErrors().addAll(bookIdResult.getErrors());
        }

        // User Id
        ValidationResult userIdResult = validateUserId(borrow.getUserId());
        if (userIdResult.hasErrors()) {
            result.getErrors().addAll(userIdResult.getErrors());
        }

        // Date
        if (borrow.getDueDate() != null) {
            ValidationResult dateResult = validateDueDate(borrow.getDueDate());
            if (dateResult.hasErrors()) {
                result.getErrors().addAll(dateResult.getErrors());
            }
        }

        return result;
    }

    @Override
    public ValidationResult validateField(String fieldName, String value) {
        ValidationResult result = new ValidationResult();

        result.addError("Field validation not supported for Borrows");
        return result;
    }

    public ValidationResult validateBookId(Integer bookId) {
        ValidationResult result = new ValidationResult();

        if (bookId == null) {
            result.addError("Book ID cannot be null");
            return result;
        }

        if (bookId <= 0) {
            result.addError("Book ID must be positive");
        }

        return result;
    }

    public ValidationResult validateUserId(String userId) {
        ValidationResult result = new ValidationResult();

        if (ValidationUtils.isEmpty(userId)) {
            result.addError("User ID cannot be empty");
        }

        return result;
    }

    public ValidationResult validateBorrowId(Integer borrowId) {
        ValidationResult result = new ValidationResult();

        if (borrowId == null) {
            result.addError("Borrow ID cannot be null");
            return result;
        }

        if (borrowId <= 0) {
            result.addError("Borrow ID must be positive");
        }

        return result;
    }

    public ValidationResult validateDueDate(java.util.Date dueDate) {
        ValidationResult result = new ValidationResult();

        if (dueDate == null) {
            result.addError("Due date cannot be null");
            return result;
        }

        java.util.Date now = new java.util.Date();
        if (dueDate.before(now)) {
            result.addError("Due date cannot be in the past");
        }

        return result;
    }

    public ValidationResult validateDaysExtended(Integer days) {
        ValidationResult result = new ValidationResult();

        if (days == null) {
            result.addError("Days extended cannot be null");
            return result;
        }

        if (days < 0) {
            result.addError("Days extended cannot be negative");
        }

        if (days > 365) {
            result.addError("Days extended cannot exceed 365");
        }

        return result;
    }

    public ValidationResult validateFineAmount(Float amount) {
        ValidationResult result = new ValidationResult();

        if (amount == null) {
            result.addError("Fine amount cannot be null");
            return result;
        }

        if (amount < 0) {
            result.addError("Fine amount cannot be negative");
        }

        if (amount > 1000000) {
            result.addError("Fine amount is too high");
        }

        return result;
    }
}
