package service;

import dao.UserDAO;
import dao.UserDAOImpl;
import exception.DatabaseException;
import exception.ValidationException;
import model.User;

/**
 * AuthenticationService – business logic for user login.
 * Demonstrates: Service Layer pattern, encapsulation, exception handling.
 */
public class AuthenticationService {

    private final UserDAO userDAO = new UserDAOImpl();

    /**
     * Validates credentials and returns the authenticated User.
     *
     * @throws ValidationException if username or password fields are empty
     * @throws SecurityException   if credentials are incorrect
     * @throws DatabaseException   if a database error occurs
     */
    public User login(String username, String password)
            throws ValidationException, DatabaseException {

        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username", "Please enter your username.");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password", "Please enter your password.");
        }

        User user = userDAO.authenticate(username.trim(), password);

        if (user == null) {
            throw new SecurityException("Invalid username or password.\nPlease try again.");
        }

        return user;
    }
}
