package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a version record of a file")
public class SharedVersionRecordDTO {
    @Schema(description = "Unique identifier of this version",
           example = "v1-123e4567-e89b-12d3-a456-426614174000")
    private String versionId;

    @Schema(description = "ID of the file this version belongs to",
           example = "123e4567-e89b-12d3-a456-426614174000")
    private String fileId;

    @Min(value = 1, message = "Version number must be at least 1")
    @Schema(description = "Version number of the file", example = "1")
    private int versionNumber;

    @NotBlank(message = "Timestamp cannot be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
            message = "Timestamp must be in ISO-8601 format")
    @Schema(description = "Timestamp when this version was created",
           example = "2023-01-01T10:00:00Z")
    private String timestamp;

    @Pattern(regexp = "^[a-fA-F0-9]{32}$", message = "Version checksum must be a valid MD5 hash")
    @Schema(description = "Checksum of the file at this version",
           example = "d41d8cd98f00b204e9800998ecf8427e")
    private String versionChecksum;

    @Schema(description = "User who created this version", example = "john.doe")
    private String createdBy;

    @Schema(description = "Size of the file in this version", example = "1048576")
    private Long fileSize;

    @Schema(description = "Additional details about this version",
           example = "{\"comment\": \"Updated financial data\", \"source\": \"web-upload\"}")
    private String additionalDetails;

    @Schema(description = "Change type for this version",
           example = "CONTENT_UPDATE",
           allowableValues = {"CONTENT_UPDATE", "METADATA_UPDATE", "INITIAL_VERSION", "CONFLICT_RESOLUTION"})
    private String changeType;

    @Schema(description = "Additional metadata specific to this version")
    private Map<String, String> versionMetadata;

    @Schema(description = "Whether this version is part of the main branch", example = "true")
    private boolean mainBranch;

    @Schema(description = "Reference to parent version if this is a branch",
           example = "v1-123e4567-e89b-12d3-a456-426614174000")
    private String parentVersionId;

    public static SharedVersionRecordDTO createNew(String fileId, int versionNumber, String createdBy) {
        return SharedVersionRecordDTO.builder()
                .versionId("v" + versionNumber + "-" + java.util.UUID.randomUUID().toString())
                .fileId(fileId)
                .versionNumber(versionNumber)
                .timestamp(Instant.now().toString())
                .createdBy(createdBy)
                .mainBranch(true)
                .changeType("INITIAL_VERSION")
                .build();
    }

    public static SharedVersionRecordDTO createUpdate(String fileId, int versionNumber,
            String createdBy, String changeType) {
        return SharedVersionRecordDTO.builder()
                .versionId("v" + versionNumber + "-" + java.util.UUID.randomUUID().toString())
                .fileId(fileId)
                .versionNumber(versionNumber)
                .timestamp(Instant.now().toString())
                .createdBy(createdBy)
                .mainBranch(true)
                .changeType(changeType)
                .build();
    }

    public static SharedVersionRecordDTO createBranch(String fileId, int versionNumber,
            String parentVersionId, String createdBy) {
        return SharedVersionRecordDTO.builder()
                .versionId("v" + versionNumber + "-" + java.util.UUID.randomUUID().toString())
                .fileId(fileId)
                .versionNumber(versionNumber)
                .timestamp(Instant.now().toString())
                .createdBy(createdBy)
                .mainBranch(false)
                .parentVersionId(parentVersionId)
                .changeType("BRANCH_CREATION")
                .build();
    }

    public SharedVersionRecordDTO withChecksum(String checksum) {
        this.versionChecksum = checksum;
        return this;
    }

    public SharedVersionRecordDTO withDetails(String details) {
        this.additionalDetails = details;
        return this;
    }

    public SharedVersionRecordDTO withMetadata(Map<String, String> metadata) {
        this.versionMetadata = metadata;
        return this;
    }
}
