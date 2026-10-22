package dao;

import exception.DatabaseException;
import model.User;

/**
 * UserDAO – Data Access Object interface for authentication.
 * Demonstrates Interface (OOP abstraction concept).
 */
public interface UserDAO {
    /**
     * Authenticates a user by username and password.
     * @return User object if valid, null if credentials are wrong.
     */
    User authenticate(String username, String password) throws DatabaseException;
}
