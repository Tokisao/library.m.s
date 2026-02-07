package services.core;

import dao.*;
import factories.ValidatorFactory;
import models.*;
import validators.BookValidator;
import util.ValidationResult;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class LibraryService {
    private final BookRepository bookRepository;
    private final BorrowsRepository borrowRepository;
    private final UserRepository userRepository;
    private final ValidatorFactory validatorFactory;
    public User currentUser;

    public LibraryService(BookRepository bookRepository, BorrowsRepository borrowRepository,
                          UserRepository userRepository, ValidatorFactory validatorFactory) {
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
        this.userRepository = userRepository;
        this.validatorFactory = validatorFactory;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void showAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        System.out.println("\n~~~ Books ~~~");
        books.forEach(System.out::println);
    }

    public void viewMyBorrows() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        List<Borrows> borrows = borrowRepository.findByUserId(currentUser.getUserId());
        if (borrows.isEmpty()) {
            System.out.println("You have no borrowed books.");
            return;
        }

        System.out.println("\n~~~ Your Borrowed Books ~~~");
        borrows.forEach(System.out::println);
    }

    public void showUserInfo() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }
        System.out.println("~~~User INFO~~~");
        System.out.println(currentUser);
    }

    public void borrowBook(Scanner scanner) {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.print("Enter book ID to borrow: ");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine());

            // Validation
            BookValidator bookValidator = validatorFactory.getBookValidator();
            ValidationResult bookIdResult = bookValidator.validateBookId(bookId);
            if (bookIdResult.hasErrors()) {
                System.out.println("Validation error:");
                bookIdResult.printErrors();
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid book ID! Must be a number.");
            return;
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }

        if (book.getAvailableCopies() <= 0) {
            System.out.println("No copies available!");
            return;
        }

        int userBorrowedCount = borrowRepository.getUserBorrowedCount(currentUser.getUserId());
        if (userBorrowedCount >= 5) {
            System.out.println("You have reached the borrowing limit (5 books)!");
            return;
        }

        if (borrowRepository.hasOverdueBooks(currentUser.getUserId())) {
            System.out.println("You have overdue books! Please return them first.");
            return;
        }

        if (borrowRepository.hasLostBooks(currentUser.getUserId())) {
            System.out.println("You lost a book, return them first.");
            return;
        }

        if (borrowRepository.isBookBorrowedByUser(bookId, currentUser.getUserId())) {
            System.out.println("You have already borrowed this book!");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 14);
        Date dueDate = calendar.getTime();

        boolean borrowCreated = borrowRepository.createBorrow(bookId, currentUser.getUserId(), dueDate);

        if (borrowCreated) {
            bookRepository.updateAvailableCopies(bookId, -1);
            System.out.println("Book borrowed successfully!");
            System.out.println("Due date: " + dueDate);
            System.out.println("You have borrowed " + (userBorrowedCount + 1) + " out of 5 books.");
        } else {
            System.out.println("Failed to borrow book.");
        }
    }

    public void searchBooks(Scanner scanner) {
        System.out.println("\n=== Search Books ===");
        System.out.println("Search by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Category");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter search term: ");
        String term = scanner.nextLine().toLowerCase();

        if (term == null || term.trim().isEmpty()) {
            System.out.println("Search term cannot be empty!");
            return;
        }

        List<Book> allBooks = bookRepository.findAll();
        List<Book> results = new ArrayList<>();
        for (Book book : allBooks) {
            boolean matches = false;

            switch (choice) {
                case 1: // by name
                    matches = book.getName().toLowerCase().contains(term);
                    break;
                case 2: // by author
                    matches = book.getAuthor().toLowerCase().contains(term);
                    break;
                case 3: // by category
                    matches = book.getCategory() != null &&
                            book.getCategory().toLowerCase().contains(term);
                    break;
                default:
                    System.out.println("Invalid choice!");
                    return;
            }

            if (matches) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("\n=== FOUND " + results.size() + " BOOK(S) ===");
            results.forEach(book -> System.out.println(book));
        }
    }

    public void viewFullBookInfo(Scanner scanner) {
        System.out.print("\nEnter book ID: ");

        try {
            int bookId = scanner.nextInt();
            scanner.nextLine();

            // Validation
            BookValidator bookValidator = validatorFactory.getBookValidator();
            ValidationResult bookIdResult = bookValidator.validateBookId(bookId);
            if (bookIdResult.hasErrors()) {
                System.out.println("Validation error:");
                bookIdResult.printErrors();
                return;
            }

            Book book = bookRepository.findById(bookId);
            if (book != null) {
                System.out.println("\n=== Full Book Information ===");
                System.out.println(book);
            } else {
                System.out.println("Book not found!");
            }

        } catch (Exception e) {
            System.out.println("Invalid input! Book ID must be a number.");
            scanner.nextLine();
        }
    }

    public void viewAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n=== All Users ===");
        users.forEach(user -> {
            System.out.println("ID: " + user.getUserId() +
                    ", Name: " + user.getFirstName() + " " + user.getLastName() +
                    ", Role: " + user.getRole() +
                    ", Fines: " + user.getFines());
        });
    }
}
