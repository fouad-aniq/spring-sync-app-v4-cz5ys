package ai.shreds.application.exceptions;

public class ApplicationValidationException extends RuntimeException {
    public ApplicationValidationException(String message) {
        super(message);
    }

    public ApplicationValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
