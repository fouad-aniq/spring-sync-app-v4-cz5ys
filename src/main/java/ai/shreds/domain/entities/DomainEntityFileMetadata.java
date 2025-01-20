package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainValueOwnership;
import ai.shreds.shared.dtos.SharedFileMetadataRequest;
import ai.shreds.shared.dtos.SharedFileMetadataResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DomainEntityFileMetadata {

    @NotBlank(message = "FileID cannot be blank")
    private String fileID;

    @NotBlank(message = "Path cannot be blank")
    private String path;

    @NotBlank(message = "Checksum cannot be blank")
    private String checksum;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant creationTimestamp;

    @NotNull(message = "Last modified timestamp cannot be null")
    private Instant lastModifiedTimestamp;

    @NotNull(message = "Ownership details cannot be null")
    private DomainValueOwnership ownershipDetails;

    @Min(value = 1, message = "Version number must be at least 1")
    private int currentVersionNumber;

    /**
     * Converts this domain entity to a shared response DTO.
     *
     * @return SharedFileMetadataResponse
     */
    public SharedFileMetadataResponse toSharedMetadataResponse() {
        return SharedFileMetadataResponse.builder()
                .fileID(this.fileID)
                .path(this.path)
                .checksum(this.checksum)
                .creationTimestamp(this.creationTimestamp.toString())
                .lastModifiedTimestamp(this.lastModifiedTimestamp.toString())
                .ownershipDetails(this.ownershipDetails.toJson())
                .currentVersionNumber(this.currentVersionNumber)
                .build();
    }

    /**
     * Creates a domain entity from a shared request DTO.
     *
     * @param request The shared request DTO
     * @return DomainEntityFileMetadata
     */
    public static DomainEntityFileMetadata fromSharedRequest(SharedFileMetadataRequest request) {
        return DomainEntityFileMetadata.builder()
                .fileID(request.getFileID())
                .path(request.getPath())
                .checksum(request.getChecksum())
                .creationTimestamp(request.getCreationTimestamp() != null ? 
                    Instant.parse(request.getCreationTimestamp()) : null)
                .lastModifiedTimestamp(request.getLastModifiedTimestamp() != null ? 
                    Instant.parse(request.getLastModifiedTimestamp()) : null)
                .ownershipDetails(DomainValueOwnership.fromJson(request.getOwnershipDetails()))
                .currentVersionNumber(request.getCurrentVersionNumber())
                .build();
    }

    /**
     * Validates if this metadata entity is valid.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return fileID != null && !fileID.trim().isEmpty()
                && path != null && !path.trim().isEmpty()
                && checksum != null && !checksum.trim().isEmpty()
                && creationTimestamp != null
                && lastModifiedTimestamp != null
                && ownershipDetails != null && ownershipDetails.isValid()
                && currentVersionNumber >= 1;
    }
}
