package services.core;

import dao.UserRepository;
import factories.ValidatorFactory;
import models.User;
import validators.UserValidator;
import util.ValidationResult;
import java.util.Optional;
import java.util.Scanner;

public class Authentication {
    private final UserRepository userRepository;
    private final ValidatorFactory validatorFactory;

    public Authentication(UserRepository userRepository, ValidatorFactory validatorFactory) {
        this.userRepository = userRepository;
        this.validatorFactory = validatorFactory;
    }

    public Optional<User> login(Scanner scanner) {
        System.out.print("\nEnter your ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // НОВАЯ ВАЛИДАЦИЯ
        UserValidator validator = validatorFactory.getUserValidator();
        ValidationResult result = new ValidationResult();

        // Валидация ID
        ValidationResult idResult = validator.validateField("userId", id);
        if (idResult.hasErrors()) {
            result.getErrors().addAll(idResult.getErrors());
        }

        // Валидация пароля
        ValidationResult passResult = validator.validateField("password", password);
        if (passResult.hasErrors()) {
            result.getErrors().addAll(passResult.getErrors());
        }

        if (result.hasErrors()) {
            System.out.println("Login error:");
            result.printErrors();
            return Optional.empty();
        }

        Optional<User> user = userRepository.findByIdAndPassword(id, password);

        if (user.isPresent()) {
            System.out.println("Login successful!");
            System.out.println("Welcome, " + user.get().getFirstName() + " " +
                    user.get().getLastName() + " (" + user.get().getRole() + ")");
            return user;
        } else {
            System.out.println("Invalid credentials. Try again.");
            return Optional.empty();
        }
    }

    public Optional<User> login(String id, String password) {
  
        UserValidator validator = validatorFactory.getUserValidator();
        ValidationResult result = new ValidationResult();

        ValidationResult idResult = validator.validateField("userId", id);
        if (idResult.hasErrors()) {
            result.getErrors().addAll(idResult.getErrors());
        }

        ValidationResult passResult = validator.validateField("password", password);
        if (passResult.hasErrors()) {
            result.getErrors().addAll(passResult.getErrors());
        }

        if (result.hasErrors()) {
            System.err.println("Login validation failed:");
            result.printErrors();
            return Optional.empty();
        }

        return userRepository.findByIdAndPassword(id, password);
    }
}
