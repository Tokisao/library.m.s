import util.LibraryService;
import util.Authentication;
import util.FineService;
import util.AdminService;
import models.User;
import dao.*;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.loadDriver();

        BookRepository bookRepository = new BookRepository();
        UserRepository userRepository = new UserRepository();
        BorrowsRepository borrowRepository = new BorrowsRepository();

        Authentication authService = new Authentication(userRepository);
        LibraryService libraryService = new LibraryService(
                bookRepository, borrowRepository, userRepository
        );
        FineService fineService = new FineService(
                userRepository, borrowRepository, bookRepository);
        AdminService adminService = AdminService.getInstance(
                bookRepository, userRepository, borrowRepository);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("~~~ Welcome to Library Management System ~~~");

        while (!exit) {
            if (libraryService.getCurrentUser() == null) {
                showMainMenu(scanner, authService, libraryService, fineService);
            } else {
                User currentUser = libraryService.getCurrentUser();
                String role = currentUser.getRole();

                switch (role.toUpperCase()) {
                    case "ADMIN":
                        showAdminMenu(scanner, libraryService, fineService, adminService, currentUser);
                        break;
                    case "LIBRARIAN":
                        showLibrarianMenu(scanner, libraryService, fineService, adminService, currentUser);
                        break;
                    case "USER":
                    default:
                        showUserMenu(scanner, libraryService, fineService);
                        break;
                }
            }
        }

        scanner.close();
        System.out.println("\nThank you for using our library system!");
    }

    private static void showMainMenu(Scanner scanner,
                                     Authentication authService,
                                     LibraryService libraryService,
                                     FineService fineService) {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Login");
        System.out.println("2. View available books");
        System.out.println("3. Register new account (User only)");
        System.out.println("4. Exit");
        System.out.print("Choose option (1-4): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                Optional<User> user = authService.login(scanner);
                user.ifPresent(libraryService::setCurrentUser);
                user.ifPresent(fineService::setCurrentUser);
                break;
            case 2:
                libraryService.showAllBooks();
                break;
            case 3:
                registerNewUser(scanner, authService, libraryService);
                break;
            case 4:
                System.out.println("Thank you for visiting our library!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void showUserMenu(Scanner scanner,
                                     LibraryService libraryService,
                                     FineService fineService) {
        boolean logout = false;

        while (!logout && libraryService.getCurrentUser() != null) {
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
                    break;
                case 2:
                    libraryService.borrowBook(scanner);
                    break;
                case 3:
                    libraryService.viewMyBorrows();
                    break;
                case 4:
                    libraryService.showUserInfo();
                    fineService.calculateAndUpdateAllFines();
                    fineService.showCurrentFines();
                    break;
                case 5:
                    libraryService.searchBooks(scanner);
                    break;
                case 6:
                    libraryService.setCurrentUser(null);
                    fineService.setCurrentUser(null);
                    System.out.println("Logged out successfully.");
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void showLibrarianMenu(Scanner scanner,
                                          LibraryService libraryService,
                                          FineService fineService,
                                          AdminService adminService,
                                          User currentUser) {
        boolean logout = false;

        while (!logout && libraryService.getCurrentUser() != null) {
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
                    adminService.manageBooks(scanner, currentUser);
                    break;
                case 2:
                    adminService.manageUsers(scanner, currentUser);
                    break;
                case 3:
                    adminService.manageBorrows(scanner, currentUser);
                    break;
                case 4:
                    libraryService.showAllBooks();
                    break;
                case 5:
                    libraryService.borrowBook(scanner);
                    break;
                case 6:
                    libraryService.showUserInfo();
                    fineService.calculateAndUpdateAllFines();
                    fineService.showCurrentFines();
                    break;
                case 7:
                    libraryService.setCurrentUser(null);
                    fineService.setCurrentUser(null);
                    System.out.println("Logged out successfully.");
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void showAdminMenu(Scanner scanner,
                                      LibraryService libraryService,
                                      FineService fineService,
                                      AdminService adminService,
                                      User currentUser) {
        boolean logout = false;

        while (!logout && libraryService.getCurrentUser() != null) {
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
                    adminService.manageBooks(scanner, currentUser);
                    break;
                case 2:
                    adminService.manageUsers(scanner, currentUser);
                    break;
                case 3:
                    adminService.manageBorrows(scanner, currentUser);
                    break;
                case 4:
                    generateReports(scanner, libraryService);
                    break;
                case 5:
                    libraryService.showAllBooks();
                    break;
                case 6:
                    libraryService.borrowBook(scanner);
                    break;
                case 7:
                    libraryService.showUserInfo();
                    fineService.calculateAndUpdateAllFines();
                    fineService.showCurrentFines();
                    break;
                case 8:
                    libraryService.setCurrentUser(null);
                    fineService.setCurrentUser(null);
                    System.out.println("Logged out successfully.");
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void registerNewUser(Scanner scanner,
                                        Authentication authService,
                                        LibraryService libraryService) {
        System.out.println("\n=== Register New Account ===");

        System.out.println("Please contact a librarian or administrator to create an account.");
        System.out.println("Only librarians and admins can create new accounts.");

        System.out.println("\nFor testing purposes:");
        System.out.println("1. Ask librarian for USER account");
        System.out.println("2. Use existing test accounts:");
        System.out.println("   - User: user1 / pass1 (USER role)");
        System.out.println("   - Librarian: lib1 / pass1 (LIBRARIAN role)");
        System.out.println("   - Admin: admin / admin (ADMIN role)");
    }
/*
private static void manageBorrows(Scanner scanner,
                                      LibraryService libraryService,
                                      User currentUser) {
        // Проверяем права доступа
        if (!currentUser.getRole().equals("ADMIN") &&
                !currentUser.getRole().equals("LIBRARIAN")) {
            System.out.println("Access denied! You need ADMIN or LIBRARIAN role.");
            return;
        }

        System.out.println("\n=== Borrow Management ===");
        System.out.println("This feature is under development.");
        System.out.println("Available actions:");
        System.out.println("1. View all borrows");
        System.out.println("2. Return book for user");
        System.out.println("3. Mark book as lost");
        System.out.println("4. Extend borrow period");

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                libraryService.showAllBorrows();
                break;
            case 2:
                System.out.print("Enter borrow ID to return: ");
                int borrowId = scanner.nextInt();
                scanner.nextLine();
                libraryService.returnBook(borrowId);
                break;
            default:
                System.out.println("Feature coming soon!");
        }
    }
*/
    private static void generateReports(Scanner scanner, LibraryService libraryService) {
        System.out.println("\n=== System Reports ===");
        System.out.println("1. Most borrowed books");
        System.out.println("2. Users with overdue books");
        System.out.println("3. Users with fines");
        System.out.println("4. Popular categories");

        System.out.print("Choose report type: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                libraryService.generateMostBorrowedReport();
                break;
            case 2:
                libraryService.generateOverdueReport();
                break;
            case 3:
                libraryService.generateFinesReport();
                break;
            case 4:
                libraryService.generateCategoryReport();
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }
}
