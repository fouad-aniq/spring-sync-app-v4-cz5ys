package ai.shreds.domain.exceptions;

/**
 * Exception thrown when a conflict is detected in the domain layer.
 * This exception indicates that there are conflicting versions or states
 * that need to be resolved before proceeding.
 */
public class DomainConflictException extends RuntimeException {

    private final String conflictType;
    private final String resourceId;

    public DomainConflictException(String message) {
        super(message);
        this.conflictType = "UNKNOWN";
        this.resourceId = null;
    }

    public DomainConflictException(String message, String conflictType, String resourceId) {
        super(message);
        this.conflictType = conflictType;
        this.resourceId = resourceId;
    }

    public DomainConflictException(String message, Throwable cause) {
        super(message, cause);
        this.conflictType = "UNKNOWN";
        this.resourceId = null;
    }

    public DomainConflictException(String message, String conflictType, String resourceId, Throwable cause) {
        super(message, cause);
        this.conflictType = conflictType;
        this.resourceId = resourceId;
    }

    public String getConflictType() {
        return conflictType;
    }

    public String getResourceId() {
        return resourceId;
    }

    @Override
    public String toString() {
        return String.format("DomainConflictException[type=%s, resourceId=%s, message=%s]",
                conflictType, resourceId, getMessage());
    }

    /**
     * Creates a version conflict exception.
     *
     * @param fileId The ID of the file with conflicting versions
     * @return A new DomainConflictException
     */
    public static DomainConflictException versionConflict(String fileId) {
        return new DomainConflictException(
                "Version conflict detected for file: " + fileId,
                "VERSION_CONFLICT",
                fileId
        );
    }

    /**
     * Creates a merge conflict exception.
     *
     * @param fileId The ID of the file that couldn't be merged
     * @param reason The reason for the merge failure
     * @return A new DomainConflictException
     */
    public static DomainConflictException mergeConflict(String fileId, String reason) {
        return new DomainConflictException(
                "Failed to merge versions for file: " + fileId + ". Reason: " + reason,
                "MERGE_CONFLICT",
                fileId
        );
    }
}
