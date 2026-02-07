package services.management;

import dao.*;
import factories.ValidatorFactory;
import models.*;
import validators.BorrowValidator;
import util.ValidationResult;
import java.util.List;
import java.util.Scanner;

public class BorrowManagementService {
    private final BorrowsRepository borrowRepository;
    private final ValidatorFactory validatorFactory;

    public BorrowManagementService(BorrowsRepository borrowRepository,
                                   ValidatorFactory validatorFactory) {
        this.borrowRepository = borrowRepository;
        this.validatorFactory = validatorFactory;
    }

    public void manageBorrows(Scanner scanner, User currentUser) {
        if (!hasPermission(currentUser, "ADMIN") && !hasPermission(currentUser, "LIBRARIAN")) {
            System.out.println("Access denied! You need ADMIN or LIBRARIAN role.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n=== Borrow Management ===");
            System.out.println("1. View all borrows");
            System.out.println("2. View overdue borrows");
            System.out.println("3. Mark book as returned");
            System.out.println("4. Mark book as lost");
            System.out.println("5. Extend due date");
            System.out.println("6. Back to main menu");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAllBorrows();
                    break;
                case 2:
                    viewOverdueBorrows();
                    break;
                case 3:
                    markBookReturned(scanner);
                    break;
                case 4:
                    markBookLost(scanner);
                    break;
                case 5:
                    extendDueDate(scanner);
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

    private void viewAllBorrows() {
        List<Borrows> borrows = borrowRepository.findAll();
        if (borrows.isEmpty()) {
            System.out.println("No borrows found.");
            return;
        }
        System.out.println("\n=== All Borrows ===");
        borrows.forEach(System.out::println);
    }

    private void viewOverdueBorrows() {
        List<Borrows> overdueBorrows = borrowRepository.findOverdueBorrows();
        if (overdueBorrows.isEmpty()) {
            System.out.println("No overdue borrows found.");
            return;
        }
        System.out.println("\n=== Overdue Borrows ===");
        overdueBorrows.forEach(borrow -> {
            System.out.println("Borrow ID: " + borrow.getId() +
                    ", User: " + borrow.getUserId() +
                    ", Book ID: " + borrow.getBookId() +
                    ", Due Date: " + borrow.getDueDate() +
                    ", Days Overdue: " + borrow.getDaysExtended());
        });
    }

    private void markBookReturned(Scanner scanner) {
        System.out.print("\nEnter borrow ID: ");
        int borrowId = scanner.nextInt();
        scanner.nextLine();

        //Validation
        BorrowValidator validator = validatorFactory.getBorrowValidator();
        ValidationResult borrowIdResult = validator.validateBorrowId(borrowId);
        if (borrowIdResult.hasErrors()) {
            System.out.println("Validation error:");
            borrowIdResult.printErrors();
            return;
        }

        boolean success = borrowRepository.markAsReturned(borrowId);
        if (success) {
            System.out.println("Book marked as returned.");
        } else {
            System.out.println("Failed to update borrow status.");
        }
    }

    private void markBookLost(Scanner scanner) {
        System.out.print("\nEnter borrow ID: ");
        int borrowId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter fine amount: ");
        float fineAmount = scanner.nextFloat();
        scanner.nextLine();

        //Validation
        BorrowValidator validator = validatorFactory.getBorrowValidator();
        ValidationResult result = new ValidationResult();

        ValidationResult borrowIdResult = validator.validateBorrowId(borrowId);
        if (borrowIdResult.hasErrors()) {
            result.getErrors().addAll(borrowIdResult.getErrors());
        }

        ValidationResult fineResult = validator.validateFineAmount(fineAmount);
        if (fineResult.hasErrors()) {
            result.getErrors().addAll(fineResult.getErrors());
        }

        if (result.hasErrors()) {
            System.out.println("Validation error:");
            result.printErrors();
            return;
        }

        boolean success = borrowRepository.markAsLost(borrowId, fineAmount);
        if (success) {
            System.out.println("Book marked as lost with fine: " + fineAmount);
        } else {
            System.out.println("Failed to update borrow status.");
        }
    }

    private void extendDueDate(Scanner scanner) {
        System.out.print("\nEnter borrow ID: ");
        int borrowId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter days to extend: ");
        int days = scanner.nextInt();
        scanner.nextLine();

        // Validation
        BorrowValidator validator = validatorFactory.getBorrowValidator();
        ValidationResult result = new ValidationResult();

        ValidationResult borrowIdResult = validator.validateBorrowId(borrowId);
        if (borrowIdResult.hasErrors()) {
            result.getErrors().addAll(borrowIdResult.getErrors());
        }

        ValidationResult daysResult = validator.validateDaysExtended(days);
        if (daysResult.hasErrors()) {
            result.getErrors().addAll(daysResult.getErrors());
        }

        if (result.hasErrors()) {
            System.out.println("Validation error:");
            result.printErrors();
            return;
        }

        boolean success = borrowRepository.extendDueDate(borrowId, days);
        if (success) {
            System.out.println("Due date extended by " + days + " days.");
        } else {
            System.out.println("Failed to extend due date.");
        }
    }
}
