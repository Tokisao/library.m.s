package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LibraryService {
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private BorrowingDAO borrowingDAO;

    private static final int MAX_BORROWINGS_PER_USER = 5;
    private static final int DEFAULT_BORROW_DAYS = 14;
    private static final double DAILY_FINE_RATE = 0.50;
    private static final int MAX_EXTENSION_DAYS = 7;
    private static final int MIN_USER_AGE = 16;

    public LibraryService() {
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
        this.borrowingDAO = new BorrowingDAO();
    }

    public Borrowing borrowBook(int userId, int bookId, int borrowDays) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("User not found!");
            return null;
        }

        if (!user.isActive()) {
            System.out.println("User account is not active!");
            return null;
        }

        if (user.getAge() < MIN_USER_AGE) {
            System.out.println("User must be at least " + MIN_USER_AGE + " years old to borrow books!");
            return null;
        }

        if (!user.canBorrowMore(MAX_BORROWINGS_PER_USER)) {
            System.out.println("User has reached the maximum borrowing limit (" + MAX_BORROWINGS_PER_USER + " books)!");
            return null;
        }

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found!");
            return null;
        }

        if (!book.isAvailable()) {
            System.out.println("Book is not available for borrowing!");
            return null;
        }

        if (borrowingDAO.isBookBorrowedByUser(userId, bookId)) {
            System.out.println("User has already borrowed this book!");
            return null;
        }

        if (user.getTotalFines() > 0) {
            System.out.printf("User has unpaid fines: $%.2f%n", user.getTotalFines());
            System.out.println("User can still borrow, but fines must be paid soon.");
        }

        int actualBorrowDays = (borrowDays > 0) ? borrowDays : DEFAULT_BORROW_DAYS;

        Borrowing borrowing = new Borrowing(userId, bookId, actualBorrowDays);

        Connection conn = null;
        try {
            conn = com.library.DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (!borrowingDAO.createBorrowing(borrowing)) {
                throw new SQLException("Failed to create borrowing record");
            }

            book.borrowCopy();
            if (!bookDAO.updateBook(book)) {
                throw new SQLException("Failed to update book availability");
            }

            user.incrementBorrowings();
            if (!userDAO.updateUser(user)) {
                throw new SQLException("Failed to update user borrowings count");
            }

            if (!bookDAO.updateBook(book)) {
                throw new SQLException("Failed to update book borrow count");
            }

            conn.commit();

            System.out.println("Book borrowed successfully!");
            System.out.println("Due date: " + borrowing.getDueDate());
            System.out.println("Book: " + book.getTitle());
            System.out.println("User: " + user.getFirstName() + " " + user.getLastName());

            return borrowing;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            System.err.println("Error borrowing book: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
    }

    public boolean returnBook(int borrowingId, double bookRating) {
        Borrowing borrowing = borrowingDAO.getBorrowingById(borrowingId);
        if (borrowing == null) {
            System.out.println("Borrowing record not found!");
            return false;
        }

        if ("RETURNED".equals(borrowing.getStatus())) {
            System.out.println("Book has already been returned!");
            return false;
        }

        Book book = bookDAO.getBookById(borrowing.getBookId());
        User user = userDAO.getUserById(borrowing.getUserId());

        if (book == null || user == null) {
            System.out.println("Book or user information not found!");
            return false;
        }

        borrowing.returnBook();

        if (borrowing.isOverdue()) {
            long daysOverdue = borrowing.getDaysOverdue();
            double fineAmount = daysOverdue * DAILY_FINE_RATE;
            borrowing.setFineAmount(fineAmount);
            borrowing.setStatus("OVERDUE");

            System.out.printf("Book returned %d days late! Fine: $%.2f%n", daysOverdue, fineAmount);

            user.addFine(fineAmount);
        }

        if (bookRating >= 0 && bookRating <= 5) {
            book.updateRating(bookRating);
        }

        Connection conn = null;
        try {
            conn = com.library.DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (!borrowingDAO.updateBorrowing(borrowing)) {
                throw new SQLException("Failed to update borrowing record");
            }

            book.returnCopy();
            if (!bookDAO.updateBook(book)) {
                throw new SQLException("Failed to update book availability");
            }

            user.decrementBorrowings();
            if (!userDAO.updateUser(user)) {
                throw new SQLException("Failed to update user borrowings count");
            }

            if (bookRating >= 0 && bookRating <= 5) {
                if (!bookDAO.updateBookRating(book.getBookId(), book.getRating())) {
                    System.out.println("Could not update book rating, but book was returned successfully");
                }
            }

            if (borrowing.getFineAmount() > 0) {
                if (!userDAO.updateUserFines(user.getUserId(), borrowing.getFineAmount())) {
                    System.out.println("Could not update user fines, but book was returned successfully");
                }
            }

            conn.commit();

            System.out.println("Book returned successfully!");
            System.out.println("Book: " + book.getTitle());
            System.out.println("User: " + user.getFirstName() + " " + user.getLastName());
            if (borrowing.getFineAmount() > 0) {
                System.out.printf("Fine to pay: $%.2f%n", borrowing.getFineAmount());
            }

            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
    }

    public boolean extendBorrowing(int borrowingId, int additionalDays) {
        if (additionalDays <= 0 || additionalDays > MAX_EXTENSION_DAYS) {
            System.out.println("Extension must be between 1 and " + MAX_EXTENSION_DAYS + " days");
            return false;
        }

        Borrowing borrowing = borrowingDAO.getBorrowingById(borrowingId);
        if (borrowing == null) {
            System.out.println("Borrowing record not found!");
            return false;
        }

        if ("RETURNED".equals(borrowing.getStatus())) {
            System.out.println("Cannot extend a returned book!");
            return false;
        }

        if (borrowing.isOverdue()) {
            System.out.println("Cannot extend an overdue book! Please return it first.");
            return false;
        }

        if (borrowing.getDaysExtended() >= MAX_EXTENSION_DAYS) {
            System.out.println("Maximum extension limit reached (" + MAX_EXTENSION_DAYS + " days)!");
            return false;
        }

        if (borrowing.getDaysExtended() + additionalDays > MAX_EXTENSION_DAYS) {
            System.out.println("Cannot extend more than " + MAX_EXTENSION_DAYS + " days total!");
            return false;
        }

        if (borrowingDAO.extendBorrowing(borrowingId, additionalDays)) {
            System.out.println("Borrowing extended successfully!");
            System.out.println("New due date: " + borrowing.getDueDate().plusDays(additionalDays));
            System.out.println("Total extension: " + (borrowing.getDaysExtended() + additionalDays) + " days");
            return true;
        } else {
            System.out.println("Failed to extend borrowing!");
            return false;
        }
    }

    public boolean payFine(int borrowingId, double amount) {
        Borrowing borrowing = borrowingDAO.getBorrowingById(borrowingId);
        if (borrowing == null) {
            System.out.println("Borrowing record not found!");
            return false;
        }

        if (borrowing.getFineAmount() <= 0) {
            System.out.println("No fines to pay for this borrowing!");
            return false;
        }

        if (amount <= 0 || amount > borrowing.getFineAmount()) {
            System.out.printf("Invalid payment amount! Fine is $%.2f%n", borrowing.getFineAmount());
            return false;
        }

        borrowing.setFinePaid(true);

        if (Math.abs(amount - borrowing.getFineAmount()) < 0.01) {
            if (borrowingDAO.updateFinePayment(borrowingId, true)) {
                System.out.printf("Fine of $%.2f paid successfully!%n", amount);

                User user = userDAO.getUserById(borrowing.getUserId());
                if (user != null) {
                    user.payFine(amount);
                    userDAO.updateUser(user);
                }

                return true;
            }
        } else {
            System.out.printf("Partial payment of $%.2f received. Remaining: $%.2f%n",
                    amount, borrowing.getFineAmount() - amount);
            return false;
        }

        return false;
    }

    public void updateOverdueStatuses() {
        List<Borrowing> overdueBorrowings = borrowingDAO.getOverdueBorrowings();

        int updated = 0;
        for (Borrowing borrowing : overdueBorrowings) {
            if ("BORROWED".equals(borrowing.getStatus())) {
                borrowing.setStatus("OVERDUE");
                borrowingDAO.updateBorrowing(borrowing);
                updated++;
            }
        }

        if (updated > 0) {
            System.out.println("Updated " + updated + " borrowings to OVERDUE status");
        }
    }

    public List<Book> getBookRecommendations(int userId) {
        List<Borrowing> userHistory = borrowingDAO.getBorrowingHistoryByUser(userId);

        if (userHistory.isEmpty()) {
            return bookDAO.getPopularBooks(5);
        }

        String favoriteGenre = determineFavoriteGenre(userHistory);

        return getUnreadBooksByGenre(userId, favoriteGenre, 5);
    }

    private String determineFavoriteGenre(List<Borrowing> borrowings) {
        java.util.Map<String, Integer> genreCount = new java.util.HashMap<>();

        for (Borrowing borrowing : borrowings) {
            Book book = bookDAO.getBookById(borrowing.getBookId());
            if (book != null) {
                String genre = book.getGenre();
                genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
            }
        }

        return genreCount.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("Fiction");
    }

    private List<Book> getUnreadBooksByGenre(int userId, String genre, int limit) {
        List<Book> genreBooks = bookDAO.getBooksByGenre(genre);

        List<Book> unreadBooks = new java.util.ArrayList<>();
        for (Book book : genreBooks) {
            if (!borrowingDAO.isBookBorrowedByUser(userId, book.getBookId())) {
                unreadBooks.add(book);
                if (unreadBooks.size() >= limit) {
                    break;
                }
            }
        }

        return unreadBooks;
    }

    public void generateMonthlyReport(int year, int month) {
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("MONTHLY REPORT - %d/%d%n", month, year);
        System.out.println("=".repeat(80));

        List<Book> allBooks = bookDAO.getAllBooks();
        List<Book> popularBooks = bookDAO.getPopularBooks(5);

        System.out.printf("\n Total Books in Library: %d%n", allBooks.size());
        System.out.printf("Most Popular Books this Month:%n");

        for (int i = 0; i < Math.min(popularBooks.size(), 5); i++) {
            Book book = popularBooks.get(i);
            System.out.printf("  %d. %s (%d borrows)%n",
                    i + 1, book.getTitle(), book.getTimesBorrowed());
        }

        List<User> allUsers = userDAO.getAllUsers();
        List<User> activeUsers = userDAO.getActiveUsers();

        System.out.printf("\n Total Users: %d%n", allUsers.size());
        System.out.printf("Active Users: %d%n", activeUsers.size());

        List<Borrowing> activeBorrowings = borrowingDAO.getAllActiveBorrowings();
        List<Borrowing> overdueBorrowings = borrowingDAO.getOverdueBorrowings();

        System.out.printf("\n Active Borrowings: %d%n", activeBorrowings.size());
        System.out.printf("Overdue Borrowings: %d%n", overdueBorrowings.size());

        double totalFines = overdueBorrowings.stream()
                .mapToDouble(Borrowing::getFineAmount)
                .sum();

        System.out.printf("Total Fines Owed: $%.2f%n", totalFines);

        System.out.println("=".repeat(80));
    }

    public void generateUserActivityReport(int userId) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        List<Borrowing> history = borrowingDAO.getBorrowingHistoryByUser(userId);
        List<Borrowing> active = borrowingDAO.getActiveBorrowingsByUser(userId);

        System.out.println("\n" + "=".repeat(80));
        System.out.printf("USER ACTIVITY REPORT: %s %s%n", user.getFirstName(), user.getLastName());
        System.out.println("=".repeat(80));

        System.out.println("\n User Statistics:");
        System.out.printf("  Total Books Borrowed: %d%n", user.getTotalBorrowed());
        System.out.printf("  Currently Borrowed: %d%n", user.getCurrentBorrowings());
        System.out.printf("  Total Fines: $%.2f%n", user.getTotalFines());
        System.out.printf("  Member Since: %s%n", user.getRegistrationDate());

        System.out.println("\n Currently Borrowed Books:");
        if (active.isEmpty()) {
            System.out.println("  No active borrowings");
        } else {
            for (Borrowing borrowing : active) {
                System.out.printf("  - %s (Due: %s, Status: %s)%n",
                        borrowing.getBookTitle(), borrowing.getDueDate(), borrowing.getStatus());
            }
        }

        System.out.println("\n Borrowing History (Last 10):");
        int count = Math.min(history.size(), 10);
        for (int i = 0; i < count; i++) {
            Borrowing borrowing = history.get(i);
            System.out.printf("  %d. %s (Borrowed: %s, Returned: %s)%n",
                    i + 1, borrowing.getBookTitle(),
                    borrowing.getBorrowedDate(),
                    borrowing.getReturnedDate() != null ? borrowing.getReturnedDate() : "Not returned");
        }

        System.out.println("=".repeat(80));
    }

    public BookDAO getBookDAO() { return bookDAO; }
    public UserDAO getUserDAO() { return userDAO; }
    public BorrowingDAO getBorrowingDAO() { return borrowingDAO; }

    public static int getMaxBorrowingsPerUser() { return MAX_BORROWINGS_PER_USER; }
    public static int getDefaultBorrowDays() { return DEFAULT_BORROW_DAYS; }
    public static double getDailyFineRate() { return DAILY_FINE_RATE; }
    public static int getMaxExtensionDays() { return MAX_EXTENSION_DAYS; }
    public static int getMinUserAge() { return MIN_USER_AGE; }
}