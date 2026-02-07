package services.management;

import dao.*;
import factories.ValidatorFactory;
import models.*;
import services.core.LibraryService;
import validators.BookValidator;
import util.ValidationResult;
import java.util.Scanner;

public class BookManagementService {
    private final BookRepository bookRepository;
    private final LibraryService libraryService;
    private final ValidatorFactory validatorFactory;

    public BookManagementService(BookRepository bookRepository,
                                 LibraryService libraryService,
                                 ValidatorFactory validatorFactory) {
        this.bookRepository = bookRepository;
        this.libraryService = libraryService;
        this.validatorFactory = validatorFactory;
    }

    public void manageBooks(Scanner scanner, User currentUser) {
        if (!hasPermission(currentUser, "ADMIN") && !hasPermission(currentUser, "LIBRARIAN")) {
            System.out.println("Access denied! You need ADMIN or LIBRARIAN role.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n=== Book Management ===");
            System.out.println("1. View all books");
            System.out.println("2. Add new book");
            System.out.println("3. Update book");
            System.out.println("4. Delete book");
            System.out.println("5. Search books");
            System.out.println("6. View full book information");
            System.out.println("7. Back to main menu");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    libraryService.showAllBooks();
                    break;
                case 2:
                    addBook(scanner);
                    break;
                case 3:
                    updateBook(scanner);
                    break;
                case 4:
                    if (!hasPermission(currentUser, "ADMIN")) {
                        System.out.println("Only ADMIN can delete books!");
                        break;
                    }
                    deleteBook(scanner);
                    break;
                case 5:
                    libraryService.searchBooks(scanner);
                    break;
                case 6:
                    libraryService.viewFullBookInfo(scanner);
                    break;
                case 7:
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

    private void addBook(Scanner scanner) {
        System.out.println("\n=== Add New Book ===");
        System.out.print("Book title: ");
        String title = scanner.nextLine();

        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Total copies: ");
        int totalCopies = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Category: ");
        String category = scanner.nextLine();

        //Validation
        BookValidator validator = validatorFactory.getBookValidator();
        ValidationResult result = new ValidationResult();

        ValidationResult titleResult = validator.validateField("title", title);
        if (titleResult.hasErrors()) {
            result.getErrors().addAll(titleResult.getErrors());
        }

        ValidationResult authorResult = validator.validateField("author", author);
        if (authorResult.hasErrors()) {
            result.getErrors().addAll(authorResult.getErrors());
        }

        if (description != null && !description.isEmpty()) {
            ValidationResult descResult = validator.validateField("description", description);
            if (descResult.hasErrors()) {
                result.getErrors().addAll(descResult.getErrors());
            }
        }

        ValidationResult categoryResult = validator.validateField("category", category);
        if (categoryResult.hasErrors()) {
            result.getErrors().addAll(categoryResult.getErrors());
        }

        ValidationResult copiesResult = validator.validateCopies(totalCopies);
        if (copiesResult.hasErrors()) {
            result.getErrors().addAll(copiesResult.getErrors());
        }

        if (result.hasErrors()) {
            System.out.println("Validation error:");
            result.printErrors();
            return;
        }

        boolean success = bookRepository.createBook(title, author, description, totalCopies, category);
        if (success) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Failed to add book.");
        }
    }

    private void updateBook(Scanner scanner) {
        System.out.print("\nEnter book ID to update: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }

        System.out.println("Current book information:");
        System.out.println(book);

        System.out.print("\nNew title (leave empty to keep current): ");
        String title = scanner.nextLine();
        if (title.isEmpty()) title = book.getName();

        System.out.print("New author (leave empty to keep current): ");
        String author = scanner.nextLine();
        if (author.isEmpty()) author = book.getAuthor();

        System.out.print("New description (leave empty to keep current): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = book.getDescription();

        System.out.print("New total copies (enter 0 to keep current): ");
        int totalCopies = scanner.nextInt();
        scanner.nextLine();
        if (totalCopies == 0) totalCopies = book.getTotalCopies();

        System.out.print("New total copies (enter 0 to keep current): ");
        int availableCopies = scanner.nextInt();
        scanner.nextLine();
        if (availableCopies == 0) availableCopies = book.getAvailableCopies();

        System.out.print("New category (leave empty to keep current): ");
        String category = scanner.nextLine();
        if (category.isEmpty()) category = book.getCategory();

        // Validation
        BookValidator validator = validatorFactory.getBookValidator();
        ValidationResult result = new ValidationResult();

        ValidationResult bookIdResult = validator.validateBookId(bookId);
        if (bookIdResult.hasErrors()) {
            result.getErrors().addAll(bookIdResult.getErrors());
        }

        ValidationResult titleResult = validator.validateField("title", title);
        if (titleResult.hasErrors()) {
            result.getErrors().addAll(titleResult.getErrors());
        }

        ValidationResult authorResult = validator.validateField("author", author);
        if (authorResult.hasErrors()) {
            result.getErrors().addAll(authorResult.getErrors());
        }

        if (description != null && !description.isEmpty()) {
            ValidationResult descResult = validator.validateField("description", description);
            if (descResult.hasErrors()) {
                result.getErrors().addAll(descResult.getErrors());
            }
        }

        ValidationResult copiesResult = validator.validateCopies(totalCopies);
        if (copiesResult.hasErrors()) {
            result.getErrors().addAll(copiesResult.getErrors());
        }

        ValidationResult categoryResult = validator.validateField("category", category);
        if (categoryResult.hasErrors()) {
            result.getErrors().addAll(categoryResult.getErrors());
        }

        if (result.hasErrors()) {
            System.out.println("Validation error:");
            result.printErrors();
            return;
        }

        boolean success = bookRepository.updateBook(bookId, title, author, description, availableCopies, totalCopies, category);
        if (success) {
            System.out.println("Book updated successfully!");
        } else {
            System.out.println("Failed to update book.");
        }
    }

    private void deleteBook(Scanner scanner) {
        System.out.print("\nEnter book ID to delete: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        // Validation
        BookValidator validator = validatorFactory.getBookValidator();
        ValidationResult bookIdResult = validator.validateBookId(bookId);
        if (bookIdResult.hasErrors()) {
            System.out.println("Validation error:");
            bookIdResult.printErrors();
            return;
        }

        System.out.print("Are you sure? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            boolean success = bookRepository.deleteBook(bookId);
            if (success) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Failed to delete book.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}
