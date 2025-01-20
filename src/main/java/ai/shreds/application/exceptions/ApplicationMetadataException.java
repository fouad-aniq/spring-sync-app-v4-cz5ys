package ai.shreds.application.exceptions;

import lombok.Getter;

/**
 * Exception thrown by the application layer when metadata operations fail.
 */
@Getter
public class ApplicationMetadataException extends RuntimeException {

    private final String errorCode;
    private final String operation;
    private final String resourceId;

    /**
     * Creates a new ApplicationMetadataException.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     */
    public ApplicationMetadataException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    /**
     * Creates a new ApplicationMetadataException with a cause.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param cause The underlying cause of the error
     */
    public ApplicationMetadataException(String message, String errorCode, Throwable cause) {
        this(message, errorCode, null, null, cause);
    }

    /**
     * Creates a new ApplicationMetadataException with operation and resource details.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param operation The operation that failed
     * @param resourceId The ID of the resource being operated on
     */
    public ApplicationMetadataException(String message, String errorCode, String operation, String resourceId) {
        this(message, errorCode, operation, resourceId, null);
    }

    /**
     * Creates a new ApplicationMetadataException with full details.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param operation The operation that failed
     * @param resourceId The ID of the resource being operated on
     * @param cause The underlying cause of the error
     */
    public ApplicationMetadataException(String message, String errorCode, String operation, String resourceId, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.operation = operation;
        this.resourceId = resourceId;
    }

    /**
     * Creates a formatted error message including all available details.
     *
     * @return Formatted error message
     */
    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(super.getMessage());
        if (operation != null) {
            message.append(" [Operation: ").append(operation).append("]");
        }
        if (resourceId != null) {
            message.append(" [Resource: ").append(resourceId).append("]");
        }
        message.append(" [Error Code: ").append(errorCode).append("]");
        return message.toString();
    }

    /**
     * Checks if this exception is related to a specific error code.
     *
     * @param code The error code to check
     * @return true if this exception has the specified error code
     */
    public boolean hasErrorCode(String code) {
        return errorCode != null && errorCode.equals(code);
    }

    /**
     * Checks if this exception is related to a specific operation.
     *
     * @param op The operation to check
     * @return true if this exception is related to the specified operation
     */
    public boolean isOperationType(String op) {
        return operation != null && operation.equals(op);
    }
}
