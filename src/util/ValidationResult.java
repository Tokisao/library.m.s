package util;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private boolean isValid;
    private List<String> errors;

    public ValidationResult() {
        this.isValid = true;
        this.errors = new ArrayList<>();
    }

    public void addError(String error) {
        this.errors.add(error);
        this.isValid = false;
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void printErrors() {
        if (!isValid) {
            System.out.println("Validation errors:");
            for (String error : errors) {
                System.out.println("  - " + error);
            }
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
