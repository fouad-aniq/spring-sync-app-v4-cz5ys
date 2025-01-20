package ai.shreds.shared.dtos;

import ai.shreds.shared.enums.SharedMetadataStatusEnum;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for metadata creation or update operations")
public class SharedCreateUpdateMetadataResponseDTO {
    @NotNull(message = "File ID cannot be null")
    @Schema(description = "Unique identifier of the file",
           example = "123e4567-e89b-12d3-a456-426614174000")
    private String fileID;

    @NotNull(message = "Metadata cannot be null")
    @Schema(description = "Complete metadata information about the file")
    private SharedFileMetadataDTO metadata;

    @NotNull(message = "Status cannot be null")
    @Schema(description = "Status of the create/update operation", example = "CREATED")
    private SharedMetadataStatusEnum status;

    @Schema(description = "Optional message providing additional details about the operation")
    private String message;

    @Schema(description = "Timestamp of the operation", example = "2023-01-01T10:00:00Z")
    private String timestamp;

    @Schema(description = "User who performed the operation", example = "john.doe")
    private String operatedBy;

    @Schema(description = "Version number after the operation", example = "2")
    private Integer resultingVersion;

    @Schema(description = "Processing time in milliseconds", example = "150")
    private Long processingTimeMs;

    @Schema(description = "Whether the operation triggered any notifications", example = "true")
    private boolean notificationsSent;

    public static SharedCreateUpdateMetadataResponseDTO success(
            String fileID,
            SharedFileMetadataDTO metadata,
            SharedMetadataStatusEnum status,
            String operatedBy) {
        return SharedCreateUpdateMetadataResponseDTO.builder()
                .fileID(fileID)
                .metadata(metadata)
                .status(status)
                .timestamp(Instant.now().toString())
                .operatedBy(operatedBy)
                .resultingVersion(metadata.getCurrentVersionNumber())
                .build();
    }

    public static SharedCreateUpdateMetadataResponseDTO failure(
            String fileID,
            String message,
            String operatedBy) {
        return SharedCreateUpdateMetadataResponseDTO.builder()
                .fileID(fileID)
                .status(SharedMetadataStatusEnum.FAILED)
                .message(message)
                .timestamp(Instant.now().toString())
                .operatedBy(operatedBy)
                .build();
    }

    public static SharedCreateUpdateMetadataResponseDTO conflict(
            String fileID,
            String message,
            SharedFileMetadataDTO conflictingMetadata) {
        return SharedCreateUpdateMetadataResponseDTO.builder()
                .fileID(fileID)
                .status(SharedMetadataStatusEnum.CONFLICT_DETECTED)
                .message(message)
                .metadata(conflictingMetadata)
                .timestamp(Instant.now().toString())
                .build();
    }

    public SharedCreateUpdateMetadataResponseDTO withProcessingTime(long startTimeMs) {
        this.processingTimeMs = System.currentTimeMillis() - startTimeMs;
        return this;
    }

    public SharedCreateUpdateMetadataResponseDTO withNotificationStatus(boolean sent) {
        this.notificationsSent = sent;
        return this;
    }
}
