package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.domain.entities.DomainEntityConflictResolutionRecord;
import ai.shreds.infrastructure.exceptions.InfrastructureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class InfrastructureSyncClient {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureSyncClient.class);

    @Value("${sync.service.base-url}")
    private String syncServiceBaseUrl;

    @Value("${sync.service.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public InfrastructureSyncClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifySync(DomainEntityFileMetadata metadata) {
        try {
            logger.debug("Notifying sync service about metadata change for file: {}", metadata.getFileID());
            String url = syncServiceBaseUrl + "/api/sync/notify";
            
            Map<String, Object> payload = Map.of(
                "fileId", metadata.getFileID(),
                "timestamp", metadata.getLastModifiedTimestamp(),
                "version", metadata.getCurrentVersionNumber(),
                "metadata", metadata
            );

            sendRequest(url, payload);
            logger.info("Successfully notified sync service for file: {}", metadata.getFileID());
        } catch (Exception e) {
            logger.error("Failed to notify sync service", e);
            throw new InfrastructureException("Sync service notification failed", e);
        }
    }

    public List<String> checkConflicts(String fileID) {
        try {
            logger.debug("Checking conflicts for file: {}", fileID);
            String url = syncServiceBaseUrl + "/api/sync/conflicts/" + fileID;
            
            ResponseEntity<List<String>> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                createHttpEntity(null),
                new org.springframework.core.ParameterizedTypeReference<List<String>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to check conflicts", e);
            throw new InfrastructureException("Conflict check failed", e);
        }
    }

    public void notifyConflictResolved(DomainEntityConflictResolutionRecord resolution) {
        try {
            logger.debug("Notifying sync service about resolved conflict for versions: {}", 
                    resolution.getConflictingVersionIDs());
            String url = syncServiceBaseUrl + "/api/sync/conflicts/resolved";
            
            Map<String, Object> payload = Map.of(
                "resolutionId", resolution.getId(),
                "conflictingVersions", resolution.getConflictingVersionIDs(),
                "strategy", resolution.getResolutionStrategy(),
                "timestamp", resolution.getResolutionTimestamp()
            );

            sendRequest(url, payload);
            logger.info("Successfully notified sync service about conflict resolution");
        } catch (Exception e) {
            logger.error("Failed to notify conflict resolution", e);
            throw new InfrastructureException("Conflict resolution notification failed", e);
        }
    }

    private void sendRequest(String url, Object payload) {
        restTemplate.postForObject(url, createHttpEntity(payload), Void.class);
    }

    private HttpEntity<?> createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);
        return new HttpEntity<>(body, headers);
    }
}
