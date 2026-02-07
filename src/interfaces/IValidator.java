package interfaces;

import util.ValidationResult;

public interface IValidator<T> {
    ValidationResult validate(T entity);
    ValidationResult validateField(String fieldName, String value);
}
