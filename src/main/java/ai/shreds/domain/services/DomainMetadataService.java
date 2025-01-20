package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainFileMetadataEntity;
import ai.shreds.domain.exceptions.DomainExceptionMetadata;
import ai.shreds.domain.ports.DomainPortFileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Domain service for handling file metadata operations.
 * This service implements core business logic for metadata management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DomainMetadataService {

    private final DomainPortFileMetadataRepository metadataRepository;

    /**
     * Validates metadata entity according to business rules.
     *
     * @param metadata The metadata entity to validate
     * @throws DomainExceptionMetadata if validation fails
     */
    public void validateMetadata(DomainFileMetadataEntity metadata) {
        log.debug("Validating metadata for fileID: {}", metadata.getFileID());

        if (!metadata.isValid()) {
            throw new DomainExceptionMetadata("Invalid metadata", "META_001");
        }

        // Validate timestamps
        if (metadata.getLastModifiedTimestamp().isBefore(metadata.getCreationTimestamp())) {
            throw new DomainExceptionMetadata(
                    "Last modified timestamp cannot be before creation timestamp",
                    "META_002"
            );
        }

        // Validate path format
        if (!isValidPath(metadata.getPath())) {
            throw new DomainExceptionMetadata(
                    "Invalid path format: " + metadata.getPath(),
                    "META_003"
            );
        }

        log.debug("Metadata validation passed for fileID: {}", metadata.getFileID());
    }

    /**
     * Applies business rules to metadata entity.
     *
     * @param metadata The metadata entity to process
     */
    public void applyBusinessRules(DomainFileMetadataEntity metadata) {
        log.debug("Applying business rules for fileID: {}", metadata.getFileID());

        // Set creation timestamp if new metadata
        if (metadata.getCreationTimestamp() == null) {
            metadata.setCreationTimestamp(Instant.now());
        }

        // Always update last modified timestamp
        metadata.setLastModifiedTimestamp(Instant.now());

        // Check if this is an update to existing metadata
        DomainFileMetadataEntity existingMetadata = metadataRepository.findById(metadata.getFileID());
        if (existingMetadata != null) {
            // Ensure version number is incremented
            if (metadata.getCurrentVersionNumber() <= existingMetadata.getCurrentVersionNumber()) {
                metadata.setCurrentVersionNumber(existingMetadata.getCurrentVersionNumber() + 1);
            }

            // Preserve original creation timestamp
            metadata.setCreationTimestamp(existingMetadata.getCreationTimestamp());

            // Log checksum changes
            if (!existingMetadata.getChecksum().equals(metadata.getChecksum())) {
                log.info("Checksum changed for fileID: {}. Old: {}, New: {}",
                        metadata.getFileID(),
                        existingMetadata.getChecksum(),
                        metadata.getChecksum());
            }
        }

        log.debug("Business rules applied for fileID: {}", metadata.getFileID());
    }

    /**
     * Creates or updates metadata.
     *
     * @param metadata The metadata entity to save
     * @return The saved metadata entity
     */
    public DomainFileMetadataEntity createOrUpdateMetadata(DomainFileMetadataEntity metadata) {
        log.debug("Processing metadata for fileID: {}", metadata.getFileID());

        validateMetadata(metadata);
        applyBusinessRules(metadata);

        DomainFileMetadataEntity savedMetadata = metadataRepository.save(metadata);
        log.info("Metadata {} for fileID: {}",
                metadata.getCreationTimestamp() == null ? "created" : "updated",
                metadata.getFileID());

        return savedMetadata;
    }

    /**
     * Retrieves metadata by file ID.
     *
     * @param fileID The file ID to look up
     * @return The metadata entity
     * @throws DomainExceptionMetadata if metadata is not found
     */
    public DomainFileMetadataEntity getMetadata(String fileID) {
        log.debug("Retrieving metadata for fileID: {}", fileID);

        DomainFileMetadataEntity metadata = metadataRepository.findById(fileID);
        if (metadata == null) {
            log.error("Metadata not found for fileID: {}", fileID);
            throw new DomainExceptionMetadata(
                    "Metadata not found for fileID: " + fileID,
                    "META_004"
            );
        }

        return metadata;
    }

    private boolean isValidPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }

        // Path must start with /
        if (!path.startsWith("/")) {
            return false;
        }

        // Path cannot contain ..
        if (path.contains("..")) {
            return false;
        }

        // Path cannot contain consecutive slashes
        if (path.contains("//")) {
            return false;
        }

        // Path cannot end with /
        if (path.length() > 1 && path.endsWith("/")) {
            return false;
        }

        return true;
    }
}
