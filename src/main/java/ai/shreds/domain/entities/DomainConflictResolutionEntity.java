package ai.shreds.domain.entities;

import ai.shreds.shared.dtos.SharedConflictResolutionResponse;
import ai.shreds.shared.enums.SharedEnumResolutionStrategy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Domain entity representing a conflict resolution record.
 */
@Data
@Builder
public class DomainConflictResolutionEntity {

    @NotBlank(message = "Conflict resolution ID cannot be blank")
    private String id;

    @NotBlank(message = "FileID cannot be blank")
    private String fileID;

    @NotEmpty(message = "Conflicting version IDs cannot be empty")
    private List<String> conflictingVersionIDs;

    @NotNull(message = "Resolution strategy cannot be null")
    private SharedEnumResolutionStrategy resolutionStrategy;

    @NotNull(message = "Resolution timestamp cannot be null")
    private Instant resolutionTimestamp;

    private boolean resolved;

    /**
     * Converts this domain entity to a shared response DTO.
     *
     * @return SharedConflictResolutionResponse
     */
    public SharedConflictResolutionResponse toSharedConflictResolutionResponse() {
        return SharedConflictResolutionResponse.builder()
                .fileID(this.fileID)
                .conflictResolved(this.resolved)
                .resolutionStrategy(this.resolutionStrategy)
                .build();
    }

    /**
     * Creates a new conflict resolution entity.
     *
     * @param fileID The ID of the file with conflicts
     * @param conflictingVersionIDs List of conflicting version IDs
     * @param strategy The resolution strategy to apply
     * @return DomainConflictResolutionEntity
     */
    public static DomainConflictResolutionEntity createNew(
            String fileID,
            List<String> conflictingVersionIDs,
            SharedEnumResolutionStrategy strategy) {
        return DomainConflictResolutionEntity.builder()
                .id(java.util.UUID.randomUUID().toString())
                .fileID(fileID)
                .conflictingVersionIDs(conflictingVersionIDs)
                .resolutionStrategy(strategy)
                .resolutionTimestamp(Instant.now())
                .resolved(false)
                .build();
    }

    /**
     * Marks this conflict as resolved.
     */
    public void markAsResolved() {
        this.resolved = true;
        this.resolutionTimestamp = Instant.now();
    }

    /**
     * Validates if this conflict resolution entity is valid.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.trim().isEmpty()
                && fileID != null && !fileID.trim().isEmpty()
                && conflictingVersionIDs != null && !conflictingVersionIDs.isEmpty()
                && resolutionStrategy != null
                && resolutionTimestamp != null;
    }

    /**
     * Checks if this conflict involves a specific version.
     *
     * @param versionId The version ID to check
     * @return true if the version is involved in this conflict
     */
    public boolean involvesVersion(String versionId) {
        return conflictingVersionIDs.contains(versionId);
    }

    /**
     * Gets the number of versions involved in this conflict.
     *
     * @return The number of conflicting versions
     */
    public int getConflictingVersionCount() {
        return conflictingVersionIDs.size();
    }

    /**
     * Updates the resolution strategy.
     *
     * @param newStrategy The new resolution strategy
     */
    public void updateStrategy(SharedEnumResolutionStrategy newStrategy) {
        this.resolutionStrategy = newStrategy;
        this.resolutionTimestamp = Instant.now();
        this.resolved = false;
    }
}
