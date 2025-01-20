package ai.shreds.application.services;

import ai.shreds.application.exceptions.ApplicationMetadataException;
import ai.shreds.application.ports.ApplicationInputPortFileMetadata;
import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.domain.services.DomainServiceMetadata;
import ai.shreds.domain.services.DomainServiceVersion;
import ai.shreds.shared.dtos.SharedFileMetadataRequest;
import ai.shreds.shared.dtos.SharedCreateUpdateMetadataResponse;
import ai.shreds.shared.dtos.SharedFileMetadataResponse;
import ai.shreds.shared.dtos.SharedVersionRecordResponse;
import ai.shreds.shared.enums.SharedEnumMetadataStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ApplicationServiceFileMetadata implements ApplicationInputPortFileMetadata {

    private final DomainServiceMetadata domainServiceMetadata;
    private final DomainServiceVersion domainServiceVersion;

    @Override
    public SharedCreateUpdateMetadataResponse createOrUpdateMetadata(@Valid SharedFileMetadataRequest request) {
        try {
            log.debug("Processing metadata request for fileID: {}", request.getFileID());
            
            // Convert request to domain entity
            DomainEntityFileMetadata domainEntity = DomainEntityFileMetadata.fromSharedRequest(request);
            
            // Check if metadata already exists
            boolean isUpdate = false;
            try {
                domainServiceMetadata.getMetadata(request.getFileID());
                isUpdate = true;
            } catch (Exception e) {
                log.debug("No existing metadata found for fileID: {}", request.getFileID());
            }

            // Create or update metadata
            DomainEntityFileMetadata savedEntity = domainServiceMetadata.createOrUpdateMetadata(domainEntity);
            
            // Create response
            return SharedCreateUpdateMetadataResponse.builder()
                    .fileID(savedEntity.getFileID())
                    .metadata(savedEntity.toSharedMetadataResponse())
                    .status(isUpdate ? SharedEnumMetadataStatus.UPDATED : SharedEnumMetadataStatus.CREATED)
                    .build();

        } catch (Exception e) {
            log.error("Error processing metadata request for fileID: {}", request.getFileID(), e);
            throw new ApplicationMetadataException(
                    "Failed to create or update metadata: " + e.getMessage(),
                    "APP_META_001",
                    e
            );
        }
    }

    @Override
    public SharedFileMetadataResponse retrieveMetadata(String fileID) {
        try {
            log.debug("Retrieving metadata for fileID: {}", fileID);
            DomainEntityFileMetadata domainEntity = domainServiceMetadata.getMetadata(fileID);
            return domainEntity.toSharedMetadataResponse();

        } catch (Exception e) {
            log.error("Error retrieving metadata for fileID: {}", fileID, e);
            throw new ApplicationMetadataException(
                    "Failed to retrieve metadata: " + e.getMessage(),
                    "APP_META_002",
                    e
            );
        }
    }

    @Override
    public List<SharedVersionRecordResponse> retrieveVersionHistory(String fileID) {
        try {
            log.debug("Retrieving version history for fileID: {}", fileID);
            return domainServiceVersion.getVersionHistory(fileID).stream()
                    .map(DomainEntityVersionRecord::toSharedVersionRecordResponse)
                    .toList();

        } catch (Exception e) {
            log.error("Error retrieving version history for fileID: {}", fileID, e);
            throw new ApplicationMetadataException(
                    "Failed to retrieve version history: " + e.getMessage(),
                    "APP_META_003",
                    e
            );
        }
    }
}
