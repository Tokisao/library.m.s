package util;
import dao.*;
import models.*;
import java.util.List;
import java.util.Scanner;

public class AdminService {
    private static AdminService instance;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowsRepository borrowRepository;
    private final LibraryService libraryService;
    private AdminService(BookRepository bookRepository,
                         UserRepository userRepository,
                         BorrowsRepository borrowRepository, LibraryService libraryService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.borrowRepository = borrowRepository;
        this.libraryService = libraryService;
    }

    // Singleton getInstance метод
    public static synchronized AdminService getInstance(BookRepository bookRepository,
                                                        UserRepository userRepository,
                                                        BorrowsRepository borrowRepository) {
        if (instance == null) {
            LibraryService libraryService = new LibraryService(bookRepository, borrowRepository, userRepository);
            instance = new AdminService(bookRepository, userRepository, borrowRepository, libraryService);
        }
        return instance;
    }

    public boolean hasPermission(User user, String requiredRole) {
        return user != null && user.getRole() != null &&
                user.getRole().equalsIgnoreCase(requiredRole);
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

        System.out.print("New category (leave empty to keep current): ");
        String category = scanner.nextLine();
        if (category.isEmpty()) category = book.getCategory();

        boolean success = bookRepository.updateBook(bookId, title, author, description, totalCopies, category);
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

        if (!role.matches("USER|LIBRARIAN|ADMIN")) {
            System.out.println("Invalid role! Defaulting to USER.");
            role = "USER";
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

        if (!role.matches("USER|LIBRARIAN|ADMIN|")) {
            System.out.println("Invalid role! Keeping current role.");
            role = user.getRole();
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

        if (!newRole.matches("USER|LIBRARIAN|ADMIN")) {
            System.out.println("Invalid role!");
            return;
        }

        boolean success = userRepository.changeUserRole(userId, newRole);
        if (success) {
            System.out.println("User role changed successfully!");
        } else {
            System.out.println("Failed to change user role.");
        }
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

        boolean success = borrowRepository.extendDueDate(borrowId, days);
        if (success) {
            System.out.println("Due date extended by " + days + " days.");
        } else {
            System.out.println("Failed to extend due date.");
        }
    }

}
