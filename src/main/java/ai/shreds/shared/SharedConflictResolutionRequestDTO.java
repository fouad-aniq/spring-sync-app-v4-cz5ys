package ai.shreds.shared.dtos;

import ai.shreds.shared.enums.SharedResolutionStrategyEnum;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for requesting conflict resolution between file versions")
public class SharedConflictResolutionRequestDTO {
    @NotEmpty(message = "Conflicting version IDs cannot be empty")
    @Size(min = 2, message = "At least two conflicting versions are required")
    @Schema(description = "List of version IDs involved in the conflict", 
           example = "[\"v1-123\", \"v2-456\"]")
    private List<String> conflictingVersionIDs;

    @NotNull(message = "Resolution strategy must be specified")
    @Schema(description = "Strategy to be used for resolving the conflict", 
           example = "LAST_MODIFIED")
    private SharedResolutionStrategyEnum resolutionStrategy;

    @Schema(description = "Additional parameters for conflict resolution", 
           example = "{\"preferredBranchName\": \"main\", \"mergePolicy\": \"strict\"}")
    private String resolutionParameters;

    @Schema(description = "User requesting the conflict resolution", 
           example = "john.doe")
    private String requestedBy;

    @Schema(description = "Priority order for resolving conflicts", 
           example = "[\"v2-456\", \"v1-123\"]")
    private List<String> priorityOrder;

    @Schema(description = "Whether to create a new branch for the resolution", 
           example = "true")
    private boolean createNewBranch;

    @Schema(description = "Name for the new branch if created", 
           example = "resolution-branch-001")
    private String newBranchName;

    @Schema(description = "Custom merge rules for specific fields", 
           example = "{\"metadata\": \"KEEP_BOTH\", \"content\": \"KEEP_LATEST\"}")
    private Map<String, String> fieldMergeRules;

    @Schema(description = "Whether to preserve history of conflicting versions", 
           example = "true")
    private boolean preserveHistory;

    @Schema(description = "Justification for the chosen resolution strategy", 
           example = "Preserving latest changes as per team decision")
    private String resolutionJustification;

    public static SharedConflictResolutionRequestDTO createAutoResolution(List<String> versionIds) {
        return SharedConflictResolutionRequestDTO.builder()
                .conflictingVersionIDs(versionIds)
                .resolutionStrategy(SharedResolutionStrategyEnum.LAST_MODIFIED)
                .preserveHistory(true)
                .createNewBranch(false)
                .build();
    }

    public static SharedConflictResolutionRequestDTO createManualResolution(
            List<String> versionIds, 
            String requestedBy, 
            String justification) {
        return SharedConflictResolutionRequestDTO.builder()
                .conflictingVersionIDs(versionIds)
                .resolutionStrategy(SharedResolutionStrategyEnum.MANUAL)
                .requestedBy(requestedBy)
                .resolutionJustification(justification)
                .preserveHistory(true)
                .createNewBranch(true)
                .build();
    }

    public static SharedConflictResolutionRequestDTO createMergeResolution(
            List<String> versionIds, 
            String requestedBy, 
            Map<String, String> mergeRules) {
        return SharedConflictResolutionRequestDTO.builder()
                .conflictingVersionIDs(versionIds)
                .resolutionStrategy(SharedResolutionStrategyEnum.MERGE)
                .requestedBy(requestedBy)
                .fieldMergeRules(mergeRules)
                .preserveHistory(true)
                .createNewBranch(true)
                .build();
    }

    public boolean requiresManualIntervention() {
        return resolutionStrategy == SharedResolutionStrategyEnum.MANUAL ||
               (resolutionStrategy == SharedResolutionStrategyEnum.MERGE && 
                (fieldMergeRules == null || fieldMergeRules.isEmpty()));
    }

    public boolean isValidStrategy() {
        return resolutionStrategy != null && 
               (resolutionStrategy != SharedResolutionStrategyEnum.MANUAL || requestedBy != null);
    }
}
