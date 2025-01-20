package ai.shreds.infrastructure.external_services;

import ai.shreds.application.ports.ApplicationMetadataOutputPort;
import ai.shreds.shared.dtos.SharedFileMetadataDTO;
import ai.shreds.shared.dtos.SharedConflictResolutionResponseDTO;
import ai.shreds.infrastructure.exceptions.InfrastructureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Component
public class InfrastructureMonitoringClient implements ApplicationMetadataOutputPort {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureMonitoringClient.class);

    @Value("${monitoring.service.base-url}")
    private String monitoringServiceBaseUrl;

    @Value("${monitoring.service.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public InfrastructureMonitoringClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void notifyMetadataChange(SharedFileMetadataDTO metadata) {
        try {
            String url = monitoringServiceBaseUrl + "/api/events/metadata-change";
            Map<String, Object> payload = Map.of(
                "fileId", metadata.getFileID(),
                "timestamp", Instant.now().toString(),
                "metadata", metadata
            );
            sendRequest(url, payload);
        } catch (Exception e) {
            logger.error("Failed to notify metadata change", e);
            throw new InfrastructureException("Monitoring service notification failed", e);
        }
    }

    @Override
    public void notifyConflictResolution(SharedConflictResolutionResponseDTO resolution) {
        try {
            String url = monitoringServiceBaseUrl + "/api/events/conflict-resolution";
            Map<String, Object> payload = Map.of(
                "fileId", resolution.getFileID(),
                "timestamp", Instant.now().toString(),
                "resolution", resolution
            );
            sendRequest(url, payload);
        } catch (Exception e) {
            logger.error("Failed to notify conflict resolution", e);
            throw new InfrastructureException("Monitoring service notification failed", e);
        }
    }

    @Override
    public void reportError(Exception e) {
        try {
            String url = monitoringServiceBaseUrl + "/api/events/error";
            Map<String, Object> payload = Map.of(
                "timestamp", Instant.now().toString(),
                "errorType", e.getClass().getSimpleName(),
                "message", e.getMessage(),
                "stackTrace", e.getStackTrace()
            );
            sendRequest(url, payload);
        } catch (Exception ex) {
            logger.error("Failed to report error", ex);
        }
    }

    @Override
    public void recordMetrics(String operationType, long durationMs) {
        try {
            String url = monitoringServiceBaseUrl + "/api/metrics";
            Map<String, Object> payload = Map.of(
                "timestamp", Instant.now().toString(),
                "operationType", operationType,
                "durationMs", durationMs
            );
            sendRequest(url, payload);
        } catch (Exception e) {
            logger.error("Failed to record metrics", e);
        }
    }

    @Override
    public void reportHealth(String status, String details) {
        try {
            String url = monitoringServiceBaseUrl + "/api/health";
            Map<String, Object> payload = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "details", details
            );
            sendRequest(url, payload);
        } catch (Exception e) {
            logger.error("Failed to report health status", e);
        }
    }

    @Override
    public void notifySecurityEvent(String eventType, String details) {
        try {
            String url = monitoringServiceBaseUrl + "/api/events/security";
            Map<String, Object> payload = Map.of(
                "timestamp", Instant.now().toString(),
                "eventType", eventType,
                "details", details
            );
            sendRequest(url, payload);
        } catch (Exception e) {
            logger.error("Failed to notify security event", e);
        }
    }

    @Override
    public void recordAuditTrail(String action, String resourceId, String userId, String details) {
        try {
            String url = monitoringServiceBaseUrl + "/api/audit";
            Map<String, Object> payload = Map.of(
                "timestamp", Instant.now().toString(),
                "action", action,
                "resourceId", resourceId,
                "userId", userId,
                "details", details
            );
            sendRequest(url, payload);
        } catch (Exception e) {
            logger.error("Failed to record audit trail", e);
        }
    }

    private void sendRequest(String url, Object payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);

        HttpEntity<Object> request = new HttpEntity<>(payload, headers);
        restTemplate.postForObject(url, request, Void.class);
    }
}
