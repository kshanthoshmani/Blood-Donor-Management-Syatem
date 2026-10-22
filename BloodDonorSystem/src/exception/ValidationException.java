package exception;

/**
 * Custom exception for validation errors.
 * Demonstrates custom exception creation and OOP concepts.
 */
public class ValidationException extends Exception {

    private final String fieldName;

    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
    }

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        if (fieldName != null) {
            return "ValidationException [field=" + fieldName + "]: " + getMessage();
        }
        return "ValidationException: " + getMessage();
    }
}
