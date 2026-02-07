package services.management;

import services.core.*;
import models.User;
import java.util.Optional;
import java.util.Scanner;

public class MenuManager {
    private final Scanner scanner;
    private final Authentication authService;
    private final LibraryService libraryService;
    private final FineService fineService;
    private final BookManagementService bookService;
    private final UserManagementService userService;
    private final BorrowManagementService borrowService;
    private final ReportService reportService;

    public MenuManager(Scanner scanner, Authentication authService, LibraryService libraryService,
                       FineService fineService, BookManagementService bookService,
                       UserManagementService userService, BorrowManagementService borrowService,
                       ReportService reportService) {
        this.scanner = scanner;
        this.authService = authService;
        this.libraryService = libraryService;
        this.fineService = fineService;
        this.bookService = bookService;
        this.userService = userService;
        this.borrowService = borrowService;
        this.reportService = reportService;
    }

    public void run() {
        System.out.println("~~~ Welcome to Library Management System ~~~");

        while (true) {
            if (libraryService.getCurrentUser() == null) {
                if (!showMainMenu()) break;
            } else {
                User currentUser = libraryService.getCurrentUser();
                String role = currentUser.getRole().toUpperCase();

                boolean continueSession = true;
                while (continueSession && libraryService.getCurrentUser() != null) {
                    switch (role) {
                        case "ADMIN":
                            continueSession = showAdminMenu(currentUser);
                            break;
                        case "LIBRARIAN":
                            continueSession = showLibrarianMenu(currentUser);
                            break;
                        case "USER":
                        default:
                            continueSession = showUserMenu();
                            break;
                    }
                }
            }
        }

        scanner.close();
        System.out.println("\nThank you for using our library system!");
    }

    private boolean showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Login");
        System.out.println("2. View available books");
        System.out.println("3. Exit");
        System.out.print("Choose option (1-3): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                Optional<User> user = authService.login(scanner);
                user.ifPresent(libraryService::setCurrentUser);
                user.ifPresent(fineService::setCurrentUser);
                return true;
            case 2:
                libraryService.showAllBooks();
                return true;
            case 3:
                System.out.println("Thank you for visiting our library!");
                return false;
            default:
                System.out.println("Invalid choice!");
                return true;
        }
    }

    private boolean showUserMenu() {
        System.out.println("\n=== User Menu ===");
        System.out.println("1. View available books");
        System.out.println("2. Borrow a book");
        System.out.println("3. View my borrows");
        System.out.println("4. View my account & fines");
        System.out.println("5. Search books");
        System.out.println("6. Logout");
        System.out.print("Choose option (1-6): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                libraryService.showAllBooks();
                return true;
            case 2:
                libraryService.borrowBook(scanner);
                return true;
            case 3:
                libraryService.viewMyBorrows();
                return true;
            case 4:
                libraryService.showUserInfo();
                fineService.calculateAndUpdateAllFines();
                fineService.showCurrentFines();
                return true;
            case 5:
                libraryService.searchBooks(scanner);
                return true;
            case 6:
                libraryService.setCurrentUser(null);
                fineService.setCurrentUser(null);
                System.out.println("Logged out successfully.");
                return false;
            default:
                System.out.println("Invalid choice!");
                return true;
        }
    }

    private boolean showLibrarianMenu(User currentUser) {
        System.out.println("\n=== Librarian Menu ===");
        System.out.println("=== Welcome, " + currentUser.getFirstName() + " (" + currentUser.getRole() + ") ===");
        System.out.println("1. Book Management");
        System.out.println("2. User Management");
        System.out.println("3. Borrow Management");
        System.out.println("4. View all books");
        System.out.println("5. Borrow a book (personal)");
        System.out.println("6. View my account");
        System.out.println("7. Logout");
        System.out.print("Choose option (1-7): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                bookService.manageBooks(scanner, currentUser);
                return true;
            case 2:
                userService.manageUsers(scanner, currentUser);
                return true;
            case 3:
                borrowService.manageBorrows(scanner, currentUser);
                return true;
            case 4:
                libraryService.showAllBooks();
                return true;
            case 5:
                libraryService.borrowBook(scanner);
                return true;
            case 6:
                libraryService.showUserInfo();
                fineService.calculateAndUpdateAllFines();
                fineService.showCurrentFines();
                return true;
            case 7:
                libraryService.setCurrentUser(null);
                fineService.setCurrentUser(null);
                System.out.println("Logged out successfully.");
                return false;
            default:
                System.out.println("Invalid choice!");
                return true;
        }
    }

    private boolean showAdminMenu(User currentUser) {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("=== Welcome, " + currentUser.getFirstName() + " (" + currentUser.getRole() + ") ===");
        System.out.println("1. Book Management");
        System.out.println("2. User Management");
        System.out.println("3. Borrow Management");
        System.out.println("4. System Reports");
        System.out.println("5. View all books");
        System.out.println("6. Borrow a book (personal)");
        System.out.println("7. View my account");
        System.out.println("8. Logout");
        System.out.print("Choose option (1-8): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                bookService.manageBooks(scanner, currentUser);
                return true;
            case 2:
                userService.manageUsers(scanner, currentUser);
                return true;
            case 3:
                borrowService.manageBorrows(scanner, currentUser);
                return true;
            case 4:
                reportService.showReportsMenu(scanner);
                return true;
            case 5:
                libraryService.showAllBooks();
                return true;
            case 6:
                libraryService.borrowBook(scanner);
                return true;
            case 7:
                libraryService.showUserInfo();
                fineService.calculateAndUpdateAllFines();
                fineService.showCurrentFines();
                return true;
            case 8:
                libraryService.setCurrentUser(null);
                fineService.setCurrentUser(null);
                System.out.println("Logged out successfully.");
                return false;
            default:
                System.out.println("Invalid choice!");
                return true;
        }
    }
}
