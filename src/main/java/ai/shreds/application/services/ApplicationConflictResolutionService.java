package ai.shreds.application.services;

import ai.shreds.domain.services.DomainConflictService;
import ai.shreds.domain.services.DomainVersionService;
import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.entities.DomainEntityConflictResolutionRecord;
import ai.shreds.shared.dtos.SharedConflictResolutionRequestDTO;
import ai.shreds.shared.dtos.SharedConflictResolutionResponseDTO;
import ai.shreds.application.exceptions.ApplicationValidationException;
import ai.shreds.application.exceptions.ApplicationNotFoundException;
import ai.shreds.application.exceptions.ApplicationConflictException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.Instant;

@Service
public class ApplicationConflictResolutionService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConflictResolutionService.class);

    private final DomainConflictService domainConflictService;
    private final DomainVersionService domainVersionService;

    public ApplicationConflictResolutionService(
            DomainConflictService domainConflictService,
            DomainVersionService domainVersionService) {
        this.domainConflictService = domainConflictService;
        this.domainVersionService = domainVersionService;
    }

    @Transactional
    public SharedConflictResolutionResponseDTO resolveConflict(
            String fileId, SharedConflictResolutionRequestDTO request) {
        try {
            logger.debug("Resolving conflict for file {} using strategy {}", 
                    fileId, request.getResolutionStrategy());

            // Validate request
            validateRequest(fileId, request);

            // Get all involved versions
            List<DomainEntityVersionRecord> conflictingVersions = new ArrayList<>();
            for (String versionId : request.getConflictingVersionIDs()) {
                DomainEntityVersionRecord version = domainVersionService.getVersionHistory(fileId).stream()
                        .filter(v -> v.getId().equals(versionId))
                        .findFirst()
                        .orElseThrow(() -> new ApplicationNotFoundException(
                                "Version not found: " + versionId));
                conflictingVersions.add(version);
            }

            // Resolve the conflict
            DomainEntityVersionRecord resolvedVersion = domainConflictService.resolveConflict(
                    conflictingVersions, request.getResolutionStrategy().toString());

            // Record the resolution
            DomainEntityConflictResolutionRecord resolutionRecord = new DomainEntityConflictResolutionRecord();
            resolutionRecord.setConflictingVersionIDs(request.getConflictingVersionIDs());
            resolutionRecord.setResolutionStrategy(request.getResolutionStrategy().toString());
            domainConflictService.recordResolution(resolutionRecord);

            // Create response
            return SharedConflictResolutionResponseDTO.builder()
                    .fileID(fileId)
                    .conflictResolved(true)
                    .resolutionStrategy(request.getResolutionStrategy())
                    .resultingVersionId(resolvedVersion.getId())
                    .resolutionTimestamp(Instant.now().toString())
                    .resolutionDetails(String.format(
                            "Conflict resolved using %s strategy. Resulting version: %d",
                            request.getResolutionStrategy(),
                            resolvedVersion.getVersionNumber()))
                    .build();

        } catch (ApplicationNotFoundException e) {
            logger.error("Not found error during conflict resolution", e);
            throw e;
        } catch (ApplicationValidationException e) {
            logger.error("Validation error during conflict resolution", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error resolving conflict", e);
            throw new ApplicationConflictException("Failed to resolve conflict: " + e.getMessage(), e);
        }
    }

    private void validateRequest(String fileId, SharedConflictResolutionRequestDTO request) {
        if (fileId == null || fileId.isBlank()) {
            throw new ApplicationValidationException("File ID cannot be null or empty");
        }
        if (request == null) {
            throw new ApplicationValidationException("Conflict resolution request cannot be null");
        }
        if (request.getConflictingVersionIDs() == null || request.getConflictingVersionIDs().isEmpty()) {
            throw new ApplicationValidationException("Conflicting version IDs cannot be null or empty");
        }
        if (request.getResolutionStrategy() == null) {
            throw new ApplicationValidationException("Resolution strategy cannot be null");
        }
    }
}
