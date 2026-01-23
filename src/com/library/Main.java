package com.library;

import com.library.model.*;
import com.library.service.LibraryService;
import com.library.util.ConsoleHelper;
import com.library.util.ValidationHelper;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private static LibraryService libraryService = new LibraryService();

    public static void main(String[] args) {
        DatabaseConnection.testConnection();

        libraryService.updateOverdueStatuses();

        //Main menu
        boolean running = true;
        while (running) {
            ConsoleHelper.printHeader("LIBRARY MANAGEMENT SYSTEM");

            String[] mainMenu = {
                    "Manage Books",
                    "Manage Users",
                    "Manage Borrowings",
                    "Search",
                    "Reports & Analytics",
                    "System Tools",
                    "Exit"
            };

            ConsoleHelper.printMenu(mainMenu);
            int choice = ConsoleHelper.readInt("Select option (1-7): ");

            switch (choice) {
                case 1:
                    manageBooks();
                    break;
                case 2:
                    manageUsers();
                    break;
                case 3:
                    manageBorrowings();
                    break;
                case 4:
                    search();
                    break;
                case 5:
                    reportsAndAnalytics();
                    break;
                case 6:
                    systemTools();
                    break;
                case 7:
                    running = false;
                    System.out.println("\nThank you for using Library Management System!");
                    System.out.println("Happy Reading!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    ConsoleHelper.pause();
            }
        }

        DatabaseConnection.closeConnection();
    }

    private static void manageBooks() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.printHeader("MANAGE BOOKS");

            String[] menu = {
                    "View All Books",
                    "Add New Book",
                    "View Book Details",
                    "Update Book Information",
                    "Delete Book",
                    "View Available Books",
                    "View Popular Books",
                    "Search Books by Genre",
                    "Back to Main Menu"
            };

            ConsoleHelper.printMenu(menu);
            int choice = ConsoleHelper.readInt("Select option (1-9): ");

            switch (choice) {
                case 1:
                    viewAllBooks();
                    break;
                case 2:
                    addNewBook();
                    break;
                case 3:
                    viewBookDetails();
                    break;
                case 4:
                    updateBook();
                    break;
                case 5:
                    deleteBook();
                    break;
                case 6:
                    viewAvailableBooks();
                    break;
                case 7:
                    viewPopularBooks();
                    break;
                case 8:
                    searchBooksByGenre();
                    break;
                case 9:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    ConsoleHelper.pause();
            }
        }
    }

    private static void viewAllBooks() {
        ConsoleHelper.printHeader("ALL BOOKS");
        List<Book> books = libraryService.getBookDAO().getAllBooks();
        ConsoleHelper.printTable(books, "Library Books");
        ConsoleHelper.pause();
    }

    private static void addNewBook() {
        ConsoleHelper.printHeader("ADD NEW BOOK");

        System.out.println("Please enter book details:");

        String title;
        while (true) {
            title = ConsoleHelper.readString("Title: ");
            if (!title.trim().isEmpty()) break;
            System.out.println("Title cannot be empty.");
        }

        String author;
        while (true) {
            author = ConsoleHelper.readString("Author: ");
            if (!author.trim().isEmpty()) break;
            System.out.println("Author cannot be empty.");
        }

        String isbn;
        while (true) {
            isbn = ConsoleHelper.readString("ISBN (optional): ");
            if (isbn.isEmpty() || ValidationHelper.isValidISBN(isbn)) break;
            System.out.println("Invalid ISBN format.");
        }

        String publisher = ConsoleHelper.readString("Publisher: ");

        int year;
        while (true) {
            year = ConsoleHelper.readInt("Publication Year: ");
            if (ValidationHelper.isValidPublicationYear(year)) break;
            System.out.println("Invalid publication year.");
        }

        int copies;
        while (true) {
            copies = ConsoleHelper.readInt("Total Copies: ");
            if (ValidationHelper.isValidCopyCount(copies)) break;
            System.out.println("Invalid number of copies.");
        }

        String genre = ConsoleHelper.readString("Genre: ");
        String language = ConsoleHelper.readString("Language: ");

        int pages;
        while (true) {
            pages = ConsoleHelper.readInt("Number of Pages: ");
            if (ValidationHelper.isValidPageCount(pages)) break;
            System.out.println("Invalid page count.");
        }

        String description = ConsoleHelper.readString("Description: ");

        double rating;
        while (true) {
            rating = ConsoleHelper.readDouble("Initial Rating (0-5): ", 0, 5);
            if (ValidationHelper.isValidRating(rating)) break;
            System.out.println("Rating must be between 0 and 5.");
        }

        Book book = new Book(title, author, isbn, publisher, year, copies,
                genre, language, pages, description, rating);

        if (libraryService.getBookDAO().addBook(book)) {
            System.out.println("\nBook added successfully!");
            System.out.println("Book ID: " + book.getBookId());
            System.out.println("Location: " + book.getLocation());
        } else {
            System.out.println("Failed to add book. ISBN might already exist.");
        }
        ConsoleHelper.pause();
    }

    private static void viewBookDetails() {
        ConsoleHelper.printHeader("VIEW BOOK DETAILS");
        int bookId = ConsoleHelper.readInt("Enter Book ID: ");

        Book book = libraryService.getBookDAO().getBookById(bookId);
        if (book != null) {
            System.out.println(book.toDetailedString());

            List<Borrowing> history = libraryService.getBorrowingDAO().getBorrowingHistoryByBook(bookId);
            if (!history.isEmpty()) {
                System.out.println("\nBorrowing History:");
                for (int i = 0; i < Math.min(history.size(), 5); i++) {
                    Borrowing b = history.get(i);
                    System.out.printf("  %d. %s (Borrowed: %s, Status: %s)%n",
                            i + 1, b.getUserName(), b.getBorrowedDate(), b.getStatus());
                }
            }
        } else {
            System.out.println("Book not found.");
        }
        ConsoleHelper.pause();
    }

    private static void updateBook() {
        ConsoleHelper.printHeader("UPDATE BOOK");
        int bookId = ConsoleHelper.readInt("Enter Book ID to update: ");

        Book book = libraryService.getBookDAO().getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("Current book details:");
        System.out.println(book.toDetailedString());
        System.out.println("\nEnter new details (press Enter to keep current value):");

        String title = ConsoleHelper.readString("Title [" + book.getTitle() + "]: ");
        if (!title.isEmpty()) book.setTitle(title);

        String author = ConsoleHelper.readString("Author [" + book.getAuthor() + "]: ");
        if (!author.isEmpty()) book.setAuthor(author);

        String isbn = ConsoleHelper.readString("ISBN [" + book.getIsbn() + "]: ");
        if (!isbn.isEmpty()) {
            if (ValidationHelper.isValidISBN(isbn)) {
                book.setIsbn(isbn);
            } else {
                System.out.println("Invalid ISBN. Keeping current value.");
            }
        }

        String publisher = ConsoleHelper.readString("Publisher [" + book.getPublisher() + "]: ");
        if (!publisher.isEmpty()) book.setPublisher(publisher);

        String yearInput = ConsoleHelper.readString("Publication Year [" + book.getPublicationYear() + "]: ");
        if (!yearInput.isEmpty()) {
            int year = Integer.parseInt(yearInput);
            if (ValidationHelper.isValidPublicationYear(year)) {
                book.setPublicationYear(year);
            } else {
                System.out.println("Invalid year. Keeping current value.");
            }
        }

        String copiesInput = ConsoleHelper.readString("Total Copies [" + book.getTotalCopies() + "]: ");
        if (!copiesInput.isEmpty()) {
            int copies = Integer.parseInt(copiesInput);
            if (ValidationHelper.isValidCopyCount(copies)) {
                book.setTotalCopies(copies);
            } else {
                System.out.println("Invalid copy count. Keeping current value.");
            }
        }

        String genre = ConsoleHelper.readString("Genre [" + book.getGenre() + "]: ");
        if (!genre.isEmpty()) book.setGenre(genre);

        String language = ConsoleHelper.readString("Language [" + book.getLanguage() + "]: ");
        if (!language.isEmpty()) book.setLanguage(language);

        String pagesInput = ConsoleHelper.readString("Pages [" + book.getPages() + "]: ");
        if (!pagesInput.isEmpty()) {
            int pages = Integer.parseInt(pagesInput);
            if (ValidationHelper.isValidPageCount(pages)) {
                book.setPages(pages);
            } else {
                System.out.println("Invalid page count. Keeping current value.");
            }
        }

        String description = ConsoleHelper.readString("Description [" + book.getDescription() + "]: ");
        if (!description.isEmpty()) book.setDescription(description);

        String ratingInput = ConsoleHelper.readString("Rating [" + book.getRating() + "]: ");
        if (!ratingInput.isEmpty()) {
            double rating = Double.parseDouble(ratingInput);
            if (ValidationHelper.isValidRating(rating)) {
                book.setRating(rating);
            } else {
                System.out.println("Invalid rating. Keeping current value.");
            }
        }

        if (ConsoleHelper.confirm("Save changes?")) {
            if (libraryService.getBookDAO().updateBook(book)) {
                System.out.println("Book updated successfully!");
            } else {
                System.out.println("Failed to update book.");
            }
        }
        ConsoleHelper.pause();
    }

    private static void deleteBook() {
        ConsoleHelper.printHeader("DELETE BOOK");
        int bookId = ConsoleHelper.readInt("Enter Book ID to delete: ");

        Book book = libraryService.getBookDAO().getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            ConsoleHelper.pause();
            return;
        }

        List<Borrowing> activeBorrowings = libraryService.getBorrowingDAO().getAllActiveBorrowings();
        boolean isBorrowed = activeBorrowings.stream()
                .anyMatch(b -> b.getBookId() == bookId);

        if (isBorrowed) {
            System.out.println("Cannot delete book. It is currently borrowed.");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("Book to delete:");
        System.out.println(book.toDetailedString());

        if (ConsoleHelper.confirm("Are you sure you want to delete this book?")) {
            if (libraryService.getBookDAO().deleteBook(bookId)) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Failed to delete book.");
            }
        }
        ConsoleHelper.pause();
    }

    private static void viewAvailableBooks() {
        ConsoleHelper.printHeader("AVAILABLE BOOKS");
        List<Book> books = libraryService.getBookDAO().getAvailableBooks();
        ConsoleHelper.printTable(books, "Available Books");
        ConsoleHelper.pause();
    }

    private static void viewPopularBooks() {
        ConsoleHelper.printHeader("POPULAR BOOKS");
        int limit = ConsoleHelper.readInt("How many books to show? ", 1, 20);
        List<Book> books = libraryService.getBookDAO().getPopularBooks(limit);
        ConsoleHelper.printTable(books, "Most Popular Books");
        ConsoleHelper.pause();
    }

    private static void searchBooksByGenre() {
        ConsoleHelper.printHeader("SEARCH BOOKS BY GENRE");
        String genre = ConsoleHelper.readString("Enter genre: ");
        List<Book> books = libraryService.getBookDAO().getBooksByGenre(genre);
        ConsoleHelper.printTable(books, "Books in Genre: " + genre);
        ConsoleHelper.pause();
    }

    private static void manageUsers() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.printHeader("MANAGE USERS");

            String[] menu = {
                    "View All Users",
                    "Add New User",
                    "View User Details",
                    "Update User Information",
                    "Delete User",
                    "Change User Status",
                    "View Active Users",
                    "Search Users",
                    "Back to Main Menu"
            };

            ConsoleHelper.printMenu(menu);
            int choice = ConsoleHelper.readInt("Select option (1-9): ");

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    addNewUser();
                    break;
                case 3:
                    viewUserDetails();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 6:
                    changeUserStatus();
                    break;
                case 7:
                    viewActiveUsers();
                    break;
                case 8:
                    searchUsers();
                    break;
                case 9:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    ConsoleHelper.pause();
            }
        }
    }

    private static void viewAllUsers() {
        ConsoleHelper.printHeader("ALL USERS");
        List<User> users = libraryService.getUserDAO().getAllUsers();
        ConsoleHelper.printTable(users, "Library Users");
        ConsoleHelper.pause();
    }

    private static void addNewUser() {
        ConsoleHelper.printHeader("ADD NEW USER");

        System.out.println("Please enter user details:");

        String libraryId;
        while (true) {
            libraryId = ConsoleHelper.readString("Library ID (e.g., LIB001): ");
            if (!libraryId.trim().isEmpty()) break;
            System.out.println("Library ID cannot be empty.");
        }

        if (libraryService.getUserDAO().getUserByLibraryId(libraryId) != null) {
            System.out.println("Library ID already exists.");
            ConsoleHelper.pause();
            return;
        }

        String firstName;
        while (true) {
            firstName = ConsoleHelper.readString("First Name: ");
            if (!firstName.trim().isEmpty()) break;
            System.out.println("First name cannot be empty.");
        }

        String lastName;
        while (true) {
            lastName = ConsoleHelper.readString("Last Name: ");
            if (!lastName.trim().isEmpty()) break;
            System.out.println("Last name cannot be empty.");
        }

        String email;
        while (true) {
            email = ConsoleHelper.readString("Email: ");
            if (ValidationHelper.isValidEmail(email)) break;
            System.out.println("Invalid email format.");
        }

        String phone = ConsoleHelper.readString("Phone (optional): ");
        if (!phone.isEmpty() && !ValidationHelper.isValidPhone(phone)) {
            System.out.println("Phone number format may be invalid, but continuing...");
        }

        String address = ConsoleHelper.readString("Address: ");

        LocalDate dob;
        while (true) {
            dob = ConsoleHelper.readDate("Date of Birth");
            if (ValidationHelper.isValidAge(dob, LibraryService.getMinUserAge())) break;
            System.out.printf("User must be at least %d years old.%n", LibraryService.getMinUserAge());
        }

        User user = new User(libraryId, firstName, lastName, email, phone, address, dob);

        if (libraryService.getUserDAO().addUser(user)) {
            System.out.println("\nUser added successfully!");
            System.out.println("User ID: " + user.getUserId());
            System.out.println("Registration Date: " + user.getRegistrationDate());
        } else {
            System.out.println("Failed to add user. Email might already exist.");
        }
        ConsoleHelper.pause();
    }

    private static void viewUserDetails() {
        ConsoleHelper.printHeader("VIEW USER DETAILS");
        int userId = ConsoleHelper.readInt("Enter User ID: ");

        User user = libraryService.getUserDAO().getUserById(userId);
        if (user != null) {
            System.out.println(user.toDetailedString());

            List<Borrowing> activeBorrowings = libraryService.getBorrowingDAO().getActiveBorrowingsByUser(userId);
            if (!activeBorrowings.isEmpty()) {
                System.out.println("\nCurrently Borrowed Books:");
                for (Borrowing borrowing : activeBorrowings) {
                    System.out.printf("  - %s (Due: %s, Status: %s)%n",
                            borrowing.getBookTitle(), borrowing.getDueDate(), borrowing.getStatus());
                }
            }
        } else {
            System.out.println("User not found.");
        }
        ConsoleHelper.pause();
    }

    private static void updateUser() {
        ConsoleHelper.printHeader("UPDATE USER");
        int userId = ConsoleHelper.readInt("Enter User ID to update: ");

        User user = libraryService.getUserDAO().getUserById(userId);
        if (user == null) {
            System.out.println("User not found.");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("Current user details:");
        System.out.println(user.toDetailedString());
        System.out.println("\nEnter new details (press Enter to keep current value):");

        String libraryId = ConsoleHelper.readString("Library ID [" + user.getLibraryId() + "]: ");
        if (!libraryId.isEmpty()) {
            User existing = libraryService.getUserDAO().getUserByLibraryId(libraryId);
            if (existing != null && existing.getUserId() != userId) {
                System.out.println("Library ID already exists. Keeping current value.");
            } else {
                user.setLibraryId(libraryId);
            }
        }

        String firstName = ConsoleHelper.readString("First Name [" + user.getFirstName() + "]: ");
        if (!firstName.isEmpty()) user.setFirstName(firstName);

        String lastName = ConsoleHelper.readString("Last Name [" + user.getLastName() + "]: ");
        if (!lastName.isEmpty()) user.setLastName(lastName);

        String email = ConsoleHelper.readString("Email [" + user.getEmail() + "]: ");
        if (!email.isEmpty()) {
            if (ValidationHelper.isValidEmail(email)) {
                user.setEmail(email);
            } else {
                System.out.println("Invalid email. Keeping current value.");
            }
        }

        String phone = ConsoleHelper.readString("Phone [" + user.getPhone() + "]: ");
        if (!phone.isEmpty()) user.setPhone(phone);

        String address = ConsoleHelper.readString("Address [" + user.getAddress() + "]: ");
        if (!address.isEmpty()) user.setAddress(address);

        String dobInput = ConsoleHelper.readString("Date of Birth [" + user.getDateOfBirth() + "] (YYYY-MM-DD): ");
        if (!dobInput.isEmpty()) {
            LocalDate dob = LocalDate.parse(dobInput);
            if (ValidationHelper.isValidAge(dob, LibraryService.getMinUserAge())) {
                user.setDateOfBirth(dob);
            } else {
                System.out.printf("User must be at least %d years old. Keeping current value.%n",
                        LibraryService.getMinUserAge());
            }
        }

        if (ConsoleHelper.confirm("Save changes?")) {
            if (libraryService.getUserDAO().updateUser(user)) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("Failed to update user.");
            }
        }
        ConsoleHelper.pause();
    }

    private static void deleteUser() {
        ConsoleHelper.printHeader("DELETE USER");
        int userId = ConsoleHelper.readInt("Enter User ID to delete: ");

        User user = libraryService.getUserDAO().getUserById(userId);
        if (user == null) {
            System.out.println("User not found.");
            ConsoleHelper.pause();
            return;
        }

        if (user.getCurrentBorrowings() > 0) {
            System.out.println("Cannot delete user. They have " + user.getCurrentBorrowings() + " active borrowings.");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("User to delete:");
        System.out.println(user.toDetailedString());

        if (ConsoleHelper.confirm("Are you sure you want to delete this user?")) {
            if (libraryService.getUserDAO().deleteUser(userId)) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("Failed to delete user.");
            }
        }
        ConsoleHelper.pause();
    }

    private static void changeUserStatus() {
        ConsoleHelper.printHeader("CHANGE USER STATUS");
        int userId = ConsoleHelper.readInt("Enter User ID: ");

        User user = libraryService.getUserDAO().getUserById(userId);
        if (user == null) {
            System.out.println("User not found.");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("Current status: " + user.getStatus());
        System.out.println("Active borrowings: " + user.getCurrentBorrowings());

        System.out.println("\n1. Activate user");
        System.out.println("2. Deactivate user");
        System.out.println("3. Cancel");

        int choice = ConsoleHelper.readInt("Select option (1-3): ");

        switch (choice) {
            case 1:
                if (libraryService.getUserDAO().updateUserStatus(userId, "ACTIVE")) {
                    System.out.println("User activated successfully!");
                } else {
                    System.out.println("Failed to activate user.");
                }
                break;
            case 2:
                if (user.getCurrentBorrowings() > 0) {
                    System.out.println("Cannot deactivate user with active borrowings.");
                } else {
                    if (libraryService.getUserDAO().updateUserStatus(userId, "INACTIVE")) {
                        System.out.println("User deactivated successfully!");
                    } else {
                        System.out.println("Failed to deactivate user.");
                    }
                }
                break;
            case 3:
                System.out.println("Operation cancelled.");
                break;
            default:
                System.out.println("Invalid option.");
        }
        ConsoleHelper.pause();
    }

    private static void viewActiveUsers() {
        ConsoleHelper.printHeader("ACTIVE USERS");
        List<User> users = libraryService.getUserDAO().getActiveUsers();
        ConsoleHelper.printTable(users, "Active Users");
        ConsoleHelper.pause();
    }

    private static void searchUsers() {
        ConsoleHelper.printHeader("SEARCH USERS");
        String keyword = ConsoleHelper.readString("Enter search keyword: ");
        List<User> users = libraryService.getUserDAO().searchUsers(keyword);
        ConsoleHelper.printTable(users, "Search Results");
        ConsoleHelper.pause();
    }

    private static void manageBorrowings() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.printHeader("MANAGE BORROWINGS");

            String[] menu = {
                    "Borrow a Book",
                    "Return a Book",
                    "View Active Borrowings",
                    "View Overdue Books",
                    "Extend Borrowing Period",
                    "Pay Fine",
                    "View Borrowing History",
                    "Get Book Recommendations",
                    "Back to Main Menu"
            };

            ConsoleHelper.printMenu(menu);
            int choice = ConsoleHelper.readInt("Select option (1-9): ");

            switch (choice) {
                case 1:
                    borrowBook();
                    break;
                case 2:
                    returnBook();
                    break;
                case 3:
                    viewActiveBorrowings();
                    break;
                case 4:
                    viewOverdueBooks();
                    break;
                case 5:
                    extendBorrowing();
                    break;
                case 6:
                    payFine();
                    break;
                case 7:
                    viewBorrowingHistory();
                    break;
                case 8:
                    getBookRecommendations();
                    break;
                case 9:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    ConsoleHelper.pause();
            }
        }
    }

    private static void borrowBook() {
        ConsoleHelper.printHeader("BORROW A BOOK");

        System.out.println("Available Books:");
        List<Book> availableBooks = libraryService.getBookDAO().getAvailableBooks();
        if (availableBooks.isEmpty()) {
            System.out.println("No books available for borrowing.");
            ConsoleHelper.pause();
            return;
        }

        ConsoleHelper.printTable(availableBooks, "Available Books");

        int userId = ConsoleHelper.readInt("\nEnter User ID: ");
        int bookId = ConsoleHelper.readInt("Enter Book ID to borrow: ");

        User user = libraryService.getUserDAO().getUserById(userId);
        if (user == null) {
            System.out.println("User not found.");
            ConsoleHelper.pause();
            return;
        }

        Book book = libraryService.getBookDAO().getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("\nChecking borrowing eligibility...");

        int borrowDays = ConsoleHelper.readInt(
                "How many days to borrow? (Enter 0 for default " +
                        LibraryService.getDefaultBorrowDays() + " days): ",
                0, 30
        );

        Borrowing borrowing = libraryService.borrowBook(userId, bookId, borrowDays);
        if (borrowing != null) {
            System.out.println("\nBorrowing successful!");
            System.out.println("Borrowing ID: " + borrowing.getBorrowingId());
        }
        ConsoleHelper.pause();
    }

    private static void returnBook() {
        ConsoleHelper.printHeader("RETURN A BOOK");

        List<Borrowing> activeBorrowings = libraryService.getBorrowingDAO().getAllActiveBorrowings();
        if (activeBorrowings.isEmpty()) {
            System.out.println("No active borrowings.");
            ConsoleHelper.pause();
            return;
        }

        ConsoleHelper.printTable(activeBorrowings, "Active Borrowings");

        int borrowingId = ConsoleHelper.readInt("\nEnter Borrowing ID to return: ");

        double bookRating = -1;
        if (ConsoleHelper.confirm("Would you like to rate the book?")) {
            bookRating = ConsoleHelper.readDouble("Rating (0-5): ", 0, 5);
        }

        if (ConsoleHelper.confirm("Confirm return?")) {
            boolean success = libraryService.returnBook(borrowingId, bookRating);
            if (success) {
                System.out.println("Return processed successfully!");
            }
        }
        ConsoleHelper.pause();
    }

    private static void viewActiveBorrowings() {
        ConsoleHelper.printHeader("ACTIVE BORROWINGS");
        List<Borrowing> borrowings = libraryService.getBorrowingDAO().getAllActiveBorrowings();
        ConsoleHelper.printTable(borrowings, "Active Borrowings");
        ConsoleHelper.pause();
    }

    private static void viewOverdueBooks() {
        ConsoleHelper.printHeader("OVERDUE BOOKS");
        List<Borrowing> borrowings = libraryService.getBorrowingDAO().getOverdueBorrowings();

        if (borrowings.isEmpty()) {
            System.out.println("No overdue books!");
        } else {
            ConsoleHelper.printTable(borrowings, "Overdue Books");

            double totalFines = borrowings.stream()
                    .mapToDouble(Borrowing::getFineAmount)
                    .sum();
            System.out.printf("Total fines owed: $%.2f%n", totalFines);
        }
        ConsoleHelper.pause();
    }

    private static void extendBorrowing() {
        ConsoleHelper.printHeader("EXTEND BORROWING PERIOD");

        List<Borrowing> activeBorrowings = libraryService.getBorrowingDAO().getAllActiveBorrowings();
        if (activeBorrowings.isEmpty()) {
            System.out.println("No active borrowings.");
            ConsoleHelper.pause();
            return;
        }

        ConsoleHelper.printTable(activeBorrowings, "Active Borrowings");

        int borrowingId = ConsoleHelper.readInt("\nEnter Borrowing ID to extend: ");
        int additionalDays = ConsoleHelper.readInt(
                "How many additional days? (Max " + LibraryService.getMaxExtensionDays() + "): ",
                1, LibraryService.getMaxExtensionDays()
        );

        if (ConsoleHelper.confirm("Extend borrowing by " + additionalDays + " days?")) {
            libraryService.extendBorrowing(borrowingId, additionalDays);
        }
        ConsoleHelper.pause();
    }

    private static void payFine() {
        ConsoleHelper.printHeader("PAY FINE");

        List<Borrowing> overdueBorrowings = libraryService.getBorrowingDAO().getOverdueBorrowings();
        List<Borrowing> fines = overdueBorrowings.stream()
                .filter(b -> b.getFineAmount() > 0 && !b.isFinePaid())
                .toList();

        if (fines.isEmpty()) {
            System.out.println("No unpaid fines.");
            ConsoleHelper.pause();
            return;
        }

        ConsoleHelper.printTable(fines, "Unpaid Fines");

        int borrowingId = ConsoleHelper.readInt("\nEnter Borrowing ID to pay fine: ");
        Borrowing borrowing = libraryService.getBorrowingDAO().getBorrowingById(borrowingId);

        if (borrowing == null || borrowing.getFineAmount() <= 0) {
            System.out.println("No fine found for this borrowing.");
            ConsoleHelper.pause();
            return;
        }

        System.out.printf("Fine amount: $%.2f%n", borrowing.getFineAmount());
        double amount = ConsoleHelper.readDouble("Payment amount: $", 0.01, borrowing.getFineAmount());

        if (ConsoleHelper.confirm("Pay $" + amount + "?")) {
            libraryService.payFine(borrowingId, amount);
        }
        ConsoleHelper.pause();
    }

    private static void viewBorrowingHistory() {
        ConsoleHelper.printHeader("BORROWING HISTORY");

        System.out.println("1. View history by user");
        System.out.println("2. View history by book");
        System.out.println("3. View all history");
        System.out.println("4. Cancel");

        int choice = ConsoleHelper.readInt("Select option (1-4): ");

        switch (choice) {
            case 1:
                int userId = ConsoleHelper.readInt("Enter User ID: ");
                List<Borrowing> userHistory = libraryService.getBorrowingDAO().getBorrowingHistoryByUser(userId);
                ConsoleHelper.printTable(userHistory, "Borrowing History for User #" + userId);
                break;
            case 2:
                int bookId = ConsoleHelper.readInt("Enter Book ID: ");
                List<Borrowing> bookHistory = libraryService.getBorrowingDAO().getBorrowingHistoryByBook(bookId);
                ConsoleHelper.printTable(bookHistory, "Borrowing History for Book #" + bookId);
                break;
            case 3:
                List<Borrowing> allHistory = libraryService.getBorrowingDAO().getBorrowingHistoryByUser(1);
                ConsoleHelper.printTable(allHistory, "Recent Borrowing History");
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
        ConsoleHelper.pause();
    }

    private static void getBookRecommendations() {
        ConsoleHelper.printHeader("BOOK RECOMMENDATIONS");
        int userId = ConsoleHelper.readInt("Enter User ID: ");

        List<Book> recommendations = libraryService.getBookRecommendations(userId);
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available.");
        } else {
            System.out.println("\nRecommended Books for You:");
            for (int i = 0; i < recommendations.size(); i++) {
                Book book = recommendations.get(i);
                System.out.printf("%d. %s by %s (Rating: %.1f )%n",
                        i + 1, book.getTitle(), book.getAuthor(), book.getRating());
            }
        }
        ConsoleHelper.pause();
    }

    private static void search() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.printHeader("SEARCH");

            String[] menu = {
                    "Search Books",
                    "Search Users",
                    "Advanced Book Search",
                    "Back to Main Menu"
            };

            ConsoleHelper.printMenu(menu);
            int choice = ConsoleHelper.readInt("Select option (1-4): ");

            switch (choice) {
                case 1:
                    searchBooks();
                    break;
                case 2:
                    searchUsersMenu();
                    break;
                case 3:
                    advancedBookSearch();
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    ConsoleHelper.pause();
            }
        }
    }

    private static void searchBooks() {
        ConsoleHelper.printHeader("SEARCH BOOKS");
        String keyword = ConsoleHelper.readString("Enter search keyword: ");
        List<Book> books = libraryService.getBookDAO().searchBooks(keyword);
        ConsoleHelper.printTable(books, "Search Results for: \"" + keyword + "\"");
        ConsoleHelper.pause();
    }

    private static void searchUsersMenu() {
        ConsoleHelper.printHeader("SEARCH USERS");
        String keyword = ConsoleHelper.readString("Enter search keyword: ");
        List<User> users = libraryService.getUserDAO().searchUsers(keyword);
        ConsoleHelper.printTable(users, "Search Results for: \"" + keyword + "\"");
        ConsoleHelper.pause();
    }

    private static void advancedBookSearch() {
        ConsoleHelper.printHeader("ADVANCED BOOK SEARCH");

        System.out.println("Search by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Genre");
        System.out.println("4. ISBN");
        System.out.println("5. Multiple criteria");

        int choice = ConsoleHelper.readInt("Select option (1-5): ");
        String keyword = ConsoleHelper.readString("Enter search term: ");

        List<Book> results;
        switch (choice) {
            case 1:
                results = libraryService.getBookDAO().searchBooks(keyword);
                ConsoleHelper.printTable(results, "Books with title containing: \"" + keyword + "\"");
                break;
            case 2:
                results = libraryService.getBookDAO().getBooksByGenre(keyword);
                ConsoleHelper.printTable(results, "Books by author: \"" + keyword + "\"");
                break;
            case 3:
                results = libraryService.getBookDAO().getBooksByGenre(keyword);
                ConsoleHelper.printTable(results, "Books in genre: \"" + keyword + "\"");
                break;
            case 4:
                results = libraryService.getBookDAO().searchBooks(keyword);
                results = results.stream()
                        .filter(b -> b.getIsbn() != null && b.getIsbn().contains(keyword))
                        .toList();
                ConsoleHelper.printTable(results, "Books with ISBN: \"" + keyword + "\"");
                break;
            case 5:
                results = libraryService.getBookDAO().searchBooks(keyword);
                ConsoleHelper.printTable(results, "Books matching: \"" + keyword + "\"");
                break;
            default:
                System.out.println("Invalid option.");
        }
        ConsoleHelper.pause();
    }

    private static void reportsAndAnalytics() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.printHeader("REPORTS & ANALYTICS");

            String[] menu = {
                    "Monthly Report",
                    "User Activity Report",
                    "Library Statistics",
                    "Popular Books Report",
                    "Overdue Books Report",
                    "Financial Report",
                    "Back to Main Menu"
            };

            ConsoleHelper.printMenu(menu);
            int choice = ConsoleHelper.readInt("Select option (1-7): ");

            switch (choice) {
                case 1:
                    generateMonthlyReport();
                    break;
                case 2:
                    generateUserActivityReport();
                    break;
                case 3:
                    showLibraryStatistics();
                    break;
                case 4:
                    showPopularBooksReport();
                    break;
                case 5:
                    showOverdueBooksReport();
                    break;
                case 6:
                    showFinancialReport();
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    ConsoleHelper.pause();
            }
        }
    }

    private static void generateMonthlyReport() {
        ConsoleHelper.printHeader("MONTHLY REPORT");
        int year = ConsoleHelper.readInt("Enter year: ", 2000, LocalDate.now().getYear());
        int month = ConsoleHelper.readInt("Enter month (1-12): ", 1, 12);
        libraryService.generateMonthlyReport(year, month);
        ConsoleHelper.pause();
    }

    private static void generateUserActivityReport() {
        ConsoleHelper.printHeader(" USER ACTIVITY REPORT");
        int userId = ConsoleHelper.readInt("Enter User ID: ");
        libraryService.generateUserActivityReport(userId);
        ConsoleHelper.pause();
    }

    private static void showLibraryStatistics() {
        ConsoleHelper.printHeader("LIBRARY STATISTICS");

        List<Book> allBooks = libraryService.getBookDAO().getAllBooks();
        List<User> allUsers = libraryService.getUserDAO().getAllUsers();
        List<Borrowing> activeBorrowings = libraryService.getBorrowingDAO().getAllActiveBorrowings();
        List<Borrowing> overdueBorrowings = libraryService.getBorrowingDAO().getOverdueBorrowings();

        int totalBooks = allBooks.size();
        int totalUsers = allUsers.size();
        int activeUsers = libraryService.getUserDAO().getActiveUsers().size();
        int activeBorrowingsCount = activeBorrowings.size();
        int overdueCount = overdueBorrowings.size();

        int totalBorrowings = allUsers.stream()
                .mapToInt(User::getTotalBorrowed)
                .sum();

        double avgRating = allBooks.stream()
                .mapToDouble(Book::getRating)
                .average()
                .orElse(0);

        double totalFines = overdueBorrowings.stream()
                .mapToDouble(Borrowing::getFineAmount)
                .sum();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("LIBRARY STATISTICS");
        System.out.println("=".repeat(80));

        System.out.println("\nBOOK STATISTICS:");
        System.out.printf("  Total Books:           %d%n", totalBooks);
        System.out.printf("  Available Books:       %d%n", libraryService.getBookDAO().getAvailableBooks().size());
        System.out.printf("  Average Book Rating:   %.2f %n", avgRating);
        System.out.printf("  Total Times Borrowed:  %d%n", totalBorrowings);

        System.out.println("\nUSER STATISTICS:");
        System.out.printf("  Total Users:           %d%n", totalUsers);
        System.out.printf("  Active Users:          %d%n", activeUsers);
        System.out.printf("  Inactive Users:        %d%n", totalUsers - activeUsers);

        System.out.println("\nBORROWING STATISTICS:");
        System.out.printf("  Active Borrowings:     %d%n", activeBorrowingsCount);
        System.out.printf("  Overdue Books:         %d%n", overdueCount);
        System.out.printf("  Total Fines Owed:      $%.2f%n", totalFines);
        System.out.printf("  Borrowing Limit:       %d books/user%n", LibraryService.getMaxBorrowingsPerUser());

        System.out.println("\nFINANCIAL STATISTICS:");
        System.out.printf("  Daily Fine Rate:       $%.2f/day%n", LibraryService.getDailyFineRate());
        System.out.printf("  Max Extension Days:    %d days%n", LibraryService.getMaxExtensionDays());

        System.out.println("=".repeat(80));
        System.out.println("\n TOP 5 POPULAR BOOKS:");
        List<Book> popularBooks = libraryService.getBookDAO().getPopularBooks(5);
        for (int i = 0; i < popularBooks.size(); i++) {
            Book book = popularBooks.get(i);
            System.out.printf("%d. %s - Borrowed %d times%n",
                    i + 1, book.getTitle(), book.getTimesBorrowed());
        }

        ConsoleHelper.pause();
    }

    private static void showPopularBooksReport() {
        ConsoleHelper.printHeader("POPULAR BOOKS REPORT");
        int limit = ConsoleHelper.readInt("How many books to show? ", 1, 20);
        List<Book> popularBooks = libraryService.getBookDAO().getPopularBooks(limit);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("MOST POPULAR BOOKS");
        System.out.println("=".repeat(80));

        for (int i = 0; i < popularBooks.size(); i++) {
            Book book = popularBooks.get(i);
            System.out.printf("\n%d. %s%n", i + 1, book.getTitle());
            System.out.printf("   Author: %s%n", book.getAuthor());
            System.out.printf("   Times Borrowed: %d | Rating: %.2f  | Available: %d/%d%n",
                    book.getTimesBorrowed(), book.getRating(),
                    book.getAvailableCopies(), book.getTotalCopies());
        }

        System.out.println("=".repeat(80));
        ConsoleHelper.pause();
    }

    private static void showOverdueBooksReport() {
        ConsoleHelper.printHeader("OVERDUE BOOKS REPORT");
        List<Borrowing> overdue = libraryService.getBorrowingDAO().getOverdueBorrowings();

        if (overdue.isEmpty()) {
            System.out.println("No overdue books!");
            ConsoleHelper.pause();
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("OVERDUE BOOKS REPORT");
        System.out.println("=".repeat(80));

        double totalFines = 0;
        for (Borrowing borrowing : overdue) {
            System.out.printf("\nBook: %s%n", borrowing.getBookTitle());
            System.out.printf("User: %s (%s)%n", borrowing.getUserName(), borrowing.getUserEmail());
            System.out.printf("Borrowed: %s | Due: %s | Days Overdue: %d%n",
                    borrowing.getBorrowedDate(), borrowing.getDueDate(), borrowing.getDaysOverdue());
            System.out.printf("Fine: $%.2f | Paid: %s%n",
                    borrowing.getFineAmount(), borrowing.isFinePaid() ? "Yes" : "No");
            totalFines += borrowing.getFineAmount();
        }

        System.out.println("\n" + "-".repeat(80));
        System.out.printf("Total Overdue Books: %d%n", overdue.size());
        System.out.printf("Total Fines Owed: $%.2f%n", totalFines);
        System.out.printf("Daily Fine Rate: $%.2f/day%n", LibraryService.getDailyFineRate());
        System.out.println("=".repeat(80));

        ConsoleHelper.pause();
    }

    private static void showFinancialReport() {
        ConsoleHelper.printHeader("FINANCIAL REPORT");

        List<Borrowing> overdue = libraryService.getBorrowingDAO().getOverdueBorrowings();
        double totalFinesOwed = overdue.stream()
                .mapToDouble(Borrowing::getFineAmount)
                .sum();

        double finesPaid = overdue.stream()
                .filter(Borrowing::isFinePaid)
                .mapToDouble(Borrowing::getFineAmount)
                .sum();

        double finesUnpaid = totalFinesOwed - finesPaid;

        System.out.println("\n" + "=".repeat(80));
        System.out.println("FINANCIAL REPORT");
        System.out.println("=".repeat(80));

        System.out.println("\nFINE STATISTICS:");
        System.out.printf("  Total Fines Owed:      $%.2f%n", totalFinesOwed);
        System.out.printf("  Fines Paid:            $%.2f%n", finesPaid);
        System.out.printf("  Fines Unpaid:          $%.2f%n", finesUnpaid);
        System.out.printf("  Collection Rate:       %.1f%%%n",
                totalFinesOwed > 0 ? (finesPaid / totalFinesOwed * 100) : 0);

        System.out.println("\nOVERDUE STATISTICS:");
        System.out.printf("  Total Overdue Books:   %d%n", overdue.size());
        System.out.printf("  Average Overdue Days:  %.1f days%n",
                overdue.stream()
                        .mapToLong(Borrowing::getDaysOverdue)
                        .average()
                        .orElse(0));
        System.out.printf("  Daily Fine Rate:       $%.2f/day%n", LibraryService.getDailyFineRate());

        System.out.println("\nUSER FINANCES:");
        List<User> usersWithFines = libraryService.getUserDAO().getAllUsers().stream()
                .filter(u -> u.getTotalFines() > 0)
                .toList();

        System.out.printf("  Users with Fines:      %d%n", usersWithFines.size());
        if (!usersWithFines.isEmpty()) {
            System.out.println("  Top 5 Users with Fines:");
            usersWithFines.stream()
                    .sorted((u1, u2) -> Double.compare(u2.getTotalFines(), u1.getTotalFines()))
                    .limit(5)
                    .forEach(u -> System.out.printf("    - %s %s: $%.2f%n",
                            u.getFirstName(), u.getLastName(), u.getTotalFines()));
        }

        System.out.println("=".repeat(80));

        if (finesUnpaid > 0) {
            System.out.println("\nRECOMMENDATIONS:");
            System.out.println("  1. Send reminder emails to users with unpaid fines");
            System.out.println("  2. Implement automatic fine calculation");
            System.out.println("  3. Consider a grace period for first-time offenders");
            System.out.println("  4. Offer payment plans for large fines");
        }

        ConsoleHelper.pause();
    }

    private static void systemTools() {
        boolean back = false;
        while (!back) {
            ConsoleHelper.printHeader("SYSTEM TOOLS");

            String[] menu = {
                    "Update Overdue Statuses",
                    "Database Connection Test",
                    "View Business Rules",
                    "System Information",
                    "Back to Main Menu"
            };

            ConsoleHelper.printMenu(menu);
            int choice = ConsoleHelper.readInt("Select option (1-5): ");

            switch (choice) {
                case 1:
                    libraryService.updateOverdueStatuses();
                    ConsoleHelper.pause();
                    break;
                case 2:
                    DatabaseConnection.testConnection();
                    ConsoleHelper.pause();
                    break;
                case 3:
                    viewBusinessRules();
                    break;
                case 4:
                    showSystemInfo();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    ConsoleHelper.pause();
            }
        }
    }

    private static void viewBusinessRules() {
        ConsoleHelper.printHeader("BUSINESS RULES");

        System.out.println("\n" + "=".repeat(80));
        System.out.println("LIBRARY BUSINESS RULES");
        System.out.println("=".repeat(80));

        System.out.println("\nBORROWING RULES:");
        System.out.printf("  1. Maximum books per user: %d%n", LibraryService.getMaxBorrowingsPerUser());
        System.out.printf("  2. Default borrowing period: %d days%n", LibraryService.getDefaultBorrowDays());
        System.out.printf("  3. Maximum extension: %d days total%n", LibraryService.getMaxExtensionDays());
        System.out.printf("  4. Minimum user age: %d years%n", LibraryService.getMinUserAge());

        System.out.println("\nFINE RULES:");
        System.out.printf("  1. Daily fine rate: $%.2f/day%n", LibraryService.getDailyFineRate());
        System.out.println("  2. Fines calculated from due date");
        System.out.println("  3. Users with unpaid fines can still borrow (with warning)");

        System.out.println("\nUSER RULES:");
        System.out.println("  1. Users must be active to borrow books");
        System.out.println("  2. Users cannot borrow books they already have");
        System.out.println("  3. Users with active borrowings cannot be deactivated");

        System.out.println("\nSYSTEM RULES:");
        System.out.println("  1. Books with active borrowings cannot be deleted");
        System.out.println("  2. Available copies automatically updated on borrow/return");
        System.out.println("  3. Book ratings updated when books are returned");
        System.out.println("  4. Overdue statuses automatically updated daily");

        System.out.println("=".repeat(80));
        ConsoleHelper.pause();
    }

    private static void showSystemInfo() {
        ConsoleHelper.printHeader("SYSTEM INFORMATION");

        System.out.println("\n" + "=".repeat(80));
        System.out.println("LIBRARY MANAGEMENT SYSTEM v3.0");
        System.out.println("=".repeat(80));

        System.out.println("\nDATABASE INFORMATION:");
        System.out.println("  Database: PostgreSQL");
        System.out.println("  Tables: Users, Books, Borrowings");
        System.out.println("  Connection: JDBC");

        System.out.println("\nSYSTEM FEATURES:");
        System.out.println("  ✓ Complete CRUD operations for all entities");
        System.out.println("  ✓ Complex business logic and validation");
        System.out.println("  ✓ Transaction management");
        System.out.println("  ✓ Comprehensive reporting");
        System.out.println("  ✓ Book recommendations");
        System.out.println("  ✓ Fine calculation and management");
        System.out.println("  ✓ User activity tracking");

        System.out.println("\nTECHNICAL STACK:");
        System.out.println("  Language: Java 11+");
        System.out.println("  Database: PostgreSQL");
        System.out.println("  Architecture: Layered (DAO, Service, Model)");
        System.out.println("  Patterns: MVC, Repository, Service");

        System.out.println("\nPERFORMANCE FEATURES:");
        System.out.println("  ✓ Database indexes for optimization");
        System.out.println("  ✓ Connection pooling");
        System.out.println("  ✓ Transaction rollback on errors");
        System.out.println("  ✓ Input validation and sanitization");

        System.out.println("\nSECURITY FEATURES:");
        System.out.println("  ✓ Input validation");
        System.out.println("  ✓ SQL injection prevention (PreparedStatements)");
        System.out.println("  ✓ Business rule enforcement");
        System.out.println("  ✓ Data integrity constraints");

        System.out.println("=".repeat(80));
        ConsoleHelper.pause();
    }
}