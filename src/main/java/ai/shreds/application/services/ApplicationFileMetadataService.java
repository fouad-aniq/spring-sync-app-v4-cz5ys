package ai.shreds.application.services;

import ai.shreds.application.exceptions.ApplicationMetadataException;
import ai.shreds.application.ports.ApplicationInputPortFileMetadata;
import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.domain.services.DomainMetadataService;
import ai.shreds.domain.services.DomainVersionService;
import ai.shreds.infrastructure.external_services.InfrastructureMonitoringClient;
import ai.shreds.infrastructure.external_services.InfrastructureSyncCoordinatorClient;
import ai.shreds.shared.dtos.SharedCreateUpdateMetadataResponse;
import ai.shreds.shared.dtos.SharedFileMetadataRequest;
import ai.shreds.shared.dtos.SharedFileMetadataResponse;
import ai.shreds.shared.dtos.SharedVersionRecordResponse;
import ai.shreds.shared.enums.SharedEnumMetadataStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ApplicationFileMetadataService implements ApplicationInputPortFileMetadata {

    private final DomainMetadataService metadataService;
    private final DomainVersionService versionService;
    private final InfrastructureMonitoringClient monitoringClient;
    private final InfrastructureSyncCoordinatorClient syncCoordinatorClient;

    @Override
    public SharedCreateUpdateMetadataResponse createOrUpdateMetadata(@Valid SharedFileMetadataRequest request) {
        try {
            log.debug("Processing metadata request for fileID: {}", request.getFileID());

            // Record metrics before operation
            recordPreOperationMetrics(request);

            // Convert request to domain entity
            DomainEntityFileMetadata domainEntity = DomainEntityFileMetadata.fromSharedRequest(request);

            // Check if metadata exists
            boolean isUpdate = false;
            try {
                metadataService.getMetadata(request.getFileID());
                isUpdate = true;
            } catch (Exception e) {
                log.debug("No existing metadata found for fileID: {}", request.getFileID());
            }

            // Create or update metadata
            DomainEntityFileMetadata savedEntity = metadataService.createOrUpdateMetadata(domainEntity);

            // Notify external systems
            notifyExternalSystems(savedEntity, isUpdate);

            // Create response
            SharedCreateUpdateMetadataResponse response = SharedCreateUpdateMetadataResponse.builder()
                    .fileID(savedEntity.getFileID())
                    .metadata(savedEntity.toSharedMetadataResponse())
                    .status(isUpdate ? SharedEnumMetadataStatus.UPDATED : SharedEnumMetadataStatus.CREATED)
                    .build();

            // Record metrics after operation
            recordPostOperationMetrics(response);

            log.info("Completed metadata {} for fileID: {}",
                    isUpdate ? "update" : "creation", response.getFileID());

            return response;

        } catch (Exception e) {
            log.error("Error processing metadata request for fileID: {}", request.getFileID(), e);
            recordErrorMetrics(request.getFileID(), e);
            throw new ApplicationMetadataException(
                    "Failed to process metadata: " + e.getMessage(),
                    "APP_META_001",
                    e
            );
        }
    }

    @Override
    public SharedFileMetadataResponse retrieveMetadata(String fileID) {
        try {
            log.debug("Retrieving metadata for fileID: {}", fileID);

            DomainEntityFileMetadata metadata = metadataService.getMetadata(fileID);
            SharedFileMetadataResponse response = metadata.toSharedMetadataResponse();

            recordRetrievalMetrics(fileID, true);
            log.info("Retrieved metadata for fileID: {}", fileID);

            return response;

        } catch (Exception e) {
            log.error("Error retrieving metadata for fileID: {}", fileID, e);
            recordRetrievalMetrics(fileID, false);
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

            List<SharedVersionRecordResponse> versions = versionService.getVersionHistory(fileID)
                    .stream()
                    .map(DomainEntityVersionRecord::toSharedVersionRecordResponse)
                    .toList();

            recordVersionHistoryMetrics(fileID, versions.size());
            log.info("Retrieved {} versions for fileID: {}", versions.size(), fileID);

            return versions;

        } catch (Exception e) {
            log.error("Error retrieving version history for fileID: {}", fileID, e);
            recordErrorMetrics(fileID, e);
            throw new ApplicationMetadataException(
                    "Failed to retrieve version history: " + e.getMessage(),
                    "APP_META_003",
                    e
            );
        }
    }

    private void recordPreOperationMetrics(SharedFileMetadataRequest request) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("fileID", request.getFileID());
            metrics.put("operation", "metadata_operation_started");
            metrics.put("path", request.getPath());
            metrics.put("versionNumber", request.getCurrentVersionNumber());

            monitoringClient.recordMetrics(metrics);
        } catch (Exception e) {
            log.warn("Failed to record pre-operation metrics", e);
        }
    }

    private void recordPostOperationMetrics(SharedCreateUpdateMetadataResponse response) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("fileID", response.getFileID());
            metrics.put("operation", "metadata_operation_completed");
            metrics.put("status", response.getStatus());

            monitoringClient.recordMetrics(metrics);
        } catch (Exception e) {
            log.warn("Failed to record post-operation metrics", e);
        }
    }

    private void recordRetrievalMetrics(String fileID, boolean success) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("fileID", fileID);
            metrics.put("operation", "metadata_retrieval");
            metrics.put("success", success);

            monitoringClient.recordMetrics(metrics);
        } catch (Exception e) {
            log.warn("Failed to record retrieval metrics", e);
        }
    }

    private void recordVersionHistoryMetrics(String fileID, int versionCount) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("fileID", fileID);
            metrics.put("operation", "version_history_retrieval");
            metrics.put("versionCount", versionCount);

            monitoringClient.recordMetrics(metrics);
        } catch (Exception e) {
            log.warn("Failed to record version history metrics", e);
        }
    }

    private void recordErrorMetrics(String fileID, Exception error) {
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("fileID", fileID);
            context.put("errorType", error.getClass().getSimpleName());
            context.put("errorMessage", error.getMessage());

            monitoringClient.reportError("Metadata operation failed", context);
        } catch (Exception e) {
            log.warn("Failed to record error metrics", e);
        }
    }

    private void notifyExternalSystems(DomainEntityFileMetadata metadata, boolean isUpdate) {
        try {
            // Synchronize metadata with other services
            syncCoordinatorClient.synchronizeMetadata(metadata);

            // Send monitoring data
            String monitoringData = String.format(
                    "Metadata %s completed for file %s version %d",
                    isUpdate ? "update" : "creation",
                    metadata.getFileID(),
                    metadata.getCurrentVersionNumber());
            monitoringClient.sendMonitoringData(monitoringData);
        } catch (Exception e) {
            log.warn("Failed to notify external systems", e);
        }
    }
}
