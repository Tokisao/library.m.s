package dao;

import models.Book;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByIdAndPassword(String id, String password) {
        String sql = "SELECT user_id, first_name, second_name, phone_number, fines, role " +
                "FROM users WHERE user_id = ? AND password = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("user_id"),
                            rs.getString("first_name"),
                            rs.getString("second_name"),
                            rs.getString("phone_number"),
                            rs.getFloat("fines"),
                            rs.getString("role")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return Optional.empty();
    }

    public float getUserFines(String userId) {
        String sql = "SELECT fines FROM users WHERE user_id = ?";

        try (Connection con = DriverManager.getConnection(
                DatabaseConnection.URL, DatabaseConnection.USERNAME, DatabaseConnection.PASSWORD);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("fines");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user fines: " + e.getMessage());
        }
        return 0.0f;
    }

    public Optional<User> findByUserId(String id) {
        String sql = "SELECT user_id, first_name, second_name, phone_number, fines, role " +
                "FROM users WHERE user_id = ? ";

        try (Connection con = DriverManager.getConnection(
                DatabaseConnection.URL, DatabaseConnection.USERNAME, DatabaseConnection.PASSWORD);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("user_id"),
                            rs.getString("first_name"),
                            rs.getString("second_name"),
                            rs.getString("phone_number"),
                            rs.getFloat("fines"),
                            rs.getString("role")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, first_name, second_name, phone_number, fines, role FROM users ORDER BY user_id";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("user_id"),
                        rs.getString("first_name"),
                        rs.getString("second_name"),
                        rs.getString("phone_number"),
                        rs.getFloat("fines"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }
    public boolean createUser(String userId, String password, String firstName,
                              String lastName, String phone, String role) {
        String sql = "INSERT INTO users (user_id, password, first_name, second_name, phone_number, role, fines) " +
                "VALUES (?, ?, ?, ?, ?, ?, 0.0)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, phone);
            stmt.setString(6, role);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(String userId, String firstName, String lastName,
                              String phone, String role) {
        String sql = "UPDATE users SET first_name = ?, second_name = ?, " +
                "phone_number = ?, role = ? WHERE user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phone);
            stmt.setString(4, role);
            stmt.setString(5, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean changeUserRole(String userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, newRole);
            stmt.setString(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error changing user role: " + e.getMessage());
            return false;
        }
    }
}
