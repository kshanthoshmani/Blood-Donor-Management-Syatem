package model;

/**
 * User – concrete system user extending BaseUser.
 * Demonstrates Inheritance and Polymorphism (OOP concepts).
 */
public class User extends BaseUser {

    public User(int userId, String username, String password) {
        super(userId, username, password);
    }

    @Override
    public String getRole() { return "Admin"; }
}
