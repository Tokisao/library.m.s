package dao;

import models.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_id, name, author, description, available_copies FROM book ORDER BY book_id";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getInt("available_copies")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    public Book findById(int bookId) {
        String sql = "SELECT book_id, name, author, description, available_copies FROM book WHERE book_id = ?";

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
                            rs.getInt("available_copies")
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


    public boolean decreaseTotalCopies(int bookId) {
        String sql = "UPDATE book SET total_copies = total_copies - 1 " +
                "WHERE book_id = ? AND total_copies > 0";

        try (Connection con = DriverManager.getConnection(
                DatabaseConnection.URL, DatabaseConnection.USERNAME, DatabaseConnection.PASSWORD);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error decreasing total copies: " + e.getMessage());
            return false;
        }
    }
}
