package dao;

import models.Book;
import factories.ValidatorFactory;
import validators.BookValidator;
import util.ValidationResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private final ValidatorFactory validatorFactory;

    public BookRepository() {
        this.validatorFactory = new ValidatorFactory();
    }

    public BookRepository(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_id, name, author, description, available_copies, total_copies, category FROM book ORDER BY book_id";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getInt("available_copies"),
                        rs.getInt("total_copies"),
                        rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    public Book findById(int bookId) {
        String sql = "SELECT book_id, name, author, description, available_copies, total_copies, category FROM book WHERE book_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getInt("book_id"),
                            rs.getString("name"),
                            rs.getString("author"),
                            rs.getString("description"),
                            rs.getInt("available_copies"),
                            rs.getInt("total_copies"),
                            rs.getString("category")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding book: " + e.getMessage());
        }
        return null;
    }

    public boolean updateAvailableCopies(int bookId, int change) {
        String sql = "UPDATE book SET available_copies = available_copies + ? WHERE book_id = ? AND available_copies + ? >= 0";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, change);
            stmt.setInt(2, bookId);
            stmt.setInt(3, change);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book copies: " + e.getMessage());
            return false;
        }
    }

    public boolean createBook(String title, String author, String description, int totalCopies, String category) {
        // НОВАЯ ВАЛИДАЦИЯ
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
            System.err.println("Book validation failed:");
            for (String error : result.getErrors()) {
                System.err.println("  - " + error);
            }
            return false;
        }

        String sql = "INSERT INTO book (name, author, description, total_copies, available_copies, category) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, description);
            stmt.setInt(4, totalCopies);
            stmt.setInt(5, totalCopies); // При создании available_copies = total_copies
            stmt.setString(6, category);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating book: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBook(int bookId, String title, String author, String description,
                              int availableCopies, int totalCopies, String category) {
        // НОВАЯ ВАЛИДАЦИЯ
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

        ValidationResult categoryResult = validator.validateField("category", category);
        if (categoryResult.hasErrors()) {
            result.getErrors().addAll(categoryResult.getErrors());
        }

        // Валидация availableCopies
        ValidationResult availableCopiesResult = validator.validateCopies(availableCopies);
        if (availableCopiesResult.hasErrors()) {
            result.getErrors().addAll(availableCopiesResult.getErrors());
        }

        // Валидация totalCopies
        ValidationResult totalCopiesResult = validator.validateCopies(totalCopies);
        if (totalCopiesResult.hasErrors()) {
            result.getErrors().addAll(totalCopiesResult.getErrors());
        }

        // Дополнительная проверка: availableCopies не может быть больше totalCopies
        if (availableCopies > totalCopies) {
            result.addError("Available copies (" + availableCopies + ") cannot exceed total copies (" + totalCopies + ")");
        }

        if (result.hasErrors()) {
            System.err.println("Book validation failed:");
            for (String error : result.getErrors()) {
                System.err.println("  - " + error);
            }
            return false;
        }

        String sql = "UPDATE book SET name = ?, author = ?, description = ?, " +
                "available_copies = ?, total_copies = ?, category = ? WHERE book_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, description);
            stmt.setInt(4, availableCopies);
            stmt.setInt(5, totalCopies);
            stmt.setString(6, category);
            stmt.setInt(7, bookId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        // НОВАЯ ВАЛИДАЦИЯ
        BookValidator validator = validatorFactory.getBookValidator();
        ValidationResult bookIdResult = validator.validateBookId(bookId);

        if (bookIdResult.hasErrors()) {
            System.err.println("Book validation failed:");
            for (String error : bookIdResult.getErrors()) {
                System.err.println("  - " + error);
            }
            return false;
        }

        String sql = "DELETE FROM book WHERE book_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }
}
