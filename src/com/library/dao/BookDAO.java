package com.library.dao;

import com.library.DatabaseConnection;
import com.library.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, publisher, publication_year, " +
                "total_copies, available_copies, genre, language, pages, description, location, rating, times_borrowed) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getPublicationYear());
            pstmt.setInt(6, book.getTotalCopies());
            pstmt.setInt(7, book.getAvailableCopies());
            pstmt.setString(8, book.getGenre());
            pstmt.setString(9, book.getLanguage());
            pstmt.setInt(10, book.getPages());
            pstmt.setString(11, book.getDescription());
            pstmt.setString(12, book.getLocation());
            pstmt.setDouble(13, book.getRating());
            pstmt.setInt(14, book.getTimesBorrowed());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setBookId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println(" Error adding book: " + e.getMessage());
        }
        return false;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching books: " + e.getMessage());
        }
        return books;
    }

    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching book: " + e.getMessage());
        }
        return null;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) OR " +
                "LOWER(author) LIKE LOWER(?) OR LOWER(genre) LIKE LOWER(?) OR " +
                "LOWER(description) LIKE LOWER(?) OR isbn LIKE ? " +
                "ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Error searching books: " + e.getMessage());
        }
        return books;
    }


    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, publisher = ?, " +
                "publication_year = ?, total_copies = ?, available_copies = ?, " +
                "genre = ?, language = ?, pages = ?, description = ?, location = ?, " +
                "rating = ?, times_borrowed = ? WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getPublicationYear());
            pstmt.setInt(6, book.getTotalCopies());
            pstmt.setInt(7, book.getAvailableCopies());
            pstmt.setString(8, book.getGenre());
            pstmt.setString(9, book.getLanguage());
            pstmt.setInt(10, book.getPages());
            pstmt.setString(11, book.getDescription());
            pstmt.setString(12, book.getLocation());
            pstmt.setDouble(13, book.getRating());
            pstmt.setInt(14, book.getTimesBorrowed());
            pstmt.setInt(15, book.getBookId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating book: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error deleting book: " + e.getMessage());
            return false;
        }
    }


    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available_copies > 0 ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching available books: " + e.getMessage());
        }
        return books;
    }

    public boolean updateAvailableCopies(int bookId, int availableCopies) {
        String sql = "UPDATE books SET available_copies = ? WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, availableCopies);
            pstmt.setInt(2, bookId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating available copies: " + e.getMessage());
            return false;
        }
    }

    public List<Book> getPopularBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY times_borrowed DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching popular books: " + e.getMessage());
        }
        return books;
    }

    public boolean updateBookRating(int bookId, double newRating) {
        String sql = "UPDATE books SET rating = ? WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newRating);
            pstmt.setInt(2, bookId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating book rating: " + e.getMessage());
            return false;
        }
    }

    public List<Book> getBooksByGenre(String genre) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(genre) LIKE LOWER(?) ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + genre + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching books by genre: " + e.getMessage());
        }
        return books;
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        book.setGenre(rs.getString("genre"));
        book.setLanguage(rs.getString("language"));
        book.setPages(rs.getInt("pages"));
        book.setDescription(rs.getString("description"));
        book.setLocation(rs.getString("location"));
        book.setRating(rs.getDouble("rating"));
        book.setTimesBorrowed(rs.getInt("times_borrowed"));
        return book;
    }
}