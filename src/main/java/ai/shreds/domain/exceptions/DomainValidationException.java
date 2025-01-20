package ai.shreds.domain.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when domain validation rules are violated.
 * This exception provides detailed information about validation failures
 * in the domain layer.
 */
public class DomainValidationException extends RuntimeException {

    private final List<ValidationError> errors;
    private final String entityType;

    public DomainValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.entityType = "UNKNOWN";
    }

    public DomainValidationException(String message, String entityType) {
        super(message);
        this.errors = new ArrayList<>();
        this.entityType = entityType;
    }

    public DomainValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = new ArrayList<>();
        this.entityType = "UNKNOWN";
    }

    public DomainValidationException(String message, String entityType, List<ValidationError> errors) {
        super(message);
        this.errors = new ArrayList<>(errors);
        this.entityType = entityType;
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public String getEntityType() {
        return entityType;
    }

    public void addError(String field, String message) {
        errors.add(new ValidationError(field, message));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
            .append("DomainValidationException[entityType=")
            .append(entityType)
            .append(", message=")
            .append(getMessage())
            .append(", errors=");

        if (!errors.isEmpty()) {
            errors.forEach(error -> sb.append("\n  - ").append(error));
        } else {
            sb.append("none");
        }

        return sb.append("]").toString();
    }

    /**
     * Creates a validation exception for invalid file metadata.
     *
     * @param field The invalid field
     * @param message The validation message
     * @return A new DomainValidationException
     */
    public static DomainValidationException invalidFileMetadata(String field, String message) {
        DomainValidationException ex = new DomainValidationException(
            "Invalid file metadata: " + message,
            "FILE_METADATA"
        );
        ex.addError(field, message);
        return ex;
    }

    /**
     * Creates a validation exception for invalid version data.
     *
     * @param field The invalid field
     * @param message The validation message
     * @return A new DomainValidationException
     */
    public static DomainValidationException invalidVersionData(String field, String message) {
        DomainValidationException ex = new DomainValidationException(
            "Invalid version data: " + message,
            "VERSION_RECORD"
        );
        ex.addError(field, message);
        return ex;
    }

    /**
     * Inner class representing a specific validation error.
     */
    public static class ValidationError {
        private final String field;
        private final String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return field + ": " + message;
        }
    }
}
