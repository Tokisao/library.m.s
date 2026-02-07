package factories;

import interfaces.IValidator;
import validators.*;

public class ValidatorFactory {

    public IValidator<?> createValidator(Class<?> entityType) {
        if (entityType == models.User.class) {
            return new UserValidator();
        } else if (entityType == models.Book.class) {
            return new BookValidator();
        } else if (entityType == models.Borrows.class) {
            return new BorrowValidator();
        }
        throw new IllegalArgumentException("No validator found for type: " + entityType.getName());
    }

    public IValidator<?> createValidator(String entityName) {
        switch (entityName.toUpperCase()) {
            case "USER":
                return new UserValidator();
            case "BOOK":
                return new BookValidator();
            case "BORROW":
            case "BORROWS":
                return new BorrowValidator();
            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityName);
        }
    }

    public UserValidator getUserValidator() {
        return new UserValidator();
    }

    public BookValidator getBookValidator() {
        return new BookValidator();
    }

    public BorrowValidator getBorrowValidator() {
        return new BorrowValidator();
    }
}
