package ai.shreds.shared;

import ai.shreds.shared.enums.SharedEnumResolutionStrategy;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for conflict resolution response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedConflictResolutionResponse {

    @NotBlank(message = "FileID cannot be blank")
    private String fileID;

    @NotNull(message = "Conflict resolution status cannot be null")
    private boolean conflictResolved;

    @NotNull(message = "Resolution strategy cannot be null")
    private SharedEnumResolutionStrategy resolutionStrategy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String resolutionTimestamp;

    private String resolvedVersionId;
    private String resolutionDetails;

    /**
     * Checks if this resolution requires manual intervention.
     *
     * @return true if manual intervention is required
     */
    public boolean requiresManualIntervention() {
        return resolutionStrategy == SharedEnumResolutionStrategy.MANUAL_MERGE;
    }

    /**
     * Checks if all versions were preserved.
     *
     * @return true if all versions were kept
     */
    public boolean keptAllVersions() {
        return resolutionStrategy == SharedEnumResolutionStrategy.KEEP_BOTH;
    }

    /**
     * Creates a builder pre-populated with this response's values.
     *
     * @return A builder initialized with current values
     */
    public SharedConflictResolutionResponseBuilder toBuilder() {
        return builder()
                .fileID(this.fileID)
                .conflictResolved(this.conflictResolved)
                .resolutionStrategy(this.resolutionStrategy)
                .resolutionTimestamp(this.resolutionTimestamp)
                .resolvedVersionId(this.resolvedVersionId)
                .resolutionDetails(this.resolutionDetails);
    }

    /**
     * Creates a copy of this response marked as resolved.
     *
     * @param resolvedVersion The ID of the resolved version
     * @return A new response marked as resolved
     */
    public SharedConflictResolutionResponse markResolved(String resolvedVersion) {
        return this.toBuilder()
                .conflictResolved(true)
                .resolvedVersionId(resolvedVersion)
                .resolutionTimestamp(java.time.Instant.now().toString())
                .build();
    }

    /**
     * Creates a copy of this response with resolution details.
     *
     * @param details The resolution details
     * @return A new response with updated details
     */
    public SharedConflictResolutionResponse withDetails(String details) {
        return this.toBuilder()
                .resolutionDetails(details)
                .build();
    }

    /**
     * Creates a copy of this response with a new strategy.
     *
     * @param newStrategy The new resolution strategy
     * @return A new response with updated strategy
     */
    public SharedConflictResolutionResponse withStrategy(SharedEnumResolutionStrategy newStrategy) {
        return this.toBuilder()
                .resolutionStrategy(newStrategy)
                .conflictResolved(false) // Reset resolution status when strategy changes
                .resolvedVersionId(null)
                .resolutionTimestamp(java.time.Instant.now().toString())
                .build();
    }

    /**
     * Creates a failure response.
     *
     * @param fileID The file ID
     * @param strategy The attempted resolution strategy
     * @param details The failure details
     * @return A new response indicating failure
     */
    public static SharedConflictResolutionResponse createFailureResponse(
            String fileID,
            SharedEnumResolutionStrategy strategy,
            String details) {
        return builder()
                .fileID(fileID)
                .conflictResolved(false)
                .resolutionStrategy(strategy)
                .resolutionTimestamp(java.time.Instant.now().toString())
                .resolutionDetails(details)
                .build();
    }
}
