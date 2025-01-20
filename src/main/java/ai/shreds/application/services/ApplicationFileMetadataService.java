package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationFileMetadataUseCasePort;
import ai.shreds.application.ports.ApplicationMetadataOutputPort;
import ai.shreds.application.mappers.ApplicationMetadataMapper;
import ai.shreds.application.exceptions.*;
import ai.shreds.domain.services.DomainMetadataService;
import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.shared.dtos.*;
import ai.shreds.shared.enums.SharedMetadataStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationFileMetadataService implements ApplicationFileMetadataUseCasePort {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFileMetadataService.class);

    private final ApplicationMetadataOutputPort metadataOutputPort;
    private final ApplicationVersionService versionService;
    private final ApplicationConflictResolutionService conflictResolutionService;
    private final DomainMetadataService domainMetadataService;
    private final ApplicationMetadataMapper mapper;

    public ApplicationFileMetadataService(
            ApplicationMetadataOutputPort metadataOutputPort,
            ApplicationVersionService versionService,
            ApplicationConflictResolutionService conflictResolutionService,
            DomainMetadataService domainMetadataService,
            ApplicationMetadataMapper mapper) {
        this.metadataOutputPort = metadataOutputPort;
        this.versionService = versionService;
        this.conflictResolutionService = conflictResolutionService;
        this.domainMetadataService = domainMetadataService;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public SharedCreateUpdateMetadataResponseDTO createOrUpdateMetadata(SharedFileMetadataCreateUpdateRequestDTO request)
            throws ApplicationValidationException, ApplicationNotFoundException, ApplicationConflictException {
        long startTime = System.currentTimeMillis();
        try {
            logger.debug("Processing metadata create/update request for fileID: {}", request.getFileID());

            // Convert DTO to domain entity
            DomainEntityFileMetadata domainMetadata = mapper.toDomainEntity(request);

            // Determine if this is a create or update operation
            boolean isUpdate = request.getFileID() != null;
            DomainEntityFileMetadata savedMetadata;

            if (isUpdate) {
                savedMetadata = domainMetadataService.updateMetadata(domainMetadata);
                versionService.createVersion(mapper.toDTO(savedMetadata));
            } else {
                savedMetadata = domainMetadataService.createMetadata(domainMetadata);
            }

            // Convert result back to DTO
            SharedFileMetadataDTO metadataDTO = mapper.toDTO(savedMetadata);

            // Notify about the change
            metadataOutputPort.notifyMetadataChange(metadataDTO);

            // Record metrics
            metadataOutputPort.recordMetrics(
                isUpdate ? "UPDATE_METADATA" : "CREATE_METADATA",
                System.currentTimeMillis() - startTime
            );

            // Create response
            return SharedCreateUpdateMetadataResponseDTO.builder()
                    .fileID(savedMetadata.getFileID())
                    .metadata(metadataDTO)
                    .status(isUpdate ? SharedMetadataStatusEnum.UPDATED : SharedMetadataStatusEnum.CREATED)
                    .build();

        } catch (Exception e) {
            logger.error("Error processing metadata request", e);
            metadataOutputPort.reportError(e);
            throw e;
        }
    }

    @Override
    public SharedFileMetadataDTO retrieveMetadata(String fileId)
            throws ApplicationValidationException, ApplicationNotFoundException {
        long startTime = System.currentTimeMillis();
        try {
            logger.debug("Retrieving metadata for fileID: {}", fileId);

            DomainEntityFileMetadata metadata = domainMetadataService.getMetadata(fileId);
            SharedFileMetadataDTO dto = mapper.toDTO(metadata);

            metadataOutputPort.recordMetrics("RETRIEVE_METADATA", System.currentTimeMillis() - startTime);
            return dto;

        } catch (Exception e) {
            logger.error("Error retrieving metadata for fileID: {}", fileId, e);
            metadataOutputPort.reportError(e);
            throw e;
        }
    }

    @Override
    public List<SharedVersionRecordDTO> retrieveVersionHistory(String fileId)
            throws ApplicationValidationException, ApplicationNotFoundException {
        long startTime = System.currentTimeMillis();
        try {
            logger.debug("Retrieving version history for fileID: {}", fileId);

            List<SharedVersionRecordDTO> versions = versionService.getVersionHistory(fileId);

            metadataOutputPort.recordMetrics("RETRIEVE_VERSION_HISTORY", System.currentTimeMillis() - startTime);
            return versions;

        } catch (Exception e) {
            logger.error("Error retrieving version history for fileID: {}", fileId, e);
            metadataOutputPort.reportError(e);
            throw e;
        }
    }

    @Override
    @Transactional
    public SharedConflictResolutionResponseDTO resolveConflict(String fileId, SharedConflictResolutionRequestDTO request)
            throws ApplicationValidationException, ApplicationNotFoundException, ApplicationConflictException {
        long startTime = System.currentTimeMillis();
        try {
            logger.debug("Resolving conflict for fileID: {} with strategy: {}", fileId, request.getResolutionStrategy());

            SharedConflictResolutionResponseDTO resolution = conflictResolutionService.resolveConflict(fileId, request);

            // Notify about the resolution
            metadataOutputPort.notifyConflictResolution(resolution);

            // Record metrics and audit
            metadataOutputPort.recordMetrics("RESOLVE_CONFLICT", System.currentTimeMillis() - startTime);
            metadataOutputPort.recordAuditTrail(
                "CONFLICT_RESOLUTION",
                fileId,
                request.getRequestedBy(),
                "Strategy: " + request.getResolutionStrategy()
            );

            return resolution;

        } catch (Exception e) {
            logger.error("Error resolving conflict for fileID: {}", fileId, e);
            metadataOutputPort.reportError(e);
            throw e;
        }
    }
}
