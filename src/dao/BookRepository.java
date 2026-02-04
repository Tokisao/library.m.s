package dao;

import models.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_id, name, author, description, available_copies, category FROM book ORDER BY book_id";

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
                        rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    public Book findById(int bookId) {
        String sql = "SELECT book_id, name, author, description, available_copies, category FROM book WHERE book_id = ?";

        try (Connection con = DriverManager.getConnection(
                DatabaseConnection.URL, DatabaseConnection.USERNAME, DatabaseConnection.PASSWORD);
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

        try (Connection con = DriverManager.getConnection(
                DatabaseConnection.URL, DatabaseConnection.USERNAME, DatabaseConnection.PASSWORD);
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
        String sql = "INSERT INTO book (name, author, description, total_copies, available_copies, category) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, description);
            stmt.setInt(4, totalCopies);
            stmt.setInt(5, totalCopies);
            stmt.setString(6, category);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating book: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBook(int bookId, String title, String author, String description, int totalCopies, String category) {
        String sql = "UPDATE book SET name = ?, author = ?, description = ?, " +
                "total_copies = ?, category = ? WHERE book_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, description);
            stmt.setInt(4, totalCopies);
            stmt.setString(5, category);
            stmt.setInt(6, bookId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
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

    public List<Book> searchBooks(String searchType, String searchTerm) {
        List<Book> books = new ArrayList<>();
        String sql;

        // Строим SQL запрос в зависимости от типа поиска
        switch (searchType.toLowerCase()) {
            case "title":
                sql = "SELECT book_id, name, author, description, available_copies, total_copies, category " +
                        "FROM book WHERE LOWER(name) LIKE LOWER(?) ORDER BY book_id";
                break;
            case "author":
                sql = "SELECT book_id, name, author, description, available_copies, total_copies, category " +
                        "FROM book WHERE LOWER(author) LIKE LOWER(?) ORDER BY book_id";
                break;
            case "category":
                sql = "SELECT book_id, name, author, description, available_copies,  category " +
                        "FROM book WHERE LOWER(category) LIKE LOWER(?) ORDER BY book_id";
                break;
            default:
                return books;
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("book_id"),
                            rs.getString("name"),
                            rs.getString("author"),
                            rs.getString("description"),
                            rs.getInt("available_copies"),
                            rs.getString("category")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        return books;
    }
}
