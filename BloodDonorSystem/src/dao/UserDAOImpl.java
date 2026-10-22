package dao;

import database.DatabaseConnection;
import exception.DatabaseException;
import model.User;

import java.sql.*;

/**
 * UserDAOImpl – JDBC implementation of UserDAO.
 * Demonstrates: JDBC PreparedStatement, ResultSet, DAO pattern.
 */
public class UserDAOImpl implements UserDAO {

    @Override
    public User authenticate(String username, String password) throws DatabaseException {
        String sql = "SELECT USER_ID, USERNAME, PASSWORD FROM USERS WHERE USERNAME = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("PASSWORD");
                    // Simple comparison (plaintext for demonstration)
                    if (storedPassword != null && storedPassword.equals(password)) {
                        return new User(
                            rs.getInt("USER_ID"),
                            rs.getString("USERNAME"),
                            storedPassword
                        );
                    }
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Authentication database error: " + e.getMessage(), e.getErrorCode(), e);
        }

        return null; // credentials not matched
    }
}
