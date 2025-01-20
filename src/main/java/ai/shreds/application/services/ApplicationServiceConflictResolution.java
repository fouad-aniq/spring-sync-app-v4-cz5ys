package ai.shreds.application.services;

import ai.shreds.application.exceptions.ApplicationMetadataException;
import ai.shreds.application.ports.ApplicationInputPortConflictResolution;
import ai.shreds.domain.entities.DomainEntityConflictResolution;
import ai.shreds.domain.services.DomainServiceConflict;
import ai.shreds.shared.dtos.SharedConflictResolutionRequest;
import ai.shreds.shared.dtos.SharedConflictResolutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ApplicationServiceConflictResolution implements ApplicationInputPortConflictResolution {

    private final DomainServiceConflict domainServiceConflict;

    @Override
    public SharedConflictResolutionResponse resolveConflict(
            String fileID,
            @Valid SharedConflictResolutionRequest request) {
        try {
            log.debug("Processing conflict resolution for fileID: {} with strategy: {}",
                    fileID, request.getResolutionStrategy());

            // Create domain entity for conflict resolution
            DomainEntityConflictResolution conflictResolution = DomainEntityConflictResolution.createNew(
                    fileID,
                    request.getConflictingVersionIDs(),
                    request.getResolutionStrategy());

            // Resolve the conflict
            DomainEntityConflictResolution resolved = domainServiceConflict.resolveConflict(conflictResolution);

            // Convert to response
            SharedConflictResolutionResponse response = resolved.toSharedConflictResolutionResponse();

            log.info("Conflict resolution completed for fileID: {} with result: {}",
                    fileID, response.isConflictResolved());

            return response;

        } catch (Exception e) {
            log.error("Error resolving conflict for fileID: {}", fileID, e);
            throw new ApplicationMetadataException(
                    "Failed to resolve conflict: " + e.getMessage(),
                    "APP_META_004",
                    e
            );
        }
    }
}
