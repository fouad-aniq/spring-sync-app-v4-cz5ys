package ai.shreds.domain.exceptions;

import lombok.Getter;

/**
 * Domain-specific exception for metadata-related errors.
 */
@Getter
public class DomainMetadataException extends RuntimeException {

    private final String errorCode;
    private final String entityType;
    private final String entityId;
    private final String validationRule;

    /**
     * Creates a new DomainMetadataException.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     */
    public DomainMetadataException(String message, String errorCode) {
        this(message, errorCode, null, null, null);
    }

    /**
     * Creates a new DomainMetadataException with entity details.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param entityType Type of entity that caused the error
     * @param entityId ID of the entity that caused the error
     */
    public DomainMetadataException(String message, String errorCode, String entityType, String entityId) {
        this(message, errorCode, entityType, entityId, null);
    }

    /**
     * Creates a new DomainMetadataException with full details.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param entityType Type of entity that caused the error
     * @param entityId ID of the entity that caused the error
     * @param validationRule The validation rule that was violated
     */
    public DomainMetadataException(String message, String errorCode, String entityType, 
            String entityId, String validationRule) {
        super(message);
        this.errorCode = errorCode;
        this.entityType = entityType;
        this.entityId = entityId;
        this.validationRule = validationRule;
    }

    /**
     * Creates a formatted error message including all available details.
     *
     * @return Formatted error message
     */
    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(super.getMessage());
        if (entityType != null) {
            message.append(" [Entity Type: ").append(entityType).append("]");
        }
        if (entityId != null) {
            message.append(" [Entity ID: ").append(entityId).append("]");
        }
        if (validationRule != null) {
            message.append(" [Validation Rule: ").append(validationRule).append("]");
        }
        message.append(" [Error Code: ").append(errorCode).append("]");
        return message.toString();
    }

    /**
     * Creates a builder for constructing domain metadata exceptions.
     *
     * @return DomainMetadataExceptionBuilder
     */
    public static DomainMetadataExceptionBuilder builder() {
        return new DomainMetadataExceptionBuilder();
    }

    /**
     * Builder class for creating domain metadata exceptions.
     */
    public static class DomainMetadataExceptionBuilder {
        private String message;
        private String errorCode;
        private String entityType;
        private String entityId;
        private String validationRule;

        public DomainMetadataExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public DomainMetadataExceptionBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public DomainMetadataExceptionBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public DomainMetadataExceptionBuilder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public DomainMetadataExceptionBuilder validationRule(String validationRule) {
            this.validationRule = validationRule;
            return this;
        }

        public DomainMetadataException build() {
            return new DomainMetadataException(message, errorCode, entityType, entityId, validationRule);
        }
    }
}
