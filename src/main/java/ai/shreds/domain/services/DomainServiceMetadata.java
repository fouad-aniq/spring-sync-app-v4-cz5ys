package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.domain.exceptions.DomainExceptionMetadata;
import ai.shreds.domain.ports.DomainPortFileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainServiceMetadata {

    private final DomainPortFileMetadataRepository fileMetadataRepository;

    public void validateMetadata(DomainEntityFileMetadata metadata) throws DomainExceptionMetadata {
        if (metadata == null) {
            throw new DomainExceptionMetadata("Metadata cannot be null", "DOMAIN_META_001");
        }
        if (metadata.getFileID() == null || metadata.getFileID().trim().isEmpty()) {
            throw new DomainExceptionMetadata("FileID is required", "DOMAIN_META_002");
        }
        if (metadata.getPath() == null || metadata.getPath().trim().isEmpty()) {
            throw new DomainExceptionMetadata("Path is required", "DOMAIN_META_003");
        }
        if (metadata.getChecksum() == null || metadata.getChecksum().trim().isEmpty()) {
            throw new DomainExceptionMetadata("Checksum is required", "DOMAIN_META_004");
        }
        if (metadata.getOwnershipDetails() == null) {
            throw new DomainExceptionMetadata("Ownership details are required", "DOMAIN_META_005");
        }
        if (metadata.getCurrentVersionNumber() < 1) {
            throw new DomainExceptionMetadata("Version number must be at least 1", "DOMAIN_META_006");
        }
    }

    public void applyBusinessRulesForMetadata(DomainEntityFileMetadata metadata) {
        // Set creation timestamp if new metadata
        if (metadata.getCreationTimestamp() == null) {
            metadata.setCreationTimestamp(Instant.now());
        }
        
        // Always update last modified timestamp
        metadata.setLastModifiedTimestamp(Instant.now());

        // Check if this is an update to existing metadata
        DomainEntityFileMetadata existingMetadata = fileMetadataRepository.findById(metadata.getFileID());
        if (existingMetadata != null) {
            // Ensure version number is incremented
            if (metadata.getCurrentVersionNumber() <= existingMetadata.getCurrentVersionNumber()) {
                metadata.setCurrentVersionNumber(existingMetadata.getCurrentVersionNumber() + 1);
            }
            
            // Preserve original creation timestamp
            metadata.setCreationTimestamp(existingMetadata.getCreationTimestamp());

            // Validate checksum change
            if (!Objects.equals(metadata.getChecksum(), existingMetadata.getChecksum())) {
                log.info("Checksum changed for fileID: {}. Old: {}, New: {}", 
                    metadata.getFileID(), existingMetadata.getChecksum(), metadata.getChecksum());
            }
        }
    }

    public DomainEntityFileMetadata createOrUpdateMetadata(DomainEntityFileMetadata metadata) {
        log.debug("Processing metadata for fileID: {}", metadata.getFileID());
        validateMetadata(metadata);
        applyBusinessRulesForMetadata(metadata);
        
        DomainEntityFileMetadata savedMetadata = fileMetadataRepository.save(metadata);
        log.info("Metadata {} for fileID: {}", 
            (metadata.getCreationTimestamp() == null ? "created" : "updated"), 
            metadata.getFileID());
        
        return savedMetadata;
    }

    public DomainEntityFileMetadata getMetadata(String fileID) {
        log.debug("Retrieving metadata for fileID: {}", fileID);
        DomainEntityFileMetadata metadata = fileMetadataRepository.findById(fileID);
        if (metadata == null) {
            log.error("Metadata not found for fileID: {}", fileID);
            throw new DomainExceptionMetadata("Metadata not found for fileID: " + fileID, "DOMAIN_META_404");
        }
        return metadata;
    }
}
