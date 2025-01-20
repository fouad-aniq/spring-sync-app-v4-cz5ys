package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.entities.DomainEntityConflictResolution;
import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.infrastructure.exceptions.InfrastructureException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfrastructureSyncCoordinatorClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${sync.coordinator.url}")
    private String syncCoordinatorUrl;

    @Value("${sync.coordinator.api-key}")
    private String apiKey;

    public void notifySync(String eventData) {
        try {
            log.debug("Notifying sync event: {}", eventData);

            Map<String, Object> payload = new HashMap<>();
            payload.put("timestamp", Instant.now().toString());
            payload.put("eventData", eventData);
            payload.put("source", "metadata-service");

            HttpEntity<String> request = createJsonRequest(payload);
            restTemplate.postForObject(syncCoordinatorUrl + "/events", request, String.class);

            log.debug("Successfully notified sync event");
        } catch (Exception e) {
            log.error("Error notifying sync event", e);
            throw new InfrastructureException(
                    "Failed to notify sync event: " + e.getMessage(),
                    "SYNC_001"
            );
        }
    }

    public void notifyConflictResolution(String fileID, DomainEntityConflictResolution resolution) {
        try {
            log.debug("Notifying conflict resolution for fileID: {}, strategy: {}",
                    fileID, resolution.getResolutionStrategy());

            Map<String, Object> payload = new HashMap<>();
            payload.put("timestamp", Instant.now().toString());
            payload.put("fileID", fileID);
            payload.put("resolutionStrategy", resolution.getResolutionStrategy());
            payload.put("conflictingVersions", resolution.getConflictingVersionIDs());
            payload.put("resolved", resolution.isResolved());
            payload.put("resolutionTimestamp", resolution.getResolutionTimestamp().toString());

            HttpEntity<String> request = createJsonRequest(payload);
            restTemplate.postForObject(syncCoordinatorUrl + "/conflicts/resolution", request, String.class);

            log.debug("Successfully notified conflict resolution");
        } catch (Exception e) {
            log.error("Error notifying conflict resolution for fileID: {}", fileID, e);
            throw new InfrastructureException(
                    "Failed to notify conflict resolution: " + e.getMessage(),
                    "SYNC_002"
            );
        }
    }

    public void synchronizeMetadata(DomainEntityFileMetadata metadata) {
        try {
            log.debug("Synchronizing metadata for fileID: {}, version: {}",
                    metadata.getFileID(), metadata.getCurrentVersionNumber());

            Map<String, Object> payload = new HashMap<>();
            payload.put("timestamp", Instant.now().toString());
            payload.put("fileID", metadata.getFileID());
            payload.put("path", metadata.getPath());
            payload.put("checksum", metadata.getChecksum());
            payload.put("creationTimestamp", metadata.getCreationTimestamp().toString());
            payload.put("lastModifiedTimestamp", metadata.getLastModifiedTimestamp().toString());
            payload.put("ownershipDetails", metadata.getOwnershipDetails());
            payload.put("currentVersionNumber", metadata.getCurrentVersionNumber());

            HttpEntity<String> request = createJsonRequest(payload);
            restTemplate.postForObject(syncCoordinatorUrl + "/metadata/sync", request, String.class);

            log.debug("Successfully synchronized metadata for fileID: {}", metadata.getFileID());
        } catch (Exception e) {
            log.error("Error synchronizing metadata for fileID: {}", metadata.getFileID(), e);
            throw new InfrastructureException(
                    "Failed to synchronize metadata: " + e.getMessage(),
                    "SYNC_003"
            );
        }
    }

    private HttpEntity<String> createJsonRequest(Map<String, Object> payload) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);

        String jsonBody = objectMapper.writeValueAsString(payload);
        return new HttpEntity<>(jsonBody, headers);
    }
}
