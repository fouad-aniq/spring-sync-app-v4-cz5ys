package ai.shreds.application.ports;

import ai.shreds.shared.dtos.*;
import ai.shreds.application.exceptions.*;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Port defining the primary operations for file metadata management.
 * Implements the use cases for creating, updating, retrieving metadata and handling conflicts.
 */
public interface ApplicationFileMetadataUseCasePort {

    /**
     * Creates new metadata for a file or updates existing metadata.
     *
     * @param request The metadata creation/update request
     * @return Response containing the created/updated metadata
     * @throws ApplicationValidationException if the request data is invalid
     * @throws ApplicationNotFoundException if the file to update doesn't exist
     * @throws ApplicationConflictException if a version conflict is detected
     */
    @Operation(summary = "Create or update file metadata")
    SharedCreateUpdateMetadataResponseDTO createOrUpdateMetadata(@Valid SharedFileMetadataCreateUpdateRequestDTO request)
        throws ApplicationValidationException, ApplicationNotFoundException, ApplicationConflictException;

    /**
     * Retrieves metadata for a specific file.
     *
     * @param fileId The ID of the file
     * @return The file's metadata
     * @throws ApplicationValidationException if the file ID is invalid
     * @throws ApplicationNotFoundException if the file doesn't exist
     */
    @Operation(summary = "Retrieve file metadata")
    SharedFileMetadataDTO retrieveMetadata(@NotBlank String fileId)
        throws ApplicationValidationException, ApplicationNotFoundException;

    /**
     * Retrieves the version history for a specific file.
     *
     * @param fileId The ID of the file
     * @return List of version records
     * @throws ApplicationValidationException if the file ID is invalid
     * @throws ApplicationNotFoundException if the file doesn't exist
     */
    @Operation(summary = "Retrieve version history")
    List<SharedVersionRecordDTO> retrieveVersionHistory(@NotBlank String fileId)
        throws ApplicationValidationException, ApplicationNotFoundException;

    /**
     * Resolves conflicts between different versions of a file.
     *
     * @param fileId The ID of the file
     * @param request The conflict resolution request
     * @return Response containing the resolution result
     * @throws ApplicationValidationException if the request data is invalid
     * @throws ApplicationNotFoundException if the file or versions don't exist
     * @throws ApplicationConflictException if the conflict cannot be resolved
     */
    @Operation(summary = "Resolve version conflict")
    SharedConflictResolutionResponseDTO resolveConflict(
            @NotBlank String fileId,
            @Valid SharedConflictResolutionRequestDTO request)
        throws ApplicationValidationException, ApplicationNotFoundException, ApplicationConflictException;
}
