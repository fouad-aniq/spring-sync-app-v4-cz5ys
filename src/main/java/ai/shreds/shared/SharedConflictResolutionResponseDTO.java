package ai.shreds.shared.dtos;

import ai.shreds.shared.enums.SharedResolutionStrategyEnum;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO containing the result of a conflict resolution operation")
public class SharedConflictResolutionResponseDTO {
    @NotBlank(message = "File ID cannot be empty")
    @Schema(description = "ID of the file involved in the conflict",
           example = "123e4567-e89b-12d3-a456-426614174000")
    private String fileID;

    @NotNull(message = "Conflict resolution status must be specified")
    @Schema(description = "Indicates whether the conflict was successfully resolved")
    private boolean conflictResolved;

    @NotNull(message = "Resolution strategy must be specified")
    @Schema(description = "Strategy used to resolve the conflict",
           example = "LAST_MODIFIED")
    private SharedResolutionStrategyEnum resolutionStrategy;

    @Schema(description = "Resulting version after conflict resolution",
           example = "v3-789")
    private String resultingVersionId;

    @Schema(description = "Timestamp when the conflict was resolved",
           example = "2023-01-01T10:00:00Z")
    private String resolutionTimestamp;

    @Schema(description = "Additional details about the resolution process",
           example = "{\"mergeDetails\": \"Automated merge successful\", \"conflictedFields\": [\"content\", \"metadata\"]}")
    private String resolutionDetails;

    @Schema(description = "User who performed the resolution",
           example = "john.doe")
    private String resolvedBy;

    @Schema(description = "List of versions that were involved in the conflict",
           example = "[\"v1-123\", \"v2-456\"]")
    private List<String> conflictingVersions;

    @Schema(description = "Name of the branch where the resolution was created",
           example = "resolution-branch-001")
    private String resolutionBranch;

    @Schema(description = "Whether the resolution was automated or manual",
           example = "true")
    private boolean automatedResolution;

    @Schema(description = "Time taken to resolve the conflict in milliseconds",
           example = "1500")
    private Long resolutionTimeMs;

    @Schema(description = "Map of field-specific resolution outcomes",
           example = "{\"metadata\": \"MERGED\", \"content\": \"KEPT_LATEST\"}")
    private Map<String, String> fieldResolutions;

    @Schema(description = "List of any warnings generated during resolution",
           example = "[\"Some metadata fields could not be merged\"]")
    private List<String> warnings;

    public static SharedConflictResolutionResponseDTO success(
            String fileID,
            SharedResolutionStrategyEnum strategy,
            String resultingVersionId,
            String resolvedBy,
            List<String> conflictingVersions) {
        return SharedConflictResolutionResponseDTO.builder()
                .fileID(fileID)
                .conflictResolved(true)
                .resolutionStrategy(strategy)
                .resultingVersionId(resultingVersionId)
                .resolutionTimestamp(Instant.now().toString())
                .resolvedBy(resolvedBy)
                .conflictingVersions(conflictingVersions)
                .automatedResolution(strategy != SharedResolutionStrategyEnum.MANUAL)
                .build();
    }

    public static SharedConflictResolutionResponseDTO failure(
            String fileID,
            String details,
            List<String> conflictingVersions) {
        return SharedConflictResolutionResponseDTO.builder()
                .fileID(fileID)
                .conflictResolved(false)
                .resolutionDetails(details)
                .resolutionTimestamp(Instant.now().toString())
                .conflictingVersions(conflictingVersions)
                .build();
    }

    public static SharedConflictResolutionResponseDTO partialSuccess(
            String fileID,
            SharedResolutionStrategyEnum strategy,
            String resultingVersionId,
            List<String> warnings) {
        return SharedConflictResolutionResponseDTO.builder()
                .fileID(fileID)
                .conflictResolved(true)
                .resolutionStrategy(strategy)
                .resultingVersionId(resultingVersionId)
                .resolutionTimestamp(Instant.now().toString())
                .warnings(warnings)
                .build();
    }

    public SharedConflictResolutionResponseDTO withResolutionTime(long startTimeMs) {
        this.resolutionTimeMs = System.currentTimeMillis() - startTimeMs;
        return this;
    }

    public SharedConflictResolutionResponseDTO withFieldResolutions(Map<String, String> resolutions) {
        this.fieldResolutions = resolutions;
        return this;
    }

    public SharedConflictResolutionResponseDTO withBranch(String branchName) {
        this.resolutionBranch = branchName;
        return this;
    }
}
