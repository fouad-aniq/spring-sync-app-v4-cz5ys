package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedConflictResolutionRequest;
import ai.shreds.shared.dtos.SharedConflictResolutionResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Application port for handling file version conflicts.
 * This port defines operations for resolving conflicts between different versions of a file.
 */
public interface ApplicationConflictResolutionInputPort {

    /**
     * Resolves conflicts between different versions of a file.
     *
     * @param fileID The unique identifier of the file with conflicts
     * @param request The conflict resolution request containing strategy and conflicting version IDs
     * @return Response indicating the result of the conflict resolution
     * @throws ai.shreds.application.exceptions.ApplicationMetadataException if the resolution fails or if
     *         the file or versions are not found
     */
    SharedConflictResolutionResponse resolveConflict(
            @NotBlank(message = "FileID cannot be blank") String fileID,
            @Valid SharedConflictResolutionRequest request);
}
