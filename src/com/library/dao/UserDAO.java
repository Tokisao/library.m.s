package com.library.dao;

import com.library.DatabaseConnection;
import com.library.model.User;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (library_id, first_name, last_name, email, " +
                "phone, address, date_of_birth, registration_date, status, total_borrowed, total_fines) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getLibraryId());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setDate(7, Date.valueOf(user.getDateOfBirth()));
            pstmt.setDate(8, Date.valueOf(user.getRegistrationDate()));
            pstmt.setString(9, user.getStatus());
            pstmt.setInt(10, user.getTotalBorrowed());
            pstmt.setDouble(11, user.getTotalFines());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println(" Error adding user: " + e.getMessage());
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, " +
                "(SELECT COUNT(*) FROM borrowings b WHERE b.user_id = u.user_id AND b.status IN ('BORROWED', 'OVERDUE')) as current_borrowings " +
                "FROM users u ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                user.setCurrentBorrowings(rs.getInt("current_borrowings"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public User getUserById(int userId) {
        String sql = "SELECT u.*, " +
                "(SELECT COUNT(*) FROM borrowings b WHERE b.user_id = u.user_id AND b.status IN ('BORROWED', 'OVERDUE')) as current_borrowings " +
                "FROM users u WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                user.setCurrentBorrowings(rs.getInt("current_borrowings"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public User getUserByLibraryId(String libraryId) {
        String sql = "SELECT u.*, " +
                "(SELECT COUNT(*) FROM borrowings b WHERE b.user_id = u.user_id AND b.status IN ('BORROWED', 'OVERDUE')) as current_borrowings " +
                "FROM users u WHERE library_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, libraryId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                user.setCurrentBorrowings(rs.getInt("current_borrowings"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching user by library ID: " + e.getMessage());
        }
        return null;
    }

    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, " +
                "(SELECT COUNT(*) FROM borrowings b WHERE b.user_id = u.user_id AND b.status IN ('BORROWED', 'OVERDUE')) as current_borrowings " +
                "FROM users u WHERE LOWER(first_name) LIKE LOWER(?) OR " +
                "LOWER(last_name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) OR " +
                "library_id LIKE ? ORDER BY last_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                user.setCurrentBorrowings(rs.getInt("current_borrowings"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println(" Error searching users: " + e.getMessage());
        }
        return users;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET library_id = ?, first_name = ?, last_name = ?, " +
                "email = ?, phone = ?, address = ?, date_of_birth = ?, status = ?, " +
                "total_borrowed = ?, total_fines = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getLibraryId());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setDate(7, Date.valueOf(user.getDateOfBirth()));
            pstmt.setString(8, user.getStatus());
            pstmt.setInt(9, user.getTotalBorrowed());
            pstmt.setDouble(10, user.getTotalFines());
            pstmt.setInt(11, user.getUserId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating user status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserFines(int userId, double fineAmount) {
        String sql = "UPDATE users SET total_fines = total_fines + ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, fineAmount);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating user fines: " + e.getMessage());
            return false;
        }
    }

    public boolean incrementUserBorrowings(int userId) {
        String sql = "UPDATE users SET total_borrowed = total_borrowed + 1 WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error incrementing user borrowings: " + e.getMessage());
            return false;
        }
    }

    public List<User> getActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, " +
                "(SELECT COUNT(*) FROM borrowings b WHERE b.user_id = u.user_id AND b.status IN ('BORROWED', 'OVERDUE')) as current_borrowings " +
                "FROM users u WHERE status = 'ACTIVE' ORDER BY last_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                user.setCurrentBorrowings(rs.getInt("current_borrowings"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching active users: " + e.getMessage());
        }
        return users;
    }

    public boolean canUserBorrow(int userId, int maxBorrowings) {
        String sql = "SELECT status, " +
                "(SELECT COUNT(*) FROM borrowings WHERE user_id = ? AND status IN ('BORROWED', 'OVERDUE')) as current_borrowings " +
                "FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                int currentBorrowings = rs.getInt("current_borrowings");

                return "ACTIVE".equals(status) && currentBorrowings < maxBorrowings;
            }
        } catch (SQLException e) {
            System.err.println(" Error checking user borrow eligibility: " + e.getMessage());
        }
        return false;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setLibraryId(rs.getString("library_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            user.setDateOfBirth(dob.toLocalDate());
        }

        Date regDate = rs.getDate("registration_date");
        if (regDate != null) {
            user.setRegistrationDate(regDate.toLocalDate());
        }

        user.setStatus(rs.getString("status"));
        user.setTotalBorrowed(rs.getInt("total_borrowed"));
        user.setTotalFines(rs.getDouble("total_fines"));
        return user;
    }
}