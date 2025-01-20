package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedCreateUpdateMetadataResponse;
import ai.shreds.shared.dtos.SharedFileMetadataRequest;
import ai.shreds.shared.dtos.SharedFileMetadataResponse;
import ai.shreds.shared.dtos.SharedVersionRecordResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Application port for file metadata operations.
 * This port defines the primary operations that can be performed on file metadata.
 */
public interface ApplicationFileMetadataInputPort {

    /**
     * Creates new metadata for a file or updates existing metadata.
     *
     * @param request The metadata information to create or update
     * @return Response containing the created/updated metadata and operation status
     * @throws ai.shreds.application.exceptions.ApplicationMetadataException if the operation fails
     */
    SharedCreateUpdateMetadataResponse createOrUpdateMetadata(@Valid SharedFileMetadataRequest request);

    /**
     * Retrieves metadata for a specific file.
     *
     * @param fileID The unique identifier of the file
     * @return The file's metadata
     * @throws ai.shreds.application.exceptions.ApplicationMetadataException if the file is not found
     */
    SharedFileMetadataResponse retrieveMetadata(@NotBlank(message = "FileID cannot be blank") String fileID);

    /**
     * Retrieves the version history for a specific file.
     *
     * @param fileID The unique identifier of the file
     * @return List of version records for the file
     * @throws ai.shreds.application.exceptions.ApplicationMetadataException if the file is not found
     */
    List<SharedVersionRecordResponse> retrieveVersionHistory(@NotBlank(message = "FileID cannot be blank") String fileID);
}
