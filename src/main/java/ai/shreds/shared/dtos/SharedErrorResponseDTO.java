package ai.shreds.shared.dtos;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Standard error response format")
public class SharedErrorResponseDTO {
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error message", example = "Invalid input data")
    private String message;

    @Schema(description = "Error code for client reference", example = "VALIDATION_ERROR")
    private String errorCode;

    @Schema(description = "Timestamp when the error occurred", example = "2023-01-01T10:00:00Z")
    private String timestamp;

    @Schema(description = "Additional details about the error", example = "{\"field\": \"fileID\", \"violation\": \"must not be empty\"}")
    private String details;

    public static SharedErrorResponseDTO of(int status, String message, String errorCode) {
        return SharedErrorResponseDTO.builder()
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public static SharedErrorResponseDTO withDetails(int status, String message, String errorCode, String details) {
        return SharedErrorResponseDTO.builder()
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .details(details)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }
}
