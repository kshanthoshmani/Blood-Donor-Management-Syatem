package exception;

/**
 * Custom exception for database-related errors.
 * Wraps SQLException and other DB errors with meaningful messages.
 */
public class DatabaseException extends Exception {

    private final int errorCode;

    public DatabaseException(String message) {
        super(message);
        this.errorCode = -1;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = -1;
    }

    public DatabaseException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "DatabaseException [code=" + errorCode + "]: " + getMessage();
    }
}
