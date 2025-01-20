package ai.shreds.domain.exceptions;

import lombok.Getter;

/**
 * Domain-specific exception for metadata-related errors.
 */
@Getter
public class DomainExceptionMetadata extends RuntimeException {

    private final String errorCode;
    private final String entityType;
    private final String entityId;
    private final String validationRule;

    /**
     * Creates a new DomainExceptionMetadata with just a message.
     *
     * @param message Error message
     */
    public DomainExceptionMetadata(String message) {
        this(message, "UNKNOWN");
    }

    /**
     * Creates a new DomainExceptionMetadata with message and error code.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     */
    public DomainExceptionMetadata(String message, String errorCode) {
        this(message, errorCode, null, null, null);
    }

    /**
     * Creates a new DomainExceptionMetadata with entity details.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param entityType Type of entity that caused the error
     * @param entityId ID of the entity that caused the error
     */
    public DomainExceptionMetadata(String message, String errorCode, String entityType, String entityId) {
        this(message, errorCode, entityType, entityId, null);
    }

    /**
     * Creates a new DomainExceptionMetadata with full details.
     *
     * @param message Error message
     * @param errorCode Error code for categorizing the error
     * @param entityType Type of entity that caused the error
     * @param entityId ID of the entity that caused the error
     * @param validationRule The validation rule that was violated
     */
    public DomainExceptionMetadata(String message, String errorCode, String entityType, 
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
     * @return DomainExceptionMetadataBuilder
     */
    public static DomainExceptionMetadataBuilder builder() {
        return new DomainExceptionMetadataBuilder();
    }

    /**
     * Builder class for creating domain metadata exceptions.
     */
    public static class DomainExceptionMetadataBuilder {
        private String message;
        private String errorCode;
        private String entityType;
        private String entityId;
        private String validationRule;

        public DomainExceptionMetadataBuilder message(String message) {
            this.message = message;
            return this;
        }

        public DomainExceptionMetadataBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public DomainExceptionMetadataBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public DomainExceptionMetadataBuilder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public DomainExceptionMetadataBuilder validationRule(String validationRule) {
            this.validationRule = validationRule;
            return this;
        }

        public DomainExceptionMetadata build() {
            return new DomainExceptionMetadata(message, errorCode, entityType, entityId, validationRule);
        }
    }
}
