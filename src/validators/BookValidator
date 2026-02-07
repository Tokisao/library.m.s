package validators;

import interfaces.IValidator;
import models.Book;
import util.*;
import java.util.regex.Pattern;

public class BookValidator implements IValidator<Book> {
    private static final Pattern CATEGORY = Pattern.compile("^[A-Za-z\\s&-]{2,30}$");

    @Override
    public ValidationResult validate(Book book) {
        ValidationResult result = new ValidationResult();

        if (book == null) {
            result.addError("Book cannot be null");
            return result;
        }

        // Name
        ValidationResult titleResult = validateField("title", book.getName());
        if (titleResult.hasErrors()) {
            result.getErrors().addAll(titleResult.getErrors());
        }

        // Author
        ValidationResult authorResult = validateField("author", book.getAuthor());
        if (authorResult.hasErrors()) {
            result.getErrors().addAll(authorResult.getErrors());
        }

        // Descr
        if (book.getDescription() != null) {
            ValidationResult descResult = validateField("description", book.getDescription());
            if (descResult.hasErrors()) {
                result.getErrors().addAll(descResult.getErrors());
            }
        }

        // Category
        ValidationResult categoryResult = validateField("category", book.getCategory());
        if (categoryResult.hasErrors()) {
            result.getErrors().addAll(categoryResult.getErrors());
        }

        // Copies
        if (book.getAvailableCopies() != null) {
            ValidationResult copiesResult = validateCopies(book.getAvailableCopies());
            if (copiesResult.hasErrors()) {
                result.getErrors().addAll(copiesResult.getErrors());
            }
        }

        return result;
    }

    @Override
    public ValidationResult validateField(String fieldName, String value) {
        ValidationResult result = new ValidationResult();

        try {
            switch (fieldName) {
                case "title":
                    validateTitle(value, result);
                    break;
                case "author":
                    validateAuthor(value, result);
                    break;
                case "description":
                    validateDescription(value, result);
                    break;
                case "category":
                    validateCategory(value, result);
                    break;
                default:
                    result.addError("Unknown field: " + fieldName);
            }
        } catch (Exception e) {
            result.addError("Validation error for " + fieldName + ": " + e.getMessage());
        }

        return result;
    }

    public ValidationResult validateCopies(Integer copies) {
        ValidationResult result = new ValidationResult();

        if (copies == null) {
            result.addError("Copies cannot be null");
            return result;
        }

        if (copies < 0) {
            result.addError("Copies cannot be negative");
        }

        if (copies > 15) {
            result.addError("Copies cannot exceed 15");
        }

        return result;
    }

    private void validateTitle(String title, ValidationResult result) {
        if (ValidationUtils.isEmpty(title)) {
            result.addError("Book title cannot be empty");
            return;
        }

        if (title.length() < 2 || title.length() > 200) {
            result.addError("Book title must be 2-200 characters");
        }
    }

    private void validateAuthor(String author, ValidationResult result) {
        if (ValidationUtils.isEmpty(author)) {
            result.addError("Author cannot be empty");
            return;
        }

        if (author.length() < 2 || author.length() > 50) {
            result.addError("Author name must be 2-50 characters");
        }
    }

    private void validateDescription(String description, ValidationResult result) {
        if (description != null && description.length() > 200) {
            result.addError("Description cannot exceed 200 characters");
        }
    }

    private void validateCategory(String category, ValidationResult result) {
        if (ValidationUtils.isEmpty(category)) {
            result.addError("Category cannot be empty");
            return;
        }

        if (!CATEGORY.matcher(category).matches()) {
            result.addError("Category must be 2-30 English letters, spaces, & or -");
        }
    }


    //  Book ID
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
}
