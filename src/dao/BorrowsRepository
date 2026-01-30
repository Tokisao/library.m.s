package dao;

import models.Borrows;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BorrowsRepository {

    public List<Borrows> findByUserId(String userId) {
        List<Borrows> borrows = new ArrayList<>();
        String sql = "SELECT id, book_id, user_id, borrowed_date, " +
                "due_date, returned_day, days_extended, status " +
                "FROM borrows " +
                "WHERE user_id = ? " +
                "ORDER BY book_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    borrows.add(new Borrows(
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getString("user_id"),
                            rs.getDate("borrowed_date"),
                            rs.getDate("due_date"),
                            rs.getDate("returned_day"),
                            rs.getInt("days_extended"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading borrows: " + e.getMessage());
        }
        return borrows;
    }

    public boolean createBorrow(int bookId, String userId, Date dueDate) {
        String sql = "INSERT INTO borrows (book_id, user_id, borrowed_date, due_date, status) " +
                "VALUES (?, ?, CURRENT_DATE, ?, 'BORROWED')";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            stmt.setString(2, userId);
            stmt.setDate(3, new java.sql.Date(dueDate.getTime()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating borrow: " + e.getMessage());
            return false;
        }
    }

    public int getUserBorrowedCount(String userId) {
        String sql = "SELECT COUNT(*) as count FROM borrows WHERE user_id = ? AND status = 'BORROWED'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user borrowed count: " + e.getMessage());
        }
        return 0;
    }

    public boolean hasOverdueBooks(String userId) {
        String sql = "SELECT COUNT(*) as count FROM borrows WHERE user_id = ? AND status = 'OVERDUE' ";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking overdue books: " + e.getMessage());
        }
        return false;
    }

    public boolean isBookBorrowedByUser(int bookId, String userId) {
        String sql = "SELECT COUNT(*) as count FROM borrows WHERE book_id = ? AND user_id = ? AND status = 'BORROWED'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            stmt.setString(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if book is borrowed: " + e.getMessage());
        }
        return false;
    }


    public boolean updateFine(int borrowId, float fineAmount) {
        String sql = "UPDATE borrows SET fine_amount = ? WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setFloat(1, fineAmount);
            stmt.setInt(2, borrowId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating fine: " + e.getMessage());
            return false;
        }
    }


    public boolean markAsLost(int borrowId) {
        String sql = "UPDATE borrows SET status = 'LOST', return_date = CURRENT_DATE WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, borrowId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error marking book as lost: " + e.getMessage());
            return false;
        }
    }


    public Borrows findById(int borrowId) {
        String sql = "SELECT id, book_id, user_id, borrowed_date, " +
                "due_date, returned_day, days_extended, fine_amount, status " +
                "FROM borrows WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, borrowId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Borrows(
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getString("user_id"),
                            rs.getDate("borrowed_date"),
                            rs.getDate("due_date"),
                            rs.getDate("returned_day"),
                            rs.getInt("days_extended"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding borrow by ID: " + e.getMessage());
        }
        return null;
    }








}
