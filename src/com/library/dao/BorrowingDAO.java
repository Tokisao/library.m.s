package com.library.dao;

import com.library.DatabaseConnection;
import com.library.model.Borrowing;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {

    public boolean createBorrowing(Borrowing borrowing) {
        String sql = "INSERT INTO borrowings (user_id, book_id, borrowed_date, due_date, " +
                "returned_date, status, fine_amount, fine_paid, days_extended) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, borrowing.getUserId());
            pstmt.setInt(2, borrowing.getBookId());
            pstmt.setDate(3, Date.valueOf(borrowing.getBorrowedDate()));
            pstmt.setDate(4, Date.valueOf(borrowing.getDueDate()));

            if (borrowing.getReturnedDate() != null) {
                pstmt.setDate(5, Date.valueOf(borrowing.getReturnedDate()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setString(6, borrowing.getStatus());
            pstmt.setDouble(7, borrowing.getFineAmount());
            pstmt.setBoolean(8, borrowing.isFinePaid());
            pstmt.setInt(9, borrowing.getDaysExtended());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        borrowing.setBorrowingId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println(" Error creating borrowing: " + e.getMessage());
        }
        return false;
    }

    public boolean updateBorrowing(Borrowing borrowing) {
        String sql = "UPDATE borrowings SET user_id = ?, book_id = ?, borrowed_date = ?, " +
                "due_date = ?, returned_date = ?, status = ?, fine_amount = ?, " +
                "fine_paid = ?, days_extended = ? WHERE borrowing_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, borrowing.getUserId());
            pstmt.setInt(2, borrowing.getBookId());
            pstmt.setDate(3, Date.valueOf(borrowing.getBorrowedDate()));
            pstmt.setDate(4, Date.valueOf(borrowing.getDueDate()));

            if (borrowing.getReturnedDate() != null) {
                pstmt.setDate(5, Date.valueOf(borrowing.getReturnedDate()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setString(6, borrowing.getStatus());
            pstmt.setDouble(7, borrowing.getFineAmount());
            pstmt.setBoolean(8, borrowing.isFinePaid());
            pstmt.setInt(9, borrowing.getDaysExtended());
            pstmt.setInt(10, borrowing.getBorrowingId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating borrowing: " + e.getMessage());
            return false;
        }
    }

    public Borrowing getBorrowingById(int borrowingId) {
        String sql = "SELECT b.*, u.first_name, u.last_name, u.email, bk.title as book_title " +
                "FROM borrowings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN books bk ON b.book_id = bk.book_id " +
                "WHERE b.borrowing_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, borrowingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Borrowing borrowing = extractBorrowingFromResultSet(rs);
                borrowing.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
                borrowing.setUserEmail(rs.getString("email"));
                borrowing.setBookTitle(rs.getString("book_title"));
                return borrowing;
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching borrowing: " + e.getMessage());
        }
        return null;
    }

    public List<Borrowing> getActiveBorrowingsByUser(int userId) {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, u.email, bk.title as book_title " +
                "FROM borrowings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN books bk ON b.book_id = bk.book_id " +
                "WHERE b.user_id = ? AND b.status IN ('BORROWED', 'OVERDUE') " +
                "ORDER BY b.due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Borrowing borrowing = extractBorrowingFromResultSet(rs);
                borrowing.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
                borrowing.setUserEmail(rs.getString("email"));
                borrowing.setBookTitle(rs.getString("book_title"));
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching active borrowings: " + e.getMessage());
        }
        return borrowings;
    }

    public List<Borrowing> getAllActiveBorrowings() {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, u.email, bk.title as book_title " +
                "FROM borrowings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN books bk ON b.book_id = bk.book_id " +
                "WHERE b.status IN ('BORROWED', 'OVERDUE') " +
                "ORDER BY b.due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Borrowing borrowing = extractBorrowingFromResultSet(rs);
                borrowing.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
                borrowing.setUserEmail(rs.getString("email"));
                borrowing.setBookTitle(rs.getString("book_title"));
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching all active borrowings: " + e.getMessage());
        }
        return borrowings;
    }

    public List<Borrowing> getOverdueBorrowings() {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, u.email, bk.title as book_title " +
                "FROM borrowings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN books bk ON b.book_id = bk.book_id " +
                "WHERE (b.status = 'BORROWED' OR b.status = 'OVERDUE') " +
                "AND b.due_date < CURRENT_DATE " +
                "ORDER BY b.due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Borrowing borrowing = extractBorrowingFromResultSet(rs);
                borrowing.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
                borrowing.setUserEmail(rs.getString("email"));
                borrowing.setBookTitle(rs.getString("book_title"));
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching overdue borrowings: " + e.getMessage());
        }
        return borrowings;
    }

    public List<Borrowing> getBorrowingHistoryByUser(int userId) {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, u.email, bk.title as book_title " +
                "FROM borrowings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN books bk ON b.book_id = bk.book_id " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.borrowed_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Borrowing borrowing = extractBorrowingFromResultSet(rs);
                borrowing.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
                borrowing.setUserEmail(rs.getString("email"));
                borrowing.setBookTitle(rs.getString("book_title"));
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching borrowing history: " + e.getMessage());
        }
        return borrowings;
    }

    public List<Borrowing> getBorrowingHistoryByBook(int bookId) {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, u.email, bk.title as book_title " +
                "FROM borrowings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN books bk ON b.book_id = bk.book_id " +
                "WHERE b.book_id = ? " +
                "ORDER BY b.borrowed_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Borrowing borrowing = extractBorrowingFromResultSet(rs);
                borrowing.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
                borrowing.setUserEmail(rs.getString("email"));
                borrowing.setBookTitle(rs.getString("book_title"));
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching book borrowing history: " + e.getMessage());
        }
        return borrowings;
    }

    public List<Object[]> getMonthlyBorrowingStats(int year) {
        List<Object[]> stats = new ArrayList<>();
        String sql = "SELECT EXTRACT(MONTH FROM borrowed_date) as month, " +
                "COUNT(*) as borrow_count " +
                "FROM borrowings " +
                "WHERE EXTRACT(YEAR FROM borrowed_date) = ? " +
                "GROUP BY EXTRACT(MONTH FROM borrowed_date) " +
                "ORDER BY month";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] stat = new Object[2];
                stat[0] = rs.getInt("month");
                stat[1] = rs.getInt("borrow_count");
                stats.add(stat);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching monthly stats: " + e.getMessage());
        }
        return stats;
    }

    public boolean extendBorrowing(int borrowingId, int additionalDays) {
        String sql = "UPDATE borrowings SET due_date = due_date + INTERVAL '1 day' * ?, " +
                "days_extended = days_extended + ?, status = 'BORROWED' " +
                "WHERE borrowing_id = ? AND status IN ('BORROWED', 'OVERDUE')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, additionalDays);
            pstmt.setInt(2, additionalDays);
            pstmt.setInt(3, borrowingId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error extending borrowing: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFinePayment(int borrowingId, boolean paid) {
        String sql = "UPDATE borrowings SET fine_paid = ? WHERE borrowing_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, paid);
            pstmt.setInt(2, borrowingId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating fine payment: " + e.getMessage());
            return false;
        }
    }

    public boolean isBookBorrowedByUser(int userId, int bookId) {
        String sql = "SELECT COUNT(*) FROM borrowings " +
                "WHERE user_id = ? AND book_id = ? AND status IN ('BORROWED', 'OVERDUE')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println(" Error checking if book is borrowed: " + e.getMessage());
        }
        return false;
    }

    private Borrowing extractBorrowingFromResultSet(ResultSet rs) throws SQLException {
        Borrowing borrowing = new Borrowing();
        borrowing.setBorrowingId(rs.getInt("borrowing_id"));
        borrowing.setUserId(rs.getInt("user_id"));
        borrowing.setBookId(rs.getInt("book_id"));
        borrowing.setBorrowedDate(rs.getDate("borrowed_date").toLocalDate());
        borrowing.setDueDate(rs.getDate("due_date").toLocalDate());

        if (rs.getDate("returned_date") != null) {
            borrowing.setReturnedDate(rs.getDate("returned_date").toLocalDate());
        }

        borrowing.setStatus(rs.getString("status"));
        borrowing.setFineAmount(rs.getDouble("fine_amount"));
        borrowing.setFinePaid(rs.getBoolean("fine_paid"));
        borrowing.setDaysExtended(rs.getInt("days_extended"));
        return borrowing;
    }
}