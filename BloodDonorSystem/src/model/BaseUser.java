package model;

/**
 * BaseUser – abstract base class demonstrating Inheritance (OOP concept).
 * All system users extend this class with common attributes.
 */
public abstract class BaseUser {
    protected int    userId;
    protected String username;
    protected String password;

    public BaseUser(int userId, String username, String password) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
    }

    public int    getUserId()  { return userId; }
    public String getUsername(){ return username; }
    public String getPassword(){ return password; }

    /** Returns the role of this user (demonstrates Polymorphism). */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + ":" + username;
    }
}
