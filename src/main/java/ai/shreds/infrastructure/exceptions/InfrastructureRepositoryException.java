package ai.shreds.infrastructure.exceptions;

import lombok.Getter;

/**
 * Exception thrown by repository implementations in the infrastructure layer.
 */
@Getter
public class InfrastructureRepositoryException extends InfrastructureException {

    private final String repository;
    private final String entity;
    private final String operation;

    /**
     * Creates a new InfrastructureRepositoryException.
     *
     * @param message Error message
     * @param repository The repository where the error occurred
     * @param errorCode Error code for categorizing the error
     */
    public InfrastructureRepositoryException(String message, String repository, String errorCode) {
        this(message, repository, null, null, errorCode, null);
    }

    /**
     * Creates a new InfrastructureRepositoryException with a cause.
     *
     * @param message Error message
     * @param repository The repository where the error occurred
     * @param errorCode Error code for categorizing the error
     * @param cause The underlying cause of the error
     */
    public InfrastructureRepositoryException(String message, String repository, String errorCode, Throwable cause) {
        this(message, repository, null, null, errorCode, cause);
    }

    /**
     * Creates a new InfrastructureRepositoryException with full details.
     *
     * @param message Error message
     * @param repository The repository where the error occurred
     * @param entity The entity type being operated on
     * @param operation The operation that failed
     * @param errorCode Error code for categorizing the error
     * @param cause The underlying cause of the error
     */
    public InfrastructureRepositoryException(String message, String repository, String entity, 
            String operation, String errorCode, Throwable cause) {
        super(message, errorCode, "Repository", operation, cause);
        this.repository = repository;
        this.entity = entity;
        this.operation = operation;
    }

    /**
     * Creates a formatted error message including all repository-specific details.
     *
     * @return Formatted error message
     */
    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(super.getMessage());
        if (repository != null) {
            message.append(" [Repository: ").append(repository).append("]");
        }
        if (entity != null) {
            message.append(" [Entity: ").append(entity).append("]");
        }
        if (operation != null) {
            message.append(" [Operation: ").append(operation).append("]");
        }
        return message.toString();
    }

    /**
     * Creates a builder for constructing repository exceptions.
     *
     * @return InfrastructureRepositoryExceptionBuilder
     */
    public static InfrastructureRepositoryExceptionBuilder builder() {
        return new InfrastructureRepositoryExceptionBuilder();
    }

    /**
     * Builder class for creating repository exceptions.
     */
    public static class InfrastructureRepositoryExceptionBuilder {
        private String message;
        private String repository;
        private String entity;
        private String operation;
        private String errorCode;
        private Throwable cause;

        public InfrastructureRepositoryExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public InfrastructureRepositoryExceptionBuilder repository(String repository) {
            this.repository = repository;
            return this;
        }

        public InfrastructureRepositoryExceptionBuilder entity(String entity) {
            this.entity = entity;
            return this;
        }

        public InfrastructureRepositoryExceptionBuilder operation(String operation) {
            this.operation = operation;
            return this;
        }

        public InfrastructureRepositoryExceptionBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public InfrastructureRepositoryExceptionBuilder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public InfrastructureRepositoryException build() {
            return new InfrastructureRepositoryException(
                    message, repository, entity, operation, errorCode, cause);
        }
    }
}
