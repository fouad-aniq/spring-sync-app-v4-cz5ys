package ai.shreds.infrastructure.exceptions;

import lombok.Getter;

/**
 * Base exception for infrastructure layer errors.
 */
@Getter
public class InfrastructureException extends RuntimeException {

    private final String errorCode;
    private final String component;
    private final String operation;

    /**
     * Creates a new InfrastructureException.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     */
    public InfrastructureException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    /**
     * Creates a new InfrastructureException with a cause.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param cause The underlying cause of the error
     */
    public InfrastructureException(String message, String errorCode, Throwable cause) {
        this(message, errorCode, null, null, cause);
    }

    /**
     * Creates a new InfrastructureException with component and operation details.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param component The infrastructure component where the error occurred
     * @param operation The operation that failed
     */
    public InfrastructureException(String message, String errorCode, String component, String operation) {
        this(message, errorCode, component, operation, null);
    }

    /**
     * Creates a new InfrastructureException with full details.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param component The infrastructure component where the error occurred
     * @param operation The operation that failed
     * @param cause The underlying cause of the error
     */
    public InfrastructureException(String message, String errorCode, String component, String operation, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.component = component;
        this.operation = operation;
    }

    /**
     * Creates a formatted error message including all available details.
     *
     * @return Formatted error message
     */
    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(super.getMessage());
        if (component != null) {
            message.append(" [Component: ").append(component).append("]");
        }
        if (operation != null) {
            message.append(" [Operation: ").append(operation).append("]");
        }
        message.append(" [Error Code: ").append(errorCode).append("]");
        return message.toString();
    }

    /**
     * Checks if this exception is related to a specific component.
     *
     * @param comp The component to check
     * @return true if this exception is related to the specified component
     */
    public boolean isComponent(String comp) {
        return component != null && component.equals(comp);
    }

    /**
     * Checks if this exception is related to a specific operation.
     *
     * @param op The operation to check
     * @return true if this exception is related to the specified operation
     */
    public boolean isOperation(String op) {
        return operation != null && operation.equals(op);
    }

    /**
     * Creates a builder for constructing infrastructure exceptions.
     *
     * @return InfrastructureExceptionBuilder
     */
    public static InfrastructureExceptionBuilder builder() {
        return new InfrastructureExceptionBuilder();
    }

    /**
     * Builder class for creating infrastructure exceptions.
     */
    public static class InfrastructureExceptionBuilder {
        private String message;
        private String errorCode;
        private String component;
        private String operation;
        private Throwable cause;

        public InfrastructureExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public InfrastructureExceptionBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public InfrastructureExceptionBuilder component(String component) {
            this.component = component;
            return this;
        }

        public InfrastructureExceptionBuilder operation(String operation) {
            this.operation = operation;
            return this;
        }

        public InfrastructureExceptionBuilder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public InfrastructureException build() {
            return new InfrastructureException(message, errorCode, component, operation, cause);
        }
    }
}
