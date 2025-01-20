package ai.shreds.shared;

import ai.shreds.shared.enums.SharedEnumResolutionStrategy;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for conflict resolution requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedConflictResolutionRequest {

    @NotEmpty(message = "Conflicting version IDs list cannot be empty")
    @Size(min = 2, message = "At least two conflicting versions are required")
    private List<String> conflictingVersionIDs;

    @NotNull(message = "Resolution strategy cannot be null")
    private SharedEnumResolutionStrategy resolutionStrategy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String requestTimestamp;

    private String requestDetails;

    /**
     * Checks if this request requires manual intervention.
     *
     * @return true if manual intervention is required
     */
    public boolean requiresManualIntervention() {
        return resolutionStrategy == SharedEnumResolutionStrategy.MANUAL_MERGE;
    }

    /**
     * Checks if this request will preserve all versions.
     *
     * @return true if all versions will be kept
     */
    public boolean willKeepAllVersions() {
        return resolutionStrategy == SharedEnumResolutionStrategy.KEEP_BOTH;
    }

    /**
     * Creates a builder pre-populated with this request's values.
     *
     * @return A builder initialized with current values
     */
    public SharedConflictResolutionRequestBuilder toBuilder() {
        return builder()
                .conflictingVersionIDs(this.conflictingVersionIDs)
                .resolutionStrategy(this.resolutionStrategy)
                .requestTimestamp(this.requestTimestamp)
                .requestDetails(this.requestDetails);
    }

    /**
     * Creates a copy of this request with a new strategy.
     *
     * @param newStrategy The new resolution strategy
     * @return A new request with updated strategy
     */
    public SharedConflictResolutionRequest withStrategy(SharedEnumResolutionStrategy newStrategy) {
        return this.toBuilder()
                .resolutionStrategy(newStrategy)
                .requestTimestamp(java.time.Instant.now().toString())
                .build();
    }

    /**
     * Creates a copy of this request with additional conflicting versions.
     *
     * @param additionalVersions Additional version IDs to include
     * @return A new request with updated version list
     */
    public SharedConflictResolutionRequest withAdditionalVersions(List<String> additionalVersions) {
        List<String> newList = new java.util.ArrayList<>(this.conflictingVersionIDs);
        newList.addAll(additionalVersions);
        return this.toBuilder()
                .conflictingVersionIDs(newList)
                .requestTimestamp(java.time.Instant.now().toString())
                .build();
    }

    /**
     * Creates a copy of this request with additional details.
     *
     * @param details Additional request details
     * @return A new request with updated details
     */
    public SharedConflictResolutionRequest withDetails(String details) {
        return this.toBuilder()
                .requestDetails(details)
                .build();
    }

    /**
     * Validates that the request is properly configured for its strategy.
     *
     * @return true if the request is valid for its strategy
     */
    public boolean isValidForStrategy() {
        return switch (resolutionStrategy) {
            case MANUAL_MERGE -> conflictingVersionIDs.size() == 2;
            case KEEP_BOTH -> conflictingVersionIDs.size() >= 2;
            default -> !conflictingVersionIDs.isEmpty();
        };
    }

    /**
     * Creates a new request with current timestamp.
     *
     * @param versions The conflicting version IDs
     * @param strategy The resolution strategy
     * @return A new conflict resolution request
     */
    public static SharedConflictResolutionRequest createNew(
            List<String> versions,
            SharedEnumResolutionStrategy strategy) {
        return builder()
                .conflictingVersionIDs(versions)
                .resolutionStrategy(strategy)
                .requestTimestamp(java.time.Instant.now().toString())
                .build();
    }
}
