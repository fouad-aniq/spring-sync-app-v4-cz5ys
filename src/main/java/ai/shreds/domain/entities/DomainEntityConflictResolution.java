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

@Data
@Builder
public class DomainEntityConflictResolution {

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
     * @return DomainEntityConflictResolution
     */
    public static DomainEntityConflictResolution createNew(
            String fileID,
            List<String> conflictingVersionIDs,
            SharedEnumResolutionStrategy strategy) {
        return DomainEntityConflictResolution.builder()
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
}
