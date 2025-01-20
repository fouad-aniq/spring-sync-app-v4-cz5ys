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

/**
 * Domain entity representing file metadata.
 * This entity contains all the core information about a file's metadata.
 */
@Data
@Builder
public class DomainFileMetadataEntity {

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
     * @return DomainFileMetadataEntity
     */
    public static DomainFileMetadataEntity fromSharedRequest(SharedFileMetadataRequest request) {
        return DomainFileMetadataEntity.builder()
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
     * Creates a new metadata entity with default timestamps.
     *
     * @param fileID The file ID
     * @param path The file path
     * @param checksum The file checksum
     * @param ownership The ownership details
     * @return DomainFileMetadataEntity
     */
    public static DomainFileMetadataEntity createNew(
            String fileID,
            String path,
            String checksum,
            DomainValueOwnership ownership) {
        Instant now = Instant.now();
        return DomainFileMetadataEntity.builder()
                .fileID(fileID)
                .path(path)
                .checksum(checksum)
                .creationTimestamp(now)
                .lastModifiedTimestamp(now)
                .ownershipDetails(ownership)
                .currentVersionNumber(1)
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

    /**
     * Updates the metadata with new information while preserving creation details.
     *
     * @param newPath New file path
     * @param newChecksum New checksum
     * @param newOwnership New ownership details
     */
    public void update(String newPath, String newChecksum, DomainValueOwnership newOwnership) {
        this.path = newPath;
        this.checksum = newChecksum;
        this.ownershipDetails = newOwnership;
        this.lastModifiedTimestamp = Instant.now();
        this.currentVersionNumber++;
    }
}
