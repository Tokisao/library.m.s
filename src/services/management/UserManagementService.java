package services.management;

import dao.*;
import factories.ValidatorFactory;
import models.*;
import services.core.LibraryService;
import validators.UserValidator;
import util.ValidationResult;
import java.util.Scanner;

public class UserManagementService {
    private final UserRepository userRepository;
    private final LibraryService libraryService;
    private final ValidatorFactory validatorFactory;

    public UserManagementService(UserRepository userRepository,
                                 LibraryService libraryService,
                                 ValidatorFactory validatorFactory) {
        this.userRepository = userRepository;
        this.libraryService = libraryService;
        this.validatorFactory = validatorFactory;
    }

    public void manageUsers(Scanner scanner, User currentUser) {
        if (!hasPermission(currentUser, "ADMIN") && !hasPermission(currentUser, "LIBRARIAN")) {
            System.out.println("Access denied! You need ADMIN or LIBRARIAN role.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n=== User Management ===");
            System.out.println("1. View all users");
            System.out.println("2. Add new user");
            System.out.println("3. Update user");
            System.out.println("4. Delete user");
            System.out.println("5. Change user role");
            System.out.println("6. Back to main menu");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    libraryService.viewAllUsers();
                    break;
                case 2:
                    addUser(scanner);
                    break;
                case 3:
                    updateUser(scanner);
                    break;
                case 4:
                    if (!hasPermission(currentUser, "ADMIN")) {
                        System.out.println("Only ADMIN can delete users!");
                        break;
                    }
                    deleteUser(scanner);
                    break;
                case 5:
                    changeUserRole(scanner);
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private boolean hasPermission(User user, String requiredRole) {
        return user != null && user.getRole() != null &&
                user.getRole().equalsIgnoreCase(requiredRole);
    }

    private void addUser(Scanner scanner) {
        System.out.println("\n=== Add New User ===");
        System.out.print("User ID: ");
        String userId = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("First name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Phone number: ");
        String phone = scanner.nextLine();

        System.out.print("Role (USER/LIBRARIAN/ADMIN): ");
        String role = scanner.nextLine().toUpperCase();

        // Validation
        UserValidator validator = validatorFactory.getUserValidator();
        ValidationResult result = new ValidationResult();

        ValidationResult userIdResult = validator.validateField("userId", userId);
        if (userIdResult.hasErrors()) {
            result.getErrors().addAll(userIdResult.getErrors());
        }

        ValidationResult passResult = validator.validateField("password", password);
        if (passResult.hasErrors()) {
            result.getErrors().addAll(passResult.getErrors());
        }

        ValidationResult firstNameResult = validator.validateField("firstName", firstName);
        if (firstNameResult.hasErrors()) {
            result.getErrors().addAll(firstNameResult.getErrors());
        }

        ValidationResult lastNameResult = validator.validateField("lastName", lastName);
        if (lastNameResult.hasErrors()) {
            result.getErrors().addAll(lastNameResult.getErrors());
        }

        ValidationResult phoneResult = validator.validateField("phone", phone);
        if (phoneResult.hasErrors()) {
            result.getErrors().addAll(phoneResult.getErrors());
        }

        ValidationResult roleResult = validator.validateField("role", role);
        if (roleResult.hasErrors()) {
            result.getErrors().addAll(roleResult.getErrors());
        }

        if (result.hasErrors()) {
            System.out.println("Validation error:");
            result.printErrors();
            return;
        }

        boolean success = userRepository.createUser(userId, password, firstName, lastName, phone, role);
        if (success) {
            System.out.println("User added successfully!");
        } else {
            System.out.println("Failed to add user.");
        }
    }

    private void updateUser(Scanner scanner) {
        System.out.print("\nEnter user ID to update: ");
        String userId = scanner.nextLine();

        User user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        System.out.println("Current user information:");
        System.out.println(user);

        System.out.print("\nNew first name (leave empty to keep current): ");
        String firstName = scanner.nextLine();
        if (firstName.isEmpty()) firstName = user.getFirstName();

        System.out.print("New last name (leave empty to keep current): ");
        String lastName = scanner.nextLine();
        if (lastName.isEmpty()) lastName = user.getLastName();

        System.out.print("New phone number (leave empty to keep current): ");
        String phone = scanner.nextLine();
        if (phone.isEmpty()) phone = user.getPhoneNumber();

        System.out.print("New role (leave empty to keep current): ");
        String role = scanner.nextLine().toUpperCase();
        if (role.isEmpty()) role = user.getRole();

        // Validation
        UserValidator validator = validatorFactory.getUserValidator();
        ValidationResult result = new ValidationResult();

        ValidationResult userIdResult = validator.validateField("userId", userId);
        if (userIdResult.hasErrors()) {
            result.getErrors().addAll(userIdResult.getErrors());
        }

        ValidationResult firstNameResult = validator.validateField("firstName", firstName);
        if (firstNameResult.hasErrors()) {
            result.getErrors().addAll(firstNameResult.getErrors());
        }

        ValidationResult lastNameResult = validator.validateField("lastName", lastName);
        if (lastNameResult.hasErrors()) {
            result.getErrors().addAll(lastNameResult.getErrors());
        }

        ValidationResult phoneResult = validator.validateField("phone", phone);
        if (phoneResult.hasErrors()) {
            result.getErrors().addAll(phoneResult.getErrors());
        }

        if (!role.isEmpty()) {
            ValidationResult roleResult = validator.validateField("role", role);
            if (roleResult.hasErrors()) {
                result.getErrors().addAll(roleResult.getErrors());
            }
        }

        if (result.hasErrors()) {
            System.out.println("Validation error:");
            result.printErrors();
            return;
        }

        boolean success = userRepository.updateUser(userId, firstName, lastName, phone, role);
        if (success) {
            System.out.println("User updated successfully!");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private void deleteUser(Scanner scanner) {
        System.out.print("\nEnter user ID to delete: ");
        String userId = scanner.nextLine();

        // Validation
        UserValidator validator = validatorFactory.getUserValidator();
        ValidationResult userIdResult = validator.validateField("userId", userId);
        if (userIdResult.hasErrors()) {
            System.out.println("Validation error:");
            userIdResult.printErrors();
            return;
        }

        System.out.print("Are you sure? This will delete all user's borrow records. (yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            boolean success = userRepository.deleteUser(userId);
            if (success) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("Failed to delete user.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void changeUserRole(Scanner scanner) {
        System.out.print("\nEnter user ID: ");
        String userId = scanner.nextLine();

        User user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        System.out.println("Current role: " + user.getRole());
        System.out.print("New role (USER/LIBRARIAN/ADMIN): ");
        String newRole = scanner.nextLine().toUpperCase();

        // Validation
        UserValidator validator = validatorFactory.getUserValidator();
        ValidationResult result = new ValidationResult();

        ValidationResult userIdResult = validator.validateField("userId", userId);
        if (userIdResult.hasErrors()) {
            result.getErrors().addAll(userIdResult.getErrors());
        }

        ValidationResult roleResult = validator.validateField("role", newRole);
        if (roleResult.hasErrors()) {
            result.getErrors().addAll(roleResult.getErrors());
        }

        if (result.hasErrors()) {
            System.out.println("Validation error:");
            result.printErrors();
            return;
        }

        boolean success = userRepository.changeUserRole(userId, newRole);
        if (success) {
            System.out.println("User role changed successfully!");
        } else {
            System.out.println("Failed to change user role.");
        }
    }
}
