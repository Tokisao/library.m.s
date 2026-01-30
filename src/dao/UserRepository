
package dao;

import models.User;
import java.sql.*;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByIdAndPassword(String id, String password) {
        String sql = "SELECT user_id, first_name, second_name, phone_number, fines " +
                "FROM users WHERE user_id = ? AND password = ?";

        try (Connection con = DriverManager.getConnection(
                DatabaseConnection.URL, DatabaseConnection.USERNAME, DatabaseConnection.PASSWORD);
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
                            rs.getFloat("fines")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean updateFines(String userId, float amount) {
        String sql = "UPDATE users SET fines = fines + ? WHERE user_id = ?";

        try (Connection con = DriverManager.getConnection(
                DatabaseConnection.URL, DatabaseConnection.USERNAME, DatabaseConnection.PASSWORD);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setFloat(1, amount);
            stmt.setString(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating fines: " + e.getMessage());
            return false;
        }
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
}
