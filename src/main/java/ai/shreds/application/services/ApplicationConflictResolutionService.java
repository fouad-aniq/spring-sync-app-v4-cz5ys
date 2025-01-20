package ai.shreds.application.services;

import ai.shreds.application.exceptions.ApplicationMetadataException;
import ai.shreds.application.ports.ApplicationInputPortConflictResolution;
import ai.shreds.domain.entities.DomainEntityConflictResolution;
import ai.shreds.domain.services.DomainConflictService;
import ai.shreds.infrastructure.external_services.InfrastructureMonitoringClient;
import ai.shreds.infrastructure.external_services.InfrastructureSyncCoordinatorClient;
import ai.shreds.shared.dtos.SharedConflictResolutionRequest;
import ai.shreds.shared.dtos.SharedConflictResolutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ApplicationConflictResolutionService implements ApplicationInputPortConflictResolution {

    private final DomainConflictService domainConflictService;
    private final InfrastructureMonitoringClient monitoringClient;
    private final InfrastructureSyncCoordinatorClient syncCoordinatorClient;

    @Override
    public SharedConflictResolutionResponse resolveConflict(
            String fileID,
            @Valid SharedConflictResolutionRequest request) {
        try {
            log.debug("Processing conflict resolution for fileID: {} with strategy: {}",
                    fileID, request.getResolutionStrategy());

            // Create domain entity
            DomainEntityConflictResolution conflictResolution = DomainEntityConflictResolution.createNew(
                    fileID,
                    request.getConflictingVersionIDs(),
                    request.getResolutionStrategy());

            // Record metrics before resolution
            recordPreResolutionMetrics(fileID, request);

            // Resolve conflict
            DomainEntityConflictResolution resolved = domainConflictService.resolveConflict(conflictResolution);

            // Notify external systems
            notifyExternalSystems(fileID, resolved);

            // Record metrics after resolution
            recordPostResolutionMetrics(fileID, resolved);

            // Create response
            SharedConflictResolutionResponse response = resolved.toSharedConflictResolutionResponse();

            log.info("Completed conflict resolution for fileID: {} with result: {}",
                    fileID, response.isConflictResolved());

            return response;

        } catch (Exception e) {
            log.error("Error resolving conflict for fileID: {}", fileID, e);
            
            // Record error metrics
            recordErrorMetrics(fileID, e);
            
            throw new ApplicationMetadataException(
                    "Failed to resolve conflict: " + e.getMessage(),
                    "APP_CONF_001",
                    e
            );
        }
    }

    private void recordPreResolutionMetrics(String fileID, SharedConflictResolutionRequest request) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("fileID", fileID);
            metrics.put("conflictingVersionsCount", request.getConflictingVersionIDs().size());
            metrics.put("resolutionStrategy", request.getResolutionStrategy());
            metrics.put("event", "conflict_resolution_started");

            monitoringClient.recordMetrics(metrics);
        } catch (Exception e) {
            log.warn("Failed to record pre-resolution metrics", e);
        }
    }

    private void recordPostResolutionMetrics(String fileID, DomainEntityConflictResolution resolved) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("fileID", fileID);
            metrics.put("resolved", resolved.isResolved());
            metrics.put("resolutionStrategy", resolved.getResolutionStrategy());
            metrics.put("resolutionDuration", resolved.getResolutionTimestamp().toEpochMilli());
            metrics.put("event", "conflict_resolution_completed");

            monitoringClient.recordMetrics(metrics);
        } catch (Exception e) {
            log.warn("Failed to record post-resolution metrics", e);
        }
    }

    private void recordErrorMetrics(String fileID, Exception error) {
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("fileID", fileID);
            context.put("errorType", error.getClass().getSimpleName());
            context.put("errorMessage", error.getMessage());

            monitoringClient.reportError("Conflict resolution failed", context);
        } catch (Exception e) {
            log.warn("Failed to record error metrics", e);
        }
    }

    private void notifyExternalSystems(String fileID, DomainEntityConflictResolution resolved) {
        try {
            // Notify sync coordinator about the resolution
            syncCoordinatorClient.notifyConflictResolution(fileID, resolved);

            // Send monitoring data
            String monitoringData = String.format(
                    "Conflict resolution completed for file %s using strategy %s",
                    fileID, resolved.getResolutionStrategy());
            monitoringClient.sendMonitoringData(monitoringData);
        } catch (Exception e) {
            log.warn("Failed to notify external systems about conflict resolution", e);
        }
    }
}
