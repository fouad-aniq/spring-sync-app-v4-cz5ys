package ai.shreds.infrastructure.external_services;

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
public class InfrastructureMonitoringClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${monitoring.service.url}")
    private String monitoringServiceUrl;

    @Value("${monitoring.service.api-key}")
    private String apiKey;

    public void sendMonitoringData(String data) {
        try {
            log.debug("Sending monitoring data: {}", data);

            Map<String, Object> payload = new HashMap<>();
            payload.put("timestamp", Instant.now().toString());
            payload.put("data", data);
            payload.put("source", "metadata-service");

            HttpEntity<String> request = createJsonRequest(payload);
            restTemplate.postForObject(monitoringServiceUrl + "/data", request, String.class);

            log.debug("Successfully sent monitoring data");
        } catch (Exception e) {
            log.error("Error sending monitoring data", e);
            throw new InfrastructureException(
                    "Failed to send monitoring data: " + e.getMessage(),
                    "MON_001"
            );
        }
    }

    public void reportError(String error, Map<String, Object> context) {
        try {
            log.debug("Reporting error: {} with context: {}", error, context);

            Map<String, Object> payload = new HashMap<>();
            payload.put("timestamp", Instant.now().toString());
            payload.put("error", error);
            payload.put("context", context);
            payload.put("severity", "ERROR");
            payload.put("source", "metadata-service");

            HttpEntity<String> request = createJsonRequest(payload);
            restTemplate.postForObject(monitoringServiceUrl + "/error", request, String.class);

            log.debug("Successfully reported error");
        } catch (Exception e) {
            log.error("Error reporting error to monitoring service", e);
            throw new InfrastructureException(
                    "Failed to report error: " + e.getMessage(),
                    "MON_002"
            );
        }
    }

    public void recordMetrics(Map<String, Object> metrics) {
        try {
            log.debug("Recording metrics: {}", metrics);

            Map<String, Object> payload = new HashMap<>();
            payload.put("timestamp", Instant.now().toString());
            payload.put("metrics", metrics);
            payload.put("source", "metadata-service");

            HttpEntity<String> request = createJsonRequest(payload);
            restTemplate.postForObject(monitoringServiceUrl + "/metrics", request, String.class);

            log.debug("Successfully recorded metrics");
        } catch (Exception e) {
            log.error("Error recording metrics", e);
            throw new InfrastructureException(
                    "Failed to record metrics: " + e.getMessage(),
                    "MON_003"
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
