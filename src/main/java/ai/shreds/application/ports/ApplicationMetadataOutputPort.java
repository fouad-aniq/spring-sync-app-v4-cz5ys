package ai.shreds.application.ports;

import ai.shreds.shared.dtos.*;

/**
 * Output port for metadata-related notifications and monitoring.
 * Implements the observer pattern for metadata changes and system monitoring.
 */
public interface ApplicationMetadataOutputPort {

    /**
     * Notifies observers about changes in file metadata.
     *
     * @param metadata The updated metadata
     */
    void notifyMetadataChange(SharedFileMetadataDTO metadata);

    /**
     * Notifies observers about conflict resolution outcomes.
     *
     * @param resolution The conflict resolution result
     */
    void notifyConflictResolution(SharedConflictResolutionResponseDTO resolution);

    /**
     * Reports system errors for monitoring and logging.
     *
     * @param e The exception that occurred
     */
    void reportError(Exception e);

    /**
     * Records performance metrics for monitoring.
     *
     * @param operationType The type of operation (e.g., "CREATE", "UPDATE")
     * @param durationMs The duration of the operation in milliseconds
     */
    void recordMetrics(String operationType, long durationMs);

    /**
     * Reports system health status.
     *
     * @param status The current health status
     * @param details Additional health check details
     */
    void reportHealth(String status, String details);

    /**
     * Notifies about security-related events.
     *
     * @param eventType The type of security event
     * @param details Event details
     */
    void notifySecurityEvent(String eventType, String details);

    /**
     * Records audit trail for compliance purposes.
     *
     * @param action The action performed
     * @param resourceId The ID of the affected resource
     * @param userId The ID of the user performing the action
     * @param details Additional audit details
     */
    void recordAuditTrail(String action, String resourceId, String userId, String details);
}
